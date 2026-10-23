package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Woodcutting
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.LocChanged
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: Woodcutting's logs, session rate and respawn timers (Woodcutting). */
class WoodcuttingTest {

	private var now: Long = 1_000_000L

	private val notified: ArrayList<String> = ArrayList()

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"sceneBaseX" -> 3200
			"sceneBaseY" -> 3200
			"notify" -> {
				notified.add(args[0] as String)
				true
			}
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		Woodcutting.enabled = true
		Woodcutting.session = null
		Woodcutting.respawns.clear()
		Woodcutting.clock = { now }
	}

	@AfterTest
	fun tidy() {
		fresh()
		Woodcutting.enabled = false
		Woodcutting.clock = { System.currentTimeMillis() }
	}

	private fun game(text: String) = Woodcutting.chat(client, ChatMessage(ChatType.GAME_FILTERED, "", text, null))

	private fun change(oldId: Int, newId: Int) {
		val event = LocChanged()
		event.sceneX = 10
		event.sceneY = 20
		event.oldId = oldId
		event.newId = newId
		Woodcutting.locChanged(client, event)
	}

	@Test
	fun logsCountAndTheRateComesAtThree() {
		game("You get some oak logs.")
		now += 60_000L
		game("You get some oak logs.")
		assertEquals(0, Woodcutting.session!!.logsPerHr)
		now += 60_000L
		game("You get some oak logs.")
		assertEquals(3, Woodcutting.session!!.logsCut)
		assertEquals(90, Woodcutting.session!!.logsPerHr)
	}

	@Test
	fun otherLinesAreNotLogs() {
		game("You swing your hatchet at the tree.")
		assertNull(Woodcutting.session)
	}

	@Test
	fun aNestNotifies() {
		game("<col=ff0000>A bird's nest falls out of the tree!")
		assertEquals(listOf("A bird nest has spawned!"), notified)
	}

	@Test
	fun aFelledOakTimesItsVoidRespawn() {
		change(1281, 1356)
		assertEquals(1, Woodcutting.respawns.size)
		// Void's oak: base 14 ticks, 14 + 7 * sqrt(14) / 0.6 = 58 ticks
		assertEquals(58 * 600L, Woodcutting.respawns[0].millis)
		assertEquals(3210, Woodcutting.respawns[0].x)
		change(1356, 1281)
		assertEquals(0, Woodcutting.respawns.size)
	}

	@Test
	fun aNormalTreeIsGivenItsLongestTime() {
		change(1276, 1342)
		assertEquals(100 * 600L, Woodcutting.respawns[0].millis)
	}

	@Test
	fun aChangeToSomethingElseIsNoStump() {
		change(1276, 99)
		assertEquals(0, Woodcutting.respawns.size)
	}
}
