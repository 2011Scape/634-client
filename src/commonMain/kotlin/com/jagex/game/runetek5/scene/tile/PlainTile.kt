package com.jagex.game.runetek5.scene.tile

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!mga")
class PlainTile {

	@OriginalMember(owner = "client!mga", name = "b", descriptor = "S")
	@JvmField
	var hslSw: Short = 0

	@OriginalMember(owner = "client!mga", name = "c", descriptor = "S")
	@JvmField
	var hslNe: Short = 0

	@OriginalMember(owner = "client!mga", name = "d", descriptor = "S")
	@JvmField
	var hslSe: Short = 0

	@OriginalMember(owner = "client!mga", name = "e", descriptor = "S")
	@JvmField
	var hslNw: Short = 0

	@OriginalMember(owner = "client!mga", name = "f", descriptor = "S")
	@JvmField
	var texture: Short = 0

	@OriginalMember(owner = "client!mga", name = "g", descriptor = "B")
	@JvmField
	var functionMask: Byte = 0

	@OriginalMember(owner = "client!mga", name = "a", descriptor = "I")
	@JvmField
	var color: Int = -1
}
