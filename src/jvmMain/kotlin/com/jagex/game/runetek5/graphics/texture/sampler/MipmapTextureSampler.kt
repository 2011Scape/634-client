package com.jagex.game.runetek5.graphics.texture.sampler

import com.jagex.game.runetek5.DataType
import com.jagex.game.runetek5.FilterMode
import com.jagex.game.runetek5.PixelFormat
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.toolkit.GlxToolkit
import com.jagex.game.runetek5.toolkit.opengl.GLActionFormat
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!hda")
abstract class MipmapTextureSampler : TextureSampler {

	@OriginalMember(owner = "client!hda", name = "t", descriptor = "Lclient!ria;")
	private var filter: FilterMode? = SampleMode.LINEAR

	@OriginalMember(owner = "client!hda", name = "i", descriptor = "I")
	private val texelCount: Int

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "Lclient!bga;")
	@JvmField
	val activeToolkit: GlxToolkit?

	@OriginalMember(owner = "client!hda", name = "c", descriptor = "Lclient!wq;")
	@JvmField
	val pixelFormat: PixelFormat?

	@OriginalMember(owner = "client!hda", name = "g", descriptor = "Lclient!baa;")
	@JvmField
	val pixelActionFormat: DataType?

	@OriginalMember(owner = "client!hda", name = "r", descriptor = "Z")
	private val mipmapping: Boolean

	@OriginalMember(owner = "client!hda", name = "l", descriptor = "I")
	@JvmField
	val target: Int

	@OriginalMember(owner = "client!hda", name = "o", descriptor = "I")
	private var texture: Int = 0

	@OriginalMember(owner = "client!hda", name = "<init>", descriptor = "(Lclient!bga;ILclient!wq;Lclient!baa;IZ)V")
	constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) textureTarget: Int, @OriginalArg(2) pixelFormat: PixelFormat?, @OriginalArg(3) pixelActionFormat: DataType?, @OriginalArg(4) pixelCount: Int, @OriginalArg(5) mipmapping: Boolean) {
		this.texelCount = pixelCount
		this.activeToolkit = glxToolkit
		this.pixelFormat = pixelFormat
		this.pixelActionFormat = pixelActionFormat
		this.mipmapping = mipmapping
		this.target = textureTarget
		OpenGL.glGenTextures(1, MipmapTextureSampler.TEXTURE_ID_BUFFER, 0)
		this.texture = MipmapTextureSampler.TEXTURE_ID_BUFFER[0]
		this.applyFilter()
		this.trackAllocation()
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(B)V")
	final override fun bindTexture() {
		@Pc(8) val currentTextureSlot: Int = this.activeToolkit!!.getCurrentTextureSlot()
		@Pc(20) val boundTarget: Int = this.activeToolkit!!.textureSlots[currentTextureSlot]
		if (boundTarget != this.target) {
			if (boundTarget != 0) {
				OpenGL.glBindTexture(boundTarget, 0)
				OpenGL.glDisable(boundTarget)
			}
			OpenGL.glEnable(this.target)
			this.activeToolkit!!.textureSlots[currentTextureSlot] = this.target
		}
		OpenGL.glBindTexture(this.target, this.texture)
	}

	@OriginalMember(owner = "client!hda", name = "c", descriptor = "(I)I")
	fun getInternalFormat(): Int {
		if (GLFormat.RGBA === this.pixelActionFormat) {
			if (this.pixelFormat === PixelFormat.aClass375_38) {
				return 6407
			}
			if (this.pixelFormat === GLFormat.HDR) {
				return OpenGLConstants.GL_RGBA
			}
			if (this.pixelFormat === GLActionFormat.PIXEL_FORMAT) {
				return OpenGLConstants.GL_ALPHA
			}
			if (PixelFormat.aClass375_44 === this.pixelFormat) {
				return 6409
			}
			if (this.pixelFormat === PixelFormat.aClass375_39) {
				return 6410
			}
			if (this.pixelFormat === PixelFormat.aClass375_49) {
				return 6145
			}
		} else if (DataType.aClass26_7 === this.pixelActionFormat) {
			if (this.pixelFormat === PixelFormat.aClass375_38) {
				return 34843
			}
			if (GLFormat.HDR === this.pixelFormat) {
				return OpenGLConstants.GL_RGBA16F
			}
			if (this.pixelFormat === GLActionFormat.PIXEL_FORMAT) {
				return 34844
			}
			if (this.pixelFormat === PixelFormat.aClass375_44) {
				return 34846
			}
			if (PixelFormat.aClass375_39 === this.pixelFormat) {
				return 34847
			}
			if (this.pixelFormat === PixelFormat.aClass375_49) {
				return 6145
			}
		} else if (this.pixelActionFormat === DataType.aClass26_8) {
			if (PixelFormat.aClass375_38 === this.pixelFormat) {
				return 34837
			}
			if (this.pixelFormat === GLFormat.HDR) {
				return 34836
			}
			if (this.pixelFormat === GLActionFormat.PIXEL_FORMAT) {
				return 34838
			}
			if (PixelFormat.aClass375_44 === this.pixelFormat) {
				return 34840
			}
			if (PixelFormat.aClass375_39 === this.pixelFormat) {
				return 34841
			}
			if (PixelFormat.aClass375_49 === this.pixelFormat) {
				return 6145
			}
		}
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!hda", name = "b", descriptor = "(Z)V")
	private fun applyFilter() {
		this.activeToolkit!!.setTextureSampler(this)
		if (this.filter === SampleMode.LINEAR) {
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MIN_FILTER, if (this.mipmapping) OpenGLConstants.GL_LINEAR_MIPMAP_LINEAR else OpenGLConstants.GL_LINEAR)
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MAG_FILTER, OpenGLConstants.GL_LINEAR)
		} else {
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MIN_FILTER, if (this.mipmapping) OpenGLConstants.GL_NEAREST_MIPMAP_NEAREST else OpenGLConstants.GL_NEAREST)
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MAG_FILTER, OpenGLConstants.GL_NEAREST)
		}
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(II[BII)V")
	fun generateMipmaps(@OriginalArg(1) width: Int, @OriginalArg(2) pixels: ByteArray?, @OriginalArg(3) height: Int, @OriginalArg(4) target: Int) {
		var width: Int = width
		var height: Int = height
		if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
			throw IllegalArgumentException("")
		} else if (height <= 0 || MipmapTextureSampler.isPowerOfTwo(height)) {
			@Pc(36) val bytesPerPixel: Int = this.pixelFormat!!.componentCount
			@Pc(38) var level: Int = 0
			@Pc(45) var minDimension: Int = if (width < height) width else height
			@Pc(49) var halfWidth: Int = width shr 1
			@Pc(60) var halfHeight: Int = height shr 1
			@Pc(62) var source: ByteArray? = pixels
			@Pc(69) var dest: ByteArray? = ByteArray(halfHeight * halfWidth * bytesPerPixel)
			while (true) {
				OpenGL.glTexImage2Dub(target, level, this.getInternalFormat(), width, height, 0, MipmapTextureSampler.getGlFormat(this.pixelFormat), 5121, source, 0)
				if (minDimension <= 1) {
					return
				}
				@Pc(94) val rowSize: Int = width * bytesPerPixel
				@Pc(96) var i: Int = 0
				while (i < bytesPerPixel) {
					@Pc(100) var destIndex: Int = i
					@Pc(102) var srcTopIndex: Int = i
					@Pc(107) var srcBottomIndex: Int = i + rowSize
					@Pc(109) var j: Int = 0
					while (j < halfHeight) {
						@Pc(113) var k: Int = 0
						while (k < halfWidth) {
							@Pc(119) val sample: Byte = source!![srcTopIndex]
							srcTopIndex += bytesPerPixel
							@Pc(129) val topSum: Int = sample.toInt() + source[srcTopIndex].toInt()
							@Pc(135) val partialSum: Int = topSum + source[srcBottomIndex].toInt()
							srcTopIndex += bytesPerPixel
							srcBottomIndex += bytesPerPixel
							@Pc(149) val sum: Int = partialSum + source[srcBottomIndex].toInt()
							dest!![destIndex] = (sum shr 2).toByte()
							srcBottomIndex += bytesPerPixel
							destIndex += bytesPerPixel
							k++
						}
						srcTopIndex += rowSize
						srcBottomIndex += rowSize
						j++
					}
					i++
				}
				@Pc(190) val swap: ByteArray? = dest
				dest = source
				width = halfWidth
				height = halfHeight
				source = swap
				halfWidth = halfWidth shr 0x1
				halfHeight = halfHeight shr 0x1
				level++
				minDimension = minDimension shr 0x1
			}
		} else {
			throw IllegalArgumentException("")
		}
	}

	@OriginalMember(owner = "client!hda", name = "d", descriptor = "(I)I")
	private fun getSizeInBytes(): Int {
		@Pc(14) val size: Int = this.texelCount * this.pixelActionFormat!!.bytesPerEntry * this.pixelFormat!!.componentCount
		return if (this.mipmapping) size * 4 / 3 else size
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(II[III)V")
	fun generateMipmaps(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int, @OriginalArg(2) pixels: IntArray?, @OriginalArg(3) target: Int) {
		var width: Int = width
		var height: Int = height
		if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
			throw IllegalArgumentException("")
		} else if (height > 0 && !MipmapTextureSampler.isPowerOfTwo(height)) {
			throw IllegalArgumentException("")
		} else if (GLFormat.HDR === this.pixelFormat) {
			@Pc(46) var level: Int = 0
			@Pc(57) var minDimension: Int = if (height <= width) height else width
			@Pc(61) var halfWidth: Int = width shr 1
			@Pc(65) var halfHeight: Int = height shr 1
			@Pc(67) var source: IntArray? = pixels
			@Pc(72) var dest: IntArray? = IntArray(halfHeight * halfWidth)
			while (true) {
				OpenGL.glTexImage2Di(target, level, this.getInternalFormat(), width, height, 0, OpenGLConstants.GL_BGRA, this.activeToolkit!!.pixelType, source, 0)
				if (minDimension <= 1) {
					return
				}
				@Pc(99) var destIndex: Int = 0
				@Pc(101) var srcIndex: Int = 0
				@Pc(105) var srcNextRowIndex: Int = width
				@Pc(107) var i: Int = 0
				while (i < halfHeight) {
					@Pc(111) var j: Int = 0
					while (j < halfWidth) {
						@Pc(118) val colourA: Int = source!![srcIndex++]
						@Pc(123) val colourB: Int = source[srcIndex++]
						@Pc(128) val colourC: Int = source[srcNextRowIndex++]
						@Pc(134) val red: Int = (colourA shr 16) and 0xFF
						@Pc(140) val alpha: Int = (colourA shr 24) and 0xFF
						@Pc(144) val blue: Int = colourA and 0xFF
						@Pc(150) val green: Int = (colourA shr 8) and 0xFF
						@Pc(155) val colourD: Int = source[srcNextRowIndex++]
						@Pc(161) val blueAB: Int = blue + (colourB and 0xFF)
						@Pc(169) val redAB: Int = red + ((colourB shr 16) and 0xFF)
						@Pc(177) val alphaAB: Int = alpha + ((colourB shr 24) and 0xFF)
						@Pc(185) val greenAB: Int = green + ((colourB shr 8) and 0xFF)
						@Pc(193) val alphaABC: Int = alphaAB + ((colourC shr 24) and 0xFF)
						@Pc(199) val blueABC: Int = blueAB + (colourC and 0xFF)
						@Pc(207) val greenABC: Int = greenAB + ((colourC shr 8) and 0xFF)
						@Pc(215) val redABC: Int = redAB + ((colourC shr 16) and 0xFF)
						@Pc(223) val alphaSum: Int = alphaABC + ((colourD shr 24) and 0xFF)
						@Pc(231) val redSum: Int = redABC + ((colourD shr 16) and 0xFF)
						@Pc(239) val greenSum: Int = greenABC + ((colourD shr 8) and 0xFF)
						@Pc(245) val blueSum: Int = blueABC + (colourD and 0xFF)
						dest!![destIndex++] = ((greenSum and 0x3FC) shl 6) or ((alphaSum and 0x3FC) shl 22) or ((redSum and 0x3FC) shl 14) or ((blueSum shr 2) and 0xFF)
						j++
					}
					srcIndex += width
					srcNextRowIndex += width
					i++
				}
				@Pc(294) val swap: IntArray? = dest
				dest = source
				height = halfHeight
				source = swap
				width = halfWidth
				halfWidth = halfWidth shr 0x1
				level++
				minDimension = minDimension shr 0x1
				halfHeight = halfHeight shr 0x1
			}
		} else {
			throw IllegalArgumentException("")
		}
	}

	@OriginalMember(owner = "client!hda", name = "e", descriptor = "(I)V")
	private fun delete() {
		if (this.texture > 0) {
			this.activeToolkit!!.requestTextureDeletion(this.getSizeInBytes(), this.texture)
			this.texture = 0
		}
	}

	@OriginalMember(owner = "client!hda", name = "b", descriptor = "(IB)V")
	private fun trackAllocation() {
		this.activeToolkit!!.texturesSizeOccupied += this.getSizeInBytes()
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(Lclient!ria;I)V")
	final override fun setFilterMode(@OriginalArg(0) filter: FilterMode?) {
		if (filter !== this.filter) {
			this.filter = filter
			this.applyFilter()
		}
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(III[FI)V")
	fun generateMipmaps(@OriginalArg(0) target: Int, @OriginalArg(2) width: Int, @OriginalArg(3) pixels: FloatArray?, @OriginalArg(4) height: Int) {
		var width: Int = width
		var height: Int = height
		if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
			throw IllegalArgumentException("")
		} else if (height <= 0 || MipmapTextureSampler.isPowerOfTwo(height)) {
			@Pc(36) val componentCount: Int = this.pixelFormat!!.componentCount
			@Pc(38) var level: Int = 0
			@Pc(56) var minDimension: Int = if (width < height) width else height
			@Pc(60) var halfWidth: Int = width shr 1
			@Pc(64) var halfHeight: Int = height shr 1
			@Pc(66) var source: FloatArray? = pixels
			@Pc(73) var dest: FloatArray? = FloatArray(halfWidth * halfHeight * componentCount)
			while (true) {
				OpenGL.glTexImage2Df(target, level, this.getInternalFormat(), width, height, 0, MipmapTextureSampler.getGlFormat(this.pixelFormat), OpenGLConstants.GL_FLOAT, source, 0)
				if (minDimension <= 1) {
					return
				}
				@Pc(97) val rowSize: Int = width * componentCount
				@Pc(99) var i: Int = 0
				while (i < componentCount) {
					@Pc(103) var destIndex: Int = i
					@Pc(105) var srcTopIndex: Int = i
					@Pc(109) var srcBottomIndex: Int = i + rowSize
					@Pc(111) var j: Int = 0
					while (j < halfHeight) {
						@Pc(115) var k: Int = 0
						while (k < halfWidth) {
							@Pc(121) val sample: Float = source!![srcTopIndex]
							srcTopIndex += componentCount
							@Pc(131) val topSum: Float = sample + source[srcTopIndex]
							@Pc(137) val partialSum: Float = topSum + source[srcBottomIndex]
							srcTopIndex += componentCount
							srcBottomIndex += componentCount
							@Pc(151) val sum: Float = partialSum + source[srcBottomIndex]
							srcBottomIndex += componentCount
							dest!![destIndex] = sum * 0.25f
							destIndex += componentCount
							k++
						}
						srcBottomIndex += rowSize
						srcTopIndex += rowSize
						j++
					}
					i++
				}
				@Pc(187) val swap: FloatArray? = dest
				dest = source
				width = halfWidth
				height = halfHeight
				source = swap
				halfWidth = halfWidth shr 0x1
				halfHeight = halfHeight shr 0x1
				minDimension = minDimension shr 0x1
				level++
			}
		} else {
			throw IllegalArgumentException("")
		}
	}

	@OriginalMember(owner = "client!hda", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	fun finalize() {
		this.delete()
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	companion object {
		@OriginalMember(owner = "client!hda", name = "j", descriptor = "[I")
		@JvmField
		var textures: IntArray? = null

		@OriginalMember(owner = "client!qq", name = "a", descriptor = "(II)Z")
		@JvmStatic
		fun isPowerOfTwo(@OriginalArg(1) value: Int): Boolean {
			return (value and -value) == value
		}

		@OriginalMember(owner = "client!oda", name = "m", descriptor = "[I")
		@JvmField
		val TEXTURE_ID_BUFFER: IntArray = IntArray(1)

		@OriginalMember(owner = "client!hga", name = "a", descriptor = "(ILclient!wq;)I")
		@JvmStatic
		fun getGlFormat(@OriginalArg(1) pixelFormat: PixelFormat?): Int {
			if (pixelFormat === PixelFormat.aClass375_38) {
				return 6407
			} else if (GLFormat.HDR === pixelFormat) {
				return 6408
			} else if (pixelFormat === GLActionFormat.PIXEL_FORMAT) {
				return 6406
			} else if (PixelFormat.aClass375_44 === pixelFormat) {
				return 6409
			} else if (pixelFormat === PixelFormat.aClass375_39) {
				return 6410
			} else if (pixelFormat === PixelFormat.aClass375_49) {
				return 6145
			} else {
				throw IllegalStateException()
			}
		}
	}
}
