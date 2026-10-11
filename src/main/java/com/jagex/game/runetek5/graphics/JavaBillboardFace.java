package com.jagex.game.runetek5.graphics;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!go")
public final class JavaBillboardFace {

	@OriginalMember(owner = "client!go", name = "c", descriptor = "Z")
	public final boolean hideFace;

	@OriginalMember(owner = "client!go", name = "n", descriptor = "B")
	public final byte blendMode;

	@OriginalMember(owner = "client!go", name = "l", descriptor = "I")
	public final int distance;

	@OriginalMember(owner = "client!go", name = "a", descriptor = "S")
	public final short height;

	@OriginalMember(owner = "client!go", name = "f", descriptor = "B")
	public final byte colourOp;

	@OriginalMember(owner = "client!go", name = "d", descriptor = "S")
	public final short texture;

	@OriginalMember(owner = "client!go", name = "h", descriptor = "S")
	public final short width;

	@OriginalMember(owner = "client!go", name = "b", descriptor = "I")
	public final int face;

	@OriginalMember(owner = "client!go", name = "<init>", descriptor = "(IIIIIIIIIZI)V")
	public JavaBillboardFace(@OriginalArg(0) int face, @OriginalArg(1) int vertexA, @OriginalArg(2) int vertexB, @OriginalArg(3) int vertexC, @OriginalArg(4) int width, @OriginalArg(5) int height, @OriginalArg(6) int texture, @OriginalArg(7) int colourOp, @OriginalArg(8) int blendMode, @OriginalArg(9) boolean hideFace, @OriginalArg(10) int distance) {
		this.hideFace = hideFace;
		this.blendMode = (byte) blendMode;
		this.distance = distance;
		this.height = (short) height;
		this.colourOp = (byte) colourOp;
		this.texture = (short) texture;
		this.width = (short) width;
		this.face = face;
	}
}
