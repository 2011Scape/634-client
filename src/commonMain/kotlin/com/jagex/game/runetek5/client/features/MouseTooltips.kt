package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
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
 * Not part of the original client: RuneLite's Mouse Tooltips (its mousehighlight plugin). What a left
 * click would do (the menu's top entry, "Attack Goblin (level: 2)") follows the mouse as a tooltip, as
 * the game's own text at the top left does; Walk here, Cancel and Continue are left out, and the
 * interfaces', the chat box's and the spellbook's can be. The entry is read as each frame's menu is
 * built (MenuBuilt, which does not come while the menu is open) and shown by the overlay, which draws
 * before the tooltips. 634's own interface tooltips are drawn by its scripts as components, not a
 * client state RuneLite checks, so the plugin's may show beside them. A built-in plugin
 * (BuiltinPlugins, docs/PLUGINS.md).
 */
object MouseTooltips : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("mousehighlight", "Mouse Tooltips", "1", PluginApi.VERSION, "Shows what a left click will do in a tooltip by the mouse.", "Pixel")

	/** The click types of an interface component's options (MiniMenuAction), less the 2000 a sunk entry carries. */
	@JvmField
	val COMPONENT_TYPES: IntArray = intArrayOf(6, 13, 16, 18, 1011)

	/** The plugins' overlay menu and Cancel. */
	const val OVERLAY_MENU: Int = 1014
	const val CANCEL: Int = 1004

	/** The chat box (137), its frame (752) and its buttons (751). */
	@JvmField
	val CHATBOX_INTERFACES: IntArray = intArrayOf(137, 751, 752)

	/** The spellbooks: standard, Ancient, Lunar and Dungeoneering. */
	@JvmField
	val SPELLBOOK_INTERFACES: IntArray = intArrayOf(192, 193, 430, 950)

	@JvmField var enabled: Boolean = true
	@JvmField var uiTooltip: Boolean = true
	@JvmField var chatboxTooltip: Boolean = true
	@JvmField var disableSpellbooktooltip: Boolean = false

	/** The top entry when the menu was last built, and when (client cycle). */
	private var option: String? = null
	private var target: String? = null
	private var clickType: Int = 0
	private var param1: Int = 0
	private var builtCycle: Int = -1

	/** The tooltip given last frame, for the smoke test. */
	@JvmField var shown: String? = null

	private var context: PluginContext? = null

	private val menuListener: EventListener<MenuBuilt> = EventListener { _ -> context?.client()?.let { menuBuilt(it) } }

	/** RuneLite's MouseHighlightOverlay: the tooltip, given while the overlays draw (the tooltips draw after them). */
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
		context?.client()?.consolePrint(if (enabled) "Mouse tooltips enabled." else "Mouse tooltips disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Mouse tooltips", true)
		// RuneLite's MouseHighlightConfig, in its order
		s.toggle("uiTooltip", "Interface tooltips", true)
		s.toggle("chatboxTooltip", "Chatbox tooltips", true)
		s.toggle("disableSpellbooktooltip", "Disable spellbook tooltips", false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_BUILT, menuListener)
		context.addOverlay(overlay)
		context.registerCommand("mousetooltips", toggle)
	}

	override fun stop() {
		context = null
		option = null
		builtCycle = -1
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		uiTooltip = config.getBoolean("uiTooltip", true)
		chatboxTooltip = config.getBoolean("chatboxTooltip", true)
		disableSpellbooktooltip = config.getBoolean("disableSpellbooktooltip", false)
	}

	/** The menu as built this frame: its top entry noted. */
	private fun menuBuilt(client: ClientApi) {
		val top: MenuEntry? = if (client.menuEntryCount() > 0) client.menuEntry(0) else null
		option = top?.option()
		target = top?.target()
		clickType = top?.clickType() ?: 0
		param1 = top?.param1() ?: 0
		builtCycle = client.clientCycle()
	}

	private fun render(client: ClientApi) {
		shown = null
		// a menu built a cycle or more ago is an open menu's (MenuBuilt does not come while it is open)
		if (!enabled || client.gameState() !== GameState.LOGGED_IN || builtCycle < client.clientCycle() - 1) {
			return
		}
		val text: String = tooltip(option, target, clickType, param1) ?: return
		client.addTooltip(text)
		shown = text
	}

	/** RuneLite's render: the entry's tooltip, or null for one it leaves out. */
	@JvmStatic
	fun tooltip(option: String?, target: String?, clickType: Int, param1: Int): String? {
		if (option.isNullOrEmpty()) {
			return null
		}
		val type: Int = clickType % 2000
		if (type == OVERLAY_MENU || type == CANCEL) {
			return null
		}
		val plainOption: String = ClientFeatures.plainText(option)
		val plainTarget: String = ClientFeatures.plainText(target ?: "")
		if (plainOption == "Walk here" || plainOption == "Cancel" || plainOption == "Continue" || plainOption == "Set heading") {
			return null
		}
		if (plainOption == "Move" && plainTarget.contains("Sliding piece")) {
			return null
		}
		if (type in COMPONENT_TYPES) {
			val group: Int = param1 ushr 16
			if (!uiTooltip || (!chatboxTooltip && group in CHATBOX_INTERFACES) || (disableSpellbooktooltip && group in SPELLBOOK_INTERFACES)) {
				return null
			}
		}
		val shownTarget: String = target ?: ""
		return if (shownTarget.isEmpty()) option else option + " " + shownTarget
	}
}
