package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Inventory Grid. While you drag an item in the backpack, the
 * backpack's 28 slots are shaded as a grid, the slot under the mouse is highlighted, and the item is shown
 * see-through where it would land, with the item it would swap with shown where it came from. The drag comes
 * from the client's own (ClientApi.isDraggingWidget and the rest, API 31). A built-in plugin
 * (BuiltinPlugins, docs/PLUGINS.md).
 */
object InventoryGrid : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("inventorygrid", "Inventory Grid", "1", PluginApi.VERSION, "Shows a grid over the backpack while you drag an item, and where it would go.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = false

	/** The backpack's item grid (149:0). */
	const val BACKPACK_INTERFACE: Int = 149
	const val BACKPACK_GRID: Int = 0
	const val SLOTS: Int = 28

	/** A client cycle, for the drag delay. */
	const val CYCLE_MILLIS: Int = 20

	/** RuneLite's: the preview at half strength. */
	const val PREVIEW_ALPHA: Int = 128

	@JvmField var showItem: Boolean = true
	@JvmField var showGrid: Boolean = true
	@JvmField var showHighlight: Boolean = true
	@JvmField var dragDelay: Int = 0
	@JvmField var gridColor: Int = GRID_DEFAULT
	@JvmField var highlightColor: Int = HIGHLIGHT_DEFAULT

	/** For the smoke test: the slots shaded and the previews drawn in the last frame. */
	@JvmField var slotsDrawn: Int = 0
	@JvmField var previewsDrawn: Int = 0

	private var context: PluginContext? = null

	private val frameListener: EventListener<FrameDraw> = EventListener { event -> if (enabled) context?.client()?.let { draw(event.graphics, it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		showGrid = !showGrid
		context?.config()?.setBoolean("showGrid", showGrid)
		context?.config()?.save()
		context?.client()?.consolePrint(if (showGrid) "Inventory grid on." else "Inventory grid off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's InventoryGridConfig, in its order
		s.toggle("enabled", "Inventory Grid", false)
		s.toggle("showItem", "Show item", true)
		s.toggle("showGrid", "Show grid", true)
		s.toggle("showHighlight", "Highlight background", true)
		s.number("dragDelay", "Drag delay (ms)", 0, 0, MAX_DELAY, CYCLE_MILLIS)
		s.colour("gridColor", "Grid color", GRID_DEFAULT, true)
		s.colour("highlightColor", "Highlight color", HIGHLIGHT_DEFAULT, true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.registerCommand("inventorygrid", toggle)
	}

	override fun stop() {
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		showItem = config.getBoolean("showItem", true)
		showGrid = config.getBoolean("showGrid", true)
		showHighlight = config.getBoolean("showHighlight", true)
		dragDelay = config.getInt("dragDelay", 0)
		gridColor = config.getColour("gridColor", GRID_DEFAULT)
		highlightColor = config.getColour("highlightColor", HIGHLIGHT_DEFAULT)
	}

	/** Whether [widget] is one of the backpack's slots. */
	@JvmStatic
	fun isBackpackSlot(widget: Widget): Boolean =
		widget.slot() >= 0 && (widget.id() == BACKPACK_ID || widget.parentId() == BACKPACK_ID)

	/** RuneLite's drag delay: the press held long enough, in client cycles. */
	@JvmStatic
	fun delayPassed(pressedCycles: Int): Boolean = pressedCycles >= dragDelay / CYCLE_MILLIS

	/** RuneLite's InventoryGridOverlay. */
	private fun draw(g: Graphics, client: ClientApi) {
		slotsDrawn = 0
		previewsDrawn = 0
		if (client.gameState() !== GameState.LOGGED_IN || !client.isDraggingWidget()) {
			return
		}
		val dragged: Widget = client.draggedWidget() ?: return
		if (!isBackpackSlot(dragged) || dragged.itemId() < 0 || !delayPassed(client.itemPressedDuration())) {
			return
		}
		val grid: Widget = client.widget(BACKPACK_INTERFACE, BACKPACK_GRID) ?: return
		if (!grid.isDrawnThisFrame()) {
			return
		}
		val mx: Int = client.mouseX()
		val my: Int = client.mouseY()
		for (i in 0 until minOf(SLOTS, grid.childCount())) {
			val target: Widget = grid.child(i) ?: continue
			val x: Int = target.screenX()
			val y: Int = target.screenY()
			val inBounds: Boolean = mx >= x && my >= y && mx < x + target.width() && my < y + target.height()
			if (showItem && inBounds) {
				// the dragged item where it would land, and what it would swap with where it came from
				g.itemIconAlpha(dragged.itemId(), dragged.itemQuantity(), x, y, PREVIEW_ALPHA)
				if (target.itemId() >= 0 && target.slot() != dragged.slot()) {
					g.itemIconAlpha(target.itemId(), target.itemQuantity(), dragged.screenX(), dragged.screenY(), PREVIEW_ALPHA)
				}
				previewsDrawn++
			}
			if (showHighlight && inBounds) {
				g.fillRect(x, y, target.width(), target.height(), highlightColor)
				slotsDrawn++
			} else if (showGrid) {
				g.fillRect(x, y, target.width(), target.height(), gridColor)
				slotsDrawn++
			}
		}
	}

	private const val BACKPACK_ID: Int = (BACKPACK_INTERFACE shl 16) or BACKPACK_GRID
	private const val MAX_DELAY: Int = 600
	private const val GRID_DEFAULT: Int = 0x2DFFFFFF
	private const val HIGHLIGHT_DEFAULT: Int = 0x2D00FF00
}
