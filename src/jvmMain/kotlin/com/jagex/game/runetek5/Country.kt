package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!vh")
class Country {

	@OriginalMember(owner = "client!vh", name = "b", descriptor = "I")
	@JvmField
	var flag: Int = 0

	@OriginalMember(owner = "client!vh", name = "h", descriptor = "Ljava/lang/String;")
	@JvmField
	var name: String? = null

	companion object {
		@OriginalMember(owner = "client!bd", name = "x", descriptor = "[Lclient!vh;")
		@JvmField
		var aClass359Array1: Array<Country?>? = null
	}
}
