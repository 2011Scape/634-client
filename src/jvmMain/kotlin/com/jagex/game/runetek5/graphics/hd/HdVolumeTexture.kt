/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlVolumeTexture (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField

class HdVolumeTexture : HdTexture {

	private var framebufferAttachment: Int = -1

	private var framebufferTarget: Int = -1

	@JvmField
	val width: Int

	@JvmField
	val depth: Int

	@JvmField
	val height: Int

	constructor(glToolkit: HdToolkit?, internalFormat: Int, width: Int, height: Int, depth: Int, pixels: ByteArray?, format: Int) : super(glToolkit, 32879, internalFormat, width * height * depth, false) {
		this.width = width
		this.depth = depth
		this.height = height
		super.glToolkit!!.setActiveTexture(this)
		HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_LINES)
		HdGl.glTexImage3Dub(super.target, 0, super.internalFormat, this.width, this.height, this.depth, 0, format, OpenGLConstants.GL_UNSIGNED_BYTE, pixels, 0)
		HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_TRIANGLES)
		this.setLinearFilter(true)
	}

	constructor(glToolkit: HdToolkit?, internalFormat: Int, width: Int, height: Int, depth: Int) : super(glToolkit, 32879, internalFormat, depth * width * height, false) {
		this.width = width
		this.height = height
		this.depth = depth
		super.glToolkit!!.setActiveTexture(this)
		HdGl.glTexImage3Dub(super.target, 0, super.internalFormat, this.width, this.height, this.depth, 0, HdTexture.getBaseFormat(super.internalFormat), OpenGLConstants.GL_UNSIGNED_BYTE, null, 0)
		this.setLinearFilter(true)
	}

	fun copyFromFramebuffer(width: Int, zOffset: Int, height: Int) {
		super.glToolkit!!.setActiveTexture(this)
		HdGl.glCopyTexSubImage3D(super.target, OpenGLConstants.GL_POINTS, OpenGLConstants.GL_POINTS, OpenGLConstants.GL_POINTS, zOffset, 0, 0, width, height)
		HdGl.glFlush()
	}

	override fun detach() {
		HdGl.glFramebufferTexture3DEXT(this.framebufferTarget, this.framebufferAttachment, super.target, 0, 0, 0)
		this.framebufferAttachment = -1
		this.framebufferTarget = -1
	}
}
