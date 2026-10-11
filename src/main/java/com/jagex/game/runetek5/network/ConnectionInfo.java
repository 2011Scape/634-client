package com.jagex.game.runetek5.network;

import com.jagex.game.runetek5.ClientPacket;
import org.openrs2.deob.annotation.OriginalMember;

public final class ConnectionInfo {
	@OriginalMember(owner = "client!fm", name = "f", descriptor = "Lclient!ej;")
	public static final ClientPacket EVENT_KEYBOARD = new ClientPacket(69, -1);

	private ConnectionInfo() {
		/* empty */
	}
}
