package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Hunter
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.events.LocChanged
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: Hunter's traps from Void's trap locations (Hunter). */
class HunterTest {

	private var now: Long = 1_000_000L

	private var playerX: Int = 10

	private val player: Player = Proxy.newProxyInstance(Player::class.java.classLoader, arrayOf(Player::class.java)) { _, method, _ ->
		when (method.name) {
			"tileX" -> playerX
			"tileY" -> 10
			"level" -> 0
			else -> null
		}
	} as Player

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, _ ->
		when (method.name) {
			"localPlayer" -> player
			"sceneBaseX", "sceneBaseY" -> 2500
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		Hunter.traps.clear()
		Hunter.clock = { now }
	}

	@AfterTest
	fun tidy() {
		fresh()
		Hunter.clock = { System.currentTimeMillis() }
	}

	private fun change(x: Int, oldId: Int, newId: Int) {
		val event = LocChanged()
		event.sceneX = x
		event.sceneY = 10
		event.oldId = oldId
		event.newId = newId
		Hunter.locChanged(client, event)
	}

	@Test
	fun aTrapSetBesideYouIsYoursAndFollowsItsState() {
		change(11, -1, 19187)
		assertEquals(1, Hunter.traps.size)
		assertEquals(Hunter.OPEN, Hunter.traps[0].state)
		change(11, 19187, 19188)
		assertEquals(Hunter.TRANSITION, Hunter.traps[0].state)
		change(11, 19188, 28557)
		assertEquals(Hunter.FULL, Hunter.traps[0].state)
		change(11, 28557, -1)
		assertEquals(0, Hunter.traps.size)
	}

	@Test
	fun someoneElsesTrapIsNotMarked() {
		change(20, -1, 19175)
		assertEquals(0, Hunter.traps.size)
	}

	@Test
	fun aTrapLeftTwiceItsTimeIsForgotten() {
		change(10, -1, 19175)
		change(10, 19175, 19174)
		assertEquals(Hunter.EMPTY, Hunter.traps[0].state)
		now += 120_001L
		Hunter.tick()
		assertEquals(0, Hunter.traps.size)
	}
}
