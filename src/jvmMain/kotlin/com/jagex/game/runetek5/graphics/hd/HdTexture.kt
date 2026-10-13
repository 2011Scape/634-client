/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlTexture (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.toolkit.companion.Binding
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

abstract class HdTexture : Binding {

	private var linearFilter: Boolean = false

	private var mipmapped: Boolean

	@JvmField
	val target: Int

	private val pixelCount: Int

	@JvmField
	val internalFormat: Int

	@JvmField
	val glToolkit: HdToolkit?

	@JvmField
	var id: Int

	constructor(glToolkit: HdToolkit?, target: Int, internalFormat: Int, pixelCount: Int, mipmapped: Boolean) {
		this.mipmapped = mipmapped
		this.target = target
		this.pixelCount = pixelCount
		this.internalFormat = internalFormat
		this.glToolkit = glToolkit
		HdGl.glGenTextures(1, HdTexture.TEXTURE_ID_BUFFER, 0)
		this.id = HdTexture.TEXTURE_ID_BUFFER[0]
		this.updateOccupiedSize(0)
	}

	fun generateMipmaps(): Boolean {
		if (!this.glToolkit!!.extFramebufferObject) {
			return false
		}
		val previousSize: Int = this.getSizeInBytes()
		this.glToolkit.setActiveTexture(this)
		HdGl.glGenerateMipmapEXT(this.target)
		this.mipmapped = true
		this.applyFilter()
		this.updateOccupiedSize(previousSize)
		return true
	}

	private fun getSizeInBytes(): Int {
		val size: Int = this.glToolkit!!.getBytesPerPixel(this.internalFormat) * this.pixelCount
		return if (this.mipmapped) size * 4 / 3 else size
	}

	fun delete() {
		if (this.id > 0) {
			this.glToolkit!!.requestTextureCleanup(this.id, this.getSizeInBytes())
			this.id = 0
		}
	}

	fun getId(): Int {
		return this.id
	}

	private fun updateOccupiedSize(previousSize: Int) {
		this.glToolkit!!.textureBuffersSize -= previousSize
		this.glToolkit.textureBuffersSize += this.getSizeInBytes()
	}

	fun setMipmapped(mipmapped: Boolean) {
		if (this.mipmapped != mipmapped) {
			val previousSize: Int = this.getSizeInBytes()
			this.mipmapped = true
			this.applyFilter()
			this.updateOccupiedSize(previousSize)
		}
	}

	private fun applyFilter() {
		this.glToolkit!!.setActiveTexture(this)
		if (this.linearFilter) {
			HdGl.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MIN_FILTER, if (this.mipmapped) OpenGLConstants.GL_LINEAR_MIPMAP_LINEAR else OpenGLConstants.GL_LINEAR)
			HdGl.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MAG_FILTER, OpenGLConstants.GL_LINEAR)
		} else {
			HdGl.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MIN_FILTER, if (this.mipmapped) OpenGLConstants.GL_NEAREST_MIPMAP_NEAREST else OpenGLConstants.GL_NEAREST)
			HdGl.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MAG_FILTER, OpenGLConstants.GL_NEAREST)
		}
	}

	fun setLinearFilter(linear: Boolean) {
		if (this.linearFilter != linear) {
			this.linearFilter = linear
			this.applyFilter()
		}
	}

	@Throws(Throwable::class)
	fun finalize() {
		this.delete()
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	abstract override fun detach()

	companion object {
		@JvmStatic
		fun uploadMipmapped(target: Int, internalFormat: Int, height: Int, pixelType: Int, width: Int, pixels: IntArray?) {
			var height: Int = height
			var width: Int = width
			if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
				throw IllegalArgumentException("")
			} else if (height <= 0 || MipmapTextureSampler.isPowerOfTwo(height)) {
				var mipmapLevel: Int = 0
				var minDimension: Int = if (height > width) width else height
				var halfWidth: Int = width shr 1
				var halfHeight: Int = height shr 1
				var argbPixels: IntArray? = pixels
				var replacementPixels: IntArray? = IntArray(halfHeight * halfWidth)
				while (true) {
					HdGl.glTexImage2Di(target, mipmapLevel, internalFormat, width, height, 0, OpenGLConstants.GL_BGRA, pixelType, argbPixels, 0)
					if (minDimension <= 1) {
						return
					}
					var dstPixelIndex: Int = 0
					var srcPixelIndex: Int = 0
					var srcPixelIndexOffset: Int = width
					val swapTemp: IntArray? = replacementPixels
					for (i in 0 until halfHeight) {
						for (j in 0 until halfWidth) {
							val colorArgbA: Int = argbPixels!![srcPixelIndex++]
							val colorArgbB: Int = argbPixels[srcPixelIndex++]
							val colorArgbC: Int = argbPixels[srcPixelIndexOffset++]
							val blueA: Int = colorArgbA and 0xFF
							val redA: Int = (colorArgbA shr 16) and 0xFF
							val alphaA: Int = (colorArgbA shr 24) and 0xFF
							val greenA: Int = (colorArgbA shr 8) and 0xFF
							val colorArgbD: Int = argbPixels[srcPixelIndexOffset++]
							val alphaAB: Int = alphaA + ((colorArgbB shr 24) and 0xFF)
							val redAB: Int = redA + ((colorArgbB shr 16) and 0xFF)
							val blueAB: Int = blueA + (colorArgbB and 0xFF)
							val greenAB: Int = greenA + ((colorArgbB shr 8) and 0xFF)
							val alphaABC: Int = alphaAB + ((colorArgbC shr 24) and 0xFF)
							val redABC: Int = redAB + ((colorArgbC shr 16) and 0xFF)
							val greenABC: Int = greenAB + ((colorArgbC shr 8) and 0xFF)
							val blueABC: Int = blueAB + (colorArgbC and 0xFF)
							val blueSum: Int = blueABC + (colorArgbD and 0xFF)
							val greenSum: Int = greenABC + ((colorArgbD shr 8) and 0xFF)
							val alphaSum: Int = alphaABC + ((colorArgbD shr 24) and 0xFF)
							val redSum: Int = redABC + ((colorArgbD shr 16) and 0xFF)
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

		@JvmField
		val TEXTURE_ID_BUFFER: IntArray = IntArray(1)

		@JvmStatic
		fun getBaseFormat(internalFormat: Int): Int {
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
