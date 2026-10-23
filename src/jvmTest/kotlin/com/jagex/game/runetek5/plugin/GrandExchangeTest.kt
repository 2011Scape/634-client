package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.GrandExchange
import com.jagex.game.runetek5.plugin.api.GrandExchangeOffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: the Grand Exchange plugin's offer lines and what it keeps (GrandExchange). */
class GrandExchangeTest {

	private fun offer(state: Int, sell: Boolean = false, quantity: Int = 10, done: Int = 0): GrandExchangeOffer {
		val o: GrandExchangeOffer = GrandExchangeOffer()
		o.state = state
		o.sell = sell
		o.itemId = 2363
		o.price = 1500
		o.quantity = quantity
		o.quantityDone = done
		o.coins = done * 1500
		return o
	}

	private val empty: GrandExchangeOffer = GrandExchangeOffer()

	@Test
	fun aPartTradeIsTold() {
		assertEquals("Grand Exchange: Bought 5 / 10 x Rune bar.", GrandExchange.message(offer(GrandExchangeOffer.OPEN), offer(GrandExchangeOffer.OPEN, done = 5), "Rune bar"))
		assertEquals("Grand Exchange: Sold 1,000 / 2,000 x Rune bar.", GrandExchange.message(offer(GrandExchangeOffer.OPEN, true, 2000), offer(GrandExchangeOffer.OPEN, true, 2000, 1000), "Rune bar"))
	}

	@Test
	fun finishedAndAbortedAreToldOnce() {
		val finished: GrandExchangeOffer = offer(GrandExchangeOffer.FINISHED, done = 10)
		assertEquals("Grand Exchange: Finished buying 10 x Rune bar.", GrandExchange.message(offer(GrandExchangeOffer.OPEN, done = 5), finished, "Rune bar"))
		assertNull(GrandExchange.message(finished, finished, "Rune bar"))
		assertEquals("Grand Exchange: Aborted offer to sell 10 x Rune bar. 3 / 10 sold.", GrandExchange.message(offer(GrandExchangeOffer.OPEN, true, done = 3), offer(GrandExchangeOffer.FINISHED, true, done = 3), "Rune bar"))
	}

	@Test
	fun placingCollectingAndNothingNewAreNotTold() {
		assertNull(GrandExchange.message(empty, offer(GrandExchangeOffer.SUBMITTING), "Rune bar"))
		assertNull(GrandExchange.message(empty, offer(GrandExchangeOffer.OPEN), "Rune bar"))
		assertNull(GrandExchange.message(offer(GrandExchangeOffer.FINISHED, done = 10), empty, "Rune bar"))
		assertNull(GrandExchange.message(offer(GrandExchangeOffer.OPEN, done = 4), offer(GrandExchangeOffer.OPEN, done = 4), "Rune bar"))
	}

	@Test
	fun anOfferThatTradedWhileLoggedOutIsToldAtLogin() {
		// nothing remembered for the slot: counted from nothing
		assertEquals("Grand Exchange: Bought 4 / 10 x Rune bar.", GrandExchange.message(empty, offer(GrandExchangeOffer.OPEN, done = 4), "Rune bar"))
		assertEquals("Grand Exchange: Finished buying 10 x Rune bar.", GrandExchange.message(empty, offer(GrandExchangeOffer.FINISHED, done = 10), "Rune bar"))
	}

	@Test
	fun theOffersAreKeptAndReadBack() {
		val offers: Array<GrandExchangeOffer> = Array(GrandExchangeOffer.SLOTS) { GrandExchangeOffer() }
		offers[2].set(offer(GrandExchangeOffer.OPEN, true, 2000, 7))
		val text: String = GrandExchange.encode(offers)
		val back: Array<GrandExchangeOffer> = Array(GrandExchangeOffer.SLOTS) { GrandExchangeOffer() }
		GrandExchange.decode(text, back)
		assertEquals(text, GrandExchange.encode(back))
		assertEquals(7, back[2].quantityDone)
		assertEquals(true, back[2].sell)
		assertEquals(2, back[2].slot)
		GrandExchange.decode("junk", back)
		assertEquals(GrandExchangeOffer.EMPTY, back[0].state)
		assertEquals(-1, back[2].itemId)
	}
}
