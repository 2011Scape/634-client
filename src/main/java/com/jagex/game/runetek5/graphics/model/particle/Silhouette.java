package com.jagex.game.runetek5.graphics.model.particle;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.model.Model;

@OriginalClass("client!hw")
public final class Silhouette {

	@OriginalMember(owner = "client!hw", name = "e", descriptor = "I")
	private int absX;

	@OriginalMember(owner = "client!hw", name = "n", descriptor = "I")
	private int absY;

	@OriginalMember(owner = "client!hw", name = "b", descriptor = "I")
	private int squareRadius;

	@OriginalMember(owner = "client!hw", name = "f", descriptor = "I")
	private int y;

	@OriginalMember(owner = "client!hw", name = "i", descriptor = "I")
	private int z1;

	@OriginalMember(owner = "client!hw", name = "c", descriptor = "I")
	private int y2;

	@OriginalMember(owner = "client!hw", name = "m", descriptor = "I")
	private int y1;

	@OriginalMember(owner = "client!hw", name = "a", descriptor = "I")
	private int z2;

	@OriginalMember(owner = "client!hw", name = "p", descriptor = "I")
	private int x1;

	@OriginalMember(owner = "client!hw", name = "o", descriptor = "I")
	private int x2;

	@OriginalMember(owner = "client!hw", name = "<init>", descriptor = "(IIIIIIIIII)V")
	public Silhouette(@OriginalArg(0) int absX, @OriginalArg(1) int absY, @OriginalArg(2) int absZ, @OriginalArg(3) int size2d, @OriginalArg(4) int minX, @OriginalArg(5) int maxX, @OriginalArg(6) int minY, @OriginalArg(7) int maxY, @OriginalArg(8) int minZ, @OriginalArg(9) int maxZ) {
		this.absX = absZ;
		this.absY = absY;
		this.squareRadius = size2d * size2d;
		this.y = absX;
		this.z1 = this.absX + minZ;
		this.y2 = maxY + this.absY;
		this.y1 = minY + this.absY;
		this.z2 = maxZ + this.absX;
		this.x1 = maxX + this.y;
		this.x2 = this.y + minX;
	}

	@OriginalMember(owner = "client!hw", name = "a", descriptor = "(IIBIIIIIIII)V")
	public void set(@OriginalArg(0) int maxY, @OriginalArg(1) int minY, @OriginalArg(3) int centerX, @OriginalArg(4) int size2d, @OriginalArg(5) int absX, @OriginalArg(6) int minX, @OriginalArg(7) int maxZ, @OriginalArg(8) int minZ, @OriginalArg(9) int absY, @OriginalArg(10) int maxX) {
		this.y = centerX;
		this.absY = absY;
		this.squareRadius = size2d * size2d;
		this.absX = absX;
		this.z2 = maxZ + this.absX;
		this.z1 = this.absX + minZ;
		this.x1 = this.y + maxX;
		this.x2 = minX + this.y;
		this.y1 = minY + this.absY;
		this.y2 = this.absY + maxY;
	}

	@OriginalMember(owner = "client!hw", name = "a", descriptor = "(IIII)Z")
	public boolean contains(@OriginalArg(1) int posZ, @OriginalArg(2) int posY, @OriginalArg(3) int posX) {
		if (this.x2 > posX || this.x1 < posX) {
			return false;
		} else if (posY < this.y1 || posY > this.y2) {
			return false;
		} else if (posZ >= this.z1 && posZ <= this.z2) {
			@Pc(58) int deltaX = posX - this.y;
			@Pc(64) int deltaY = posZ - this.absX;
			return this.squareRadius > deltaX * deltaX + deltaY * deltaY;
		} else {
			return false;
		}
	}
	@OriginalMember(owner = "client!jm", name = "a", descriptor = "(ILclient!ka;III)Lclient!hw;")
	public static Silhouette fromModel(@OriginalArg(0) int absX, @OriginalArg(1) Model model, @OriginalArg(2) int absZ, @OriginalArg(3) int absY) {
		return model == null ? null : new Silhouette(absX, absY, absZ, model.na(), model.V(), model.RA(), model.fa(), model.EA(), model.HA(), model.G());
	}
	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(IILclient!hw;Lclient!ka;ZI)V")
	public static void updateFromModel(@OriginalArg(0) int absX, @OriginalArg(1) int absY, @OriginalArg(2) Silhouette boundingCylinder, @OriginalArg(3) Model model, @OriginalArg(5) int centerX) {
		if (model != null) {
			boundingCylinder.set(model.EA(), model.fa(), centerX, model.na(), absX, model.V(), model.G(), model.HA(), absY, model.RA());
		}
	}
}
