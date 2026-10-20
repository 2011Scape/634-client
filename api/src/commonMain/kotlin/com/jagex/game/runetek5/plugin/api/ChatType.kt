package com.jagex.game.runetek5.plugin.api

/** The types of a chat line ([events.ChatMessage.type]), as the client numbers them. */
object ChatType {
	/** A game message that no filter hides. */
	const val GAME: Int = 0

	/** Public chat from a moderator. */
	const val PUBLIC_MOD: Int = 1

	/** Public chat, over a player's head and in the box. */
	const val PUBLIC: Int = 2

	/** A private message received. */
	const val PRIVATE_FROM: Int = 3

	/** A game message the filter may hide. */
	const val GAME_FILTERED: Int = 4

	/** A friend logged in or out. */
	const val FRIEND_STATUS: Int = 5

	/** A private message sent. */
	const val PRIVATE_TO: Int = 6

	/** A private message received from a moderator. */
	const val PRIVATE_FROM_MOD: Int = 7

	/** A line in the friends chat channel. */
	const val FRIENDS_CHAT: Int = 9

	/** The quick chat forms of the above. */
	const val QUICK_PUBLIC: Int = 17

	const val QUICK_PRIVATE_FROM: Int = 18

	const val QUICK_PRIVATE_TO: Int = 19

	const val QUICK_FRIENDS_CHAT: Int = 20

	/** A line in the player's group. */
	const val GROUP: Int = 24

	const val QUICK_GROUP: Int = 25
}
