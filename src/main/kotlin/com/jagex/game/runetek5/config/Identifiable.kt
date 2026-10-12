package com.jagex.game.runetek5.config

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jha")
interface Identifiable {

	@OriginalMember(owner = "client!jha", name = "a", descriptor = "(Z)I")
	fun getId(): Int
}
