package com.jagex.game.runetek5.cache.node

import com.jagex.game.runetek5.NativeMatrix
import com.jagex.game.runetek5.PrimitiveType
import com.jagex.game.runetek5.TextureTransformMode
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.graphics.GpuIndexBuffer
import com.jagex.game.runetek5.scene.ground.GpuFloorModel
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.toolkit.companion.NativeStream
import jaclib.memory.Buffer
import jaclib.memory.Stream
import jaclib.memory.heap.NativeHeapBuffer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!eu")
class GpuFloorLayer : Node {

	@OriginalMember(owner = "client!eu", name = "u", descriptor = "Lclient!jaclib/memory/heap/NativeHeapBuffer;")
	private var colourHeapBuffer: NativeHeapBuffer? = null

	@OriginalMember(owner = "client!eu", name = "w", descriptor = "Lclient!jaclib/memory/Stream;")
	private var colourWriter: Stream? = null

	@OriginalMember(owner = "client!eu", name = "z", descriptor = "Lclient!tc;")
	private var colourStream: NativeStream? = null

	@OriginalMember(owner = "client!eu", name = "M", descriptor = "I")
	@JvmField
	var triangleCount: Int = 0

	@OriginalMember(owner = "client!eu", name = "o", descriptor = "Lclient!qm;")
	private val gpuFloorModel: GpuFloorModel

	@OriginalMember(owner = "client!eu", name = "r", descriptor = "I")
	@JvmField
	val waterIntensity: Int

	@OriginalMember(owner = "client!eu", name = "E", descriptor = "F")
	@JvmField
	val textureScale: Float

	@OriginalMember(owner = "client!eu", name = "N", descriptor = "Lclient!wga;")
	private val nativeToolkit: AbstractNativeToolkit?

	@OriginalMember(owner = "client!eu", name = "x", descriptor = "I")
	@JvmField
	val waterDepth: Int

	@OriginalMember(owner = "client!eu", name = "p", descriptor = "I")
	@JvmField
	val materialId: Int

	@OriginalMember(owner = "client!eu", name = "v", descriptor = "[I")
	private val tileTriangleMasks: IntArray

	@OriginalMember(owner = "client!eu", name = "t", descriptor = "I")
	@JvmField
	val waterFogColour: Int

	@OriginalMember(owner = "client!eu", name = "<init>", descriptor = "(Lclient!qm;IIIII)V")
	constructor(@OriginalArg(0) gpuFloorModel: GpuFloorModel?, @OriginalArg(1) materialId: Int, @OriginalArg(2) textureScale: Int, @OriginalArg(3) waterFogColour: Int, @OriginalArg(4) waterDepth: Int, @OriginalArg(5) waterIntensity: Int) {
		this.gpuFloorModel = gpuFloorModel!!
		this.waterIntensity = waterIntensity
		this.textureScale = textureScale.toFloat()
		this.nativeToolkit = this.gpuFloorModel.nativeToolkit
		this.waterDepth = waterDepth
		this.materialId = materialId
		this.tileTriangleMasks = IntArray(this.gpuFloorModel.length * this.gpuFloorModel.width)
		this.waterFogColour = waterFogColour
	}

	@OriginalMember(owner = "client!eu", name = "a", descriptor = "(BI)V")
	fun initColours(@OriginalArg(1) vertexCount: Int) {
		this.colourHeapBuffer = this.nativeToolkit!!.generateNativeHeapBuffer(true, vertexCount * 4)
		this.colourWriter = Stream(this.colourHeapBuffer, 0, vertexCount * 4)
	}

	@OriginalMember(owner = "client!eu", name = "a", descriptor = "(B[II)V")
	fun render(@OriginalArg(1) tileIndices: IntArray?, @OriginalArg(2) tileCount: Int) {
		@Pc(14) val gpuIndexBuffer: GpuIndexBuffer? = this.nativeToolkit!!.getOrResizeIndexBuffer(this.triangleCount * 3)
		@Pc(19) val buffer: Buffer? = gpuIndexBuffer!!.lock()
		if (buffer == null) {
			return
		}
		@Pc(35) val stream: Stream? = this.nativeToolkit.wrapBuffer(buffer)
		@Pc(37) var drawnTriangles: Int = 0
		@Pc(39) var minIndex: Int = 32767
		@Pc(41) var maxIndex: Int = -32768
		@Pc(100) var vertexIndex: Int
		@Pc(57) var vertexIndices: ShortArray?
		@Pc(51) var tileIndex: Int
		@Pc(62) var mask: Int
		@Pc(45) var i: Int
		@Pc(71) var triangle: Int
		@Pc(73) var pos: Int
		@Pc(91) var corner: Int
		if (Stream.c()) {
			i = 0
			label118@ while (i < tileCount) {
				tileIndex = tileIndices!![i]
				vertexIndices = this.gpuFloorModel.tileVertexIndices[tileIndex]
				mask = this.tileTriangleMasks[tileIndex]
				if (mask != 0 && vertexIndices != null) {
					triangle = 0
					pos = 0
					while (true) {
						while (true) {
							if (vertexIndices.size <= pos) {
								i++
								continue@label118
							}
							if ((mask and (0x1 shl triangle++)) == 0) {
								pos += 3
							} else {
								drawnTriangles++
								corner = 0
								while (corner < 3) {
									vertexIndex = vertexIndices[pos++].toInt() and 0xFFFF
									if (minIndex > vertexIndex) {
										minIndex = vertexIndex
									}
									stream!!.d(vertexIndex)
									if (vertexIndex > maxIndex) {
										maxIndex = vertexIndex
									}
									corner++
								}
							}
						}
					}
				}
				i++
			}
		} else {
			i = 0
			label94@ while (i < tileCount) {
				tileIndex = tileIndices!![i]
				vertexIndices = this.gpuFloorModel.tileVertexIndices[tileIndex]
				mask = this.tileTriangleMasks[tileIndex]
				if (mask != 0 && vertexIndices != null) {
					triangle = 0
					pos = 0
					while (true) {
						while (true) {
							if (pos >= vertexIndices.size) {
								i++
								continue@label94
							}
							if (((0x1 shl triangle++) and mask) == 0) {
								pos += 3
							} else {
								drawnTriangles++
								corner = 0
								while (corner < 3) {
									vertexIndex = vertexIndices[pos++].toInt() and 0xFFFF
									if (vertexIndex < minIndex) {
										minIndex = vertexIndex
									}
									stream!!.a(vertexIndex)
									if (maxIndex < vertexIndex) {
										maxIndex = vertexIndex
									}
									corner++
								}
							}
						}
					}
				}
				i++
			}
		}
		stream!!.a()
		if (!gpuIndexBuffer.unlock() || drawnTriangles <= 0) {
			return
		}
		this.nativeToolkit.setActiveMaterial((this.gpuFloorModel.featureFlags and 0x8) != 0, (this.gpuFloorModel.featureFlags and 0x7) != 0, this.materialId)
		if (this.nativeToolkit.missingWaterColor) {
			this.nativeToolkit.EA(Int.MAX_VALUE, this.waterFogColour, this.waterDepth, this.waterIntensity)
		}
		@Pc(296) val textureMatrix: NativeMatrix? = this.nativeToolkit.getCurrentTextureMatrix()
		textureMatrix!!.setScale(1.0f, 1.0f / this.textureScale, 1.0f / this.textureScale)
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
		this.nativeToolkit.setStreamSource(this.colourStream, 1)
		this.nativeToolkit.setVertexDeclaration(this.gpuFloorModel.vertexDeclaration)
		this.nativeToolkit.drawIndexedPrimitive(PrimitiveType.aClass134_6, drawnTriangles, gpuIndexBuffer, minIndex, 0, maxIndex + 1 - minIndex)
		this.nativeToolkit.resetCurrentTextureMatrix()
	}

	@OriginalMember(owner = "client!eu", name = "c", descriptor = "(II)V")
	fun finishColours(@OriginalArg(1) vertexCount: Int) {
		this.colourWriter!!.a()
		this.colourStream = this.nativeToolkit!!.createStream(false)
		this.colourStream!!.createFromSource(this.colourHeapBuffer, 4, vertexCount * 4)
		this.colourHeapBuffer = null
		this.colourWriter = null
	}

	@OriginalMember(owner = "client!eu", name = "a", descriptor = "(IFIII)V")
	fun setVertexColour(@OriginalArg(0) vertex: Int, @OriginalArg(1) brightness: Float, @OriginalArg(3) lightness: Int, @OriginalArg(4) colour: Int) {
		var colour: Int = colour
		@Pc(19) var local19: Int
		@Pc(35) var local35: Int
		if (this.materialId != -1) {
			@Pc(14) val textureMetrics: MaterialType? = this.nativeToolkit!!.materialProvider!!.getMetrics(this.materialId)
			val alpha: Int = textureMetrics!!.alpha.toInt() and 0xFF
			@Pc(60) var local60: Int
			if (alpha != 0 && textureMetrics.effectType.toInt() != 4) {
				val lightnessColour: Int
				if (lightness < 0) {
					lightnessColour = 0
				} else if (lightness <= 127) {
					lightnessColour = lightness * 131586
				} else {
					lightnessColour = 16777215
				}
				if (alpha == 256) {
					colour = lightnessColour
				} else {
					val inverseAlpha: Int = 256 - alpha
					colour = ((((inverseAlpha * (colour and 0xFF00)) + (alpha * (lightnessColour and 0xFF00))) and 0xFF0000) + (((alpha * (lightnessColour and 0xFF00FF)) + ((colour and 0xFF00FF) * inverseAlpha)) and 0xFF00FF00.toInt())) shr 8
				}
			}
			var materialBrightness: Int = textureMetrics.brightness.toInt() and 0xFF
			if (materialBrightness != 0) {
				materialBrightness += 256
				@Pc(114) var red: Int = ((colour shr 16) and 0xFF) * materialBrightness
				if (red > 65535) {
					red = 65535
				}
				var green: Int = ((colour shr 8) and 0xFF) * materialBrightness
				if (green > 65535) {
					green = 65535
				}
				@Pc(138) var blue: Int = materialBrightness * (colour and 0xFF)
				if (blue > 65535) {
					blue = 65535
				}
				colour = (blue shr 8) + (green and 0xFF00) + ((red and 0x5400FF00) shl 8)
			}
		}
		this.colourWriter!!.e(vertex * 4)
		if (brightness != 1.0f) {
			@Pc(177) var red: Int = (colour shr 16) and 0xFF
			var green: Int = (colour shr 8) and 0xFF
			red = (red.toFloat() * brightness).toInt()
			var blue: Int = colour and 0xFF
			if (red < 0) {
				red = 0
			} else if (red > 255) {
				red = 255
			}
			green = (green.toFloat() * brightness).toInt()
			if (green < 0) {
				green = 0
			} else if (green > 255) {
				green = 255
			}
			blue = (blue.toFloat() * brightness).toInt()
			if (blue < 0) {
				blue = 0
			} else if (blue > 255) {
				blue = 255
			}
			colour = blue or (red shl 16) or (green shl 8)
		}
		if (this.nativeToolkit!!.endiannessType == 0) {
			this.colourWriter!!.f(colour.toByte().toInt())
			this.colourWriter!!.f((colour shr 8).toByte().toInt())
			this.colourWriter!!.f((colour shr 16).toByte().toInt())
		} else {
			this.colourWriter!!.f((colour shr 16).toByte().toInt())
			this.colourWriter!!.f((colour shr 8).toByte().toInt())
			this.colourWriter!!.f(colour.toByte().toInt())
		}
	}

	@OriginalMember(owner = "client!eu", name = "a", descriptor = "(IIIB)V")
	fun addTriangle(@OriginalArg(0) triangle: Int, @OriginalArg(1) tileZ: Int, @OriginalArg(2) tileX: Int) {
		this.tileTriangleMasks[tileZ * this.gpuFloorModel.width + tileX] = this.tileTriangleMasks[tileZ * this.gpuFloorModel.width + tileX] or (0x1 shl triangle)
		this.triangleCount++
	}

	@OriginalMember(owner = "client!eu", name = "b", descriptor = "(BI)V")
	fun setVertexOpaque(@OriginalArg(1) vertex: Int) {
		this.colourWriter!!.e(vertex * 4 + 3)
		this.colourWriter!!.f(-1)
	}
}
