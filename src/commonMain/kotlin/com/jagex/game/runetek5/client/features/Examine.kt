package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.MenuOptionClicked
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Examine. Examining an item adds a line after its examine text
 * with its price: "Price of 1,000 x Feather: GE average 3,000 (3 ea) HA value 1,200 (1 ea)", the exchange's
 * guide price (ClientApi.itemPrice) and High Level Alchemy's (ClientApi.itemHaPrice), times the stack examined.
 * Void sends an item's examine text as an item examine line (chat type 27); the price line comes on the next
 * client tick, after it is stored. Coins and an item with neither price get no line. A built-in plugin
 * (BuiltinPlugins, docs/PLUGINS.md).
 */
object Examine : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("examine", "Examine", "1", PluginApi.VERSION, "Adds an item's exchange price and alchemy value after its examine text.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = true

	/** Void's ChatType.ItemExamine. */
	const val ITEM_EXAMINE: Int = 27

	const val COINS: Int = 995

	/** RuneLite's examine highlight in the opaque chat box. */
	const val HIGHLIGHT: String = "<col=0000ff>"

	/** How long a click waits for its examine text. */
	const val WAIT_MILLIS: Long = 5_000L

	/** The item examined, its quantity and when; -1 for none. */
	@JvmField var pendingItem: Int = -1
	@JvmField var pendingQuantity: Int = 1
	@JvmField var pendingAt: Long = 0L

	/** The price line to add at the next client tick, null for none. */
	@JvmField var queued: String? = null

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	private var context: PluginContext? = null

	private val clickListener: EventListener<MenuOptionClicked> = EventListener { event -> if (enabled) context?.client()?.let { clicked(it, event) } }

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> if (enabled) context?.client()?.let { chat(it, event) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val tickListener: EventListener<ClientTick> = EventListener { _ -> context?.client()?.let { flush(it) } }

	override fun start(context: PluginContext) {
		this.context = context
		// RuneLite's Examine has no settings: only the switch
		context.settings().toggle("enabled", "Examine", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_OPTION_CLICKED, clickListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.CLIENT_TICK, tickListener)
	}

	override fun stop() {
		context = null
		pendingItem = -1
		queued = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		if (!enabled) {
			pendingItem = -1
			queued = null
		}
	}

	/** RuneLite's onMenuOptionClicked: an item's Examine, with the stack in its slot. */
	@JvmStatic
	fun clicked(client: ClientApi, event: MenuOptionClicked) {
		val option: String = ClientFeatures.plainText(event.option ?: return)
		if (option != "Examine" || event.itemId < 0) {
			return
		}
		pendingItem = event.itemId
		pendingQuantity = maxOf(1, slotQuantity(client, event))
		pendingAt = clock()
	}

	/** The quantity in the clicked component's slot (param1 the component, param0 the slot), 1 for a ground item. */
	private fun slotQuantity(client: ClientApi, event: MenuOptionClicked): Int {
		val component: Widget = client.widgetById(event.param1) ?: return 1
		val slot: Widget = if (event.param0 >= 0) component.child(event.param0) ?: component else component
		return if (slot.itemId() == event.itemId) slot.itemQuantity() else 1
	}

	/** RuneLite's onChatMessage: the examine text of the item clicked queues its price line. */
	@JvmStatic
	fun chat(client: ClientApi, event: ChatMessage) {
		if (event.type != ITEM_EXAMINE || pendingItem < 0) {
			return
		}
		val itemId: Int = pendingItem
		pendingItem = -1
		if (clock() - pendingAt > WAIT_MILLIS) {
			return
		}
		queued = priceLine(client.itemName(itemId) ?: return, pendingQuantity, client.itemPrice(itemId), client.itemHaPrice(itemId), itemId)
	}

	private fun flush(client: ClientApi) {
		val line: String = queued ?: return
		queued = null
		client.addChatLine(ITEM_EXAMINE, null, line, null)
	}

	/** RuneLite's getItemPrice message: null for coins or an item with neither price. */
	@JvmStatic
	fun priceLine(name: String, quantity: Int, gePrice: Int, haPrice: Int, itemId: Int): String? {
		if (itemId == COINS || (gePrice <= 0 && haPrice <= 0)) {
			return null
		}
		val b: StringBuilder = StringBuilder("Price of ")
		if (quantity > 1) {
			b.append(HIGHLIGHT).append(formatNumber(quantity.toLong())).append("</col> x ")
		}
		b.append(HIGHLIGHT).append(name).append("</col>:")
		if (gePrice > 0) {
			b.append(" GE average ").append(HIGHLIGHT).append(formatNumber(gePrice.toLong() * quantity)).append("</col>")
			if (quantity > 1) {
				b.append(" (").append(HIGHLIGHT).append(formatNumber(gePrice.toLong())).append("</col> ea)")
			}
		}
		if (haPrice > 0) {
			b.append(" HA value ").append(HIGHLIGHT).append(formatNumber(haPrice.toLong() * quantity)).append("</col>")
			if (quantity > 1) {
				b.append(" (").append(HIGHLIGHT).append(formatNumber(haPrice.toLong())).append("</col> ea)")
			}
		}
		return b.toString()
	}

	/** RuneLite's QuantityFormatter.formatNumber: thousands parted by commas. */
	@JvmStatic
	fun formatNumber(value: Long): String {
		val digits: String = kotlin.math.abs(value).toString()
		val b: StringBuilder = StringBuilder()
		for (i in digits.indices) {
			if (i > 0 && (digits.length - i) % GROUP == 0) {
				b.append(',')
			}
			b.append(digits[i])
		}
		return (if (value < 0) "-" else "") + b
	}

	private const val GROUP: Int = 3
}
