package com.jagex.game.runetek5.graphics.texture

import com.jagex.game.runetek5.graphics.gl.GlToolkit
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler
import com.jagex.game.runetek5.utils.Utils
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!eba")
class OpenGl2DImageTexture : OpenGl2DTexture {

	@OriginalMember(owner = "client!eba", name = "I", descriptor = "Z")
	@JvmField
	val tiled: Boolean

	@OriginalMember(owner = "client!eba", name = "F", descriptor = "F")
	@JvmField
	val t: Float

	@OriginalMember(owner = "client!eba", name = "E", descriptor = "F")
	@JvmField
	val s: Float

	@OriginalMember(owner = "client!eba", name = "H", descriptor = "I")
	@JvmField
	val singleWidth: Int

	@OriginalMember(owner = "client!eba", name = "G", descriptor = "I")
	@JvmField
	val singleLength: Int

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIII)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) target: Int, @OriginalArg(2) internalFormat: Int, @OriginalArg(3) width: Int, @OriginalArg(4) length: Int) : super(glToolkit, target, internalFormat, width, length) {
		if (super.target == 34037) {
			this.t = length.toFloat()
			this.tiled = false
			this.s = width.toFloat()
		} else {
			this.tiled = true
			this.t = 1.0f
			this.s = this.t
		}
		this.singleWidth = width
		this.singleLength = length
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIIII)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) target: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) width: Int, @OriginalArg(5) length: Int) : super(glToolkit, 3553, target, width, length) {
		this.s = x.toFloat() / width.toFloat()
		this.singleLength = y
		this.singleWidth = x
		this.t = y.toFloat() / length.toFloat()
		this.tiled = false
		this.setRepeat(false, false)
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIIIIZ)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) target: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) width: Int, @OriginalArg(5) length: Int, @OriginalArg(6) copyFromFramebuffer: Boolean) : super(glToolkit, target, x, y, width, length) {
		this.singleWidth = width
		if (super.target == 34037) {
			this.tiled = false
			this.s = width.toFloat()
			this.t = length.toFloat()
		} else {
			this.tiled = true
			this.t = 1.0f
			this.s = this.t
		}
		this.singleLength = length
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIIIIIZ)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) singleWidth: Int, @OriginalArg(4) singleLength: Int, @OriginalArg(5) width: Int, @OriginalArg(6) length: Int, @OriginalArg(7) copyFromFramebuffer: Boolean) : super(glToolkit, 3553, x, y, width, length) {
		this.singleWidth = singleWidth
		this.t = singleLength.toFloat() / length.toFloat()
		this.s = singleWidth.toFloat() / width.toFloat()
		this.singleLength = singleLength
		this.tiled = false
		this.setRepeat(false, false)
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIIII[BI)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) internalFormat: Int, @OriginalArg(2) singleWidth: Int, @OriginalArg(3) singleLength: Int, @OriginalArg(4) width: Int, @OriginalArg(5) length: Int, @OriginalArg(6) data: ByteArray?, @OriginalArg(7) format: Int) : super(glToolkit, 3553, internalFormat, width, length) {
		this.singleWidth = singleWidth
		this.singleLength = singleLength
		this.uploadTextureData(singleWidth, true, singleLength, format, data)
		this.t = singleLength.toFloat() / length.toFloat()
		this.s = singleWidth.toFloat() / width.toFloat()
		this.tiled = false
		this.setRepeat(false, false)
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIIZ[III)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) target: Int, @OriginalArg(2) width: Int, @OriginalArg(3) length: Int, @OriginalArg(4) mipmapped: Boolean, @OriginalArg(5) pixels: IntArray?, @OriginalArg(6) rowLength: Int, @OriginalArg(7) offset: Int) : super(glToolkit, target, 6408, width, length, mipmapped, pixels, rowLength, offset, true) {
		if (super.target == 34037) {
			this.s = width.toFloat()
			this.tiled = false
			this.t = length.toFloat()
		} else {
			this.tiled = true
			this.t = 1.0f
			this.s = this.t
		}
		this.singleLength = length
		this.singleWidth = width
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIII[I)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) imageWidth: Int, @OriginalArg(2) imageLength: Int, @OriginalArg(3) totalWidth: Int, @OriginalArg(4) totalLength: Int, @OriginalArg(5) pixels: IntArray?) : super(glToolkit, 3553, 6408, totalWidth, totalLength) {
		this.singleWidth = imageWidth
		this.singleLength = imageLength
		this.updateTextureRegion(imageLength, pixels, imageWidth)
		this.s = imageWidth.toFloat() / totalWidth.toFloat()
		this.t = imageLength.toFloat() / totalLength.toFloat()
		this.tiled = false
		this.setRepeat(false, false)
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIIIZ[BI)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) target: Int, @OriginalArg(2) internalFormat: Int, @OriginalArg(3) width: Int, @OriginalArg(4) length: Int, @OriginalArg(5) mipmapped: Boolean, @OriginalArg(6) pixels: ByteArray?, @OriginalArg(7) format: Int) : super(glToolkit, target, internalFormat, width, length, mipmapped, pixels, format, true) {
		this.singleWidth = width
		if (super.target == 34037) {
			this.t = length.toFloat()
			this.tiled = false
			this.s = width.toFloat()
		} else {
			this.tiled = true
			this.t = 1.0f
			this.s = this.t
		}
		this.singleLength = length
	}

	companion object {
		@OriginalMember(owner = "client!fr", name = "a", descriptor = "(Z[BIILclient!qo;III)Lclient!eba;")
		@JvmStatic
		fun createSurface(@OriginalArg(1) data: ByteArray?, @OriginalArg(3) height: Int, @OriginalArg(4) glToolkit: GlToolkit?, @OriginalArg(7) width: Int): OpenGl2DImageTexture {
			if (glToolkit!!.framebufferObjectSupported || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
				return OpenGl2DImageTexture(glToolkit, 3553, 6406, width, height, false, data, 6406)
			} else if (glToolkit.arbRectangleTexturesSupported) {
				return OpenGl2DImageTexture(glToolkit, 34037, 6406, width, height, false, data, 6406)
			} else {
				return OpenGl2DImageTexture(glToolkit, 6406, width, height, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height), data, 6406)
			}
		}

		@OriginalMember(owner = "client!la", name = "a", descriptor = "(Lclient!qo;IIIII)Lclient!eba;")
		@JvmStatic
		fun createFromScreenCapture(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) x: Int, @OriginalArg(3) width: Int, @OriginalArg(4) y: Int, @OriginalArg(5) height: Int): OpenGl2DImageTexture {
			if (glToolkit!!.framebufferObjectSupported || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
				return OpenGl2DImageTexture(glToolkit, 3553, x, y, width, height, true)
			} else if (glToolkit.arbRectangleTexturesSupported) {
				return OpenGl2DImageTexture(glToolkit, 34037, x, y, width, height, true)
			} else {
				return OpenGl2DImageTexture(glToolkit, x, y, width, height, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height), true)
			}
		}

		@OriginalMember(owner = "client!bca", name = "a", descriptor = "(ILclient!qo;III)Lclient!eba;")
		@JvmStatic
		fun createEmpty(@OriginalArg(1) glToolkit: GlToolkit?, @OriginalArg(2) internalFormat: Int, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int): OpenGl2DImageTexture {
			if (glToolkit!!.framebufferObjectSupported || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
				return OpenGl2DImageTexture(glToolkit, 3553, internalFormat, width, height)
			} else if (glToolkit.arbRectangleTexturesSupported) {
				return OpenGl2DImageTexture(glToolkit, 34037, internalFormat, width, height)
			} else {
				return OpenGl2DImageTexture(glToolkit, internalFormat, width, height, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height))
			}
		}

		@OriginalMember(owner = "client!wi", name = "a", descriptor = "([IIIIILclient!qo;ZB)Lclient!eba;")
		@JvmStatic
		fun createFromPixels(@OriginalArg(0) pixels: IntArray?, @OriginalArg(1) pixelOffset: Int, @OriginalArg(2) rowLength: Int, @OriginalArg(3) height: Int, @OriginalArg(4) width: Int, @OriginalArg(5) glToolkit: GlToolkit?): OpenGl2DImageTexture {
			if (glToolkit!!.framebufferObjectSupported || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
				return OpenGl2DImageTexture(glToolkit, 3553, width, height, false, pixels, rowLength, 0)
			} else if (glToolkit.arbRectangleTexturesSupported) {
				return OpenGl2DImageTexture(glToolkit, 34037, width, height, false, pixels, rowLength, 0)
			} else {
				return OpenGl2DImageTexture(glToolkit, width, height, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height), pixels)
			}
		}
	}
}
