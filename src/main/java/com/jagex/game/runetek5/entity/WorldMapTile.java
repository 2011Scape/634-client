package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.entity.EntityNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!rt")
public final class WorldMapTile extends EntityNode {

	@OriginalMember(owner = "client!rt", name = "o", descriptor = "B")
	public final byte x;

	@OriginalMember(owner = "client!rt", name = "m", descriptor = "B")
	public final byte z;

	@OriginalMember(owner = "client!rt", name = "n", descriptor = "I")
	public int underlayIdOrColour;

	@OriginalMember(owner = "client!rt", name = "i", descriptor = "B")
	public final byte overlayId;

	@OriginalMember(owner = "client!rt", name = "l", descriptor = "B")
	public final byte shape;

	@OriginalMember(owner = "client!rt", name = "j", descriptor = "[S")
	public final short[] locIds;

	@OriginalMember(owner = "client!rt", name = "k", descriptor = "[B")
	public final byte[] locShapes;

	@OriginalMember(owner = "client!rt", name = "<init>", descriptor = "(IIIII[S[B)V")
	public WorldMapTile(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int underlayId, @OriginalArg(3) int overlayId, @OriginalArg(4) int shape, @OriginalArg(5) short[] locIds, @OriginalArg(6) byte[] locShapes) {
		this.x = (byte) x;
		this.z = (byte) z;
		this.underlayIdOrColour = underlayId;
		this.overlayId = (byte) overlayId;
		this.shape = (byte) shape;
		this.locIds = locIds;
		this.locShapes = locShapes;
	}
}
