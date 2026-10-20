package com.jagex.game.runetek5.plugin.api

interface Player : Actor {
	fun name(): String?

	fun combatLevel(): Int

	/** The player's index on this world, 0 to 2047. */
	fun index(): Int

	fun isLocalPlayer(): Boolean

	/** The name as shown, with the title and the display name the player chose, or null when unknown. */
	fun displayName(): String?

	/** The prayer icon over the player's head, an index into the prayer icons sprite group, or -1 for none. */
	fun prayerIcon(): Int

	/** The skull over the player's head, an index into the skull sprite group, or -1 for none. */
	fun skullIcon(): Int
}
