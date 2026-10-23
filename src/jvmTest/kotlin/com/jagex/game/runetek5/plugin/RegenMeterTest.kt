package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.RegenMeter
import com.jagex.game.runetek5.client.features.StatusBars
import com.jagex.game.runetek5.plugin.api.ClientApi
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: the regeneration rings on Void's timers (RegenMeter). */
class RegenMeterTest {

	private var lp: Int = 990

	private var special: Int = 1000

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"varbit" -> if (args[0] == StatusBars.LIFE_POINTS_VARBIT) lp else 0
			"varp" -> if (args[0] == RegenMeter.SPECIAL_VARP) special else 0
			"skillBaseLevel" -> 99
			"skillLevel" -> lp / 10
			"notify" -> true
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		RegenMeter.showWhenNoChange = false
		RegenMeter.notifyBeforeHpRegenDuration = 0
		RegenMeter.ticksSinceHpRegen = 0
		RegenMeter.ticksSinceSpecRegen = 0
		RegenMeter.lastLifePoints = -1
		RegenMeter.lastSpecial = -1
		RegenMeter.lastNotification = null
	}

	@AfterTest
	fun tidy() = fresh()

	private fun ticks(n: Int) = repeat(n) { RegenMeter.tick(client) }

	@Test
	fun theRingStartsWhenTheLifePointsFallFromFull() {
		ticks(3)
		assertEquals(0.0, RegenMeter.hitpointsPercentage)
		lp = 900
		ticks(1)
		assertEquals(0.0, RegenMeter.hitpointsPercentage)
		ticks(4)
		assertEquals(0.4, RegenMeter.hitpointsPercentage, 1e-9)
	}

	@Test
	fun aHealStartsItAgainAndDamageDoesNot() {
		lp = 900
		ticks(4)
		lp = 850
		ticks(1)
		assertEquals(0.4, RegenMeter.hitpointsPercentage, 1e-9)
		lp = 852
		ticks(1)
		assertEquals(0.0, RegenMeter.hitpointsPercentage)
	}

	@Test
	fun overTheMaximumTheRingEmpties() {
		lp = 1100
		ticks(1)
		ticks(3)
		assertEquals(0.7, RegenMeter.hitpointsPercentage, 1e-9)
	}

	@Test
	fun theSpecialFillsOverFiftyTicksAndStopsWhenFull() {
		special = 500
		ticks(1)
		ticks(25)
		assertEquals(0.5, RegenMeter.specialPercentage, 1e-9)
		special = 600
		ticks(1)
		assertEquals(0.0, RegenMeter.specialPercentage)
		special = 1000
		ticks(5)
		assertEquals(0.0, RegenMeter.specialPercentage)
	}

	@Test
	fun theNotificationComesTheSetSecondsBefore() {
		RegenMeter.notifyBeforeHpRegenDuration = 2
		lp = 900
		ticks(1)
		// 2 s is 4 ticks before the heal: 10 - 4 = 6 ticks in
		ticks(5)
		assertNull(RegenMeter.lastNotification)
		ticks(1)
		assertEquals("Your next hitpoint will regenerate soon!", RegenMeter.lastNotification)
	}
}
