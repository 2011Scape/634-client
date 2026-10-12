package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!ria")
class FilterMode {

	@OriginalMember(owner = "client!ria", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!rg", name = "k", descriptor = "Lclient!ria;")
		@JvmField
		val aClass298_10: FilterMode = FilterMode()
	}
}
