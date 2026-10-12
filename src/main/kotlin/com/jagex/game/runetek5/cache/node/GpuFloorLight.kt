package com.jagex.game.runetek5.cache.node

import com.jagex.game.runetek5.PrimitiveType
import com.jagex.game.runetek5.cache.node.impl.ShortNode
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.graphics.GpuIndexBuffer
import com.jagex.game.runetek5.scene.ground.GpuFloorModel
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.toolkit.companion.NativeStream
import com.jagex.game.runetek5.utils.Utils
import jaclib.memory.Buffer
import jaclib.memory.Stream
import jaclib.memory.heap.NativeHeapBuffer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!sea")
class GpuFloorLight : Node {

	@OriginalMember(owner = "client!sea", name = "B", descriptor = "I")
	private var vertexCount: Int = 0

	@OriginalMember(owner = "client!sea", name = "v", descriptor = "Lclient!wga;")
	private val nativeToolkit: AbstractNativeToolkit?

	@OriginalMember(owner = "client!sea", name = "I", descriptor = "Lclient!qm;")
	private val gpuFloorModel: GpuFloorModel

	@OriginalMember(owner = "client!sea", name = "s", descriptor = "Lclient!ab;")
	private val pointLight: PointLight

	@OriginalMember(owner = "client!sea", name = "M", descriptor = "I")
	private val minTileX: Int

	@OriginalMember(owner = "client!sea", name = "F", descriptor = "I")
	private val maxTileX: Int

	@OriginalMember(owner = "client!sea", name = "C", descriptor = "I")
	private val minTileZ: Int

	@OriginalMember(owner = "client!sea", name = "y", descriptor = "I")
	private val maxTileZ: Int

	@OriginalMember(owner = "client!sea", name = "t", descriptor = "[[F")
	private var normalsX: Array<FloatArray?>? = null

	@OriginalMember(owner = "client!sea", name = "E", descriptor = "[[F")
	private var normalsY: Array<FloatArray?>? = null

	@OriginalMember(owner = "client!sea", name = "p", descriptor = "[[F")
	private var normalsZ: Array<FloatArray?>? = null

	@OriginalMember(owner = "client!sea", name = "H", descriptor = "I")
	private var indexCount: Int = 0

	@OriginalMember(owner = "client!sea", name = "z", descriptor = "Lclient!eq;")
	private var vertexTable: IterableHashTable? = null

	@OriginalMember(owner = "client!sea", name = "q", descriptor = "Lclient!uq;")
	private val indexBuffer: GpuIndexBuffer?

	@OriginalMember(owner = "client!sea", name = "L", descriptor = "Lclient!jaclib/memory/Stream;")
	private var vertexWriter: Stream? = null

	@OriginalMember(owner = "client!sea", name = "K", descriptor = "Lclient!jaclib/memory/Stream;")
	private var indexWriter: Stream? = null

	// final in Java; a var here because Kotlin does not allow assigning a val inside the retry loop below
	@OriginalMember(owner = "client!sea", name = "J", descriptor = "Lclient!tc;")
	private var vertexStream: NativeStream? = null

	@OriginalMember(owner = "client!sea", name = "<init>", descriptor = "(Lclient!wga;Lclient!qm;Lclient!ab;[I)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(1) gpuFloorModel: GpuFloorModel?, @OriginalArg(2) pointLight: PointLight?, @OriginalArg(3) tileMask: IntArray?) {
		this.nativeToolkit = nativeToolkit
		this.gpuFloorModel = gpuFloorModel!!
		this.pointLight = pointLight!!
		@Pc(23) val extent: Int = this.pointLight.getRadius() - (gpuFloorModel.tileUnits shr 1)
		this.minTileX = (this.pointLight.getX() - extent) shr gpuFloorModel.tileScale
		this.maxTileX = (this.pointLight.getX() + extent) shr gpuFloorModel.tileScale
		this.minTileZ = (this.pointLight.getZ() - extent) shr gpuFloorModel.tileScale
		this.maxTileZ = (this.pointLight.getZ() + extent) shr gpuFloorModel.tileScale
		@Pc(77) val spanX: Int = this.maxTileX + 1 - this.minTileX
		@Pc(86) val spanZ: Int = this.maxTileZ + 1 - this.minTileZ
		this.normalsX = Array<FloatArray?>(spanX + 1) { FloatArray(spanZ + 1) }
		this.normalsY = Array<FloatArray?>(spanX + 1) { FloatArray(spanZ + 1) }
		this.normalsZ = Array<FloatArray?>(spanX + 1) { FloatArray(spanZ + 1) }
		@Pc(122) var local122: Int
		@Pc(137) var local137: Int
		@Pc(144) var tileX: Int
		@Pc(172) var local172: Int
		@Pc(189) var local189: Int
		@Pc(115) var i: Int = 0
		while (i <= spanZ) {
			val tileZ: Int = this.minTileZ + i
			if (tileZ > 0 && tileZ < this.gpuFloorModel.length - 1) {
				var localTileX: Int = 0
				while (localTileX <= spanX) {
					tileX = this.minTileX + localTileX
					if (tileX > 0 && tileX < this.gpuFloorModel.width - 1) {
						val heightDeltaX: Int = gpuFloorModel.getHeight(tileZ, tileX + 1) - gpuFloorModel.getHeight(tileZ, tileX - 1)
						val heightDeltaZ: Int = gpuFloorModel.getHeight(tileZ + 1, tileX) - gpuFloorModel.getHeight(tileZ - 1, tileX)
						@Pc(204) val invLength: Float = (1.0 / java.lang.Math.sqrt((heightDeltaZ * heightDeltaZ + heightDeltaX * heightDeltaX + 65536).toDouble())).toFloat()
						this.normalsX!![localTileX]!![i] = heightDeltaX.toFloat() * invLength
						this.normalsY!![localTileX]!![i] = -256.0f * invLength
						this.normalsZ!![localTileX]!![i] = heightDeltaZ.toFloat() * invLength
					}
					localTileX++
				}
			}
			i++
		}
		var maskIndex: Int = 0
		@Pc(299) var local299: Int
		var tileZ: Int = this.minTileZ
		while (tileZ <= this.maxTileZ) {
			if (tileZ >= 0 && gpuFloorModel.length > tileZ) {
				tileX = this.minTileX
				while (tileX <= this.maxTileX) {
					if (tileX >= 0 && gpuFloorModel.width > tileX) {
						val mask: Int = tileMask!![maskIndex]
						@Pc(290) val colours: IntArray? = gpuFloorModel.tileColours[tileX][tileZ]
						if (colours != null && mask != 0) {
							if (mask == 1) {
								var colourIndex: Int = 0
								while (colours.size > colourIndex) {
									if (colours[colourIndex++] != -1 && colours[colourIndex++] != -1 && colours[colourIndex++] != -1) {
										this.indexCount += 3
									}
								}
							} else {
								this.indexCount += 3
							}
						}
					}
					maskIndex++
					tileX++
				}
			} else {
				maskIndex += this.maxTileX - this.minTileX
			}
			tileZ++
		}
		if (this.indexCount > 0) {
			this.vertexTable = IterableHashTable(Utils.nextPowerOfTwo(this.indexCount))
			this.indexBuffer = this.nativeToolkit!!.createIndexBuffer(false)
			this.indexBuffer!!.allocate(this.indexCount)
			@Pc(403) val nativeHeapBuffer: NativeHeapBuffer? = this.nativeToolkit.generateNativeHeapBuffer(false, this.indexCount * 16)
			this.vertexWriter = Stream(nativeHeapBuffer)
			while (true) {
				@Pc(415) var buffer: Buffer?
				do {
					buffer = this.indexBuffer.lock()
				} while (buffer == null)
				this.indexWriter = Stream(buffer)
				maskIndex = 0
				var localZ: Int = 0
				var tileZ2: Int = this.minTileZ
				while (tileZ2 <= this.maxTileZ) {
					if (tileZ2 >= 0 && gpuFloorModel.length > tileZ2) {
						@Pc(444) var localX: Int = 0
						@Pc(447) var j: Int = this.minTileX
						while (j <= this.maxTileX) {
							if (j >= 0 && gpuFloorModel.width > j) {
								@Pc(466) val mask: Int = tileMask!![maskIndex]
								@Pc(473) val colours: IntArray? = gpuFloorModel.tileColours[j][tileZ2]
								if (colours != null && mask != 0) {
									if (mask == 1) {
										@Pc(631) val offsetsX: IntArray? = gpuFloorModel.tileOffsetsX[j][tileZ2]
										@Pc(638) val offsetsZ: IntArray? = gpuFloorModel.tileOffsetsZ[j][tileZ2]
										@Pc(640) var index: Int = 0
										label119@ while (true) {
											while (true) {
												if (index >= colours.size) {
													break@label119
												}
												if (colours[index] == -1 || colours[index + 1] == -1 || colours[index + 2] == -1) {
													index += 3
												} else {
													this.addVertex(localZ, j, offsetsZ!![index], tileZ2, offsetsX!![index], localX)
													index++
													this.addVertex(localZ, j, offsetsZ[index], tileZ2, offsetsX[index], localX)
													index++
													this.addVertex(localZ, j, offsetsZ[index], tileZ2, offsetsX[index], localX)
													index++
												}
											}
										}
									} else if (mask == 3) {
										this.addVertex(localZ, j, 0, tileZ2, 0, localX)
										this.addVertex(localZ, j, 0, tileZ2, gpuFloorModel.tileUnits, localX)
										this.addVertex(localZ, j, gpuFloorModel.tileUnits, tileZ2, 0, localX)
									} else if (mask == 2) {
										this.addVertex(localZ, j, 0, tileZ2, gpuFloorModel.tileUnits, localX)
										this.addVertex(localZ, j, gpuFloorModel.tileUnits, tileZ2, gpuFloorModel.tileUnits, localX)
										this.addVertex(localZ, j, 0, tileZ2, 0, localX)
									} else if (mask == 5) {
										this.addVertex(localZ, j, gpuFloorModel.tileUnits, tileZ2, gpuFloorModel.tileUnits, localX)
										this.addVertex(localZ, j, gpuFloorModel.tileUnits, tileZ2, 0, localX)
										this.addVertex(localZ, j, 0, tileZ2, gpuFloorModel.tileUnits, localX)
									} else if (mask == 4) {
										this.addVertex(localZ, j, gpuFloorModel.tileUnits, tileZ2, 0, localX)
										this.addVertex(localZ, j, 0, tileZ2, 0, localX)
										this.addVertex(localZ, j, gpuFloorModel.tileUnits, tileZ2, gpuFloorModel.tileUnits, localX)
									}
								}
							}
							maskIndex++
							localX++
							j++
						}
					} else {
						maskIndex += this.maxTileX - this.minTileX
					}
					localZ++
					tileZ2++
				}
				this.indexWriter!!.a()
				if (this.indexBuffer.unlock()) {
					this.vertexWriter!!.a()
					this.vertexStream = this.nativeToolkit.createStream(false)
					this.vertexStream!!.createFromSource(nativeHeapBuffer, 16, this.vertexCount * 16)
					break
				}
				this.vertexWriter!!.e(0)
				this.vertexTable!!.clear()
			}
		} else {
			this.vertexStream = null
			this.indexBuffer = null
		}
		this.normalsZ = null
		this.normalsY = null
		this.normalsX = null
		this.vertexWriter = null
		this.indexWriter = null
		this.vertexTable = null
	}

	@OriginalMember(owner = "client!sea", name = "a", descriptor = "(II[[ZZI)V")
	fun render(@OriginalArg(0) tileZ: Int, @OriginalArg(1) radius: Int, @OriginalArg(2) visibility: Array<BooleanArray?>?, @OriginalArg(4) tileX: Int) {
		if (this.indexBuffer == null || (this.minTileX > radius + tileX || (tileX - radius > this.maxTileX || (tileZ + radius < this.minTileZ || tileZ - radius > this.maxTileZ)))) {
			return
		}
		@Pc(57) var i: Int = this.minTileZ
		while (i <= this.maxTileZ) {
			@Pc(62) var j: Int = this.minTileX
			while (j <= this.maxTileX) {
				@Pc(69) val dx: Int = j - tileX
				@Pc(74) val dz: Int = i - tileZ
				if (dx > -radius && dx < radius && dz > -radius && radius > dz && visibility!![dx + radius]!![radius + dz]) {
					this.nativeToolkit!!.setUniformColourValue((this.pointLight.getIntensity() * 255.0f).toInt().toByte())
					this.nativeToolkit.setStreamSource(this.vertexStream, 0)
					this.nativeToolkit.setVertexDeclaration(this.nativeToolkit.positionColorDeclaration)
					this.nativeToolkit.drawIndexedPrimitive(PrimitiveType.aClass134_6, this.indexCount / 3, this.indexBuffer, 0, 0, this.vertexCount)
					return
				}
				j++
			}
			i++
		}
	}

	@OriginalMember(owner = "client!sea", name = "a", descriptor = "(IIIIIII)V")
	private fun addVertex(@OriginalArg(0) localZ: Int, @OriginalArg(1) tileX: Int, @OriginalArg(3) offsetZ: Int, @OriginalArg(4) tileZ: Int, @OriginalArg(5) offsetX: Int, @OriginalArg(6) localX: Int) {
		@Pc(7) var key: Long = -1L
		@Pc(15) val x: Int = offsetX + (tileX shl this.gpuFloorModel.tileScale)
		@Pc(24) val z: Int = offsetZ + (tileZ shl this.gpuFloorModel.tileScale)
		@Pc(31) val y: Int = this.gpuFloorModel.averageHeight(x, z)
		if ((offsetX and 0x7F) == 0 || (offsetZ and 0x7F) == 0) {
			key = ((z.toLong() and 0xFFFFL) shl 16) or (x.toLong() and 0xFFFFL)
			@Pc(65) val node: Node? = this.vertexTable!!.get(key)
			if (node != null) {
				this.writeIndex((node as ShortNode).value)
				return
			}
		}
		@Pc(83) val vertexIndex: Short = (this.vertexCount++).toShort()
		if (key != -1L) {
			this.vertexTable!!.put(key, ShortNode(vertexIndex))
		}
		@Pc(116) val normalX: Float
		@Pc(109) val normalY: Float
		@Pc(123) val normalZ: Float
		@Pc(197) var local197: Float
		@Pc(205) var local205: Float
		@Pc(212) var local212: Float
		@Pc(219) var local219: Float
		@Pc(226) var local226: Float
		@Pc(235) var local235: Float
		@Pc(244) var local244: Float
		@Pc(301) var local301: Float
		if (offsetX == 0 && offsetZ == 0) {
			normalY = this.normalsY!![localX]!![localZ]
			normalX = this.normalsX!![localX]!![localZ]
			normalZ = this.normalsZ!![localX]!![localZ]
		} else if (offsetX == this.gpuFloorModel.tileUnits && offsetZ == 0) {
			normalY = this.normalsY!![localX + 1]!![localZ]
			normalZ = this.normalsZ!![localX + 1]!![localZ]
			normalX = this.normalsX!![localX + 1]!![localZ]
		} else if (this.gpuFloorModel.tileUnits == offsetX && this.gpuFloorModel.tileUnits == offsetZ) {
			normalX = this.normalsX!![localX + 1]!![localZ + 1]
			normalZ = this.normalsZ!![localX + 1]!![localZ + 1]
			normalY = this.normalsY!![localX + 1]!![localZ + 1]
		} else if (offsetX == 0 && offsetZ == this.gpuFloorModel.tileUnits) {
			normalZ = this.normalsZ!![localX]!![localZ + 1]
			normalY = this.normalsY!![localX]!![localZ + 1]
			normalX = this.normalsX!![localX]!![localZ + 1]
		} else {
			val fracX: Float = offsetX.toFloat() / this.gpuFloorModel.tileUnits.toFloat()
			val fracZ: Float = offsetZ.toFloat() / this.gpuFloorModel.tileUnits.toFloat()
			val normalX00: Float = this.normalsX!![localX]!![localZ]
			val normalY00: Float = this.normalsY!![localX]!![localZ]
			val normalZ00: Float = this.normalsZ!![localX]!![localZ]
			val normalX10: Float = this.normalsX!![localX + 1]!![localZ]
			val normalY10: Float = this.normalsY!![localX + 1]!![localZ]
			@Pc(259) val normalZ0: Float = normalZ00 + fracX * (this.normalsZ!![localX]!![localZ + 1] - normalZ00)
			@Pc(274) val normalY0: Float = normalY00 + (this.normalsY!![localX]!![localZ + 1] - normalY00) * fracX
			@Pc(292) val normalY1: Float = normalY10 + fracX * (this.normalsY!![localX + 1]!![localZ + 1] - normalY10)
			val normalZ10: Float = this.normalsZ!![localX + 1]!![localZ]
			@Pc(316) val normalX0: Float = normalX00 + (this.normalsX!![localX]!![localZ + 1] - normalX00) * fracX
			@Pc(333) val normalX1: Float = normalX10 + fracX * (this.normalsX!![localX + 1]!![localZ + 1] - normalX10)
			normalX = normalX0 + (normalX1 - normalX0) * fracZ
			normalY = (normalY1 - normalY0) * fracZ + normalY0
			@Pc(366) val normalZ1: Float = normalZ10 + fracX * (this.normalsZ!![localX + 1]!![localZ + 1] - normalZ10)
			normalZ = normalZ0 + (normalZ1 - normalZ0) * fracZ
		}
		var lightDirX: Float = (this.pointLight.getX() - x).toFloat()
		var lightDirY: Float = (this.pointLight.getY() - y).toFloat()
		var lightDirZ: Float = (this.pointLight.getZ() - z).toFloat()
		val distance: Float = java.lang.Math.sqrt((lightDirZ * lightDirZ + lightDirX * lightDirX + lightDirY * lightDirY).toDouble()).toFloat()
		val invDistance: Float = 1.0f / distance
		lightDirX *= invDistance
		lightDirY *= invDistance
		lightDirZ *= invDistance
		val distanceRatio: Float = distance / this.pointLight.getRadius().toFloat()
		var attenuation: Float = 1.0f - distanceRatio * distanceRatio
		if (attenuation < 0.0f) {
			attenuation = 0.0f
		}
		var diffuse: Float = lightDirZ * normalZ + lightDirX * normalX + normalY * lightDirY
		if (diffuse < 0.0f) {
			diffuse = 0.0f
		}
		@Pc(545) var lighting: Float = attenuation * diffuse * 2.0f
		if (lighting > 1.0f) {
			lighting = 1.0f
		}
		@Pc(556) val colour: Int = this.pointLight.getColour()
		@Pc(566) var red: Int = (lighting * ((colour shr 16) and 0xFF).toFloat()).toInt()
		if (red > 255) {
			red = 255
		}
		@Pc(583) var green: Int = (lighting * ((colour shr 8) and 0xFF).toFloat()).toInt()
		if (green > 255) {
			green = 255
		}
		@Pc(596) var blue: Int = ((colour and 0xFF).toFloat() * lighting).toInt()
		if (blue > 255) {
			blue = 255
		}
		if (Stream.c()) {
			this.vertexWriter!!.a(x.toFloat())
			this.vertexWriter!!.a(y.toFloat())
			this.vertexWriter!!.a(z.toFloat())
		} else {
			this.vertexWriter!!.b(x.toFloat())
			this.vertexWriter!!.b(y.toFloat())
			this.vertexWriter!!.b(z.toFloat())
		}
		if (this.nativeToolkit!!.endiannessType == 0) {
			this.vertexWriter!!.f(blue)
			this.vertexWriter!!.f(green)
			this.vertexWriter!!.f(red)
		} else {
			this.vertexWriter!!.f(red)
			this.vertexWriter!!.f(green)
			this.vertexWriter!!.f(blue)
		}
		this.vertexWriter!!.f(255)
		this.writeIndex(vertexIndex)
	}

	@OriginalMember(owner = "client!sea", name = "a", descriptor = "(SB)V")
	private fun writeIndex(@OriginalArg(0) index: Short) {
		if (Stream.c()) {
			this.indexWriter!!.d(index.toInt())
		} else {
			this.indexWriter!!.a(index.toInt())
		}
	}
}
