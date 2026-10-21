package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField

/**
 * Not part of the original client: what Discord is told the player is doing, as the plugin builds
 * it. Two instances with the same fields are the same presence, so a rebuild that changed nothing
 * is not sent again. [startTimestamp] is in seconds since the epoch, 0 for no elapsed time.
 */
data class DiscordActivity(
	@JvmField val state: String,
	@JvmField val details: String,
	@JvmField val largeImageText: String,
	@JvmField val smallImageKey: String?,
	@JvmField val startTimestamp: Long,
)

/** Where [DiscordState] sends the presence: the connection to Discord, or a test's list. Null clears it. */
fun interface DiscordSink {
	fun update(activity: DiscordActivity?)
}

/**
 * Not part of the original client: the stack of what the player is doing, after RuneLite's
 * DiscordState. Each event fires with [triggerEvent]; the stack keeps one entry per event with
 * when it first fired and when it last did, sorted by priority then recency. The presence takes
 * the first details, state and image found walking down the stack, so a skill names the details
 * while the place names the state. Skill events expire [timeoutMillis] after they last fired
 * ([checkForTimeout]); a place or a state event clears the others when it fires. [clock] is the
 * time in milliseconds, [largeImageText] the tooltip of the big icon.
 */
class DiscordState(private val clock: () -> Long, private val sink: DiscordSink) {

	/** Elapsed time since the first event of the session, the one at login. */
	@JvmField
	var elapsedMode: Int = ELAPSED_TOTAL

	/** How long a skill stays shown after its last experience drop. */
	@JvmField
	var timeoutMillis: Long = DEFAULT_TIMEOUT_MINUTES * MILLIS_PER_MINUTE

	@JvmField
	var largeImageText: String = ""

	/** What was last sent, or null when the presence is clear. */
	@JvmField
	var lastActivity: DiscordActivity? = null

	private val events: ArrayList<EventWithTime> = ArrayList()

	private class EventWithTime(@JvmField val type: DiscordGameEvent, @JvmField var start: Long) {
		@JvmField
		var updated: Long = start
	}

	/** Clears the stack and the presence. */
	fun reset() {
		events.clear()
		lastActivity = null
		sink.update(null)
	}

	/** The number of events on the stack. */
	fun eventCount(): Int {
		return events.size
	}

	/** The event at [index] of the sorted stack (0 is the top). */
	fun eventAt(index: Int): DiscordGameEvent {
		return events[index].type
	}

	fun triggerEvent(type: DiscordGameEvent) {
		val now: Long = clock()
		var event: EventWithTime? = null
		for (candidate in events) {
			if (candidate.type === type) {
				event = candidate
				break
			}
		}
		if (event == null) {
			event = EventWithTime(type, now)
			events.add(event)
		}
		event.updated = now
		if (type.shouldClear) {
			events.removeAll { other -> other.type !== type && other.type.shouldBeCleared }
		}
		if (type.shouldRestart) {
			event.start = now
		}
		events.sortWith(compareByDescending<EventWithTime> { it.type.priority }.thenByDescending { it.updated })
		updatePresence()
	}

	/** Drops the skill events that have not fired for [timeoutMillis], and sends the presence again when any went. */
	fun checkForTimeout() {
		if (events.isEmpty()) {
			return
		}
		val now: Long = clock()
		val removed: Boolean = events.removeAll { event -> event.type.shouldBeCleared && event.type.shouldTimeout && now > event.updated + timeoutMillis }
		if (removed) {
			updatePresence()
		}
	}

	private fun updatePresence() {
		if (events.isEmpty()) {
			reset()
			return
		}
		var imageKey: String? = null
		var state: String? = null
		var details: String? = null
		for (event in events) {
			if (imageKey == null) {
				imageKey = event.type.imageKey
			}
			if (details == null) {
				details = event.type.details
			}
			if (state == null) {
				state = event.type.state
			}
			if (imageKey != null && details != null && state != null) {
				break
			}
		}
		val start: Long = when (elapsedMode) {
			ELAPSED_HIDDEN -> 0L
			ELAPSED_ACTIVITY -> events[0].start
			else -> events.minOf { it.start }
		}
		val activity: DiscordActivity = DiscordActivity(state ?: "", details ?: "", largeImageText, imageKey, if (start == 0L) 0L else start / MILLIS_PER_SECOND)
		if (activity != lastActivity) {
			lastActivity = activity
			sink.update(activity)
		}
	}

	companion object {
		const val ELAPSED_TOTAL: Int = 0

		const val ELAPSED_ACTIVITY: Int = 1

		const val ELAPSED_HIDDEN: Int = 2

		const val DEFAULT_TIMEOUT_MINUTES: Long = 5

		const val MILLIS_PER_MINUTE: Long = 60_000

		private const val MILLIS_PER_SECOND: Long = 1000
	}
}
