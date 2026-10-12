package com.jagex.game.runetek5.toolkit.opengl.effect

import com.jagex.game.runetek5.graphics.texture.OpenGl1DTexture
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

@OriginalClass("client!tn")
class RipplingWaterEffect : MaterialEffectGL {

	@OriginalMember(owner = "client!tn", name = "l", descriptor = "Lclient!jv;")
	private var glLists: GlRenderList? = null

	@OriginalMember(owner = "client!tn", name = "f", descriptor = "Lclient!lc;")
	private val textureContainer: TextureContainerGL?

	@OriginalMember(owner = "client!tn", name = "m", descriptor = "Lclient!wha;")
	private val texture1d: OpenGl1DTexture

	@OriginalMember(owner = "client!tn", name = "<init>", descriptor = "(Lclient!qo;Lclient!lc;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) glWaterNoiseTextureSet: TextureContainerGL?) : super(glToolkit) {
		this.textureContainer = glWaterNoiseTextureSet
		this.buildDisplayList()
		this.texture1d = OpenGl1DTexture(super.toolkit, OpenGLConstants.GL_ALPHA, 2, byteArrayOf(0, -1), OpenGLConstants.GL_ALPHA)
		this.texture1d.setClampToEdge()
	}

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(IIB)V")
	override fun setEffectParams(@OriginalArg(0) effectParam1: Int, @OriginalArg(1) effectParam2: Int) {
		if ((effectParam1 and 0x1) == 1) {
			if (this.textureContainer!!.texture3dSupported) {
				super.toolkit!!.setActiveTexture(this.textureContainer.waveTexture3d)
				RipplingWaterEffect.texGenPlane[2] = 0.0f
				RipplingWaterEffect.texGenPlane[3] = (super.toolkit!!.lastTickMillis % 4000).toFloat() / 4000.0f
				RipplingWaterEffect.texGenPlane[0] = 0.0f
				RipplingWaterEffect.texGenPlane[1] = 0.0f
				OpenGL.glTexGenfv(OpenGLConstants.GL_R, OpenGLConstants.GL_OBJECT_PLANE, RipplingWaterEffect.texGenPlane, 0)
			} else {
				@Pc(26) val frame: Int = super.toolkit!!.lastTickMillis % 4000 * 16 / 4000
				super.toolkit!!.setActiveTexture(this.textureContainer.waveTextures2d!![frame])
			}
		} else if (this.textureContainer!!.texture3dSupported) {
			super.toolkit!!.setActiveTexture(this.textureContainer.waveTexture3d)
			RipplingWaterEffect.texGenPlane[0] = 0.0f
			RipplingWaterEffect.texGenPlane[2] = 0.0f
			RipplingWaterEffect.texGenPlane[1] = 0.0f
			RipplingWaterEffect.texGenPlane[3] = 0.0f
			OpenGL.glTexGenfv(OpenGLConstants.GL_R, OpenGLConstants.GL_OBJECT_PLANE, RipplingWaterEffect.texGenPlane, 0)
		} else {
			super.toolkit!!.setActiveTexture(this.textureContainer.waveTextures2d!![0])
		}
	}

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(ZZ)V")
	override fun enable(@OriginalArg(0) lit: Boolean) {
		if (super.toolkit!!.fogDensity > 0) {
			@Pc(10) val depthScale: Float = -0.5f / super.toolkit!!.fogDensity.toFloat()
			super.toolkit!!.activateTexture(1)
			RipplingWaterEffect.texGenPlane[3] = super.toolkit!!.fogStartPosition * depthScale + 0.25f
			RipplingWaterEffect.texGenPlane[0] = 0.0f
			RipplingWaterEffect.texGenPlane[2] = depthScale
			RipplingWaterEffect.texGenPlane[1] = 0.0f
			OpenGL.glPushMatrix()
			OpenGL.glLoadIdentity()
			OpenGL.glTexGenfv(OpenGLConstants.GL_S, OpenGLConstants.GL_EYE_PLANE, RipplingWaterEffect.texGenPlane, 0)
			OpenGL.glPopMatrix()
			super.toolkit!!.adjustFog(super.toolkit!!.fogDensity.toFloat(), 0.5f)
			super.toolkit!!.setActiveTexture(this.texture1d)
			super.toolkit!!.activateTexture(0)
		}
		this.glLists!!.callList('\u0000')
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		OpenGL.glPushMatrix()
		OpenGL.glScalef(0.25f, 0.25f, 1.0f)
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	@OriginalMember(owner = "client!tn", name = "f", descriptor = "(I)V")
	private fun buildDisplayList() {
		this.glLists = GlRenderList(super.toolkit, 2)
		this.glLists!!.newList(0)
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.loadTextureCombineMode(7681, 260)
		super.toolkit!!.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_COLOR, 0)
		OpenGL.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR)
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S)
		super.toolkit!!.activateTexture(0)
		OpenGL.glTexEnvf(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_RGB_SCALE, 2.0f)
		if (this.textureContainer!!.texture3dSupported) {
			OpenGL.glTexGeni(OpenGLConstants.GL_R, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_OBJECT_LINEAR)
			OpenGL.glTexGeni(OpenGLConstants.GL_Q, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_OBJECT_LINEAR)
			OpenGL.glTexGenfv(OpenGLConstants.GL_Q, OpenGLConstants.GL_OBJECT_PLANE, floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f), 0)
			OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_R)
			OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_Q)
		}
		this.glLists!!.endList()
		this.glLists!!.newList(1)
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE)
		super.toolkit!!.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0)
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S)
		super.toolkit!!.activateTexture(0)
		OpenGL.glTexEnvf(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_RGB_SCALE, 1.0f)
		if (this.textureContainer.texture3dSupported) {
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_R)
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_Q)
		}
		this.glLists!!.endList()
	}

	@OriginalMember(owner = "client!tn", name = "c", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(I)V")
	override fun disable() {
		this.glLists!!.callList('\u0001')
		if (super.toolkit!!.fogDensity > 0) {
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.setActiveTexture(null)
			super.toolkit!!.adjustFog(0.0f, 1.0f)
			super.toolkit!!.activateTexture(0)
		}
		super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE)
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		OpenGL.glPopMatrix()
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(Lclient!ud;BI)V")
	override fun bindTexture(@OriginalArg(0) texture: GlTexture?, @OriginalArg(2) combineMode: Int) {
	}

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
		super.toolkit!!.loadTextureCombineMode(260, 8448)
	}

	companion object {
		@OriginalMember(owner = "client!pa", name = "c", descriptor = "[F")
		@JvmField
		val texGenPlane: FloatArray = FloatArray(4)
	}
}
