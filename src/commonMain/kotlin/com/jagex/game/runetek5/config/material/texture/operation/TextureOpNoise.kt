package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.ClientTrigger
import com.jagex.game.runetek5.config.material.texture.TextureRaster

@OriginalClass("client!rn")
class TextureOpNoise : TextureOperation {

	companion object {
		init {
			ClientTrigger("", 73)
		}
	}

	@OriginalMember(owner = "client!rn", name = "<init>", descriptor = "()V")
	constructor() : super(0, true)

	@OriginalMember(owner = "client!rn", name = "d", descriptor = "(III)I")
	private fun hashNoise(@OriginalArg(0) y: Int, @OriginalArg(1) x: Int): Int {
		@Pc(15) val seed: Int = x + y * 57
		@Pc(21) val hash: Int = seed xor (seed shl 1)
		return 4096 - ((((hash * 15731 * hash + 789221) * hash + 1376312589) and Int.MAX_VALUE) / 262144)
	}

	@OriginalMember(owner = "client!rn", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(19) val verticalPos: Int = TextureRaster.verticalTable!![row]
			@Pc(21) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				output[i] = this.hashNoise(verticalPos, EnvironmentLight.horizontalTable!![i]) % 4096
				i++
			}
		}
		return output
	}
}
