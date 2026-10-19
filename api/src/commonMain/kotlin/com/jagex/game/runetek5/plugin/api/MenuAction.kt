package com.jagex.game.runetek5.plugin.api

/**
 * What a plugin's menu entry does when clicked. [target] is the kind of thing the menu was opened on,
 * [sceneX] and [sceneY] where it is in the scene (tiles from the scene's south-west corner), and [id]
 * what it is: an npc's index for [MenuTarget.NPC], a player's index for [MenuTarget.PLAYER], an
 * item id for [MenuTarget.GROUND_ITEM], a location id for [MenuTarget.LOC], 0 for [MenuTarget.TILE].
 */
fun interface MenuAction {
	fun clicked(target: MenuTarget, sceneX: Int, sceneY: Int, id: Int)
}
