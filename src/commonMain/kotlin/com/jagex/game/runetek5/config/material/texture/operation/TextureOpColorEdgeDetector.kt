package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import com.jagex.game.runetek5.config.material.texture.TextureRaster

@OriginalClass("client!cfa")
class TextureOpColorEdgeDetector @OriginalMember(owner = "client!cfa", name = "<init>", descriptor = "()V") constructor() : TextureOperation(1, false) {

	@OriginalMember(owner = "client!cfa", name = "P", descriptor = "I")
	private var strength: Int = 4096

	@OriginalMember(owner = "client!cfa", name = "Q", descriptor = "Z")
	private var normalized: Boolean = true

	@OriginalMember(owner = "client!cfa", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.strength = packet!!.readUShort()
		} else if (opcode == 1) {
			this.normalized = packet!!.readUByte() == 1
		}
	}

	@OriginalMember(owner = "client!cfa", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) row: Int): Array<IntArray?> {
		@Pc(11) val output: Array<IntArray?> = this.colorImageCache!!.get(row)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(32) val prevRow: IntArray = this.getChildMonochromeOutput((row - 1) and TextureOperation.columnEnd, 0)!!
			@Pc(40) val currentRow: IntArray = this.getChildMonochromeOutput(row, 0)!!
			@Pc(50) val nextRow: IntArray = this.getChildMonochromeOutput(TextureOperation.columnEnd and (row + 1), 0)!!
			@Pc(54) val outputRed: IntArray = output[0]!!
			@Pc(58) val outputGreen: IntArray = output[1]!!
			@Pc(62) val outputBlue: IntArray = output[2]!!
			@Pc(64) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(78) val deltaY: Int = (nextRow[i] - prevRow[i]) * this.strength
				@Pc(97) val deltaX: Int = this.strength * (currentRow[(i + 1) and TextureRaster.widthMask] - currentRow[TextureRaster.widthMask and (i - 1)])
				@Pc(101) val gradientX: Int = deltaX shr 12
				@Pc(105) val gradientY: Int = deltaY shr 12
				@Pc(111) val gradientXSquared: Int = (gradientX * gradientX) shr 12
				@Pc(117) val gradientYSquared: Int = (gradientY * gradientY) shr 12
				@Pc(132) val magnitude: Int = (kotlin.math.sqrt(((gradientXSquared + gradientYSquared + 4096).toFloat() / 4096.0f).toDouble()) * 4096.0).toInt()
				@Pc(138) var normalX: Int
				@Pc(142) var normalY: Int
				@Pc(146) var normalZ: Int
				if (magnitude == 0) {
					normalY = 0
					normalX = 0
					normalZ = 0
				} else {
					normalX = deltaX / magnitude
					normalY = deltaY / magnitude
					normalZ = 16777216 / magnitude
				}
				if (this.normalized) {
					normalZ = (normalZ shr 1) + 2048
					normalX = (normalX shr 1) + 2048
					normalY = (normalY shr 1) + 2048
				}
				outputRed[i] = normalX
				outputGreen[i] = normalY
				outputBlue[i] = normalZ
				i++
			}
		}
		return output
	}
}
