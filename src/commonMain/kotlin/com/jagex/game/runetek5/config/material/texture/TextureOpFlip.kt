package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.graphics.EnvironmentLight
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!jia")
class TextureOpFlip : TextureOperation {

	@OriginalMember(owner = "client!jia", name = "I", descriptor = "Z")
	private var mirrorHorizontally: Boolean = true

	@OriginalMember(owner = "client!jia", name = "P", descriptor = "Z")
	private var mirrorVertically: Boolean = true

	@OriginalMember(owner = "client!jia", name = "<init>", descriptor = "()V")
	constructor() : super(1, false)

	@OriginalMember(owner = "client!jia", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) count: Int): Array<IntArray?>? {
		@Pc(11) val output: Array<IntArray?>? = super.colorImageCache!!.get(count)
		if (super.colorImageCache!!.dirty) {
			@Pc(29) val input: Array<IntArray?>? = this.getChildColorOutput(if (this.mirrorVertically) TextureOperation.columnEnd - count else count, 0)
			@Pc(33) val inputRed: IntArray? = input!![0]
			@Pc(37) val inputGreen: IntArray? = input[1]
			@Pc(41) val inputBlue: IntArray? = input[2]
			@Pc(45) val outputRed: IntArray? = output!![0]
			@Pc(49) val outputGreen: IntArray? = output[1]
			@Pc(53) val outputBlue: IntArray? = output[2]
			@Pc(58) var x: Int
			if (this.mirrorHorizontally) {
				x = 0
				while (x < EnvironmentLight.textureOpWidth) {
					outputRed!![x] = inputRed!![TextureRaster.widthMask - x]
					outputGreen!![x] = inputGreen!![TextureRaster.widthMask - x]
					outputBlue!![x] = inputBlue!![TextureRaster.widthMask - x]
					x++
				}
			} else {
				x = 0
				while (x < EnvironmentLight.textureOpWidth) {
					outputRed!![x] = inputRed!![x]
					outputGreen!![x] = inputGreen!![x]
					outputBlue!![x] = inputBlue!![x]
					x++
				}
			}
		}
		return output
	}

	@OriginalMember(owner = "client!jia", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.mirrorHorizontally = packet!!.readUByte() == 1
		} else if (opcode == 1) {
			this.mirrorVertically = packet!!.readUByte() == 1
		} else if (opcode == 2) {
			super.monochrome = packet!!.readUByte() == 1
		}
	}

	@OriginalMember(owner = "client!jia", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray? {
		@Pc(16) val output: IntArray? = super.monochromeImageCache!!.get(row)
		if (super.monochromeImageCache!!.dirty) {
			@Pc(35) val input: IntArray? = this.getChildMonochromeOutput(if (this.mirrorVertically) TextureOperation.columnEnd - row else row, 0)
			if (this.mirrorHorizontally) {
				@Pc(48) var i: Int = 0
				while (i < EnvironmentLight.textureOpWidth) {
					output!![i] = input!![TextureRaster.widthMask - i]
					i++
				}
			} else {
				Arrays.copy(input, 0, output, 0, EnvironmentLight.textureOpWidth)
			}
		}
		return output
	}
}
