package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation

@OriginalClass("client!uf")
class TextureOpColorize @OriginalMember(owner = "client!uf", name = "<init>", descriptor = "()V") constructor() : TextureOperation(1, false) {

	@OriginalMember(owner = "client!uf", name = "I", descriptor = "I")
	private var redMultiplier: Int = 4096

	@OriginalMember(owner = "client!uf", name = "L", descriptor = "I")
	private var blueMultiplier: Int = 4096

	@OriginalMember(owner = "client!uf", name = "S", descriptor = "I")
	private var greenMultiplier: Int = 4096

	@OriginalMember(owner = "client!uf", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) row: Int): Array<IntArray?> {
		@Pc(18) val output: Array<IntArray?> = this.colorImageCache!!.get(row)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(28) val input: Array<IntArray?> = this.getChildColorOutput(row, 0)!!
			@Pc(32) val inputRed: IntArray = input[0]!!
			@Pc(36) val inputGreen: IntArray = input[1]!!
			@Pc(40) val inputBlue: IntArray = input[2]!!
			@Pc(44) val outputRed: IntArray = output[0]!!
			@Pc(48) val outputGreen: IntArray = output[1]!!
			@Pc(52) val outputBlue: IntArray = output[2]!!
			@Pc(54) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(60) val red: Int = inputRed[i]
				@Pc(64) val blue: Int = inputBlue[i]
				@Pc(68) val green: Int = inputGreen[i]
				if (blue == red && green == blue) {
					outputRed[i] = (red * this.redMultiplier) shr 12
					outputGreen[i] = (this.greenMultiplier * blue) shr 12
					outputBlue[i] = (this.blueMultiplier * green) shr 12
				} else {
					outputRed[i] = this.redMultiplier
					outputGreen[i] = this.greenMultiplier
					outputBlue[i] = this.blueMultiplier
				}
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!uf", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.redMultiplier = packet!!.readUShort()
		} else if (opcode == 1) {
			this.greenMultiplier = packet!!.readUShort()
		} else if (opcode == 2) {
			this.blueMultiplier = packet!!.readUShort()
		}
	}
}
