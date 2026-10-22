package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.InfoBoxPriority
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.LoopTimer
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.NpcDefinition
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.InterfaceChanged
import com.jagex.game.runetek5.plugin.api.events.MenuOptionClicked
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Barrows Brothers, for the Barrows as the 634 server (Void)
 * runs it. The six brothers' mounds are lettered on the minimap (red once the brother is slain); in the
 * crypts a panel takes the place of the game's own list of brothers and kill count, with the reward
 * potential; the tunnels' prayer drain gets a timer; the puzzle door's answer is outlined; and the
 * chest's loot is priced in the chat.
 *
 * The reward potential is Void's rule, not RuneLite's: the server rolls the chest's runes on the sum of
 * the combat levels killed in the run (at most 1012), which it never sends; the plugin adds up the level of
 * each kill as the killed-monsters count (varbit 464) rises, and keeps the sum between sessions. The
 * thresholds are the same as RuneLite's (Void's rune table is OSRS's). A built-in plugin (BuiltinPlugins,
 * docs/PLUGINS.md); the drawing is in BarrowsOverlay.
 */
object BarrowsBrothers : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("barrows", "Barrows Brothers", "1", PluginApi.VERSION, "Letters the brothers' mounds on the minimap, shows who is slain and the chest's reward potential in the crypts, times the prayer drain, outlines the puzzle door's answer and prices the chest's loot.", "Pixel")

	/** A brother: his name, his mound's tile and the varbit set once he is slain. */
	class Brother(@JvmField val name: String, @JvmField val x: Int, @JvmField val y: Int, @JvmField val killedVarbit: Int, @JvmField val npcId: Int)

	/** RuneLite's BarrowsBrothers, with Void's varbits (barrows.varbits.toml) and npc ids. */
	@JvmField
	val BROTHERS: Array<Brother> = arrayOf(
		Brother("Ahrim", 3566, 3289, 457, 2025),
		Brother("Dharok", 3575, 3298, 458, 2026),
		Brother("Guthan", 3577, 3283, 459, 2027),
		Brother("Karil", 3566, 3275, 460, 2028),
		Brother("Torag", 3553, 3283, 461, 2029),
		Brother("Verac", 3557, 3298, 462, 2030),
	)

	/** Void's barrows_killed_monsters: every kill in the run, brothers too, cleared when the chest is looted. */
	const val KILLED_MONSTERS_VARBIT: Int = 464

	/** The Barrows hills and the crypts and tunnels under them. */
	const val BARROWS_REGION: Int = 14131
	const val CRYPT_REGION: Int = 14231

	/** The game's overlay in the crypts (the brothers slain and the kill count) and the puzzle door's window. */
	const val OVERLAY_INTERFACE: Int = 24
	const val PUZZLE_INTERFACE: Int = 25

	/** The overlay's components the panel stands in for: "Brothers slain:" and its list, and "Creatures slain:" and the count. */
	@JvmField
	val OVERLAY_HIDDEN: IntArray = intArrayOf(2, 5)

	/** The puzzle's first picture of the sequence, and the three answers to choose from (RuneLite's PIC_A to PIC_C). */
	const val PUZZLE_FIRST: Int = 6

	@JvmField
	val PUZZLE_CHOICES: IntArray = intArrayOf(2, 3, 5)

	/** Void's prayer drain in the crypts: 30 ticks, every 18 seconds (RuneLite's is OSRS's 18.2). */
	const val PRAYER_DRAIN_MILLIS: Long = 18_000L

	/** The prayer orb's icon. */
	const val PRAYER_SPRITE: Int = 1198

	/** The most the reward potential counts (the dragon med helm's roll). */
	const val MAX_POTENTIAL: Int = 1012

	const val BACKPACK: Int = 93

	/** What the server says right after the chest's loot is in the backpack. */
	const val CHEST_LOOTED: String = "The cave begins to collapse!"

	/** How long after Search the collapse may come and still be this chest's (cycles of 20 ms). */
	private const val CHEST_WINDOW: Int = 150

	/** How long after login varbit changes are the server's catching up, not kills (cycles). */
	private const val LOGIN_SYNC: Int = 150

	@JvmField var enabled: Boolean = true
	@JvmField var showBrotherLoc: Boolean = true
	@JvmField var showChestValue: Boolean = true
	@JvmField var brotherLocColour: Int = CYAN
	@JvmField var deadBrotherLocColour: Int = RED
	@JvmField var showPuzzleAnswer: Boolean = true
	@JvmField var showPrayerDrainTimer: Boolean = true

	/** The sum of the combat levels killed this run, Void's barrows_kill_levels as far as the client can tell. */
	@JvmField var killLevels: Int = 0

	/** The killed-monsters count last seen, -1 before the server's is known. */
	@JvmField var lastKilledMonsters: Int = -1

	/** Each brother's slain varbit last seen. */
	private val lastSlain: BooleanArray = BooleanArray(6)

	/** The npc the player last fought, and its combat level: what a rise in the count is put down to. */
	private var lastTarget: Npc? = null
	@JvmField var lastTargetLevel: Int = 0

	private var syncUntil: Int = 0

	private var syncing: Boolean = false

	/** Whether the overlay and the puzzle are open, from the interface events. */
	@JvmField var overlayOpen: Boolean = false
	@JvmField var puzzleOpen: Boolean = false

	@JvmField var prayerDrainTimer: LoopTimer? = null
	private var lastPrayer: Int = -1

	/** The backpack when the chest was searched, and when (-1 for none waiting). */
	private var chestBefore: IntArray? = null
	private var chestCycle: Int = -1

	/** The last chest's value told, for the smoke test. */
	@JvmField var lastChestValue: Long = -1L

	private var context: PluginContext? = null

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (event.state === GameState.LOGGED_IN) {
			lastKilledMonsters = -1
			syncUntil = client.clientCycle() + LOGIN_SYNC
		} else if (event.previous === GameState.LOGGED_IN) {
			overlayOpen = false
			puzzleOpen = false
			stopPrayerDrainTimer()
		}
	}

	private val openListener: EventListener<InterfaceChanged> = EventListener { event -> interfaceChanged(event.interfaceId, true) }

	private val closeListener: EventListener<InterfaceChanged> = EventListener { event -> interfaceChanged(event.interfaceId, false) }

	private val interactingListener: EventListener<Actor> = EventListener { source ->
		val client: ClientApi = context?.client() ?: return@EventListener
		val target: Actor? = source.interacting()
		if (source === client.localPlayer() && target is Npc) {
			lastTarget = target
			lastTargetLevel = target.combatLevel()
		}
	}

	private val clickListener: EventListener<MenuOptionClicked> = EventListener { event -> context?.client()?.let { clicked(it, event) } }

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> context?.client()?.let { chat(it, event) } }

	/** The minimap and the puzzle's outline go on at the end of the frame, over the interfaces. */
	private val frameListener: EventListener<FrameDraw> = EventListener { event -> context?.client()?.let { BarrowsOverlay.drawFrame(event.graphics, it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			if (!enabled || !showPrayerDrainTimer) {
				stopPrayerDrainTimer()
			}
			if (!enabled) {
				showOverlay(context?.client())
			}
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		if (!enabled) {
			stopPrayerDrainTimer()
			showOverlay(context?.client())
		}
		context?.client()?.consolePrint(if (enabled) "Barrows Brothers enabled." else "Barrows Brothers disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Barrows Brothers", true)
		// RuneLite's BarrowsConfig, in its order
		s.toggle("showBrotherLoc", "Show brothers location", true)
		s.toggle("showChestValue", "Show value of chests", true)
		s.colour("brotherLocColor", "Brother location color", CYAN, false)
		s.colour("deadBrotherLocColor", "Dead brother loc. color", RED, false)
		s.toggle("showPuzzleAnswer", "Show puzzle answer", true)
		s.toggle("showPrayerDrainTimer", "Show prayer drain timer", true)
		readSettings(context.config())
		killLevels = context.config().getInt("killLevels", 0)
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.INTERFACE_OPENED, openListener)
		context.events().subscribe(Events.INTERFACE_CLOSED, closeListener)
		context.events().subscribe(Events.INTERACTING_CHANGED, interactingListener)
		context.events().subscribe(Events.MENU_OPTION_CLICKED, clickListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.addOverlay(BarrowsOverlay.panel)
		context.registerCommand("barrows", toggle)
	}

	override fun stop() {
		val client: ClientApi? = context?.client()
		stopPrayerDrainTimer()
		showOverlay(client)
		context = null
		lastTarget = null
		chestBefore = null
		chestCycle = -1
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		showBrotherLoc = config.getBoolean("showBrotherLoc", true)
		showChestValue = config.getBoolean("showChestValue", true)
		brotherLocColour = config.getColour("brotherLocColor", CYAN)
		deadBrotherLocColour = config.getColour("deadBrotherLocColor", RED)
		showPuzzleAnswer = config.getBoolean("showPuzzleAnswer", true)
		showPrayerDrainTimer = config.getBoolean("showPrayerDrainTimer", true)
	}

	@JvmStatic
	fun context(): PluginContext? = context

	/** The run's state forgotten: the kills, the last fight, the windows and the chest. */
	@JvmStatic
	fun reset() {
		killLevels = 0
		lastKilledMonsters = -1
		lastSlain.fill(false)
		lastTarget = null
		lastTargetLevel = 0
		syncUntil = 0
		syncing = false
		overlayOpen = false
		puzzleOpen = false
		chestBefore = null
		chestCycle = -1
		lastChestValue = -1L
	}

	@JvmStatic
	fun interfaceChanged(interfaceId: Int, open: Boolean) {
		if (interfaceId == OVERLAY_INTERFACE) {
			overlayOpen = open
		} else if (interfaceId == PUZZLE_INTERFACE) {
			puzzleOpen = open
		}
	}

	/** Each client cycle: the game's overlay hidden behind the panel, the prayer timer, and the kills counted. */
	@JvmStatic
	fun tick(client: ClientApi) {
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		// RuneLite's onBeforeRender: the overlay's scripts show its parts again, so they are hidden every cycle
		if (overlayOpen) {
			setOverlayHidden(client, true)
		}
		prayerTimer(client)
		countKills(client)
	}

	/** RuneLite's onGameStateChanged, as the crypt is entered or left: the timer runs while the player is in it. */
	private fun prayerTimer(client: ClientApi) {
		val inCrypt: Boolean = client.regionId() == CRYPT_REGION
		if (!inCrypt) {
			stopPrayerDrainTimer()
			lastPrayer = -1
			return
		}
		if (prayerDrainTimer == null && showPrayerDrainTimer) {
			startPrayerDrainTimer()
		}
		// the server's drain started when the crypt was entered: a drop of 8 to 13 points at once is it, so the loop starts again there
		val prayer: Int = client.skillLevel(Skill.PRAYER)
		if (lastPrayer - prayer in 8..13) {
			prayerDrainTimer?.reset()
		}
		lastPrayer = prayer
	}

	private fun startPrayerDrainTimer() {
		val timer: LoopTimer = LoopTimer("Prayer Drain", PRAYER_DRAIN_MILLIS, true)
		timer.spriteId = PRAYER_SPRITE
		timer.priority = InfoBoxPriority.MED
		timer.tooltip = "Prayer Drain"
		context?.addInfoBox(timer)
		prayerDrainTimer = timer
	}

	@JvmStatic
	fun stopPrayerDrainTimer() {
		val timer: LoopTimer = prayerDrainTimer ?: return
		context?.removeInfoBox(timer)
		prayerDrainTimer = null
	}

	/**
	 * Void's barrows_kill_levels, followed: a rise of the killed-monsters count adds the level of the brother
	 * just slain, else of the npc last fought; the count back at 0 (the chest looted) clears it.
	 */
	@JvmStatic
	fun countKills(client: ClientApi) {
		val count: Int = client.varbit(KILLED_MONSTERS_VARBIT)
		var slainLevel: Int = 0
		for (i in BROTHERS.indices) {
			val slain: Boolean = client.varbit(BROTHERS[i].killedVarbit) != 0
			if (slain && !lastSlain[i]) {
				slainLevel = brotherLevel(client, BROTHERS[i])
			}
			lastSlain[i] = slain
		}
		val last: Int = lastKilledMonsters
		lastKilledMonsters = count
		if (client.clientCycle() < syncUntil || last == -1) {
			// the server's values arriving after login (0 until they do) are no kills
			syncing = true
			return
		}
		if (syncing) {
			// the first look once they are in: a count of 0 is a run looted (or none begun) while the sum was kept
			syncing = false
			if (count == 0) {
				setKillLevels(0)
			}
			return
		}
		if (count == last) {
			return
		}
		if (count < last) {
			setKillLevels(0)
			return
		}
		val level: Int = if (slainLevel > 0) slainLevel else lastTargetLevel
		setKillLevels(killLevels + (count - last) * level)
	}

	private fun brotherLevel(client: ClientApi, brother: Brother): Int {
		val type: NpcDefinition? = client.npcType(brother.npcId)
		return type?.combatLevel() ?: 0
	}

	private fun setKillLevels(levels: Int) {
		if (levels == killLevels) {
			return
		}
		killLevels = levels
		context?.config()?.setInt("killLevels", levels)
		context?.config()?.save()
	}

	/** The reward potential, 0 to [MAX_POTENTIAL]. */
	@JvmStatic
	fun rewardPotential(): Int = minOf(killLevels, MAX_POTENTIAL)

	/** Whether [brother] is slain this run. */
	@JvmStatic
	fun isSlain(client: ClientApi, brother: Brother): Boolean = client.varbit(brother.killedVarbit) != 0

	/** RuneLite's isBarrowsLoaded: whether the Barrows hills are in the scene. */
	@JvmStatic
	fun isBarrowsLoaded(client: ClientApi): Boolean {
		val baseX: Int = client.sceneBaseX()
		val baseY: Int = client.sceneBaseY()
		val size: Int = client.sceneSize()
		val regionX: Int = (BARROWS_REGION shr 8) shl 6
		val regionY: Int = (BARROWS_REGION and 0xFF) shl 6
		return regionX + 64 > baseX && regionX < baseX + size && regionY + 64 > baseY && regionY < baseY + size
	}

	/** RuneLite's shutDown: the game's overlay shown again. */
	@JvmStatic
	fun showOverlay(client: ClientApi?) {
		if (client != null) {
			setOverlayHidden(client, false)
		}
	}

	private fun setOverlayHidden(client: ClientApi, hidden: Boolean) {
		for (component in OVERLAY_HIDDEN) {
			client.widget(OVERLAY_INTERFACE, component)?.setHidden(hidden)
		}
	}

	/** RuneLite's puzzle answer: the choice whose model is the first picture's less 3; null while there is none. */
	@JvmStatic
	fun puzzleAnswer(client: ClientApi): Widget? {
		if (!puzzleOpen) {
			return null
		}
		val first: Widget = client.widget(PUZZLE_INTERFACE, PUZZLE_FIRST) ?: return null
		if (first.modelId() < 0) {
			return null
		}
		val answer: Int = first.modelId() - 3
		for (component in PUZZLE_CHOICES) {
			val choice: Widget? = client.widget(PUZZLE_INTERFACE, component)
			if (choice != null && choice.modelId() == answer) {
				return choice
			}
		}
		return null
	}

	/** The chest searched in the crypt: the backpack is noted, to price what it gains. */
	private fun clicked(client: ClientApi, event: MenuOptionClicked) {
		if (!enabled || !showChestValue || event.option != "Search" || event.itemId >= 0) {
			return
		}
		if (!ClientFeatures.plainText(event.target ?: "").equals("Chest", ignoreCase = true) || client.regionId() != CRYPT_REGION) {
			return
		}
		chestBefore = snapshot(client.itemContainer(BACKPACK))
		chestCycle = client.clientCycle()
	}

	/** RuneLite's onWidgetLoaded for the reward window: Void has none, so the chest's loot is what the backpack gained. */
	private fun chat(client: ClientApi, event: ChatMessage) {
		if (event.type != ChatType.GAME && event.type != ChatType.GAME_FILTERED) {
			return
		}
		if (ClientFeatures.plainText(event.message ?: return) != CHEST_LOOTED) {
			return
		}
		val before: IntArray = chestBefore ?: return
		val searched: Int = chestCycle
		chestBefore = null
		chestCycle = -1
		if (!enabled || !showChestValue || client.clientCycle() - searched > CHEST_WINDOW) {
			return
		}
		val value: Long = gainedValue(client, before, snapshot(client.itemContainer(BACKPACK)))
		lastChestValue = value
		client.chatMessage("<col=ef1020>Your chest is worth around " + GroundItemRules.withCommas(value) + " coins.</col>")
	}

	/** Each item's amount in [container], as pairs of id and amount. */
	@JvmStatic
	fun snapshot(container: ItemContainer?): IntArray {
		if (container == null) {
			return IntArray(0)
		}
		val out: IntArray = IntArray(container.size() * 2)
		for (slot in 0 until container.size()) {
			out[slot * 2] = container.itemId(slot)
			out[slot * 2 + 1] = container.amount(slot)
		}
		return out
	}

	/** What the items gained from [before] to [after] are worth, at the Grand Exchange's prices. */
	@JvmStatic
	fun gainedValue(client: ClientApi, before: IntArray, after: IntArray): Long {
		val counts: HashMap<Int, Long> = HashMap()
		for (i in 0 until after.size / 2) {
			if (after[i * 2] >= 0) {
				counts[after[i * 2]] = (counts[after[i * 2]] ?: 0L) + after[i * 2 + 1]
			}
		}
		for (i in 0 until before.size / 2) {
			if (before[i * 2] >= 0) {
				counts[before[i * 2]] = (counts[before[i * 2]] ?: 0L) - before[i * 2 + 1]
			}
		}
		var total: Long = 0L
		for ((id, amount) in counts) {
			if (amount > 0L) {
				total += client.itemPrice(id).toLong() * amount
			}
		}
		return total
	}

	/** Java's Color.CYAN and Color.RED, RuneLite's defaults. */
	const val CYAN: Int = 0xFF00FFFF.toInt()
	const val RED: Int = 0xFFFF0000.toInt()
}
