/*
 * Not part of the original client: 634HD (toolkit 6), copied from RipplingWaterEffect (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField

class HdRipplingWaterEffect : HdMaterialEffect {

	private var glLists: HdRenderList? = null

	private val textureContainer: HdTextureContainer?

	private val texture1d: Hd1DTexture

	constructor(glToolkit: HdToolkit?, glWaterNoiseTextureSet: HdTextureContainer?) : super(glToolkit) {
		this.textureContainer = glWaterNoiseTextureSet
		this.buildDisplayList()
		this.texture1d = Hd1DTexture(super.toolkit, OpenGLConstants.GL_ALPHA, 2, byteArrayOf(0, -1), OpenGLConstants.GL_ALPHA)
		this.texture1d.setClampToEdge()
	}

	override fun setEffectParams(effectParam1: Int, effectParam2: Int) {
		if ((effectParam1 and 0x1) == 1) {
			if (this.textureContainer!!.texture3dSupported) {
				super.toolkit!!.setActiveTexture(this.textureContainer.waveTexture3d)
				HdRipplingWaterEffect.texGenPlane[2] = 0.0f
				HdRipplingWaterEffect.texGenPlane[3] = (super.toolkit!!.lastTickMillis % 4000).toFloat() / 4000.0f
				HdRipplingWaterEffect.texGenPlane[0] = 0.0f
				HdRipplingWaterEffect.texGenPlane[1] = 0.0f
				HdGl.glTexGenfv(OpenGLConstants.GL_R, OpenGLConstants.GL_OBJECT_PLANE, HdRipplingWaterEffect.texGenPlane, 0)
			} else {
				val frame: Int = super.toolkit!!.lastTickMillis % 4000 * 16 / 4000
				super.toolkit!!.setActiveTexture(this.textureContainer.waveTextures2d!![frame])
			}
		} else if (this.textureContainer!!.texture3dSupported) {
			super.toolkit!!.setActiveTexture(this.textureContainer.waveTexture3d)
			HdRipplingWaterEffect.texGenPlane[0] = 0.0f
			HdRipplingWaterEffect.texGenPlane[2] = 0.0f
			HdRipplingWaterEffect.texGenPlane[1] = 0.0f
			HdRipplingWaterEffect.texGenPlane[3] = 0.0f
			HdGl.glTexGenfv(OpenGLConstants.GL_R, OpenGLConstants.GL_OBJECT_PLANE, HdRipplingWaterEffect.texGenPlane, 0)
		} else {
			super.toolkit!!.setActiveTexture(this.textureContainer.waveTextures2d!![0])
		}
	}

	override fun enable(lit: Boolean) {
		if (super.toolkit!!.fogDensity > 0) {
			val depthScale: Float = -0.5f / super.toolkit!!.fogDensity.toFloat()
			super.toolkit!!.activateTexture(1)
			HdRipplingWaterEffect.texGenPlane[3] = super.toolkit!!.fogStartPosition * depthScale + 0.25f
			HdRipplingWaterEffect.texGenPlane[0] = 0.0f
			HdRipplingWaterEffect.texGenPlane[2] = depthScale
			HdRipplingWaterEffect.texGenPlane[1] = 0.0f
			HdGl.glPushMatrix()
			HdGl.glLoadIdentity()
			HdGl.glTexGenfv(OpenGLConstants.GL_S, OpenGLConstants.GL_EYE_PLANE, HdRipplingWaterEffect.texGenPlane, 0)
			HdGl.glPopMatrix()
			super.toolkit!!.adjustFog(super.toolkit!!.fogDensity.toFloat(), 0.5f)
			super.toolkit!!.setActiveTexture(this.texture1d)
			super.toolkit!!.activateTexture(0)
		}
		this.glLists!!.callList('\u0000')
		HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		HdGl.glPushMatrix()
		HdGl.glScalef(0.25f, 0.25f, 1.0f)
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	private fun buildDisplayList() {
		this.glLists = HdRenderList(super.toolkit, 2)
		this.glLists!!.newList(0)
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.loadTextureCombineMode(7681, 260)
		super.toolkit!!.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_COLOR, 0)
		HdGl.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR)
		HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S)
		super.toolkit!!.activateTexture(0)
		HdGl.glTexEnvf(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_RGB_SCALE, 2.0f)
		if (this.textureContainer!!.texture3dSupported) {
			HdGl.glTexGeni(OpenGLConstants.GL_R, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_OBJECT_LINEAR)
			HdGl.glTexGeni(OpenGLConstants.GL_Q, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_OBJECT_LINEAR)
			HdGl.glTexGenfv(OpenGLConstants.GL_Q, OpenGLConstants.GL_OBJECT_PLANE, floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f), 0)
			HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_R)
			HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_Q)
		}
		this.glLists!!.endList()
		this.glLists!!.newList(1)
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE)
		super.toolkit!!.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0)
		HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S)
		super.toolkit!!.activateTexture(0)
		HdGl.glTexEnvf(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_RGB_SCALE, 1.0f)
		if (this.textureContainer.texture3dSupported) {
			HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_R)
			HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_Q)
		}
		this.glLists!!.endList()
	}

	override fun isSupported(): Boolean {
		return true
	}

	override fun disable() {
		this.glLists!!.callList('\u0001')
		if (super.toolkit!!.fogDensity > 0) {
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.setActiveTexture(null)
			super.toolkit!!.adjustFog(0.0f, 1.0f)
			super.toolkit!!.activateTexture(0)
		}
		super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE)
		HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		HdGl.glPopMatrix()
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	override fun bindTexture(texture: HdTexture?, combineMode: Int) {
	}

	override fun applyTextureCombine(lit: Boolean) {
		super.toolkit!!.loadTextureCombineMode(260, 8448)
	}

	companion object {
		@JvmField
		val texGenPlane: FloatArray = FloatArray(4)
	}
}
