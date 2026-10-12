package com.jagex.game.runetek5.core.io.connection

import io.IOException
import io.OutputStream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!sc")
class DummyOutputStream : OutputStream() {

	@OriginalMember(owner = "client!sc", name = "write", descriptor = "(I)V")
	@Throws(IOException::class)
	override fun write(@OriginalArg(0) value: Int) {
		throw IOException()
	}
}
