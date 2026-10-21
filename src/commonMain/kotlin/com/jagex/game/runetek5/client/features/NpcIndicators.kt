package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuEntry
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.MenuBuilt
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: npcs highlighted on the ground and in the menu, after RuneLite's
 * NPC Indicators. Hold Shift and right-click an npc for Tag (this one, by its index, until it goes)
 * or Tag-All (every npc of its name, kept in the settings as a list with `*` wildcards); Untag and
 * Untag-All undo them, `::tag <name>` and `::untag <name>` edit the list. A highlighted npc is drawn
 * by its hull (the body model's box), its tile, its true (server) tile, or the south-west tile of
 * either, named over its head and on the minimap when asked, its menu entries recoloured, and left
 * alone once its health bar is empty when asked. The respawn timer remembers where a tagged npc
 * stood and how long it took to come back, and counts down on that tile after it dies.
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md) on API 7: the hull, the true tile, the dying
 * flag, the menu target text and the text setting are what API 6 lacked.
 */
object NpcIndicators : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("npcindicators", "NPC Indicators", "2", PluginApi.VERSION, "Highlights the npcs you tag (Shift right-click: Tag, Tag-All) by hull or tile, names them, colours their menu entries and times their respawn (::tag <name>, ::untag <name>).", "Pixel")

	/** The colours the settings offer, cyan first as RuneLite has it. */
	@JvmField
	val COLOUR_NAMES: Array<String> = arrayOf("Cyan", "Yellow", "Red", "Green", "Blue", "Magenta", "Orange", "White", "Purple", "Pink")

	private val COLOUR_VALUES: IntArray = intArrayOf(0xFF00FFFF.toInt(), 0xFFFFFF00.toInt(), 0xFFFF0000.toInt(), 0xFF00FF00.toInt(), 0xFF0000FF.toInt(), 0xFFFF00FF.toInt(), 0xFFFF8000.toInt(), 0xFFFFFFFF.toInt(), 0xFF8000FF.toInt(), 0xFFFF80C0.toInt())

	/** The menu highlight styles, as RuneLite names them. */
	@JvmField
	val MENU_STYLES: Array<String> = arrayOf("None", "Level", "Name", "Both")

	const val MENU_NONE: Int = 0

	const val MENU_LEVEL: Int = 1

	const val MENU_NAME: Int = 2

	const val MENU_BOTH: Int = 3

	private const val DEFAULT_FILL_OPACITY: Int = 20

	private const val DEFAULT_BORDER_WIDTH: Int = 2

	private const val MAX_BORDER_WIDTH: Int = 5

	private const val MAX_NAMES: Int = 300

	/** How far from the player a despawn still counts as a death (RuneLite's rule), in tiles. */
	private const val DEATH_RANGE: Int = 15

	/** A respawn longer than this is not a respawn (the npc was elsewhere), in server ticks. */
	private const val MAX_RESPAWN_TICKS: Int = 500

	/** Client cycles (GameTick) to a server tick. */
	private const val CYCLES_PER_TICK: Int = 30

	private const val SHADOW: Int = 0xFF000000.toInt()

	/** The count-up before a respawn time is known. */
	private const val DIM_LABEL: Int = 0xFFB0B0B0.toInt()

	/** The client's click types of an npc's own entries (MiniMenuAction OPNPC1..6, TGT_NPC), and the attack penalty added above them. */
	private val NPC_CLICK_TYPES: IntArray = intArrayOf(25, 20, 44, 46, 60, 1008, 30)

	private const val LEVEL_PENALTY: Int = 2000

	/**
	 * A tagged npc's remembered spawn: where it last appeared (its spawn tile, once it has been seen
	 * to appear), the index it had, how long it took to come back, and when it last died.
	 */
	class Memory(@JvmField val name: String, @JvmField var worldX: Int, @JvmField var worldY: Int, @JvmField var level: Int, @JvmField var index: Int) {
		/** The shortest respawn seen, in client cycles; -1 until one was. */
		@JvmField
		var respawnCycles: Int = -1

		/** The cycle it last died on; -1 while it is alive. */
		@JvmField
		var diedOnCycle: Int = -1
	}

	/** How far from where it died a returning npc of the same name still counts as the same spawn, in tiles. */
	private const val RESPAWN_RANGE: Int = 3

	@JvmField
	var enabled: Boolean = true

	private var highlightHull: Boolean = true

	private var highlightTile: Boolean = false

	private var highlightTrueTile: Boolean = false

	private var highlightSouthWestTile: Boolean = false

	private var highlightSouthWestTrueTile: Boolean = false

	private var colour: Int = COLOUR_VALUES[0]

	private var fillOpacity: Int = DEFAULT_FILL_OPACITY

	private var borderWidth: Int = DEFAULT_BORDER_WIDTH

	private var drawNames: Boolean = false

	private var drawMinimapNames: Boolean = false

	private var menuStyle: Int = MENU_NONE

	private var ignoreDead: Boolean = true

	/** 0 for none, else the colour a dying npc's menu entries get. */
	private var deadMenuColour: Int = 0

	/** A field, so that the smoke test can turn it on. */
	@JvmField
	var showRespawnTimer: Boolean = false

	/** How many respawn timers the last frame drew, for the smoke test. */
	@JvmField
	var respawnsDrawnLastFrame: Int = 0

	/** The names and wildcard rules from the settings, in order. */
	@JvmField
	val names: ArrayList<String> = ArrayList()

	/** The indexes of the npcs tagged one by one, for this session. */
	@JvmField
	val taggedIndexes: HashSet<Int> = HashSet()

	/** Remembered spawns, and the one each living npc index came from. */
	private val memories: ArrayList<Memory> = ArrayList()

	private val alive: HashMap<Int, Memory> = HashMap()

	/** How many npcs the last frame highlighted, for the smoke test. */
	@JvmField
	var drawnLastFrame: Int = 0

	private var context: PluginContext? = null

	private val xs: IntArray = IntArray(16)

	private val ys: IntArray = IntArray(16)

	private val tileXs: IntArray = IntArray(4)

	private val tileYs: IntArray = IntArray(4)

	private val point: IntArray = IntArray(2)

	private val area: IntArray = IntArray(4)

	private var tagAction: Int = -1

	private var untagAction: Int = -1

	private var tagAllAction: Int = -1

	private var untagAllAction: Int = -1

	private val overlay: Overlay = Overlay { g, view -> draw(g, view) }

	/** The minimap is an interface drawn over the game view: its names go on at the end of the frame, over everything. */
	private val frameListener: EventListener<FrameDraw> = EventListener { event -> drawMinimapNames(event.graphics) }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	/** The Shift menu of an npc: Tag or Untag, and Tag-All or Untag-All (not for a name a wildcard already takes). */
	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (!enabled || event.target !== MenuTarget.NPC || !client.isKeyHeld(KeyCode.SHIFT)) {
			return@EventListener
		}
		val npc: Npc = client.npcByIndex(event.id) ?: return@EventListener
		val name: String = npc.name() ?: return@EventListener
		// the last entry added is the highest: Tag ends up on top
		if (!matchedByWildcard(name)) {
			client.addMenuEntry(if (listed(name)) "Untag-All" else "Tag-All", if (listed(name)) untagAllAction else tagAllAction)
		}
		client.addMenuEntry(if (taggedIndexes.contains(npc.index())) "Untag" else "Tag", if (taggedIndexes.contains(npc.index())) untagAction else tagAction)
	}

	/** The finished menu: a highlighted npc's entries in the highlight colour, a dying one's in the dead colour. */
	private val menuBuiltListener: EventListener<MenuBuilt> = EventListener { _ ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (!enabled || (menuStyle == MENU_NONE && deadMenuColour == 0)) {
			return@EventListener
		}
		for (i in 0 until client.menuEntryCount()) {
			recolourEntry(client, i)
		}
	}

	private fun recolourEntry(client: ClientApi, i: Int) {
		val entry: MenuEntry = client.menuEntry(i) ?: return
		if (!isNpcEntry(entry.clickType())) {
			return
		}
		val npc: Npc = client.npcByIndex(entry.entityUid().toInt()) ?: return
		val target: String = entry.target() ?: return
		if (deadMenuColour != 0 && npc.isDying()) {
			client.retargetMenuEntry(i, recolourTarget(target, MENU_BOTH, deadMenuColour))
		} else if (menuStyle != MENU_NONE && highlighted(npc)) {
			client.retargetMenuEntry(i, recolourTarget(target, menuStyle, colour))
		}
	}

	private val spawnListener: EventListener<Npc> = EventListener { npc -> spawned(npc) }

	private val despawnListener: EventListener<Npc> = EventListener { npc -> despawned(npc) }

	private val stateListener: EventListener<GameStateChanged> = EventListener { _ ->
		// a login or a hop: the indexes mean nothing now, and nothing is waiting to come back
		taggedIndexes.clear()
		alive.clear()
		for (memory in memories) {
			memory.diedOnCycle = -1
		}
	}

	private val tagCommand: CommandHandler = CommandHandler { _, args -> command(args, true) }

	private val untagCommand: CommandHandler = CommandHandler { _, args -> command(args, false) }

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		val context: PluginContext = this.context ?: return@CommandHandler
		enabled = !enabled
		context.config().setBoolean("enabled", enabled)
		context.config().save()
		context.client().consolePrint(if (enabled) "NPC indicators on." else "NPC indicators off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		readSettings(context.config())
		context.settings().toggle("enabled", "NPC indicators", true)
		context.settings().toggle("highlightHull", "Highlight hull", true)
		context.settings().toggle("highlightTile", "Highlight tile", false)
		context.settings().toggle("highlightTrueTile", "Highlight true tile", false)
		context.settings().toggle("highlightSouthWestTile", "Highlight south-west tile", false)
		context.settings().toggle("highlightSouthWestTrueTile", "Highlight south-west true tile", false)
		context.settings().choice("colour", "Highlight colour", COLOUR_NAMES[0], COLOUR_NAMES)
		context.settings().number("fillOpacity", "Fill opacity", DEFAULT_FILL_OPACITY, 0, 255, 5)
		context.settings().number("borderWidth", "Border width", DEFAULT_BORDER_WIDTH, 1, MAX_BORDER_WIDTH, 1)
		context.settings().text("names", "NPCs to highlight", "", MAX_NAMES)
		context.settings().toggle("drawNames", "Draw names above npcs", false)
		context.settings().toggle("drawMinimapNames", "Draw names on the minimap", false)
		context.settings().choice("menuStyle", "Highlight menu style", MENU_STYLES[0], MENU_STYLES)
		context.settings().toggle("ignoreDead", "Ignore dead npcs", true)
		context.settings().choice("deadMenuColour", "Dead npc menu colour", "None", arrayOf("None") + COLOUR_NAMES)
		context.settings().toggle("showRespawnTimer", "Show respawn timer", false)
		tagAction = context.registerMenuAction(MenuAction { _, _, _, index -> tag(index) })
		untagAction = context.registerMenuAction(MenuAction { _, _, _, index -> untag(index) })
		tagAllAction = context.registerMenuAction(MenuAction { _, _, _, index -> nameOf(index)?.let { addName(it) } })
		untagAllAction = context.registerMenuAction(MenuAction { _, _, _, index -> nameOf(index)?.let { removeName(it) } })
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.events().subscribe(Events.MENU_BUILT, menuBuiltListener)
		context.events().subscribe(Events.NPC_SPAWNED, spawnListener)
		context.events().subscribe(Events.NPC_DESPAWNED, despawnListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.addOverlay(overlay)
		context.registerCommand("tag", tagCommand)
		context.registerCommand("untag", untagCommand)
		context.registerCommand("npcindicators", toggle)
	}

	override fun stop() {
		context = null
		taggedIndexes.clear()
		alive.clear()
		memories.clear()
	}

	private fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		highlightHull = config.getBoolean("highlightHull", true)
		highlightTile = config.getBoolean("highlightTile", false)
		highlightTrueTile = config.getBoolean("highlightTrueTile", false)
		highlightSouthWestTile = config.getBoolean("highlightSouthWestTile", false)
		highlightSouthWestTrueTile = config.getBoolean("highlightSouthWestTrueTile", false)
		colour = colourNamed(config.get("colour", COLOUR_NAMES[0]), COLOUR_VALUES[0])
		fillOpacity = config.getInt("fillOpacity", DEFAULT_FILL_OPACITY).coerceIn(0, 255)
		borderWidth = config.getInt("borderWidth", DEFAULT_BORDER_WIDTH).coerceIn(1, MAX_BORDER_WIDTH)
		setNames(config.get("names", ""))
		rememberAll() // a name typed into the list: the npcs of it that are here get their spawns remembered
		drawNames = config.getBoolean("drawNames", false)
		drawMinimapNames = config.getBoolean("drawMinimapNames", false)
		menuStyle = MENU_STYLES.indexOf(config.get("menuStyle", MENU_STYLES[0])).coerceAtLeast(0)
		ignoreDead = config.getBoolean("ignoreDead", true)
		deadMenuColour = colourNamed(config.get("deadMenuColour", "None"), 0)
		showRespawnTimer = config.getBoolean("showRespawnTimer", false)
	}

	/** The colour of a settings name, or [none] for one the list has not ("None" among them). */
	@JvmStatic
	fun colourNamed(name: String, none: Int): Int {
		for (i in COLOUR_NAMES.indices) {
			if (COLOUR_NAMES[i].equals(name, ignoreCase = true)) {
				return COLOUR_VALUES[i]
			}
		}
		return none
	}

	// --- the list ---

	/** Takes the list as the setting holds it: names separated by commas, blanks dropped. */
	@JvmStatic
	fun setNames(csv: String) {
		names.clear()
		for (part in csv.split(',')) {
			val name: String = part.trim()
			if (name.isNotEmpty()) {
				names.add(name)
			}
		}
	}

	@JvmStatic
	fun namesCsv(): String {
		return names.joinToString(", ")
	}

	/** Whether [name] is on the list as itself (not through a wildcard). */
	@JvmStatic
	fun listed(name: String): Boolean {
		for (rule in names) {
			if (rule.equals(name, ignoreCase = true)) {
				return true
			}
		}
		return false
	}

	@JvmStatic
	fun matchedByWildcard(name: String): Boolean {
		for (rule in names) {
			if (rule.indexOf('*') >= 0 && wildcardMatches(rule, name)) {
				return true
			}
		}
		return false
	}

	@JvmStatic
	fun matches(name: String): Boolean {
		for (rule in names) {
			if (wildcardMatches(rule, name)) {
				return true
			}
		}
		return false
	}

	/** Whether [text] is [pattern], where `*` stands for anything (RuneLite's wildcard), case aside. */
	@JvmStatic
	fun wildcardMatches(pattern: String, text: String): Boolean {
		var p: Int = 0
		var t: Int = 0
		var starP: Int = -1
		var starT: Int = 0
		while (t < text.length) {
			if (p < pattern.length && pattern[p] == '*') {
				starP = p++
				starT = t
			} else if (p < pattern.length && pattern[p].equals(text[t], ignoreCase = true)) {
				p++
				t++
			} else if (starP >= 0) {
				p = starP + 1
				t = ++starT
			} else {
				return false
			}
		}
		while (p < pattern.length && pattern[p] == '*') {
			p++
		}
		return p == pattern.length
	}

	private fun saveNames() {
		val config: PluginConfig = context?.config() ?: return
		config.set("names", namesCsv())
		config.save()
	}

	private fun addName(name: String) {
		if (!listed(name)) {
			names.add(name)
			saveNames()
			rememberAll()
		}
	}

	private fun removeName(name: String) {
		var removed: Boolean = false
		var i: Int = names.size - 1
		while (i >= 0) {
			if (names[i].equals(name, ignoreCase = true)) {
				names.removeAt(i)
				removed = true
			}
			i--
		}
		if (removed) {
			saveNames()
		}
	}

	private fun command(args: Array<String>, add: Boolean) {
		val client: ClientApi = context?.client() ?: return
		val name: String = args.joinToString(" ").trim()
		if (name.isEmpty()) {
			client.consolePrint("NPCs highlighted: " + (if (names.isEmpty()) "none" else namesCsv()))
			return
		}
		if (add) {
			addName(name)
		} else {
			removeName(name)
		}
		client.consolePrint((if (add) "Tagged " else "Untagged ") + name + "; the list is now " + (if (names.isEmpty()) "empty" else namesCsv()) + ".")
	}

	// --- tagging one npc ---

	private fun nameOf(index: Int): String? {
		return context?.client()?.npcByIndex(index)?.name()
	}

	@JvmStatic
	fun tag(index: Int) {
		val npc: Npc = context?.client()?.npcByIndex(index) ?: return
		taggedIndexes.add(index)
		remember(npc, false)
	}

	@JvmStatic
	fun untag(index: Int) {
		taggedIndexes.remove(index)
		val npc: Npc? = context?.client()?.npcByIndex(index)
		if (npc == null || !highlighted(npc)) {
			alive.remove(index)
		}
	}

	@JvmStatic
	fun highlighted(npc: Npc): Boolean {
		if (taggedIndexes.contains(npc.index())) {
			return true
		}
		val name: String = npc.name() ?: return false
		return matches(name)
	}

	// --- the respawn timer ---

	/**
	 * The memory a returning npc belongs to: a dead one with its index, else a dead one of its name
	 * within [RESPAWN_RANGE] tiles (it came back on its spawn tile, wherever it was tagged), else
	 * one on its very tile, else none.
	 */
	private fun memoryFor(name: String, index: Int, worldX: Int, worldY: Int, level: Int): Memory? {
		var near: Memory? = null
		var here: Memory? = null
		for (memory in memories) {
			if (memory.name != name || memory.level != level) {
				continue
			}
			if (memory.diedOnCycle >= 0 && memory.index == index) {
				return memory
			}
			val dx: Int = kotlin.math.abs(memory.worldX - worldX)
			val dy: Int = kotlin.math.abs(memory.worldY - worldY)
			if (dx == 0 && dy == 0) {
				here = memory
			} else if (memory.diedOnCycle >= 0 && dx <= RESPAWN_RANGE && dy <= RESPAWN_RANGE) {
				near = memory
			}
		}
		return near ?: here
	}

	/**
	 * A highlighted npc that is here: its spawn is remembered, and a return after a death teaches the
	 * respawn time and ends the timer. [appeared] says the npc just spawned, so its tile is its spawn tile.
	 */
	private fun remember(npc: Npc, appeared: Boolean) {
		val client: ClientApi = context?.client() ?: return
		if (alive.containsKey(npc.index())) {
			return
		}
		val name: String = npc.name() ?: return
		val worldX: Int = client.sceneBaseX() + npc.serverTileX()
		val worldY: Int = client.sceneBaseY() + npc.serverTileY()
		var memory: Memory? = memoryFor(name, npc.index(), worldX, worldY, npc.level())
		if (memory == null) {
			memory = Memory(name, worldX, worldY, npc.level(), npc.index())
			memories.add(memory)
		}
		memory.index = npc.index()
		if (appeared) {
			memory.worldX = worldX
			memory.worldY = worldY
		}
		alive[npc.index()] = memory
		if (memory.diedOnCycle >= 0) {
			val respawn: Int = client.clientCycle() - memory.diedOnCycle
			if (respawn > 0 && respawn <= MAX_RESPAWN_TICKS * CYCLES_PER_TICK && (memory.respawnCycles < 0 || respawn < memory.respawnCycles)) {
				memory.respawnCycles = respawn
			}
			memory.diedOnCycle = -1
		}
	}

	private fun rememberAll() {
		val client: ClientApi = context?.client() ?: return
		for (i in 0 until client.npcCount()) {
			val npc: Npc = client.npc(i) ?: continue
			if (highlighted(npc)) {
				remember(npc, false)
			}
		}
	}

	private fun spawned(npc: Npc) {
		if (enabled && highlighted(npc)) {
			remember(npc, true)
		}
	}

	/** A remembered npc that went while near the player died: the timer starts. Its tag stays: a respawned npc keeps its index. */
	private fun despawned(npc: Npc) {
		val client: ClientApi = context?.client() ?: return
		val memory: Memory = alive.remove(npc.index()) ?: return
		val me: Player? = client.localPlayer()
		val near: Boolean = me != null && kotlin.math.abs(client.sceneBaseX() + me.serverTileX() - memory.worldX) <= DEATH_RANGE && kotlin.math.abs(client.sceneBaseY() + me.serverTileY() - memory.worldY) <= DEATH_RANGE
		if (near || npc.isDying()) {
			memory.diedOnCycle = client.clientCycle()
		}
	}

	// --- drawing ---

	private fun isNpcEntry(clickType: Int): Boolean {
		for (type in NPC_CLICK_TYPES) {
			if (clickType == type || clickType == type + LEVEL_PENALTY) {
				return true
			}
		}
		return false
	}

	/**
	 * The target of an npc's entry, `<col=ffff00>Name<col=xxxxxx> (level: N)`, with its name (the
	 * first coloured run), its level (the runs after it) or both in [argb]'s colour.
	 */
	@JvmStatic
	fun recolourTarget(target: String, style: Int, argb: Int): String {
		val hex: String = hex6(argb)
		val out: StringBuilder = StringBuilder()
		var run: Int = 0
		var at: Int = 0
		if (!target.startsWith("<col=")) {
			// an uncoloured target: the whole of it is the first run
			if (style == MENU_NAME || style == MENU_BOTH) {
				out.append("<col=").append(hex).append('>')
			}
			run = 1
		}
		while (at < target.length) {
			val tag: Int = target.indexOf("<col=", at)
			val end: Int = if (tag < 0) -1 else target.indexOf('>', tag)
			if (end < 0) {
				out.append(target, at, target.length)
				break
			}
			out.append(target, at, tag)
			run++
			val recolour: Boolean = style == MENU_BOTH || (style == MENU_NAME && run == 1) || (style == MENU_LEVEL && run > 1)
			out.append("<col=").append(if (recolour) hex else target.substring(tag + 5, end)).append('>')
			at = end + 1
		}
		return out.toString()
	}

	private fun hex6(argb: Int): String {
		val digits: String = "0123456789abcdef"
		val out: StringBuilder = StringBuilder()
		for (shift in 20 downTo 0 step 4) {
			out.append(digits[(argb ushr shift) and 0xF])
		}
		return out.toString()
	}

	private fun draw(g: Graphics, view: Viewport) {
		drawnLastFrame = 0
		respawnsDrawnLastFrame = 0
		val client: ClientApi = context?.client() ?: return
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val fill: Int = (fillOpacity shl 24) or (colour and 0xFFFFFF)
		for (i in 0 until client.npcCount()) {
			val npc: Npc? = client.npc(i)
			if (npc != null && highlighted(npc) && !(ignoreDead && npc.isDying())) {
				drawNpc(g, view, client, npc, fill)
				drawnLastFrame++
			}
		}
		if (showRespawnTimer) {
			drawRespawns(g, view, client)
		}
	}

	private fun drawNpc(g: Graphics, view: Viewport, client: ClientApi, npc: Npc, fill: Int) {
		val level: Int = npc.level()
		if (highlightHull) {
			val count: Int = client.actorHull(npc, xs, ys)
			if (count >= 3) {
				for (c in 0 until count) {
					xs[c] += view.x
					ys[c] += view.y
				}
				g.fillPolygon(xs, ys, count, fill)
				outline(g, xs, ys, count)
			} else {
				// a corner of the body is off the screen: its tile stands in, so the highlight does not blink out at the edge
				tile(g, view, client, npc.tileX(), npc.tileY(), level, npc.sizeInTiles(), fill)
			}
		}
		if (highlightTile) {
			tile(g, view, client, npc.tileX(), npc.tileY(), level, npc.sizeInTiles(), fill)
		}
		if (highlightTrueTile) {
			tile(g, view, client, npc.serverTileX(), npc.serverTileY(), level, npc.sizeInTiles(), fill)
		}
		if (highlightSouthWestTile) {
			tile(g, view, client, npc.tileX(), npc.tileY(), level, 1, fill)
		}
		if (highlightSouthWestTrueTile) {
			tile(g, view, client, npc.serverTileX(), npc.serverTileY(), level, 1, fill)
		}
		val name: String? = npc.name()
		if (name != null && drawNames && g.fontReady() && client.projectActor(npc, npc.overheadHeight() + 40, point)) {
			g.textCentered(view.x + point[0], view.y + point[1], name, colour, SHADOW)
		}
	}

	/** At the end of the frame: each highlighted npc's name over its dot on the minimap. */
	private fun drawMinimapNames(g: Graphics) {
		val client: ClientApi = context?.client() ?: return
		if (!enabled || !drawMinimapNames || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN || !client.minimapArea(area)) {
			return
		}
		g.clip(area[0], area[1], area[2], area[3])
		for (i in 0 until client.npcCount()) {
			val npc: Npc? = client.npc(i)
			val name: String? = npc?.name()
			if (npc != null && name != null && highlighted(npc) && !(ignoreDead && npc.isDying()) && client.projectActorMinimap(npc, point)) {
				g.textCentered(point[0], point[1] - 2, name, colour, SHADOW)
			}
		}
		g.unclip()
	}

	private fun tile(g: Graphics, view: Viewport, client: ClientApi, tileX: Int, tileY: Int, level: Int, size: Int, fill: Int) {
		if (!client.tilePolygon(tileX, tileY, level, size, tileXs, tileYs)) {
			return
		}
		for (c in 0 until 4) {
			tileXs[c] += view.x
			tileYs[c] += view.y
		}
		g.fillPolygon(tileXs, tileYs, 4, fill)
		outline(g, tileXs, tileYs, 4)
	}

	private fun outline(g: Graphics, px: IntArray, py: IntArray, count: Int) {
		for (i in 0 until count) {
			val j: Int = if (i + 1 == count) 0 else i + 1
			g.thickLine(px[i], py[i], px[j], py[j], borderWidth, colour)
		}
	}

	/** The seconds left on each dead npc's tile, once its respawn time is known; a timer that ran out is dropped. */
	/** For the smoke test: how many remembered npcs are dead now, and how many have a respawn time. */
	@JvmStatic
	fun pendingRespawns(): Int {
		return memories.count { it.diedOnCycle >= 0 }
	}

	@JvmStatic
	fun learnedRespawns(): Int {
		return memories.count { it.respawnCycles >= 0 }
	}

	private fun drawRespawns(g: Graphics, view: Viewport, client: ClientApi) {
		val now: Int = client.clientCycle()
		val level: Int = client.localPlayer()?.level() ?: 0
		for (memory in memories) {
			if (memory.diedOnCycle >= 0 && memory.level == level) {
				drawRespawn(g, view, client, memory, now, level)
			}
		}
	}

	/**
	 * The time left until the npc is back, counting down on its tile; while its respawn time is not
	 * yet known (the first death teaches it) the time since it died counts up, dimmed, so the
	 * tile is marked from the first death on.
	 */
	private fun drawRespawn(g: Graphics, view: Viewport, client: ClientApi, memory: Memory, now: Int, level: Int) {
		val known: Boolean = memory.respawnCycles >= 0
		val shown: Int = if (known) memory.diedOnCycle + memory.respawnCycles - now else now - memory.diedOnCycle
		if ((known && shown <= 0) || shown > MAX_RESPAWN_TICKS * CYCLES_PER_TICK) {
			memory.diedOnCycle = -1 // back by now, or never coming back here
			return
		}
		val sceneX: Int = memory.worldX - client.sceneBaseX()
		val sceneY: Int = memory.worldY - client.sceneBaseY()
		if (!client.tilePolygon(sceneX, sceneY, level, 1, tileXs, tileYs)) {
			return
		}
		for (c in 0 until 4) {
			tileXs[c] += view.x
			tileYs[c] += view.y
		}
		outline(g, tileXs, tileYs, 4)
		respawnsDrawnLastFrame++
		if (g.fontReady() && client.projectTile(sceneX, sceneY, level, 0, point)) {
			// client cycles are 20 ms: tenths of a second with one decimal
			val tenths: Int = shown / 5
			g.textCentered(view.x + point[0], view.y + point[1] + g.fontHeight() / 2, (if (known) "" else "+") + (tenths / 10) + "." + (tenths % 10), if (known) colour else DIM_LABEL, SHADOW)
		}
	}
}
