package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Metronome
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.GameState
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: Metronome's ticks and tocks (Metronome). */
class MetronomeTest {

	private val played: ArrayList<Int> = ArrayList()

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"gameState" -> GameState.LOGGED_IN
			"playSound" -> {
				played.add(args[0] as Int)
				null
			}
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		Metronome.enabled = true
		Metronome.tickCount = 1
		Metronome.tickVolume = 3
		Metronome.tockVolume = 3
		Metronome.ticksSince = 0
		Metronome.tockNext = false
		played.clear()
	}

	@AfterTest
	fun tidy() {
		fresh()
		Metronome.enabled = false
	}

	@Test
	fun theTickAndTheTockTakeTurns() {
		repeat(4) { Metronome.tick(client) }
		assertEquals(listOf(Metronome.TICK_SOUND, Metronome.TOCK_SOUND, Metronome.TICK_SOUND, Metronome.TOCK_SOUND), played)
	}

	@Test
	fun everyNthTickAndAMutedTockIsSilent() {
		Metronome.tickCount = 2
		Metronome.tockVolume = 0
		repeat(8) { Metronome.tick(client) }
		assertEquals(listOf(Metronome.TICK_SOUND, Metronome.TICK_SOUND), played)
	}
}
