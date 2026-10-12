package com.jagex.game.runetek5.client.input.keyboard

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ll")
interface KeyRecord {

	@OriginalMember(owner = "client!ll", name = "a", descriptor = "(I)I")
	fun getModifierFlags(): Int

	@OriginalMember(owner = "client!ll", name = "b", descriptor = "(I)I")
	fun getType(): Int

	@OriginalMember(owner = "client!ll", name = "a", descriptor = "(B)C")
	fun getKeyChar(): Char

	@OriginalMember(owner = "client!ll", name = "b", descriptor = "(B)J")
	fun getTime(): Long

	@OriginalMember(owner = "client!ll", name = "a", descriptor = "(Z)I")
	fun getKeyCode(): Int
}
