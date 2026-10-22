package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.LocDefinition
import com.jagex.game.runetek5.plugin.api.LocVisitor
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Object Markers. Hold Shift and right-click an object
 * for Mark object; a marked object's menu has Unmark object and three submenus: Mark border color and
 * Mark fill color (the colours already in use and five defaults, or Pick color for the client's colour
 * picker; Reset for the fill) and Mark style (Hull, Outline, Clickbox, Tile, Reset), each its own
 * choice over the settings' defaults. Marked objects are drawn every frame in their styles: the convex
 * hull of the model, its outline, the game's click box, or its tiles. A marker is kept by the map
 * region in RuneLite's JSON (ObjectPoint), so RuneLite's markers can be pasted into the settings file.
 * An object that changes with the player's variables (a tree, a door) is drawn only while it is the
 * one that was marked, by name.
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object ObjectIndicators : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("objectindicators", "Object Markers", "1", PluginApi.VERSION, "Shift right-click an object to mark it; marked objects are outlined (hull, outline, click box or tiles) in a colour of their own, kept between sessions.", "Pixel")

	/** One marker, RuneLite's ObjectPoint: the base id and the name it had, where (a 64x64 map region and the tile in it), and its own colours and styles (null: the settings'). */
	class ObjectPoint(@JvmField val id: Int, @JvmField val name: String, @JvmField val regionId: Int, @JvmField val regionX: Int, @JvmField val regionY: Int, @JvmField val z: Int) {
		@JvmField var borderColour: Int? = null
		@JvmField var fillColour: Int? = null
		@JvmField var hull: Boolean? = null
		@JvmField var outline: Boolean? = null
		@JvmField var clickbox: Boolean? = null
		@JvmField var tile: Boolean? = null

		fun worldX(): Int = ((regionId shr 8) shl 6) + regionX

		fun worldY(): Int = ((regionId and 0xFF) shl 6) + regionY
	}

	const val HF_HULL: Int = 1
	const val HF_OUTLINE: Int = 2
	const val HF_CLICKBOX: Int = 4
	const val HF_TILE: Int = 8

	const val REGION_PREFIX: String = "region_"

	private const val YELLOW: Int = 0xFFFFFF00.toInt()
	private const val RED: Int = 0xFFFF0000.toInt()
	private const val GREEN: Int = 0xFF00FF00.toInt()
	private const val BLUE: Int = 0xFF0000FF.toInt()
	private const val MAGENTA: Int = 0xFFFF00FF.toInt()

	/** RuneLite's default hull fill, Color(0, 0, 0, 50). */
	const val DEFAULT_HULL_FILL: Int = 0x32000000

	/** The most colours a colour submenu offers. */
	private const val MAX_COLOURS: Int = 10

	@JvmField var enabled: Boolean = true
	@JvmField var highlightHull: Boolean = true
	@JvmField var highlightOutline: Boolean = false
	@JvmField var highlightClickbox: Boolean = false
	@JvmField var highlightTile: Boolean = false
	@JvmField var markerColour: Int = YELLOW
	@JvmField var fillColour: Int? = null
	@JvmField var borderWidth: Int = 2
	@JvmField var outlineFeather: Int = 0

	/** Every marker the settings hold, by region. */
	@JvmField
	val points: HashMap<Int, ArrayList<ObjectPoint>> = HashMap()

	/** How many markers the last frame drew, for the smoke test. */
	@JvmField
	var drawnLastFrame: Int = 0

	private var context: PluginContext? = null

	private var markAction: Int = -1
	private var pickBorderAction: Int = -1
	private var pickFillAction: Int = -1
	private var resetFillAction: Int = -1
	private val borderActions: IntArray = IntArray(MAX_COLOURS)
	private val fillActions: IntArray = IntArray(MAX_COLOURS)
	private val styleActions: IntArray = IntArray(5)

	/** The colours the last menu offered, for its Set color entries. */
	private var offeredBorder: IntArray = IntArray(0)

	private var offeredFill: IntArray = IntArray(0)

	private val xs: IntArray = IntArray(256)

	private val ys: IntArray = IntArray(256)

	private val point: IntArray = IntArray(2)

	private val overlay: Overlay = Overlay { g, view -> draw(g, view) }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event -> context?.client()?.let { menuEntryAdded(it, event) } }

	private val command: CommandHandler = CommandHandler { _, _ ->
		val context: PluginContext = this.context ?: return@CommandHandler
		enabled = !enabled
		context.config().setBoolean("enabled", enabled)
		context.config().save()
		context.client().consolePrint(if (enabled) "Object markers on." else "Object markers off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		context.settings().toggle("enabled", "Object markers", true)
		context.settings().toggle("highlightHull", "Highlight hull", true)
		context.settings().toggle("highlightOutline", "Highlight outline", false)
		context.settings().toggle("highlightClickbox", "Highlight clickbox", false)
		context.settings().toggle("highlightTile", "Highlight tile", false)
		context.settings().colour("markerColor", "Marker colour", YELLOW, true)
		context.settings().toggle("useFillColor", "Use a fill colour for new markers", false)
		context.settings().colour("fillColor", "Fill colour", DEFAULT_HULL_FILL, true)
		context.settings().number("borderWidth", "Border width", 2, 1, 10, 1)
		context.settings().number("outlineFeather", "Outline feather", 0, 0, 4, 1)
		readSettings(context.config())
		markAction = context.registerMenuAction(MenuAction { _, x, y, id -> toggleMark(x, y, id) })
		pickBorderAction = context.registerMenuAction(MenuAction { _, x, y, id -> pick(x, y, id, true) })
		pickFillAction = context.registerMenuAction(MenuAction { _, x, y, id -> pick(x, y, id, false) })
		resetFillAction = context.registerMenuAction(MenuAction { _, x, y, id -> update(x, y, id) { it.fillColour = null } })
		for (k in 0 until MAX_COLOURS) {
			borderActions[k] = context.registerMenuAction(MenuAction { _, x, y, id -> offeredBorder.getOrNull(k)?.let { c -> update(x, y, id) { it.borderColour = c } } })
			fillActions[k] = context.registerMenuAction(MenuAction { _, x, y, id -> offeredFill.getOrNull(k)?.let { c -> update(x, y, id) { it.fillColour = c } } })
		}
		styleActions[0] = context.registerMenuAction(MenuAction { _, x, y, id -> update(x, y, id) { it.hull = it.hull != true } })
		styleActions[1] = context.registerMenuAction(MenuAction { _, x, y, id -> update(x, y, id) { it.outline = it.outline != true } })
		styleActions[2] = context.registerMenuAction(MenuAction { _, x, y, id -> update(x, y, id) { it.clickbox = it.clickbox != true } })
		styleActions[3] = context.registerMenuAction(MenuAction { _, x, y, id -> update(x, y, id) { it.tile = it.tile != true } })
		styleActions[4] = context.registerMenuAction(MenuAction { _, x, y, id ->
			update(x, y, id) {
				it.hull = null
				it.outline = null
				it.clickbox = null
				it.tile = null
			}
		})
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.addOverlay(overlay)
		context.registerCommand("objectmarkers", command)
	}

	override fun stop() {
		context = null
		points.clear()
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		highlightHull = config.getBoolean("highlightHull", true)
		highlightOutline = config.getBoolean("highlightOutline", false)
		highlightClickbox = config.getBoolean("highlightClickbox", false)
		highlightTile = config.getBoolean("highlightTile", false)
		markerColour = config.getColour("markerColor", YELLOW)
		fillColour = if (config.getBoolean("useFillColor", false)) config.getColour("fillColor", DEFAULT_HULL_FILL) else null
		borderWidth = config.getInt("borderWidth", 2)
		outlineFeather = config.getInt("outlineFeather", 0)
		points.clear()
		for (key in config.keys()) {
			loadRegion(config, key)
		}
	}

	/** A region's markers from its setting, when [key] is one. */
	private fun loadRegion(config: PluginConfig, key: String) {
		if (!key.startsWith(REGION_PREFIX)) {
			return
		}
		val region: Int = key.substring(REGION_PREFIX.length).toIntOrNull() ?: return
		val loaded: ArrayList<ObjectPoint> = ObjectPointJson.parse(config.get(key, "")) ?: return
		// RuneLite drops the markers of objects whose name was "null"
		loaded.removeAll { it.name.isEmpty() || it.name == "null" }
		points[region] = loaded
	}

	private fun save(regionId: Int) {
		val config: PluginConfig = context?.config() ?: return
		val list: ArrayList<ObjectPoint>? = points[regionId]
		if (list == null || list.isEmpty()) {
			points.remove(regionId)
			config.remove(REGION_PREFIX + regionId)
		} else {
			config.set(REGION_PREFIX + regionId, ObjectPointJson.format(list))
		}
		config.save()
	}

	@JvmStatic
	fun regionOf(worldX: Int, worldY: Int): Int = ((worldX shr 6) shl 8) or (worldY shr 6)

	// --- the menu ---

	/** A location on a tile: its base id, kind, rotation, and its definition as it is now. */
	private class Found(@JvmField val baseId: Int, @JvmField val rotation: Int, @JvmField val type: LocDefinition?)

	/** The location on scene tile ([sceneX], [sceneY]) whose base or current id is [id] (RuneLite's findTileObject, with its impostor check). */
	private fun findLoc(client: ClientApi, sceneX: Int, sceneY: Int, level: Int, id: Int): Found? {
		var found: Found? = null
		client.locsAt(sceneX, sceneY, level, LocVisitor { baseId, _, rotation, _ ->
			if (found == null && (baseId == id || client.resolveLoc(baseId) == id)) {
				found = Found(baseId, rotation, client.locType(client.resolveLoc(baseId)) ?: client.locType(baseId))
			}
		})
		return found
	}

	/** The tile RuneLite keeps an object by: its middle (its south-west tile plus half its size, the size turned with it). */
	private fun markTile(client: ClientApi, sceneX: Int, sceneY: Int, found: Found): IntArray {
		val base: LocDefinition? = client.locType(found.baseId)
		var sizeX: Int = base?.sizeX() ?: 1
		var sizeY: Int = base?.sizeY() ?: 1
		if (found.rotation and 1 == 1) {
			val t: Int = sizeX
			sizeX = sizeY
			sizeY = t
		}
		return intArrayOf(client.sceneBaseX() + sceneX + sizeX / 2, client.sceneBaseY() + sceneY + sizeY / 2)
	}

	private fun pointAt(worldX: Int, worldY: Int, level: Int, baseId: Int): ObjectPoint? {
		val list: ArrayList<ObjectPoint> = points[regionOf(worldX, worldY)] ?: return null
		return list.firstOrNull { it.id == baseId && it.worldX() == worldX && it.worldY() == worldY && it.z == level }
	}

	private fun menuEntryAdded(client: ClientApi, event: MenuEntryAdded) {
		if (!enabled || event.target !== MenuTarget.LOC || !client.isKeyHeld(KeyCode.SHIFT)) {
			return
		}
		val found: Found = findLoc(client, event.sceneX, event.sceneY, event.level, event.id) ?: return
		val tile: IntArray = markTile(client, event.sceneX, event.sceneY, found)
		val marked: ObjectPoint? = pointAt(tile[0], tile[1], event.level, found.baseId)
		val name: String = found.type?.name() ?: return
		val target: String = " <col=00ffff>" + name
		// the last added is the highest: the submenus first, Mark or Unmark on top
		if (marked != null) {
			styleMenu(client, target)
			fillMenu(client, target)
			borderMenu(client, target)
		}
		client.addMenuEntry((if (marked != null) "Unmark object" else "Mark object") + target, markAction)
	}

	/** The colours markers use (border or fill), padded with RuneLite's five defaults. */
	private fun usedColours(border: Boolean): ArrayList<Int> {
		val colours: ArrayList<Int> = ArrayList()
		for (list in points.values) {
			for (p in list) {
				val c: Int? = if (border) p.borderColour else p.fillColour
				if (c != null && !colours.contains(c) && colours.size < MAX_COLOURS) {
					colours.add(c)
				}
			}
		}
		for (base in intArrayOf(RED, GREEN, BLUE, YELLOW, MAGENTA)) {
			// RuneLite's fill defaults are the same colours at a twelfth of their alpha
			val c: Int = if (border) base else ((255 / 12) shl 24) or (base and 0xFFFFFF)
			if (colours.size < 5 && !colours.contains(c)) {
				colours.add(c)
			}
		}
		return colours
	}

	private fun borderMenu(client: ClientApi, target: String) {
		val submenu: Int = client.addSubmenu("Mark border color" + target)
		val colours: ArrayList<Int> = usedColours(true)
		offeredBorder = colours.toIntArray()
		for (k in colours.indices) {
			client.addSubmenuEntry(submenu, GroundItems.colourTag(colours[k]) + "Set color", borderActions[k])
		}
		client.addSubmenuEntry(submenu, "Pick color", pickBorderAction)
	}

	private fun fillMenu(client: ClientApi, target: String) {
		val submenu: Int = client.addSubmenu("Mark fill color" + target)
		val colours: ArrayList<Int> = usedColours(false)
		offeredFill = colours.toIntArray()
		for (k in colours.indices) {
			client.addSubmenuEntry(submenu, GroundItems.colourTag(colours[k]) + "Set color", fillActions[k])
		}
		client.addSubmenuEntry(submenu, "Pick color", pickFillAction)
		client.addSubmenuEntry(submenu, "Reset", resetFillAction)
	}

	private fun styleMenu(client: ClientApi, target: String) {
		val submenu: Int = client.addSubmenu("Mark style" + target)
		val labels: Array<String> = arrayOf("Hull", "Outline", "Clickbox", "Tile", "Reset")
		for (k in labels.indices) {
			client.addSubmenuEntry(submenu, labels[k], styleActions[k])
		}
	}

	/** RuneLite's markObject: marks the object, or unmarks it when it is marked (by id, or by name at its tile). */
	@JvmStatic
	fun toggleMark(sceneX: Int, sceneY: Int, id: Int) {
		val client: ClientApi = context?.client() ?: return
		val level: Int = client.localPlayer()?.level() ?: return
		val found: Found = findLoc(client, sceneX, sceneY, level, id) ?: return
		val name: String = found.type?.name() ?: return
		if (name.isEmpty() || name == "null") {
			return
		}
		val tile: IntArray = markTile(client, sceneX, sceneY, found)
		val region: Int = regionOf(tile[0], tile[1])
		val list: ArrayList<ObjectPoint> = points.getOrPut(region) { ArrayList() }
		val regionX: Int = tile[0] and 63
		val regionY: Int = tile[1] and 63
		val existing: ObjectPoint? = list.firstOrNull { (it.id == -1 || it.id == found.baseId || it.name == name) && it.regionX == regionX && it.regionY == regionY && it.z == level }
		if (existing != null) {
			list.remove(existing)
		} else {
			val p: ObjectPoint = ObjectPoint(found.baseId, name, region, regionX, regionY, level)
			p.borderColour = markerColour
			p.fillColour = fillColour
			list.add(p)
		}
		save(region)
	}

	private fun update(sceneX: Int, sceneY: Int, id: Int, change: (ObjectPoint) -> Unit) {
		val client: ClientApi = context?.client() ?: return
		val level: Int = client.localPlayer()?.level() ?: return
		val found: Found = findLoc(client, sceneX, sceneY, level, id) ?: return
		val tile: IntArray = markTile(client, sceneX, sceneY, found)
		val p: ObjectPoint = pointAt(tile[0], tile[1], level, found.baseId) ?: return
		change(p)
		save(p.regionId)
	}

	private fun pick(sceneX: Int, sceneY: Int, id: Int, border: Boolean) {
		val context: PluginContext = this.context ?: return
		val client: ClientApi = context.client()
		val level: Int = client.localPlayer()?.level() ?: return
		val found: Found = findLoc(client, sceneX, sceneY, level, id) ?: return
		val tile: IntArray = markTile(client, sceneX, sceneY, found)
		val p: ObjectPoint = pointAt(tile[0], tile[1], level, found.baseId) ?: return
		val initial: Int = if (border) p.borderColour ?: markerColour else p.fillColour ?: DEFAULT_HULL_FILL
		context.pickColour(initial, true) { argb ->
			if (border) {
				update(sceneX, sceneY, id) { it.borderColour = argb }
			} else {
				update(sceneX, sceneY, id) { it.fillColour = argb }
			}
		}
	}

	// --- drawing ---

	/** The styles a marker is drawn in: its own when it chose any, else the settings' (RuneLite's flags). */
	@JvmStatic
	fun flags(p: ObjectPoint): Int {
		val own: Int = (if (p.hull == true) HF_HULL else 0) or (if (p.outline == true) HF_OUTLINE else 0) or (if (p.clickbox == true) HF_CLICKBOX else 0) or (if (p.tile == true) HF_TILE else 0)
		if (own != 0) {
			return own
		}
		return (if (highlightHull) HF_HULL else 0) or (if (highlightOutline) HF_OUTLINE else 0) or (if (highlightClickbox) HF_CLICKBOX else 0) or (if (highlightTile) HF_TILE else 0)
	}

	private fun draw(g: Graphics, view: Viewport) {
		drawnLastFrame = 0
		val client: ClientApi = context?.client() ?: return
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val player: Player = client.localPlayer() ?: return
		val baseX: Int = client.sceneBaseX()
		val baseY: Int = client.sceneBaseY()
		val size: Int = client.sceneSize()
		for (list in points.values) {
			for (p in list) {
				val sceneX: Int = p.worldX() - baseX
				val sceneY: Int = p.worldY() - baseY
				if (p.z == player.level() && sceneX >= 0 && sceneY >= 0 && sceneX < size && sceneY < size) {
					drawPoint(g, view, client, p, sceneX, sceneY)
				}
			}
		}
	}

	/** One marker, if its object is on its tile and (a changing object) is still the one marked. */
	private fun drawPoint(g: Graphics, view: Viewport, client: ClientApi, p: ObjectPoint, sceneX: Int, sceneY: Int) {
		var present: Boolean = false
		var rotation: Int = 0
		client.locsAt(sceneX, sceneY, p.z, LocVisitor { id, _, rot, _ ->
			if (id == p.id) {
				present = true
				rotation = rot
			}
		})
		if (!present) {
			return
		}
		val now: LocDefinition? = client.locType(client.resolveLoc(p.id))
		val nowName: String? = now?.name()
		if (nowName == null || nowName.isEmpty() || nowName == "null" || nowName != p.name) {
			return // a changing object that is not the one marked now (a stump, an open door)
		}
		val flags: Int = flags(p)
		val border: Int = p.borderColour ?: markerColour
		var drawn: Boolean = false
		if (flags and HF_HULL != 0) {
			val n: Int = client.locHull(sceneX, sceneY, p.z, p.id, xs, ys)
			if (n >= 3) {
				shape(g, view, n, border, p.fillColour ?: DEFAULT_HULL_FILL, borderWidth)
				drawn = true
			}
		}
		if (flags and HF_OUTLINE != 0) {
			drawn = client.locOutline(sceneX, sceneY, p.z, p.id, borderWidth, outlineFeather, border) || drawn
		}
		if (flags and HF_CLICKBOX != 0) {
			val n: Int = client.locClickbox(sceneX, sceneY, p.z, p.id, xs, ys)
			if (n >= 3) {
				shape(g, view, n, border, p.fillColour ?: withTwelfthAlpha(border), borderWidth)
				drawn = true
			}
		}
		if (flags and HF_TILE != 0 && tilePolygon(client, p, sceneX, sceneY, rotation)) {
			shape(g, view, 4, border, p.fillColour ?: withTwelfthAlpha(border), minOf(borderWidth, 2))
			drawn = true
		}
		if (drawn) {
			drawnLastFrame++
		}
	}

	private fun withTwelfthAlpha(argb: Int): Int = (((argb ushr 24) / 12) shl 24) or (argb and 0xFFFFFF)

	/** The object's tiles: RuneLite's getCanvasTilePoly, the area its size (turned with it) covers around the tile it is kept by. */
	private fun tilePolygon(client: ClientApi, p: ObjectPoint, sceneX: Int, sceneY: Int, rotation: Int): Boolean {
		val type: LocDefinition? = client.locType(p.id)
		var sizeX: Int = type?.sizeX() ?: 1
		var sizeY: Int = type?.sizeY() ?: 1
		if (rotation and 1 == 1) {
			val t: Int = sizeX
			sizeX = sizeY
			sizeY = t
		}
		val x0: Int = (sceneX - sizeX / 2) * 512
		val y0: Int = (sceneY - sizeY / 2) * 512
		val cornersX: IntArray = intArrayOf(x0, x0 + sizeX * 512, x0 + sizeX * 512, x0)
		val cornersY: IntArray = intArrayOf(y0, y0, y0 + sizeY * 512, y0 + sizeY * 512)
		for (i in 0 until 4) {
			if (!client.projectPoint(cornersX[i], cornersY[i], p.z, 0, point)) {
				return false
			}
			xs[i] = point[0]
			ys[i] = point[1]
		}
		return true
	}

	/** RuneLite's renderPolygon: filled, then outlined [width] pixels wide; the points are view-relative. */
	private fun shape(g: Graphics, view: Viewport, n: Int, border: Int, fill: Int, width: Int) {
		for (i in 0 until n) {
			xs[i] += view.x
			ys[i] += view.y
		}
		g.fillPolygon(xs, ys, n, fill)
		for (i in 0 until n) {
			val j: Int = (i + 1) % n
			g.thickLine(xs[i], ys[i], xs[j], ys[j], width, border)
		}
	}
}
