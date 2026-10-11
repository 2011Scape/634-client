package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!gr")
public final class VertexNormalGL {

	@OriginalMember(owner = "client!gr", name = "a", descriptor = "I")
	public int z;

	@OriginalMember(owner = "client!gr", name = "b", descriptor = "I")
	public int magnitude;

	@OriginalMember(owner = "client!gr", name = "c", descriptor = "I")
	public int x;

	@OriginalMember(owner = "client!gr", name = "d", descriptor = "I")
	public int y;
}
