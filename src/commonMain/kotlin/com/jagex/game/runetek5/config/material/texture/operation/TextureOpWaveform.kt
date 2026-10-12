package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.TextureRaster

@OriginalClass("client!dn")
class TextureOpWaveform : TextureOperation {

	@OriginalMember(owner = "client!dn", name = "J", descriptor = "I")
	private var waveType: Int = 0

	@OriginalMember(owner = "client!dn", name = "L", descriptor = "I")
	private var frequency: Int = 1

	@OriginalMember(owner = "client!dn", name = "N", descriptor = "I")
	private var waveShape: Int = 0

	@OriginalMember(owner = "client!dn", name = "<init>", descriptor = "()V")
	constructor() : super(0, true)

	@OriginalMember(owner = "client!dn", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.waveType = packet!!.readUByte()
		} else if (opcode == 1) {
			this.waveShape = packet!!.readUByte()
		} else if (opcode == 3) {
			this.frequency = packet!!.readUByte()
		}
	}

	@OriginalMember(owner = "client!dn", name = "e", descriptor = "(I)V")
	override fun postDecode() {
		TextureRaster.calculateTrigTables()
	}

	@OriginalMember(owner = "client!dn", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(25) val rowCoord: Int = TextureRaster.verticalTable!![row]
			@Pc(31) val centreOffsetY: Int = (rowCoord - 2048) shr 1
			@Pc(33) var x: Int = 0
			while (x < EnvironmentLight.textureOpWidth) {
				@Pc(39) val columnCoord: Int = EnvironmentLight.horizontalTable!![x]
				@Pc(45) val centreOffsetX: Int = (columnCoord - 2048) shr 1
				@Pc(59) var value: Int
				if (this.waveType == 0) {
					value = this.frequency * (columnCoord - rowCoord)
				} else {
					@Pc(71) val squareDistance: Int = (centreOffsetX * centreOffsetX + centreOffsetY * centreOffsetY) shr 12
					value = (kotlin.math.sqrt((squareDistance.toFloat() / 4096.0f).toDouble()) * 4096.0).toInt()
					value = ((this.frequency * value).toDouble() * 3.141592653589793).toInt()
				}
				value -= value and 0xFFFFF000.toInt()
				if (this.waveShape == 0) {
					value = (TextureRaster.PALETTE_SIN!![(value shr 4) and 0xFF] + 4096) shr 1
				} else if (this.waveShape == 2) {
					value -= 2048
					if (value < 0) {
						value = -value
					}
					value = (2048 - value) shl 1
				}
				output[x] = value
				x++
			}
		}
		return output
	}
}
