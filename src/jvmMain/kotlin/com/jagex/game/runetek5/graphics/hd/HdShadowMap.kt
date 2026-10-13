/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlShadowMap (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

class HdShadowMap {

	private var shadowTiles: Array<Array<HdShadowTile?>?>? = null

	private val glToolkit: HdToolkit?

	private val glGround: HdGround?

	@JvmField
	val width: Int

	private val length: Int

	private val sizeShift: Int

	@JvmField
	val shadowMapperData: ByteArray

	private val sizeX: Int

	private val sizeY: Int

	constructor(glToolkit: HdToolkit?, glGround: HdGround?) {
		this.glToolkit = glToolkit
		this.glGround = glGround
		this.width = ((this.glGround!!.tileUnits * this.glGround.width) shr this.glToolkit!!.shadowScaleShift) + 2
		this.length = ((this.glGround.tileUnits * this.glGround.length) shr this.glToolkit.shadowScaleShift) + 2
		this.sizeShift = this.glToolkit.shadowScaleShift + 7 - this.glGround.tileScale
		this.shadowMapperData = ByteArray(this.width * this.length)
		this.sizeX = this.glGround.width shr this.sizeShift
		this.sizeY = this.glGround.length shr this.sizeShift
	}

	fun removeShadow(shadow: Shadow?, x: Int, y: Int) {
		var x: Int = x
		var y: Int = y
		val glShadow: HdShadow? = shadow as HdShadow?
		x += glShadow!!.startX + 1
		y += glShadow.startZ + 1
		var dataOffset: Int = this.width * y + x
		var srcOffset: Int = 0
		var clippedHeight: Int = glShadow.sizeZ
		var clippedWidth: Int = glShadow.sizeX
		var dstRowStride: Int = this.width - clippedWidth
		var srcRowStride: Int = 0
		var clippedAmount: Int
		if (y <= 0) {
			clippedAmount = 1 - y
			srcOffset = clippedAmount * clippedWidth
			clippedHeight -= clippedAmount
			y = 1
			dataOffset += clippedAmount * this.width
		}
		if (this.length <= y + clippedHeight) {
			clippedAmount = clippedHeight + y + 1 - this.length
			clippedHeight -= clippedAmount
		}
		if (x <= 0) {
			clippedAmount = 1 - x
			clippedWidth -= clippedAmount
			dataOffset += clippedAmount
			x = 1
			dstRowStride += clippedAmount
			srcRowStride = clippedAmount
			srcOffset += clippedAmount
		}
		if (this.width <= clippedWidth + x) {
			clippedAmount = x + clippedWidth + 1 - this.width
			srcRowStride += clippedAmount
			dstRowStride += clippedAmount
			clippedWidth -= clippedAmount
		}
		if (clippedWidth > 0 && clippedHeight > 0) {
			HdShadowMap.subtractShadowData(srcOffset, this.shadowMapperData, clippedHeight, dataOffset, dstRowStride, srcRowStride, glShadow.shade, clippedWidth)
			this.invalidateShadowTiles(clippedHeight, clippedWidth, x, y)
		}
	}

	private fun invalidateShadowTiles(regionHeight: Int, regionWidth: Int, startX: Int, startY: Int) {
		if (this.shadowTiles == null) {
			return
		}
		val minChunkX: Int = (startX - 1) shr 7
		val maxChunkX: Int = (startX + regionWidth - 1 - 1) shr 7
		val minChunkY: Int = (startY - 1) shr 7
		val maxChunkY: Int = (regionHeight + startY - 1 - 1) shr 7
		for (i in minChunkX..maxChunkX) {
			val glShadowMapBlocks: Array<HdShadowTile?>? = this.shadowTiles!![i]
			for (j in minChunkY..maxChunkY) {
				glShadowMapBlocks!![j]!!.needsTextureRegeneration = true
			}
		}
	}

	fun shouldApplyShadow(x: Int, y: Int, shadow: Shadow?): Boolean {
		var x: Int = x
		var y: Int = y
		val glShadow: HdShadow? = shadow as HdShadow?
		y += glShadow!!.startZ + 1
		x += glShadow.startX + 1
		var dataOffset: Int = y * this.width + x
		var clippedHeight: Int = glShadow.sizeZ
		var clippedWidth: Int = glShadow.sizeX
		var rowStride: Int = this.width - clippedWidth
		var clippedAmount: Int
		if (y <= 0) {
			clippedAmount = 1 - y
			clippedHeight -= clippedAmount
			y = 1
			dataOffset += this.width * clippedAmount
		}
		if (this.length <= clippedHeight + y) {
			clippedAmount = y + clippedHeight + 1 - this.length
			clippedHeight -= clippedAmount
		}
		if (x <= 0) {
			clippedAmount = 1 - x
			clippedWidth -= clippedAmount
			dataOffset += clippedAmount
			rowStride += clippedAmount
			x = 1
		}
		if (this.width <= x + clippedWidth) {
			clippedAmount = x + clippedWidth + 1 - this.width
			clippedWidth -= clippedAmount
			rowStride += clippedAmount
		}
		if (clippedWidth > 0 && clippedHeight > 0) {
			rowStride += this.width * 7
			return HdShadowMap.containsUnshadowedBlock(rowStride, clippedHeight, clippedWidth, dataOffset, this.shadowMapperData)
		} else {
			return false
		}
	}

	fun renderShadowOverlay(belowLowestPlane: Boolean, tilesOnScreen: Array<BooleanArray?>?, visibleRange: Int, cameraGridY: Int, cameraGridX: Int) {
		this.glToolkit!!.setLightingEnabled(false)
		this.glToolkit.setDepthMask(false)
		this.glToolkit.renderTexture(-2)
		this.glToolkit.setColourOp(1)
		this.glToolkit.setBlendMode(1)
		val texCoordScale: Float = 1.0f / (this.glToolkit.shadowScale * 128).toFloat()
		var chunkY: Int
		var chunkStartY: Int
		var chunkEndY: Int
		var chunkX: Int
		var local71: Int
		var local76: Int
		var local83: Int
		if (belowLowestPlane) {
			chunkY = 0
			while (chunkY < this.sizeY) {
				chunkStartY = chunkY shl this.sizeShift
				chunkEndY = (chunkY + 1) shl this.sizeShift
				chunkX = 0
				label81@ while (chunkX < this.sizeX) {
					val chunkStartX: Int = chunkX shl this.sizeShift
					val chunkEndX: Int = (chunkX + 1) shl this.sizeShift
					for (tileX in chunkStartX until chunkEndX) {
						if (tileX - cameraGridX >= -visibleRange && visibleRange >= tileX - cameraGridX) {
							for (i in chunkStartY until chunkEndY) {
								if (-visibleRange <= i - cameraGridY && i - cameraGridY <= visibleRange && tilesOnScreen!![visibleRange + tileX - cameraGridX]!![visibleRange + i - cameraGridY]) {
									HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
									HdGl.glLoadIdentity()
									HdGl.glScalef(texCoordScale, texCoordScale, 1.0f)
									HdGl.glTranslatef((-chunkX).toFloat() / texCoordScale, (-chunkY).toFloat() / texCoordScale, 1.0f)
									HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
									this.shadowTiles!![chunkX]!![chunkY]!!.drawShadow()
									// Java's "continue label81" runs the loop update first.
									chunkX++
									continue@label81
								}
							}
						}
					}
					chunkX++
				}
				chunkY++
			}
		} else {
			chunkY = 0
			while (chunkY < this.sizeY) {
				chunkStartY = chunkY shl this.sizeShift
				chunkEndY = (chunkY + 1) shl this.sizeShift
				chunkX = 0
				while (chunkX < this.sizeX) {
					var indexCount: Int = 0
					val chunkStartX: Int = chunkX shl this.sizeShift
					val chunkEndX: Int = (chunkX + 1) shl this.sizeShift
					val gpuPacket: GlBitBuffer? = this.glToolkit.textureBuffer
					gpuPacket!!.currentPosition = 0
					for (j in chunkStartY until chunkEndY) {
						if (j - cameraGridY >= -visibleRange && j - cameraGridY <= visibleRange) {
							var tileIndex: Int = chunkStartX + j * this.glGround!!.width
							for (k in chunkStartX until chunkEndX) {
								if (k - cameraGridX >= -visibleRange && k - cameraGridX <= visibleRange && tilesOnScreen!![visibleRange + k - cameraGridX]!![visibleRange + j - cameraGridY]) {
									val tileVertexIndices: ShortArray? = this.glGround.tileVertexIndices[tileIndex]
									if (tileVertexIndices != null) {
										var index: Int
										if (this.glToolkit.bigEndianess) {
											index = 0
											while (index < tileVertexIndices.size) {
												indexCount++
												gpuPacket.writeShort(tileVertexIndices[index].toInt() and 0xFFFF)
												index++
											}
										} else {
											index = 0
											while (index < tileVertexIndices.size) {
												gpuPacket.writeShortLE(tileVertexIndices[index].toInt() and 0xFFFF)
												indexCount++
												index++
											}
										}
									}
								}
								tileIndex++
							}
						}
					}
					if (indexCount > 0) {
						HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
						HdGl.glLoadIdentity()
						HdGl.glScalef(texCoordScale, texCoordScale, 1.0f)
						HdGl.glTranslatef((-chunkX).toFloat() / texCoordScale, (-chunkY).toFloat() / texCoordScale, 1.0f)
						HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
						this.shadowTiles!![chunkX]!![chunkY]!!.drawShadowWithIndices(gpuPacket.buffer, indexCount)
					}
					chunkX++
				}
				chunkY++
			}
		}
		HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		HdGl.glLoadIdentity()
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	fun initializeShadowTiles() {
		this.shadowTiles = Array<Array<HdShadowTile?>?>(this.sizeX) { arrayOfNulls<HdShadowTile>(this.sizeY) }
		for (chunkY in 0 until this.sizeY) {
			for (chunkX in 0 until this.sizeX) {
				this.shadowTiles!![chunkX]!![chunkY] = HdShadowTile(this.glToolkit, this, this.glGround, chunkX, chunkY, this.sizeShift, chunkX * 128 + 1, chunkY * 128 + 1)
			}
		}
	}

	fun addShadow(x: Int, y: Int, shadow: Shadow?) {
		var x: Int = x
		var y: Int = y
		val glShadow: HdShadow? = shadow as HdShadow?
		y += glShadow!!.startZ + 1
		x += glShadow.startX + 1
		var dataOffset: Int = this.width * y + x
		var srcOffset: Int = 0
		var clippedHeight: Int = glShadow.sizeZ
		var clippedWidth: Int = glShadow.sizeX
		var dstRowStride: Int = this.width - clippedWidth
		var clippedAmount: Int
		if (y <= 0) {
			clippedAmount = 1 - y
			srcOffset = clippedWidth * clippedAmount
			clippedHeight -= clippedAmount
			y = 1
			dataOffset += clippedAmount * this.width
		}
		var srcRowStride: Int = 0
		if (y + clippedHeight >= this.length) {
			clippedAmount = y + clippedHeight + 1 - this.length
			clippedHeight -= clippedAmount
		}
		if (x <= 0) {
			clippedAmount = 1 - x
			clippedWidth -= clippedAmount
			dataOffset += clippedAmount
			x = 1
			srcRowStride = clippedAmount
			srcOffset += clippedAmount
			dstRowStride += clippedAmount
		}
		if (this.width <= clippedWidth + x) {
			clippedAmount = clippedWidth + x + 1 - this.width
			srcRowStride += clippedAmount
			clippedWidth -= clippedAmount
			dstRowStride += clippedAmount
		}
		if (clippedWidth > 0 && clippedHeight > 0) {
			HdShadowMap.addShadowData(srcRowStride, srcOffset, glShadow.shade, clippedWidth, this.shadowMapperData, clippedHeight, dataOffset, dstRowStride)
			this.invalidateShadowTiles(clippedHeight, clippedWidth, x, y)
		}
	}

	companion object {
		@JvmStatic
		fun subtractShadowData(srcOffset: Int, dst: ByteArray?, height: Int, dstOffset: Int, dstRowStride: Int, srcRowStride: Int, src: ByteArray?, width: Int) {
			var srcOffset: Int = srcOffset
			var dstOffset: Int = dstOffset
			val blockCount: Int = -(width shr 2)
			val remainderCount: Int = -(width and 0x3)
			for (i in -height until 0) {
				var dstIndex: Int
				for (j in blockCount until 0) {
					dstIndex = dstOffset++
					dst!![dstIndex] = (dst[dstIndex] - src!![srcOffset++]).toByte()
					val dstIndex2: Int = dstOffset++
					dst[dstIndex2] = (dst[dstIndex2] - src[srcOffset++]).toByte()
					val dstIndex3: Int = dstOffset++
					dst[dstIndex3] = (dst[dstIndex3] - src[srcOffset++]).toByte()
					val dstIndex4: Int = dstOffset++
					dst[dstIndex4] = (dst[dstIndex4] - src[srcOffset++]).toByte()
				}
				for (k in remainderCount until 0) {
					dstIndex = dstOffset++
					dst!![dstIndex] = (dst[dstIndex] - src!![srcOffset++]).toByte()
				}
				dstOffset += dstRowStride
				srcOffset += srcRowStride
			}
		}

		@JvmStatic
		fun addShadowData(srcRowStride: Int, srcOffset: Int, src: ByteArray?, width: Int, dst: ByteArray?, height: Int, dstOffset: Int, dstRowStride: Int) {
			var srcOffset: Int = srcOffset
			var dstOffset: Int = dstOffset
			val blockCount: Int = -(width shr 2)
			val remainder: Int = -(width and 0x3)
			for (i in -height until 0) {
				var dstIndex: Int
				for (j in blockCount until 0) {
					dstIndex = dstOffset++
					dst!![dstIndex] = (dst[dstIndex] + src!![srcOffset++]).toByte()
					val dstIndex2: Int = dstOffset++
					dst[dstIndex2] = (dst[dstIndex2] + src[srcOffset++]).toByte()
					val dstIndex3: Int = dstOffset++
					dst[dstIndex3] = (dst[dstIndex3] + src[srcOffset++]).toByte()
					val dstIndex4: Int = dstOffset++
					dst[dstIndex4] = (dst[dstIndex4] + src[srcOffset++]).toByte()
				}
				for (k in remainder until 0) {
					dstIndex = dstOffset++
					dst!![dstIndex] = (dst[dstIndex] + src!![srcOffset++]).toByte()
				}
				dstOffset += dstRowStride
				srcOffset += srcRowStride
			}
		}

		@JvmStatic
		fun containsUnshadowedBlock(rowStride: Int, height: Int, width: Int, offset: Int, data: ByteArray?): Boolean {
			var offset: Int = offset
			val remainder: Int = width % 8
			val padding: Int
			if (remainder == 0) {
				padding = 0
			} else {
				padding = 8 - remainder
			}
			val rowBlocks: Int = -((height + 8 - 1) / 8)
			val columnBlocks: Int = -((width + 8 - 1) / 8)
			for (i in rowBlocks until 0) {
				for (j in columnBlocks until 0) {
					if (data!![offset].toInt() == 0) {
						return true
					}
					offset += 8
				}
				offset -= padding
				if (data!![offset - 1].toInt() == 0) {
					return true
				}
				offset += rowStride
			}
			return false
		}
	}
}
