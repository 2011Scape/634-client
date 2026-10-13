/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlCubeTexture (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField

class HdCubeTexture : HdTexture {

	private var framebufferAttachment: Int = -1

	private var framebufferTarget: Int = -1

	@JvmField
	val size: Int

	constructor(glToolkit: HdToolkit?, internalFormat: Int, size: Int) : super(glToolkit, 34067, internalFormat, size * size * 6, false) {
		this.size = size
		super.glToolkit!!.setActiveTexture(this)
		for (i in 0 until 6) {
			HdGl.glTexImage2Dub(i + OpenGLConstants.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, super.internalFormat, size, size, 0, HdTexture.getBaseFormat(super.internalFormat), OpenGLConstants.GL_UNSIGNED_BYTE, null, 0)
		}
		this.setLinearFilter(true)
	}

	constructor(glToolkit: HdToolkit?, internalFormat: Int, size: Int, mipmapped: Boolean, faces: Array<ByteArray?>?, format: Int) : super(glToolkit, 34067, internalFormat, size * size * 6, mipmapped) {
		this.size = size
		super.glToolkit!!.setActiveTexture(this)
		for (i in 0 until 6) {
			HdGl.glTexImage2Dub(i + OpenGLConstants.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, super.internalFormat, size, size, 0, format, OpenGLConstants.GL_UNSIGNED_BYTE, faces!![i], 0)
		}
		this.setLinearFilter(true)
	}

	constructor(glToolkit: HdToolkit?, internalFormat: Int, size: Int, mipmapped: Boolean, faces: Array<IntArray?>?) : super(glToolkit, 34067, internalFormat, size * 6 * size, mipmapped) {
		this.size = size
		super.glToolkit!!.setActiveTexture(this)
		var face: Int
		if (mipmapped) {
			face = 0
			while (face < 6) {
				HdTexture.uploadMipmapped(face + 34069, super.internalFormat, size, super.glToolkit!!.pixelType, size, faces!![face])
				face++
			}
		} else {
			face = 0
			while (face < 6) {
				HdGl.glTexImage2Di(face + OpenGLConstants.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, super.internalFormat, size, size, 0, OpenGLConstants.GL_BGRA, super.glToolkit!!.pixelType, faces!![face], 0)
				face++
			}
		}
		this.setLinearFilter(true)
	}

	fun attachToFramebuffer(framebufferTarget: Int, faceTarget: Int, attachment: Int) {
		HdGl.glFramebufferTexture2DEXT(framebufferTarget, attachment, faceTarget, super.id, 0)
		this.framebufferTarget = framebufferTarget
		this.framebufferAttachment = attachment
	}

	override fun detach() {
		HdGl.glFramebufferTexture2DEXT(this.framebufferTarget, this.framebufferAttachment, OpenGLConstants.GL_TEXTURE_2D, 0, 0)
		this.framebufferAttachment = -1
		this.framebufferTarget = -1
	}
}
