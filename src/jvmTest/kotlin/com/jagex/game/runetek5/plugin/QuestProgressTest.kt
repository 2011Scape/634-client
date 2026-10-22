package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.QuestState
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: a quest's state from its progress variables, against the 634 cache's ranges. */
class QuestProgressTest {

	private fun state(value: Int, start: Int, end: Int): Int = QuestProgress.state(intArrayOf(value), intArrayOf(start), intArrayOf(end))

	@Test
	fun cooksAssistant() {
		// varp 29, cache range 1..2, the Void server's unstarted 0, started 1, completed 2
		assertEquals(QuestState.NOT_STARTED, state(0, 1, 2))
		assertEquals(QuestState.IN_PROGRESS, state(1, 1, 2))
		assertEquals(QuestState.FINISHED, state(2, 1, 2))
	}

	@Test
	fun aStartAboveOne() {
		// Doric's Quest: varp 31, range 10..100; a value under the start is not started
		assertEquals(QuestState.NOT_STARTED, state(5, 10, 100))
		assertEquals(QuestState.IN_PROGRESS, state(10, 10, 100))
		assertEquals(QuestState.FINISHED, state(100, 10, 100))
		// Plague City's "completed with spell" (30) is past the end (29): still finished
		assertEquals(QuestState.FINISHED, state(30, 1, 29))
	}

	@Test
	fun severalVariables() {
		val starts: IntArray = intArrayOf(1, 1)
		val ends: IntArray = intArrayOf(5, 3)
		assertEquals(QuestState.NOT_STARTED, QuestProgress.state(intArrayOf(0, 0), starts, ends))
		assertEquals(QuestState.IN_PROGRESS, QuestProgress.state(intArrayOf(5, 0), starts, ends))
		assertEquals(QuestState.IN_PROGRESS, QuestProgress.state(intArrayOf(2, 3), starts, ends))
		assertEquals(QuestState.FINISHED, QuestProgress.state(intArrayOf(5, 3), starts, ends))
	}

	@Test
	fun noVariables() {
		assertEquals(QuestState.NOT_STARTED, QuestProgress.state(IntArray(0), IntArray(0), IntArray(0)))
	}
}
