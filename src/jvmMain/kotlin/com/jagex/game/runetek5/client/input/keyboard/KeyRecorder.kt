package com.jagex.game.runetek5.client.input.keyboard

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!efa")
abstract class KeyRecorder @OriginalMember(owner = "client!efa", name = "<init>", descriptor = "()V") constructor() {

	@OriginalMember(owner = "client!efa", name = "a", descriptor = "(I)V")
	abstract fun prepareAwaitingKeys()

	@OriginalMember(owner = "client!efa", name = "a", descriptor = "(II)Z")
	abstract fun isPressed(@OriginalArg(0) keyCode: Int): Boolean

	@OriginalMember(owner = "client!efa", name = "b", descriptor = "(I)Lclient!ll;")
	abstract fun removeFirstRecorded(): KeyRecord?

	@OriginalMember(owner = "client!efa", name = "c", descriptor = "(I)V")
	abstract fun remove()

	companion object {
		@OriginalMember(owner = "client!qca", name = "e", descriptor = "Lclient!efa;")
		@JvmField
		var keyRecorder: KeyRecorder? = null
		@OriginalMember(owner = "client!wu", name = "f", descriptor = "[Lclient!ll;")
		@JvmField
		val KEYS_PRESSED: Array<KeyRecord?> = arrayOfNulls<KeyRecord>(75)

		@OriginalMember(owner = "client!gda", name = "y", descriptor = "J")
		@JvmField
		var lastKeyTime: Long = -1L
		@OriginalMember(owner = "client!u", name = "c", descriptor = "I")
		@JvmField
		var keyCachePointerPoss: Int = 0
	}
}
