package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.InterfaceVisitor
import com.jagex.game.runetek5.plugin.api.ItemContainer
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
import com.jagex.game.runetek5.plugin.api.events.ItemContainerChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's plugin-hub Bank Organizer (by Ideonomy) as a built-in.
 * While the bank is open, every item in an enabled category gets a box over its slot in the
 * category's colour, split into one segment a category when it falls in several. The categories
 * match item names (BankOrganizerMatcher); their lists are made for the 2011 items (BankCategories).
 *
 * 634 differences: no Sailing category (the lists were rebuilt against this cache's names), and no
 * Bank Cleanup (its keep/discard rules are OSRS quests and diaries).
 */
object BankOrganizer : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("bankorganizer", "Bank Organizer", "1", PluginApi.VERSION, "Boxes over bank items by category (melee, magic, range, skilling, potions, slayer loot, tools, food, farming, outfits), each in its own colour (::bankorganizer turns it on and off).", "Pixel")

	/** The bank's interface and its item grid. */
	const val BANK_INTERFACE: Int = 762

	const val BANK_ITEMS: Int = 93

	/** The bank's item container. */
	const val BANK_CONTAINER: Int = 95

	/** RuneLite's fill: the category colour at this alpha, under a solid outline. */
	private const val FILL_ALPHA: Int = 50

	private const val MAX_LIST: Int = 2000

	@JvmField
	var enabled: Boolean = false

	@JvmField
	var showBoxes: Boolean = true

	@JvmField
	val matcher: BankOrganizerMatcher = BankOrganizerMatcher()

	/** By item id: the colours of its box segments, made when the bank changes or a setting does. */
	@JvmField
	val marks: HashMap<Int, IntArray> = HashMap()

	/** The boxes drawn last frame, for the smoke test. */
	@JvmField
	var drawnBoxes: Int = 0

	private var context: PluginContext? = null

	private var bankOpen: Boolean = false

	private val openVisitor: InterfaceVisitor = InterfaceVisitor { interfaceId, _, _ ->
		if (interfaceId == BANK_INTERFACE) {
			bankOpen = true
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		val context: PluginContext? = this.context
		if (event.pluginId == DESCRIPTOR.id && context != null) {
			readSettings(context.config())
			mark(context.client())
		}
	}

	private val containerListener: EventListener<ItemContainerChanged> = EventListener { event ->
		val client: ClientApi? = context?.client()
		if (event.containerId == BANK_CONTAINER && client != null) {
			mark(client)
		}
	}

	private val frameListener: EventListener<FrameDraw> = EventListener { event -> draw(event.graphics) }

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		val context: PluginContext = this.context ?: return@CommandHandler
		enabled = !enabled
		context.config().setBoolean("enabled", enabled)
		context.config().save()
		context.client().consolePrint(if (enabled) "Bank Organizer on." else "Bank Organizer off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		declareSettings(context.settings())
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.ITEM_CONTAINER_CHANGED, containerListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.registerCommand("bankorganizer", toggle)
		// the bank as it is when the plugin starts
		mark(context.client())
	}

	override fun stop() {
		context = null
		marks.clear()
	}

	private fun declareSettings(s: SettingsSchema) {
		s.toggle("enabled", "Bank Organizer", false)
		s.toggle("showBoxes", "Show boxes", true)
		for (id in 1..BankCategories.COUNT) {
			val name: String = BankCategories.NAMES[id]
			s.toggle("cat" + id + "Active", name + " categorisation", BankCategories.DEFAULT_ACTIVE[id])
			s.colour("colorCat" + id, name + " colour", BankCategories.DEFAULT_COLOURS[id], false)
			s.text("excludeCat" + id, name + " excluded items", "", MAX_LIST)
		}
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		showBoxes = config.getBoolean("showBoxes", true)
		for (id in 1..BankCategories.COUNT) {
			matcher.active[id] = config.getBoolean("cat" + id + "Active", BankCategories.DEFAULT_ACTIVE[id])
			matcher.colours[id] = config.getColour("colorCat" + id, BankCategories.DEFAULT_COLOURS[id])
			matcher.exclude(id, config.get("excludeCat" + id, ""))
		}
	}

	/** Works out the box colours of every item in the bank. */
	@JvmStatic
	fun mark(client: ClientApi) {
		marks.clear()
		val bank: ItemContainer = client.itemContainer(BANK_CONTAINER) ?: return
		for (slot in 0 until bank.size()) {
			markItem(client, bank.itemId(slot))
		}
	}

	private fun markItem(client: ClientApi, id: Int) {
		if (id <= 0 || marks.containsKey(id)) {
			return
		}
		val name: String = client.itemName(id) ?: return
		val colours: IntArray = matcher.colours(name)
		if (colours.isNotEmpty()) {
			marks[id] = colours
		}
	}

	private fun draw(g: Graphics) {
		drawnBoxes = 0
		val client: ClientApi = context?.client() ?: return
		if (!enabled || !showBoxes || marks.isEmpty()) {
			return
		}
		bankOpen = false
		client.openInterfaces(openVisitor)
		val grid: Widget = client.widget(BANK_INTERFACE, BANK_ITEMS) ?: return
		if (!bankOpen || grid.isHidden()) {
			return
		}
		// the slots scroll under the grid's edges
		g.clip(grid.screenX(), grid.screenY(), grid.width(), grid.height())
		for (i in 0 until grid.childCount()) {
			drawSlot(g, grid.child(i) ?: continue)
		}
		g.unclip()
	}

	private fun drawSlot(g: Graphics, slot: Widget) {
		val colours: IntArray = marks[slot.itemId()] ?: return
		if (slot.isHidden()) {
			return
		}
		drawBox(g, slot.screenX() + 1, slot.screenY() + 1, maxOf(0, slot.width() - 2), maxOf(0, slot.height() - 2), colours)
		drawnBoxes++
	}

	/** RuneLite's drawMultiColorBox: one outlined, lightly filled segment a colour, side by side. */
	private fun drawBox(g: Graphics, x: Int, y: Int, w: Int, h: Int, colours: IntArray) {
		for (i in colours.indices) {
			val segX: Int = x + segmentStart(w, colours.size, i)
			val segW: Int = segmentWidth(w, colours.size, i)
			val colour: Int = colours[i]
			g.drawRect(segX, y, segW, h, colour or (0xFF shl 24))
			g.fillRect(segX, y, segW, h, (colour and 0xFFFFFF) or (FILL_ALPHA shl 24))
		}
	}

	/** Where segment [i] of [n] starts across a box [w] wide: each is w / n, the last takes what is left. */
	@JvmStatic
	fun segmentStart(w: Int, n: Int, i: Int): Int = (w / n) * i

	@JvmStatic
	fun segmentWidth(w: Int, n: Int, i: Int): Int = if (i == n - 1) w - (w / n) * i else w / n
}
