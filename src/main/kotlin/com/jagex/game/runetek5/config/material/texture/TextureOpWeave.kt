package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!oca")
class TextureOpWeave : TextureOperation {

	@OriginalMember(owner = "client!oca", name = "K", descriptor = "I")
	private var threadWidth: Int = 585

	@OriginalMember(owner = "client!oca", name = "<init>", descriptor = "()V")
	constructor() : super(0, true)

	@OriginalMember(owner = "client!oca", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray? {
		@Pc(11) val output: IntArray? = super.monochromeImageCache!!.get(row)
		if (super.monochromeImageCache!!.dirty) {
			@Pc(24) val y: Int = TextureRaster.verticalTable!![row]
			@Pc(26) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(32) val x: Int = EnvironmentLight.horizontalTable[i]
				@Pc(76) var distance: Int
				if (x > this.threadWidth && x < 4096 - this.threadWidth && 2048 - this.threadWidth < y && this.threadWidth + 2048 > y) {
					distance = 2048 - x
					distance = if (distance < 0) -distance else distance
					distance = distance shl 0xC
					distance /= 2048 - this.threadWidth
					output!![i] = 4096 - distance
				} else if (x > 2048 - this.threadWidth && x < this.threadWidth + 2048) {
					distance = y - 2048
					distance = if (distance < 0) -distance else distance
					distance -= this.threadWidth
					distance = distance shl 0xC
					output!![i] = distance / (2048 - this.threadWidth)
				} else if (this.threadWidth > y || y > 4096 - this.threadWidth) {
					distance = x - 2048
					@Pc(176) val absDistance: Int = if (distance >= 0) distance else -distance
					@Pc(181) val edgeDistance: Int = absDistance - this.threadWidth
					@Pc(185) val scaledDistance: Int = edgeDistance shl 12
					output!![i] = scaledDistance / (2048 - this.threadWidth)
				} else if (x >= this.threadWidth && x <= 4096 - this.threadWidth) {
					output!![i] = 0
				} else {
					distance = 2048 - y
					distance = if (distance < 0) -distance else distance
					distance = distance shl 0xC
					distance /= 2048 - this.threadWidth
					output!![i] = 4096 - distance
				}
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!oca", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.threadWidth = packet!!.readUShort()
		}
	}
}
