package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ofa")
class JavaComplexBlendedTile {

	@OriginalMember(owner = "client!ofa", name = "a", descriptor = "[I")
	@JvmField
	var vertexColours: IntArray? = null

	@OriginalMember(owner = "client!ofa", name = "b", descriptor = "[S")
	@JvmField
	var verticesZ: ShortArray? = null

	@OriginalMember(owner = "client!ofa", name = "c", descriptor = "S")
	@JvmField
	var vertexCount: Short = 0

	@OriginalMember(owner = "client!ofa", name = "d", descriptor = "B")
	@JvmField
	var flags: Byte = 0

	@OriginalMember(owner = "client!ofa", name = "e", descriptor = "[S")
	@JvmField
	var faceSizes: ShortArray? = null

	@OriginalMember(owner = "client!ofa", name = "f", descriptor = "[S")
	@JvmField
	var waterDepths: ShortArray? = null

	@OriginalMember(owner = "client!ofa", name = "g", descriptor = "[S")
	@JvmField
	var verticesY: ShortArray? = null

	@OriginalMember(owner = "client!ofa", name = "h", descriptor = "[S")
	@JvmField
	var faceTextures: ShortArray? = null

	@OriginalMember(owner = "client!ofa", name = "i", descriptor = "[S")
	@JvmField
	var verticesX: ShortArray? = null

	@OriginalMember(owner = "client!ofa", name = "j", descriptor = "S")
	@JvmField
	var faceCount: Short = 0

	@OriginalMember(owner = "client!ofa", name = "k", descriptor = "[I")
	@JvmField
	var faceBlendedColours: IntArray? = null
}
