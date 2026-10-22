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
import com.jagex.game.runetek5.plugin.api.QuestState
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.ItemContainerChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.VarpChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's plugin-hub Bank Organizer (by Ideonomy) as a built-in.
 * While the bank is open, every item in an enabled category gets a box over its slot in the
 * category's colour, split into one segment a category when it falls in several. The categories
 * match item names (BankOrganizerMatcher); their lists are made for the 2011 items (BankCategories).
 *
 * Bank Cleanup adds a last segment to quest items (BankCleanupRules): Keep while a quest that needs the
 * item is not finished, Discard once all of them are, by the quests' state in the game ([ClientApi.questState]).
 *
 * 634 differences: no Sailing category (the lists were rebuilt against this cache's names); Bank Cleanup's
 * rules are remade for 2011 quests and leave out the achievement diaries.
 */
object BankOrganizer : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("bankorganizer", "Bank Organizer", "1", PluginApi.VERSION, "Colours bank items by category (melee, magic, ranged, skilling, potions, food, tools, farming, slayer loot, outfits) and marks quest items to keep or discard.", "Pixel")

	/** The bank's interface and its item grid. */
	const val BANK_INTERFACE: Int = 762

	const val BANK_ITEMS: Int = 93

	/** The bank's item container. */
	const val BANK_CONTAINER: Int = 95

	/** RuneLite's fill: the category colour at this alpha, under a solid outline. */
	private const val FILL_ALPHA: Int = 50

	private const val MAX_LIST: Int = 2000

	/** Bank Cleanup's default colours, RuneLite's: white to keep, black to discard. */
	const val KEEP: Int = 0xFFFFFFFF.toInt()

	const val DISCARD: Int = 0xFF000000.toInt()

	@JvmField
	var enabled: Boolean = false

	@JvmField
	var showBoxes: Boolean = true

	@JvmField
	val matcher: BankOrganizerMatcher = BankOrganizerMatcher()

	/** Bank Cleanup: quest items marked to keep or to discard, in these colours. */
	@JvmField
	var cleanupActive: Boolean = false

	@JvmField
	var keepColour: Int = KEEP

	@JvmField
	var discardColour: Int = DISCARD

	/** A variable changed while Bank Cleanup is on: a quest may have moved on, so the marks are made again. */
	private var questsChanged: Boolean = false

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

	private val varpListener: EventListener<VarpChanged> = EventListener {
		if (cleanupActive) {
			questsChanged = true
		}
	}

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
		context.events().subscribe(Events.VARP_CHANGED, varpListener)
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
		s.toggle("specialItemsActive", "Bank cleanup", false)
		s.colour("specialItemsKeepColor", "Keep colour (still needed by a quest)", KEEP, false)
		s.colour("specialItemsDiscardColor", "Discard colour (no quest needs it)", DISCARD, false)
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
		cleanupActive = config.getBoolean("specialItemsActive", false)
		keepColour = config.getColour("specialItemsKeepColor", KEEP)
		discardColour = config.getColour("specialItemsDiscardColor", DISCARD)
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
		var colours: IntArray = matcher.colours(name)
		val cleanup: Int? = if (cleanupActive) cleanupColour(client, id) else null
		if (cleanup != null) {
			colours += cleanup
		}
		if (colours.isNotEmpty()) {
			marks[id] = colours
		}
	}

	/** Bank Cleanup's colour for item [id]: Keep while a quest that needs it is not finished, Discard once all are; null for no rule. */
	@JvmStatic
	fun cleanupColour(client: ClientApi, id: Int): Int? {
		val quests: IntArray = BankCleanupRules.quests(id) ?: return null
		return if (quests.any { client.questState(it) != QuestState.FINISHED }) keepColour else discardColour
	}

	private fun draw(g: Graphics) {
		drawnBoxes = 0
		val client: ClientApi = context?.client() ?: return
		if (!enabled || !showBoxes) {
			return
		}
		bankOpen = false
		client.openInterfaces(openVisitor)
		val grid: Widget = client.widget(BANK_INTERFACE, BANK_ITEMS) ?: return
		if (!bankOpen || grid.isHidden()) {
			return
		}
		if (questsChanged) {
			questsChanged = false
			mark(client)
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
