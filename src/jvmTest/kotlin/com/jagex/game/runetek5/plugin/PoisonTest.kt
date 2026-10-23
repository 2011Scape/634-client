package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Poison
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Image
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

/** Not part of the original client: Poison's damage and times on Void's poison, and the orb's heart (Poison). */
class PoisonTest {

	private var now: Long = 1_000_000L

	private var poison: Int = 0

	private var disease: Int = 0

	private val overrides: HashMap<Int, Image?> = HashMap()

	private val image: Image = Proxy.newProxyInstance(Image::class.java.classLoader, arrayOf(Image::class.java)) { _, _, _ -> 0 } as Image

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"varp" -> if (args[0] == Poison.POISON_VARP) poison else if (args[0] == Poison.DISEASE_VARP) disease else 0
			"createImage" -> image
			"overrideSprite" -> {
				overrides[args[0] as Int] = args[1] as Image?
				null
			}
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		Poison.clock = { now }
		Poison.changeHealthIcon = true
		Poison.showInfoboxes = false
		Poison.heart = -1
		Poison.lastDamage = 0
	}

	@AfterTest
	fun tidy() {
		Poison.heart = -1
		Poison.clock = { System.currentTimeMillis() }
	}

	@Test
	fun theNextHitIsTheVarpUntilItWouldWearOff() {
		assertEquals(60, Poison.nextDamage(60))
		assertEquals(13, Poison.nextDamage(13))
		assertEquals(0, Poison.nextDamage(12))
		assertEquals(0, Poison.nextDamage(-200))
	}

	@Test
	fun theHitsLeftFallByTwoToTheCure() {
		// 60, 58, ... 14: twenty-four hits
		assertEquals(24, Poison.hitsLeft(60))
		assertEquals(1, Poison.hitsLeft(13))
		assertEquals(0, Poison.hitsLeft(12))
	}

	@Test
	fun theTooltipCountsDown() {
		poison = 20
		Poison.poisonChanged(client, 20)
		now += 6_000L
		// 20, 18, 16, 14: four hits, then the cure 18 seconds after the last
		assertEquals("Next poison damage: <col=ff0000>20</col></br>Time until damage: 0:12</br>Time until cure: 1:24", Poison.createTooltip())
	}

	@Test
	fun theHeartTurnsGreenThenYellowThenBack() {
		poison = 40
		Poison.checkHealthIcon(client)
		assertSame(image, overrides[Poison.HEART_SPRITE])
		assertEquals(Poison.HEART_POISON, Poison.heart)
		poison = 0
		disease = 3
		Poison.checkHealthIcon(client)
		assertEquals(Poison.HEART_DISEASE, Poison.heart)
		disease = 0
		Poison.checkHealthIcon(client)
		assertNull(overrides[Poison.HEART_SPRITE])
		assertEquals(-1, Poison.heart)
	}
}
