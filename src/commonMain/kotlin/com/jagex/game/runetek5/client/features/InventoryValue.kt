package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.ItemDefinition
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: what the backpack is worth, at the top left of the game view,
 * with the three most valuable items drawn as their inventory icons. On only with
 * `client.inventoryValue=true` or after `::invvalue`, kept in the plugin's settings.
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md) on API 4: the item icons, the bold font and
 * its line height are what API 3 lacked; the values come from the item types (API 2).
 */
object InventoryValue : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("invvalue", "Inventory value", "1", PluginApi.VERSION, "What the backpack is worth, with its three most valuable items, at the top of the game view (::invvalue turns it on and off).", "Pixel")

	/** The backpack's container id. */
	private const val BACKPACK: Int = 93

	private const val SHOWN: Int = 3

	private const val TITLE_COLOUR: Int = 0xFFFFFF00.toInt()

	private const val TEXT_COLOUR: Int = 0xFFFFFFFF.toInt()

	private const val SHADOW: Int = 0xFF000000.toInt()

	private const val PANEL: Int = 0x80000000.toInt()

	@JvmField
	var enabled: Boolean = false

	/** The frame's three most valuable slots, by value, most first. */
	private val topIds: IntArray = IntArray(SHOWN)

	private val topAmounts: IntArray = IntArray(SHOWN)

	private val topValues: LongArray = LongArray(SHOWN)

	/** The last total drawn, for the smoke test. */
	@JvmField
	var lastTotal: Long = -1L

	private var context: PluginContext? = null

	private val overlay: Overlay = Overlay { g, view -> draw(g, view) }

	/** The plugin manager changed a setting: read it back. */
	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			enabled = context?.config()?.getBoolean("enabled", enabled) ?: enabled
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		val context: PluginContext = this.context ?: return@CommandHandler
		enabled = !enabled
		context.config().setBoolean("enabled", enabled)
		context.config().save()
		context.client().consolePrint(if (enabled) "Inventory value on." else "Inventory value off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val default: Boolean = lang.getBoolean("client.inventoryValue")
		enabled = context.config().getBoolean("enabled", default)
		context.settings().toggle("enabled", "Inventory value", default)
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.addOverlay(overlay)
		context.registerCommand("invvalue", toggle)
	}

	override fun stop() {
		context = null
	}

	/** [n] with a comma every three digits. */
	@JvmStatic
	fun withCommas(n: Long): String {
		val digits: String = n.toString()
		val out: StringBuilder = StringBuilder(digits.length + 6)
		var next: Int = digits.length % 3
		for (i in 0 until digits.length) {
			if (i > 0 && i == next) {
				out.append(',')
				next += 3
			} else if (i == 0 && next == 0) {
				next = 3
			}
			out.append(digits[i])
		}
		return out.toString()
	}

	private fun draw(g: Graphics, view: Viewport) {
		val client: ClientApi = context?.client() ?: return
		if (!enabled || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val backpack: ItemContainer = client.itemContainer(BACKPACK) ?: return
		var total: Long = 0L
		var shown: Int = 0
		for (slot in 0 until backpack.size()) {
			val id: Int = backpack.itemId(slot)
			val item: ItemDefinition = (if (id < 0) null else client.item(id)) ?: continue
			val amount: Int = backpack.amount(slot)
			val value: Long = item.value().toLong() * amount
			total += value
			// keep the three biggest, in order
			var at: Int = shown
			while (at > 0 && topValues[at - 1] < value) {
				at--
			}
			if (at < SHOWN) {
				var j: Int = (if (shown < SHOWN) shown else SHOWN - 1)
				while (j > at) {
					topIds[j] = topIds[j - 1]
					topAmounts[j] = topAmounts[j - 1]
					topValues[j] = topValues[j - 1]
					j--
				}
				topIds[at] = id
				topAmounts[at] = amount
				topValues[at] = value
				if (shown < SHOWN) {
					shown++
				}
			}
		}
		lastTotal = total
		val x: Int = view.x + 8
		val top: Int = view.y + 34
		g.fillRect(x - 4, top, 220, 6 + 34 * (shown + 1), PANEL)
		g.font(Font.BOLD)
		// the first baseline sits one line height below the panel's top
		var y: Int = top + g.fontHeight()
		g.text(x, y, "Backpack: " + withCommas(total) + " coins", TITLE_COLOUR, SHADOW)
		g.font(Font.SMALL)
		y += 8
		for (i in 0 until shown) {
			g.itemIcon(topIds[i], topAmounts[i], x, y)
			val name: String = client.item(topIds[i])?.name() ?: "?"
			g.text(x + 40, y + 20, name + ": " + withCommas(topValues[i]), TEXT_COLOUR, SHADOW)
			y += 34
		}
	}
}
