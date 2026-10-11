package com.jagex.game.runetek5;

import com.jagex.game.runetek5.DepthBuffer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ru")
public final class JavaDepthBuffer implements DepthBuffer {

	@OriginalMember(owner = "client!ru", name = "e", descriptor = "I")
	public final int width;

	@OriginalMember(owner = "client!ru", name = "a", descriptor = "I")
	public final int height;

	@OriginalMember(owner = "client!ru", name = "g", descriptor = "[F")
	public final float[] depths;

	@OriginalMember(owner = "client!ru", name = "<init>", descriptor = "(II)V")
	public JavaDepthBuffer(@OriginalArg(0) int height, @OriginalArg(1) int width) {
		this.width = width;
		this.height = height;
		this.depths = new float[height * width];
	}
}
