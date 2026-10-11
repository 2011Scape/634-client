package com.jagex.game.runetek5.graphics;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!en")
public final class NativeBillboardFace {

	@OriginalMember(owner = "client!en", name = "b", descriptor = "I")
	public final int vertexA;

	@OriginalMember(owner = "client!en", name = "l", descriptor = "I")
	public final int vertexC;

	@OriginalMember(owner = "client!en", name = "c", descriptor = "S")
	public final short width;

	@OriginalMember(owner = "client!en", name = "g", descriptor = "I")
	public final int vertexB;

	@OriginalMember(owner = "client!en", name = "j", descriptor = "I")
	public final int distance;

	@OriginalMember(owner = "client!en", name = "f", descriptor = "B")
	public final byte blendMode;

	@OriginalMember(owner = "client!en", name = "e", descriptor = "I")
	public final int face;

	@OriginalMember(owner = "client!en", name = "d", descriptor = "S")
	public final short height;

	@OriginalMember(owner = "client!en", name = "m", descriptor = "Z")
	public final boolean hideWithBloom;

	@OriginalMember(owner = "client!en", name = "k", descriptor = "S")
	public final short texture;

	@OriginalMember(owner = "client!en", name = "<init>", descriptor = "(IIIIIIIIIZZI)V")
	public NativeBillboardFace(@OriginalArg(0) int face, @OriginalArg(1) int vertexC, @OriginalArg(2) int vertexB, @OriginalArg(3) int vertexA, @OriginalArg(4) int height, @OriginalArg(5) int width, @OriginalArg(6) int texture, @OriginalArg(7) int colourOp, @OriginalArg(8) int blendMode, @OriginalArg(9) boolean hideFace, @OriginalArg(10) boolean hideWithBloom, @OriginalArg(11) int distance) {
		this.vertexA = vertexA;
		this.vertexC = vertexC;
		this.width = (short) width;
		this.vertexB = vertexB;
		this.distance = distance;
		this.blendMode = (byte) blendMode;
		this.face = face;
		this.height = (short) height;
		this.hideWithBloom = hideWithBloom;
		this.texture = (short) texture;
	}
}
