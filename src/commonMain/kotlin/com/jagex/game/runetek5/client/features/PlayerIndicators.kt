package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Image
import com.jagex.game.runetek5.plugin.api.MenuEntry
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.MenuBuilt
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Player Indicators. Players are coloured by what they are to
 * you (yourself, a friend, a member of your clan chat, your team, anyone else): their names over them, a
 * tile under them, their names on the minimap and their right-click entries, with the clan chat rank's
 * icon (the game's own rank sprites, ClientApi.spriteImage) before a member's name.
 *
 * 634 has one chat channel of this kind, its Clan Chat, which OSRS later renamed the friends chat: the
 * plugin's friends chat settings (RuneLite's keys) are labelled as 634 names it. The clans OSRS added in
 * 2021 (RuneLite's clan member settings) and RuneLite's party do not exist here. A highlight set to PvP
 * colours only players who can be attacked where they stand. A built-in plugin (BuiltinPlugins,
 * docs/PLUGINS.md).
 */
object PlayerIndicators : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("playerindicators", "Player Indicators", "1", PluginApi.VERSION, "Colours friends, clan chat members, team mates and others by name, tile, minimap and menu.", "Pixel")

	/** RuneLite's HighlightSetting. */
	@JvmField
	val HIGHLIGHT_SETTINGS: Array<String> = arrayOf("Disabled", "Enabled", "PvP")

	const val DISABLED: Int = 0
	const val ENABLED: Int = 1
	const val PVP: Int = 2

	/** RuneLite's PlayerNameLocation. */
	@JvmField
	val NAME_POSITIONS: Array<String> = arrayOf("Disabled", "Above head", "Center of model", "Right of model")

	const val NAME_DISABLED: Int = 0
	const val ABOVE_HEAD: Int = 1
	const val MODEL_CENTER: Int = 2
	const val MODEL_RIGHT: Int = 3

	/** What a player is to you, in RuneLite's order of precedence. */
	const val NONE: Int = -1
	const val SELF: Int = 0
	const val FRIEND: Int = 1
	const val FRIENDS_CHAT: Int = 2
	const val TEAM: Int = 3
	const val OTHER: Int = 4

	/** The clan chat rank sprites (NameVisitor ranks 0 friend .. 7 owner): smiley, one to three chevrons, bronze, silver and gold stars, key. */
	@JvmField
	val RANK_SPRITES: IntArray = intArrayOf(1004, 1011, 1010, 1005, 1009, 1008, 1007, 1006)

	/** The client's click types of a player's own entries (MiniMenuAction OPPLAYER1..8, TGT_PLAYER), the attack penalty added above them. */
	private val PLAYER_CLICK_TYPES: IntArray = intArrayOf(11, 17, 45, 23, 48, 8, 57, 58, 51)

	private const val LEVEL_PENALTY: Int = 2000

	@JvmField var enabled: Boolean = false

	/** Highlight settings by what the player is (SELF .. OTHER), and their colours. */
	@JvmField val highlight: IntArray = intArrayOf(DISABLED, ENABLED, ENABLED, ENABLED, DISABLED)
	@JvmField val colours: IntArray = intArrayOf(OWN, FRIEND_COLOUR, FRIENDS_CHAT_COLOUR, TEAM_COLOUR, OTHER_COLOUR)

	@JvmField var drawPlayerTiles: Boolean = false
	@JvmField var playerNamePosition: Int = ABOVE_HEAD
	@JvmField var drawMinimapNames: Boolean = false
	@JvmField var colorPlayerMenu: Boolean = true
	@JvmField var clanMenuIcons: Boolean = true

	/** For the smoke test: the names the last frame drew. */
	@JvmField var drawnLastFrame: Int = 0

	/** For the smoke test: where (in the game view) the last name drawn above a head went, -1 for none. */
	@JvmField var lastAboveHeadY: Int = -1

	/** The clan chat's ranks by name, read again each game tick. */
	private val ranks: HashMap<String, Int> = HashMap()

	private var ranksTick: Int = -1

	/** The rank icons' `<img=N>` numbers, -1 until added (the game's icons must be loaded first). */
	private val rankIcons: IntArray = IntArray(RANK_SPRITES.size) { -1 }

	private val xs: IntArray = IntArray(16)
	private val ys: IntArray = IntArray(16)
	private val point: IntArray = IntArray(2)
	private val area: IntArray = IntArray(4)

	private var context: PluginContext? = null

	/** RuneLite's PlayerIndicatorsOverlay and PlayerIndicatorsTileOverlay, under the interfaces. */
	private val overlay: Overlay = Overlay { g, view -> context?.client()?.let { draw(g, view, it) } }

	/** RuneLite's PlayerIndicatorsMinimapOverlay, over the minimap. */
	private val frameListener: EventListener<FrameDraw> = EventListener { event -> context?.client()?.let { drawMinimap(event.graphics, it) } }

	private val menuListener: EventListener<MenuBuilt> = EventListener { _ -> context?.client()?.let { menuBuilt(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		context?.client()?.consolePrint(if (enabled) "Player indicators on." else "Player indicators off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Player Indicators", false)
		// RuneLite's PlayerIndicatorsConfig, in its order: the Highlight options section first. 634's Clan Chat is
		// RuneLite's friends chat (its keys), and OSRS's 2021 clans have no settings here
		s.choice("highlightSelf", "Highlight own player", HIGHLIGHT_SETTINGS[DISABLED], HIGHLIGHT_SETTINGS)
		s.colour("ownNameColor", "Own player", OWN, false)
		s.choice("highlightFriends", "Highlight friends", HIGHLIGHT_SETTINGS[ENABLED], HIGHLIGHT_SETTINGS)
		s.colour("friendNameColor", "Friend", FRIEND_COLOUR, false)
		s.choice("highlightFriendsChat", "Highlight clan chat members", HIGHLIGHT_SETTINGS[ENABLED], HIGHLIGHT_SETTINGS)
		s.colour("clanMemberColor", "Clan chat", FRIENDS_CHAT_COLOUR, false)
		s.choice("highlightTeamMembers", "Highlight team members", HIGHLIGHT_SETTINGS[ENABLED], HIGHLIGHT_SETTINGS)
		s.colour("teamMemberColor", "Team member", TEAM_COLOUR, false)
		s.choice("highlightOthers", "Highlight others", HIGHLIGHT_SETTINGS[DISABLED], HIGHLIGHT_SETTINGS)
		s.colour("nonClanMemberColor", "Others", OTHER_COLOUR, false)
		s.toggle("drawPlayerTiles", "Draw tiles under players", false)
		s.choice("playerNamePosition", "Name position", NAME_POSITIONS[ABOVE_HEAD], NAME_POSITIONS)
		s.toggle("drawMinimapNames", "Draw names on minimap", false)
		s.toggle("colorPlayerMenu", "Colorize player menu", true)
		s.toggle("clanMenuIcons", "Show clan chat ranks", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.events().subscribe(Events.MENU_BUILT, menuListener)
		context.addOverlay(overlay)
		context.registerCommand("playerindicators", toggle)
	}

	override fun stop() {
		context = null
		ranks.clear()
		ranksTick = -1
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		highlight[SELF] = choice(config, "highlightSelf", HIGHLIGHT_SETTINGS, DISABLED)
		colours[SELF] = config.getColour("ownNameColor", OWN)
		highlight[FRIEND] = choice(config, "highlightFriends", HIGHLIGHT_SETTINGS, ENABLED)
		colours[FRIEND] = config.getColour("friendNameColor", FRIEND_COLOUR)
		highlight[FRIENDS_CHAT] = choice(config, "highlightFriendsChat", HIGHLIGHT_SETTINGS, ENABLED)
		colours[FRIENDS_CHAT] = config.getColour("clanMemberColor", FRIENDS_CHAT_COLOUR)
		highlight[TEAM] = choice(config, "highlightTeamMembers", HIGHLIGHT_SETTINGS, ENABLED)
		colours[TEAM] = config.getColour("teamMemberColor", TEAM_COLOUR)
		highlight[OTHER] = choice(config, "highlightOthers", HIGHLIGHT_SETTINGS, DISABLED)
		colours[OTHER] = config.getColour("nonClanMemberColor", OTHER_COLOUR)
		drawPlayerTiles = config.getBoolean("drawPlayerTiles", false)
		playerNamePosition = choice(config, "playerNamePosition", NAME_POSITIONS, ABOVE_HEAD)
		drawMinimapNames = config.getBoolean("drawMinimapNames", false)
		colorPlayerMenu = config.getBoolean("colorPlayerMenu", true)
		clanMenuIcons = config.getBoolean("clanMenuIcons", true)
	}

	private fun choice(config: PluginConfig, key: String, options: Array<String>, default: Int): Int {
		val i: Int = options.indexOf(config.get(key, options[default]))
		return if (i < 0) default else i
	}

	/** A highlight setting on for this player: Enabled always, PvP only for one who can be attacked here. */
	@JvmStatic
	fun applies(setting: Int, player: Player): Boolean {
		return setting == ENABLED || (setting == PVP && player.isAttackable())
	}

	/**
	 * RuneLite's PlayerIndicatorsService.forEachPlayer: what [player] is to you, the first of yourself,
	 * friend, clan chat, team and others whose highlight is on, or NONE.
	 */
	@JvmStatic
	fun relation(client: ClientApi, player: Player): Int {
		if (player.isLocalPlayer()) {
			return if (applies(highlight[SELF], player)) SELF else NONE
		}
		val name: String? = player.name()
		val friend: Boolean = client.isFriend(name)
		val inChat: Boolean = client.isInFriendsChat(name)
		val team: Int = player.teamId()
		val ownTeam: Int = client.localPlayer()?.teamId() ?: 0
		return when {
			friend && applies(highlight[FRIEND], player) -> FRIEND
			inChat && applies(highlight[FRIENDS_CHAT], player) -> FRIENDS_CHAT
			team > 0 && team == ownTeam && applies(highlight[TEAM], player) -> TEAM
			!friend && !inChat && applies(highlight[OTHER], player) -> OTHER
			else -> NONE
		}
	}

	/** [name]'s clan chat rank (0 friend .. 7 owner), -1 when not ranked or not in the chat. */
	@JvmStatic
	fun rank(client: ClientApi, name: String?): Int {
		if (name == null) {
			return -1
		}
		val tick: Int = client.serverTick() / CYCLES_PER_TICK
		if (tick != ranksTick) {
			ranksTick = tick
			ranks.clear()
			client.clanMembers { member, _, rank -> if (member != null && rank >= 0) ranks[normal(member)] = rank }
		}
		return ranks[normal(name)] ?: -1
	}

	private fun normal(name: String): String = name.replace(' ', ' ').lowercase()

	/** The rank's icon as `<img=N>`, or "" when it cannot be had (yet). */
	@JvmStatic
	fun rankIcon(client: ClientApi, rank: Int): String {
		if (rank < 0 || rank >= RANK_SPRITES.size) {
			return ""
		}
		if (rankIcons[rank] < 0) {
			val image: Image = client.spriteImage(RANK_SPRITES[rank]) ?: return ""
			rankIcons[rank] = client.addChatIcon(image)
		}
		return if (rankIcons[rank] < 0) "" else "<img=" + rankIcons[rank] + ">"
	}

	private fun draw(g: Graphics, view: Viewport, client: ClientApi) {
		drawnLastFrame = 0
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		for (i in 0 until client.playerCount()) {
			val player: Player? = client.player(i)
			if (player != null && player.isDrawnThisFrame()) {
				drawPlayer(g, view, client, player)
			}
		}
	}

	private fun drawPlayer(g: Graphics, view: Viewport, client: ClientApi, player: Player) {
		val relation: Int = relation(client, player)
		if (relation == NONE) {
			return
		}
		val colour: Int = colours[relation]
		if (drawPlayerTiles) {
			val fill: Int = (TILE_FILL_ALPHA shl 24) or (colour and 0xFFFFFF)
			TileOutline.draw(g, view, client, player.tileX(), player.tileY(), player.level(), player.sizeInTiles(), colour, fill, 2, xs, ys)
		}
		if (playerNamePosition != NAME_DISABLED && g.fontReady() && drawName(g, view, client, player, relation, colour)) {
			drawnLastFrame++
		}
	}

	/** RuneLite's PlayerIndicatorsOverlay.renderPlayerOverlay: the name where the setting puts it, the rank's icon before a chat member's. */
	private fun drawName(g: Graphics, view: Viewport, client: ClientApi, player: Player, relation: Int, colour: Int): Boolean {
		val name: String = player.displayName() ?: return false
		var x: Int
		var y: Int
		when (playerNamePosition) {
			MODEL_CENTER -> {
				if (!client.projectActor(player, player.overheadHeight() / 2, point)) {
					return false
				}
				x = view.x + point[0]
				y = view.y + point[1]
			}
			MODEL_RIGHT -> {
				val count: Int = client.actorHull(player, xs, ys)
				if (count < 3 || !client.projectActor(player, player.overheadHeight() / 2, point)) {
					return false
				}
				var right: Int = xs[0]
				for (c in 1 until count) {
					right = maxOf(right, xs[c])
				}
				x = view.x + right + g.textWidth(name) / 2 + RIGHT_GAP
				y = view.y + point[1]
			}
			else -> {
				if (!client.projectActor(player, player.overheadHeight() + ABOVE_RAISE, point)) {
					return false
				}
				x = view.x + point[0]
				y = view.y + point[1]
				// above the health bar, the head icons and the chat line, where the client drew those
				val top: Int = player.overheadTop()
				if (top >= 0) {
					y = view.y + top - ABOVE_GAP
				}
				lastAboveHeadY = y - view.y
			}
		}
		val rank: Int = if (relation == FRIENDS_CHAT && clanMenuIcons) rank(client, player.name()) else -1
		if (rank >= 0 && rank < RANK_SPRITES.size) {
			val sprite: Int = RANK_SPRITES[rank]
			val left: Int = x - g.textWidth(name) / 2 - g.spriteWidth(sprite) - ICON_GAP
			g.sprite(sprite, left, y - g.fontHeight() / 2 - g.spriteHeight(sprite) / 2)
		}
		g.textCentered(x, y, name, colour, SHADOW)
		return true
	}

	/** RuneLite's PlayerIndicatorsMinimapOverlay: each highlighted player's name over their dot. */
	private fun drawMinimap(g: Graphics, client: ClientApi) {
		if (!enabled || !drawMinimapNames || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN || !client.minimapArea(area)) {
			return
		}
		g.clip(area[0], area[1], area[2], area[3])
		for (i in 0 until client.playerCount()) {
			val player: Player = client.player(i) ?: continue
			val relation: Int = relation(client, player)
			val name: String? = player.displayName()
			if (relation != NONE && name != null && client.projectActorMinimap(player, point)) {
				g.textCentered(point[0], point[1] - 2, name, colours[relation], SHADOW)
			}
		}
		g.unclip()
	}

	@JvmStatic
	fun isPlayerEntry(clickType: Int): Boolean {
		for (type in PLAYER_CLICK_TYPES) {
			if (clickType == type || clickType == type + LEVEL_PENALTY) {
				return true
			}
		}
		return false
	}

	/** RuneLite's PlayerIndicatorsPlugin.onMenuEntryAdded, on the finished menu: a highlighted player's name coloured, a chat member's rank icon before it. */
	@JvmStatic
	fun menuBuilt(client: ClientApi) {
		if (!enabled || (!colorPlayerMenu && !clanMenuIcons)) {
			return
		}
		for (i in 0 until client.menuEntryCount()) {
			val entry: MenuEntry? = client.menuEntry(i)
			if (entry != null && isPlayerEntry(entry.clickType())) {
				retarget(client, i, entry)
			}
		}
	}

	private fun retarget(client: ClientApi, i: Int, entry: MenuEntry) {
		val player: Player = client.playerByIndex(entry.entityUid().toInt()) ?: return
		val relation: Int = relation(client, player)
		val target: String = entry.target() ?: return
		if (relation == NONE) {
			return
		}
		val icon: String = if (relation == FRIENDS_CHAT && clanMenuIcons) rankIcon(client, rank(client, player.name())) else ""
		client.retargetMenuEntry(i, menuTarget(target, if (colorPlayerMenu) colours[relation] else 0, icon))
	}

	/**
	 * A player entry's target `<col=ffffff>Name<col=..>  (level-N)` with the name in [argb] (0 leaves it) and
	 * [icon] before it.
	 */
	@JvmStatic
	fun menuTarget(target: String, argb: Int, icon: String): String {
		val coloured: String = if (argb == 0) target else NpcIndicators.recolourTarget(target, NpcIndicators.MENU_NAME, argb)
		return icon + coloured
	}

	private const val CYCLES_PER_TICK: Int = 30
	private const val ABOVE_RAISE: Int = 40
	private const val ABOVE_GAP: Int = 4
	private const val RIGHT_GAP: Int = 4
	private const val ICON_GAP: Int = 2
	private const val TILE_FILL_ALPHA: Int = 50

	private const val SHADOW: Int = 0xFF000000.toInt()
	private const val OWN: Int = 0xFF00B8D4.toInt()
	private const val FRIEND_COLOUR: Int = 0xFF00C853.toInt()
	private const val FRIENDS_CHAT_COLOUR: Int = 0xFFAA00FF.toInt()
	private const val TEAM_COLOUR: Int = 0xFF136EF7.toInt()
	private const val OTHER_COLOUR: Int = 0xFFFF0000.toInt()
}
