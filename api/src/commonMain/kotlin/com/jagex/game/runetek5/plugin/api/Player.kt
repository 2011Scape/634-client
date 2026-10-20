package com.jagex.game.runetek5.plugin.api

interface Player : Actor {
	/** The player's name, or null before the appearance arrived. */
	fun name(): String?

	/** The combat level, 3 to 138. */
	fun combatLevel(): Int

	/** The player's index on this world, 0 to 2047. */
	fun index(): Int

	/** Whether this is the player at the keyboard ([ClientApi.localPlayer]). */
	fun isLocalPlayer(): Boolean

	/** The name as the client draws it, with the player's title in front when there is one. */
	fun displayName(): String?

	/** The prayer icon over the player's head, an index into the prayer icons sprite group, or -1 for none. */
	fun prayerIcon(): Int

	/** The skull over the player's head, an index into the skull sprite group, or -1 for none. */
	fun skullIcon(): Int
}
