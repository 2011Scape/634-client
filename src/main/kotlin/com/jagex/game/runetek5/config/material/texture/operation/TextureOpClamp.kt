package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation

@OriginalClass("client!fi")
class TextureOpClamp @OriginalMember(owner = "client!fi", name = "<init>", descriptor = "()V") constructor() : TextureOperation(1, false) {

	@OriginalMember(owner = "client!fi", name = "K", descriptor = "I")
	private var maxValue: Int = 4096

	@OriginalMember(owner = "client!fi", name = "O", descriptor = "I")
	private var minValue: Int = 0

	@OriginalMember(owner = "client!fi", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.minValue = packet!!.readUShort()
		} else if (opcode == 1) {
			this.maxValue = packet!!.readUShort()
		} else if (opcode == 2) {
			this.monochrome = packet!!.readUByte() == 1
		}
	}

	@OriginalMember(owner = "client!fi", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) row: Int): Array<IntArray?> {
		@Pc(16) val output: Array<IntArray?> = this.colorImageCache!!.get(row)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(26) val input: Array<IntArray?> = this.getChildColorOutput(row, 0)!!
			@Pc(30) val inputRed: IntArray = input[0]!!
			@Pc(34) val inputGreen: IntArray = input[1]!!
			@Pc(38) val inputBlue: IntArray = input[2]!!
			@Pc(42) val outputRed: IntArray = output[0]!!
			@Pc(46) val outputGreen: IntArray = output[1]!!
			@Pc(50) val outputBlue: IntArray = output[2]!!
			@Pc(52) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(58) val red: Int = inputRed[i]
				@Pc(62) val green: Int = inputGreen[i]
				@Pc(66) val blue: Int = inputBlue[i]
				if (red < this.minValue) {
					outputRed[i] = this.minValue
				} else if (red <= this.maxValue) {
					outputRed[i] = red
				} else {
					outputRed[i] = this.maxValue
				}
				if (this.minValue > green) {
					outputGreen[i] = this.minValue
				} else if (this.maxValue >= green) {
					outputGreen[i] = green
				} else {
					outputGreen[i] = this.maxValue
				}
				if (blue < this.minValue) {
					outputBlue[i] = this.minValue
				} else if (this.maxValue >= blue) {
					outputBlue[i] = blue
				} else {
					outputBlue[i] = this.maxValue
				}
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!fi", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(13) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(23) val input: IntArray = this.getChildMonochromeOutput(row, 0)!!
			@Pc(25) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(31) val value: Int = input[i]
				if (value < this.minValue) {
					output[i] = this.minValue
				} else if (value > this.maxValue) {
					output[i] = this.maxValue
				} else {
					output[i] = value
				}
				i++
			}
		}
		return output
	}
}
