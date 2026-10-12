package com.jagex.game.runetek5

import com.jagex.game.runetek5.network.packet.ServerProt
import org.openrs2.deob.annotation.OriginalMember

object UpdateZoneFullFollowsProt {

	@OriginalMember(owner = "client!sea", name = "D", descriptor = "Lclient!mu;")
	@JvmField
	val UPDATE_ZONE_FULL_FOLLOWS: ServerProt = ServerProt(33, 3)
}
