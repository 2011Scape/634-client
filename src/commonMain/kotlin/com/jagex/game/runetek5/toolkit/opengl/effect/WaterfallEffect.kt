package com.jagex.game.runetek5.toolkit.opengl.effect

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.toolkit.opengl.GlRenderList
import com.jagex.game.runetek5.graphics.gl.GlTexture
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import com.jagex.game.runetek5.graphics.texture.water.TextureContainerGL
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!gn")
class WaterfallEffect : MaterialEffectGL {

	@OriginalMember(owner = "client!gn", name = "q", descriptor = "Lclient!lc;")
	private val textureContainer: TextureContainerGL?

	@OriginalMember(owner = "client!gn", name = "l", descriptor = "Lclient!jv;")
	private val listUtils: GlRenderList

	@OriginalMember(owner = "client!gn", name = "<init>", descriptor = "(Lclient!qo;Lclient!lc;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) glWaterNoiseTextureSet: TextureContainerGL?) : super(glToolkit) {
		this.textureContainer = glWaterNoiseTextureSet
		this.listUtils = GlRenderList(glToolkit, 2)
		this.listUtils.newList(0)
		super.toolkit!!.activateTexture(1)
		if (this.textureContainer!!.texture3dSupported) {
			OpenGL.glTexGeni(OpenGLConstants.GL_R, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_OBJECT_LINEAR)
			OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_R)
		}
		OpenGL.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR)
		OpenGL.glTexGeni(OpenGLConstants.GL_T, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR)
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S)
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_T)
		super.toolkit!!.activateTexture(0)
		this.listUtils.endList()
		this.listUtils.newList(1)
		super.toolkit!!.activateTexture(1)
		if (this.textureContainer.texture3dSupported) {
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_R)
		}
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S)
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_T)
		super.toolkit!!.activateTexture(0)
		this.listUtils.endList()
	}

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(IIB)V")
	override fun setEffectParams(@OriginalArg(0) effectParam1: Int, @OriginalArg(1) effectParam2: Int) {
		@Pc(14) val flowSpeed: Float = -5.0E-4f * ((effectParam1 and 0x3) + 1).toFloat()
		@Pc(25) val animationSpeed: Float = (((effectParam1 shr 3) and 0x3) + 1).toFloat() * 5.0E-4f
		@Pc(36) val textureScale: Float = if ((effectParam1 and 0x40) == 0) 4.8828125E-4f else 9.765625E-4f
		@Pc(44) val flowAlongX: Boolean = (effectParam1 and 0x80) != 0
		super.toolkit!!.activateTexture(1)
		if (flowAlongX) {
			WaterfallEffect.PLANE_PARAMETERS[1] = 0.0f
			WaterfallEffect.PLANE_PARAMETERS[0] = textureScale
			WaterfallEffect.PLANE_PARAMETERS[3] = 0.0f
			WaterfallEffect.PLANE_PARAMETERS[2] = 0.0f
		} else {
			WaterfallEffect.PLANE_PARAMETERS[1] = 0.0f
			WaterfallEffect.PLANE_PARAMETERS[0] = 0.0f
			WaterfallEffect.PLANE_PARAMETERS[2] = textureScale
			WaterfallEffect.PLANE_PARAMETERS[3] = 0.0f
		}
		OpenGL.glTexGenfv(OpenGLConstants.GL_S, OpenGLConstants.GL_EYE_PLANE, WaterfallEffect.PLANE_PARAMETERS, 0)
		WaterfallEffect.PLANE_PARAMETERS[0] = 0.0f
		WaterfallEffect.PLANE_PARAMETERS[1] = textureScale
		WaterfallEffect.PLANE_PARAMETERS[3] = flowSpeed * super.toolkit!!.lastTickMillis.toFloat() % 1.0f
		WaterfallEffect.PLANE_PARAMETERS[2] = 0.0f
		OpenGL.glTexGenfv(8193, 9474, WaterfallEffect.PLANE_PARAMETERS, 0)
		if (this.textureContainer!!.texture3dSupported) {
			WaterfallEffect.PLANE_PARAMETERS[0] = 0.0f
			WaterfallEffect.PLANE_PARAMETERS[3] = super.toolkit!!.lastTickMillis.toFloat() * animationSpeed % 1.0f
			WaterfallEffect.PLANE_PARAMETERS[2] = 0.0f
			WaterfallEffect.PLANE_PARAMETERS[1] = 0.0f
			OpenGL.glTexGenfv(OpenGLConstants.GL_R, OpenGLConstants.GL_OBJECT_PLANE, WaterfallEffect.PLANE_PARAMETERS, 0)
		} else {
			@Pc(170) val frame: Int = (super.toolkit!!.lastTickMillis.toFloat() * 16.0f * animationSpeed).toInt()
			super.toolkit!!.setActiveTexture(this.textureContainer.billowTextures2d!![frame % 16])
		}
		super.toolkit!!.activateTexture(0)
	}

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(Lclient!ud;BI)V")
	override fun bindTexture(@OriginalArg(0) texture: GlTexture?, @OriginalArg(2) combineMode: Int) {
		super.toolkit!!.setActiveTexture(texture)
		super.toolkit!!.setColourOp(combineMode)
	}

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(I)V")
	override fun disable() {
		this.listUtils.callList('\u0001')
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.setActiveTexture(null)
		super.toolkit!!.activateTexture(0)
	}

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(ZZ)V")
	override fun enable(@OriginalArg(0) lit: Boolean) {
		this.listUtils.callList('\u0000')
		if (this.textureContainer!!.texture3dSupported) {
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.setActiveTexture(this.textureContainer.texture3d)
			super.toolkit!!.activateTexture(0)
		}
	}

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
	}

	@OriginalMember(owner = "client!gn", name = "c", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return true
	}

	companion object {
		@OriginalMember(owner = "client!mf", name = "v", descriptor = "[F")
		@JvmField
		val PLANE_PARAMETERS: FloatArray = FloatArray(4)
	}
}
