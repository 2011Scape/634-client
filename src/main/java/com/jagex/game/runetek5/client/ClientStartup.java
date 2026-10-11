package com.jagex.game.runetek5.client;

import com.jagex.game.runetek5.LoadingRequirement;
import org.openrs2.deob.annotation.OriginalMember;

public final class ClientStartup {
	@OriginalMember(owner = "client!kl", name = "y", descriptor = "I")
	public static int loadingStatusDependencies = 0;

	@OriginalMember(owner = "client!nia", name = "g", descriptor = "[Lclient!up;")
	public static LoadingRequirement[] fullyCachedDependencies;

	@OriginalMember(owner = "client!st", name = "M", descriptor = "I")
	public static int loadingPercentDependencies = 0;

	private ClientStartup() {
		/* empty */
	}
}
