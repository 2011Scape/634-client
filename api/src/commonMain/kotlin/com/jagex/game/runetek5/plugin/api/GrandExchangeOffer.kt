package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmField

/**
 * One of the player's six Grand Exchange offer slots, as the server last sent it: RuneLite's
 * GrandExchangeOffer. [state] is one of the constants below; an offer the player aborted is [FINISHED]
 * with [quantityDone] short of [quantity]. Since API 35.
 */
class GrandExchangeOffer {
	/** The slot, 0 to 5. */
	@JvmField
	var slot: Int = 0

	@JvmField
	var state: Int = EMPTY

	/** Whether it sells [itemId]; false for a buy offer. */
	@JvmField
	var sell: Boolean = false

	/** The item offered, -1 for an empty slot. */
	@JvmField
	var itemId: Int = -1

	/** The price asked for each one. */
	@JvmField
	var price: Int = 0

	/** How many were offered. */
	@JvmField
	var quantity: Int = 0

	/** How many have been bought or sold so far. */
	@JvmField
	var quantityDone: Int = 0

	/** The coins spent (a buy) or received (a sell) so far. */
	@JvmField
	var coins: Int = 0

	/** Copies [other] into this one. */
	fun set(other: GrandExchangeOffer) {
		slot = other.slot
		state = other.state
		sell = other.sell
		itemId = other.itemId
		price = other.price
		quantity = other.quantity
		quantityDone = other.quantityDone
		coins = other.coins
	}

	companion object {
		/** Nothing offered. */
		const val EMPTY: Int = 0

		/** Just placed, not yet on the exchange. */
		const val SUBMITTING: Int = 1

		/** On the exchange, waiting to trade. */
		const val OPEN: Int = 2

		/** Done: all of it traded, or aborted. */
		const val FINISHED: Int = 5

		/** How many slots there are. */
		const val SLOTS: Int = 6
	}
}
