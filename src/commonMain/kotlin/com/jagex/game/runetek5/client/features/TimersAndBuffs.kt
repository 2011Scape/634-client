package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.Counter
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.InfoBox
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Timer
import com.jagex.game.runetek5.plugin.api.events.AnimationChanged
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.OverheadTextChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.VarpChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Timers and Buffs, for what 634 has: antifire and super
 * antifire, overload, antipoison, teleblock, freezes (the ice spells, bind, snare, entangle), magic imbue,
 * vengeance and vengeance active, charge, the cannon and the juju potions, each an infobox counting down,
 * each with its own switch.
 *
 * RuneLite reads most of them from variables the Old School server sends; the Void server sends only the
 * poison one (varp 102: below 0 is an antipoison's protection), so the rest start from its chat lines, the
 * player's animations and graphics and the "Taste vengeance!" overhead, and are timed here. Freezes last as
 * RuneLite's (half under Protect from Magic or Deflect Magic) and go once the player moves; the infoboxes
 * timed here go at logout, and those RuneLite drops on death go on "Oh dear, you are dead!".
 */
object TimersAndBuffs : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("timers", "Timers and Buffs", "1", PluginApi.VERSION, "Show various timers in an infobox: potions, teleblock, freezes, vengeance, charge, cannon and more (::timers turns it on and off).", "Pixel")

	/** A server tick in milliseconds, and in client cycles. */
	const val TICK_MILLIS: Long = 600L
	private const val CYCLES_PER_TICK: Int = 30

	/** RuneLite's GameTimer for 634: the setting that shows it, its name, its picture (an item or a sprite), its length in game ticks (0: no countdown), whether death takes it away. */
	class TimerType(
		@JvmField val setting: String,
		@JvmField val name: String,
		@JvmField val itemId: Int,
		@JvmField val spriteId: Int,
		@JvmField val ticks: Int,
		@JvmField val removedOnDeath: Boolean,
	)

	// item icons: antifire (4), super antifire (4), overload (4), antipoison (4), cannon barrels, juju potions (4)
	@JvmField val ANTIFIRE: TimerType = TimerType("showAntiFire", "Antifire", 2452, -1, 600, false)
	@JvmField val SUPER_ANTIFIRE: TimerType = TimerType("showAntiFire", "Super antifire", 15304, -1, 600, false)
	@JvmField val OVERLOAD: TimerType = TimerType("showOverload", "Overload", 15332, -1, 500, true)
	@JvmField val ANTIPOISON: TimerType = TimerType("showAntipoison", "Antipoison", 2446, -1, 0, false)
	@JvmField val CANNON: TimerType = TimerType("showCannon", "Cannon", 10, -1, 2500, false)
	@JvmField val JUJU_MINING: TimerType = TimerType("showJuju", "Juju mining potion", 20003, -1, 500, false)
	@JvmField val JUJU_WOODCUTTING: TimerType = TimerType("showJuju", "Juju woodcutting potion", 20015, -1, 500, false)
	@JvmField val JUJU_FARMING: TimerType = TimerType("showJuju", "Juju farming potion", 20011, -1, 500, false)
	@JvmField val JUJU_FISHING: TimerType = TimerType("showJuju", "Juju fishing potion", 20019, -1, 500, false)
	@JvmField val SCENTLESS: TimerType = TimerType("showJuju", "Scentless potion", 20027, -1, 500, false)
	@JvmField val SARADOMINS_BLESSING: TimerType = TimerType("showJuju", "Saradomin's blessing", 20031, -1, 500, false)
	@JvmField val GUTHIXS_GIFT: TimerType = TimerType("showJuju", "Guthix's gift", 20035, -1, 500, false)
	@JvmField val ZAMORAKS_FAVOUR: TimerType = TimerType("showJuju", "Zamorak's favour", 20039, -1, 500, false)

	// spell icons from the 634 spellbooks (192, 193, 430)
	@JvmField val TELEBLOCK: TimerType = TimerType("showTeleblock", "Teleblock", -1, 1571, 500, true)
	@JvmField val BIND: TimerType = TimerType("showFreezes", "Bind", -1, 369, 8, true)
	@JvmField val SNARE: TimerType = TimerType("showFreezes", "Snare", -1, 370, 16, true)
	@JvmField val ENTANGLE: TimerType = TimerType("showFreezes", "Entangle", -1, 371, 24, true)
	@JvmField val ICE_RUSH: TimerType = TimerType("showFreezes", "Ice rush", -1, 375, 8, true)
	@JvmField val ICE_BURST: TimerType = TimerType("showFreezes", "Ice burst", -1, 376, 16, true)
	@JvmField val ICE_BLITZ: TimerType = TimerType("showFreezes", "Ice blitz", -1, 377, 24, true)
	@JvmField val ICE_BARRAGE: TimerType = TimerType("showFreezes", "Ice barrage", -1, 378, 32, true)
	@JvmField val MAGIC_IMBUE: TimerType = TimerType("showMagicImbue", "Magic imbue", -1, 602, 20, false)
	@JvmField val VENGEANCE: TimerType = TimerType("showVengeance", "Vengeance", -1, 614, 50, false)
	@JvmField val VENGEANCE_ACTIVE: TimerType = TimerType("showVengeanceActive", "Vengeance active", -1, 611, 0, true)
	@JvmField val CHARGE: TimerType = TimerType("showCharge", "Charge", -1, 372, 252, true)

	/** The settings, RuneLite's keys and names, in its order; Juju potions are 634's. */
	@JvmField
	val SETTINGS: Array<Array<String>> = arrayOf(
		arrayOf("showAntipoison", "Antipoison timers"),
		arrayOf("showAntiFire", "Antifire timer"),
		arrayOf("showOverload", "Overload timer"),
		arrayOf("showJuju", "Juju potion timers"),
		arrayOf("showCannon", "Cannon timer"),
		arrayOf("showMagicImbue", "Magic imbue timer"),
		arrayOf("showCharge", "Charge timer"),
		arrayOf("showVengeance", "Vengeance timer"),
		arrayOf("showVengeanceActive", "Vengeance active"),
		arrayOf("showTeleblock", "Teleblock timer"),
		arrayOf("showFreezes", "Freeze timer"),
	)

	/** The freeze spells' impact graphics on the player, to tell which froze it (Void's magic.gfx.toml). */
	@JvmField
	val FREEZE_GRAPHICS: Map<Int, TimerType> = mapOf(361 to ICE_RUSH, 363 to ICE_BURST, 367 to ICE_BLITZ, 369 to ICE_BARRAGE, 181 to BIND, 180 to SNARE, 179 to ENTANGLE)

	/** The juju potions, by the start of the name the drink line gives. */
	private val JUJUS: Array<Pair<String, TimerType>> = arrayOf(
		"juju mining" to JUJU_MINING, "juju woodcutting" to JUJU_WOODCUTTING, "juju farming" to JUJU_FARMING, "juju fishing" to JUJU_FISHING,
		"scentless" to SCENTLESS, "saradomin's blessing" to SARADOMINS_BLESSING, "guthix's gift" to GUTHIXS_GIFT, "zamorak's favour" to ZAMORAKS_FAVOUR,
	)

	/** The animations: vengeance's cast, charge's (the god spells share it, so only with nothing targeted), overload's drink. */
	const val VENGEANCE_ANIMATION: Int = 4410
	const val CHARGE_ANIMATION: Int = 811
	const val OVERLOAD_ANIMATION: Int = 3170

	/** Vengeance Other cast on the player. */
	const val VENGEANCE_OTHER_GRAPHIC: Int = 725

	/** Void's variables: poison (below 0, an antipoison's protection, rising 2 every 30 ticks), the prayers and curses. */
	const val POISON_VARP: Int = 102
	private const val POISON_STEP_TICKS: Int = 30
	private const val PROTECT_FROM_MAGIC_BIT: Int = 12
	private const val DEFLECT_MAGIC_BIT: Int = 7

	/** By setting key: shown. */
	@JvmField
	val shown: HashMap<String, Boolean> = HashMap()

	@JvmField
	var enabled: Boolean = true

	/** The boxes up, by timer. */
	@JvmField
	val active: HashMap<TimerType, InfoBox> = HashMap()

	/** A freeze message this tick: the graphic seen on the player refines which (RuneLite's freezeTime). */
	private var freezeTick: Int = -1

	/** Where the player stood when frozen, to end the freeze once it moves on a later tick. */
	private var frozenX: Int = -1
	private var frozenY: Int = -1
	private var frozenTick: Int = -1

	private var lastTick: Int = -1
	private var lastGraphic: Int = -1

	private var context: PluginContext? = null

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			// a timer switched off goes
			for (type in active.keys.toList()) {
				if (!isShown(type)) {
					remove(type)
				}
			}
		}
	}

	private val chatListener: EventListener<ChatMessage> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		chat(client, ClientFeatures.plainText(event.message ?: return@EventListener))
	}

	private val varpListener: EventListener<VarpChanged> = EventListener { event ->
		if (event.id == POISON_VARP) {
			poison(event.value)
		}
	}

	private val animationListener: EventListener<AnimationChanged> = EventListener { event ->
		val player: Player = context?.client()?.localPlayer() ?: return@EventListener
		if (event.actor === player) {
			animation(player, event.animationId)
		}
	}

	private val overheadListener: EventListener<OverheadTextChanged> = EventListener { event ->
		val player: Player = context?.client()?.localPlayer() ?: return@EventListener
		if (event.actor === player && event.text == "Taste vengeance!") {
			remove(VENGEANCE_ACTIVE)
		}
	}

	private val tickListener: EventListener<GameTick> = EventListener {
		val client: ClientApi = context?.client() ?: return@EventListener
		val tick: Int = client.serverTick() / CYCLES_PER_TICK
		if (tick != lastTick) {
			lastTick = tick
			gameTick(client, tick)
		}
	}

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state !== GameState.LOGGED_IN) {
			// the server starts its own timers again at login without saying so: those timed here would be wrong
			clear()
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		val context: PluginContext = this.context ?: return@CommandHandler
		enabled = !enabled
		context.config().setBoolean("enabled", enabled)
		context.config().save()
		if (!enabled) {
			clear()
		}
		context.client().consolePrint(if (enabled) "Timers on." else "Timers off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		declareSettings(context.settings())
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.VARP_CHANGED, varpListener)
		context.events().subscribe(Events.ANIMATION_CHANGED, animationListener)
		context.events().subscribe(Events.OVERHEAD_TEXT_CHANGED, overheadListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.registerCommand("timers", toggle)
		poison(context.client().varp(POISON_VARP))
	}

	override fun stop() {
		clear()
		context = null
	}

	private fun declareSettings(s: SettingsSchema) {
		s.toggle("enabled", "Timers and buffs", true)
		for (setting in SETTINGS) {
			s.toggle(setting[0], setting[1], true)
		}
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		for (setting in SETTINGS) {
			shown[setting[0]] = config.getBoolean(setting[0], true)
		}
	}

	private fun isShown(type: TimerType): Boolean = enabled && shown[type.setting] != false

	/** RuneLite's onChatMessage, on the Void server's lines (tags stripped). */
	@JvmStatic
	fun chat(client: ClientApi, message: String) {
		when {
			message.startsWith("You drink the ") -> drink(message.substring("You drink the ".length).lowercase())
			message == "Your resistance to dragonfire has run out." -> {
				remove(ANTIFIRE)
				remove(SUPER_ANTIFIRE)
			}
			message == "The effects of overload have worn off and you feel normal again." -> remove(OVERLOAD)
			message == "You have been teleblocked." -> add(TELEBLOCK, TELEBLOCK.ticks)
			message == "Your teleblock has worn off." -> remove(TELEBLOCK)
			message == "You have been frozen!" -> {
				freezeTick = client.serverTick() / CYCLES_PER_TICK
				val player: Player? = client.localPlayer()
				// the graphic may have come in this tick already
				freeze(client, FREEZE_GRAPHICS[player?.spotAnimationId() ?: -1] ?: ICE_BARRAGE)
			}
			message == "You are charged to combine runes!" -> add(MAGIC_IMBUE, MAGIC_IMBUE.ticks)
			message == "You add the furnace." -> add(CANNON, CANNON.ticks)
			message == "You pick up the cannon. It's really heavy." || message.startsWith("Your cannon has decayed") -> remove(CANNON)
			message == "Oh dear, you are dead!" -> active.keys.filter { it.removedOnDeath }.forEach { remove(it) }
		}
	}

	private fun drink(item: String) {
		when {
			item.startsWith("super antifire") -> add(SUPER_ANTIFIRE, SUPER_ANTIFIRE.ticks)
			item.startsWith("antifire") -> add(ANTIFIRE, ANTIFIRE.ticks)
			item.startsWith("overload") -> add(OVERLOAD, OVERLOAD.ticks)
			else -> JUJUS.firstOrNull { item.startsWith(it.first) }?.let { add(it.second, it.second.ticks) }
		}
	}

	/** Poison's varp: below 0 the antipoison's protection, its time left read from the value. */
	@JvmStatic
	fun poison(value: Int) {
		if (value < 0) {
			add(ANTIPOISON, -value / 2 * POISON_STEP_TICKS)
		} else {
			remove(ANTIPOISON)
		}
	}

	@JvmStatic
	fun animation(player: Player, animation: Int) {
		when (animation) {
			VENGEANCE_ANIMATION -> {
				add(VENGEANCE, VENGEANCE.ticks)
				add(VENGEANCE_ACTIVE, 0)
			}
			CHARGE_ANIMATION -> if (player.interacting() == null) add(CHARGE, CHARGE.ticks)
			OVERLOAD_ANIMATION -> if (OVERLOAD !in active) add(OVERLOAD, OVERLOAD.ticks)
		}
	}

	/** Once a game tick: a freeze graphic refines this tick's freeze; a freeze ends when the player moves; Vengeance Other cast on the player. */
	@JvmStatic
	fun gameTick(client: ClientApi, tick: Int) {
		// boxes whose time ran out are gone from the panel
		active.entries.removeAll { it.value.cull() }
		val player: Player = client.localPlayer() ?: return
		val graphic: Int = player.spotAnimationId()
		if (graphic != lastGraphic) {
			lastGraphic = graphic
			val refined: TimerType? = FREEZE_GRAPHICS[graphic]
			if (refined != null && freezeTick >= 0 && tick - freezeTick <= 1) {
				freeze(client, refined)
			}
			if (graphic == VENGEANCE_OTHER_GRAPHIC) {
				add(VENGEANCE_ACTIVE, 0)
			}
		}
		if (frozenTick >= 0 && tick > frozenTick && (player.serverTileX() != frozenX || player.serverTileY() != frozenY)) {
			FREEZE_GRAPHICS.values.forEach { remove(it) }
			frozenTick = -1
		}
	}

	private fun freeze(client: ClientApi, type: TimerType) {
		FREEZE_GRAPHICS.values.forEach { remove(it) }
		val protect: Boolean = client.varp(Boosts.PRAYERS_VARP) and (1 shl PROTECT_FROM_MAGIC_BIT) != 0 ||
			client.varp(Boosts.CURSES_VARP) and (1 shl DEFLECT_MAGIC_BIT) != 0
		add(type, if (protect) type.ticks / 2 else type.ticks)
		val player: Player? = client.localPlayer()
		frozenX = player?.serverTileX() ?: -1
		frozenY = player?.serverTileY() ?: -1
		frozenTick = client.serverTick() / CYCLES_PER_TICK
	}

	/** Puts [type]'s box up for [ticks] game ticks (0: no countdown), in place of one already up. */
	@JvmStatic
	fun add(type: TimerType, ticks: Int) {
		if (!isShown(type)) {
			return
		}
		remove(type)
		val box: InfoBox = if (ticks > 0) Timer(type.name, ticks * TICK_MILLIS) else ActiveBox(type.name)
		box.itemId = type.itemId
		box.spriteId = type.spriteId
		box.tooltip = type.name
		active[type] = box
		context?.addInfoBox(box)
	}

	@JvmStatic
	fun remove(type: TimerType) {
		val box: InfoBox = active.remove(type) ?: return
		context?.removeInfoBox(box)
	}

	@JvmStatic
	fun clear() {
		for (type in active.keys.toList()) {
			remove(type)
		}
		freezeTick = -1
		frozenTick = -1
		lastGraphic = -1
	}

	/** A buff with no countdown (Vengeance active): the picture alone, until it is taken away. */
	private class ActiveBox(name: String) : Counter(name, 0) {
		override fun text(): String? = null
	}
}
