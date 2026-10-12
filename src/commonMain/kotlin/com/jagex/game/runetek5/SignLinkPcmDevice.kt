package com.jagex.game.runetek5

import com.jagex.game.runetek5.PcmDevice
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object SignLinkPcmDevice {

	@OriginalMember(owner = "client!qn", name = "G", descriptor = "Lclient!ee;")
	@JvmField
	var pcmDevice: PcmDevice? = null
}
