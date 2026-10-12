package com.jagex.game.runetek5.core.io

import com.jagex.game.runetek5.core.util.TimeUtils
import java.io.InputStream
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!sfa")
class DummyInputStream : InputStream() {

	@OriginalMember(owner = "client!sfa", name = "read", descriptor = "()I")
	override fun read(): Int {
		TimeUtils.sleep(30000L)
		return -1
	}
}
