package com.jagex.game.runetek5.plugin.api

/** Receives the players of a list: friends, ignores, the friends chat. */
fun interface NameVisitor {
	/** [worldId] is 0 when the player is offline, [rank] the friends chat rank (-1 for none, 0 friend, up to 7 owner). */
	fun visit(name: String?, worldId: Int, rank: Int)
}
