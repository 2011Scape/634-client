package com.jagex.game.runetek5.graphics.gl

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer
import com.jagex.game.runetek5.cache.node.lru.Shadow
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.lru.GlShadow
import com.jagex.game.runetek5.graphics.shadow.ShadowTileGL
import com.jagex.game.runetek5.scene.ground.GlGround

@OriginalClass("client!mha")
class GlShadowMap {

	@OriginalMember(owner = "client!mha", name = "n", descriptor = "[[Lclient!sm;")
	private var shadowTiles: Array<Array<ShadowTileGL?>?>? = null

	@OriginalMember(owner = "client!mha", name = "g", descriptor = "Lclient!qo;")
	private val glToolkit: GlToolkit?

	@OriginalMember(owner = "client!mha", name = "m", descriptor = "Lclient!ed;")
	private val glGround: GlGround?

	@OriginalMember(owner = "client!mha", name = "o", descriptor = "I")
	@JvmField
	val width: Int

	@OriginalMember(owner = "client!mha", name = "c", descriptor = "I")
	private val length: Int

	@OriginalMember(owner = "client!mha", name = "d", descriptor = "I")
	private val sizeShift: Int

	@OriginalMember(owner = "client!mha", name = "h", descriptor = "[B")
	@JvmField
	val shadowMapperData: ByteArray

	@OriginalMember(owner = "client!mha", name = "i", descriptor = "I")
	private val sizeX: Int

	@OriginalMember(owner = "client!mha", name = "p", descriptor = "I")
	private val sizeY: Int

	@OriginalMember(owner = "client!mha", name = "<init>", descriptor = "(Lclient!qo;Lclient!ed;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) glGround: GlGround?) {
		this.glToolkit = glToolkit
		this.glGround = glGround
		this.width = ((this.glGround!!.tileUnits * this.glGround.width) shr this.glToolkit!!.shadowScaleShift) + 2
		this.length = ((this.glGround.tileUnits * this.glGround.length) shr this.glToolkit.shadowScaleShift) + 2
		this.sizeShift = this.glToolkit.shadowScaleShift + 7 - this.glGround.tileScale
		this.shadowMapperData = ByteArray(this.width * this.length)
		this.sizeX = this.glGround.width shr this.sizeShift
		this.sizeY = this.glGround.length shr this.sizeShift
	}

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(Lclient!r;III)V")
	fun removeShadow(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int) {
		var x: Int = x
		var y: Int = y
		@Pc(6) val glShadow: GlShadow? = shadow as GlShadow?
		x += glShadow!!.startX + 1
		y += glShadow.startZ + 1
		@Pc(27) var dataOffset: Int = this.width * y + x
		@Pc(29) var srcOffset: Int = 0
		@Pc(32) var clippedHeight: Int = glShadow.sizeZ
		@Pc(35) var clippedWidth: Int = glShadow.sizeX
		@Pc(40) var dstRowStride: Int = this.width - clippedWidth
		@Pc(42) var srcRowStride: Int = 0
		@Pc(49) var clippedAmount: Int
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
			GlShadowMap.subtractShadowData(srcOffset, this.shadowMapperData, clippedHeight, dataOffset, dstRowStride, srcRowStride, glShadow.shade, clippedWidth)
			this.invalidateShadowTiles(clippedHeight, clippedWidth, x, y)
		}
	}

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(IIIII)V")
	private fun invalidateShadowTiles(@OriginalArg(0) regionHeight: Int, @OriginalArg(1) regionWidth: Int, @OriginalArg(2) startX: Int, @OriginalArg(4) startY: Int) {
		if (this.shadowTiles == null) {
			return
		}
		@Pc(15) val minChunkX: Int = (startX - 1) shr 7
		@Pc(26) val maxChunkX: Int = (startX + regionWidth - 1 - 1) shr 7
		@Pc(32) val minChunkY: Int = (startY - 1) shr 7
		@Pc(42) val maxChunkY: Int = (regionHeight + startY - 1 - 1) shr 7
		for (@Pc(44) i in minChunkX..maxChunkX) {
			@Pc(51) val glShadowMapBlocks: Array<ShadowTileGL?>? = this.shadowTiles!![i]
			for (@Pc(53) j in minChunkY..maxChunkY) {
				glShadowMapBlocks!![j]!!.needsTextureRegeneration = true
			}
		}
	}

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(IBILclient!r;)Z")
	fun shouldApplyShadow(@OriginalArg(0) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) shadow: Shadow?): Boolean {
		var x: Int = x
		var y: Int = y
		@Pc(6) val glShadow: GlShadow? = shadow as GlShadow?
		y += glShadow!!.startZ + 1
		x += glShadow.startX + 1
		@Pc(33) var dataOffset: Int = y * this.width + x
		@Pc(36) var clippedHeight: Int = glShadow.sizeZ
		@Pc(39) var clippedWidth: Int = glShadow.sizeX
		@Pc(45) var rowStride: Int = this.width - clippedWidth
		@Pc(52) var clippedAmount: Int
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
			return GlShadowMap.containsUnshadowedBlock(rowStride, clippedHeight, clippedWidth, dataOffset, this.shadowMapperData)
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(ZI[[ZIII)V")
	fun renderShadowOverlay(@OriginalArg(0) belowLowestPlane: Boolean, @OriginalArg(2) tilesOnScreen: Array<BooleanArray?>?, @OriginalArg(3) visibleRange: Int, @OriginalArg(4) cameraGridY: Int, @OriginalArg(5) cameraGridX: Int) {
		this.glToolkit!!.setLightingEnabled(false)
		this.glToolkit.setDepthMask(false)
		this.glToolkit.renderTexture(-2)
		this.glToolkit.setColourOp(1)
		this.glToolkit.setBlendMode(1)
		@Pc(47) val texCoordScale: Float = 1.0f / (this.glToolkit.shadowScale * 128).toFloat()
		@Pc(51) var chunkY: Int
		@Pc(58) var chunkStartY: Int
		@Pc(65) var chunkEndY: Int
		@Pc(67) var chunkX: Int
		@Pc(71) var local71: Int
		@Pc(76) var local76: Int
		@Pc(83) var local83: Int
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
							for (@Pc(331) i in chunkStartY until chunkEndY) {
								if (-visibleRange <= i - cameraGridY && i - cameraGridY <= visibleRange && tilesOnScreen!![visibleRange + tileX - cameraGridX]!![visibleRange + i - cameraGridY]) {
									OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
									OpenGL.glLoadIdentity()
									OpenGL.glScalef(texCoordScale, texCoordScale, 1.0f)
									OpenGL.glTranslatef((-chunkX).toFloat() / texCoordScale, (-chunkY).toFloat() / texCoordScale, 1.0f)
									OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
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
					@Pc(87) val gpuPacket: GlBitBuffer? = this.glToolkit.textureBuffer
					gpuPacket!!.currentPosition = 0
					for (@Pc(92) j in chunkStartY until chunkEndY) {
						if (j - cameraGridY >= -visibleRange && j - cameraGridY <= visibleRange) {
							@Pc(119) var tileIndex: Int = chunkStartX + j * this.glGround!!.width
							for (@Pc(121) k in chunkStartX until chunkEndX) {
								if (k - cameraGridX >= -visibleRange && k - cameraGridX <= visibleRange && tilesOnScreen!![visibleRange + k - cameraGridX]!![visibleRange + j - cameraGridY]) {
									@Pc(161) val tileVertexIndices: ShortArray? = this.glGround.tileVertexIndices[tileIndex]
									if (tileVertexIndices != null) {
										@Pc(169) var index: Int
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
						OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
						OpenGL.glLoadIdentity()
						OpenGL.glScalef(texCoordScale, texCoordScale, 1.0f)
						OpenGL.glTranslatef((-chunkX).toFloat() / texCoordScale, (-chunkY).toFloat() / texCoordScale, 1.0f)
						OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
						this.shadowTiles!![chunkX]!![chunkY]!!.drawShadowWithIndices(gpuPacket.buffer, indexCount)
					}
					chunkX++
				}
				chunkY++
			}
		}
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		OpenGL.glLoadIdentity()
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(I)V")
	fun initializeShadowTiles() {
		this.shadowTiles = Array<Array<ShadowTileGL?>?>(this.sizeX) { arrayOfNulls<ShadowTileGL>(this.sizeY) }
		for (@Pc(14) chunkY in 0 until this.sizeY) {
			for (@Pc(18) chunkX in 0 until this.sizeX) {
				this.shadowTiles!![chunkX]!![chunkY] = ShadowTileGL(this.glToolkit, this, this.glGround, chunkX, chunkY, this.sizeShift, chunkX * 128 + 1, chunkY * 128 + 1)
			}
		}
	}

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(IIBLclient!r;)V")
	fun addShadow(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(3) shadow: Shadow?) {
		var x: Int = x
		var y: Int = y
		@Pc(6) val glShadow: GlShadow? = shadow as GlShadow?
		y += glShadow!!.startZ + 1
		x += glShadow.startX + 1
		@Pc(27) var dataOffset: Int = this.width * y + x
		@Pc(29) var srcOffset: Int = 0
		@Pc(32) var clippedHeight: Int = glShadow.sizeZ
		@Pc(35) var clippedWidth: Int = glShadow.sizeX
		@Pc(46) var dstRowStride: Int = this.width - clippedWidth
		@Pc(53) var clippedAmount: Int
		if (y <= 0) {
			clippedAmount = 1 - y
			srcOffset = clippedWidth * clippedAmount
			clippedHeight -= clippedAmount
			y = 1
			dataOffset += clippedAmount * this.width
		}
		@Pc(74) var srcRowStride: Int = 0
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
			GlShadowMap.addShadowData(srcRowStride, srcOffset, glShadow.shade, clippedWidth, this.shadowMapperData, clippedHeight, dataOffset, dstRowStride)
			this.invalidateShadowTiles(clippedHeight, clippedWidth, x, y)
		}
	}

	companion object {
		@OriginalMember(owner = "client!tha", name = "a", descriptor = "(I[BIIII[BIB)V")
		@JvmStatic
		fun subtractShadowData(@OriginalArg(0) srcOffset: Int, @OriginalArg(1) dst: ByteArray?, @OriginalArg(2) height: Int, @OriginalArg(3) dstOffset: Int, @OriginalArg(4) dstRowStride: Int, @OriginalArg(5) srcRowStride: Int, @OriginalArg(6) src: ByteArray?, @OriginalArg(7) width: Int) {
			var srcOffset: Int = srcOffset
			var dstOffset: Int = dstOffset
			@Pc(10) val blockCount: Int = -(width shr 2)
			@Pc(15) val remainderCount: Int = -(width and 0x3)
			for (@Pc(23) i in -height until 0) {
				@Pc(31) var dstIndex: Int
				for (@Pc(27) j in blockCount until 0) {
					dstIndex = dstOffset++
					dst!![dstIndex] = (dst[dstIndex] - src!![srcOffset++]).toByte()
					@Pc(44) val dstIndex2: Int = dstOffset++
					dst[dstIndex2] = (dst[dstIndex2] - src[srcOffset++]).toByte()
					@Pc(57) val dstIndex3: Int = dstOffset++
					dst[dstIndex3] = (dst[dstIndex3] - src[srcOffset++]).toByte()
					@Pc(70) val dstIndex4: Int = dstOffset++
					dst[dstIndex4] = (dst[dstIndex4] - src[srcOffset++]).toByte()
				}
				for (@Pc(86) k in remainderCount until 0) {
					dstIndex = dstOffset++
					dst!![dstIndex] = (dst[dstIndex] - src!![srcOffset++]).toByte()
				}
				dstOffset += dstRowStride
				srcOffset += srcRowStride
			}
		}

		@OriginalMember(owner = "client!hk", name = "a", descriptor = "(III[BI[BIII)V")
		@JvmStatic
		fun addShadowData(@OriginalArg(0) srcRowStride: Int, @OriginalArg(1) srcOffset: Int, @OriginalArg(3) src: ByteArray?, @OriginalArg(4) width: Int, @OriginalArg(5) dst: ByteArray?, @OriginalArg(6) height: Int, @OriginalArg(7) dstOffset: Int, @OriginalArg(8) dstRowStride: Int) {
			var srcOffset: Int = srcOffset
			var dstOffset: Int = dstOffset
			@Pc(10) val blockCount: Int = -(width shr 2)
			@Pc(15) val remainder: Int = -(width and 0x3)
			for (@Pc(23) i in -height until 0) {
				@Pc(31) var dstIndex: Int
				for (@Pc(27) j in blockCount until 0) {
					dstIndex = dstOffset++
					dst!![dstIndex] = (dst[dstIndex] + src!![srcOffset++]).toByte()
					@Pc(43) val dstIndex2: Int = dstOffset++
					dst[dstIndex2] = (dst[dstIndex2] + src[srcOffset++]).toByte()
					@Pc(55) val dstIndex3: Int = dstOffset++
					dst[dstIndex3] = (dst[dstIndex3] + src[srcOffset++]).toByte()
					@Pc(67) val dstIndex4: Int = dstOffset++
					dst[dstIndex4] = (dst[dstIndex4] + src[srcOffset++]).toByte()
				}
				for (@Pc(82) k in remainder until 0) {
					dstIndex = dstOffset++
					dst!![dstIndex] = (dst[dstIndex] + src!![srcOffset++]).toByte()
				}
				dstOffset += dstRowStride
				srcOffset += srcRowStride
			}
		}

		@OriginalMember(owner = "client!rga", name = "a", descriptor = "(IIBIII[B)Z")
		@JvmStatic
		fun containsUnshadowedBlock(@OriginalArg(0) rowStride: Int, @OriginalArg(1) height: Int, @OriginalArg(4) width: Int, @OriginalArg(5) offset: Int, @OriginalArg(6) data: ByteArray?): Boolean {
			var offset: Int = offset
			@Pc(9) val remainder: Int = width % 8
			@Pc(26) val padding: Int
			if (remainder == 0) {
				padding = 0
			} else {
				padding = 8 - remainder
			}
			@Pc(40) val rowBlocks: Int = -((height + 8 - 1) / 8)
			@Pc(49) val columnBlocks: Int = -((width + 8 - 1) / 8)
			for (@Pc(51) i in rowBlocks until 0) {
				for (@Pc(55) j in columnBlocks until 0) {
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
