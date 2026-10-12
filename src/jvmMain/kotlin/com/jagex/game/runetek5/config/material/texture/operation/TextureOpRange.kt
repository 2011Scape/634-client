package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!kq")
class TextureOpRange : TextureOperation {

	@OriginalMember(owner = "client!kq", name = "K", descriptor = "I")
	private var min: Int = 1024

	@OriginalMember(owner = "client!kq", name = "J", descriptor = "I")
	private var rangeMax: Int = 3072

	@OriginalMember(owner = "client!kq", name = "Q", descriptor = "I")
	private var range: Int = 2048

	@OriginalMember(owner = "client!kq", name = "<init>", descriptor = "()V")
	constructor() : super(1, false)

	@OriginalMember(owner = "client!kq", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.min = packet!!.readUShort()
		} else if (opcode == 1) {
			this.rangeMax = packet!!.readUShort()
		} else if (opcode == 2) {
			this.monochrome = packet!!.readUByte() == 1
		}
	}

	@OriginalMember(owner = "client!kq", name = "e", descriptor = "(I)V")
	override fun postDecode() {
		this.range = this.rangeMax - this.min
	}

	@OriginalMember(owner = "client!kq", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) y: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(y)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(21) val input: IntArray = this.getChildMonochromeOutput(y, 0)!!
			@Pc(23) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				output[i] = this.min + ((this.range * input[i]) shr 12)
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!kq", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) y: Int): Array<IntArray?> {
		@Pc(11) val output: Array<IntArray?> = this.colorImageCache!!.get(y)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(29) val input: Array<IntArray?> = this.getChildColorOutput(y, 0)!!
			@Pc(33) val inputRed: IntArray = input[0]!!
			@Pc(37) val inputGreen: IntArray = input[1]!!
			@Pc(41) val inputBlue: IntArray = input[2]!!
			@Pc(45) val outputRed: IntArray = output[0]!!
			@Pc(49) val outputGreen: IntArray = output[1]!!
			@Pc(53) val outputBlue: IntArray = output[2]!!
			@Pc(55) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				outputRed[i] = ((this.range * inputRed[i]) shr 12) + this.min
				outputGreen[i] = ((this.range * inputGreen[i]) shr 12) + this.min
				outputBlue[i] = ((inputBlue[i] * this.range) shr 12) + this.min
				i++
			}
		}
		return output
	}
}
