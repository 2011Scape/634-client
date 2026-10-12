package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!rb")
class JavaGenericBlendedTile {

	@OriginalMember(owner = "client!rb", name = "a", descriptor = "[S")
	@JvmField
	var verticesZ: ShortArray? = null

	@OriginalMember(owner = "client!rb", name = "b", descriptor = "[S")
	@JvmField
	var vertexSizes: ShortArray? = null

	@OriginalMember(owner = "client!rb", name = "c", descriptor = "[I")
	@JvmField
	var faceBlendedColours: IntArray? = null

	@OriginalMember(owner = "client!rb", name = "d", descriptor = "[S")
	@JvmField
	var vertexTextures: ShortArray? = null

	@OriginalMember(owner = "client!rb", name = "e", descriptor = "[B")
	@JvmField
	var vertexLight: ByteArray? = null

	@OriginalMember(owner = "client!rb", name = "f", descriptor = "[S")
	@JvmField
	var verticesY: ShortArray? = null

	@OriginalMember(owner = "client!rb", name = "g", descriptor = "S")
	@JvmField
	var faceCount: Short = 0

	@OriginalMember(owner = "client!rb", name = "h", descriptor = "S")
	@JvmField
	var vertexCount: Short = 0

	@OriginalMember(owner = "client!rb", name = "i", descriptor = "[S")
	@JvmField
	var waterDepths: ShortArray? = null

	@OriginalMember(owner = "client!rb", name = "j", descriptor = "[S")
	@JvmField
	var verticesX: ShortArray? = null

	@OriginalMember(owner = "client!rb", name = "k", descriptor = "[I")
	@JvmField
	var vertexColours: IntArray? = null
}
