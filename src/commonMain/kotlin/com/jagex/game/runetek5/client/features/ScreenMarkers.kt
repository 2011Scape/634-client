package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.OverlayOrigin
import com.jagex.game.runetek5.plugin.api.OverlayOriginX
import com.jagex.game.runetek5.plugin.api.OverlayOriginY
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.MouseClicked
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Screen Markers. `::marker` (or `::marker <name>`) and a drag
 * with the left button on the screen draws a box, with the border and fill colours and the border width
 * of the settings; each box is a panel of its own (OverlayLayout), so holding Alt moves it and drags its
 * edges, as with every panel. Shift-right-clicking a box changes its colour, shows or hides its name, or
 * deletes it. The boxes are kept in the plugin's settings (`::marker clear` deletes them all). A built-in
 * plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object ScreenMarkers : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("screenmarkers", "Screen Markers", "1", PluginApi.VERSION, "Draw coloured boxes on the screen (::marker, then drag) to mark places; hold Alt to move or resize them.", "Pixel")

	/** The colours a box's Colour entry steps through: RuneLite's default orange, then red, green, blue, yellow, white. */
	@JvmField
	val PALETTE: IntArray = intArrayOf(0xFFFFA500.toInt(), 0xFFFF0000.toInt(), 0xFF00FF00.toInt(), 0xFF0080FF.toInt(), 0xFFFFFF00.toInt(), 0xFFFFFFFF.toInt())

	/** The smallest box a drag makes. */
	const val MIN_SIZE: Int = 6

	private const val SHADOW: Int = 0xFF000000.toInt()

	/** A box: its colours, border width, whether its name shows, and where it is from the game view's top left. */
	class Marker(@JvmField val id: Int, @JvmField var name: String) {
		@JvmField var border: Int = PALETTE[0]
		@JvmField var fill: Int = 0
		@JvmField var thickness: Int = 3
		@JvmField var labelled: Boolean = false
		@JvmField var x: Int = 0
		@JvmField var y: Int = 0
		@JvmField var width: Int = 0
		@JvmField var height: Int = 0
	}

	@JvmField var enabled: Boolean = true
	@JvmField var defaultBorder: Int = PALETTE[0]
	@JvmField var defaultFill: Int = 0
	@JvmField var defaultThickness: Int = 3

	@JvmField
	val markers: ArrayList<Marker> = ArrayList()

	private val panels: HashMap<Int, MarkerPanel> = HashMap()

	/** Drawing: waiting for the press ([drawing]), then the drag from ([startX], [startY]) to the mouse ([dragging]). */
	@JvmField var drawing: Boolean = false
	@JvmField var dragging: Boolean = false
	@JvmField var startX: Int = 0
	@JvmField var startY: Int = 0
	private var endX: Int = 0
	private var endY: Int = 0
	private var newName: String? = null

	/** The game view's top left as last drawn: a box's place is kept from it. */
	private var viewX: Int = 0
	private var viewY: Int = 0

	private var context: PluginContext? = null

	private val pressListener: EventListener<MouseClicked> = EventListener { event ->
		if (drawing && !dragging && event.button == 0) {
			event.consume()
			dragging = true
			startX = event.x
			startY = event.y
			endX = event.x
			endY = event.y
		}
	}

	private val tickListener: EventListener<ClientTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	/** The box being dragged out, over the game. */
	private val preview: Overlay = Overlay { g, view ->
		viewX = view.x
		viewY = view.y
		if (dragging) {
			val r: IntArray = rect(startX, startY, endX, endY)
			drawBox(g, r[0], r[1], r[2], r[3], defaultBorder, defaultFill, defaultThickness)
		}
	}

	private val command: CommandHandler = CommandHandler { _, args ->
		val context: PluginContext = this.context ?: return@CommandHandler
		if (args.size == 1 && args[0].equals("clear", true)) {
			for (marker in ArrayList(markers)) {
				delete(marker)
			}
			context.client().consolePrint("Screen markers deleted.")
			return@CommandHandler
		}
		drawing = true
		dragging = false
		newName = if (args.isEmpty()) null else args.joinToString(" ")
		context.client().consolePrint("Drag on the screen with the left button to draw the marker.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Screen Markers", true)
		s.colour("border", "Default border colour", PALETTE[0], true)
		s.colour("fill", "Default fill colour", 0, true)
		s.number("thickness", "Default border width", 3, 0, 10, 1)
		readSettings(context.config())
		markers.clear()
		panels.clear()
		decode(context.config().get("markers", ""), markers)
		for (marker in markers) {
			show(marker)
		}
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MOUSE_CLICKED, pressListener)
		context.events().subscribe(Events.CLIENT_TICK, tickListener)
		context.addOverlay(preview)
		context.registerCommand("marker", command)
	}

	override fun stop() {
		context = null
		drawing = false
		dragging = false
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		defaultBorder = config.getColour("border", PALETTE[0])
		defaultFill = config.getColour("fill", 0)
		defaultThickness = config.getInt("thickness", 3).coerceIn(0, 10)
	}

	/** The box between two corners as x, y, width, height, whichever way it was dragged. */
	@JvmStatic
	fun rect(x0: Int, y0: Int, x1: Int, y1: Int): IntArray = intArrayOf(minOf(x0, x1), minOf(y0, y1), kotlin.math.abs(x1 - x0), kotlin.math.abs(y1 - y0))

	private fun tick(client: ClientApi) {
		if (!dragging) {
			// a box moved or resized (holding Alt) is kept once the button is up
			if (!client.isMouseButtonDown(0) && markers.any { moved(it) }) {
				save()
			}
			return
		}
		endX = client.mouseX()
		endY = client.mouseY()
		if (client.isMouseButtonDown(0)) {
			return
		}
		finish(startX, startY, endX, endY)
	}

	/** The drag ended at ([x1], [y1]): a box there, unless it is too small. Returns it, or null. */
	@JvmStatic
	fun finish(x0: Int, y0: Int, x1: Int, y1: Int): Marker? {
		drawing = false
		dragging = false
		val r: IntArray = rect(x0, y0, x1, y1)
		if (r[2] < MIN_SIZE || r[3] < MIN_SIZE) {
			return null
		}
		val id: Int = (markers.maxOfOrNull { it.id } ?: 0) + 1
		val marker: Marker = Marker(id, newName ?: ("Marker " + id))
		marker.border = defaultBorder
		marker.fill = defaultFill
		marker.thickness = defaultThickness
		marker.x = r[0] - viewX
		marker.y = r[1] - viewY
		marker.width = r[2]
		marker.height = r[3]
		markers.add(marker)
		show(marker)
		save()
		return marker
	}

	private fun show(marker: Marker) {
		val context: PluginContext = this.context ?: return
		val panel: MarkerPanel = MarkerPanel(marker)
		panels[marker.id] = panel
		context.addOverlay(panel)
	}

	private fun delete(marker: Marker) {
		markers.remove(marker)
		panels.remove(marker.id)?.let { context?.removeOverlay(it) }
		save()
	}

	private fun moved(marker: Marker): Boolean {
		val panel: MarkerPanel = panels[marker.id] ?: return false
		return (panel.hasPreferredLocation && (panel.preferredX != marker.x || panel.preferredY != marker.y)) ||
			(panel.preferredWidth > 0 && panel.preferredHeight > 0 && (panel.preferredWidth != marker.width || panel.preferredHeight != marker.height))
	}

	private fun save() {
		val config: PluginConfig = context?.config() ?: return
		for (marker in markers) {
			val panel: MarkerPanel = panels[marker.id] ?: continue
			// where the player moved or resized it
			if (panel.hasPreferredLocation) {
				marker.x = panel.preferredX
				marker.y = panel.preferredY
			}
			if (panel.preferredWidth > 0 && panel.preferredHeight > 0) {
				marker.width = panel.preferredWidth
				marker.height = panel.preferredHeight
			}
		}
		config.set("markers", encode(markers))
		config.save()
	}

	/** The boxes as kept: "id|name|border|fill|thickness|labelled|x|y|width|height" each, ';' between. */
	@JvmStatic
	fun encode(markers: List<Marker>): String {
		val out: StringBuilder = StringBuilder()
		for (m in markers) {
			if (out.isNotEmpty()) {
				out.append(';')
			}
			out.append(m.id).append('|').append(m.name.replace('|', ' ').replace(';', ' ')).append('|').append(m.border).append('|').append(m.fill)
				.append('|').append(m.thickness).append('|').append(if (m.labelled) 1 else 0).append('|').append(m.x).append('|').append(m.y)
				.append('|').append(m.width).append('|').append(m.height)
		}
		return out.toString()
	}

	/** [text] from [encode] into [out]; an unreadable box is skipped. */
	@JvmStatic
	fun decode(text: String, out: MutableList<Marker>) {
		for (entry in text.split(';')) {
			val f: List<String> = entry.split('|')
			if (f.size != 10) {
				continue
			}
			val n: List<Int?> = f.mapIndexed { i, s -> if (i == 1) 0 else s.toIntOrNull() }
			if (n.any { it == null }) {
				continue
			}
			val m: Marker = Marker(n[0]!!, f[1])
			m.border = n[2]!!
			m.fill = n[3]!!
			m.thickness = n[4]!!
			m.labelled = n[5] == 1
			m.x = n[6]!!
			m.y = n[7]!!
			m.width = n[8]!!
			m.height = n[9]!!
			out.add(m)
		}
	}

	private fun drawBox(g: Graphics, x: Int, y: Int, width: Int, height: Int, border: Int, fill: Int, thickness: Int) {
		if ((fill ushr 24) != 0) {
			g.fillRect(x, y, width, height, fill)
		}
		for (i in 0 until thickness) {
			if (width - 2 * i <= 0 || height - 2 * i <= 0) {
				break
			}
			g.drawRect(x + i, y + i, width - 2 * i, height - 2 * i, border)
		}
	}

	/** A box as a panel: placed by the client from where it was drawn, moved and resized by the player holding Alt. */
	class MarkerPanel(@JvmField val marker: Marker) : PanelOverlay("ScreenMarker" + marker.id) {
		private val size: Size = Size()

		init {
			resizable = true
			snappable = false
			resettable = false
			minimumSize = MIN_SIZE
			origin = OverlayOrigin.MANUAL
			originX = OverlayOriginX.LEFT
			originY = OverlayOriginY.TOP
			hasPreferredLocation = true
			preferredX = marker.x
			preferredY = marker.y
			preferredWidth = marker.width
			preferredHeight = marker.height
			addMenuEntry("Colour", marker.name) {
				marker.border = PALETTE[(PALETTE.indexOf(marker.border) + 1) % PALETTE.size]
				save()
			}
			addMenuEntry("Label", marker.name) {
				marker.labelled = !marker.labelled
				save()
			}
			addMenuEntry("Delete", marker.name) { delete(marker) }
		}

		override fun render(g: Graphics, view: Viewport): Size? {
			val client: ClientApi = ScreenMarkers.context?.client() ?: return null
			if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
				return null
			}
			val width: Int = if (preferredWidth > 0) preferredWidth else marker.width
			val height: Int = if (preferredHeight > 0) preferredHeight else marker.height
			drawBox(g, 0, 0, width, height, marker.border, marker.fill, marker.thickness)
			if (marker.labelled && g.fontReady()) {
				g.text(marker.thickness + 2, marker.thickness + g.fontHeight(), marker.name, marker.border, SHADOW)
			}
			return size.set(width, height)
		}
	}
}
