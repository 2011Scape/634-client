package com.jagex.game.runetek5.graphics.gl

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!q")
class GlCubeTexture : GlTexture {

	@OriginalMember(owner = "client!q", name = "w", descriptor = "I")
	private var framebufferAttachment: Int = -1

	@OriginalMember(owner = "client!q", name = "x", descriptor = "I")
	private var framebufferTarget: Int = -1

	@OriginalMember(owner = "client!q", name = "z", descriptor = "I")
	@JvmField
	val size: Int

	@OriginalMember(owner = "client!q", name = "<init>", descriptor = "(Lclient!qo;II)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) internalFormat: Int, @OriginalArg(2) size: Int) : super(glToolkit, 34067, internalFormat, size * size * 6, false) {
		this.size = size
		super.glToolkit!!.setActiveTexture(this)
		for (@Pc(28) i in 0 until 6) {
			OpenGL.glTexImage2Dub(i + OpenGLConstants.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, super.internalFormat, size, size, 0, GlTexture.getBaseFormat(super.internalFormat), OpenGLConstants.GL_UNSIGNED_BYTE, null, 0)
		}
		this.setLinearFilter(true)
	}

	@OriginalMember(owner = "client!q", name = "<init>", descriptor = "(Lclient!qo;IIZ[[BI)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) internalFormat: Int, @OriginalArg(2) size: Int, @OriginalArg(3) mipmapped: Boolean, @OriginalArg(4) faces: Array<ByteArray?>?, @OriginalArg(5) format: Int) : super(glToolkit, 34067, internalFormat, size * size * 6, mipmapped) {
		this.size = size
		super.glToolkit!!.setActiveTexture(this)
		for (@Pc(28) i in 0 until 6) {
			OpenGL.glTexImage2Dub(i + OpenGLConstants.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, super.internalFormat, size, size, 0, format, OpenGLConstants.GL_UNSIGNED_BYTE, faces!![i], 0)
		}
		this.setLinearFilter(true)
	}

	@OriginalMember(owner = "client!q", name = "<init>", descriptor = "(Lclient!qo;IIZ[[I)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) internalFormat: Int, @OriginalArg(2) size: Int, @OriginalArg(3) mipmapped: Boolean, @OriginalArg(4) faces: Array<IntArray?>?) : super(glToolkit, 34067, internalFormat, size * 6 * size, mipmapped) {
		this.size = size
		super.glToolkit!!.setActiveTexture(this)
		@Pc(30) var face: Int
		if (mipmapped) {
			face = 0
			while (face < 6) {
				GlTexture.uploadMipmapped(face + 34069, super.internalFormat, size, super.glToolkit!!.pixelType, size, faces!![face])
				face++
			}
		} else {
			face = 0
			while (face < 6) {
				OpenGL.glTexImage2Di(face + OpenGLConstants.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, super.internalFormat, size, size, 0, OpenGLConstants.GL_BGRA, super.glToolkit!!.pixelType, faces!![face], 0)
				face++
			}
		}
		this.setLinearFilter(true)
	}

	@OriginalMember(owner = "client!q", name = "a", descriptor = "(IIIII)V")
	fun attachToFramebuffer(@OriginalArg(0) framebufferTarget: Int, @OriginalArg(1) faceTarget: Int, @OriginalArg(2) attachment: Int) {
		OpenGL.glFramebufferTexture2DEXT(framebufferTarget, attachment, faceTarget, super.id, 0)
		this.framebufferTarget = framebufferTarget
		this.framebufferAttachment = attachment
	}

	@OriginalMember(owner = "client!q", name = "a", descriptor = "(I)V")
	override fun detach() {
		OpenGL.glFramebufferTexture2DEXT(this.framebufferTarget, this.framebufferAttachment, OpenGLConstants.GL_TEXTURE_2D, 0, 0)
		this.framebufferAttachment = -1
		this.framebufferTarget = -1
	}
}
