package com.jagex.game.runetek5.graphics.texture

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.gl.GlTexture
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!wha")
class OpenGl1DTexture : GlTexture {

	@OriginalMember(owner = "client!wha", name = "v", descriptor = "I")
	private val width: Int

	@OriginalMember(owner = "client!wha", name = "<init>", descriptor = "(Lclient!qo;II[BI)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) internalFormat: Int, @OriginalArg(2) width: Int, @OriginalArg(3) data: ByteArray?, @OriginalArg(4) format: Int) : super(glToolkit, 3552, internalFormat, width, false) {
		this.width = width
		super.glToolkit!!.setActiveTexture(this)
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_LINES)
		OpenGL.glTexImage1Dub(super.target, 0, super.internalFormat, this.width, 0, format, OpenGLConstants.GL_UNSIGNED_BYTE, data, 0)
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_TRIANGLES)
		this.setLinearFilter(true)
	}

	@OriginalMember(owner = "client!wha", name = "a", descriptor = "(I)V")
	override fun detach() {
	}

	@OriginalMember(owner = "client!wha", name = "a", descriptor = "(BZ)V")
	fun setClampToEdge() {
		super.glToolkit!!.setActiveTexture(this)
		OpenGL.glTexParameteri(super.target, OpenGLConstants.GL_TEXTURE_WRAP_S, 33071)
	}
}
