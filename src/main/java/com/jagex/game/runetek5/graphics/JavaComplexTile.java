package com.jagex.game.runetek5.graphics;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!sp")
public final class JavaComplexTile {

	@OriginalMember(owner = "client!sp", name = "a", descriptor = "[S")
	public short[] verticesLight;

	@OriginalMember(owner = "client!sp", name = "b", descriptor = "S")
	public short vertexCount;

	@OriginalMember(owner = "client!sp", name = "c", descriptor = "[S")
	public short[] faceB;

	@OriginalMember(owner = "client!sp", name = "d", descriptor = "[S")
	public short[] faceA;

	@OriginalMember(owner = "client!sp", name = "e", descriptor = "[I")
	public int[] faceBlendedColours;

	@OriginalMember(owner = "client!sp", name = "f", descriptor = "[I")
	public int[] faceColours;

	@OriginalMember(owner = "client!sp", name = "g", descriptor = "[S")
	public short[] verticesZ;

	@OriginalMember(owner = "client!sp", name = "h", descriptor = "[S")
	public short[] faceTextures;

	@OriginalMember(owner = "client!sp", name = "i", descriptor = "[S")
	public short[] verticesX;

	@OriginalMember(owner = "client!sp", name = "j", descriptor = "[S")
	public short[] verticesY;

	@OriginalMember(owner = "client!sp", name = "k", descriptor = "[S")
	public short[] faceSizes;

	@OriginalMember(owner = "client!sp", name = "l", descriptor = "S")
	public short faceCount;

	@OriginalMember(owner = "client!sp", name = "m", descriptor = "B")
	public byte flags;

	@OriginalMember(owner = "client!sp", name = "n", descriptor = "[S")
	public short[] faceC;
}
