package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!b")
public final class DisplayModeDetails {

	@OriginalMember(owner = "client!b", name = "a", descriptor = "I")
	public int bitDepth;

	@OriginalMember(owner = "client!b", name = "b", descriptor = "I")
	public int width;

	@OriginalMember(owner = "client!b", name = "g", descriptor = "I")
	public int refreshRate;

	@OriginalMember(owner = "client!b", name = "i", descriptor = "I")
	public int height;
}
