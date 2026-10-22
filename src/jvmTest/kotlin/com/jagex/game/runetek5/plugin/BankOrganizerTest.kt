package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.BankCategories
import com.jagex.game.runetek5.client.features.BankCleanupRules
import com.jagex.game.runetek5.client.features.BankOrganizer
import com.jagex.game.runetek5.client.features.BankOrganizerMatcher
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.QuestState
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: Bank Organizer against RuneLite's rules (BankOrganizerPlugin, BankOrganizerOverlay). */
class BankOrganizerTest {

	private val matcher: BankOrganizerMatcher = BankOrganizerMatcher()

	/** By quest id: its state for the stub client; not started when missing. */
	private val quests: HashMap<Int, Int> = HashMap()

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"questState" -> quests[args[0] as Int] ?: QuestState.NOT_STARTED
			else -> null
		}
	} as ClientApi

	@AfterTest
	fun coloursBack() {
		BankOrganizer.keepColour = BankOrganizer.KEEP
		BankOrganizer.discardColour = BankOrganizer.DISCARD
	}

	private fun allOn() {
		for (id in 1..BankCategories.COUNT) {
			matcher.active[id] = true
		}
	}

	@Test
	fun defaultsAreRuneLites() {
		// Melee and Farming on, the rest off
		assertContentEquals(intArrayOf(BankCategories.DEFAULT_COLOURS[1]), matcher.colours("Rune scimitar"))
		assertEquals(0, matcher.colours("Lobster").size)
		assertEquals(0xFFFF0000.toInt(), BankCategories.DEFAULT_COLOURS[1])
	}

	@Test
	fun coloursComeInCategoryOrder() {
		allOn()
		for (id in 1..BankCategories.COUNT) {
			matcher.colours[id] = id
		}
		// a rune scimitar is a melee weapon and, as in RuneLite's lists, slayer loot: two segments, in order
		assertContentEquals(intArrayOf(1, 6), matcher.colours("Rune scimitar"))
		assertContentEquals(intArrayOf(BankCategories.TOOLS), matcher.colours("Rune pickaxe"))
	}

	@Test
	fun hiddenExclusionsWin() {
		allOn()
		// "sword" is a melee pattern, swordfish is food
		assertFalse(matcher.inCategory(1, "swordfish"))
		assertFalse(matcher.inCategory(1, "raw swordfish"))
		assertTrue(matcher.inCategory(1, "rune 2h sword"))
	}

	@Test
	fun theirOwnExclusionsWin() {
		assertTrue(matcher.inCategory(1, "rune scimitar"))
		matcher.exclude(1, " Rune scimitar , ,dragon")
		assertFalse(matcher.inCategory(1, "rune scimitar"))
		assertFalse(matcher.inCategory(1, "dragon longsword"))
		assertTrue(matcher.inCategory(1, "adamant scimitar"))
		matcher.exclude(1, "")
		assertTrue(matcher.inCategory(1, "rune scimitar"))
	}

	@Test
	fun categoriesOffGiveNothing() {
		for (id in 1..BankCategories.COUNT) {
			matcher.active[id] = false
		}
		assertEquals(0, matcher.colours("Rune scimitar").size)
	}

	@Test
	fun toolsMatchWholeWords() {
		assertTrue(matcher.inCategory(BankCategories.TOOLS, "hammer"))
		assertTrue(matcher.inCategory(BankCategories.TOOLS, "chisel"))
		// a word inside another is not a tool
		assertFalse(matcher.inCategory(BankCategories.TOOLS, "warhammer"))
	}

	@Test
	fun rawSnakeskinIsNotRange() {
		assertFalse(matcher.inCategory(BankCategories.RANGE, "snakeskin"))
		assertTrue(matcher.inCategory(BankCategories.RANGE, "snakeskin body"))
	}

	@Test
	fun everyListIsLowercase() {
		for (id in 1..BankCategories.COUNT) {
			assertTrue(BankCategories.patterns(id).isNotEmpty(), BankCategories.NAMES[id])
			for (s in BankCategories.patterns(id) + BankCategories.exclusions(id)) {
				assertEquals(s.lowercase(), s)
			}
		}
	}

	@Test
	fun segmentsCoverTheBoxWithoutGaps() {
		for (n in 1..4) {
			var x: Int = 0
			for (i in 0 until n) {
				assertEquals(x, BankOrganizer.segmentStart(34, n, i))
				x += BankOrganizer.segmentWidth(34, n, i)
			}
			assertEquals(34, x)
		}
		// the last takes the leftover pixel: 34 / 3 = 11, 11, 12
		assertEquals(12, BankOrganizer.segmentWidth(34, 3, 2))
	}

	@Test
	fun cleanupKeepsUntilEveryQuestIsFinished() {
		// the gas mask: Plague City (58) and Biohazard (128) both need it
		assertContentEquals(intArrayOf(58, 128), BankCleanupRules.quests(GAS_MASK))
		assertEquals(BankOrganizer.KEEP, BankOrganizer.cleanupColour(client, GAS_MASK))
		quests[58] = QuestState.FINISHED
		assertEquals(BankOrganizer.KEEP, BankOrganizer.cleanupColour(client, GAS_MASK))
		quests[128] = QuestState.IN_PROGRESS
		assertEquals(BankOrganizer.KEEP, BankOrganizer.cleanupColour(client, GAS_MASK))
		quests[128] = QuestState.FINISHED
		assertEquals(BankOrganizer.DISCARD, BankOrganizer.cleanupColour(client, GAS_MASK))
		BankOrganizer.discardColour = 0xFF123456.toInt()
		assertEquals(0xFF123456.toInt(), BankOrganizer.cleanupColour(client, GAS_MASK))
	}

	@Test
	fun cleanupLeavesLastingItemsAlone() {
		// rewards and items other content needs have no rule: the ghostspeak amulet, the dramen staff, ice gloves
		for (id in intArrayOf(552, 772, 1580, COINS)) {
			assertNull(BankCleanupRules.quests(id), "item " + id)
			assertNull(BankOrganizer.cleanupColour(client, id))
		}
	}

	@Test
	fun cleanupRulesAreWellFormed() {
		assertTrue(BankCleanupRules.size() > 1000)
		assertNotNull(BankCleanupRules.quests(GAS_MASK))
		for (id in 0 until 25000) {
			val q: IntArray = BankCleanupRules.quests(id) ?: continue
			assertTrue(q.isNotEmpty() && q.all { it in 0 until 186 }, "item " + id)
		}
	}

	companion object {
		private const val GAS_MASK: Int = 1506

		private const val COINS: Int = 995
	}
}
