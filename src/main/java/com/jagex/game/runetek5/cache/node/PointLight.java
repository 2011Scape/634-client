package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ab")
public abstract class PointLight extends Node {

	@OriginalMember(owner = "client!ab", name = "r", descriptor = "I")
	public int y;

	@OriginalMember(owner = "client!ab", name = "w", descriptor = "I")
	public int z;

	@OriginalMember(owner = "client!ab", name = "x", descriptor = "I")
	private final int radius;

	@OriginalMember(owner = "client!ab", name = "t", descriptor = "F")
	public float intensity;

	@OriginalMember(owner = "client!ab", name = "o", descriptor = "I")
	private final int colour;

	@OriginalMember(owner = "client!ab", name = "F", descriptor = "I")
	public int x;

	@OriginalMember(owner = "client!ab", name = "<init>", descriptor = "(IIIIIF)V")
	public PointLight(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int radius, @OriginalArg(4) int colour, @OriginalArg(5) float intensity) {
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.intensity = intensity;
		this.colour = colour;
		this.x = x;
	}

	@OriginalMember(owner = "client!ab", name = "a", descriptor = "(IF)V")
	public abstract void setIntensity(@OriginalArg(1) float intensity);

	@OriginalMember(owner = "client!ab", name = "a", descriptor = "(B)I")
	public final int getZ() {
		return this.z;
	}

	@OriginalMember(owner = "client!ab", name = "d", descriptor = "(I)I")
	public final int getColour() {
		return this.colour;
	}

	@OriginalMember(owner = "client!ab", name = "e", descriptor = "(I)F")
	public final float getIntensity() {
		return this.intensity;
	}

	@OriginalMember(owner = "client!ab", name = "f", descriptor = "(I)I")
	public final int getY() {
		return this.y;
	}

	@OriginalMember(owner = "client!ab", name = "g", descriptor = "(I)I")
	public final int getRadius() {
		return this.radius;
	}

	@OriginalMember(owner = "client!ab", name = "h", descriptor = "(I)I")
	public final int getX() {
		return this.x;
	}

	@OriginalMember(owner = "client!ab", name = "a", descriptor = "(IBII)V")
	public abstract void setPosition(@OriginalArg(0) int x, @OriginalArg(2) int z, @OriginalArg(3) int y);
}
