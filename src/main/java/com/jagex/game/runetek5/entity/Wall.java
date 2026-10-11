package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.cache.node.PointLight;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.Protocol;
import com.jagex.game.runetek5.SceneVisibleTiles;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.SceneDrawer;
import com.jagex.game.runetek5.scene.culling.Culling;

@OriginalClass("client!qfa")
public abstract class Wall extends Entity {

	@OriginalMember(owner = "client!qfa", name = "B", descriptor = "S")
	public final short sideMask;

	@OriginalMember(owner = "client!qfa", name = "<init>", descriptor = "(IIIIII)V")
	public Wall(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int level, @OriginalArg(4) int virtualLevel, @OriginalArg(5) int sideMask) {
		super.x = x;
		super.y = y;
		super.virtualLevel = (byte) virtualLevel;
		super.z = z;
		super.level = (byte) level;
		this.sideMask = (short) sideMask;
	}

	@OriginalMember(owner = "client!qfa", name = "a", descriptor = "([Lclient!ab;I)I")
	@Override
	public final int findLights(@OriginalArg(0) PointLight[] lights) {
		@Pc(10) int localX = super.x >> Scene.tileUnits;
		@Pc(15) int localZ = super.z >> Scene.tileUnits;
		@Pc(25) int directionIndex = 0;
		if (localX == SceneDrawer.cameraGridPositionX) {
			directionIndex++;
		} else if (localX > SceneDrawer.cameraGridPositionX) {
			directionIndex += 2;
		}
		if (localZ == Scene.cameraTileZ) {
			directionIndex += 3;
		} else if (localZ < Scene.cameraTileZ) {
			directionIndex += 6;
		}
		@Pc(57) int facingSides = Protocol.removedIds[directionIndex];
		if ((this.sideMask & facingSides) != 0) {
			return this.findTileLights(lights, localX, localZ);
		} else if (this.sideMask == 1 && localX > 0) {
			return this.findTileLights(lights, localX - 1, localZ);
		} else if (this.sideMask == 4 && localX <= Scene.sceneWidth) {
			return this.findTileLights(lights, localX + 1, localZ);
		} else if (this.sideMask == 8 && localZ > 0) {
			return this.findTileLights(lights, localX, localZ - 1);
		} else if (this.sideMask == 2 && Camera.sceneLength >= localZ) {
			return this.findTileLights(lights, localX, localZ + 1);
		} else if (this.sideMask == 16 && localX > 0 && localZ <= Camera.sceneLength) {
			return this.findTileLights(lights, localX - 1, localZ + 1);
		} else if (this.sideMask == 32 && Scene.sceneWidth >= localX && Camera.sceneLength >= localZ) {
			return this.findTileLights(lights, localX + 1, localZ + 1);
		} else if (this.sideMask == 128 && localX > 0 && localZ > 0) {
			return this.findTileLights(lights, localX - 1, localZ - 1);
		} else if (this.sideMask == 64 && Scene.sceneWidth >= localX && localZ > 0) {
			return this.findTileLights(lights, localX + 1, localZ + -1);
		} else {
			throw new RuntimeException("");
		}
	}

	@OriginalMember(owner = "client!qfa", name = "c", descriptor = "(B)Z")
	@Override
	public final boolean isOccluded() {
		return Culling.hideWall(super.z >> Scene.tileUnits, super.x >> Scene.tileUnits, super.virtualLevel, this);
	}

	@OriginalMember(owner = "client!qfa", name = "h", descriptor = "(I)Z")
	@Override
	public final boolean canRender() {
		return Scene.tileInFrustum[(super.x >> Scene.tileUnits) + SceneVisibleTiles.visibleTiles - SceneDrawer.cameraGridPositionX][SceneVisibleTiles.visibleTiles + (super.z >> Scene.tileUnits) - Scene.cameraTileZ];
	}
}
