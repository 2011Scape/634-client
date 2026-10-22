package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.MenuEntry
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.NpcDefinition
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.MenuBuilt
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Status Bars. A bar on each side of the side panel shows
 * lifepoints, prayer, run energy, special attack or summoning points (each side its own choice), filled
 * from the bottom in RuneLite's colours, with the orb's icon and, if chosen, the value; hovering food or a
 * potion in the backpack shows what it would restore (ItemHeals). With a delay set, the bars hide that
 * many game ticks after the last fight.
 *
 * 634 differences: Summoning points take the place of Old School's Warmth; the bar width applies on the
 * resizable screen, as RuneLite's applies on its resizable bottom layout; there is no venom, stamina or
 * parasite, and resting takes the stamina colour on the run bar. The drawing is in StatusBarsOverlay.
 */
object StatusBars : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("statusbars", "Status Bars", "1", PluginApi.VERSION, "Shows lifepoints, prayer, run energy, special attack or summoning as bars beside the side panel, with optional counters and icons, and how much a hovered food or potion would restore.", "Pixel")

	/** RuneLite's BarMode, as the settings show them; Summoning in Warmth's place. */
	@JvmField
	val MODES: Array<String> = arrayOf("Disabled", "Hitpoints", "Prayer", "Run Energy", "Special Attack", "Summoning")

	const val DISABLED: Int = 0
	const val HITPOINTS: Int = 1
	const val PRAYER: Int = 2
	const val RUN_ENERGY: Int = 3
	const val SPECIAL_ATTACK: Int = 4
	const val SUMMONING: Int = 5

	/** RuneLite's BarRenderer widths. */
	const val DEFAULT_WIDTH: Int = 20
	const val MIN_WIDTH: Int = 3
	const val MAX_WIDTH: Int = 40

	/** The backpack's item grid, whose hovered item the restore preview reads. */
	const val BACKPACK_WIDGET: Int = 149 shl 16

	private const val CYCLES_PER_TICK: Int = 30

	/** Void's variables: lifepoints, special attack energy (tenths of a percent), poison, disease, movement (3 = resting), prayers. */
	const val LIFE_POINTS_VARBIT: Int = 7198
	const val SPECIAL_VARP: Int = 300
	const val POISON_VARP: Int = 102
	const val DISEASE_VARP: Int = 456
	const val MOVEMENT_VARP: Int = 173
	const val RESTING: Int = 3

	@JvmField var enabled: Boolean = false
	@JvmField var enableCounter: Boolean = false
	@JvmField var enableSkillIcon: Boolean = true
	@JvmField var enableRestorationBars: Boolean = true
	@JvmField var leftBarMode: Int = HITPOINTS
	@JvmField var rightBarMode: Int = PRAYER
	@JvmField var hideAfterCombatDelay: Int = 0
	@JvmField var barWidth: Int = DEFAULT_WIDTH

	/** RuneLite's barsDisplayed: false once the delay after the last fight has passed. */
	@JvmField
	var barsDisplayed: Boolean = true

	/** The game tick of the last fight (RuneLite's lastCombatActionTickCount). */
	private var lastCombatTick: Int = 0

	private var lastTick: Int = -1

	/** The item under the mouse in the backpack, from the menu's top entry as it was last built; -1 for none. */
	@JvmField
	var hoveredItem: Int = -1

	private var context: PluginContext? = null

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			context?.client()?.let { checkStatusBars(it, it.serverTick() / CYCLES_PER_TICK) }
		}
	}

	private val tickListener: EventListener<GameTick> = EventListener {
		val client: ClientApi = context?.client() ?: return@EventListener
		val tick: Int = client.serverTick() / CYCLES_PER_TICK
		if (tick != lastTick) {
			lastTick = tick
			checkStatusBars(client, tick)
		}
	}

	private val menuListener: EventListener<MenuBuilt> = EventListener {
		val client: ClientApi = context?.client() ?: return@EventListener
		val top: MenuEntry? = if (client.menuEntryCount() > 0) client.menuEntry(0) else null
		hoveredItem = if (top != null && top.param1() == BACKPACK_WIDGET && top.itemId() >= 0) top.itemId() else -1
	}

	private val frameListener: EventListener<FrameDraw> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (enabled && barsDisplayed && client.gameState() === GameState.LOGGED_IN) {
			StatusBarsOverlay.draw(event.graphics, client, event.width)
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		val context: PluginContext = this.context ?: return@CommandHandler
		enabled = !enabled
		context.config().setBoolean("enabled", enabled)
		context.config().save()
		context.client().consolePrint(if (enabled) "Status bars on." else "Status bars off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		declareSettings(context.settings())
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.MENU_BUILT, menuListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.registerCommand("statusbars", toggle)
	}

	override fun stop() {
		context = null
		barsDisplayed = false
		hoveredItem = -1
	}

	private fun declareSettings(s: SettingsSchema) {
		s.toggle("enabled", "Status bars", false)
		s.toggle("enableCounter", "Show counters", false)
		s.toggle("enableSkillIcon", "Show icons", true)
		s.toggle("enableRestorationBars", "Show restores", true)
		s.choice("leftBarMode", "Left bar", MODES[HITPOINTS], MODES)
		s.choice("rightBarMode", "Right bar", MODES[PRAYER], MODES)
		s.number("hideAfterCombatDelay", "Hide after combat delay (ticks)", 0, 0, 1000, 1)
		s.number("barWidth", "Bar width (resizable)", DEFAULT_WIDTH, MIN_WIDTH, MAX_WIDTH, 1)
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		enableCounter = config.getBoolean("enableCounter", false)
		enableSkillIcon = config.getBoolean("enableSkillIcon", true)
		enableRestorationBars = config.getBoolean("enableRestorationBars", true)
		leftBarMode = mode(config.get("leftBarMode", MODES[HITPOINTS]), HITPOINTS)
		rightBarMode = mode(config.get("rightBarMode", MODES[PRAYER]), PRAYER)
		hideAfterCombatDelay = maxOf(0, config.getInt("hideAfterCombatDelay", 0))
		barWidth = config.getInt("barWidth", DEFAULT_WIDTH).coerceIn(MIN_WIDTH, MAX_WIDTH)
	}

	/** The mode named [name] (as the settings or RuneLite's config store it), [default] for a name not known. */
	@JvmStatic
	fun mode(name: String, default: Int): Int {
		val i: Int = MODES.indexOfFirst { it.equals(name, true) || it.replace(' ', '_').equals(name, true) }
		if (i >= 0) {
			return i
		}
		// RuneLite's WARMTH becomes Summoning
		return if (name.equals("WARMTH", true)) SUMMONING else default
	}

	/** RuneLite's checkStatusBars, once a game tick. */
	@JvmStatic
	fun checkStatusBars(client: ClientApi, tick: Int) {
		val player: Player = client.localPlayer() ?: return
		if (hideAfterCombatDelay == 0) {
			barsDisplayed = true
		} else if (inCombat(player.interacting())) {
			lastCombatTick = tick
			barsDisplayed = true
		} else if (tick - lastCombatTick >= hideAfterCombatDelay) {
			barsDisplayed = false
		}
	}

	/** Dealing with an npc that can be attacked, or with a player who could be attacked here (RuneLite: in a PvP area). */
	@JvmStatic
	fun inCombat(target: Actor?): Boolean {
		if (target is Npc) {
			val type: NpcDefinition = target.type() ?: return false
			return (0 until 5).any { type.action(it) == "Attack" }
		}
		return target is Player && target.isAttackable()
	}

	/** The bar's current value and its most, by mode. */
	@JvmStatic
	fun current(client: ClientApi, mode: Int): Int = when (mode) {
		HITPOINTS -> lifePoints(client)
		PRAYER -> client.skillLevel(Skill.PRAYER)
		RUN_ENERGY -> client.runEnergy()
		SPECIAL_ATTACK -> client.varp(SPECIAL_VARP) / 10
		SUMMONING -> client.skillLevel(Skill.SUMMONING)
		else -> 0
	}

	@JvmStatic
	fun maximum(client: ClientApi, mode: Int): Int = when (mode) {
		HITPOINTS -> client.skillBaseLevel(Skill.CONSTITUTION) * 10
		PRAYER -> client.skillBaseLevel(Skill.PRAYER)
		SUMMONING -> client.skillBaseLevel(Skill.SUMMONING)
		else -> 100
	}

	/** Lifepoints: the server's varbit, or the Constitution level times ten before it comes. */
	@JvmStatic
	fun lifePoints(client: ClientApi): Int {
		val lp: Int = client.varbit(LIFE_POINTS_VARBIT)
		return if (lp > 0) lp else client.skillLevel(Skill.CONSTITUTION) * 10
	}

	/** What the hovered backpack item restores of the bar's stat (RuneLite's getRestoreValue). */
	@JvmStatic
	fun restore(client: ClientApi, mode: Int): Int {
		val item: Int = hoveredItem
		if (item < 0) {
			return 0
		}
		val stat: Int = when (mode) {
			HITPOINTS -> ItemHeals.HITPOINTS
			PRAYER -> ItemHeals.PRAYER
			RUN_ENERGY -> ItemHeals.RUN_ENERGY
			SPECIAL_ATTACK -> ItemHeals.SPECIAL_ATTACK
			SUMMONING -> ItemHeals.SUMMONING
			else -> return 0
		}
		return ItemHeals.restore(item, stat, maximum(client, mode))
	}
}
