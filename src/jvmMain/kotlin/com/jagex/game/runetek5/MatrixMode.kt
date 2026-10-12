package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!qv")
class MatrixMode {

	@OriginalMember(owner = "client!qv", name = "a", descriptor = "(I)Z")
	fun isSceneMode(): Boolean {
		return (MatrixMode.aClass286_6 === this) or (MatrixMode.aClass286_1 === this)
	}

	@OriginalMember(owner = "client!qv", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!sb", name = "c", descriptor = "Lclient!qv;")
		@JvmField
		val aClass286_7: MatrixMode = MatrixMode()

		@OriginalMember(owner = "client!at", name = "ld", descriptor = "Lclient!qv;")
		@JvmField
		val aClass286_1: MatrixMode = MatrixMode()
		@OriginalMember(owner = "client!qaa", name = "c", descriptor = "Lclient!qv;")
		@JvmField
		val aClass286_6: MatrixMode = MatrixMode()

		@OriginalMember(owner = "client!oga", name = "f", descriptor = "Lclient!qv;")
		@JvmField
		val aClass286_5: MatrixMode = MatrixMode()
	}
}
