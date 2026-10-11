package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ofa")
public final class JavaComplexBlendedTile {

	@OriginalMember(owner = "client!ofa", name = "a", descriptor = "[I")
	public int[] vertexColours;

	@OriginalMember(owner = "client!ofa", name = "b", descriptor = "[S")
	public short[] verticesZ;

	@OriginalMember(owner = "client!ofa", name = "c", descriptor = "S")
	public short vertexCount;

	@OriginalMember(owner = "client!ofa", name = "d", descriptor = "B")
	public byte flags;

	@OriginalMember(owner = "client!ofa", name = "e", descriptor = "[S")
	public short[] faceSizes;

	@OriginalMember(owner = "client!ofa", name = "f", descriptor = "[S")
	public short[] waterDepths;

	@OriginalMember(owner = "client!ofa", name = "g", descriptor = "[S")
	public short[] verticesY;

	@OriginalMember(owner = "client!ofa", name = "h", descriptor = "[S")
	public short[] faceTextures;

	@OriginalMember(owner = "client!ofa", name = "i", descriptor = "[S")
	public short[] verticesX;

	@OriginalMember(owner = "client!ofa", name = "j", descriptor = "S")
	public short faceCount;

	@OriginalMember(owner = "client!ofa", name = "k", descriptor = "[I")
	public int[] faceBlendedColours;
}
