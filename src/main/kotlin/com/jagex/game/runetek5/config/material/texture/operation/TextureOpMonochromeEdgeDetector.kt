package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.TextureRaster

@OriginalClass("client!kj")
class TextureOpMonochromeEdgeDetector : TextureOperation {

	@OriginalMember(owner = "client!kj", name = "J", descriptor = "I")
	private var strength: Int = 4096

	@OriginalMember(owner = "client!kj", name = "<init>", descriptor = "()V")
	constructor() : super(1, true)

	@OriginalMember(owner = "client!kj", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.strength = packet!!.readUShort()
		}
	}

	@OriginalMember(owner = "client!kj", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) y: Int): IntArray {
		@Pc(13) val output: IntArray = this.monochromeImageCache!!.get(y)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(27) val above: IntArray = this.getChildMonochromeOutput(TextureOperation.columnEnd and (y - 1), 0)!!
			@Pc(33) val source: IntArray = this.getChildMonochromeOutput(y, 0)!!
			@Pc(43) val below: IntArray = this.getChildMonochromeOutput((y + 1) and TextureOperation.columnEnd, 0)!!
			@Pc(45) var x: Int = 0
			while (x < EnvironmentLight.textureOpWidth) {
				@Pc(59) val scaledGradientY: Int = this.strength * (below[x] - above[x])
				@Pc(79) val scaledGradientX: Int = this.strength * (source[TextureRaster.widthMask and (x + 1)] - source[TextureRaster.widthMask and (x - 1)])
				@Pc(83) val gradientX: Int = scaledGradientX shr 12
				@Pc(87) val gradientY: Int = scaledGradientY shr 12
				@Pc(93) val squareX: Int = (gradientX * gradientX) shr 12
				@Pc(99) val squareY: Int = (gradientY * gradientY) shr 12
				@Pc(113) val normalLength: Int = (Math.sqrt(((squareY + squareX + 4096).toFloat() / 4096.0f).toDouble()) * 4096.0).toInt()
				@Pc(121) val flatness: Int = if (normalLength == 0) 0 else 16777216 / normalLength
				output[x] = 4096 - flatness
				x++
			}
		}
		return output
	}
}
