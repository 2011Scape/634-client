package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!eg")
public final class FaceNormalGL {

	@OriginalMember(owner = "client!eg", name = "b", descriptor = "I")
	public int y;

	@OriginalMember(owner = "client!eg", name = "e", descriptor = "I")
	public int x;

	@OriginalMember(owner = "client!eg", name = "f", descriptor = "I")
	public int z;
}
