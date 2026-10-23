package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Timer
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's NPC Aggression Timer. Aggressive monsters stop attacking you
 * once you have stayed in one area for 10 minutes: a timer counts down to it and the area's edges can be
 * drawn on the ground (in one colour while they are still aggressive, another once not), with a
 * notification when the time is up. It shows near the monsters you name, near your slayer task's, or
 * always.
 *
 * Void's tolerance, not OSRS's two areas: one 21 by 21 square around where its clock started (10 tiles
 * each way); stepping out of it starts a new square and the 10 minutes again (Tolerance). The Wilderness
 * is left out, as RuneLite leaves it. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object NpcAggression : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("npcUnaggroArea", "NPC Aggression Timer", "1", PluginApi.VERSION, "Times how long until monsters stop being aggressive in the area you stay in, and shows the area.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = false

	/** Void's Tolerance: 10 minutes, a square 10 tiles each way. */
	const val TOLERANCE_MILLIS: Long = 600_000L
	const val RADIUS: Int = 10

	/** The infobox's picture: the Attack skill's sword. */
	const val ICON_SPRITE: Int = 197

	@JvmField var npcUnaggroAlwaysActive: Boolean = false
	@JvmField var npcUnaggroNames: String = ""
	@JvmField var npcUnaggroShowTimer: Boolean = true
	@JvmField var npcUnaggroShowAreaLines: Boolean = false
	@JvmField var npcAggroAreaColor: Int = 0x64FFFF00
	@JvmField var npcUnaggroAreaColor: Int = 0xFFFFFF00.toInt()
	@JvmField var notifyExpire: Boolean = false
	@JvmField var hideIfOutOfCombat: Boolean = false
	@JvmField var showOnSlayerTask: Boolean = false

	/** The area's centre (world tile and level) and when its clock started; -1 before one. */
	@JvmField var centreX: Int = -1
	@JvmField var centreY: Int = -1
	@JvmField var plane: Int = -1
	@JvmField var started: Long = 0L

	/** Whether the timer shows: a named monster, a task's or "always" makes it active. */
	@JvmField var active: Boolean = false
	@JvmField var notified: Boolean = false

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	@JvmField var timer: Timer? = null

	/** For the smoke test: the edge pieces the last frame drew. */
	@JvmField var linesDrawn: Int = 0

	private var names: List<String> = emptyList()
	private val point: IntArray = IntArray(2)
	private val previous: IntArray = IntArray(2)
	private var context: PluginContext? = null

	/** RuneLite's NpcAggroAreaOverlay, under the interfaces. */
	private val overlay: Overlay = Overlay { g, view -> if (enabled) context?.client()?.let { drawArea(g, view, it) } }

	// the square is followed while off too, so switching on shows the time Void is counting
	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state === GameState.LOGGED_IN && event.previous !== GameState.LOGGED_IN) {
			// Void's playerSpawn: a new square where the player comes in, the clock running on
			centreX = -1
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		npcUnaggroAlwaysActive = !npcUnaggroAlwaysActive
		context?.config()?.setBoolean("npcUnaggroAlwaysActive", npcUnaggroAlwaysActive)
		context?.config()?.save()
		context?.client()?.consolePrint(if (npcUnaggroAlwaysActive) "The aggression timer is always on." else "The aggression timer shows near the monsters named.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's NpcAggroAreaConfig, in its order
		s.toggle("enabled", "NPC Aggression Timer", false)
		s.toggle("npcUnaggroAlwaysActive", "Always active", false)
		s.text("npcUnaggroNames", "NPC names (comma separated, * wildcards)", "", MAX_TEXT)
		s.toggle("npcUnaggroShowTimer", "Show timer", true)
		s.toggle("npcUnaggroShowAreaLines", "Show area lines", false)
		s.colour("npcAggroAreaColor", "Aggressive color", 0x64FFFF00, true)
		s.colour("npcUnaggroAreaColor", "Unaggressive color", 0xFFFFFF00.toInt(), true)
		s.toggle("notifyExpire", "Notify expiration", false)
		s.toggle("hideIfOutOfCombat", "Hide when out of combat", false)
		s.toggle("showOnSlayerTask", "Show on slayer task", false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.addOverlay(overlay)
		context.registerCommand("aggressiontimer", toggle)
	}

	override fun stop() {
		removeTimer()
		context = null
		centreX = -1
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		npcUnaggroAlwaysActive = config.getBoolean("npcUnaggroAlwaysActive", false)
		npcUnaggroNames = config.get("npcUnaggroNames", "")
		npcUnaggroShowTimer = config.getBoolean("npcUnaggroShowTimer", true)
		npcUnaggroShowAreaLines = config.getBoolean("npcUnaggroShowAreaLines", false)
		npcAggroAreaColor = config.getColour("npcAggroAreaColor", 0x64FFFF00)
		npcUnaggroAreaColor = config.getColour("npcUnaggroAreaColor", 0xFFFFFF00.toInt())
		notifyExpire = config.getBoolean("notifyExpire", false)
		hideIfOutOfCombat = config.getBoolean("hideIfOutOfCombat", false)
		showOnSlayerTask = config.getBoolean("showOnSlayerTask", false)
		names = npcUnaggroNames.split(',').map { it.trim().lowercase() }.filter { it.isNotEmpty() }
	}

	/** Whether world tile ([x], [y]) on [level] is in the area (Void's tile.toCuboid(10)). */
	@JvmStatic
	fun inArea(x: Int, y: Int, level: Int): Boolean =
		centreX >= 0 && level == plane && x in centreX - RADIUS..centreX + RADIUS && y in centreY - RADIUS..centreY + RADIUS

	@JvmStatic
	fun remainingMillis(): Long = maxOf(0L, TOLERANCE_MILLIS - (clock() - started))

	/** Void's Tolerance.moved: out of the square, a new one around the player and the clock again. */
	@JvmStatic
	fun tick(client: ClientApi) {
		val player: Player = client.localPlayer() ?: return
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val x: Int = player.tileX() + client.sceneBaseX()
		val y: Int = player.tileY() + client.sceneBaseY()
		if (centreX < 0) {
			newArea(x, y, player.level(), started == 0L)
		} else if (!inArea(x, y, player.level())) {
			newArea(x, y, player.level(), true)
		}
		active = enabled && (npcUnaggroAlwaysActive || monsterNear(client)) && CombatLevel.wildernessLevel(x, y) == 0
		if (active && remainingMillis() == 0L && !notified) {
			notified = true
			if (notifyExpire) {
				client.notify("NPC aggression has expired!")
			}
		}
		val show: Boolean = active && npcUnaggroShowTimer && !(hideIfOutOfCombat && player.interacting() == null)
		if (show && timer == null) {
			addTimer()
		} else if (!show) {
			removeTimer()
		}
	}

	private fun newArea(x: Int, y: Int, level: Int, restart: Boolean) {
		centreX = x
		centreY = y
		plane = level
		if (restart) {
			started = clock()
			notified = false
			removeTimer()
		}
	}

	/** RuneLite's onNpcSpawned: a monster named, or the slayer task's, around. */
	private fun monsterNear(client: ClientApi): Boolean {
		for (i in 0 until client.npcCount()) {
			val npc: Npc = client.npc(i) ?: continue
			if (matches(npc.name()) || (showOnSlayerTask && Slayer.isTarget(npc))) {
				return true
			}
		}
		return false
	}

	@JvmStatic
	fun matches(name: String?): Boolean {
		if (name == null || names.isEmpty()) {
			return false
		}
		val lower: String = name.lowercase()
		return names.any { NpcIndicators.wildcardMatches(it, lower) }
	}

	private fun addTimer() {
		val box: Timer = Timer("Aggression", remainingMillis())
		box.spriteId = ICON_SPRITE
		box.tooltip = "Monsters here stop being aggressive when this runs out"
		timer = box
		context?.addInfoBox(box)
	}

	private fun removeTimer() {
		timer?.let { context?.removeInfoBox(it) }
		timer = null
	}

	/** RuneLite's NpcAggroAreaOverlay: the square's edges along the ground, aggressive or not by the clock. */
	private fun drawArea(g: Graphics, view: Viewport, client: ClientApi) {
		linesDrawn = 0
		val player: Player = client.localPlayer() ?: return
		if (!active || !npcUnaggroShowAreaLines || centreX < 0 || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		if (hideIfOutOfCombat && player.interacting() == null) {
			return
		}
		val colour: Int = if (remainingMillis() > 0L) npcAggroAreaColor else npcUnaggroAreaColor
		val minX: Int = centreX - RADIUS - client.sceneBaseX()
		val minY: Int = centreY - RADIUS - client.sceneBaseY()
		val maxX: Int = minX + RADIUS * 2 + 1
		val maxY: Int = minY + RADIUS * 2 + 1
		edge(g, view, client, minX, minY, maxX, minY, colour)
		edge(g, view, client, maxX, minY, maxX, maxY, colour)
		edge(g, view, client, maxX, maxY, minX, maxY, colour)
		edge(g, view, client, minX, maxY, minX, minY, colour)
	}

	/** One edge, tile corner by tile corner so it follows the ground. */
	private fun edge(g: Graphics, view: Viewport, client: ClientApi, x0: Int, y0: Int, x1: Int, y1: Int, colour: Int) {
		val steps: Int = maxOf(kotlin.math.abs(x1 - x0), kotlin.math.abs(y1 - y0))
		var hasPrevious: Boolean = false
		for (i in 0..steps) {
			val x: Int = x0 + (x1 - x0) * i / steps
			val y: Int = y0 + (y1 - y0) * i / steps
			if (!client.projectPoint(x * TILE, y * TILE, plane, 0, point)) {
				hasPrevious = false
				continue
			}
			if (hasPrevious) {
				g.thickLine(view.x + previous[0], view.y + previous[1], view.x + point[0], view.y + point[1], 2, colour)
				linesDrawn++
			}
			previous[0] = point[0]
			previous[1] = point[1]
			hasPrevious = true
		}
	}

	private const val TILE: Int = 512
	private const val MAX_TEXT: Int = 500
}
