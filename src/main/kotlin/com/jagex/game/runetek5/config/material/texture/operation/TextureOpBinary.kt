package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation

@OriginalClass("client!hga")
class TextureOpBinary @OriginalMember(owner = "client!hga", name = "<init>", descriptor = "()V") constructor() : TextureOperation(1, true) {

	@OriginalMember(owner = "client!hga", name = "N", descriptor = "I")
	private var upperThreshold: Int = 4096

	@OriginalMember(owner = "client!hga", name = "K", descriptor = "I")
	private var lowerThreshold: Int = 0

	@OriginalMember(owner = "client!hga", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.lowerThreshold = packet!!.readUShort()
		} else if (opcode == 1) {
			this.upperThreshold = packet!!.readUShort()
		}
	}

	@OriginalMember(owner = "client!hga", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(19) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(29) val input: IntArray = this.getChildMonochromeOutput(row, 0)!!
			@Pc(31) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(37) val value: Int = input[i]
				output[i] = if (value >= this.lowerThreshold && this.upperThreshold >= value) 4096 else 0
				i++
			}
		}
		return output
	}
}
