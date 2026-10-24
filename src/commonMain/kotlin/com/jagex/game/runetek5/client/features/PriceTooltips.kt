package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.MenuEntry
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.MenuBuilt
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Item Prices. Hovering an item in the backpack, the bank or the
 * bank's backpack shows a tooltip with what the stack is worth: the Grand Exchange guide price and High Level
 * Alchemy's, each for the stack and (on a stack of more than one) for one, and, when asked, what alching it
 * would make after the exchange price of the item and a nature rune. With High Level Alchemy picked, it
 * shows on the backpack's items even when the backpack's tooltips are hidden.
 *
 * The prices are ClientApi.itemPrice (the website's guide prices) and itemHaPrice; a noted item is priced as
 * the item it notes. Coins get no tooltip. 634 has no seed vault, treasure chest or explorer's ring, so the
 * backpack and the bank are the places. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object PriceTooltips : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("itemprices", "Item Prices", "1", PluginApi.VERSION, "Shows the Grand Exchange and High Level Alchemy prices of an item in a tooltip.", "Pixel")

	/** The backpack, the bank's items and the bank's own backpack, and the containers they show. */
	const val BACKPACK: Int = (149 shl 16) or 0
	const val BANK: Int = (762 shl 16) or 93
	const val BANK_BACKPACK_INTERFACE: Int = 763
	const val BACKPACK_CONTAINER: Int = 93
	const val BANK_CONTAINER: Int = 95

	const val COINS: Int = 995
	const val NATURE_RUNE: Int = 561

	/** RuneLite's tooltip colour, (238, 238, 238). */
	const val TEXT_COLOUR: String = "<col=eeeeee>"
	const val PROFIT_COLOUR: String = "<col=00ff00>"
	const val LOSS_COLOUR: String = "<col=ff0000>"

	@JvmField var enabled: Boolean = true
	@JvmField var showGEPrice: Boolean = true
	@JvmField var showHAValue: Boolean = true
	@JvmField var showAlchProfit: Boolean = false
	@JvmField var showWhileAlching: Boolean = true
	@JvmField var showEA: Boolean = true
	@JvmField var hideInventory: Boolean = false

	/** The hovered item, its stack and whether High Level Alchemy is picked, from the menu as last built. */
	private var hoveredItem: Int = -1
	private var hoveredQuantity: Int = 0
	private var hoveredAlching: Boolean = false
	private var builtCycle: Int = -1

	/** An item and stack the smoke test hovers (the mouse cannot), -1 for the menu's. */
	@JvmField var testItem: Int = -1
	@JvmField var testQuantity: Int = 1

	/** The tooltip given last frame, null for none, for the smoke test. */
	@JvmField var shown: String? = null

	private var context: PluginContext? = null

	private val menuListener: EventListener<MenuBuilt> = EventListener { _ -> context?.client()?.let { menuBuilt(it) } }

	/** RuneLite's ItemPricesOverlay: the tooltip, given while the overlays draw (the tooltips draw after them). */
	@JvmField
	val overlay: Overlay = Overlay { _, _ -> context?.client()?.let { render(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		context?.client()?.consolePrint(if (enabled) "Item prices enabled." else "Item prices disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Item prices", true)
		// RuneLite's ItemPricesConfig, in its order
		s.toggle("showGEPrice", "Show Grand Exchange prices", true)
		s.toggle("showHAValue", "Show High Alchemy values", true)
		s.toggle("showAlchProfit", "Show High Alchemy profit", false)
		s.toggle("showWhileAlching", "Show prices while alching", true)
		s.toggle("showEA", "Show price each on stacks", true)
		s.toggle("hideInventory", "Hide tooltips on backpack items", false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_BUILT, menuListener)
		context.addOverlay(overlay)
		context.registerCommand("itemprices", toggle)
	}

	override fun stop() {
		context = null
		hoveredItem = -1
		shown = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		showGEPrice = config.getBoolean("showGEPrice", true)
		showHAValue = config.getBoolean("showHAValue", true)
		showAlchProfit = config.getBoolean("showAlchProfit", false)
		showWhileAlching = config.getBoolean("showWhileAlching", true)
		showEA = config.getBoolean("showEA", true)
		hideInventory = config.getBoolean("hideInventory", false)
	}

	/** The menu as built this frame: the item its top entry is on, when it is in a place the plugin reads. */
	private fun menuBuilt(client: ClientApi) {
		hoveredItem = -1
		builtCycle = client.clientCycle()
		val top: MenuEntry = (if (client.menuEntryCount() > 0) client.menuEntry(0) else null) ?: return
		val alching: Boolean = isAlching(top.option(), top.target())
		val container: Int = containerOf(top.param1(), alching) ?: return
		val items: ItemContainer = client.itemContainer(container) ?: return
		val slot: Int = top.param0()
		if (slot < 0 || slot >= items.size()) {
			return
		}
		val item: Int = items.itemId(slot)
		if (item < 0 || (top.itemId() >= 0 && top.itemId() != item)) {
			return
		}
		hoveredItem = item
		hoveredQuantity = items.amount(slot)
		hoveredAlching = alching
	}

	/** Whether a menu entry is High Level Alchemy cast on an item ("Cast" on "High Level Alchemy -> ..."). */
	@JvmStatic
	fun isAlching(option: String?, target: String?): Boolean {
		return option != null && target != null && OpponentInfo.removeTags(option) == "Cast" && OpponentInfo.removeTags(target).contains("High Level Alchemy")
	}

	/**
	 * The container whose items component [widgetId] shows, when it is one the plugin reads: RuneLite's choice
	 * (the backpack, unless its tooltips are hidden and High Level Alchemy is not picked; the bank; the bank's
	 * backpack); null for any other.
	 */
	@JvmStatic
	fun containerOf(widgetId: Int, alching: Boolean): Int? {
		if (alching && !showWhileAlching) {
			return null
		}
		return when {
			widgetId == BACKPACK -> if (hideInventory && !(showWhileAlching && alching)) null else BACKPACK_CONTAINER
			widgetId == BANK -> BANK_CONTAINER
			widgetId ushr 16 == BANK_BACKPACK_INTERFACE -> BACKPACK_CONTAINER
			else -> null
		}
	}

	private fun render(client: ClientApi) {
		shown = null
		val testing: Boolean = testItem >= 0
		// a menu built a cycle or more ago is an open menu's (MenuBuilt does not come while it is open)
		if (!enabled || client.gameState() !== GameState.LOGGED_IN || (!testing && (hoveredItem < 0 || builtCycle < client.clientCycle() - 1))) {
			return
		}
		val item: Int = if (testing) testItem else hoveredItem
		val quantity: Int = if (testing) testQuantity else hoveredQuantity
		val text: String = tooltip(client, item, quantity) ?: return
		client.addTooltip(TEXT_COLOUR + text)
		shown = text
	}

	/** RuneLite's makeValueTooltip for [quantity] of item [itemId]: null for coins and for an item with no price shown. */
	@JvmStatic
	fun tooltip(client: ClientApi, itemId: Int, quantity: Int): String? {
		if (itemId == COINS) {
			return null
		}
		val ge: Int = if (showGEPrice) maxOf(client.itemPrice(itemId), 0) else 0
		val ha: Int = if (showHAValue) client.itemHaPrice(itemId) else 0
		val nature: Int = maxOf(client.itemPrice(NATURE_RUNE), 0)
		return stackValueText(maxOf(quantity, 1), ge, ha, nature)
	}

	/**
	 * RuneLite's stackValueText: "GE: <stack> gp (<one> ea)", "HA: ..." and "HA Profit: ..." lines, `</br>`
	 * between them; the profit is High Level Alchemy's value less the exchange price and a nature rune's, its
	 * figure green when it gains and red when it loses. Null when neither price is above 0.
	 */
	@JvmStatic
	fun stackValueText(quantity: Int, gePrice: Int, haValue: Int, naturePrice: Int): String? {
		if (gePrice <= 0 && haValue <= 0) {
			return null
		}
		val text: StringBuilder = StringBuilder()
		if (gePrice > 0) {
			text.append("GE: ").append(GroundItemRules.quantityToStackSize(gePrice.toLong() * quantity)).append(" gp")
			each(text, quantity, gePrice.toLong())
		}
		if (haValue > 0) {
			if (gePrice > 0) {
				text.append("</br>")
			}
			text.append("HA: ").append(GroundItemRules.quantityToStackSize(haValue.toLong() * quantity)).append(" gp")
			each(text, quantity, haValue.toLong())
		}
		if (showAlchProfit && haValue > 0 && gePrice > 0) {
			val profit: Long = haValue.toLong() - gePrice - naturePrice
			text.append("</br>HA Profit: ").append(if (profit > 0) PROFIT_COLOUR else LOSS_COLOUR)
				.append(GroundItemRules.quantityToStackSize(profit * quantity)).append(TEXT_COLOUR).append(" gp")
			each(text, quantity, profit)
		}
		return text.toString()
	}

	private fun each(text: StringBuilder, quantity: Int, price: Long) {
		if (showEA && quantity > 1) {
			text.append(" (").append(GroundItemRules.quantityToStackSize(price)).append(" ea)")
		}
	}
}
