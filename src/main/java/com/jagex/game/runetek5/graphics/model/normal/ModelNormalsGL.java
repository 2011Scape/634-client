package com.jagex.game.runetek5.graphics.model.normal;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!oba")
public final class ModelNormalsGL {

	@OriginalMember(owner = "client!oba", name = "a", descriptor = "[B")
	public byte[] magnitude;

	@OriginalMember(owner = "client!oba", name = "d", descriptor = "[S")
	public short[] normalsY;

	@OriginalMember(owner = "client!oba", name = "f", descriptor = "[S")
	public short[] normalsZ;

	@OriginalMember(owner = "client!oba", name = "g", descriptor = "[S")
	public short[] normalsX;
}
