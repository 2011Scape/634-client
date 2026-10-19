package com.jagex.game.runetek5.plugin.api.events

import com.jagex.game.runetek5.plugin.api.Graphics
import kotlin.jvm.JvmField

/** See [Events.FRAME_DRAW]: the whole frame's size, and what to draw with. One reused instance. */
class FrameDraw(@JvmField val graphics: Graphics) {
	@JvmField
	var width: Int = 0

	@JvmField
	var height: Int = 0
}
