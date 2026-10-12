package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!sp")
class JavaComplexTile {

	@OriginalMember(owner = "client!sp", name = "a", descriptor = "[S")
	@JvmField
	var verticesLight: ShortArray? = null

	@OriginalMember(owner = "client!sp", name = "b", descriptor = "S")
	@JvmField
	var vertexCount: Short = 0

	@OriginalMember(owner = "client!sp", name = "c", descriptor = "[S")
	@JvmField
	var faceB: ShortArray? = null

	@OriginalMember(owner = "client!sp", name = "d", descriptor = "[S")
	@JvmField
	var faceA: ShortArray? = null

	@OriginalMember(owner = "client!sp", name = "e", descriptor = "[I")
	@JvmField
	var faceBlendedColours: IntArray? = null

	@OriginalMember(owner = "client!sp", name = "f", descriptor = "[I")
	@JvmField
	var faceColours: IntArray? = null

	@OriginalMember(owner = "client!sp", name = "g", descriptor = "[S")
	@JvmField
	var verticesZ: ShortArray? = null

	@OriginalMember(owner = "client!sp", name = "h", descriptor = "[S")
	@JvmField
	var faceTextures: ShortArray? = null

	@OriginalMember(owner = "client!sp", name = "i", descriptor = "[S")
	@JvmField
	var verticesX: ShortArray? = null

	@OriginalMember(owner = "client!sp", name = "j", descriptor = "[S")
	@JvmField
	var verticesY: ShortArray? = null

	@OriginalMember(owner = "client!sp", name = "k", descriptor = "[S")
	@JvmField
	var faceSizes: ShortArray? = null

	@OriginalMember(owner = "client!sp", name = "l", descriptor = "S")
	@JvmField
	var faceCount: Short = 0

	@OriginalMember(owner = "client!sp", name = "m", descriptor = "B")
	@JvmField
	var flags: Byte = 0

	@OriginalMember(owner = "client!sp", name = "n", descriptor = "[S")
	@JvmField
	var faceC: ShortArray? = null
}
