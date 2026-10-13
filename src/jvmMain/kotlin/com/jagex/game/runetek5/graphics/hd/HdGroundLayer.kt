/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlGroundLayer (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.cache.node.Node

import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer
import jaclib.memory.Stream
import jaclib.memory.heap.NativeHeapBuffer
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField

class HdGroundLayer : Node {

	private var colourHeapBuffer: NativeHeapBuffer? = null

	private var colourDrawBuffer: OpenGlArrayDrawBuffer? = null

	private var colourWriter: Stream? = null

	private val glGround: HdGround

	private val tileTriangleMasks: IntArray

	@JvmField
	val waterFogColour: Int

	@JvmField
	val textureScale: Float

	private val glToolkit: HdToolkit?

	@JvmField
	val materialId: Int

	@JvmField
	val waterDepth: Int

	@JvmField
	val waterIntensity: Int

	private val indexBuffer: HdElementsArrayBuffer

	constructor(glGround: HdGround?, materialId: Int, textureScale: Int, waterFogColour: Int, waterDepth: Int, waterIntensity: Int) {
		this.glGround = glGround!!
		this.tileTriangleMasks = IntArray(this.glGround.width * this.glGround.length)
		this.waterFogColour = waterFogColour
		this.textureScale = textureScale.toFloat()
		this.glToolkit = this.glGround.glToolkit
		this.materialId = materialId
		this.waterDepth = waterDepth
		this.waterIntensity = waterIntensity
		this.indexBuffer = HdElementsArrayBuffer(this.glToolkit, 5123, null, 1)
	}

	fun addTriangle(tileX: Int, tileZ: Int, triangle: Int) {
		this.tileTriangleMasks[tileZ * this.glGround.width + tileX] = this.tileTriangleMasks[tileZ * this.glGround.width + tileX] or (0x1 shl triangle)
	}

	fun initColours(vertexCount: Int) {
		this.colourHeapBuffer = this.glToolkit!!.nativeHeap!!.a(vertexCount * 4, true)
		this.colourWriter = Stream(this.colourHeapBuffer)
	}

	fun render(tileCount: Int, tileIndices: IntArray?) {
		var indexCount: Int = 0
		val gpuPacket: GlBitBuffer? = this.glToolkit!!.textureBuffer
		gpuPacket!!.currentPosition = 0
		var vertexIndices: ShortArray?
		var tileIndex: Int
		var mask: Int
		var i: Int
		var triangle: Int
		var pos: Int
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
		HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		HdGl.glPushMatrix()
		HdGl.glScalef(1.0f / this.textureScale, 1.0f / this.textureScale, 1.0f)
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		this.glToolkit.toggleDrawingClientstates(this.glGround.vertexBuffer, this.colourDrawBuffer, this.glGround.textureCoordinatesBuffer, this.glGround.normalsBuffer)
		this.glToolkit.drawElements(indexCount, this.indexBuffer, 0)
		HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		HdGl.glPopMatrix()
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	fun setVertexColour(vertex: Int, lightness: Int, colour: Int, brightness: Float) {
		var colour: Int = colour
		var local23: Int
		var local44: Int
		if (this.materialId != -1) {
			val textureMetrics: MaterialType? = this.glToolkit!!.materialProvider!!.getMetrics(this.materialId)
			val alpha: Int = textureMetrics!!.alpha.toInt() and 0xFF
			var local66: Int
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
				var red: Int = materialBrightness * ((colour shr 16) and 0xFF)
				if (red > 65535) {
					red = 65535
				}
				var green: Int = ((colour shr 8) and 0xFF) * materialBrightness
				if (green > 65535) {
					green = 65535
				}
				var blue: Int = (colour and 0xFF) * materialBrightness
				if (blue > 65535) {
					blue = 65535
				}
				colour = (green and 0xFF00) + (((red and 0xFA00FF00.toInt()) shl 8) + (blue shr 8))
			}
		}
		this.colourWriter!!.e(vertex * 4)
		if (brightness != 1.0f) {
			var red: Int = (colour shr 16) and 0xFF
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

	fun setVertexOpaque(vertex: Int) {
		this.colourWriter!!.e(vertex * 4 + 3)
		this.colourWriter!!.f(-1)
	}

	fun finishColours(vertexCount: Int) {
		this.colourWriter!!.a()
		val colourArrayBuffer: ArrayBufferGL? = this.glToolkit!!.createDataArrayBuffer(this.colourHeapBuffer, 4, vertexCount * 4)
		this.colourDrawBuffer = OpenGlArrayDrawBuffer(colourArrayBuffer, 5121, 4, 0)
		this.colourWriter = null
		this.colourHeapBuffer = null
	}
}
