package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.PanelComponent
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Combat Level. The combat tab's "Combat Lvl" shows your level
 * to three places, with the mouse over it a tooltip says how many levels of Attack or Strength, Defence or
 * Constitution, Ranged, Magic, Prayer and Summoning would take you to the next, and in the Wilderness the
 * levels you can attack and be attacked by are written beside its skull.
 *
 * Void's numbers: its combat level ((the most of attack plus strength, ranged by 1.5 and magic by 1.5, by
 * 1.3) plus defence, constitution, half prayer and on members' worlds half summoning, over 4), its
 * wilderness level (eight squares a level north of the ditch, and in the Wilderness dungeons) and its
 * attack range (your level, give or take the wilderness level plus 5 plus a tenth of it). The Account
 * Summary RuneLite also changes is OSRS's. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object CombatLevel : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("combatlevel", "Combat Level", "1", PluginApi.VERSION, "Shows your combat level to three places, the levels to the next one, and the Wilderness attack range.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = true

	/** The combat tab's "Combat Lvl: N" (884:1). */
	const val COMBAT_TAB: Int = 884
	const val COMBAT_LEVEL_TEXT: Int = 1

	/** The Wilderness skull's empty texts, fixed screen (381:1) and resizable (381:5). */
	const val WILDERNESS: Int = 381
	const val WILDERNESS_FIXED_TEXT: Int = 1
	const val WILDERNESS_RESIZABLE_TEXT: Int = 5

	@JvmField var showLevelsUntil: Boolean = true
	@JvmField var showPreciseCombatLevel: Boolean = true
	@JvmField var wildernessAttackLevelRange: Boolean = true

	private var replaced: Boolean = false
	private var context: PluginContext? = null

	/** RuneLite's CombatLevelOverlay: the levels to the next with the mouse over the combat level. */
	private val overlay: Overlay = Overlay { _, _ -> if (enabled) context?.client()?.let { tooltip(it) } }

	private val tickListener: EventListener<GameTick> = EventListener { _ -> if (enabled) context?.client()?.let { update(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			if (!enabled) {
				context?.client()?.let { off(it) }
			}
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		showPreciseCombatLevel = !showPreciseCombatLevel
		context?.config()?.setBoolean("showPreciseCombatLevel", showPreciseCombatLevel)
		context?.config()?.save()
		context?.client()?.consolePrint(if (showPreciseCombatLevel) "Precise combat level on." else "Precise combat level off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's CombatLevelConfig, in its order
		s.toggle("enabled", "Combat Level", true)
		s.toggle("showLevelsUntil", "Calculate next level", true)
		s.toggle("showPreciseCombatLevel", "Show precise combat level", true)
		s.toggle("wildernessAttackLevelRange", "Show level range in wilderness", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.addOverlay(overlay)
		context.registerCommand("combatlevel", toggle)
	}

	override fun stop() {
		context?.client()?.let { off(it) }
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		showLevelsUntil = config.getBoolean("showLevelsUntil", true)
		showPreciseCombatLevel = config.getBoolean("showPreciseCombatLevel", true)
		wildernessAttackLevelRange = config.getBoolean("wildernessAttackLevelRange", true)
	}

	/** The levels the formula takes, by index: attack, strength, defence, constitution, ranged, magic, prayer, summoning. */
	class Levels(@JvmField val values: IntArray, @JvmField val members: Boolean)

	@JvmStatic
	fun levels(client: ClientApi): Levels = Levels(
		intArrayOf(
			client.skillBaseLevel(Skill.ATTACK), client.skillBaseLevel(Skill.STRENGTH), client.skillBaseLevel(Skill.DEFENCE), client.skillBaseLevel(Skill.CONSTITUTION),
			client.skillBaseLevel(Skill.RANGED), client.skillBaseLevel(Skill.MAGIC), client.skillBaseLevel(Skill.PRAYER), client.skillBaseLevel(Skill.SUMMONING),
		),
		client.isMember(),
	)

	/** Void's CombatLevel.calculateCombatLevel, to the fraction its integer steps leave: its floor is the level the server gives. */
	@JvmStatic
	fun precise(l: IntArray, members: Boolean): Double {
		val highest: Int = maxOf(l[ATT] + l[STR], l[RNG] * 3 / 2, l[MAG] * 3 / 2) * 13
		var defensive: Int = l[DEF] + l[HP] + l[PRAY] / 2
		if (members) {
			defensive += l[SUMM] / 2
		}
		return (highest / 10.0 + defensive) / 4.0
	}

	@JvmStatic
	fun level(l: IntArray, members: Boolean): Int = precise(l, members).toInt()

	/** RuneLite's CombatLevelOverlay: the levels of [skill] (by the formula's index) the next combat level takes, 0 past 99. */
	@JvmStatic
	fun levelsUntilNext(l: IntArray, members: Boolean, skill: Int, partner: Int = -1): Int {
		val current: Int = level(l, members)
		val trial: IntArray = l.copyOf()
		var added: Int = 0
		while (level(trial, members) == current) {
			if (trial[skill] < MAX_LEVEL) {
				trial[skill]++
			} else if (partner >= 0 && trial[partner] < MAX_LEVEL) {
				trial[partner]++
			} else {
				return 0
			}
			added++
		}
		return added
	}

	/** Void's wildernessLevel: north of the ditch, or in the Wilderness dungeons; 0 elsewhere. */
	@JvmStatic
	fun wildernessLevel(x: Int, y: Int): Int {
		val level: Int = when {
			x in 3008..3135 && y in 9920..10367 -> (y - 9920) / 8 + 1
			x in 2944..3392 && y in 3525..3967 -> (y - 3520) / 8 + 1
			else -> 0
		}
		return level.coerceIn(0, 60)
	}

	/** Void's Wilderness.combatRange. */
	@JvmStatic
	fun combatRange(combat: Int, wilderness: Int): IntRange {
		val min: Int = (combat - (wilderness + (5 + combat / 10))).coerceAtLeast(20)
		var max: Int = (combat + (wilderness + (5 + combat / 10))).coerceAtMost(MAX_COMBAT)
		while (max < MAX_COMBAT + 1 && max - (wilderness + (5 + max / 10)) <= combat) {
			max++
		}
		return min..(max - 1)
	}

	/** RuneLite's COMBAT_INTERFACE_SETUP and PVP_WIDGET_BUILDER: the texts, kept each tick over the scripts'. */
	@JvmStatic
	fun update(client: ClientApi) {
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val l: Levels = levels(client)
		val text: Widget? = client.widget(COMBAT_TAB, COMBAT_LEVEL_TEXT)
		if (showPreciseCombatLevel && text != null) {
			val wanted: String = "Combat Lvl: " + format(precise(l.values, l.members))
			if (text.text() != wanted) {
				text.setText(wanted)
			}
			replaced = true
		} else if (replaced) {
			restore(client)
		}
		wildernessRange(client, l)
	}

	private fun wildernessRange(client: ClientApi, l: Levels) {
		val player: Player = client.localPlayer() ?: return
		val wilderness: Int = wildernessLevel(player.tileX() + client.sceneBaseX(), player.tileY() + client.sceneBaseY())
		val text: String = if (!wildernessAttackLevelRange || wilderness == 0) "" else {
			val range: IntRange = combatRange(level(l.values, l.members), wilderness)
			"Level: " + wilderness + "<br>" + range.first + "-" + range.last
		}
		for (component in intArrayOf(WILDERNESS_FIXED_TEXT, WILDERNESS_RESIZABLE_TEXT)) {
			val widget: Widget = client.widget(WILDERNESS, component) ?: continue
			if (widget.text() != text) {
				widget.setText(text)
			}
		}
	}

	/** Switched off or stopped: the game's own number back and the Wilderness texts empty again. */
	private fun off(client: ClientApi) {
		restore(client)
		for (component in intArrayOf(WILDERNESS_FIXED_TEXT, WILDERNESS_RESIZABLE_TEXT)) {
			client.widget(WILDERNESS, component)?.setText("")
		}
	}

	/** The game's whole number back on the combat tab. */
	private fun restore(client: ClientApi) {
		replaced = false
		val l: Levels = levels(client)
		client.widget(COMBAT_TAB, COMBAT_LEVEL_TEXT)?.setText("Combat Lvl: " + level(l.values, l.members))
	}

	/** RuneLite's DecimalFormat("#.###"): up to three places, no trailing zeros. */
	@JvmStatic
	fun format(value: Double): String {
		val thousandths: Long = kotlin.math.round(value * THOUSAND).toLong()
		val fraction: String = (thousandths % THOUSAND).toString().padStart(3, '0').trimEnd('0')
		val whole: String = (thousandths / THOUSAND).toString()
		return if (fraction.isEmpty()) whole else whole + "." + fraction
	}

	/** RuneLite's CombatLevelOverlay: with the mouse over the combat level, the levels each style needs. */
	private fun tooltip(client: ClientApi) {
		if (!showLevelsUntil || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val text: Widget = client.widget(COMBAT_TAB, COMBAT_LEVEL_TEXT) ?: return
		if (text.isHidden() || !text.isDrawnThisFrame()) {
			return
		}
		val x: Int = client.mouseX() - text.screenX()
		val y: Int = client.mouseY() - text.screenY()
		if (x < 0 || y < 0 || x >= text.width() || y >= text.height()) {
			return
		}
		val l: Levels = levels(client)
		val panel: PanelComponent = PanelComponent()
		panel.addTitle("Next combat level:", WHITE)
		line(panel, "Attack/Strength", levelsUntilNext(l.values, l.members, ATT, STR))
		line(panel, "Defence/Constitution", levelsUntilNext(l.values, l.members, DEF, HP))
		line(panel, "Ranged", levelsUntilNext(l.values, l.members, RNG))
		line(panel, "Magic", levelsUntilNext(l.values, l.members, MAG))
		line(panel, "Prayer", levelsUntilNext(l.values, l.members, PRAY))
		if (l.members) {
			line(panel, "Summoning", levelsUntilNext(l.values, l.members, SUMM))
		}
		client.addTooltip(panel)
	}

	private fun line(panel: PanelComponent, name: String, levels: Int) {
		if (levels > 0) {
			panel.addLine(name + ":", WHITE, levels.toString(), GREEN)
		}
	}

	const val ATT: Int = 0
	const val STR: Int = 1
	const val DEF: Int = 2
	const val HP: Int = 3
	const val RNG: Int = 4
	const val MAG: Int = 5
	const val PRAY: Int = 6
	const val SUMM: Int = 7

	private const val MAX_LEVEL: Int = 99
	private const val THOUSAND: Int = 1000
	private const val MAX_COMBAT: Int = 138
	private const val WHITE: Int = -0x1
	private const val GREEN: Int = 0xFF0DC10D.toInt()
}
