package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.graphics.EnvironmentLight
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!qu")
class TextureOpHorizontalGradient : TextureOperation {

	@OriginalMember(owner = "client!qu", name = "<init>", descriptor = "()V")
	constructor() : super(0, true)

	@OriginalMember(owner = "client!qu", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray? {
		return EnvironmentLight.horizontalTable
	}
}
