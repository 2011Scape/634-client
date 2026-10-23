package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Cannon
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.ProjectileSpawned
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: Cannon's ammo, counted from Void's chat lines and cannonballs (Cannon). */
class CannonTest {

	private val notified: ArrayList<String> = ArrayList()

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"sceneBaseX" -> 3000
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
		Cannon.forget()
		Cannon.enabled = true
		Cannon.lowWarningThreshold = 0
		Cannon.showEmptyCannonNotification = true
		// a cannon whose south-west tile is scene 50,50
		Cannon.cannonPlaced = true
		Cannon.cannonX = 3050
		Cannon.cannonY = 3250
		Cannon.cannonLevel = 0
	}

	@AfterTest
	fun tidy() {
		Cannon.forget()
	}

	private fun game(text: String) {
		Cannon.chat(client, ChatMessage(ChatType.GAME, "", text, null))
	}

	/** A cannonball leaving scene tile ([tileX], [tileY]). */
	private fun shot(tileX: Int, tileY: Int, id: Int = Cannon.CANNONBALL_PROJECTILE, source: Int = 0) {
		Cannon.projectile(client, ProjectileSpawned(id, 0, tileX * 512 + 256, tileY * 512 + 256, 0, 0, 0, 30, source, 1))
	}

	@Test
	fun loadingAddsTheBallsUpToThirty() {
		game("You load the cannon with 20 cannon balls.")
		assertEquals(20, Cannon.cballsLeft)
		game("You load the cannon with 15 cannon balls.")
		assertEquals(30, Cannon.cballsLeft)
		game("You load the cannon with 1 cannon ball.")
		assertEquals(30, Cannon.cballsLeft)
	}

	@Test
	fun eachShotFromTheCannonTakesOne() {
		game("You load the cannon with 10 cannon balls.")
		shot(51, 52)
		shot(52, 50)
		assertEquals(8, Cannon.cballsLeft)
	}

	@Test
	fun otherProjectilesAndOtherCannonsTakeNothing() {
		game("You load the cannon with 10 cannon balls.")
		shot(60, 60)
		shot(51, 51, id = 10)
		shot(51, 51, source = 5)
		assertEquals(10, Cannon.cballsLeft)
	}

	@Test
	fun outOfAmmoEmptiesItAndNotifies() {
		game("You load the cannon with 10 cannon balls.")
		game("Your cannon is out of ammo!")
		assertEquals(0, Cannon.cballsLeft)
		assertEquals(listOf("Your cannon is out of ammo!"), notified)
	}

	@Test
	fun theLowWarningComesOnceUntilItIsLoadedAgain() {
		Cannon.lowWarningThreshold = 9
		game("You load the cannon with 10 cannon balls.")
		shot(51, 51)
		shot(51, 51)
		assertEquals(1, notified.size)
		game("You load the cannon with 5 cannon balls.")
		shot(51, 51)
		assertEquals(1, notified.size)
		Cannon.cballsLeft = 9
		shot(51, 51)
		assertEquals(2, notified.size)
	}

	@Test
	fun pickingItUpForgetsIt() {
		game("You load the cannon with 10 cannon balls.")
		game("You pick up the cannon. It's really heavy.")
		assertFalse(Cannon.cannonPlaced)
		assertEquals(0, Cannon.cballsLeft)
	}

	@Test
	fun theSpotsNeedAllFourParts() {
		val pack = { ids: IntArray ->
			Proxy.newProxyInstance(ItemContainer::class.java.classLoader, arrayOf(ItemContainer::class.java)) { _, method, args ->
				when (method.name) {
					"size" -> ids.size
					"itemId" -> ids[args[0] as Int]
					else -> 1
				}
			} as ItemContainer
		}
		assertTrue(Cannon.carriesAllParts(pack(intArrayOf(6, 8, 10, 12, 2))))
		assertFalse(Cannon.carriesAllParts(pack(intArrayOf(6, 8, 10))))
	}

	@Test
	fun theCountIsGreenOrangeOrRed() {
		assertEquals(0xFF00FF00.toInt(), Cannon.stateColour(16))
		assertEquals(0xFFFFC800.toInt(), Cannon.stateColour(15))
		assertEquals(0xFFFFC800.toInt(), Cannon.stateColour(6))
		assertEquals(0xFFFF0000.toInt(), Cannon.stateColour(5))
	}
}
