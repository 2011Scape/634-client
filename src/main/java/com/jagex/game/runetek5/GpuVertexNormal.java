package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!fr")
public final class GpuVertexNormal {

	@OriginalMember(owner = "client!fr", name = "a", descriptor = "I")
	public int x;

	@OriginalMember(owner = "client!fr", name = "c", descriptor = "I")
	public int magnitude;

	@OriginalMember(owner = "client!fr", name = "e", descriptor = "I")
	public int z;

	@OriginalMember(owner = "client!fr", name = "f", descriptor = "I")
	public int y;
}
