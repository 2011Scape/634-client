package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!uia")
class TextureOpInterpolate : TextureOperation {

	@OriginalMember(owner = "client!uia", name = "<init>", descriptor = "()V")
	constructor() : super(3, false)

	@OriginalMember(owner = "client!uia", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) y: Int): Array<IntArray?> {
		@Pc(16) val output: Array<IntArray?> = this.colorImageCache!!.get(y)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(28) val blendFactor: IntArray = this.getChildMonochromeOutput(y, 2)!!
			@Pc(34) val input0: Array<IntArray?> = this.getChildColorOutput(y, 0)!!
			@Pc(40) val input1: Array<IntArray?> = this.getChildColorOutput(y, 1)!!
			@Pc(44) val outputRed: IntArray = output[0]!!
			@Pc(48) val outputGreen: IntArray = output[1]!!
			@Pc(52) val outputBlue: IntArray = output[2]!!
			@Pc(56) val input0Red: IntArray = input0[0]!!
			@Pc(60) val input0Green: IntArray = input0[1]!!
			@Pc(64) val input0Blue: IntArray = input0[2]!!
			@Pc(68) val input1Red: IntArray = input1[0]!!
			@Pc(72) val input1Green: IntArray = input1[1]!!
			@Pc(76) val input1Blue: IntArray = input1[2]!!
			@Pc(78) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(84) val blend: Int = blendFactor[i]
				if (blend == 4096) {
					outputRed[i] = input0Red[i]
					outputGreen[i] = input0Green[i]
					outputBlue[i] = input0Blue[i]
				} else if (blend == 0) {
					outputRed[i] = input1Red[i]
					outputGreen[i] = input1Green[i]
					outputBlue[i] = input1Blue[i]
				} else {
					@Pc(97) val inverseBlend: Int = 4096 - blend
					outputRed[i] = (input1Red[i] * inverseBlend + input0Red[i] * blend) shr 12
					outputGreen[i] = (input1Green[i] * inverseBlend + input0Green[i] * blend) shr 12
					outputBlue[i] = (input0Blue[i] * blend + input1Blue[i] * inverseBlend) shr 12
				}
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!uia", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) y: Int): IntArray {
		@Pc(16) val output: IntArray = this.monochromeImageCache!!.get(y)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(28) val input0: IntArray = this.getChildMonochromeOutput(y, 0)!!
			@Pc(34) val input1: IntArray = this.getChildMonochromeOutput(y, 1)!!
			@Pc(42) val blendFactor: IntArray = this.getChildMonochromeOutput(y, 2)!!
			@Pc(44) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(50) val blend: Int = blendFactor[i]
				if (blend == 4096) {
					output[i] = input0[i]
				} else if (blend == 0) {
					output[i] = input1[i]
				} else {
					output[i] = (blend * input0[i] + input1[i] * (4096 - blend)) shr 12
				}
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!uia", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.monochrome = packet!!.readUByte() == 1
		}
	}
}
