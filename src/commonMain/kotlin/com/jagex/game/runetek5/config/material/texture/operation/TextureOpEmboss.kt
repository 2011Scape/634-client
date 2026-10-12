package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import com.jagex.game.runetek5.config.material.texture.TextureRaster

@OriginalClass("client!dda")
class TextureOpEmboss @OriginalMember(owner = "client!dda", name = "<init>", descriptor = "()V") constructor() : TextureOperation(1, true) {

	@OriginalMember(owner = "client!dda", name = "O", descriptor = "I")
	private var lightElevation: Int = 3216

	@OriginalMember(owner = "client!dda", name = "Q", descriptor = "I")
	private var lightAzimuth: Int = 3216

	@OriginalMember(owner = "client!dda", name = "R", descriptor = "I")
	private var embossDepth: Int = 4096

	@OriginalMember(owner = "client!dda", name = "S", descriptor = "[I")
	private val lightDirection: IntArray = IntArray(3)

	@OriginalMember(owner = "client!dda", name = "f", descriptor = "(I)V")
	private fun computeLightDirection() {
		@Pc(17) val cosElevation: Double = kotlin.math.cos((this.lightElevation.toFloat() / 4096.0f).toDouble())
		this.lightDirection[0] = (cosElevation * kotlin.math.sin((this.lightAzimuth.toFloat() / 4096.0f).toDouble()) * 4096.0).toInt()
		this.lightDirection[1] = (cosElevation * kotlin.math.cos((this.lightAzimuth.toFloat() / 4096.0f).toDouble()) * 4096.0).toInt()
		this.lightDirection[2] = (kotlin.math.sin((this.lightElevation.toFloat() / 4096.0f).toDouble()) * 4096.0).toInt()
		@Pc(75) val xSquared: Int = (this.lightDirection[0] * this.lightDirection[0]) shr 12
		@Pc(87) val ySquared: Int = (this.lightDirection[1] * this.lightDirection[1]) shr 12
		@Pc(99) val zSquared: Int = (this.lightDirection[2] * this.lightDirection[2]) shr 12
		@Pc(112) val magnitude: Int = (kotlin.math.sqrt(((ySquared + xSquared + zSquared) shr 12).toDouble()) * 4096.0).toInt()
		if (magnitude != 0) {
			this.lightDirection[1] = (this.lightDirection[1] shl 12) / magnitude
			this.lightDirection[0] = (this.lightDirection[0] shl 12) / magnitude
			this.lightDirection[2] = (this.lightDirection[2] shl 12) / magnitude
		}
	}

	@OriginalMember(owner = "client!dda", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(22) val scaledDepth: Int = (TextureRaster.scaledWidth * this.embossDepth) shr 12
			@Pc(32) val prevRow: IntArray = this.getChildMonochromeOutput(TextureOperation.columnEnd and (row - 1), 0)!!
			@Pc(38) val currentRow: IntArray = this.getChildMonochromeOutput(row, 0)!!
			@Pc(50) val nextRow: IntArray = this.getChildMonochromeOutput(TextureOperation.columnEnd and (row + 1), 0)!!
			@Pc(52) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(66) val gradientY: Int = (scaledDepth * (nextRow[i] - prevRow[i])) shr 12
				@Pc(87) val gradientX: Int = (scaledDepth * (currentRow[(i - 1) and TextureRaster.widthMask] - currentRow[TextureRaster.widthMask and (i + 1)])) shr 12
				@Pc(91) var absGradientX: Int = gradientX shr 4
				@Pc(95) var absGradientY: Int = gradientY shr 4
				if (absGradientX < 0) {
					absGradientX = -absGradientX
				}
				if (absGradientY < 0) {
					absGradientY = -absGradientY
				}
				if (absGradientX > 255) {
					absGradientX = 255
				}
				if (absGradientY > 255) {
					absGradientY = 255
				}
				@Pc(136) val normalScale: Int = Component.normalizeTable!![absGradientX + ((absGradientY * (absGradientY + 1)) shr 1)].toInt() and 0xFF
				@Pc(142) val normalZ: Int = (normalScale * 4096) shr 8
				@Pc(148) val normalX: Int = (normalScale * gradientX) shr 8
				@Pc(154) val normalY: Int = (gradientY * normalScale) shr 8
				@Pc(163) val dotY: Int = (normalY * this.lightDirection[1]) shr 12
				@Pc(172) val dotX: Int = (normalX * this.lightDirection[0]) shr 12
				@Pc(181) val dotZ: Int = (this.lightDirection[2] * normalZ) shr 12
				output[i] = dotZ + dotX + dotY
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!dda", name = "e", descriptor = "(I)V")
	override fun postDecode() {
		this.computeLightDirection()
	}

	@OriginalMember(owner = "client!dda", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.embossDepth = packet!!.readUShort()
		} else if (opcode == 1) {
			this.lightAzimuth = packet!!.readUShort()
		} else if (opcode == 2) {
			this.lightElevation = packet!!.readUShort()
		}
	}
}
