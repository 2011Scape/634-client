package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Mining
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.LocChanged
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/** Not part of the original client: Mining's session and respawn timers (Mining). */
class MiningTest {

	private var now: Long = 1_000_000L

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, _ ->
		when (method.name) {
			"sceneBaseX" -> 2900
			"sceneBaseY" -> 3300
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		Mining.enabled = true
		Mining.session = null
		Mining.respawns.clear()
		Mining.clock = { now }
	}

	@AfterTest
	fun tidy() {
		fresh()
		Mining.enabled = false
		Mining.clock = { System.currentTimeMillis() }
	}

	private fun game(text: String) = Mining.chat(ChatMessage(ChatType.GAME_FILTERED, "", text, null))

	private fun change(oldId: Int, newId: Int) {
		val event = LocChanged()
		event.sceneX = 5
		event.sceneY = 6
		event.oldId = oldId
		event.newId = newId
		Mining.locChanged(client, event)
	}

	@Test
	fun aSwingStartsTheSessionAndOresAreCounted() {
		game("You swing your pickaxe at the rock.")
		assertNotNull(Mining.session)
		game("You manage to mine some copper ore.")
		game("You manage to mine two copper ores!")
		assertEquals(2, Mining.session!!.mined)
	}

	@Test
	fun aMinedOutCopperRockTimesItsVoidRespawn() {
		change(5779, 5763)
		// Void's copper: 3 ticks, 3 + 7 * sqrt(3) / 0.6 = 23 ticks
		assertEquals(23 * 600L, Mining.respawns[0].millis)
		assertEquals(2905, Mining.respawns[0].x)
		change(5763, 5779)
		assertEquals(0, Mining.respawns.size)
	}

	@Test
	fun thePieGoesWhenItsTimeIsUp() {
		change(5779, 5763)
		now += 23 * 600L + 1
		Mining.respawns.removeAll { now - it.start > it.millis }
		assertEquals(0, Mining.respawns.size)
	}
}
