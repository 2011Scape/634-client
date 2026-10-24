package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmField

/**
 * What a left click would act on now, from the top of the closed right-click menu: [ClientApi.hoveredTarget]
 * fills it. [kind] is [MenuTarget.NPC], [MenuTarget.PLAYER], [MenuTarget.LOC] or [MenuTarget.GROUND_ITEM];
 * [id] is the npc's or the player's index ([ClientApi.npcByIndex], [ClientApi.playerByIndex]), the location's
 * id or the item's id; [sceneX] and [sceneY] the scene tile for a location or a ground item (0 for an actor);
 * [option] the entry's option ("Talk-to", "Chop down"). Since API 36.
 */
class HoveredTarget {
	@JvmField
	var kind: MenuTarget = MenuTarget.TILE

	@JvmField
	var id: Int = -1

	@JvmField
	var sceneX: Int = 0

	@JvmField
	var sceneY: Int = 0

	@JvmField
	var option: String? = null
}
