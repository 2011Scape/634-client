package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmField
import kotlin.time.TimeSource

/**
 * A small square with a picture and a line of text, RuneLite's InfoBox: given to
 * [PluginContext.addInfoBox], it joins the infobox panel at the top left of the game view with every
 * plugin's others, sorted by [priority] and then by plugin. The picture is the cache sprite [spriteId], the
 * plugin's [image] or the icon of item [itemId] (since API 14), shrunk to fit the box; hovering shows [tooltip]. Shift-right-clicking one offers
 * to detach it into a panel of its own (with the others of the same [name]), to flip a panel between
 * across and down, and to put a detached panel's boxes back. Since API 12.
 */
abstract class InfoBox(
	/** What the client keeps the box's panel under: boxes sharing a name are moved together. */
	@JvmField val name: String,
) {
	/** The cache sprite to show, -1 for [image] instead. */
	@JvmField
	var spriteId: Int = -1

	@JvmField
	var image: Image? = null

	/** The item whose icon to show when there is neither [image] nor [spriteId], -1 for none. Since API 14. */
	@JvmField
	var itemId: Int = -1

	@JvmField
	var tooltip: String? = null

	@JvmField
	var priority: InfoBoxPriority = InfoBoxPriority.NONE

	private val menuEntries: ArrayList<OverlayMenuEntry> = ArrayList()

	/** The text under the picture, or null for none. */
	abstract fun text(): String?

	/** The text's colour, ARGB. */
	abstract fun textColour(): Int

	/** Whether to show it this frame. */
	open fun render(): Boolean = true

	/** Whether it is finished with, and the client may drop it. */
	open fun cull(): Boolean = false

	/** The text shown on hovering it, or null for none; [tooltip] unless overridden. */
	open fun tooltip(): String? = tooltip

	/** Adds an entry the player gets on shift-right-clicking the box. */
	fun addMenuEntry(option: String, target: String, action: Task) {
		menuEntries.add(OverlayMenuEntry(option, target, action))
	}

	open fun menuEntries(): List<OverlayMenuEntry> = menuEntries
}

/** RuneLite's InfoBoxPriority: [HIGH] boxes come first. Since API 12. */
enum class InfoBoxPriority {
	HIGH,
	MED,
	NONE,
	LOW,
}

/** Which infoboxes [PluginContext.removeInfoBoxIf] takes away. Since API 12. */
fun interface InfoBoxFilter {
	fun test(box: InfoBox): Boolean
}

/** RuneLite's Counter: [count] in white. Since API 12. */
open class Counter(name: String, @JvmField var count: Int) : InfoBox(name) {
	override fun text(): String? = count.toString()

	override fun textColour(): Int = WHITE

	companion object {
		const val WHITE: Int = -0x1
	}
}

/**
 * RuneLite's Timer: the time left of [durationMillis] from when it was made, as m:ss, red in its last
 * tenth; gone once it runs out. Since API 12.
 */
open class Timer(name: String, @JvmField val durationMillis: Long) : InfoBox(name) {
	private val start: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()

	/** Milliseconds left, 0 once it has run out. */
	fun remainingMillis(): Long {
		val left: Long = durationMillis - start.elapsedNow().inWholeMilliseconds
		return if (left < 0L) 0L else left
	}

	override fun text(): String? {
		val seconds: Long = remainingMillis() / 1000L
		val secondsPart: Long = seconds % 60L
		return (seconds / 60L).toString() + ":" + (if (secondsPart < 10L) "0" else "") + secondsPart
	}

	override fun textColour(): Int {
		return if (remainingMillis() < durationMillis / 10L) RED_BRIGHTER else Counter.WHITE
	}

	override fun render(): Boolean = remainingMillis() > 0L

	override fun cull(): Boolean = remainingMillis() <= 0L

	companion object {
		/** Java's Color.RED.brighter(): red stays 255, so it is red. */
		const val RED_BRIGHTER: Int = 0xFFFF0000.toInt()
	}
}

/**
 * RuneLite's LoopTimer: a timer that starts again each time [periodMillis] runs out, as m:ss, red in the
 * last tenth of a loop; the time left in the loop when [reverse], else the time gone. [reset] starts the
 * loop again from now. Since API 17.
 */
open class LoopTimer(name: String, @JvmField val periodMillis: Long, @JvmField val reverse: Boolean) : InfoBox(name) {
	private var start: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()

	init {
		require(periodMillis > 0L) { "negative period!" }
	}

	/** Starts the loop again from now. */
	fun reset() {
		start = TimeSource.Monotonic.markNow()
	}

	/** Milliseconds gone in this loop, or left of it when [reverse]. */
	fun progressMillis(): Long {
		val progress: Long = start.elapsedNow().inWholeMilliseconds % periodMillis
		return if (reverse) periodMillis - progress else progress
	}

	override fun text(): String? {
		val seconds: Long = progressMillis() / 1000L
		val secondsPart: Long = seconds % 60L
		return ((seconds % 3600L) / 60L).toString() + ":" + (if (secondsPart < 10L) "0" else "") + secondsPart
	}

	override fun textColour(): Int {
		// RuneLite compares whole seconds against a tenth of the period's seconds
		return if (progressMillis() / 1000L < (periodMillis / 1000L) * 0.10) Timer.RED_BRIGHTER else Counter.WHITE
	}
}
