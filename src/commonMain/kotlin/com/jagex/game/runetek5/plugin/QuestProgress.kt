package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.QuestState
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: a quest's state from its progress variables, as the cache's quest
 * types give them (variable, start, end) and the quest journal's scripts read them: a variable is
 * finished at or past its end and started at or past its start. A quest is finished when all its
 * variables are, not started when none is started, and in progress otherwise.
 */
object QuestProgress {

	/** The [QuestState] of a quest whose variables hold [values], against their [starts] and [ends]. */
	@JvmStatic
	fun state(values: IntArray, starts: IntArray, ends: IntArray): Int {
		if (values.isEmpty()) {
			return QuestState.NOT_STARTED
		}
		var finished: Int = 0
		var started: Int = 0
		for (i in values.indices) {
			if (values[i] >= ends[i]) {
				finished++
			} else if (values[i] >= starts[i]) {
				started++
			}
		}
		if (finished == values.size) {
			return QuestState.FINISHED
		}
		return if (finished == 0 && started == 0) QuestState.NOT_STARTED else QuestState.IN_PROGRESS
	}
}
