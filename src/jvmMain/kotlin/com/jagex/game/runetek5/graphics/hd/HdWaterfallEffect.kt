/*
 * Not part of the original client: 634HD (toolkit 6), copied from WaterfallEffect (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField

class HdWaterfallEffect : HdMaterialEffect {

	private val textureContainer: HdTextureContainer?

	private val listUtils: HdRenderList

	constructor(glToolkit: HdToolkit?, glWaterNoiseTextureSet: HdTextureContainer?) : super(glToolkit) {
		this.textureContainer = glWaterNoiseTextureSet
		this.listUtils = HdRenderList(glToolkit, 2)
		this.listUtils.newList(0)
		super.toolkit!!.activateTexture(1)
		if (this.textureContainer!!.texture3dSupported) {
			HdGl.glTexGeni(OpenGLConstants.GL_R, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_OBJECT_LINEAR)
			HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_R)
		}
		HdGl.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR)
		HdGl.glTexGeni(OpenGLConstants.GL_T, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR)
		HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S)
		HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_T)
		super.toolkit!!.activateTexture(0)
		this.listUtils.endList()
		this.listUtils.newList(1)
		super.toolkit!!.activateTexture(1)
		if (this.textureContainer.texture3dSupported) {
			HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_R)
		}
		HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S)
		HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_T)
		super.toolkit!!.activateTexture(0)
		this.listUtils.endList()
	}

	override fun setEffectParams(effectParam1: Int, effectParam2: Int) {
		val flowSpeed: Float = -5.0E-4f * ((effectParam1 and 0x3) + 1).toFloat()
		val animationSpeed: Float = (((effectParam1 shr 3) and 0x3) + 1).toFloat() * 5.0E-4f
		val textureScale: Float = if ((effectParam1 and 0x40) == 0) 4.8828125E-4f else 9.765625E-4f
		val flowAlongX: Boolean = (effectParam1 and 0x80) != 0
		super.toolkit!!.activateTexture(1)
		if (flowAlongX) {
			HdWaterfallEffect.PLANE_PARAMETERS[1] = 0.0f
			HdWaterfallEffect.PLANE_PARAMETERS[0] = textureScale
			HdWaterfallEffect.PLANE_PARAMETERS[3] = 0.0f
			HdWaterfallEffect.PLANE_PARAMETERS[2] = 0.0f
		} else {
			HdWaterfallEffect.PLANE_PARAMETERS[1] = 0.0f
			HdWaterfallEffect.PLANE_PARAMETERS[0] = 0.0f
			HdWaterfallEffect.PLANE_PARAMETERS[2] = textureScale
			HdWaterfallEffect.PLANE_PARAMETERS[3] = 0.0f
		}
		HdGl.glTexGenfv(OpenGLConstants.GL_S, OpenGLConstants.GL_EYE_PLANE, HdWaterfallEffect.PLANE_PARAMETERS, 0)
		HdWaterfallEffect.PLANE_PARAMETERS[0] = 0.0f
		HdWaterfallEffect.PLANE_PARAMETERS[1] = textureScale
		HdWaterfallEffect.PLANE_PARAMETERS[3] = flowSpeed * super.toolkit!!.lastTickMillis.toFloat() % 1.0f
		HdWaterfallEffect.PLANE_PARAMETERS[2] = 0.0f
		HdGl.glTexGenfv(8193, 9474, HdWaterfallEffect.PLANE_PARAMETERS, 0)
		if (this.textureContainer!!.texture3dSupported) {
			HdWaterfallEffect.PLANE_PARAMETERS[0] = 0.0f
			HdWaterfallEffect.PLANE_PARAMETERS[3] = super.toolkit!!.lastTickMillis.toFloat() * animationSpeed % 1.0f
			HdWaterfallEffect.PLANE_PARAMETERS[2] = 0.0f
			HdWaterfallEffect.PLANE_PARAMETERS[1] = 0.0f
			HdGl.glTexGenfv(OpenGLConstants.GL_R, OpenGLConstants.GL_OBJECT_PLANE, HdWaterfallEffect.PLANE_PARAMETERS, 0)
		} else {
			val frame: Int = (super.toolkit!!.lastTickMillis.toFloat() * 16.0f * animationSpeed).toInt()
			super.toolkit!!.setActiveTexture(this.textureContainer.billowTextures2d!![frame % 16])
		}
		super.toolkit!!.activateTexture(0)
	}

	override fun bindTexture(texture: HdTexture?, combineMode: Int) {
		super.toolkit!!.setActiveTexture(texture)
		super.toolkit!!.setColourOp(combineMode)
	}

	override fun disable() {
		this.listUtils.callList('\u0001')
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.setActiveTexture(null)
		super.toolkit!!.activateTexture(0)
	}

	override fun enable(lit: Boolean) {
		this.listUtils.callList('\u0000')
		if (this.textureContainer!!.texture3dSupported) {
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.setActiveTexture(this.textureContainer.texture3d)
			super.toolkit!!.activateTexture(0)
		}
	}

	override fun applyTextureCombine(lit: Boolean) {
	}

	override fun isSupported(): Boolean {
		return true
	}

	companion object {
		@JvmField
		val PLANE_PARAMETERS: FloatArray = FloatArray(4)
	}
}
