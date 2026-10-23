package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.NpcAggression
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.PluginConfig
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: the aggression timer on Void's tolerance (NpcAggression). */
class NpcAggressionTest {

	private var now: Long = 1_000_000L

	private var tileX: Int = 50

	private var notified: String? = null

	private val cow: Npc = Proxy.newProxyInstance(Npc::class.java.classLoader, arrayOf(Npc::class.java)) { _, method, _ ->
		if (method.name == "name") "Cow" else null
	} as Npc

	private val player: Player = Proxy.newProxyInstance(Player::class.java.classLoader, arrayOf(Player::class.java)) { _, method, _ ->
		when (method.name) {
			"tileX" -> tileX
			"tileY" -> 50
			"level" -> 0
			else -> null
		}
	} as Player

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"localPlayer" -> player
			"sceneBaseX" -> 3200
			"sceneBaseY" -> 3200
			"gameState" -> GameState.LOGGED_IN
			"npcCount" -> 1
			"npc" -> cow
			"notify" -> {
				notified = args[0] as String
				true
			}
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		NpcAggression.clock = { now }
		NpcAggression.centreX = -1
		NpcAggression.started = 0L
		NpcAggression.notified = false
		NpcAggression.npcUnaggroAlwaysActive = false
		NpcAggression.npcUnaggroShowTimer = false
		NpcAggression.notifyExpire = true
		NpcAggression.npcUnaggroNames = "co*"
		NpcAggression.readSettings(config(mapOf("enabled" to "true", "npcUnaggroNames" to "co*", "notifyExpire" to "true", "npcUnaggroShowTimer" to "false")))
	}

	/** A plugin config holding [values], the rest at their defaults. */
	private fun config(values: Map<String, String>): PluginConfig = Proxy.newProxyInstance(PluginConfig::class.java.classLoader, arrayOf(PluginConfig::class.java)) { _, method, args ->
		val value: String? = values[args[0] as String]
		when (method.name) {
			"get" -> value ?: args[1]
			"getBoolean" -> value?.toBoolean() ?: args[1]
			"getInt", "getColour" -> value?.toInt() ?: args[1]
			else -> null
		}
	} as PluginConfig

	@AfterTest
	fun tidy() {
		NpcAggression.clock = { System.currentTimeMillis() }
	}

	@Test
	fun aNamedMonsterNearStartsTheTimer() {
		NpcAggression.tick(client)
		assertTrue(NpcAggression.active)
		assertEquals(3250, NpcAggression.centreX)
		assertEquals(600_000L, NpcAggression.remainingMillis())
	}

	@Test
	fun stayingTenMinutesEndsTheAggression() {
		NpcAggression.tick(client)
		now += 300_000L
		tileX = 60
		NpcAggression.tick(client)
		assertEquals(300_000L, NpcAggression.remainingMillis())
		now += 300_000L
		NpcAggression.tick(client)
		assertEquals("NPC aggression has expired!", notified)
	}

	@Test
	fun leavingTheSquareStartsAgain() {
		NpcAggression.tick(client)
		now += 300_000L
		tileX = 61
		NpcAggression.tick(client)
		assertEquals(3261, NpcAggression.centreX)
		assertEquals(600_000L, NpcAggression.remainingMillis())
		assertFalse(NpcAggression.inArea(3250, 3250, 0))
	}
}
