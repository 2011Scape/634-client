package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.InfoBox
import com.jagex.game.runetek5.plugin.api.ItemContainer
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
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.ServerTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Not part of the original client: RuneLite's Prayer. While you pray, a flick helper sweeps across the prayer
 * orb (and/or a prayer bar over your head) in time with the server's ticks (ServerTick, API 28); the orb glows
 * when a prayer potion in your backpack would restore without waste; hovering the orb tells the time your
 * prayer points last and your prayer bonus, or the orb's number shows that time; and an infobox can show each
 * active prayer, overheads too if you like.
 *
 * The time follows Void's drain (PrayerDrain): each tick the active prayers' drain is added to a counter, and a
 * point goes each time it passes 60 plus twice the prayer bonus. Void reads that bonus from a variable that its
 * worn equipment never sets (the equipment keeps "prayer_bonus"), so on Void the bonus does not slow the drain
 * and is left out of the time; the tooltip still says the worn items' bonus. The prayers, their bits, drains
 * and places in the list are Void's (prayers.toml, prayer.varps.toml); the icons are the prayer list's own.
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object Prayer : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("prayer", "Prayer", "1", PluginApi.VERSION, "A prayer flick helper, the time your prayer points last, a dose indicator and infoboxes for active prayers.", "Pixel")

	/** RuneLite's PrayerFlickLocation. */
	@JvmField
	val FLICK_LOCATIONS: Array<String> = arrayOf("None", "Prayer orb", "Prayer bar", "Both")

	const val FLICK_NONE: Int = 0
	const val FLICK_ORB: Int = 1
	const val FLICK_BAR: Int = 2
	const val FLICK_BOTH: Int = 3

	/** Void's prayer book (0 normal, 1 curses) and active prayers and curses. */
	const val PRAYER_BOOK_VARP: Int = 1584
	const val PRAYERS_VARP: Int = 1395
	const val CURSES_VARP: Int = 1582

	/** The prayer orb (749): its circle, its number. */
	const val PRAYER_ORB: Int = 749
	const val ORB_CIRCLE: Int = 2
	const val ORB_TEXT: Int = 4

	/** The backpack and worn equipment. */
	const val BACKPACK: Int = 93
	const val WORN: Int = 94

	/** The cache's prayer bonus param (ItemStats). */
	const val PRAYER_BONUS_PARAM: Int = 11

	/** Void's drain resistance without a bonus. */
	const val DRAIN_RESISTANCE: Int = 60

	const val TICK_MILLIS: Long = 600L

	/** A prayer: its name, its drain a tick, its place in the prayer list (whose icon it has) and whether it is an overhead. */
	class Kind(@JvmField val name: String, @JvmField val drain: Int, @JvmField val index: Int, @JvmField val overhead: Boolean)

	/** The normal prayers by their bit in varp 1395. */
	@JvmField
	val PRAYERS: Array<Kind> = arrayOf(
		Kind("Thick Skin", 3, 0, false), Kind("Burst of Strength", 3, 1, false), Kind("Clarity of Thought", 3, 2, false),
		Kind("Rock Skin", 6, 5, false), Kind("Superhuman Strength", 6, 6, false), Kind("Improved Reflexes", 6, 7, false),
		Kind("Rapid Restore", 1, 8, false), Kind("Rapid Heal", 2, 9, false), Kind("Protect Item", 2, 10, false),
		Kind("Steel Skin", 12, 13, false), Kind("Ultimate Strength", 12, 14, false), Kind("Incredible Reflexes", 12, 15, false),
		Kind("Protect from Magic", 12, 17, true), Kind("Protect from Missiles", 12, 18, true), Kind("Protect from Melee", 12, 19, true),
		Kind("Retribution", 3, 22, true), Kind("Redemption", 6, 23, true), Kind("Smite", 18, 24, true),
		Kind("Sharp Eye", 3, 3, false), Kind("Mystic Will", 3, 4, false), Kind("Hawk Eye", 6, 11, false),
		Kind("Mystic Lore", 6, 12, false), Kind("Eagle Eye", 12, 20, false), Kind("Mystic Might", 12, 21, false),
		Kind("Protect from Summoning", 12, 16, true), Kind("Chivalry", 18, 25, false), Kind("Piety", 20, 27, false),
		Kind("Rapid Renewal", 14, 26, false), Kind("Augury", 18, 29, false), Kind("Rigour", 16, 28, false),
	)

	/** The curses by their bit in varp 1582, which is also their place in the list. */
	@JvmField
	val CURSES: Array<Kind> = arrayOf(
		Kind("Protect Item", 2, 0, false), Kind("Sap Warrior", 14, 1, false), Kind("Sap Ranger", 14, 2, false),
		Kind("Sap Mage", 14, 3, false), Kind("Sap Spirit", 14, 4, false), Kind("Berserker", 2, 5, false),
		Kind("Deflect Summoning", 12, 6, true), Kind("Deflect Magic", 12, 7, true), Kind("Deflect Missiles", 12, 8, true),
		Kind("Deflect Melee", 12, 9, true), Kind("Leech Attack", 10, 10, false), Kind("Leech Ranged", 10, 11, false),
		Kind("Leech Magic", 10, 12, false), Kind("Leech Defence", 10, 13, false), Kind("Leech Strength", 10, 14, false),
		Kind("Leech Energy", 10, 15, false), Kind("Leech Special Attack", 10, 16, false), Kind("Wrath", 3, 17, true),
		Kind("Soul Split", 16, 18, true), Kind("Turmoil", 16, 19, false),
	)

	/** The prayer list's icons, by place (271:8's children): normal prayers, then curses. */
	@JvmField
	val PRAYER_SPRITES: IntArray = intArrayOf(135, 136, 137, 153, 154, 138, 139, 140, 141, 142, 143, 506, 507, 144, 145, 146, 948, 147, 148, 149, 508, 509, 151, 150, 152, 949, 3045, 950, 508, 3044)

	@JvmField
	val CURSE_SPRITES: IntArray = intArrayOf(2355, 2351, 2352, 2350, 2349, 2371, 2365, 2367, 2366, 2354, 2357, 2358, 2359, 2360, 2362, 2363, 2372, 2348, 2356, 2347)

	@JvmField var prayerFlickLocation: Int = FLICK_ORB
	@JvmField var prayerFlickAlwaysOn: Boolean = false
	@JvmField var prayerIndicator: Boolean = false
	@JvmField var prayerIndicatorOverheads: Boolean = false
	@JvmField var showPrayerDoseIndicator: Boolean = true
	@JvmField var showPrayerStatistics: Boolean = true
	@JvmField var showPrayerBar: Boolean = false
	@JvmField var hideIfNotPraying: Boolean = true
	@JvmField var hideIfOutOfCombat: Boolean = false
	@JvmField var replaceOrbText: Boolean = false

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	/** When the server's last tick came. */
	@JvmField var lastTick: Long = 0L

	/** The infoboxes shown, by book and bit (book * 32 + bit). */
	@JvmField val infoboxes: HashMap<Int, InfoBox> = HashMap()

	/** For the smoke test: what the last frame drew. */
	@JvmField var flickDrawn: Boolean = false
	@JvmField var doseDrawn: Boolean = false
	@JvmField var barDrawn: Boolean = false

	private var orbTextReplaced: Boolean = false
	private val point: IntArray = IntArray(2)
	private val ringXs: IntArray = IntArray(RING_POINTS)
	private val ringYs: IntArray = IntArray(RING_POINTS)
	private var context: PluginContext? = null

	/** RuneLite's PrayerBarOverlay, under the interfaces, and the orb's tooltip. */
	private val overlay: Overlay = Overlay { g, view ->
		context?.client()?.let {
			drawBar(g, view, it)
			tooltip(it)
		}
	}

	/** RuneLite's PrayerFlickOverlay and PrayerDoseOverlay, over the orb. */
	private val frameListener: EventListener<FrameDraw> = EventListener { event -> context?.client()?.let { drawOrb(event.graphics, it) } }

	private val serverTickListener: EventListener<ServerTick> = EventListener { _ -> lastTick = clock() }

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			context?.client()?.let {
				removeInfoboxes()
				resetOrbText(it)
				tick(it)
			}
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		showPrayerBar = !showPrayerBar
		context?.config()?.setBoolean("showPrayerBar", showPrayerBar)
		context?.config()?.save()
		context?.client()?.consolePrint(if (showPrayerBar) "Prayer bar on." else "Prayer bar off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's PrayerConfig, in its order
		s.choice("prayerFlickLocation", "Pray flick location", FLICK_LOCATIONS[FLICK_ORB], FLICK_LOCATIONS)
		s.toggle("prayerFlickAlwaysOn", "Never hide prayer flick helper", false)
		s.toggle("prayerIndicator", "Boost indicator", false)
		s.toggle("prayerIndicatorOverheads", "Overhead indicator", false)
		s.toggle("showPrayerDoseIndicator", "Show prayer dose indicator", true)
		s.toggle("showPrayerTooltip", "Show prayer orb tooltip", true)
		s.toggle("showPrayerBar", "Show prayer bar", false)
		s.toggle("prayerBarHideIfNotPraying", "Hide bar while prayer is inactive", true)
		s.toggle("prayerBarHideIfNonCombat", "Hide bar while out-of-combat", false)
		s.toggle("replaceOrbText", "Replace orb text with prayer time left", false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.SERVER_TICK, serverTickListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.addOverlay(overlay)
		context.registerCommand("prayerbar", toggle)
	}

	override fun stop() {
		removeInfoboxes()
		context?.client()?.let { resetOrbText(it) }
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		prayerFlickLocation = FLICK_LOCATIONS.indexOf(config.get("prayerFlickLocation", FLICK_LOCATIONS[FLICK_ORB])).takeIf { it >= 0 } ?: FLICK_ORB
		prayerFlickAlwaysOn = config.getBoolean("prayerFlickAlwaysOn", false)
		prayerIndicator = config.getBoolean("prayerIndicator", false)
		prayerIndicatorOverheads = config.getBoolean("prayerIndicatorOverheads", false)
		showPrayerDoseIndicator = config.getBoolean("showPrayerDoseIndicator", true)
		showPrayerStatistics = config.getBoolean("showPrayerTooltip", true)
		showPrayerBar = config.getBoolean("showPrayerBar", false)
		hideIfNotPraying = config.getBoolean("prayerBarHideIfNotPraying", true)
		hideIfOutOfCombat = config.getBoolean("prayerBarHideIfNonCombat", false)
		replaceOrbText = config.getBoolean("replaceOrbText", false)
	}

	/** Whether the curses are the book in use. */
	@JvmStatic
	fun curses(client: ClientApi): Boolean = client.varp(PRAYER_BOOK_VARP) == 1

	/** The active prayers' bits in the book in use. */
	@JvmStatic
	fun activeBits(client: ClientApi): Int = client.varp(if (curses(client)) CURSES_VARP else PRAYERS_VARP)

	@JvmStatic
	fun praying(client: ClientApi): Boolean = activeBits(client) != 0

	/** Void's getTotalDrainEffect: the active prayers' drain a tick. */
	@JvmStatic
	fun drainPerTick(curses: Boolean, bits: Int): Int {
		val kinds: Array<Kind> = if (curses) CURSES else PRAYERS
		var total: Int = 0
		for (bit in kinds.indices) {
			if ((bits ushr bit) and 1 == 1) {
				total += kinds[bit].drain
			}
		}
		return total
	}

	/** RuneLite's getEstimatedTimeRemaining, on Void's drain: seconds the points last, -1 while nothing drains. */
	@JvmStatic
	fun secondsLeft(points: Int, drain: Int): Double {
		if (drain <= 0) {
			return -1.0
		}
		return points.toDouble() * DRAIN_RESISTANCE / drain * TICK_MILLIS / MILLIS
	}

	/** "m:ss", or "h:mm:ss" past the hour. */
	@JvmStatic
	fun formatTime(seconds: Double): String {
		val total: Int = seconds.toInt()
		val h: Int = total / HOUR
		val m: Int = total % HOUR / MINUTE
		val s: Int = total % MINUTE
		val ss: String = (if (s < TEN) "0" else "") + s
		if (h > 0) {
			return h.toString() + ":" + (if (m < TEN) "0" else "") + m + ":" + ss
		}
		return m.toString() + ":" + ss
	}

	/** The worn items' prayer bonus. */
	@JvmStatic
	fun prayerBonus(client: ClientApi): Int {
		val worn: ItemContainer = client.itemContainer(WORN) ?: return 0
		var total: Int = 0
		for (slot in 0 until worn.size()) {
			val id: Int = worn.itemId(slot)
			if (id >= 0) {
				total += client.item(id)?.intParam(PRAYER_BONUS_PARAM, 0) ?: 0
			}
		}
		return total
	}

	/** RuneLite's tick progress: 0 to pi across a server tick. */
	@JvmStatic
	fun tickProgress(): Double = minOf((clock() - lastTick).toDouble() / TICK_MILLIS, 1.0) * PI

	/** RuneLite's onGameTick: the infoboxes kept to the active prayers, and the orb's number. */
	@JvmStatic
	fun tick(client: ClientApi) {
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		updateInfoboxes(client)
		if (replaceOrbText && praying(client)) {
			val wanted: String = formatTime(secondsLeft(client.skillLevel(Skill.PRAYER), drainPerTick(curses(client), activeBits(client))))
			val text: Widget? = client.widget(PRAYER_ORB, ORB_TEXT)
			if (text != null && text.text() != wanted) {
				text.setText(wanted)
			}
			orbTextReplaced = true
		} else {
			resetOrbText(client)
		}
	}

	private fun resetOrbText(client: ClientApi) {
		if (!orbTextReplaced) {
			return
		}
		orbTextReplaced = false
		client.widget(PRAYER_ORB, ORB_TEXT)?.setText(client.skillLevel(Skill.PRAYER).toString())
	}

	private fun updateInfoboxes(client: ClientApi) {
		val ctx: PluginContext = context ?: return
		val curses: Boolean = curses(client)
		val bits: Int = if (prayerIndicator) activeBits(client) else 0
		val kinds: Array<Kind> = if (curses) CURSES else PRAYERS
		val book: Int = if (curses) 1 else 0
		val wanted: HashSet<Int> = HashSet()
		for (bit in kinds.indices) {
			if ((bits ushr bit) and 1 == 1 && (prayerIndicatorOverheads || !kinds[bit].overhead)) {
				wanted.add(book * BOOK + bit)
			}
		}
		val gone: List<Int> = infoboxes.keys.filter { it !in wanted }
		for (key in gone) {
			infoboxes.remove(key)?.let { ctx.removeInfoBox(it) }
		}
		for (key in wanted) {
			if (key in infoboxes) {
				continue
			}
			val kind: Kind = kinds[key % BOOK]
			val box: InfoBox = object : InfoBox("Prayer") {
				override fun text(): String? = null

				override fun textColour(): Int = WHITE
			}
			box.spriteId = (if (curses) CURSE_SPRITES else PRAYER_SPRITES)[kind.index]
			box.tooltip = kind.name
			infoboxes[key] = box
			ctx.addInfoBox(box)
		}
	}

	private fun removeInfoboxes() {
		for (box in infoboxes.values) {
			context?.removeInfoBox(box)
		}
		infoboxes.clear()
	}

	/** Whether a prayer potion in the backpack would restore without waste (RuneLite's PrayerDoseOverlay). */
	@JvmStatic
	fun doseFits(client: ClientApi): Boolean {
		val backpack: ItemContainer = client.itemContainer(BACKPACK) ?: return false
		val max: Int = client.skillBaseLevel(Skill.PRAYER)
		val missing: Int = max - client.skillLevel(Skill.PRAYER)
		for (slot in 0 until backpack.size()) {
			val id: Int = backpack.itemId(slot)
			val restore: Int = if (id < 0) 0 else ItemHeals.restore(id, ItemHeals.PRAYER, max)
			if (restore in 1..missing) {
				return true
			}
		}
		return false
	}

	private fun drawOrb(g: Graphics, client: ClientApi) {
		flickDrawn = false
		doseDrawn = false
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val orb: Widget = client.widget(PRAYER_ORB, ORB_CIRCLE) ?: return
		if (orb.isHidden() || !orb.isDrawnThisFrame()) {
			return
		}
		val x: Int = orb.screenX()
		val y: Int = orb.screenY()
		val w: Int = orb.width()
		val h: Int = orb.height()
		if (showPrayerDoseIndicator && doseFits(client)) {
			// RuneLite's pulse between its two colours, once a second and a half
			val t: Double = (clock() % PULSE_MILLIS).toDouble() / PULSE_MILLIS
			val alpha: Int = (DOSE_ALPHA * (0.5 + 0.5 * sin(t * 2 * PI))).toInt()
			ring(g, x + w / 2, y + h / 2, w / 2, (alpha shl 24) or DOSE_RGB)
			doseDrawn = true
		}
		if ((prayerFlickLocation == FLICK_ORB || prayerFlickLocation == FLICK_BOTH) && (prayerFlickAlwaysOn || praying(client))) {
			val t: Double = tickProgress()
			val xOffset: Int = (-cos(t) * w / 2).toInt() + w / 2
			val indicatorHeight: Int = (sin(t) * h).toInt()
			val yOffset: Int = h / 2 - indicatorHeight / 2
			g.fillRect(x + xOffset, y + yOffset, 1, maxOf(1, indicatorHeight), CYAN)
			flickDrawn = true
		}
	}

	private fun ring(g: Graphics, cx: Int, cy: Int, radius: Int, argb: Int) {
		for (i in 0 until RING_POINTS) {
			val a: Double = i * 2 * PI / RING_POINTS
			ringXs[i] = cx + (cos(a) * radius).toInt()
			ringYs[i] = cy + (sin(a) * radius).toInt()
		}
		g.polygon(ringXs, ringYs, RING_POINTS, argb)
	}

	/** RuneLite's PrayerBarOverlay: a bar of the points over the player's head, with the flick helper. */
	private fun drawBar(g: Graphics, view: Viewport, client: ClientApi) {
		barDrawn = false
		val player: Player = client.localPlayer() ?: return
		if (!showPrayerBar || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val praying: Boolean = praying(client)
		if ((hideIfNotPraying && !praying) || (hideIfOutOfCombat && player.interacting() == null)) {
			return
		}
		if (!client.projectActor(player, player.overheadHeight() + BAR_RAISE, point)) {
			return
		}
		val x: Int = view.x + point[0] - BAR_WIDTH / 2
		val y: Int = view.y + point[1]
		val max: Int = maxOf(1, client.skillBaseLevel(Skill.PRAYER))
		val filled: Int = minOf(BAR_WIDTH, BAR_WIDTH * client.skillLevel(Skill.PRAYER) / max)
		g.fillRect(x, y, BAR_WIDTH, BAR_HEIGHT, BAR_BACKGROUND)
		g.fillRect(x, y, filled, BAR_HEIGHT, BAR_FOREGROUND)
		if ((prayerFlickLocation == FLICK_BAR || prayerFlickLocation == FLICK_BOTH) && (prayerFlickAlwaysOn || praying)) {
			val xOffset: Int = (-cos(tickProgress()) * BAR_WIDTH / 2).toInt() + BAR_WIDTH / 2
			g.fillRect(x + xOffset, y - 1, 1, BAR_HEIGHT + 2, WHITE)
		}
		barDrawn = true
	}

	/** RuneLite's PrayerDoseOverlay tooltip: the time the points last and the prayer bonus, with the mouse over the orb. */
	private fun tooltip(client: ClientApi) {
		if (!showPrayerStatistics || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val orb: Widget = client.widget(PRAYER_ORB, ORB_CIRCLE) ?: return
		if (orb.isHidden() || !orb.isDrawnThisFrame()) {
			return
		}
		val mx: Int = client.mouseX() - orb.screenX()
		val my: Int = client.mouseY() - orb.screenY()
		if (mx < 0 || my < 0 || mx >= orb.width() || my >= orb.height()) {
			return
		}
		val seconds: Double = secondsLeft(client.skillLevel(Skill.PRAYER), drainPerTick(curses(client), activeBits(client)))
		val panel: PanelComponent = PanelComponent()
		panel.addLine("Time Remaining:", WHITE, if (seconds < 0) "N/A" else formatTime(seconds), WHITE)
		val bonus: Int = prayerBonus(client)
		panel.addLine("Prayer Bonus:", WHITE, bonus.toString(), WHITE)
		client.addTooltip(panel)
	}

	private const val BOOK: Int = 32
	private const val MILLIS: Double = 1000.0
	private const val HOUR: Int = 3600
	private const val MINUTE: Int = 60
	private const val TEN: Int = 10
	private const val PULSE_MILLIS: Long = 1500L
	private const val DOSE_ALPHA: Int = 255
	private const val DOSE_RGB: Int = 0x00FFFF
	private const val RING_POINTS: Int = 32
	private const val BAR_WIDTH: Int = 30
	private const val BAR_HEIGHT: Int = 5
	private const val BAR_RAISE: Int = 10
	private const val BAR_BACKGROUND: Int = 0xFF961414.toInt()
	private const val BAR_FOREGROUND: Int = 0xFF00A0E0.toInt()

	private const val WHITE: Int = -0x1
	private const val CYAN: Int = 0xFF00FFFF.toInt()
}
