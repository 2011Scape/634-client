/*
 * Not part of the original client: 634HD (toolkit 6), copied from ShadowTileGL (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.buffer.ElementsBufferGL
import kotlin.jvm.JvmField

class HdShadowTile {

	@JvmField
	var needsTextureRegeneration: Boolean = true

	private var lastModifiedPixelCount: Int = -1

	private val toolkit: HdToolkit?

	private val shadowMapper: HdShadowMap?

	private val tileLength: Int

	private val tileWidth: Int

	private val materialCount: Int

	private var cachedTexture: Hd2DTexture? = null

	private var elementsBuffer: ElementsBufferGL? = null

	private var indexBuffer: HdElementsArrayBuffer? = null

	constructor(glToolkit: HdToolkit?, glShadowMap: HdShadowMap?, glGround: HdGround?, chunkX: Int, chunkY: Int, sizeShift: Int, tileLength: Int, tileWidth: Int) {
		this.toolkit = glToolkit
		this.shadowMapper = glShadowMap
		this.tileLength = tileLength
		this.tileWidth = tileWidth
		val chunkSize: Int = 0x1 shl sizeShift
		var materialCount: Int = 0
		val chunkStartX: Int = chunkX shl sizeShift
		val chunkStartY: Int = chunkY shl sizeShift
		var local52: Int
		var i: Int = 0
		while (i < chunkSize) {
			var materialOffset: Int = glGround!!.width * (chunkStartY + i) + chunkStartX
			var column: Int = 0
			while (column < chunkSize) {
				val materialIndices: ShortArray? = glGround.tileVertexIndices[materialOffset++]
				if (materialIndices != null) {
					materialCount += materialIndices.size
				}
				column++
			}
			i++
		}
		this.materialCount = materialCount
		if (materialCount <= 0) {
			this.cachedTexture = null
		} else {
			val packet: Packet = Packet(materialCount * 2)
			var dx: Int
			var materialIndices: ShortArray?
			var matIdx: Int
			var materialOffset: Int
			if (this.toolkit!!.bigEndianess) {
				var row: Int = 0
				while (row < chunkSize) {
					materialOffset = chunkStartX + (row + chunkStartY) * glGround!!.width
					dx = 0
					while (dx < chunkSize) {
						materialIndices = glGround.tileVertexIndices[materialOffset++]
						if (materialIndices != null) {
							matIdx = 0
							while (matIdx < materialIndices.size) {
								packet.writeShort(materialIndices[matIdx].toInt() and 0xFFFF)
								matIdx++
							}
						}
						dx++
					}
					row++
				}
			} else {
				var row: Int = 0
				while (row < chunkSize) {
					materialOffset = (chunkStartY + row) * glGround!!.width + chunkStartX
					dx = 0
					while (dx < chunkSize) {
						materialIndices = glGround.tileVertexIndices[materialOffset++]
						if (materialIndices != null) {
							matIdx = 0
							while (matIdx < materialIndices.size) {
								packet.writeShortLE(materialIndices[matIdx].toInt() and 0xFFFF)
								matIdx++
							}
						}
						dx++
					}
					row++
				}
			}
			this.elementsBuffer = this.toolkit.createElementsArrayBuffer(packet.currentPosition, packet.buffer, false)
			this.indexBuffer = HdElementsArrayBuffer(this.toolkit, 5123, null, 1)
		}
	}

	fun drawShadowWithIndices(data: ByteArray?, indexCount: Int) {
		this.indexBuffer!!.putElements(data, this.toolkit!!.getDataTypeSize(5123) * indexCount)
		this.drawShadowElements(this.indexBuffer, indexCount)
	}

	fun drawShadow() {
		this.drawShadowElements(this.elementsBuffer, this.materialCount)
	}

	private fun drawShadowElements(glBinding: ElementsBufferGL?, indexCount: Int) {
		if (indexCount != 0) {
			this.updateTexture()
			this.toolkit!!.setActiveTexture(this.cachedTexture)
			this.toolkit.drawElements(indexCount, glBinding, 0)
		}
	}

	private fun updateTexture() {
		if (!this.needsTextureRegeneration) {
			return
		}
		this.needsTextureRegeneration = false
		val shadowMapperData: ByteArray = this.shadowMapper!!.shadowMapperData!!
		val shadowTextureData: ByteArray? = this.toolkit!!.shadowTextureData
		var modifiedPixelCount: Int = 0
		val shadowMapperWidth: Int = this.shadowMapper.width
		var shadowOffset: Int = this.tileLength + this.tileWidth * this.shadowMapper.width
		var local54: Int
		var i: Int = -128
		while (i < 0) {
			modifiedPixelCount = (modifiedPixelCount shl 8) - modifiedPixelCount
			var column: Int = -128
			while (column < 0) {
				if (shadowMapperData[shadowOffset++].toInt() != 0) {
					modifiedPixelCount++
				}
				column++
			}
			shadowOffset += shadowMapperWidth - 128
			i++
		}
		if (this.cachedTexture != null && modifiedPixelCount == this.lastModifiedPixelCount) {
			this.needsTextureRegeneration = false
			return
		}
		this.lastModifiedPixelCount = modifiedPixelCount
		shadowOffset = this.tileLength + shadowMapperWidth * this.tileWidth
		var texelIndex: Int = 0
		var j: Int = -128
		while (j < 0) {
			var k: Int = -128
			while (k < 0) {
				if (shadowMapperData[shadowOffset].toInt() == 0) {
					var neighborCount: Int = 0
					if (shadowMapperData[shadowOffset - 1].toInt() != 0) {
						neighborCount++
					}
					if (shadowMapperData[shadowOffset + 1].toInt() != 0) {
						neighborCount++
					}
					if (shadowMapperData[shadowOffset - shadowMapperWidth].toInt() != 0) {
						neighborCount++
					}
					if (shadowMapperData[shadowMapperWidth + shadowOffset].toInt() != 0) {
						neighborCount++
					}
					shadowTextureData!![texelIndex++] = (neighborCount * 17).toByte()
				} else {
					shadowTextureData!![texelIndex++] = 68
				}
				shadowOffset++
				k++
			}
			shadowOffset += this.shadowMapper.width - 128
			j++
		}
		if (this.cachedTexture == null) {
			this.cachedTexture = Hd2DTexture(this.toolkit, 3553, 6406, 128, 128, false, this.toolkit.shadowTextureData, 6406, false)
			this.cachedTexture!!.setRepeat(false, false)
			this.cachedTexture!!.setLinearFilter(true)
		} else {
			this.cachedTexture!!.uploadTextureData(128, false, 128, 6406, this.toolkit.shadowTextureData)
		}
	}
}
