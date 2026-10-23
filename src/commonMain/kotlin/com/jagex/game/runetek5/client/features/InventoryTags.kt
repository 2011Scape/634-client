package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Image
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Inventory Tags. Shift and right-click an item in the backpack
 * or the worn equipment for an "Inventory tag" submenu: a colour already used, Pick for any colour, or Reset.
 * Every item of that kind is then marked in its colour wherever items are shown (the backpack, the bank, the
 * deposit box, the equipment; ItemGrids): an outline round its shape (ClientApi.itemOutline, API 30), a line
 * under it, or its shape filled (ClientApi.itemFill, API 30). A built-in plugin (BuiltinPlugins,
 * docs/PLUGINS.md).
 */
object InventoryTags : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("inventorytags", "Inventory Tags", "1", PluginApi.VERSION, "Marks the items you tag with a colour, by outline, underline or fill.", "Pixel")

	/** The settings key of item [id]'s tag, RuneLite's. */
	const val TAG_PREFIX: String = "tag_"

	/** The backpack (149) and the worn equipment (387), where the submenu is offered, and their containers. */
	const val BACKPACK_INTERFACE: Int = 149
	const val EQUIPMENT_INTERFACE: Int = 387
	const val BACKPACK: Int = 93
	const val WORN: Int = 94

	/** The most colours offered in the submenu. */
	const val MAX_OFFERED: Int = 5

	@JvmField var showTagOutline: Boolean = true
	@JvmField var tagUnderline: Boolean = false
	@JvmField var tagFill: Boolean = false
	@JvmField var fillOpacity: Int = 50

	/** The tags, item id to colour. */
	@JvmField val tags: HashMap<Int, Int> = HashMap()

	/** For the smoke test: the items marked in the last frame. */
	@JvmField var drawnLastFrame: Int = 0

	private var context: PluginContext? = null
	private var pickAction: Int = -1
	private var resetAction: Int = -1
	private val colourActions: IntArray = IntArray(MAX_OFFERED) { -1 }
	private var offered: IntArray = IntArray(0)

	/** The item the submenu was opened for. */
	private var menuItem: Int = -1

	private val frameListener: EventListener<FrameDraw> = EventListener { event -> context?.client()?.let { draw(event.graphics, it) } }

	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event -> context?.client()?.let { menuEntryAdded(it, event) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		showTagOutline = !showTagOutline
		context?.config()?.setBoolean("showTagOutline", showTagOutline)
		context?.config()?.save()
		context?.client()?.consolePrint(if (showTagOutline) "Tag outlines on." else "Tag outlines off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's InventoryTagsConfig, in its order
		s.toggle("showTagOutline", "Outline", true)
		s.toggle("tagUnderline", "Underline", false)
		s.toggle("tagFill", "Fill", false)
		s.number("fillOpacity", "Fill opacity", 50, 0, 255, 1)
		readSettings(context.config())
		pickAction = context.registerMenuAction(MenuAction { _, _, _, _ -> pick() })
		resetAction = context.registerMenuAction(MenuAction { _, _, _, _ -> setTag(menuItem, null) })
		for (k in colourActions.indices) {
			colourActions[k] = context.registerMenuAction(MenuAction { _, _, _, _ -> offeredColour(k) })
		}
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.registerCommand("inventorytags", toggle)
	}

	override fun stop() {
		context = null
		tags.clear()
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		showTagOutline = config.getBoolean("showTagOutline", true)
		tagUnderline = config.getBoolean("tagUnderline", false)
		tagFill = config.getBoolean("tagFill", false)
		fillOpacity = config.getInt("fillOpacity", 50).coerceIn(0, OPAQUE)
		tags.clear()
		for (key in config.keys()) {
			val id: Int? = if (key.startsWith(TAG_PREFIX)) key.substring(TAG_PREFIX.length).toIntOrNull() else null
			if (id != null) {
				tags[id] = config.getColour(key, 0)
			}
		}
	}

	/** Tags item [itemId] with [argb], or takes its tag off for null; saved. */
	@JvmStatic
	fun setTag(itemId: Int, argb: Int?) {
		if (itemId < 0) {
			return
		}
		if (argb == null) {
			tags.remove(itemId)
		} else {
			tags[itemId] = argb
		}
		val config: PluginConfig = context?.config() ?: return
		if (argb == null) {
			config.remove(TAG_PREFIX + itemId)
		} else {
			config.setColour(TAG_PREFIX + itemId, argb)
		}
		config.save()
	}

	/** The colours offered for an item: those already used (RuneLite's), then a few of its own, not the item's own. */
	@JvmStatic
	fun offeredColours(own: Int?): IntArray {
		val colours: ArrayList<Int> = ArrayList()
		for (c in tags.values) {
			if (c != own && c !in colours && colours.size < MAX_OFFERED) {
				colours.add(c)
			}
		}
		for (c in DEFAULT_COLOURS) {
			if (c != own && c !in colours && colours.size < MAX_OFFERED) {
				colours.add(c)
			}
		}
		return colours.toIntArray()
	}

	/** RuneLite's onMenuOpened: with shift held, the "Inventory tag" submenu on a backpack or equipment item. */
	@JvmStatic
	fun menuEntryAdded(client: ClientApi, event: MenuEntryAdded) {
		if (event.target !== MenuTarget.WIDGET || !client.isKeyHeld(KeyCode.SHIFT) || pickAction < 0) {
			return
		}
		val group: Int = event.widgetId ushr 16
		if (group != BACKPACK_INTERFACE && group != EQUIPMENT_INTERFACE) {
			return
		}
		val itemId: Int = itemAt(client, group, event) ?: return
		val submenu: Int = client.addSubmenu("Inventory tag")
		if (submenu < 0) {
			return
		}
		menuItem = itemId
		val own: Int? = tags[itemId]
		offered = offeredColours(own)
		for (k in offered.indices) {
			client.addSubmenuEntry(submenu, GroundItems.colourTag(offered[k]) + "Color", colourActions[k])
		}
		client.addSubmenuEntry(submenu, "Pick", pickAction)
		if (own != null) {
			client.addSubmenuEntry(submenu, "Reset", resetAction)
		}
	}

	/** The item the entry is for: the event's, else the container's at its slot. */
	private fun itemAt(client: ClientApi, group: Int, event: MenuEntryAdded): Int? {
		if (event.itemId >= 0) {
			return event.itemId
		}
		val container: ItemContainer = client.itemContainer(if (group == BACKPACK_INTERFACE) BACKPACK else WORN) ?: return null
		if (event.slot < 0 || event.slot >= container.size()) {
			return null
		}
		return container.itemId(event.slot).takeIf { it >= 0 }
	}

	private fun pick() {
		val ctx: PluginContext = context ?: return
		val itemId: Int = menuItem
		ctx.pickColour(tags[itemId] ?: DEFAULT_COLOURS[0], false) { argb -> setTag(itemId, argb) }
	}

	private fun offeredColour(k: Int) {
		if (k < offered.size) {
			setTag(menuItem, offered[k])
		}
	}

	/** RuneLite's InventoryTagsOverlay: each tagged item outlined, underlined or filled in its colour. */
	private fun draw(g: Graphics, client: ClientApi) {
		drawnLastFrame = 0
		if (tags.isEmpty() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		ItemGrids.forEach(client) { itemId, quantity, x, y, width, height ->
			val colour: Int? = tags[itemId]
			if (colour != null && mark(g, client, itemId, quantity, x, y, width, height, colour or OPAQUE_ALPHA)) {
				drawnLastFrame++
			}
		}
	}

	private fun mark(g: Graphics, client: ClientApi, itemId: Int, quantity: Int, x: Int, y: Int, width: Int, height: Int, colour: Int): Boolean {
		var drawn: Boolean = false
		if (tagFill) {
			val fill: Image? = client.itemFill(itemId, quantity, (fillOpacity shl ALPHA_SHIFT) or (colour and RGB))
			if (fill != null && g.image(fill, x, y)) {
				drawn = true
			}
		}
		if (showTagOutline) {
			val outline: Image? = client.itemOutline(itemId, quantity, colour)
			if (outline != null && g.image(outline, x, y)) {
				drawn = true
			}
		}
		if (tagUnderline) {
			g.line(x, y + height + UNDERLINE_GAP, x + width, y + height + UNDERLINE_GAP, colour)
			drawn = true
		}
		return drawn
	}

	private const val OPAQUE: Int = 255
	private const val ALPHA_SHIFT: Int = 24
	private const val RGB: Int = 0xFFFFFF
	private const val OPAQUE_ALPHA: Int = -0x1000000
	private const val UNDERLINE_GAP: Int = 2

	@JvmField
	val DEFAULT_COLOURS: IntArray = intArrayOf(0xFFFF0000.toInt(), 0xFF00FF00.toInt(), 0xFF0000FF.toInt(), 0xFFFFFF00.toInt(), 0xFFFF00FF.toInt())
}
