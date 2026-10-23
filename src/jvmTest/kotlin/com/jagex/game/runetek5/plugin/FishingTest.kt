package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Fishing
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: Fishing's spots from Void's table, its catches and its session (Fishing). */
class FishingTest {

	private var now: Long = 1_000_000L

	private var animation: Int = -1

	private val spot: Npc = Proxy.newProxyInstance(Npc::class.java.classLoader, arrayOf(Npc::class.java)) { _, method, _ ->
		when (method.name) {
			"typeId" -> 321
			"name" -> "Fishing spot"
			else -> null
		}
	} as Npc

	private val player: Player = Proxy.newProxyInstance(Player::class.java.classLoader, arrayOf(Player::class.java)) { _, method, _ ->
		when (method.name) {
			"interacting" -> spot
			"animationId" -> animation
			else -> null
		}
	} as Player

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, _ ->
		when (method.name) {
			"localPlayer" -> player
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		Fishing.session = null
		Fishing.currentSpot = null
		Fishing.statTimeout = 5
		Fishing.clock = { now }
	}

	@AfterTest
	fun tidy() {
		fresh()
		Fishing.clock = { System.currentTimeMillis() }
	}

	private fun game(text: String) = Fishing.chat(ChatMessage(ChatType.GAME_FILTERED, "", text, null))

	@Test
	fun catherbysSpotsComeFromVoidsTable() {
		val lobster = Fishing.spot(321)!!
		assertEquals(377, lobster.iconItemId)
		assertEquals("Lobster, Tuna, Swordfish", lobster.name)
		assertEquals(383, Fishing.spot(322)!!.iconItemId)
		assertEquals("Shrimps, Anchovies, Sardine, Herring", Fishing.spot(320)!!.name)
		assertNull(Fishing.spot(1))
	}

	@Test
	fun catchesAreCountedAndStartTheSession() {
		game("You cast out your net...")
		assertNull(Fishing.session)
		game("You catch some raw shrimps.")
		game("You catch an enormous raw bass!")
		game("You catch a raw baron shark!")
		assertEquals(3, Fishing.session!!.caught)
	}

	@Test
	fun fishingNeedsTheSpotAndTheAnimation() {
		assertFalse(Fishing.isFishing(client))
		animation = 619
		assertTrue(Fishing.isFishing(client))
	}

	@Test
	fun theSpotFishedIsKeptAndTheSessionTimesOut() {
		game("You catch a raw lobster.")
		Fishing.tick(client)
		assertEquals("Lobster, Tuna, Swordfish", Fishing.currentSpot!!.name)
		now += 5 * 60_000L
		Fishing.tick(client)
		assertNull(Fishing.session)
		Fishing.tick(client)
		assertNotNull(Fishing.currentSpot)
	}
}
