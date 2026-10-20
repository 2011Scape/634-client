package com.jagex.game.runetek5.plugin.api

/**
 * A player or an npc in the scene. Positions are in scene units: 512 to a tile, from the scene's
 * south-west corner ([ClientApi.sceneBaseX] and [sceneBaseY][ClientApi.sceneBaseY] give the corner's
 * tile in the world), and [level] is the floor, 0 to 3.
 */
interface Actor {
	/** The actor's position in scene units, west to east; shift right 9 for the tile. */
	fun x(): Int

	/** The actor's position in scene units, south to north; shift right 9 for the tile. */
	fun z(): Int

	/** The floor, 0 to 3. */
	fun level(): Int

	/** The actor's size in tiles along one side. */
	fun sizeInTiles(): Int

	/** How high above the ground the point over the actor's head is, in scene units. */
	fun overheadHeight(): Int

	/** The health bar's fill, 0 to 255, or 0 when no bar shows. */
	fun healthRatio(): Int

	/** Whether the last frame drew this actor. */
	fun isDrawnThisFrame(): Boolean

	/** The sequence (animation) the actor plays, an id in the cache's sequence list, or -1 when none. */
	fun animationId(): Int

	/** The graphic (spot animation) on the actor, an id in the cache's spot animation list, or -1 when none. */
	fun spotAnimationId(): Int

	/** The actor this one faces and deals with: fights, talks to or follows. Null when none. */
	fun interacting(): Actor?

	/**
	 * Which way the actor faces: 16384 to a full turn, 0 facing south, 4096 west, 8192 north and
	 * 12288 east.
	 */
	fun orientation(): Int

	/** Whether the step the actor is taking is a run step; false while it stands. */
	fun isRunning(): Boolean

	/** The text over the actor's head, or null while none shows. */
	fun overheadText(): String?

	/** How many hit splat slots an actor has; the slot methods take 0 until this. */
	fun hitsplatSlots(): Int

	/** The type of the hit splat in [slot], an id in the cache's hit mark list, or -1 for a slot that is empty or whose splat has gone. */
	fun hitsplatType(slot: Int): Int

	/** The damage the hit splat in [slot] shows, 0 for an empty slot. */
	fun hitsplatDamage(slot: Int): Int

	/** The client cycle the splat in [slot] shows until: it is on screen while [ClientApi.clientCycle] is below this. */
	fun hitsplatEndCycle(slot: Int): Int
}
