package com.jagex.game.runetek5.graphics;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ok")
public final class GpuModelNormals {

	@OriginalMember(owner = "client!ok", name = "a", descriptor = "[S")
	public short[] normalsZ;

	@OriginalMember(owner = "client!ok", name = "c", descriptor = "[S")
	public short[] normalsY;

	@OriginalMember(owner = "client!ok", name = "j", descriptor = "[S")
	public short[] normalsX;

	@OriginalMember(owner = "client!ok", name = "l", descriptor = "[B")
	public byte[] magnitudes;
}
