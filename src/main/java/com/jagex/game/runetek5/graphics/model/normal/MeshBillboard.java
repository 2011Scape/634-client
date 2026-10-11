package com.jagex.game.runetek5.graphics.model.normal;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!pd")
public final class MeshBillboard {

	@OriginalMember(owner = "client!pd", name = "e", descriptor = "I")
	public final int depth;

	@OriginalMember(owner = "client!pd", name = "d", descriptor = "I")
	public final int appliesOnFace;

	@OriginalMember(owner = "client!pd", name = "g", descriptor = "I")
	public final int distance;

	@OriginalMember(owner = "client!pd", name = "b", descriptor = "I")
	public final int billboardId;

	@OriginalMember(owner = "client!pd", name = "<init>", descriptor = "(IIII)V")
	public MeshBillboard(@OriginalArg(0) int billboardId, @OriginalArg(1) int appliesOnFace, @OriginalArg(2) int depth, @OriginalArg(3) int distance) {
		this.depth = depth;
		this.appliesOnFace = appliesOnFace;
		this.distance = distance;
		this.billboardId = billboardId;
	}

	@OriginalMember(owner = "client!pd", name = "a", descriptor = "(II)Lclient!pd;")
	public MeshBillboard copyWithFace(@OriginalArg(1) int appliesOnFace) {
		return new MeshBillboard(this.billboardId, appliesOnFace, this.depth, this.distance);
	}
}
