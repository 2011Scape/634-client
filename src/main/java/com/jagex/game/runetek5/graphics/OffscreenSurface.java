package com.jagex.game.runetek5.graphics;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!is")
public interface OffscreenSurface {

	@OriginalMember(owner = "client!is", name = "a", descriptor = "(IIIIIIZZ)V")
	public void copyToSurface(@OriginalArg(0) int srcX, @OriginalArg(1) int srcY, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int dstX, @OriginalArg(5) int dstY);

	@OriginalMember(owner = "client!is", name = "b", descriptor = "(IIIIIIZZ)V")
	public void copyFromSurface(@OriginalArg(0) int srcX, @OriginalArg(1) int srcY, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(6) boolean copyRaster);
}
