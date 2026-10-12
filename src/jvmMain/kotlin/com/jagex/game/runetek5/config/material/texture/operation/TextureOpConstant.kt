package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation

@OriginalClass("client!pba")
class TextureOpConstant @OriginalMember(owner = "client!pba", name = "<init>", descriptor = "(I)V") constructor(@OriginalArg(0) intensity: Int) : TextureOperation(0, true) {

	@OriginalMember(owner = "client!pba", name = "M", descriptor = "I")
	private var intensity: Int = 0

	init {
		this.intensity = 4096
		this.intensity = intensity
	}

	@OriginalMember(owner = "client!pba", name = "<init>", descriptor = "()V")
	constructor() : this(4096)

	@OriginalMember(owner = "client!pba", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.intensity = (packet!!.readUByte() shl 12) / 255
		}
	}

	@OriginalMember(owner = "client!pba", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(9) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			Arrays.setVariables(output, 0, EnvironmentLight.textureOpWidth, this.intensity)
		}
		return output
	}
}
