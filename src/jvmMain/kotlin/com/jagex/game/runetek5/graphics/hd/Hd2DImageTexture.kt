/*
 * Not part of the original client: 634HD (toolkit 6), copied from OpenGl2DImageTexture (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

class Hd2DImageTexture : Hd2DTexture {

	@JvmField
	val tiled: Boolean

	@JvmField
	val t: Float

	@JvmField
	val s: Float

	@JvmField
	val singleWidth: Int

	@JvmField
	val singleLength: Int

	constructor(glToolkit: HdToolkit?, target: Int, internalFormat: Int, width: Int, length: Int) : super(glToolkit, target, internalFormat, width, length) {
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

	constructor(glToolkit: HdToolkit?, target: Int, x: Int, y: Int, width: Int, length: Int) : super(glToolkit, 3553, target, width, length) {
		this.s = x.toFloat() / width.toFloat()
		this.singleLength = y
		this.singleWidth = x
		this.t = y.toFloat() / length.toFloat()
		this.tiled = false
		this.setRepeat(false, false)
	}

	constructor(glToolkit: HdToolkit?, target: Int, x: Int, y: Int, width: Int, length: Int, copyFromFramebuffer: Boolean) : super(glToolkit, target, x, y, width, length) {
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

	constructor(glToolkit: HdToolkit?, x: Int, y: Int, singleWidth: Int, singleLength: Int, width: Int, length: Int, copyFromFramebuffer: Boolean) : super(glToolkit, 3553, x, y, width, length) {
		this.singleWidth = singleWidth
		this.t = singleLength.toFloat() / length.toFloat()
		this.s = singleWidth.toFloat() / width.toFloat()
		this.singleLength = singleLength
		this.tiled = false
		this.setRepeat(false, false)
	}

	constructor(glToolkit: HdToolkit?, internalFormat: Int, singleWidth: Int, singleLength: Int, width: Int, length: Int, data: ByteArray?, format: Int) : super(glToolkit, 3553, internalFormat, width, length) {
		this.singleWidth = singleWidth
		this.singleLength = singleLength
		this.uploadTextureData(singleWidth, true, singleLength, format, data)
		this.t = singleLength.toFloat() / length.toFloat()
		this.s = singleWidth.toFloat() / width.toFloat()
		this.tiled = false
		this.setRepeat(false, false)
	}

	constructor(glToolkit: HdToolkit?, target: Int, width: Int, length: Int, mipmapped: Boolean, pixels: IntArray?, rowLength: Int, offset: Int) : super(glToolkit, target, 6408, width, length, mipmapped, pixels, rowLength, offset, true) {
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

	constructor(glToolkit: HdToolkit?, imageWidth: Int, imageLength: Int, totalWidth: Int, totalLength: Int, pixels: IntArray?) : super(glToolkit, 3553, 6408, totalWidth, totalLength) {
		this.singleWidth = imageWidth
		this.singleLength = imageLength
		this.updateTextureRegion(imageLength, pixels, imageWidth)
		this.s = imageWidth.toFloat() / totalWidth.toFloat()
		this.t = imageLength.toFloat() / totalLength.toFloat()
		this.tiled = false
		this.setRepeat(false, false)
	}

	constructor(glToolkit: HdToolkit?, target: Int, internalFormat: Int, width: Int, length: Int, mipmapped: Boolean, pixels: ByteArray?, format: Int) : super(glToolkit, target, internalFormat, width, length, mipmapped, pixels, format, true) {
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
		@JvmStatic
		fun createSurface(data: ByteArray?, height: Int, glToolkit: HdToolkit?, width: Int): Hd2DImageTexture {
			if (glToolkit!!.framebufferObjectSupported || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
				return Hd2DImageTexture(glToolkit, 3553, 6406, width, height, false, data, 6406)
			} else if (glToolkit.arbRectangleTexturesSupported) {
				return Hd2DImageTexture(glToolkit, 34037, 6406, width, height, false, data, 6406)
			} else {
				return Hd2DImageTexture(glToolkit, 6406, width, height, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height), data, 6406)
			}
		}

		@JvmStatic
		fun createFromScreenCapture(glToolkit: HdToolkit?, x: Int, width: Int, y: Int, height: Int): Hd2DImageTexture {
			if (glToolkit!!.framebufferObjectSupported || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
				return Hd2DImageTexture(glToolkit, 3553, x, y, width, height, true)
			} else if (glToolkit.arbRectangleTexturesSupported) {
				return Hd2DImageTexture(glToolkit, 34037, x, y, width, height, true)
			} else {
				return Hd2DImageTexture(glToolkit, x, y, width, height, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height), true)
			}
		}

		@JvmStatic
		fun createEmpty(glToolkit: HdToolkit?, internalFormat: Int, width: Int, height: Int): Hd2DImageTexture {
			if (glToolkit!!.framebufferObjectSupported || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
				return Hd2DImageTexture(glToolkit, 3553, internalFormat, width, height)
			} else if (glToolkit.arbRectangleTexturesSupported) {
				return Hd2DImageTexture(glToolkit, 34037, internalFormat, width, height)
			} else {
				return Hd2DImageTexture(glToolkit, internalFormat, width, height, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height))
			}
		}

		@JvmStatic
		fun createFromPixels(pixels: IntArray?, pixelOffset: Int, rowLength: Int, height: Int, width: Int, glToolkit: HdToolkit?): Hd2DImageTexture {
			if (glToolkit!!.framebufferObjectSupported || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
				return Hd2DImageTexture(glToolkit, 3553, width, height, false, pixels, rowLength, 0)
			} else if (glToolkit.arbRectangleTexturesSupported) {
				return Hd2DImageTexture(glToolkit, 34037, width, height, false, pixels, rowLength, 0)
			} else {
				return Hd2DImageTexture(glToolkit, width, height, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height), pixels)
			}
		}
	}
}
