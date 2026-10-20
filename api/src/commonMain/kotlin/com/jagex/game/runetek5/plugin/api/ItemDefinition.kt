package com.jagex.game.runetek5.plugin.api

/** An item type from the cache: what every item with this id is, whether in a container or on the ground. */
interface ItemDefinition {
	fun id(): Int

	fun name(): String?

	/** Whether several of the item take one slot. */
	fun isStackable(): Boolean

	/** Whether this is the banknote of another item; [linkedId] is then that item. */
	fun isNoted(): Boolean

	/** For an item with a banknote, the note's id; for a note, the item's; -1 for an item with neither. */
	fun linkedId(): Int

	/** The item's shop value, which the alchemy spells and the order of a ground pile go by. */
	fun value(): Int

	fun isMembers(): Boolean

	/** Whether the item can be worn. */
	fun isEquipable(): Boolean

	/** The right-click option [i] (0 to 4) of the item in an inventory, or null for none. */
	fun inventoryAction(i: Int): String?

	/** The right-click option [i] (0 to 4) of the item on the ground, or null for none. */
	fun groundAction(i: Int): String?

	/** The team the item puts its wearer on (team capes), 0 for none. */
	fun teamId(): Int

	/** The item's parameter [key] from the cache, or [default] when it has none. */
	fun intParam(key: Int, default: Int): Int

	fun stringParam(key: Int, default: String?): String?
}
