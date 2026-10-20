package com.jagex.game.runetek5.plugin.api

/** Sees each item on the ground in the scene, in [ClientApi.groundItems]: where (scene tiles and level), what, and how many. */
fun interface GroundItemVisitor {
	/** One item of a pile; a pile's items come one after another, most valuable first. */
	fun visit(sceneX: Int, sceneY: Int, level: Int, itemId: Int, amount: Int)
}
