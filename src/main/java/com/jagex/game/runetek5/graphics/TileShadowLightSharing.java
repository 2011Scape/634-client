package com.jagex.game.runetek5.graphics;

import com.jagex.game.runetek5.entity.Entity;
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity;
import com.jagex.game.runetek5.entity.InteractiveEntityContainer;
import com.jagex.game.runetek5.scene.tile.Tile;
import com.jagex.game.runetek5.entity.Wall;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.scene.culling.Culling;

public final class TileShadowLightSharing {

	@OriginalMember(owner = "client!jc", name = "a", descriptor = "(Lclient!ga;IIIII)V")
	public static void castShadow(@OriginalArg(0) Entity entity, @OriginalArg(1) int basePlane, @OriginalArg(2) int startX, @OriginalArg(3) int startZ, @OriginalArg(4) int width, @OriginalArg(5) int length) {
		@Pc(1) boolean isSamePlane = true;
		@Pc(3) int lowestX = startX;
		@Pc(7) int highestX = startX + width;
		@Pc(11) int lowestZ = startZ - 1;
		@Pc(15) int highestZ = startZ + length;
		for (@Pc(17) int i = basePlane; i <= basePlane + 1; i++) {
			if (i != Scene.length) {
				for (@Pc(23) int j = lowestX; j <= highestX; j++) {
					if (j >= 0 && j < Scene.sceneWidth) {
						for (@Pc(32) int k = lowestZ; k <= highestZ; k++) {
							if (k >= 0 && k < Camera.sceneLength && (!isSamePlane || j >= highestX || k >= highestZ || k < startZ && j != startX)) {
								@Pc(62) Tile tile = Scene.aClass100ArrayArrayArray3[i][j][k];
								if (tile != null) {
									@Pc(148) int heightDelta = (Scene.aClass65Array1[i].getHeight(k, j) + Scene.aClass65Array1[i].getHeight(k, j + 1) + Scene.aClass65Array1[i].getHeight(k + 1, j) + Scene.aClass65Array1[i].getHeight(k + 1, j + 1)) / 4 - (Scene.aClass65Array1[basePlane].getHeight(startZ, startX) + Scene.aClass65Array1[basePlane].getHeight(startZ, startX + 1) + Scene.aClass65Array1[basePlane].getHeight(startZ + 1, startX) + Scene.aClass65Array1[basePlane].getHeight(startZ + 1, startX + 1)) / 4;
									@Pc(151) Wall wall = tile.primaryWall;
									@Pc(154) Wall wall2 = tile.secondaryWall;
									if (wall != null && wall.hasTileShadow()) {
										entity.shareLight(Scene.toolkit, heightDelta, isSamePlane, wall, (k - startZ) * Culling.fullTileDimension + (1 - length) * Scene.halfTileOffset, (j - startX) * Culling.fullTileDimension + (1 - width) * Scene.halfTileOffset);
									}
									if (wall2 != null && wall2.hasTileShadow()) {
										entity.shareLight(Scene.toolkit, heightDelta, isSamePlane, wall2, (k - startZ) * Culling.fullTileDimension + (1 - length) * Scene.halfTileOffset, (j - startX) * Culling.fullTileDimension + (1 - width) * Scene.halfTileOffset);
									}
									for (@Pc(227) InteractiveEntityContainer positionEntityNode = tile.interactives; positionEntityNode != null; positionEntityNode = positionEntityNode.child) {
										@Pc(231) AbstractInteractiveEntity positionEntity = positionEntityNode.interactiveObject;
										if (positionEntity != null && positionEntity.hasTileShadow() && (j == positionEntity.startX || j == lowestX) && (k == positionEntity.startZ || k == lowestZ)) {
											@Pc(260) int sizeX = positionEntity.endX + 1 - positionEntity.startX;
											@Pc(268) int sizeZ = positionEntity.endZ + 1 - positionEntity.startZ;
											entity.shareLight(Scene.toolkit, heightDelta, isSamePlane, positionEntity, (positionEntity.startZ - startZ) * Culling.fullTileDimension + (sizeZ - length) * Scene.halfTileOffset, (positionEntity.startX - startX) * Culling.fullTileDimension + (sizeX - width) * Scene.halfTileOffset);
										}
									}
								}
							}
						}
					}
				}
				lowestX--;
				isSamePlane = false;
			}
		}
	}
}
