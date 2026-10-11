package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!rb")
public final class JavaGenericBlendedTile {

	@OriginalMember(owner = "client!rb", name = "a", descriptor = "[S")
	public short[] verticesZ;

	@OriginalMember(owner = "client!rb", name = "b", descriptor = "[S")
	public short[] vertexSizes;

	@OriginalMember(owner = "client!rb", name = "c", descriptor = "[I")
	public int[] faceBlendedColours;

	@OriginalMember(owner = "client!rb", name = "d", descriptor = "[S")
	public short[] vertexTextures;

	@OriginalMember(owner = "client!rb", name = "e", descriptor = "[B")
	public byte[] vertexLight;

	@OriginalMember(owner = "client!rb", name = "f", descriptor = "[S")
	public short[] verticesY;

	@OriginalMember(owner = "client!rb", name = "g", descriptor = "S")
	public short faceCount;

	@OriginalMember(owner = "client!rb", name = "h", descriptor = "S")
	public short vertexCount;

	@OriginalMember(owner = "client!rb", name = "i", descriptor = "[S")
	public short[] waterDepths;

	@OriginalMember(owner = "client!rb", name = "j", descriptor = "[S")
	public short[] verticesX;

	@OriginalMember(owner = "client!rb", name = "k", descriptor = "[I")
	public int[] vertexColours;
}
