package com.jagex.game.runetek5

import com.jagex.game.runetek5.network.packet.ServerProt
import org.openrs2.deob.annotation.OriginalMember

object MusicTrackProt {

	@OriginalMember(owner = "client!st", name = "I", descriptor = "Lclient!mu;")
	@JvmField
	val MUSIC_TRACK: ServerProt = ServerProt(85, 4)
}
