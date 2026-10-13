/*
 * Not part of the original client: 634HD (toolkit 6), copied from UnlitEffect (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd


class HdUnlitEffect : HdMaterialEffect {

	constructor(glToolkit: HdToolkit?) : super(glToolkit)

	override fun disable() {
		super.toolkit!!.setHideLights(false)
	}

	override fun bindTexture(texture: HdTexture?, combineMode: Int) {
		super.toolkit!!.setActiveTexture(texture)
		super.toolkit!!.setColourOp(combineMode)
	}

	override fun setEffectParams(effectParam1: Int, effectParam2: Int) {
	}

	override fun isSupported(): Boolean {
		return true
	}

	override fun enable(lit: Boolean) {
		super.toolkit!!.setHideLights(true)
	}

	override fun applyTextureCombine(lit: Boolean) {
	}
}
