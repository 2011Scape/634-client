package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.InfoBox
import com.jagex.game.runetek5.plugin.api.InfoBoxPriority
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.VarpChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Time Tracking, its farming part, on Void's farming. Each
 * patch's varbit (FarmingPatchData, from Void's tables) says what grows in it and how far along; Void
 * moves a crop on a stage every growth cycle, farming.growth.mins (5) times the patch's multiple, on the
 * minutes of the epoch that fit the player's own offset (0 to 29 minutes, which Void keeps to itself). The
 * offset is learnt from the first stages seen to grow while logged in, and kept; until then the times are
 * reckoned as if it were 0, up to a cycle out.
 *
 * An infobox for each kind of patch with something in it shows how long until all of its crops are grown
 * ("Done" once they are); its tooltip has each patch. A crop that grows while you play is told in the
 * chat and on the desktop; at login the ones that finished while you were away are told once together.
 * The patches are kept for each account. `::farming` prints every patch. A built-in plugin
 * (BuiltinPlugins, docs/PLUGINS.md).
 */
object TimeTracking : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("timetracking", "Time Tracking", "1", PluginApi.VERSION, "Times your farming patches: an infobox for each kind of patch and a notification when your crops are grown.", "Pixel")

	/** What a patch's value means. */
	const val EMPTY: Int = 0
	const val GROWING: Int = 1
	const val GROWN: Int = 2
	const val DISEASED: Int = 3
	const val DEAD: Int = 4

	/** A tree stump, growing back in a cycle. */
	const val STUMP: Int = 5

	/** Client cycles (20 ms) after login before changes count as growing: the server sends every patch at login. */
	const val SETTLE_TICKS: Int = 250

	private const val GREEN: Int = 0xFF00FF00.toInt()

	private const val WHITE: Int = 0xFFFFFFFF.toInt()

	@JvmField var enabled: Boolean = true
	@JvmField var infoboxes: Boolean = true
	@JvmField var notifyGrown: Boolean = true
	@JvmField var growthMinutes: Int = 5

	/** A patch's value as last seen, and the epoch minute it last grew a stage (-1 when not seen). */
	@JvmField val values: IntArray = IntArray(FarmingPatchData.PATCHES.size).also { it.fill(-1) }
	@JvmField val grewAt: IntArray = IntArray(FarmingPatchData.PATCHES.size).also { it.fill(-1) }

	/** What is known of the account's growth offset: it is [offsetResidue] modulo [offsetModulus] (0 for nothing known). */
	@JvmField var offsetModulus: Int = 0
	@JvmField var offsetResidue: Int = 0

	/** The account the patches are of, lower case. */
	@JvmField var profile: String? = null

	/** Client cycles since login, and the patches found grown while settling (told together after). */
	@JvmField var ticksLoggedIn: Int = 0
	private val grownWhileAway: ArrayList<Int> = ArrayList()

	/** For the smoke test and the tests: the last line told. */
	@JvmField var lastMessage: String? = null

	private var context: PluginContext? = null

	private var dirty: Boolean = false

	/** Each value set of FarmingPatchData.VALUES as names by value, then each value's kind, stage and stages. */
	private val names: Array<Array<String?>?> = arrayOfNulls(FarmingPatchData.VALUES.size)

	private val boxes: HashMap<FarmingCategory, PatchBox> = HashMap()

	private val varpListener: EventListener<VarpChanged> = EventListener { _ -> dirty = true }

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.let { tick(it) } }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state !== GameState.LOGGED_IN) {
			ticksLoggedIn = 0
			grownWhileAway.clear()
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val print: CommandHandler = CommandHandler { _, _ ->
		val context: PluginContext = this.context ?: return@CommandHandler
		val now: Long = nowSeconds()
		var any: Boolean = false
		for (i in FarmingPatchData.PATCHES.indices) {
			val line: String = describe(i, now) ?: continue
			context.client().consolePrint(FarmingPatchData.PATCHES[i].category.title + ", " + line)
			any = true
		}
		if (!any) {
			context.client().consolePrint("No farming patches in use.")
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Time Tracking", true)
		s.toggle("infoboxes", "Farming infoboxes", true)
		s.toggle("notifyGrown", "Notify when crops are grown", true)
		s.number("growthMinutes", "Growth cycle (minutes)", 5, 1, 60, 1)
		readSettings(context.config())
		profile = null
		ticksLoggedIn = if (context.client().gameState() === GameState.LOGGED_IN) SETTLE_TICKS else 0
		dirty = true
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.VARP_CHANGED, varpListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.registerCommand("farming", print)
	}

	override fun stop() {
		context = null
		boxes.clear()
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		infoboxes = config.getBoolean("infoboxes", true)
		notifyGrown = config.getBoolean("notifyGrown", true)
		growthMinutes = config.getInt("growthMinutes", 5).coerceIn(1, 60)
	}

	private fun nowSeconds(): Long = lang.currentTimeMillis() / 1000L

	// --- what a value means ---

	/** Value set [set]'s names by value. */
	@JvmStatic
	fun names(set: Int): Array<String?> {
		names[set]?.let { return it }
		val out: Array<String?> = arrayOfNulls(256)
		for (pair in FarmingPatchData.VALUES[set].split(',')) {
			val eq: Int = pair.indexOf('=')
			val value: Int = pair.substring(0, eq).toInt()
			if (value in out.indices) {
				out[value] = pair.substring(eq + 1)
			}
		}
		names[set] = out
		return out
	}

	/** What value name [name] (Void's: "guam_2", "guam_watered_2", "guam_life1", "weeds_3"...) means. */
	@JvmStatic
	fun kind(name: String?): Int {
		if (name == null || name.startsWith("weeds") || name.startsWith("scarecrow")) {
			return EMPTY
		}
		if (name.contains("_diseased") || name.contains("_dieased")) {
			return DISEASED
		}
		if (name.contains("_dead")) {
			return DEAD
		}
		if (name.endsWith("_claim") || name.substring(name.lastIndexOf('_') + 1).startsWith("life")) {
			return GROWN
		}
		if (name.endsWith("_stump")) {
			return STUMP
		}
		return if (stage(name) >= 0) GROWING else EMPTY
	}

	/** The crop of a value name: "guam" for "guam_watered_2", "dwarf_weed" for "dwarf_weed_life1". */
	@JvmStatic
	fun crop(name: String): String {
		var s: String = name.substring(0, name.lastIndexOf('_').coerceAtLeast(0))
		for (suffix in arrayOf("_watered", "_diseased", "_dieased", "_dead")) {
			if (s.endsWith(suffix)) {
				s = s.substring(0, s.length - suffix.length)
			}
		}
		return s
	}

	/** The growing stage of a value name ("guam_2", "guam_watered_2" are 2), -1 when it has none. */
	@JvmStatic
	fun stage(name: String): Int = name.substring(name.lastIndexOf('_') + 1).toIntOrNull() ?: -1

	/** The cycles until value [name] of set [set] is grown: a growing crop's stages left and one more, a stump's one. */
	@JvmStatic
	fun cyclesLeft(set: Int, name: String?): Int {
		if (name == null) {
			return 0
		}
		return when (kind(name)) {
			STUMP -> 1
			GROWING -> {
				val crop: String = crop(name)
				var last: Int = 0
				for (other in names(set)) {
					if (other != null && kind(other) == GROWING && crop(other) == crop) {
						last = maxOf(last, stage(other))
					}
				}
				last - stage(name) + 1
			}
			else -> 0
		}
	}

	/** Whether the value going from [before] to [after] is a stage grown (not a planting, watering, cure or harvest). */
	@JvmStatic
	fun grew(set: Int, before: String?, after: String?): Boolean {
		if (before == null || after == null || before == after) {
			return false
		}
		val was: Int = kind(before)
		if (was != GROWING && was != STUMP && was != DISEASED) {
			return false
		}
		if (crop(before) != crop(after)) {
			return false
		}
		val now: Int = kind(after)
		if (was == GROWING && now == GROWING) {
			return stage(after) == stage(before) + 1
		}
		if (was == DISEASED) {
			return now == DEAD
		}
		if (was == STUMP) {
			return now == GROWN
		}
		// a growing crop grown, or caught a disease, on the cycle
		return (now == GROWN && cyclesLeft(set, before) == 1) || now == DISEASED
	}

	// --- the clock ---

	/** Learns from a stage grown at epoch minute [minute] on a patch whose cycle is [cycle] minutes. */
	@JvmStatic
	fun learnOffset(minute: Int, cycle: Int) {
		if (cycle > offsetModulus) {
			offsetModulus = cycle
			offsetResidue = minute % cycle
		}
	}

	/** The epoch minute a patch with a [cycle] minute cycle grows its next stage after [nowMinute]. */
	@JvmStatic
	fun nextGrowth(nowMinute: Int, cycle: Int): Int {
		// Void's offset is under 30: a modulus of 30 or more is the offset itself, a smaller one one of its candidates
		val offset: Int = if (offsetModulus > 0) offsetResidue % cycle else 0
		var m: Int = nowMinute + 1
		val r: Int = ((m - offset) % cycle + cycle) % cycle
		if (r != 0) {
			m += cycle - r
		}
		return m
	}

	/** Seconds until patch [i] is grown at [now] (epoch seconds); 0 once grown, -1 for nothing growing. */
	@JvmStatic
	fun secondsLeft(i: Int, now: Long): Long {
		val patch: FarmingPatchData.Patch = FarmingPatchData.PATCHES[i]
		val left: Int = cyclesLeft(patch.values, names(patch.values).getOrNull(values[i]))
		if (left <= 0) {
			return if (kind(names(patch.values).getOrNull(values[i])) == GROWN) 0L else -1L
		}
		val cycle: Int = growthMinutes * patch.cycles
		val done: Int = nextGrowth((now / 60L).toInt(), cycle) + (left - 1) * cycle
		return (done * 60L - now).coerceAtLeast(0L)
	}

	@JvmStatic
	fun duration(seconds: Long): String {
		val minutes: Long = (seconds + 59L) / 60L
		if (minutes < 60L) {
			return minutes.toString() + "m"
		}
		val m: Long = minutes % 60L
		return (minutes / 60L).toString() + "h" + (if (m < 10L) "0" else "") + m
	}

	/** Patch [i] as a line ("Falador: ranarr, 1h20"), null for an empty one. */
	@JvmStatic
	fun describe(i: Int, now: Long): String? {
		val patch: FarmingPatchData.Patch = FarmingPatchData.PATCHES[i]
		val name: String = names(patch.values).getOrNull(values[i]) ?: return null
		val crop: String = crop(name).replace('_', ' ')
		val what: String = when (kind(name)) {
			GROWING, STUMP -> duration(secondsLeft(i, now))
			GROWN -> "grown"
			DISEASED -> "diseased"
			DEAD -> "dead"
			else -> return null
		}
		return patch.place + ": " + crop + ", " + what
	}

	// --- each cycle ---

	private fun tick(context: PluginContext) {
		val client: ClientApi = context.client()
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		ticksLoggedIn++
		val name: String = client.localPlayer()?.name()?.lowercase() ?: return
		val config: PluginConfig = context.config()
		if (name != profile) {
			profile = name
			load(config, name)
			// what changed since the kept patches is told as grown while away, and teaches nothing of the clock
			ticksLoggedIn = 1
			dirty = true
		}
		if (dirty) {
			dirty = false
			scan(client, config, name)
		}
		if (ticksLoggedIn == SETTLE_TICKS && grownWhileAway.isNotEmpty()) {
			val text: StringBuilder = StringBuilder()
			for (i in grownWhileAway) {
				text.append(if (text.isEmpty()) "" else ", ").append(grownText(i))
			}
			tell(client, (if (grownWhileAway.size == 1) "Grown while you were away: " else "Grown while you were away (" + grownWhileAway.size + "): ") + text + ".")
			grownWhileAway.clear()
		}
		if (ticksLoggedIn % 50 == 0) {
			refreshBoxes(context)
		}
	}

	private fun grownText(i: Int): String {
		val patch: FarmingPatchData.Patch = FarmingPatchData.PATCHES[i]
		val name: String = names(patch.values).getOrNull(values[i]) ?: "?"
		return crop(name).replace('_', ' ') + " at " + patch.place
	}

	private fun scan(client: ClientApi, config: PluginConfig, name: String) {
		val minute: Int = (nowSeconds() / 60L).toInt()
		val settled: Boolean = ticksLoggedIn >= SETTLE_TICKS
		var changed: Boolean = false
		for (i in FarmingPatchData.PATCHES.indices) {
			val patch: FarmingPatchData.Patch = FarmingPatchData.PATCHES[i]
			val value: Int = client.varbit(patch.varbit)
			if (value == values[i]) {
				continue
			}
			val set: Int = patch.values
			val before: String? = names(set).getOrNull(values[i])
			val after: String? = names(set).getOrNull(value)
			values[i] = value
			changed = true
			if (settled && grew(set, before, after)) {
				grewAt[i] = minute
				learnOffset(minute, growthMinutes * patch.cycles)
			}
			if (kind(after) == GROWN && kind(before) != GROWN && before != null) {
				if (settled) {
					if (enabled && notifyGrown) {
						tell(client, "Your " + grownText(i) + " is grown.")
					}
				} else if (enabled && notifyGrown && (kind(before) == GROWING || kind(before) == STUMP)) {
					grownWhileAway.add(i)
				}
			}
		}
		if (changed) {
			config.set("patches_" + name, encode())
			config.set("offset_" + name, offsetModulus.toString() + ":" + offsetResidue)
			config.save()
			context?.let { refreshBoxes(it) }
		}
	}

	private fun tell(client: ClientApi, text: String) {
		lastMessage = text
		client.chatMessage(text)
		client.notify(text)
	}

	private fun load(config: PluginConfig, name: String) {
		decode(config.get("patches_" + name, ""))
		val offset: List<String> = config.get("offset_" + name, "0:0").split(':')
		offsetModulus = offset.getOrNull(0)?.toIntOrNull() ?: 0
		offsetResidue = offset.getOrNull(1)?.toIntOrNull() ?: 0
		grownWhileAway.clear()
	}

	/** The patches as kept in the settings: "varbit=value@minute" for each seen, ',' between. */
	@JvmStatic
	fun encode(): String {
		val out: StringBuilder = StringBuilder()
		for (i in FarmingPatchData.PATCHES.indices) {
			if (values[i] < 0) {
				continue
			}
			if (out.isNotEmpty()) {
				out.append(',')
			}
			out.append(FarmingPatchData.PATCHES[i].varbit).append('=').append(values[i]).append('@').append(grewAt[i])
		}
		return out.toString()
	}

	@JvmStatic
	fun decode(text: String) {
		values.fill(-1)
		grewAt.fill(-1)
		for (entry in text.split(',')) {
			val eq: Int = entry.indexOf('=')
			val at: Int = entry.indexOf('@')
			if (eq < 0 || at < eq) {
				continue
			}
			val varbit: Int = entry.substring(0, eq).toIntOrNull() ?: continue
			val i: Int = FarmingPatchData.PATCHES.indexOfFirst { it.varbit == varbit }
			if (i < 0) {
				continue
			}
			values[i] = entry.substring(eq + 1, at).toIntOrNull() ?: -1
			grewAt[i] = entry.substring(at + 1).toIntOrNull() ?: -1
		}
	}

	// --- the infoboxes ---

	/** A kind of patch with something in it: how long until all are grown, "Done" once they are; each patch in the tooltip. */
	class PatchBox(@JvmField val category: FarmingCategory) : InfoBox("TimeTracking " + category.title) {
		init {
			itemId = category.itemId
			priority = InfoBoxPriority.LOW
		}

		@JvmField var seconds: Long = -1L

		override fun text(): String? = if (seconds <= 0L) "Done" else duration(seconds)

		override fun textColour(): Int = if (seconds <= 0L) GREEN else WHITE

		override fun render(): Boolean = enabled && infoboxes && seconds >= 0L

		override fun tooltip(): String? {
			val now: Long = lang.currentTimeMillis() / 1000L
			val out: StringBuilder = StringBuilder(category.title)
			for (i in FarmingPatchData.PATCHES.indices) {
				if (FarmingPatchData.PATCHES[i].category !== category) {
					continue
				}
				val line: String = describe(i, now) ?: continue
				out.append("</br>").append(line)
			}
			return out.toString()
		}
	}

	private fun refreshBoxes(context: PluginContext) {
		val now: Long = nowSeconds()
		for (category in FarmingCategory.entries) {
			var latest: Long = -1L
			for (i in FarmingPatchData.PATCHES.indices) {
				if (FarmingPatchData.PATCHES[i].category === category) {
					latest = maxOf(latest, secondsLeft(i, now))
				}
			}
			var box: PatchBox? = boxes[category]
			if (box == null) {
				if (latest < 0L) {
					continue
				}
				box = PatchBox(category)
				boxes[category] = box
				context.addInfoBox(box)
			}
			box.seconds = latest
		}
	}
}
