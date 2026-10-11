package com.jagex.game.runetek5.scene.culling;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.scene.Scene;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!ea")
public final class CullingCluster {

	@OriginalMember(owner = "client!ea", name = "d", descriptor = "[I")
	public final int[] tileCullingsZ = new int[4];

	@OriginalMember(owner = "client!ea", name = "k", descriptor = "[I")
	public final int[] drawHeights = new int[4];

	@OriginalMember(owner = "client!ea", name = "l", descriptor = "B")
	public final byte type;

	@OriginalMember(owner = "client!ea", name = "a", descriptor = "B")
	public final byte plane;

	@OriginalMember(owner = "client!ea", name = "g", descriptor = "[I")
	public final int[] tileCullingsX;

	@OriginalMember(owner = "client!ea", name = "e", descriptor = "S")
	public final short startX;

	@OriginalMember(owner = "client!ea", name = "b", descriptor = "S")
	public final short endX;

	@OriginalMember(owner = "client!ea", name = "m", descriptor = "S")
	public final short startY;

	@OriginalMember(owner = "client!ea", name = "j", descriptor = "S")
	public final short endY;

	@OriginalMember(owner = "client!ea", name = "h", descriptor = "[S")
	public final short[] projectionX;

	@OriginalMember(owner = "client!ea", name = "c", descriptor = "[S")
	public final short[] projectionY;

	@OriginalMember(owner = "client!ea", name = "n", descriptor = "[S")
	public final short[] projectionZ;

	@OriginalMember(owner = "client!ea", name = "<init>", descriptor = "(IIIIIIIIIIIIII)V")
	public CullingCluster(@OriginalArg(0) int type, @OriginalArg(1) int plane, @OriginalArg(2) int tileEndX, @OriginalArg(3) int tileStartX, @OriginalArg(4) int tileStartX2, @OriginalArg(5) int tileEndX2, @OriginalArg(6) int heightSouthWest, @OriginalArg(7) int heightNorthEast, @OriginalArg(8) int heightNorthWest, @OriginalArg(9) int heightSouthEast, @OriginalArg(10) int tileEndY, @OriginalArg(11) int tileStartY, @OriginalArg(12) int tileStartY2, @OriginalArg(13) int tileEndY2) {
		this.type = (byte) type;
		this.plane = (byte) plane;
		this.tileCullingsX = new int[4];
		this.tileCullingsX[0] = tileEndX;
		this.tileCullingsX[1] = tileStartX;
		this.tileCullingsX[3] = tileEndX2;
		this.tileCullingsX[2] = tileStartX2;
		this.drawHeights[0] = heightSouthWest;
		this.drawHeights[2] = heightNorthWest;
		this.drawHeights[3] = heightSouthEast;
		this.drawHeights[1] = heightNorthEast;
		this.tileCullingsZ[1] = tileStartY;
		this.startX = (short) (tileEndX >> Scene.tileUnits);
		this.tileCullingsZ[3] = tileEndY2;
		this.tileCullingsZ[0] = tileEndY;
		this.tileCullingsZ[2] = tileStartY2;
		this.endX = (short) (tileStartX2 >> Scene.tileUnits);
		this.startY = (short) (tileEndY >> Scene.tileUnits);
		this.endY = (short) (tileStartY2 >> Scene.tileUnits);
		this.projectionX = new short[4];
		this.projectionY = new short[4];
		this.projectionZ = new short[4];
	}
	@OriginalMember(owner = "client!eo", name = "a", descriptor = "(IILclient!ea;)Z")
	public static boolean projectCorner(@OriginalArg(0) int corner, @OriginalArg(2) CullingCluster locOccluder) {
		Culling.aClass30_7.method4652(locOccluder.tileCullingsX[corner], locOccluder.drawHeights[corner], locOccluder.tileCullingsZ[corner], Culling.occlusionProjection);
		@Pc(22) int depth = Culling.occlusionProjection[2];
		if (depth < 50) {
			return false;
		} else {
			locOccluder.projectionZ[corner] = (short) (Culling.occlusionCenterX + Culling.occlusionProjection[0] * Culling.occlusionScaleX / depth);
			locOccluder.projectionY[corner] = (short) (Culling.occlusionScaleY * Culling.occlusionProjection[1] / depth + Culling.occlusionCenterY);
			locOccluder.projectionX[corner] = (short) depth;
			return true;
		}
	}
}
