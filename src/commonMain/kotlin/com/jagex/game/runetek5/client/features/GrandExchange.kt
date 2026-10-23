package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.GrandExchangeOffer
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.GrandExchangeOfferChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Grand Exchange plugin, as far as 634 goes. Void only says
 * "One or more of your Grand Exchange offers have been updated.", so each offer that trades gets OSRS's
 * line in the chat ("Grand Exchange: Bought 5 / 10 x Rune bar.") and, when asked, on the desktop; a panel
 * shows the offers and how far along they are, for watching them away from the exchange.
 *
 * The slots come from UPDATE_GE_SLOT (GrandExchangeOfferChanged, API 35). The last seen offers are kept
 * per account, so offers that traded while logged out are told once at the next login, when the server
 * sends all six. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object GrandExchange : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("grandexchange", "Grand Exchange", "1", PluginApi.VERSION, "Tells you when your Grand Exchange offers trade, in the chat and on the desktop, and shows them in a panel.", "Pixel")

	private const val SLOTS: Int = GrandExchangeOffer.SLOTS

	private const val TITLE_COLOUR: Int = 0xFFFF981F.toInt()

	private const val TEXT_COLOUR: Int = 0xFFFFFFFF.toInt()

	private const val SHADOW: Int = 0xFF000000.toInt()

	/** RuneLite's progress bar: green while trading and done, red for an aborted offer, on dark grey. */
	private const val BAR_DONE: Int = 0xFF2EB82E.toInt()

	private const val BAR_ABORTED: Int = 0xFFB82E2E.toInt()

	private const val BAR_BACK: Int = 0xFF3C3C3C.toInt()

	private const val WIDTH: Int = 180

	private const val ROW: Int = 36

	@JvmField var enabled: Boolean = true
	@JvmField var chatMessages: Boolean = true
	@JvmField var notifications: Boolean = false
	@JvmField var showPanel: Boolean = false

	/** The offers as last seen, the account's (kept in the settings); the ones the server sent since; which those are. */
	@JvmField
	val offers: Array<GrandExchangeOffer> = Array(SLOTS) { GrandExchangeOffer().also { o -> o.slot = it } }

	private val incoming: Array<GrandExchangeOffer> = Array(SLOTS) { GrandExchangeOffer().also { o -> o.slot = it } }

	private val dirty: BooleanArray = BooleanArray(SLOTS)

	private val live: Array<GrandExchangeOffer> = Array(SLOTS) { GrandExchangeOffer().also { o -> o.slot = it } }

	/** The account [offers] are of, lower case; null before the first. */
	@JvmField
	var profile: String? = null

	/** For the smoke test and the tests: the last line told. */
	@JvmField
	var lastMessage: String? = null

	private var context: PluginContext? = null

	private val size: Size = Size()

	private val offerListener: EventListener<GrandExchangeOfferChanged> = EventListener { event ->
		val slot: Int = event.offer.slot
		if (slot in 0 until SLOTS) {
			incoming[slot].set(event.offer)
			dirty[slot] = true
			context?.let { flush(it) }
		}
	}

	// the slots come at login before the local player may have its name: told on the first tick that has it
	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.let { flush(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		val context: PluginContext = this.context ?: return@CommandHandler
		showPanel = !showPanel
		context.config().setBoolean("showPanel", showPanel)
		context.config().save()
		context.client().consolePrint(if (showPanel) "Grand Exchange panel on." else "Grand Exchange panel off.")
	}

	/** The offers panel, at the top left of the game view unless the player moved it. */
	private val overlay: PanelOverlay = object : PanelOverlay("GrandExchangeOffersOverlay") {
		override fun render(g: Graphics, view: Viewport): Size? = draw(g, this)
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Grand Exchange", true)
		s.toggle("chatMessages", "Offer messages in chat", true)
		s.toggle("notifications", "Desktop notifications", false)
		s.toggle("showPanel", "Offers panel", false)
		readSettings(context.config())
		profile = null
		// turned on while logged in: the slots the server already sent
		val loggedIn: Boolean = context.client().gameState() === GameState.LOGGED_IN
		for (slot in 0 until SLOTS) {
			dirty[slot] = loggedIn && context.client().grandExchangeOffer(slot, incoming[slot])
		}
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GRAND_EXCHANGE_OFFER_CHANGED, offerListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.addOverlay(overlay)
		context.registerCommand("geoffers", toggle)
	}

	override fun stop() {
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		chatMessages = config.getBoolean("chatMessages", true)
		notifications = config.getBoolean("notifications", false)
		showPanel = config.getBoolean("showPanel", false)
	}

	/** Tells the offers the server sent since the last time, once the account is known, and keeps them. */
	private fun flush(context: PluginContext) {
		if (!dirty.any { it }) {
			return
		}
		val client: ClientApi = context.client()
		val name: String = client.localPlayer()?.name()?.lowercase() ?: return
		val config: PluginConfig = context.config()
		if (name != profile) {
			profile = name
			decode(config.get(key(name), ""), offers)
		}
		for (slot in 0 until SLOTS) {
			if (!dirty[slot]) {
				continue
			}
			dirty[slot] = false
			val now: GrandExchangeOffer = incoming[slot]
			val text: String? = message(offers[slot], now, client.itemName(now.itemId) ?: "item")
			offers[slot].set(now)
			if (text != null && enabled) {
				tell(client, text)
			}
		}
		config.set(key(name), encode(offers))
		config.save()
	}

	private fun tell(client: ClientApi, text: String) {
		lastMessage = text
		if (chatMessages) {
			client.chatMessage(text)
		}
		if (notifications && !client.notify(text) && !chatMessages) {
			client.chatMessage(text)
		}
	}

	private fun key(name: String): String = "offers_" + name

	/**
	 * OSRS's line for offer [now] after [before] (the slot as last seen), or null when there is nothing to
	 * tell: an empty slot, an offer just placed, or nothing traded since. A different offer in the slot
	 * counts from nothing, so one that traded while logged out is told at login.
	 */
	@JvmStatic
	fun message(before: GrandExchangeOffer, now: GrandExchangeOffer, itemName: String): String? {
		if (now.state == GrandExchangeOffer.EMPTY || now.state == GrandExchangeOffer.SUBMITTING) {
			return null
		}
		val same: Boolean = before.state != GrandExchangeOffer.EMPTY && before.itemId == now.itemId && before.sell == now.sell &&
			before.quantity == now.quantity && before.price == now.price
		val done: Int = if (same) before.quantityDone else 0
		val wasFinished: Boolean = same && before.state == GrandExchangeOffer.FINISHED
		val quantity: String = InventoryValue.withCommas(now.quantity.toLong())
		val traded: String = InventoryValue.withCommas(now.quantityDone.toLong())
		if (now.state == GrandExchangeOffer.FINISHED) {
			if (wasFinished) {
				return null
			}
			if (now.quantityDone >= now.quantity) {
				return "Grand Exchange: Finished " + (if (now.sell) "selling " else "buying ") + quantity + " x " + itemName + "."
			}
			return "Grand Exchange: Aborted offer to " + (if (now.sell) "sell " else "buy ") + quantity + " x " + itemName + ". " +
				traded + " / " + quantity + (if (now.sell) " sold." else " bought.")
		}
		if (now.quantityDone > done) {
			return "Grand Exchange: " + (if (now.sell) "Sold " else "Bought ") + traded + " / " + quantity + " x " + itemName + "."
		}
		return null
	}

	/** The six offers as kept in the settings: "state,sell,item,price,quantity,done,coins" each, ';' between. */
	@JvmStatic
	fun encode(offers: Array<GrandExchangeOffer>): String {
		val out: StringBuilder = StringBuilder()
		for (o in offers) {
			if (out.isNotEmpty()) {
				out.append(';')
			}
			out.append(o.state).append(',').append(if (o.sell) 1 else 0).append(',').append(o.itemId).append(',').append(o.price)
				.append(',').append(o.quantity).append(',').append(o.quantityDone).append(',').append(o.coins)
		}
		return out.toString()
	}

	/** [text] from [encode] into [offers]; a slot missing or unreadable is empty. */
	@JvmStatic
	fun decode(text: String, offers: Array<GrandExchangeOffer>) {
		val parts: List<String> = if (text.isEmpty()) emptyList() else text.split(';')
		for (slot in offers.indices) {
			val o: GrandExchangeOffer = offers[slot]
			o.slot = slot
			val f: List<Int?> = parts.getOrNull(slot)?.split(',')?.map { it.toIntOrNull() } ?: emptyList()
			if (f.size != 7 || f.any { it == null }) {
				o.state = GrandExchangeOffer.EMPTY
				o.sell = false
				o.itemId = -1
				o.price = 0
				o.quantity = 0
				o.quantityDone = 0
				o.coins = 0
				continue
			}
			o.state = f[0]!!
			o.sell = f[1] == 1
			o.itemId = f[2]!!
			o.price = f[3]!!
			o.quantity = f[4]!!
			o.quantityDone = f[5]!!
			o.coins = f[6]!!
		}
	}

	/** The panel at (0, 0): a row for each offer with its item, what it does and a bar of how far along; null when hidden or empty. */
	private fun draw(g: Graphics, panel: PanelOverlay): Size? {
		val client: ClientApi = context?.client() ?: return null
		if (!enabled || !showPanel || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return null
		}
		// the client's slots, not the remembered ones: Void sends only the slots in use at login
		var shown: Int = 0
		for (o in live) {
			client.grandExchangeOffer(o.slot, o)
			if (o.state != GrandExchangeOffer.EMPTY) {
				shown++
			}
		}
		if (shown == 0) {
			return null
		}
		val height: Int = 6 + 18 + ROW * shown
		g.fillRect(0, 0, WIDTH, height, panel.backgroundColour)
		g.font(Font.BOLD)
		var y: Int = g.fontHeight()
		g.text(4, y, "Grand Exchange", TITLE_COLOUR, SHADOW)
		g.font(Font.SMALL)
		y += 6
		for (o in live) {
			if (o.state == GrandExchangeOffer.EMPTY) {
				continue
			}
			g.itemIcon(o.itemId, 1, 4, y)
			val name: String = client.itemName(o.itemId) ?: "?"
			g.text(40, y + 12, (if (o.sell) "Sell " else "Buy ") + name, TEXT_COLOUR, SHADOW)
			val barWidth: Int = WIDTH - 44
			val fill: Int = if (o.quantity <= 0) 0 else (barWidth.toLong() * o.quantityDone / o.quantity).toInt()
			val aborted: Boolean = o.state == GrandExchangeOffer.FINISHED && o.quantityDone < o.quantity
			g.fillRect(40, y + 18, barWidth, 12, BAR_BACK)
			g.fillRect(40, y + 18, fill, 12, if (aborted) BAR_ABORTED else BAR_DONE)
			g.textCentered(40 + barWidth / 2, y + 28, InventoryValue.withCommas(o.quantityDone.toLong()) + " / " + InventoryValue.withCommas(o.quantity.toLong()), TEXT_COLOUR, SHADOW)
			y += ROW
		}
		return size.set(WIDTH, height)
	}
}
