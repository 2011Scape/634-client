package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!wi")
class TextureOpInvert : TextureOperation {

	@OriginalMember(owner = "client!wi", name = "<init>", descriptor = "()V")
	constructor() : super(1, false)

	@OriginalMember(owner = "client!wi", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) y: Int): Array<IntArray?> {
		@Pc(16) val output: Array<IntArray?> = this.colorImageCache!!.get(y)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(26) val input: Array<IntArray?> = this.getChildColorOutput(y, 0)!!
			@Pc(30) val inputRed: IntArray = input[0]!!
			@Pc(34) val inputGreen: IntArray = input[1]!!
			@Pc(38) val inputBlue: IntArray = input[2]!!
			@Pc(42) val outputRed: IntArray = output[0]!!
			@Pc(46) val outputGreen: IntArray = output[1]!!
			@Pc(50) val outputBlue: IntArray = output[2]!!
			@Pc(52) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				outputRed[i] = 4096 - inputRed[i]
				outputGreen[i] = 4096 - inputGreen[i]
				outputBlue[i] = 4096 - inputBlue[i]
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!wi", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) y: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(y)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(29) val input: IntArray = this.getChildMonochromeOutput(y, 0)!!
			@Pc(31) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				output[i] = 4096 - input[i]
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!wi", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.monochrome = packet!!.readUByte() == 1
		}
	}
}
