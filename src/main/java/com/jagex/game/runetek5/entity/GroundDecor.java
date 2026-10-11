package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.cache.node.PointLight;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.SceneVisibleTiles;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.SceneDrawer;
import com.jagex.game.runetek5.scene.culling.Culling;

@OriginalClass("client!aba")
public abstract class GroundDecor extends Entity {

	@OriginalMember(owner = "client!aba", name = "F", descriptor = "S")
	public final short offsetY;

	@OriginalMember(owner = "client!aba", name = "<init>", descriptor = "(IIIIII)V")
	public GroundDecor(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int level, @OriginalArg(4) int virtualLevel, @OriginalArg(5) int groundDecorationHeight) {
		super.virtualLevel = (byte) virtualLevel;
		super.x = x;
		super.z = z;
		this.offsetY = (short) groundDecorationHeight;
		super.y = y;
		super.level = (byte) level;
	}

	@OriginalMember(owner = "client!aba", name = "c", descriptor = "(B)Z")
	@Override
	public final boolean isOccluded() {
		return Culling.isTileHidden(super.virtualLevel, super.x >> Scene.tileUnits, super.z >> Scene.tileUnits);
	}

	@OriginalMember(owner = "client!aba", name = "a", descriptor = "([Lclient!ab;I)I")
	@Override
	public final int findLights(@OriginalArg(0) PointLight[] pointLights) {
		return this.findTileLights(pointLights, super.x >> Scene.tileUnits, super.z >> Scene.tileUnits);
	}

	@OriginalMember(owner = "client!aba", name = "h", descriptor = "(I)Z")
	@Override
	public final boolean canRender() {
		return Scene.tileInFrustum[SceneVisibleTiles.visibleTiles + (super.x >> Scene.tileUnits) - SceneDrawer.cameraGridPositionX][(super.z >> Scene.tileUnits) + SceneVisibleTiles.visibleTiles - Scene.cameraTileZ];
	}
}
