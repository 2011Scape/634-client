package com.jagex.game.runetek5.plugin.api

/** An inventory the server sent: the backpack (93), the bank, a shop... [itemId] is -1 for an empty slot. */
interface ItemContainer {
	/** How many slots; the other methods take 0 until this. */
	fun size(): Int

	/** The item in [slot], or -1 for an empty one. */
	fun itemId(slot: Int): Int

	/** How many of the item are in [slot]. */
	fun amount(slot: Int): Int
}
