package com.jagex.game.runetek5.cache.compression

import org.openrs2.deob.annotation.OriginalMember

object Bzip2Compressor {
	@OriginalMember(owner = "client!em", name = "s", descriptor = "[I")
	@JvmField
	var transformTable: IntArray? = null
}
