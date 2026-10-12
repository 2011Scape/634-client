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

@OriginalClass("client!uv")
class ReflectingTextureEffect : MaterialEffectGL {

	@OriginalMember(owner = "client!uv", name = "n", descriptor = "Z")
	private var loaded: Boolean = false

	@OriginalMember(owner = "client!uv", name = "o", descriptor = "Lclient!jv;")
	private var listUtils: GlRenderList? = null

	@OriginalMember(owner = "client!uv", name = "<init>", descriptor = "(Lclient!qo;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?) : super(glToolkit) {
		if (glToolkit!!.arbTextureCubeMap) {
			this.listUtils = GlRenderList(glToolkit, 2)
			this.listUtils!!.newList(0)
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.loadTextureCombineMode(34165, 7681)
			super.toolkit!!.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_ALPHA, 2)
			super.toolkit!!.setTextureTransparency(0, 34167)
			OpenGL.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_REFLECTION_MAP)
			OpenGL.glTexGeni(OpenGLConstants.GL_T, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_REFLECTION_MAP)
			OpenGL.glTexGeni(OpenGLConstants.GL_R, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_REFLECTION_MAP)
			OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S)
			OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_T)
			OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_R)
			super.toolkit!!.activateTexture(0)
			this.listUtils!!.endList()
			this.listUtils!!.newList(1)
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE)
			super.toolkit!!.setTexture(OpenGLConstants.GL_CONSTANT, OpenGLConstants.GL_SRC_ALPHA, 2)
			super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE)
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S)
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_T)
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_R)
			OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
			OpenGL.glLoadIdentity()
			OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
			super.toolkit!!.activateTexture(0)
			this.listUtils!!.endList()
		}
	}

	@OriginalMember(owner = "client!uv", name = "a", descriptor = "(I)V")
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

	@OriginalMember(owner = "client!uv", name = "a", descriptor = "(Lclient!ud;BI)V")
	override fun bindTexture(@OriginalArg(0) texture: GlTexture?, @OriginalArg(2) combineMode: Int) {
		super.toolkit!!.setActiveTexture(texture)
		super.toolkit!!.setColourOp(combineMode)
	}

	@OriginalMember(owner = "client!uv", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
		super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, 7681)
	}

	@OriginalMember(owner = "client!uv", name = "c", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!uv", name = "a", descriptor = "(ZZ)V")
	override fun enable(@OriginalArg(0) lit: Boolean) {
		@Pc(13) val environmentMap: GlCubeTexture? = super.toolkit!!.getCubeMap()
		if (this.listUtils == null || environmentMap == null || !lit) {
			super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_PREVIOUS)
			return
		}
		this.listUtils!!.callList('\u0000')
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.setActiveTexture(environmentMap)
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		OpenGL.glLoadMatrixf(super.toolkit!!.inverseModelViewMatrix.getRotationColumnMajor(), 0)
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		super.toolkit!!.activateTexture(0)
		this.loaded = true
	}

	@OriginalMember(owner = "client!uv", name = "a", descriptor = "(IIB)V")
	override fun setEffectParams(@OriginalArg(0) effectParam1: Int, @OriginalArg(1) effectParam2: Int) {
	}
}
