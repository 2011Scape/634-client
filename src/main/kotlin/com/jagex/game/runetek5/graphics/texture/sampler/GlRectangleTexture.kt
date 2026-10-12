package com.jagex.game.runetek5.graphics.texture.sampler

import com.jagex.game.runetek5.DataType
import com.jagex.game.runetek5.PixelFormat
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.graphics.GpuTexture
import com.jagex.game.runetek5.toolkit.GlxToolkit
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!tw")
class GlRectangleTexture : MipmapTextureSampler, GpuTexture {

	@OriginalMember(owner = "client!tw", name = "E", descriptor = "I")
	private val width: Int

	@OriginalMember(owner = "client!tw", name = "y", descriptor = "I")
	private val height: Int

	@OriginalMember(owner = "client!tw", name = "<init>", descriptor = "(Lclient!bga;II[III)V")
	constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) pixels: IntArray?, @OriginalArg(4) offset: Int, @OriginalArg(5) rowLength: Int) : super(glxToolkit, 34037, GLFormat.HDR, GLFormat.RGBA, width * height, false) {
		this.width = width
		this.height = height
		super.activeToolkit!!.setTextureSampler(this)
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, rowLength)
		OpenGL.glTexImage2Di(super.target, 0, OpenGLConstants.GL_RGBA, this.width, this.height, 0, OpenGLConstants.GL_BGRA, super.activeToolkit!!.pixelType, pixels, offset * 4)
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, 0)
	}

	@OriginalMember(owner = "client!tw", name = "<init>", descriptor = "(Lclient!bga;Lclient!wq;II[BII)V")
	constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) pixelFormat: PixelFormat?, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) data: ByteArray?, @OriginalArg(5) offset: Int, @OriginalArg(6) rowLength: Int) : super(glxToolkit, 34037, pixelFormat, GLFormat.RGBA, width * height, false) {
		this.height = height
		this.width = width
		super.activeToolkit!!.setTextureSampler(this)
		OpenGL.glPixelStorei(3317, 1)
		OpenGL.glPixelStorei(3314, rowLength)
		OpenGL.glTexImage2Dub(super.target, 0, this.getInternalFormat(), width, height, 0, MipmapTextureSampler.getGlFormat(super.pixelFormat), 5121, data, offset)
		OpenGL.glPixelStorei(3314, 0)
		OpenGL.glPixelStorei(3317, 4)
	}

	@OriginalMember(owner = "client!tw", name = "<init>", descriptor = "(Lclient!bga;Lclient!wq;Lclient!baa;II)V")
	constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) pixelFormat: PixelFormat?, @OriginalArg(2) pixelActionFormat: DataType?, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int) : super(glxToolkit, 34037, pixelFormat, pixelActionFormat, height * width, false) {
		this.height = height
		this.width = width
		super.activeToolkit!!.setTextureSampler(this)
		OpenGL.glTexImage2Dub(super.target, 0, this.getInternalFormat(), width, height, 0, MipmapTextureSampler.getGlFormat(super.pixelFormat), DataType.getGlType(super.pixelActionFormat), null, 0)
	}

	@OriginalMember(owner = "client!tw", name = "<init>", descriptor = "(Lclient!bga;Lclient!wq;II[FII)V")
	constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) pixelFormat: PixelFormat?, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) data: FloatArray?, @OriginalArg(5) offset: Int, @OriginalArg(6) rowLength: Int) : super(glxToolkit, 34037, pixelFormat, DataType.aClass26_8, height * width, false) {
		this.width = width
		this.height = height
		super.activeToolkit!!.setTextureSampler(this)
		OpenGL.glPixelStorei(3314, rowLength)
		OpenGL.glTexImage2Df(super.target, 0, this.getInternalFormat(), width, height, 0, MipmapTextureSampler.getGlFormat(super.pixelFormat), 5126, data, offset * 4)
		OpenGL.glPixelStorei(3314, 0)
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(I)I")
	override fun getTextureHeight(): Int {
		return this.height
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(ZZI)V")
	override fun setWarp(@OriginalArg(0) repeatS: Boolean, @OriginalArg(1) repeatT: Boolean) {
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(I[IIIIBI)V")
	override fun readPixels(@OriginalArg(1) dest: IntArray?, @OriginalArg(2) length: Int, @OriginalArg(4) height: Int) {
		@Pc(18) val pixels: IntArray = IntArray(this.height * this.width)
		super.activeToolkit!!.setTextureSampler(this)
		OpenGL.glGetTexImagei(super.target, 0, OpenGLConstants.GL_BGRA, OpenGLConstants.GL_UNSIGNED_BYTE, pixels, 0)
		@Pc(33) var i: Int = 0
		while (i < height) {
			Arrays.copy(pixels, this.width * (height - i - 1), dest, i * length, length)
			i++
		}
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(FI)F")
	override fun toTextureCoordS(@OriginalArg(0) x: Float): Float {
		return x
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(BF)F")
	override fun toTextureCoordT(@OriginalArg(1) y: Float): Float {
		return y
	}

	@OriginalMember(owner = "client!tw", name = "b", descriptor = "(I)Z")
	override fun supportsTextureRepeat(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(Z)I")
	override fun getTextureWidth(): Int {
		return this.width
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(II[IIIIII)V")
	override fun updateTextureRegion(@OriginalArg(0) rowLength: Int, @OriginalArg(2) pixels: IntArray?, @OriginalArg(3) x: Int, @OriginalArg(5) width: Int, @OriginalArg(6) y: Int, @OriginalArg(7) height: Int) {
		var rowLength: Int = rowLength
		super.activeToolkit!!.setTextureSampler(this)
		if (rowLength == 0) {
			rowLength = width
		}
		if (width != rowLength) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, rowLength)
		}
		OpenGL.glTexSubImage2Di(super.target, 0, x, y, width, height, OpenGLConstants.GL_BGRA, super.activeToolkit!!.pixelType, pixels, 0)
		if (width != rowLength) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, 0)
		}
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(IIBIIII[BLclient!wq;)V")
	override fun upload(@OriginalArg(3) rowLength: Int, @OriginalArg(4) height: Int, @OriginalArg(5) width: Int, @OriginalArg(7) data: ByteArray?, @OriginalArg(8) pixelFormat: PixelFormat?) {
		var rowLength: Int = rowLength
		super.activeToolkit!!.setTextureSampler(this)
		if (rowLength == 0) {
			rowLength = width
		}
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, 1)
		if (width != rowLength) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, rowLength)
		}
		OpenGL.glTexSubImage2Dub(super.target, 0, 0, 0, width, height, MipmapTextureSampler.getGlFormat(pixelFormat), OpenGLConstants.GL_UNSIGNED_BYTE, data, 0)
		if (rowLength != width) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, 0)
		}
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, 4)
	}
}
