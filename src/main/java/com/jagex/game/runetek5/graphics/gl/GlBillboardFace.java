package com.jagex.game.runetek5.graphics.gl;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!nea")
public final class GlBillboardFace {

	@OriginalMember(owner = "client!nea", name = "a", descriptor = "I")
	public final int vertexB;

	@OriginalMember(owner = "client!nea", name = "b", descriptor = "I")
	public final int vertexC;

	@OriginalMember(owner = "client!nea", name = "h", descriptor = "I")
	public final int vertexA;

	@OriginalMember(owner = "client!nea", name = "d", descriptor = "I")
	public final int distance;

	@OriginalMember(owner = "client!nea", name = "i", descriptor = "B")
	public final byte blendMode;

	@OriginalMember(owner = "client!nea", name = "k", descriptor = "Z")
	public final boolean hideWithBloom;

	@OriginalMember(owner = "client!nea", name = "e", descriptor = "S")
	public final short width;

	@OriginalMember(owner = "client!nea", name = "c", descriptor = "I")
	public final int face;

	@OriginalMember(owner = "client!nea", name = "g", descriptor = "S")
	public final short texture;

	@OriginalMember(owner = "client!nea", name = "j", descriptor = "S")
	public final short height;

	@OriginalMember(owner = "client!nea", name = "<init>", descriptor = "(IIIIIIIIIZZI)V")
	public GlBillboardFace(@OriginalArg(0) int face, @OriginalArg(1) int vertexB, @OriginalArg(2) int vertexA, @OriginalArg(3) int vertexC, @OriginalArg(4) int width, @OriginalArg(5) int height, @OriginalArg(6) int texture, @OriginalArg(7) int blendType, @OriginalArg(8) int blendMode, @OriginalArg(9) boolean hasUid, @OriginalArg(10) boolean hideWithBloom, @OriginalArg(11) int distance) {
		this.vertexB = vertexB;
		this.vertexC = vertexC;
		this.vertexA = vertexA;
		this.distance = distance;
		this.blendMode = (byte) blendMode;
		this.hideWithBloom = hideWithBloom;
		this.width = (short) width;
		this.face = face;
		this.texture = (short) texture;
		this.height = (short) height;
	}
}
