package com.jagex.game.runetek5.plugin.api

/** Sees each item on the ground in the scene, in [ClientApi.groundItems]: where (scene tiles and level), what, and how many. */
fun interface GroundItemVisitor {
	fun visit(sceneX: Int, sceneY: Int, level: Int, itemId: Int, amount: Int)
}
