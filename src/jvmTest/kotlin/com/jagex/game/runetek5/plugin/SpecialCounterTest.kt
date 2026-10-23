package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.SpecialCounter
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.events.HitsplatApplied
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: which hitsplat a special attack counts, and how (SpecialCounter). */
class SpecialCounterTest {

	private var weapon: Int = 11696

	private var tick: Int = 100

	private fun npc(index: Int): Npc = Proxy.newProxyInstance(Npc::class.java.classLoader, arrayOf(Npc::class.java)) { _, method, _ ->
		when (method.name) {
			"index" -> index
			"tileX", "tileY" -> 10
			"isDying" -> false
			else -> null
		}
	} as Npc

	private var target: Npc = npc(7)

	private val player: Player = Proxy.newProxyInstance(Player::class.java.classLoader, arrayOf(Player::class.java)) { _, method, _ ->
		when (method.name) {
			"interacting" -> target
			"tileX" -> 11
			"tileY" -> 10
			else -> null
		}
	} as Player

	private val worn: ItemContainer = Proxy.newProxyInstance(ItemContainer::class.java.classLoader, arrayOf(ItemContainer::class.java)) { _, method, args ->
		when (method.name) {
			"size" -> 14
			"itemId" -> if (args[0] == SpecialCounter.WEAPON_SLOT) weapon else -1
			else -> 0
		}
	} as ItemContainer

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"itemContainer" -> if (args[0] == SpecialCounter.WORN) worn else null
			"localPlayer" -> player
			"serverTick" -> tick * 30
			"clientCycle" -> tick * 30
			"notify" -> true
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		SpecialCounter.enabled = true
		SpecialCounter.infobox = true
		SpecialCounter.specDropMisses = false
		SpecialCounter.thresholdNotification = false
		SpecialCounter.thresholds.fill(0)
		SpecialCounter.removeCounters()
		SpecialCounter.drops.clear()
		SpecialCounter.pendingWeapon = -1
		SpecialCounter.energy = -1
		SpecialCounter.lastNotification = null
		SpecialCounter.energyChanged(client, 1000)
	}

	@AfterTest
	fun tidy() {
		fresh()
		SpecialCounter.energy = -1
	}

	private fun spec(energy: Int) = SpecialCounter.energyChanged(client, energy)

	private fun hit(on: Npc, type: Int, damage: Int) = SpecialCounter.hitsplat(client, HitsplatApplied(on, type, damage, -1, 0, 0))

	@Test
	fun theGodswordCountsItsDamage() {
		spec(500)
		hit(target, 0, 450)
		spec(0)
		hit(target, 10, 300)
		assertEquals(750, SpecialCounter.counters[1]!!.count)
		assertEquals(2, SpecialCounter.drops.size)
	}

	@Test
	fun darklightCountsHitsNotDamage() {
		weapon = 6746
		spec(500)
		hit(target, 0, 170)
		assertEquals(1, SpecialCounter.counters[0]!!.count)
	}

	@Test
	fun someoneElsesHitsplatAndAnotherNpcAreNotTheSpecials() {
		spec(500)
		hit(target, 14, 300)
		hit(npc(8), 0, 300)
		assertNull(SpecialCounter.counters[1])
		hit(target, 0, 120)
		assertEquals(120, SpecialCounter.counters[1]!!.count)
	}

	@Test
	fun aMissIsNotCountedButDropsWhenAsked() {
		SpecialCounter.specDropMisses = true
		spec(500)
		hit(target, 8, 0)
		assertNull(SpecialCounter.counters[1])
		assertEquals("0", SpecialCounter.drops[0].text)
	}

	@Test
	fun aNewNpcStartsTheCountsAgain() {
		spec(500)
		hit(target, 0, 200)
		target = npc(9)
		spec(0)
		hit(target, 0, 100)
		assertEquals(100, SpecialCounter.counters[1]!!.count)
	}

	@Test
	fun theThresholdNotifies() {
		SpecialCounter.thresholdNotification = true
		SpecialCounter.thresholds[1] = 400
		spec(500)
		hit(target, 0, 450)
		assertEquals("Bandos Godsword special attack threshold reached!", SpecialCounter.lastNotification)
	}

	@Test
	fun aWeaponNotCountedOrARiseInEnergyWaitsForNothing() {
		weapon = 4151
		spec(500)
		assertEquals(-1, SpecialCounter.pendingWeapon)
		weapon = 11696
		spec(600)
		assertEquals(-1, SpecialCounter.pendingWeapon)
	}

	@Test
	fun theSeercullWaitsForItsArrowAndGivesUpAfter() {
		weapon = 6724
		spec(0)
		// (46 + 5 * 1) / 30 + 1 = 2 ticks
		assertEquals(tick + 2, SpecialCounter.hitsplatTick)
		SpecialCounter.tick(tick + 4)
		assertEquals(-1, SpecialCounter.pendingWeapon)
	}
}
