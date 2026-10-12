package com.jagex.game.runetek5.cache.node

import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import com.jagex.game.runetek5.scene.ground.GlGround
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlElementsArrayBuffer
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer
import jaclib.memory.Stream
import jaclib.memory.heap.NativeHeapBuffer
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!jn")
class GlGroundLayer : Node {

	@OriginalMember(owner = "client!jn", name = "D", descriptor = "Lclient!jaclib/memory/heap/NativeHeapBuffer;")
	private var colourHeapBuffer: NativeHeapBuffer? = null

	@OriginalMember(owner = "client!jn", name = "I", descriptor = "Lclient!nh;")
	private var colourDrawBuffer: OpenGlArrayDrawBuffer? = null

	@OriginalMember(owner = "client!jn", name = "K", descriptor = "Lclient!jaclib/memory/Stream;")
	private var colourWriter: Stream? = null

	@OriginalMember(owner = "client!jn", name = "C", descriptor = "Lclient!ed;")
	private val glGround: GlGround

	@OriginalMember(owner = "client!jn", name = "G", descriptor = "[I")
	private val tileTriangleMasks: IntArray

	@OriginalMember(owner = "client!jn", name = "o", descriptor = "I")
	@JvmField
	val waterFogColour: Int

	@OriginalMember(owner = "client!jn", name = "v", descriptor = "F")
	@JvmField
	val textureScale: Float

	@OriginalMember(owner = "client!jn", name = "u", descriptor = "Lclient!qo;")
	private val glToolkit: GlToolkit?

	@OriginalMember(owner = "client!jn", name = "F", descriptor = "I")
	@JvmField
	val materialId: Int

	@OriginalMember(owner = "client!jn", name = "w", descriptor = "I")
	@JvmField
	val waterDepth: Int

	@OriginalMember(owner = "client!jn", name = "p", descriptor = "I")
	@JvmField
	val waterIntensity: Int

	@OriginalMember(owner = "client!jn", name = "H", descriptor = "Lclient!wv;")
	private val indexBuffer: GlElementsArrayBuffer

	@OriginalMember(owner = "client!jn", name = "<init>", descriptor = "(Lclient!ed;IIIII)V")
	constructor(@OriginalArg(0) glGround: GlGround?, @OriginalArg(1) materialId: Int, @OriginalArg(2) textureScale: Int, @OriginalArg(3) waterFogColour: Int, @OriginalArg(4) waterDepth: Int, @OriginalArg(5) waterIntensity: Int) {
		this.glGround = glGround!!
		this.tileTriangleMasks = IntArray(this.glGround.width * this.glGround.length)
		this.waterFogColour = waterFogColour
		this.textureScale = textureScale.toFloat()
		this.glToolkit = this.glGround.glToolkit
		this.materialId = materialId
		this.waterDepth = waterDepth
		this.waterIntensity = waterIntensity
		this.indexBuffer = GlElementsArrayBuffer(this.glToolkit, 5123, null, 1)
	}

	@OriginalMember(owner = "client!jn", name = "a", descriptor = "(IBII)V")
	fun addTriangle(@OriginalArg(0) tileX: Int, @OriginalArg(2) tileZ: Int, @OriginalArg(3) triangle: Int) {
		this.tileTriangleMasks[tileZ * this.glGround.width + tileX] = this.tileTriangleMasks[tileZ * this.glGround.width + tileX] or (0x1 shl triangle)
	}

	@OriginalMember(owner = "client!jn", name = "c", descriptor = "(II)V")
	fun initColours(@OriginalArg(1) vertexCount: Int) {
		this.colourHeapBuffer = this.glToolkit!!.nativeHeap!!.a(vertexCount * 4, true)
		this.colourWriter = Stream(this.colourHeapBuffer)
	}

	@OriginalMember(owner = "client!jn", name = "a", descriptor = "(II[I)V")
	fun render(@OriginalArg(1) tileCount: Int, @OriginalArg(2) tileIndices: IntArray?) {
		@Pc(7) var indexCount: Int = 0
		@Pc(11) val gpuPacket: GlBitBuffer? = this.glToolkit!!.textureBuffer
		gpuPacket!!.currentPosition = 0
		@Pc(32) var vertexIndices: ShortArray?
		@Pc(26) var tileIndex: Int
		@Pc(37) var mask: Int
		@Pc(20) var i: Int
		@Pc(46) var triangle: Int
		@Pc(48) var pos: Int
		if (this.glToolkit.bigEndianess) {
			i = 0
			while (i < tileCount) {
				tileIndex = tileIndices!![i]
				vertexIndices = this.glGround.tileVertexIndices[tileIndex]
				mask = this.tileTriangleMasks[tileIndex]
				if (mask != 0 && vertexIndices != null) {
					triangle = 0
					pos = 0
					while (vertexIndices.size > pos) {
						if ((mask and (0x1 shl triangle++)) == 0) {
							pos += 3
						} else {
							gpuPacket.writeShort(vertexIndices[pos++].toInt() and 0xFFFF)
							indexCount++
							gpuPacket.writeShort(vertexIndices[pos++].toInt() and 0xFFFF)
							indexCount++
							indexCount++
							gpuPacket.writeShort(vertexIndices[pos++].toInt() and 0xFFFF)
						}
					}
				}
				i++
			}
		} else {
			i = 0
			while (i < tileCount) {
				tileIndex = tileIndices!![i]
				mask = this.tileTriangleMasks[tileIndex]
				vertexIndices = this.glGround.tileVertexIndices[tileIndex]
				if (mask != 0 && vertexIndices != null) {
					triangle = 0
					pos = 0
					while (pos < vertexIndices.size) {
						if ((mask and (0x1 shl triangle++)) == 0) {
							pos += 3
						} else {
							indexCount++
							gpuPacket.writeShortLE(vertexIndices[pos++].toInt() and 0xFFFF)
							gpuPacket.writeShortLE(vertexIndices[pos++].toInt() and 0xFFFF)
							indexCount++
							indexCount++
							gpuPacket.writeShortLE(vertexIndices[pos++].toInt() and 0xFFFF)
						}
					}
				}
				i++
			}
		}
		if (indexCount <= 0) {
			return
		}
		this.indexBuffer.putElements(gpuPacket.buffer, gpuPacket.currentPosition)
		this.glToolkit.toggleDrawingClientstates(this.glGround.vertexBuffer, this.colourDrawBuffer, this.glGround.textureCoordinatesBuffer, this.glGround.normalsBuffer)
		this.glToolkit.renderTexture((this.glGround.featureFlags and 0x7) != 0, this.materialId, (this.glGround.featureFlags and 0x8) != 0)
		if (this.glToolkit.stillWaterToggled) {
			this.glToolkit.EA(Int.MAX_VALUE, this.waterFogColour, this.waterDepth, this.waterIntensity)
		}
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		OpenGL.glPushMatrix()
		OpenGL.glScalef(1.0f / this.textureScale, 1.0f / this.textureScale, 1.0f)
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		this.glToolkit.toggleDrawingClientstates(this.glGround.vertexBuffer, this.colourDrawBuffer, this.glGround.textureCoordinatesBuffer, this.glGround.normalsBuffer)
		this.glToolkit.drawElements(indexCount, this.indexBuffer, 0)
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		OpenGL.glPopMatrix()
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	@OriginalMember(owner = "client!jn", name = "a", descriptor = "(BIIIF)V")
	fun setVertexColour(@OriginalArg(1) vertex: Int, @OriginalArg(2) lightness: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) brightness: Float) {
		var colour: Int = colour
		@Pc(23) var local23: Int
		@Pc(44) var local44: Int
		if (this.materialId != -1) {
			@Pc(18) val textureMetrics: MaterialType? = this.glToolkit!!.materialProvider!!.getMetrics(this.materialId)
			val alpha: Int = textureMetrics!!.alpha.toInt() and 0xFF
			@Pc(66) var local66: Int
			if (alpha != 0 && textureMetrics.effectType.toInt() != 4) {
				val lightnessColour: Int
				if (lightness < 0) {
					lightnessColour = 0
				} else if (lightness > 127) {
					lightnessColour = 16777215
				} else {
					lightnessColour = lightness * 131586
				}
				if (alpha == 256) {
					colour = lightnessColour
				} else {
					val inverseAlpha: Int = 256 - alpha
					colour = ((((inverseAlpha * (colour and 0xFF00FF)) + ((lightnessColour and 0xFF00FF) * alpha)) and 0xFF00FF00.toInt()) + (((inverseAlpha * (colour and 0xFF00)) + (alpha * (lightnessColour and 0xFF00))) and 0xFF0000)) shr 8
				}
			}
			var materialBrightness: Int = textureMetrics.brightness.toInt() and 0xFF
			if (materialBrightness != 0) {
				materialBrightness += 256
				@Pc(120) var red: Int = materialBrightness * ((colour shr 16) and 0xFF)
				if (red > 65535) {
					red = 65535
				}
				var green: Int = ((colour shr 8) and 0xFF) * materialBrightness
				if (green > 65535) {
					green = 65535
				}
				@Pc(148) var blue: Int = (colour and 0xFF) * materialBrightness
				if (blue > 65535) {
					blue = 65535
				}
				colour = (green and 0xFF00) + (((red and 0xFA00FF00.toInt()) shl 8) + (blue shr 8))
			}
		}
		this.colourWriter!!.e(vertex * 4)
		if (brightness != 1.0f) {
			@Pc(190) var red: Int = (colour shr 16) and 0xFF
			var green: Int = (colour shr 8) and 0xFF
			var blue: Int = colour and 0xFF
			red = (red.toFloat() * brightness).toInt()
			if (red < 0) {
				red = 0
			} else if (red > 255) {
				red = 255
			}
			green = (green.toFloat() * brightness).toInt()
			blue = (blue.toFloat() * brightness).toInt()
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
			colour = (green shl 8) or (red shl 16) or blue
		}
		this.colourWriter!!.f((colour shr 16).toByte().toInt())
		this.colourWriter!!.f((colour shr 8).toByte().toInt())
		this.colourWriter!!.f(colour.toByte().toInt())
	}

	@OriginalMember(owner = "client!jn", name = "d", descriptor = "(II)V")
	fun setVertexOpaque(@OriginalArg(1) vertex: Int) {
		this.colourWriter!!.e(vertex * 4 + 3)
		this.colourWriter!!.f(-1)
	}

	@OriginalMember(owner = "client!jn", name = "e", descriptor = "(II)V")
	fun finishColours(@OriginalArg(1) vertexCount: Int) {
		this.colourWriter!!.a()
		@Pc(22) val colourArrayBuffer: ArrayBufferGL? = this.glToolkit!!.createDataArrayBuffer(this.colourHeapBuffer, 4, vertexCount * 4)
		this.colourDrawBuffer = OpenGlArrayDrawBuffer(colourArrayBuffer, 5121, 4, 0)
		this.colourWriter = null
		this.colourHeapBuffer = null
	}
}
