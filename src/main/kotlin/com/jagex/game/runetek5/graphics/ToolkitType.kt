package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember

object ToolkitType {

	const val JAVA: Int = 0

	const val GL: Int = 1

	const val SSE: Int = 2

	const val D3D: Int = 3

	const val GLX: Int = 5

	@OriginalMember(owner = "client!eh", name = "b", descriptor = "(II)Z")
	@JvmStatic
	fun isHardwareToolkit(@OriginalArg(1) toolkitId: Int): Boolean {
		return toolkitId == 1 || toolkitId == 3 || toolkitId == 5
	}

	@OriginalMember(owner = "client!ok", name = "a", descriptor = "(BI)Z")
	@JvmStatic
	fun isSoftwareToolkit(@OriginalArg(1) toolkitId: Int): Boolean {
		return toolkitId == 0 || toolkitId == 2
	}
}
