package com.jagex.game.runetek5.plugin.api

/** An inventory the server sent: the backpack (93), the bank, a shop... [itemId] is -1 for an empty slot. */
interface ItemContainer {
	fun size(): Int

	fun itemId(slot: Int): Int

	fun amount(slot: Int): Int
}
