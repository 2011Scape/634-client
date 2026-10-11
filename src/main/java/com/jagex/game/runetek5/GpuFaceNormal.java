package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!k")
public final class GpuFaceNormal {

	@OriginalMember(owner = "client!k", name = "b", descriptor = "I")
	public int x;

	@OriginalMember(owner = "client!k", name = "c", descriptor = "I")
	public int y;

	@OriginalMember(owner = "client!k", name = "f", descriptor = "I")
	public int z;
}
