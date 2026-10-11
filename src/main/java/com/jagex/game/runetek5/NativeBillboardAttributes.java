package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!dk")
public final class NativeBillboardAttributes {

	@OriginalMember(owner = "client!dk", name = "a", descriptor = "I")
	public int angle;

	@OriginalMember(owner = "client!dk", name = "d", descriptor = "I")
	public int offsetY;

	@OriginalMember(owner = "client!dk", name = "h", descriptor = "I")
	public int offsetX;

	@OriginalMember(owner = "client!dk", name = "l", descriptor = "I")
	public int scaleY = 128;

	@OriginalMember(owner = "client!dk", name = "o", descriptor = "I")
	public int scaleX = 128;

	@OriginalMember(owner = "client!dk", name = "f", descriptor = "I")
	public int colour;

	@OriginalMember(owner = "client!dk", name = "<init>", descriptor = "(I)V")
	public NativeBillboardAttributes(@OriginalArg(0) int colour) {
		this.colour = colour;
	}

	@OriginalMember(owner = "client!dk", name = "<init>", descriptor = "(IIIIII)V")
	private NativeBillboardAttributes(@OriginalArg(0) int colour, @OriginalArg(1) int scaleX, @OriginalArg(2) int scaleY, @OriginalArg(3) int offsetX, @OriginalArg(4) int offsetY, @OriginalArg(5) int angle) {
		this.colour = colour;
		this.scaleX = scaleX;
		this.offsetY = offsetY;
		this.offsetX = offsetX;
		this.scaleY = scaleY;
		this.angle = angle;
	}

	@OriginalMember(owner = "client!dk", name = "a", descriptor = "(Lclient!dk;B)V")
	public void copyFrom(@OriginalArg(0) NativeBillboardAttributes nativeBillboardAttributes) {
		this.offsetX = nativeBillboardAttributes.offsetX;
		this.angle = nativeBillboardAttributes.angle;
		this.colour = nativeBillboardAttributes.colour;
		this.scaleX = nativeBillboardAttributes.scaleX;
		this.offsetY = nativeBillboardAttributes.offsetY;
		this.scaleY = nativeBillboardAttributes.scaleY;
	}

	@OriginalMember(owner = "client!dk", name = "a", descriptor = "(I)Lclient!dk;")
	public NativeBillboardAttributes copy() {
		return new NativeBillboardAttributes(this.colour, this.scaleX, this.scaleY, this.offsetX, this.offsetY, this.angle);
	}
}
