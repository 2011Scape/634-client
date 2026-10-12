package com.jagex.game.runetek5

import com.jagex.game.runetek5.network.packet.ServerProt
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object ThirdLastPacket {

	@OriginalMember(owner = "client!dn", name = "O", descriptor = "Lclient!mu;")
	@JvmField
	var thirdLastPacket: ServerProt? = null
}
