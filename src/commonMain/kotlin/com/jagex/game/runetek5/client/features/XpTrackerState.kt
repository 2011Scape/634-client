package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.pow

/**
 * Not part of the original client: one skill's (or the total's) tracking for the XP Tracker,
 * RuneLite's XpStateSingle and XpPauseStateSingle: the experience it started from, what was gained
 * before and since the last per-hour reset, the actions (each experience update one) with the last ten
 * actions' experience for the actions left, and the time it was trained, which only runs while it has
 * gained since the reset and is not paused. Rates use at least a minute of it, as RuneLite's do.
 * Nothing here reads the game; times are milliseconds handed in.
 */
class XpTrackerState(startXp: Long) {

	@JvmField var startXp: Long = startXp
	@JvmField var xpGainedBeforeReset: Int = 0
	@JvmField var xpGainedSinceReset: Int = 0
	@JvmField var actions: Int = 0
	@JvmField var actionsSinceReset: Int = 0
	@JvmField var skillTime: Long = 0L
	@JvmField var lastChangeMillis: Long = 0L
	@JvmField var startLevelExp: Int = 0
	@JvmField var endLevelExp: Int = 0
	@JvmField var compact: Boolean = false

	/** RuneLite's pause reasons: by the player, at logout, after the idle timeout. */
	@JvmField var pausedManual: Boolean = false
	@JvmField var pausedLogout: Boolean = false
	@JvmField var pausedTimeout: Boolean = false

	/** The experience the pause check last saw, and when it changed. */
	@JvmField var pauseXp: Long = -1L
	@JvmField var pauseChangeMillis: Long = 0L

	private var actionsHistoryInitialized: Boolean = false

	private val actionExps: IntArray = IntArray(10)

	private var actionExpIndex: Int = 0

	fun isPaused(): Boolean = pausedManual || pausedLogout || pausedTimeout

	fun currentXp(): Long = startXp + totalXpGained()

	fun totalXpGained(): Int = xpGainedBeforeReset + xpGainedSinceReset

	private fun elapsedSeconds(): Long = maxOf(60L, skillTime / 1000L)

	private fun toHourly(value: Int): Int = ((1.0 / (elapsedSeconds() / 3600.0)) * value).toInt()

	fun xpPerHour(): Int = toHourly(xpGainedSinceReset)

	fun actionsPerHour(): Int = toHourly(actionsSinceReset)

	fun xpRemaining(): Int = endLevelExp - currentXp().toInt()

	/** How many more actions like the last ten reach the goal; [Int.MAX_VALUE] before any action (RuneLite shows N/A). */
	fun actionsRemaining(): Int {
		if (!actionsHistoryInitialized) {
			return Int.MAX_VALUE
		}
		val xpRemaining: Long = xpRemaining().toLong() * actionExps.size
		var total: Long = 0L
		for (x in actionExps) {
			total += x
		}
		if (total <= 0L) {
			return Int.MAX_VALUE
		}
		val quotient: Long = xpRemaining / total
		val remainder: Long = xpRemaining % total
		return (quotient + (if (remainder > 0) 1 else 0)).toInt()
	}

	/** How far through the goal, 0 to 100. */
	fun progress(): Double {
		val goal: Double = (endLevelExp - startLevelExp).toDouble()
		return if (goal <= 0) 100.0 else (currentXp() - startLevelExp) / goal * 100.0
	}

	fun secondsTillLevel(): Long {
		val seconds: Long = elapsedSeconds()
		if (xpGainedSinceReset <= 0) {
			return -1L
		}
		return xpRemaining().toLong() * seconds / xpGainedSinceReset
	}

	/**
	 * Time to the goal in RuneLite's three ways: [SHORT] `1:02:03` or `02:03`, [HOURS] `3 hours 02:03`,
	 * [DAYS] `2 days 01:02:03`; `∞` with no rate.
	 */
	fun timeTillLevel(format: Int): String {
		val remaining: Long = secondsTillLevel()
		if (remaining < 0) {
			return "∞"
		}
		val days: Long = remaining / 86400L
		val hours: Long = remaining % 86400L / 3600L
		val hoursTotal: Long = remaining / 3600L
		val minutes: Long = remaining % 3600L / 60L
		val seconds: Long = remaining % 60L
		if (format == DAYS && days > 1) {
			return days.toString() + " days " + two(hours) + ":" + two(minutes) + ":" + two(seconds)
		}
		if (format == DAYS && days == 1L) {
			return "1 day " + two(hours) + ":" + two(minutes) + ":" + two(seconds)
		}
		if (format != SHORT && hoursTotal > 1) {
			return hoursTotal.toString() + " hours " + two(minutes) + ":" + two(seconds)
		}
		if (format != SHORT && hoursTotal == 1L) {
			return "1 hour " + two(minutes) + ":" + two(seconds)
		}
		if (hoursTotal > 0) {
			return hoursTotal.toString() + ":" + two(minutes) + ":" + two(seconds)
		}
		return two(minutes) + ":" + two(seconds)
	}

	private fun two(value: Long): String = value.toString().padStart(2, '0')

	/** RuneLite's update: a new experience total; false when it did not change. */
	fun update(currentXp: Long, nowMillis: Long): Boolean {
		val original: Long = totalXpGained() + startXp
		val actionExp: Int = (currentXp - original).toInt()
		if (actionExp == 0) {
			return false
		}
		if (actionsHistoryInitialized) {
			actionExps[actionExpIndex] = actionExp
		} else {
			actionExps.fill(actionExp)
			actionsHistoryInitialized = true
		}
		actionExpIndex = (actionExpIndex + 1) % actionExps.size
		actions++
		actionsSinceReset++
		xpGainedSinceReset = (currentXp - (startXp + xpGainedBeforeReset)).toInt()
		lastChangeMillis = nowMillis
		return true
	}

	/** The goal: from the current level to the next (RuneLite's updateGoals with no skill-guide goal), 200M past the last level. */
	fun updateGoals(currentXp: Long) {
		val level: Int = levelForXp(currentXp)
		startLevelExp = xpForLevel(level)
		endLevelExp = if (level + 1 <= MAX_VIRT_LEVEL) xpForLevel(level + 1) else MAX_SKILL_XP
	}

	/** RuneLite's resetPerHour: the rates start again, what was gained is kept. */
	fun resetPerHour(nowMillis: Long) {
		actionsSinceReset = 0
		xpGainedBeforeReset += xpGainedSinceReset
		xpGainedSinceReset = 0
		lastChangeMillis = nowMillis
		skillTime = 0L
	}

	/** Time passes, only while it has gained since the reset (the caller skips a paused one). */
	fun tick(deltaMillis: Long) {
		if (xpGainedSinceReset > 0) {
			skillTime += deltaMillis
		}
	}

	/**
	 * RuneLite's XpPauseState for this skill once a second: experience that changed clears every pause;
	 * otherwise, with [pauseAfterMinutes], so long without a change pauses it.
	 */
	fun tickPause(currentXp: Long, pauseAfterMinutes: Int, nowMillis: Long) {
		if (pauseXp != currentXp) {
			pauseXp = currentXp
			pauseChangeMillis = nowMillis
			pausedManual = false
			pausedLogout = false
			pausedTimeout = false
		} else if (pauseAfterMinutes > 0 && pauseChangeMillis != 0L && nowMillis - pauseChangeMillis >= pauseAfterMinutes * 60_000L) {
			pausedTimeout = true
		}
	}

	/** RuneLite's unpause: every reason cleared, the idle clock started again. */
	fun unpause(nowMillis: Long) {
		pauseChangeMillis = nowMillis
		pausedManual = false
		pausedLogout = false
		pausedTimeout = false
	}

	/** What RuneLite's XpSaveSingle keeps: start, gained before and since the reset, and the time. */
	fun save(): LongArray = longArrayOf(startXp, xpGainedBeforeReset.toLong(), xpGainedSinceReset.toLong(), skillTime)

	fun restore(save: LongArray) {
		startXp = save[0]
		xpGainedBeforeReset = save[1].toInt()
		xpGainedSinceReset = save[2].toInt()
		skillTime = save[3]
	}

	companion object {
		const val SHORT: Int = 0
		const val HOURS: Int = 1
		const val DAYS: Int = 2

		/** RuneLite's Experience.MAX_VIRT_LEVEL and MAX_SKILL_XP. */
		const val MAX_VIRT_LEVEL: Int = 126
		const val MAX_SKILL_XP: Int = 200_000_000

		private val XP_TABLE: IntArray = buildTable()

		private fun buildTable(): IntArray {
			val table: IntArray = IntArray(MAX_VIRT_LEVEL + 1)
			var points: Int = 0
			for (level in 1 until MAX_VIRT_LEVEL) {
				points += kotlin.math.floor(level + 300.0 * 2.0.pow(level / 7.0)).toInt()
				table[level + 1] = points / 4
			}
			return table
		}

		/** The experience level [level] starts at (the RuneScape table, levels 1 to 126). */
		@JvmStatic
		fun xpForLevel(level: Int): Int = XP_TABLE[level.coerceIn(1, MAX_VIRT_LEVEL)]

		/** The level [xp] experience is, virtual levels past 99 included. */
		@JvmStatic
		fun levelForXp(xp: Long): Int {
			var level: Int = 1
			while (level < MAX_VIRT_LEVEL && XP_TABLE[level + 1] <= xp) {
				level++
			}
			return level
		}
	}
}
