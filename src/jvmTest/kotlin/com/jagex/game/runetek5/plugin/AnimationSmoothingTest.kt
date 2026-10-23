package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.AnimationSmoothing
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.plugin.api.AnimationFilter
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: animation smoothing's filter (API 34), SeqType.smoothed and the built-in's choices. */
class AnimationSmoothingTest {

	private val owner: PluginRegistration = PluginRegistration(PluginDescriptor("smoother", "smoother", "1", PluginApi.VERSION, "", ""), null, false, "test")

	private fun sequence(id: Int, tweened: Boolean = false): SeqType {
		val seq: SeqType = SeqType()
		seq.id = id
		seq.tweened = tweened
		return seq
	}

	@AfterTest
	fun tidy() {
		PluginRuntime.release(owner)
		AnimationSmoothing.enabled = false
		AnimationSmoothing.excludedByPlayer = IntArray(0)
	}

	@Test
	fun noFilterLeavesOnlyTheCachesTweening() {
		assertFalse(PluginRuntime.smoothAnimation(808))
		assertFalse(sequence(808).smoothed())
		assertTrue(sequence(808, tweened = true).smoothed())
	}

	@Test
	fun aFilterSmoothsWhatItSaysYesTo() {
		PluginRuntime.setAnimationFilter(owner, AnimationFilter { id -> id != 1 })
		assertTrue(sequence(808).smoothed())
		assertFalse(sequence(1).smoothed())
	}

	@Test
	fun theAnswerIsKeptUntilTheFilterIsSetAgain() {
		var yes = true
		var asked = 0
		PluginRuntime.setAnimationFilter(owner, AnimationFilter { _ -> asked++; yes })
		val seq: SeqType = sequence(808)
		assertTrue(seq.smoothed())
		yes = false
		assertTrue(seq.smoothed())
		assertEquals(1, asked)
		PluginRuntime.setAnimationFilter(owner, AnimationFilter { _ -> yes })
		assertFalse(seq.smoothed())
	}

	@Test
	fun clearingOrReleasingTakesTheFilterBack() {
		val seq: SeqType = sequence(808)
		PluginRuntime.setAnimationFilter(owner, AnimationFilter { _ -> true })
		assertTrue(seq.smoothed())
		PluginRuntime.setAnimationFilter(owner, null)
		assertFalse(seq.smoothed())
		PluginRuntime.setAnimationFilter(owner, AnimationFilter { _ -> true })
		PluginRuntime.release(owner)
		assertFalse(seq.smoothed())
	}

	@Test
	fun theBuiltInSkipsExcludedSequencesAndIsOffByDefault() {
		assertFalse(AnimationSmoothing.shouldSmooth(808))
		AnimationSmoothing.enabled = true
		assertTrue(AnimationSmoothing.shouldSmooth(808))
		AnimationSmoothing.excludedByPlayer = AnimationSmoothing.parseIds("808, 9")
		assertFalse(AnimationSmoothing.shouldSmooth(808))
		for (id in AnimationSmoothing.EXCLUDED) {
			assertFalse(AnimationSmoothing.shouldSmooth(id))
		}
	}

	@Test
	fun idsParseFromCommasAndSpaces() {
		assertContentEquals(intArrayOf(1, 22, 333), AnimationSmoothing.parseIds(" 1,22  333, x,"))
		assertContentEquals(IntArray(0), AnimationSmoothing.parseIds(""))
	}
}
