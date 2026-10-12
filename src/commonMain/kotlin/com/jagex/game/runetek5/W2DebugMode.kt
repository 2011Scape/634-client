package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import org.openrs2.deob.annotation.OriginalArg
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object W2DebugMode {

	@OriginalMember(owner = "client!ju", name = "J", descriptor = "I")
	@JvmField
	var w2DebugMode: Int = 0
	@OriginalMember(owner = "client!qw", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun setW2DebugMode(@OriginalArg(0) w2DebugMode: Int) {
		W2DebugMode.sceneDebugMode = w2DebugMode
	}

	@OriginalMember(owner = "client!bw", name = "a", descriptor = "(Lclient!da;)V")
	@JvmStatic
	fun setFont(@OriginalArg(0) font: AbstractFontRenderer?) {
		W2DebugMode.font = font
	}

	@OriginalMember(owner = "client!jg", name = "C", descriptor = "I")
	@JvmField
	var sceneDebugMode: Int = 0

	@OriginalMember(owner = "client!fg", name = "a", descriptor = "Lclient!da;")
	@JvmField
	var font: AbstractFontRenderer? = null
}
