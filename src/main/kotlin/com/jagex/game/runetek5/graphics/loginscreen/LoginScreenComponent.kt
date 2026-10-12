package com.jagex.game.runetek5.graphics.loginscreen

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!dca")
interface LoginScreenComponent {

	@OriginalMember(owner = "client!dca", name = "a", descriptor = "(B)I")
	fun getFadeDuration(): Int

	@OriginalMember(owner = "client!dca", name = "a", descriptor = "(BJ)Z")
	fun finished(@OriginalArg(1) initTime: Long): Boolean

	@OriginalMember(owner = "client!dca", name = "a", descriptor = "(I)V")
	fun init()

	@OriginalMember(owner = "client!dca", name = "a", descriptor = "(ZI)V")
	fun render(@OriginalArg(0) fullRepaint: Boolean)

	@OriginalMember(owner = "client!dca", name = "b", descriptor = "(I)V")
	fun cleanup()

	@OriginalMember(owner = "client!dca", name = "c", descriptor = "(I)I")
	fun percentage(): Int
}
