package com.jagex.game.runetek5.graphics.model.billboard;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!iq")
public final class BillboardSurfaceGL {

	@OriginalMember(owner = "client!iq", name = "a", descriptor = "I")
	public int offsetY;

	@OriginalMember(owner = "client!iq", name = "g", descriptor = "I")
	public int angle;

	@OriginalMember(owner = "client!iq", name = "h", descriptor = "I")
	public int offsetX;

	@OriginalMember(owner = "client!iq", name = "k", descriptor = "I")
	public int scaleY = 128;

	@OriginalMember(owner = "client!iq", name = "j", descriptor = "I")
	public int scaleX = 128;

	@OriginalMember(owner = "client!iq", name = "l", descriptor = "I")
	public int diffuseColor;

	@OriginalMember(owner = "client!iq", name = "<init>", descriptor = "(I)V")
	public BillboardSurfaceGL(@OriginalArg(0) int diffuseColor) {
		this.diffuseColor = diffuseColor;
	}

	@OriginalMember(owner = "client!iq", name = "<init>", descriptor = "(IIIIII)V")
	private BillboardSurfaceGL(@OriginalArg(0) int diffuseColor, @OriginalArg(1) int scaleX, @OriginalArg(2) int scaleY, @OriginalArg(3) int offsetX, @OriginalArg(4) int offsetY, @OriginalArg(5) int rotation) {
		this.offsetY = offsetY;
		this.offsetX = offsetX;
		this.angle = rotation;
		this.diffuseColor = diffuseColor;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	@OriginalMember(owner = "client!iq", name = "a", descriptor = "(Lclient!iq;B)V")
	public void copyFrom(@OriginalArg(0) BillboardSurfaceGL glBillboardAttributes) {
		this.scaleY = glBillboardAttributes.scaleY;
		this.diffuseColor = glBillboardAttributes.diffuseColor;
		this.scaleX = glBillboardAttributes.scaleX;
		this.offsetX = glBillboardAttributes.offsetX;
		this.offsetY = glBillboardAttributes.offsetY;
		this.angle = glBillboardAttributes.angle;
	}

	@OriginalMember(owner = "client!iq", name = "a", descriptor = "(Z)Lclient!iq;")
	public BillboardSurfaceGL copy() {
		return new BillboardSurfaceGL(this.diffuseColor, this.scaleX, this.scaleY, this.offsetX, this.offsetY, this.angle);
	}
}
