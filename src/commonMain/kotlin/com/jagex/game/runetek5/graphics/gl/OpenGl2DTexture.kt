package com.jagex.game.runetek5.graphics.gl

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!tp")
open class OpenGl2DTexture : GlTexture {

	@OriginalMember(owner = "client!tp", name = "B", descriptor = "I")
	private var framebufferTarget: Int = -1

	@OriginalMember(owner = "client!tp", name = "x", descriptor = "I")
	private var framebufferAttachment: Int = -1

	@OriginalMember(owner = "client!tp", name = "z", descriptor = "I")
	@JvmField
	val width: Int

	@OriginalMember(owner = "client!tp", name = "D", descriptor = "I")
	@JvmField
	val length: Int

	@OriginalMember(owner = "client!tp", name = "<init>", descriptor = "(Lclient!qo;IIII)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) target: Int, @OriginalArg(2) internalFormat: Int, @OriginalArg(3) width: Int, @OriginalArg(4) length: Int) : super(glToolkit, target, internalFormat, width * length, false) {
		this.width = width
		this.length = length
		super.glToolkit!!.setActiveTexture(this)
		OpenGL.glTexImage2Dub(super.target, 0, super.internalFormat, width, length, 0, GlTexture.getBaseFormat(super.internalFormat), OpenGLConstants.GL_UNSIGNED_BYTE, null, 0)
		this.setLinearFilter(true)
	}

	@OriginalMember(owner = "client!tp", name = "<init>", descriptor = "(Lclient!qo;IIIIZ[FI)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) target: Int, @OriginalArg(2) internalFormat: Int, @OriginalArg(3) width: Int, @OriginalArg(4) length: Int, @OriginalArg(5) mipmapped: Boolean, @OriginalArg(6) pixels: FloatArray?, @OriginalArg(7) format: Int) : super(glToolkit, target, internalFormat, length * width, mipmapped) {
		this.width = width
		this.length = length
		super.glToolkit!!.setActiveTexture(this)
		if (mipmapped && super.target != 34037) {
			OpenGl2DTexture.uploadMipmaps(width, format, target, pixels, length, internalFormat)
			this.setMipmapped(true)
		} else {
			OpenGL.glTexImage2Df(super.target, 0, super.internalFormat, this.width, this.length, 0, format, OpenGLConstants.GL_FLOAT, pixels, 0)
			this.setMipmapped(false)
		}
		this.setLinearFilter(true)
	}

	@OriginalMember(owner = "client!tp", name = "<init>", descriptor = "(Lclient!qo;IIIIZ[IIIZ)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) target: Int, @OriginalArg(2) internalFormat: Int, @OriginalArg(3) width: Int, @OriginalArg(4) length: Int, @OriginalArg(5) mipmapped: Boolean, @OriginalArg(6) pixels: IntArray?, @OriginalArg(7) rowLength: Int, @OriginalArg(8) offset: Int, @OriginalArg(9) flipVertical: Boolean) : super(glToolkit, target, internalFormat, length * width, mipmapped) {
		var pixels: IntArray? = pixels
		this.width = width
		this.length = length
		if (flipVertical) {
			@Pc(28) val flipped: IntArray = IntArray(pixels!!.size)
			for (@Pc(30) i in 0 until length) {
				@Pc(36) var dst: Int = i * width
				@Pc(45) var src: Int = width * (length - i - 1)
				for (@Pc(47) j in 0 until width) {
					flipped[dst++] = pixels[src++]
				}
			}
			pixels = flipped
		}
		super.glToolkit!!.setActiveTexture(this)
		if (super.target != 34037 && mipmapped && rowLength == 0) {
			GlTexture.uploadMipmapped(super.target, super.internalFormat, this.length, super.glToolkit!!.pixelType, this.width, pixels)
			this.setMipmapped(true)
		} else {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, rowLength)
			OpenGL.glTexImage2Di(super.target, 0, super.internalFormat, this.width, this.length, 0, OpenGLConstants.GL_BGRA, super.glToolkit!!.pixelType, pixels, offset * 4)
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, OpenGLConstants.GL_POINTS)
			this.setMipmapped(false)
		}
		this.setLinearFilter(true)
	}

	@OriginalMember(owner = "client!tp", name = "<init>", descriptor = "(Lclient!qo;IIIIZ[BIZ)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) target: Int, @OriginalArg(2) internalFormat: Int, @OriginalArg(3) width: Int, @OriginalArg(4) length: Int, @OriginalArg(5) mipmapped: Boolean, @OriginalArg(6) pixels: ByteArray?, @OriginalArg(7) format: Int, @OriginalArg(8) flipVertical: Boolean) : super(glToolkit, target, internalFormat, width * length, mipmapped) {
		var pixels: ByteArray? = pixels
		this.width = width
		this.length = length
		if (flipVertical) {
			@Pc(28) val flipped: ByteArray = ByteArray(pixels!!.size)
			for (@Pc(30) i in 0 until length) {
				@Pc(36) var dst: Int = width * i
				@Pc(45) var src: Int = width * (length - i - 1)
				for (@Pc(47) j in 0 until width) {
					flipped[dst++] = pixels[src++]
				}
			}
			pixels = flipped
		}
		super.glToolkit!!.setActiveTexture(this)
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_LINES)
		if (mipmapped && super.target != 34037) {
			OpenGl2DTexture.uploadMipmaps(length, width, pixels, format, target, internalFormat)
			this.setMipmapped(true)
		} else {
			OpenGL.glTexImage2Dub(super.target, 0, super.internalFormat, this.width, this.length, 0, format, OpenGLConstants.GL_UNSIGNED_BYTE, pixels, 0)
			this.setMipmapped(false)
		}
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_TRIANGLES)
		this.setLinearFilter(true)
	}

	@OriginalMember(owner = "client!tp", name = "<init>", descriptor = "(Lclient!qo;IIIII)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) target: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) width: Int, @OriginalArg(5) length: Int) : super(glToolkit, target, 6407, length * width, false) {
		this.width = width
		this.length = length
		@Pc(30) val flippedY: Int = super.glToolkit!!.viewportHeight - y - length
		super.glToolkit!!.setActiveTexture(this)
		super.glToolkit!!.beginReadFrame(x, flippedY, width, length) // Not part of the original client: see GlToolkit.beginReadFrame.
		OpenGL.glCopyTexImage2D(super.target, 0, super.internalFormat, x, flippedY, width, length, 0)
		super.glToolkit!!.endReadFrame()
		this.setLinearFilter(true)
	}

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(IIII)V")
	fun attachToFramebuffer(@OriginalArg(0) framebufferTarget: Int, @OriginalArg(3) attachment: Int) {
		OpenGL.glFramebufferTexture2DEXT(framebufferTarget, attachment, super.target, super.id, 0)
		this.framebufferTarget = framebufferTarget
		this.framebufferAttachment = attachment
	}

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(ZII[IIBIII)V")
	fun updateTextureRegion(@OriginalArg(1) height: Int, @OriginalArg(3) pixels: IntArray?, @OriginalArg(6) width: Int) {
		@Pc(16) val flipped: IntArray = IntArray(width * height)
		for (@Pc(18) i in 0 until height) {
			@Pc(24) var dst: Int = width * i
			@Pc(34) var src: Int = (height - i - 1) * width
			for (@Pc(36) j in 0 until width) {
				flipped[dst++] = pixels!![src++]
			}
		}
		super.glToolkit!!.setActiveTexture(this)
		if (width != width) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, width)
		}
		OpenGL.glTexSubImage2Di(super.target, OpenGLConstants.GL_POINTS, 0, 0, width, height, OpenGLConstants.GL_BGRA, super.glToolkit!!.pixelType, flipped, 0)
		if (width != width) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, OpenGLConstants.GL_POINTS)
		}
	}

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(ZZI)V")
	fun setRepeat(@OriginalArg(0) repeatT: Boolean, @OriginalArg(1) repeatS: Boolean) {
		if (super.target == 3553) {
			super.glToolkit!!.setActiveTexture(this)
			OpenGL.glTexParameteri(super.target, OpenGLConstants.GL_TEXTURE_WRAP_S, if (repeatS) OpenGLConstants.GL_REPEAT else 33071)
			OpenGL.glTexParameteri(super.target, OpenGLConstants.GL_TEXTURE_WRAP_T, if (repeatT) OpenGLConstants.GL_REPEAT else 33071)
		}
	}

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(IIIIIBI)V")
	fun copyTextureSubImage(@OriginalArg(0) offsetX: Int, @OriginalArg(1) height: Int, @OriginalArg(2) y: Int, @OriginalArg(3) offsetY: Int, @OriginalArg(4) x: Int, @OriginalArg(6) width: Int) {
		@Pc(12) val flippedY: Int = super.glToolkit!!.viewportHeight - y - height
		super.glToolkit!!.setActiveTexture(this)
		super.glToolkit!!.beginReadFrame(x, flippedY, width, height) // Not part of the original client: see GlToolkit.beginReadFrame.
		OpenGL.glCopyTexSubImage2D(super.target, OpenGLConstants.GL_POINTS, offsetX, this.length - height - offsetY, x, flippedY, width, height)
		super.glToolkit!!.endReadFrame()
		OpenGL.glFlush()
	}

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(I)V")
	final override fun detach() {
		OpenGL.glFramebufferTexture2DEXT(this.framebufferTarget, this.framebufferAttachment, super.target, 0, 0)
		this.framebufferAttachment = -1
		this.framebufferTarget = -1
	}

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(IIIZIIII[BI)V")
	fun uploadTextureData(@OriginalArg(2) width: Int, @OriginalArg(3) flipVertical: Boolean, @OriginalArg(5) height: Int, @OriginalArg(6) format: Int, @OriginalArg(8) data: ByteArray?) {
		var data: ByteArray? = data
		if (flipVertical) {
			@Pc(15) val bytesPerPixel: Int = OpenGl2DTexture.getBytesPerPixel(6406)
			@Pc(19) val rowSize: Int = bytesPerPixel * width
			@Pc(23) val srcRowSize: Int = width * bytesPerPixel
			@Pc(28) val flipped: ByteArray = ByteArray(height * rowSize)
			for (@Pc(30) i in 0 until height) {
				@Pc(36) var dst: Int = rowSize * i
				@Pc(46) var src: Int = (height - i - 1) * srcRowSize
				for (@Pc(48) j in 0 until rowSize) {
					flipped[dst++] = data!![src++]
				}
			}
			data = flipped
		}
		super.glToolkit!!.setActiveTexture(this)
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_LINES)
		if (width != width) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, width)
		}
		OpenGL.glTexSubImage2Dub(super.target, OpenGLConstants.GL_POINTS, OpenGLConstants.GL_POINTS, OpenGLConstants.GL_POINTS, width, height, OpenGLConstants.GL_ALPHA, OpenGLConstants.GL_UNSIGNED_BYTE, data, 0)
		if (width != width) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, OpenGLConstants.GL_POINTS)
		}
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_TRIANGLES)
	}

	companion object {
		@OriginalMember(owner = "client!la", name = "a", descriptor = "(II[BZIII)V")
		@JvmStatic
		fun uploadMipmaps(@OriginalArg(0) height: Int, @OriginalArg(1) width: Int, @OriginalArg(2) data: ByteArray?, @OriginalArg(4) format: Int, @OriginalArg(5) target: Int, @OriginalArg(6) internalFormat: Int) {
			var height: Int = height
			var width: Int = width
			if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
				throw IllegalArgumentException("")
			} else if (height <= 0 || MipmapTextureSampler.isPowerOfTwo(height)) {
				@Pc(34) val bytesPerPixel: Int = OpenGl2DTexture.getBytesPerPixel(format)
				@Pc(36) var mipmapLevel: Int = 0
				@Pc(43) var minDimension: Int = if (width < height) width else height
				@Pc(52) var halfWidth: Int = width shr 1
				@Pc(56) var halfHeight: Int = height shr 1
				@Pc(58) var sourceData: ByteArray? = data
				@Pc(65) var destData: ByteArray? = ByteArray(halfWidth * halfHeight * bytesPerPixel)
				while (true) {
					OpenGL.glTexImage2Dub(target, mipmapLevel, internalFormat, width, height, 0, format, 5121, sourceData, 0)
					if (minDimension <= 1) {
						return
					}
					@Pc(84) val rowBytes: Int = width * bytesPerPixel
					@Pc(86) val swapTemp: ByteArray? = destData
					for (@Pc(88) i in 0 until bytesPerPixel) {
						@Pc(92) var destIndex: Int = i
						@Pc(94) var srcTopIndex: Int = i
						@Pc(98) var srcBottomIndex: Int = rowBytes + i
						for (@Pc(100) j in 0 until halfHeight) {
							for (@Pc(104) k in 0 until halfWidth) {
								@Pc(110) val topLeftSample: Byte = sourceData!![srcTopIndex]
								srcTopIndex += bytesPerPixel
								@Pc(120) val topSum: Int = topLeftSample + sourceData[srcTopIndex]
								@Pc(126) val partialSum: Int = topSum + sourceData[srcBottomIndex]
								srcTopIndex += bytesPerPixel
								srcBottomIndex += bytesPerPixel
								@Pc(140) val sampleSum: Int = partialSum + sourceData[srcBottomIndex]
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

		@OriginalMember(owner = "client!qe", name = "a", descriptor = "(II)I")
		@JvmStatic
		fun getBytesPerPixel(@OriginalArg(0) format: Int): Int {
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

		@OriginalMember(owner = "client!rga", name = "a", descriptor = "(III[FIII)V")
		@JvmStatic
		fun uploadMipmaps(@OriginalArg(0) width: Int, @OriginalArg(1) format: Int, @OriginalArg(2) target: Int, @OriginalArg(3) pixels: FloatArray?, @OriginalArg(5) height: Int, @OriginalArg(6) internalFormat: Int) {
			var width: Int = width
			var height: Int = height
			if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
				throw IllegalArgumentException("")
			} else if (height <= 0 || MipmapTextureSampler.isPowerOfTwo(height)) {
				@Pc(34) val componentCount: Int = OpenGl2DTexture.getBytesPerPixel(6408)
				@Pc(36) var level: Int = 0
				@Pc(47) var minDimension: Int = if (width < height) width else height
				@Pc(51) var nextWidth: Int = width shr 1
				@Pc(55) var nextHeight: Int = height shr 1
				@Pc(57) var levelPixels: FloatArray? = pixels
				@Pc(64) var scratch: FloatArray? = FloatArray(componentCount * nextWidth * nextHeight)
				while (true) {
					OpenGL.glTexImage2Df(OpenGLConstants.GL_TEXTURE_2D, level, OpenGLConstants.GL_RGBA16F, width, height, 0, OpenGLConstants.GL_RGBA, OpenGLConstants.GL_FLOAT, levelPixels, 0)
					if (minDimension <= 1) {
						return
					}
					@Pc(85) val rowStride: Int = width * componentCount
					@Pc(87) val downsampled: FloatArray? = scratch
					for (@Pc(89) i in 0 until componentCount) {
						@Pc(93) var dstIndex: Int = i
						@Pc(95) var srcIndex0: Int = i
						@Pc(99) var srcIndex1: Int = rowStride + i
						for (@Pc(101) j in 0 until nextHeight) {
							for (@Pc(105) k in 0 until nextWidth) {
								@Pc(111) val topLeft: Float = levelPixels!![srcIndex0]
								srcIndex0 += componentCount
								@Pc(121) val topSum: Float = topLeft + levelPixels[srcIndex0]
								srcIndex0 += componentCount
								@Pc(131) val partialSum: Float = topSum + levelPixels[srcIndex1]
								srcIndex1 += componentCount
								@Pc(141) val sum: Float = partialSum + levelPixels[srcIndex1]
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
