package com.jagex.game.runetek5.config.material;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!d")
public interface MaterialProvider {

	@OriginalMember(owner = "client!d", name = "a", descriptor = "(IFZIII)[F")
	public float[] floatArgbOutput(@OriginalArg(0) int width, @OriginalArg(1) float brightness, @OriginalArg(4) int height, @OriginalArg(5) int materialId);

	@OriginalMember(owner = "client!d", name = "a", descriptor = "(Z)I")
	public int textureCount();

	@OriginalMember(owner = "client!d", name = "a", descriptor = "(II)Lclient!hd;")
	public MaterialType getMetrics(@OriginalArg(0) int materialId);

	@OriginalMember(owner = "client!d", name = "b", descriptor = "(II)Z")
	public boolean textureAvailable(@OriginalArg(1) int materialId);

	@OriginalMember(owner = "client!d", name = "a", descriptor = "(ZIFIII)[I")
	public int[] getPixels(@OriginalArg(0) boolean readReversed, @OriginalArg(1) int materialId, @OriginalArg(2) float brightness, @OriginalArg(3) int width, @OriginalArg(4) int height);

	@OriginalMember(owner = "client!d", name = "a", descriptor = "(IIFIZI)[I")
	public int[] getPixelsArgb(@OriginalArg(1) int height, @OriginalArg(2) float brightness, @OriginalArg(3) int materialId, @OriginalArg(5) int width);
}
