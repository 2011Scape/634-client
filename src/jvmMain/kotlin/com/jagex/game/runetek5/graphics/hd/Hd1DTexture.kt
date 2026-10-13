/*
 * Not part of the original client: 634HD (toolkit 6), copied from OpenGl1DTexture (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext

class Hd1DTexture : HdTexture {

	private val width: Int

	constructor(glToolkit: HdToolkit?, internalFormat: Int, width: Int, data: ByteArray?, format: Int) : super(glToolkit, 3552, internalFormat, width, false) {
		this.width = width
		super.glToolkit!!.setActiveTexture(this)
		HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_LINES)
		HdGl.glTexImage1Dub(super.target, 0, super.internalFormat, this.width, 0, format, OpenGLConstants.GL_UNSIGNED_BYTE, data, 0)
		HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_TRIANGLES)
		this.setLinearFilter(true)
	}

	override fun detach() {
	}

	fun setClampToEdge() {
		super.glToolkit!!.setActiveTexture(this)
		HdGl.glTexParameteri(super.target, OpenGLConstants.GL_TEXTURE_WRAP_S, 33071)
	}
}
