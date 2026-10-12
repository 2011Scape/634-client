package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import util.Random
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.TextureRaster
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!v")
class TextureOpLineNoise : TextureOperation {

	@OriginalMember(owner = "client!v", name = "M", descriptor = "I")
	private var angleSpread: Int = 4096

	@OriginalMember(owner = "client!v", name = "Q", descriptor = "I")
	private var lineLength: Int = 16

	@OriginalMember(owner = "client!v", name = "R", descriptor = "I")
	private var angleOffset: Int = 0

	@OriginalMember(owner = "client!v", name = "K", descriptor = "I")
	private var lineCount: Int = 2000

	@OriginalMember(owner = "client!v", name = "P", descriptor = "I")
	private var randomSeed: Int = 0

	@OriginalMember(owner = "client!v", name = "<init>", descriptor = "()V")
	constructor() : super(0, true)

	@OriginalMember(owner = "client!v", name = "e", descriptor = "(I)V")
	override fun postDecode() {
		TextureRaster.calculateTrigTables()
	}

	@OriginalMember(owner = "client!v", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.randomSeed = packet!!.readUByte()
		} else if (opcode == 1) {
			this.lineCount = packet!!.readUShort()
		} else if (opcode == 2) {
			this.lineLength = packet!!.readUByte()
		} else if (opcode == 3) {
			this.angleOffset = packet!!.readUShort()
		} else if (opcode == 4) {
			this.angleSpread = packet!!.readUShort()
		}
	}

	@OriginalMember(owner = "client!v", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(17) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(26) val halfSpread: Int = this.angleSpread shr 1
			@Pc(31) val pixelRows: Array<IntArray?> = this.monochromeImageCache!!.getAllPaletteData()!!
			@Pc(38) val random: Random = Random(this.randomSeed.toLong())
			@Pc(40) var i: Int = 0
			while (i < this.lineCount) {
				@Pc(63) val angle: Int = if (this.angleSpread > 0) this.angleOffset + Utils.randomInt(this.angleSpread, random) - halfSpread else this.angleOffset
				@Pc(69) val angleIndex: Int = (angle shr 4) and 0xFF
				@Pc(74) var startX: Int = Utils.randomInt(EnvironmentLight.textureOpWidth, random)
				@Pc(79) var startY: Int = Utils.randomInt(TextureOperation.currentHeight, random)
				@Pc(91) var endX: Int = startX + ((this.lineLength * TextureRaster.PALETTE_COS!![angleIndex]) shr 12)
				@Pc(102) var endY: Int = ((TextureRaster.PALETTE_SIN!![angleIndex] * this.lineLength) shr 12) + startY
				@Pc(107) var lineDeltaY: Int = endY - startY
				@Pc(112) var lineDeltaX: Int = endX - startX
				if (lineDeltaX != 0 || lineDeltaY != 0) {
					if (lineDeltaY < 0) {
						lineDeltaY = -lineDeltaY
					}
					if (lineDeltaX < 0) {
						lineDeltaX = -lineDeltaX
					}
					@Pc(139) val steep: Boolean = lineDeltaX < lineDeltaY
					@Pc(143) var local143: Int
					@Pc(145) var local145: Int
					if (steep) {
						val savedStartX: Int = startX
						val savedEndX: Int = endX
						startX = startY
						startY = savedStartX
						endX = endY
						endY = savedEndX
					}
					if (startX > endX) {
						val savedStartX: Int = startX
						startX = endX
						val savedStartY: Int = startY
						endX = savedStartX
						startY = endY
						endY = savedStartY
					}
					var currentY: Int = startY
					val deltaX: Int = endX - startX
					@Pc(184) var deltaY: Int = endY - startY
					@Pc(189) var error: Int = -deltaX / 2
					@Pc(193) val brightnessStep: Int = 2048 / deltaX
					@Pc(202) val baseBrightness: Int = 1024 - (Utils.randomInt(4096, random) shr 2)
					if (deltaY < 0) {
						deltaY = -deltaY
					}
					@Pc(218) val yStep: Int = if (startY >= endY) -1 else 1
					@Pc(220) var j: Int = startX
					while (j < endX) {
						@Pc(232) val brightness: Int = (j - startX) * brightnessStep + baseBrightness + 1024
						@Pc(236) val wrappedX: Int = TextureRaster.widthMask and j
						@Pc(240) val wrappedY: Int = currentY and TextureOperation.columnEnd
						if (steep) {
							pixelRows[wrappedY]!![wrappedX] = brightness
						} else {
							pixelRows[wrappedX]!![wrappedY] = brightness
						}
						error += deltaY
						if (error > 0) {
							currentY -= -yStep
							error += -deltaX
						}
						j++
					}
				}
				i++
			}
		}
		return output
	}
}
