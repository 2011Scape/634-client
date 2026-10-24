package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.FarmingCategory
import com.jagex.game.runetek5.client.features.FarmingPatchData
import com.jagex.game.runetek5.client.features.TimeTracking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: Time Tracking's reading of Void's farming patches and its clock (TimeTracking). */
class TimeTrackingTest {

	/** Falador's herb patch (varbit 780) and its values. */
	private val herb: Int = FarmingPatchData.PATCHES.indexOfFirst { it.varbit == 780 }
	private val herbs: Int = FarmingPatchData.PATCHES[herb].values

	@BeforeTest
	fun fresh() {
		TimeTracking.decode("")
		TimeTracking.offsetModulus = 0
		TimeTracking.offsetResidue = 0
		TimeTracking.growthMinutes = 5
	}

	@AfterTest
	fun tidy() = fresh()

	@Test
	fun theTableIsVoids() {
		assertEquals(FarmingCategory.HERB, FarmingPatchData.PATCHES[herb].category)
		assertEquals(4, FarmingPatchData.PATCHES[herb].cycles)
		assertEquals("guam_0", TimeTracking.names(herbs)[4])
		// "catherby" holds "herb": the category comes from the patch's kind
		assertEquals(FarmingCategory.FLOWER, FarmingPatchData.PATCHES.first { it.varbit == 729 }.category)
	}

	@Test
	fun valuesAreRead() {
		assertEquals(TimeTracking.EMPTY, TimeTracking.kind("weeds_3"))
		assertEquals(TimeTracking.GROWING, TimeTracking.kind("guam_watered_2"))
		assertEquals(TimeTracking.GROWN, TimeTracking.kind("guam_life1"))
		assertEquals(TimeTracking.GROWN, TimeTracking.kind("oak_claim"))
		assertEquals(TimeTracking.STUMP, TimeTracking.kind("oak_stump"))
		assertEquals(TimeTracking.DISEASED, TimeTracking.kind("banana_dieased_2"))
		assertEquals(TimeTracking.DEAD, TimeTracking.kind("herb_dead_1"))
		assertEquals("dwarf_weed", TimeTracking.crop("dwarf_weed_watered_3"))
		// guam grows 0 to 3, then is grown: four cycles from planting
		assertEquals(4, TimeTracking.cyclesLeft(herbs, "guam_0"))
		assertEquals(1, TimeTracking.cyclesLeft(herbs, "guam_3"))
	}

	@Test
	fun onlyAStageGrownCounts() {
		assertTrue(TimeTracking.grew(herbs, "guam_1", "guam_2"))
		assertTrue(TimeTracking.grew(herbs, "guam_watered_1", "guam_2"))
		assertTrue(TimeTracking.grew(herbs, "guam_3", "guam_life1"))
		assertFalse(TimeTracking.grew(herbs, "weeds_0", "guam_0"), "planted")
		assertFalse(TimeTracking.grew(herbs, "guam_1", "guam_watered_1"), "watered")
		assertFalse(TimeTracking.grew(herbs, "guam_life2", "guam_life1"), "picked")
	}

	@Test
	fun theClockFollowsTheLearntOffset() {
		// nothing learnt: the cycles of the epoch
		assertEquals(20, TimeTracking.nextGrowth(0, 20))
		assertEquals(20, TimeTracking.nextGrowth(19, 20))
		// a tree (40 minutes) grew at minute 1000007: the offset is 7 (1000007 % 40 = 7 + 25 * 40... the residue)
		TimeTracking.learnOffset(1000007, 40)
		val off: Int = 1000007 % 40
		assertEquals(40, TimeTracking.offsetModulus)
		val next: Int = TimeTracking.nextGrowth(1000010, 20)
		assertEquals(0, (next - off) % 20)
		assertTrue(next in 1000011..1000030)
	}

	@Test
	fun aPatchTimesToGrown() {
		TimeTracking.values[herb] = 4 // guam_0
		val now: Long = 60L * 1000000L // minute 1000000, a multiple of 20
		// next growth at 1000020, then three more 20 minute cycles: 80 minutes
		assertEquals(80L * 60L, TimeTracking.secondsLeft(herb, now))
		assertEquals("Falador: guam, 1h20", TimeTracking.describe(herb, now))
		TimeTracking.values[herb] = 8 // guam_life1
		assertEquals(0L, TimeTracking.secondsLeft(herb, now))
		assertEquals("12m", TimeTracking.duration(11L * 60L + 5L))
	}

	@Test
	fun thePatchesAreKept() {
		TimeTracking.values[herb] = 6
		TimeTracking.grewAt[herb] = 123
		val text: String = TimeTracking.encode()
		assertEquals("780=6@123", text)
		TimeTracking.decode("")
		TimeTracking.decode(text + ",junk,99999=1@1")
		assertEquals(6, TimeTracking.values[herb])
		assertEquals(123, TimeTracking.grewAt[herb])
	}
}
