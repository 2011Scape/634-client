package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!ok")
class GpuModelNormals {

	@OriginalMember(owner = "client!ok", name = "a", descriptor = "[S")
	@JvmField
	var normalsZ: ShortArray? = null

	@OriginalMember(owner = "client!ok", name = "c", descriptor = "[S")
	@JvmField
	var normalsY: ShortArray? = null

	@OriginalMember(owner = "client!ok", name = "j", descriptor = "[S")
	@JvmField
	var normalsX: ShortArray? = null

	@OriginalMember(owner = "client!ok", name = "l", descriptor = "[B")
	@JvmField
	var magnitudes: ByteArray? = null
}
