package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!b")
class DisplayModeDetails {

	@OriginalMember(owner = "client!b", name = "a", descriptor = "I")
	@JvmField
	var bitDepth: Int = 0

	@OriginalMember(owner = "client!b", name = "b", descriptor = "I")
	@JvmField
	var width: Int = 0

	@OriginalMember(owner = "client!b", name = "g", descriptor = "I")
	@JvmField
	var refreshRate: Int = 0

	@OriginalMember(owner = "client!b", name = "i", descriptor = "I")
	@JvmField
	var height: Int = 0
}
