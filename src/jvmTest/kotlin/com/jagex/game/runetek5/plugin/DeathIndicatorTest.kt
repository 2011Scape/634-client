package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.DeathIndicator
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: Death Indicator's grave from Void's lines (DeathIndicator). */
class DeathIndicatorTest {

	private var now: Long = 1_000_000L

	private var tileX: Int = 10

	private var arrow: IntArray? = null

	private val player: Player = Proxy.newProxyInstance(Player::class.java.classLoader, arrayOf(Player::class.java)) { _, method, _ ->
		when (method.name) {
			"tileX" -> tileX
			"tileY" -> 20
			"level" -> 0
			else -> null
		}
	} as Player

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"localPlayer" -> player
			"sceneBaseX" -> 3200
			"sceneBaseY" -> 3200
			"worldId" -> 1
			"gameState" -> GameState.LOGGED_IN
			"setHintArrow" -> {
				arrow = intArrayOf(args[0] as Int, args[1] as Int, args[2] as Int)
				null
			}
			"clearHintArrow" -> {
				arrow = null
				null
			}
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		DeathIndicator.clock = { now }
		DeathIndicator.deathInfoBox = false
		DeathIndicator.deathHintArrow = true
		DeathIndicator.clear(client)
	}

	@AfterTest
	fun tidy() {
		DeathIndicator.clear(client)
		DeathIndicator.clock = { System.currentTimeMillis() }
	}

	private fun game(text: String) = DeathIndicator.chat(client, ChatMessage(ChatType.GAME, "", text, null))

	@Test
	fun theGravestoneMarksTheDeathTile() {
		game("Oh dear, you are dead!")
		assertFalse(DeathIndicator.hasDeath())
		tileX = 30
		game("Your gravestone has appeared and will last 5 minutes before crumbling to dust. You")
		assertEquals(3210, DeathIndicator.deathX)
		assertEquals(3220, DeathIndicator.deathY)
		assertTrue(DeathIndicator.hasDeath())
		DeathIndicator.tick(client)
		assertEquals(listOf(3210, 3220, 0), arrow?.toList())
	}

	@Test
	fun noGravestoneNoMark() {
		game("Oh dear, you are dead!")
		DeathIndicator.tick(client)
		assertFalse(DeathIndicator.hasDeath())
	}

	@Test
	fun theMarkGoesOnTheTileOrWhenTheGraveCrumbles() {
		game("Oh dear, you are dead!")
		game("Your gravestone has appeared and will last 1 minute before crumbling to dust. You")
		tileX = 40
		DeathIndicator.tick(client)
		now += 61_000L
		DeathIndicator.tick(client)
		assertFalse(DeathIndicator.hasDeath())
		assertEquals(null, arrow)
		game("Oh dear, you are dead!")
		game("Your gravestone has appeared and will last 5 minutes before crumbling to dust. You")
		// the gravestone's line comes on the tile itself: the mark stays
		DeathIndicator.tick(client)
		assertTrue(DeathIndicator.hasDeath())
		tileX = 41
		DeathIndicator.tick(client)
		assertTrue(DeathIndicator.hasDeath())
		// back on the tile died on (3240): the mark goes
		tileX = 40
		DeathIndicator.tick(client)
		assertFalse(DeathIndicator.hasDeath())
	}
}
