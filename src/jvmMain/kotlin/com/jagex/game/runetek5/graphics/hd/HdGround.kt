/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlGround (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.client.features.HdSettings
import com.jagex.game.runetek5.client.features.RenderStats

import com.jagex.game.runetek5.scene.ground.Ground

import com.jagex.game.runetek5.cache.node.impl.ShortNode
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.config.material.MaterialProvider
import jaclib.memory.Stream
import jaclib.memory.heap.NativeHeapBuffer
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer
import com.jagex.game.runetek5.utils.ColorUtil
import com.jagex.game.runetek5.utils.ArrayUtils
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField

class HdGround : Ground {

	private var vertexCount: Int = 0

	private var tileWaterDepths: Array<Array<IntArray?>>? = null

	private var indexCount: Int = 0

	private var colorBuffer: OpenGlArrayDrawBuffer? = null

	@JvmField
	var normalsBuffer: OpenGlArrayDrawBuffer? = null

	@JvmField
	var vertexBuffer: OpenGlArrayDrawBuffer? = null

	@JvmField
	var textureCoordinatesBuffer: OpenGlArrayDrawBuffer? = null

	private var dataArrayBuffer: ArrayBufferGL? = null

	private var maxTileVertexCount: Int = 0

	private var layers: Array<HdGroundLayer?>? = null

	private var totalTileVertexCount: Int = 0

	private val lights: Deque = Deque()

	@JvmField
	val glToolkit: HdToolkit

	private val quarterTileScale: Int

	private var tileBlendColours: Array<Array<IntArray?>>?

	private val tileFlags: Array<ByteArray>

	@JvmField
	var tileOffsetsX: Array<Array<IntArray?>>?

	@JvmField
	var tileColours: Array<Array<IntArray?>>?

	private var normalX: Array<FloatArray>?

	private var tileLayers: Array<Array<Array<HdGroundLayer?>?>>?

	private var tileHeightOffsets: Array<Array<IntArray?>>?

	private var normalY: Array<FloatArray>?

	private var shadowLevels: Array<ByteArray>?

	@JvmField
	val tileVertexIndices: Array<ShortArray?>

	private val quarterTileUnits: Int

	@JvmField
	var tileOffsetsZ: Array<Array<IntArray?>>?

	private var normalZ: Array<FloatArray>?

	@JvmField
	val featureFlags: Int

	private var layerTable: IterableHashTable? = null

	private var shadowMap: HdShadowMap? = null

	constructor(glToolkit: HdToolkit?, groundFlags: Int, featureFlags: Int, width: Int, length: Int, tileHeights: Array<IntArray?>?, normalHeights: Array<IntArray?>?, scaling: Int) : super(width, length, scaling, tileHeights) {
		this.glToolkit = glToolkit!!
		this.quarterTileScale = super.tileScale - 2
		this.tileBlendColours = Array(width) { arrayOfNulls<IntArray>(length) }
		this.tileFlags = Array(width) { ByteArray(length) }
		this.tileOffsetsX = Array(width) { arrayOfNulls<IntArray>(length) }
		this.tileColours = Array(width) { arrayOfNulls<IntArray>(length) }
		this.normalX = Array(super.width + 1) { FloatArray(super.length + 1) }
		this.tileLayers = Array(width) { arrayOfNulls<Array<HdGroundLayer?>>(length) }
		this.tileHeightOffsets = Array(width) { arrayOfNulls<IntArray>(length) }
		this.normalY = Array(super.width + 1) { FloatArray(super.length + 1) }
		this.shadowLevels = Array(width + 1) { ByteArray(length + 1) }
		this.tileVertexIndices = arrayOfNulls<ShortArray>(length * width)
		this.quarterTileUnits = 0x1 shl this.quarterTileScale
		this.tileOffsetsZ = Array(width) { arrayOfNulls<IntArray>(length) }
		this.normalZ = Array(super.width + 1) { FloatArray(super.length + 1) }
		this.featureFlags = featureFlags
		var i: Int = 1
		while (super.length > i) {
			var j: Int = 1
			while (super.width > j) {
				val heightDeltaX: Int = normalHeights!![j + 1]!![i] - normalHeights[j - 1]!![i]
				val heightDeltaZ: Int = normalHeights[j]!![i + 1] - normalHeights[j]!![i - 1]
				val normalScale: Float = (1.0 / kotlin.math.sqrt((heightDeltaZ * heightDeltaZ + scaling * scaling * 4 + heightDeltaX * heightDeltaX).toDouble())).toFloat()
				this.normalX!![j][i] = normalScale * heightDeltaX.toFloat()
				this.normalY!![j][i] = normalScale * (-scaling * 2).toFloat()
				this.normalZ!![j][i] = heightDeltaZ.toFloat() * normalScale
				j++
			}
			i++
		}
		this.layerTable = IterableHashTable(128)
		if ((this.featureFlags and 0x10) != 0) {
			this.shadowMap = HdShadowMap(this.glToolkit, this)
		}
	}

	override fun wa(shadow: Shadow?, x: Int, heightOffset: Int, z: Int, arg4: Int, arg5: Boolean) {
		if (this.shadowMap != null && shadow != null) {
			val shadowX: Int = (x - ((this.glToolkit.sunSlopeX * heightOffset) shr 8)) shr this.glToolkit.shadowScaleShift
			val shadowZ: Int = (z - ((heightOffset * this.glToolkit.sunSlopeZ) shr 8)) shr this.glToolkit.shadowScaleShift
			this.shadowMap!!.removeShadow(shadow, shadowX, shadowZ)
		}
	}

	override fun addTile(x: Int, z: Int, offsetsX: IntArray?, heightOffsets: IntArray?, offsetsZ: IntArray?, waterDepths: IntArray?, faceA: IntArray?, faceB: IntArray?, faceC: IntArray?, colours: IntArray?, blendedColours: IntArray?, materialIds: IntArray?, textureScales: IntArray?, waterColour: Int, waterDepth: Int, waterBias: Int) {
		val length: Int = colours!!.size
		val expandedOffsetsX: IntArray = IntArray(length * 3)
		val expandedOffsetsZ: IntArray = IntArray(length * 3)
		val expandedColours: IntArray = IntArray(length * 3)
		val expandedBlendedColours: IntArray = IntArray(length * 3)
		val expandedMaterialIds: IntArray = IntArray(length * 3)
		val expandedTextureScales: IntArray = IntArray(length * 3)
		val expandedHeightOffsets: IntArray? = if (heightOffsets == null) null else IntArray(length * 3)
		val expandedWaterDepths: IntArray? = if (waterDepths == null) null else IntArray(length * 3)
		var vertexIndex: Int = 0
		for (i in 0 until length) {
			val vertexA: Int = faceA!![i]
			val vertexB: Int = faceB!![i]
			val vertexC: Int = faceC!![i]
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

	override fun YA() {
		if (this.totalTileVertexCount <= 0) {
			this.shadowMap = null
		} else {
			val smoothedShadowLevels: Array<ByteArray> = Array(super.width + 1) { ByteArray(super.length + 1) }
			var local28: Int
			for (i in 1 until super.width) {
				for (tileZ in 1 until super.length) {
					smoothedShadowLevels[i][tileZ] = ((this.shadowLevels!![i][tileZ + 1].toInt() shr 3) + (this.shadowLevels!![i][tileZ - 1].toInt() shr 2) + (this.shadowLevels!![i + 1][tileZ].toInt() shr 3) + (this.shadowLevels!![i - 1][tileZ].toInt() shr 2) + (this.shadowLevels!![i][tileZ].toInt() shr 1)).toByte()
				}
			}
			this.layers = arrayOfNulls<HdGroundLayer>(this.layerTable!!.size())
			@Suppress("UNCHECKED_CAST")
			this.layerTable!!.toArray(this.layers as Array<Node?>?)
			for (layerIndex in 0 until this.layers!!.size) {
				this.layers!![layerIndex]!!.initColours(this.totalTileVertexCount)
			}
			var vertexStride: Int = 24
			if (this.tileWaterDepths != null) {
				vertexStride += 4
			}
			if ((this.featureFlags and 0x7) != 0) {
				vertexStride += 12
			}
			val nativeHeapBuffer: NativeHeapBuffer = this.glToolkit.nativeHeap!!.a(this.totalTileVertexCount * vertexStride, false)
			val stream: Stream = Stream(nativeHeapBuffer)
			val glGroundLayers: Array<HdGroundLayer?> = arrayOfNulls<HdGroundLayer>(this.totalTileVertexCount)
			var hashCapacity: Int = Utils.floorPowerOfTwo(this.totalTileVertexCount / 4)
			if (hashCapacity < 1) {
				hashCapacity = 1
			}
			val iterableHashTable: IterableHashTable = IterableHashTable(hashCapacity)
			val glGroundLayers2: Array<HdGroundLayer?> = arrayOfNulls<HdGroundLayer>(this.maxTileVertexCount)
			var local194: Int
			for (j in 0 until super.width) {
				for (tileZ in 0 until super.length) {
					if (this.tileColours!![j][tileZ] != null) {
						val glGroundLayers3: Array<HdGroundLayer?>? = this.tileLayers!![j][tileZ]
						val offsetsX: IntArray? = this.tileOffsetsX!![j][tileZ]
						val offsetsZ: IntArray? = this.tileOffsetsZ!![j][tileZ]
						var blendedColours: IntArray? = this.tileBlendColours!![j][tileZ]
						val colours: IntArray? = this.tileColours!![j][tileZ]
						val heightOffsets: IntArray? = if (this.tileHeightOffsets == null) null else this.tileHeightOffsets!![j][tileZ]
						if (blendedColours == null) {
							blendedColours = colours
						}
						val waterDepths: IntArray? = if (this.tileWaterDepths == null) null else this.tileWaterDepths!![j][tileZ]
						val normalX00: Float = this.normalX!![j][tileZ]
						val normalY00: Float = this.normalY!![j][tileZ]
						val normalZ00: Float = this.normalZ!![j][tileZ]
						val normalX01: Float = this.normalX!![j][tileZ + 1]
						val normalY01: Float = this.normalY!![j][tileZ + 1]
						val normalZ01: Float = this.normalZ!![j][tileZ + 1]
						val normalX11: Float = this.normalX!![j + 1][tileZ + 1]
						val normalY11: Float = this.normalY!![j + 1][tileZ + 1]
						val normalZ11: Float = this.normalZ!![j + 1][tileZ + 1]
						val normalX10: Float = this.normalX!![j + 1][tileZ]
						val normalY10: Float = this.normalY!![j + 1][tileZ]
						val normalZ10: Float = this.normalZ!![j + 1][tileZ]
						val shadow00: Int = smoothedShadowLevels[j][tileZ].toInt() and 0xFF
						val shadow01: Int = smoothedShadowLevels[j][tileZ + 1].toInt() and 0xFF
						val shadow11: Int = smoothedShadowLevels[j + 1][tileZ + 1].toInt() and 0xFF
						val shadow10: Int = smoothedShadowLevels[j + 1][tileZ].toInt() and 0xFF
						var uniqueLayerCount: Int = 0
						var local426: Int
						label337@ for (k in 0 until colours!!.size) {
							val glGroundLayer: HdGroundLayer? = glGroundLayers3!![k]
							for (uniqueIndex in 0 until uniqueLayerCount) {
								if (glGroundLayers2[uniqueIndex] === glGroundLayer) {
									continue@label337
								}
							}
							glGroundLayers2[uniqueLayerCount++] = glGroundLayer
						}
						val vertexIndices: ShortArray = ShortArray(colours.size)
						this.tileVertexIndices[super.width * tileZ + j] = vertexIndices
						for (tileVertex in 0 until colours.size) {
							val vertexX: Int = (j shl super.tileScale) + offsetsX!![tileVertex]
							val vertexZ: Int = (tileZ shl super.tileScale) + offsetsZ!![tileVertex]
							val gridX: Int = vertexX shr this.quarterTileScale
							val gridZ: Int = vertexZ shr this.quarterTileScale
							val colour: Int = colours[tileVertex]
							val blendedColour: Int = blendedColours!![tileVertex]
							val heightOffset: Int = if (heightOffsets == null) 0 else heightOffsets[tileVertex]
							val vertexKey: Long = (gridX shl 16).toLong() or (colour.toLong() shl 32) or (blendedColour.toLong() shl 48) or gridZ.toLong()
							val offsetX: Int = offsetsX[tileVertex]
							val offsetZ: Int = offsetsZ[tileVertex]
							val baseLightness: Byte = 74
							var rgb: Int = 0
							var vertexNormalX: Float
							var vertexNormalY: Float
							var vertexNormalZ: Float
							var local626: Float
							var lightness: Int
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
								val fracX: Float = offsetX.toFloat() / super.tileUnits.toFloat()
								val fracZ: Float = offsetZ.toFloat() / super.tileUnits.toFloat()
								val lerpNormalX0: Float = (normalX10 - normalX00) * fracX + normalX00
								val lerpNormalY0: Float = (normalY10 - normalY00) * fracX + normalY00
								val lerpNormalZ0: Float = fracX * (normalZ10 - normalZ00) + normalZ00
								val lerpNormalX1: Float = fracX * (normalX11 - normalX01) + normalX01
								val lerpNormalY1: Float = normalY01 + (normalY11 - normalY01) * fracX
								val lerpNormalZ1: Float = (normalZ11 - normalZ01) * fracX + normalZ01
								vertexNormalY = lerpNormalY0 + (lerpNormalY1 - lerpNormalY0) * fracZ
								vertexNormalX = fracZ * (lerpNormalX1 - lerpNormalX0) + lerpNormalX0
								vertexNormalZ = lerpNormalZ0 + (lerpNormalZ1 - lerpNormalZ0) * fracZ
								val lerpShadow0: Int = (((shadow10 - shadow00) * offsetX) shr super.tileScale) + shadow00
								val lerpShadow1: Int = ((offsetX * (shadow11 - shadow01)) shr super.tileScale) + shadow01
								lightness = baseLightness - lerpShadow0 - (((lerpShadow1 - lerpShadow0) * offsetZ) shr super.tileScale)
							}
							var lightIntensity: Float = 1.0f
							if (colour != -1) {
								var adjustedLightness: Int = ((colour and 0x7F) * lightness) shr 7
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
							var node: Node? = null
							if ((vertexX and (this.quarterTileUnits - 1)) == 0 && ((this.quarterTileUnits - 1) and vertexZ) == 0) {
								node = iterableHashTable.get(vertexKey)
							}
							var vertexIndex: Int
							var local937: Int
							if (node == null) {
								var blendedRgb: Int
								if (colour == blendedColour) {
									blendedRgb = rgb
								} else {
									var blendedLightness: Int = ((blendedColour and 0x7F) * lightness) shr 7
									if (blendedLightness < 2) {
										blendedLightness = 2
									} else if (blendedLightness > 126) {
										blendedLightness = 126
									}
									blendedRgb = ColorUtil.hslToRgb!![(blendedColour and 0xFF80) or blendedLightness]
									if ((this.featureFlags and 0x7) == 0) {
										var blendedLightIntensity: Float = this.glToolkit.sunDirection[0] * vertexNormalX + vertexNormalY * this.glToolkit.sunDirection[1] + vertexNormalZ * this.glToolkit.sunDirection[2]
										blendedLightIntensity = (if (lightIntensity > 0.0f) this.glToolkit.sunColorIntensity else this.glToolkit.antiSunColorIntensity) * lightIntensity + this.glToolkit.sunBrightness
										var red: Int = (blendedRgb shr 16) and 0xFF
										var green: Int = (blendedRgb shr 8) and 0xFF
										var blue: Int = blendedRgb and 0xFF
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
				val glGroundLayer2: HdGroundLayer? = glGroundLayers[vertex]
				if (glGroundLayer2 != null) {
					glGroundLayer2.setVertexOpaque(vertex)
				}
				vertex++
			}
			var local1360: Int
			for (m in 0 until super.width) {
				for (z in 0 until super.length) {
					val triangleVertices: ShortArray? = this.tileVertexIndices[z * super.width + m]
					if (triangleVertices != null) {
						var triangleIndex: Int = 0
						var position: Int = 0
						while (triangleVertices.size > position) {
							val vertexA: Int = triangleVertices[position++].toInt() and 0xFFFF
							val vertexB: Int = triangleVertices[position++].toInt() and 0xFFFF
							val vertexC: Int = triangleVertices[position++].toInt() and 0xFFFF
							val glGroundLayer3: HdGroundLayer? = glGroundLayers[vertexA]
							val glGroundLayer4: HdGroundLayer? = glGroundLayers[vertexB]
							val glGroundLayer5: HdGroundLayer? = glGroundLayers[vertexC]
							var glGroundLayer6: HdGroundLayer? = null
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
			val normalsOffset: Byte
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
			val layerPointers: LongArray = LongArray(this.layers!!.size)
			for (layerIndex in 0 until this.layers!!.size) {
				val glGroundLayer7: HdGroundLayer? = this.layers!![layerIndex]
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

	private fun projectShadow(tileX: Int, tileZ: Int, shadow: HdShadow?) {
		val offsetsX: IntArray? = this.tileOffsetsX!![tileX][tileZ]
		val offsetsZ: IntArray? = this.tileOffsetsZ!![tileX][tileZ]
		val length: Int = offsetsX!!.size
		if (this.glToolkit.scratchVerticesX.size < length) {
			this.glToolkit.scratchVerticesY = IntArray(length)
			this.glToolkit.scratchVerticesX = IntArray(length)
		}
		val projectedX: IntArray = this.glToolkit.scratchVerticesX
		val projectedZ: IntArray = this.glToolkit.scratchVerticesY
		for (i in 0 until length) {
			projectedX[i] = offsetsX[i] shr this.glToolkit.shadowScaleShift
			projectedZ[i] = offsetsZ!![i] shr this.glToolkit.shadowScaleShift
		}
		var index: Int = 0
		while (index < length) {
			val xA: Int = projectedX[index]
			val zA: Int = projectedZ[index++]
			val xB: Int = projectedX[index]
			val zB: Int = projectedZ[index++]
			val xC: Int = projectedX[index]
			val zC: Int = projectedZ[index++]
			if ((xA - xB) * (zB - zC) - (xC - xB) * (zB - zA) > 0) {
				shadow!!.fillTriangle(xC, xA, zA, xB, zC, zB)
			}
		}
	}

	override fun renderTile(x: Int, z: Int) {
	}

	override fun CA(shadow: Shadow?, x: Int, heightOffset: Int, z: Int, arg4: Int, arg5: Boolean) {
		if (this.shadowMap != null && shadow != null) {
			val shadowX: Int = (x - ((heightOffset * this.glToolkit.sunSlopeX) shr 8)) shr this.glToolkit.shadowScaleShift
			val shadowZ: Int = (z - ((this.glToolkit.sunSlopeZ * heightOffset) shr 8)) shr this.glToolkit.shadowScaleShift
			this.shadowMap!!.addShadow(shadowX, shadowZ, shadow)
		}
	}

	override fun method8002(shadow: Shadow?, x: Int, heightOffset: Int, z: Int): Boolean {
		if (this.shadowMap == null || shadow == null) {
			return false
		} else {
			val shadowX: Int = (x - ((heightOffset * this.glToolkit.sunSlopeX) shr 8)) shr this.glToolkit.shadowScaleShift
			val shadowZ: Int = (z - ((this.glToolkit.sunSlopeZ * heightOffset) shr 8)) shr this.glToolkit.shadowScaleShift
			return this.shadowMap!!.shouldApplyShadow(shadowX, shadowZ, shadow)
		}
	}

	override fun renderTiles(x: Int, z: Int, radius: Int, visibility: Array<BooleanArray?>?, belowLowestPlane: Boolean, skipFlags: Int) {
		this.renderGround(-1, radius, belowLowestPlane, x, skipFlags, visibility, z)
	}

	override fun drawMinimap(x1: Int, y1: Int, x2: Int, y2: Int, visibility: Array<BooleanArray?>?) {
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
		HdGround.minimapMatrix[0] = 1024.toFloat() / (this.glToolkit.viewportWidth.toFloat() * super.tileUnits.toFloat() * 128.0f)
		HdGround.minimapMatrix[8] = 0.0f
		HdGround.minimapMatrix[13] = 1.0f - ((y2 * 1024).toFloat() / 128.0f + 0.toFloat()) / this.glToolkit.viewportHeight.toFloat()
		HdGround.minimapMatrix[7] = 0.0f
		HdGround.minimapMatrix[6] = 0.0f
		HdGround.minimapMatrix[5] = 1024.toFloat() / (this.glToolkit.viewportHeight.toFloat() * super.tileUnits.toFloat() * 128.0f)
		HdGround.minimapMatrix[1] = 0.0f
		HdGround.minimapMatrix[4] = 0.0f
		HdGround.minimapMatrix[14] = 0.0f
		HdGround.minimapMatrix[12] = -(((x1 * 1024).toFloat() / 128.0f - 0.toFloat()) / this.glToolkit.viewportWidth.toFloat()) - 1.0f
		HdGround.minimapMatrix[10] = 0.0f
		HdGround.minimapMatrix[15] = 1.0f
		HdGround.minimapMatrix[3] = 0.0f
		HdGround.minimapMatrix[2] = 0.0f
		HdGround.minimapMatrix[9] = 0.0f
		HdGround.minimapMatrix[11] = 0.0f
		HdGl.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		HdGl.glLoadMatrixf(HdGround.minimapMatrix, 0)
		HdGround.minimapMatrix[12] = 0.0f
		HdGround.minimapMatrix[4] = 0.0f
		HdGround.minimapMatrix[0] = 1.0f
		HdGround.minimapMatrix[14] = 0.0f
		HdGround.minimapMatrix[6] = 1.0f
		HdGround.minimapMatrix[8] = 0.0f
		HdGround.minimapMatrix[11] = 0.0f
		HdGround.minimapMatrix[13] = 0.0f
		HdGround.minimapMatrix[7] = 0.0f
		HdGround.minimapMatrix[5] = 0.0f
		HdGround.minimapMatrix[15] = 1.0f
		HdGround.minimapMatrix[2] = 0.0f
		HdGround.minimapMatrix[3] = 0.0f
		HdGround.minimapMatrix[9] = 1.0f
		HdGround.minimapMatrix[10] = 0.0f
		HdGround.minimapMatrix[1] = 0.0f
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		HdGl.glLoadMatrixf(HdGround.minimapMatrix, 0)
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
		var elementCount: Int = 0
		val gpuPacket: GlBitBuffer = this.glToolkit.textureBuffer
		var z: Int
		var tileIndex: Int
		var x: Int
		var vertexIndices: ShortArray?
		var i: Int
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
			val indexBuffer: HdElementsArrayBuffer = HdElementsArrayBuffer(this.glToolkit, 5123, gpuPacket.buffer, gpuPacket.currentPosition)
			this.glToolkit.drawElements(elementCount, indexBuffer, 0)
		}
	}

	override fun fa(x: Int, z: Int, shadow: Shadow?): Shadow? {
		if ((this.tileFlags[x][z].toInt() and 0x1) == 0) {
			return null
		}
		val shadowSize: Int = super.tileUnits shr this.glToolkit.shadowScaleShift
		val glShadow: HdShadow? = shadow as HdShadow?
		val glShadow2: HdShadow
		if (glShadow != null && glShadow.fitsDimension(shadowSize, shadowSize)) {
			glShadow2 = glShadow
			glShadow.reset()
		} else {
			glShadow2 = HdShadow(this.glToolkit, shadowSize, shadowSize)
		}
		glShadow2.setDimension(shadowSize, shadowSize, 0, 0)
		this.projectShadow(x, z, glShadow2)
		return glShadow2
	}

	override fun renderFloor(x: Int, z: Int, radius: Int, visibility: Array<BooleanArray?>?, belowLowestPlane: Boolean, orthoZoom: Int, skipFlags: Int) {
		this.renderGround(orthoZoom, radius, belowLowestPlane, x, skipFlags, visibility, z)
	}

	override fun addLight(pointLight: PointLight?, tileMask: IntArray?) {
		this.lights.addLast(HdLight(this.glToolkit, this, pointLight, tileMask))
	}

	override fun U(x: Int, z: Int, offsetsX: IntArray?, heightOffsets: IntArray?, offsetsZ: IntArray?, waterDepths: IntArray?, colours: IntArray?, blendedColours: IntArray?, materialIds: IntArray?, textureScales: IntArray?, waterColour: Int, waterDepth: Int, waterBias: Int, hasShadow: Boolean) {
		if (heightOffsets != null && this.tileHeightOffsets == null) {
			this.tileHeightOffsets = Array(super.width) { arrayOfNulls<IntArray>(super.length) }
		}
		val textureSource: MaterialProvider? = this.glToolkit.materialProvider
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
		val glGroundLayers: Array<HdGroundLayer?> = arrayOfNulls<HdGroundLayer>(colours!!.size)
		this.tileLayers!![x][z] = glGroundLayers
		for (i in 0 until colours.size) {
			var materialId: Int = materialIds!![i]
			var textureScale: Int = textureScales!![i]
			if ((this.featureFlags and 0x20) != 0 && materialId != -1 && textureSource!!.getMetrics(materialId)!!.detailsOnly) {
				materialId = -1
				textureScale = 128
			}
			val layerKey: Long = (waterColour.toLong() shl 28) or (waterDepth.toLong() shl 42) or (waterBias.toLong() shl 48) or (textureScale shl 14).toLong() or materialId.toLong()
			var node: Node?
			node = this.layerTable!!.get(layerKey)
			while (node != null) {
				val glGroundLayer: HdGroundLayer = node as HdGroundLayer
				if (materialId == glGroundLayer.materialId && glGroundLayer.textureScale == textureScale.toFloat() && glGroundLayer.waterFogColour == waterColour && waterDepth == glGroundLayer.waterDepth && waterBias == glGroundLayer.waterIntensity) {
					break
				}
				node = this.layerTable!!.nextWithKey()
			}
			if (node == null) {
				glGroundLayers[i] = HdGroundLayer(this, materialId, textureScale, waterColour, waterDepth, waterBias)
				this.layerTable!!.put(layerKey, glGroundLayers[i])
			} else {
				glGroundLayers[i] = node as HdGroundLayer
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

	// 634HD: tile overrides (tile_overrides.json) with worldTextures - the tile's untextured faces (or all,
	// with replace) take the override's material at its scale (scene units per repeat: 512 a tile)
	override fun hdRetexture(level: Int, x: Int, z: Int, underlayId: Int, overlayId: Int, textures: IntArray?, textureScales: IntArray?) {
		if (TALLY != null && textures != null && level == 0) {
			// client.test.hdFloorTally: the floor ids whose faces the game leaves untextured, and how many
			var untextured: Int = 0
			for (t in textures) if (t == -1) untextured++
			if (untextured > 0) {
				val key: Int = (underlayId shl 8) or overlayId
				TALLY[key] = (TALLY[key] ?: 0) + untextured
			}
		}
		if (!HdSettings.worldTextures || textures == null || textureScales == null) {
			return
		}
		val override = this.glToolkit.hdContent.tileOverrideFor(x + com.jagex.game.runetek5.MapRegion.sceneBaseX, z + com.jagex.game.runetek5.MapRegion.sceneBaseY, underlayId, overlayId) ?: return
		if (override.material < 0) {
			return
		}
		val scale: Int = (512.0f * override.scale).toInt()
		for (i in textures.indices) {
			if (textures[i] == -1 || override.replace) {
				textures[i] = override.material
				if (i < textureScales.size) {
					textureScales[i] = scale
				}
			}
		}
	}

	override fun ka(x: Int, z: Int, shadowLevel: Int) {
		if (shadowLevel > (this.shadowLevels!![x][z].toInt() and 0xFF)) {
			this.shadowLevels!![x][z] = shadowLevel.toByte()
		}
	}

	private fun renderGround(orthoZoom: Int, radius: Int, belowLowestPlane: Boolean, x: Int, skipFlags: Int, visibility: Array<BooleanArray?>?, z: Int) {
		if (this.layers == null) {
			return
		}
		RenderStats.category = RenderStats.CATEGORY_GROUND
		// 634HD: the ground's draws are marked, for its texture blending (HdSettings.terrainBlend); 2 when
		// its baked occlusion is bound, 3 while it has none yet (HdShadows.bindGroundOcclusion)
		HdGl.state.hdGround = this.glToolkit.hdFeatures.shadows.bindGroundOcclusion(this, 1000000L)
		try {
			this.renderGroundLayers(orthoZoom, radius, belowLowestPlane, x, skipFlags, visibility, z)
		} finally {
			HdGl.state.hdGround = 0
			RenderStats.category = RenderStats.CATEGORY_OTHER
		}
	}

	private fun renderGroundLayers(orthoZoom: Int, radius: Int, belowLowestPlane: Boolean, x: Int, skipFlags: Int, visibility: Array<BooleanArray?>?, z: Int) {
		val diameter: Int = radius + radius + 1
		val maxVisibleTiles: Int = diameter * diameter
		if (this.glToolkit.visibleTileIndices.size < maxVisibleTiles) {
			this.glToolkit.visibleTileIndices = IntArray(maxVisibleTiles)
		}
		if (this.glToolkit.textureBuffer.buffer!!.size < this.indexCount * 2) {
			this.glToolkit.textureBuffer = GlBitBuffer(this.indexCount * 2)
		}
		var minX: Int = x - radius
		val originX: Int = minX
		if (minX < 0) {
			minX = 0
		}
		var minZ: Int = z - radius
		val originZ: Int = minZ
		if (minZ < 0) {
			minZ = 0
		}
		var maxX: Int = radius + x
		if (maxX > super.width - 1) {
			maxX = super.width - 1
		}
		var maxZ: Int = z + radius
		if (maxZ > super.length - 1) {
			maxZ = super.length - 1
		}
		var visibleTileCount: Int = 0
		val visibleTileIndices: IntArray = this.glToolkit.visibleTileIndices
		var local136: Int
		for (i in minX..maxX) {
			val visibilityRow: BooleanArray? = visibility!![i - originX]
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
		for (j in 0 until this.layers!!.size) {
			this.layers!![j]!!.render(visibleTileCount, visibleTileIndices)
		}
		if (!this.lights.isEmpty()) {
			val fogColour: Int = this.glToolkit.fogColor
			val fogDensity: Int = this.glToolkit.fogDensity
			this.glToolkit.L(0, fogDensity, this.glToolkit.fogOffset)
			this.glToolkit.setLightingEnabled(false)
			this.glToolkit.setDepthMask(false)
			this.glToolkit.setBlendMode(128)
			this.glToolkit.renderTexture(-2)
			this.glToolkit.setActiveTexture(this.glToolkit.texture2d)
			this.glToolkit.loadTextureCombineMode(8448, 7681)
			this.glToolkit.setTexture(34166, 770, 0)
			this.glToolkit.setTextureTransparency(0, 34167)
			var node: Node? = this.lights.first()
			while (node != null) {
				val glGroundLight: HdLight = node as HdLight
				glGroundLight.drawLightMesh(visibility, x, z, radius)
				node = this.lights.next()
			}
			this.glToolkit.setTexture(5890, 768, 0)
			this.glToolkit.setTextureTransparency(0, 5890)
			this.glToolkit.setActiveTexture(null)
			this.glToolkit.L(fogColour, fogDensity, this.glToolkit.fogOffset)
		}
		// 634HD: the game's blob shadows under trees and other locs are left out while the sun's shadows
		// (HdSettings.shadows) draw them for real; both would darken the ground twice
		if (this.shadowMap == null || HdSettings.shadows != 0) {
			return
		}
		HdGl.glPushMatrix()
		HdGl.glTranslatef(0.0f, -1.0f, 0.0f)
		this.glToolkit.toggleDrawingClientstates(this.vertexBuffer, null, this.textureCoordinatesBuffer, null)
		this.shadowMap!!.renderShadowOverlay(belowLowestPlane, visibility, radius, z, x)
		HdGl.glPopMatrix()
		return
	}

	companion object {
		@JvmField
		val TALLY: HashMap<Int, Int>? = if (System.getProperty("client.test.hdFloorTally") != null) HashMap() else null

		/** The tally so far, most untextured faces first (underlay/overlay: faces). */
		@JvmStatic
		fun tallyReport(): String {
			val tally: HashMap<Int, Int> = TALLY ?: return ""
			val out: StringBuilder = StringBuilder()
			for ((key, faces) in tally.entries.sortedByDescending { it.value }.take(16)) {
				out.append("  underlay ").append(key shr 8).append(", overlay ").append(key and 0xFF).append(": ").append(faces).append(" untextured faces\n")
			}
			return out.toString()
		}

		@JvmField
		val minimapMatrix: FloatArray = FloatArray(16)
	}
}
