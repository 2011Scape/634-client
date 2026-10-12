package com.jagex.game.runetek5.graphics

import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object SpriteDataProvider {

	@OriginalMember(owner = "client!rh", name = "a", descriptor = "(Lclient!in;I)Lclient!rh;")
	@JvmStatic
	fun listSingle(@OriginalArg(0) resource: Js5ResourceProvider?, @OriginalArg(1) fileId: Int): SpriteType? {
		@Pc(4) val data: ByteArray? = resource!!.getFile(fileId)
		return if (data == null) null else load(data)[0]
	}

	@OriginalMember(owner = "client!rh", name = "a", descriptor = "([B)[Lclient!rh;")
	@JvmStatic
	fun load(@OriginalArg(0) data: ByteArray?): Array<SpriteType?> {
		@Pc(4) val packet: Packet = Packet(data)
		packet.currentPosition = data!!.size - 2
		@Pc(14) val count: Int = packet.readUShort()
		@Pc(17) val indexedImages: Array<SpriteType?> = arrayOfNulls<SpriteType>(count)
		@Pc(19) var i0: Int = 0
		while (i0 < count) {
			indexedImages[i0] = SpriteType()
			i0++
		}
		packet.currentPosition = data.size - count * 8 - 7
		@Pc(44) val biggestWidth: Int = packet.readUShort()
		@Pc(48) val biggestHeight: Int = packet.readUShort()
		@Pc(56) val palleteLength: Int = (packet.readUByte() and 0xFF) + 1
		@Pc(58) var j: Int = 0
		while (j < count) {
			indexedImages[j]!!.minX = packet.readUShort()
			j++
		}
		@Pc(72) var k: Int = 0
		while (k < count) {
			indexedImages[k]!!.minY = packet.readUShort()
			k++
		}
		@Pc(86) var m: Int = 0
		while (m < count) {
			indexedImages[m]!!.width = packet.readUShort()
			m++
		}
		@Pc(100) var i1: Int = 0
		while (i1 < count) {
			indexedImages[i1]!!.height = packet.readUShort()
			i1++
		}
		@Pc(114) var i2: Int = 0
		while (i2 < count) {
			@Pc(119) val indexedImage: SpriteType = indexedImages[i2]!!
			indexedImage.borderX = biggestWidth - indexedImage.width - indexedImage.minX
			indexedImage.borderY = biggestHeight - indexedImage.height - indexedImage.minY
			i2++
		}
		packet.currentPosition = data.size - count * 8 - (palleteLength - 1) * 3 - 7
		@Pc(160) val pallete: IntArray = IntArray(palleteLength)
		@Pc(162) var i3: Int = 1
		while (i3 < palleteLength) {
			pallete[i3] = packet.readUMedium()
			if (pallete[i3] == 0) {
				pallete[i3] = 1
			}
			i3++
		}
		@Pc(183) var i4: Int = 0
		while (i4 < count) {
			indexedImages[i4]!!.pallete = pallete
			i4++
		}
		packet.currentPosition = 0
		@Pc(198) var i: Int = 0
		while (i < count) {
			@Pc(203) val indexedImage2: SpriteType = indexedImages[i]!!
			@Pc(209) val numPixels: Int = indexedImage2.width * indexedImage2.height
			indexedImage2.pixels = ByteArray(numPixels)
			@Pc(217) val flags: Int = packet.readUByte()
			@Pc(245) var local245: Int
			if ((flags and 0x2) == 0) {
				@Pc(227) var local227: Int
				if ((flags and 0x1) == 0) {
					var pixelIndex: Int = 0
					while (pixelIndex < numPixels) {
						indexedImage2.pixels!![pixelIndex] = packet.readByte()
						pixelIndex++
					}
				} else {
					var x: Int = 0
					while (x < indexedImage2.width) {
						var y: Int = 0
						while (y < indexedImage2.height) {
							indexedImage2.pixels!![x + y * indexedImage2.width] = packet.readByte()
							y++
						}
						x++
					}
				}
			} else {
				@Pc(271) var usesAlpha: Boolean = false
				indexedImage2.alpha = ByteArray(numPixels)
				@Pc(295) var local295: Int
				if ((flags and 0x1) == 0) {
					var pixelIndex: Int = 0
					while (pixelIndex < numPixels) {
						indexedImage2.pixels!![pixelIndex] = packet.readByte()
						pixelIndex++
					}
					pixelIndex = 0
					while (pixelIndex < numPixels) {
						@Pc(305) val alphaValue: Byte = packet.readByte()
						indexedImage2.alpha!![pixelIndex] = alphaValue
						usesAlpha = usesAlpha or (alphaValue.toInt() != -1)
						pixelIndex++
					}
				} else {
					var x: Int = 0
					while (x < indexedImage2.width) {
						var y: Int = 0
						while (y < indexedImage2.height) {
							indexedImage2.pixels!![x + y * indexedImage2.width] = packet.readByte()
							y++
						}
						x++
					}
					x = 0
					while (x < indexedImage2.width) {
						@Pc(352) var y: Int = 0
						while (y < indexedImage2.height) {
							@Pc(367) val alphaValue: Byte = packet.readByte()
							indexedImage2.alpha!![x + y * indexedImage2.width] = alphaValue
							usesAlpha = usesAlpha or (alphaValue.toInt() != -1)
							y++
						}
						x++
					}
				}
				if (!usesAlpha) {
					indexedImage2.alpha = null
				}
			}
			i++
		}
		return indexedImages
	}

	@OriginalMember(owner = "client!rh", name = "b", descriptor = "(Lclient!in;II)Lclient!rh;")
	@JvmStatic
	fun listSingle(@OriginalArg(0) resource: Js5ResourceProvider?, @OriginalArg(1) archiveId: Int, @OriginalArg(2) fileId: Int): SpriteType? {
		@Pc(5) val data: ByteArray? = resource!!.getFile(archiveId, fileId)
		return if (data == null) null else load(data)[0]
	}
}
