package com.jagex.game.runetek5.toolkit.opengl

import com.jagex.game.runetek5.DataType
import com.jagex.game.runetek5.PixelFormat
import org.openrs2.deob.annotation.OriginalMember

object GLFormat {
	@OriginalMember(owner = "client!baa", name = "f", descriptor = "Lclient!baa;")
	@JvmField
	val RGBA: DataType = DataType(1)

	@OriginalMember(owner = "client!mo", name = "g", descriptor = "Lclient!wq;")
	@JvmField
	val HDR: PixelFormat = PixelFormat(4)
}
