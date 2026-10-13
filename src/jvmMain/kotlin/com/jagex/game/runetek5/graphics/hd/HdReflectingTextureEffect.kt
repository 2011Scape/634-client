/*
 * Not part of the original client: 634HD (toolkit 6), copied from ReflectingTextureEffect (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext

class HdReflectingTextureEffect : HdMaterialEffect {

	private var loaded: Boolean = false

	private var listUtils: HdRenderList? = null

	constructor(glToolkit: HdToolkit?) : super(glToolkit) {
		if (glToolkit!!.arbTextureCubeMap) {
			this.listUtils = HdRenderList(glToolkit, 2)
			this.listUtils!!.newList(0)
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.loadTextureCombineMode(34165, 7681)
			super.toolkit!!.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_ALPHA, 2)
			super.toolkit!!.setTextureTransparency(0, 34167)
			HdGl.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_REFLECTION_MAP)
			HdGl.glTexGeni(OpenGLConstants.GL_T, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_REFLECTION_MAP)
			HdGl.glTexGeni(OpenGLConstants.GL_R, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_REFLECTION_MAP)
			HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S)
			HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_T)
			HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_R)
			super.toolkit!!.activateTexture(0)
			this.listUtils!!.endList()
			this.listUtils!!.newList(1)
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE)
			super.toolkit!!.setTexture(OpenGLConstants.GL_CONSTANT, OpenGLConstants.GL_SRC_ALPHA, 2)
			super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE)
			HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S)
			HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_T)
			HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_R)
			HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
			HdGl.glLoadIdentity()
			HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
			super.toolkit!!.activateTexture(0)
			this.listUtils!!.endList()
		}
	}

	override fun disable() {
		if (this.loaded) {
			this.listUtils!!.callList('\u0001')
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.setActiveTexture(null)
			super.toolkit!!.activateTexture(0)
		} else {
			super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE)
		}
		super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE)
		this.loaded = false
	}

	override fun bindTexture(texture: HdTexture?, combineMode: Int) {
		super.toolkit!!.setActiveTexture(texture)
		super.toolkit!!.setColourOp(combineMode)
	}

	override fun applyTextureCombine(lit: Boolean) {
		super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, 7681)
	}

	override fun isSupported(): Boolean {
		return true
	}

	override fun enable(lit: Boolean) {
		val environmentMap: HdCubeTexture? = super.toolkit!!.getCubeMap()
		if (this.listUtils == null || environmentMap == null || !lit) {
			super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_PREVIOUS)
			return
		}
		this.listUtils!!.callList('\u0000')
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.setActiveTexture(environmentMap)
		HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		HdGl.glLoadMatrixf(super.toolkit!!.inverseModelViewMatrix.getRotationColumnMajor(), 0)
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		super.toolkit!!.activateTexture(0)
		this.loaded = true
	}

	override fun setEffectParams(effectParam1: Int, effectParam2: Int) {
	}
}
