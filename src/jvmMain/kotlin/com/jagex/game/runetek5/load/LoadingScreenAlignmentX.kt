package com.jagex.game.runetek5.load

import com.jagex.game.runetek5.client.GameShell
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!sd")
class LoadingScreenAlignmentX {

	@OriginalMember(owner = "client!sd", name = "a", descriptor = "(IIB)I")
	fun getX(@OriginalArg(0) width: Int, @OriginalArg(1) elementWidth: Int): Int {
		@Pc(14) val containerWidth: Int = if (width < GameShell.screenCanvasWidth) GameShell.screenCanvasWidth else width
		if (LoadingScreenAlignmentX.ALIGN_LEFT === this) {
			return 0
		} else if (LoadingScreenAlignmentX.ALIGN_RIGHT === this) {
			return containerWidth - elementWidth
		} else if (LoadingScreenAlignmentX.ALIGN_CENTER === this) {
			return (containerWidth - elementWidth) / 2
		} else {
			return 0
		}
	}

	@OriginalMember(owner = "client!sd", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!ce", name = "z", descriptor = "Lclient!sd;")
		@JvmField
		val ALIGN_LEFT: LoadingScreenAlignmentX = LoadingScreenAlignmentX()

		@OriginalMember(owner = "client!mha", name = "k", descriptor = "Lclient!sd;")
		@JvmField
		val ALIGN_CENTER: LoadingScreenAlignmentX = LoadingScreenAlignmentX()

		@OriginalMember(owner = "client!ad", name = "a", descriptor = "Lclient!sd;")
		@JvmField
		val ALIGN_RIGHT: LoadingScreenAlignmentX = LoadingScreenAlignmentX()

		@OriginalMember(owner = "client!ot", name = "a", descriptor = "(I)[Lclient!sd;")
		@JvmStatic
		fun loadingScreenAlignmentXValues(): Array<LoadingScreenAlignmentX?> {
			return arrayOf<LoadingScreenAlignmentX?>(LoadingScreenAlignmentX.ALIGN_LEFT, LoadingScreenAlignmentX.ALIGN_CENTER, LoadingScreenAlignmentX.ALIGN_RIGHT)
		}
	}
}
