package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.BrightnessTableBuilder
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpConstant
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!lr")
class TextureType : CacheableNode {

	@OriginalMember(owner = "client!lr", name = "z", descriptor = "[I")
	private val materialFileIds: IntArray

	@OriginalMember(owner = "client!lr", name = "A", descriptor = "[I")
	private val textureFileIds: IntArray

	@OriginalMember(owner = "client!lr", name = "E", descriptor = "Lclient!tf;")
	private val hdrOperation: TextureOperation?

	@OriginalMember(owner = "client!lr", name = "x", descriptor = "Lclient!tf;")
	private val opaqueOperation: TextureOperation?

	@OriginalMember(owner = "client!lr", name = "D", descriptor = "Lclient!tf;")
	private val opacityOperation: TextureOperation?

	@OriginalMember(owner = "client!lr", name = "w", descriptor = "[Lclient!tf;")
	private val textureOperations: Array<TextureOperation?>

	@OriginalMember(owner = "client!lr", name = "<init>", descriptor = "()V")
	private constructor() : super() {
		this.materialFileIds = IntArray(0)
		this.textureFileIds = IntArray(0)
		this.hdrOperation = TextureOpConstant(0)
		this.hdrOperation.imageCacheCapacity = 1
		this.opaqueOperation = TextureOpConstant()
		this.opaqueOperation.imageCacheCapacity = 1
		this.opacityOperation = TextureOpConstant()
		this.textureOperations = arrayOf<TextureOperation?>(this.opaqueOperation, this.opacityOperation, this.hdrOperation)
		this.opacityOperation.imageCacheCapacity = 1
	}

	@OriginalMember(owner = "client!lr", name = "<init>", descriptor = "(Lclient!cea;)V")
	constructor(@OriginalArg(0) packet: Packet?) : super() {
		@Pc(7) val operationCount: Int = packet!!.readUByte()
		@Pc(9) var textureFileCount: Int = 0
		@Pc(11) var materialFileCount: Int = 0
		@Pc(14) val childIndices: Array<IntArray?> = arrayOfNulls<IntArray>(operationCount)
		this.textureOperations = arrayOfNulls<TextureOperation>(operationCount)
		@Pc(50) var length: Int
		@Pc(20) var i: Int = 0
		while (i < operationCount) {
			@Pc(26) val textureOp: TextureOperation = TextureTypeList.decodeTextureOperation(packet)
			if (textureOp.getSpriteId() >= 0) {
				textureFileCount++
			}
			if (textureOp.getTextureId() >= 0) {
				materialFileCount++
			}
			@Pc(43) val childCount: Int = textureOp.operations.size
			childIndices[i] = IntArray(childCount)
			length = 0
			while (length < childCount) {
				childIndices[i]!![length] = packet.readUByte()
				length++
			}
			this.textureOperations[i] = textureOp
			i++
		}
		this.textureFileIds = IntArray(textureFileCount)
		this.materialFileIds = IntArray(materialFileCount)
		textureFileCount = 0
		materialFileCount = 0
		@Pc(91) var j: Int = 0
		while (j < operationCount) {
			@Pc(98) val textureOp2: TextureOperation? = this.textureOperations[j]
			length = textureOp2!!.operations.size
			@Pc(104) var k: Int = 0
			while (k < length) {
				textureOp2.operations[k] = this.textureOperations[childIndices[j]!![k]]
				k++
			}
			@Pc(130) val spriteId: Int = textureOp2.getSpriteId()
			@Pc(134) val textureId: Int = textureOp2.getTextureId()
			if (spriteId > 0) {
				this.textureFileIds[textureFileCount++] = spriteId
			}
			if (textureId > 0) {
				this.materialFileIds[materialFileCount++] = textureId
			}
			childIndices[j] = null
			j++
		}
		this.opaqueOperation = this.textureOperations[packet.readUByte()]
		this.opacityOperation = this.textureOperations[packet.readUByte()]
		this.hdrOperation = this.textureOperations[packet.readUByte()]
	}

	@OriginalMember(owner = "client!lr", name = "a", descriptor = "(Lclient!d;IIZDLclient!in;B)[I")
	fun getPixelsArgb(@OriginalArg(0) textureSource: MaterialProvider?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) transposed: Boolean, @OriginalArg(4) currentBrightness: Double, @OriginalArg(5) textureSpriteResource: Js5ResourceProvider?): IntArray {
		TextureTypeList.textureSource = textureSource
		TextureTypeList.textureSpriteResource = textureSpriteResource
		@Pc(11) var i: Int = 0
		while (i < this.textureOperations.size) {
			this.textureOperations[i]!!.initCache(width, height)
			i++
		}
		BrightnessTableBuilder.setBrightness(currentBrightness)
		TextureRaster.setDimensions(height, width)
		@Pc(39) val pixels: IntArray = IntArray(width * height)
		@Pc(41) var writeIndex: Int = 0
		@Pc(43) var j: Int = 0
		while (j < height) {
			@Pc(57) val redChannel: IntArray?
			@Pc(61) val greenChannel: IntArray?
			@Pc(59) val blueChannel: IntArray?
			@Pc(55) val local55: IntArray?
			if (this.opaqueOperation!!.monochrome) {
				val monochromeChannel: IntArray? = this.opaqueOperation.monochromeOutput(j)
				redChannel = monochromeChannel
				blueChannel = monochromeChannel
				greenChannel = monochromeChannel
			} else {
				@Pc(69) val colorOutput: Array<IntArray?>? = this.opaqueOperation.getColorOutput(j)
				blueChannel = colorOutput!![2]
				greenChannel = colorOutput[1]
				redChannel = colorOutput[0]
			}
			if (transposed) {
				writeIndex = j
			}
			val alphaChannel: IntArray?
			if (this.opacityOperation!!.monochrome) {
				alphaChannel = this.opacityOperation.monochromeOutput(j)
			} else {
				alphaChannel = this.opacityOperation.getColorOutput(j)!![0]
			}
			@Pc(111) var k: Int = width - 1
			while (k >= 0) {
				@Pc(119) var red: Int = redChannel!![k] shr 4
				if (red > 255) {
					red = 255
				}
				if (red < 0) {
					red = 0
				}
				@Pc(137) var green: Int = greenChannel!![k] shr 4
				if (green > 255) {
					green = 255
				}
				if (green < 0) {
					green = 0
				}
				@Pc(152) var blue: Int = blueChannel!![k] shr 4
				if (blue > 255) {
					blue = 255
				}
				green = TextureTypeList.BRIGHTNESS_TABLE[green]
				if (blue < 0) {
					blue = 0
				}
				red = TextureTypeList.BRIGHTNESS_TABLE[red]
				blue = TextureTypeList.BRIGHTNESS_TABLE[blue]
				@Pc(184) var alpha: Int
				if (red == 0 && green == 0 && blue == 0) {
					alpha = 0
				} else {
					alpha = alphaChannel!![k] shr 4
					if (alpha > 255) {
						alpha = 255
					}
					if (alpha < 0) {
						alpha = 0
					}
				}
				pixels[writeIndex++] = blue + (alpha shl 24) + (red shl 16) + (green shl 8)
				if (transposed) {
					writeIndex += width - 1
				}
				k--
			}
			j++
		}
		@Pc(244) var m: Int = 0
		while (m < this.textureOperations.size) {
			this.textureOperations[m]!!.cacheReset()
			m++
		}
		return pixels
	}

	@OriginalMember(owner = "client!lr", name = "a", descriptor = "(Lclient!d;Lclient!in;I)Z")
	fun isReady(@OriginalArg(0) textureSource: MaterialProvider?, @OriginalArg(1) spriteResource: Js5ResourceProvider?): Boolean {
		@Pc(12) var i: Int
		if (TextureType.SPRITE_GROUP_ID < 0) {
			i = 0
			while (i < this.textureFileIds.size) {
				if (!spriteResource!!.isFileReady(this.textureFileIds[i])) {
					return false
				}
				i++
			}
		} else {
			i = 0
			while (i < this.textureFileIds.size) {
				if (!spriteResource!!.isFileReady(TextureType.SPRITE_GROUP_ID, this.textureFileIds[i])) {
					return false
				}
				i++
			}
		}
		i = 0
		while (i < this.materialFileIds.size) {
			if (!textureSource!!.textureAvailable(this.materialFileIds[i])) {
				return false
			}
			i++
		}
		return true
	}

	@OriginalMember(owner = "client!lr", name = "a", descriptor = "(ILclient!d;IZDZLclient!in;I)[I")
	fun getPixelsRgb(@OriginalArg(0) width: Int, @OriginalArg(1) textureSource: MaterialProvider?, @OriginalArg(3) transposed: Boolean, @OriginalArg(4) currentBrightness: Double, @OriginalArg(5) readReversed: Boolean, @OriginalArg(6) textureSpriteResource: Js5ResourceProvider?, @OriginalArg(7) height: Int): IntArray {
		TextureTypeList.textureSource = textureSource
		TextureTypeList.textureSpriteResource = textureSpriteResource
		@Pc(11) var i: Int = 0
		while (i < this.textureOperations.size) {
			this.textureOperations[i]!!.initCache(width, height)
			i++
		}
		BrightnessTableBuilder.setBrightness(currentBrightness)
		TextureRaster.setDimensions(height, width)
		@Pc(41) val pixels: IntArray = IntArray(height * width)
		@Pc(45) val startX: Int
		@Pc(49) val endX: Int
		@Pc(47) val step: Byte
		if (readReversed) {
			startX = width - 1
			step = -1
			endX = -1
		} else {
			startX = 0
			step = 1
			endX = width
		}
		@Pc(61) var writeIndex: Int = 0
		@Pc(63) var j: Int = 0
		while (j < height) {
			@Pc(79) val redChannel: IntArray?
			@Pc(77) val greenChannel: IntArray?
			@Pc(81) val blueChannel: IntArray?
			if (this.opaqueOperation!!.monochrome) {
				@Pc(75) val monochromeOutput: IntArray? = this.opaqueOperation.monochromeOutput(j)
				greenChannel = monochromeOutput
				redChannel = monochromeOutput
				blueChannel = monochromeOutput
			} else {
				@Pc(89) val colorOutput: Array<IntArray?>? = this.opaqueOperation.getColorOutput(j)
				greenChannel = colorOutput!![1]
				redChannel = colorOutput[0]
				blueChannel = colorOutput[2]
			}
			if (transposed) {
				writeIndex = j
			}
			@Pc(107) var k: Int = startX
			while (k != endX) {
				@Pc(115) var red: Int = redChannel!![k] shr 4
				if (red > 255) {
					red = 255
				}
				if (red < 0) {
					red = 0
				}
				@Pc(130) var green: Int = greenChannel!![k] shr 4
				if (green > 255) {
					green = 255
				}
				if (green < 0) {
					green = 0
				}
				@Pc(145) var blue: Int = blueChannel!![k] shr 4
				if (blue > 255) {
					blue = 255
				}
				green = TextureTypeList.BRIGHTNESS_TABLE[green]
				red = TextureTypeList.BRIGHTNESS_TABLE[red]
				if (blue < 0) {
					blue = 0
				}
				blue = TextureTypeList.BRIGHTNESS_TABLE[blue]
				@Pc(181) var rgb: Int = (green shl 8) + (red shl 16) + blue
				if (rgb != 0) {
					rgb = rgb or 0xFF000000.toInt()
				}
				pixels[writeIndex++] = rgb
				if (transposed) {
					writeIndex += width - 1
				}
				k += step.toInt()
			}
			j++
		}
		@Pc(216) var m: Int = 0
		while (m < this.textureOperations.size) {
			this.textureOperations[m]!!.cacheReset()
			m++
		}
		return pixels
	}

	@OriginalMember(owner = "client!lr", name = "a", descriptor = "(ILclient!d;ZILclient!in;I)[F")
	fun getPixelsHdr(@OriginalArg(0) width: Int, @OriginalArg(1) textureSource: MaterialProvider?, @OriginalArg(2) transposed: Boolean, @OriginalArg(3) height: Int, @OriginalArg(4) textureSpriteResource: Js5ResourceProvider?): FloatArray {
		TextureTypeList.textureSpriteResource = textureSpriteResource
		TextureTypeList.textureSource = textureSource
		@Pc(24) var i: Int = 0
		while (i < this.textureOperations.size) {
			this.textureOperations[i]!!.initCache(width, height)
			i++
		}
		TextureRaster.setDimensions(height, width)
		@Pc(51) val pixels: FloatArray = FloatArray(width * 4 * height)
		@Pc(53) var writeIndex: Int = 0
		@Pc(55) var j: Int = 0
		while (j < height) {
			@Pc(75) val redChannel: IntArray?
			@Pc(71) val greenChannel: IntArray?
			@Pc(79) val blueChannel: IntArray?
			if (this.opaqueOperation!!.monochrome) {
				@Pc(87) val monochromeOutput: IntArray? = this.opaqueOperation.monochromeOutput(j)
				blueChannel = monochromeOutput
				greenChannel = monochromeOutput
				redChannel = monochromeOutput
			} else {
				@Pc(67) val colorOutput: Array<IntArray?>? = this.opaqueOperation.getColorOutput(j)
				greenChannel = colorOutput!![1]
				redChannel = colorOutput[0]
				blueChannel = colorOutput[2]
			}
			@Pc(103) val opacityChannel: IntArray?
			if (this.opacityOperation!!.monochrome) {
				opacityChannel = this.opacityOperation.monochromeOutput(j)
			} else {
				opacityChannel = this.opacityOperation.getColorOutput(j)!![0]
			}
			@Pc(123) val hdrChannel: IntArray?
			if (this.hdrOperation!!.monochrome) {
				hdrChannel = this.hdrOperation.monochromeOutput(j)
			} else {
				hdrChannel = this.hdrOperation.getColorOutput(j)!![0]
			}
			if (transposed) {
				writeIndex = j shl 2
			}
			@Pc(143) var k: Int = width - 1
			while (k >= 0) {
				@Pc(152) var alpha: Float = opacityChannel!![k].toFloat() / 4096.0f
				@Pc(165) val intensity: Float = (hdrChannel!![k].toFloat() * 31.0f / 4096.0f + 1.0f) / 4096.0f
				if (alpha < 0.0f) {
					alpha = 0.0f
				} else if (alpha > 1.0f) {
					alpha = 1.0f
				}
				pixels[writeIndex++] = redChannel!![k].toFloat() * intensity
				pixels[writeIndex++] = intensity * greenChannel!![k].toFloat()
				pixels[writeIndex++] = blueChannel!![k].toFloat() * intensity
				pixels[writeIndex++] = alpha
				if (transposed) {
					writeIndex += (width shl 2) - 4
				}
				k--
			}
			j++
		}
		@Pc(236) var m: Int = 0
		while (m < this.textureOperations.size) {
			this.textureOperations[m]!!.cacheReset()
			m++
		}
		return pixels
	}

	companion object {
		@OriginalMember(owner = "client!haa", name = "E", descriptor = "I")
		const val SPRITE_GROUP_ID: Int = -1
	}
}
