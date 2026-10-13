/*
 * Not part of the original client: 634HD (toolkit 6), copied from CubeMapSpecularEffect (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.math.pow

class HdCubeMapSpecularEffect : HdMaterialEffect {

	private var listUtils: HdRenderList? = null

	private var enabled: Boolean = false

	private var isLimitedTextureUnits: Boolean = false

	private var cubeMapTextures: Array<HdCubeTexture?>? = null

	constructor(glToolkit: HdToolkit?) : super(glToolkit) {
		if (glToolkit!!.arbTextureCubeMap) {
			this.isLimitedTextureUnits = glToolkit.maxTextureUnits < 3
			val maxIntensity: Int = if (this.isLimitedTextureUnits) 48 else 127
			val specular12: Array<ByteArray?> = Array<ByteArray?>(6) { ByteArray(4096) }
			val specular96: Array<ByteArray?> = Array<ByteArray?>(6) { ByteArray(4096) }
			val specular36: Array<ByteArray?> = Array<ByteArray?>(6) { ByteArray(4096) }
			var texelIndex: Int = 0
			for (i in 0 until 64) {
				for (j in 0 until 64) {
					val rawY: Float = i.toFloat() * 2.0f / 64.0f - 1.0f
					val rawX: Float = j.toFloat() * 2.0f / 64.0f - 1.0f
					val nz: Float = (1.0 / kotlin.math.sqrt((rawY * rawY + rawX * rawX + 1.0f).toDouble())).toFloat()
					val nx: Float = rawX * nz
					val ny: Float = rawY * nz
					for (k in 0 until 6) {
						val dot: Float
						if (k == 0) {
							dot = -nx
						} else if (k == 1) {
							dot = nx
						} else if (k == 2) {
							dot = ny
						} else if (k == 3) {
							dot = -ny
						} else if (k == 4) {
							dot = nz
						} else {
							dot = -nz
						}
						val intensity96: Int
						val intensity36: Int
						val intensity12: Int
						if (dot > 0.0f) {
							intensity96 = (maxIntensity.toDouble() * (dot.toDouble()).pow(96.0)).toInt()
							intensity36 = (maxIntensity.toDouble() * (dot.toDouble()).pow(36.0)).toInt()
							intensity12 = (maxIntensity.toDouble() * (dot.toDouble()).pow(12.0)).toInt()
						} else {
							intensity12 = 0
							intensity36 = 0
							intensity96 = 0
						}
						specular96[k]!![texelIndex] = intensity96.toByte()
						specular36[k]!![texelIndex] = intensity36.toByte()
						specular12[k]!![texelIndex] = intensity12.toByte()
					}
					texelIndex++
				}
			}
			this.cubeMapTextures = arrayOfNulls<HdCubeTexture>(3)
			this.cubeMapTextures!![0] = HdCubeTexture(super.toolkit, OpenGLConstants.GL_ALPHA, 64, false, specular96, OpenGLConstants.GL_ALPHA)
			this.cubeMapTextures!![1] = HdCubeTexture(super.toolkit, OpenGLConstants.GL_ALPHA, 64, false, specular36, OpenGLConstants.GL_ALPHA)
			this.cubeMapTextures!![2] = HdCubeTexture(super.toolkit, OpenGLConstants.GL_ALPHA, 64, false, specular12, OpenGLConstants.GL_ALPHA)
			this.buildDisplayList()
		}
	}

	override fun disable() {
		if (this.enabled) {
			if (!this.isLimitedTextureUnits) {
				super.toolkit!!.activateTexture(2)
				super.toolkit!!.setActiveTexture(null)
			}
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.setActiveTexture(null)
			super.toolkit!!.activateTexture(0)
			this.listUtils!!.callList('\u0001')
			this.enabled = false
		} else {
			super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE)
		}
		super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE)
	}

	override fun bindTexture(texture: HdTexture?, combineMode: Int) {
		super.toolkit!!.setActiveTexture(texture)
		super.toolkit!!.setColourOp(combineMode)
	}

	private fun buildDisplayList() {
		this.listUtils = HdRenderList(super.toolkit, 2)
		this.listUtils!!.newList(0)
		super.toolkit!!.activateTexture(1)
		HdGl.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_NORMAL_MAP)
		HdGl.glTexGeni(OpenGLConstants.GL_T, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_NORMAL_MAP)
		HdGl.glTexGeni(OpenGLConstants.GL_R, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_NORMAL_MAP)
		HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S)
		HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_T)
		HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_R)
		HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		HdGl.glLoadIdentity()
		HdGl.glRotatef(22.5f, 1.0f, 0.0f, 0.0f)
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		if (this.isLimitedTextureUnits) {
			super.toolkit!!.loadTextureCombineMode(260, 7681)
			super.toolkit!!.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_ALPHA, 0)
			super.toolkit!!.setTextureTransparency(0, 34167)
		} else {
			super.toolkit!!.loadTextureCombineMode(7681, 8448)
			super.toolkit!!.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_COLOR, 0)
			super.toolkit!!.activateTexture(2)
			super.toolkit!!.loadTextureCombineMode(260, 7681)
			super.toolkit!!.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_COLOR, 0)
			super.toolkit!!.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_ALPHA, 1)
			super.toolkit!!.setTextureTransparency(0, 34167)
		}
		super.toolkit!!.activateTexture(0)
		this.listUtils!!.endList()
		this.listUtils!!.newList(1)
		super.toolkit!!.activateTexture(1)
		HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S)
		HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_T)
		HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_R)
		HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		HdGl.glLoadIdentity()
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		if (this.isLimitedTextureUnits) {
			super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE)
			super.toolkit!!.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0)
			super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE)
		} else {
			super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE)
			super.toolkit!!.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0)
			super.toolkit!!.activateTexture(2)
			super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE)
			super.toolkit!!.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0)
			super.toolkit!!.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_COLOR, 1)
			super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE)
		}
		super.toolkit!!.activateTexture(0)
		this.listUtils!!.endList()
	}

	override fun setEffectParams(effectParam1: Int, effectParam2: Int) {
		if (this.enabled) {
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.setActiveTexture(this.cubeMapTextures!![effectParam1 - 1])
			super.toolkit!!.activateTexture(0)
		}
	}

	override fun isSupported(): Boolean {
		return true
	}

	override fun enable(lit: Boolean) {
		if (this.listUtils == null || !lit) {
			super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_PREVIOUS)
			return
		}
		if (!this.isLimitedTextureUnits) {
			super.toolkit!!.activateTexture(2)
			super.toolkit!!.setActiveTexture(super.toolkit!!.texture2d)
			super.toolkit!!.activateTexture(0)
		}
		this.listUtils!!.callList('\u0000')
		this.enabled = true
	}

	override fun applyTextureCombine(lit: Boolean) {
		super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, 7681)
	}
}
