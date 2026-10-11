package com.jagex.game.runetek5.scene;

import com.jagex.game.runetek5.statics.UnderwaterTileGrid;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.ScriptRunner;
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity;
import com.jagex.game.runetek5.entity.GroundDecor;
import com.jagex.game.runetek5.entity.InteractiveEntityContainer;
import com.jagex.game.runetek5.entity.Wall;
import com.jagex.game.runetek5.entity.WallDecor;
import com.jagex.game.runetek5.graphics.TileShadowLightSharing;
import com.jagex.game.runetek5.scene.tile.Tile;
import org.openrs2.deob.annotation.OriginalArg;
import com.jagex.game.runetek5.AbstractNanoTimer;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.entity.Entity;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.PickList;
import com.jagex.game.runetek5.entity.EntityNode;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.RenderQueue;
import com.jagex.game.runetek5.RenderWorker;
import com.jagex.game.runetek5.core.constants.LocLayer;
import com.jagex.game.runetek5.core.constants.LocShapes;
import com.jagex.game.runetek5.entity.DynamicGroundDecor;
import com.jagex.game.runetek5.entity.DynamicLocation;
import com.jagex.game.runetek5.entity.DynamicWall;
import com.jagex.game.runetek5.entity.DynamicWallDecor;
import com.jagex.game.runetek5.entity.gameobject.GameObject;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.scene.culling.Culling;
import com.jagex.game.runetek5.SceneDrawer;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.entity.grounditem.AbstractGroundItem;
import com.jagex.game.runetek5.graphics.light.StaticPointLight;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.core.constants.TileFlag;
import com.jagex.game.runetek5.entity.PathingEntity;

public final class Scene {

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "()V")
	public static void destroy() {
		@Pc(3) int local3;
		@Pc(6) int local6;
		@Pc(9) int local9;
		if (Scene.aClass100ArrayArrayArray1 != null) {
			for (int level = 0; level < Scene.aClass100ArrayArrayArray1.length; level++) {
				for (int tileX = 0; tileX < Scene.sceneWidth; tileX++) {
					for (int tileZ = 0; tileZ < Camera.sceneLength; tileZ++) {
						if (Scene.aClass100ArrayArrayArray1[level][tileX][tileZ] != null) {
							Scene.aClass100ArrayArrayArray1[level][tileX][tileZ].clearInteractives();
						}
						Scene.aClass100ArrayArrayArray1[level][tileX][tileZ] = null;
					}
				}
			}
		}
		Scene.aClass100ArrayArrayArray1 = null;
		Scene.aClass65Array2 = null;
		if (UnderwaterTileGrid.aClass100ArrayArrayArray2 != null) {
			for (int level = 0; level < UnderwaterTileGrid.aClass100ArrayArrayArray2.length; level++) {
				for (int tileX = 0; tileX < Scene.sceneWidth; tileX++) {
					for (int tileZ = 0; tileZ < Camera.sceneLength; tileZ++) {
						if (UnderwaterTileGrid.aClass100ArrayArrayArray2[level][tileX][tileZ] != null) {
							UnderwaterTileGrid.aClass100ArrayArrayArray2[level][tileX][tileZ].clearInteractives();
						}
						UnderwaterTileGrid.aClass100ArrayArrayArray2[level][tileX][tileZ] = null;
					}
				}
			}
		}
		UnderwaterTileGrid.aClass100ArrayArrayArray2 = null;
		Scene.aClass65Array3 = null;
		Scene.aClass100ArrayArrayArray3 = null;
		Scene.aClass65Array1 = null;
		Scene.tileInFrustum = null;
		Scene.tileOnScreen = null;
		Scene.tileVisibility = null;
		Scene.levelTileVisibility = null;
		Scene.aBooleanArrayArrayArray1 = null;
		Culling.reset();
		if (Scene.aClass3_Sub1_Sub3Array1 != null) {
			for (int objectIndex = 0; objectIndex < Scene.interactiveObjectCount; objectIndex++) {
				Scene.aClass3_Sub1_Sub3Array1[objectIndex] = null;
			}
			Scene.interactiveObjectCount = 0;
		}
		Scene.stringStack = null;
		Scene.aClass3_Sub1Array129 = null;
		Scene.aClass3_Sub1Array94 = null;
		if (Scene.aClass3_Sub1Array143 != null) {
			for (int opaqueIndex = 0; opaqueIndex < Scene.aClass3_Sub1Array143.length; opaqueIndex++) {
				Scene.aClass3_Sub1Array143[opaqueIndex] = null;
			}
			Scene.onscreenOpaqueCount = 0;
		}
		if (Scene.aClass3_Sub1Array145 != null) {
			for (int transparentIndex = 0; transparentIndex < Scene.aClass3_Sub1Array145.length; transparentIndex++) {
				Scene.aClass3_Sub1Array145[transparentIndex] = null;
			}
			Scene.transparentEntityCount = 0;
		}
		if (Scene.aClass300Array1 != null) {
			for (int lightSlot = 0; lightSlot < Scene.lightSlotCount; lightSlot++) {
				Scene.aClass300Array1[lightSlot] = null;
			}
			for (int level = 0; level < Scene.length; level++) {
				for (int tileX = 0; tileX < Scene.sceneWidth; tileX++) {
					for (@Pc(195) int i = 0; i < Camera.sceneLength; i++) {
						Scene.tileLightUids[level][tileX][i] = 0L;
					}
				}
			}
			Scene.lightSlotCount = 0;
		}
		EntityNode.resetPickableEntityPools();
		PickList.aClass182_1 = PickList.aClass182_2;
		PickList.aClass182_1.clear();
		Scene.waterIntensities = null;
		Scene.waterColors = null;
		Scene.waterScales = null;
		if (RenderWorker.aClass366Array1 != null) {
			Scene.stopRenderWorkers();
			Scene.toolkit.allocateThreads(1);
			Scene.toolkit.linkThreads(0);
		}
		if (Scene.aClass59Array1 != null) {
			Scene.aClass59Array1 = null;
		}
		Scene.toolkit = null;
	}
	@OriginalMember(owner = "client!pu", name = "a", descriptor = "(IIILjava/lang/Class;)Lclient!jb;")
	public static AbstractInteractiveEntity getInteractiveOfType(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z, @OriginalArg(3) Class class2) {
		@Pc(7) Tile tile = Scene.aClass100ArrayArrayArray3[level][x][z];
		if (tile == null) {
			return null;
		}
		for (@Pc(14) InteractiveEntityContainer positionEntityNode = tile.interactives; positionEntityNode != null; positionEntityNode = positionEntityNode.child) {
			@Pc(18) AbstractInteractiveEntity positionEntity = positionEntityNode.interactiveObject;
			if (class2.isAssignableFrom(positionEntity.getClass()) && positionEntity.startX == x && positionEntity.startZ == z) {
				return positionEntity;
			}
		}
		return null;
	}

	@OriginalMember(owner = "client!ik", name = "a", descriptor = "(IIILclient!un;Lclient!un;)V")
	public static void addWallDecoration(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z, @OriginalArg(3) WallDecor wallDecor, @OriginalArg(4) WallDecor wallDecor2) {
		@Pc(4) Tile tile = Scene.getTile(level, x, z);
		if (tile == null) {
			return;
		}
		tile.primaryWallDecoration = wallDecor;
		tile.secondaryWallDecoration = wallDecor2;
		@Pc(19) int listIndex = Scene.aClass65Array1 == Scene.aClass65Array3 ? 1 : 0;
		if (!wallDecor.isStationary()) {
			wallDecor.nextEntity = Scene.aClass3_Sub1Array94[listIndex];
			Scene.aClass3_Sub1Array94[listIndex] = wallDecor;
		} else if (wallDecor.hasAlpha()) {
			wallDecor.nextEntity = Scene.aClass3_Sub1Array129[listIndex];
			Scene.aClass3_Sub1Array129[listIndex] = wallDecor;
		} else {
			wallDecor.nextEntity = Scene.stringStack[listIndex];
			Scene.stringStack[listIndex] = wallDecor;
			Scene.staticSceneDirty = true;
		}
		if (wallDecor2 == null) {
			return;
		}
		if (wallDecor2.isStationary()) {
			if (wallDecor2.hasAlpha()) {
				wallDecor2.nextEntity = Scene.aClass3_Sub1Array129[listIndex];
				Scene.aClass3_Sub1Array129[listIndex] = wallDecor2;
				return;
			}
			wallDecor2.nextEntity = Scene.stringStack[listIndex];
			Scene.stringStack[listIndex] = wallDecor2;
			Scene.staticSceneDirty = true;
			return;
		}
		wallDecor2.nextEntity = Scene.aClass3_Sub1Array94[listIndex];
		Scene.aClass3_Sub1Array94[listIndex] = wallDecor2;
	}

	@OriginalMember(owner = "client!hfa", name = "a", descriptor = "(II)V")
	public static void linkBelow(@OriginalArg(0) int x, @OriginalArg(1) int z) {
		@Pc(7) Tile tile = Scene.aClass100ArrayArrayArray3[0][x][z];
		for (@Pc(9) int i = 0; i < 3; i++) {
			@Pc(28) Tile tile2 = Scene.aClass100ArrayArrayArray3[i][x][z] = Scene.aClass100ArrayArrayArray3[i + 1][x][z];
			if (tile2 != null) {
				for (@Pc(33) InteractiveEntityContainer positionEntityNode = tile2.interactives; positionEntityNode != null; positionEntityNode = positionEntityNode.child) {
					@Pc(37) AbstractInteractiveEntity positionEntity = positionEntityNode.interactiveObject;
					if (positionEntity.startX == x && positionEntity.startZ == z) {
						positionEntity.level--;
					}
				}
				if (tile2.groundDecoration != null) {
					tile2.groundDecoration.level--;
				}
				if (tile2.primaryWall != null) {
					tile2.primaryWall.level--;
				}
				if (tile2.secondaryWall != null) {
					tile2.secondaryWall.level--;
				}
				if (tile2.primaryWallDecoration != null) {
					tile2.primaryWallDecoration.level--;
				}
				if (tile2.secondaryWallDecoration != null) {
					tile2.secondaryWallDecoration.level--;
				}
			}
		}
		if (Scene.aClass100ArrayArrayArray3[0][x][z] == null) {
			Scene.aClass100ArrayArrayArray3[0][x][z] = new Tile(0);
			Scene.aClass100ArrayArrayArray3[0][x][z].plane = 1;
		}
		Scene.aClass100ArrayArrayArray3[0][x][z].bridge = tile;
		Scene.aClass100ArrayArrayArray3[3][x][z] = null;
	}

	@OriginalMember(owner = "client!ma", name = "i", descriptor = "(III)Lclient!aba;")
	public static GroundDecor getGroundDecoration(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z) {
		@Pc(7) Tile tile = Scene.aClass100ArrayArrayArray3[level][x][z];
		return tile == null || tile.groundDecoration == null ? null : tile.groundDecoration;
	}

	@OriginalMember(owner = "client!ffa", name = "a", descriptor = "(IIILclient!qfa;Lclient!qfa;)V")
	public static void addWall(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z, @OriginalArg(3) Wall wall, @OriginalArg(4) Wall wall2) {
		@Pc(4) Tile tile = Scene.getTile(level, x, z);
		if (tile == null) {
			return;
		}
		tile.primaryWall = wall;
		tile.secondaryWall = wall2;
		@Pc(19) int listIndex = Scene.aClass65Array1 == Scene.aClass65Array3 ? 1 : 0;
		if (!wall.isStationary()) {
			wall.nextEntity = Scene.aClass3_Sub1Array94[listIndex];
			Scene.aClass3_Sub1Array94[listIndex] = wall;
		} else if (wall.hasAlpha()) {
			wall.nextEntity = Scene.aClass3_Sub1Array129[listIndex];
			Scene.aClass3_Sub1Array129[listIndex] = wall;
		} else {
			wall.nextEntity = Scene.stringStack[listIndex];
			Scene.stringStack[listIndex] = wall;
			Scene.staticSceneDirty = true;
		}
		if (wall2 == null) {
			return;
		}
		if (wall2.isStationary()) {
			if (wall2.hasAlpha()) {
				wall2.nextEntity = Scene.aClass3_Sub1Array129[listIndex];
				Scene.aClass3_Sub1Array129[listIndex] = wall2;
				return;
			}
			wall2.nextEntity = Scene.stringStack[listIndex];
			Scene.stringStack[listIndex] = wall2;
			Scene.staticSceneDirty = true;
			return;
		}
		wall2.nextEntity = Scene.aClass3_Sub1Array94[listIndex];
		Scene.aClass3_Sub1Array94[listIndex] = wall2;
	}

	@OriginalMember(owner = "client!ma", name = "j", descriptor = "(III)V")
	public static void removeGroundDecoration(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z) {
		@Pc(7) Tile tile = Scene.aClass100ArrayArrayArray3[level][x][z];
		if (tile != null) {
			Scene.removeFromEntityLists(tile.groundDecoration);
			if (tile.groundDecoration != null) {
				tile.groundDecoration = null;
			}
		}
	}

	@OriginalMember(owner = "client!eca", name = "a", descriptor = "()V")
	public static void applyShading() {
		for (@Pc(1) int i = ScriptRunner.minimumDrawLevel; i < Scene.length; i++) {
			for (@Pc(4) int j = 0; j < Scene.sceneWidth; j++) {
				for (@Pc(7) int k = 0; k < Camera.sceneLength; k++) {
					@Pc(16) Tile tile = Scene.aClass100ArrayArrayArray3[i][j][k];
					if (tile != null) {
						@Pc(21) Wall wall = tile.primaryWall;
						@Pc(24) Wall wall2 = tile.secondaryWall;
						if (wall != null && wall.hasTileShadow()) {
							TileShadowLightSharing.castShadow(wall, i, j, k, 1, 1);
							if (wall2 != null && wall2.hasTileShadow()) {
								TileShadowLightSharing.castShadow(wall2, i, j, k, 1, 1);
								wall2.shareLight(Scene.toolkit, 0, false, wall, 0, 0);
								wall2.removeTileShadow();
							}
							wall.removeTileShadow();
						}
						for (@Pc(68) InteractiveEntityContainer positionEntityNode = tile.interactives; positionEntityNode != null; positionEntityNode = positionEntityNode.child) {
							@Pc(72) AbstractInteractiveEntity positionEntity = positionEntityNode.interactiveObject;
							if (positionEntity != null && positionEntity.hasTileShadow()) {
								TileShadowLightSharing.castShadow(positionEntity, i, j, k, positionEntity.endX + 1 - positionEntity.startX, positionEntity.endZ - positionEntity.startZ + 1);
								positionEntity.removeTileShadow();
							}
						}
						@Pc(108) GroundDecor groundDecor = tile.groundDecoration;
						if (groundDecor != null && groundDecor.hasTileShadow()) {
							Scene.shareLightWithNeighbourGroundDecor(groundDecor, i, j, k);
							groundDecor.removeTileShadow();
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!rba", name = "A", descriptor = "I")
	public static int sceneWidth;

	@OriginalMember(owner = "client!fg", name = "d", descriptor = "I")
	public static int tileUnits;

	@OriginalMember(owner = "client!eca", name = "p", descriptor = "[[I")
	public static int[][] waterColors;

	@OriginalMember(owner = "client!ffa", name = "a", descriptor = "(IIILjava/lang/Class;)V")
	public static void removeInteractiveOfType(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z, @OriginalArg(3) Class class2) {
		@Pc(7) Tile tile = Scene.aClass100ArrayArrayArray3[level][x][z];
		if (tile == null) {
			return;
		}
		for (@Pc(13) InteractiveEntityContainer positionEntityNode = tile.interactives; positionEntityNode != null; positionEntityNode = positionEntityNode.child) {
			@Pc(17) AbstractInteractiveEntity positionEntity = positionEntityNode.interactiveObject;
			if (class2.isAssignableFrom(positionEntity.getClass()) && positionEntity.startX == x && positionEntity.startZ == z) {
				Scene.removeInteractive(positionEntity, false);
				return;
			}
		}
	}
	@OriginalMember(owner = "client!lba", name = "a", descriptor = "(III)Lclient!qfa;")
	public static Wall getSecondaryWall(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int y) {
		@Pc(7) Tile tile = Scene.aClass100ArrayArrayArray3[level][x][y];
		return tile == null ? null : tile.secondaryWall;
	}

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(III)V")
	public static void removeWallDecorations(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z) {
		@Pc(7) Tile tile = Scene.aClass100ArrayArrayArray3[level][x][z];
		if (tile == null) {
			return;
		}
		Scene.removeFromEntityLists(tile.primaryWallDecoration);
		Scene.removeFromEntityLists(tile.secondaryWallDecoration);
		if (tile.primaryWallDecoration != null) {
			tile.primaryWallDecoration = null;
		}
		if (tile.secondaryWallDecoration != null) {
			tile.secondaryWallDecoration = null;
		}
	}

	@OriginalMember(owner = "client!uc", name = "b", descriptor = "(III)Lclient!qfa;")
	public static Wall getWall(@OriginalArg(0) int minLevel, @OriginalArg(1) int x, @OriginalArg(2) int z) {
		@Pc(7) Tile tile = Scene.aClass100ArrayArrayArray3[minLevel][x][z];
		return tile == null ? null : tile.primaryWall;
	}
	@OriginalMember(owner = "client!naa", name = "bb", descriptor = "I")
	public static int halfTileOffset;

	@OriginalMember(owner = "client!og", name = "i", descriptor = "[[[Lclient!es;")
	public static Tile[][][] aClass100ArrayArrayArray3;

	@OriginalMember(owner = "client!kj", name = "e", descriptor = "(B)V")
	public static void setRoofOverride() {
		@Pc(10) int roofMode = GamePreferences.uid.lb.getValue();
		if (roofMode == 0) {
			MapRegion.roofStamps = null;
			Scene.initializeRoofSettings(0);
		} else if (roofMode == 1) {
			Scene.fillRoofStamps((byte) 0);
			Scene.initializeRoofSettings(512);
			if (SettingsBits.tileFunctionMasks != null) {
				Scene.hideAllRoofs();
			}
		} else {
			Scene.fillRoofStamps((byte) (SceneDrawer.timesSceneRendered - 4 & 0xFF));
			Scene.initializeRoofSettings(2);
		}
		PlayerUpdate.MY_PLAYER_PLANE = PlayerUpdate.currentLevel;
	}

	@OriginalMember(owner = "client!o", name = "m", descriptor = "Lclient!io;")
	public static AbstractNanoTimer aClass158_1;
	@OriginalMember(owner = "client!ta", name = "a", descriptor = "(IIII)V")
	public static void offsetWallDecoration(@OriginalArg(0) int minLevel, @OriginalArg(1) int x, @OriginalArg(2) int z, @OriginalArg(3) int decorDisplacement) {
		@Pc(7) Tile tile = Scene.aClass100ArrayArrayArray3[minLevel][x][z];
		if (tile == null) {
			return;
		}
		@Pc(13) WallDecor wallDecor = tile.primaryWallDecoration;
		@Pc(16) WallDecor wallDecor2 = tile.secondaryWallDecoration;
		if (wallDecor != null) {
			wallDecor.offsetX = (short) (wallDecor.offsetX * decorDisplacement / (0x10 << Scene.tileUnits - 7));
			wallDecor.offsetZ = (short) (wallDecor.offsetZ * decorDisplacement / (0x10 << Scene.tileUnits - 7));
		}
		if (wallDecor2 != null) {
			wallDecor2.offsetX = (short) (wallDecor2.offsetX * decorDisplacement / (0x10 << Scene.tileUnits - 7));
			wallDecor2.offsetZ = (short) (wallDecor2.offsetZ * decorDisplacement / (0x10 << Scene.tileUnits - 7));
		}
	}

	@OriginalMember(owner = "client!bu", name = "a", descriptor = "(ILclient!s;)V")
	public static void setLocalGround(@OriginalArg(0) int level, @OriginalArg(1) Ground ground) {
		Scene.aClass65Array1[level] = ground;
	}

	@OriginalMember(owner = "client!qe", name = "a", descriptor = "(Lclient!ga;)V")
	public static void removeFromEntityLists(@OriginalArg(0) Entity entity) {
		if (entity == null) {
			return;
		}
		for (@Pc(4) int i = 0; i < 2; i++) {
			@Pc(7) Entity entity2 = null;
			for (@Pc(11) Entity entity3 = Scene.stringStack[i]; entity3 != null; entity3 = entity3.nextEntity) {
				if (entity3 == entity) {
					if (entity2 == null) {
						Scene.stringStack[i] = entity3.nextEntity;
					} else {
						entity2.nextEntity = entity3.nextEntity;
					}
					Scene.staticSceneDirty = true;
					return;
				}
				entity2 = entity3;
			}
			entity2 = null;
			for (@Pc(43) Entity entity4 = Scene.aClass3_Sub1Array129[i]; entity4 != null; entity4 = entity4.nextEntity) {
				if (entity4 == entity) {
					if (entity2 == null) {
						Scene.aClass3_Sub1Array129[i] = entity4.nextEntity;
					} else {
						entity2.nextEntity = entity4.nextEntity;
					}
					Scene.staticSceneDirty = true;
					return;
				}
				entity2 = entity4;
			}
			entity2 = null;
			for (@Pc(75) Entity entity5 = Scene.aClass3_Sub1Array94[i]; entity5 != null; entity5 = entity5.nextEntity) {
				if (entity5 == entity) {
					if (entity2 == null) {
						Scene.aClass3_Sub1Array94[i] = entity5.nextEntity;
					} else {
						entity2.nextEntity = entity5.nextEntity;
					}
					Scene.staticSceneDirty = true;
					return;
				}
				entity2 = entity5;
			}
		}
	}

	@OriginalMember(owner = "client!wl", name = "b", descriptor = "(III)Lclient!es;")
	public static Tile getTile(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z) {
		if (Scene.aClass100ArrayArrayArray3[level][x][z] == null) {
			@Pc(28) boolean isBridge = Scene.aClass100ArrayArrayArray3[0][x][z] != null && Scene.aClass100ArrayArrayArray3[0][x][z].bridge != null;
			if (isBridge && level >= Scene.length - 1) {
				return null;
			}
			Scene.loadTiles(level, x, z);
		}
		return Scene.aClass100ArrayArrayArray3[level][x][z];
	}

	@OriginalMember(owner = "client!wl", name = "a", descriptor = "(Lclient!jb;Z)V")
	public static void removeInteractive(@OriginalArg(0) AbstractInteractiveEntity positionEntity, @OriginalArg(1) boolean clearFully) {
		for (@Pc(2) int i = positionEntity.startX; i <= positionEntity.endX; i++) {
			for (@Pc(6) int j = positionEntity.startZ; j <= positionEntity.endZ; j++) {
				@Pc(16) Tile tile = Scene.aClass100ArrayArrayArray3[positionEntity.level][i][j];
				if (tile != null) {
					@Pc(21) InteractiveEntityContainer positionEntityNode = tile.interactives;
					@Pc(23) InteractiveEntityContainer positionEntityNode2 = null;
					while (positionEntityNode != null) {
						if (positionEntityNode.interactiveObject == positionEntity) {
							if (positionEntityNode2 == null) {
								tile.interactives = positionEntityNode.child;
							} else {
								positionEntityNode2.child = positionEntityNode.child;
							}
							positionEntityNode.release();
							break;
						}
						positionEntityNode2 = positionEntityNode;
						positionEntityNode = positionEntityNode.child;
					}
				}
			}
		}
		if (!clearFully) {
			Scene.removeFromEntityLists(positionEntity);
		}
	}

	@OriginalMember(owner = "client!pha", name = "a", descriptor = "(Lclient!r;III[Z)V")
	public static void removeShadow(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int virtualLevel, @OriginalArg(2) int x, @OriginalArg(3) int z, @OriginalArg(4) boolean[] castShadowAtPlane) {
		if (Scene.aClass65Array1 == Scene.aClass65Array3) {
			return;
		}
		@Pc(10) int height = Scene.aClass65Array2[virtualLevel].averageHeight(x, z);
		for (@Pc(12) int i = 0; i <= virtualLevel; i++) {
			if (castShadowAtPlane == null || castShadowAtPlane[i]) {
				@Pc(23) Ground ground = Scene.aClass65Array2[i];
				if (ground != null) {
					ground.wa(shadow, x, height - ground.averageHeight(x, z), z, 0, false);
				}
			}
		}
	}

	@OriginalMember(owner = "client!ag", name = "d", descriptor = "[Lclient!s;")
	public static Ground[] aClass65Array1;

	@OriginalMember(owner = "client!wi", name = "d", descriptor = "(III)Lclient!un;")
	public static WallDecor getWallDecoration(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z) {
		@Pc(7) Tile tile = Scene.aClass100ArrayArrayArray3[level][x][z];
		return tile == null ? null : tile.primaryWallDecoration;
	}
	@OriginalMember(owner = "client!ufa", name = "a", descriptor = "()V")
	public static void stopRenderWorkers() {
		for (@Pc(1) int i = 0; i < RenderWorker.aClass366Array1.length; i++) {
			RenderWorker.aClass366Array1[i].stop();
		}
		RenderWorker.aClass366Array1 = null;
	}

	@OriginalMember(owner = "client!iha", name = "C", descriptor = "Z")
	public static boolean staticSceneDirty = true;

	@OriginalMember(owner = "client!dia", name = "h", descriptor = "I")
	public static int lightSlotCount;

	@OriginalMember(owner = "client!ww", name = "K", descriptor = "I")
	public static int onscreenOpaqueCount;

	@OriginalMember(owner = "client!lj", name = "x", descriptor = "[[[Z")
	public static boolean[][][] aBooleanArrayArrayArray1;

	@OriginalMember(owner = "client!mv", name = "d", descriptor = "[Lclient!ga;")
	public static Entity[] aClass3_Sub1Array94;

	@OriginalMember(owner = "client!dj", name = "e", descriptor = "[Lclient!s;")
	public static Ground[] aClass65Array3;

	@OriginalMember(owner = "client!ap", name = "d", descriptor = "[Lclient!jb;")
	public static AbstractInteractiveEntity[] aClass3_Sub1_Sub3Array1;

	@OriginalMember(owner = "client!gia", name = "q", descriptor = "[[Z")
	public static boolean[][] tileOnScreen;

	@OriginalMember(owner = "client!me", name = "a", descriptor = "[[Z")
	public static boolean[][] tileInFrustum;

	@OriginalMember(owner = "client!gs", name = "e", descriptor = "Lclient!ha;")
	public static Toolkit toolkit;

	@OriginalMember(owner = "client!lea", name = "a", descriptor = "I")
	public static int interactiveObjectCount = 0;

	@OriginalMember(owner = "client!mv", name = "a", descriptor = "[[[Z")
	public static boolean[][][] levelTileVisibility;
	@OriginalMember(owner = "client!lk", name = "C", descriptor = "I")
	public static int transparentEntityCount;

	@OriginalMember(owner = "client!qf", name = "a", descriptor = "(III)V")
	public static void loadTiles(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int y) {
		@Pc(20) boolean hasBridge = Scene.aClass100ArrayArrayArray3[0][x][y] != null && Scene.aClass100ArrayArrayArray3[0][x][y].bridge != null;
		for (@Pc(22) int i = level; i >= 0; i--) {
			if (Scene.aClass100ArrayArrayArray3[i][x][y] == null) {
				@Pc(44) Tile tile = Scene.aClass100ArrayArrayArray3[i][x][y] = new Tile(i);
				if (hasBridge) {
					tile.plane++;
				}
			}
		}
	}

	@OriginalMember(owner = "client!ol", name = "a", descriptor = "(I[[[Lclient!es;)V")
	public static void uploadGameObjects(@OriginalArg(1) Tile[][][] tiles) {
		for (@Pc(7) int i = 0; i < tiles.length; i++) {
			@Pc(13) Tile[][] tiles2 = tiles[i];
			for (@Pc(15) int j = 0; j < tiles2.length; j++) {
				for (@Pc(19) int k = 0; k < tiles2[j].length; k++) {
					@Pc(27) Tile tile = tiles2[j][k];
					if (tile != null) {
						if (tile.groundDecoration instanceof GameObject) {
							((GameObject) tile.groundDecoration).upload();
						}
						if (tile.primaryWallDecoration instanceof GameObject) {
							((GameObject) tile.primaryWallDecoration).upload();
						}
						if (tile.secondaryWallDecoration instanceof GameObject) {
							((GameObject) tile.secondaryWallDecoration).upload();
						}
						if (tile.primaryWall instanceof GameObject) {
							((GameObject) tile.primaryWall).upload();
						}
						if (tile.secondaryWall instanceof GameObject) {
							((GameObject) tile.secondaryWall).upload();
						}
						for (@Pc(77) InteractiveEntityContainer positionEntityNode = tile.interactives; positionEntityNode != null; positionEntityNode = positionEntityNode.child) {
							@Pc(82) AbstractInteractiveEntity positionEntity = positionEntityNode.interactiveObject;
							if (positionEntity instanceof GameObject) {
								((GameObject) positionEntity).upload();
							}
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!qj", name = "d", descriptor = "I")
	public static int length;

	@OriginalMember(owner = "client!lca", name = "b", descriptor = "(I)V")
	public static void fillLevelTiles(@OriginalArg(0) int level) {
		ScriptRunner.minimumDrawLevel = level;
		for (@Pc(3) int i = 0; i < Scene.sceneWidth; i++) {
			for (@Pc(6) int j = 0; j < Camera.sceneLength; j++) {
				if (Scene.aClass100ArrayArrayArray3[level][i][j] == null) {
					Scene.aClass100ArrayArrayArray3[level][i][j] = new Tile(level);
				}
			}
		}
	}

	@OriginalMember(owner = "client!un", name = "a", descriptor = "(II)I")
	public static int getWaterScale(@OriginalArg(0) int x, @OriginalArg(1) int z) {
		return Scene.waterScales == null ? 0 : Scene.waterScales[x][z] & 0xFFFF;
	}

	@OriginalMember(owner = "client!eh", name = "i", descriptor = "[Lclient!ga;")
	public static Entity[] stringStack;

	@OriginalMember(owner = "client!dga", name = "N", descriptor = "[[B")
	public static byte[][] waterIntensities;

	@OriginalMember(owner = "client!vw", name = "s", descriptor = "[Lclient!ga;")
	public static Entity[] aClass3_Sub1Array145;

	@OriginalMember(owner = "client!kb", name = "k", descriptor = "[[[Lclient!es;")
	public static Tile[][][] aClass100ArrayArrayArray1;

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIIII)V")
	public static void animateObject(@OriginalArg(0) int y, @OriginalArg(1) int animationId, @OriginalArg(2) int shape, @OriginalArg(3) int x, @OriginalArg(4) int rotation, @OriginalArg(6) int plane, @OriginalArg(7) int layer) {
		if (x < 0 || y < 0 || MapRegion.sceneSizeX - 1 <= x || MapRegion.sceneSizeY - 1 <= y) {
			return;
		}
		if (Scene.aClass100ArrayArrayArray3 == null) {
			return;
		}
		@Pc(37) GameObject location;
		if (layer == LocLayer.WALL) {
			location = (GameObject) Scene.getWall(plane, x, y);
			@Pc(43) GameObject location2 = (GameObject) Scene.getSecondaryWall(plane, x, y);
			if (location != null && shape != LocShapes.WALL_L) {
				if (location instanceof DynamicWall) {
					((DynamicWall) location).locEntity.playSequence(animationId);
				} else {
					LocAction.replaceLoc(x, layer, animationId, location.getId(), shape, y, rotation, plane);
				}
			}
			if (location2 != null) {
				if (location2 instanceof DynamicWall) {
					((DynamicWall) location2).locEntity.playSequence(animationId);
				} else {
					LocAction.replaceLoc(x, layer, animationId, location2.getId(), shape, y, rotation, plane);
				}
			}
		} else if (layer == LocLayer.WALLDECOR) {
			location = (GameObject) Scene.getWallDecoration(plane, x, y);
			if (location != null) {
				if (location instanceof DynamicWallDecor) {
					((DynamicWallDecor) location).locEntity.playSequence(animationId);
				} else {
					@Pc(215) int locId = location.getId();
					if (shape == LocShapes.WALLDECOR_STRAIGHT_NOOFFSET || shape == LocShapes.WALLDECOR_STRAIGHT_OFFSET) {
						LocAction.replaceLoc(x, layer, animationId, locId, LocShapes.WALLDECOR_STRAIGHT_NOOFFSET, y, rotation, plane);
					} else if (shape == 6) {
						LocAction.replaceLoc(x, layer, animationId, locId, LocShapes.WALLDECOR_STRAIGHT_NOOFFSET, y, rotation + 4, plane);
					} else if (shape == 7) {
						LocAction.replaceLoc(x, layer, animationId, locId, LocShapes.WALLDECOR_STRAIGHT_NOOFFSET, y, (rotation + 2 & 0x3) + 4, plane);
					} else if (shape == 8) {
						LocAction.replaceLoc(x, layer, animationId, locId, LocShapes.WALLDECOR_STRAIGHT_NOOFFSET, y, rotation + 4, plane);
						LocAction.replaceLoc(x, layer, animationId, locId, LocShapes.WALLDECOR_STRAIGHT_NOOFFSET, y, (rotation + 2 & 0x3) + 4, plane);
					}
				}
			}
		} else if (layer == LocLayer.GROUND) {
			location = (GameObject) Scene.getInteractiveOfType(plane, x, y, GameObject.class);
			if (location != null) {
				if (shape == LocShapes.CENTREPIECE_DIAGONAL) {
					shape = LocShapes.CENTREPIECE_STRAIGHT;
				}
				if (location instanceof DynamicLocation) {
					((DynamicLocation) location).locEntity.playSequence(animationId);
				} else {
					LocAction.replaceLoc(x, layer, animationId, location.getId(), shape, y, rotation, plane);
				}
			}
		} else if (layer == LocLayer.GROUNDDECOR) {
			location = (GameObject) Scene.getGroundDecoration(plane, x, y);
			if (location != null) {
				if (location instanceof DynamicGroundDecor) {
					((DynamicGroundDecor) location).locEntity.playSequence(animationId);
				} else {
					LocAction.replaceLoc(x, layer, animationId, location.getId(), shape, y, rotation, plane);
				}
			}
		}
	}

	@OriginalMember(owner = "client!vu", name = "x", descriptor = "[Lclient!ga;")
	public static Entity[] aClass3_Sub1Array143;

	@OriginalMember(owner = "client!sba", name = "t", descriptor = "I")
	public static int renderCores = 1;

	@OriginalMember(owner = "client!tca", name = "a", descriptor = "(Z)V")
	public static void setUnderwater(@OriginalArg(0) boolean underwater) {
		if (underwater) {
			Scene.aClass100ArrayArrayArray3 = UnderwaterTileGrid.aClass100ArrayArrayArray2;
			Scene.aClass65Array1 = Scene.aClass65Array3;
		} else {
			Scene.aClass100ArrayArrayArray3 = Scene.aClass100ArrayArrayArray1;
			Scene.aClass65Array1 = Scene.aClass65Array2;
		}
		Scene.length = Scene.aClass100ArrayArrayArray3.length;
	}

	@OriginalMember(owner = "client!rc", name = "a", descriptor = "(II)I")
	public static int getWaterIntensity(@OriginalArg(0) int x, @OriginalArg(1) int z) {
		return Scene.waterIntensities == null ? 0 : Scene.waterIntensities[x][z] & 0xFF;
	}

	@OriginalMember(owner = "client!pf", name = "J", descriptor = "[[[J")
	public static long[][][] tileLightUids;

	@OriginalMember(owner = "client!tm", name = "i", descriptor = "[Lclient!ga;")
	public static Entity[] aClass3_Sub1Array129;

	@OriginalMember(owner = "client!cn", name = "e", descriptor = "[Lclient!cia;")
	public static RenderQueue[] aClass59Array1;
	@OriginalMember(owner = "client!lha", name = "a", descriptor = "(Lclient!jb;Z)Z")
	public static boolean addInteractive(@OriginalArg(0) AbstractInteractiveEntity positionEntity, @OriginalArg(1) boolean temporary) {
		@Pc(6) boolean isUnderwater = Scene.aClass65Array1 == Scene.aClass65Array3;
		@Pc(8) int color = 0;
		@Pc(10) short scale = 0;
		@Pc(12) byte intensity = 0;
		positionEntity.updateBounds();
		if (positionEntity.startX < 0 || positionEntity.startZ < 0 || positionEntity.endX >= Scene.sceneWidth || positionEntity.endZ >= Camera.sceneLength) {
			return false;
		}
		@Pc(33) short offsetY = 0;
		@Pc(40) int local40;
		for (@Pc(36) int i = positionEntity.startX; i <= positionEntity.endX; i++) {
			for (int tileZ = positionEntity.startZ; tileZ <= positionEntity.endZ; tileZ++) {
				@Pc(47) Tile tile = Scene.getTile(positionEntity.level, i, tileZ);
				if (tile != null) {
					@Pc(53) InteractiveEntityContainer positionEntityNode = InteractiveEntityContainer.obtain(positionEntity);
					@Pc(56) InteractiveEntityContainer positionEntityNode2 = tile.interactives;
					if (positionEntityNode2 == null) {
						tile.interactives = positionEntityNode;
					} else {
						while (positionEntityNode2.child != null) {
							positionEntityNode2 = positionEntityNode2.child;
						}
						positionEntityNode2.child = positionEntityNode;
					}
					if (isUnderwater && (Scene.waterColors[i][tileZ] & 0xFF000000) != 0) {
						color = Scene.waterColors[i][tileZ];
						scale = Scene.waterScales[i][tileZ];
						intensity = Scene.waterIntensities[i][tileZ];
					}
					if (!temporary && tile.groundDecoration != null && tile.groundDecoration.offsetY > offsetY) {
						offsetY = tile.groundDecoration.offsetY;
					}
				}
			}
		}
		if (isUnderwater && (color & 0xFF000000) != 0) {
			for (int tileX = positionEntity.startX; tileX <= positionEntity.endX; tileX++) {
				for (@Pc(136) int j = positionEntity.startZ; j <= positionEntity.endZ; j++) {
					if ((Scene.waterColors[tileX][j] & 0xFF000000) == 0) {
						Scene.waterColors[tileX][j] = color;
						Scene.waterScales[tileX][j] = scale;
						Scene.waterIntensities[tileX][j] = intensity;
					}
				}
			}
		}
		if (temporary) {
			Scene.aClass3_Sub1_Sub3Array1[Scene.interactiveObjectCount++] = positionEntity;
		} else {
			int underwaterLayer = Scene.aClass65Array1 == Scene.aClass65Array3 ? 1 : 0;
			if (!positionEntity.isStationary()) {
				positionEntity.nextEntity = Scene.aClass3_Sub1Array94[underwaterLayer];
				Scene.aClass3_Sub1Array94[underwaterLayer] = positionEntity;
			} else if (positionEntity.hasAlpha()) {
				positionEntity.nextEntity = Scene.aClass3_Sub1Array129[underwaterLayer];
				Scene.aClass3_Sub1Array129[underwaterLayer] = positionEntity;
			} else {
				positionEntity.nextEntity = Scene.stringStack[underwaterLayer];
				Scene.stringStack[underwaterLayer] = positionEntity;
				Scene.staticSceneDirty = true;
			}
		}
		if (temporary) {
			positionEntity.y -= offsetY;
		}
		return true;
	}

	@OriginalMember(owner = "client!mw", name = "F", descriptor = "I")
	public static int cameraTileZ;

	@OriginalMember(owner = "client!pp", name = "z", descriptor = "[Lclient!ro;")
	public static StaticPointLight[] aClass300Array1;

	@OriginalMember(owner = "client!pl", name = "f", descriptor = "[I")
	public static int[] tileVisibility;

	@OriginalMember(owner = "client!pl", name = "a", descriptor = "()V")
	public static void addHdLight() {
		for (@Pc(1) int i = 0; i < Scene.lightSlotCount; i++) {
			if (!Scene.pointLightApplied[i]) {
				@Pc(10) StaticPointLight staticPointLight = Scene.aClass300Array1[i];
				@Pc(13) PointLight pointLight = staticPointLight.pointLight;
				@Pc(16) int level = staticPointLight.level;
				@Pc(22) int offset = pointLight.getRadius() - Scene.halfTileOffset;
				@Pc(30) int dimension = (offset * 2 >> Scene.tileUnits) + 1;
				@Pc(32) int rangeIndex = 0;
				@Pc(37) int[] lightDisplacement = new int[dimension * dimension];
				@Pc(45) int sceneBaseX = pointLight.getX() - offset >> Scene.tileUnits;
				@Pc(53) int sceneStartY = pointLight.getZ() - offset >> Scene.tileUnits;
				@Pc(61) int sceneEndY = pointLight.getZ() + offset >> Scene.tileUnits;
				if (sceneStartY < 0) {
					rangeIndex = -sceneStartY;
					sceneStartY = 0;
				}
				if (sceneEndY >= Camera.sceneLength) {
					sceneEndY = Camera.sceneLength - 1;
				}
				for (@Pc(78) int j = sceneStartY; j <= sceneEndY; j++) {
					@Pc(84) short range = staticPointLight.ranges[rangeIndex];
					@Pc(88) int rangeStartOffset = range >>> 8;
					@Pc(94) int displacementIndex = rangeIndex * dimension + rangeStartOffset;
					@Pc(100) int sceneStartX = sceneBaseX + (range >>> 8);
					@Pc(108) int sceneEndX = sceneStartX + (range & 0xFF) - 1;
					if (sceneStartX < 0) {
						displacementIndex -= sceneStartX;
						sceneStartX = 0;
					}
					if (sceneEndX >= Scene.sceneWidth) {
						sceneEndX = Scene.sceneWidth - 1;
					}
					for (@Pc(125) int k = sceneStartX; k <= sceneEndX; k++) {
						@Pc(128) byte displacement = 1;
						@Pc(141) AbstractInteractiveEntity positionEntity = Scene.getInteractiveOfType(level, k, j, AbstractInteractiveEntity.class);
						if (positionEntity != null && positionEntity.rotation != 0) {
							@Pc(159) boolean westWithinScene;
							@Pc(168) boolean eastWithinScene;
							@Pc(182) short bitpackedRange;
							@Pc(188) int startX;
							@Pc(194) int endX;
							if (positionEntity.rotation == 1) {
								westWithinScene = k - 1 >= sceneStartX;
								eastWithinScene = k + 1 <= sceneEndX;
								if (!westWithinScene && j + 1 <= sceneEndY) {
									bitpackedRange = staticPointLight.ranges[rangeIndex + 1];
									startX = sceneBaseX + (bitpackedRange >>> 8);
									endX = startX + (bitpackedRange & 0xFF);
									westWithinScene = k > startX && k < endX;
								}
								if (!eastWithinScene && j - 1 >= sceneStartY) {
									bitpackedRange = staticPointLight.ranges[rangeIndex - 1];
									startX = sceneBaseX + (bitpackedRange >>> 8);
									endX = startX + (bitpackedRange & 0xFF);
									eastWithinScene = k > startX && k < endX;
								}
								if (westWithinScene && !eastWithinScene) {
									displacement = 4;
								} else if (eastWithinScene && !westWithinScene) {
									displacement = 2;
								}
							} else {
								westWithinScene = k - 1 >= sceneStartX;
								eastWithinScene = k + 1 <= sceneEndX;
								if (!westWithinScene && j - 1 >= sceneStartY) {
									bitpackedRange = staticPointLight.ranges[rangeIndex - 1];
									startX = sceneBaseX + (bitpackedRange >>> 8);
									endX = startX + (bitpackedRange & 0xFF);
									westWithinScene = k > startX && k < endX;
								}
								if (!eastWithinScene && j + 1 <= sceneEndY) {
									bitpackedRange = staticPointLight.ranges[rangeIndex + 1];
									startX = sceneBaseX + (bitpackedRange >>> 8);
									endX = startX + (bitpackedRange & 0xFF);
									eastWithinScene = k > startX && k < endX;
								}
								if (westWithinScene && !eastWithinScene) {
									displacement = 3;
								} else if (eastWithinScene && !westWithinScene) {
									displacement = 5;
								}
							}
						}
						lightDisplacement[displacementIndex++] = displacement;
					}
					rangeIndex++;
				}
				Scene.pointLightApplied[i] = true;
				Scene.aClass65Array1[level].addLight(pointLight, lightDisplacement);
			}
		}
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(I)V")
	public static void hideAllRoofs() {
		@Pc(11) int viewIndex = 0;
		for (@Pc(13) int i = 0; i < MapRegion.sceneSizeX; i++) {
			for (@Pc(17) int j = 0; j < MapRegion.sceneSizeY; j++) {
				if (MapRegion.calculateVisibilityAdjustments(j, Scene.aClass100ArrayArrayArray3, i, viewIndex, true)) {
					viewIndex++;
				}
				if (viewIndex >= 512) {
					return;
				}
			}
		}
	}

	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(IIIILclient!fha;)V")
	public static void addGroundItem(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z, @OriginalArg(3) int y, @OriginalArg(4) AbstractGroundItem groundItem) {
		@Pc(4) Tile tile = Scene.getTile(level, x, z);
		if (tile == null) {
			return;
		}
		groundItem.x = (x << Scene.tileUnits) + Scene.halfTileOffset;
		groundItem.y = y;
		groundItem.z = (z << Scene.tileUnits) + Scene.halfTileOffset;
		tile.groundItem = groundItem;
		@Pc(33) int layerIndex = Scene.aClass65Array1 == Scene.aClass65Array3 ? 1 : 0;
		if (groundItem.isStationary()) {
			if (groundItem.hasAlpha()) {
				groundItem.nextEntity = Scene.aClass3_Sub1Array129[layerIndex];
				Scene.aClass3_Sub1Array129[layerIndex] = groundItem;
				return;
			}
			groundItem.nextEntity = Scene.stringStack[layerIndex];
			Scene.stringStack[layerIndex] = groundItem;
			Scene.staticSceneDirty = true;
			return;
		}
		groundItem.nextEntity = Scene.aClass3_Sub1Array94[layerIndex];
		Scene.aClass3_Sub1Array94[layerIndex] = groundItem;
	}

	@OriginalMember(owner = "client!kv", name = "c", descriptor = "(I)V")
	public static void uploadAllGameObjects() {
		if (Scene.gameObjectsUploaded) {
			return;
		}
		Scene.uploadGameObjects(Scene.aClass100ArrayArrayArray1);
		if (UnderwaterTileGrid.aClass100ArrayArrayArray2 != null) {
			Scene.uploadGameObjects(UnderwaterTileGrid.aClass100ArrayArrayArray2);
		}
		Scene.gameObjectsUploaded = true;
	}

	@OriginalMember(owner = "client!hl", name = "d", descriptor = "(III)V")
	public static void removeWalls(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z) {
		@Pc(7) Tile tile = Scene.aClass100ArrayArrayArray3[level][x][z];
		if (tile == null) {
			return;
		}
		Scene.removeFromEntityLists(tile.primaryWall);
		Scene.removeFromEntityLists(tile.secondaryWall);
		if (tile.primaryWall != null) {
			tile.primaryWall = null;
		}
		if (tile.secondaryWall != null) {
			tile.secondaryWall = null;
		}
	}

	@OriginalMember(owner = "client!bg", name = "H", descriptor = "[Lclient!s;")
	public static Ground[] aClass65Array2;

	@OriginalMember(owner = "client!pl", name = "a", descriptor = "(BIIIII)I")
	public static int getBridgedAverageHeight(@OriginalArg(1) int x, @OriginalArg(2) int z, @OriginalArg(3) int centerTileX, @OriginalArg(4) int level, @OriginalArg(5) int centerTileZ) {
		if (Scene.aClass65Array1 == null) {
			return 0;
		} else if (level < 3) {
			@Pc(22) int tileX = x >> 9;
			@Pc(26) int tileZ = z >> 9;
			if (centerTileX < 0 || centerTileZ < 0 || MapRegion.sceneSizeX - 1 < centerTileX || MapRegion.sceneSizeY - 1 < centerTileZ) {
				return 0;
			} else if (tileX >= 1 && tileZ >= 1 && MapRegion.sceneSizeX - 1 >= tileX && tileZ <= MapRegion.sceneSizeY - 1) {
				@Pc(91) boolean bridged = (SettingsBits.tileFunctionMasks[1][x >> 9][z >> 9] & 0x2) != 0;
				@Pc(116) boolean previousBridged;
				@Pc(132) boolean currentBridged;
				if ((x & 0x1FF) == 0) {
					previousBridged = (SettingsBits.tileFunctionMasks[1][tileX - 1][z >> 9] & 0x2) != 0;
					currentBridged = (SettingsBits.tileFunctionMasks[1][tileX][z >> 9] & 0x2) != 0;
					if (previousBridged != currentBridged) {
						bridged = (SettingsBits.tileFunctionMasks[1][centerTileX][centerTileZ] & 0x2) != 0;
					}
				}
				if ((z & 0x1FF) == 0) {
					previousBridged = (SettingsBits.tileFunctionMasks[1][x >> 9][tileZ - 1] & 0x2) != 0;
					currentBridged = (SettingsBits.tileFunctionMasks[1][x >> 9][tileZ] & 0x2) != 0;
					if (previousBridged != currentBridged) {
						bridged = (SettingsBits.tileFunctionMasks[1][centerTileX][centerTileZ] & 0x2) != 0;
					}
				}
				if (bridged) {
					level++;
				}
				return Scene.aClass65Array1[level].averageHeight(x, z);
			} else {
				return 0;
			}
		} else {
			return Scene.aClass65Array1[level].averageHeight(x, z);
		}
	}

	@OriginalMember(owner = "client!aj", name = "u", descriptor = "[[S")
	public static short[][] waterScales;

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(II)I")
	public static int getWaterColor(@OriginalArg(0) int tileX, @OriginalArg(1) int tileZ) {
		return Scene.waterColors == null ? 0 : Scene.waterColors[tileX][tileZ] & 0xFFFFFF;
	}

	@OriginalMember(owner = "client!qc", name = "I", descriptor = "Z")
	public static boolean gameObjectsUploaded = false;
	@OriginalMember(owner = "client!r", name = "y", descriptor = "[Z")
	public static boolean[] pointLightApplied;

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(IB)V")
	public static void initializeRoofSettings(@OriginalArg(0) int size) {
		MapRegion.roofMaxX = new int[size];
		MapRegion.roofMaxZ = new int[size];
		MapRegion.roofMinX = new int[size];
		MapRegion.roofMinZ = new int[size];
		MapRegion.visibleBelowDrawheight = new int[size];
	}

	@OriginalMember(owner = "client!ug", name = "a", descriptor = "()V")
	public static void clearDynamicInteractives() {
		for (@Pc(1) int i = 0; i < Scene.interactiveObjectCount; i++) {
			@Pc(6) AbstractInteractiveEntity positionEntity = Scene.aClass3_Sub1_Sub3Array1[i];
			Scene.removeInteractive(positionEntity, true);
			Scene.aClass3_Sub1_Sub3Array1[i] = null;
		}
		Scene.interactiveObjectCount = 0;
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(IIII)Lclient!kda;")
	public static PathingEntity getLargestPathingEntityOnTile(@OriginalArg(1) int x, @OriginalArg(2) int level, @OriginalArg(3) int y) {
		@Pc(11) Tile tile = Scene.aClass100ArrayArrayArray3[level][x][y];
		if (tile == null) {
			return null;
		}
		@Pc(22) PathingEntity pathingEntity = null;
		@Pc(24) int bestArea = -1;
		for (@Pc(27) InteractiveEntityContainer positionEntityNode = tile.interactives; positionEntityNode != null; positionEntityNode = positionEntityNode.child) {
			@Pc(31) AbstractInteractiveEntity positionEntity = positionEntityNode.interactiveObject;
			if (positionEntity instanceof PathingEntity) {
				@Pc(37) PathingEntity pathingEntity2 = (PathingEntity) positionEntity;
				@Pc(47) int halfSize = (pathingEntity2.getSize() - 1) * 256 + 252;
				@Pc(55) int minX = pathingEntity2.x - halfSize >> 9;
				@Pc(63) int minY = pathingEntity2.z - halfSize >> 9;
				@Pc(70) int maxX = halfSize + pathingEntity2.x >> 9;
				@Pc(77) int maxY = pathingEntity2.z + halfSize >> 9;
				if (minX <= x && y >= minY && x <= maxX && y <= maxY) {
					@Pc(110) int area = (maxY + 1 - y) * (-x + 1 + maxX);
					if (area > bestArea) {
						bestArea = area;
						pathingEntity = pathingEntity2;
					}
				}
			}
		}
		return pathingEntity;
	}

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(Lclient!ga;III)V")
	public static void shareLightWithNeighbourGroundDecor(@OriginalArg(0) Entity entity, @OriginalArg(1) int level, @OriginalArg(2) int x, @OriginalArg(3) int y) {
		@Pc(12) Tile tile;
		if (x < Scene.sceneWidth) {
			tile = Scene.aClass100ArrayArrayArray3[level][x + 1][y];
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration.hasTileShadow()) {
				entity.shareLight(Scene.toolkit, 0, true, tile.groundDecoration, 0, Culling.fullTileDimension);
			}
		}
		if (y < Scene.sceneWidth) {
			tile = Scene.aClass100ArrayArrayArray3[level][x][y + 1];
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration.hasTileShadow()) {
				entity.shareLight(Scene.toolkit, 0, true, tile.groundDecoration, Culling.fullTileDimension, 0);
			}
		}
		if (x < Scene.sceneWidth && y < Camera.sceneLength) {
			tile = Scene.aClass100ArrayArrayArray3[level][x + 1][y + 1];
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration.hasTileShadow()) {
				entity.shareLight(Scene.toolkit, 0, true, tile.groundDecoration, Culling.fullTileDimension, Culling.fullTileDimension);
			}
		}
		if (x < Scene.sceneWidth && y > 0) {
			tile = Scene.aClass100ArrayArrayArray3[level][x + 1][y - 1];
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration.hasTileShadow()) {
				entity.shareLight(Scene.toolkit, 0, true, tile.groundDecoration, -Culling.fullTileDimension, Culling.fullTileDimension);
			}
		}
	}

	@OriginalMember(owner = "client!qt", name = "a", descriptor = "(III)Lclient!oga;")
	public static InteractiveEntityContainer getTileInteractives(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z) {
		@Pc(7) Tile tile = Scene.aClass100ArrayArrayArray3[level][x][z];
		return tile == null ? null : tile.interactives;
	}

	@OriginalMember(owner = "client!vb", name = "a", descriptor = "(IIII)I")
	public static int tileHeightForAbsolutePosition(@OriginalArg(0) int absoluteX, @OriginalArg(1) int level, @OriginalArg(3) int absoluteY) {
		if (Scene.aClass65Array1 == null) {
			return 0;
		}
		@Pc(11) int tileX = absoluteX >> 9;
		@Pc(15) int tileY = absoluteY >> 9;
		if (tileX < 0 || tileY < 0 || tileX > MapRegion.sceneSizeX - 1 || MapRegion.sceneSizeY - 1 < tileY) {
			return 0;
		}
		@Pc(43) int heightLevel = level;
		if (level < 3 && (SettingsBits.tileFunctionMasks[1][tileX][tileY] & TileFlag.BRIDGE) != 0) {
			heightLevel = level + 1;
		}
		return Scene.aClass65Array1[heightLevel].averageHeight(absoluteX, absoluteY);
	}

	@OriginalMember(owner = "client!ada", name = "P", descriptor = "I")
	public static int renderThreadCount = 1;

	@OriginalMember(owner = "client!ada", name = "a", descriptor = "(IIILclient!aba;)V")
	public static void setGroundDecoration(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z, @OriginalArg(3) GroundDecor groundDecor) {
		@Pc(4) Tile tile = Scene.getTile(level, x, z);
		if (tile == null) {
			return;
		}
		tile.groundDecoration = groundDecor;
		@Pc(16) int listIndex = Scene.aClass65Array1 == Scene.aClass65Array3 ? 1 : 0;
		if (groundDecor.isStationary()) {
			if (groundDecor.hasAlpha()) {
				groundDecor.nextEntity = Scene.aClass3_Sub1Array129[listIndex];
				Scene.aClass3_Sub1Array129[listIndex] = groundDecor;
				return;
			}
			groundDecor.nextEntity = Scene.stringStack[listIndex];
			Scene.stringStack[listIndex] = groundDecor;
			Scene.staticSceneDirty = true;
			return;
		}
		groundDecor.nextEntity = Scene.aClass3_Sub1Array94[listIndex];
		Scene.aClass3_Sub1Array94[listIndex] = groundDecor;
	}

	@OriginalMember(owner = "client!vga", name = "a", descriptor = "(III)Lclient!fha;")
	public static AbstractGroundItem removeGroundItem(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z) {
		@Pc(7) Tile tile = Scene.aClass100ArrayArrayArray3[level][x][z];
		if (tile == null) {
			return null;
		} else {
			@Pc(14) AbstractGroundItem groundItem = tile.groundItem;
			tile.groundItem = null;
			Scene.removeFromEntityLists(groundItem);
			return groundItem;
		}
	}

	@OriginalMember(owner = "client!cda", name = "a", descriptor = "(BI)V")
	public static void fillRoofStamps(@OriginalArg(0) byte value) {
		if (MapRegion.roofStamps == null) {
			MapRegion.roofStamps = new byte[4][MapRegion.sceneSizeX][MapRegion.sceneSizeY];
		}
		for (@Pc(23) int i = 0; i < 4; i++) {
			for (@Pc(27) int j = 0; j < MapRegion.sceneSizeX; j++) {
				for (@Pc(31) int k = 0; k < MapRegion.sceneSizeY; k++) {
					MapRegion.roofStamps[i][j][k] = value;
				}
			}
		}
	}
}
