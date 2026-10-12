package com.jagex.game.runetek5.toolkit.opengl

import com.jagex.game.runetek5.DataType
import com.jagex.game.runetek5.PixelFormat
import org.openrs2.deob.annotation.OriginalMember

object GLActionFormat {
	@OriginalMember(owner = "client!baa", name = "d", descriptor = "Lclient!baa;")
	@JvmField
	val DEPTH_COMPONENT_24: DataType = DataType(2)

	@OriginalMember(owner = "client!fi", name = "L", descriptor = "Lclient!wq;")
	@JvmField
	val PIXEL_FORMAT: PixelFormat = PixelFormat(1)
}
