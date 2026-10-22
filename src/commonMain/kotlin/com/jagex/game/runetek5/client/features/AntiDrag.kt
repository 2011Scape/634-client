package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Anti Drag. An item in the backpack, the bank, the bank's
 * backpack or the deposit box only starts to drag once the mouse has been held on it for the drag delay
 * (the game's is 5 cycles, 100 ms, 10 on the bank's backpack): always, only while Shift is held, or not
 * while Control is held. The items' slots are made again by the game's scripts as they are drawn, so the
 * delay is put on them each client cycle while it applies, and the game's own put back when it stops.
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object AntiDrag : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("antidrag", "Anti Drag", "1", PluginApi.VERSION, "Makes items in the backpack and the bank harder to drag by accident: they only move once held for a while.", "Pixel")

	/** RuneLite's default: a game tick's worth of client cycles. */
	const val DEFAULT_DELAY: Int = 30

	/**
	 * The item grids: the backpack (149:0), the bank (762:93), the bank's backpack (763:0) and the
	 * deposit box (11:17), as interface and component.
	 */
	@JvmField
	val GRIDS: Array<IntArray> = arrayOf(intArrayOf(149, 0), intArrayOf(762, 93), intArrayOf(763, 0), intArrayOf(11, 17))

	@JvmField var enabled: Boolean = false
	@JvmField var dragDelay: Int = DEFAULT_DELAY
	@JvmField var onShiftOnly: Boolean = true
	@JvmField var disableOnCtrl: Boolean = false

	/** Each grid's own drag dead time, seen before the delay went on (-1 for not yet seen). */
	private val original: IntArray = IntArray(GRIDS.size) { -1 }

	/** Whether the delay is on the grids now. */
	@JvmField var applied: Boolean = false

	/** The keys as a test holds them, for the smoke test; null for the real ones. */
	@JvmField var testShift: Boolean? = null

	private var context: PluginContext? = null

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		context?.client()?.consolePrint(if (enabled) "Anti drag enabled." else "Anti drag disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Anti drag", false)
		// RuneLite's AntiDragConfig, in its order
		s.number("dragDelay", "Drag delay", DEFAULT_DELAY, 0, 600, 1)
		s.toggle("onShiftOnly", "On shift only", true)
		s.toggle("disableOnCtrl", "Disable on control pressed", false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.registerCommand("antidrag", toggle)
	}

	override fun stop() {
		context?.client()?.let { restore(it) }
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		dragDelay = config.getInt("dragDelay", DEFAULT_DELAY)
		onShiftOnly = config.getBoolean("onShiftOnly", true)
		disableOnCtrl = config.getBoolean("disableOnCtrl", false)
	}

	/**
	 * RuneLite's key listener and focus check: with On shift only, the delay while Shift is held; else
	 * always, but not while Control is held with Disable on control pressed. Never while the window is
	 * not focused (the keys' release would be missed).
	 */
	@JvmStatic
	fun wanted(shift: Boolean, control: Boolean, focused: Boolean): Boolean {
		if (!enabled || !focused) {
			return false
		}
		if (onShiftOnly) {
			return shift
		}
		return !(disableOnCtrl && control)
	}

	/** Each client cycle: the delay on the grids while it is wanted, the game's put back once it is not. */
	@JvmStatic
	fun tick(client: ClientApi) {
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val shift: Boolean = testShift ?: client.isKeyHeld(KeyCode.SHIFT)
		val focused: Boolean = testShift != null || client.isFocused()
		if (wanted(shift, client.isKeyHeld(KeyCode.CONTROL), focused)) {
			apply(client)
		} else if (applied) {
			restore(client)
		}
	}

	private fun apply(client: ClientApi) {
		for (i in GRIDS.indices) {
			val grid: Widget = client.widget(GRIDS[i][0], GRIDS[i][1]) ?: continue
			setSlots(grid, i, dragDelay)
		}
		applied = true
	}

	/** The game's own drag dead time back on every grid. */
	@JvmStatic
	fun restore(client: ClientApi) {
		for (i in GRIDS.indices) {
			val grid: Widget = client.widget(GRIDS[i][0], GRIDS[i][1]) ?: continue
			if (original[i] >= 0) {
				setSlots(grid, i, original[i])
			}
		}
		applied = false
	}

	/** Every slot of [grid] given [cycles], its own noted first (from a slot the plugin has not set). */
	private fun setSlots(grid: Widget, index: Int, cycles: Int) {
		for (slot in 0 until grid.childCount()) {
			val child: Widget = grid.child(slot) ?: continue
			if (original[index] < 0 && child.dragDeadTime() != cycles) {
				original[index] = child.dragDeadTime()
			}
			child.setDragDeadTime(cycles)
		}
	}
}
