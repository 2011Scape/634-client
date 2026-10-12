package com.jagex.game.runetek5.graphics.gl

import com.jagex.game.runetek5.toolkit.companion.Binding
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler

@OriginalClass("client!ud")
abstract class GlTexture : Binding {

	@OriginalMember(owner = "client!ud", name = "j", descriptor = "Z")
	private var linearFilter: Boolean = false

	@OriginalMember(owner = "client!ud", name = "n", descriptor = "Z")
	private var mipmapped: Boolean

	@OriginalMember(owner = "client!ud", name = "c", descriptor = "I")
	@JvmField
	val target: Int

	@OriginalMember(owner = "client!ud", name = "a", descriptor = "I")
	private val pixelCount: Int

	@OriginalMember(owner = "client!ud", name = "l", descriptor = "I")
	@JvmField
	val internalFormat: Int

	@OriginalMember(owner = "client!ud", name = "e", descriptor = "Lclient!qo;")
	@JvmField
	val glToolkit: GlToolkit?

	@OriginalMember(owner = "client!ud", name = "m", descriptor = "I")
	@JvmField
	var id: Int

	@OriginalMember(owner = "client!ud", name = "<init>", descriptor = "(Lclient!qo;IIIZ)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) target: Int, @OriginalArg(2) internalFormat: Int, @OriginalArg(3) pixelCount: Int, @OriginalArg(4) mipmapped: Boolean) {
		this.mipmapped = mipmapped
		this.target = target
		this.pixelCount = pixelCount
		this.internalFormat = internalFormat
		this.glToolkit = glToolkit
		OpenGL.glGenTextures(1, GlTexture.TEXTURE_ID_BUFFER, 0)
		this.id = GlTexture.TEXTURE_ID_BUFFER[0]
		this.updateOccupiedSize(0)
	}

	@OriginalMember(owner = "client!ud", name = "b", descriptor = "(I)Z")
	fun generateMipmaps(): Boolean {
		if (!this.glToolkit!!.extFramebufferObject) {
			return false
		}
		@Pc(19) val previousSize: Int = this.getSizeInBytes()
		this.glToolkit.setActiveTexture(this)
		OpenGL.glGenerateMipmapEXT(this.target)
		this.mipmapped = true
		this.applyFilter()
		this.updateOccupiedSize(previousSize)
		return true
	}

	@OriginalMember(owner = "client!ud", name = "c", descriptor = "(I)I")
	private fun getSizeInBytes(): Int {
		@Pc(19) val size: Int = this.glToolkit!!.getBytesPerPixel(this.internalFormat) * this.pixelCount
		return if (this.mipmapped) size * 4 / 3 else size
	}

	@OriginalMember(owner = "client!ud", name = "d", descriptor = "(I)V")
	fun delete() {
		if (this.id > 0) {
			this.glToolkit!!.requestTextureCleanup(this.id, this.getSizeInBytes())
			this.id = 0
		}
	}

	@OriginalMember(owner = "client!ud", name = "e", descriptor = "(I)I")
	fun getId(): Int {
		return this.id
	}

	@OriginalMember(owner = "client!ud", name = "a", descriptor = "(II)V")
	private fun updateOccupiedSize(@OriginalArg(0) previousSize: Int) {
		this.glToolkit!!.textureBuffersSize -= previousSize
		this.glToolkit.textureBuffersSize += this.getSizeInBytes()
	}

	@OriginalMember(owner = "client!ud", name = "a", descriptor = "(ZB)V")
	fun setMipmapped(@OriginalArg(0) mipmapped: Boolean) {
		if (this.mipmapped != mipmapped) {
			@Pc(18) val previousSize: Int = this.getSizeInBytes()
			this.mipmapped = true
			this.applyFilter()
			this.updateOccupiedSize(previousSize)
		}
	}

	@OriginalMember(owner = "client!ud", name = "a", descriptor = "(Z)V")
	private fun applyFilter() {
		this.glToolkit!!.setActiveTexture(this)
		if (this.linearFilter) {
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MIN_FILTER, if (this.mipmapped) OpenGLConstants.GL_LINEAR_MIPMAP_LINEAR else OpenGLConstants.GL_LINEAR)
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MAG_FILTER, OpenGLConstants.GL_LINEAR)
		} else {
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MIN_FILTER, if (this.mipmapped) OpenGLConstants.GL_NEAREST_MIPMAP_NEAREST else OpenGLConstants.GL_NEAREST)
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MAG_FILTER, OpenGLConstants.GL_NEAREST)
		}
	}

	@OriginalMember(owner = "client!ud", name = "a", descriptor = "(IZ)V")
	fun setLinearFilter(@OriginalArg(1) linear: Boolean) {
		if (this.linearFilter != linear) {
			this.linearFilter = linear
			this.applyFilter()
		}
	}

	@OriginalMember(owner = "client!ud", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	fun finalize() {
		this.delete()
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	@OriginalMember(owner = "client!ud", name = "a", descriptor = "(I)V")
	abstract override fun detach()

	companion object {
		@OriginalMember(owner = "client!en", name = "a", descriptor = "(IIIII[III)V")
		@JvmStatic
		fun uploadMipmapped(@OriginalArg(0) target: Int, @OriginalArg(1) internalFormat: Int, @OriginalArg(2) height: Int, @OriginalArg(3) pixelType: Int, @OriginalArg(4) width: Int, @OriginalArg(5) pixels: IntArray?) {
			var height: Int = height
			var width: Int = width
			if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
				throw IllegalArgumentException("")
			} else if (height <= 0 || MipmapTextureSampler.isPowerOfTwo(height)) {
				@Pc(37) var mipmapLevel: Int = 0
				@Pc(44) var minDimension: Int = if (height > width) width else height
				@Pc(53) var halfWidth: Int = width shr 1
				@Pc(57) var halfHeight: Int = height shr 1
				@Pc(59) var argbPixels: IntArray? = pixels
				@Pc(64) var replacementPixels: IntArray? = IntArray(halfHeight * halfWidth)
				while (true) {
					OpenGL.glTexImage2Di(target, mipmapLevel, internalFormat, width, height, 0, OpenGLConstants.GL_BGRA, pixelType, argbPixels, 0)
					if (minDimension <= 1) {
						return
					}
					@Pc(80) var dstPixelIndex: Int = 0
					@Pc(82) var srcPixelIndex: Int = 0
					@Pc(86) var srcPixelIndexOffset: Int = width
					@Pc(88) val swapTemp: IntArray? = replacementPixels
					for (@Pc(90) i in 0 until halfHeight) {
						for (@Pc(94) j in 0 until halfWidth) {
							@Pc(101) val colorArgbA: Int = argbPixels!![srcPixelIndex++]
							@Pc(106) val colorArgbB: Int = argbPixels[srcPixelIndex++]
							@Pc(111) val colorArgbC: Int = argbPixels[srcPixelIndexOffset++]
							@Pc(115) val blueA: Int = colorArgbA and 0xFF
							@Pc(121) val redA: Int = (colorArgbA shr 16) and 0xFF
							@Pc(127) val alphaA: Int = (colorArgbA shr 24) and 0xFF
							@Pc(133) val greenA: Int = (colorArgbA shr 8) and 0xFF
							@Pc(138) val colorArgbD: Int = argbPixels[srcPixelIndexOffset++]
							@Pc(146) val alphaAB: Int = alphaA + ((colorArgbB shr 24) and 0xFF)
							@Pc(154) val redAB: Int = redA + ((colorArgbB shr 16) and 0xFF)
							@Pc(160) val blueAB: Int = blueA + (colorArgbB and 0xFF)
							@Pc(168) val greenAB: Int = greenA + ((colorArgbB shr 8) and 0xFF)
							@Pc(176) val alphaABC: Int = alphaAB + ((colorArgbC shr 24) and 0xFF)
							@Pc(184) val redABC: Int = redAB + ((colorArgbC shr 16) and 0xFF)
							@Pc(192) val greenABC: Int = greenAB + ((colorArgbC shr 8) and 0xFF)
							@Pc(198) val blueABC: Int = blueAB + (colorArgbC and 0xFF)
							@Pc(204) val blueSum: Int = blueABC + (colorArgbD and 0xFF)
							@Pc(212) val greenSum: Int = greenABC + ((colorArgbD shr 8) and 0xFF)
							@Pc(220) val alphaSum: Int = alphaABC + ((colorArgbD shr 24) and 0xFF)
							@Pc(228) val redSum: Int = redABC + ((colorArgbD shr 16) and 0xFF)
							replacementPixels!![dstPixelIndex++] = ((greenSum and 0x3FC) shl 6) or ((redSum and 0x3FC) shl 14) or ((alphaSum and 0x3FC) shl 22) or ((blueSum shr 2) and 0xFF)
						}
						srcPixelIndex += width
						srcPixelIndexOffset += width
					}
					replacementPixels = argbPixels
					height = halfHeight
					width = halfWidth
					argbPixels = swapTemp
					mipmapLevel++
					halfWidth = halfWidth shr 0x1
					minDimension = minDimension shr 0x1
					halfHeight = halfHeight shr 0x1
				}
			} else {
				throw IllegalArgumentException("")
			}
		}

		@OriginalMember(owner = "client!gn", name = "r", descriptor = "[I")
		@JvmField
		val TEXTURE_ID_BUFFER: IntArray = IntArray(1)

		@OriginalMember(owner = "client!hf", name = "a", descriptor = "(ZI)I")
		@JvmStatic
		fun getBaseFormat(@OriginalArg(1) internalFormat: Int): Int {
			if (internalFormat == 6407 || internalFormat == 34843 || internalFormat == 34837) {
				return 6407
			} else if (internalFormat == 6408 || internalFormat == 34842 || internalFormat == 34836) {
				return 6408
			} else if (internalFormat == 6406 || internalFormat == 34844) {
				return 6406
			} else if (internalFormat == 6409 || internalFormat == 34846) {
				return 6409
			} else if (internalFormat == 6410 || internalFormat == 34847) {
				return 6410
			} else if (internalFormat == 6402) {
				return 6402
			} else {
				throw IllegalArgumentException("")
			}
		}
	}
}
