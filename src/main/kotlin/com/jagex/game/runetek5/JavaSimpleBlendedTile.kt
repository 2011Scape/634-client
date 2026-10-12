package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ul")
class JavaSimpleBlendedTile {

	@OriginalMember(owner = "client!ul", name = "a", descriptor = "S")
	@JvmField
	var texture: Short = 0

	@OriginalMember(owner = "client!ul", name = "b", descriptor = "I")
	@JvmField
	var colourNe: Int = 0

	@OriginalMember(owner = "client!ul", name = "c", descriptor = "I")
	@JvmField
	var colourNw: Int = 0

	@OriginalMember(owner = "client!ul", name = "d", descriptor = "S")
	@JvmField
	var waterDepthSw: Short = 0

	@OriginalMember(owner = "client!ul", name = "e", descriptor = "S")
	@JvmField
	var waterDepthSe: Short = 0

	@OriginalMember(owner = "client!ul", name = "f", descriptor = "S")
	@JvmField
	var waterDepthNe: Short = 0

	@OriginalMember(owner = "client!ul", name = "g", descriptor = "S")
	@JvmField
	var waterDepthNw: Short = 0

	@OriginalMember(owner = "client!ul", name = "h", descriptor = "B")
	@JvmField
	var flags: Byte = 0

	@OriginalMember(owner = "client!ul", name = "i", descriptor = "I")
	@JvmField
	var colourSe: Int = 0

	@OriginalMember(owner = "client!ul", name = "k", descriptor = "I")
	@JvmField
	var colourSw: Int = 0

	@OriginalMember(owner = "client!ul", name = "j", descriptor = "I")
	@JvmField
	var blendedColour: Int = 0
}
