package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!tv")
interface GlRenderTarget {

	@OriginalMember(owner = "client!tv", name = "a", descriptor = "(B)V")
	fun unbindRead()

	@OriginalMember(owner = "client!tv", name = "a", descriptor = "(I)V")
	fun bind()

	@OriginalMember(owner = "client!tv", name = "b", descriptor = "(I)V")
	fun bindDraw()

	@OriginalMember(owner = "client!tv", name = "c", descriptor = "(I)V")
	fun unbind()

	@OriginalMember(owner = "client!tv", name = "d", descriptor = "(I)V")
	fun bindRead()

	@OriginalMember(owner = "client!tv", name = "e", descriptor = "(I)V")
	fun unbindDraw()
}
