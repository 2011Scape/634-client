package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.MenuEntry
import com.jagex.game.runetek5.plugin.api.Npc
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
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.MenuBuilt
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Opponent Information. The npc or player you last dealt with
 * (attacked, followed, traded) is your opponent until you have dealt with nothing for five seconds; a panel
 * at the top left of the game view shows its name and health bar, as lifepoints out of its most (an npc's
 * from NpcHitpoints, a player's from the hiscores), as a percentage, or both. An npc's Attack entry is
 * marked with a `*` while it is your opponent or fights you, and a player you deal with can be compared
 * with you, their combat levels against yours, in a panel at the bottom left.
 *
 * RuneLite's boss health bar (the game's own, which hides the panel, and the text it adds to it) has no
 * counterpart in 634. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md); the drawing is in
 * OpponentInfoOverlay and PlayerComparisonOverlay.
 */
object OpponentInfo : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("opponentinfo", "Opponent Information", "1", PluginApi.VERSION, "Show name and hitpoints information about the NPC you are fighting", "Pixel")

	/** RuneLite's HitpointsDisplayStyle. */
	@JvmField val STYLES: Array<String> = arrayOf("Hitpoints", "Percentage", "Both")

	const val STYLE_HITPOINTS: Int = 0
	const val STYLE_PERCENTAGE: Int = 1
	const val STYLE_BOTH: Int = 2

	/** RuneLite's WAIT: how long after you stop dealing with it an opponent is forgotten. */
	const val WAIT_MILLIS: Long = 5_000L

	@JvmField var enabled: Boolean = true
	@JvmField var lookupOnInteraction: Boolean = false
	@JvmField var hitpointsDisplayStyle: Int = STYLE_HITPOINTS
	@JvmField var showOpponentsInMenu: Boolean = false
	@JvmField var showOpponentHealthOverlay: Boolean = true

	/** The opponent, and when you last stopped dealing with anything (0 for not yet): RuneLite's lastOpponent and lastTime. */
	@JvmField var lastOpponent: Actor? = null
	@JvmField var lastTime: Long = 0L

	/** The clock, the wall's; a test sets its own. */
	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	private var context: PluginContext? = null

	private val interactingListener: EventListener<Actor> = EventListener { source -> context?.client()?.let { interactingChanged(it, source) } }

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.previous === GameState.LOGGED_IN) {
			reset()
		}
	}

	private val menuListener: EventListener<MenuBuilt> = EventListener { _ -> context?.client()?.let { markOpponents(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		if (!enabled) {
			reset()
		}
		context?.client()?.consolePrint(if (enabled) "Opponent information enabled." else "Opponent information disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Opponent information", true)
		// RuneLite's OpponentInfoConfig, in its order
		s.toggle("lookupOnInteraction", "Lookup players on interaction", false)
		s.choice("hitpointsDisplayStyle", "Display style", STYLES[STYLE_HITPOINTS], STYLES)
		s.toggle("showOpponentsInMenu", "Show opponents in menu", false)
		s.toggle("showOpponentHealthOverlay", "Show opponent health overlay", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.INTERACTING_CHANGED, interactingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.MENU_BUILT, menuListener)
		context.addOverlay(OpponentInfoOverlay.panel)
		context.addOverlay(PlayerComparisonOverlay.panel)
		context.registerCommand("opponentinfo", toggle)
	}

	override fun stop() {
		context = null
		reset()
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		lookupOnInteraction = config.getBoolean("lookupOnInteraction", false)
		val style: Int = STYLES.indexOf(config.get("hitpointsDisplayStyle", STYLES[STYLE_HITPOINTS]))
		hitpointsDisplayStyle = if (style < 0) STYLE_HITPOINTS else style
		showOpponentsInMenu = config.getBoolean("showOpponentsInMenu", false)
		showOpponentHealthOverlay = config.getBoolean("showOpponentHealthOverlay", true)
	}

	@JvmStatic
	fun context(): PluginContext? = context

	/** RuneLite's shutDown and logout: no opponent, and the panel's last values forgotten. */
	@JvmStatic
	fun reset() {
		lastOpponent = null
		lastTime = 0L
		OpponentInfoOverlay.reset()
	}

	/** RuneLite's onInteractingChanged: only your own dealings make an opponent; dealing with nothing starts the wait. */
	@JvmStatic
	fun interactingChanged(client: ClientApi, source: Actor) {
		if (!enabled || source !== client.localPlayer()) {
			return
		}
		val target: Actor? = source.interacting()
		if (target == null) {
			lastTime = clock()
			return
		}
		lastOpponent = target
	}

	/** RuneLite's onGameTick: the opponent goes once you have dealt with nothing for more than [WAIT_MILLIS]. */
	@JvmStatic
	fun tick(client: ClientApi) {
		if (lastOpponent != null && lastTime != 0L && client.localPlayer()?.interacting() == null && clock() - lastTime > WAIT_MILLIS) {
			lastOpponent = null
		}
	}

	/** RuneLite's onMenuEntryAdded: an npc's Attack entry gets a `*` before its name while the npc fights you or is your opponent. */
	@JvmStatic
	fun markOpponents(client: ClientApi) {
		if (!enabled || !showOpponentsInMenu) {
			return
		}
		val me: Player = client.localPlayer() ?: return
		for (i in 0 until client.menuEntryCount()) {
			val entry: MenuEntry = client.menuEntry(i) ?: continue
			val npc: Npc? = attacked(client, entry)
			if (npc != null && (npc.interacting() === me || lastOpponent === npc)) {
				client.retargetMenuEntry(i, "*" + (entry.target() ?: ""))
			}
		}
	}

	/** The npc an Attack entry is on, or null for any other entry. */
	private fun attacked(client: ClientApi, entry: MenuEntry): Npc? {
		if (!NpcIndicators.isNpcEntry(entry.clickType()) || removeTags(entry.option() ?: "") != "Attack") {
			return null
		}
		return client.npcByIndex(entry.entityUid().toInt())
	}

	/** RuneLite's Text.removeTags: colour and image tags out of a name. */
	@JvmStatic
	fun removeTags(text: String): String = TAGS.replace(text, "")

	private val TAGS: Regex = Regex("<[^>]*>")

	/** The name to show for [actor], tags removed; null for an actor without one. */
	@JvmStatic
	fun nameOf(actor: Actor): String? {
		val name: String? = when (actor) {
			is Npc -> actor.name()
			is Player -> actor.name()
			else -> null
		}
		return name?.let { removeTags(it) }
	}

	/** [actor]'s most lifepoints, if known: an npc's from NpcHitpoints, a player's Constitution level (from the hiscores) times ten. */
	@JvmStatic
	fun maxHealthOf(actor: Actor): Int? {
		if (actor is Npc) {
			return NpcHitpoints.hitpoints(actor.typeId()) ?: actor.type()?.id()?.let { NpcHitpoints.hitpoints(it) }
		}
		if (actor is Player) {
			val name: String = nameOf(actor) ?: return null
			val level: Int = HiscoreLookup.lookup(name)?.levels?.get(Skill.CONSTITUTION) ?: return null
			return if (level > 0) level * 10 else null
		}
		return null
	}
}
