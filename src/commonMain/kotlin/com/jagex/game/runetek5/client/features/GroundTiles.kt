package com.jagex.game.runetek5.client.features

import awt.datatransfer.STRING_FLAVOR
import awt.datatransfer.StringSelection
import awt.datatransfer.Transferable
import com.jagex.game.runetek5.client.input.console.Console
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Overlay
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
import com.jagex.game.runetek5.plugin.api.events.KeyPressed
import com.jagex.game.runetek5.plugin.api.events.KeyTyped
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: tiles the player marks on the ground, after RuneLite's Ground
 * Markers. Hold Shift and right-click a tile for Mark tile; a marked tile's menu has Unmark tile,
 * Label tile (type the label, Enter keeps it, Escape leaves it) and Colour tile (gives it the
 * colour chosen in the settings). Each tile is drawn as a filled outline on the ground, with its
 * label, and on the minimap when asked. The tiles are kept between sessions in the plugin's
 * settings, one line a map region in RuneLite's JSON, which is also what Export puts on the
 * clipboard and Import reads from it (`::groundtiles export|import|clear`, or the same three in
 * the Shift menu).
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md) that replaced the client's own tile markers.
 */
object GroundTiles : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("groundtiles", "Ground Tiles", "1", PluginApi.VERSION, "Shift right-click a tile to mark it: outlined and filled, with a label and a colour, kept between sessions and shared in RuneLite's ground marker format (::groundtiles export|import|clear).", "Pixel")

	/** One marked tile, where RuneLite keeps it: a 64x64 map region and the tile inside it. */
	class Marker(@JvmField val regionId: Int, @JvmField val regionX: Int, @JvmField val regionY: Int, @JvmField val z: Int, @JvmField var colour: Int, @JvmField var label: String?) {
		@JvmField
		val worldX: Int = ((regionId shr 8) shl 6) + regionX

		@JvmField
		val worldY: Int = ((regionId and 0xFF) shl 6) + regionY
	}

	/** The colours the settings offer, and their values. */
	@JvmField
	val COLOUR_NAMES: Array<String> = arrayOf("Yellow", "Red", "Green", "Blue", "Cyan", "Magenta", "Orange", "White", "Purple", "Pink", "Black")

	private val COLOUR_VALUES: IntArray = intArrayOf(0xFFFFFF00.toInt(), 0xFFFF0000.toInt(), 0xFF00FF00.toInt(), 0xFF0000FF.toInt(), 0xFF00FFFF.toInt(), 0xFFFF00FF.toInt(), 0xFFFF8000.toInt(), 0xFFFFFFFF.toInt(), 0xFF8000FF.toInt(), 0xFFFF80C0.toInt(), 0xFF000000.toInt())

	private const val DEFAULT_FILL_OPACITY: Int = 50

	private const val DEFAULT_BORDER_WIDTH: Int = 2

	private const val MAX_BORDER_WIDTH: Int = 5

	private const val MAX_LABEL: Int = 32

	/** The settings key of a region's tiles: this plus the region id. */
	const val REGION_PREFIX: String = "region_"

	/** The client's key codes for Enter, Backspace and Escape (CanvasKeyRecorder.REMAPPED_KEYS). */
	private const val KEY_ENTER: Int = 84

	private const val KEY_BACKSPACE: Int = 85

	private const val KEY_ESCAPE: Int = 13

	private const val LABEL_COLOUR: Int = 0xFFFFFFFF.toInt()

	private const val SHADOW: Int = 0xFF000000.toInt()

	private const val MINIMAP_DOT: Int = 3

	@JvmField
	var enabled: Boolean = true

	private var colour: Int = COLOUR_VALUES[0]

	private var fillOpacity: Int = DEFAULT_FILL_OPACITY

	private var borderWidth: Int = DEFAULT_BORDER_WIDTH

	private var drawOnMinimap: Boolean = false

	private var showImportExport: Boolean = true

	/** Every marked tile the settings hold, in no order. */
	@JvmField
	val markers: ArrayList<Marker> = ArrayList()

	/** How many tiles the last frame drew, for the smoke test. */
	@JvmField
	var drawnLastFrame: Int = 0

	/** The screen corners of the last tile drawn, x then y for each, for the smoke test. */
	@JvmField
	val lastCorners: IntArray = IntArray(8)

	private var context: PluginContext? = null

	/** The tile whose label is being typed, and the text so far. */
	private var labelling: Marker? = null

	private val labelText: StringBuilder = StringBuilder()

	private val xs: IntArray = IntArray(4)

	private val ys: IntArray = IntArray(4)

	private val point: IntArray = IntArray(2)

	private val area: IntArray = IntArray(4)

	private var markAction: Int = -1

	private var unmarkAction: Int = -1

	private var labelAction: Int = -1

	private var colourAction: Int = -1

	private var exportAction: Int = -1

	private var importAction: Int = -1

	private var clearAction: Int = -1

	private val overlay: Overlay = Overlay { g, view -> draw(g, view) }

	/** The minimap is an interface drawn over the game view: the tiles' dots go on at the end of the frame, over everything. */
	private val frameListener: EventListener<FrameDraw> = EventListener { event -> drawMinimapDots(event.graphics) }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	/** The Shift menu of a tile: Mark or Unmark, Label and Colour for a marked one, and the three sharing entries. */
	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (!enabled || event.target !== MenuTarget.TILE || !client.isKeyHeld(KeyCode.SHIFT)) {
			return@EventListener
		}
		// the last entry added is the highest: added bottom up
		if (showImportExport) {
			client.addMenuEntry("Clear ground tiles", clearAction)
			client.addMenuEntry("Import ground tiles", importAction)
			client.addMenuEntry("Export ground tiles", exportAction)
		}
		val marker: Marker? = markerAt(client.sceneBaseX() + event.sceneX, client.sceneBaseY() + event.sceneY, event.level)
		if (marker == null) {
			client.addMenuEntry("Mark tile", markAction)
		} else {
			if (marker.colour != colour) {
				client.addMenuEntry("Colour tile", colourAction)
			}
			client.addMenuEntry("Label tile", labelAction)
			client.addMenuEntry("Unmark tile", unmarkAction)
		}
	}

	private val keyTypedListener: EventListener<KeyTyped> = EventListener { event ->
		if (labelling != null) {
			event.consume()
			val c: Char = event.keyChar
			if (c >= ' ' && c < '\u007f' && labelText.length < MAX_LABEL) {
				labelText.append(c)
			}
		}
	}

	private val keyPressedListener: EventListener<KeyPressed> = EventListener { event ->
		val marker: Marker = labelling ?: return@EventListener
		when (event.keyCode) {
			KEY_ENTER -> {
				marker.label = if (labelText.isEmpty()) null else labelText.toString()
				labelling = null
				saveRegion(marker.regionId)
				event.consume()
			}
			KEY_ESCAPE -> {
				labelling = null
				event.consume()
			}
			KEY_BACKSPACE -> {
				if (labelText.isNotEmpty()) {
					labelText.setLength(labelText.length - 1)
				}
				event.consume()
			}
		}
	}

	private val stateListener: EventListener<GameStateChanged> = EventListener { _ -> labelling = null }

	private val command: CommandHandler = CommandHandler { _, args ->
		val context: PluginContext = this.context ?: return@CommandHandler
		val client: ClientApi = context.client()
		val what: String = if (args.isEmpty()) "" else args[0].lowercase()
		when (what) {
			"export" -> export(client)
			"import" -> import(client)
			"clear" -> clear(client)
			else -> {
				enabled = !enabled
				context.config().setBoolean("enabled", enabled)
				context.config().save()
				client.consolePrint(if (enabled) "Ground tiles on." else "Ground tiles off.")
			}
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val config: PluginConfig = context.config()
		readSettings(config)
		context.settings().toggle("enabled", "Ground tiles", true)
		context.settings().choice("colour", "Tile colour", COLOUR_NAMES[0], COLOUR_NAMES)
		context.settings().number("fillOpacity", "Fill opacity", DEFAULT_FILL_OPACITY, 0, 255, 5)
		context.settings().number("borderWidth", "Border width", DEFAULT_BORDER_WIDTH, 1, MAX_BORDER_WIDTH, 1)
		context.settings().toggle("drawOnMinimap", "Draw tiles on the minimap", false)
		context.settings().toggle("showImportExport", "Import, Export and Clear in the Shift menu", true)
		loadAll(config)
		markAction = context.registerMenuAction(MenuAction { _, sceneX, sceneY, _ -> mark(sceneX, sceneY) })
		unmarkAction = context.registerMenuAction(MenuAction { _, sceneX, sceneY, _ -> unmark(sceneX, sceneY) })
		labelAction = context.registerMenuAction(MenuAction { _, sceneX, sceneY, _ -> startLabel(sceneX, sceneY) })
		colourAction = context.registerMenuAction(MenuAction { _, sceneX, sceneY, _ -> colourTile(sceneX, sceneY) })
		exportAction = context.registerMenuAction(MenuAction { _, _, _, _ -> export(context.client()) })
		importAction = context.registerMenuAction(MenuAction { _, _, _, _ -> import(context.client()) })
		clearAction = context.registerMenuAction(MenuAction { _, _, _, _ -> clear(context.client()) })
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.events().subscribe(Events.KEY_TYPED, keyTypedListener)
		context.events().subscribe(Events.KEY_PRESSED, keyPressedListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.addOverlay(overlay)
		context.registerCommand("groundtiles", command)
	}

	override fun stop() {
		context = null
		labelling = null
		markers.clear()
	}

	private fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		colour = colourNamed(config.get("colour", COLOUR_NAMES[0]))
		fillOpacity = config.getInt("fillOpacity", DEFAULT_FILL_OPACITY).coerceIn(0, 255)
		borderWidth = config.getInt("borderWidth", DEFAULT_BORDER_WIDTH).coerceIn(1, MAX_BORDER_WIDTH)
		drawOnMinimap = config.getBoolean("drawOnMinimap", false)
		showImportExport = config.getBoolean("showImportExport", true)
	}

	/** The colour of a settings name; the first (yellow) for one the list has not. */
	@JvmStatic
	fun colourNamed(name: String): Int {
		for (i in COLOUR_NAMES.indices) {
			if (COLOUR_NAMES[i].equals(name, ignoreCase = true)) {
				return COLOUR_VALUES[i]
			}
		}
		return COLOUR_VALUES[0]
	}

	// --- the tiles ---

	/** The id of the 64x64 map region holding a world tile, as RuneLite numbers them. */
	@JvmStatic
	fun regionOf(worldX: Int, worldY: Int): Int {
		return ((worldX shr 6) shl 8) or (worldY shr 6)
	}

	@JvmStatic
	fun markerAt(worldX: Int, worldY: Int, level: Int): Marker? {
		for (marker in markers) {
			if (marker.worldX == worldX && marker.worldY == worldY && marker.z == level) {
				return marker
			}
		}
		return null
	}

	/** Whether the scene tile is marked on the local player's level. */
	@JvmStatic
	fun isMarked(sceneX: Int, sceneY: Int): Boolean {
		val client: ClientApi = context?.client() ?: return false
		return markerAt(client.sceneBaseX() + sceneX, client.sceneBaseY() + sceneY, level(client)) != null
	}

	/** Marks the scene tile on the local player's level with the settings' colour, if it is not marked. */
	@JvmStatic
	fun mark(sceneX: Int, sceneY: Int) {
		val client: ClientApi = context?.client() ?: return
		val worldX: Int = client.sceneBaseX() + sceneX
		val worldY: Int = client.sceneBaseY() + sceneY
		val level: Int = level(client)
		if (markerAt(worldX, worldY, level) != null) {
			return
		}
		val regionId: Int = regionOf(worldX, worldY)
		markers.add(Marker(regionId, worldX and 63, worldY and 63, level, colour, null))
		saveRegion(regionId)
	}

	@JvmStatic
	fun unmark(sceneX: Int, sceneY: Int) {
		val client: ClientApi = context?.client() ?: return
		val marker: Marker = markerAt(client.sceneBaseX() + sceneX, client.sceneBaseY() + sceneY, level(client)) ?: return
		if (labelling === marker) {
			labelling = null
		}
		markers.remove(marker)
		saveRegion(marker.regionId)
	}

	private fun startLabel(sceneX: Int, sceneY: Int) {
		val client: ClientApi = context?.client() ?: return
		val marker: Marker = markerAt(client.sceneBaseX() + sceneX, client.sceneBaseY() + sceneY, level(client)) ?: return
		labelling = marker
		labelText.setLength(0)
		labelText.append(marker.label ?: "")
		client.chatMessage("Type the tile's label and press Enter; Escape leaves it as it is.")
	}

	private fun colourTile(sceneX: Int, sceneY: Int) {
		val client: ClientApi = context?.client() ?: return
		val marker: Marker = markerAt(client.sceneBaseX() + sceneX, client.sceneBaseY() + sceneY, level(client)) ?: return
		marker.colour = colour
		saveRegion(marker.regionId)
	}

	private fun level(client: ClientApi): Int {
		return client.localPlayer()?.level() ?: 0
	}

	// --- the settings' copy ---

	private fun loadAll(config: PluginConfig) {
		markers.clear()
		for (key in config.keys()) {
			if (key.startsWith(REGION_PREFIX)) {
				markers.addAll(GroundTileJson.parse(config.get(key, "")) ?: ArrayList())
			}
		}
	}

	/** Writes one region's tiles to the settings, as RuneLite's JSON; a region with none loses its line. */
	private fun saveRegion(regionId: Int) {
		val config: PluginConfig = context?.config() ?: return
		val inRegion: ArrayList<Marker> = ArrayList()
		for (marker in markers) {
			if (marker.regionId == regionId) {
				inRegion.add(marker)
			}
		}
		if (inRegion.isEmpty()) {
			config.remove(REGION_PREFIX + regionId)
		} else {
			config.set(REGION_PREFIX + regionId, GroundTileJson.format(inRegion))
		}
		config.save()
	}

	// --- sharing ---

	/** The ids of the map regions the scene covers. */
	private fun sceneRegions(client: ClientApi): ArrayList<Int> {
		val regions: ArrayList<Int> = ArrayList()
		val baseX: Int = client.sceneBaseX()
		val baseY: Int = client.sceneBaseY()
		val size: Int = client.sceneSize()
		var x: Int = baseX
		while (x < baseX + size) {
			var y: Int = baseY
			while (y < baseY + size) {
				regions.add(regionOf(x, y))
				y += 64
			}
			x += 64
		}
		return regions
	}

	private fun export(client: ClientApi) {
		val regions: ArrayList<Int> = sceneRegions(client)
		val here: ArrayList<Marker> = ArrayList()
		for (marker in markers) {
			if (regions.contains(marker.regionId)) {
				here.add(marker)
			}
		}
		if (here.isEmpty()) {
			client.chatMessage("No ground tiles around here to export.")
			return
		}
		val clipboard = Console.clipboard
		if (clipboard == null) {
			client.chatMessage("There is no clipboard to export to.")
			return
		}
		clipboard.setContents(StringSelection(GroundTileJson.format(here)), null)
		client.chatMessage("Exported " + here.size + " ground tile" + (if (here.size == 1) "" else "s") + " to the clipboard.")
	}

	private fun import(client: ClientApi) {
		val text: String? = clipboardText()
		val loaded: ArrayList<Marker>? = if (text == null) null else GroundTileJson.parse(text)
		if (loaded == null || loaded.isEmpty()) {
			client.chatMessage("The clipboard holds no ground tiles (expected RuneLite's ground marker JSON).")
			return
		}
		val regions: ArrayList<Int> = ArrayList()
		for (marker in loaded) {
			val present: Marker? = markerAt(marker.worldX, marker.worldY, marker.z)
			if (present != null) {
				markers.remove(present)
			}
			markers.add(marker)
			if (!regions.contains(marker.regionId)) {
				regions.add(marker.regionId)
			}
		}
		for (regionId in regions) {
			saveRegion(regionId)
		}
		client.chatMessage("Imported " + loaded.size + " ground tile" + (if (loaded.size == 1) "" else "s") + ".")
	}

	private fun clear(client: ClientApi) {
		val regions: ArrayList<Int> = sceneRegions(client)
		var cleared: Int = 0
		var i: Int = markers.size - 1
		while (i >= 0) {
			if (regions.contains(markers[i].regionId)) {
				markers.removeAt(i)
				cleared++
			}
			i--
		}
		labelling = null
		for (regionId in regions) {
			saveRegion(regionId)
		}
		client.chatMessage("Cleared " + cleared + " ground tile" + (if (cleared == 1) "" else "s") + " around here.")
	}

	@Suppress("TooGenericExceptionCaught", "SwallowedException") // a clipboard that cannot be read is an empty one
	private fun clipboardText(): String? {
		val clipboard = Console.clipboard ?: return null
		try {
			val contents: Transferable = clipboard.getContents(null) ?: return null
			return contents.getTransferData(STRING_FLAVOR) as String?
		} catch (error: Exception) {
			return null
		}
	}

	// --- drawing ---

	private fun draw(g: Graphics, view: Viewport) {
		drawnLastFrame = 0
		val client: ClientApi = context?.client() ?: return
		if (!enabled || markers.isEmpty() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val level: Int = level(client)
		val baseX: Int = client.sceneBaseX()
		val baseY: Int = client.sceneBaseY()
		val size: Int = client.sceneSize()
		for (marker in markers) {
			val sceneX: Int = marker.worldX - baseX
			val sceneY: Int = marker.worldY - baseY
			if (marker.z != level || sceneX < 0 || sceneY < 0 || sceneX >= size || sceneY >= size) {
				continue
			}
			if (drawTile(g, view, client, marker, sceneX, sceneY, level)) {
				drawnLastFrame++
			}
		}
	}

	private fun drawMinimapDots(g: Graphics) {
		val client: ClientApi = context?.client() ?: return
		if (!enabled || !drawOnMinimap || markers.isEmpty() || client.gameState() !== GameState.LOGGED_IN || !client.minimapArea(area)) {
			return
		}
		drawMinimap(g, client, level(client), client.sceneBaseX(), client.sceneBaseY(), client.sceneSize())
	}

	/** One tile: its fill, its border and its label (or the label being typed); false when it is off the screen. */
	private fun drawTile(g: Graphics, view: Viewport, client: ClientApi, marker: Marker, sceneX: Int, sceneY: Int, level: Int): Boolean {
		if (!TileOutline.draw(g, view, client, sceneX, sceneY, level, 1, marker.colour, (fillOpacity shl 24) or (marker.colour and 0xFFFFFF), borderWidth, xs, ys)) {
			return false
		}
		for (i in 0 until 4) {
			lastCorners[i * 2] = xs[i]
			lastCorners[i * 2 + 1] = ys[i]
		}
		val label: String? = if (labelling === marker) labelText.toString() + "_" else marker.label
		if (label != null && g.fontReady() && client.projectTile(sceneX, sceneY, level, 0, point)) {
			g.textCentered(view.x + point[0], view.y + point[1] + g.fontHeight() / 2, label, LABEL_COLOUR, SHADOW)
		}
		return true
	}

	private fun drawMinimap(g: Graphics, client: ClientApi, level: Int, baseX: Int, baseY: Int, size: Int) {
		g.clip(area[0], area[1], area[2], area[3])
		for (marker in markers) {
			val sceneX: Int = marker.worldX - baseX
			val sceneY: Int = marker.worldY - baseY
			if (marker.z != level || sceneX < 0 || sceneY < 0 || sceneX >= size || sceneY >= size || !client.projectMinimap(sceneX, sceneY, point)) {
				continue
			}
			g.fillRect(point[0] - MINIMAP_DOT / 2, point[1] - MINIMAP_DOT / 2, MINIMAP_DOT, MINIMAP_DOT, marker.colour or SHADOW)
		}
		g.unclip()
	}
}

/**
 * RuneLite's ground marker JSON: an array of `{"regionId":12850,"regionX":20,"regionY":37,"z":0,"color":"#FFFFFF00","label":"Home"}`,
 * the colour `#AARRGGBB` (or `#RRGGBB`, opaque) and the label left out when there is none. The
 * reader takes any flat array of objects with those names and ignores other members.
 */
object GroundTileJson {

	private const val HEX: String = "0123456789ABCDEF"

	@JvmStatic
	fun format(markers: List<GroundTiles.Marker>): String {
		val out: StringBuilder = StringBuilder("[")
		for (i in markers.indices) {
			val marker: GroundTiles.Marker = markers[i]
			if (i > 0) {
				out.append(',')
			}
			out.append("{\"regionId\":").append(marker.regionId)
			out.append(",\"regionX\":").append(marker.regionX)
			out.append(",\"regionY\":").append(marker.regionY)
			out.append(",\"z\":").append(marker.z)
			out.append(",\"color\":\"").append(formatColour(marker.colour)).append('"')
			val label: String? = marker.label
			if (label != null) {
				out.append(",\"label\":\"")
				appendEscaped(out, label)
				out.append('"')
			}
			out.append('}')
		}
		return out.append(']').toString()
	}

	@JvmStatic
	fun formatColour(argb: Int): String {
		val out: StringBuilder = StringBuilder("#")
		for (shift in 28 downTo 0 step 4) {
			out.append(HEX[(argb ushr shift) and 0xF])
		}
		return out.toString()
	}

	/** `#AARRGGBB` or `#RRGGBB` (opaque) to ARGB; null for anything else. */
	@JvmStatic
	fun parseColour(text: String): Int? {
		val hex: String = if (text.startsWith("#")) text.substring(1) else text
		if (hex.length != 6 && hex.length != 8) {
			return null
		}
		var value: Long = 0
		for (c in hex) {
			val digit: Int = HEX.indexOf(c.uppercaseChar())
			if (digit < 0) {
				return null
			}
			value = (value shl 4) or digit.toLong()
		}
		if (hex.length == 6) {
			value = value or 0xFF000000L
		}
		return value.toInt()
	}

	private fun appendEscaped(out: StringBuilder, text: String) {
		for (c in text) {
			when {
				c == '"' -> out.append("\\\"")
				c == '\\' -> out.append("\\\\")
				c < ' ' -> out.append("\\u00").append(HEX[(c.code shr 4) and 0xF]).append(HEX[c.code and 0xF])
				else -> out.append(c)
			}
		}
	}

	/** The markers in [text], or null when it is not such an array; an object missing a field or out of range is skipped. */
	@JvmStatic
	fun parse(text: String): ArrayList<GroundTiles.Marker>? {
		val reader: Reader = Reader(text)
		val objects: ArrayList<HashMap<String, Any?>> = reader.array() ?: return null
		val markers: ArrayList<GroundTiles.Marker> = ArrayList()
		for (fields in objects) {
			val marker: GroundTiles.Marker? = marker(fields)
			if (marker != null) {
				markers.add(marker)
			}
		}
		return markers
	}

	private fun marker(fields: HashMap<String, Any?>): GroundTiles.Marker? {
		val regionId: Int = int(fields["regionId"]) ?: return null
		val regionX: Int = int(fields["regionX"]) ?: return null
		val regionY: Int = int(fields["regionY"]) ?: return null
		val z: Int = int(fields["z"]) ?: return null
		if (regionId < 0 || regionX !in 0..63 || regionY !in 0..63 || z !in 0..3) {
			return null
		}
		val colourText: Any? = fields["color"]
		val colour: Int = (if (colourText is String) parseColour(colourText) else null) ?: GroundTiles.colourNamed("Yellow")
		val label: Any? = fields["label"]
		return GroundTiles.Marker(regionId, regionX, regionY, z, colour, if (label is String && label.isNotEmpty()) label else null)
	}

	private fun int(value: Any?): Int? {
		return if (value is Long && value >= Int.MIN_VALUE && value <= Int.MAX_VALUE) value.toInt() else null
	}

	/** A reader for an array of flat objects: strings, integers, true, false and null; anything deeper fails the whole text. */
	private class Reader(private val text: String) {
		private var at: Int = 0

		private fun skipSpace() {
			while (at < text.length && text[at] <= ' ') {
				at++
			}
		}

		private fun take(c: Char): Boolean {
			skipSpace()
			if (at < text.length && text[at] == c) {
				at++
				return true
			}
			return false
		}

		fun array(): ArrayList<HashMap<String, Any?>>? {
			if (!take('[')) {
				return null
			}
			val objects: ArrayList<HashMap<String, Any?>> = ArrayList()
			if (take(']')) {
				return objects
			}
			do {
				objects.add(obj() ?: return null)
			} while (take(','))
			return if (take(']')) objects else null
		}

		private fun obj(): HashMap<String, Any?>? {
			if (!take('{')) {
				return null
			}
			val fields: HashMap<String, Any?> = HashMap()
			if (take('}')) {
				return fields
			}
			do {
				val key: String = string() ?: return null
				if (!take(':')) {
					return null
				}
				if (!value(fields, key)) {
					return null
				}
			} while (take(','))
			return if (take('}')) fields else null
		}

		/** Reads one value into [fields] under [key]; false when it is not a value the reader knows. */
		private fun value(fields: HashMap<String, Any?>, key: String): Boolean {
			skipSpace()
			if (at >= text.length) {
				return false
			}
			val c: Char = text[at]
			if (c == '"') {
				fields[key] = string() ?: return false
				return true
			}
			if (c == '-' || c in '0'..'9') {
				fields[key] = number() ?: return false
				return true
			}
			for (word in LITERALS) {
				if (text.startsWith(word, at)) {
					at += word.length
					fields[key] = when (word) {
						"true" -> true
						"false" -> false
						else -> null
					}
					return true
				}
			}
			return false
		}

		private fun number(): Long? {
			val start: Int = at
			if (text[at] == '-') {
				at++
			}
			while (at < text.length && text[at] in '0'..'9') {
				at++
			}
			// a fraction or an exponent is not a tile coordinate
			if (at < text.length && (text[at] == '.' || text[at] == 'e' || text[at] == 'E')) {
				return null
			}
			return text.substring(start, at).toLongOrNull()
		}

		private fun string(): String? {
			if (!take('"')) {
				return null
			}
			val out: StringBuilder = StringBuilder()
			while (at < text.length) {
				val c: Char = text[at++]
				when (c) {
					'"' -> return out.toString()
					'\\' -> {
						if (!escape(out)) {
							return null
						}
					}
					else -> out.append(c)
				}
			}
			return null
		}

		private fun escape(out: StringBuilder): Boolean {
			if (at >= text.length) {
				return false
			}
			when (val c: Char = text[at++]) {
				'"', '\\', '/' -> out.append(c)
				'n' -> out.append('\n')
				't' -> out.append('\t')
				'r' -> out.append('\r')
				'b' -> out.append('\b')
				'f' -> out.append('\u000c')
				'u' -> {
					if (at + 4 > text.length) {
						return false
					}
					val code: Int = text.substring(at, at + 4).toIntOrNull(16) ?: return false
					out.append(code.toChar())
					at += 4
				}
				else -> return false
			}
			return true
		}

		companion object {
			private val LITERALS: Array<String> = arrayOf("true", "false", "null")
		}
	}
}
