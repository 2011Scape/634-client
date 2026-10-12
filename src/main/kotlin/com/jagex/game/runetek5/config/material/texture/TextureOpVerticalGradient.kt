package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.graphics.EnvironmentLight
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!hl")
class TextureOpVerticalGradient : TextureOperation {

	@OriginalMember(owner = "client!hl", name = "<init>", descriptor = "()V")
	constructor() : super(0, true)

	@OriginalMember(owner = "client!hl", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) y: Int): IntArray? {
		@Pc(14) val dest: IntArray? = super.monochromeImageCache!!.get(y)
		if (super.monochromeImageCache!!.dirty) {
			Arrays.setVariables(dest, 0, EnvironmentLight.textureOpWidth, TextureRaster.verticalTable!![y])
		}
		return dest
	}
}
