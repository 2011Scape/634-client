package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Cooking
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: Cooking's session from Void's cooking lines (Cooking). */
class CookingTest {

	private var now: Long = 1_000_000L

	@BeforeTest
	fun fresh() {
		Cooking.session = null
		Cooking.statTimeout = 5
		Cooking.clock = { now }
	}

	@AfterTest
	fun tidy() {
		Cooking.session = null
		Cooking.clock = { System.currentTimeMillis() }
	}

	private fun game(text: String) = Cooking.chat(ChatMessage(ChatType.GAME_FILTERED, "", text, null))

	@Test
	fun voidsLinesAreCounted() {
		game("You successfully cook a trout.")
		game("You cook a piece of meat.")
		game("You roast a lobster.")
		game("You manage to cook a tuna.")
		game("You successfully bake a cake.")
		game("You accidentally burn the shark.")
		game("You catch a raw trout.")
		assertEquals(5, Cooking.session?.cookAmount)
		assertEquals(1, Cooking.session?.burnAmount)
		assertEquals("16.7", Cooking.oneDecimal(Cooking.session!!.burntPercentage()))
	}

	@Test
	fun theSessionGoesAfterTheTimeout() {
		game("You successfully cook a trout.")
		now += 4 * 60_000L
		Cooking.tick()
		assertEquals(1, Cooking.session?.cookAmount)
		now += 60_000L
		Cooking.tick()
		assertNull(Cooking.session)
	}

	@Test
	fun anHourOfCooking() {
		game("You successfully cook a trout.")
		now += 30 * 60_000L
		game("You successfully cook a trout.")
		assertEquals(4, Cooking.perHour(Cooking.session!!))
	}
}
