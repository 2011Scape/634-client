package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation

@OriginalClass("client!oi")
class TextureOpGreyscale @OriginalMember(owner = "client!oi", name = "<init>", descriptor = "()V") constructor() : TextureOperation(1, true) {

	@OriginalMember(owner = "client!oi", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(13) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(31) val source: Array<IntArray?> = this.getChildColorOutput(row, 0)!!
			@Pc(35) val sourceRed: IntArray = source[0]!!
			@Pc(39) val sourceGreen: IntArray = source[1]!!
			@Pc(43) val sourceBlue: IntArray = source[2]!!
			@Pc(45) var x: Int = 0
			while (x < EnvironmentLight.textureOpWidth) {
				output[x] = (sourceRed[x] + sourceGreen[x] + sourceBlue[x]) / 3
				x++
			}
		}
		return output
	}
}
