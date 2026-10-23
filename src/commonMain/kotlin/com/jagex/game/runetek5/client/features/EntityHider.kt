package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ActorFilter
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.NpcDefinition
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.ProjectileFilter
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Entity Hider. Hides other players (all of them, or your friends,
 * your clan chat's members or the players you ignore), yourself, NPCs, other players' pets and familiars, whoever
 * is attacking you, projectiles, dying NPCs and the random event NPCs that come to you; the models and their
 * overheads, hitsplats and health bars (the "2D" settings) can be hidden apart. The client asks the plugin's
 * filters (PluginContext.addActorFilter and addProjectileFilter, API 32) as it builds each frame's scene.
 *
 * 634's Clan Chat is RuneLite's friends chat; OSRS's clans, party members and thralls are not in 634. A pet or a
 * familiar is an NPC whose menu has "Interact" (634's followers), and yours is the one whose chathead the
 * follower details show (Void's varp 1174). The random events are Void's that send an NPC to you: the Sandwich
 * Lady, Giles (the Certer), the frog herald and the Mysterious Old Man; the rest take you away. A built-in plugin
 * (BuiltinPlugins, docs/PLUGINS.md).
 */
object EntityHider : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("entityhider", "Entity Hider", "1", PluginApi.VERSION, "Hides players, NPCs, pets, attackers, projectiles and the like, and their overheads.", "Pixel")

	/** Void's follower details: the follower's chathead, its npc. */
	const val FOLLOWER_VARP: Int = 1174

	/** A follower's menu entry in 634. */
	const val INTERACT: String = "Interact"

	/** Void's random event NPCs that come to the player: Sandwich Lady, Giles, the frog herald, the Mysterious Old Man. */
	@JvmField
	val RANDOM_EVENT_NPCS: IntArray = intArrayOf(8629, 2538, 2472, 410)

	@JvmField var hideOthers: Boolean = false
	@JvmField var hideOthers2D: Boolean = false
	@JvmField var hideFriends: Boolean = false
	@JvmField var hideFriendsChatMembers: Boolean = false
	@JvmField var hideIgnores: Boolean = false
	@JvmField var hideLocalPlayer: Boolean = false
	@JvmField var hideLocalPlayer2D: Boolean = false
	@JvmField var hideNPCs: Boolean = false
	@JvmField var hideNPCs2D: Boolean = false
	@JvmField var hidePets: Boolean = false
	@JvmField var hideAttackers: Boolean = false
	@JvmField var hideProjectiles: Boolean = false
	@JvmField var hideDeadNpcs: Boolean = false
	@JvmField var hideRandomEvents: Boolean = false

	private var context: PluginContext? = null

	private val actorFilter: ActorFilter = ActorFilter { actor, drawingUI -> context?.client()?.let { shouldDraw(it, actor, drawingUI) } ?: true }

	private val projectileFilter: ProjectileFilter = ProjectileFilter { _ -> !hideProjectiles }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		hideOthers = !hideOthers
		context?.config()?.setBoolean("hidePlayers", hideOthers)
		context?.config()?.save()
		context?.client()?.consolePrint(if (hideOthers) "Other players hidden." else "Other players shown.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's EntityHiderConfig, in its order, but OSRS's party members, clans and thralls
		s.toggle("hidePlayers", "Hide others", false)
		s.toggle("hidePlayers2D", "Hide others 2D", false)
		s.toggle("hideFriends", "Hide friends", false)
		s.toggle("hideClanMates", "Hide clan chat members", false)
		s.toggle("hideIgnores", "Hide ignores", false)
		s.toggle("hideLocalPlayer", "Hide local player", false)
		s.toggle("hideLocalPlayer2D", "Hide local player 2D", false)
		s.toggle("hideNPCs", "Hide NPCs", false)
		s.toggle("hideNPCs2D", "Hide NPCs 2D", false)
		s.toggle("hidePets", "Hide other players' pets", false)
		s.toggle("hideAttackers", "Hide attackers", false)
		s.toggle("hideProjectiles", "Hide projectiles", false)
		s.toggle("hideDeadNpcs", "Hide dead NPCs", false)
		s.toggle("hideRandomEvents", "Hide random events", false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.addActorFilter(actorFilter)
		context.addProjectileFilter(projectileFilter)
		context.registerCommand("entityhider", toggle)
	}

	override fun stop() {
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		hideOthers = config.getBoolean("hidePlayers", false)
		hideOthers2D = config.getBoolean("hidePlayers2D", false)
		hideFriends = config.getBoolean("hideFriends", false)
		hideFriendsChatMembers = config.getBoolean("hideClanMates", false)
		hideIgnores = config.getBoolean("hideIgnores", false)
		hideLocalPlayer = config.getBoolean("hideLocalPlayer", false)
		hideLocalPlayer2D = config.getBoolean("hideLocalPlayer2D", false)
		hideNPCs = config.getBoolean("hideNPCs", false)
		hideNPCs2D = config.getBoolean("hideNPCs2D", false)
		hidePets = config.getBoolean("hidePets", false)
		hideAttackers = config.getBoolean("hideAttackers", false)
		hideProjectiles = config.getBoolean("hideProjectiles", false)
		hideDeadNpcs = config.getBoolean("hideDeadNpcs", false)
		hideRandomEvents = config.getBoolean("hideRandomEvents", false)
	}

	/** RuneLite's EntityHiderPlugin.shouldDraw. */
	@JvmStatic
	fun shouldDraw(client: ClientApi, actor: Actor, drawingUI: Boolean): Boolean = when (actor) {
		is Player -> drawPlayer(client, actor, drawingUI)
		is Npc -> drawNpc(client, actor, drawingUI)
		else -> true
	}

	private fun drawPlayer(client: ClientApi, player: Player, drawingUI: Boolean): Boolean {
		val name: String = player.name() ?: return true
		val local: Player? = client.localPlayer()
		if (player === local || player.isLocalPlayer()) {
			return !(if (drawingUI) hideLocalPlayer2D else hideLocalPlayer)
		}
		if (hideAttackers && local != null && player.interacting() === local) {
			return false
		}
		if (client.isFriend(name)) {
			return !hideFriends
		}
		if (client.isInFriendsChat(name)) {
			return !hideFriendsChatMembers
		}
		if (client.isIgnored(name)) {
			return !hideIgnores
		}
		return !(if (drawingUI) hideOthers2D else hideOthers)
	}

	private fun drawNpc(client: ClientApi, npc: Npc, drawingUI: Boolean): Boolean {
		if (isOthersFollower(client, npc)) {
			return !hidePets
		}
		if (hideDeadNpcs && npc.isDying()) {
			return false
		}
		val local: Player? = client.localPlayer()
		if (hideAttackers && local != null && npc.interacting() === local) {
			return false
		}
		if (hideRandomEvents && npc.typeId() in RANDOM_EVENT_NPCS) {
			return false
		}
		return !(if (drawingUI) hideNPCs2D else hideNPCs)
	}

	/** A pet or familiar (an "Interact" in its menu) that is not the player's own follower. */
	@JvmStatic
	fun isOthersFollower(client: ClientApi, npc: Npc): Boolean {
		val type: NpcDefinition = npc.type() ?: return false
		var follower: Boolean = false
		for (i in 0 until ACTIONS) {
			if (type.action(i) == INTERACT) {
				follower = true
			}
		}
		return follower && npc.typeId() != client.varp(FOLLOWER_VARP)
	}

	private const val ACTIONS: Int = 5
}
