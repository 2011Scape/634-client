package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ItemCharges
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: Item Charges' charges from 634's names and Void's messages (ItemCharges). */
class ItemChargesTest {

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, _ ->
		when (method.name) {
			"notify" -> true
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		ItemCharges.bindingCharges = -1
		ItemCharges.lastNotification = null
		ItemCharges.recoilNotification = false
		ItemCharges.bindingNotification = true
	}

	@AfterTest
	fun tidy() = fresh()

	private fun game(text: String) = ItemCharges.chat(client, ChatMessage(ChatType.GAME, "", text, null))

	@Test
	fun chargesAreReadFromTheName() {
		assertContentEquals(intArrayOf(ItemCharges.TELEPORT, 4), ItemCharges.classify("Amulet of glory(4)", false))
		assertContentEquals(intArrayOf(ItemCharges.TELEPORT, 8), ItemCharges.classify("Ring of duelling(8)", false))
		assertContentEquals(intArrayOf(ItemCharges.WATERCAN, 8), ItemCharges.classify("Watering can(8)", false))
		assertContentEquals(intArrayOf(ItemCharges.BELLOWS, 3), ItemCharges.classify("Ogre bellows (3)", false))
		assertContentEquals(intArrayOf(ItemCharges.SACK, 10), ItemCharges.classify("Potatoes(10)", false))
		assertContentEquals(intArrayOf(ItemCharges.FUNGICIDE, 7), ItemCharges.classify("Fungicide spray 7", false))
		assertContentEquals(intArrayOf(ItemCharges.GUTHIX_REST, 2), ItemCharges.classify("Guthix rest(2)", true))
	}

	@Test
	fun potionsAreDrinkableWithDoses() {
		assertContentEquals(intArrayOf(ItemCharges.POTION, 3), ItemCharges.classify("Super restore(3)", true))
		assertNull(ItemCharges.classify("Super restore(3)", false))
		assertNull(ItemCharges.classify("Bronze arrow", false))
	}

	@Test
	fun theBindingNecklaceCountsFromVoidsMessage() {
		game("You have 12 charges left before your Binding necklace disintegrates.")
		assertEquals(12, ItemCharges.bindingCharges)
		game("You have 1 charge left before your Binding necklace disintegrates.")
		assertEquals(1, ItemCharges.bindingCharges)
	}

	@Test
	fun theRecoilShatteringIsNotifiedWhenAsked() {
		game("Your Ring of Recoil has shattered.")
		assertNull(ItemCharges.lastNotification)
		ItemCharges.recoilNotification = true
		game("Your Ring of Recoil has shattered.")
		assertEquals("Your Ring of Recoil has shattered", ItemCharges.lastNotification)
	}

	@Test
	fun theWarningColours() {
		assertEquals(ItemCharges.veryLowWarningColor, ItemCharges.colour(1))
		assertEquals(ItemCharges.lowWarningColor, ItemCharges.colour(2))
		assertEquals(-1, ItemCharges.colour(3))
	}
}
