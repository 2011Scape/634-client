package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmField

/** A width and a height in pixels, what a [PanelOverlay] drew. Since API 12. */
class Size(
	@JvmField var width: Int,
	@JvmField var height: Int,
) {
	constructor() : this(0, 0)

	fun set(width: Int, height: Int): Size {
		this.width = width
		this.height = height
		return this
	}
}
