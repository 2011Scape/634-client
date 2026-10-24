package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.HoveredTargets
import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.HoveredTarget
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.NpcDefinition
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.MenuOptionClicked
import com.jagex.game.runetek5.plugin.api.events.ServerTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Interact Highlight. The npc or location a left click would
 * act on is outlined by its own silhouette (ClientApi.actorOutline and locOutline, API 36 and 10), in the
 * hover colour, an npc that can be attacked in the attack colour; the npc you are fighting or talking to
 * and the location you last clicked are outlined in the interact colours until you are done with them.
 * Players can be outlined too, off at first. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object InteractHighlight : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("interacthighlight", "Interact Highlight", "1", PluginApi.VERSION, "Outlines the npc or object under your mouse, and the one you are interacting with, by its own shape.", "Pixel")

	/** RuneLite's InteractHighlightConfig colours: cyan to hover, yellow to attack, red once interacting. */
	const val HOVER_COLOUR: Int = 0x9000FFFF.toInt()
	const val ATTACK_HOVER_COLOUR: Int = 0x90FFFF00.toInt()
	const val INTERACT_COLOUR: Int = 0x90FF0000.toInt()
	const val ATTACK_INTERACT_COLOUR: Int = 0x90FFFF00.toInt()

	/** Server ticks a clicked location stays outlined once you stand still with nothing to walk to. */
	const val IDLE_TICKS: Int = 2

	@JvmField var enabled: Boolean = true
	@JvmField var showHover: Boolean = true
	@JvmField var showInteract: Boolean = true
	@JvmField var npcs: Boolean = true
	@JvmField var objects: Boolean = true
	@JvmField var players: Boolean = false
	@JvmField var hoverColour: Int = HOVER_COLOUR
	@JvmField var attackHoverColour: Int = ATTACK_HOVER_COLOUR
	@JvmField var objectHoverColour: Int = HOVER_COLOUR
	@JvmField var interactColour: Int = INTERACT_COLOUR
	@JvmField var attackInteractColour: Int = ATTACK_INTERACT_COLOUR
	@JvmField var objectInteractColour: Int = INTERACT_COLOUR
	@JvmField var borderWidth: Int = 4
	@JvmField var feather: Int = 4

	/** The location last clicked (scene tile, id; -1 for none) and how many ticks you have stood idle since. */
	@JvmField var clickedLocX: Int = -1
	@JvmField var clickedLocY: Int = -1
	@JvmField var clickedLocId: Int = -1
	@JvmField var idleTicks: Int = 0

	/** For the smoke test: what was outlined in the last frame drawn (an npc index, a location id; -1 for none). */
	@JvmField var lastNpc: Int = -1
	@JvmField var lastLoc: Int = -1
	@JvmField var lastPlayer: Int = -1

	/** For the smoke test: what to treat as hovered in place of the mouse (null for the mouse). */
	@JvmField var testHover: HoveredTarget? = null

	private var context: PluginContext? = null

	private val hovered: HoveredTarget = HoveredTarget()

	private val clicked: HoveredTarget = HoveredTarget()

	private val overlay: Overlay = Overlay { _, _ -> context?.client()?.let { draw(it) } }

	private val clickListener: EventListener<MenuOptionClicked> = EventListener { event ->
		if (HoveredTargets.decode(event.clickType, event.param0, event.param1, event.entityUid, event.option, clicked) && clicked.kind === MenuTarget.LOC) {
			clickedLocX = clicked.sceneX
			clickedLocY = clicked.sceneY
			clickedLocId = clicked.id
		} else {
			clickedLocId = -1 // a click on anything else (a walk, an npc) is done with the location
		}
		idleTicks = 0
	}

	private val tickListener: EventListener<ServerTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		val context: PluginContext = this.context ?: return@CommandHandler
		enabled = !enabled
		context.config().setBoolean("enabled", enabled)
		context.config().save()
		context.client().consolePrint(if (enabled) "Interact Highlight on." else "Interact Highlight off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's InteractHighlightConfig, with players added
		s.toggle("enabled", "Interact Highlight", true)
		s.toggle("showHover", "Show on hover", true)
		s.toggle("showInteract", "Show on interact", true)
		s.toggle("npcs", "Npcs", true)
		s.toggle("objects", "Objects", true)
		s.toggle("players", "Players", false)
		s.colour("hoverColour", "Npc hover", HOVER_COLOUR, true)
		s.colour("attackHoverColour", "Npc attack hover", ATTACK_HOVER_COLOUR, true)
		s.colour("objectHoverColour", "Object hover", HOVER_COLOUR, true)
		s.colour("interactColour", "Npc interact", INTERACT_COLOUR, true)
		s.colour("attackInteractColour", "Npc attack", ATTACK_INTERACT_COLOUR, true)
		s.colour("objectInteractColour", "Object interact", INTERACT_COLOUR, true)
		s.number("borderWidth", "Border width", 4, 1, 16, 1)
		s.number("feather", "Outline feather", 4, 0, 4, 1)
		readSettings(context.config())
		clickedLocId = -1
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_OPTION_CLICKED, clickListener)
		context.events().subscribe(Events.SERVER_TICK, tickListener)
		context.addOverlay(overlay)
		context.registerCommand("interacthighlight", toggle)
	}

	override fun stop() {
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		showHover = config.getBoolean("showHover", true)
		showInteract = config.getBoolean("showInteract", true)
		npcs = config.getBoolean("npcs", true)
		objects = config.getBoolean("objects", true)
		players = config.getBoolean("players", false)
		hoverColour = config.getColour("hoverColour", HOVER_COLOUR)
		attackHoverColour = config.getColour("attackHoverColour", ATTACK_HOVER_COLOUR)
		objectHoverColour = config.getColour("objectHoverColour", HOVER_COLOUR)
		interactColour = config.getColour("interactColour", INTERACT_COLOUR)
		attackInteractColour = config.getColour("attackInteractColour", ATTACK_INTERACT_COLOUR)
		objectInteractColour = config.getColour("objectInteractColour", INTERACT_COLOUR)
		borderWidth = config.getInt("borderWidth", 4).coerceIn(1, 16)
		feather = config.getInt("feather", 4).coerceIn(0, 4)
	}

	/**
	 * RuneLite's rule for a clicked location: it stays outlined while you walk to it and work on it, and is
	 * let go once you have stood idle (no walk destination, no animation) for [IDLE_TICKS] ticks.
	 */
	@JvmStatic
	fun idle(walking: Boolean, animating: Boolean): Boolean {
		if (walking || animating) {
			idleTicks = 0
			return false
		}
		idleTicks++
		if (idleTicks >= IDLE_TICKS) {
			clickedLocId = -1
		}
		return true
	}

	private fun tick(client: ClientApi) {
		if (clickedLocId < 0) {
			return
		}
		val player: Player = client.localPlayer() ?: return
		idle(client.destinationTileX() >= 0, player.animationId() >= 0)
	}

	/** Whether npc [npc] offers Attack: its outlines take the attack colours. */
	@JvmStatic
	fun attackable(npc: Npc): Boolean {
		val type: NpcDefinition = npc.type() ?: return false
		for (i in 0 until 5) {
			if ("Attack".equals(type.action(i), true)) {
				return true
			}
		}
		return false
	}

	private fun draw(client: ClientApi) {
		lastNpc = -1
		lastLoc = -1
		lastPlayer = -1
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val me: Player = client.localPlayer() ?: return
		var hoveredActor: Actor? = null
		var hoveredLoc: Int = -1
		if (showHover && hover(client)) {
			when (hovered.kind) {
				MenuTarget.NPC -> if (npcs) {
					val npc: Npc? = client.npcByIndex(hovered.id)
					if (npc != null) {
						hoveredActor = npc
						outlineNpc(client, npc, if ("Attack".equals(hovered.option, true)) attackHoverColour else hoverColour)
					}
				}
				MenuTarget.PLAYER -> if (players) {
					val player: Player? = client.playerByIndex(hovered.id)
					if (player != null) {
						hoveredActor = player
						if (client.actorOutline(player, borderWidth, feather, hoverColour)) {
							lastPlayer = hovered.id
						}
					}
				}
				MenuTarget.LOC -> if (objects) {
					hoveredLoc = hovered.id
					if (client.locOutline(hovered.sceneX, hovered.sceneY, me.level(), hovered.id, borderWidth, feather, objectHoverColour)) {
						lastLoc = hovered.id
					}
				}
				else -> {}
			}
		}
		if (!showInteract) {
			return
		}
		// what you are interacting with, unless it is the one hovered (hover wins)
		val target: Actor? = me.interacting()
		if (npcs && target is Npc && target !== hoveredActor) {
			outlineNpc(client, target, if (attackable(target)) attackInteractColour else interactColour)
		}
		if (objects && clickedLocId >= 0 && !(hoveredLoc == clickedLocId && hovered.sceneX == clickedLocX && hovered.sceneY == clickedLocY)) {
			if (client.locOutline(clickedLocX, clickedLocY, me.level(), clickedLocId, borderWidth, feather, objectInteractColour)) {
				lastLoc = clickedLocId
			}
		}
	}

	/** What a left click would act on, or the smoke test's stand-in for it ([testHover]). */
	private fun hover(client: ClientApi): Boolean {
		val test: HoveredTarget = testHover ?: return client.hoveredTarget(hovered)
		hovered.kind = test.kind
		hovered.id = test.id
		hovered.sceneX = test.sceneX
		hovered.sceneY = test.sceneY
		hovered.option = test.option
		return true
	}

	private fun outlineNpc(client: ClientApi, npc: Npc, argb: Int): Boolean {
		val drawn: Boolean = client.actorOutline(npc, borderWidth, feather, argb)
		if (drawn) {
			lastNpc = npc.index()
		}
		return drawn
	}
}
