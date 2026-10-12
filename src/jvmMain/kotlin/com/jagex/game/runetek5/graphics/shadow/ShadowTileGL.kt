package com.jagex.game.runetek5.graphics.shadow

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.ElementsBufferGL
import com.jagex.game.runetek5.scene.ground.GlGround
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlElementsArrayBuffer
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture
import com.jagex.game.runetek5.graphics.gl.GlShadowMap
import com.jagex.game.runetek5.graphics.gl.GlToolkit

@OriginalClass("client!sm")
class ShadowTileGL {

	@OriginalMember(owner = "client!sm", name = "l", descriptor = "Z")
	@JvmField
	var needsTextureRegeneration: Boolean = true

	@OriginalMember(owner = "client!sm", name = "m", descriptor = "I")
	private var lastModifiedPixelCount: Int = -1

	@OriginalMember(owner = "client!sm", name = "c", descriptor = "Lclient!qo;")
	private val toolkit: GlToolkit?

	@OriginalMember(owner = "client!sm", name = "b", descriptor = "Lclient!mha;")
	private val shadowMapper: GlShadowMap?

	@OriginalMember(owner = "client!sm", name = "f", descriptor = "I")
	private val tileLength: Int

	@OriginalMember(owner = "client!sm", name = "e", descriptor = "I")
	private val tileWidth: Int

	@OriginalMember(owner = "client!sm", name = "k", descriptor = "I")
	private val materialCount: Int

	@OriginalMember(owner = "client!sm", name = "a", descriptor = "Lclient!tp;")
	private var cachedTexture: OpenGl2DTexture? = null

	@OriginalMember(owner = "client!sm", name = "d", descriptor = "Lclient!ni;")
	private var elementsBuffer: ElementsBufferGL? = null

	@OriginalMember(owner = "client!sm", name = "n", descriptor = "Lclient!wv;")
	private var indexBuffer: GlElementsArrayBuffer? = null

	@OriginalMember(owner = "client!sm", name = "<init>", descriptor = "(Lclient!qo;Lclient!mha;Lclient!ed;IIIII)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) glShadowMap: GlShadowMap?, @OriginalArg(2) glGround: GlGround?, @OriginalArg(3) chunkX: Int, @OriginalArg(4) chunkY: Int, @OriginalArg(5) sizeShift: Int, @OriginalArg(6) tileLength: Int, @OriginalArg(7) tileWidth: Int) {
		this.toolkit = glToolkit
		this.shadowMapper = glShadowMap
		this.tileLength = tileLength
		this.tileWidth = tileWidth
		@Pc(25) val chunkSize: Int = 0x1 shl sizeShift
		@Pc(27) var materialCount: Int = 0
		@Pc(31) val chunkStartX: Int = chunkX shl sizeShift
		@Pc(35) val chunkStartY: Int = chunkY shl sizeShift
		@Pc(52) var local52: Int
		@Pc(37) var i: Int = 0
		while (i < chunkSize) {
			@Pc(50) var materialOffset: Int = glGround!!.width * (chunkStartY + i) + chunkStartX
			var column: Int = 0
			while (column < chunkSize) {
				@Pc(60) val materialIndices: ShortArray? = glGround.tileVertexIndices[materialOffset++]
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
			@Pc(99) val packet: Packet = Packet(materialCount * 2)
			@Pc(120) var dx: Int
			@Pc(128) var materialIndices: ShortArray?
			@Pc(132) var matIdx: Int
			@Pc(118) var materialOffset: Int
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
			this.indexBuffer = GlElementsArrayBuffer(this.toolkit, 5123, null, 1)
		}
	}

	@OriginalMember(owner = "client!sm", name = "a", descriptor = "([BIIB)V")
	fun drawShadowWithIndices(@OriginalArg(0) data: ByteArray?, @OriginalArg(2) indexCount: Int) {
		this.indexBuffer!!.putElements(data, this.toolkit!!.getDataTypeSize(5123) * indexCount)
		this.drawShadowElements(this.indexBuffer, indexCount)
	}

	@OriginalMember(owner = "client!sm", name = "a", descriptor = "(B)V")
	fun drawShadow() {
		this.drawShadowElements(this.elementsBuffer, this.materialCount)
	}

	@OriginalMember(owner = "client!sm", name = "a", descriptor = "(Lclient!ni;II)V")
	private fun drawShadowElements(@OriginalArg(0) glBinding: ElementsBufferGL?, @OriginalArg(2) indexCount: Int) {
		if (indexCount != 0) {
			this.updateTexture()
			this.toolkit!!.setActiveTexture(this.cachedTexture)
			this.toolkit.drawElements(indexCount, glBinding, 0)
		}
	}

	@OriginalMember(owner = "client!sm", name = "b", descriptor = "(B)V")
	private fun updateTexture() {
		if (!this.needsTextureRegeneration) {
			return
		}
		this.needsTextureRegeneration = false
		@Pc(16) val shadowMapperData: ByteArray = this.shadowMapper!!.shadowMapperData!!
		@Pc(20) val shadowTextureData: ByteArray? = this.toolkit!!.shadowTextureData
		@Pc(26) var modifiedPixelCount: Int = 0
		@Pc(30) val shadowMapperWidth: Int = this.shadowMapper.width
		@Pc(41) var shadowOffset: Int = this.tileLength + this.tileWidth * this.shadowMapper.width
		@Pc(54) var local54: Int
		@Pc(43) var i: Int = -128
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
		@Pc(104) var j: Int = -128
		while (j < 0) {
			@Pc(108) var k: Int = -128
			while (k < 0) {
				if (shadowMapperData[shadowOffset].toInt() == 0) {
					@Pc(126) var neighborCount: Int = 0
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
			this.cachedTexture = OpenGl2DTexture(this.toolkit, 3553, 6406, 128, 128, false, this.toolkit.shadowTextureData, 6406, false)
			this.cachedTexture!!.setRepeat(false, false)
			this.cachedTexture!!.setLinearFilter(true)
		} else {
			this.cachedTexture!!.uploadTextureData(128, false, 128, 6406, this.toolkit.shadowTextureData)
		}
	}
}
