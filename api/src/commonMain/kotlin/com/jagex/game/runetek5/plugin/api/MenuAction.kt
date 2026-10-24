package com.jagex.game.runetek5.plugin.api

/**
 * What a plugin's menu entry does when clicked. [target] is the kind of thing the menu was opened on,
 * [sceneX] and [sceneY] where it is in the scene (tiles from the scene's south-west corner; an npc's or
 * a player's south-west tile when it is bigger than one), and [id]
 * what it is: an npc's index for [MenuTarget.NPC], a player's index for [MenuTarget.PLAYER], an
 * item id for [MenuTarget.GROUND_ITEM], a location id for [MenuTarget.LOC], 0 for [MenuTarget.TILE].
 * For [MenuTarget.WIDGET] the three are the component's slot (-1 for none), its [Widget.id] and the
 * item it shows (-1 for none). For [MenuTarget.WORLD_MAP] (since API 37) they are the world tile's x and y
 * and its level.
 */
fun interface MenuAction {
	/** The player clicked the plugin's entry. */
	fun clicked(target: MenuTarget, sceneX: Int, sceneY: Int, id: Int)
}
