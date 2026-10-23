package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Smelting
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: Smelting's session from Void's furnace lines (Smelting). */
class SmeltingTest {

	private var now: Long = 1_000_000L

	@BeforeTest
	fun fresh() {
		Smelting.session = null
		Smelting.statTimeout = 5
		Smelting.clock = { now }
	}

	@AfterTest
	fun tidy() {
		Smelting.session = null
		Smelting.clock = { System.currentTimeMillis() }
	}

	private fun game(text: String, type: Int = ChatType.GAME) = Smelting.chat(ChatMessage(type, "", text, null))

	@Test
	fun barsAndTheVarrockArmoursSecond() {
		game("You retrieve a bar of iron.")
		game("The magic of the Varrock armour enables you to smelt 2 bars at the same time.")
		game("The ore is too impure and you fail to refine it.", ChatType.GAME_FILTERED)
		assertEquals(2, Smelting.session?.barsSmelted)
	}

	@Test
	fun cannonballsFourAMould() {
		game("You heat the steel bar into a liquid state.", ChatType.GAME_FILTERED)
		game("The molten metal cools slowly to form 4 cannonballs.", ChatType.GAME_FILTERED)
		game("You remove the cannonballs from the mould.", ChatType.GAME_FILTERED)
		game("You remove the cannonballs from the mould.", ChatType.GAME_FILTERED)
		assertEquals(8, Smelting.session?.cannonBallsSmelted)
		assertEquals(0, Smelting.session?.barsSmelted)
	}

	@Test
	fun theSessionGoesAfterTheTimeout() {
		game("You retrieve a bar of bronze.")
		now += 30 * 60_000L / 10
		game("You retrieve a bar of bronze.")
		assertEquals(40, Smelting.perHour(Smelting.session!!, 2))
		now += 5 * 60_000L
		Smelting.tick()
		assertNull(Smelting.session)
	}
}
