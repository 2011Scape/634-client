package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.PointLight;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!bg")
public final class GlPointLight extends PointLight {

	@OriginalMember(owner = "client!bg", name = "<init>", descriptor = "(IIIIIF)V")
	public GlPointLight(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int radius, @OriginalArg(4) int colour, @OriginalArg(5) float intensity) {
		super(x, y, z, radius, colour, intensity);
	}

	@OriginalMember(owner = "client!bg", name = "a", descriptor = "(IBII)V")
	@Override
	public void setPosition(@OriginalArg(0) int x, @OriginalArg(2) int z, @OriginalArg(3) int y) {
		super.z = z;
		super.y = y;
		super.x = x;
	}

	@OriginalMember(owner = "client!bg", name = "a", descriptor = "(IF)V")
	@Override
	public void setIntensity(@OriginalArg(1) float intensity) {
		super.intensity = intensity;
	}
}
