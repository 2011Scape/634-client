package com.jagex.game.runetek5.client.input.keyboard

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!ica")
class KeyRecordEntry : Node(), KeyRecord {

	@OriginalMember(owner = "client!ica", name = "p", descriptor = "C")
	@JvmField
	var keyCharacter: Char = '\u0000'

	@OriginalMember(owner = "client!ica", name = "s", descriptor = "J")
	@JvmField
	var timeSubmitted: Long = 0L

	@OriginalMember(owner = "client!ica", name = "u", descriptor = "I")
	@JvmField
	var modifierFlags: Int = 0

	@OriginalMember(owner = "client!ica", name = "v", descriptor = "I")
	@JvmField
	var keyCode: Int = 0

	@OriginalMember(owner = "client!ica", name = "z", descriptor = "I")
	@JvmField
	var type: Int = 0

	@OriginalMember(owner = "client!ica", name = "b", descriptor = "(I)I")
	override fun getType(): Int {
		return this.type
	}

	@OriginalMember(owner = "client!ica", name = "a", descriptor = "(B)C")
	override fun getKeyChar(): Char {
		return this.keyCharacter
	}

	@OriginalMember(owner = "client!ica", name = "a", descriptor = "(I)I")
	override fun getModifierFlags(): Int {
		return this.modifierFlags
	}

	@OriginalMember(owner = "client!ica", name = "b", descriptor = "(B)J")
	override fun getTime(): Long {
		return this.timeSubmitted
	}

	@OriginalMember(owner = "client!ica", name = "a", descriptor = "(Z)I")
	override fun getKeyCode(): Int {
		return this.keyCode
	}
}
