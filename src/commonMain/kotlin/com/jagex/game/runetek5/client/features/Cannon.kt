package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.Counter
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.ProjectileSpawned
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Cannon, for the dwarf multicannon as the 634 server (Void)
 * runs it. The cannonballs left show over the placed cannon (and in an infobox when asked), with a
 * notification when it runs out or falls to a threshold; the tiles where a cannon hits twice can be
 * shown, and common cannon spots are marked while all four parts are carried.
 *
 * Void never sends the ammo (RuneLite reads OSRS's varps), so the plugin counts it: "You load the cannon
 * with N cannon balls." adds N, each cannonball (projectile 53) leaving the cannon's barrels takes one,
 * "Your cannon is out of ammo!" empties it. Where the cannon stands is the multicannon loc (6, or a part
 * while it is being built) found round the player as it is set up; picking it up or its decay forgets it.
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object Cannon : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("cannon", "Cannon", "1", PluginApi.VERSION, "Counts the cannonballs in your dwarf multicannon, warns when it runs low or empty, and marks double-hit tiles and common cannon spots.", "Pixel")

	/** Void's multicannon locs: the finished cannon, then its base, stand and barrels as it is set up. */
	@JvmField val CANNON_LOCS: IntArray = intArrayOf(6, 7, 8, 9)

	/** The cannon's parts (base, stand, barrels, furnace) and its ammo. */
	@JvmField val PARTS: IntArray = intArrayOf(6, 8, 10, 12)
	const val CANNONBALL: Int = 2

	/** The cannonball's projectile (Void's dwarf_cannon.gfx.toml). */
	const val CANNONBALL_PROJECTILE: Int = 53

	/** Void's world.objs.cannon.maxAmmo. */
	const val MAX_AMMO: Int = 30

	const val BACKPACK: Int = 93

	/** RuneLite's MAX_OVERLAY_DISTANCE, in scene units (128 to a tile here, as RuneLite's local units). */
	private const val MAX_OVERLAY_TILES: Int = 32

	private val LOADED: Regex = Regex("You load the cannon with (\\d+) cannon balls?\\.")
	const val OUT_OF_AMMO: String = "Your cannon is out of ammo!"
	const val FURNACE_ADDED: String = "You add the furnace."
	const val BASE_PLACED: String = "You place the cannon base on the ground."

	/** The lines after which the cannon is no longer the player's in the world. */
	@JvmField
	val GONE: Array<String> = arrayOf(
		"You pick up the cannon. It's really heavy.",
		"Your cannon has decayed and the parts have been returned to you.",
		"Your cannon was sent to your bank.",
	)

	@JvmField var enabled: Boolean = true
	@JvmField var showEmptyCannonNotification: Boolean = true
	@JvmField var lowWarningThreshold: Int = 0
	@JvmField var showInfobox: Boolean = false
	@JvmField var showDoubleHitSpot: Boolean = false
	@JvmField var highlightDoubleHitColour: Int = RED
	@JvmField var showCannonSpots: Boolean = true

	/** Whether the player's cannon stands, where (its south-west tile, world coordinates) and on which world. */
	@JvmField var cannonPlaced: Boolean = false
	@JvmField var cannonX: Int = -1
	@JvmField var cannonY: Int = -1
	@JvmField var cannonLevel: Int = 0
	@JvmField var cannonWorld: Int = -1

	/** The cannonballs in it, as counted. */
	@JvmField var cballsLeft: Int = 0
	private var lowWarned: Boolean = false

	/** Whether the backpack holds all four parts (the spots then show). */
	@JvmField var hasAllParts: Boolean = false

	private var counter: CannonCounter? = null

	/** What was drawn last frame, for the smoke test. */
	@JvmField var drawnCount: String? = null
	@JvmField var drawnSpots: Int = 0

	private var context: PluginContext? = null

	private val xs: IntArray = IntArray(4)
	private val ys: IntArray = IntArray(4)
	private val point: IntArray = IntArray(2)

	/** RuneLite's CannonOverlay and CannonSpotOverlay, under the interfaces. */
	@JvmField
	val overlay: Overlay = Overlay { g, view -> context?.client()?.let { draw(g, view, it) } }

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> context?.client()?.let { chat(it, event) } }

	private val projectileListener: EventListener<ProjectileSpawned> = EventListener { event -> context?.client()?.let { projectile(it, event) } }

	private val containerListener: EventListener<com.jagex.game.runetek5.plugin.api.events.ItemContainerChanged> = EventListener { event ->
		if (event.containerId == BACKPACK) {
			context?.client()?.let { hasAllParts = carriesAllParts(it.itemContainer(BACKPACK)) }
		}
	}

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state === GameState.LOGIN_SCREEN) {
			// a cannon left in the world is still there on Void, but the count cannot be known after a new login
			forget()
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			updateCounter()
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		updateCounter()
		context?.client()?.consolePrint(if (enabled) "Cannon enabled." else "Cannon disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Cannon", true)
		// RuneLite's CannonConfig, in its order
		s.toggle("showEmptyCannonNotification", "Enable cannon notifications", true)
		s.number("lowWarningThreshold", "Low warning threshold", 0, 0, MAX_AMMO, 1)
		s.toggle("showInfobox", "Show cannonball infobox", false)
		s.toggle("showDoubleHitSpot", "Show double hit spots", false)
		s.colour("highlightDoubleHitColor", "Double hit spots", RED, true)
		s.toggle("showCannonSpots", "Show common cannon spots", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.PROJECTILE_SPAWNED, projectileListener)
		context.events().subscribe(Events.ITEM_CONTAINER_CHANGED, containerListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.addOverlay(overlay)
		context.registerCommand("cannon", toggle)
		hasAllParts = carriesAllParts(context.client().itemContainer(BACKPACK))
	}

	override fun stop() {
		removeCounter()
		context = null
		forget()
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		showEmptyCannonNotification = config.getBoolean("showEmptyCannonNotification", true)
		lowWarningThreshold = config.getInt("lowWarningThreshold", 0)
		showInfobox = config.getBoolean("showInfobox", false)
		showDoubleHitSpot = config.getBoolean("showDoubleHitSpot", false)
		highlightDoubleHitColour = config.getColour("highlightDoubleHitColor", RED)
		showCannonSpots = config.getBoolean("showCannonSpots", true)
	}

	/** No cannon known any more. */
	@JvmStatic
	fun forget() {
		cannonPlaced = false
		cannonX = -1
		cannonY = -1
		cballsLeft = 0
		lowWarned = false
		removeCounter()
	}

	@JvmStatic
	fun chat(client: ClientApi, event: ChatMessage) {
		if (event.type != ChatType.GAME && event.type != ChatType.GAME_FILTERED) {
			return
		}
		val message: String = ClientFeatures.plainText(event.message ?: return)
		if (message == BASE_PLACED || message == FURNACE_ADDED) {
			// RuneLite's onChatMessage for the furnace: the cannon's world; its place is the loc round the player
			if (findCannon(client)) {
				cannonPlaced = message == FURNACE_ADDED || cannonPlaced
				cannonWorld = client.worldId()
				updateCounter()
			}
			return
		}
		val loaded: MatchResult? = LOADED.matchEntire(message)
		if (loaded != null) {
			setBalls(client, minOf(MAX_AMMO, cballsLeft + loaded.groupValues[1].toInt()))
			return
		}
		if (message == OUT_OF_AMMO) {
			setBalls(client, 0)
			if (enabled && showEmptyCannonNotification) {
				client.notify("Your cannon is out of ammo!")
			}
			return
		}
		if (message in GONE) {
			forget()
		}
	}

	/** A cannonball leaving the player's cannon (from a barrel, a tile from its middle) is one fewer in it. */
	@JvmStatic
	fun projectile(client: ClientApi, event: ProjectileSpawned) {
		if (!cannonPlaced || event.id != CANNONBALL_PROJECTILE || event.sourceIndex != 0) {
			return
		}
		val tileX: Int = (event.startX shr 9) + client.sceneBaseX()
		val tileY: Int = (event.startY shr 9) + client.sceneBaseY()
		if (event.level != cannonLevel || tileX < cannonX || tileX > cannonX + 2 || tileY < cannonY || tileY > cannonY + 2) {
			return
		}
		setBalls(client, maxOf(0, cballsLeft - 1))
	}

	/** RuneLite's onVarbitChanged for the ammo: the count, and the low warning once as it falls to the threshold. */
	private fun setBalls(client: ClientApi, balls: Int) {
		if (balls > cballsLeft) {
			lowWarned = false
		}
		cballsLeft = balls
		if (enabled && !lowWarned && lowWarningThreshold >= balls && balls > 0) {
			client.notify("Your cannon has " + balls + " cannon balls remaining!")
			lowWarned = true
		}
	}

	/** The multicannon (or a part of one) within four tiles of the player: its south-west tile noted; false when there is none. */
	@JvmStatic
	fun findCannon(client: ClientApi): Boolean {
		val player: Player = client.localPlayer() ?: return false
		val level: Int = player.level()
		var minX: Int = Int.MAX_VALUE
		var minY: Int = Int.MAX_VALUE
		for (dx in -4..4) {
			for (dy in -4..4) {
				val x: Int = player.tileX() + dx
				val y: Int = player.tileY() + dy
				var found = false
				client.locsAt(x, y, level) { id, _, _, _ ->
					if (id in CANNON_LOCS) {
						found = true
					}
				}
				if (found) {
					minX = minOf(minX, x)
					minY = minOf(minY, y)
				}
			}
		}
		if (minX == Int.MAX_VALUE) {
			return false
		}
		cannonX = minX + client.sceneBaseX()
		cannonY = minY + client.sceneBaseY()
		cannonLevel = level
		return true
	}

	/** RuneLite's onItemContainerChanged: all four parts in the backpack. */
	@JvmStatic
	fun carriesAllParts(backpack: ItemContainer?): Boolean {
		if (backpack == null) {
			return false
		}
		for (part in PARTS) {
			var found = false
			for (slot in 0 until backpack.size()) {
				if (backpack.itemId(slot) == part) {
					found = true
					break
				}
			}
			if (!found) {
				return false
			}
		}
		return true
	}

	/** RuneLite's getStateColor: green over 15, orange over 5, else red. */
	@JvmStatic
	fun stateColour(balls: Int): Int {
		if (balls > 15) {
			return GREEN
		}
		return if (balls > 5) ORANGE else RED
	}

	private fun updateCounter() {
		if (enabled && showInfobox && cannonPlaced) {
			if (counter == null) {
				val box = CannonCounter()
				counter = box
				context?.addInfoBox(box)
			}
		} else {
			removeCounter()
		}
	}

	private fun removeCounter() {
		val box: CannonCounter = counter ?: return
		context?.removeInfoBox(box)
		counter = null
	}

	/** The cannon's count and double-hit tiles, then the spots while the parts are carried and no cannon stands. */
	@JvmStatic
	fun draw(g: Graphics, view: Viewport, client: ClientApi) {
		drawnCount = null
		drawnSpots = 0
		val player: Player = client.localPlayer() ?: return
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		if (cannonPlaced && cannonWorld == client.worldId() && cannonLevel == player.level()) {
			drawCannon(g, view, client, player)
		}
		if (showCannonSpots && hasAllParts && !cannonPlaced) {
			drawSpots(g, view, client, player)
		}
	}

	private fun drawCannon(g: Graphics, view: Viewport, client: ClientApi, player: Player) {
		// the middle of the cannon's three tiles by three
		val sceneX: Int = cannonX + 1 - client.sceneBaseX()
		val sceneY: Int = cannonY + 1 - client.sceneBaseY()
		if (!inScene(client, sceneX, sceneY) || !near(player, sceneX, sceneY)) {
			return
		}
		val text: String = cballsLeft.toString()
		if (g.fontReady() && client.projectTile(sceneX, sceneY, cannonLevel, 150, point)) {
			g.textCentered(view.x + point[0], view.y + point[1], text, stateColour(cballsLeft), BLACK)
			drawnCount = text
		}
		if (showDoubleHitSpot) {
			drawDoubleHitSpots(g, view, client, sceneX, sceneY)
		}
	}

	/** RuneLite's drawDoubleHitSpots: the tiles on the cannon's middle rows and columns, 2 or 3 out. */
	private fun drawDoubleHitSpots(g: Graphics, view: Viewport, client: ClientApi, sceneX: Int, sceneY: Int) {
		val colour: Int = highlightDoubleHitColour
		for (x in -3..3) {
			for (y in -3..3) {
				if (isDoubleHitSpot(x, y)) {
					TileOutline.draw(g, view, client, sceneX - x, sceneY - y, cannonLevel, 1, colour or OPAQUE, fill(colour), 2, xs, ys)
				}
			}
		}
	}

	/** On a row or column next to the middle one, and outside the cannon itself. */
	@JvmStatic
	fun isDoubleHitSpot(x: Int, y: Int): Boolean {
		val nextToMiddle: Boolean = y == 1 || x == 1 || y == -1 || x == -1
		val onCannon: Boolean = y >= -1 && y <= 1 && x >= -1 && x <= 1
		return nextToMiddle && !onCannon
	}

	private fun drawSpots(g: Graphics, view: Viewport, client: ClientApi, player: Player) {
		for (spot in CannonSpots.POINTS) {
			val sceneX: Int = spot[0] - client.sceneBaseX()
			val sceneY: Int = spot[1] - client.sceneBaseY()
			if (spot[2] != player.level() || !inScene(client, sceneX, sceneY) || !near(player, sceneX, sceneY)) {
				continue
			}
			TileOutline.draw(g, view, client, sceneX, sceneY, spot[2], 1, RED, fill(RED), 2, xs, ys)
			if (client.projectTile(sceneX, sceneY, spot[2], 0, point)) {
				g.itemIcon(CANNONBALL, 1, view.x + point[0] - ICON_WIDTH / 2, view.y + point[1] - ICON_HEIGHT / 2)
			}
			drawnSpots++
		}
	}

	private fun inScene(client: ClientApi, sceneX: Int, sceneY: Int): Boolean {
		return sceneX >= 0 && sceneY >= 0 && sceneX < client.sceneSize() && sceneY < client.sceneSize()
	}

	private fun near(player: Player, sceneX: Int, sceneY: Int): Boolean {
		return maxOf(kotlin.math.abs(player.tileX() - sceneX), kotlin.math.abs(player.tileY() - sceneY)) <= MAX_OVERLAY_TILES
	}

	/** RuneLite's OverlayUtil.renderPolygon fill: the colour at alpha 50. */
	private fun fill(argb: Int): Int = (argb and 0xFFFFFF) or (50 shl 24)

	/** RuneLite's CannonCounter: the cannonball's icon and the count, in the count's colour. */
	class CannonCounter : Counter("Cannonballs", 0) {
		init {
			itemId = CANNONBALL
			tooltip = "Cannonballs"
		}

		override fun text(): String? = cballsLeft.toString()

		override fun textColour(): Int = stateColour(cballsLeft)
	}

	private const val ICON_WIDTH: Int = 36
	private const val ICON_HEIGHT: Int = 32

	private const val OPAQUE: Int = -0x1000000
	private const val BLACK: Int = -0x1000000
	private const val RED: Int = 0xFFFF0000.toInt()
	private const val GREEN: Int = 0xFF00FF00.toInt()

	/** Java's Color.ORANGE. */
	private const val ORANGE: Int = 0xFFFFC800.toInt()
}

/**
 * RuneLite's CannonSpots where Void has the monsters: the spots whose monster spawns within ten tiles on
 * the 634 server (the rest are OSRS-only places, such as Kourend and the Wilderness slayer cave). Each
 * is a world x, y and floor.
 */
object CannonSpots {

	@JvmField
	val POINTS: Array<IntArray> = arrayOf(
		intArrayOf(3037, 3700, 0), // bandits
		intArrayOf(2859, 9778, 0), // black demons
		intArrayOf(3089, 9960, 0),
		intArrayOf(2906, 9685, 0), // black knights
		intArrayOf(3053, 3852, 0),
		intArrayOf(3596, 9743, 0), // bloodvelds
		intArrayOf(2707, 10132, 0), // brine rats
		intArrayOf(2524, 10020, 0), // dagannoth
		intArrayOf(1992, 4655, 0), // dark beasts
		intArrayOf(3030, 3632, 0), // dark warriors
		intArrayOf(3218, 9366, 0), // dust devils
		intArrayOf(3120, 9987, 0), // earth warriors
		intArrayOf(3047, 10340, 0), // fire giants
		intArrayOf(3044, 10318, 0), // hill giants
		intArrayOf(2955, 3876, 0), // ice warriors
		intArrayOf(2838, 9559, 0), // lesser demons
		intArrayOf(3190, 3960, 0), // magic axes
		intArrayOf(3297, 9252, 0), // minions of Scarabas
		intArrayOf(3159, 9903, 0), // moss giants
		intArrayOf(3285, 3930, 0), // rogues
		intArrayOf(3017, 3589, 0), // skeletons
		intArrayOf(3169, 3886, 0), // spiders
		intArrayOf(2114, 3943, 0), // suqahs
		intArrayOf(2401, 3856, 0), // trolls
	)
}
