package com.jagex.game.runetek5.graphics.loginscreen

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!aaa")
interface DrawableItem {

	@OriginalMember(owner = "client!aaa", name = "a", descriptor = "(I)V")
	fun init()

	@OriginalMember(owner = "client!aaa", name = "a", descriptor = "(B)Z")
	fun ready(): Boolean

	@OriginalMember(owner = "client!aaa", name = "a", descriptor = "(BZ)V")
	fun execute()
}
