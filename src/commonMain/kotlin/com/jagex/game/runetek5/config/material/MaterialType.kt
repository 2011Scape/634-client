package com.jagex.game.runetek5.config.material

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!hd")
class MaterialType {

	@OriginalMember(owner = "client!hd", name = "a", descriptor = "B")
	@JvmField
	var speedU: Byte = 0

	@OriginalMember(owner = "client!hd", name = "b", descriptor = "Z")
	@JvmField
	var isSmall: Boolean = false

	@OriginalMember(owner = "client!hd", name = "c", descriptor = "I")
	@JvmField
	var effectCombiner: Int = 0

	@OriginalMember(owner = "client!hd", name = "d", descriptor = "B")
	@JvmField
	var alpha: Byte = 0

	@OriginalMember(owner = "client!hd", name = "e", descriptor = "B")
	@JvmField
	var effectParam1: Byte = 0

	@OriginalMember(owner = "client!hd", name = "f", descriptor = "I")
	@JvmField
	var combineMode: Int = 0

	@OriginalMember(owner = "client!hd", name = "g", descriptor = "Z")
	@JvmField
	var skipTriangles: Boolean = false

	@OriginalMember(owner = "client!hd", name = "h", descriptor = "B")
	@JvmField
	var mipmapping: Byte = 0

	@OriginalMember(owner = "client!hd", name = "i", descriptor = "I")
	@JvmField
	var effectParam2: Int = 0

	@OriginalMember(owner = "client!hd", name = "j", descriptor = "Z")
	@JvmField
	var transposed: Boolean = false

	@OriginalMember(owner = "client!hd", name = "k", descriptor = "S")
	@JvmField
	var colorHsl: Short = 0

	@OriginalMember(owner = "client!hd", name = "l", descriptor = "Z")
	@JvmField
	var detailsOnly: Boolean = false

	@OriginalMember(owner = "client!hd", name = "n", descriptor = "B")
	@JvmField
	var speedV: Byte = 0

	@OriginalMember(owner = "client!hd", name = "o", descriptor = "Z")
	@JvmField
	var unusedFlag: Boolean = false

	@OriginalMember(owner = "client!hd", name = "p", descriptor = "B")
	@JvmField
	var effectType: Byte = 0

	@OriginalMember(owner = "client!hd", name = "r", descriptor = "Z")
	@JvmField
	var repeatT: Boolean = false

	@OriginalMember(owner = "client!hd", name = "s", descriptor = "B")
	@JvmField
	var brightness: Byte = 0

	@OriginalMember(owner = "client!hd", name = "t", descriptor = "Z")
	@JvmField
	var repeatS: Boolean = false

	@OriginalMember(owner = "client!hd", name = "u", descriptor = "Z")
	@JvmField
	var hdr: Boolean = false

	companion object {
		@OriginalMember(owner = "client!tr", name = "a", descriptor = "(BI)Z")
		@JvmStatic
		fun isOpaqueEffect(@OriginalArg(1) effectType: Int): Boolean {
			return effectType != 1 && effectType != 7
		}
	}
}
