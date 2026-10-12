package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!gia")
class MidiProgramNode : Node {

	@OriginalMember(owner = "client!gia", name = "r", descriptor = "[B")
	@JvmField
	val notes: ByteArray?

	@OriginalMember(owner = "client!gia", name = "<init>", descriptor = "([B)V")
	constructor(@OriginalArg(0) value: ByteArray?) {
		this.notes = value
	}
}
