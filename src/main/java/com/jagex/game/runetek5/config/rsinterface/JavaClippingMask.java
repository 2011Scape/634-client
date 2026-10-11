package com.jagex.game.runetek5.config.rsinterface;

import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!nba")
public final class JavaClippingMask extends ComponentSurface {

	@OriginalMember(owner = "client!nba", name = "e", descriptor = "[I")
	public final int[] lineWidths;

	@OriginalMember(owner = "client!nba", name = "f", descriptor = "[I")
	public final int[] lineOffsets;

	@OriginalMember(owner = "client!nba", name = "<init>", descriptor = "(II[I[I)V")
	public JavaClippingMask(@OriginalArg(0) int width, @OriginalArg(1) int height, @OriginalArg(2) int[] lineWidths, @OriginalArg(3) int[] lineOffsets) {
		this.lineWidths = lineWidths;
		this.lineOffsets = lineOffsets;
	}
}
