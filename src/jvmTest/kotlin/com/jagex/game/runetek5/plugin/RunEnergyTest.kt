package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.RunEnergy
import com.jagex.game.runetek5.plugin.api.ClientApi
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: Run Energy's times on Void's drain and restore (RunEnergy). */
class RunEnergyTest {

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, _ ->
		when (method.name) {
			"runEnergy" -> 50
			"weight" -> 32
			"skillLevel" -> 1
			"varp" -> 0
			"destinationTileX" -> -1
			else -> null
		}
	} as ClientApi

	@AfterTest
	fun tidy() {
		RunEnergy.replaceOrbText = false
	}

	@Test
	fun theDrainIsVoids() {
		assertEquals(67, RunEnergy.drainPerTick(0))
		assertEquals(100, RunEnergy.drainPerTick(32))
		assertEquals(134, RunEnergy.drainPerTick(64))
		assertEquals(134, RunEnergy.drainPerTick(90))
		assertEquals(67, RunEnergy.drainPerTick(-5))
	}

	@Test
	fun theRestoreFollowsAgilityAndResting() {
		assertEquals(27, RunEnergy.restorePerTick(1, 0))
		assertEquals(157, RunEnergy.restorePerTick(99, 0))
		assertEquals(168, RunEnergy.restorePerTick(1, RunEnergy.RESTING))
		assertEquals(400, RunEnergy.restorePerTick(99, RunEnergy.MUSIC))
		assertEquals(92, RunEnergy.restorePerTick(50, 0))
	}

	@Test
	fun runTimeRemaining() {
		// 10,000 energy at 67 a tick: 150 ticks, 90 seconds
		assertEquals("1:30", RunEnergy.runTimeRemaining(100, 0, false))
		assertEquals("90s", RunEnergy.runTimeRemaining(100, 0, true))
		// 5,000 at 100 a tick: 50 ticks, 30 seconds
		assertEquals("0:30", RunEnergy.runTimeRemaining(50, 32, false))
	}

	@Test
	fun recoverTime() {
		// 5,000 to go at 27 a tick: 186 ticks, 111 seconds
		assertEquals(111, RunEnergy.recoverTimeRemaining(50, 1, 0, false))
		assertEquals(-1, RunEnergy.recoverTimeRemaining(50, 1, 0, true))
		assertEquals(0, RunEnergy.recoverTimeRemaining(100, 1, 0, false))
	}

	@Test
	fun theTooltipReadsAsRuneLites() {
		assertEquals("Weight: 32 kg</br>Run Time Remaining: 0:30</br>100% Energy In: 1:51", RunEnergy.tooltipText(client))
		RunEnergy.replaceOrbText = true
		assertEquals("Weight: 32 kg</br>Run Energy: 50%</br>100% Energy In: 1:51", RunEnergy.tooltipText(client))
	}
}
