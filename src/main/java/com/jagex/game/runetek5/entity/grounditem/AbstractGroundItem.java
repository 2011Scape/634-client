package com.jagex.game.runetek5.entity.grounditem;

import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.SceneVisibleTiles;
import com.jagex.game.runetek5.entity.Entity;
import com.jagex.game.runetek5.entity.InteractiveEntityContainer;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.scene.culling.Culling;
import com.jagex.game.runetek5.SceneDrawer;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!fha")
public abstract class AbstractGroundItem extends Entity {

	@OriginalMember(owner = "client!fha", name = "<init>", descriptor = "(IIIII)V")
	public AbstractGroundItem(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int level, @OriginalArg(4) int virtualLevel) {
		super.x = x;
		super.level = (byte) level;
		super.y = y;
		super.virtualLevel = (byte) virtualLevel;
		super.z = z;
	}

	@OriginalMember(owner = "client!fha", name = "c", descriptor = "(B)Z")
	@Override
	public final boolean isOccluded() {
		@Pc(20) InteractiveEntityContainer positionEntityNode = Scene.getTileInteractives(super.level, super.x >> Scene.tileUnits, super.z >> Scene.tileUnits);
		return positionEntityNode != null && positionEntityNode.interactiveObject.supportsItems ? Culling.isTileHidden(super.z >> Scene.tileUnits, positionEntityNode.interactiveObject.getModelMinY() + this.getModelMinY(), super.level, super.x >> Scene.tileUnits) : Culling.isTileHidden(super.level, super.x >> Scene.tileUnits, super.z >> Scene.tileUnits);
	}

	@OriginalMember(owner = "client!fha", name = "h", descriptor = "(I)Z")
	@Override
	public final boolean canRender() {
		return Scene.tileInFrustum[SceneVisibleTiles.visibleTiles + (super.x >> Scene.tileUnits) - SceneDrawer.cameraGridPositionX][SceneVisibleTiles.visibleTiles + (super.z >> Scene.tileUnits) - Scene.cameraTileZ];
	}

	@OriginalMember(owner = "client!fha", name = "b", descriptor = "(Z)V")
	@Override
	public final void removeTileShadow() {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!fha", name = "a", descriptor = "([Lclient!ab;I)I")
	@Override
	public final int findLights(@OriginalArg(0) PointLight[] pointLights) {
		return this.findTileLights(pointLights, super.x >> Scene.tileUnits, super.z >> Scene.tileUnits);
	}

	@OriginalMember(owner = "client!fha", name = "j", descriptor = "(I)Z")
	@Override
	public final boolean hasTileShadow() {
		return false;
	}

	@OriginalMember(owner = "client!fha", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	@Override
	public final void shareLight(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int offsetY, @OriginalArg(2) boolean hideSharedFaces, @OriginalArg(3) Entity entity, @OriginalArg(4) int offsetZ, @OriginalArg(6) int offsetX) {
		throw new IllegalStateException();
	}
}
