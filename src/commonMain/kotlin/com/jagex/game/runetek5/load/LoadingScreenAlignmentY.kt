package com.jagex.game.runetek5.load

import com.jagex.game.runetek5.client.GameShell
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!ec")
class LoadingScreenAlignmentY {

	@OriginalMember(owner = "client!ec", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!ec", name = "a", descriptor = "(III)I")
	fun getY(@OriginalArg(0) height: Int, @OriginalArg(1) elementHeight: Int): Int {
		@Pc(15) val containerHeight: Int = if (GameShell.screenCanvasHeight <= height) height else GameShell.screenCanvasHeight
		if (LoadingScreenAlignmentY.ALIGN_TOP === this) {
			return 0
		} else if (LoadingScreenAlignmentY.ALIGN_BOTTOM === this) {
			return containerHeight - elementHeight
		} else if (LoadingScreenAlignmentY.ALIGN_MIDDLE === this) {
			return (containerHeight - elementHeight) / 2
		} else {
			return 0
		}
	}

	companion object {
		@OriginalMember(owner = "client!ht", name = "m", descriptor = "Lclient!ec;")
		@JvmField
		val ALIGN_MIDDLE: LoadingScreenAlignmentY = LoadingScreenAlignmentY()

		@OriginalMember(owner = "client!jw", name = "a", descriptor = "(I)[Lclient!ec;")
		@JvmStatic
		fun loadingScreenAlignmentYValues(): Array<LoadingScreenAlignmentY?> {
			return arrayOf<LoadingScreenAlignmentY?>(LoadingScreenAlignmentY.ALIGN_TOP, LoadingScreenAlignmentY.ALIGN_MIDDLE, LoadingScreenAlignmentY.ALIGN_BOTTOM)
		}

		@OriginalMember(owner = "client!vaa", name = "K", descriptor = "Lclient!ec;")
		@JvmField
		val ALIGN_BOTTOM: LoadingScreenAlignmentY = LoadingScreenAlignmentY()

		@OriginalMember(owner = "client!la", name = "m", descriptor = "Lclient!ec;")
		@JvmField
		val ALIGN_TOP: LoadingScreenAlignmentY = LoadingScreenAlignmentY()
	}
}
