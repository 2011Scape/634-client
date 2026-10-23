package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.Counter
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.ItemDefinition
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.ItemContainerChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Item Charges. The charges left on the items shown (backpack,
 * bank, equipment) are drawn on them, white, or in the low and very low warning colours, and the worn
 * ones can be infoboxes: teleport jewellery, potion doses, imp-in-a-box, fungicide, watering cans,
 * waterskins, ogre bellows, fruit baskets, vegetable sacks and Guthix rest, each read from the item's own
 * name as 634 counts them in it ("Amulet of glory(4)", "Watering can(8)"), and the Binding necklace,
 * whose charges Void tells after each use (kept in the plugin's settings). The Ring of recoil's shattering
 * and the Binding necklace's end can be notified.
 *
 * Not on Void: the dodgy necklace, the ring of forging and the bracelet of clay have no charge messages
 * there; the amulets of chemistry and bounty, the bracelets of slaughter and expeditious, the abyssal
 * bracelet, the Chronicle, blood essence and the explorer's ring's alchemy are OSRS-only. A built-in
 * plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object ItemCharges : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("itemcharges", "Item Charges", "1", PluginApi.VERSION, "Shows the charges left on jewellery, potions, watering cans and other items with charges.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = true

	/** RuneLite's ItemChargeType, the ones 634 has. */
	const val TELEPORT: Int = 0
	const val POTION: Int = 1
	const val IMPBOX: Int = 2
	const val FUNGICIDE: Int = 3
	const val WATERCAN: Int = 4
	const val WATERSKIN: Int = 5
	const val BELLOWS: Int = 6
	const val FRUIT_BASKET: Int = 7
	const val SACK: Int = 8
	const val GUTHIX_REST: Int = 9
	const val BINDING_NECKLACE: Int = 10

	const val BINDING_NECKLACE_ID: Int = 5521
	const val WORN: Int = 94

	/** The teleport jewellery 634 counts in its name (RuneLite's TELEPORT items, but the ring of wealth, which has no charges in 634). */
	@JvmField
	val TELEPORT_NAMES: Array<String> = arrayOf("amulet of glory", "ring of duelling", "games necklace", "skills necklace", "combat bracelet", "ring of slaying", "digsite pendant", "ferocious ring")

	@JvmField val FRUIT_NAMES: Array<String> = arrayOf("apples", "oranges", "strawberries", "bananas", "tomatoes")
	@JvmField val SACK_NAMES: Array<String> = arrayOf("potatoes", "onions", "cabbages")

	/** RuneLite's BINDING_CHARGES_PATTERN, as Void says it. */
	@JvmField
	val BINDING_CHARGES: Regex = Regex("You have (\\d+) charges? left before your Binding necklace disintegrates\\.")

	const val BINDING_DISINTEGRATED: String = "Your Binding necklace has disintegrated."
	const val RECOIL_SHATTERED: String = "Your Ring of Recoil has shattered."

	private val NAME_CHARGES: Regex = Regex("^(.*?)\\s*\\((\\d+)\\)$")
	private val FUNGICIDE_CHARGES: Regex = Regex("^Fungicide spray (\\d+)$")

	@JvmField var veryLowWarningColor: Int = RED
	@JvmField var lowWarningColor: Int = YELLOW
	@JvmField var veryLowWarning: Int = 1
	@JvmField var lowWarning: Int = 2

	/** The types shown, by type: RuneLite's show... settings. */
	@JvmField val shown: BooleanArray = BooleanArray(BINDING_NECKLACE + 1) { it != POTION }

	@JvmField var recoilNotification: Boolean = false
	@JvmField var bindingNotification: Boolean = true
	@JvmField var showInfoboxes: Boolean = false

	/** The Binding necklace's charges, -1 until Void tells them. */
	@JvmField var bindingCharges: Int = -1

	@JvmField var lastNotification: String? = null

	/** For the smoke test: the counts the last frame drew. */
	@JvmField var drawnLastFrame: Int = 0

	/** RuneLite's ItemChargeInfobox: a worn item's charges, coloured by the warnings. */
	class ChargeInfobox(@JvmField val item: Int, name: String, charges: Int) : Counter(name, charges) {
		init {
			itemId = item
			tooltip = name
		}

		override fun textColour(): Int = colour(count)
	}

	/** The infoboxes by equipment slot. */
	@JvmField val infoboxes: HashMap<Int, ChargeInfobox> = HashMap()

	/** Each item's type and charges ([-1, 0] for none), read once from its name. */
	private val known: HashMap<Int, IntArray> = HashMap()

	private var wasWearingBinding: Boolean = false

	private var context: PluginContext? = null

	/** RuneLite's ItemChargeOverlay, over the interfaces. */
	private val frameListener: EventListener<FrameDraw> = EventListener { event -> if (enabled) context?.client()?.let { drawCharges(event.graphics, it) } }

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> if (enabled) context?.client()?.let { chat(it, event) } }

	private val containerListener: EventListener<ItemContainerChanged> = EventListener { event ->
		if (event.containerId == WORN) {
			context?.client()?.let { wornChanged(it) }
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			context?.client()?.let { updateInfoboxes(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		showInfoboxes = !showInfoboxes
		context?.config()?.setBoolean("showInfoboxes", showInfoboxes)
		context?.config()?.save()
		context?.client()?.let { updateInfoboxes(it) }
		context?.client()?.consolePrint(if (showInfoboxes) "Item charge infoboxes on." else "Item charge infoboxes off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's ItemChargeConfig, in its order, but the settings for what is not on Void
		s.toggle("enabled", "Item Charges", true)
		s.colour("veryLowWarningColor", "Very low warning", RED, false)
		s.colour("lowWarningColor", "Low warning", YELLOW, false)
		s.number("veryLowWarning", "Very low warning", 1, 0, 1000, 1)
		s.number("lowWarning", "Low warning", 2, 0, 1000, 1)
		s.toggle("showTeleportCharges", "Teleport charges", true)
		s.toggle("showImpCharges", "Imp-in-a-box charges", true)
		s.toggle("showFungicideCharges", "Fungicide charges", true)
		s.toggle("showWateringCanCharges", "Watering can charges", true)
		s.toggle("showWaterskinCharges", "Waterskin charges", true)
		s.toggle("showBellowCharges", "Bellows charges", true)
		s.toggle("showBasketCharges", "Basket charges", true)
		s.toggle("showSackCharges", "Sack charges", true)
		s.toggle("recoilNotification", "Ring of recoil notification", false)
		s.toggle("showBindingNecklaceCharges", "Binding necklace charges", true)
		s.toggle("bindingNotification", "Binding necklace notification", true)
		s.toggle("showInfoboxes", "Infoboxes", false)
		s.toggle("showPotionDoseCount", "Potion doses", false)
		s.toggle("showGuthixRestDoses", "Guthix rest doses", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.ITEM_CONTAINER_CHANGED, containerListener)
		context.registerCommand("itemcharges", toggle)
	}

	override fun stop() {
		removeInfoboxes()
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		veryLowWarningColor = config.getColour("veryLowWarningColor", RED)
		lowWarningColor = config.getColour("lowWarningColor", YELLOW)
		veryLowWarning = config.getInt("veryLowWarning", 1)
		lowWarning = config.getInt("lowWarning", 2)
		shown[TELEPORT] = config.getBoolean("showTeleportCharges", true)
		shown[IMPBOX] = config.getBoolean("showImpCharges", true)
		shown[FUNGICIDE] = config.getBoolean("showFungicideCharges", true)
		shown[WATERCAN] = config.getBoolean("showWateringCanCharges", true)
		shown[WATERSKIN] = config.getBoolean("showWaterskinCharges", true)
		shown[BELLOWS] = config.getBoolean("showBellowCharges", true)
		shown[FRUIT_BASKET] = config.getBoolean("showBasketCharges", true)
		shown[SACK] = config.getBoolean("showSackCharges", true)
		recoilNotification = config.getBoolean("recoilNotification", false)
		shown[BINDING_NECKLACE] = config.getBoolean("showBindingNecklaceCharges", true)
		bindingNotification = config.getBoolean("bindingNotification", true)
		showInfoboxes = config.getBoolean("showInfoboxes", false)
		shown[POTION] = config.getBoolean("showPotionDoseCount", false)
		shown[GUTHIX_REST] = config.getBoolean("showGuthixRestDoses", true)
		bindingCharges = config.getInt("bindingNecklace", -1)
	}

	/** RuneLite's getColor: very low, low, else white. */
	@JvmStatic
	fun colour(charges: Int): Int = if (charges <= veryLowWarning) veryLowWarningColor else if (charges <= lowWarning) lowWarningColor else WHITE

	/** The type and charges of an item called [name] whose first actions are [actions], or null for one without charges. */
	@JvmStatic
	fun classify(name: String, drinkable: Boolean): IntArray? {
		FUNGICIDE_CHARGES.matchEntire(name)?.let { return intArrayOf(FUNGICIDE, it.groupValues[1].toInt()) }
		val match: MatchResult = NAME_CHARGES.matchEntire(name) ?: return null
		val base: String = match.groupValues[1].lowercase()
		val charges: Int = match.groupValues[2].toInt()
		val type: Int = when {
			TELEPORT_NAMES.any { base.startsWith(it) } -> TELEPORT
			base.startsWith("imp-in-a-box") -> IMPBOX
			base.startsWith("watering can") -> WATERCAN
			base.startsWith("waterskin") -> WATERSKIN
			base.startsWith("ogre bellows") -> BELLOWS
			base in FRUIT_NAMES -> FRUIT_BASKET
			base in SACK_NAMES -> SACK
			base.startsWith("guthix rest") -> GUTHIX_REST
			drinkable && charges <= MAX_DOSES -> POTION
			else -> return null
		}
		return intArrayOf(type, charges)
	}

	/** Item [itemId]'s type and charges, from its name; the Binding necklace's from Void's messages. */
	@JvmStatic
	fun charges(client: ClientApi, itemId: Int): IntArray? {
		if (itemId == BINDING_NECKLACE_ID) {
			return intArrayOf(BINDING_NECKLACE, bindingCharges)
		}
		val cached: IntArray? = known[itemId]
		if (cached != null) {
			return if (cached[0] < 0) null else cached
		}
		val definition: ItemDefinition? = client.item(itemId)
		val name: String = definition?.name() ?: return null
		var drinkable: Boolean = false
		for (i in 0 until ACTIONS) {
			if (definition.inventoryAction(i) == "Drink") {
				drinkable = true
			}
		}
		val found: IntArray? = classify(name, drinkable)
		known[itemId] = found ?: intArrayOf(-1, 0)
		return found
	}

	/** RuneLite's ItemChargeOverlay.renderItemOverlay: the charges at the slot's top left, "?" for unknown. */
	private fun drawCharges(g: Graphics, client: ClientApi) {
		drawnLastFrame = 0
		if (!g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		ItemGrids.forEach(client) { itemId, _, x, y, _, _ ->
			val charge: IntArray? = charges(client, itemId)
			if (charge != null && shown[charge[0]]) {
				val count: Int = charge[1]
				g.text(x - 1, y + TEXT_DROP, if (count < 0) "?" else count.toString(), colour(count), SHADOW)
				drawnLastFrame++
			}
		}
	}

	/** RuneLite's onChatMessage: the Binding necklace's charges, and the shattering ring and spent necklace notified. */
	@JvmStatic
	fun chat(client: ClientApi, event: ChatMessage) {
		if (event.type != ChatType.GAME && event.type != ChatType.GAME_FILTERED) {
			return
		}
		val message: String = ClientFeatures.plainText(event.message ?: return)
		val binding: MatchResult? = BINDING_CHARGES.matchEntire(message)
		when {
			binding != null -> setBindingCharges(client, binding.groupValues[1].toInt())
			message == RECOIL_SHATTERED && recoilNotification -> notify(client, "Your Ring of Recoil has shattered")
			message == BINDING_DISINTEGRATED -> bindingSpent(client)
		}
	}

	private fun setBindingCharges(client: ClientApi, charges: Int) {
		bindingCharges = charges
		context?.config()?.setInt("bindingNecklace", charges)
		context?.config()?.save()
		updateInfoboxes(client)
	}

	/** The Binding necklace's last charge used: Void says nothing, the necklace is just gone. */
	private fun bindingSpent(client: ClientApi) {
		if (bindingNotification) {
			notify(client, BINDING_DISINTEGRATED)
		}
		setBindingCharges(client, BINDING_FULL)
	}

	private fun notify(client: ClientApi, text: String) {
		lastNotification = text
		client.notify(text)
	}

	/** The equipment changed: the infoboxes again, and a necklace on its last charge gone means it disintegrated. */
	@JvmStatic
	fun wornChanged(client: ClientApi) {
		val wearing: Boolean = wearing(client, BINDING_NECKLACE_ID)
		if (wasWearingBinding && !wearing && bindingCharges == 1) {
			bindingSpent(client)
		}
		wasWearingBinding = wearing
		updateInfoboxes(client)
	}

	private fun wearing(client: ClientApi, itemId: Int): Boolean {
		val worn: ItemContainer = client.itemContainer(WORN) ?: return false
		for (slot in 0 until worn.size()) {
			if (worn.itemId(slot) == itemId) {
				return true
			}
		}
		return false
	}

	/** RuneLite's updateInfoboxes: an infobox for each worn item with charges shown, kept to its count. */
	@JvmStatic
	fun updateInfoboxes(client: ClientApi) {
		val worn: ItemContainer? = client.itemContainer(WORN)
		if (!enabled || !showInfoboxes || worn == null) {
			removeInfoboxes()
			return
		}
		for (slot in 0 until worn.size()) {
			val itemId: Int = worn.itemId(slot)
			val charge: IntArray? = if (itemId < 0) null else charges(client, itemId)
			val box: ChargeInfobox? = infoboxes[slot]
			if (charge == null || !shown[charge[0]] || charge[1] <= 0) {
				box?.let { context?.removeInfoBox(it) }
				infoboxes.remove(slot)
			} else if (box != null && box.item == itemId) {
				box.count = charge[1]
			} else {
				box?.let { context?.removeInfoBox(it) }
				val made: ChargeInfobox = ChargeInfobox(itemId, client.itemName(itemId) ?: "", charge[1])
				infoboxes[slot] = made
				context?.addInfoBox(made)
			}
		}
	}

	private fun removeInfoboxes() {
		for (box in infoboxes.values) {
			context?.removeInfoBox(box)
		}
		infoboxes.clear()
	}

	private const val MAX_DOSES: Int = 4
	private const val ACTIONS: Int = 5
	private const val BINDING_FULL: Int = 16
	private const val TEXT_DROP: Int = 15

	private const val WHITE: Int = -0x1
	private const val RED: Int = 0xFFFF0000.toInt()
	private const val YELLOW: Int = 0xFFFFFF00.toInt()
	private const val SHADOW: Int = 0xFF000000.toInt()
}
