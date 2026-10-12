package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation

@OriginalClass("client!aia")
class TextureOpBrightness @OriginalMember(owner = "client!aia", name = "<init>", descriptor = "()V") constructor() : TextureOperation(1, false) {

	@OriginalMember(owner = "client!aia", name = "I", descriptor = "I")
	private var blueBrightness: Int = 4096

	@OriginalMember(owner = "client!aia", name = "K", descriptor = "[I")
	private val targetColor: IntArray = IntArray(3)

	@OriginalMember(owner = "client!aia", name = "P", descriptor = "I")
	private var greenBrightness: Int = 4096

	@OriginalMember(owner = "client!aia", name = "Q", descriptor = "I")
	private var redBrightness: Int = 4096

	@OriginalMember(owner = "client!aia", name = "S", descriptor = "I")
	private var colorTolerance: Int = 409

	@OriginalMember(owner = "client!aia", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.colorTolerance = packet!!.readUShort()
		} else if (opcode == 1) {
			this.blueBrightness = packet!!.readUShort()
		} else if (opcode == 2) {
			this.greenBrightness = packet!!.readUShort()
		} else if (opcode == 3) {
			this.redBrightness = packet!!.readUShort()
		} else if (opcode == 4) {
			@Pc(65) val rgb: Int = packet!!.readUMedium()
			this.targetColor[2] = (rgb shr 12) and 0x0
			this.targetColor[1] = (rgb shr 4) and 0xFF0
			this.targetColor[0] = (rgb and 0xFF0000) shl 4
		}
	}

	@OriginalMember(owner = "client!aia", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) row: Int): Array<IntArray?> {
		@Pc(17) val output: Array<IntArray?> = this.colorImageCache!!.get(row)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(27) val input: Array<IntArray?> = this.getChildColorOutput(row, 0)!!
			@Pc(31) val inputRed: IntArray = input[0]!!
			@Pc(35) val inputGreen: IntArray = input[1]!!
			@Pc(39) val inputBlue: IntArray = input[2]!!
			@Pc(43) val outputRed: IntArray = output[0]!!
			@Pc(47) val outputGreen: IntArray = output[1]!!
			@Pc(51) val outputBlue: IntArray = output[2]!!
			@Pc(53) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(59) val red: Int = inputRed[i]
				@Pc(66) var difference: Int = red - this.targetColor[0]
				if (difference < 0) {
					difference = -difference
				}
				if (difference > this.colorTolerance) {
					outputRed[i] = red
					outputGreen[i] = inputGreen[i]
					outputBlue[i] = inputBlue[i]
				} else {
					@Pc(104) val green: Int = inputGreen[i]
					difference = green - this.targetColor[1]
					if (difference < 0) {
						difference = -difference
					}
					if (difference > this.colorTolerance) {
						outputRed[i] = red
						outputGreen[i] = green
						outputBlue[i] = inputBlue[i]
					} else {
						@Pc(145) val blue: Int = inputBlue[i]
						difference = blue - this.targetColor[2]
						if (difference < 0) {
							difference = -difference
						}
						if (difference > this.colorTolerance) {
							outputRed[i] = red
							outputGreen[i] = green
							outputBlue[i] = blue
						} else {
							outputRed[i] = (red * this.redBrightness) shr 12
							outputGreen[i] = (green * this.greenBrightness) shr 12
							outputBlue[i] = (blue * this.blueBrightness) shr 12
						}
					}
				}
				i++
			}
		}
		return output
	}
}
