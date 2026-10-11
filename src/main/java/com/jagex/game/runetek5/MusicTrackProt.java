package com.jagex.game.runetek5;

import com.jagex.game.runetek5.network.packet.ServerProt;
import org.openrs2.deob.annotation.OriginalMember;

public final class MusicTrackProt {

	@OriginalMember(owner = "client!st", name = "I", descriptor = "Lclient!mu;")
	public static final ServerProt MUSIC_TRACK = new ServerProt(85, 4);
}
