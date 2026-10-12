package com.jagex.game.runetek5.graphics

import jaclib.memory.Buffer
import jaclib.memory.Stream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.scene.ground.GpuFloorModel
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.toolkit.opengl.GLActionFormat
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import com.jagex.game.runetek5.PrimitiveType
import kotlin.jvm.JvmField

@OriginalClass("client!mc")
class FloorHardShadowsBlock {

	@OriginalMember(owner = "client!mc", name = "o", descriptor = "I")
	private var lastModifiedPixelCount: Int = -1

	@OriginalMember(owner = "client!mc", name = "s", descriptor = "Z")
	@JvmField
	var needsTextureRegeneration: Boolean = true

	@OriginalMember(owner = "client!mc", name = "r", descriptor = "Lclient!wga;")
	private val nativeToolkit: AbstractNativeToolkit?

	@OriginalMember(owner = "client!mc", name = "a", descriptor = "I")
	private val offsetX: Int

	@OriginalMember(owner = "client!mc", name = "p", descriptor = "I")
	private val offsetZ: Int

	@OriginalMember(owner = "client!mc", name = "q", descriptor = "Lclient!dg;")
	private val floorHardShadows: FloorHardShadows?

	@OriginalMember(owner = "client!mc", name = "t", descriptor = "I")
	@JvmField
	val triangleCount: Int

	@OriginalMember(owner = "client!mc", name = "m", descriptor = "Lclient!wfa;")
	private var cachedTexture: GpuTexture? = null

	@OriginalMember(owner = "client!mc", name = "c", descriptor = "I")
	private var maxIndex: Int = 0

	@OriginalMember(owner = "client!mc", name = "d", descriptor = "I")
	private var minIndex: Int = 0

	@OriginalMember(owner = "client!mc", name = "h", descriptor = "Lclient!uq;")
	private var indexBuffer: GpuIndexBuffer? = null

	@OriginalMember(owner = "client!mc", name = "<init>", descriptor = "(Lclient!wga;Lclient!dg;Lclient!qm;IIIII)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(1) floorHardShadows: FloorHardShadows?, @OriginalArg(2) gpuFloorModel: GpuFloorModel?, @OriginalArg(3) chunkX: Int, @OriginalArg(4) chunkZ: Int, @OriginalArg(5) sizeShift: Int, @OriginalArg(6) offsetX: Int, @OriginalArg(7) offsetZ: Int) {
		this.nativeToolkit = nativeToolkit
		this.offsetX = offsetX
		this.offsetZ = offsetZ
		this.floorHardShadows = floorHardShadows
		@Pc(25) val chunkSize: Int = 0x1 shl sizeShift
		@Pc(27) var indexCount: Int = 0
		@Pc(31) val chunkStartX: Int = chunkX shl sizeShift
		@Pc(35) val chunkStartZ: Int = chunkZ shl sizeShift
		@Pc(48) var local48: Int
		@Pc(37) var i: Int = 0
		while (i < chunkSize) {
			var tileOffset: Int = chunkStartX + (i + chunkStartZ) * gpuFloorModel!!.width
			@Pc(50) var j: Int = 0
			while (j < chunkSize) {
				@Pc(58) val tileIndices: ShortArray? = gpuFloorModel.tileVertexIndices[tileOffset++]
				if (tileIndices != null) {
					indexCount += tileIndices.size
				}
				j++
			}
			i++
		}
		if (indexCount <= 0) {
			this.triangleCount = 0
			this.cachedTexture = null
		} else {
			this.maxIndex = Int.MIN_VALUE
			this.minIndex = Int.MAX_VALUE
			this.indexBuffer = this.nativeToolkit!!.createIndexBuffer(false)
			this.indexBuffer!!.allocate(indexCount)
			var attempt: Int = 0
			while (attempt < 4) {
				@Pc(110) val buffer: Buffer? = this.indexBuffer!!.lock()
				if (buffer != null) {
					@Pc(118) val stream: Stream = this.nativeToolkit.wrapBuffer(buffer)!!
					@Pc(122) var dz: Int
					@Pc(134) var tileOffset: Int
					@Pc(136) var dx: Int
					@Pc(144) var tileIndices: ShortArray?
					@Pc(148) var index: Int
					@Pc(156) var vertexIndex: Int
					if (Stream.c()) {
						dz = 0
						while (dz < chunkSize) {
							tileOffset = gpuFloorModel!!.width * (dz + chunkStartZ) + chunkStartX
							dx = 0
							while (dx < chunkSize) {
								tileIndices = gpuFloorModel.tileVertexIndices[tileOffset++]
								if (tileIndices != null) {
									index = 0
									while (index < tileIndices.size) {
										vertexIndex = tileIndices[index].toInt() and 0xFFFF
										if (this.maxIndex < vertexIndex) {
											this.maxIndex = vertexIndex
										}
										if (vertexIndex < this.minIndex) {
											this.minIndex = vertexIndex
										}
										stream.d(vertexIndex)
										index++
									}
								}
								dx++
							}
							dz++
						}
					} else {
						dz = 0
						while (dz < chunkSize) {
							tileOffset = gpuFloorModel!!.width * (chunkStartZ + dz) + chunkStartX
							dx = 0
							while (dx < chunkSize) {
								tileIndices = gpuFloorModel.tileVertexIndices[tileOffset++]
								if (tileIndices != null) {
									index = 0
									while (index < tileIndices.size) {
										vertexIndex = tileIndices[index].toInt() and 0xFFFF
										if (vertexIndex < this.minIndex) {
											this.minIndex = vertexIndex
										}
										if (vertexIndex > this.maxIndex) {
											this.maxIndex = vertexIndex
										}
										stream.a(vertexIndex)
										index++
									}
								}
								dx++
							}
							dz++
						}
					}
					stream.a()
					if (this.indexBuffer!!.unlock()) {
						break
					}
				}
				attempt++
			}
			this.triangleCount = indexCount / 3
		}
	}

	@OriginalMember(owner = "client!mc", name = "a", descriptor = "(B)V")
	fun drawShadow() {
		this.drawShadowElements(this.indexBuffer, this.triangleCount)
	}

	@OriginalMember(owner = "client!mc", name = "a", descriptor = "(I)V")
	private fun updateTexture() {
		if (!this.needsTextureRegeneration) {
			return
		}
		this.needsTextureRegeneration = false
		@Pc(16) val shadowData: ByteArray = this.floorHardShadows!!.shadowMapperData!!
		@Pc(18) var modifiedPixelCount: Int = 0
		@Pc(22) val shadowWidth: Int = this.floorHardShadows.width
		@Pc(32) var shadowOffset: Int = this.floorHardShadows.width * this.offsetZ + this.offsetX
		@Pc(44) var local44: Int
		@Pc(34) var i: Int = -128
		while (i < 0) {
			modifiedPixelCount = (modifiedPixelCount shl 8) - modifiedPixelCount
			var column: Int = -128
			while (column < 0) {
				if (shadowData[shadowOffset++].toInt() != 0) {
					modifiedPixelCount++
				}
				column++
			}
			shadowOffset += shadowWidth - 128
			i++
		}
		if (this.cachedTexture != null && this.lastModifiedPixelCount == modifiedPixelCount) {
			this.needsTextureRegeneration = false
			return
		}
		this.lastModifiedPixelCount = modifiedPixelCount
		var pixelIndex: Int = 0
		shadowOffset = this.offsetX + this.offsetZ * shadowWidth
		@Pc(120) var y: Int
		@Pc(124) var x: Int
		@Pc(135) var neighborCount: Int
		if (!this.nativeToolkit!!.supportsTextureFormat(GLFormat.RGBA, GLActionFormat.PIXEL_FORMAT)) {
			if (FloorHardShadowsBlock.shadowPixels == null) {
				FloorHardShadowsBlock.shadowPixels = IntArray(16384)
			}
			@Pc(254) val pixels: IntArray = FloorHardShadowsBlock.shadowPixels!!
			y = -128
			while (y < 0) {
				x = -128
				while (x < 0) {
					if (shadowData[shadowOffset].toInt() == 0) {
						neighborCount = 0
						if (shadowData[shadowOffset - 1].toInt() != 0) {
							neighborCount++
						}
						if (shadowData[shadowOffset + 1].toInt() != 0) {
							neighborCount++
						}
						if (shadowData[shadowOffset - shadowWidth].toInt() != 0) {
							neighborCount++
						}
						if (shadowData[shadowWidth + shadowOffset].toInt() != 0) {
							neighborCount++
						}
						pixels[pixelIndex++] = (neighborCount * 17) shl 24
					} else {
						pixels[pixelIndex++] = 1140850688
					}
					shadowOffset++
					x++
				}
				shadowOffset += this.floorHardShadows.width - 128
				y++
			}
			if (this.cachedTexture == null) {
				this.cachedTexture = this.nativeToolkit.createTexture(128, 128, false, FloorHardShadowsBlock.shadowPixels)
				this.cachedTexture!!.setWarp(false, false)
			} else {
				this.cachedTexture!!.updateTextureRegion(128, FloorHardShadowsBlock.shadowPixels, 0, 128, 0, 128)
			}
			return
		}
		if (FloorHardShadowsBlock.shadowTexels == null) {
			FloorHardShadowsBlock.shadowTexels = ByteArray(16384)
		}
		@Pc(118) val texels: ByteArray = FloorHardShadowsBlock.shadowTexels!!
		y = -128
		while (y < 0) {
			x = -128
			while (x < 0) {
				if (shadowData[shadowOffset].toInt() == 0) {
					neighborCount = 0
					if (shadowData[shadowOffset - 1].toInt() != 0) {
						neighborCount++
					}
					if (shadowData[shadowOffset + 1].toInt() != 0) {
						neighborCount++
					}
					if (shadowData[shadowOffset - shadowWidth].toInt() != 0) {
						neighborCount++
					}
					if (shadowData[shadowWidth + shadowOffset].toInt() != 0) {
						neighborCount++
					}
					texels[pixelIndex++] = (neighborCount * 17).toByte()
				} else {
					texels[pixelIndex++] = 68
				}
				shadowOffset++
				x++
			}
			shadowOffset += this.floorHardShadows.width - 128
			y++
		}
		if (this.cachedTexture == null) {
			this.cachedTexture = this.nativeToolkit.createTextureSampler(FloorHardShadowsBlock.shadowTexels, 128, GLActionFormat.PIXEL_FORMAT, false, 128)
			this.cachedTexture!!.setWarp(false, false)
		} else {
			this.cachedTexture!!.upload(128, 128, 128, FloorHardShadowsBlock.shadowTexels, GLActionFormat.PIXEL_FORMAT)
		}
	}

	@OriginalMember(owner = "client!mc", name = "a", descriptor = "(ILclient!uq;I)V")
	fun drawShadowElements(@OriginalArg(1) gpuIndexBuffer: GpuIndexBuffer?, @OriginalArg(2) triangleCount: Int) {
		if (triangleCount > 0) {
			this.updateTexture()
			this.nativeToolkit!!.setTextureSampler(this.cachedTexture)
			this.nativeToolkit.drawIndexedPrimitive(PrimitiveType.aClass134_6, triangleCount, gpuIndexBuffer, this.minIndex, 0, this.maxIndex + 1 - this.minIndex)
		}
	}

	companion object {
		@OriginalMember(owner = "client!ig", name = "v", descriptor = "[I")
		@JvmField
		var shadowPixels: IntArray? = null
		@OriginalMember(owner = "client!or", name = "m", descriptor = "[B")
		@JvmField
		var shadowTexels: ByteArray? = null
	}
}
