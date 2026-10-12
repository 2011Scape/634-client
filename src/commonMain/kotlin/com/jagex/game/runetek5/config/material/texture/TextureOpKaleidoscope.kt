package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!wj")
class TextureOpKaleidoscope : TextureOperation {

	@OriginalMember(owner = "client!wj", name = "<init>", descriptor = "()V")
	constructor() : super(1, false)

	@OriginalMember(owner = "client!wj", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) row: Int): Array<IntArray?>? {
		@Pc(11) val output: Array<IntArray?>? = super.colorImageCache!!.get(row)
		if (super.colorImageCache!!.dirty) {
			@Pc(24) val outputRed: IntArray? = output!![0]
			@Pc(28) val outputGreen: IntArray? = output[1]
			@Pc(32) val outputBlue: IntArray? = output[2]
			@Pc(34) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				this.reflect(i, row)
				@Pc(47) val input: Array<IntArray?>? = this.getChildColorOutput(TextureOpKaleidoscope.mirroredY, 0)
				outputRed!![i] = input!![0]!![TextureOpKaleidoscope.mirroredX]
				outputGreen!![i] = input[1]!![TextureOpKaleidoscope.mirroredX]
				outputBlue!![i] = input[2]!![TextureOpKaleidoscope.mirroredX]
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!wj", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray? {
		@Pc(16) val output: IntArray? = super.monochromeImageCache!!.get(row)
		if (super.monochromeImageCache!!.dirty) {
			@Pc(22) var x: Int = 0
			while (x < EnvironmentLight.textureOpWidth) {
				this.reflect(x, row)
				@Pc(37) val source: IntArray? = this.getChildMonochromeOutput(TextureOpKaleidoscope.mirroredY, 0)
				output!![x] = source!![TextureOpKaleidoscope.mirroredX]
				x++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!wj", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			super.monochrome = packet!!.readUByte() == 1
		}
	}

	@OriginalMember(owner = "client!wj", name = "a", descriptor = "(IIB)V")
	private fun reflect(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int) {
		@Pc(9) val columnCoord: Int = EnvironmentLight.horizontalTable!![x]
		@Pc(13) val rowCoord: Int = TextureRaster.verticalTable!![y]
		@Pc(32) val angle: Float = kotlin.math.atan2((columnCoord - 2048).toDouble(), (rowCoord - 2048).toDouble()).toFloat()
		if (angle.toDouble() >= -3.141592653589793 && angle.toDouble() <= -2.356194490192345) {
			TextureOpKaleidoscope.mirroredY = y
			TextureOpKaleidoscope.mirroredX = x
		} else if (angle.toDouble() <= -1.5707963267948966 && angle.toDouble() >= -2.356194490192345) {
			TextureOpKaleidoscope.mirroredY = x
			TextureOpKaleidoscope.mirroredX = y
		} else if (angle.toDouble() <= -0.7853981633974483 && angle.toDouble() >= -1.5707963267948966) {
			TextureOpKaleidoscope.mirroredX = EnvironmentLight.textureOpWidth - y
			TextureOpKaleidoscope.mirroredY = x
		} else if (angle <= 0.0f && angle.toDouble() >= -0.7853981633974483) {
			TextureOpKaleidoscope.mirroredX = x
			TextureOpKaleidoscope.mirroredY = TextureOperation.currentHeight - y
		} else if (angle >= 0.0f && angle.toDouble() <= 0.7853981633974483) {
			TextureOpKaleidoscope.mirroredX = EnvironmentLight.textureOpWidth - x
			TextureOpKaleidoscope.mirroredY = TextureOperation.currentHeight - y
		} else if (angle.toDouble() >= 0.7853981633974483 && angle.toDouble() <= 1.5707963267948966) {
			TextureOpKaleidoscope.mirroredX = EnvironmentLight.textureOpWidth - y
			TextureOpKaleidoscope.mirroredY = TextureOperation.currentHeight - x
		} else if (angle.toDouble() >= 1.5707963267948966 && angle.toDouble() <= 2.356194490192345) {
			TextureOpKaleidoscope.mirroredY = TextureOperation.currentHeight - x
			TextureOpKaleidoscope.mirroredX = y
		} else if (angle.toDouble() >= 2.356194490192345 && angle.toDouble() <= 3.141592653589793) {
			TextureOpKaleidoscope.mirroredY = y
			TextureOpKaleidoscope.mirroredX = EnvironmentLight.textureOpWidth - x
		}
		TextureOpKaleidoscope.mirroredY = TextureOpKaleidoscope.mirroredY and TextureOperation.columnEnd
		TextureOpKaleidoscope.mirroredX = TextureOpKaleidoscope.mirroredX and TextureRaster.widthMask
	}

	companion object {
		@OriginalMember(owner = "client!ng", name = "c", descriptor = "I")
		@JvmField
		var mirroredX: Int = 0

		@OriginalMember(owner = "client!ah", name = "d", descriptor = "I")
		@JvmField
		var mirroredY: Int = 0
	}
}
