package com.jagex.game.runetek5.graphics.model.normal

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!oba")
class ModelNormalsGL {

	@OriginalMember(owner = "client!oba", name = "a", descriptor = "[B")
	@JvmField
	var magnitude: ByteArray? = null

	@OriginalMember(owner = "client!oba", name = "d", descriptor = "[S")
	@JvmField
	var normalsY: ShortArray? = null

	@OriginalMember(owner = "client!oba", name = "f", descriptor = "[S")
	@JvmField
	var normalsZ: ShortArray? = null

	@OriginalMember(owner = "client!oba", name = "g", descriptor = "[S")
	@JvmField
	var normalsX: ShortArray? = null
}
