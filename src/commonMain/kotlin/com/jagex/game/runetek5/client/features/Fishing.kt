package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.OverlayPanel
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.MinimapDraw
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Fishing, for the fishing spots of the 634 server (Void). Each
 * spot shows its tile, the icon of its best catch and its name (what it gives, from Void's fishing spots
 * table), and a dot on the minimap; "Display only currently fished fish" keeps to the kind of spot you fish.
 * A panel at the top left says whether you are fishing and how many fish you caught this session and an
 * hour (the XP Tracker's Fishing actions, as RuneLite takes them; the catches counted when the XP Tracker is
 * off), and goes after Reset stats minutes without a catch.
 *
 * Minnows, aerial fishing, harpoonfish (Tempoross) and their colours and notification are OSRS-only; the
 * Fishing Trawler's timer and contribution have no Trawler to read on Void. A built-in plugin
 * (BuiltinPlugins, docs/PLUGINS.md).
 */
object Fishing : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("fishing", "Fishing", "1", PluginApi.VERSION, "Marks fishing spots with their catch and counts the fish you catch this session and an hour.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = true

	/** Void's fishing spots (its fishing_spots table): npc, the icon's item (the best catch), the catches. */
	const val SPOTS: String = "309,335,Trout, Salmon, Rainbow fish, Pike;310,335,Trout, Salmon, Rainbow fish, Pike;311,335,Trout, Salmon, Rainbow fish, Pike;312,377,Lobster, Tuna, Swordfish;313,383,Shark, Mackerel, Cod, Bass;314,335,Trout, Salmon, Rainbow fish, Pike;315,335,Trout, Salmon, Rainbow fish, Pike;316,317,Shrimps, Anchovies, Sardine, Herring;317,335,Trout, Salmon, Rainbow fish, Pike;318,335,Trout, Salmon, Rainbow fish, Pike;319,317,Shrimps, Anchovies, Sardine, Herring;320,317,Shrimps, Anchovies, Sardine, Herring;321,377,Lobster, Tuna, Swordfish;322,383,Shark, Mackerel, Cod, Bass;323,317,Shrimps, Anchovies, Sardine, Herring;324,377,Lobster, Tuna, Swordfish;325,317,Shrimps, Anchovies, Sardine, Herring;326,317,Shrimps, Anchovies, Sardine, Herring;327,317,Shrimps, Anchovies, Sardine, Herring;328,335,Trout, Salmon, Rainbow fish, Pike;329,335,Trout, Salmon, Rainbow fish, Pike;330,317,Shrimps, Anchovies, Sardine, Herring;800,2148,Lava eel;927,335,Trout, Salmon, Rainbow fish, Pike;952,317,Shrimps;1174,317,Shrimps, Karambwanji;1176,3142,Karambwan;1177,3142,Karambwan;1189,335,Trout, Salmon, Rainbow fish, Pike;1190,335,Trout, Salmon, Rainbow fish, Pike;1236,3379,Slimy eel;1237,3379,Slimy eel;1238,3379,Slimy eel;1331,317,Shrimps, Anchovies, Sardine, Herring;1332,377,Lobster, Tuna, Swordfish;1333,383,Shark, Mackerel, Cod, Bass;1405,383,Shark, Mackerel, Cod, Bass;1406,383,Shark, Mackerel, Cod, Bass;2067,5004,Frog spawn, Slimy eel, Cave eel;2068,5004,Frog spawn, Slimy eel, Cave eel;2724,317,Shrimps, Anchovies, Sardine, Herring;2859,6715,Sluglings, Karamthulhu;3574,383,Shark, Mackerel, Cod, Bass;3575,383,Shark, Mackerel, Cod, Bass;3804,377,Lobster, Tuna, Swordfish;3848,359,Tuna, Swordfish, Monkfish;4908,317,Shrimps, Anchovies, Sardine, Herring;5470,377,Lobster, Tuna, Swordfish;5471,383,Shark, Mackerel, Cod, Bass;5748,5004,Frog spawn, Slimy eel, Cave eel;5749,5004,Frog spawn, Slimy eel, Cave eel;6267,13435,Crayfish;7044,383,Shark, Mackerel, Cod, Bass;7045,317,Shrimps, Anchovies, Sardine, Herring;7046,377,Lobster, Tuna, Swordfish;7636,3379,Slimy eel, Cave eel;7862,377,Lobster;7863,377,Lobster;8841,15264,Cavefish;8842,15270,Rocktail"

	/** Void's fishing animations: harpoon, cage, nets, rods, karambwan vessel, barb-tail harpoon, crayfish cage and bare hands. */
	@JvmField
	val FISHING_ANIMATIONS: IntArray = intArrayOf(618, 619, 620, 621, 622, 623, 1193, 5108, 10009, 6705, 6706, 6707, 6708, 6709, 6710, 6711)

	/** RuneLite's FISHING_CATCH_REGEX, as Void says it: "You catch some raw shrimps.", "You catch an enormous raw bass!". */
	@JvmField
	val FISHING_CATCH: Regex = Regex("You catch (?:a|an|some) ")

	const val FISHING_SPOT: String = "Fishing spot"

	/** A fishing spot of the table: the icon's item and the catches, which also say what kind of spot it is. */
	class Spot(@JvmField val iconItemId: Int, @JvmField val name: String)

	/** RuneLite's FishingSession. */
	class Session(@JvmField val start: Long) {
		@JvmField var lastFishCaught: Long = 0L
		@JvmField var caught: Int = 0
	}

	@JvmField var onlyCurrent: Boolean = false
	@JvmField var showTiles: Boolean = true
	@JvmField var showIcons: Boolean = true
	@JvmField var showNames: Boolean = false
	@JvmField var overlayColor: Int = CYAN
	@JvmField var statTimeout: Int = 5
	@JvmField var showFishingStats: Boolean = true

	@JvmField var session: Session? = null

	/** The kind of spot fished now (RuneLite's currentSpot), null for none. */
	@JvmField var currentSpot: Spot? = null

	/** For the smoke test: the spots the last frame drew. */
	@JvmField var drawnLastFrame: Int = 0

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	private val spots: HashMap<Int, Spot> = HashMap()

	private var context: PluginContext? = null

	private val xs: IntArray = IntArray(4)
	private val ys: IntArray = IntArray(4)
	private val point: IntArray = IntArray(2)
	private val area: IntArray = IntArray(4)
	private val drawn: HashSet<Long> = HashSet()

	/** RuneLite's FishingOverlay. */
	@JvmField
	val panel: OverlayPanel = object : OverlayPanel("FishingOverlay") {
		init {
			addMenuEntry("Reset", "Fishing overlay", Task { session = null })
		}

		override fun render(g: Graphics, view: Viewport): Size? {
			if (!enabled) {
				return null
			}
			val client: ClientApi = context?.client() ?: return null
			val current: Session = session ?: return null
			if (!showFishingStats || client.gameState() !== GameState.LOGGED_IN) {
				return null
			}
			if (isFishing(client)) {
				panelComponent.addTitle("Fishing", GREEN)
			} else {
				panelComponent.addTitle("NOT fishing", RED)
			}
			val actions: Int = totalCaught(current)
			if (actions > 0) {
				panelComponent.addLine("Caught fish:", WHITE, actions.toString(), WHITE)
				if (actions > 2) {
					panelComponent.addLine("Fish/hr:", WHITE, caughtPerHour(current).toString(), WHITE)
				}
			}
			return super.render(g, view)
		}
	}

	/** RuneLite's FishingSpotOverlay, under the interfaces. */
	@JvmField
	val spotOverlay: Overlay = Overlay { g, view -> if (enabled) context?.client()?.let { drawSpots(g, view, it) } }

	/** RuneLite's FishingSpotMinimapOverlay, over the minimap. */
	// on the minimap as it is drawn: under the orbs, as the game's own dots are (API 38)
	private val minimapListener: EventListener<MinimapDraw> = EventListener { event -> if (enabled) context?.client()?.let { drawMinimap(event.graphics, it) } }

	private val tickListener: EventListener<GameTick> = EventListener { _ -> if (enabled) context?.client()?.let { tick(it) } }

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> if (enabled) chat(event) }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		showFishingStats = !showFishingStats
		context?.config()?.setBoolean("showFishingStats", showFishingStats)
		context?.config()?.save()
		context?.client()?.consolePrint(if (showFishingStats) "Fishing stats on." else "Fishing stats off.")
	}

	init {
		for (row in SPOTS.split(';')) {
			val comma: Int = row.indexOf(',')
			val second: Int = row.indexOf(',', comma + 1)
			spots[row.substring(0, comma).toInt()] = Spot(row.substring(comma + 1, second).toInt(), row.substring(second + 1))
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's FishingConfig, in its order, but the OSRS-only minnow, aerial, harpoonfish and Trawler settings
		s.toggle("enabled", "Fishing", true)
		s.toggle("onlyCurrent", "Display only currently fished fish", false)
		s.toggle("showTiles", "Display spot tiles", true)
		s.toggle("showIcons", "Display spot icons", true)
		s.toggle("showNames", "Display spot names", false)
		s.colour("overlayColor", "Overlay color", CYAN, false)
		s.number("statTimeout", "Reset stats (minutes)", 5, 1, 60, 1)
		s.toggle("showFishingStats", "Show fishing session stats", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.MINIMAP_DRAW, minimapListener)
		context.addOverlay(panel)
		context.addOverlay(spotOverlay)
		context.registerCommand("fishing", toggle)
	}

	override fun stop() {
		context = null
		session = null
		currentSpot = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		onlyCurrent = config.getBoolean("onlyCurrent", false)
		showTiles = config.getBoolean("showTiles", true)
		showIcons = config.getBoolean("showIcons", true)
		showNames = config.getBoolean("showNames", false)
		overlayColor = config.getColour("overlayColor", CYAN)
		statTimeout = config.getInt("statTimeout", 5)
		showFishingStats = config.getBoolean("showFishingStats", true)
	}

	/** The fishing spot of npc type [typeId], null for any other npc. */
	@JvmStatic
	fun spot(typeId: Int): Spot? = spots[typeId]

	/** RuneLite's FishingOverlay test: at a fishing spot and in a fishing animation. */
	@JvmStatic
	fun isFishing(client: ClientApi): Boolean {
		val player: Player = client.localPlayer() ?: return false
		val target: Actor? = player.interacting()
		return target is Npc && (target.name() ?: "").contains(FISHING_SPOT) && player.animationId() in FISHING_ANIMATIONS
	}

	/** The XP Tracker's Fishing actions (RuneLite's), or the catches counted here when that is more (the tracker off or just begun). */
	@JvmStatic
	fun totalCaught(current: Session): Int {
		val state: XpTrackerState? = if (XpTracker.enabled) XpTracker.states[Skill.FISHING] else null
		return maxOf(state?.actions ?: 0, current.caught)
	}

	@JvmStatic
	fun caughtPerHour(current: Session): Int {
		val state: XpTrackerState? = if (XpTracker.enabled) XpTracker.states[Skill.FISHING] else null
		if (state != null && state.actions >= current.caught) {
			return state.actionsPerHour()
		}
		val elapsed: Long = maxOf(MINUTE_MILLIS, clock() - current.start)
		return (current.caught.toDouble() * HOUR_MILLIS / elapsed).toInt()
	}

	/** RuneLite's onInteractingChanged and onGameTick: the spot fished now, and the session dropped after the timeout. */
	@JvmStatic
	fun tick(client: ClientApi) {
		val target: Actor? = client.localPlayer()?.interacting()
		if (target is Npc) {
			spot(target.typeId())?.let { currentSpot = it }
		}
		val current: Session = session ?: return
		if (current.lastFishCaught != 0L && clock() - current.lastFishCaught >= statTimeout * MINUTE_MILLIS) {
			session = null
			currentSpot = null
		}
	}

	/** RuneLite's onChatMessage: a catch starts or keeps the session and is counted. */
	@JvmStatic
	fun chat(event: ChatMessage) {
		if (event.type != ChatType.GAME && event.type != ChatType.GAME_FILTERED) {
			return
		}
		val message: String = ClientFeatures.plainText(event.message ?: return)
		if (FISHING_CATCH.containsMatchIn(message)) {
			val now: Long = clock()
			val current: Session = session ?: Session(now).also { session = it }
			current.lastFishCaught = now
			current.caught++
		}
	}

	/** A spot to draw: in the table and, with "only current", of the kind fished now. */
	private fun shown(npc: Npc): Spot? {
		val spot: Spot = spot(npc.typeId()) ?: return null
		val current: Spot? = currentSpot
		if (onlyCurrent && current != null && current.name != spot.name) {
			return null
		}
		return spot
	}

	private fun drawSpots(g: Graphics, view: Viewport, client: ClientApi) {
		drawnLastFrame = 0
		if (client.gameState() !== GameState.LOGGED_IN || !(showTiles || showIcons || showNames)) {
			return
		}
		drawn.clear()
		val colour: Int = darker(overlayColor)
		for (i in 0 until client.npcCount()) {
			val npc: Npc? = client.npc(i)
			val spot: Spot? = if (npc == null) null else shown(npc)
			// one marking for the spots of one kind on one tile (RuneLite's previousSpot)
			if (npc != null && spot != null && drawn.add((npc.tileX().toLong() shl 32) or (npc.tileY().toLong() shl 16) or (spot.iconItemId.toLong() and 0xFFFF))) {
				drawSpot(g, view, client, npc, spot, colour)
				drawnLastFrame++
			}
		}
	}

	private fun drawSpot(g: Graphics, view: Viewport, client: ClientApi, npc: Npc, spot: Spot, colour: Int) {
		if (showTiles) {
			TileOutline.draw(g, view, client, npc.tileX(), npc.tileY(), npc.level(), 1, colour, (FILL_ALPHA shl 24) or (colour and 0xFFFFFF), 2, xs, ys)
		}
		if (showIcons && client.projectActor(npc, npc.overheadHeight(), point)) {
			g.itemIcon(spot.iconItemId, 1, view.x + point[0] - ICON_WIDTH / 2, view.y + point[1] - ICON_HEIGHT / 2)
		}
		if (showNames && g.fontReady() && client.projectActor(npc, npc.overheadHeight() + NAME_RAISE, point)) {
			g.textCentered(view.x + point[0], view.y + point[1], spot.name, colour, SHADOW)
		}
	}

	private fun drawMinimap(g: Graphics, client: ClientApi) {
		if (client.gameState() !== GameState.LOGGED_IN || !(showTiles || showIcons || showNames) || !client.minimapArea(area)) {
			return
		}
		val colour: Int = darker(overlayColor)
		g.clip(area[0], area[1], area[2], area[3])
		for (i in 0 until client.npcCount()) {
			val npc: Npc = client.npc(i) ?: continue
			if (shown(npc) != null && client.projectActorMinimap(npc, point)) {
				g.fillCircle(point[0], point[1], 2, colour)
			}
		}
		g.unclip()
	}

	/** java.awt.Color.darker: each channel times 0.7. */
	@JvmStatic
	fun darker(argb: Int): Int {
		val r: Int = ((argb ushr 16) and 0xFF) * 7 / 10
		val gr: Int = ((argb ushr 8) and 0xFF) * 7 / 10
		val b: Int = (argb and 0xFF) * 7 / 10
		return (argb and -0x1000000) or (r shl 16) or (gr shl 8) or b
	}

	private const val HOUR_MILLIS: Long = 3_600_000L
	private const val MINUTE_MILLIS: Long = 60_000L
	private const val ICON_WIDTH: Int = 36
	private const val ICON_HEIGHT: Int = 32
	private const val NAME_RAISE: Int = 40
	private const val FILL_ALPHA: Int = 50

	private const val WHITE: Int = -0x1
	private const val GREEN: Int = 0xFF00FF00.toInt()
	private const val RED: Int = 0xFFFF0000.toInt()
	private const val CYAN: Int = 0xFF00FFFF.toInt()
	private const val SHADOW: Int = 0xFF000000.toInt()
}
