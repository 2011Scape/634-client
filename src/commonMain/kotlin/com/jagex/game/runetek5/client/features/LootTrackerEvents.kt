package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GroundItem
import com.jagex.game.runetek5.plugin.api.events.ItemContainerChanged
import com.jagex.game.runetek5.plugin.api.events.MenuOptionClicked
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the Loot Tracker's loot that is not a kill's, RuneLite's chat and
 * menu events as the 634 server (Void) gives them: pickpocketing ("You pick the Man's pocket.", and
 * the double-loot line, named by the npc last clicked), the crystal chest ("You find some treasure in
 * the chest!" in Taverley), the thieving trap chests ("You find treasure inside!"), the Barrows chest
 * (Search in the crypt), an impling caught barehanded ("You manage to catch the impling and acquire some
 * loot."), an impling jar looted, a casket opened, a bird's nest searched, and a clue scroll's reward
 * ("You have completed N easy Treasure Trails.", should the server give them). None of them has a
 * reward window in 634, so the loot is what the backpack gained, and what fell on the player's tile.
 *
 * The server may send the items before the chat line, in the same tick: the backpack before the
 * event is the one a little earlier (the last of [history] older than [LOOK_BACK] cycles), and the
 * loot is what it has gained once it has been still for [SETTLE] cycles, at most [TIMEOUT] after the
 * event (RuneLite's INVCHANGE_TIMEOUT, 10 server ticks).
 */
object LootTrackerEvents {

	const val BACKPACK: Int = 93

	/** The trail reward container, which RuneLite reads for a clue scroll's reward. */
	const val TRAIL_REWARD: Int = 141

	private const val LOOK_BACK: Int = 45

	private const val SETTLE: Int = 30

	private const val TIMEOUT: Int = 300

	/** RuneLite's CHEST_EVENT_TYPES for the 634 map: Taverley's crystal chest. */
	private const val CRYSTAL_CHEST_REGION: Int = 11573

	private const val BARROWS_REGION: Int = 14231

	private val PICKPOCKET: Regex = Regex("You pick (the )?(.+?)'s? pocket.*")

	private const val MULTI_PICKPOCKET: String = "allow you to steal"

	private const val CHEST_LOOTED: String = "You find some treasure in the chest!"

	private const val TRAP_CHEST_LOOTED: String = "You find treasure inside!"

	private const val IMPLING_CATCH: String = "You manage to catch the impling and acquire some loot."

	private val CLUE_SCROLL: Regex = Regex("You have completed [0-9]+ ([a-z]+) Treasure Trails?\\.")

	/** The backpack as it was, newest last: the cycle it changed at and what it held after, as id then amount pairs. */
	private class Snapshot(@JvmField val cycle: Int, @JvmField val items: HashMap<Int, Long>)

	private val history: ArrayList<Snapshot> = ArrayList()

	/** An event waiting for its loot: its name and type, the backpack before it, when it began, and whether the ground counts too. */
	private class Pending(@JvmField val name: String, @JvmField val type: String, @JvmField val before: HashMap<Int, Long>, @JvmField val cycle: Int, @JvmField val ground: Boolean, @JvmField val container: Int, @JvmField val countItem: Int)

	private var pending: Pending? = null

	/** Items that appeared on the player's tile lately, for the events whose loot may fall there when the backpack is full. */
	private class Fallen(@JvmField val itemId: Int, @JvmField val amount: Int, @JvmField val cycle: Int)

	private val fallen: ArrayList<Fallen> = ArrayList()

	/** The npc last clicked to pickpocket (RuneLite's lastPickpocketTarget) and the location last clicked. */
	@JvmField var lastPickpocketTarget: String? = null

	@JvmField var lastLocName: String? = null

	private var context: PluginContext? = null

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> context?.client()?.let { chat(it, event) } }

	private val containerListener: EventListener<ItemContainerChanged> = EventListener { event -> context?.client()?.let { containerChanged(it, event.containerId) } }

	private val clickListener: EventListener<MenuOptionClicked> = EventListener { event -> context?.client()?.let { clicked(it, event) } }

	@JvmStatic
	fun subscribe(context: PluginContext) {
		this.context = context
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.ITEM_CONTAINER_CHANGED, containerListener)
		context.events().subscribe(Events.MENU_OPTION_CLICKED, clickListener)
		history.clear()
		history.add(Snapshot(context.client().clientCycle(), read(context.client(), BACKPACK)))
	}

	@JvmStatic
	fun reset() {
		pending = null
		history.clear()
		fallen.clear()
		lastPickpocketTarget = null
		lastLocName = null
	}

	private fun read(client: ClientApi, containerId: Int): HashMap<Int, Long> {
		val out: HashMap<Int, Long> = HashMap()
		val container: ItemContainer = client.itemContainer(containerId) ?: return out
		for (slot in 0 until container.size()) {
			val id: Int = container.itemId(slot)
			if (id >= 0) {
				out[id] = (out[id] ?: 0L) + container.amount(slot)
			}
		}
		return out
	}

	/** The backpack before [cycle] less [LOOK_BACK]: the newest snapshot older than that, or the oldest kept. */
	private fun backpackBefore(cycle: Int): HashMap<Int, Long> {
		var chosen: Snapshot? = null
		for (snapshot in history) {
			if (snapshot.cycle <= cycle - LOOK_BACK) {
				chosen = snapshot
			}
		}
		return HashMap((chosen ?: history.firstOrNull())?.items ?: HashMap())
	}

	/** RuneLite's onInvChange for event [name]: the loot is what the backpack (or [container]) gains from now, and with [ground] what falls on the player's tile. */
	@JvmStatic
	fun begin(client: ClientApi, name: String, type: String, ground: Boolean, container: Int, countItem: Int) {
		val now: Int = client.clientCycle()
		val before: HashMap<Int, Long> = if (container == BACKPACK) backpackBefore(now) else HashMap()
		pending = Pending(name, type, before, now, ground, container, countItem)
	}

	private fun containerChanged(client: ClientApi, containerId: Int) {
		if (containerId == BACKPACK) {
			history.add(Snapshot(client.clientCycle(), read(client, BACKPACK)))
			while (history.size > 1 && client.clientCycle() - history[1].cycle > TIMEOUT) {
				history.removeAt(0)
			}
		}
	}

	/** Each cycle: an event whose backpack has settled (or that has waited its longest) is recorded. */
	@JvmStatic
	fun cycle(client: ClientApi) {
		val now: Int = client.clientCycle()
		fallen.removeAll { now - it.cycle > TIMEOUT + LOOK_BACK }
		val event: Pending = pending ?: return
		val lastChange: Int = history.lastOrNull()?.cycle ?: event.cycle
		val changedSince: Boolean = lastChange > event.cycle - LOOK_BACK
		val settled: Boolean = changedSince && now - lastChange >= SETTLE && now - event.cycle >= SETTLE
		if (!settled && now - event.cycle < TIMEOUT) {
			return
		}
		pending = null
		settle(client, event)
	}

	private fun settle(client: ClientApi, event: Pending) {
		val after: HashMap<Int, Long> = read(client, event.container)
		val items: ArrayList<IntArray> = ArrayList()
		var used: Int = 1
		for (entry in after.entries) {
			val gained: Long = entry.value - (event.before[entry.key] ?: 0L)
			if (gained > 0) {
				items.add(intArrayOf(entry.key, minOf(gained, Int.MAX_VALUE.toLong()).toInt()))
			}
		}
		if (event.countItem >= 0) {
			// RuneLite's impling jars: as many events as jars went
			val lost: Long = (event.before[event.countItem] ?: 0L) - (after[event.countItem] ?: 0L)
			if (lost <= 0) {
				return
			}
			used = lost.toInt()
		}
		if (event.ground) {
			for (f in fallen) {
				if (f.cycle >= event.cycle - LOOK_BACK) {
					items.add(intArrayOf(f.itemId, f.amount))
				}
			}
		}
		if (items.isEmpty()) {
			return
		}
		LootTracker.addLoot(client, event.name, -1, event.type, items, used)
		if (event.name == "Barrows" && LootTracker.showChestLootValue) {
			client.chatMessage("Your loot is worth around <col=ef1020>" + GroundItemRules.withCommas(LootTracker.totalPrice(client, items)) + "</col> coins.")
		}
	}

	/** An item on the ground: kept a while when it is on the player's tile and nobody else's. */
	@JvmStatic
	fun itemSpawned(client: ClientApi, item: GroundItem) {
		val player: Player = client.localPlayer() ?: return
		if (item.ownership != GroundItem.OWNERSHIP_OTHER && item.sceneX == player.tileX() && item.sceneY == player.tileY() && item.level == player.level()) {
			fallen.add(Fallen(item.itemId, item.amount, client.clientCycle()))
		}
	}

	private fun regionId(client: ClientApi): Int = client.regionId()

	private fun chat(client: ClientApi, event: ChatMessage) {
		if (event.type != ChatType.GAME && event.type != ChatType.GAME_FILTERED && event.type != GAME_FILTER_RAW) {
			return
		}
		val message: String = ClientFeatures.plainText(event.message ?: return)
		if (message == CHEST_LOOTED && regionId(client) == CRYSTAL_CHEST_REGION) {
			begin(client, "Crystal Chest", LootTrackerData.EVENT, true, BACKPACK, -1)
			return
		}
		if (message == TRAP_CHEST_LOOTED) {
			begin(client, ClientPage.capital(lastLocName ?: "Chest"), LootTrackerData.EVENT, true, BACKPACK, -1)
			return
		}
		val pick: MatchResult? = PICKPOCKET.matchEntire(message)
		if (pick != null) {
			var target: String = capitalizeWords(pick.groupValues[2])
			val clicked: String? = lastPickpocketTarget
			if (clicked != null && !clicked.equals(target, ignoreCase = true)) {
				target = clicked // RuneLite's disambiguation: the chat names some targets otherwise
			}
			begin(client, target, LootTrackerData.PICKPOCKET, true, BACKPACK, -1)
			return
		}
		if (message.contains(MULTI_PICKPOCKET) && lastPickpocketTarget != null) {
			begin(client, lastPickpocketTarget!!, LootTrackerData.PICKPOCKET, true, BACKPACK, -1)
			return
		}
		if (message == IMPLING_CATCH) {
			val name: String = client.localPlayer()?.interacting()?.let { (it as? com.jagex.game.runetek5.plugin.api.Npc)?.name() } ?: "Impling"
			begin(client, name, LootTrackerData.EVENT, true, BACKPACK, -1)
			return
		}
		val clue: MatchResult? = CLUE_SCROLL.find(message)
		if (clue != null) {
			val tier: String = clue.groupValues[1].lowercase()
			if (tier in arrayOf("beginner", "easy", "medium", "hard", "elite", "master")) {
				begin(client, "Clue Scroll (" + ClientPage.capital(tier) + ")", LootTrackerData.EVENT, false, TRAIL_REWARD, -1)
			}
		}
	}

	private fun capitalizeWords(text: String): String {
		return text.split(' ').joinToString(" ") { ClientPage.capital(it) }
	}

	private fun clicked(client: ClientApi, event: MenuOptionClicked) {
		val option: String = event.option ?: return
		val target: String = ClientFeatures.plainText(event.target ?: "")
		if (option == "Pickpocket") {
			lastPickpocketTarget = target.substringBefore(" (level").trim()
			return
		}
		if (event.itemId < 0) {
			lastLocName = target
			if (option == "Search" && target.equals("Chest", ignoreCase = true) && regionId(client) == BARROWS_REGION) {
				begin(client, "Barrows", LootTrackerData.EVENT, true, BACKPACK, -1)
			}
			return
		}
		val name: String = client.itemName(event.itemId) ?: return
		if (option == "Loot" && name.endsWith("impling jar", ignoreCase = true)) {
			begin(client, name, LootTrackerData.EVENT, false, BACKPACK, event.itemId)
		} else if (option == "Open" && name.equals("Casket", ignoreCase = true)) {
			begin(client, "Casket", LootTrackerData.EVENT, false, BACKPACK, -1)
		} else if (option == "Search" && name.contains("nest", ignoreCase = true)) {
			begin(client, "Bird nest", LootTrackerData.EVENT, false, BACKPACK, -1)
		}
	}

	/** The server's filterable game messages, as their raw type, in case the client hands them on so. */
	private const val GAME_FILTER_RAW: Int = 109
}
