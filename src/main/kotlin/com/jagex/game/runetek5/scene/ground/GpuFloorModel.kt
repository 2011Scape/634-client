package com.jagex.game.runetek5.scene.ground

import com.jagex.game.runetek5.VertexAttribute
import com.jagex.game.runetek5.cache.node.lru.GpuShadow
import com.jagex.game.runetek5.NativeMatrix
import com.jagex.game.runetek5.cache.node.GpuFloorLight
import com.jagex.game.runetek5.cache.node.impl.ShortNode
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.config.material.MaterialProvider
import jaclib.memory.Buffer
import jaclib.memory.Stream
import jaclib.memory.heap.NativeHeapBuffer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.GpuFloorLayer
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent
import com.jagex.game.runetek5.graphics.FloorHardShadows
import com.jagex.game.runetek5.graphics.GpuIndexBuffer
import com.jagex.game.runetek5.toolkit.companion.NativeStream
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration
import com.jagex.game.runetek5.utils.ColorUtil
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationAction
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.PrimitiveType
import com.jagex.game.runetek5.TextureCombineFunction
import com.jagex.game.runetek5.utils.ArrayUtils
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!qm")
class GpuFloorModel : Ground {

	@OriginalMember(owner = "client!qm", name = "z", descriptor = "I")
	private var indexCount: Int = 0

	@OriginalMember(owner = "client!qm", name = "B", descriptor = "I")
	private var vertexCount: Int = 0

	@OriginalMember(owner = "client!qm", name = "M", descriptor = "[[[I")
	private var tileWaterDepths: Array<Array<IntArray?>>? = null

	@OriginalMember(owner = "client!qm", name = "W", descriptor = "Lclient!np;")
	@JvmField
	var vertexDeclaration: VertexDeclaration? = null

	@OriginalMember(owner = "client!qm", name = "Y", descriptor = "Lclient!tc;")
	private var colourStream: NativeStream? = null

	@OriginalMember(owner = "client!qm", name = "Z", descriptor = "Lclient!tc;")
	private var vertexStream: NativeStream? = null

	@OriginalMember(owner = "client!qm", name = "ab", descriptor = "[Lclient!eu;")
	private var layers: Array<GpuFloorLayer?>? = null

	@OriginalMember(owner = "client!qm", name = "bb", descriptor = "I")
	private var totalTileVertexCount: Int = 0

	@OriginalMember(owner = "client!qm", name = "db", descriptor = "I")
	private var maxTileVertexCount: Int = 0

	@OriginalMember(owner = "client!qm", name = "D", descriptor = "F")
	private var maxHeight: Float = -3.4028235E38f

	@OriginalMember(owner = "client!qm", name = "E", descriptor = "F")
	private var minHeight: Float = Float.MAX_VALUE

	@OriginalMember(owner = "client!qm", name = "k", descriptor = "Lclient!uh;")
	private val lights: Deque = Deque()

	@OriginalMember(owner = "client!qm", name = "A", descriptor = "I")
	private val quarterTileScale: Int = this.tileScale - 2

	@OriginalMember(owner = "client!qm", name = "U", descriptor = "Lclient!wga;")
	@JvmField
	val nativeToolkit: AbstractNativeToolkit

	@OriginalMember(owner = "client!qm", name = "G", descriptor = "[[[I")
	private var tileHeightOffsets: Array<Array<IntArray?>>?

	@OriginalMember(owner = "client!qm", name = "p", descriptor = "[[[I")
	private var tileBlendColours: Array<Array<IntArray?>>?

	@OriginalMember(owner = "client!qm", name = "u", descriptor = "[[[I")
	@JvmField
	var tileColours: Array<Array<IntArray?>>?

	@OriginalMember(owner = "client!qm", name = "fb", descriptor = "[[F")
	private var normalX: Array<FloatArray>?

	@OriginalMember(owner = "client!qm", name = "K", descriptor = "[[[Lclient!eu;")
	private var tileLayers: Array<Array<Array<GpuFloorLayer?>?>>?

	@OriginalMember(owner = "client!qm", name = "P", descriptor = "[[B")
	private val tileFlags: Array<ByteArray>

	@OriginalMember(owner = "client!qm", name = "J", descriptor = "I")
	private val quarterTileUnits: Int

	@OriginalMember(owner = "client!qm", name = "x", descriptor = "[[S")
	@JvmField
	val tileVertexIndices: Array<ShortArray?>

	@OriginalMember(owner = "client!qm", name = "cb", descriptor = "[[B")
	private var shadowLevels: Array<ByteArray>?

	@OriginalMember(owner = "client!qm", name = "L", descriptor = "[[[I")
	@JvmField
	var tileOffsetsZ: Array<Array<IntArray?>>?

	@OriginalMember(owner = "client!qm", name = "eb", descriptor = "[[F")
	private var normalY: Array<FloatArray>?

	@OriginalMember(owner = "client!qm", name = "T", descriptor = "[[[I")
	@JvmField
	var tileOffsetsX: Array<Array<IntArray?>>?

	@OriginalMember(owner = "client!qm", name = "s", descriptor = "I")
	@JvmField
	val featureFlags: Int

	@OriginalMember(owner = "client!qm", name = "V", descriptor = "[[F")
	private var normalZ: Array<FloatArray>?

	@OriginalMember(owner = "client!qm", name = "X", descriptor = "Lclient!eq;")
	private var layerTable: IterableHashTable? = null

	@OriginalMember(owner = "client!qm", name = "F", descriptor = "Lclient!dg;")
	private var hardShadows: FloorHardShadows? = null

	@OriginalMember(owner = "client!qm", name = "<init>", descriptor = "(Lclient!wga;IIII[[I[[II)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(1) groundFlags: Int, @OriginalArg(2) featureFlags: Int, @OriginalArg(3) width: Int, @OriginalArg(4) length: Int, @OriginalArg(5) tileHeights: Array<IntArray?>?, @OriginalArg(6) normalHeights: Array<IntArray?>?, @OriginalArg(7) scaling: Int) : super(width, length, scaling, tileHeights) {
		this.nativeToolkit = nativeToolkit!!
		this.tileHeightOffsets = Array(width) { arrayOfNulls<IntArray>(length) }
		this.tileBlendColours = Array(width) { arrayOfNulls<IntArray>(length) }
		this.tileColours = Array(width) { arrayOfNulls<IntArray>(length) }
		this.normalX = Array(super.width + 1) { FloatArray(super.length + 1) }
		this.tileLayers = Array(width) { arrayOfNulls<Array<GpuFloorLayer?>>(length) }
		this.tileFlags = Array(width) { ByteArray(length) }
		this.quarterTileUnits = 0x1 shl this.quarterTileScale
		this.tileVertexIndices = arrayOfNulls<ShortArray>(width * length)
		this.shadowLevels = Array(width + 1) { ByteArray(length + 1) }
		this.tileOffsetsZ = Array(width) { arrayOfNulls<IntArray>(length) }
		this.normalY = Array(super.width + 1) { FloatArray(super.length + 1) }
		this.tileOffsetsX = Array(width) { arrayOfNulls<IntArray>(length) }
		this.featureFlags = featureFlags
		this.normalZ = Array(super.width + 1) { FloatArray(super.length + 1) }
		@Pc(121) var i: Int = 0
		while (i <= super.length) {
			@Pc(125) var j: Int = 0
			while (super.width >= j) {
				@Pc(134) val height: Int = super.tileHeights!![j]!![i]
				if (height.toFloat() > this.maxHeight) {
					this.maxHeight = height.toFloat()
				}
				if (height.toFloat() < this.minHeight) {
					this.minHeight = height.toFloat()
				}
				if (j > 0 && i > 0 && super.width > j && i < super.length) {
					@Pc(191) val heightDeltaX: Int = normalHeights!![j + 1]!![i] - normalHeights[j - 1]!![i]
					@Pc(208) val heightDeltaZ: Int = normalHeights[j]!![i + 1] - normalHeights[j]!![i - 1]
					@Pc(228) val normalScale: Float = (1.0 / Math.sqrt((scaling * 4 * scaling + heightDeltaX * heightDeltaX + heightDeltaZ * heightDeltaZ).toDouble())).toFloat()
					this.normalX!![j][i] = heightDeltaX.toFloat() * normalScale
					this.normalY!![j][i] = (-scaling * 2).toFloat() * normalScale
					this.normalZ!![j][i] = normalScale * heightDeltaZ.toFloat()
				}
				j++
			}
			i++
		}
		this.minHeight--
		this.maxHeight++
		this.layerTable = IterableHashTable(128)
		if ((this.featureFlags and 0x10) != 0) {
			this.hardShadows = FloorHardShadows(this.nativeToolkit, this)
		}
	}

	@OriginalMember(owner = "client!qm", name = "fa", descriptor = "(IILclient!r;)Lclient!r;")
	override fun fa(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) shadow: Shadow?): Shadow? {
		if ((this.tileFlags[x][z].toInt() and 0x1) == 0) {
			return null
		}
		@Pc(24) val shadowSize: Int = super.tileUnits shr this.nativeToolkit.shadowSizeShift
		@Pc(27) val reusableShadow: GpuShadow? = shadow as GpuShadow?
		@Pc(37) val tileShadow: GpuShadow
		if (reusableShadow != null && reusableShadow.fitsDimension(shadowSize, shadowSize)) {
			tileShadow = reusableShadow
			reusableShadow.reset()
		} else {
			tileShadow = GpuShadow(this.nativeToolkit, shadowSize, shadowSize)
		}
		tileShadow.setDimension(shadowSize, shadowSize, 0, 0)
		this.projectShadow(tileShadow, z, x)
		return tileShadow
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(Lclient!fc;III)V")
	private fun projectShadow(@OriginalArg(0) shadow: GpuShadow?, @OriginalArg(1) tileZ: Int, @OriginalArg(2) tileX: Int) {
		@Pc(21) val offsetsX: IntArray? = this.tileOffsetsX!![tileX][tileZ]
		@Pc(28) val offsetsZ: IntArray? = this.tileOffsetsZ!![tileX][tileZ]
		@Pc(31) val length: Int = offsetsX!!.size
		if (GpuFloorModel.shadowVerticesX.size < length) {
			GpuFloorModel.shadowVerticesX = IntArray(length)
			GpuFloorModel.shadowVerticesZ = IntArray(length)
		}
		for (@Pc(43) i in 0 until length) {
			GpuFloorModel.shadowVerticesX[i] = offsetsX[i] shr this.nativeToolkit.shadowSizeShift
			GpuFloorModel.shadowVerticesZ[i] = offsetsZ!![i] shr this.nativeToolkit.shadowSizeShift
		}
		@Pc(75) var index: Int = 0
		while (length > index) {
			@Pc(81) val xA: Int = GpuFloorModel.shadowVerticesX[index]
			@Pc(86) val zA: Int = GpuFloorModel.shadowVerticesZ[index++]
			@Pc(90) val xB: Int = GpuFloorModel.shadowVerticesX[index]
			@Pc(95) val zB: Int = GpuFloorModel.shadowVerticesZ[index++]
			@Pc(99) val xC: Int = GpuFloorModel.shadowVerticesX[index]
			@Pc(104) val zC: Int = GpuFloorModel.shadowVerticesZ[index++]
			if (-((xC - xB) * (zB - zA)) + (xA - xB) * (zB - zC) > 0) {
				shadow!!.fillTriangle(zA, xA, xB, zB, zC, xC)
			}
		}
	}

	@OriginalMember(owner = "client!qm", name = "wa", descriptor = "(Lclient!r;IIIIZ)V")
	override fun wa(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) x: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) z: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean) {
		if (this.hardShadows != null && shadow != null) {
			@Pc(24) val shadowX: Int = (x - ((heightOffset * this.nativeToolkit.shadowOffsetX) shr 8)) shr this.nativeToolkit.shadowSizeShift
			@Pc(38) val shadowZ: Int = (z - ((this.nativeToolkit.shadowOffsetZ * heightOffset) shr 8)) shr this.nativeToolkit.shadowSizeShift
			this.hardShadows!!.removeShadow(shadowZ, shadow, shadowX)
		}
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(II)V")
	override fun renderTile(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int) {
	}

	@OriginalMember(owner = "client!qm", name = "CA", descriptor = "(Lclient!r;IIIIZ)V")
	override fun CA(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) x: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) z: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean) {
		if (this.hardShadows != null && shadow != null) {
			@Pc(24) val shadowX: Int = (x - ((this.nativeToolkit.shadowOffsetX * heightOffset) shr 8)) shr this.nativeToolkit.shadowSizeShift
			@Pc(39) val shadowZ: Int = (z - ((this.nativeToolkit.shadowOffsetZ * heightOffset) shr 8)) shr this.nativeToolkit.shadowSizeShift
			this.hardShadows!!.addShadow(shadowX, shadow, shadowZ)
		}
	}

	@OriginalMember(owner = "client!qm", name = "YA", descriptor = "()V")
	override fun YA() {
		if (this.totalTileVertexCount <= 0) {
			this.hardShadows = null
		} else {
			@Pc(19) val smoothedShadowLevels: Array<ByteArray> = Array(super.width + 1) { ByteArray(super.length + 1) }
			for (@Pc(21) i in 1 until super.width) {
				for (@Pc(25) j in 1 until super.length) {
					smoothedShadowLevels[i][j] = ((this.shadowLevels!![i][j - 1].toInt() shr 2) + (this.shadowLevels!![i + 1][j].toInt() shr 3) + (this.shadowLevels!![i + -1][j].toInt() shr 2) + (this.shadowLevels!![i][j + 1].toInt() shr 3) + (this.shadowLevels!![i][j].toInt() shr 1)).toByte()
				}
			}
			@Pc(105) val allLayers: Array<GpuFloorLayer?> = arrayOfNulls<GpuFloorLayer>(this.layerTable!!.size())
			@Suppress("UNCHECKED_CAST")
			this.layerTable!!.toArray(allLayers as Array<Node?>)
			for (@Pc(113) k in 0 until allLayers.size) {
				allLayers[k]!!.initColours(this.totalTileVertexCount)
			}
			@Pc(129) var vertexStride: Int = 20
			if (this.tileWaterDepths != null) {
				vertexStride += 4
			}
			if ((this.featureFlags and 0x7) != 0) {
				vertexStride += 12
			}
			@Pc(152) val nativeHeapBuffer: NativeHeapBuffer = this.nativeToolkit.nativeHeap.a(this.totalTileVertexCount * 4, false)
			@Pc(162) val nativeHeapBuffer2: NativeHeapBuffer = this.nativeToolkit.nativeHeap.a(this.totalTileVertexCount * vertexStride, false)
			@Pc(167) val stream: Stream = Stream(nativeHeapBuffer2)
			@Pc(172) val stream2: Stream = Stream(nativeHeapBuffer)
			@Pc(176) val vertexLayers: Array<GpuFloorLayer?> = arrayOfNulls<GpuFloorLayer>(this.totalTileVertexCount)
			@Pc(183) var hashCapacity: Int = Utils.floorPowerOfTwo(this.totalTileVertexCount / 4)
			if (hashCapacity < 1) {
				hashCapacity = 1
			}
			@Pc(193) val iterableHashTable: IterableHashTable = IterableHashTable(hashCapacity)
			@Pc(197) val tileUniqueLayers: Array<GpuFloorLayer?> = arrayOfNulls<GpuFloorLayer>(this.maxTileVertexCount)
			@Pc(203) var local203: Int
			for (@Pc(199) m in 0 until super.width) {
				for (tileZ in 0 until super.length) {
					if (this.tileColours!![m][tileZ] != null) {
						@Pc(219) val tileVertexLayers: Array<GpuFloorLayer?>? = this.tileLayers!![m][tileZ]
						@Pc(226) val offsetsX: IntArray? = this.tileOffsetsX!![m][tileZ]
						@Pc(233) val offsetsZ: IntArray? = this.tileOffsetsZ!![m][tileZ]
						@Pc(240) var blendedColours: IntArray? = this.tileBlendColours!![m][tileZ]
						@Pc(247) val colours: IntArray? = this.tileColours!![m][tileZ]
						@Pc(259) val heightOffsets: IntArray? = if (this.tileHeightOffsets == null) null else this.tileHeightOffsets!![m][tileZ]
						@Pc(271) val waterDepths: IntArray? = if (this.tileWaterDepths == null) null else this.tileWaterDepths!![m][tileZ]
						if (blendedColours == null) {
							blendedColours = colours
						}
						@Pc(282) val normalX00: Float = this.normalX!![m][tileZ]
						@Pc(289) val normalY00: Float = this.normalY!![m][tileZ]
						@Pc(296) val normalZ00: Float = this.normalZ!![m][tileZ]
						@Pc(305) val normalX01: Float = this.normalX!![m][tileZ + 1]
						@Pc(314) val normalY01: Float = this.normalY!![m][tileZ + 1]
						@Pc(323) val normalZ01: Float = this.normalZ!![m][tileZ + 1]
						@Pc(334) val normalX11: Float = this.normalX!![m + 1][tileZ + 1]
						@Pc(345) val normalY11: Float = this.normalY!![m + 1][tileZ + 1]
						@Pc(356) val normalZ11: Float = this.normalZ!![m + 1][tileZ + 1]
						@Pc(365) val normalX10: Float = this.normalX!![m + 1][tileZ]
						@Pc(374) val normalY10: Float = this.normalY!![m + 1][tileZ]
						@Pc(383) val normalZ10: Float = this.normalZ!![m + 1][tileZ]
						@Pc(391) val shadow00: Int = smoothedShadowLevels[m][tileZ].toInt() and 0xFF
						@Pc(401) val shadow01: Int = smoothedShadowLevels[m][tileZ + 1].toInt() and 0xFF
						@Pc(413) val shadow11: Int = smoothedShadowLevels[m + 1][tileZ + 1].toInt() and 0xFF
						@Pc(423) val shadow10: Int = smoothedShadowLevels[m + 1][tileZ].toInt() and 0xFF
						@Pc(425) var uniqueLayerCount: Int = 0
						@Pc(435) var local435: Int
						label355@ for (@Pc(427) tileVertex in 0 until colours!!.size) {
							@Pc(433) val layer: GpuFloorLayer? = tileVertexLayers!![tileVertex]
							for (layerSlot in 0 until uniqueLayerCount) {
								if (layer === tileUniqueLayers[layerSlot]) {
									continue@label355
								}
							}
							tileUniqueLayers[uniqueLayerCount++] = layer
						}
						@Pc(477) val vertexIndices: ShortArray = ShortArray(colours.size)
						this.tileVertexIndices[tileZ * super.width + m] = vertexIndices
						for (tileVertexIndex in 0 until colours.size) {
							@Pc(490) val vertexX: Int = (m shl super.tileScale) + offsetsX!![tileVertexIndex]
							@Pc(500) val vertexZ: Int = (tileZ shl super.tileScale) + offsetsZ!![tileVertexIndex]
							@Pc(505) val gridX: Int = vertexX shr this.quarterTileScale
							@Pc(510) val gridZ: Int = vertexZ shr this.quarterTileScale
							@Pc(514) val colour: Int = colours[tileVertexIndex]
							@Pc(518) val blendedColour: Int = blendedColours!![tileVertexIndex]
							@Pc(526) val heightOffset: Int = if (heightOffsets == null) 0 else heightOffsets[tileVertexIndex]
							@Pc(544) val vertexKey: Long = gridZ.toLong() or (colour.toLong() shl 32) or (blendedColour.toLong() shl 48) or (gridX shl 16).toLong()
							@Pc(548) val offsetX: Int = offsetsX[tileVertexIndex]
							@Pc(552) val offsetZ: Int = offsetsZ[tileVertexIndex]
							@Pc(554) val baseLightness: Byte = 74
							@Pc(556) var rgb: Int = 0
							@Pc(587) var vertexNormalX: Float
							@Pc(591) var vertexNormalY: Float
							@Pc(589) var vertexNormalZ: Float
							@Pc(641) var local641: Float
							@Pc(595) var lightness: Int
							if (offsetX == 0 && offsetZ == 0) {
								lightness = baseLightness - shadow00
								vertexNormalY = normalY00
								vertexNormalX = normalX00
								vertexNormalZ = normalZ00
							} else if (offsetX == 0 && offsetZ == super.tileUnits) {
								lightness = baseLightness - shadow01
								vertexNormalX = normalX01
								vertexNormalY = normalY01
								vertexNormalZ = normalZ01
							} else if (super.tileUnits == offsetX && super.tileUnits == offsetZ) {
								vertexNormalX = normalX11
								vertexNormalZ = normalZ11
								vertexNormalY = normalY11
								lightness = baseLightness - shadow11
							} else if (offsetX == super.tileUnits && offsetZ == 0) {
								vertexNormalZ = normalZ10
								vertexNormalX = normalX10
								vertexNormalY = normalY10
								lightness = baseLightness - shadow10
							} else {
								@Pc(617) val fracX: Float = offsetX.toFloat() / super.tileUnits.toFloat()
								@Pc(624) val fracZ: Float = offsetZ.toFloat() / super.tileUnits.toFloat()
								@Pc(632) val lerpNormalX0: Float = normalX00 + (normalX10 - normalX00) * fracX
								val lerpNormalY0: Float = normalY00 + (normalY10 - normalY00) * fracX
								@Pc(649) val lerpNormalZ0: Float = fracX * (normalZ10 - normalZ00) + normalZ00
								@Pc(658) val lerpNormalX1: Float = fracX * (normalX11 - normalX01) + normalX01
								@Pc(666) val lerpNormalY1: Float = normalY01 + (normalY11 - normalY01) * fracX
								vertexNormalX = lerpNormalX0 + fracZ * (lerpNormalX1 - lerpNormalX0)
								vertexNormalY = lerpNormalY0 + fracZ * (lerpNormalY1 - lerpNormalY0)
								@Pc(692) val lerpNormalZ1: Float = normalZ01 + (normalZ11 - normalZ01) * fracX
								vertexNormalZ = lerpNormalZ0 + fracZ * (lerpNormalZ1 - lerpNormalZ0)
								@Pc(712) val lerpShadow0: Int = (((shadow10 - shadow00) * offsetX) shr super.tileScale) + shadow00
								@Pc(724) val lerpShadow1: Int = shadow01 + ((offsetX * (shadow11 - shadow01)) shr super.tileScale)
								lightness = baseLightness - lerpShadow0 - ((offsetZ * (lerpShadow1 - lerpShadow0)) shr super.tileScale)
							}
							@Pc(777) var lightIntensity: Float = 1.0f
							if (colour != -1) {
								@Pc(788) var adjustedLightness: Int = (lightness * (colour and 0x7F)) shr 7
								if (adjustedLightness < 2) {
									adjustedLightness = 2
								} else if (adjustedLightness > 126) {
									adjustedLightness = 126
								}
								rgb = ColorUtil.hslToRgb!![adjustedLightness or (colour and 0xFF80)]
								if ((this.featureFlags and 0x7) == 0) {
									lightIntensity = vertexNormalX * this.nativeToolkit.sunPosition[0] + vertexNormalY * this.nativeToolkit.sunPosition[1] + this.nativeToolkit.sunPosition[2] * vertexNormalZ
									lightIntensity = lightIntensity * (if (lightIntensity > 0.0f) this.nativeToolkit.sunColorIntensity else this.nativeToolkit.antiSunColorIntensity) + this.nativeToolkit.intensity
								}
							}
							@Pc(864) var node: Node? = null
							if ((vertexX and (this.quarterTileUnits - 1)) == 0 && ((this.quarterTileUnits - 1) and vertexZ) == 0) {
								node = iterableHashTable.get(vertexKey)
							}
							@Pc(1197) var vertexIndex: Int
							@Pc(918) var local918: Int
							if (node == null) {
								var blendedRgb: Int
								if (blendedColour == colour) {
									blendedRgb = rgb
								} else {
									@Pc(896) var blendedLightness: Int = ((blendedColour and 0x7F) * lightness) shr 7
									if (blendedLightness < 2) {
										blendedLightness = 2
									} else if (blendedLightness > 126) {
										blendedLightness = 126
									}
									blendedRgb = ColorUtil.hslToRgb!![blendedLightness or (blendedColour and 0xFF80)]
									if ((this.featureFlags and 0x7) == 0) {
										var blendedIntensity: Float = this.nativeToolkit.sunPosition[2] * vertexNormalZ + vertexNormalY * this.nativeToolkit.sunPosition[1] + this.nativeToolkit.sunPosition[0] * vertexNormalX
										blendedIntensity = this.nativeToolkit.intensity + (if (lightIntensity > 0.0f) this.nativeToolkit.sunColorIntensity else this.nativeToolkit.antiSunColorIntensity) * lightIntensity
										@Pc(974) var red: Int = (blendedRgb shr 16) and 0xFF
										@Pc(980) var green: Int = (blendedRgb shr 8) and 0xFF
										@Pc(984) var blue: Int = blendedRgb and 0xFF
										red = (red.toFloat() * blendedIntensity).toInt()
										green = (green.toFloat() * blendedIntensity).toInt()
										if (red < 0) {
											red = 0
										} else if (red > 255) {
											red = 255
										}
										blue = (blue.toFloat() * blendedIntensity).toInt()
										if (green < 0) {
											green = 0
										} else if (green > 255) {
											green = 255
										}
										if (blue < 0) {
											blue = 0
										} else if (blue > 255) {
											blue = 255
										}
										blendedRgb = (red shl 16) or (green shl 8) or blue
									}
								}
								if (Stream.c()) {
									stream.a(vertexX.toFloat())
									stream.a((heightOffset + this.averageHeight(vertexX, vertexZ)).toFloat())
									stream.a(vertexZ.toFloat())
									stream.a(vertexX.toFloat())
									stream.a(vertexZ.toFloat())
									if (this.tileWaterDepths != null) {
										stream.a((if (waterDepths == null) 0 else waterDepths[tileVertexIndex] - 1).toFloat())
									}
									if ((this.featureFlags and 0x7) != 0) {
										stream.a(vertexNormalX)
										stream.a(vertexNormalY)
										stream.a(vertexNormalZ)
									}
								} else {
									stream.b(vertexX.toFloat())
									stream.b((this.averageHeight(vertexX, vertexZ) + heightOffset).toFloat())
									stream.b(vertexZ.toFloat())
									stream.b(vertexX.toFloat())
									stream.b(vertexZ.toFloat())
									if (this.tileWaterDepths != null) {
										stream.b((if (waterDepths == null) 0 else waterDepths[tileVertexIndex] - 1).toFloat())
									}
									if ((this.featureFlags and 0x7) != 0) {
										stream.b(vertexNormalX)
										stream.b(vertexNormalY)
										stream.b(vertexNormalZ)
									}
								}
								if (this.nativeToolkit.endiannessType == 0) {
									stream2.b(blendedRgb or 0xFF000000.toInt())
								} else {
									stream2.c(blendedRgb or 0xFF000000.toInt())
								}
								vertexIndex = this.vertexCount++
								vertexIndices[tileVertexIndex] = vertexIndex.toShort()
								if (colour != -1) {
									vertexLayers[vertexIndex] = tileVertexLayers!![tileVertexIndex]
								}
								iterableHashTable.put(vertexKey, ShortNode(vertexIndices[tileVertexIndex]))
							} else {
								vertexIndices[tileVertexIndex] = (node as ShortNode).value
								vertexIndex = vertexIndices[tileVertexIndex].toInt() and 0xFFFF
								if (colour != -1 && tileVertexLayers!![tileVertexIndex]!!.key < vertexLayers[vertexIndex]!!.key) {
									vertexLayers[vertexIndex] = tileVertexLayers[tileVertexIndex]
								}
							}
							for (uniqueLayer in 0 until uniqueLayerCount) {
								tileUniqueLayers[uniqueLayer]!!.setVertexColour(vertexIndex, lightIntensity, lightness, rgb)
							}
							this.indexCount++
						}
					}
				}
			}
			var vertex: Int = 0
			while (vertex < this.vertexCount) {
				@Pc(1312) val vertexLayer: GpuFloorLayer? = vertexLayers[vertex]
				if (vertexLayer != null) {
					vertexLayer.setVertexOpaque(vertex)
				}
				vertex++
			}
			@Pc(1333) var local1333: Int
			@Pc(1352) var local1352: Int
			for (@Pc(1329) x in 0 until super.width) {
				for (tileZ in 0 until super.length) {
					@Pc(1346) val triangleVertices: ShortArray? = this.tileVertexIndices[tileZ * super.width + x]
					if (triangleVertices != null) {
						@Pc(1350) var triangleIndex: Int = 0
						var triangleVertexOffset: Int = 0
						while (triangleVertices.size > triangleVertexOffset) {
							@Pc(1361) val vertexA: Int = triangleVertices[triangleVertexOffset++].toInt() and 0xFFFF
							@Pc(1368) val vertexB: Int = triangleVertices[triangleVertexOffset++].toInt() and 0xFFFF
							@Pc(1375) val vertexC: Int = triangleVertices[triangleVertexOffset++].toInt() and 0xFFFF
							@Pc(1379) val layerA: GpuFloorLayer? = vertexLayers[vertexA]
							@Pc(1383) val layerB: GpuFloorLayer? = vertexLayers[vertexB]
							@Pc(1387) val layerC: GpuFloorLayer? = vertexLayers[vertexC]
							@Pc(1389) var primaryLayer: GpuFloorLayer? = null
							if (layerA != null) {
								primaryLayer = layerA
								layerA.addTriangle(triangleIndex, tileZ, x)
							}
							if (layerB != null) {
								layerB.addTriangle(triangleIndex, tileZ, x)
								if (primaryLayer == null || primaryLayer.key > layerB.key) {
									primaryLayer = layerB
								}
							}
							if (layerC != null) {
								layerC.addTriangle(triangleIndex, tileZ, x)
								if (primaryLayer == null || primaryLayer.key > layerC.key) {
									primaryLayer = layerC
								}
							}
							if (primaryLayer != null) {
								if (layerA != null) {
									primaryLayer.setVertexOpaque(vertexA)
								}
								if (layerB != null) {
									primaryLayer.setVertexOpaque(vertexB)
								}
								if (layerC != null) {
									primaryLayer.setVertexOpaque(vertexC)
								}
								primaryLayer.addTriangle(triangleIndex, tileZ, x)
							}
							triangleIndex++
						}
					}
				}
			}
			stream.a()
			stream2.a()
			this.colourStream = this.nativeToolkit.createStream(false)
			this.colourStream!!.createFromSource(nativeHeapBuffer, 4, this.vertexCount * 4)
			this.vertexStream = this.nativeToolkit.createStream(false)
			this.vertexStream!!.createFromSource(nativeHeapBuffer2, vertexStride, vertexStride * this.vertexCount)
			if ((this.featureFlags and 0x7) == 0) {
				if (this.tileWaterDepths == null) {
					this.vertexDeclaration = this.nativeToolkit.createVertexDeclaration(arrayOf(VertexDeclarationComponent(arrayOf<VertexAttribute>(VertexDeclarationAction.POSITION, VertexDeclarationAction.TEXCOORD)), VertexDeclarationComponent(VertexDeclarationAction.COLOR)))
				} else {
					this.vertexDeclaration = this.nativeToolkit.createVertexDeclaration(arrayOf(VertexDeclarationComponent(arrayOf<VertexAttribute>(VertexDeclarationAction.POSITION, VertexDeclarationAction.TEXCOORD, VertexDeclarationAction.aClass69_4)), VertexDeclarationComponent(VertexDeclarationAction.COLOR)))
				}
			} else if (this.tileWaterDepths == null) {
				this.vertexDeclaration = this.nativeToolkit.createVertexDeclaration(arrayOf(VertexDeclarationComponent(arrayOf<VertexAttribute>(VertexDeclarationAction.POSITION, VertexDeclarationAction.TEXCOORD, VertexDeclarationAction.NORMAL_POINTER_ACTION)), VertexDeclarationComponent(VertexDeclarationAction.COLOR)))
			} else {
				this.vertexDeclaration = this.nativeToolkit.createVertexDeclaration(arrayOf(VertexDeclarationComponent(arrayOf<VertexAttribute>(VertexDeclarationAction.POSITION, VertexDeclarationAction.TEXCOORD, VertexDeclarationAction.aClass69_4, VertexDeclarationAction.NORMAL_POINTER_ACTION)), VertexDeclarationComponent(VertexDeclarationAction.COLOR)))
			}
			var activeLayerCount: Int = 0
			for (@Pc(1684) layerIndex in 0 until allLayers.size) {
				if (allLayers[layerIndex]!!.triangleCount > 0) {
					allLayers[activeLayerCount++] = allLayers[layerIndex]
				}
			}
			this.layers = arrayOfNulls<GpuFloorLayer>(activeLayerCount)
			@Pc(1713) val layerPointers: LongArray = LongArray(activeLayerCount)
			for (layerSlot in 0 until activeLayerCount) {
				@Pc(1721) val activeLayer: GpuFloorLayer? = allLayers[layerSlot]
				layerPointers[layerSlot] = activeLayer!!.key
				this.layers!![layerSlot] = activeLayer
				activeLayer.finishColours(this.vertexCount)
			}
			@Suppress("UNCHECKED_CAST")
			ArrayUtils.quicksort(this.layers as Array<Any?>?, layerPointers)
			if (this.hardShadows != null) {
				this.hardShadows!!.initializeShadowTiles()
			}
		}
		this.tileHeightOffsets = null
		this.layerTable = null
		this.tileBlendColours = null
		this.tileLayers = null
		this.normalZ = null
		this.normalY = null
		this.normalX = null
		this.tileColours = null
		this.shadowLevels = null
		this.tileWaterDepths = null
		this.tileOffsetsZ = null
		this.tileOffsetsX = null
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(Lclient!r;IIIIZ)Z")
	override fun method8002(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) x: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) z: Int): Boolean {
		if (this.hardShadows == null || shadow == null) {
			return false
		} else {
			@Pc(25) val shadowX: Int = (x - ((this.nativeToolkit.shadowOffsetX * heightOffset) shr 8)) shr this.nativeToolkit.shadowSizeShift
			@Pc(40) val shadowZ: Int = (z - ((this.nativeToolkit.shadowOffsetZ * heightOffset) shr 8)) shr this.nativeToolkit.shadowSizeShift
			return this.hardShadows!!.applyShadow(shadowX, shadow, shadowZ)
		}
	}

	@OriginalMember(owner = "client!qm", name = "ka", descriptor = "(III)V")
	override fun ka(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) shadowLevel: Int) {
		if (shadowLevel > (this.shadowLevels!![x][z].toInt() and 0xFF)) {
			this.shadowLevels!![x][z] = shadowLevel.toByte()
		}
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(III[[ZZII)V")
	override fun renderFloor(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) radius: Int, @OriginalArg(3) visibility: Array<BooleanArray?>?, @OriginalArg(4) belowLowestPlane: Boolean, @OriginalArg(5) orthoZoom: Int, @OriginalArg(6) skipFlags: Int) {
		this.renderGround(x, z, orthoZoom, belowLowestPlane, visibility, skipFlags, radius)
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(IIIZ[[ZIII)V")
	private fun renderGround(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) orthoZoom: Int, @OriginalArg(3) belowLowestPlane: Boolean, @OriginalArg(4) visibility: Array<BooleanArray?>?, @OriginalArg(6) skipFlags: Int, @OriginalArg(7) radius: Int) {
		if (this.layers == null) {
			return
		}
		@Pc(15) val diameter: Int = radius + radius + 1
		@Pc(19) val maxVisibleTiles: Int = diameter * diameter
		if (GpuFloorModel.visibleTileIndices.size < maxVisibleTiles) {
			GpuFloorModel.visibleTileIndices = IntArray(maxVisibleTiles)
		}
		@Pc(35) var minX: Int = x - radius
		@Pc(37) val originX: Int = minX
		if (minX < 0) {
			minX = 0
		}
		@Pc(46) var minZ: Int = z - radius
		@Pc(48) val originZ: Int = minZ
		if (minZ < 0) {
			minZ = 0
		}
		@Pc(56) var maxX: Int = radius + x
		if (super.width - 1 < maxX) {
			maxX = super.width - 1
		}
		@Pc(71) var maxZ: Int = z + radius
		if (super.length - 1 < maxZ) {
			maxZ = super.length - 1
		}
		GpuFloorModel.visibleTileCount = 0
		for (@Pc(90) i in minX..maxX) {
			@Pc(98) val visibilityRow: BooleanArray? = visibility!![i - originX]
			for (@Pc(100) j in minZ..maxZ) {
				if (visibilityRow!![j - originZ]) {
					GpuFloorModel.visibleTileIndices[GpuFloorModel.visibleTileCount++] = i + super.width * j
				}
			}
		}
		if (orthoZoom == -1) {
			this.nativeToolkit.setupSceneRenderState()
		} else {
			this.nativeToolkit.setOrthoZoom(orthoZoom.toFloat())
			this.nativeToolkit.setupOrthoSceneRenderState()
		}
		this.nativeToolkit.resetModelMatrix()
		this.nativeToolkit.toggleLights((this.featureFlags and 0x7) != 0)
		this.nativeToolkit.setActiveMaterial(false, false, -1)
		this.nativeToolkit.setStreamSource(this.vertexStream, 0)
		for (@Pc(192) k in 0 until this.layers!!.size) {
			this.layers!![k]!!.render(GpuFloorModel.visibleTileIndices, GpuFloorModel.visibleTileCount)
		}
		@Pc(213) val modelMatrix: NativeMatrix = this.nativeToolkit.getModelMatrix()
		modelMatrix.applyTranslation(0, -1, 0)
		this.nativeToolkit.commitModelMatrix()
		if (!this.lights.isEmpty()) {
			@Pc(231) val fogColour: Int = this.nativeToolkit.fogColorRgb
			@Pc(235) val fogDistance: Int = this.nativeToolkit.fogDistance
			this.nativeToolkit.L(0, fogDistance, this.nativeToolkit.fogOffset)
			this.nativeToolkit.toggleLights(false)
			this.nativeToolkit.setDepthMaskEnabled(false)
			this.nativeToolkit.toggleBlending(128)
			this.nativeToolkit.setActiveMaterial(false, false, -2)
			this.nativeToolkit.setTextureSampler(this.nativeToolkit.defaultTexture)
			this.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineMode.MODULATE)
			this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR)
			this.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.PRIMARY_COLOR)
			@Pc(297) var node: Node? = this.lights.first()
			while (node != null) {
				@Pc(302) val light: GpuFloorLight = node as GpuFloorLight
				light.render(z, radius, visibility, x)
				node = this.lights.next()
			}
			this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
			this.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
			this.nativeToolkit.setTextureSampler(null)
			this.nativeToolkit.L(fogColour, fogDistance, this.nativeToolkit.fogOffset)
		}
		if (this.hardShadows != null) {
			this.nativeToolkit.setStreamSource(this.vertexStream, 0)
			this.nativeToolkit.setStreamSource(this.colourStream, 1)
			this.nativeToolkit.setVertexDeclaration(this.vertexDeclaration)
			this.hardShadows!!.renderShadowOverlay(radius, x, belowLowestPlane, visibility, z)
		}
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(Lclient!ab;[I)V")
	override fun addLight(@OriginalArg(0) pointLight: PointLight?, @OriginalArg(1) tileMask: IntArray?) {
		this.lights.addLast(GpuFloorLight(this.nativeToolkit, this, pointLight, tileMask))
	}

	@OriginalMember(owner = "client!qm", name = "U", descriptor = "(II[I[I[I[I[I[I[I[IIIIZ)V")
	override fun U(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) offsetsX: IntArray?, @OriginalArg(3) heightOffsets: IntArray?, @OriginalArg(4) offsetsZ: IntArray?, @OriginalArg(5) waterDepths: IntArray?, @OriginalArg(6) colours: IntArray?, @OriginalArg(7) blendedColours: IntArray?, @OriginalArg(8) materialIds: IntArray?, @OriginalArg(9) textureScales: IntArray?, @OriginalArg(10) waterColour: Int, @OriginalArg(11) waterDepth: Int, @OriginalArg(12) waterBias: Int, @OriginalArg(13) hasShadow: Boolean) {
		@Pc(9) val textureSource: MaterialProvider? = this.nativeToolkit.materialProvider
		if (waterDepths != null && this.tileWaterDepths == null) {
			this.tileWaterDepths = Array(super.width) { arrayOfNulls<IntArray>(super.length) }
		}
		if (heightOffsets != null && this.tileHeightOffsets == null) {
			this.tileHeightOffsets = Array(super.width) { arrayOfNulls<IntArray>(super.length) }
		}
		this.tileOffsetsX!![x][z] = offsetsX
		this.tileOffsetsZ!![x][z] = offsetsZ
		this.tileColours!![x][z] = colours
		this.tileBlendColours!![x][z] = blendedColours
		if (this.tileWaterDepths != null) {
			this.tileWaterDepths!![x][z] = waterDepths
		}
		if (this.tileHeightOffsets != null) {
			this.tileHeightOffsets!![x][z] = heightOffsets
		}
		@Pc(92) val tileVertexLayers: Array<GpuFloorLayer?> = arrayOfNulls<GpuFloorLayer>(colours!!.size)
		this.tileLayers!![x][z] = tileVertexLayers
		for (@Pc(94) i in 0 until colours.size) {
			@Pc(100) var materialId: Int = materialIds!![i]
			@Pc(104) var textureScale: Int = textureScales!![i]
			if ((this.featureFlags and 0x20) != 0 && materialId != -1 && textureSource!!.getMetrics(materialId)!!.detailsOnly) {
				textureScale = 128
				materialId = -1
			}
			@Pc(146) val layerKey: Long = materialId.toLong() or (textureScale shl 14).toLong() or (waterDepth.toLong() shl 42) or (waterBias.toLong() shl 48) or (waterColour.toLong() shl 28)
			@Pc(152) var node: Node?
			node = this.layerTable!!.get(layerKey)
			while (node != null) {
				@Pc(157) val layer: GpuFloorLayer = node as GpuFloorLayer
				if (materialId == layer.materialId && layer.textureScale == textureScale.toFloat() && layer.waterFogColour == waterColour && layer.waterDepth == waterDepth && layer.waterIntensity == waterBias) {
					break
				}
				node = this.layerTable!!.nextWithKey()
			}
			if (node == null) {
				tileVertexLayers[i] = GpuFloorLayer(this, materialId, textureScale, waterColour, waterDepth, waterBias)
				this.layerTable!!.put(layerKey, tileVertexLayers[i])
			} else {
				tileVertexLayers[i] = node as GpuFloorLayer
			}
		}
		if (hasShadow) {
			this.tileFlags[x][z] = (this.tileFlags[x][z].toInt() or 0x1).toByte()
		}
		if (this.maxTileVertexCount < colours.size) {
			this.maxTileVertexCount = colours.size
		}
		this.totalTileVertexCount += colours.size
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(III[[ZZI)V")
	override fun renderTiles(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) radius: Int, @OriginalArg(3) visibility: Array<BooleanArray?>?, @OriginalArg(4) belowLowestPlane: Boolean, @OriginalArg(5) skipFlags: Int) {
		this.renderGround(x, z, -1, belowLowestPlane, visibility, skipFlags, radius)
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(II[I[I[I[I[I[I[I[I[I[I[IIIIZ)V")
	override fun addTile(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) offsetsX: IntArray?, @OriginalArg(3) heightOffsets: IntArray?, @OriginalArg(4) offsetsZ: IntArray?, @OriginalArg(5) waterDepths: IntArray?, @OriginalArg(6) faceA: IntArray?, @OriginalArg(7) faceB: IntArray?, @OriginalArg(8) faceC: IntArray?, @OriginalArg(9) colours: IntArray?, @OriginalArg(10) blendedColours: IntArray?, @OriginalArg(11) materialIds: IntArray?, @OriginalArg(12) textureScales: IntArray?, @OriginalArg(13) waterColour: Int, @OriginalArg(14) waterDepth: Int, @OriginalArg(15) waterBias: Int) {
		@Pc(8) val length: Int = colours!!.size
		@Pc(13) val expandedOffsetsX: IntArray = IntArray(length * 3)
		@Pc(18) val expandedOffsetsZ: IntArray = IntArray(length * 3)
		@Pc(23) val expandedColours: IntArray = IntArray(length * 3)
		@Pc(28) val expandedBlendedColours: IntArray = IntArray(length * 3)
		@Pc(33) val expandedMaterialIds: IntArray = IntArray(length * 3)
		@Pc(38) val expandedTextureScales: IntArray = IntArray(length * 3)
		@Pc(47) val expandedHeightOffsets: IntArray? = if (heightOffsets == null) null else IntArray(length * 3)
		@Pc(56) val expandedWaterDepths: IntArray? = if (waterDepths == null) null else IntArray(length * 3)
		@Pc(58) var vertexIndex: Int = 0
		for (@Pc(60) i in 0 until length) {
			@Pc(66) val vertexA: Int = faceA!![i]
			@Pc(70) val vertexB: Int = faceB!![i]
			@Pc(74) val vertexC: Int = faceC!![i]
			expandedOffsetsX[vertexIndex] = offsetsX!![vertexA]
			expandedOffsetsZ[vertexIndex] = offsetsZ!![vertexA]
			expandedColours[vertexIndex] = colours[i]
			expandedMaterialIds[vertexIndex] = materialIds!![i]
			expandedTextureScales[vertexIndex] = textureScales!![i]
			expandedBlendedColours[vertexIndex] = if (blendedColours == null) colours[i] else blendedColours[i]
			if (heightOffsets != null) {
				expandedHeightOffsets!![vertexIndex] = heightOffsets[vertexA]
			}
			if (waterDepths != null) {
				expandedWaterDepths!![vertexIndex] = waterDepths[vertexA]
			}
			vertexIndex++
			expandedOffsetsX[vertexIndex] = offsetsX[vertexB]
			expandedOffsetsZ[vertexIndex] = offsetsZ[vertexB]
			expandedColours[vertexIndex] = colours[i]
			expandedMaterialIds[vertexIndex] = materialIds[i]
			expandedTextureScales[vertexIndex] = textureScales[i]
			expandedBlendedColours[vertexIndex] = if (blendedColours == null) colours[i] else blendedColours[i]
			if (heightOffsets != null) {
				expandedHeightOffsets!![vertexIndex] = heightOffsets[vertexB]
			}
			if (waterDepths != null) {
				expandedWaterDepths!![vertexIndex] = waterDepths[vertexB]
			}
			vertexIndex++
			expandedOffsetsX[vertexIndex] = offsetsX[vertexC]
			expandedOffsetsZ[vertexIndex] = offsetsZ[vertexC]
			expandedColours[vertexIndex] = colours[i]
			expandedMaterialIds[vertexIndex] = materialIds[i]
			expandedTextureScales[vertexIndex] = textureScales[i]
			expandedBlendedColours[vertexIndex] = if (blendedColours == null) colours[i] else blendedColours[i]
			if (heightOffsets != null) {
				expandedHeightOffsets!![vertexIndex] = heightOffsets[vertexC]
			}
			if (waterDepths != null) {
				expandedWaterDepths!![vertexIndex] = waterDepths[vertexC]
			}
			vertexIndex++
		}
		this.U(x, z, expandedOffsetsX, expandedHeightOffsets, expandedOffsetsZ, expandedWaterDepths, expandedColours, expandedBlendedColours, expandedMaterialIds, expandedTextureScales, waterColour, waterDepth, waterBias, false)
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(IIIIIII[[Z)V")
	override fun drawMinimap(@OriginalArg(3) x1: Int, @OriginalArg(4) y1: Int, @OriginalArg(5) x2: Int, @OriginalArg(6) y2: Int, @OriginalArg(7) visibility: Array<BooleanArray?>?) {
		if (this.totalTileVertexCount <= 0) {
			return
		}
		@Pc(16) val gpuIndexBuffer: GpuIndexBuffer = this.nativeToolkit.getOrResizeIndexBuffer(this.indexCount)
		@Pc(18) var elementCount: Int = 0
		@Pc(20) var minVertexIndex: Int = 32767
		@Pc(22) var maxVertexIndex: Int = -32768
		for (@Pc(24) i in 0 until 4) {
			elementCount = 0
			@Pc(33) val buffer: Buffer? = gpuIndexBuffer.lock()
			if (buffer != null) {
				@Pc(41) val stream: Stream = this.nativeToolkit.wrapBuffer(buffer)
				@Pc(45) var z: Int
				@Pc(54) var tileIndex: Int
				@Pc(56) var x: Int
				@Pc(74) var vertexIndices: ShortArray?
				@Pc(78) var k: Int
				@Pc(86) var vertexIndex: Int
				if (Stream.c()) {
					z = y1
					while (z < y2) {
						tileIndex = z * super.width + x1
						x = x1
						while (x < x2) {
							if (visibility!![x - x1]!![z - y1]) {
								vertexIndices = this.tileVertexIndices[tileIndex]
								if (vertexIndices != null) {
									k = 0
									while (k < vertexIndices.size) {
										vertexIndex = vertexIndices[k].toInt() and 0xFFFF
										stream.d(vertexIndex)
										if (vertexIndex > maxVertexIndex) {
											maxVertexIndex = vertexIndex
										}
										elementCount++
										if (vertexIndex < minVertexIndex) {
											minVertexIndex = vertexIndex
										}
										k++
									}
								}
							}
							tileIndex++
							x++
						}
						z++
					}
				} else {
					z = y1
					while (z < y2) {
						tileIndex = x1 + z * super.width
						x = x1
						while (x < x2) {
							if (visibility!![x - x1]!![z - y1]) {
								vertexIndices = this.tileVertexIndices[tileIndex]
								if (vertexIndices != null) {
									k = 0
									while (k < vertexIndices.size) {
										vertexIndex = vertexIndices[k].toInt() and 0xFFFF
										stream.a(vertexIndex)
										if (maxVertexIndex < vertexIndex) {
											maxVertexIndex = vertexIndex
										}
										elementCount++
										if (vertexIndex < minVertexIndex) {
											minVertexIndex = vertexIndex
										}
										k++
									}
								}
							}
							tileIndex++
							x++
						}
						z++
					}
				}
				stream.a()
				if (gpuIndexBuffer.unlock()) {
					break
				}
			}
		}
		if (elementCount <= 0) {
			return
		}
		this.nativeToolkit.setOrthoMatrixMode()
		this.nativeToolkit.toggleFog(false)
		this.nativeToolkit.toggleLights(false)
		this.nativeToolkit.toggleDepthTest(false)
		this.nativeToolkit.setDepthMaskEnabled(false)
		this.nativeToolkit.toggleBlending(0)
		this.nativeToolkit.setActiveMaterial(false, false, -2)
		this.nativeToolkit.setTextureSampler(null)
		@Pc(260) val modelMatrix: NativeMatrix = this.nativeToolkit.getModelMatrix()
		@Pc(265) val projectionMatrix: FloatArray = this.nativeToolkit.getOrthoProjectionMatrix()
		projectionMatrix[6] = 0.0f
		projectionMatrix[13] = 1.0f - (0.toFloat() + (y2 * 1024).toFloat() / 128.0f) / this.nativeToolkit.surfaceHeight.toFloat()
		projectionMatrix[11] = 0.0f
		projectionMatrix[0] = 1024.toFloat() / (this.nativeToolkit.surfaceWidth.toFloat() * super.tileUnits.toFloat() * 128.0f)
		projectionMatrix[14] = -this.minHeight / (this.maxHeight - this.minHeight)
		projectionMatrix[2] = 0.0f
		projectionMatrix[15] = 1.0f
		projectionMatrix[5] = 1024.toFloat() / (super.tileUnits.toFloat() * 128.0f * this.nativeToolkit.surfaceHeight.toFloat())
		projectionMatrix[4] = 0.0f
		projectionMatrix[3] = 0.0f
		projectionMatrix[8] = 0.0f
		projectionMatrix[7] = 0.0f
		projectionMatrix[10] = 1.0f / (this.maxHeight - this.minHeight)
		projectionMatrix[9] = 0.0f
		projectionMatrix[12] = -1.0f - ((x1 * 1024).toFloat() / 128.0f - 0.toFloat()) / this.nativeToolkit.surfaceWidth.toFloat()
		projectionMatrix[1] = 0.0f
		modelMatrix.setRotation(0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f)
		this.nativeToolkit.applyOrthoMatrixMode()
		this.nativeToolkit.commitModelMatrix()
		if ((this.featureFlags and 0x7) == 0) {
			this.nativeToolkit.toggleLights(false)
		} else {
			this.nativeToolkit.toggleLights(true)
			this.nativeToolkit.setLightPosition()
		}
		this.nativeToolkit.toggleMultisampling(false)
		this.nativeToolkit.setStreamSource(this.vertexStream, 0)
		this.nativeToolkit.setStreamSource(this.colourStream, 1)
		this.nativeToolkit.setVertexDeclaration(this.vertexDeclaration)
		this.nativeToolkit.drawIndexedPrimitive(PrimitiveType.aClass134_6, elementCount / 3, gpuIndexBuffer, minVertexIndex, 0, maxVertexIndex + 1 - minVertexIndex)
		this.nativeToolkit.toggleMultisampling(true)
	}

	companion object {
		@OriginalMember(owner = "client!an", name = "k", descriptor = "[I")
		@JvmField
		var visibleTileIndices: IntArray = IntArray(1)

		@OriginalMember(owner = "client!sm", name = "q", descriptor = "[I")
		@JvmField
		var shadowVerticesZ: IntArray = IntArray(1)

		@OriginalMember(owner = "client!hw", name = "d", descriptor = "[I")
		@JvmField
		var shadowVerticesX: IntArray = IntArray(1)

		@OriginalMember(owner = "client!bw", name = "v", descriptor = "I")
		@JvmField
		var visibleTileCount: Int = 0
	}
}
