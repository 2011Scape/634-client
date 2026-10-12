package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic
import lang.Class
import lang.getDeclaredConstructor

@OriginalClass("client!io")
abstract class AbstractNanoTimer {

	@OriginalMember(owner = "client!io", name = "b", descriptor = "(I)J")
	abstract fun nanoTime(): Long

	companion object {
		@OriginalMember(owner = "client!bea", name = "d", descriptor = "(I)Lclient!io;")
		@JvmStatic
		fun create(): AbstractNanoTimer {
			try {
				return lang.forName("com.jagex.game.runetek5.NanoTimer").getDeclaredConstructor().newInstance() as AbstractNanoTimer
			} catch (@Pc(9) ex: Throwable) {
				return MillisNanoTimer()
			}
		}
	}
}
