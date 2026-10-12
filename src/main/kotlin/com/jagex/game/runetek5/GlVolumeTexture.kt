package com.jagex.game.runetek5

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.gl.GlTexture
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!gj")
class GlVolumeTexture : GlTexture {

	@OriginalMember(owner = "client!gj", name = "u", descriptor = "I")
	private var framebufferAttachment: Int = -1

	@OriginalMember(owner = "client!gj", name = "x", descriptor = "I")
	private var framebufferTarget: Int = -1

	@OriginalMember(owner = "client!gj", name = "s", descriptor = "I")
	@JvmField
	val width: Int

	@OriginalMember(owner = "client!gj", name = "r", descriptor = "I")
	@JvmField
	val depth: Int

	@OriginalMember(owner = "client!gj", name = "y", descriptor = "I")
	@JvmField
	val height: Int

	@OriginalMember(owner = "client!gj", name = "<init>", descriptor = "(Lclient!qo;IIII[BI)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) internalFormat: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) depth: Int, @OriginalArg(5) pixels: ByteArray?, @OriginalArg(6) format: Int) : super(glToolkit, 32879, internalFormat, width * height * depth, false) {
		this.width = width
		this.depth = depth
		this.height = height
		super.glToolkit!!.setActiveTexture(this)
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_LINES)
		OpenGL.glTexImage3Dub(super.target, 0, super.internalFormat, this.width, this.height, this.depth, 0, format, OpenGLConstants.GL_UNSIGNED_BYTE, pixels, 0)
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_TRIANGLES)
		this.setLinearFilter(true)
	}

	@OriginalMember(owner = "client!gj", name = "<init>", descriptor = "(Lclient!qo;IIII)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) internalFormat: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) depth: Int) : super(glToolkit, 32879, internalFormat, depth * width * height, false) {
		this.width = width
		this.height = height
		this.depth = depth
		super.glToolkit!!.setActiveTexture(this)
		OpenGL.glTexImage3Dub(super.target, 0, super.internalFormat, this.width, this.height, this.depth, 0, GlTexture.getBaseFormat(super.internalFormat), OpenGLConstants.GL_UNSIGNED_BYTE, null, 0)
		this.setLinearFilter(true)
	}

	@OriginalMember(owner = "client!gj", name = "a", descriptor = "(IIIIIIII)V")
	fun copyFromFramebuffer(@OriginalArg(2) width: Int, @OriginalArg(5) zOffset: Int, @OriginalArg(6) height: Int) {
		super.glToolkit!!.setActiveTexture(this)
		OpenGL.glCopyTexSubImage3D(super.target, OpenGLConstants.GL_POINTS, OpenGLConstants.GL_POINTS, OpenGLConstants.GL_POINTS, zOffset, 0, 0, width, height)
		OpenGL.glFlush()
	}

	@OriginalMember(owner = "client!gj", name = "a", descriptor = "(I)V")
	override fun detach() {
		OpenGL.glFramebufferTexture3DEXT(this.framebufferTarget, this.framebufferAttachment, super.target, 0, 0, 0)
		this.framebufferAttachment = -1
		this.framebufferTarget = -1
	}
}
