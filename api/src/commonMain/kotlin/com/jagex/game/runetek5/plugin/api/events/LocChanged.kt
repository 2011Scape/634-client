package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * See [Events.LOC_CHANGED]: the server changed the location on scene tile ([sceneX], [sceneY]) of floor
 * [level], in its [layer] (0 wall, 1 wall decoration, 2 object, 3 floor decoration): [oldId] was there (-1
 * for nothing) and [newId] is now (-1 when it was taken away), with its [shape] and [rotation]. A tree
 * felled to a stump, a rock mined out, a door opened. For a location bigger than a tile, its south-west
 * tile. One reused instance. Since API 22.
 */
class LocChanged {
	@JvmField
	var sceneX: Int = 0

	@JvmField
	var sceneY: Int = 0

	@JvmField
	var level: Int = 0

	@JvmField
	var layer: Int = 0

	@JvmField
	var oldId: Int = -1

	@JvmField
	var newId: Int = -1

	@JvmField
	var shape: Int = 0

	@JvmField
	var rotation: Int = 0
}
