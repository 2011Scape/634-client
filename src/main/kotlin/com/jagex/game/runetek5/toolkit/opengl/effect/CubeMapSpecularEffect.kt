package com.jagex.game.runetek5.toolkit.opengl.effect

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.toolkit.opengl.GlRenderList
import com.jagex.game.runetek5.graphics.gl.GlTexture
import com.jagex.game.runetek5.graphics.gl.GlCubeTexture
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!wk")
class CubeMapSpecularEffect : MaterialEffectGL {

	@OriginalMember(owner = "client!wk", name = "i", descriptor = "Lclient!jv;")
	private var listUtils: GlRenderList? = null

	@OriginalMember(owner = "client!wk", name = "o", descriptor = "Z")
	private var enabled: Boolean = false

	@OriginalMember(owner = "client!wk", name = "g", descriptor = "Z")
	private var isLimitedTextureUnits: Boolean = false

	@OriginalMember(owner = "client!wk", name = "p", descriptor = "[Lclient!q;")
	private var cubeMapTextures: Array<GlCubeTexture?>? = null

	@OriginalMember(owner = "client!wk", name = "<init>", descriptor = "(Lclient!qo;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?) : super(glToolkit) {
		if (glToolkit!!.arbTextureCubeMap) {
			this.isLimitedTextureUnits = glToolkit.maxTextureUnits < 3
			@Pc(26) val maxIntensity: Int = if (this.isLimitedTextureUnits) 48 else 127
			@Pc(30) val specular12: Array<ByteArray?> = Array<ByteArray?>(6) { ByteArray(4096) }
			@Pc(34) val specular96: Array<ByteArray?> = Array<ByteArray?>(6) { ByteArray(4096) }
			@Pc(38) val specular36: Array<ByteArray?> = Array<ByteArray?>(6) { ByteArray(4096) }
			@Pc(40) var texelIndex: Int = 0
			for (@Pc(42) i in 0 until 64) {
				for (@Pc(46) j in 0 until 64) {
					@Pc(57) val rawY: Float = i.toFloat() * 2.0f / 64.0f - 1.0f
					@Pc(66) val rawX: Float = j.toFloat() * 2.0f / 64.0f - 1.0f
					@Pc(81) val nz: Float = (1.0 / Math.sqrt((rawY * rawY + rawX * rawX + 1.0f).toDouble())).toFloat()
					@Pc(85) val nx: Float = rawX * nz
					@Pc(89) val ny: Float = rawY * nz
					for (@Pc(91) k in 0 until 6) {
						@Pc(98) val dot: Float
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
						@Pc(146) val intensity96: Int
						@Pc(145) val intensity36: Int
						@Pc(143) val intensity12: Int
						if (dot > 0.0f) {
							intensity96 = (maxIntensity.toDouble() * Math.pow(dot.toDouble(), 96.0)).toInt()
							intensity36 = (maxIntensity.toDouble() * Math.pow(dot.toDouble(), 36.0)).toInt()
							intensity12 = (maxIntensity.toDouble() * Math.pow(dot.toDouble(), 12.0)).toInt()
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
			this.cubeMapTextures = arrayOfNulls<GlCubeTexture>(3)
			this.cubeMapTextures!![0] = GlCubeTexture(super.toolkit, OpenGLConstants.GL_ALPHA, 64, false, specular96, OpenGLConstants.GL_ALPHA)
			this.cubeMapTextures!![1] = GlCubeTexture(super.toolkit, OpenGLConstants.GL_ALPHA, 64, false, specular36, OpenGLConstants.GL_ALPHA)
			this.cubeMapTextures!![2] = GlCubeTexture(super.toolkit, OpenGLConstants.GL_ALPHA, 64, false, specular12, OpenGLConstants.GL_ALPHA)
			this.buildDisplayList()
		}
	}

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(I)V")
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

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(Lclient!ud;BI)V")
	override fun bindTexture(@OriginalArg(0) texture: GlTexture?, @OriginalArg(2) combineMode: Int) {
		super.toolkit!!.setActiveTexture(texture)
		super.toolkit!!.setColourOp(combineMode)
	}

	@OriginalMember(owner = "client!wk", name = "e", descriptor = "(I)V")
	private fun buildDisplayList() {
		this.listUtils = GlRenderList(super.toolkit, 2)
		this.listUtils!!.newList(0)
		super.toolkit!!.activateTexture(1)
		OpenGL.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_NORMAL_MAP)
		OpenGL.glTexGeni(OpenGLConstants.GL_T, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_NORMAL_MAP)
		OpenGL.glTexGeni(OpenGLConstants.GL_R, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_NORMAL_MAP)
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S)
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_T)
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_R)
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		OpenGL.glLoadIdentity()
		OpenGL.glRotatef(22.5f, 1.0f, 0.0f, 0.0f)
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
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
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S)
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_T)
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_R)
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		OpenGL.glLoadIdentity()
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
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

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(IIB)V")
	override fun setEffectParams(@OriginalArg(0) effectParam1: Int, @OriginalArg(1) effectParam2: Int) {
		if (this.enabled) {
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.setActiveTexture(this.cubeMapTextures!![effectParam1 - 1])
			super.toolkit!!.activateTexture(0)
		}
	}

	@OriginalMember(owner = "client!wk", name = "c", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(ZZ)V")
	override fun enable(@OriginalArg(0) lit: Boolean) {
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

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
		super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, 7681)
	}
}
