/*
 * Not part of the original client: 634HD (toolkit 6), copied from MaterialEffectGL (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import kotlin.jvm.JvmField

abstract class HdMaterialEffect {

	@JvmField
	val toolkit: HdToolkit?

	constructor(glToolkit: HdToolkit?) {
		this.toolkit = glToolkit
	}

	abstract fun disable()

	abstract fun enable(lit: Boolean)

	abstract fun setEffectParams(effectParam1: Int, effectParam2: Int)

	abstract fun isSupported(): Boolean

	abstract fun applyTextureCombine(lit: Boolean)

	abstract fun bindTexture(texture: HdTexture?, combineMode: Int)
}
