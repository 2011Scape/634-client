package com.jagex.game.runetek5.plugin.api.events

import com.jagex.game.runetek5.plugin.api.GrandExchangeOffer
import kotlin.jvm.JvmField

/**
 * See [Events.GRAND_EXCHANGE_OFFER_CHANGED]: [offer] is the slot as the server sent it now. One reused
 * instance. Since API 35.
 */
class GrandExchangeOfferChanged {
	@JvmField
	val offer: GrandExchangeOffer = GrandExchangeOffer()
}
