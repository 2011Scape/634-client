package com.jagex.game.runetek5.plugin.api

/**
 * A player or an npc in the scene. Positions are in scene units: 512 to a tile, from the scene's
 * south-west corner ([ClientApi.sceneBaseX] and [sceneBaseY][ClientApi.sceneBaseY] give the corner's
 * tile in the world), and [level] is the floor, 0 to 3.
 */
interface Actor {
	fun x(): Int

	fun z(): Int

	fun level(): Int

	/** The actor's size in tiles along one side. */
	fun sizeInTiles(): Int

	/** How high above the ground the point over the actor's head is, in scene units. */
	fun overheadHeight(): Int

	/** The health bar's fill, 0 to 255, or 0 when no bar shows. */
	fun healthRatio(): Int

	/** Whether the last frame drew this actor. */
	fun isDrawnThisFrame(): Boolean
}
