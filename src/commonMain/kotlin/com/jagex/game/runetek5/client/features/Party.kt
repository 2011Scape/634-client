package com.jagex.game.runetek5.client.features

import ClientCommandProcessor
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.OverlayMenuEntry
import com.jagex.game.runetek5.plugin.api.PanelComponent
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.MenuBuilt
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.ViewportDraw
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Party, kept on the server. The server (Void's content/social/party/Party)
 * holds the party and shows each member the others' hitpoints, prayer, special attack, run energy and tile in client
 * variables (party.varcs.toml, ids [BASE] to [BASE] + 60); the plugin reads them, draws the panel, the bars over the
 * members, their dots on the minimap and the world map and the pings, and sends only commands: `::party
 * invite|accept|decline|leave|kick <name>` (typed in the console, or from the panel's and the invite line's menus) and
 * `::ping x y level` (the ping hotkey held and a tile clicked). A member's tile is hidden while they are in a PvP area.
 *
 * Off by default, as RuneLite's. On a server without parties nothing is ever set, and nothing is shown. A built-in
 * plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object Party : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("party", "Party", "1", PluginApi.VERSION, "Shows your party's health, prayer, special attack, run energy and location, and lets you ping tiles for them.", "Pixel")

	/** The first of the server's party variables (party_meta). */
	const val BASE: Int = 2300
	const val MAX_MEMBERS: Int = 8
	const val FIELDS: Int = 7
	const val PING_TILE: Int = BASE + 1 + MAX_MEMBERS * FIELDS
	const val PING_SEQ: Int = PING_TILE + 1
	const val INVITE_HI: Int = PING_SEQ + 1
	const val INVITE_LO: Int = INVITE_HI + 1

	/** The line the server sends with an invite, in the request colour as a duel challenge's. */
	const val INVITE_TEXT: String = "wishes to join you in a party"

	const val ACCEPT_OPTION: String = "Accept party invite"

	/** How long a ping shows: 3 s of client cycles. */
	const val PING_CYCLES: Int = 150

	/** RuneLite plays a sound with a ping; the idle notifier's ding. */
	const val PING_SOUND: Int = 2266

	@JvmField var enabled: Boolean = false
	@JvmField var pings: Boolean = true
	@JvmField var sounds: Boolean = true
	@JvmField var recolorNames: Boolean = true
	@JvmField var pingHotkey: Int = KeyCode.CONTROL
	@JvmField var compactPanel: Boolean = false
	@JvmField var showOnMap: Boolean = true

	/** One member as the server shows them. A hidden tile (PvP) has [x] -1. */
	class Member(
		@JvmField val slot: Int,
		@JvmField val name: String,
		@JvmField val hp: Int,
		@JvmField val hpMax: Int,
		@JvmField val prayer: Int,
		@JvmField val prayerMax: Int,
		@JvmField val spec: Int,
		@JvmField val run: Int,
		@JvmField val x: Int,
		@JvmField val y: Int,
		@JvmField val level: Int,
		@JvmField val inPvp: Boolean,
	)

	/** The party as the variables say: [members] by slot, [local] and [leader] slots, [invite] the inviter or null. */
	class State(@JvmField val members: List<Member>, @JvmField val local: Int, @JvmField val leader: Int, @JvmField val invite: String?) {
		fun inParty(): Boolean = members.isNotEmpty()
	}

	@JvmField
	var state: State = State(emptyList(), -1, -1, null)

	private var context: PluginContext? = null
	private var lastPingSeq: Int = -1
	private var pingPrimed: Boolean = false
	private var pingX: Int = -1
	private var pingY: Int = -1
	private var pingLevel: Int = 0
	private var pingSlot: Int = 0
	private var pingCycle: Int = 0
	private var cycle: Int = 0
	private var pingAction: Int = -1
	private var acceptAction: Int = -1
	private var declineAction: Int = -1

	private val point: IntArray = IntArray(2)
	private val area: IntArray = IntArray(4)
	private val xs: IntArray = IntArray(4)
	private val ys: IntArray = IntArray(4)
	private val size: Size = Size()

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val tickListener: EventListener<GameTick> = EventListener { _ ->
		val client: ClientApi = context?.client() ?: return@EventListener
		cycle++
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			state = State(emptyList(), -1, -1, null)
			pingPrimed = false
			return@EventListener
		}
		state = read { client.varcInt(it) }
		pinged(client, client.varcInt(PING_SEQ), client.varcInt(PING_TILE))
	}

	private val viewportListener: EventListener<ViewportDraw> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		val g: Graphics = event.graphics
		if (enabled && state.inParty()) {
			drawPing(g, client)
		}
	}

	private val frameListener: EventListener<FrameDraw> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (enabled && showOnMap && state.inParty()) {
			val g: Graphics = event.graphics
			drawMinimap(g, client)
			drawWorldMap(g, client)
		}
	}

	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (!enabled) {
			return@EventListener
		}
		if (event.target === MenuTarget.TILE && pings && state.inParty() && pingAction >= 0 && client.isKeyHeld(pingHotkey)) {
			client.addMenuEntry("Ping", pingAction)
		} else if (event.target === MenuTarget.WIDGET && state.invite != null && acceptAction >= 0 && isInviteLine(client, event.widgetId, event.slot)) {
			client.addMenuEntry("Decline party invite", declineAction)
			client.addMenuEntry(ACCEPT_OPTION, acceptAction)
		}
	}

	/** An invite's line takes the left click as a duel challenge's does: Accept party invite becomes the top row. */
	private val menuBuiltListener: EventListener<MenuBuilt> = EventListener { _ ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (!enabled || state.invite == null) {
			return@EventListener
		}
		for (i in 0 until client.menuEntryCount()) {
			if (client.menuEntry(i)?.option() == ACCEPT_OPTION) {
				client.promoteMenuEntry(i)
				return@EventListener
			}
		}
	}

	/** RuneLite's party panel: the members with their bars, and the invite waiting. */
	@JvmField
	val panel: PanelOverlay = object : PanelOverlay("PartyPanel") {
		init {
			resizable = true
		}

		override fun menuEntries(): List<OverlayMenuEntry> {
			val entries: ArrayList<OverlayMenuEntry> = ArrayList()
			if (state.invite != null) {
				entries.add(OverlayMenuEntry("Accept", "Party invite", Task { command("party accept") }))
				entries.add(OverlayMenuEntry("Decline", "Party invite", Task { command("party decline") }))
			}
			if (state.inParty()) {
				entries.add(OverlayMenuEntry("Leave", "Party", Task { command("party leave") }))
			}
			entries.add(OverlayMenuEntry("Configure", "Party", Task { PluginManagerScreen.openSettings(DESCRIPTOR.id) }))
			return entries
		}

		override fun render(g: Graphics, view: Viewport): Size? = drawPanel(g, this)
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's PartyConfig, in its order, but the self colour (names are coloured by their hash), Discord and the
		// status over members' heads (the game's own health bars show there)
		s.toggle("enabled", "Party", false)
		s.toggle("pings", "Pings", true)
		s.toggle("sounds", "Sound on ping", true)
		s.toggle("recolorNames", "Recolor names", true)
		s.keybind("pingHotkey", "Ping hotkey", KeyCode.CONTROL)
		s.toggle("compactPanel", "Compact panel (one line a member)", false)
		s.toggle("showOnMap", "Show members on the maps", true)
		readSettings(context.config())
		pingAction = context.registerMenuAction(MenuAction { _, sceneX, sceneY, _ -> ping(sceneX, sceneY) })
		acceptAction = context.registerMenuAction(MenuAction { _, _, _, _ -> command("party accept") })
		declineAction = context.registerMenuAction(MenuAction { _, _, _, _ -> command("party decline") })
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.VIEWPORT_DRAW, viewportListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.events().subscribe(Events.MENU_BUILT, menuBuiltListener)
		context.addOverlay(panel)
	}

	override fun stop() {
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		pings = config.getBoolean("pings", true)
		sounds = config.getBoolean("sounds", true)
		recolorNames = config.getBoolean("recolorNames", true)
		pingHotkey = config.getInt("pingHotkey", KeyCode.CONTROL)
		compactPanel = config.getBoolean("compactPanel", false)
		showOnMap = config.getBoolean("showOnMap", true)
	}

	/** The party from the server's variables, [varc] reading one. */
	@JvmStatic
	fun read(varc: (Int) -> Int): State {
		val meta: Int = varc(BASE)
		val count: Int = meta and 0xF
		val invite: String? = name(varc(INVITE_HI), varc(INVITE_LO))
		if (count == 0) {
			return State(emptyList(), -1, -1, invite)
		}
		val members: ArrayList<Member> = ArrayList(count)
		for (slot in 0 until minOf(count, MAX_MEMBERS)) {
			val base: Int = BASE + 1 + slot * FIELDS
			val name: String = name(varc(base), varc(base + 1)) ?: continue
			val hp: Int = varc(base + 2)
			val prayer: Int = varc(base + 3)
			val energy: Int = varc(base + 4)
			val tile: Int = varc(base + 5)
			val hidden: Boolean = tile == -1 || tile == 0
			members.add(
				Member(
					slot, name, hp ushr 16, hp and 0xFFFF, prayer ushr 16, prayer and 0xFFFF, energy ushr 8 and 0xFF, energy and 0xFF,
					if (hidden) -1 else tile ushr 14 and 0x3FFF, if (hidden) -1 else tile and 0x3FFF, if (hidden) 0 else tile ushr 28 and 0x3,
					varc(base + 6) and 1 != 0,
				),
			)
		}
		return State(members, meta ushr 4 and 0xF, meta ushr 8 and 0xF, invite)
	}

	/** A base-37 name from its two halves, as the game shows names ("Zezima", "Bob the cat"); null for 0. */
	@JvmStatic
	fun name(hi: Int, lo: Int): String? {
		var value: Long = (hi.toLong() shl 32) or (lo.toLong() and 0xFFFFFFFFL)
		if (value <= 0L) {
			return null
		}
		val chars: CharArray = CharArray(12)
		var length = 0
		while (value != 0L && length < chars.size) {
			chars[length++] = BASE37[(value % 37).toInt()]
			value /= 37
		}
		val builder: StringBuilder = StringBuilder()
		var start = true
		for (i in length - 1 downTo 0) {
			val c: Char = chars[i]
			if (c == '_') {
				builder.append(' ')
				start = true
			} else {
				builder.append(if (start) c.uppercaseChar() else c)
				start = false
			}
		}
		return builder.toString().trim().ifEmpty { null }
	}

	/**
	 * A ping when the server's count changes; the first value read after a login is old. The count and the tile are
	 * two variables that may land a cycle apart: a new count waits for its tile.
	 */
	@JvmStatic
	fun pinged(client: ClientApi?, seq: Int, tile: Int) {
		if (!pingPrimed) {
			// what the variable holds at login (-1 before the server set it) is no new ping
			pingPrimed = true
			lastPingSeq = seq
			return
		}
		if (seq == lastPingSeq) {
			return
		}
		if (seq == 0 || seq == -1) {
			lastPingSeq = seq
			return
		}
		if (tile == 0 || tile == -1) {
			return
		}
		lastPingSeq = seq
		pingX = tile ushr 14 and 0x3FFF
		pingY = tile and 0x3FFF
		pingLevel = tile ushr 28 and 0x3
		pingSlot = seq ushr 24
		pingCycle = cycle
		if (pings && sounds) {
			client?.playSound(PING_SOUND, 255)
		}
	}

	/** Forgets the pings seen: for the tests. */
	@JvmStatic
	fun resetPings() {
		pingPrimed = false
		lastPingSeq = -1
		pingX = -1
	}

	/** Whether a ping shows now, for the tests. */
	@JvmStatic
	fun pingShowing(): Boolean = pingX >= 0 && cycle - pingCycle < PING_CYCLES

	/** The colour of a member's name, from the name (RuneLite's recolour by hash). */
	@JvmStatic
	fun colour(name: String): Int {
		if (!recolorNames) {
			return WHITE
		}
		val hash: Int = name.lowercase().hashCode()
		val hue: Int = (hash and 0x7FFFFFFF) % 360
		return hsv(hue)
	}

	private fun hsv(hue: Int): Int {
		val h: Double = hue / 60.0
		val c: Double = 0.85
		val x: Double = c * (1 - kotlin.math.abs(h % 2 - 1))
		val m: Double = 0.15
		val (r, g, b) = when (h.toInt()) {
			0 -> Triple(c, x, 0.0)
			1 -> Triple(x, c, 0.0)
			2 -> Triple(0.0, c, x)
			3 -> Triple(0.0, x, c)
			4 -> Triple(x, 0.0, c)
			else -> Triple(c, 0.0, x)
		}
		return (0xFF shl 24) or (((r + m) * 255).toInt() shl 16) or (((g + m) * 255).toInt() shl 8) or ((b + m) * 255).toInt()
	}

	private fun command(text: String) {
		ClientCommandProcessor.sendCheat(text, false, false)
	}

	private fun ping(sceneX: Int, sceneY: Int) {
		val client: ClientApi = context?.client() ?: return
		val level: Int = client.localPlayer()?.level() ?: return
		command("ping " + (client.sceneBaseX() + sceneX) + " " + (client.sceneBaseY() + sceneY) + " " + level)
	}

	private fun isInviteLine(client: ClientApi, widgetId: Int, slot: Int): Boolean {
		if (widgetId ushr 16 != ChatHistory.CHATBOX) {
			return false
		}
		val widget: Widget = client.widgetById(widgetId) ?: return false
		val line: Widget = if (slot >= 0) widget.child(slot) ?: return false else widget
		return ClientFeatures.plainText(line.text() ?: return false).contains(INVITE_TEXT)
	}

	/**
	 * The panel: the party's size, then each member's name (a crown for the leader, a red Wilderness tag while their
	 * location is hidden) and their hitpoints and prayer, each as a bar with its name and value on it; both on one line when
	 * compact. Hovering a member shows them in full.
	 */
	private fun drawPanel(g: Graphics, overlay: PanelOverlay): Size? {
		val current: State = state
		if (!enabled || !g.fontReady() || (!current.inParty() && current.invite == null)) {
			return null
		}
		val client: ClientApi? = context?.client()
		val line: Int = g.fontHeight()
		val width: Int = if (overlay.preferredWidth > 0) overlay.preferredWidth else PANEL_WIDTH
		val statLines: Int = if (compactPanel) 1 else 2
		val memberHeight: Int = line + statLines * STAT_LINE + GAP
		val height: Int = BORDER + line + (if (current.invite != null) line else 0) + current.members.size * memberHeight + BORDER
		PanelComponent.background(g, 0, 0, width, height, overlay.backgroundColour)
		var y: Int = BORDER
		g.text(BORDER, y + line - 2, if (current.inParty()) "Party (" + current.members.size + "/" + MAX_MEMBERS + ")" else "Party", TITLE, BLACK)
		y += line
		current.invite?.let {
			g.text(BORDER, y + line - 2, "Invite from " + it, INVITE, BLACK)
			y += line
		}
		val mouseX: Int = (client?.mouseX() ?: -1) - overlay.boundsX
		val mouseY: Int = (client?.mouseY() ?: -1) - overlay.boundsY
		for (member in current.members) {
			drawMember(g, member, member.slot == current.leader, y, width, line)
			if (client != null && mouseX in 0 until width && mouseY in y until y + memberHeight) {
				client.addTooltip(tooltip(member))
			}
			y += memberHeight
		}
		return size.set(width, height)
	}

	private fun drawMember(g: Graphics, member: Member, leader: Boolean, top: Int, width: Int, line: Int) {
		var x: Int = BORDER
		if (leader) {
			crown(g, x, top + 2)
			x += CROWN + 2
		}
		g.text(x, top + line - 2, member.name, colour(member.name), BLACK)
		if (member.inPvp) {
			g.textRight(width - BORDER, top + line - 2, "Wilderness", PVP, BLACK)
		}
		val y: Int = top + line
		if (compactPanel) {
			val half: Int = (width - BORDER * 2 - GAP) / 2
			stat(g, BORDER, y, half, member.hp, member.hpMax, HP, "HP " + member.hp)
			stat(g, BORDER + half + GAP, y, half, member.prayer, member.prayerMax, PRAYER, "Pray " + member.prayer)
		} else {
			val full: Int = width - BORDER * 2
			stat(g, BORDER, y, full, member.hp, member.hpMax, HP, "HP " + member.hp + "/" + member.hpMax)
			stat(g, BORDER, y + STAT_LINE, full, member.prayer, member.prayerMax, PRAYER, "Prayer " + member.prayer + "/" + member.prayerMax)
		}
	}

	/** One stat: its bar, with its name and value written over it ("HP 45/99", "Spec 75%"). */
	private fun stat(g: Graphics, x: Int, y: Int, width: Int, value: Int, max: Int, colour: Int, label: String) {
		g.fillRect(x, y + 2, width, STAT_BAR, BAR_BACK)
		if (max > 0) {
			g.fillRect(x, y + 2, width * value.coerceIn(0, max) / max, STAT_BAR, colour)
		}
		g.textCentered(x + width / 2, y + STAT_LINE - 3, label, WHITE, BLACK)
	}

	/** A small gold crown: a band with three points. */
	private fun crown(g: Graphics, x: Int, y: Int) {
		xs[0] = x
		ys[0] = y + CROWN
		xs[1] = x + CROWN
		ys[1] = y + CROWN
		xs[2] = x + CROWN
		ys[2] = y + 3
		xs[3] = x
		ys[3] = y + 3
		g.fillPolygon(xs, ys, 4, GOLD)
		g.polygon(xs, ys, 4, BLACK)
		for (i in 0..2) {
			g.fillRect(x + i * (CROWN - 2) / 2, y, 2, 3, GOLD)
		}
	}

	/** The hover tooltip: every stat in full, and where they are. */
	@JvmStatic
	fun tooltip(member: Member): String {
		val where: String = if (member.x < 0) "<col=ff3030>Location hidden (Wilderness)</col>" else "Tile " + member.x + ", " + member.y + (if (member.level > 0) ", floor " + member.level else "")
		return "<col=ffffff>" + member.name + "</col></br>" +
			"<col=ff3030>Hitpoints</col> " + member.hp + "/" + member.hpMax + "</br>" +
			"<col=30c8e0>Prayer</col> " + member.prayer + "/" + member.prayerMax + "</br>" +
			"<col=40d040>Special attack</col> " + member.spec + "%</br>" +
			"<col=e0b020>Run energy</col> " + member.run + "%</br>" + where
	}

	/** RuneLite's PartyPingOverlay: the pinged tile in the pinger's colour while it lasts. */
	private fun drawPing(g: Graphics, client: ClientApi) {
		if (!pings || !pingShowing()) {
			return
		}
		if (!client.tilePolygon(pingX - client.sceneBaseX(), pingY - client.sceneBaseY(), pingLevel, 1, xs, ys)) {
			return
		}
		val name: String = state.members.firstOrNull { it.slot == pingSlot }?.name ?: ""
		val colour: Int = colour(name)
		val fade: Int = 255 - 255 * (cycle - pingCycle) / PING_CYCLES
		g.fillPolygon(xs, ys, 4, (colour and 0xFFFFFF) or ((fade / 3) shl 24))
		g.polygon(xs, ys, 4, (colour and 0xFFFFFF) or (fade shl 24))
	}

	private fun drawMinimap(g: Graphics, client: ClientApi) {
		if (!client.minimapArea(area)) {
			return
		}
		g.clip(area[0], area[1], area[2], area[3])
		for (member in state.members) {
			if (member.slot == state.local || member.x < 0 || !client.projectMinimap(member.x, member.y, point)) {
				continue
			}
			g.fillCircle(point[0], point[1], DOT, colour(member.name))
		}
		g.unclip()
	}

	private fun drawWorldMap(g: Graphics, client: ClientApi) {
		if (!client.worldMapArea(area)) {
			return
		}
		g.clip(area[0], area[1], area[2], area[3])
		for (member in state.members) {
			if (member.slot == state.local || member.x < 0 || !client.projectWorldMap(member.x, member.y, point)) {
				continue
			}
			g.fillCircle(point[0], point[1], DOT + 1, colour(member.name))
			if (g.fontReady()) {
				g.textCentered(point[0], point[1] - DOT - 3, member.name, WHITE, BLACK)
			}
		}
		g.unclip()
	}

	private const val BASE37: String = "_abcdefghijklmnopqrstuvwxyz0123456789"
	private const val BORDER: Int = 4
	private const val GAP: Int = 2
	private const val PANEL_WIDTH: Int = 150
	private const val STAT_LINE: Int = 13
	private const val STAT_BAR: Int = 10
	private const val CROWN: Int = 9
	private const val DOT: Int = 3
	private const val WHITE: Int = -0x1
	private const val BLACK: Int = 0xFF000000.toInt()
	private const val TITLE: Int = 0xFFFF981F.toInt()
	private const val INVITE: Int = 0xFF00FF00.toInt()
	private const val BAR_BACK: Int = 0xC0000000.toInt()
	private const val HP: Int = 0xFFD01010.toInt()
	private const val PRAYER: Int = 0xFF20A8C0.toInt()
	private const val GOLD: Int = 0xFFFFD700.toInt()
	private const val PVP: Int = 0xFFFF3030.toInt()
}
