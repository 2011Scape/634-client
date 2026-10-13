/*
 * Not part of the original client: 634HD (toolkit 6), copied from OpenGl2DTexture (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

open class Hd2DTexture : HdTexture {

	private var framebufferTarget: Int = -1

	private var framebufferAttachment: Int = -1

	@JvmField
	val width: Int

	@JvmField
	val length: Int

	constructor(glToolkit: HdToolkit?, target: Int, internalFormat: Int, width: Int, length: Int) : super(glToolkit, target, internalFormat, width * length, false) {
		this.width = width
		this.length = length
		super.glToolkit!!.setActiveTexture(this)
		HdGl.glTexImage2Dub(super.target, 0, super.internalFormat, width, length, 0, HdTexture.getBaseFormat(super.internalFormat), OpenGLConstants.GL_UNSIGNED_BYTE, null, 0)
		this.setLinearFilter(true)
	}

	constructor(glToolkit: HdToolkit?, target: Int, internalFormat: Int, width: Int, length: Int, mipmapped: Boolean, pixels: FloatArray?, format: Int) : super(glToolkit, target, internalFormat, length * width, mipmapped) {
		this.width = width
		this.length = length
		super.glToolkit!!.setActiveTexture(this)
		if (mipmapped && super.target != 34037) {
			Hd2DTexture.uploadMipmaps(width, format, target, pixels, length, internalFormat)
			this.setMipmapped(true)
		} else {
			HdGl.glTexImage2Df(super.target, 0, super.internalFormat, this.width, this.length, 0, format, OpenGLConstants.GL_FLOAT, pixels, 0)
			this.setMipmapped(false)
		}
		this.setLinearFilter(true)
	}

	constructor(glToolkit: HdToolkit?, target: Int, internalFormat: Int, width: Int, length: Int, mipmapped: Boolean, pixels: IntArray?, rowLength: Int, offset: Int, flipVertical: Boolean) : super(glToolkit, target, internalFormat, length * width, mipmapped) {
		var pixels: IntArray? = pixels
		this.width = width
		this.length = length
		if (flipVertical) {
			val flipped: IntArray = IntArray(pixels!!.size)
			for (i in 0 until length) {
				var dst: Int = i * width
				var src: Int = width * (length - i - 1)
				for (j in 0 until width) {
					flipped[dst++] = pixels[src++]
				}
			}
			pixels = flipped
		}
		super.glToolkit!!.setActiveTexture(this)
		if (super.target != 34037 && mipmapped && rowLength == 0) {
			HdTexture.uploadMipmapped(super.target, super.internalFormat, this.length, super.glToolkit!!.pixelType, this.width, pixels)
			this.setMipmapped(true)
		} else {
			HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, rowLength)
			HdGl.glTexImage2Di(super.target, 0, super.internalFormat, this.width, this.length, 0, OpenGLConstants.GL_BGRA, super.glToolkit!!.pixelType, pixels, offset * 4)
			HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, OpenGLConstants.GL_POINTS)
			this.setMipmapped(false)
		}
		this.setLinearFilter(true)
	}

	constructor(glToolkit: HdToolkit?, target: Int, internalFormat: Int, width: Int, length: Int, mipmapped: Boolean, pixels: ByteArray?, format: Int, flipVertical: Boolean) : super(glToolkit, target, internalFormat, width * length, mipmapped) {
		var pixels: ByteArray? = pixels
		this.width = width
		this.length = length
		if (flipVertical) {
			val flipped: ByteArray = ByteArray(pixels!!.size)
			for (i in 0 until length) {
				var dst: Int = width * i
				var src: Int = width * (length - i - 1)
				for (j in 0 until width) {
					flipped[dst++] = pixels[src++]
				}
			}
			pixels = flipped
		}
		super.glToolkit!!.setActiveTexture(this)
		HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_LINES)
		if (mipmapped && super.target != 34037) {
			Hd2DTexture.uploadMipmaps(length, width, pixels, format, target, internalFormat)
			this.setMipmapped(true)
		} else {
			HdGl.glTexImage2Dub(super.target, 0, super.internalFormat, this.width, this.length, 0, format, OpenGLConstants.GL_UNSIGNED_BYTE, pixels, 0)
			this.setMipmapped(false)
		}
		HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_TRIANGLES)
		this.setLinearFilter(true)
	}

	constructor(glToolkit: HdToolkit?, target: Int, x: Int, y: Int, width: Int, length: Int) : super(glToolkit, target, 6407, length * width, false) {
		this.width = width
		this.length = length
		val flippedY: Int = super.glToolkit!!.viewportHeight - y - length
		super.glToolkit!!.setActiveTexture(this)
		super.glToolkit!!.beginReadFrame(x, flippedY, width, length) // Not part of the original client: see HdToolkit.beginReadFrame.
		HdGl.glCopyTexImage2D(super.target, 0, super.internalFormat, x, flippedY, width, length, 0)
		super.glToolkit!!.endReadFrame()
		this.setLinearFilter(true)
	}

	fun attachToFramebuffer(framebufferTarget: Int, attachment: Int) {
		HdGl.glFramebufferTexture2DEXT(framebufferTarget, attachment, super.target, super.id, 0)
		this.framebufferTarget = framebufferTarget
		this.framebufferAttachment = attachment
	}

	fun updateTextureRegion(height: Int, pixels: IntArray?, width: Int) {
		val flipped: IntArray = IntArray(width * height)
		for (i in 0 until height) {
			var dst: Int = width * i
			var src: Int = (height - i - 1) * width
			for (j in 0 until width) {
				flipped[dst++] = pixels!![src++]
			}
		}
		super.glToolkit!!.setActiveTexture(this)
		if (width != width) {
			HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, width)
		}
		HdGl.glTexSubImage2Di(super.target, OpenGLConstants.GL_POINTS, 0, 0, width, height, OpenGLConstants.GL_BGRA, super.glToolkit!!.pixelType, flipped, 0)
		if (width != width) {
			HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, OpenGLConstants.GL_POINTS)
		}
	}

	fun setRepeat(repeatT: Boolean, repeatS: Boolean) {
		if (super.target == 3553) {
			super.glToolkit!!.setActiveTexture(this)
			HdGl.glTexParameteri(super.target, OpenGLConstants.GL_TEXTURE_WRAP_S, if (repeatS) OpenGLConstants.GL_REPEAT else 33071)
			HdGl.glTexParameteri(super.target, OpenGLConstants.GL_TEXTURE_WRAP_T, if (repeatT) OpenGLConstants.GL_REPEAT else 33071)
		}
	}

	fun copyTextureSubImage(offsetX: Int, height: Int, y: Int, offsetY: Int, x: Int, width: Int) {
		val flippedY: Int = super.glToolkit!!.viewportHeight - y - height
		super.glToolkit!!.setActiveTexture(this)
		super.glToolkit!!.beginReadFrame(x, flippedY, width, height) // Not part of the original client: see HdToolkit.beginReadFrame.
		HdGl.glCopyTexSubImage2D(super.target, OpenGLConstants.GL_POINTS, offsetX, this.length - height - offsetY, x, flippedY, width, height)
		super.glToolkit!!.endReadFrame()
		HdGl.glFlush()
	}

	final override fun detach() {
		HdGl.glFramebufferTexture2DEXT(this.framebufferTarget, this.framebufferAttachment, super.target, 0, 0)
		this.framebufferAttachment = -1
		this.framebufferTarget = -1
	}

	fun uploadTextureData(width: Int, flipVertical: Boolean, height: Int, format: Int, data: ByteArray?) {
		var data: ByteArray? = data
		if (flipVertical) {
			val bytesPerPixel: Int = Hd2DTexture.getBytesPerPixel(6406)
			val rowSize: Int = bytesPerPixel * width
			val srcRowSize: Int = width * bytesPerPixel
			val flipped: ByteArray = ByteArray(height * rowSize)
			for (i in 0 until height) {
				var dst: Int = rowSize * i
				var src: Int = (height - i - 1) * srcRowSize
				for (j in 0 until rowSize) {
					flipped[dst++] = data!![src++]
				}
			}
			data = flipped
		}
		super.glToolkit!!.setActiveTexture(this)
		HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_LINES)
		if (width != width) {
			HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, width)
		}
		HdGl.glTexSubImage2Dub(super.target, OpenGLConstants.GL_POINTS, OpenGLConstants.GL_POINTS, OpenGLConstants.GL_POINTS, width, height, OpenGLConstants.GL_ALPHA, OpenGLConstants.GL_UNSIGNED_BYTE, data, 0)
		if (width != width) {
			HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, OpenGLConstants.GL_POINTS)
		}
		HdGl.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_TRIANGLES)
	}

	companion object {
		@JvmStatic
		fun uploadMipmaps(height: Int, width: Int, data: ByteArray?, format: Int, target: Int, internalFormat: Int) {
			var height: Int = height
			var width: Int = width
			if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
				throw IllegalArgumentException("")
			} else if (height <= 0 || MipmapTextureSampler.isPowerOfTwo(height)) {
				val bytesPerPixel: Int = Hd2DTexture.getBytesPerPixel(format)
				var mipmapLevel: Int = 0
				var minDimension: Int = if (width < height) width else height
				var halfWidth: Int = width shr 1
				var halfHeight: Int = height shr 1
				var sourceData: ByteArray? = data
				var destData: ByteArray? = ByteArray(halfWidth * halfHeight * bytesPerPixel)
				while (true) {
					HdGl.glTexImage2Dub(target, mipmapLevel, internalFormat, width, height, 0, format, 5121, sourceData, 0)
					if (minDimension <= 1) {
						return
					}
					val rowBytes: Int = width * bytesPerPixel
					val swapTemp: ByteArray? = destData
					for (i in 0 until bytesPerPixel) {
						var destIndex: Int = i
						var srcTopIndex: Int = i
						var srcBottomIndex: Int = rowBytes + i
						for (j in 0 until halfHeight) {
							for (k in 0 until halfWidth) {
								val topLeftSample: Byte = sourceData!![srcTopIndex]
								srcTopIndex += bytesPerPixel
								val topSum: Int = topLeftSample + sourceData[srcTopIndex]
								val partialSum: Int = topSum + sourceData[srcBottomIndex]
								srcTopIndex += bytesPerPixel
								srcBottomIndex += bytesPerPixel
								val sampleSum: Int = partialSum + sourceData[srcBottomIndex]
								destData!![destIndex] = (sampleSum shr 2).toByte()
								srcBottomIndex += bytesPerPixel
								destIndex += bytesPerPixel
							}
							srcBottomIndex += rowBytes
							srcTopIndex += rowBytes
						}
					}
					destData = sourceData
					sourceData = swapTemp
					height = halfHeight
					width = halfWidth
					halfHeight = halfHeight shr 0x1
					mipmapLevel++
					minDimension = minDimension shr 0x1
					halfWidth = halfWidth shr 0x1
				}
			} else {
				throw IllegalArgumentException("")
			}
		}

		@JvmStatic
		fun getBytesPerPixel(format: Int): Int {
			if (format == 6406) {
				return 1
			} else if (format == 6409) {
				return 1
			} else if (format == 32841) {
				return 1
			} else if (format == 6410) {
				return 2
			} else if (format == 6407) {
				return 3
			} else if (format == 6408) {
				return 4
			} else {
				throw IllegalArgumentException("")
			}
		}

		@JvmStatic
		fun uploadMipmaps(width: Int, format: Int, target: Int, pixels: FloatArray?, height: Int, internalFormat: Int) {
			var width: Int = width
			var height: Int = height
			if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
				throw IllegalArgumentException("")
			} else if (height <= 0 || MipmapTextureSampler.isPowerOfTwo(height)) {
				val componentCount: Int = Hd2DTexture.getBytesPerPixel(6408)
				var level: Int = 0
				var minDimension: Int = if (width < height) width else height
				var nextWidth: Int = width shr 1
				var nextHeight: Int = height shr 1
				var levelPixels: FloatArray? = pixels
				var scratch: FloatArray? = FloatArray(componentCount * nextWidth * nextHeight)
				while (true) {
					HdGl.glTexImage2Df(OpenGLConstants.GL_TEXTURE_2D, level, OpenGLConstants.GL_RGBA16F, width, height, 0, OpenGLConstants.GL_RGBA, OpenGLConstants.GL_FLOAT, levelPixels, 0)
					if (minDimension <= 1) {
						return
					}
					val rowStride: Int = width * componentCount
					val downsampled: FloatArray? = scratch
					for (i in 0 until componentCount) {
						var dstIndex: Int = i
						var srcIndex0: Int = i
						var srcIndex1: Int = rowStride + i
						for (j in 0 until nextHeight) {
							for (k in 0 until nextWidth) {
								val topLeft: Float = levelPixels!![srcIndex0]
								srcIndex0 += componentCount
								val topSum: Float = topLeft + levelPixels[srcIndex0]
								srcIndex0 += componentCount
								val partialSum: Float = topSum + levelPixels[srcIndex1]
								srcIndex1 += componentCount
								val sum: Float = partialSum + levelPixels[srcIndex1]
								srcIndex1 += componentCount
								scratch!![dstIndex] = sum * 0.25f
								dstIndex += componentCount
							}
							srcIndex0 += rowStride
							srcIndex1 += rowStride
						}
					}
					scratch = levelPixels
					levelPixels = downsampled
					width = nextWidth
					height = nextHeight
					level++
					minDimension = minDimension shr 0x1
					nextWidth = nextWidth shr 0x1
					nextHeight = nextHeight shr 0x1
				}
			} else {
				throw IllegalArgumentException("")
			}
		}
	}
}
