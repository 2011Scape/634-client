package com.jagex.game.runetek5.client.input.console;

import org.openrs2.deob.annotation.OriginalMember;

public final class CommandExecutor {
	@OriginalMember(owner = "client!bb", name = "Bb", descriptor = "I")
	public static int teleportCommandPositionY;

	@OriginalMember(owner = "client!sfa", name = "f", descriptor = "I")
	public static int teleportCommandPositionZ;

	@OriginalMember(owner = "client!uf", name = "N", descriptor = "I")
	public static int teleportCommandPositionX;

	private CommandExecutor() {
		/* empty */
	}
}
