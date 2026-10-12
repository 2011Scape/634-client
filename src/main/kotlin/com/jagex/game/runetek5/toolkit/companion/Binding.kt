package com.jagex.game.runetek5.toolkit.companion

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!pca")
interface Binding {

	@OriginalMember(owner = "client!pca", name = "a", descriptor = "(I)V")
	fun detach()
}
