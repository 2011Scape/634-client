package com.jagex.game.runetek5.graphics.texture.sampler

import com.jagex.game.runetek5.PixelFormat
import com.jagex.game.runetek5.Texture3DSampler
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.toolkit.GlxToolkit
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!qba")
class GlTexture3D : MipmapTextureSampler, Texture3DSampler {

	@OriginalMember(owner = "client!qba", name = "z", descriptor = "I")
	private val depth: Int

	@OriginalMember(owner = "client!qba", name = "G", descriptor = "I")
	private val width: Int

	@OriginalMember(owner = "client!qba", name = "E", descriptor = "I")
	private val height: Int

	@OriginalMember(owner = "client!qba", name = "<init>", descriptor = "(Lclient!bga;Lclient!wq;III[B)V")
	constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) pixelFormat: PixelFormat?, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) depth: Int, @OriginalArg(5) data: ByteArray?) : super(glxToolkit, 32879, pixelFormat, GLFormat.RGBA, height * width * depth, false) {
		this.depth = depth
		this.width = width
		this.height = height
		super.activeToolkit!!.setTextureSampler(this)
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, 1)
		OpenGL.glTexImage3Dub(super.target, 0, this.getInternalFormat(), this.width, this.height, this.depth, 0, MipmapTextureSampler.getGlFormat(super.pixelFormat), OpenGLConstants.GL_UNSIGNED_BYTE, data, 0)
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, 4)
	}
}
