/*
 * Not part of the original client: 634HD (toolkit 6), copied from PostProcessEffect (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.cache.node.Node
import kotlin.jvm.JvmField

abstract class HdPostProcessEffect : Node {

	@JvmField
	var isActive: Boolean = false

	@JvmField
	val activeToolkit: HdToolkit?

	constructor(glToolkit: HdToolkit?) : super() {
		this.activeToolkit = glToolkit
	}

	abstract fun draw(depthTexture: Hd2DTexture?, colorTexture: Hd2DTexture?, pass: Int)

	abstract fun isLoaded(): Boolean

	abstract fun resize(height: Int, width: Int)

	abstract fun endPass(pass: Int)

	abstract fun createShaders(): Boolean

	fun requiresDepth(): Boolean {
		return false
	}

	fun isActive(): Boolean {
		return this.isActive
	}

	open fun getFormat(): Int {
		return 0
	}

	abstract fun destroy()

	fun getPassCount(): Int {
		return 1
	}
}
