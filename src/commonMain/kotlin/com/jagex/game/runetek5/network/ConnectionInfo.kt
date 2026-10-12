package com.jagex.game.runetek5.network

import com.jagex.game.runetek5.ClientPacket
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object ConnectionInfo {
	@OriginalMember(owner = "client!fm", name = "f", descriptor = "Lclient!ej;")
	@JvmField
	val EVENT_KEYBOARD: ClientPacket = ClientPacket(69, -1)
}
