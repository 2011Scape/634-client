package com.jagex.game.runetek5.plugin.api.events

import com.jagex.game.runetek5.plugin.api.Graphics
import kotlin.jvm.JvmField

/**
 * See [Events.MINIMAP_DRAW]: the minimap was just drawn at ([x], [y]), [width] by [height], and what to draw
 * on it with, clipped to its box. Drawn here, a plugin's marks are under the orbs and frames the game draws
 * over the minimap, as its own dots are. One reused instance.
 */
class MinimapDraw(@JvmField val graphics: Graphics) {
	@JvmField
	var x: Int = 0

	@JvmField
	var y: Int = 0

	@JvmField
	var width: Int = 0

	@JvmField
	var height: Int = 0
}
