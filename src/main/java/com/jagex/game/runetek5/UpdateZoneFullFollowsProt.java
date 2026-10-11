package com.jagex.game.runetek5;

import com.jagex.game.runetek5.network.packet.ServerProt;
import org.openrs2.deob.annotation.OriginalMember;

public final class UpdateZoneFullFollowsProt {

	@OriginalMember(owner = "client!sea", name = "D", descriptor = "Lclient!mu;")
	public static final ServerProt UPDATE_ZONE_FULL_FOLLOWS = new ServerProt(33, 3);
}
