package com.jagex.game.runetek5.core.io

import java.io.IOException
import java.io.OutputStream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!tk")
class DummyOutputStream : OutputStream() {

	@OriginalMember(owner = "client!tk", name = "write", descriptor = "(I)V")
	@Throws(IOException::class)
	override fun write(@OriginalArg(0) value: Int) {
		throw IOException()
	}
}
