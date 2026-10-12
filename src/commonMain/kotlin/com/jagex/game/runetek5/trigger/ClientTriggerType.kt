package com.jagex.game.runetek5.trigger

import com.jagex.game.runetek5.ClientTrigger
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object ClientTriggerType {
	@OriginalMember(owner = "client!bc", name = "a", descriptor = "Lclient!uw;")
	@JvmField
	val MEC_MOUSE_ENTER: ClientTrigger = ClientTrigger("", 15)

	@OriginalMember(owner = "client!ku", name = "j", descriptor = "Lclient!uw;")
	@JvmField
	val MEC_MOUSE_LEAVE: ClientTrigger = ClientTrigger("", 16)

	@OriginalMember(owner = "client!sv", name = "T", descriptor = "Lclient!uw;")
	@JvmField
	val MEC_MOUSE_OVER: ClientTrigger = ClientTrigger("", 17)
}
