package com.jagex.game.runetek5.scene.ground

import com.jagex.game.runetek5.cache.node.impl.ShortNode
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.config.material.MaterialProvider
import jaclib.memory.Stream
import jaclib.memory.heap.NativeHeapBuffer
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.cache.node.GlGroundLayer
import com.jagex.game.runetek5.cache.node.lru.GlShadow
import com.jagex.game.runetek5.graphics.light.LightGL
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlElementsArrayBuffer
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer
import com.jagex.game.runetek5.graphics.gl.GlShadowMap
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import com.jagex.game.runetek5.utils.ColorUtil
import com.jagex.game.runetek5.utils.ArrayUtils
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField

@OriginalClass("client!ed")
class GlGround : Ground {

	@OriginalMember(owner = "client!ed", name = "w", descriptor = "I")
	private var vertexCount: Int = 0

	@OriginalMember(owner = "client!ed", name = "C", descriptor = "[[[I")
	private var tileWaterDepths: Array<Array<IntArray?>>? = null

	@OriginalMember(owner = "client!ed", name = "H", descriptor = "I")
	private var indexCount: Int = 0

	@OriginalMember(owner = "client!ed", name = "Y", descriptor = "Lclient!nh;")
	private var colorBuffer: OpenGlArrayDrawBuffer? = null

	@OriginalMember(owner = "client!ed", name = "Z", descriptor = "Lclient!nh;")
	@JvmField
	var normalsBuffer: OpenGlArrayDrawBuffer? = null

	@OriginalMember(owner = "client!ed", name = "ab", descriptor = "Lclient!nh;")
	@JvmField
	var vertexBuffer: OpenGlArrayDrawBuffer? = null

	@OriginalMember(owner = "client!ed", name = "bb", descriptor = "Lclient!nh;")
	@JvmField
	var textureCoordinatesBuffer: OpenGlArrayDrawBuffer? = null

	@OriginalMember(owner = "client!ed", name = "db", descriptor = "Lclient!hca;")
	private var dataArrayBuffer: ArrayBufferGL? = null

	@OriginalMember(owner = "client!ed", name = "eb", descriptor = "I")
	private var maxTileVertexCount: Int = 0

	@OriginalMember(owner = "client!ed", name = "gb", descriptor = "[Lclient!jn;")
	private var layers: Array<GlGroundLayer?>? = null

	@OriginalMember(owner = "client!ed", name = "ib", descriptor = "I")
	private var totalTileVertexCount: Int = 0

	@OriginalMember(owner = "client!ed", name = "G", descriptor = "Lclient!uh;")
	private val lights: Deque = Deque()

	@OriginalMember(owner = "client!ed", name = "W", descriptor = "Lclient!qo;")
	@JvmField
	val glToolkit: GlToolkit

	@OriginalMember(owner = "client!ed", name = "O", descriptor = "I")
	private val quarterTileScale: Int

	@OriginalMember(owner = "client!ed", name = "v", descriptor = "[[[I")
	private var tileBlendColours: Array<Array<IntArray?>>?

	@OriginalMember(owner = "client!ed", name = "q", descriptor = "[[B")
	private val tileFlags: Array<ByteArray>

	@OriginalMember(owner = "client!ed", name = "S", descriptor = "[[[I")
	@JvmField
	var tileOffsetsX: Array<Array<IntArray?>>?

	@OriginalMember(owner = "client!ed", name = "D", descriptor = "[[[I")
	@JvmField
	var tileColours: Array<Array<IntArray?>>?

	@OriginalMember(owner = "client!ed", name = "jb", descriptor = "[[F")
	private var normalX: Array<FloatArray>?

	@OriginalMember(owner = "client!ed", name = "t", descriptor = "[[[Lclient!jn;")
	private var tileLayers: Array<Array<Array<GlGroundLayer?>?>>?

	@OriginalMember(owner = "client!ed", name = "I", descriptor = "[[[I")
	private var tileHeightOffsets: Array<Array<IntArray?>>?

	@OriginalMember(owner = "client!ed", name = "fb", descriptor = "[[F")
	private var normalY: Array<FloatArray>?

	@OriginalMember(owner = "client!ed", name = "X", descriptor = "[[B")
	private var shadowLevels: Array<ByteArray>?

	@OriginalMember(owner = "client!ed", name = "R", descriptor = "[[S")
	@JvmField
	val tileVertexIndices: Array<ShortArray?>

	@OriginalMember(owner = "client!ed", name = "K", descriptor = "I")
	private val quarterTileUnits: Int

	@OriginalMember(owner = "client!ed", name = "k", descriptor = "[[[I")
	@JvmField
	var tileOffsetsZ: Array<Array<IntArray?>>?

	@OriginalMember(owner = "client!ed", name = "hb", descriptor = "[[F")
	private var normalZ: Array<FloatArray>?

	@OriginalMember(owner = "client!ed", name = "l", descriptor = "I")
	@JvmField
	val featureFlags: Int

	@OriginalMember(owner = "client!ed", name = "cb", descriptor = "Lclient!eq;")
	private var layerTable: IterableHashTable? = null

	@OriginalMember(owner = "client!ed", name = "Q", descriptor = "Lclient!mha;")
	private var shadowMap: GlShadowMap? = null

	@OriginalMember(owner = "client!ed", name = "<init>", descriptor = "(Lclient!qo;IIII[[I[[II)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) groundFlags: Int, @OriginalArg(2) featureFlags: Int, @OriginalArg(3) width: Int, @OriginalArg(4) length: Int, @OriginalArg(5) tileHeights: Array<IntArray?>?, @OriginalArg(6) normalHeights: Array<IntArray?>?, @OriginalArg(7) scaling: Int) : super(width, length, scaling, tileHeights) {
		this.glToolkit = glToolkit!!
		this.quarterTileScale = super.tileScale - 2
		this.tileBlendColours = Array(width) { arrayOfNulls<IntArray>(length) }
		this.tileFlags = Array(width) { ByteArray(length) }
		this.tileOffsetsX = Array(width) { arrayOfNulls<IntArray>(length) }
		this.tileColours = Array(width) { arrayOfNulls<IntArray>(length) }
		this.normalX = Array(super.width + 1) { FloatArray(super.length + 1) }
		this.tileLayers = Array(width) { arrayOfNulls<Array<GlGroundLayer?>>(length) }
		this.tileHeightOffsets = Array(width) { arrayOfNulls<IntArray>(length) }
		this.normalY = Array(super.width + 1) { FloatArray(super.length + 1) }
		this.shadowLevels = Array(width + 1) { ByteArray(length + 1) }
		this.tileVertexIndices = arrayOfNulls<ShortArray>(length * width)
		this.quarterTileUnits = 0x1 shl this.quarterTileScale
		this.tileOffsetsZ = Array(width) { arrayOfNulls<IntArray>(length) }
		this.normalZ = Array(super.width + 1) { FloatArray(super.length + 1) }
		this.featureFlags = featureFlags
		@Pc(115) var i: Int = 1
		while (super.length > i) {
			@Pc(119) var j: Int = 1
			while (super.width > j) {
				@Pc(138) val heightDeltaX: Int = normalHeights!![j + 1]!![i] - normalHeights[j - 1]!![i]
				@Pc(155) val heightDeltaZ: Int = normalHeights[j]!![i + 1] - normalHeights[j]!![i - 1]
				@Pc(174) val normalScale: Float = (1.0 / kotlin.math.sqrt((heightDeltaZ * heightDeltaZ + scaling * scaling * 4 + heightDeltaX * heightDeltaX).toDouble())).toFloat()
				this.normalX!![j][i] = normalScale * heightDeltaX.toFloat()
				this.normalY!![j][i] = normalScale * (-scaling * 2).toFloat()
				this.normalZ!![j][i] = heightDeltaZ.toFloat() * normalScale
				j++
			}
			i++
		}
		this.layerTable = IterableHashTable(128)
		if ((this.featureFlags and 0x10) != 0) {
			this.shadowMap = GlShadowMap(this.glToolkit, this)
		}
	}

	@OriginalMember(owner = "client!ed", name = "wa", descriptor = "(Lclient!r;IIIIZ)V")
	override fun wa(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) x: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) z: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean) {
		if (this.shadowMap != null && shadow != null) {
			@Pc(23) val shadowX: Int = (x - ((this.glToolkit.sunSlopeX * heightOffset) shr 8)) shr this.glToolkit.shadowScaleShift
			@Pc(38) val shadowZ: Int = (z - ((heightOffset * this.glToolkit.sunSlopeZ) shr 8)) shr this.glToolkit.shadowScaleShift
			this.shadowMap!!.removeShadow(shadow, shadowX, shadowZ)
		}
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(II[I[I[I[I[I[I[I[I[I[I[IIIIZ)V")
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

	@OriginalMember(owner = "client!ed", name = "YA", descriptor = "()V")
	override fun YA() {
		if (this.totalTileVertexCount <= 0) {
			this.shadowMap = null
		} else {
			@Pc(22) val smoothedShadowLevels: Array<ByteArray> = Array(super.width + 1) { ByteArray(super.length + 1) }
			@Pc(28) var local28: Int
			for (@Pc(24) i in 1 until super.width) {
				for (tileZ in 1 until super.length) {
					smoothedShadowLevels[i][tileZ] = ((this.shadowLevels!![i][tileZ + 1].toInt() shr 3) + (this.shadowLevels!![i][tileZ - 1].toInt() shr 2) + (this.shadowLevels!![i + 1][tileZ].toInt() shr 3) + (this.shadowLevels!![i - 1][tileZ].toInt() shr 2) + (this.shadowLevels!![i][tileZ].toInt() shr 1)).toByte()
				}
			}
			this.layers = arrayOfNulls<GlGroundLayer>(this.layerTable!!.size())
			@Suppress("UNCHECKED_CAST")
			this.layerTable!!.toArray(this.layers as Array<Node?>?)
			for (layerIndex in 0 until this.layers!!.size) {
				this.layers!![layerIndex]!!.initColours(this.totalTileVertexCount)
			}
			@Pc(136) var vertexStride: Int = 24
			if (this.tileWaterDepths != null) {
				vertexStride += 4
			}
			if ((this.featureFlags and 0x7) != 0) {
				vertexStride += 12
			}
			@Pc(156) val nativeHeapBuffer: NativeHeapBuffer = this.glToolkit.nativeHeap!!.a(this.totalTileVertexCount * vertexStride, false)
			@Pc(161) val stream: Stream = Stream(nativeHeapBuffer)
			@Pc(165) val glGroundLayers: Array<GlGroundLayer?> = arrayOfNulls<GlGroundLayer>(this.totalTileVertexCount)
			@Pc(172) var hashCapacity: Int = Utils.floorPowerOfTwo(this.totalTileVertexCount / 4)
			if (hashCapacity < 1) {
				hashCapacity = 1
			}
			@Pc(184) val iterableHashTable: IterableHashTable = IterableHashTable(hashCapacity)
			@Pc(188) val glGroundLayers2: Array<GlGroundLayer?> = arrayOfNulls<GlGroundLayer>(this.maxTileVertexCount)
			@Pc(194) var local194: Int
			for (@Pc(190) j in 0 until super.width) {
				for (tileZ in 0 until super.length) {
					if (this.tileColours!![j][tileZ] != null) {
						@Pc(210) val glGroundLayers3: Array<GlGroundLayer?>? = this.tileLayers!![j][tileZ]
						@Pc(217) val offsetsX: IntArray? = this.tileOffsetsX!![j][tileZ]
						@Pc(224) val offsetsZ: IntArray? = this.tileOffsetsZ!![j][tileZ]
						@Pc(231) var blendedColours: IntArray? = this.tileBlendColours!![j][tileZ]
						@Pc(238) val colours: IntArray? = this.tileColours!![j][tileZ]
						@Pc(250) val heightOffsets: IntArray? = if (this.tileHeightOffsets == null) null else this.tileHeightOffsets!![j][tileZ]
						if (blendedColours == null) {
							blendedColours = colours
						}
						@Pc(266) val waterDepths: IntArray? = if (this.tileWaterDepths == null) null else this.tileWaterDepths!![j][tileZ]
						@Pc(273) val normalX00: Float = this.normalX!![j][tileZ]
						@Pc(280) val normalY00: Float = this.normalY!![j][tileZ]
						@Pc(287) val normalZ00: Float = this.normalZ!![j][tileZ]
						@Pc(296) val normalX01: Float = this.normalX!![j][tileZ + 1]
						@Pc(305) val normalY01: Float = this.normalY!![j][tileZ + 1]
						@Pc(314) val normalZ01: Float = this.normalZ!![j][tileZ + 1]
						@Pc(325) val normalX11: Float = this.normalX!![j + 1][tileZ + 1]
						@Pc(336) val normalY11: Float = this.normalY!![j + 1][tileZ + 1]
						@Pc(347) val normalZ11: Float = this.normalZ!![j + 1][tileZ + 1]
						@Pc(356) val normalX10: Float = this.normalX!![j + 1][tileZ]
						@Pc(365) val normalY10: Float = this.normalY!![j + 1][tileZ]
						@Pc(374) val normalZ10: Float = this.normalZ!![j + 1][tileZ]
						@Pc(382) val shadow00: Int = smoothedShadowLevels[j][tileZ].toInt() and 0xFF
						@Pc(392) val shadow01: Int = smoothedShadowLevels[j][tileZ + 1].toInt() and 0xFF
						@Pc(404) val shadow11: Int = smoothedShadowLevels[j + 1][tileZ + 1].toInt() and 0xFF
						@Pc(414) val shadow10: Int = smoothedShadowLevels[j + 1][tileZ].toInt() and 0xFF
						@Pc(416) var uniqueLayerCount: Int = 0
						@Pc(426) var local426: Int
						label337@ for (@Pc(418) k in 0 until colours!!.size) {
							@Pc(424) val glGroundLayer: GlGroundLayer? = glGroundLayers3!![k]
							for (uniqueIndex in 0 until uniqueLayerCount) {
								if (glGroundLayers2[uniqueIndex] === glGroundLayer) {
									continue@label337
								}
							}
							glGroundLayers2[uniqueLayerCount++] = glGroundLayer
						}
						@Pc(463) val vertexIndices: ShortArray = ShortArray(colours.size)
						this.tileVertexIndices[super.width * tileZ + j] = vertexIndices
						for (tileVertex in 0 until colours.size) {
							@Pc(476) val vertexX: Int = (j shl super.tileScale) + offsetsX!![tileVertex]
							@Pc(486) val vertexZ: Int = (tileZ shl super.tileScale) + offsetsZ!![tileVertex]
							@Pc(491) val gridX: Int = vertexX shr this.quarterTileScale
							@Pc(496) val gridZ: Int = vertexZ shr this.quarterTileScale
							@Pc(500) val colour: Int = colours[tileVertex]
							@Pc(504) val blendedColour: Int = blendedColours!![tileVertex]
							@Pc(512) val heightOffset: Int = if (heightOffsets == null) 0 else heightOffsets[tileVertex]
							@Pc(530) val vertexKey: Long = (gridX shl 16).toLong() or (colour.toLong() shl 32) or (blendedColour.toLong() shl 48) or gridZ.toLong()
							@Pc(534) val offsetX: Int = offsetsX[tileVertex]
							@Pc(538) val offsetZ: Int = offsetsZ[tileVertex]
							@Pc(540) val baseLightness: Byte = 74
							@Pc(542) var rgb: Int = 0
							@Pc(589) var vertexNormalX: Float
							@Pc(585) var vertexNormalY: Float
							@Pc(587) var vertexNormalZ: Float
							@Pc(626) var local626: Float
							@Pc(593) var lightness: Int
							if (offsetX == 0 && offsetZ == 0) {
								vertexNormalY = normalY00
								vertexNormalX = normalX00
								lightness = baseLightness - shadow00
								vertexNormalZ = normalZ00
							} else if (offsetX == 0 && offsetZ == super.tileUnits) {
								vertexNormalZ = normalZ01
								vertexNormalY = normalY01
								lightness = baseLightness - shadow01
								vertexNormalX = normalX01
							} else if (super.tileUnits == offsetX && offsetZ == super.tileUnits) {
								vertexNormalX = normalX11
								vertexNormalY = normalY11
								vertexNormalZ = normalZ11
								lightness = baseLightness - shadow11
							} else if (super.tileUnits == offsetX && offsetZ == 0) {
								vertexNormalY = normalY10
								vertexNormalZ = normalZ10
								vertexNormalX = normalX10
								lightness = baseLightness - shadow10
							} else {
								@Pc(602) val fracX: Float = offsetX.toFloat() / super.tileUnits.toFloat()
								@Pc(609) val fracZ: Float = offsetZ.toFloat() / super.tileUnits.toFloat()
								@Pc(618) val lerpNormalX0: Float = (normalX10 - normalX00) * fracX + normalX00
								val lerpNormalY0: Float = (normalY10 - normalY00) * fracX + normalY00
								@Pc(635) val lerpNormalZ0: Float = fracX * (normalZ10 - normalZ00) + normalZ00
								@Pc(643) val lerpNormalX1: Float = fracX * (normalX11 - normalX01) + normalX01
								@Pc(652) val lerpNormalY1: Float = normalY01 + (normalY11 - normalY01) * fracX
								@Pc(660) val lerpNormalZ1: Float = (normalZ11 - normalZ01) * fracX + normalZ01
								vertexNormalY = lerpNormalY0 + (lerpNormalY1 - lerpNormalY0) * fracZ
								vertexNormalX = fracZ * (lerpNormalX1 - lerpNormalX0) + lerpNormalX0
								vertexNormalZ = lerpNormalZ0 + (lerpNormalZ1 - lerpNormalZ0) * fracZ
								@Pc(698) val lerpShadow0: Int = (((shadow10 - shadow00) * offsetX) shr super.tileScale) + shadow00
								@Pc(710) val lerpShadow1: Int = ((offsetX * (shadow11 - shadow01)) shr super.tileScale) + shadow01
								lightness = baseLightness - lerpShadow0 - (((lerpShadow1 - lerpShadow0) * offsetZ) shr super.tileScale)
							}
							@Pc(763) var lightIntensity: Float = 1.0f
							if (colour != -1) {
								@Pc(775) var adjustedLightness: Int = ((colour and 0x7F) * lightness) shr 7
								if (adjustedLightness < 2) {
									adjustedLightness = 2
								} else if (adjustedLightness > 126) {
									adjustedLightness = 126
								}
								if ((this.featureFlags and 0x7) == 0) {
									lightIntensity = this.glToolkit.sunDirection[2] * vertexNormalZ + vertexNormalX * this.glToolkit.sunDirection[0] + vertexNormalY * this.glToolkit.sunDirection[1]
									lightIntensity = this.glToolkit.sunBrightness + (if (lightIntensity > 0.0f) this.glToolkit.sunColorIntensity else this.glToolkit.antiSunColorIntensity) * lightIntensity
								}
								rgb = ColorUtil.hslToRgb!![(colour and 0xFF80) or adjustedLightness]
							}
							@Pc(849) var node: Node? = null
							if ((vertexX and (this.quarterTileUnits - 1)) == 0 && ((this.quarterTileUnits - 1) and vertexZ) == 0) {
								node = iterableHashTable.get(vertexKey)
							}
							@Pc(882) var vertexIndex: Int
							@Pc(937) var local937: Int
							if (node == null) {
								var blendedRgb: Int
								if (colour == blendedColour) {
									blendedRgb = rgb
								} else {
									@Pc(915) var blendedLightness: Int = ((blendedColour and 0x7F) * lightness) shr 7
									if (blendedLightness < 2) {
										blendedLightness = 2
									} else if (blendedLightness > 126) {
										blendedLightness = 126
									}
									blendedRgb = ColorUtil.hslToRgb!![(blendedColour and 0xFF80) or blendedLightness]
									if ((this.featureFlags and 0x7) == 0) {
										var blendedLightIntensity: Float = this.glToolkit.sunDirection[0] * vertexNormalX + vertexNormalY * this.glToolkit.sunDirection[1] + vertexNormalZ * this.glToolkit.sunDirection[2]
										blendedLightIntensity = (if (lightIntensity > 0.0f) this.glToolkit.sunColorIntensity else this.glToolkit.antiSunColorIntensity) * lightIntensity + this.glToolkit.sunBrightness
										@Pc(993) var red: Int = (blendedRgb shr 16) and 0xFF
										@Pc(999) var green: Int = (blendedRgb shr 8) and 0xFF
										@Pc(1003) var blue: Int = blendedRgb and 0xFF
										red = (red.toFloat() * blendedLightIntensity).toInt()
										green = (green.toFloat() * blendedLightIntensity).toInt()
										if (red < 0) {
											red = 0
										} else if (red > 255) {
											red = 255
										}
										if (green < 0) {
											green = 0
										} else if (green > 255) {
											green = 255
										}
										blue = (blue.toFloat() * blendedLightIntensity).toInt()
										if (blue < 0) {
											blue = 0
										} else if (blue > 255) {
											blue = 255
										}
										blendedRgb = (red shl 16) or (green shl 8) or blue
									}
								}
								if (this.glToolkit.bigEndianess) {
									stream.a(vertexX.toFloat())
									stream.a((this.averageHeight(vertexX, vertexZ) + heightOffset).toFloat())
									stream.a(vertexZ.toFloat())
									stream.f((blendedRgb shr 16).toByte().toInt())
									stream.f((blendedRgb shr 8).toByte().toInt())
									stream.f(blendedRgb.toByte().toInt())
									stream.f(-1)
									stream.a(vertexX.toFloat())
									stream.a(vertexZ.toFloat())
									if (this.tileWaterDepths != null) {
										stream.a((if (waterDepths == null) 0 else waterDepths[tileVertex] - 1).toFloat())
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
									stream.f((blendedRgb shr 16).toByte().toInt())
									stream.f((blendedRgb shr 8).toByte().toInt())
									stream.f(blendedRgb.toByte().toInt())
									stream.f(-1)
									stream.b(vertexX.toFloat())
									stream.b(vertexZ.toFloat())
									if (this.tileWaterDepths != null) {
										stream.b((if (waterDepths == null) 0 else waterDepths[tileVertex] - 1).toFloat())
									}
									if ((this.featureFlags and 0x7) != 0) {
										stream.b(vertexNormalX)
										stream.b(vertexNormalY)
										stream.b(vertexNormalZ)
									}
								}
								vertexIndex = this.vertexCount++
								vertexIndices[tileVertex] = vertexIndex.toShort()
								if (colour != -1) {
									glGroundLayers[vertexIndex] = glGroundLayers3!![tileVertex]
								}
								iterableHashTable.put(vertexKey, ShortNode(vertexIndices[tileVertex]))
							} else {
								vertexIndices[tileVertex] = (node as ShortNode).value
								vertexIndex = vertexIndices[tileVertex].toInt() and 0xFFFF
								if (colour != -1 && glGroundLayers[vertexIndex]!!.key > glGroundLayers3!![tileVertex]!!.key) {
									glGroundLayers[vertexIndex] = glGroundLayers3[tileVertex]
								}
							}
							for (uniqueIndex in 0 until uniqueLayerCount) {
								glGroundLayers2[uniqueIndex]!!.setVertexColour(vertexIndex, lightness, rgb, lightIntensity)
							}
							this.indexCount++
						}
					}
				}
			}
			var vertex: Int = 0
			while (vertex < this.vertexCount) {
				@Pc(1327) val glGroundLayer2: GlGroundLayer? = glGroundLayers[vertex]
				if (glGroundLayer2 != null) {
					glGroundLayer2.setVertexOpaque(vertex)
				}
				vertex++
			}
			@Pc(1360) var local1360: Int
			for (@Pc(1340) m in 0 until super.width) {
				for (@Pc(1344) z in 0 until super.length) {
					@Pc(1356) val triangleVertices: ShortArray? = this.tileVertexIndices[z * super.width + m]
					if (triangleVertices != null) {
						var triangleIndex: Int = 0
						@Pc(1362) var position: Int = 0
						while (triangleVertices.size > position) {
							@Pc(1371) val vertexA: Int = triangleVertices[position++].toInt() and 0xFFFF
							@Pc(1378) val vertexB: Int = triangleVertices[position++].toInt() and 0xFFFF
							@Pc(1385) val vertexC: Int = triangleVertices[position++].toInt() and 0xFFFF
							@Pc(1389) val glGroundLayer3: GlGroundLayer? = glGroundLayers[vertexA]
							@Pc(1393) val glGroundLayer4: GlGroundLayer? = glGroundLayers[vertexB]
							@Pc(1397) val glGroundLayer5: GlGroundLayer? = glGroundLayers[vertexC]
							@Pc(1399) var glGroundLayer6: GlGroundLayer? = null
							if (glGroundLayer3 != null) {
								glGroundLayer3.addTriangle(m, z, triangleIndex)
								glGroundLayer6 = glGroundLayer3
							}
							if (glGroundLayer4 != null) {
								glGroundLayer4.addTriangle(m, z, triangleIndex)
								if (glGroundLayer6 == null || glGroundLayer6.key > glGroundLayer4.key) {
									glGroundLayer6 = glGroundLayer4
								}
							}
							if (glGroundLayer5 != null) {
								glGroundLayer5.addTriangle(m, z, triangleIndex)
								if (glGroundLayer6 == null || glGroundLayer5.key < glGroundLayer6.key) {
									glGroundLayer6 = glGroundLayer5
								}
							}
							if (glGroundLayer6 != null) {
								if (glGroundLayer3 != null) {
									glGroundLayer6.setVertexOpaque(vertexA)
								}
								if (glGroundLayer4 != null) {
									glGroundLayer6.setVertexOpaque(vertexB)
								}
								if (glGroundLayer5 != null) {
									glGroundLayer6.setVertexOpaque(vertexC)
								}
								glGroundLayer6.addTriangle(m, z, triangleIndex)
							}
							triangleIndex++
						}
					}
				}
			}
			stream.a()
			this.dataArrayBuffer = this.glToolkit.createDataArrayBuffer(nativeHeapBuffer, vertexStride, stream.b())
			this.vertexBuffer = OpenGlArrayDrawBuffer(this.dataArrayBuffer, 5126, 3, 0)
			this.colorBuffer = OpenGlArrayDrawBuffer(this.dataArrayBuffer, 5121, 4, 12)
			@Pc(1528) val normalsOffset: Byte
			if (this.tileWaterDepths == null) {
				normalsOffset = 24
				this.textureCoordinatesBuffer = OpenGlArrayDrawBuffer(this.dataArrayBuffer, 5126, 2, 16)
			} else {
				normalsOffset = 28
				this.textureCoordinatesBuffer = OpenGlArrayDrawBuffer(this.dataArrayBuffer, 5126, 3, 16)
			}
			if ((this.featureFlags and 0x7) != 0) {
				this.normalsBuffer = OpenGlArrayDrawBuffer(this.dataArrayBuffer, 5126, 3, normalsOffset.toInt())
			}
			@Pc(1572) val layerPointers: LongArray = LongArray(this.layers!!.size)
			for (layerIndex in 0 until this.layers!!.size) {
				@Pc(1581) val glGroundLayer7: GlGroundLayer? = this.layers!![layerIndex]
				layerPointers[layerIndex] = glGroundLayer7!!.key
				glGroundLayer7.finishColours(this.vertexCount)
			}
			@Suppress("UNCHECKED_CAST")
			ArrayUtils.quicksort(this.layers as Array<Any?>?, layerPointers)
			if (this.shadowMap != null) {
				this.shadowMap!!.initializeShadowTiles()
			}
		}
		this.tileHeightOffsets = null
		this.normalZ = null
		this.normalY = null
		this.normalX = null
		this.tileWaterDepths = null
		this.tileLayers = null
		this.tileColours = null
		this.shadowLevels = null
		this.layerTable = null
		this.tileBlendColours = null
		this.tileOffsetsZ = null
		this.tileOffsetsX = null
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(BIILclient!uc;)V")
	private fun projectShadow(@OriginalArg(1) tileX: Int, @OriginalArg(2) tileZ: Int, @OriginalArg(3) shadow: GlShadow?) {
		@Pc(12) val offsetsX: IntArray? = this.tileOffsetsX!![tileX][tileZ]
		@Pc(26) val offsetsZ: IntArray? = this.tileOffsetsZ!![tileX][tileZ]
		@Pc(29) val length: Int = offsetsX!!.size
		if (this.glToolkit.scratchVerticesX.size < length) {
			this.glToolkit.scratchVerticesY = IntArray(length)
			this.glToolkit.scratchVerticesX = IntArray(length)
		}
		@Pc(49) val projectedX: IntArray = this.glToolkit.scratchVerticesX
		@Pc(53) val projectedZ: IntArray = this.glToolkit.scratchVerticesY
		for (@Pc(55) i in 0 until length) {
			projectedX[i] = offsetsX[i] shr this.glToolkit.shadowScaleShift
			projectedZ[i] = offsetsZ!![i] shr this.glToolkit.shadowScaleShift
		}
		@Pc(83) var index: Int = 0
		while (index < length) {
			@Pc(89) val xA: Int = projectedX[index]
			@Pc(94) val zA: Int = projectedZ[index++]
			@Pc(98) val xB: Int = projectedX[index]
			@Pc(103) val zB: Int = projectedZ[index++]
			@Pc(107) val xC: Int = projectedX[index]
			@Pc(112) val zC: Int = projectedZ[index++]
			if ((xA - xB) * (zB - zC) - (xC - xB) * (zB - zA) > 0) {
				shadow!!.fillTriangle(xC, xA, zA, xB, zC, zB)
			}
		}
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(II)V")
	override fun renderTile(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int) {
	}

	@OriginalMember(owner = "client!ed", name = "CA", descriptor = "(Lclient!r;IIIIZ)V")
	override fun CA(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) x: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) z: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean) {
		if (this.shadowMap != null && shadow != null) {
			@Pc(23) val shadowX: Int = (x - ((heightOffset * this.glToolkit.sunSlopeX) shr 8)) shr this.glToolkit.shadowScaleShift
			@Pc(38) val shadowZ: Int = (z - ((this.glToolkit.sunSlopeZ * heightOffset) shr 8)) shr this.glToolkit.shadowScaleShift
			this.shadowMap!!.addShadow(shadowX, shadowZ, shadow)
		}
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(Lclient!r;IIIIZ)Z")
	override fun method8002(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) x: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) z: Int): Boolean {
		if (this.shadowMap == null || shadow == null) {
			return false
		} else {
			@Pc(25) val shadowX: Int = (x - ((heightOffset * this.glToolkit.sunSlopeX) shr 8)) shr this.glToolkit.shadowScaleShift
			@Pc(40) val shadowZ: Int = (z - ((this.glToolkit.sunSlopeZ * heightOffset) shr 8)) shr this.glToolkit.shadowScaleShift
			return this.shadowMap!!.shouldApplyShadow(shadowX, shadowZ, shadow)
		}
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(III[[ZZI)V")
	override fun renderTiles(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) radius: Int, @OriginalArg(3) visibility: Array<BooleanArray?>?, @OriginalArg(4) belowLowestPlane: Boolean, @OriginalArg(5) skipFlags: Int) {
		this.renderGround(-1, radius, belowLowestPlane, x, skipFlags, visibility, z)
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(IIIIIII[[Z)V")
	override fun drawMinimap(@OriginalArg(3) x1: Int, @OriginalArg(4) y1: Int, @OriginalArg(5) x2: Int, @OriginalArg(6) y2: Int, @OriginalArg(7) visibility: Array<BooleanArray?>?) {
		if (this.totalTileVertexCount <= 0) {
			return
		}
		this.glToolkit.setOrthoMatrixMode()
		this.glToolkit.toggleFog(false)
		this.glToolkit.setLightingEnabled(false)
		this.glToolkit.toggleZbuffer(false)
		this.glToolkit.setDepthMask(false)
		this.glToolkit.setBlendMode(0)
		this.glToolkit.renderTexture(-2)
		this.glToolkit.setActiveTexture(null)
		GlGround.minimapMatrix[0] = 1024.toFloat() / (this.glToolkit.viewportWidth.toFloat() * super.tileUnits.toFloat() * 128.0f)
		GlGround.minimapMatrix[8] = 0.0f
		GlGround.minimapMatrix[13] = 1.0f - ((y2 * 1024).toFloat() / 128.0f + 0.toFloat()) / this.glToolkit.viewportHeight.toFloat()
		GlGround.minimapMatrix[7] = 0.0f
		GlGround.minimapMatrix[6] = 0.0f
		GlGround.minimapMatrix[5] = 1024.toFloat() / (this.glToolkit.viewportHeight.toFloat() * super.tileUnits.toFloat() * 128.0f)
		GlGround.minimapMatrix[1] = 0.0f
		GlGround.minimapMatrix[4] = 0.0f
		GlGround.minimapMatrix[14] = 0.0f
		GlGround.minimapMatrix[12] = -(((x1 * 1024).toFloat() / 128.0f - 0.toFloat()) / this.glToolkit.viewportWidth.toFloat()) - 1.0f
		GlGround.minimapMatrix[10] = 0.0f
		GlGround.minimapMatrix[15] = 1.0f
		GlGround.minimapMatrix[3] = 0.0f
		GlGround.minimapMatrix[2] = 0.0f
		GlGround.minimapMatrix[9] = 0.0f
		GlGround.minimapMatrix[11] = 0.0f
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		OpenGL.glLoadMatrixf(GlGround.minimapMatrix, 0)
		GlGround.minimapMatrix[12] = 0.0f
		GlGround.minimapMatrix[4] = 0.0f
		GlGround.minimapMatrix[0] = 1.0f
		GlGround.minimapMatrix[14] = 0.0f
		GlGround.minimapMatrix[6] = 1.0f
		GlGround.minimapMatrix[8] = 0.0f
		GlGround.minimapMatrix[11] = 0.0f
		GlGround.minimapMatrix[13] = 0.0f
		GlGround.minimapMatrix[7] = 0.0f
		GlGround.minimapMatrix[5] = 0.0f
		GlGround.minimapMatrix[15] = 1.0f
		GlGround.minimapMatrix[2] = 0.0f
		GlGround.minimapMatrix[3] = 0.0f
		GlGround.minimapMatrix[9] = 1.0f
		GlGround.minimapMatrix[10] = 0.0f
		GlGround.minimapMatrix[1] = 0.0f
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		OpenGL.glLoadMatrixf(GlGround.minimapMatrix, 0)
		if ((this.featureFlags and 0x7) == 0) {
			this.glToolkit.setLightingEnabled(false)
		} else {
			this.glToolkit.setLightingEnabled(true)
			this.glToolkit.setLightingSourceParams()
		}
		this.glToolkit.toggleDrawingClientstates(this.vertexBuffer, this.colorBuffer, this.textureCoordinatesBuffer, this.normalsBuffer)
		if (this.indexCount * 2 > this.glToolkit.textureBuffer.buffer!!.size) {
			this.glToolkit.textureBuffer = GlBitBuffer(this.indexCount * 2)
		} else {
			this.glToolkit.textureBuffer.currentPosition = 0
		}
		@Pc(314) var elementCount: Int = 0
		@Pc(318) val gpuPacket: GlBitBuffer = this.glToolkit.textureBuffer
		@Pc(324) var z: Int
		@Pc(333) var tileIndex: Int
		@Pc(335) var x: Int
		@Pc(354) var vertexIndices: ShortArray?
		@Pc(358) var i: Int
		if (this.glToolkit.bigEndianess) {
			z = y1
			while (z < y2) {
				tileIndex = z * super.width + x1
				x = x1
				while (x < x2) {
					if (visibility!![x - x1]!![z - y1]) {
						vertexIndices = this.tileVertexIndices[tileIndex]
						if (vertexIndices != null) {
							i = 0
							while (i < vertexIndices.size) {
								gpuPacket.writeShort(vertexIndices[i].toInt() and 0xFFFF)
								elementCount++
								i++
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
							i = 0
							while (i < vertexIndices.size) {
								gpuPacket.writeShortLE(vertexIndices[i].toInt() and 0xFFFF)
								elementCount++
								i++
							}
						}
					}
					tileIndex++
					x++
				}
				z++
			}
		}
		if (elementCount > 0) {
			@Pc(470) val indexBuffer: GlElementsArrayBuffer = GlElementsArrayBuffer(this.glToolkit, 5123, gpuPacket.buffer, gpuPacket.currentPosition)
			this.glToolkit.drawElements(elementCount, indexBuffer, 0)
		}
	}

	@OriginalMember(owner = "client!ed", name = "fa", descriptor = "(IILclient!r;)Lclient!r;")
	override fun fa(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) shadow: Shadow?): Shadow? {
		if ((this.tileFlags[x][z].toInt() and 0x1) == 0) {
			return null
		}
		@Pc(24) val shadowSize: Int = super.tileUnits shr this.glToolkit.shadowScaleShift
		@Pc(27) val glShadow: GlShadow? = shadow as GlShadow?
		@Pc(43) val glShadow2: GlShadow
		if (glShadow != null && glShadow.fitsDimension(shadowSize, shadowSize)) {
			glShadow2 = glShadow
			glShadow.reset()
		} else {
			glShadow2 = GlShadow(this.glToolkit, shadowSize, shadowSize)
		}
		glShadow2.setDimension(shadowSize, shadowSize, 0, 0)
		this.projectShadow(x, z, glShadow2)
		return glShadow2
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(III[[ZZII)V")
	override fun renderFloor(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) radius: Int, @OriginalArg(3) visibility: Array<BooleanArray?>?, @OriginalArg(4) belowLowestPlane: Boolean, @OriginalArg(5) orthoZoom: Int, @OriginalArg(6) skipFlags: Int) {
		this.renderGround(orthoZoom, radius, belowLowestPlane, x, skipFlags, visibility, z)
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(Lclient!ab;[I)V")
	override fun addLight(@OriginalArg(0) pointLight: PointLight?, @OriginalArg(1) tileMask: IntArray?) {
		this.lights.addLast(LightGL(this.glToolkit, this, pointLight, tileMask))
	}

	@OriginalMember(owner = "client!ed", name = "U", descriptor = "(II[I[I[I[I[I[I[I[IIIIZ)V")
	override fun U(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) offsetsX: IntArray?, @OriginalArg(3) heightOffsets: IntArray?, @OriginalArg(4) offsetsZ: IntArray?, @OriginalArg(5) waterDepths: IntArray?, @OriginalArg(6) colours: IntArray?, @OriginalArg(7) blendedColours: IntArray?, @OriginalArg(8) materialIds: IntArray?, @OriginalArg(9) textureScales: IntArray?, @OriginalArg(10) waterColour: Int, @OriginalArg(11) waterDepth: Int, @OriginalArg(12) waterBias: Int, @OriginalArg(13) hasShadow: Boolean) {
		if (heightOffsets != null && this.tileHeightOffsets == null) {
			this.tileHeightOffsets = Array(super.width) { arrayOfNulls<IntArray>(super.length) }
		}
		@Pc(21) val textureSource: MaterialProvider? = this.glToolkit.materialProvider
		if (waterDepths != null && this.tileWaterDepths == null) {
			this.tileWaterDepths = Array(super.width) { arrayOfNulls<IntArray>(super.length) }
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
		@Pc(92) val glGroundLayers: Array<GlGroundLayer?> = arrayOfNulls<GlGroundLayer>(colours!!.size)
		this.tileLayers!![x][z] = glGroundLayers
		for (@Pc(94) i in 0 until colours.size) {
			@Pc(100) var materialId: Int = materialIds!![i]
			@Pc(104) var textureScale: Int = textureScales!![i]
			if ((this.featureFlags and 0x20) != 0 && materialId != -1 && textureSource!!.getMetrics(materialId)!!.detailsOnly) {
				materialId = -1
				textureScale = 128
			}
			@Pc(148) val layerKey: Long = (waterColour.toLong() shl 28) or (waterDepth.toLong() shl 42) or (waterBias.toLong() shl 48) or (textureScale shl 14).toLong() or materialId.toLong()
			@Pc(154) var node: Node?
			node = this.layerTable!!.get(layerKey)
			while (node != null) {
				@Pc(159) val glGroundLayer: GlGroundLayer = node as GlGroundLayer
				if (materialId == glGroundLayer.materialId && glGroundLayer.textureScale == textureScale.toFloat() && glGroundLayer.waterFogColour == waterColour && waterDepth == glGroundLayer.waterDepth && waterBias == glGroundLayer.waterIntensity) {
					break
				}
				node = this.layerTable!!.nextWithKey()
			}
			if (node == null) {
				glGroundLayers[i] = GlGroundLayer(this, materialId, textureScale, waterColour, waterDepth, waterBias)
				this.layerTable!!.put(layerKey, glGroundLayers[i])
			} else {
				glGroundLayers[i] = node as GlGroundLayer
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

	@OriginalMember(owner = "client!ed", name = "ka", descriptor = "(III)V")
	override fun ka(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) shadowLevel: Int) {
		if (shadowLevel > (this.shadowLevels!![x][z].toInt() and 0xFF)) {
			this.shadowLevels!![x][z] = shadowLevel.toByte()
		}
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(IIZIII[[ZI)V")
	private fun renderGround(@OriginalArg(0) orthoZoom: Int, @OriginalArg(1) radius: Int, @OriginalArg(2) belowLowestPlane: Boolean, @OriginalArg(4) x: Int, @OriginalArg(5) skipFlags: Int, @OriginalArg(6) visibility: Array<BooleanArray?>?, @OriginalArg(7) z: Int) {
		if (this.layers == null) {
			return
		}
		@Pc(20) val diameter: Int = radius + radius + 1
		@Pc(24) val maxVisibleTiles: Int = diameter * diameter
		if (this.glToolkit.visibleTileIndices.size < maxVisibleTiles) {
			this.glToolkit.visibleTileIndices = IntArray(maxVisibleTiles)
		}
		if (this.glToolkit.textureBuffer.buffer!!.size < this.indexCount * 2) {
			this.glToolkit.textureBuffer = GlBitBuffer(this.indexCount * 2)
		}
		@Pc(64) var minX: Int = x - radius
		@Pc(66) val originX: Int = minX
		if (minX < 0) {
			minX = 0
		}
		@Pc(74) var minZ: Int = z - radius
		@Pc(76) val originZ: Int = minZ
		if (minZ < 0) {
			minZ = 0
		}
		@Pc(87) var maxX: Int = radius + x
		if (maxX > super.width - 1) {
			maxX = super.width - 1
		}
		@Pc(102) var maxZ: Int = z + radius
		if (maxZ > super.length - 1) {
			maxZ = super.length - 1
		}
		@Pc(119) var visibleTileCount: Int = 0
		@Pc(123) val visibleTileIndices: IntArray = this.glToolkit.visibleTileIndices
		@Pc(136) var local136: Int
		for (@Pc(125) i in minX..maxX) {
			@Pc(134) val visibilityRow: BooleanArray? = visibility!![i - originX]
			for (tileZ in minZ..maxZ) {
				if (visibilityRow!![tileZ - originZ]) {
					visibleTileIndices[visibleTileCount++] = i + tileZ * super.width
				}
			}
		}
		if (orthoZoom == -1) {
			this.glToolkit.setupMatrixRendering()
		} else {
			this.glToolkit.setOrthoZoom(orthoZoom.toFloat())
			this.glToolkit.setupOrthoMatrixRendering()
		}
		this.glToolkit.setLightingEnabled((this.featureFlags and 0x7) != 0)
		for (@Pc(207) j in 0 until this.layers!!.size) {
			this.layers!![j]!!.render(visibleTileCount, visibleTileIndices)
		}
		if (!this.lights.isEmpty()) {
			val fogColour: Int = this.glToolkit.fogColor
			@Pc(236) val fogDensity: Int = this.glToolkit.fogDensity
			this.glToolkit.L(0, fogDensity, this.glToolkit.fogOffset)
			this.glToolkit.setLightingEnabled(false)
			this.glToolkit.setDepthMask(false)
			this.glToolkit.setBlendMode(128)
			this.glToolkit.renderTexture(-2)
			this.glToolkit.setActiveTexture(this.glToolkit.texture2d)
			this.glToolkit.loadTextureCombineMode(8448, 7681)
			this.glToolkit.setTexture(34166, 770, 0)
			this.glToolkit.setTextureTransparency(0, 34167)
			@Pc(296) var node: Node? = this.lights.first()
			while (node != null) {
				@Pc(301) val glGroundLight: LightGL = node as LightGL
				glGroundLight.drawLightMesh(visibility, x, z, radius)
				node = this.lights.next()
			}
			this.glToolkit.setTexture(5890, 768, 0)
			this.glToolkit.setTextureTransparency(0, 5890)
			this.glToolkit.setActiveTexture(null)
			this.glToolkit.L(fogColour, fogDensity, this.glToolkit.fogOffset)
		}
		if (this.shadowMap == null) {
			return
		}
		OpenGL.glPushMatrix()
		OpenGL.glTranslatef(0.0f, -1.0f, 0.0f)
		this.glToolkit.toggleDrawingClientstates(this.vertexBuffer, null, this.textureCoordinatesBuffer, null)
		this.shadowMap!!.renderShadowOverlay(belowLowestPlane, visibility, radius, z, x)
		OpenGL.glPopMatrix()
		return
	}

	companion object {
		@OriginalMember(owner = "client!kk", name = "a", descriptor = "[F")
		@JvmField
		val minimapMatrix: FloatArray = FloatArray(16)
	}
}
