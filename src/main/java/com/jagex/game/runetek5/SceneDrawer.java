package com.jagex.game.runetek5;

import com.jagex.game.runetek5.entity.EntityNodeList;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.sound.SceneCullingActive;
import com.jagex.game.runetek5.statics.UnderwaterTileGrid;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.PickableEntity;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.entity.Entity;
import com.jagex.game.runetek5.entity.GroundRenderTask;
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager;
import com.jagex.game.runetek5.scene.culling.Culling;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity;
import com.jagex.game.runetek5.entity.BoundingBoxPolygon;

public final class SceneDrawer {

	@OriginalMember(owner = "client!gj", name = "a", descriptor = "(IIII[[[B[I[I[I[I[IIBIIZZIIZ)V")
	public static void drawScene(@OriginalArg(0) int cycle, @OriginalArg(1) int cameraX, @OriginalArg(2) int cameraY, @OriginalArg(3) int cameraZ, @OriginalArg(4) byte[][][] settingBits, @OriginalArg(5) int[] visibleBelowDrawheight, @OriginalArg(6) int[] roofMinX, @OriginalArg(7) int[] roofMaxX, @OriginalArg(8) int[] roofMaxZ, @OriginalArg(9) int[] roofMinZ, @OriginalArg(10) int lowestRenderedPlane, @OriginalArg(11) byte roofPreference, @OriginalArg(12) int playerTileX, @OriginalArg(13) int playerTileZ, @OriginalArg(14) boolean disableFlickeringEffects, @OriginalArg(16) int orthoZoom, @OriginalArg(17) int skipFlags, @OriginalArg(18) boolean markDirtyOnRender) {
		SceneCullingActive.cullingActive = true;
		SceneDrawer.copyLights = Scene.toolkit.getMaxLights() > 0;
		SceneDrawer.calculateTilesOnScreen = true;
		SceneDrawer.cameraGridPositionX = cameraX >> Scene.tileUnits;
		Scene.cameraTileZ = cameraZ >> Scene.tileUnits;
		WorldMapDisplay.sceneCameraX = cameraX;
		SceneCameraZ.sceneCameraZ = cameraZ;
		Culling.cameraY = cameraY;
		SceneDrawer.visibleToXnegative = SceneDrawer.cameraGridPositionX - SceneVisibleTiles.visibleTiles;
		if (SceneDrawer.visibleToXnegative < 0) {
			SceneDrawer.visibleToInvertedXnegative = -SceneDrawer.visibleToXnegative;
			SceneDrawer.visibleToXnegative = 0;
		} else {
			SceneDrawer.visibleToInvertedXnegative = 0;
		}
		SceneDrawer.visibleToYnegative = Scene.cameraTileZ - SceneVisibleTiles.visibleTiles;
		if (SceneDrawer.visibleToYnegative < 0) {
			SceneDrawer.visibleToInvertedYnegative = -SceneDrawer.visibleToYnegative;
			SceneDrawer.visibleToYnegative = 0;
		} else {
			SceneDrawer.visibleToInvertedYnegative = 0;
		}
		SceneDrawer.visibleToXpositive = SceneDrawer.cameraGridPositionX + SceneVisibleTiles.visibleTiles;
		if (SceneDrawer.visibleToXpositive > Scene.sceneWidth) {
			SceneDrawer.visibleToXpositive = Scene.sceneWidth;
		}
		SceneDrawer.visibleToYpositive = Scene.cameraTileZ + SceneVisibleTiles.visibleTiles;
		if (SceneDrawer.visibleToYpositive > Camera.sceneLength) {
			SceneDrawer.visibleToYpositive = Camera.sceneLength;
		}
		@Pc(72) boolean[][] unadjustedAdjacentTilesOnScreen = Scene.tileInFrustum;
		@Pc(74) boolean[][] unadjustedTilesOnScreen = Scene.tileOnScreen;
		@Pc(78) int local78;
		@Pc(81) int local81;
		@Pc(83) int local83;
		if (SceneDrawer.calculateTilesOnScreen) {
			for (int gridX = 0; gridX < SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 2; gridX++) {
				int prevTileClip = 0;
				int tileClip = 0;
				for (@Pc(85) int i = 0; i < SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 2; i++) {
					if (i > 1) {
						Scene.tileVisibility[i - 2] = prevTileClip;
					}
					prevTileClip = tileClip;
					@Pc(103) int sceneX = SceneDrawer.cameraGridPositionX + gridX - SceneVisibleTiles.visibleTiles;
					@Pc(109) int sceneZ = Scene.cameraTileZ + i - SceneVisibleTiles.visibleTiles;
					@Pc(123) int local123;
					if (sceneX >= 0 && sceneZ >= 0 && sceneX < Scene.sceneWidth && sceneZ < Camera.sceneLength) {
						int worldX = sceneX << Scene.tileUnits;
						@Pc(127) int worldZ = sceneZ << Scene.tileUnits;
						@Pc(144) int lowY = Scene.aClass65Array2[Scene.aClass65Array2.length - 1].getHeight(sceneZ, sceneX) - (0x3E8 << Scene.tileUnits - 7);
						@Pc(166) int highY = Scene.aClass65Array3 == null ? Scene.aClass65Array2[0].getHeight(sceneZ, sceneX) + Culling.fullTileDimension : Scene.aClass65Array3[0].getHeight(sceneZ, sceneX) + Culling.fullTileDimension;
						tileClip = orthoZoom >= 0 ? Scene.toolkit.r(worldX, lowY, worldZ, worldX, highY, worldZ, orthoZoom) : Scene.toolkit.JA(worldX, lowY, worldZ, worldX, highY, worldZ);
						Scene.tileOnScreen[gridX][i] = tileClip == 0;
					} else {
						tileClip = -1;
						Scene.tileOnScreen[gridX][i] = false;
					}
					if (gridX > 0 && i > 0) {
						int combinedClip = Scene.tileVisibility[i - 1] & Scene.tileVisibility[i] & prevTileClip & tileClip;
						Scene.tileInFrustum[gridX - 1][i - 1] = combinedClip == 0;
					}
				}
				Scene.tileVisibility[SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles] = prevTileClip;
				Scene.tileVisibility[SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 1] = tileClip;
			}
			if (orthoZoom >= 0) {
				SceneCullingActive.cullingActive = false;
			} else {
				SceneDrawer.visibleBelowDrawheight = visibleBelowDrawheight;
				SceneDrawer.roofMinX = roofMinX;
				SceneDrawer.roofMaxX = roofMaxX;
				SceneDrawer.roofMaxZ = roofMaxZ;
				SceneDrawer.roofMinZ = roofMinZ;
				Culling.calculateOcclusion(lowestRenderedPlane, Scene.toolkit);
			}
		} else {
			if (SceneDrawer.allTilesVisibleFallback == null) {
				SceneDrawer.allTilesVisibleFallback = new boolean[Scene.sceneWidth + Scene.sceneWidth + 1][Camera.sceneLength + Scene.sceneWidth + 1];
			}
			for (int gridX = 0; gridX < SceneDrawer.allTilesVisibleFallback.length; gridX++) {
				for (int gridZ = 0; gridZ < SceneDrawer.allTilesVisibleFallback[0].length; gridZ++) {
					SceneDrawer.allTilesVisibleFallback[gridX][gridZ] = true;
				}
			}
			Scene.tileOnScreen = SceneDrawer.allTilesVisibleFallback;
			Scene.tileInFrustum = SceneDrawer.allTilesVisibleFallback;
			SceneDrawer.visibleToXnegative = 0;
			SceneDrawer.visibleToYnegative = 0;
			SceneDrawer.visibleToXpositive = Scene.sceneWidth;
			SceneDrawer.visibleToYpositive = Camera.sceneLength;
			SceneCullingActive.cullingActive = false;
		}
		ParticleSystemManager.processParticleCycle(Scene.toolkit);
		if (!PickList.aClass182_1.retained) {
			@Pc(349) EntityNodeList linkedList = PickList.aClass182_1.entities;
			for (@Pc(354) PickableEntity pickableEntity = (PickableEntity) linkedList.getFirst(); pickableEntity != null; pickableEntity = (PickableEntity) linkedList.getNext()) {
				pickableEntity.unlinkNode();
				PickableEntity.recycle(pickableEntity);
			}
		}
		if (SceneDrawer.copyLights) {
			for (int lightIndex = 0; lightIndex < Scene.lightSlotCount; lightIndex++) {
				Scene.aClass300Array1[lightIndex].applyEffect(disableFlickeringEffects, cycle);
			}
		}
		if (SceneDrawer.multithreadedRendering) {
			SceneDrawer.projectionParams = Scene.toolkit.Y();
			Scene.toolkit.K(SceneDrawer.clipBounds);
			int sliceWidth = (SceneDrawer.clipBounds[2] - SceneDrawer.clipBounds[0]) / Scene.renderThreadCount;
			for (int threadIndex = 0; threadIndex < Scene.renderThreadCount - 1; threadIndex++) {
				ScriptRunner.threadSplitX[threadIndex] = sliceWidth * (threadIndex + 1) + SceneDrawer.threadSplitOffsets[threadIndex];
			}
			for (int workerIndex = 0; workerIndex < RenderWorker.aClass366Array1.length; workerIndex++) {
				RenderWorker.aClass366Array1[workerIndex].resume();
			}
		}
		if (UnderwaterTileGrid.aClass100ArrayArrayArray2 != null) {
			if (SceneDrawer.multithreadedRendering) {
				SceneDrawer.assignRenderQueues(0);
			}
			Scene.setUnderwater(true);
			Scene.toolkit.ra(-1, 1583160, 40, 127);
			SceneDrawer.drawTiles(true, settingBits, lowestRenderedPlane, roofPreference, orthoZoom, skipFlags, markDirtyOnRender);
			if (SceneDrawer.multithreadedRendering) {
				RenderWorker.waitForWorkers();
			}
			Scene.toolkit.pa();
			Scene.setUnderwater(false);
		}
		SceneDrawer.drawTiles(false, settingBits, lowestRenderedPlane, roofPreference, orthoZoom, skipFlags, markDirtyOnRender);
		if (SceneDrawer.multithreadedRendering) {
			for (int level = 0; level < Scene.length; level++) {
				Scene.aBooleanArrayArrayArray1[level] = Scene.levelTileVisibility[level];
			}
			SceneDrawer.assignRenderQueues(0);
			for (int workerIndex = 0; workerIndex < RenderWorker.aClass366Array1.length; workerIndex++) {
				RenderWorker.aClass366Array1[workerIndex].resume();
			}
		}
		if (SceneDrawer.multithreadedRendering) {
			RenderWorker.waitForWorkers();
			for (int level = 0; level < Scene.length; level++) {
				Scene.levelTileVisibility[level] = Scene.aBooleanArrayArrayArray1[level];
			}
			if (SceneDrawer.renderThreadCount == 2) {
				@Pc(543) int local543;
				if (SceneDrawer.workerCompletionTimes[0] < SceneDrawer.workerCompletionTimes[1]) {
					if (ScriptRunner.threadSplitX[0] + SceneDrawer.threadSplitOffsets[0] > SceneDrawer.clipBounds[0]) {
						int previousOffset = SceneDrawer.threadSplitOffsets[0]++;
					}
				} else if (SceneDrawer.workerCompletionTimes[0] > SceneDrawer.workerCompletionTimes[1] && ScriptRunner.threadSplitX[0] + SceneDrawer.threadSplitOffsets[0] < SceneDrawer.clipBounds[2]) {
					int previousOffset = SceneDrawer.threadSplitOffsets[0]--;
				}
			}
		}
		if (!SceneDrawer.calculateTilesOnScreen) {
			Scene.tileInFrustum = unadjustedAdjacentTilesOnScreen;
			Scene.tileOnScreen = unadjustedTilesOnScreen;
		}
		Culling.drawOcclusionDebug();
	}
	@OriginalMember(owner = "client!am", name = "a", descriptor = "(Lclient!ga;ZZ)V")
	public static void submitEntity(@OriginalArg(0) Entity entity, @OriginalArg(2) boolean markDirtyOnRender) {
		entity.markDirtyOnRender = markDirtyOnRender;
		if (SceneDrawer.multithreadedRendering) {
			Scene.aClass59Array1[Scene.aClass59Array1.length - 1].addEntity(entity);
		} else {
			SceneDrawer.renderSceneEntity(entity, SceneDrawer.aClass2_Sub1Array4);
		}
	}

	@OriginalMember(owner = "client!be", name = "u", descriptor = "I")
	public static int visibleToYnegative;

	@OriginalMember(owner = "client!jca", name = "m", descriptor = "[I")
	public static int[] projectionParams = new int[4];

	@OriginalMember(owner = "client!ida", name = "c", descriptor = "I")
	public static int visibleToXnegative;

	@OriginalMember(owner = "client!ht", name = "a", descriptor = "I")
	public static int visibleToXpositive;
	@OriginalMember(owner = "client!ta", name = "o", descriptor = "I")
	public static int visibleToInvertedYnegative;

	@OriginalMember(owner = "client!rga", name = "G", descriptor = "I")
	public static int visibleToYpositive;

	@OriginalMember(owner = "client!um", name = "r", descriptor = "[[Z")
	public static boolean[][] allTilesVisibleFallback;

	@OriginalMember(owner = "client!gh", name = "a", descriptor = "(Z[[[BIBIIZ)V")
	public static void drawTiles(@OriginalArg(0) boolean underwater, @OriginalArg(1) byte[][][] settingBits, @OriginalArg(2) int lowestRenderedPlane, @OriginalArg(3) byte roofPreference, @OriginalArg(4) int orthoZoom, @OriginalArg(5) int skipFlags, @OriginalArg(6) boolean markDirtyOnRender) {
		@Pc(5) int listIndex = underwater ? 1 : 0;
		Scene.onscreenOpaqueCount = 0;
		Scene.transparentEntityCount = 0;
		Culling.cullingCycle++;
		@Pc(21) Entity entity;
		if ((skipFlags & 0x2) == 0) {
			for (entity = Scene.stringStack[listIndex]; entity != null; entity = entity.nextEntity) {
				if (!SceneDrawer.isEntityCulled(entity, underwater, settingBits, lowestRenderedPlane, roofPreference)) {
					Entity.computeScreenPosition(entity);
					if (entity.screenDepth != -1) {
						Scene.aClass3_Sub1Array143[Scene.onscreenOpaqueCount++] = entity;
					}
				}
			}
		}
		@Pc(137) int length;
		if ((skipFlags & 0x1) == 0) {
			for (entity = Scene.aClass3_Sub1Array129[listIndex]; entity != null; entity = entity.nextEntity) {
				if (!SceneDrawer.isEntityCulled(entity, underwater, settingBits, lowestRenderedPlane, roofPreference)) {
					Entity.computeScreenPosition(entity);
					if (entity.screenDepth != -1) {
						Scene.aClass3_Sub1Array145[Scene.transparentEntityCount++] = entity;
					}
				}
			}
			for (@Pc(87) Entity entity2 = Scene.aClass3_Sub1Array94[listIndex]; entity2 != null; entity2 = entity2.nextEntity) {
				if (!SceneDrawer.isEntityCulled(entity2, underwater, settingBits, lowestRenderedPlane, roofPreference)) {
					if (entity2.hasAlpha()) {
						Entity.computeScreenPosition(entity2);
						if (entity2.screenDepth != -1) {
							Scene.aClass3_Sub1Array145[Scene.transparentEntityCount++] = entity2;
						}
					} else {
						Entity.computeScreenPosition(entity2);
						if (entity2.screenDepth != -1) {
							Scene.aClass3_Sub1Array143[Scene.onscreenOpaqueCount++] = entity2;
						}
					}
				}
			}
			if (!underwater) {
				for (length = 0; length < Scene.interactiveObjectCount; length++) {
					if (!SceneDrawer.isEntityCulled(Scene.aClass3_Sub1_Sub3Array1[length], underwater, settingBits, lowestRenderedPlane, roofPreference)) {
						Entity.computeScreenPosition(Scene.aClass3_Sub1_Sub3Array1[length]);
						if (Scene.aClass3_Sub1_Sub3Array1[length].screenDepth != -1) {
							if (Scene.aClass3_Sub1_Sub3Array1[length].hasAlpha()) {
								Scene.aClass3_Sub1Array145[Scene.transparentEntityCount++] = Scene.aClass3_Sub1_Sub3Array1[length];
							} else {
								Scene.aClass3_Sub1Array143[Scene.onscreenOpaqueCount++] = Scene.aClass3_Sub1_Sub3Array1[length];
							}
						}
					}
				}
			}
		}
		@Pc(198) int local198;
		if (Scene.onscreenOpaqueCount > 0) {
			SceneDrawer.sortEntitiesByDepth(Scene.aClass3_Sub1Array143, 0, Scene.onscreenOpaqueCount - 1);
			for (int entityIndex = 0; entityIndex < Scene.onscreenOpaqueCount; entityIndex++) {
				SceneDrawer.submitEntity(Scene.aClass3_Sub1Array143[entityIndex], markDirtyOnRender);
			}
		}
		if (SceneDrawer.copyLights) {
			Scene.toolkit.copyLights(0, null);
		}
		if ((skipFlags & 0x2) == 0) {
			for (int level = ScriptRunner.minimumDrawLevel; level < Scene.length; level++) {
				@Pc(271) boolean[][] visibility;
				@Pc(281) int local281;
				@Pc(288) int tileX;
				@Pc(290) int z;
				@Pc(230) int endX;
				if (level < lowestRenderedPlane || settingBits == null) {
					endX = Scene.tileInFrustum.length;
					if (SceneDrawer.visibleToXnegative + Scene.tileInFrustum.length > Scene.sceneWidth) {
						endX -= SceneDrawer.visibleToXnegative + Scene.tileInFrustum.length - Scene.sceneWidth;
					}
					length = Scene.tileInFrustum[0].length;
					if (SceneDrawer.visibleToYnegative + Scene.tileInFrustum[0].length > Camera.sceneLength) {
						length -= SceneDrawer.visibleToYnegative + Scene.tileInFrustum[0].length - Camera.sceneLength;
					}
					visibility = Scene.tileOnScreen;
					if (SceneDrawer.calculateTilesOnScreen) {
						if (SceneDrawer.multithreadedRendering) {
							visibility = Scene.levelTileVisibility[level];
						}
						for (int gridX = SceneDrawer.visibleToInvertedXnegative; gridX < endX; gridX++) {
							tileX = gridX + SceneDrawer.visibleToXnegative - SceneDrawer.visibleToInvertedXnegative;
							for (z = SceneDrawer.visibleToInvertedYnegative; z < length; z++) {
								if (Scene.tileInFrustum[gridX][z] && !Culling.isTileHidden(level, tileX, z + SceneDrawer.visibleToYnegative - SceneDrawer.visibleToInvertedYnegative)) {
									visibility[gridX][z] = true;
								} else {
									visibility[gridX][z] = false;
								}
							}
						}
					}
					if (SceneDrawer.multithreadedRendering) {
						if (orthoZoom >= 0) {
							Scene.aClass65Array1[level].renderFloor(0, 0, 0, null, false, orthoZoom, skipFlags);
						} else {
							Scene.aClass65Array1[level].renderTiles(0, 0, 0, null, false, skipFlags);
						}
						for (int threadIndex = 0; threadIndex < SceneDrawer.renderThreadCount; threadIndex++) {
							Scene.aClass59Array1[threadIndex].addGroundTask(new GroundRenderTask(level + 1));
						}
					} else if (orthoZoom >= 0) {
						Scene.aClass65Array1[level].renderFloor(SceneDrawer.cameraGridPositionX, Scene.cameraTileZ, SceneVisibleTiles.visibleTiles, Scene.tileOnScreen, true, orthoZoom, skipFlags);
					} else {
						Scene.aClass65Array1[level].renderTiles(SceneDrawer.cameraGridPositionX, Scene.cameraTileZ, SceneVisibleTiles.visibleTiles, Scene.tileOnScreen, true, skipFlags);
					}
				} else {
					endX = Scene.tileInFrustum.length;
					if (SceneDrawer.visibleToXnegative + Scene.tileInFrustum.length > Scene.sceneWidth) {
						endX -= SceneDrawer.visibleToXnegative + Scene.tileInFrustum.length - Scene.sceneWidth;
					}
					length = Scene.tileInFrustum[0].length;
					if (SceneDrawer.visibleToYnegative + Scene.tileInFrustum[0].length > Camera.sceneLength) {
						length -= SceneDrawer.visibleToYnegative + Scene.tileInFrustum[0].length - Camera.sceneLength;
					}
					visibility = Scene.tileOnScreen;
					if (SceneDrawer.calculateTilesOnScreen) {
						if (SceneDrawer.multithreadedRendering) {
							visibility = Scene.levelTileVisibility[level];
						}
						for (int gridX = SceneDrawer.visibleToInvertedXnegative; gridX < endX; gridX++) {
							tileX = gridX + SceneDrawer.visibleToXnegative - SceneDrawer.visibleToInvertedXnegative;
							for (z = SceneDrawer.visibleToInvertedYnegative; z < length; z++) {
								visibility[gridX][z] = false;
								if (Scene.tileInFrustum[gridX][z]) {
									@Pc(309) int tileZ = z + SceneDrawer.visibleToYnegative - SceneDrawer.visibleToInvertedYnegative;
									for (@Pc(311) int i = level; i >= 0; i--) {
										if (Scene.aClass100ArrayArrayArray3[i][tileX][tileZ] != null && Scene.aClass100ArrayArrayArray3[i][tileX][tileZ].plane == level) {
											if ((i < lowestRenderedPlane || settingBits[i][tileX][tileZ] != roofPreference) && !Culling.isTileHidden(level, tileX, tileZ)) {
												visibility[gridX][z] = true;
												break;
											}
											visibility[gridX][z] = false;
											break;
										}
									}
								}
							}
						}
					}
					if (SceneDrawer.multithreadedRendering) {
						if (orthoZoom >= 0) {
							Scene.aClass65Array1[level].renderFloor(0, 0, 0, null, false, orthoZoom, skipFlags);
						} else {
							Scene.aClass65Array1[level].renderTiles(0, 0, 0, null, false, skipFlags);
						}
						for (int threadIndex = 0; threadIndex < SceneDrawer.renderThreadCount; threadIndex++) {
							Scene.aClass59Array1[threadIndex].addGroundTask(new GroundRenderTask(level + 1));
						}
					} else if (orthoZoom >= 0) {
						Scene.aClass65Array1[level].renderFloor(SceneDrawer.cameraGridPositionX, Scene.cameraTileZ, SceneVisibleTiles.visibleTiles, Scene.tileOnScreen, false, orthoZoom, skipFlags);
					} else {
						Scene.aClass65Array1[level].renderTiles(SceneDrawer.cameraGridPositionX, Scene.cameraTileZ, SceneVisibleTiles.visibleTiles, Scene.tileOnScreen, false, skipFlags);
					}
				}
			}
		}
		if (Scene.transparentEntityCount > 0) {
			Entity.quicksortAbove(Scene.aClass3_Sub1Array145, 0, Scene.transparentEntityCount - 1);
			for (int entityIndex = 0; entityIndex < Scene.transparentEntityCount; entityIndex++) {
				SceneDrawer.submitEntity(Scene.aClass3_Sub1Array145[entityIndex], markDirtyOnRender);
			}
		}
	}

	@OriginalMember(owner = "client!eea", name = "A", descriptor = "I")
	public static int cameraGridPositionX;

	@OriginalMember(owner = "client!sv", name = "V", descriptor = "[I")
	public static int[] roofMaxX;
	@OriginalMember(owner = "client!pj", name = "T", descriptor = "Z")
	public static boolean calculateTilesOnScreen;

	@OriginalMember(owner = "client!hn", name = "N", descriptor = "Z")
	public static boolean multithreadedRendering = false;

	@OriginalMember(owner = "client!ff", name = "zd", descriptor = "I")
	public static int renderThreadCount;

	@OriginalMember(owner = "client!pr", name = "y", descriptor = "I")
	public static int timesSceneRendered = 0;

	@OriginalMember(owner = "client!jd", name = "w", descriptor = "I")
	public static int visibleToInvertedXnegative;

	@OriginalMember(owner = "client!ef", name = "d", descriptor = "[I")
	public static int[] threadSplitOffsets;

	@OriginalMember(owner = "client!bba", name = "g", descriptor = "(I)V")
	public static void assignRenderQueues(@OriginalArg(0) int stage) {
		if (stage == 0) {
			if (SceneDrawer.renderThreadCount == 2) {
				RenderWorker.aClass366Array1[0].setQueue(Scene.aClass59Array1[0]);
				RenderWorker.aClass366Array1[1].setQueue(Scene.aClass59Array1[1]);
			} else if (SceneDrawer.renderThreadCount == 3) {
				RenderWorker.aClass366Array1[0].setQueue(Scene.aClass59Array1[0]);
				RenderWorker.aClass366Array1[1].setQueue(Scene.aClass59Array1[1]);
				RenderWorker.aClass366Array1[2].setQueue(Scene.aClass59Array1[2]);
			} else {
				RenderWorker.aClass366Array1[0].setQueue(Scene.aClass59Array1[0]);
				RenderWorker.aClass366Array1[1].setQueue(Scene.aClass59Array1[1]);
				RenderWorker.aClass366Array1[2].setQueue(Scene.aClass59Array1[2]);
				RenderWorker.aClass366Array1[3].setQueue(Scene.aClass59Array1[3]);
			}
		} else if (stage == 1) {
			if (SceneDrawer.renderThreadCount == 2) {
				RenderWorker.aClass366Array1[0].setQueue(Scene.aClass59Array1[2]);
			} else if (SceneDrawer.renderThreadCount == 3) {
				RenderWorker.aClass366Array1[0].setQueue(Scene.aClass59Array1[3]);
				RenderWorker.aClass366Array1[1].setQueue(Scene.aClass59Array1[4]);
			} else {
				RenderWorker.aClass366Array1[0].setQueue(Scene.aClass59Array1[4]);
				RenderWorker.aClass366Array1[1].setQueue(Scene.aClass59Array1[5]);
				RenderWorker.aClass366Array1[2].setQueue(Scene.aClass59Array1[6]);
			}
		} else if (stage == 2) {
			if (SceneDrawer.renderThreadCount == 2) {
				RenderWorker.aClass366Array1[0].setQueue(Scene.aClass59Array1[3]);
				return;
			}
			if (SceneDrawer.renderThreadCount == 3) {
				RenderWorker.aClass366Array1[0].setQueue(Scene.aClass59Array1[5]);
				return;
			}
			RenderWorker.aClass366Array1[0].setQueue(Scene.aClass59Array1[7]);
		}
	}

	@OriginalMember(owner = "client!bia", name = "b", descriptor = "[J")
	public static final long[] workerCompletionTimes = new long[10];

	@OriginalMember(owner = "client!kq", name = "R", descriptor = "[I")
	public static final int[] clipBounds = new int[4];

	@OriginalMember(owner = "client!kv", name = "m", descriptor = "[I")
	public static int[] roofMaxZ;

	@OriginalMember(owner = "client!ke", name = "ne", descriptor = "[I")
	public static int[] visibleBelowDrawheight;

	@OriginalMember(owner = "client!kda", name = "mb", descriptor = "Z")
	public static boolean copyLights;
	@OriginalMember(owner = "client!hf", name = "a", descriptor = "(Lclient!ga;Z[[[BIB)Z")
	public static boolean isEntityCulled(@OriginalArg(0) Entity entity, @OriginalArg(1) boolean underwater, @OriginalArg(2) byte[][][] settingBits, @OriginalArg(3) int lowestRenderedPlane, @OriginalArg(4) byte roofPreference) {
		if (!SceneDrawer.calculateTilesOnScreen) {
			return false;
		}
		@Pc(8) int minX = entity.x >> Scene.tileUnits;
		@Pc(10) int maxX = minX;
		@Pc(15) int minZ = entity.z >> Scene.tileUnits;
		@Pc(17) int maxZ = minZ;
		if (entity instanceof AbstractInteractiveEntity) {
			maxX = ((AbstractInteractiveEntity) entity).endX;
			maxZ = ((AbstractInteractiveEntity) entity).endZ;
			minX = ((AbstractInteractiveEntity) entity).startX;
			minZ = ((AbstractInteractiveEntity) entity).startZ;
		}
		for (@Pc(38) int i = minX; i <= maxX; i++) {
			for (@Pc(41) int j = minZ; j <= maxZ; j++) {
				if (entity.virtualLevel < Scene.length && i >= SceneDrawer.visibleToXnegative && i < SceneDrawer.visibleToXpositive && j >= SceneDrawer.visibleToYnegative && j < SceneDrawer.visibleToYpositive) {
					if ((settingBits == null || entity.level < lowestRenderedPlane || settingBits[entity.level][i][j] != roofPreference) && entity.canRender() && !entity.isOccluded()) {
						return false;
					}
					if (!underwater && i >= SceneDrawer.cameraGridPositionX - 16 && i <= SceneDrawer.cameraGridPositionX + 16 && j >= Scene.cameraTileZ - 16 && j <= Scene.cameraTileZ + 16) {
						if (SceneDrawer.multithreadedRendering) {
							Scene.aClass59Array1[SceneDrawer.particleWorkerIndex++].addParticleEntity(entity);
							SceneDrawer.particleWorkerIndex %= Scene.renderThreadCount;
						} else {
							entity.particleCycle(Scene.toolkit);
						}
					}
				}
			}
		}
		return true;
	}

	@OriginalMember(owner = "client!oba", name = "b", descriptor = "I")
	public static int particleWorkerIndex = 0;

	@OriginalMember(owner = "client!iea", name = "a", descriptor = "(Lclient!ga;[Lclient!ab;)V")
	public static void renderSceneEntity(@OriginalArg(0) Entity entity, @OriginalArg(1) PointLight[] pointLights) {
		@Pc(6) int local6;
		if (SceneDrawer.copyLights) {
			int lightCount = entity.findLights(pointLights);
			Scene.toolkit.copyLights(lightCount, pointLights);
		}
		if (Scene.aClass65Array3 == Scene.aClass65Array1) {
			@Pc(28) int tileZ;
			int tileX;
			if (entity instanceof AbstractInteractiveEntity) {
				tileX = ((AbstractInteractiveEntity) entity).startX;
				tileZ = ((AbstractInteractiveEntity) entity).startZ;
			} else {
				tileX = entity.x >> Scene.tileUnits;
				tileZ = entity.z >> Scene.tileUnits;
			}
			Scene.toolkit.EA(Scene.aClass65Array2[0].averageHeight(entity.x, entity.z), Scene.getWaterColor(tileX, tileZ), Scene.getWaterScale(tileX, tileZ), Scene.getWaterIntensity(tileX, tileZ));
		}
		@Pc(64) PickableEntity pickableEntity = entity.render(Scene.toolkit);
		if (pickableEntity == null) {
			return;
		}
		if (entity.markDirtyOnRender) {
			@Pc(72) BoundingBoxPolygon[] pickingCylinders = pickableEntity.boundingBoxPolygons;
			for (@Pc(74) int i = 0; i < pickingCylinders.length; i++) {
				@Pc(79) BoundingBoxPolygon pickingCylinder = pickingCylinders[i];
				if (pickingCylinder.positionCalculated) {
					OrthoMode.markOrthoTilesDirty(pickingCylinder.left - pickingCylinder.size, pickingCylinder.right + pickingCylinder.size, pickingCylinder.bottom - pickingCylinder.size, pickingCylinder.top + pickingCylinder.size);
				}
			}
		}
		if (pickableEntity.interactive) {
			pickableEntity.entity = entity;
			if (SceneDrawer.multithreadedRendering) {
				@Pc(119) PickList pickList = PickList.aClass182_1;
				synchronized (PickList.aClass182_1) {
					PickList.aClass182_1.add(pickableEntity);
					return;
				}
			}
			PickList.aClass182_1.add(pickableEntity);
			return;
		}
		PickableEntity.recycle(pickableEntity);
	}

	@OriginalMember(owner = "client!mv", name = "b", descriptor = "[Lclient!ab;")
	public static final PointLight[] aClass2_Sub1Array4 = new PointLight[8];

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "([Lclient!ga;II)V")
	public static void sortEntitiesByDepth(@OriginalArg(0) Entity[] entitys, @OriginalArg(1) int start, @OriginalArg(2) int end) {
		if (start >= end) {
			return;
		}
		@Pc(8) int pivotIndex = (start + end) / 2;
		@Pc(10) int storeIndex = start;
		@Pc(14) Entity entity = entitys[pivotIndex];
		entitys[pivotIndex] = entitys[end];
		entitys[end] = entity;
		@Pc(27) int screenDepth = entity.screenDepth;
		for (@Pc(29) int i = start; i < end; i++) {
			if (entitys[i].screenDepth < screenDepth + (i & 0x1)) {
				@Pc(44) Entity entity2 = entitys[i];
				entitys[i] = entitys[storeIndex];
				entitys[storeIndex++] = entity2;
			}
		}
		entitys[end] = entitys[storeIndex];
		entitys[storeIndex] = entity;
		sortEntitiesByDepth(entitys, start, storeIndex - 1);
		sortEntitiesByDepth(entitys, storeIndex + 1, end);
	}
	@OriginalMember(owner = "client!tm", name = "c", descriptor = "[I")
	public static int[] roofMinX;

	@OriginalMember(owner = "client!client", name = "nb", descriptor = "[I")
	public static int[] roofMinZ;
}
