package com.jagex.game.runetek5.graphics;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ei")
public final class JavaBillboardAttributes {

	@OriginalMember(owner = "client!ei", name = "a", descriptor = "I")
	public int projectedHeight;

	@OriginalMember(owner = "client!ei", name = "b", descriptor = "I")
	public int rotation;

	@OriginalMember(owner = "client!ei", name = "c", descriptor = "I")
	public int projectedWidth;

	@OriginalMember(owner = "client!ei", name = "d", descriptor = "I")
	public int y;

	@OriginalMember(owner = "client!ei", name = "f", descriptor = "I")
	public int x;

	@OriginalMember(owner = "client!ei", name = "j", descriptor = "I")
	public int offsetX;

	@OriginalMember(owner = "client!ei", name = "k", descriptor = "I")
	public int offsetY;

	@OriginalMember(owner = "client!ei", name = "n", descriptor = "I")
	public int z;

	@OriginalMember(owner = "client!ei", name = "e", descriptor = "I")
	public int scaleY = 128;

	@OriginalMember(owner = "client!ei", name = "h", descriptor = "I")
	public int scaleX = 128;

	@OriginalMember(owner = "client!ei", name = "g", descriptor = "I")
	public int diffuseColor;

	@OriginalMember(owner = "client!ei", name = "<init>", descriptor = "(I)V")
	public JavaBillboardAttributes(@OriginalArg(0) int diffuseColor) {
		this.diffuseColor = diffuseColor;
	}

	@OriginalMember(owner = "client!ei", name = "<init>", descriptor = "(IIIIII)V")
	private JavaBillboardAttributes(@OriginalArg(0) int diffuseColor, @OriginalArg(1) int scaleX, @OriginalArg(2) int scaleY, @OriginalArg(3) int offsetX, @OriginalArg(4) int offsetY, @OriginalArg(5) int rotation) {
		this.offsetY = offsetY;
		this.diffuseColor = diffuseColor;
		this.offsetX = offsetX;
		this.scaleY = scaleY;
		this.rotation = rotation;
		this.scaleX = scaleX;
	}

	@OriginalMember(owner = "client!ei", name = "a", descriptor = "(Z)Lclient!ei;")
	public JavaBillboardAttributes copy() {
		return new JavaBillboardAttributes(this.diffuseColor, this.scaleX, this.scaleY, this.offsetX, this.offsetY, this.rotation);
	}

	@OriginalMember(owner = "client!ei", name = "a", descriptor = "(Lclient!ei;Z)V")
	public void copyFrom(@OriginalArg(0) JavaBillboardAttributes javaBillboardAttributes) {
		this.rotation = javaBillboardAttributes.rotation;
		this.scaleX = javaBillboardAttributes.scaleX;
		this.offsetX = javaBillboardAttributes.offsetX;
		this.offsetY = javaBillboardAttributes.offsetY;
		this.diffuseColor = javaBillboardAttributes.diffuseColor;
		this.scaleY = javaBillboardAttributes.scaleY;
	}
}
