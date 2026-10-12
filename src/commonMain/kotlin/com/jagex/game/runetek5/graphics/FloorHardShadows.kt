package com.jagex.game.runetek5.graphics

import com.jagex.game.runetek5.cache.node.lru.GpuShadow
import com.jagex.game.runetek5.NativeMatrix
import jaclib.memory.Buffer
import jaclib.memory.Stream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.scene.ground.GpuFloorModel
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.TextureTransformMode
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!dg")
class FloorHardShadows {

	@OriginalMember(owner = "client!dg", name = "m", descriptor = "[[Lclient!mc;")
	private var shadowTiles: Array<Array<FloorHardShadowsBlock?>?>? = null

	@OriginalMember(owner = "client!dg", name = "b", descriptor = "Lclient!wga;")
	private val nativeToolkit: AbstractNativeToolkit?

	@OriginalMember(owner = "client!dg", name = "g", descriptor = "Lclient!qm;")
	private val gpuFloorModel: GpuFloorModel?

	@OriginalMember(owner = "client!dg", name = "d", descriptor = "I")
	@JvmField
	val width: Int

	@OriginalMember(owner = "client!dg", name = "n", descriptor = "I")
	private val length: Int

	@OriginalMember(owner = "client!dg", name = "c", descriptor = "[B")
	@JvmField
	val shadowMapperData: ByteArray?

	@OriginalMember(owner = "client!dg", name = "o", descriptor = "I")
	private val sizeShift: Int

	@OriginalMember(owner = "client!dg", name = "l", descriptor = "I")
	private val sizeX: Int

	@OriginalMember(owner = "client!dg", name = "i", descriptor = "I")
	private val sizeY: Int

	@OriginalMember(owner = "client!dg", name = "<init>", descriptor = "(Lclient!wga;Lclient!qm;)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(1) gpuFloorModel: GpuFloorModel?) {
		this.nativeToolkit = nativeToolkit
		this.gpuFloorModel = gpuFloorModel
		this.width = ((this.gpuFloorModel!!.width * this.gpuFloorModel.tileUnits) shr this.nativeToolkit!!.shadowSizeShift) + 2
		this.length = ((this.gpuFloorModel.tileUnits * this.gpuFloorModel.length) shr this.nativeToolkit.shadowSizeShift) + 2
		this.shadowMapperData = ByteArray(this.width * this.length)
		this.sizeShift = this.nativeToolkit.shadowSizeShift + 7 - this.gpuFloorModel.tileScale
		this.sizeX = this.gpuFloorModel.width shr this.sizeShift
		this.sizeY = this.gpuFloorModel.length shr this.sizeShift
	}

	@OriginalMember(owner = "client!dg", name = "a", descriptor = "(IBLclient!r;I)Z")
	fun applyShadow(@OriginalArg(0) x: Int, @OriginalArg(2) shadow: Shadow?, @OriginalArg(3) z: Int): Boolean {
		var x: Int = x
		var z: Int = z
		@Pc(6) val nativeShadow: GpuShadow = shadow as GpuShadow
		z += nativeShadow.startZ + 1
		x += nativeShadow.startX + 1
		@Pc(35) var dataOffset: Int = z * this.width + x
		@Pc(38) var clippedHeight: Int = nativeShadow.sizeZ
		@Pc(41) var clippedWidth: Int = nativeShadow.sizeX
		@Pc(47) var rowStride: Int = this.width - clippedWidth
		@Pc(57) var clippedAmount: Int
		if (z <= 0) {
			clippedAmount = 1 - z
			clippedHeight -= clippedAmount
			dataOffset += this.width * clippedAmount
			z = 1
		}
		if (clippedHeight + z >= this.length) {
			clippedAmount = clippedHeight + z + 1 - this.length
			clippedHeight -= clippedAmount
		}
		if (x <= 0) {
			clippedAmount = 1 - x
			x = 1
			rowStride += clippedAmount
			clippedWidth -= clippedAmount
			dataOffset += clippedAmount
		}
		if (clippedWidth + x >= this.width) {
			clippedAmount = x + clippedWidth + 1 - this.width
			clippedWidth -= clippedAmount
			rowStride += clippedAmount
		}
		if (clippedWidth > 0 && clippedHeight > 0) {
			rowStride += this.width * 7
			return FloorHardShadows.hasUnshadedSample(rowStride, dataOffset, this.shadowMapperData, clippedWidth, clippedHeight)
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!dg", name = "a", descriptor = "(ILclient!r;II)V")
	fun removeShadow(@OriginalArg(0) z: Int, @OriginalArg(1) shadow: Shadow?, @OriginalArg(3) x: Int) {
		var z: Int = z
		var x: Int = x
		@Pc(6) val nativeShadow: GpuShadow = shadow as GpuShadow
		x += nativeShadow.startX + 1
		z += nativeShadow.startZ + 1
		@Pc(27) var dataOffset: Int = z * this.width + x
		@Pc(29) var srcOffset: Int = 0
		@Pc(32) var clippedHeight: Int = nativeShadow.sizeZ
		@Pc(35) var clippedWidth: Int = nativeShadow.sizeX
		@Pc(41) var dstRowStride: Int = this.width - clippedWidth
		@Pc(48) var clippedAmount: Int
		if (z <= 0) {
			clippedAmount = 1 - z
			dataOffset += this.width * clippedAmount
			clippedHeight -= clippedAmount
			z = 1
			srcOffset = clippedAmount * clippedWidth
		}
		@Pc(69) var srcRowStride: Int = 0
		if (clippedHeight + z >= this.length) {
			clippedAmount = z + clippedHeight + 1 - this.length
			clippedHeight -= clippedAmount
		}
		if (x <= 0) {
			clippedAmount = 1 - x
			srcRowStride = clippedAmount
			dstRowStride += clippedAmount
			x = 1
			dataOffset += clippedAmount
			srcOffset += clippedAmount
			clippedWidth -= clippedAmount
		}
		if (this.width <= x + clippedWidth) {
			clippedAmount = clippedWidth + x + 1 - this.width
			dstRowStride += clippedAmount
			clippedWidth -= clippedAmount
			srcRowStride += clippedAmount
		}
		if (clippedWidth > 0 && clippedHeight > 0) {
			FloorHardShadows.subtractShadowData(dstRowStride, clippedWidth, srcRowStride, clippedHeight, dataOffset, this.shadowMapperData, nativeShadow.shade, srcOffset)
			this.invalidateShadowTiles(z, x, clippedHeight, clippedWidth)
		}
	}

	@OriginalMember(owner = "client!dg", name = "a", descriptor = "(IIIZ[[ZI)V")
	fun renderShadowOverlay(@OriginalArg(1) radius: Int, @OriginalArg(2) x: Int, @OriginalArg(3) belowLowestPlane: Boolean, @OriginalArg(4) visibility: Array<BooleanArray?>?, @OriginalArg(5) z: Int) {
		this.nativeToolkit!!.toggleLights(false)
		this.nativeToolkit.setDepthMaskEnabled(false)
		this.nativeToolkit.toggleBlending(1)
		this.nativeToolkit.setColourType(1)
		this.nativeToolkit.setActiveMaterial(false, false, -2)
		@Pc(41) val texCoordScale: Float = 1.0f / (this.nativeToolkit.activeFlags * 128).toFloat()
		@Pc(45) var chunkZ: Int
		@Pc(52) var chunkStartZ: Int
		@Pc(59) var chunkEndZ: Int
		@Pc(61) var chunkX: Int
		if (belowLowestPlane) {
			chunkZ = 0
			while (chunkZ < this.sizeY) {
				chunkStartZ = chunkZ shl this.sizeShift
				chunkEndZ = (chunkZ + 1) shl this.sizeShift
				chunkX = 0
				label138@ while (chunkX < this.sizeX) {
					if (this.shadowTiles!![chunkX]!![chunkZ] != null) {
						@Pc(75) val chunkStartX: Int = chunkX shl this.sizeShift
						@Pc(82) val chunkEndX: Int = (chunkX + 1) shl this.sizeShift
						@Pc(84) var i: Int = chunkStartX
						while (i < chunkEndX) {
							if (-radius <= i - x && radius >= i - x) {
								@Pc(105) var j: Int = chunkStartZ
								while (j < chunkEndZ) {
									if (-radius <= j - z && radius >= j - z && visibility!![radius + i - x]!![j + radius - z]) {
										@Pc(141) val textureMatrix: NativeMatrix = this.nativeToolkit.getCurrentTextureMatrix()!!
										textureMatrix.setScale(1.0f, texCoordScale, texCoordScale)
										textureMatrix.translate(-chunkX, -chunkZ, 0)
										this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
										this.shadowTiles!![chunkX]!![chunkZ]!!.drawShadow()
										chunkX++
										continue@label138
									}
									j++
								}
							}
							i++
						}
					}
					chunkX++
				}
				chunkZ++
			}
		} else {
			chunkZ = 0
			while (chunkZ < this.sizeY) {
				chunkStartZ = chunkZ shl this.sizeShift
				chunkEndZ = (chunkZ + 1) shl this.sizeShift
				chunkX = 0
				while (chunkX < this.sizeX) {
					@Pc(226) val floorHardShadowsBlock: FloorHardShadowsBlock? = this.shadowTiles!![chunkX]!![chunkZ]
					if (floorHardShadowsBlock != null) {
						@Pc(237) val gpuIndexBuffer: GpuIndexBuffer = this.nativeToolkit.getOrResizeIndexBuffer(floorHardShadowsBlock.triangleCount * 3)!!
						@Pc(242) val buffer: Buffer? = gpuIndexBuffer.lock()
						if (buffer != null) {
							@Pc(250) val stream: Stream = this.nativeToolkit.wrapBuffer(buffer)!!
							@Pc(252) var indexCount: Int = 0
							@Pc(257) val chunkStartX: Int = chunkX shl this.sizeShift
							@Pc(264) val chunkEndX: Int = (chunkX + 1) shl this.sizeShift
							@Pc(266) var k: Int = chunkStartZ
							while (k < chunkEndZ) {
								if (k - z >= -radius && radius >= k - z) {
									@Pc(293) var tileIndex: Int = k * this.gpuFloorModel!!.width + chunkStartX
									@Pc(295) var m: Int = chunkStartX
									while (m < chunkEndX) {
										if (-radius <= m - x && radius >= m - x && visibility!![radius + m - x]!![radius + k - z]) {
											@Pc(335) val tileIndices: ShortArray? = this.gpuFloorModel.tileVertexIndices[tileIndex]
											if (tileIndices != null) {
												@Pc(341) var index: Int
												if (Stream.c()) {
													index = 0
													while (index < tileIndices.size) {
														stream.d(tileIndices[index].toInt() and 0xFFFF)
														indexCount++
														index++
													}
												} else {
													index = 0
													while (index < tileIndices.size) {
														indexCount++
														stream.a(tileIndices[index].toInt() and 0xFFFF)
														index++
													}
												}
											}
										}
										tileIndex++
										m++
									}
								}
								k++
							}
							stream.a()
							if (gpuIndexBuffer.unlock() && indexCount > 0) {
								@Pc(405) val textureMatrix: NativeMatrix = this.nativeToolkit.getCurrentTextureMatrix()!!
								textureMatrix.setScale(1.0f, texCoordScale, texCoordScale)
								textureMatrix.translate(-chunkX, -chunkZ, 0)
								this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
								floorHardShadowsBlock.drawShadowElements(gpuIndexBuffer, indexCount / 3)
							}
						}
					}
					chunkX++
				}
				chunkZ++
			}
		}
		this.nativeToolkit.resetCurrentTextureMatrix()
	}

	@OriginalMember(owner = "client!dg", name = "b", descriptor = "(I)V")
	fun initializeShadowTiles() {
		this.shadowTiles = Array<Array<FloorHardShadowsBlock?>?>(this.sizeX) { arrayOfNulls<FloorHardShadowsBlock>(this.sizeY) }
		@Pc(25) var i: Int = 0
		while (i < this.sizeY) {
			@Pc(29) var j: Int = 0
			while (j < this.sizeX) {
				this.shadowTiles!![j]!![i] = FloorHardShadowsBlock(this.nativeToolkit, this, this.gpuFloorModel, j, i, this.sizeShift, j * 128 + 1, i * 128 + 1)
				if (this.shadowTiles!![j]!![i]!!.triangleCount == 0) {
					this.shadowTiles!![j]!![i] = null
				}
				j++
			}
			i++
		}
	}

	@OriginalMember(owner = "client!dg", name = "a", descriptor = "(IILclient!r;I)V")
	fun addShadow(@OriginalArg(1) x: Int, @OriginalArg(2) shadow: Shadow?, @OriginalArg(3) z: Int) {
		var x: Int = x
		var z: Int = z
		@Pc(6) val nativeShadow: GpuShadow = shadow as GpuShadow
		z += nativeShadow.startZ + 1
		x += nativeShadow.startX + 1
		@Pc(37) var dataOffset: Int = x + this.width * z
		@Pc(39) var srcOffset: Int = 0
		@Pc(42) var clippedHeight: Int = nativeShadow.sizeZ
		@Pc(45) var clippedWidth: Int = nativeShadow.sizeX
		@Pc(51) var dstRowStride: Int = this.width - clippedWidth
		@Pc(58) var clippedAmount: Int
		if (z <= 0) {
			clippedAmount = 1 - z
			dataOffset += this.width * clippedAmount
			clippedHeight -= clippedAmount
			srcOffset = clippedAmount * clippedWidth
			z = 1
		}
		@Pc(79) var srcRowStride: Int = 0
		if (this.length <= clippedHeight + z) {
			clippedAmount = z + clippedHeight + 1 - this.length
			clippedHeight -= clippedAmount
		}
		if (x <= 0) {
			clippedAmount = 1 - x
			srcOffset += clippedAmount
			dstRowStride += clippedAmount
			srcRowStride = clippedAmount
			dataOffset += clippedAmount
			x = 1
			clippedWidth -= clippedAmount
		}
		if (this.width <= clippedWidth + x) {
			clippedAmount = clippedWidth + x + 1 - this.width
			srcRowStride += clippedAmount
			clippedWidth -= clippedAmount
			dstRowStride += clippedAmount
		}
		if (clippedWidth > 0 && clippedHeight > 0) {
			FloorHardShadows.addShadowData(clippedWidth, this.shadowMapperData, nativeShadow.shade, srcOffset, clippedHeight, dataOffset, dstRowStride, srcRowStride)
			this.invalidateShadowTiles(z, x, clippedHeight, clippedWidth)
		}
	}

	@OriginalMember(owner = "client!dg", name = "a", descriptor = "(IIIII)V")
	private fun invalidateShadowTiles(@OriginalArg(0) startZ: Int, @OriginalArg(2) startX: Int, @OriginalArg(3) regionLength: Int, @OriginalArg(4) regionWidth: Int) {
		if (this.shadowTiles == null) {
			return
		}
		@Pc(15) val minChunkX: Int = (startX - 1) shr 7
		@Pc(25) val maxChunkX: Int = (regionWidth + startX - 1 - 1) shr 7
		@Pc(31) val minChunkZ: Int = (startZ - 1) shr 7
		@Pc(42) val maxChunkZ: Int = (startZ + regionLength - 1 - 1) shr 7
		@Pc(44) var i: Int = minChunkX
		while (i <= maxChunkX) {
			@Pc(51) val floorHardShadowsBlocks: Array<FloorHardShadowsBlock?> = this.shadowTiles!![i]!!
			@Pc(53) var j: Int = minChunkZ
			while (j <= maxChunkZ) {
				if (floorHardShadowsBlocks[j] != null) {
					floorHardShadowsBlocks[j]!!.needsTextureRegeneration = true
				}
				j++
			}
			i++
		}
	}

	companion object {
		@OriginalMember(owner = "client!fd", name = "a", descriptor = "(IIBIII[B[BI)V")
		@JvmStatic
		fun subtractShadowData(@OriginalArg(0) dstRowStride: Int, @OriginalArg(1) width: Int, @OriginalArg(3) srcRowStride: Int, @OriginalArg(4) height: Int, @OriginalArg(5) dstOffset: Int, @OriginalArg(6) dst: ByteArray?, @OriginalArg(7) src: ByteArray?, @OriginalArg(8) srcOffset: Int) {
			var dstOffset: Int = dstOffset
			var srcOffset: Int = srcOffset
			@Pc(10) val blockCount: Int = -(width shr 2)
			@Pc(15) val remainder: Int = -(width and 0x3)
			@Pc(23) var i: Int = -height
			while (i < 0) {
				@Pc(31) var dstIndex: Int
				@Pc(27) var j: Int = blockCount
				while (j < 0) {
					dstIndex = dstOffset++
					dst!![dstIndex] = (dst[dstIndex] - src!![srcOffset++]).toByte()
					@Pc(44) val dstIndex2: Int = dstOffset++
					dst[dstIndex2] = (dst[dstIndex2] - src[srcOffset++]).toByte()
					@Pc(57) val dstIndex3: Int = dstOffset++
					dst[dstIndex3] = (dst[dstIndex3] - src[srcOffset++]).toByte()
					@Pc(70) val dstIndex4: Int = dstOffset++
					dst[dstIndex4] = (dst[dstIndex4] - src[srcOffset++]).toByte()
					j++
				}
				@Pc(86) var k: Int = remainder
				while (k < 0) {
					dstIndex = dstOffset++
					dst!![dstIndex] = (dst[dstIndex] - src!![srcOffset++]).toByte()
					k++
				}
				dstOffset += dstRowStride
				srcOffset += srcRowStride
				i++
			}
		}

		@OriginalMember(owner = "client!jda", name = "a", descriptor = "(III[BIII)Z")
		@JvmStatic
		fun hasUnshadedSample(@OriginalArg(0) rowStride: Int, @OriginalArg(1) offset: Int, @OriginalArg(3) data: ByteArray?, @OriginalArg(4) width: Int, @OriginalArg(6) height: Int): Boolean {
			var offset: Int = offset
			@Pc(9) val widthRemainder: Int = width % 8
			@Pc(15) val padding: Int
			if (widthRemainder == 0) {
				padding = 0
			} else {
				padding = 8 - widthRemainder
			}
			@Pc(28) val rowBlocks: Int = -((height + 8 - 1) / 8)
			@Pc(48) val columnBlocks: Int = -((width + 8 - 1) / 8)
			@Pc(50) var i: Int = rowBlocks
			while (i < 0) {
				@Pc(54) var j: Int = columnBlocks
				while (j < 0) {
					if (data!![offset].toInt() == 0) {
						return true
					}
					offset += 8
					j++
				}
				offset -= padding
				if (data!![offset - 1].toInt() == 0) {
					return true
				}
				offset += rowStride
				i++
			}
			return false
		}

		@OriginalMember(owner = "client!me", name = "a", descriptor = "(I[B[BIIIIII)V")
		@JvmStatic
		fun addShadowData(@OriginalArg(0) width: Int, @OriginalArg(1) dst: ByteArray?, @OriginalArg(2) src: ByteArray?, @OriginalArg(3) srcOffset: Int, @OriginalArg(5) height: Int, @OriginalArg(6) dstOffset: Int, @OriginalArg(7) dstStride: Int, @OriginalArg(8) srcStride: Int) {
			var srcOffset: Int = srcOffset
			var dstOffset: Int = dstOffset
			@Pc(10) val blockCount: Int = -(width shr 2)
			@Pc(15) val remainder: Int = -(width and 0x3)
			@Pc(31) var i: Int = -height
			while (i < 0) {
				@Pc(39) var dstIndex: Int
				@Pc(35) var j: Int = blockCount
				while (j < 0) {
					dstIndex = dstOffset++
					dst!![dstIndex] = (dst[dstIndex] + src!![srcOffset++]).toByte()
					@Pc(51) val dstIndex2: Int = dstOffset++
					dst[dstIndex2] = (dst[dstIndex2] + src[srcOffset++]).toByte()
					@Pc(63) val dstIndex3: Int = dstOffset++
					dst[dstIndex3] = (dst[dstIndex3] + src[srcOffset++]).toByte()
					@Pc(75) val dstIndex4: Int = dstOffset++
					dst[dstIndex4] = (dst[dstIndex4] + src[srcOffset++]).toByte()
					j++
				}
				@Pc(93) var k: Int = remainder
				while (k < 0) {
					dstIndex = dstOffset++
					dst!![dstIndex] = (dst[dstIndex] + src!![srcOffset++]).toByte()
					k++
				}
				dstOffset += dstStride
				srcOffset += srcStride
				i++
			}
		}
	}
}
