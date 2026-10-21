package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.ItemDefinition
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GroundItem
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Loot Tracker. Every kill's loot (LootAttribution, the
 * rules of RuneLite's LootManager), a player's, and the loot of events (a reward chest, a clue scroll,
 * a pickpocket: LootTrackerEvents) is kept by its source, priced at the Grand Exchange and by High
 * Alchemy, and shown in the loot tracker's window (LootTrackerScreen, `::loot` or Open in the plugin
 * manager): each source with its kills and items, grouped or kill by kill, with totals. Items and
 * sources can be ignored. The loot is remembered between sessions per account as RuneLite remembers
 * it (one setting a source, RuneLite's ConfigLoot JSON), for a year.
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object LootTracker : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("loottracker", "Loot Tracker", "1", PluginApi.VERSION, "RuneLite's Loot Tracker: the loot of every kill, chest, clue scroll and pickpocket, with its Grand Exchange and High Alchemy value, by source or kill by kill, remembered between sessions (::loot).", "Pixel")

	@JvmField
	val PRICE_TYPES: Array<String> = arrayOf("Grand Exchange", "High Alchemy")

	const val PRICE_GE: Int = 0

	const val PRICE_HA: Int = 1

	/** RuneLite's MAX_DROPS and MAX_AGE, and how many kills the session keeps (MAX_SESSION_RECORDS). */
	private const val MAX_DROPS: Int = 1024

	private const val MAX_AGE_SECONDS: Long = 365L * 24L * 3600L

	const val MAX_SESSION_RECORDS: Int = 1024

	/** RuneLite's highlight colour for its loot chat lines. */
	private const val HIGHLIGHT: String = "<col=ef1020>"

	@JvmField var enabled: Boolean = true
	@JvmField var ignoredItems: ArrayList<String> = ArrayList()
	@JvmField var ignoredEvents: ArrayList<String> = ArrayList()
	@JvmField var priceType: Int = PRICE_GE
	@JvmField var showPriceType: Boolean = false
	@JvmField var rememberLoot: Boolean = true
	@JvmField var npcKillChatMessage: Boolean = false
	@JvmField var pvpKillChatMessage: Boolean = false
	@JvmField var showChestLootValue: Boolean = true

	/** Every kill this session, newest last (RuneLite's sessionRecords). */
	@JvmField
	val sessionRecords: ArrayList<LootTrackerData.Record> = ArrayList()

	/** Every source's loot together, the loot remembered included, last added last (RuneLite's aggregateRecords). */
	@JvmField
	val aggregateRecords: LinkedHashMap<String, LootTrackerData.Record> = LinkedHashMap()

	private var context: PluginContext? = null

	/** The account the remembered loot was read for, and whose keys it goes under. */
	private var profile: String? = null

	private val attribution: LootAttribution = LootAttribution { kill -> killed(kill) }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			LootTrackerScreen.refresh()
		}
	}

	private val npcListener: EventListener<com.jagex.game.runetek5.plugin.api.Npc> = EventListener { npc -> context?.client()?.let { if (enabled) attribution.npcDespawned(it, npc) } }

	private val playerListener: EventListener<Player> = EventListener { player -> context?.client()?.let { if (enabled) attribution.playerDespawned(it, player) } }

	private val spawnListener: EventListener<GroundItem> = EventListener { item ->
		context?.client()?.let {
			if (enabled) {
				attribution.itemSpawned(it, item)
				LootTrackerEvents.itemSpawned(it, item)
			}
		}
	}

	private val despawnListener: EventListener<GroundItem> = EventListener { item -> context?.client()?.let { attribution.itemDespawned(it, item) } }

	private val tickListener: EventListener<ClientTick> = EventListener { event ->
		attribution.cycle(event.cycle)
		context?.client()?.let { LootTrackerEvents.cycle(it) }
	}

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state === GameState.LOGGED_IN) {
			context?.let { loadProfile(it) }
		} else {
			attribution.clear()
			LootTrackerEvents.reset()
		}
	}

	private val command: CommandHandler = CommandHandler { _, args ->
		val context: PluginContext = this.context ?: return@CommandHandler
		val client: ClientApi = context.client()
		if (args.isNotEmpty() && args[0].equals("toggle", ignoreCase = true)) {
			enabled = !enabled
			context.config().setBoolean("enabled", enabled)
			context.config().save()
			client.consolePrint(if (enabled) "Loot tracker on." else "Loot tracker off.")
			return@CommandHandler
		}
		val why: String? = LootTrackerScreen.toggle()
		if (why != null) {
			client.consolePrint(why)
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		context.settings().toggle("enabled", "Loot tracker", true)
		context.settings().text("ignoredItems", "Ignored items", "", 2000)
		context.settings().choice("priceType", "Price type", PRICE_TYPES[PRICE_GE], PRICE_TYPES)
		context.settings().toggle("showPriceType", "Show price type", false)
		context.settings().toggle("syncPanel", "Remember loot", true)
		context.settings().text("ignoredEvents", "Ignored loot sources", "", 2000)
		context.settings().toggle("npcKillChatMessage", "Show chat message for NPC kills", false)
		context.settings().toggle("pvpKillChatMessage", "Show chat message for PvP kills", false)
		context.settings().toggle("showRaidsLootValue", "Show chat message for Barrows loot", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.NPC_DESPAWNED, npcListener)
		context.events().subscribe(Events.PLAYER_DESPAWNED, playerListener)
		context.events().subscribe(Events.ITEM_SPAWNED, spawnListener)
		context.events().subscribe(Events.ITEM_DESPAWNED, despawnListener)
		context.events().subscribe(Events.CLIENT_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		LootTrackerEvents.subscribe(context)
		context.registerCommand("loot", command)
		PluginManagerScreen.registerPanel(DESCRIPTOR.id) { LootTrackerScreen.open() }
		if (context.client().gameState() === GameState.LOGGED_IN) {
			loadProfile(context)
		}
	}

	override fun stop() {
		PluginManagerScreen.unregisterPanel(DESCRIPTOR.id)
		LootTrackerScreen.close()
		context = null
		profile = null
		attribution.clear()
		LootTrackerEvents.reset()
		sessionRecords.clear()
		aggregateRecords.clear()
	}

	@JvmStatic
	fun context(): PluginContext? = context

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		ignoredItems = GroundItemRules.splitCsv(config.get("ignoredItems", ""))
		ignoredEvents = GroundItemRules.splitCsv(config.get("ignoredEvents", ""))
		val type: Int = PRICE_TYPES.indexOf(config.get("priceType", PRICE_TYPES[PRICE_GE]))
		priceType = if (type < 0) PRICE_GE else type
		showPriceType = config.getBoolean("showPriceType", false)
		rememberLoot = config.getBoolean("syncPanel", true)
		npcKillChatMessage = config.getBoolean("npcKillChatMessage", false)
		pvpKillChatMessage = config.getBoolean("pvpKillChatMessage", false)
		showChestLootValue = config.getBoolean("showRaidsLootValue", true)
		for (record in sessionRecords) {
			markIgnored(record)
		}
		for (record in aggregateRecords.values) {
			markIgnored(record)
		}
	}

	private fun markIgnored(record: LootTrackerData.Record) {
		for (item in record.items) {
			item.ignored = isIgnored(ignoredItems, item.name)
		}
	}

	private fun isIgnored(list: List<String>, name: String): Boolean {
		for (entry in list) {
			if (entry.equals(name, ignoreCase = true)) {
				return true
			}
		}
		return false
	}

	@JvmStatic
	fun isEventIgnored(title: String): Boolean = isIgnored(ignoredEvents, title)

	// --- loot coming in ---

	/** A kill's loot from LootAttribution: an npc's (RuneLite's onNpcLootReceived) or a player's (onPlayerLootReceived). */
	private fun killed(kill: LootAttribution.Kill) {
		val client: ClientApi = context?.client() ?: return
		if (!enabled) {
			return
		}
		val items: ArrayList<IntArray> = ArrayList()
		for (stack in kill.items) {
			items.add(intArrayOf(stack.itemId, stack.quantity))
		}
		if (kill.type == LootAttribution.PLAYER) {
			addLoot(client, kill.name, kill.combatLevel, LootTrackerData.PLAYER, items)
			if (pvpKillChatMessage) {
				lootChatMessage(client, items, kill.name)
			}
		} else {
			addLoot(client, kill.name, kill.combatLevel, LootTrackerData.NPC, items)
			if (npcKillChatMessage) {
				val article: String = if ("aeiouAEIOU".indexOf(kill.name.firstOrNull() ?: 'x') >= 0) "an" else "a"
				lootChatMessage(client, items, article + " " + kill.name)
			}
		}
	}

	/** RuneLite's addLoot: [items] as (id, quantity) pairs, stacked by id, into the session, the sources and the remembered loot. */
	@JvmStatic
	fun addLoot(client: ClientApi, name: String, combatLevel: Int, type: String, items: List<IntArray>) {
		addLoot(client, name, combatLevel, type, items, 1)
	}

	/** The same for [amount] kills at once (RuneLite's: impling jars opened together). */
	@JvmStatic
	fun addLoot(client: ClientApi, name: String, combatLevel: Int, type: String, items: List<IntArray>, amount: Int) {
		val stacked: ArrayList<IntArray> = stack(items)
		if (stacked.isEmpty()) {
			return
		}
		val entries: ArrayList<LootTrackerData.Item> = ArrayList()
		for (stack in stacked) {
			entries.add(buildItem(client, stack[0], stack[1]))
		}
		val subTitle: String = if (combatLevel > 0) "(lvl-$combatLevel)" else ""
		val record: LootTrackerData.Record = LootTrackerData.Record(name, subTitle, type, entries, amount)
		sessionRecords.add(record)
		while (sessionRecords.size > MAX_SESSION_RECORDS) {
			sessionRecords.removeAt(0)
		}
		addToAggregate(record)
		save(type, name, stacked, amount)
		LootTrackerScreen.refresh()
	}

	/** RuneLite's stack(): one entry an item id, the quantities added. */
	@JvmStatic
	fun stack(items: List<IntArray>): ArrayList<IntArray> {
		val out: ArrayList<IntArray> = ArrayList()
		for (item in items) {
			if (item[0] < 0 || item[1] <= 0) {
				continue
			}
			val existing: IntArray? = out.firstOrNull { it[0] == item[0] }
			if (existing != null) {
				existing[1] += item[1]
			} else {
				out.add(intArrayOf(item[0], item[1]))
			}
		}
		return out
	}

	private fun buildItem(client: ClientApi, id: Int, quantity: Int): LootTrackerData.Item {
		val type: ItemDefinition? = client.item(id)
		val name: String = type?.name() ?: ("Item " + id)
		val ge: Int = maxOf(0, client.itemPrice(id))
		return LootTrackerData.Item(id, name, quantity, ge, client.itemHaPrice(id), isIgnored(ignoredItems, name))
	}

	/** Adds [record] to its source's aggregate (moved to the end: the newest), as the panel combines a box. */
	private fun addToAggregate(record: LootTrackerData.Record) {
		val key: String = record.type + "\u0000" + record.title
		val existing: LootTrackerData.Record? = aggregateRecords.remove(key)
		if (existing == null) {
			aggregateRecords[key] = LootTrackerData.Record(record.title, record.subTitle, record.type, copyItems(record.items), record.kills)
			return
		}
		for (item in record.items) {
			val same: LootTrackerData.Item? = existing.items.firstOrNull { it.id == item.id }
			if (same != null) {
				val sum: Long = same.quantity.toLong() + item.quantity
				same.quantity = if (sum > Int.MAX_VALUE) Int.MAX_VALUE else sum.toInt()
			} else {
				existing.items.add(LootTrackerData.Item(item.id, item.name, item.quantity, item.gePrice, item.haPrice, item.ignored))
			}
		}
		existing.kills += record.kills
		aggregateRecords[key] = existing
	}

	private fun copyItems(items: List<LootTrackerData.Item>): ArrayList<LootTrackerData.Item> {
		val out: ArrayList<LootTrackerData.Item> = ArrayList()
		for (item in items) {
			out.add(LootTrackerData.Item(item.id, item.name, item.quantity, item.gePrice, item.haPrice, item.ignored))
		}
		return out
	}

	/** "You've killed a Goblin for 1.2K loot." in RuneLite's highlight colour. */
	private fun lootChatMessage(client: ClientApi, items: List<IntArray>, name: String) {
		client.chatMessage(HIGHLIGHT + "You've killed " + name + " for " + GroundItemRules.quantityToStackSize(totalPrice(client, items)) + " loot.")
	}

	/** The loot's worth by the chosen price type. */
	@JvmStatic
	fun totalPrice(client: ClientApi, items: List<IntArray>): Long {
		var total: Long = 0L
		for (item in items) {
			val price: Int = if (priceType == PRICE_GE) maxOf(0, client.itemPrice(item[0])) else client.itemHaPrice(item[0])
			total += price.toLong() * item[1]
		}
		return total
	}

	// --- remembered loot ---

	private fun now(): Long = lang.currentTimeMillis() / 1000L

	private fun profileKey(name: String): String {
		val owner: String? = profile
		return if (owner == null) name else owner + "." + name
	}

	/** RuneLite's saveLoot for one record: added to what is kept for its source, the last time set to now. */
	private fun save(type: String, name: String, items: List<IntArray>, kills: Int) {
		if (!rememberLoot || profile == null) {
			return
		}
		val config: PluginConfig = context?.config() ?: return
		val key: String = profileKey(LootTrackerData.key(type, name))
		val loot: LootTrackerData.ConfigLoot = LootTrackerData.parse(config.get(key, "")) ?: LootTrackerData.ConfigLoot(type, name).also { it.firstSeconds = now() }
		loot.kills += kills
		for (item in items) {
			loot.add(item[0], item[1])
		}
		loot.lastSeconds = now()
		config.set(key, LootTrackerData.format(loot))
		config.save()
	}

	/** At login: the account's remembered loot, newest first up to RuneLite's 1024 drops, a year at most (older is deleted). */
	private fun loadProfile(context: PluginContext) {
		val client: ClientApi = context.client()
		val name: String = client.localPlayer()?.name()?.lowercase() ?: return
		if (name == profile) {
			return
		}
		profile = name
		aggregateRecords.clear()
		if (!rememberLoot) {
			return
		}
		val config: PluginConfig = context.config()
		val prefix: String = profileKey("drops_")
		val loots: ArrayList<LootTrackerData.ConfigLoot> = ArrayList()
		val old: Long = now() - MAX_AGE_SECONDS
		var removed: Boolean = false
		for (key in config.keys()) {
			if (!key.startsWith(prefix)) {
				continue
			}
			val loot: LootTrackerData.ConfigLoot? = LootTrackerData.parse(config.get(key, ""))
			if (loot == null || loot.lastSeconds < old) {
				config.remove(key) // malformed or more than a year old, as RuneLite drops them
				removed = true
			} else {
				loots.add(loot)
			}
		}
		if (removed) {
			config.save()
		}
		loots.sortByDescending { it.lastSeconds }
		var drops: Int = 0
		val kept: ArrayList<LootTrackerData.ConfigLoot> = ArrayList()
		for (loot in loots) {
			if (drops + loot.numDrops() > MAX_DROPS && kept.isNotEmpty()) {
				break
			}
			kept.add(loot)
			drops += loot.numDrops()
		}
		// oldest first, so that the newest ends up last (on top)
		for (i in kept.indices.reversed()) {
			addToAggregate(toRecord(client, kept[i]))
		}
		LootTrackerScreen.refresh()
	}

	private fun toRecord(client: ClientApi, loot: LootTrackerData.ConfigLoot): LootTrackerData.Record {
		val items: ArrayList<LootTrackerData.Item> = ArrayList()
		var i: Int = 0
		while (i + 1 < loot.drops.size) {
			items.add(buildItem(client, loot.drops[i], loot.drops[i + 1]))
			i += 2
		}
		return LootTrackerData.Record(loot.name, "", loot.type, items, loot.kills)
	}

	// --- what the window does ---

	/** RuneLite's toggleItem: an item name on or off the ignored list, saved. */
	@JvmStatic
	fun toggleItem(name: String) {
		val config: PluginConfig = context?.config() ?: return
		val list: ArrayList<String> = GroundItemRules.splitCsv(config.get("ignoredItems", ""))
		if (!list.removeAll { it.equals(name, ignoreCase = true) }) {
			list.add(name)
		}
		config.set("ignoredItems", GroundItemRules.joinCsv(list))
		config.save()
		readSettings(config)
	}

	/** RuneLite's toggleEvent: a source on or off the ignored list, saved. */
	@JvmStatic
	fun toggleEvent(title: String, ignore: Boolean) {
		val config: PluginConfig = context?.config() ?: return
		val list: ArrayList<String> = GroundItemRules.splitCsv(config.get("ignoredEvents", ""))
		list.removeAll { it.equals(title, ignoreCase = true) }
		if (ignore) {
			list.add(title)
		}
		config.set("ignoredEvents", GroundItemRules.joinCsv(list))
		config.save()
		readSettings(config)
	}

	/** Forgets one source's loot (or, with null, everything): this session's and what is remembered. */
	@JvmStatic
	fun reset(title: String?, type: String?) {
		sessionRecords.removeAll { it.matches(title, type) }
		val gone: ArrayList<String> = ArrayList()
		for (entry in aggregateRecords.entries) {
			if (entry.value.matches(title, type)) {
				gone.add(entry.key)
			}
		}
		for (key in gone) {
			aggregateRecords.remove(key)
		}
		val config: PluginConfig = context?.config() ?: return
		if (profile == null) {
			return
		}
		val prefix: String = profileKey(if (title == null) "drops_" else LootTrackerData.key(type ?: "", title))
		for (key in config.keys()) {
			if (if (title == null) key.startsWith(prefix) else key == prefix) {
				config.remove(key)
			}
		}
		config.save()
	}

	/** Forgets one kill from this session (a box of the kill-by-kill view). */
	@JvmStatic
	fun resetOne(record: LootTrackerData.Record) {
		sessionRecords.remove(record)
	}
}
