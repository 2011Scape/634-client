package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.ItemDefinition
import com.jagex.game.runetek5.plugin.api.MenuEntry
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.MenuBuilt
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Item Stats. Hovering a food or a potion shows what it would
 * do to each stat now (the change, what it could do, and the level after), and how long a potion lasts;
 * hovering a piece of equipment shows its bonuses against what is worn in its slot (a two-handed weapon
 * against the weapon and shield both), and any item its weight. In the backpack, the worn equipment and
 * (when asked) the bank.
 *
 * The numbers are the Void server's (ItemStatData, made from its item and potion definitions): food
 * restores lifepoints (a range, its least and most), potions boost, restore or drain as its Levels do (a
 * boost of a flat amount and a share of the level, at most 26 over; Constitution in lifepoints). The
 * bonuses are the cache's item params; the slots, two-handedness and weights are Void's, which the
 * cache does not carry. RuneLite's Grand Exchange item panel builds interface components, which the
 * plugin API cannot, so it is not here. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object ItemStats : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("itemstat", "Item Stats", "1", PluginApi.VERSION, "Shows what food and potions would restore or boost, and how equipment compares with what you wear, in a tooltip.", "Pixel")

	/** The interfaces whose items are read: the backpack, the worn equipment tab, the bank and the bank's backpack. */
	const val BACKPACK: Int = (149 shl 16) or 0
	const val WORN_INTERFACE: Int = 387
	const val BANK: Int = (762 shl 16) or 93
	const val BANK_BACKPACK_INTERFACE: Int = 763

	const val WORN: Int = 94
	const val WEAPON_SLOT: Int = 3
	const val SHIELD_SLOT: Int = 5

	/** The stat a run energy change is shown on (not a skill). */
	const val RUN_ENERGY: Int = 100

	/** The cache's item params for the bonuses. */
	@JvmField val ATTACK_PARAMS: IntArray = intArrayOf(0, 1, 2, 3, 4)
	@JvmField val DEFENCE_PARAMS: IntArray = intArrayOf(5, 6, 7, 8, 9)
	const val PRAYER_PARAM: Int = 11
	const val SPEED_PARAM: Int = 14
	const val STRENGTH_PARAM: Int = 641
	const val RANGED_STRENGTH_PARAM: Int = 643
	const val MAGIC_DAMAGE_PARAM: Int = 685

	/** An unarmed player's attack speed, RuneLite's UNARMED. */
	const val UNARMED_SPEED: Int = 4

	@JvmField val BONUS_NAMES: Array<String> = arrayOf("Stab", "Slash", "Crush", "Magic", "Range")

	@JvmField var enabled: Boolean = true
	@JvmField var consumableStats: Boolean = true
	@JvmField var equipmentStats: Boolean = true
	@JvmField var relative: Boolean = true
	@JvmField var absolute: Boolean = true
	@JvmField var theoretical: Boolean = false
	@JvmField var showWeight: Boolean = true
	@JvmField var showStatsInBank: Boolean = true
	@JvmField var alwaysShowBaseStats: Boolean = false
	@JvmField var colorBetterUncapped: Int = 0xFF33EE33.toInt()
	@JvmField var colorBetterSomecapped: Int = 0xFF9CEE33.toInt()
	@JvmField var colorBetterCapped: Int = 0xFFEEEE33.toInt()
	@JvmField var colorNoChange: Int = 0xFFEEEEEE.toInt()
	@JvmField var colorWorse: Int = 0xFFEE3333.toInt()

	/** RuneLite's Positivity. */
	const val WORSE: Int = 0
	const val NO_CHANGE: Int = 1
	const val BETTER_CAPPED: Int = 2
	const val BETTER_SOMECAPPED: Int = 3
	const val BETTER_UNCAPPED: Int = 4

	/** The hovered item and when its menu was built (client cycle). */
	private var hoveredItem: Int = -1
	private var builtCycle: Int = -1

	/** An item the smoke test hovers (the mouse cannot), -1 for the menu's. */
	@JvmField var testItem: Int = -1

	/** The tooltips given last frame, for the smoke test. */
	@JvmField val shown: ArrayList<String> = ArrayList()

	private var context: PluginContext? = null

	private val menuListener: EventListener<MenuBuilt> = EventListener { _ -> context?.client()?.let { menuBuilt(it) } }

	/** RuneLite's ItemStatOverlay: the tooltips, given while the overlays draw (the tooltips draw after them). */
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
		context?.client()?.consolePrint(if (enabled) "Item stats enabled." else "Item stats disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Item stats", true)
		// RuneLite's ItemStatConfig, in its order (less the Grand Exchange panel)
		s.toggle("consumableStats", "Enable consumable stats", true)
		s.toggle("equipmentStats", "Enable equipment stats", true)
		s.toggle("relative", "Show relative", true)
		s.toggle("absolute", "Show absolute", true)
		s.toggle("theoretical", "Show theoretical", false)
		s.toggle("showWeight", "Show weight", true)
		s.toggle("showStatsInBank", "Show stats in bank", true)
		s.toggle("alwaysShowBaseStats", "Always show base stats", false)
		s.colour("colorBetterUncapped", "Better (uncapped)", 0xFF33EE33.toInt(), false)
		s.colour("colorBetterSomecapped", "Better (some capped)", 0xFF9CEE33.toInt(), false)
		s.colour("colorBetterCapped", "Better (capped)", 0xFFEEEE33.toInt(), false)
		s.colour("colorNoChange", "No change", 0xFFEEEEEE.toInt(), false)
		s.colour("colorWorse", "Worse", 0xFFEE3333.toInt(), false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_BUILT, menuListener)
		context.addOverlay(overlay)
		context.registerCommand("itemstats", toggle)
	}

	override fun stop() {
		context = null
		hoveredItem = -1
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		consumableStats = config.getBoolean("consumableStats", true)
		equipmentStats = config.getBoolean("equipmentStats", true)
		relative = config.getBoolean("relative", true)
		absolute = config.getBoolean("absolute", true)
		theoretical = config.getBoolean("theoretical", false)
		showWeight = config.getBoolean("showWeight", true)
		showStatsInBank = config.getBoolean("showStatsInBank", true)
		alwaysShowBaseStats = config.getBoolean("alwaysShowBaseStats", false)
		colorBetterUncapped = config.getColour("colorBetterUncapped", 0xFF33EE33.toInt())
		colorBetterSomecapped = config.getColour("colorBetterSomecapped", 0xFF9CEE33.toInt())
		colorBetterCapped = config.getColour("colorBetterCapped", 0xFFEEEE33.toInt())
		colorNoChange = config.getColour("colorNoChange", 0xFFEEEEEE.toInt())
		colorWorse = config.getColour("colorWorse", 0xFFEE3333.toInt())
	}

	/** The menu as built this frame: the item its top entry is on, when it is in a place the plugin reads. */
	private fun menuBuilt(client: ClientApi) {
		val top: MenuEntry? = if (client.menuEntryCount() > 0) client.menuEntry(0) else null
		hoveredItem = if (top == null) -1 else hoveredItem(top.param1(), top.itemId())
		builtCycle = client.clientCycle()
	}

	/** RuneLite's choice of widgets: the item of an entry on component [widgetId], or -1. */
	@JvmStatic
	fun hoveredItem(widgetId: Int, itemId: Int): Int {
		if (itemId < 0) {
			return -1
		}
		val group: Int = widgetId ushr 16
		if (widgetId == BACKPACK || group == WORN_INTERFACE) {
			return itemId
		}
		if (showStatsInBank && (widgetId == BANK || group == BANK_BACKPACK_INTERFACE)) {
			return itemId
		}
		return -1
	}

	private fun render(client: ClientApi) {
		shown.clear()
		val item: Int = if (testItem >= 0) testItem else hoveredItem
		// a menu built a cycle or more ago is an open menu's (MenuBuilt does not come while it is open)
		if (!enabled || client.gameState() !== GameState.LOGGED_IN || item < 0 || (testItem < 0 && builtCycle < client.clientCycle() - 1) || (!relative && !absolute && !theoretical)) {
			return
		}
		for (tooltip in tooltips(client, item)) {
			client.addTooltip(tooltip)
			shown.add(tooltip)
		}
	}

	/** RuneLite's render for item [itemId]: the consumable's lines, its duration and the equipment's lines, as tooltips. */
	@JvmStatic
	fun tooltips(client: ClientApi, itemId: Int): List<String> {
		val out: ArrayList<String> = ArrayList()
		if (consumableStats) {
			val effects: Array<IntArray>? = ItemStatTables.consumable(itemId)
			if (effects != null) {
				val text: String = changes(client, effects).joinToString("") { statChangeString(it) }
				if (text.isNotEmpty()) {
					out.add(text)
				}
			}
			val duration: IntArray? = ItemStatTables.duration(itemId)
			if (duration != null) {
				out.add("Duration: " + minutesSeconds(duration[0]))
			}
		}
		if (equipmentStats) {
			val text: String = statBonusString(client, itemId)
			if (text.isNotEmpty()) {
				out.add(text)
			}
		}
		return out
	}

	/** A stat's change: [low] and [high] (one value, or a range's ends), what it could have been, and the level after. */
	class StatChange(@JvmField val stat: Int) {
		@JvmField var relativeLow: Int = 0
		@JvmField var relativeHigh: Int = 0
		@JvmField var theoretical: Int = 0
		@JvmField var theoreticalHigh: Int = 0
		@JvmField var absoluteLow: Int = 0
		@JvmField var absoluteHigh: Int = 0
		@JvmField var positivity: Int = NO_CHANGE
	}

	/**
	 * The stats' changes from [effects], each `kind, stat, a, b, c` (ItemStatData): B a boost of a + b/10000 of
	 * the level (at most c over, or 26), R a restore of a + b/10000 of the most, D a drain of a + b/10000 of the
	 * most, H a restore of lifepoints from a to b, E run energy +a, Z a Zamorak brew's hit of 20 and a tenth of
	 * the lifepoints. Effects on one stat follow one another, as the server does them.
	 */
	@JvmStatic
	fun changes(client: ClientApi, effects: Array<IntArray>): List<StatChange> {
		val out: ArrayList<StatChange> = ArrayList()
		val start: HashMap<Int, Int> = HashMap()
		val low: HashMap<Int, Int> = HashMap()
		val high: HashMap<Int, Int> = HashMap()
		val capped: HashMap<Int, Boolean> = HashMap()
		for (effect in effects) {
			val stat: Int = effect[1]
			var change: StatChange? = out.firstOrNull { it.stat == stat }
			if (change == null) {
				change = StatChange(stat)
				out.add(change)
				val now: Int = current(client, stat)
				start[stat] = now
				low[stat] = now
				high[stat] = now
			}
			val max: Int = maximum(client, stat)
			val lowBefore: Int = low[stat]!!
			val highBefore: Int = high[stat]!!
			val loAmount: Int = if (effect[0] == KIND_HEAL) effect[2] else effect[2]
			val hiAmount: Int = if (effect[0] == KIND_HEAL) effect[3] else effect[2]
			low[stat] = apply(effect, stat, lowBefore, max, loAmount)
			high[stat] = apply(effect, stat, highBefore, max, hiAmount)
			change.theoretical += theoreticalOf(effect, lowBefore, max, loAmount)
			change.theoreticalHigh += theoreticalOf(effect, highBefore, max, hiAmount)
			if (wouldCap(effect, stat, highBefore, max, hiAmount)) {
				capped[stat] = true
			}
		}
		for (change in out) {
			val from: Int = start[change.stat]!!
			change.absoluteLow = low[change.stat]!!
			change.absoluteHigh = high[change.stat]!!
			change.relativeLow = change.absoluteLow - from
			change.relativeHigh = change.absoluteHigh - from
			change.positivity = when {
				change.relativeHigh > 0 -> if (capped[change.stat] == true) BETTER_CAPPED else BETTER_UNCAPPED
				change.relativeHigh == 0 && change.relativeLow == 0 -> NO_CHANGE
				else -> WORSE
			}
		}
		return out
	}

	const val KIND_BOOST: Int = 'B'.code
	const val KIND_RESTORE: Int = 'R'.code
	const val KIND_DRAIN: Int = 'D'.code
	const val KIND_HEAL: Int = 'H'.code
	const val KIND_ENERGY: Int = 'E'.code
	const val KIND_ZAMORAK: Int = 'Z'.code

	/** Void's MAXIMUM_BOOST_LEVEL, and its Constitution boost's (in lifepoints). */
	private const val MAX_BOOST: Int = 26
	private const val MAX_CONSTITUTION_BOOST: Int = 260

	private fun boostAmount(effect: IntArray, cur: Int, max: Int, amount: Int): Int = amount + minOf(cur, max) * effect[3] / 10000

	private fun theoreticalOf(effect: IntArray, cur: Int, max: Int, amount: Int): Int {
		return when (effect[0]) {
			KIND_BOOST -> boostAmount(effect, cur, max, amount)
			KIND_RESTORE -> amount + max * effect[3] / 10000
			KIND_DRAIN -> -(amount + maxOf(cur, max) * effect[3] / 10000)
			KIND_HEAL, KIND_ENERGY -> amount
			KIND_ZAMORAK -> -zamorakDamage(cur)
			else -> 0
		}
	}

	/** Void's Levels for one effect: the stat's value after it. */
	@JvmStatic
	fun apply(effect: IntArray, stat: Int, cur: Int, max: Int, amount: Int): Int {
		return when (effect[0]) {
			KIND_BOOST -> {
				val boost: Int = boostAmount(effect, cur, max, amount)
				val cap: Int = if (effect[4] > 0) effect[4] else if (stat == Skill.CONSTITUTION) MAX_CONSTITUTION_BOOST else MAX_BOOST
				val top: Int = minOf(max + boost, max + cap)
				if (cur >= top) cur else minOf(cur + boost, top)
			}
			KIND_RESTORE -> if (cur >= max) cur else minOf(cur + amount + max * effect[3] / 10000, max)
			KIND_DRAIN -> {
				val floor: Int = if (stat == Skill.CONSTITUTION || stat == Skill.PRAYER || stat == Skill.SUMMONING) 0 else 1
				maxOf(cur - (amount + maxOf(cur, max) * effect[3] / 10000), minOf(floor, cur))
			}
			KIND_HEAL, KIND_ENERGY -> if (cur >= max) cur else minOf(cur + amount, max)
			KIND_ZAMORAK -> maxOf(0, cur - zamorakDamage(cur))
			else -> cur
		}
	}

	private fun wouldCap(effect: IntArray, stat: Int, cur: Int, max: Int, amount: Int): Boolean {
		val theoretical: Int = theoreticalOf(effect, cur, max, amount)
		return theoretical > 0 && apply(effect, stat, cur, max, amount) - cur < theoretical
	}

	/** Void's Zamorak brew: 20 lifepoints and ten for each hundred. */
	private fun zamorakDamage(lifePoints: Int): Int = lifePoints / 100 * 10 + 20

	/** The stat's value now: a skill's level, lifepoints for Constitution, or run energy. */
	@JvmStatic
	fun current(client: ClientApi, stat: Int): Int {
		return when (stat) {
			RUN_ENERGY -> client.runEnergy()
			Skill.CONSTITUTION -> StatusBars.lifePoints(client)
			else -> client.skillLevel(stat)
		}
	}

	@JvmStatic
	fun maximum(client: ClientApi, stat: Int): Int {
		return when (stat) {
			RUN_ENERGY -> 100
			Skill.CONSTITUTION -> client.skillBaseLevel(Skill.CONSTITUTION) * 10
			else -> client.skillBaseLevel(stat)
		}
	}

	private fun statName(stat: Int): String = if (stat == RUN_ENERGY) "Run Energy" else Skill.name(stat)

	/** RuneLite's buildStatChangeString: the change, what it could be, the level after and the stat, in its colour. */
	@JvmStatic
	fun statChangeString(c: StatChange): String {
		val b = StringBuilder()
		b.append(colourTag(positivityColour(c.positivity)))
		if (relative) {
			b.append(range(c.relativeLow, c.relativeHigh, true))
		}
		if (theoretical) {
			if (relative) {
				b.append('/')
			}
			b.append(range(c.theoretical, c.theoreticalHigh, true))
		}
		if (absolute && (relative || theoretical)) {
			b.append(" (")
		}
		if (absolute) {
			b.append(range(c.absoluteLow, c.absoluteHigh, false))
		}
		if (absolute && (relative || theoretical)) {
			b.append(')')
		}
		b.append(' ').append(statName(c.stat))
		b.append("</br>")
		return b.toString()
	}

	/** RuneLite's StatChange and RangeStatChange formats: "+5", or "+3~+6" for a range. */
	private fun range(low: Int, high: Int, signed: Boolean): String {
		val lowText: String = if (signed) signed(low) else low.toString()
		if (low == high) {
			return lowText
		}
		return lowText + "~" + (if (signed) signed(high) else high.toString())
	}

	private fun signed(value: Int): String = if (value >= 0) "+$value" else value.toString()

	@JvmStatic
	fun positivityColour(positivity: Int): Int {
		return when (positivity) {
			WORSE -> colorWorse
			NO_CHANGE -> colorNoChange
			BETTER_CAPPED -> colorBetterCapped
			BETTER_SOMECAPPED -> colorBetterSomecapped
			else -> colorBetterUncapped
		}
	}

	private fun colourTag(argb: Int): String = "<col=" + (argb and 0xFFFFFF).toString(16).padStart(6, '0') + ">"

	/** m:ss. */
	@JvmStatic
	fun minutesSeconds(seconds: Int): String = (seconds / 60).toString() + ":" + (seconds % 60).toString().padStart(2, '0')

	/** An item's bonuses, from the cache's params: attack and defence (5 each), prayer, speed, strength, ranged strength, magic damage. */
	class Bonuses {
		@JvmField val attack: IntArray = IntArray(5)
		@JvmField val defence: IntArray = IntArray(5)
		@JvmField var prayer: Int = 0
		@JvmField var speed: Int = 0
		@JvmField var strength: Int = 0
		@JvmField var rangedStrength: Int = 0
		@JvmField var magicDamage: Int = 0

		fun minus(other: Bonuses?): Bonuses {
			val out = Bonuses()
			for (i in 0 until 5) {
				out.attack[i] = attack[i] - (other?.attack?.get(i) ?: 0)
				out.defence[i] = defence[i] - (other?.defence?.get(i) ?: 0)
			}
			out.prayer = prayer - (other?.prayer ?: 0)
			out.speed = speed - (other?.speed ?: 0)
			out.strength = strength - (other?.strength ?: 0)
			out.rangedStrength = rangedStrength - (other?.rangedStrength ?: 0)
			out.magicDamage = magicDamage - (other?.magicDamage ?: 0)
			return out
		}
	}

	@JvmStatic
	fun bonuses(item: ItemDefinition): Bonuses {
		val b = Bonuses()
		for (i in 0 until 5) {
			b.attack[i] = item.intParam(ATTACK_PARAMS[i], 0)
			b.defence[i] = item.intParam(DEFENCE_PARAMS[i], 0)
		}
		b.prayer = item.intParam(PRAYER_PARAM, 0)
		b.speed = item.intParam(SPEED_PARAM, 0)
		b.strength = item.intParam(STRENGTH_PARAM, 0)
		b.rangedStrength = item.intParam(RANGED_STRENGTH_PARAM, 0)
		b.magicDamage = item.intParam(MAGIC_DAMAGE_PARAM, 0)
		return b
	}

	/** RuneLite's UNARMED: nothing but the attack speed. */
	@JvmStatic
	fun unarmed(): Bonuses {
		val b = Bonuses()
		b.speed = UNARMED_SPEED
		return b
	}

	/** RuneLite's buildStatBonusString: the weight, and the bonuses against what is worn in the item's slot. */
	@JvmStatic
	fun statBonusString(client: ClientApi, itemId: Int): String {
		val item: ItemDefinition = client.item(itemId) ?: return ""
		val slot: Int = ItemStatTables.slot(itemId)
		val equipable: Boolean = slot >= 0 && !item.isNoted()
		val stats: Bonuses = bonuses(item)
		var other: Bonuses? = null
		var offHand: Bonuses? = null
		val worn: ItemContainer? = client.itemContainer(WORN)
		if (equipable && worn != null) {
			other = wornBonuses(client, worn, slot)
			if (other == null && slot == SHIELD_SLOT) {
				// a shield where a two-handed weapon is worn: the weapon goes, and its place is unarmed
				val weapon: Int = wornItem(worn, WEAPON_SLOT)
				if (weapon >= 0 && ItemStatTables.isTwoHanded(weapon)) {
					other = client.item(weapon)?.let { bonuses(it).minus(unarmed()) }
				}
			}
			if (slot == WEAPON_SLOT) {
				if (other == null) {
					other = unarmed()
				}
				if (ItemStatTables.isTwoHanded(itemId)) {
					offHand = wornBonuses(client, worn, SHIELD_SLOT)
				}
			}
		}
		val e: Bonuses = stats.minus(other).minus(offHand)
		val b = StringBuilder()
		if (showWeight) {
			val grams: Int = if (item.isNoted()) 0 else ItemStatTables.grams(itemId)
			if (grams > 0) {
				b.append(statRow("Weight", grams / 1000.0, grams / 1000.0, true, false, equipable))
			}
		}
		if (equipable) {
			b.append(statRow("Prayer", stats.prayer.toDouble(), e.prayer.toDouble(), false, false, true))
			b.append(statRow("Speed", stats.speed.toDouble(), e.speed.toDouble(), true, false, true))
			// the cache keeps the strength bonuses in tenths (Void's max hit is in lifepoints: 5 + level * bonus / 64)
			b.append(statRow("Melee Str", stats.strength / 10.0, e.strength / 10.0, false, false, true))
			b.append(statRow("Range Str", stats.rangedStrength / 10.0, e.rangedStrength / 10.0, false, false, true))
			b.append(statRow("Magic Dmg", stats.magicDamage.toDouble(), e.magicDamage.toDouble(), false, true, true))
			val attack = StringBuilder()
			val defence = StringBuilder()
			for (i in 0 until 5) {
				attack.append(statRow(BONUS_NAMES[i], stats.attack[i].toDouble(), e.attack[i].toDouble(), false, false, true))
				defence.append(statRow(BONUS_NAMES[i], stats.defence[i].toDouble(), e.defence[i].toDouble(), false, false, true))
			}
			if (attack.isNotEmpty()) {
				b.append(MENU_TARGET).append("Attack Bonus</br></col>").append(attack)
			}
			if (defence.isNotEmpty()) {
				b.append(MENU_TARGET).append("Defence Bonus</br></col>").append(defence)
			}
		}
		return b.toString()
	}

	private fun wornItem(worn: ItemContainer, slot: Int): Int = if (slot < worn.size()) worn.itemId(slot) else -1

	private fun wornBonuses(client: ClientApi, worn: ItemContainer, slot: Int): Bonuses? {
		val id: Int = wornItem(worn, slot)
		if (id < 0) {
			return null
		}
		return client.item(id)?.let { bonuses(it) }
	}

	/** RuneLite's buildStatRow: "Label: change", or "Label: value (change)" with Always show base stats. */
	@JvmStatic
	fun statRow(label: String, value: Double, diff: Double, inverse: Boolean, percent: Boolean, showBase: Boolean): String {
		if (value == 0.0 && diff == 0.0) {
			return ""
		}
		val change: String = changeString(diff, inverse, percent)
		if (alwaysShowBaseStats && showBase) {
			return label + ": " + number(value) + (if (change.isNotEmpty()) " ($change) " else "") + "</br>"
		}
		return if (change.isNotEmpty()) "$label: $change</br>" else ""
	}

	/** RuneLite's getChangeString: "+3" in the better colour, "-3" in the worse, swapped when lower is better. */
	@JvmStatic
	fun changeString(value: Double, inverse: Boolean, percent: Boolean): String {
		if (value == 0.0) {
			return ""
		}
		val colour: Int = if ((value > 0) != inverse) colorBetterUncapped else colorWorse
		return colourTag(colour) + (if (value > 0) "+" else "") + number(value) + (if (percent) "%" else "") + "</col>"
	}

	/** RuneLite's QuantityFormatter.formatNumber for a double: up to three places, no trailing zeros. */
	@JvmStatic
	fun number(value: Double): String {
		val thousandths: Long = kotlin.math.round(value * 1000.0).toLong()
		val whole: Long = thousandths / 1000L
		var part: Long = kotlin.math.abs(thousandths % 1000L)
		val sign: String = if (thousandths < 0 && whole == 0L) "-" else ""
		if (part == 0L) {
			return sign + GroundItemRules.withCommas(whole)
		}
		var digits = 3
		while (part % 10L == 0L) {
			part /= 10L
			digits--
		}
		return sign + GroundItemRules.withCommas(whole) + "." + part.toString().padStart(digits, '0')
	}

	/** JagexColors.MENU_TARGET, the bonus headings' colour. */
	private const val MENU_TARGET: String = "<col=ff9040>"
}

/** Item Stats' tables from ItemStatData, read on first use. */
object ItemStatTables {

	private var equipment: HashMap<Int, Int>? = null
	private var weights: HashMap<Int, Int>? = null
	private var consumables: HashMap<Int, Array<IntArray>>? = null
	private var durations: HashMap<Int, IntArray>? = null

	private fun loadEquipment() {
		if (equipment != null) {
			return
		}
		val slots: HashMap<Int, Int> = HashMap()
		val grams: HashMap<Int, Int> = HashMap()
		for (part in ItemStatData.equipment()) {
			for (row in part.split(';')) {
				if (row.isEmpty()) {
					continue
				}
				val f: List<String> = row.split(',')
				val id: Int = f[0].toInt()
				val slot: Int = f[1].toInt()
				if (slot >= 0) {
					slots[id] = slot or (f[2].toInt() shl 8)
				}
				val g: Int = f[3].toInt()
				if (g >= 0) {
					grams[id] = g
				}
			}
		}
		equipment = slots
		weights = grams
	}

	/** Item [id]'s equipment slot, -1 for none. */
	@JvmStatic
	fun slot(id: Int): Int {
		loadEquipment()
		return (equipment!![id] ?: return -1) and 0xFF
	}

	@JvmStatic
	fun isTwoHanded(id: Int): Boolean {
		loadEquipment()
		return ((equipment!![id] ?: 0) shr 8) != 0
	}

	/** Item [id]'s weight in grams, 0 for none known. */
	@JvmStatic
	fun grams(id: Int): Int {
		loadEquipment()
		return weights!![id] ?: 0
	}

	/** What eating or drinking item [id] does, `kind, stat, a, b, c` each; null for nothing. */
	@JvmStatic
	fun consumable(id: Int): Array<IntArray>? {
		var table: HashMap<Int, Array<IntArray>>? = consumables
		if (table == null) {
			table = HashMap()
			for (part in ItemStatData.consumables()) {
				for (row in part.split(';')) {
					val eq: Int = row.indexOf('=')
					if (eq <= 0) {
						continue
					}
					table[row.substring(0, eq).toInt()] = row.substring(eq + 1).split('|').map { e ->
						val f: List<String> = e.split(',')
						intArrayOf(f[0][0].code, f[1].toInt(), f[2].toInt(), f[3].toInt(), f[4].toInt())
					}.toTypedArray()
				}
			}
			consumables = table
		}
		return table[id]
	}

	/** How long potion [id] lasts: its seconds; null for one that does not. */
	@JvmStatic
	fun duration(id: Int): IntArray? {
		var table: HashMap<Int, IntArray>? = durations
		if (table == null) {
			table = HashMap()
			for (part in ItemStatData.durations()) {
				for (row in part.split(';')) {
					val f: List<String> = row.split(',')
					if (f.size == 3) {
						table[f[0].toInt()] = intArrayOf(f[2].toInt())
					}
				}
			}
			durations = table
		}
		return table[id]
	}
}
