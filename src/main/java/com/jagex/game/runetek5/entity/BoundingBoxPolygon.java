package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.entity.EntityNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!iw")
public final class BoundingBoxPolygon extends EntityNode {

	@OriginalMember(owner = "client!iw", name = "j", descriptor = "I")
	public int bottom;

	@OriginalMember(owner = "client!iw", name = "k", descriptor = "I")
	public int size;

	@OriginalMember(owner = "client!iw", name = "l", descriptor = "I")
	public int top;

	@OriginalMember(owner = "client!iw", name = "m", descriptor = "I")
	public int left;

	@OriginalMember(owner = "client!iw", name = "n", descriptor = "I")
	public int right;

	@OriginalMember(owner = "client!iw", name = "i", descriptor = "Z")
	public boolean positionCalculated = false;

	@OriginalMember(owner = "client!iw", name = "a", descriptor = "(II)Z")
	public boolean hovered(@OriginalArg(0) int mouseX, @OriginalArg(1) int mouseY) {
		if (!this.positionCalculated) {
			return false;
		}
		@Pc(10) int deltaX = this.right - this.left;
		@Pc(16) int deltaY = this.top - this.bottom;
		@Pc(24) int lengthSquared = deltaX * deltaX + deltaY * deltaY;
		@Pc(42) int projection = mouseX * deltaX + mouseY * deltaY - this.left * deltaX - this.bottom * deltaY;
		@Pc(49) int distX;
		@Pc(54) int distY;
		if (projection <= 0) {
			distX = this.left - mouseX;
			distY = this.bottom - mouseY;
			return distX * distX + distY * distY < this.size * this.size;
		} else if (projection > lengthSquared) {
			distX = this.right - mouseX;
			distY = this.top - mouseY;
			return distX * distX + distY * distY < this.size * this.size;
		} else {
			projection = (projection << 10) / lengthSquared;
			distX = this.left + (deltaX * projection >> 10) - mouseX;
			distY = this.bottom + (deltaY * projection >> 10) - mouseY;
			return distX * distX + distY * distY < this.size * this.size;
		}
	}
}
