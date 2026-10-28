package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * See [Events.LOC_ANIMATION_CHANGED]: the server played [animationId] (-1 to stop one) on the location in
 * [layer] (0 wall, 1 wall decoration, 2 object, 3 floor decoration) of scene tile ([sceneX], [sceneY]) of
 * floor [level], given as placed with [shape] and [rotation]. [locId] is the location the client has
 * there in that layer, -1 when it has none (the animation then plays on nothing). A door or gate
 * swinging, a trap springing, a tree's roots growing. One reused instance. Since API 40.
 */
class LocAnimationChanged {
	@JvmField
	var sceneX: Int = 0

	@JvmField
	var sceneY: Int = 0

	@JvmField
	var level: Int = 0

	@JvmField
	var layer: Int = 0

	@JvmField
	var shape: Int = 0

	@JvmField
	var rotation: Int = 0

	@JvmField
	var locId: Int = -1

	@JvmField
	var animationId: Int = -1
}
