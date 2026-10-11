package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.SceneVisibleTiles;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.scene.culling.Culling;
import com.jagex.game.runetek5.SceneDrawer;

@OriginalClass("client!un")
public abstract class WallDecor extends Entity {

	@OriginalMember(owner = "client!un", name = "D", descriptor = "S")
	public short offsetZ;

	@OriginalMember(owner = "client!un", name = "R", descriptor = "S")
	public short offsetX;

	@OriginalMember(owner = "client!un", name = "<init>", descriptor = "(IIIIIII)V")
	public WallDecor(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int level, @OriginalArg(4) int virtualLevel, @OriginalArg(5) int offsetX, @OriginalArg(6) int offsetZ) {
		this.offsetZ = (short) offsetZ;
		super.virtualLevel = (byte) virtualLevel;
		super.y = y;
		super.x = x;
		this.offsetX = (short) offsetX;
		super.z = z;
		super.level = (byte) level;
	}

	@OriginalMember(owner = "client!un", name = "h", descriptor = "(I)Z")
	@Override
	public final boolean canRender() {
		return Scene.tileInFrustum[(super.x >> Scene.tileUnits) + SceneVisibleTiles.visibleTiles - SceneDrawer.cameraGridPositionX][SceneVisibleTiles.visibleTiles + (super.z >> Scene.tileUnits) - Scene.cameraTileZ];
	}

	@OriginalMember(owner = "client!un", name = "c", descriptor = "(B)Z")
	@Override
	public final boolean isOccluded() {
		return Culling.isTileHidden(super.z >> Scene.tileUnits, this.getModelMinY(), super.virtualLevel, super.x >> Scene.tileUnits);
	}

	@OriginalMember(owner = "client!un", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	@Override
	public final void shareLight(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int offsetY, @OriginalArg(2) boolean hideSharedFaces, @OriginalArg(3) Entity entity, @OriginalArg(4) int offsetZ, @OriginalArg(6) int offsetX) {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!un", name = "j", descriptor = "(I)Z")
	@Override
	public final boolean hasTileShadow() {
		return false;
	}

	@OriginalMember(owner = "client!un", name = "b", descriptor = "(Z)V")
	@Override
	public final void removeTileShadow() {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!un", name = "a", descriptor = "([Lclient!ab;I)I")
	@Override
	public final int findLights(@OriginalArg(0) PointLight[] pointLights) {
		return this.findTileLights(pointLights, super.x >> Scene.tileUnits, super.z >> Scene.tileUnits);
	}
}
