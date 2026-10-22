package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.OverlayPanel
import com.jagex.game.runetek5.plugin.api.OverlayPosition
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Attack Styles, for the 634 server (Void). The attack style
 * in use is shown above the chat box (always, or only while it trains a skill the player is warned
 * about); a change to a warned style is told in the chat and, when asked, as a notification; the combat
 * tab's warned styles and its auto retaliate button can be hidden.
 *
 * Void sends the style's index (varp 43) but not the weapon's type: that is the worn weapon's item param
 * 686, and the styles each type has are Void's table (weapon_styles.toml). Autocasting (varp 108) is
 * casting, defensive casting when varbit 2668 is set. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object AttackStyles : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("attackstyles", "Attack Styles", "1", PluginApi.VERSION, "Shows your attack style, warns when it trains a skill you don't want trained, and can hide those styles in the combat tab.", "Pixel")

	/** RuneLite's AttackStyle: its name and the skills it trains. */
	enum class AttackStyle(@JvmField val displayName: String, @JvmField val skills: IntArray) {
		ACCURATE("Accurate", intArrayOf(Skill.ATTACK)),
		AGGRESSIVE("Aggressive", intArrayOf(Skill.STRENGTH)),
		DEFENSIVE("Defensive", intArrayOf(Skill.DEFENCE)),
		CONTROLLED("Controlled", intArrayOf(Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE)),
		RANGING("Ranging", intArrayOf(Skill.RANGED)),
		LONGRANGE("Longrange", intArrayOf(Skill.RANGED, Skill.DEFENCE)),
		CASTING("Casting", intArrayOf(Skill.MAGIC)),
		DEFENSIVE_CASTING("Defensive Casting", intArrayOf(Skill.MAGIC, Skill.DEFENCE)),
		OTHER("Other", IntArray(0)),
	}

	const val STYLE_VARP: Int = 43
	const val AUTOCAST_VARP: Int = 108
	const val DEFENSIVE_CAST_VARBIT: Int = 2668

	/** The cache's item param naming a weapon's type (Void's weapon_style). */
	const val WEAPON_TYPE_PARAM: Int = 686

	const val WORN: Int = 94
	const val WEAPON_SLOT: Int = 3

	/** The combat tab: the style buttons are 11 to 14 by index, auto retaliate 15. */
	const val COMBAT_INTERFACE: Int = 884
	const val FIRST_STYLE: Int = 11
	const val RETALIATE: Int = 15

	@JvmField var enabled: Boolean = false
	@JvmField var alwaysShowStyle: Boolean = true
	@JvmField var warnForDefensive: Boolean = false
	@JvmField var warnForAttack: Boolean = false
	@JvmField var warnForStrength: Boolean = false
	@JvmField var warnForRanged: Boolean = false
	@JvmField var warnForMagic: Boolean = false
	@JvmField var hideAutoRetaliate: Boolean = false
	@JvmField var removeWarnedStyles: Boolean = false
	@JvmField var showChatWarnings: Boolean = true
	@JvmField var warningNotification: Boolean = false

	/** The style in use (null before the game is known) and whether it trains a warned skill. */
	@JvmField var attackStyle: AttackStyle? = null
	@JvmField var warnedSkillSelected: Boolean = false
	private var prevAttackStyle: AttackStyle? = null

	/** The combat tab's components hidden by the plugin, to show again: only those, as the tab's scripts hide others. */
	private val hidden: HashSet<Int> = HashSet()

	private var context: PluginContext? = null

	/** RuneLite's AttackStylesOverlay: the style's name above the chat box, red while it is warned about. */
	@JvmField
	val panel: OverlayPanel = object : OverlayPanel("AttackStylesOverlay") {
		init {
			position = OverlayPosition.ABOVE_CHATBOX_RIGHT
			addMenuEntry("Configure", "Attack style overlay", Task { PluginManagerScreen.openSettings(DESCRIPTOR.id) })
		}

		override fun render(g: Graphics, view: Viewport): Size? {
			val style: AttackStyle = attackStyle ?: return null
			if (!enabled || (!warnedSkillSelected && !alwaysShowStyle) || context?.client()?.gameState() !== GameState.LOGGED_IN) {
				return null
			}
			panelComponent.addTitle(style.displayName, if (warnedSkillSelected) RED else WHITE)
			panelComponent.preferredWidth = g.textWidth(style.displayName) + 10
			return super.render(g, view)
		}
	}

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			context?.client()?.let { client ->
				showHidden(client)
				tick(client)
			}
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		context?.client()?.let { showHidden(it) }
		context?.client()?.consolePrint(if (enabled) "Attack styles enabled." else "Attack styles disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Attack styles", false)
		// RuneLite's AttackStylesConfig, in its order
		s.toggle("alwaysShowStyle", "Always show style", true)
		s.toggle("warnForDefensive", "Warn for defence", false)
		s.toggle("warnForAttack", "Warn for attack", false)
		s.toggle("warnForStrength", "Warn for strength", false)
		s.toggle("warnForRanged", "Warn for ranged", false)
		s.toggle("warnForMagic", "Warn for magic", false)
		s.toggle("hideAutoRetaliate", "Hide auto retaliate", false)
		s.toggle("removeWarnedStyles", "Remove warned styles", false)
		s.toggle("showChatWarnings", "Show chat warnings", true)
		s.toggle("warningNotification", "Warning notification", false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.addOverlay(panel)
		context.registerCommand("attackstyles", toggle)
	}

	override fun stop() {
		context?.client()?.let { showHidden(it) }
		context = null
		reset()
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		alwaysShowStyle = config.getBoolean("alwaysShowStyle", true)
		warnForDefensive = config.getBoolean("warnForDefensive", false)
		warnForAttack = config.getBoolean("warnForAttack", false)
		warnForStrength = config.getBoolean("warnForStrength", false)
		warnForRanged = config.getBoolean("warnForRanged", false)
		warnForMagic = config.getBoolean("warnForMagic", false)
		hideAutoRetaliate = config.getBoolean("hideAutoRetaliate", false)
		removeWarnedStyles = config.getBoolean("removeWarnedStyles", false)
		showChatWarnings = config.getBoolean("showChatWarnings", true)
		warningNotification = config.getBoolean("warningNotification", false)
	}

	@JvmStatic
	fun reset() {
		attackStyle = null
		prevAttackStyle = null
		warnedSkillSelected = false
		hidden.clear()
	}

	/** Whether [skill] is one the player asked to be warned about. */
	@JvmStatic
	fun isWarned(skill: Int): Boolean {
		return when (skill) {
			Skill.DEFENCE -> warnForDefensive
			Skill.ATTACK -> warnForAttack
			Skill.STRENGTH -> warnForStrength
			Skill.RANGED -> warnForRanged
			Skill.MAGIC -> warnForMagic
			else -> false
		}
	}

	@JvmStatic
	fun isWarned(style: AttackStyle): Boolean = style.skills.any { isWarned(it) }

	/** Each client cycle: the style worked out again, its warning, and the combat tab's buttons. */
	@JvmStatic
	fun tick(client: ClientApi) {
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val type: Int = weaponType(client)
		val style: AttackStyle = styleOf(type, client.varp(STYLE_VARP), client.varp(AUTOCAST_VARP), client.varbit(DEFENSIVE_CAST_VARBIT))
		attackStyle = style
		warnedSkillSelected = isWarned(style)
		// RuneLite's onGameTick: a change to a warned style is told (and not the first look after login)
		val previous: AttackStyle? = prevAttackStyle
		if (previous != null && style != previous && warnedSkillSelected) {
			if (showChatWarnings) {
				client.chatMessage("<col=ef1020>Your attack style has been changed to " + style.displayName + "</col>")
			}
			if (warningNotification) {
				client.notify("Attack style changed to " + style.displayName + "!")
			}
		}
		prevAttackStyle = style
		processWidgets(client, type)
	}

	/** The worn weapon's type (Void's weapon_style), 0 for none (unarmed). */
	@JvmStatic
	fun weaponType(client: ClientApi): Int {
		val worn: ItemContainer = client.itemContainer(WORN) ?: return 0
		if (worn.size() <= WEAPON_SLOT) {
			return 0
		}
		val id: Int = worn.itemId(WEAPON_SLOT)
		if (id < 0) {
			return 0
		}
		return client.item(id)?.intParam(WEAPON_TYPE_PARAM, 0) ?: 0
	}

	/** The style of [type]'s style [index], casting while a spell is autocast (defensively with [defensiveCast] set). */
	@JvmStatic
	fun styleOf(type: Int, index: Int, autocast: Int, defensiveCast: Int): AttackStyle {
		if (autocast > 0) {
			return if (defensiveCast != 0) AttackStyle.DEFENSIVE_CASTING else AttackStyle.CASTING
		}
		val styles: Array<AttackStyle> = WeaponStyles.styles(type)
		return if (index in styles.indices) styles[index] else AttackStyle.OTHER
	}

	/** RuneLite's processWidgets: the warned styles' buttons and auto retaliate hidden when asked, the rest left to the tab. */
	private fun processWidgets(client: ClientApi, type: Int) {
		val styles: Array<AttackStyle> = WeaponStyles.styles(type)
		for (i in 0 until 4) {
			val hide: Boolean = removeWarnedStyles && i < styles.size && isWarned(styles[i])
			setHidden(client, FIRST_STYLE + i, hide)
		}
		setHidden(client, RETALIATE, hideAutoRetaliate)
	}

	private fun setHidden(client: ClientApi, component: Int, hide: Boolean) {
		if (hide) {
			client.widget(COMBAT_INTERFACE, component)?.setHidden(true)
			hidden.add(component)
		} else if (hidden.remove(component)) {
			client.widget(COMBAT_INTERFACE, component)?.setHidden(false)
		}
	}

	/** The buttons the plugin hid shown again. */
	@JvmStatic
	fun showHidden(client: ClientApi) {
		for (component in hidden) {
			client.widget(COMBAT_INTERFACE, component)?.setHidden(false)
		}
		hidden.clear()
	}

	private const val WHITE: Int = -0x1
	private const val RED: Int = 0xFFFF0000.toInt()
}

/**
 * Void's weapon_styles.toml: each weapon type's styles by index, as RuneLite's AttackStyle. A ranged
 * weapon's accurate and rapid styles train Ranged (RANGING), long range Ranged and Defence; the
 * chinchompas' fuses the same; the salamander's styles are melee, ranged and magic.
 */
object WeaponStyles {

	private val A: AttackStylesStyle = AttackStyles.AttackStyle.ACCURATE
	private val G: AttackStylesStyle = AttackStyles.AttackStyle.AGGRESSIVE
	private val D: AttackStylesStyle = AttackStyles.AttackStyle.DEFENSIVE
	private val C: AttackStylesStyle = AttackStyles.AttackStyle.CONTROLLED
	private val R: AttackStylesStyle = AttackStyles.AttackStyle.RANGING
	private val L: AttackStylesStyle = AttackStyles.AttackStyle.LONGRANGE
	private val M: AttackStylesStyle = AttackStyles.AttackStyle.CASTING

	/** By type id 0 (unarmed) to 27 (Zamorakian spear). */
	private val TABLE: Array<Array<AttackStylesStyle>> = arrayOf(
		arrayOf(A, G, D), // 0 unarmed
		arrayOf(A, G, D), // 1 staff
		arrayOf(A, G, G, D), // 2 axe
		arrayOf(A, G, D), // 3 sceptre
		arrayOf(A, G, G, D), // 4 pickaxe
		arrayOf(A, G, G, D), // 5 dagger
		arrayOf(A, G, C, D), // 6 sword
		arrayOf(A, G, G, D), // 7 2h
		arrayOf(A, G, C, D), // 8 mace
		arrayOf(A, G, C, D), // 9 claws
		arrayOf(A, G, D), // 10 hammer
		arrayOf(A, C, D), // 11 whip
		arrayOf(A, G, D), // 12 fun
		arrayOf(R, R, L), // 13 pie
		arrayOf(C, C, C, D), // 14 spear
		arrayOf(C, G, D), // 15 halberd
		arrayOf(R, R, L), // 16 bow
		arrayOf(R, R, L), // 17 crossbow
		arrayOf(R, R, L), // 18 thrown
		arrayOf(R, R, L), // 19 chinchompa
		arrayOf(R, G), // 20 fixed device
		arrayOf(G, R, M), // 21 salamander
		arrayOf(A, G, G, D), // 22 scythe
		arrayOf(A, G, D), // 23 ivandis flail
		arrayOf(R, R, L), // 24 sling
		arrayOf(A, G, D), // 25 trident
		arrayOf(A, G, D), // 26 staff of light
		arrayOf(C, C, C, D), // 27 zamorakian spear
	)

	/** The styles of weapon [type]; an unknown type's are none. */
	@JvmStatic
	fun styles(type: Int): Array<AttackStylesStyle> = if (type in TABLE.indices) TABLE[type] else emptyArray()
}

typealias AttackStylesStyle = AttackStyles.AttackStyle
