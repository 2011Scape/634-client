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
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Implings. Each kind of impling can be highlighted (its outline
 * and name in its colour, and its name on the minimap if you like) or announced ("Dragon impling is in the
 * area") when it appears; in Puro-Puro the implings' spawn tiles can be shown with their kind.
 *
 * Void's implings: baby to dragon, as RuneLite's, and the pirate, spirit, zombie and kingly implings of
 * 2011, after them; crystal and lucky implings are OSRS-only. The ids are Void's, in the open world and in
 * Puro-Puro, and its Puro-Puro spawn tiles. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object Implings : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("implings", "Implings", "1", PluginApi.VERSION, "Highlights or announces the implings you choose, and shows Puro-Puro's impling spawn tiles.", "Pixel")

	/** RuneLite's ImplingMode. */
	@JvmField
	val MODES: Array<String> = arrayOf("None", "Highlight", "Notify")

	const val NONE: Int = 0
	const val HIGHLIGHT: Int = 1
	const val NOTIFY: Int = 2

	/** An impling kind: its setting keys, name, Void's ids (open world and Puro-Puro), RuneLite's colour and default mode. */
	class Kind(@JvmField val key: String, @JvmField val name: String, @JvmField val ids: IntArray, @JvmField val colour: Int, @JvmField val defaultMode: Int)

	@JvmField
	val KINDS: Array<Kind> = arrayOf(
		Kind("baby", "Baby", intArrayOf(1028, 6055), 0xFFB18FB3.toInt(), NONE),
		Kind("young", "Young", intArrayOf(1029, 6056), 0xFFAFA488.toInt(), NONE),
		Kind("gourmet", "Gourmet", intArrayOf(1030, 6057), 0xFFA98362.toInt(), NONE),
		Kind("earth", "Earth", intArrayOf(1031, 6058), 0xFF3E5640.toInt(), NONE),
		Kind("essence", "Essence", intArrayOf(1032, 6059), 0xFF20595A.toInt(), NONE),
		Kind("eclectic", "Eclectic", intArrayOf(1033, 6060), 0xFF919B45.toInt(), NONE),
		Kind("nature", "Nature", intArrayOf(1034, 6061), 0xFF5C8A5F.toInt(), NONE),
		Kind("magpie", "Magpie", intArrayOf(1035, 6062), 0xFF8E8E13.toInt(), NONE),
		Kind("ninja", "Ninja", intArrayOf(6053, 6063), 0xFF47464B.toInt(), NONE),
		Kind("dragon", "Dragon", intArrayOf(6054, 6064), 0xFFD2554B.toInt(), HIGHLIGHT),
		Kind("pirate", "Pirate", intArrayOf(7845, 7846), 0xFF8B5A2B.toInt(), NONE),
		Kind("spirit", "Spirit", intArrayOf(7866, 7904), 0xFF7FB2C8.toInt(), NONE),
		Kind("zombie", "Zombie", intArrayOf(7902, 7905), 0xFF6B7A3A.toInt(), NONE),
		Kind("kingly", "Kingly", intArrayOf(7903, 7906), 0xFFC8A02A.toInt(), HIGHLIGHT),
	)

	/** Void's Puro-Puro impling spawns: x, y and kind. */
	const val SPAWNS: String = "2563,4291,baby;2563,4308,baby;2563,4348,baby;2568,4323,baby;2571,4337,baby;2581,4300,baby;2584,4344,baby;2596,4296,baby;2598,4351,baby;2609,4339,baby;2610,4304,baby;2616,4326,baby;2620,4291,baby;2620,4348,baby;2622,4311,baby;2564,4321,young;2569,4308,young;2574,4331,young;2576,4341,young;2587,4300,young;2590,4348,young;2592,4291,young;2597,4336,young;2603,4298,young;2612,4309,young;2612,4327,young;2621,4318,young;2568,4296,gourmet;2568,4327,gourmet;2574,4311,gourmet;2579,4288,gourmet;2580,4343,gourmet;2587,4307,gourmet;2596,4329,gourmet;2597,4293,gourmet;2602,4346,gourmet;2609,4317,gourmet;2615,4298,gourmet;2615,4342,gourmet;2618,4321,gourmet;2568,4317,earth;2570,4330,earth;2570,4347,earth;2574,4305,earth;2587,4342,earth;2590,4298,earth;2598,4340,earth;2609,4294,earth;2611,4334,earth;2612,4310,earth;2574,4317,essence;2576,4337,essence;2585,4298,essence;2601,4343,essence;2612,4318,essence;2613,4291,essence;2561,4345,eclectic;2567,4319,eclectic;2591,4295,eclectic;2591,4340,eclectic;2615,4326,eclectic"

	@JvmField val modes: IntArray = IntArray(KINDS.size) { KINDS[it].defaultMode }
	@JvmField val colours: IntArray = IntArray(KINDS.size) { KINDS[it].colour }
	@JvmField var showspawn: Boolean = false
	@JvmField var spawnColor: Int = WHITE
	@JvmField var showname: Boolean = false

	/** For the smoke test: the implings and spawns the last frame drew. */
	@JvmField var drawnLastFrame: Int = 0
	@JvmField var spawnsDrawn: Int = 0

	@JvmField var lastNotification: String? = null

	private val kindsById: HashMap<Int, Int> = HashMap()
	private val spawns: ArrayList<IntArray> = ArrayList()
	private val spawnNames: ArrayList<String> = ArrayList()
	private val xs: IntArray = IntArray(16)
	private val ys: IntArray = IntArray(16)
	private val tileXs: IntArray = IntArray(4)
	private val tileYs: IntArray = IntArray(4)
	private val point: IntArray = IntArray(2)
	private val area: IntArray = IntArray(4)
	private var context: PluginContext? = null

	/** RuneLite's NPC highlights and ImplingsOverlay, under the interfaces. */
	private val overlay: Overlay = Overlay { g, view -> context?.client()?.let { draw(g, view, it) } }

	/** The names on the minimap, over it. */
	private val frameListener: EventListener<FrameDraw> = EventListener { event -> context?.client()?.let { drawMinimap(event.graphics, it) } }

	private val spawnListener: EventListener<Npc> = EventListener { npc -> context?.client()?.let { spawned(it, npc) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		showspawn = !showspawn
		context?.config()?.setBoolean("showspawn", showspawn)
		context?.config()?.save()
		context?.client()?.consolePrint(if (showspawn) "Impling spawns shown in Puro-Puro." else "Impling spawns hidden.")
	}

	init {
		for (k in KINDS.indices) {
			for (id in KINDS[k].ids) {
				kindsById[id] = k
			}
		}
		for (row in SPAWNS.split(';')) {
			val f: List<String> = row.split(',')
			spawns.add(intArrayOf(f[0].toInt(), f[1].toInt()))
			spawnNames.add(f[2].replaceFirstChar { it.uppercase() })
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's ImplingsConfig, in its order (the Impling type settings section), but OSRS's crystal and lucky
		// implings and with 2011's pirate, spirit, zombie and kingly after dragon
		for (k in KINDS.indices) {
			val kind: Kind = KINDS[k]
			s.choice("show" + kind.key, kind.name + " implings", MODES[kind.defaultMode], MODES)
			s.colour(kind.key + "Color", kind.name + " impling color", kind.colour, false)
		}
		s.toggle("showspawn", "Show spawn locations", false)
		s.colour("spawnColor", "Impling spawn color", WHITE, false)
		s.toggle("showname", "Show name on minimap", false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.NPC_SPAWNED, spawnListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.addOverlay(overlay)
		context.registerCommand("implings", toggle)
	}

	override fun stop() {
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		for (k in KINDS.indices) {
			val kind: Kind = KINDS[k]
			val mode: Int = MODES.indexOf(config.get("show" + kind.key, MODES[kind.defaultMode]))
			modes[k] = if (mode < 0) kind.defaultMode else mode
			colours[k] = config.getColour(kind.key + "Color", kind.colour)
		}
		showspawn = config.getBoolean("showspawn", false)
		spawnColor = config.getColour("spawnColor", WHITE)
		showname = config.getBoolean("showname", false)
	}

	/** The kind (index in KINDS) of npc type [typeId], -1 for no impling. */
	@JvmStatic
	fun kindOf(typeId: Int): Int = kindsById[typeId] ?: -1

	/** RuneLite's onNpcSpawned: an impling set to Notify is announced. */
	@JvmStatic
	fun spawned(client: ClientApi, npc: Npc) {
		val kind: Int = kindOf(npc.typeId())
		if (kind >= 0 && modes[kind] == NOTIFY) {
			val text: String = KINDS[kind].name + " impling is in the area"
			lastNotification = text
			client.notify(text)
		}
	}

	private fun draw(g: Graphics, view: Viewport, client: ClientApi) {
		drawnLastFrame = 0
		spawnsDrawn = 0
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		for (i in 0 until client.npcCount()) {
			val npc: Npc? = client.npc(i)
			val kind: Int = if (npc == null) -1 else kindOf(npc.typeId())
			if (npc != null && kind >= 0 && modes[kind] != NONE && highlight(g, view, client, npc, colours[kind], KINDS[kind].name + " impling")) {
				drawnLastFrame++
			}
		}
		if (showspawn && inPuroPuro(client)) {
			drawSpawns(g, view, client)
		}
	}

	/** RuneLite's NpcOverlayService highlight: the outline and the name over it. */
	private fun highlight(g: Graphics, view: Viewport, client: ClientApi, npc: Npc, colour: Int, name: String): Boolean {
		val count: Int = client.actorHull(npc, xs, ys)
		if (count < 3) {
			return false
		}
		for (c in 0 until count) {
			xs[c] += view.x
			ys[c] += view.y
		}
		g.polygon(xs, ys, count, colour)
		if (g.fontReady() && client.projectActor(npc, npc.overheadHeight() + NAME_RAISE, point)) {
			g.textCentered(view.x + point[0], view.y + point[1], name, colour, SHADOW)
		}
		return true
	}

	/** Void's Puro-Puro: x 2560 to 2623, y 4288 to 4351. */
	@JvmStatic
	fun inPuroPuro(client: ClientApi): Boolean {
		val player: Player = client.localPlayer() ?: return false
		val x: Int = player.tileX() + client.sceneBaseX()
		val y: Int = player.tileY() + client.sceneBaseY()
		return x in PURO_MIN_X..PURO_MAX_X && y in PURO_MIN_Y..PURO_MAX_Y
	}

	/** RuneLite's ImplingsOverlay: the spawn tiles within 32 tiles, with their kind. */
	private fun drawSpawns(g: Graphics, view: Viewport, client: ClientApi) {
		val player: Player = client.localPlayer() ?: return
		val px: Int = player.tileX() + client.sceneBaseX()
		val py: Int = player.tileY() + client.sceneBaseY()
		for (i in spawns.indices) {
			val spawn: IntArray = spawns[i]
			if (kotlin.math.abs(spawn[0] - px) > SPAWN_RANGE || kotlin.math.abs(spawn[1] - py) > SPAWN_RANGE) {
				continue
			}
			val sceneX: Int = spawn[0] - client.sceneBaseX()
			val sceneY: Int = spawn[1] - client.sceneBaseY()
			if (TileOutline.draw(g, view, client, sceneX, sceneY, player.level(), 1, spawnColor, 0, 1, tileXs, tileYs)) {
				spawnsDrawn++
				if (g.fontReady() && client.projectPoint(sceneX * TILE + TILE / 2, sceneY * TILE + TILE / 2, player.level(), 0, point)) {
					g.textCentered(view.x + point[0], view.y + point[1], spawnNames[i], spawnColor, SHADOW)
				}
			}
		}
	}

	private fun drawMinimap(g: Graphics, client: ClientApi) {
		if (!showname || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN || !client.minimapArea(area)) {
			return
		}
		g.clip(area[0], area[1], area[2], area[3])
		for (i in 0 until client.npcCount()) {
			val npc: Npc? = client.npc(i)
			val kind: Int = if (npc == null) -1 else kindOf(npc.typeId())
			if (npc != null && kind >= 0 && modes[kind] != NONE && client.projectActorMinimap(npc, point)) {
				g.textCentered(point[0], point[1] - 2, KINDS[kind].name, colours[kind], SHADOW)
			}
		}
		g.unclip()
	}

	private const val TILE: Int = 512
	private const val NAME_RAISE: Int = 40
	private const val SPAWN_RANGE: Int = 32
	private const val PURO_MIN_X: Int = 2560
	private const val PURO_MAX_X: Int = 2623
	private const val PURO_MIN_Y: Int = 4288
	private const val PURO_MAX_Y: Int = 4351
	private const val WHITE: Int = -0x1
	private const val SHADOW: Int = 0xFF000000.toInt()
}
