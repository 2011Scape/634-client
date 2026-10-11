package com.jagex.game.runetek5.scene.culling;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.Sprites;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.scene.tile.Tile;
import com.jagex.game.runetek5.sound.SceneCullingActive;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.SceneCameraZ;
import com.jagex.game.runetek5.SceneDrawer;
import com.jagex.game.runetek5.SceneVisibleTiles;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay;
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.config.meltype.OccludedWallCount;
import com.jagex.game.runetek5.entity.Wall;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.ScriptRunner;
import com.jagex.game.runetek5.W2DebugMode;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;

public final class Culling {
	@OriginalMember(owner = "client!wf", name = "d", descriptor = "Z")
	public static boolean wallCullingEnabled = false;

	private Culling() {
		/* empty */
	}
	@OriginalMember(owner = "client!vu", name = "a", descriptor = "(IIIIIII)V")
	public static void addWallCulling(@OriginalArg(0) int x, @OriginalArg(1) int cullXZ, @OriginalArg(2) int cullY, @OriginalArg(3) int z, @OriginalArg(5) int level, @OriginalArg(6) int displacement) {
		if (displacement != 8 && displacement != 16) {
			@Pc(17) Tile tile = Scene.aClass100ArrayArrayArray3[level][x][z];
			if (tile == null) {
				tile = new Tile(level);
			}
			if (displacement == 1) {
				tile.cullOffsetX = (short) cullXZ;
				tile.cullHeightX = (short) cullY;
			} else if (displacement == 2) {
				tile.cullHeightZ = (short) cullY;
				tile.cullOffsetZ = (short) cullXZ;
			}
			if (Culling.tileCullingClustersBuilt) {
				Culling.rebuildTileCullingClusters();
			}
			return;
		}
		@Pc(67) int startX;
		@Pc(72) int endX;
		@Pc(76) int startZ;
		@Pc(80) int endZ;
		@Pc(90) int startHeight;
		@Pc(100) int endHeight;
		if (displacement != 8) {
			startX = Culling.fullTileDimension + (x << Scene.tileUnits);
			endX = startX - Culling.fullTileDimension;
			startZ = z << Scene.tileUnits;
			endZ = startZ + Culling.fullTileDimension;
			startHeight = Scene.aClass65Array2[level].getHeight(z, x + 1);
			endHeight = Scene.aClass65Array2[level].getHeight(z + 1, x);
			Culling.aClass82Array4[Culling.dynamicCullingClusterCount++] = new CullingCluster(displacement, level, startX, endX, endX, startX, startHeight, endHeight, endHeight - cullY, startHeight - cullY, startZ, endZ, endZ, startZ);
			return;
		}
		startX = x << Scene.tileUnits;
		endX = Culling.fullTileDimension + startX;
		startZ = z << Scene.tileUnits;
		endZ = Culling.fullTileDimension + startZ;
		startHeight = Scene.aClass65Array2[level].getHeight(z, x);
		endHeight = Scene.aClass65Array2[level].getHeight(z + 1, x - -1);
		Culling.aClass82Array4[Culling.dynamicCullingClusterCount++] = new CullingCluster(displacement, level, startX, endX, endX, startX, startHeight, endHeight, endHeight - cullY, -cullY + startHeight, startZ, endZ, endZ, startZ);
	}

	@OriginalMember(owner = "client!vo", name = "a", descriptor = "(IIIIIBI)Z")
	public static boolean hideTile(@OriginalArg(0) int level, @OriginalArg(1) int endZ, @OriginalArg(2) int startZ, @OriginalArg(3) int minY, @OriginalArg(4) int startX, @OriginalArg(6) int endX) {
		if (!Culling.cullingEnabled || !SceneCullingActive.cullingActive) {
			return false;
		} else if (Culling.depthWriteCount < 100) {
			return false;
		} else {
			@Pc(40) int x;
			@Pc(44) int local44;
			if (endX != startX || startZ != endZ) {
				for (x = startX; x <= endX; x++) {
					for (int tileZ = startZ; tileZ <= endZ; tileZ++) {
						if (Culling.tileDrawCycles[level][x][tileZ] == -Culling.cullingCycle) {
							return false;
						}
					}
				}
				int sceneX = (startX << Scene.tileUnits) + 1;
				@Pc(127) int z = (startZ << Scene.tileUnits) + 2;
				if (Culling.isBoxOccluded(minY, (endX + 1 - startX) * Culling.fullTileDimension, z, sceneX, Scene.aClass65Array1[level].getHeight(startZ, startX), Culling.fullTileDimension * (endZ + 1 - startZ))) {
					Sprites.hintHeadId++;
					return true;
				} else {
					return false;
				}
			} else if (Culling.isTileHidden(level, startX, startZ)) {
				x = startX << Scene.tileUnits;
				int sceneZ = startZ << Scene.tileUnits;
				if (Culling.isBoxOccluded(minY, Culling.fullTileDimension, sceneZ, x, Scene.aClass65Array1[level].getHeight(startZ, startX), Culling.fullTileDimension)) {
					Sprites.hintHeadId++;
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	@OriginalMember(owner = "client!lf", name = "T", descriptor = "[Lclient!ea;")
	public static CullingCluster[] tileCullingCluster;

	@OriginalMember(owner = "client!og", name = "n", descriptor = "[Lclient!ea;")
	public static CullingCluster[] aClass82Array3;
	@OriginalMember(owner = "client!nha", name = "a", descriptor = "(IIIBI)Z")
	public static boolean isTileHidden(@OriginalArg(0) int tileZ, @OriginalArg(1) int minY, @OriginalArg(2) int level, @OriginalArg(4) int tileX) {
		if (!Culling.cullingEnabled || !SceneCullingActive.cullingActive) {
			return false;
		} else if (Culling.depthWriteCount < 100) {
			return false;
		} else if (Culling.isTileHidden(level, tileX, tileZ)) {
			@Pc(26) int x = tileX << Scene.tileUnits;
			@Pc(30) int z = tileZ << Scene.tileUnits;
			if (Culling.isBoxOccluded(minY, Culling.fullTileDimension, z, x, Scene.aClass65Array1[level].getHeight(tileZ, tileX), Culling.fullTileDimension)) {
				Sprites.hintHeadId++;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!kea", name = "j", descriptor = "I")
	public static int tileCullingClusterCount;
	@OriginalMember(owner = "client!ts", name = "a", descriptor = "(IIIII)V")
	public static void removeCullingCluster(@OriginalArg(0) int level, @OriginalArg(1) int type, @OriginalArg(2) int z, @OriginalArg(4) int x) {
		if (type != 8 && type != 16) {
			@Pc(21) Tile tile = Scene.aClass100ArrayArrayArray3[level][x][z];
			if (tile != null) {
				if (type == 1) {
					tile.cullHeightX = 0;
				} else if (type == 2) {
					tile.cullHeightZ = 0;
				}
			}
			Culling.rebuildTileCullingClusters();
			return;
		}
		for (@Pc(45) int i = 0; i < Culling.dynamicCullingClusterCount; i++) {
			@Pc(51) CullingCluster locOccluder = Culling.aClass82Array4[i];
			if (locOccluder.type == type && x == locOccluder.startX && z == locOccluder.startY || x == locOccluder.endX && z == locOccluder.startY) {
				if (Culling.dynamicCullingClusterCount != i) {
					Arrays.copy(Culling.aClass82Array4, i + 1, Culling.aClass82Array4, i, Culling.aClass82Array4.length - i - 1);
				}
				Culling.dynamicCullingClusterCount--;
				return;
			}
		}
	}

	@OriginalMember(owner = "client!wba", name = "O", descriptor = "[Lclient!ea;")
	public static CullingCluster[] aClass82Array4;
	@OriginalMember(owner = "client!qo", name = "pe", descriptor = "I")
	public static int fullTileDimensionY;

	@OriginalMember(owner = "client!vo", name = "a", descriptor = "(Lclient!ea;Lclient!ha;BI)V")
	public static void processCullingCluster(@OriginalArg(0) CullingCluster locOccluder, @OriginalArg(1) Toolkit toolkit, @OriginalArg(3) int plane) {
		@Pc(22) int local22;
		if (SceneDrawer.visibleBelowDrawheight != null && plane <= locOccluder.plane) {
			for (int index = 0; index < SceneDrawer.visibleBelowDrawheight.length; index++) {
				if (SceneDrawer.visibleBelowDrawheight[index] != -1000000 && (locOccluder.drawHeights[0] <= SceneDrawer.visibleBelowDrawheight[index] || locOccluder.drawHeights[1] <= SceneDrawer.visibleBelowDrawheight[index] || locOccluder.drawHeights[2] <= SceneDrawer.visibleBelowDrawheight[index] || SceneDrawer.visibleBelowDrawheight[index] >= locOccluder.drawHeights[3]) && (locOccluder.tileCullingsX[0] <= SceneDrawer.roofMaxX[index] || locOccluder.tileCullingsX[1] <= SceneDrawer.roofMaxX[index] || locOccluder.tileCullingsX[2] <= SceneDrawer.roofMaxX[index] || SceneDrawer.roofMaxX[index] >= locOccluder.tileCullingsX[3]) && (locOccluder.tileCullingsX[0] >= SceneDrawer.roofMinX[index] || SceneDrawer.roofMinX[index] <= locOccluder.tileCullingsX[1] || locOccluder.tileCullingsX[2] >= SceneDrawer.roofMinX[index] || SceneDrawer.roofMinX[index] <= locOccluder.tileCullingsX[3]) && (SceneDrawer.roofMaxZ[index] >= locOccluder.tileCullingsZ[0] || locOccluder.tileCullingsZ[1] <= SceneDrawer.roofMaxZ[index] || locOccluder.tileCullingsZ[2] <= SceneDrawer.roofMaxZ[index] || SceneDrawer.roofMaxZ[index] >= locOccluder.tileCullingsZ[3]) && (locOccluder.tileCullingsZ[0] >= SceneDrawer.roofMinZ[index] || locOccluder.tileCullingsZ[1] >= SceneDrawer.roofMinZ[index] || locOccluder.tileCullingsZ[2] >= SceneDrawer.roofMinZ[index] || locOccluder.tileCullingsZ[3] >= SceneDrawer.roofMinZ[index])) {
					return;
				}
			}
		}
		@Pc(233) int local233;
		@Pc(261) int local261;
		@Pc(282) boolean onScreen;
		@Pc(313) float cameraOffset;
		if (locOccluder.type == 1) {
			int tileX = SceneVisibleTiles.visibleTiles + locOccluder.startX - SceneDrawer.cameraGridPositionX;
			if (tileX >= 0 && tileX <= SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
				int startZ = SceneVisibleTiles.visibleTiles + locOccluder.startY - Scene.cameraTileZ;
				if (startZ < 0) {
					startZ = 0;
				} else if (startZ > SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
					return;
				}
				int endZ = locOccluder.endY + SceneVisibleTiles.visibleTiles - Scene.cameraTileZ;
				if (SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles < endZ) {
					endZ = SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles;
				} else if (endZ < 0) {
					return;
				}
				onScreen = false;
				while (startZ <= endZ) {
					if (Scene.tileInFrustum[tileX][startZ++]) {
						onScreen = true;
						break;
					}
				}
				if (onScreen) {
					cameraOffset = WorldMapDisplay.sceneCameraX - locOccluder.tileCullingsX[0];
					if (cameraOffset < 0.0F) {
						cameraOffset *= -1.0F;
					}
					if (!(cameraOffset < (float) Culling.occluderMinCameraDistance) && (CullingCluster.projectCorner(0, locOccluder) && (CullingCluster.projectCorner(1, locOccluder) && (CullingCluster.projectCorner(2, locOccluder) && CullingCluster.projectCorner(3, locOccluder))))) {
						Culling.members[Culling.activeOccluderCount++] = locOccluder;
					}
				}
			}
		} else if (locOccluder.type == 2) {
			int tileZ = SceneVisibleTiles.visibleTiles + locOccluder.startY - Scene.cameraTileZ;
			if (tileZ >= 0 && tileZ <= SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
				int startX = locOccluder.startX + SceneVisibleTiles.visibleTiles - SceneDrawer.cameraGridPositionX;
				if (startX < 0) {
					startX = 0;
				} else if (startX > SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
					return;
				}
				int endX = locOccluder.endX + SceneVisibleTiles.visibleTiles - SceneDrawer.cameraGridPositionX;
				if (SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles < endX) {
					endX = SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles;
				} else if (endX < 0) {
					return;
				}
				onScreen = false;
				while (endX >= startX) {
					if (Scene.tileInFrustum[startX++][tileZ]) {
						onScreen = true;
						break;
					}
				}
				if (onScreen) {
					cameraOffset = SceneCameraZ.sceneCameraZ - locOccluder.tileCullingsZ[0];
					if (cameraOffset < 0.0F) {
						cameraOffset *= -1.0F;
					}
					if (!(cameraOffset < (float) Culling.occluderMinCameraDistance) && (CullingCluster.projectCorner(0, locOccluder) && (CullingCluster.projectCorner(1, locOccluder) && (CullingCluster.projectCorner(2, locOccluder) && CullingCluster.projectCorner(3, locOccluder))))) {
						Culling.members[Culling.activeOccluderCount++] = locOccluder;
					}
				}
			}
		} else if (locOccluder.type == 16 || locOccluder.type == 8) {
			int tileX = SceneVisibleTiles.visibleTiles + locOccluder.startX - SceneDrawer.cameraGridPositionX;
			if (tileX >= 0 && tileX <= SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
				int tileZ = locOccluder.startY + SceneVisibleTiles.visibleTiles - Scene.cameraTileZ;
				if (tileZ >= 0 && tileZ <= SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles && Scene.tileInFrustum[tileX][tileZ]) {
					@Pc(569) float cameraOffsetX = (float) (WorldMapDisplay.sceneCameraX - locOccluder.tileCullingsX[0]);
					if (cameraOffsetX < 0.0F) {
						cameraOffsetX *= -1.0F;
					}
					@Pc(586) float cameraOffsetZ = (float) (SceneCameraZ.sceneCameraZ - locOccluder.tileCullingsZ[0]);
					if (cameraOffsetZ < 0.0F) {
						cameraOffsetZ *= -1.0F;
					}
					if ((!(cameraOffsetX < (float) Culling.occluderMinCameraDistance) || !((float) Culling.occluderMinCameraDistance > cameraOffsetZ)) && (CullingCluster.projectCorner(0, locOccluder) && (CullingCluster.projectCorner(1, locOccluder) && (CullingCluster.projectCorner(2, locOccluder) && CullingCluster.projectCorner(3, locOccluder))))) {
						Culling.members[Culling.activeOccluderCount++] = locOccluder;
					}
				}
			}
		} else if (locOccluder.type == 4) {
			@Pc(653) float cameraOffsetY = (float) (locOccluder.drawHeights[0] - Culling.cameraY);
			if (!(cameraOffsetY <= (float) Culling.fullTileDimensionY)) {
				int startZ = SceneVisibleTiles.visibleTiles + locOccluder.startY - Scene.cameraTileZ;
				if (startZ < 0) {
					startZ = 0;
				} else if (SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles < startZ) {
					return;
				}
				int endZ = SceneVisibleTiles.visibleTiles + locOccluder.endY - Scene.cameraTileZ;
				if (SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles < endZ) {
					endZ = SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles;
				} else if (endZ < 0) {
					return;
				}
				@Pc(716) int startX = locOccluder.startX + SceneVisibleTiles.visibleTiles - SceneDrawer.cameraGridPositionX;
				if (startX < 0) {
					startX = 0;
				} else if (startX > SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
					return;
				}
				@Pc(738) int endX = SceneVisibleTiles.visibleTiles + locOccluder.endX - SceneDrawer.cameraGridPositionX;
				if (SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles < endX) {
					endX = SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles;
				} else if (endX < 0) {
					return;
				}
				@Pc(758) boolean anyVisible = false;
				label283: for (@Pc(760) int i = startX; i <= endX; i++) {
					for (@Pc(764) int j = startZ; j <= endZ; j++) {
						if (Scene.tileInFrustum[i][j]) {
							anyVisible = true;
							break label283;
						}
					}
				}
				if (anyVisible && (CullingCluster.projectCorner(0, locOccluder) && (CullingCluster.projectCorner(1, locOccluder) && (CullingCluster.projectCorner(2, locOccluder) && CullingCluster.projectCorner(3, locOccluder))))) {
					Culling.members[Culling.activeOccluderCount++] = locOccluder;
				}
			}
		}
	}

	@OriginalMember(owner = "client!tia", name = "s", descriptor = "J")
	public static long occludeCalcNanos;

	@OriginalMember(owner = "client!va", name = "a", descriptor = "(B)V")
	public static void reset() {
		@Pc(5) int index;
		if (Culling.aClass82Array3 != null) {
			for (index = 0; index < Culling.groundCullingClusterCount; index++) {
				Culling.aClass82Array3[index] = null;
			}
			Culling.aClass82Array3 = null;
		}
		if (Culling.tileCullingCluster != null) {
			for (index = 0; index < Culling.tileCullingClusterCount; index++) {
				Culling.tileCullingCluster[index] = null;
			}
			Culling.tileCullingCluster = null;
		}
		if (Culling.aClass82Array4 != null) {
			for (index = 0; index < Culling.dynamicCullingClusterCount; index++) {
				Culling.aClass82Array4[index] = null;
			}
			Culling.aClass82Array4 = null;
		}
		Culling.tileDrawCycles = null;
		MipmapTextureSampler.textures = null;
		Culling.members = null;
		Culling.occlusionBufferHeight = -1;
		Culling.occlusionBufferWidth = -1;
	}

	@OriginalMember(owner = "client!sg", name = "U", descriptor = "I")
	public static int depthWriteCount = 0;

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(IIIIBIIII)V")
	public static void addGroundCullingCluster(@OriginalArg(0) int maxX, @OriginalArg(1) int heightNorth, @OriginalArg(2) int minZ, @OriginalArg(3) int heightSouth, @OriginalArg(6) int maxZ, @OriginalArg(7) int plane, @OriginalArg(8) int minX) {
		Culling.aClass82Array3[Culling.groundCullingClusterCount++] = new CullingCluster(4, plane, minX, maxX, maxX, minX, heightSouth, heightNorth, heightNorth, heightSouth, minZ, minZ, maxZ, maxZ);
	}

	@OriginalMember(owner = "client!vf", name = "a", descriptor = "(IIIIIBIIII)Z")
	public static boolean cullTriangle(@OriginalArg(0) int xC, @OriginalArg(1) int yA, @OriginalArg(2) int xB, @OriginalArg(3) int zC, @OriginalArg(4) int zB, @OriginalArg(6) int yB, @OriginalArg(7) int zA, @OriginalArg(8) int xA, @OriginalArg(9) int yC) {
		if (yA > 2000 || yB > 2000 || yC > 2000 || xA > 2000 || xB > 2000 || xC > 2000) {
			return false;
		} else if (yA >= -2000 && yB >= -2000 && yC >= -2000 && xA >= -2000 && xB >= -2000 && xC >= -2000) {
			@Pc(66) int local66;
			if (Culling.occlusionState == 2) {
				int bufferIndex = xA + yA * Culling.occlusionBufferWidth;
				if (bufferIndex >= 0 && MipmapTextureSampler.textures.length > bufferIndex && MipmapTextureSampler.textures[bufferIndex] > (zA << 8) - 38400) {
					return false;
				}
				bufferIndex = xB + yB * Culling.occlusionBufferWidth;
				if (bufferIndex >= 0 && bufferIndex < MipmapTextureSampler.textures.length && MipmapTextureSampler.textures[bufferIndex] > (zB << 8) - 38400) {
					return false;
				}
				bufferIndex = xC + yC * Culling.occlusionBufferWidth;
				if (bufferIndex >= 0 && MipmapTextureSampler.textures.length > bufferIndex && (zC << 8) - 38400 < MipmapTextureSampler.textures[bufferIndex]) {
					return false;
				}
			}
			int slopeXBa = xB - xA;
			@Pc(150) int slopeYBa = yB - yA;
			@Pc(155) int slopeXCa = xC - xA;
			@Pc(160) int slopeYCa = yC - yA;
			@Pc(165) int slopeZBa = zB - zA;
			if (yA < yB && yC > yC) {
				yA--;
				if (yB > yC) {
					yB++;
				} else {
					yC++;
				}
			} else if (yC <= yB) {
				yC--;
				if (yB < yA) {
					yA++;
				} else {
					yB++;
				}
			} else {
				yB--;
				if (yC < yA) {
					yA++;
				} else {
					yC++;
				}
			}
			@Pc(219) int slopeZCa = zC - zA;
			@Pc(221) int perspectiveBa = 0;
			if (yA != yB) {
				perspectiveBa = (xB - xA << 12) / (yB - yA);
			}
			@Pc(240) int perspectiveCb = 0;
			if (yC != yB) {
				perspectiveCb = (xC - xB << 12) / (yC - yB);
			}
			@Pc(266) int perspectiveAc = 0;
			if (yC != yA) {
				perspectiveAc = (xA - xC << 12) / (yA - yC);
			}
			@Pc(289) int size2d = slopeYCa * slopeXBa - slopeYBa * slopeXCa;
			if (size2d == 0) {
				return false;
			}
			@Pc(309) int depthGradientX = (slopeZBa * slopeYCa - slopeZCa * slopeYBa << 8) / size2d;
			@Pc(322) int depthGradientY = (slopeXBa * slopeZCa - slopeZBa * slopeXCa << 8) / size2d;
			if (yA <= yB && yA <= yC) {
				if (yA >= Culling.occlusionBufferHeight) {
					return true;
				}
				zA = (zA << 8) - (xA * depthGradientX - depthGradientX);
				if (yC > Culling.occlusionBufferHeight) {
					yC = Culling.occlusionBufferHeight;
				}
				if (yB > Culling.occlusionBufferHeight) {
					yB = Culling.occlusionBufferHeight;
				}
				if (yB < yC) {
					xC = xA <<= 0xC;
					if (yA < 0) {
						xA -= perspectiveBa * yA;
						zA -= yA * depthGradientY;
						xC -= yA * perspectiveAc;
						yA = 0;
					}
					xB <<= 0xC;
					if (yB < 0) {
						xB -= yB * perspectiveCb;
						yB = 0;
					}
					if (yB != yA && perspectiveAc < perspectiveBa || yB == yA && perspectiveAc > perspectiveCb) {
						yC -= yB;
						yB -= yA;
						yA *= Culling.occlusionBufferWidth;
						while (true) {
							yB--;
							if (yB < 0) {
								while (true) {
									yC--;
									if (yC < 0) {
										return true;
									}
									if (!Culling.processDepthScanline(depthGradientX, zA, (xB >> 12) + 1, (xC >> 12) + -1, MipmapTextureSampler.textures, yA)) {
										return false;
									}
									xB += perspectiveCb;
									xC += perspectiveAc;
									zA += depthGradientY;
									yA += Culling.occlusionBufferWidth;
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zA, (xA >> 12) + 1, (xC >> 12) + -1, MipmapTextureSampler.textures, yA)) {
								return false;
							}
							zA += depthGradientY;
							yA += Culling.occlusionBufferWidth;
							xA += perspectiveBa;
							xC += perspectiveAc;
						}
					} else {
						yC -= yB;
						yB -= yA;
						yA *= Culling.occlusionBufferWidth;
						while (true) {
							yB--;
							if (yB < 0) {
								while (true) {
									yC--;
									if (yC < 0) {
										return true;
									}
									if (!Culling.processDepthScanline(depthGradientX, zA, (xC >> 12) + 1, (xB >> 12) + -1, MipmapTextureSampler.textures, yA)) {
										return false;
									}
									xB += perspectiveCb;
									yA += Culling.occlusionBufferWidth;
									zA += depthGradientY;
									xC += perspectiveAc;
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zA, (xC >> 12) + 1, (xA >> 12) + -1, MipmapTextureSampler.textures, yA)) {
								return false;
							}
							yA += Culling.occlusionBufferWidth;
							zA += depthGradientY;
							xC += perspectiveAc;
							xA += perspectiveBa;
						}
					}
				} else {
					xB = xA <<= 0xC;
					if (yA < 0) {
						xB -= perspectiveAc * yA;
						zA -= yA * depthGradientY;
						xA -= perspectiveBa * yA;
						yA = 0;
					}
					xC <<= 0xC;
					if (yC < 0) {
						xC -= yC * perspectiveCb;
						yC = 0;
					}
					if ((yA == yC || perspectiveBa <= perspectiveAc) && (yA != yC || perspectiveBa >= perspectiveCb)) {
						yB -= yC;
						yC -= yA;
						yA = Culling.occlusionBufferWidth * yA;
						while (true) {
							yC--;
							if (yC < 0) {
								while (true) {
									yB--;
									if (yB < 0) {
										return true;
									}
									if (!Culling.processDepthScanline(depthGradientX, zA, (xC >> 12) + 1, (xA >> 12) + -1, MipmapTextureSampler.textures, yA)) {
										return false;
									}
									xA += perspectiveBa;
									zA += depthGradientY;
									xC += perspectiveCb;
									yA += Culling.occlusionBufferWidth;
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zA, (xB >> 12) + 1, (xA >> 12) + -1, MipmapTextureSampler.textures, yA)) {
								return false;
							}
							xB += perspectiveAc;
							xA += perspectiveBa;
							zA += depthGradientY;
							yA += Culling.occlusionBufferWidth;
						}
					} else {
						yB -= yC;
						yC -= yA;
						yA *= Culling.occlusionBufferWidth;
						while (true) {
							yC--;
							if (yC < 0) {
								while (true) {
									yB--;
									if (yB < 0) {
										return true;
									}
									if (!Culling.processDepthScanline(depthGradientX, zA, (xA >> 12) + 1, (xC >> 12) - 1, MipmapTextureSampler.textures, yA)) {
										return false;
									}
									yA += Culling.occlusionBufferWidth;
									xC += perspectiveCb;
									zA += depthGradientY;
									xA += perspectiveBa;
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zA, (xA >> 12) + 1, (xB >> 12) + -1, MipmapTextureSampler.textures, yA)) {
								return false;
							}
							yA += Culling.occlusionBufferWidth;
							xA += perspectiveBa;
							zA += depthGradientY;
							xB += perspectiveAc;
						}
					}
				}
			} else if (yC < yB) {
				if (yC >= Culling.occlusionBufferHeight) {
					return true;
				}
				zC = (zC << 8) + depthGradientX - depthGradientX * xC;
				if (yA > Culling.occlusionBufferHeight) {
					yA = Culling.occlusionBufferHeight;
				}
				if (Culling.occlusionBufferHeight < yB) {
					yB = Culling.occlusionBufferHeight;
				}
				if (yA < yB) {
					xB = xC <<= 0xC;
					xA <<= 0xC;
					if (yC < 0) {
						zC -= yC * depthGradientY;
						xC -= perspectiveAc * yC;
						xB -= yC * perspectiveCb;
						yC = 0;
					}
					if (yA < 0) {
						xA -= yA * perspectiveBa;
						yA = 0;
					}
					if (perspectiveAc > perspectiveCb) {
						yB -= yA;
						yA -= yC;
						yC *= Culling.occlusionBufferWidth;
						while (true) {
							yA--;
							if (yA < 0) {
								while (true) {
									yB--;
									if (yB < 0) {
										return true;
									}
									if (!Culling.processDepthScanline(depthGradientX, zC, (xA >> 12) + 1, (xB >> 12) + -1, MipmapTextureSampler.textures, yC)) {
										return false;
									}
									yC += Culling.occlusionBufferWidth;
									xA += perspectiveBa;
									zC += depthGradientY;
									xB += perspectiveCb;
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zC, (xC >> 12) + 1, (xB >> 12) - 1, MipmapTextureSampler.textures, yC)) {
								return false;
							}
							yC += Culling.occlusionBufferWidth;
							xC += perspectiveAc;
							xB += perspectiveCb;
							zC += depthGradientY;
						}
					} else {
						yB -= yA;
						yA -= yC;
						yC *= Culling.occlusionBufferWidth;
						while (true) {
							yA--;
							if (yA < 0) {
								while (true) {
									yB--;
									if (yB < 0) {
										return true;
									}
									if (!Culling.processDepthScanline(depthGradientX, zC, (xB >> 12) + 1, (xA >> 12) - 1, MipmapTextureSampler.textures, yC)) {
										return false;
									}
									zC += depthGradientY;
									xB += perspectiveCb;
									yC += Culling.occlusionBufferWidth;
									xA += perspectiveBa;
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zC, (xB >> 12) + 1, (xC >> 12) + -1, MipmapTextureSampler.textures, yC)) {
								return false;
							}
							zC += depthGradientY;
							xB += perspectiveCb;
							yC += Culling.occlusionBufferWidth;
							xC += perspectiveAc;
						}
					}
				} else {
					xA = xC <<= 0xC;
					if (yC < 0) {
						xA -= perspectiveCb * yC;
						xC -= perspectiveAc * yC;
						zC -= yC * depthGradientY;
						yC = 0;
					}
					xB <<= 0xC;
					if (yB < 0) {
						xB -= yB * perspectiveBa;
						yB = 0;
					}
					if (perspectiveAc <= perspectiveCb) {
						yA -= yB;
						yB -= yC;
						yC *= Culling.occlusionBufferWidth;
						while (true) {
							yB--;
							if (yB < 0) {
								while (true) {
									yA--;
									if (yA < 0) {
										return true;
									}
									if (!Culling.processDepthScanline(depthGradientX, zC, (xB >> 12) + 1, (xC >> 12) + -1, MipmapTextureSampler.textures, yC)) {
										return false;
									}
									xC += perspectiveAc;
									xB += perspectiveBa;
									zC += depthGradientY;
									yC += Culling.occlusionBufferWidth;
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zC, (xA >> 12) + 1, (xC >> 12) - 1, MipmapTextureSampler.textures, yC)) {
								return false;
							}
							zC += depthGradientY;
							yC += Culling.occlusionBufferWidth;
							xA += perspectiveCb;
							xC += perspectiveAc;
						}
					} else {
						yA -= yB;
						yB -= yC;
						yC = Culling.occlusionBufferWidth * yC;
						while (true) {
							yB--;
							if (yB < 0) {
								while (true) {
									yA--;
									if (yA < 0) {
										return true;
									}
									if (!Culling.processDepthScanline(depthGradientX, zC, (xC >> 12) + 1, (xB >> 12) + -1, MipmapTextureSampler.textures, yC)) {
										return false;
									}
									zC += depthGradientY;
									xB += perspectiveBa;
									yC += Culling.occlusionBufferWidth;
									xC += perspectiveAc;
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zC, (xC >> 12) + 1, (xA >> 12) + -1, MipmapTextureSampler.textures, yC)) {
								return false;
							}
							xC += perspectiveAc;
							yC += Culling.occlusionBufferWidth;
							xA += perspectiveCb;
							zC += depthGradientY;
						}
					}
				}
			} else if (yB >= Culling.occlusionBufferHeight) {
				return true;
			} else {
				zB = depthGradientX + (zB << 8) - xB * depthGradientX;
				if (Culling.occlusionBufferHeight < yC) {
					yC = Culling.occlusionBufferHeight;
				}
				if (Culling.occlusionBufferHeight < yA) {
					yA = Culling.occlusionBufferHeight;
				}
				if (yC >= yA) {
					xC = xB <<= 0xC;
					xA <<= 0xC;
					if (yB < 0) {
						xC -= perspectiveBa * yB;
						zB -= yB * depthGradientY;
						xB -= perspectiveCb * yB;
						yB = 0;
					}
					if (yA < 0) {
						xA -= perspectiveAc * yA;
						yA = 0;
					}
					if (perspectiveBa >= perspectiveCb) {
						yC -= yA;
						yA -= yB;
						yB = Culling.occlusionBufferWidth * yB;
						while (true) {
							yA--;
							if (yA < 0) {
								while (true) {
									yC--;
									if (yC < 0) {
										return true;
									}
									if (!Culling.processDepthScanline(depthGradientX, zB, (xA >> 12) + 1, (xB >> 12) + -1, MipmapTextureSampler.textures, yB)) {
										return false;
									}
									zB += depthGradientY;
									yB += Culling.occlusionBufferWidth;
									xB += perspectiveCb;
									xA += perspectiveAc;
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zB, (xC >> 12) + 1, (xB >> 12) - 1, MipmapTextureSampler.textures, yB)) {
								return false;
							}
							xB += perspectiveCb;
							zB += depthGradientY;
							yB += Culling.occlusionBufferWidth;
							xC += perspectiveBa;
						}
					} else {
						yC -= yA;
						yA -= yB;
						yB = Culling.occlusionBufferWidth * yB;
						while (true) {
							yA--;
							if (yA < 0) {
								while (true) {
									yC--;
									if (yC < 0) {
										return true;
									}
									if (!Culling.processDepthScanline(depthGradientX, zB, (xB >> 12) + 1, (xA >> 12) - 1, MipmapTextureSampler.textures, yB)) {
										return false;
									}
									xB += perspectiveCb;
									xA += perspectiveAc;
									zB += depthGradientY;
									yB += Culling.occlusionBufferWidth;
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zB, (xB >> 12) + 1, (xC >> 12) - 1, MipmapTextureSampler.textures, yB)) {
								return false;
							}
							yB += Culling.occlusionBufferWidth;
							xB += perspectiveCb;
							zB += depthGradientY;
							xC += perspectiveBa;
						}
					}
				} else {
					xA = xB <<= 0xC;
					xC <<= 0xC;
					if (yB < 0) {
						xA -= perspectiveBa * yB;
						xB -= yB * perspectiveCb;
						zB -= depthGradientY * yB;
						yB = 0;
					}
					if (yC < 0) {
						xC -= perspectiveAc * yC;
						yC = 0;
					}
					if (yB != yC && perspectiveCb > perspectiveBa || yB == yC && perspectiveBa > perspectiveAc) {
						yA -= yC;
						yC -= yB;
						yB = Culling.occlusionBufferWidth * yB;
						while (true) {
							yC--;
							if (yC < 0) {
								while (true) {
									yA--;
									if (yA < 0) {
										return true;
									}
									if (!Culling.processDepthScanline(depthGradientX, zB, (xC >> 12) + 1, (xA >> 12) + -1, MipmapTextureSampler.textures, yB)) {
										return false;
									}
									xA += perspectiveBa;
									xC += perspectiveAc;
									zB += depthGradientY;
									yB += Culling.occlusionBufferWidth;
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zB, (xB >> 12) + 1, (xA >> 12) + -1, MipmapTextureSampler.textures, yB)) {
								return false;
							}
							xA += perspectiveBa;
							zB += depthGradientY;
							yB += Culling.occlusionBufferWidth;
							xB += perspectiveCb;
						}
					} else {
						yA -= yC;
						yC -= yB;
						yB = Culling.occlusionBufferWidth * yB;
						while (true) {
							yC--;
							if (yC < 0) {
								while (true) {
									yA--;
									if (yA < 0) {
										return true;
									}
									if (!Culling.processDepthScanline(depthGradientX, zB, (xA >> 12) + 1, (xC >> 12) + -1, MipmapTextureSampler.textures, yB)) {
										return false;
									}
									zB += depthGradientY;
									yB += Culling.occlusionBufferWidth;
									xA += perspectiveBa;
									xC += perspectiveAc;
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zB, (xA >> 12) + 1, (xB >> 12) - 1, MipmapTextureSampler.textures, yB)) {
								return false;
							}
							xB += perspectiveCb;
							xA += perspectiveBa;
							yB += Culling.occlusionBufferWidth;
							zB += depthGradientY;
						}
					}
				}
			}
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!cc", name = "J", descriptor = "Z")
	public static boolean cullingEnabled = true;

	@OriginalMember(owner = "client!sm", name = "i", descriptor = "I")
	public static int cameraY;
	@OriginalMember(owner = "client!fca", name = "p", descriptor = "I")
	public static int groundCullingClusterCount;

	@OriginalMember(owner = "client!jk", name = "J", descriptor = "[Lclient!ea;")
	public static CullingCluster[] members;

	@OriginalMember(owner = "client!gb", name = "w", descriptor = "Z")
	public static boolean tileCullingClustersBuilt = false;

	@OriginalMember(owner = "client!lp", name = "a", descriptor = "(IIBI)Z")
	public static boolean isTileHidden(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(3) int z) {
		if (!Culling.cullingEnabled || !SceneCullingActive.cullingActive) {
			return false;
		} else if (Culling.depthWriteCount < 100) {
			return false;
		} else {
			@Pc(24) int cachedVisibility = Culling.tileDrawCycles[level][x][z];
			if (-Culling.cullingCycle == cachedVisibility) {
				return false;
			} else if (Culling.cullingCycle == cachedVisibility) {
				return true;
			} else if (Scene.aClass65Array3 == Scene.aClass65Array1) {
				return false;
			} else {
				@Pc(48) int absX = x << Scene.tileUnits;
				@Pc(52) int absZ = z << Scene.tileUnits;
				if (Culling.isTriangleOccluded(absX + 1, absX - (-Culling.fullTileDimension - -1), absZ + 1, absX + 1, absZ + Culling.fullTileDimension - 1, Scene.aClass65Array1[level].getHeight(z + 1, x), Scene.aClass65Array1[level].getHeight(z + 1, x + 1), Scene.aClass65Array1[level].getHeight(z, x), Culling.fullTileDimension + absZ - 1) && Culling.isTriangleOccluded(absX + 1, Culling.fullTileDimension + absX + -1, absZ + 1, Culling.fullTileDimension + (absX - 1), absZ + Culling.fullTileDimension - 1, Scene.aClass65Array1[level].getHeight(z - -1, x + 1), Scene.aClass65Array1[level].getHeight(z, x + 1), Scene.aClass65Array1[level].getHeight(z, x), absZ + 1)) {
					Culling.occludedGroundCount++;
					Culling.tileDrawCycles[level][x][z] = Culling.cullingCycle;
					return true;
				} else {
					Culling.tileDrawCycles[level][x][z] = -Culling.cullingCycle;
					return false;
				}
			}
		}
	}

	@OriginalMember(owner = "client!em", name = "a", descriptor = "(Z)V")
	public static void rebuildTileCullingClusters() {
		for (@Pc(3) int i = 0; i < Culling.tileCullingClusterCount; i++) {
			Culling.tileCullingCluster[i] = null;
		}
		Culling.tileCullingClusterCount = 0;
		@Pc(29) int plan;
		@Pc(33) int local33;
		for (@Pc(25) int j = 0; j < Scene.length; j++) {
			for (plan = 0; plan < Scene.sceneWidth; plan++) {
				for (int tileX = 0; tileX < Camera.sceneLength; tileX++) {
					@Pc(43) Tile tile = Scene.aClass100ArrayArrayArray3[j][tileX][plan];
					if (tile != null) {
						if (tile.cullHeightZ > 0) {
							tile.cullHeightZ = (short) (tile.cullHeightZ * -1);
						}
						if (tile.cullHeightX > 0) {
							tile.cullHeightX = (short) (tile.cullHeightX * -1);
						}
					}
				}
			}
		}
		for (plan = 0; plan < Scene.length; plan++) {
			for (int tileZ = 0; tileZ < Scene.sceneWidth; tileZ++) {
				for (@Pc(99) int k = 0; k < Camera.sceneLength; k++) {
					@Pc(109) Tile tile2 = Scene.aClass100ArrayArrayArray3[plan][k][tileZ];
					if (tile2 != null) {
						@Pc(132) boolean hasBridge = Scene.aClass100ArrayArrayArray3[0][k][tileZ] != null && Scene.aClass100ArrayArrayArray3[0][k][tileZ].bridge != null;
						@Pc(140) int runStart;
						@Pc(142) int runEnd;
						@Pc(156) Tile tile3;
						@Pc(164) int baseHeight;
						@Pc(319) int levelSpan;
						@Pc(333) int heightStart;
						@Pc(340) int topHeightStart;
						@Pc(356) int heightEnd;
						@Pc(364) int topHeightEnd;
						@Pc(368) int xEnd;
						@Pc(372) int xStart;
						@Pc(378) int local378;
						@Pc(416) int cullLevel;
						@Pc(420) int runIndex;
						if (tile2.cullHeightX < 0) {
							runStart = tileZ;
							runEnd = tileZ;
							tile3 = Scene.aClass100ArrayArrayArray3[plan][k][tileZ - 1];
							baseHeight = Scene.aClass65Array2[plan].getHeight(tileZ, k);
							while (runStart > 0 && tile3 != null && tile3.cullHeightX < 0 && tile2.cullHeightX == tile3.cullHeightX && tile2.cullOffsetX == tile3.cullOffsetX && baseHeight == Scene.aClass65Array2[plan].getHeight(runStart - 1, k) && (runStart - 1 <= 0 || baseHeight == Scene.aClass65Array2[plan].getHeight(runStart - 2, k))) {
								runStart--;
								tile3 = Scene.aClass100ArrayArrayArray3[plan][k][runStart - 1];
							}
							for (tile3 = Scene.aClass100ArrayArrayArray3[plan][k][tileZ + 1]; runEnd < Camera.sceneLength && tile3 != null && tile3.cullHeightX < 0 && tile2.cullHeightX == tile3.cullHeightX && tile2.cullOffsetX == tile3.cullOffsetX && Scene.aClass65Array2[plan].getHeight(runEnd + 1, k) == baseHeight && (runEnd + 1 >= Camera.sceneLength || baseHeight == Scene.aClass65Array2[plan].getHeight(runEnd + 2, k)); tile3 = Scene.aClass100ArrayArrayArray3[plan][k][runEnd + 1]) {
								runEnd++;
							}
							levelSpan = plan + 1 - plan;
							heightStart = Scene.aClass65Array2[hasBridge ? plan + 1 : plan].getHeight(runStart, k);
							topHeightStart = tile2.cullHeightX * levelSpan + heightStart;
							heightEnd = Scene.aClass65Array2[hasBridge ? plan + 1 : plan].getHeight(runEnd + 1, k);
							topHeightEnd = heightEnd + levelSpan * tile2.cullHeightX;
							xEnd = k << Scene.tileUnits;
							xStart = runStart << Scene.tileUnits;
							int zEnd = Culling.fullTileDimension + (runEnd << Scene.tileUnits);
							Culling.tileCullingCluster[Culling.tileCullingClusterCount++] = new CullingCluster(1, plan, tile2.cullOffsetX + xEnd, tile2.cullOffsetX + xEnd, tile2.cullOffsetX + xEnd, tile2.cullOffsetX + xEnd, heightStart, heightEnd, topHeightEnd, topHeightStart, xStart, zEnd, zEnd, xStart);
							for (cullLevel = plan; cullLevel <= plan; cullLevel++) {
								for (runIndex = runStart; runIndex <= runEnd; runIndex++) {
									Scene.aClass100ArrayArrayArray3[cullLevel][k][runIndex].cullHeightX = (short) (Scene.aClass100ArrayArrayArray3[cullLevel][k][runIndex].cullHeightX * -1);
								}
							}
						}
						if (tile2.cullHeightZ < 0) {
							runStart = k;
							runEnd = k;
							tile3 = Scene.aClass100ArrayArrayArray3[plan][k - 1][tileZ];
							baseHeight = Scene.aClass65Array2[plan].getHeight(tileZ, k);
							while (runStart > 0 && tile3 != null && tile3.cullHeightZ < 0 && tile3.cullHeightZ == tile2.cullHeightZ && tile2.cullOffsetZ == tile3.cullOffsetZ && Scene.aClass65Array2[plan].getHeight(tileZ, runStart - 1) == baseHeight && (runStart - 1 <= 0 || Scene.aClass65Array2[plan].getHeight(tileZ, runStart - 2) == baseHeight)) {
								runStart--;
								tile3 = Scene.aClass100ArrayArrayArray3[plan][runStart - 1][tileZ];
							}
							for (tile3 = Scene.aClass100ArrayArrayArray3[plan][k + 1][tileZ]; runEnd < Scene.sceneWidth && tile3 != null && tile3.cullHeightZ < 0 && tile3.cullHeightZ == tile2.cullHeightZ && tile3.cullOffsetZ == tile2.cullOffsetZ && Scene.aClass65Array2[plan].getHeight(tileZ, runEnd + 1) == baseHeight && (Scene.sceneWidth <= runEnd + 1 || Scene.aClass65Array2[plan].getHeight(tileZ, runEnd + 2) == baseHeight); tile3 = Scene.aClass100ArrayArrayArray3[plan][runEnd + 1][tileZ]) {
								runEnd++;
							}
							levelSpan = plan + 1 - plan;
							heightStart = Scene.aClass65Array2[hasBridge ? plan + 1 : plan].getHeight(tileZ, runStart);
							topHeightStart = heightStart + levelSpan * tile2.cullHeightZ;
							heightEnd = Scene.aClass65Array2[hasBridge ? plan + 1 : plan].getHeight(tileZ, runEnd + 1);
							topHeightEnd = tile2.cullHeightZ * levelSpan + heightEnd;
							xEnd = runStart << Scene.tileUnits;
							xStart = (runEnd << Scene.tileUnits) + Culling.fullTileDimension;
							int sceneZ = tileZ << Scene.tileUnits;
							Culling.tileCullingCluster[Culling.tileCullingClusterCount++] = new CullingCluster(2, plan, xEnd, xStart, xStart, xEnd, heightStart, heightEnd, topHeightEnd, topHeightStart, sceneZ + tile2.cullOffsetZ, sceneZ + tile2.cullOffsetZ, tile2.cullOffsetZ + sceneZ, sceneZ - -tile2.cullOffsetZ);
							for (cullLevel = plan; cullLevel <= plan; cullLevel++) {
								for (runIndex = runStart; runIndex <= runEnd; runIndex++) {
									Scene.aClass100ArrayArrayArray3[cullLevel][runIndex][tileZ].cullHeightZ = (short) (Scene.aClass100ArrayArrayArray3[cullLevel][runIndex][tileZ].cullHeightZ * -1);
								}
							}
						}
					}
				}
			}
		}
		Culling.tileCullingClustersBuilt = true;
	}

	@OriginalMember(owner = "client!eo", name = "e", descriptor = "[[[I")
	public static int[][][] tileDrawCycles;

	@OriginalMember(owner = "client!ks", name = "a", descriptor = "(IBIIII[II)Z")
	public static boolean processDepthScanline(@OriginalArg(0) int depthStep, @OriginalArg(2) int depth, @OriginalArg(4) int endX, @OriginalArg(5) int startX, @OriginalArg(6) int[] depthBuffer, @OriginalArg(7) int offset) {
		if (Culling.occlusionBufferWidth < endX) {
			endX = Culling.occlusionBufferWidth;
		}
		if (startX < 0) {
			startX = 0;
		}
		if (startX >= endX) {
			return true;
		}
		offset += startX - 1;
		depth += depthStep * startX;
		@Pc(50) int remaining = endX - startX >> 2;
		@Pc(64) int local64;
		@Pc(77) int nextDepth;
		@Pc(66) int index;
		if (Culling.occlusionState == 1) {
			Culling.depthWriteCount += remaining;
			while (true) {
				remaining--;
				if (remaining < 0) {
					remaining = endX - startX & 0x3;
					while (true) {
						remaining--;
						if (remaining < 0) {
							return true;
						}
						int invertedDepth = ~depth;
						offset++;
						if (invertedDepth > ~depthBuffer[offset]) {
							depthBuffer[offset] = depth;
						}
						depth += depthStep;
					}
				}
				int invertedDepth = ~depth;
				index = offset + 1;
				if (invertedDepth > ~depthBuffer[index]) {
					depthBuffer[index] = depth;
				}
				nextDepth = depth + depthStep;
				invertedDepth = ~nextDepth;
				index++;
				if (invertedDepth > ~depthBuffer[index]) {
					depthBuffer[index] = nextDepth;
				}
				nextDepth += depthStep;
				index++;
				if (nextDepth < depthBuffer[index]) {
					depthBuffer[index] = nextDepth;
				}
				nextDepth += depthStep;
				invertedDepth = ~nextDepth;
				offset = index + 1;
				if (invertedDepth > ~depthBuffer[offset]) {
					depthBuffer[offset] = nextDepth;
				}
				depth = nextDepth + depthStep;
			}
		} else {
			depth -= 38400;
			while (true) {
				remaining--;
				if (remaining < 0) {
					remaining = endX - startX & 0x3;
					while (true) {
						remaining--;
						if (remaining < 0) {
							return true;
						}
						offset++;
						if (depth < depthBuffer[offset]) {
							return false;
						}
						depth += depthStep;
					}
				}
				int invertedDepth = ~depth;
				index = offset + 1;
				if (invertedDepth > ~depthBuffer[index]) {
					return false;
				}
				nextDepth = depth + depthStep;
				index++;
				if (depthBuffer[index] > nextDepth) {
					return false;
				}
				nextDepth += depthStep;
				index++;
				if (nextDepth < depthBuffer[index]) {
					return false;
				}
				nextDepth += depthStep;
				offset = index + 1;
				if (depthBuffer[offset] > nextDepth) {
					return false;
				}
				depth = nextDepth + depthStep;
			}
		}
	}

	@OriginalMember(owner = "client!hl", name = "L", descriptor = "Lclient!bca;")
	public static Matrix aClass30_7;

	@OriginalMember(owner = "client!naa", name = "db", descriptor = "I")
	public static int occludedGroundCount;

	@OriginalMember(owner = "client!ek", name = "a", descriptor = "(IIILclient!qfa;B)Z")
	public static boolean hideWall(@OriginalArg(0) int tileZ, @OriginalArg(1) int tileX, @OriginalArg(2) int level, @OriginalArg(3) Wall wall) {
		if (!Culling.cullingEnabled || !SceneCullingActive.cullingActive) {
			return false;
		} else if (Culling.depthWriteCount < 100) {
			return false;
		} else if (Culling.isTileHidden(level, tileX, tileZ)) {
			@Pc(28) int x = tileX << Scene.tileUnits;
			@Pc(32) int z = tileZ << Scene.tileUnits;
			@Pc(47) int bottomY = Scene.aClass65Array1[level].getHeight(tileZ, tileX) - 1;
			@Pc(53) int topY = bottomY + wall.getModelMinY();
			if (wall.sideMask == 1) {
				if (!Culling.isTriangleOccluded(x, x, z, x, Culling.fullTileDimension + z, topY, topY, bottomY, z)) {
					return false;
				} else if (Culling.isTriangleOccluded(x, x, z, x, Culling.fullTileDimension + z, bottomY, topY, bottomY, z + Culling.fullTileDimension)) {
					OccludedWallCount.occludedWallCount++;
					return true;
				} else {
					return false;
				}
			} else if (wall.sideMask == 2) {
				if (!Culling.isTriangleOccluded(x, x + Culling.fullTileDimension, z - -Culling.fullTileDimension, x, Culling.fullTileDimension + z, topY, topY, bottomY, z + Culling.fullTileDimension)) {
					return false;
				} else if (Culling.isTriangleOccluded(x, Culling.fullTileDimension + x, z + Culling.fullTileDimension, Culling.fullTileDimension + x, Culling.fullTileDimension + z, topY, bottomY, bottomY, z + Culling.fullTileDimension)) {
					OccludedWallCount.occludedWallCount++;
					return true;
				} else {
					return false;
				}
			} else if (wall.sideMask == 4) {
				if (!Culling.isTriangleOccluded(x + Culling.fullTileDimension, Culling.fullTileDimension + x, z, Culling.fullTileDimension + x, z - -Culling.fullTileDimension, topY, topY, bottomY, z)) {
					return false;
				} else if (Culling.isTriangleOccluded(x + Culling.fullTileDimension, x - -Culling.fullTileDimension, z, Culling.fullTileDimension + x, z - -Culling.fullTileDimension, bottomY, topY, bottomY, Culling.fullTileDimension + z)) {
					OccludedWallCount.occludedWallCount++;
					return true;
				} else {
					return false;
				}
			} else if (wall.sideMask == 8) {
				if (!Culling.isTriangleOccluded(x, Culling.fullTileDimension + x, z, x, z, topY, topY, bottomY, z)) {
					return false;
				} else if (Culling.isTriangleOccluded(x, x + Culling.fullTileDimension, z, Culling.fullTileDimension + x, z, topY, bottomY, bottomY, z)) {
					OccludedWallCount.occludedWallCount++;
					return true;
				} else {
					return false;
				}
			} else if (wall.sideMask == 16) {
				if (Culling.isBoxOccluded(topY, Scene.halfTileOffset, Scene.halfTileOffset + z, x, bottomY, Scene.halfTileOffset)) {
					OccludedWallCount.occludedWallCount++;
					return true;
				} else {
					return false;
				}
			} else if (wall.sideMask == 32) {
				if (Culling.isBoxOccluded(topY, Scene.halfTileOffset, Scene.halfTileOffset + z, x + Scene.halfTileOffset, bottomY, Scene.halfTileOffset)) {
					OccludedWallCount.occludedWallCount++;
					return true;
				} else {
					return false;
				}
			} else if (wall.sideMask == 64) {
				if (Culling.isBoxOccluded(topY, Scene.halfTileOffset, z, Scene.halfTileOffset + x, bottomY, Scene.halfTileOffset)) {
					OccludedWallCount.occludedWallCount++;
					return true;
				} else {
					return false;
				}
			} else if (wall.sideMask != 128) {
				return true;
			} else if (Culling.isBoxOccluded(topY, Scene.halfTileOffset, z, x, bottomY, Scene.halfTileOffset)) {
				OccludedWallCount.occludedWallCount++;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	@OriginalMember(owner = "client!mw", name = "B", descriptor = "I")
	public static int occlusionScaleY;

	@OriginalMember(owner = "client!pw", name = "nb", descriptor = "[I")
	public static final int[] occlusionProjection = new int[3];

	@OriginalMember(owner = "client!bp", name = "a", descriptor = "(IIII)Z")
	public static boolean projectOcclusionPoint(@OriginalArg(0) int y, @OriginalArg(1) int z, @OriginalArg(2) int x) {
		Culling.aClass30_7.method4652(x, y, z, Culling.occlusionProjection);
		@Pc(13) int depth = Culling.occlusionProjection[2];
		if (depth < 50) {
			return false;
		} else {
			Culling.occlusionProjection[2] = depth;
			Culling.occlusionProjection[0] = Culling.occlusionScaleX * Culling.occlusionProjection[0] / depth + Culling.occlusionCenterX;
			Culling.occlusionProjection[1] = Culling.occlusionCenterY + Culling.occlusionProjection[1] * Culling.occlusionScaleY / depth;
			return true;
		}
	}

	@OriginalMember(owner = "client!uf", name = "Q", descriptor = "Z")
	public static final boolean BENCHMARK_CULLING = true;

	@OriginalMember(owner = "client!cfa", name = "N", descriptor = "[I")
	public static final int[] cullingViewportSize = new int[2];

	@OriginalMember(owner = "client!ei", name = "m", descriptor = "I")
	public static int occlusionState;

	@OriginalMember(owner = "client!us", name = "c", descriptor = "I")
	public static int fullTileDimension;

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(IIIIIIIIIZ)Z")
	public static boolean isTriangleOccluded(@OriginalArg(0) int x0, @OriginalArg(1) int x1, @OriginalArg(2) int z0, @OriginalArg(3) int x2, @OriginalArg(4) int z2, @OriginalArg(5) int y2, @OriginalArg(6) int y1, @OriginalArg(7) int y0, @OriginalArg(8) int z1) {
		if (!Culling.projectOcclusionPoint(y0, z0, x0)) {
			return false;
		}
		@Pc(15) int screenY0 = Culling.occlusionProjection[1];
		@Pc(19) int screenX0 = Culling.occlusionProjection[0];
		@Pc(23) int depth0 = Culling.occlusionProjection[2];
		if (!Culling.projectOcclusionPoint(y1, z1, x1)) {
			return false;
		}
		@Pc(35) int screenX1 = Culling.occlusionProjection[0];
		@Pc(39) int depth1 = Culling.occlusionProjection[2];
		@Pc(43) int screenY1 = Culling.occlusionProjection[1];
		if (Culling.projectOcclusionPoint(y2, z2, x2)) {
			@Pc(55) int depth2 = Culling.occlusionProjection[2];
			@Pc(64) int screenY2 = Culling.occlusionProjection[1];
			@Pc(68) int screenX2 = Culling.occlusionProjection[0];
			return Culling.cullTriangle(screenX2, screenY0, screenX1, depth2, depth1, screenY1, depth0, screenX0, screenY2);
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!kea", name = "l", descriptor = "I")
	public static int occlusionCenterX;

	@OriginalMember(owner = "client!wb", name = "y", descriptor = "I")
	public static int dynamicCullingClusterCount;

	@OriginalMember(owner = "client!rq", name = "M", descriptor = "J")
	public static long occludeCalcStartNanos;

	@OriginalMember(owner = "client!td", name = "N", descriptor = "I")
	public static int activeOccluderCount = 0;

	@OriginalMember(owner = "client!so", name = "n", descriptor = "I")
	public static int cullingCycle;

	@OriginalMember(owner = "client!ua", name = "Qb", descriptor = "I")
	public static int occlusionCenterY;

	@OriginalMember(owner = "client!dfa", name = "a", descriptor = "(IBLclient!ha;)V")
	public static void calculateOcclusion(@OriginalArg(0) int lowestRenderedPlane, @OriginalArg(2) Toolkit toolkit) {
		if (!Culling.cullingEnabled || !SceneCullingActive.cullingActive) {
			Culling.activeOccluderCount = 0;
			return;
		}
		if (Culling.BENCHMARK_CULLING) {
			Culling.occludeCalcStartNanos = Scene.aClass158_1.nanoTime();
		}
		OccludedWallCount.occludedWallCount = 0;
		Culling.occludedGroundCount = 0;
		Sprites.hintHeadId = 0;
		@Pc(27) int[] viewport = toolkit.Y();
		Culling.occlusionScaleY = (int) ((float) viewport[3] / 3.0F);
		Culling.occlusionScaleX = (int) ((float) viewport[2] / 3.0F);
		toolkit.accumulateViewportDimensions(Culling.cullingViewportSize);
		if (Culling.occlusionBufferWidth != (int) ((float) Culling.cullingViewportSize[0] / 3.0F) || (int) ((float) Culling.cullingViewportSize[1] / 3.0F) != Culling.occlusionBufferHeight) {
			Culling.occlusionBufferHeight = (int) ((float) Culling.cullingViewportSize[1] / 3.0F);
			Culling.occlusionBufferWidth = (int) ((float) Culling.cullingViewportSize[0] / 3.0F);
			Culling.occlusionCenterY = Culling.occlusionBufferHeight >> 1;
			MipmapTextureSampler.textures = new int[Culling.occlusionBufferHeight * Culling.occlusionBufferWidth];
			Culling.occlusionCenterX = Culling.occlusionBufferWidth >> 1;
		}
		Culling.aClass30_7 = toolkit.camera();
		Culling.activeOccluderCount = 0;
		for (@Pc(104) int i = 0; i < Culling.groundCullingClusterCount; i++) {
			Culling.processCullingCluster(Culling.aClass82Array3[i], toolkit, lowestRenderedPlane);
		}
		for (@Pc(130) int j = 0; j < Culling.tileCullingClusterCount; j++) {
			Culling.processCullingCluster(Culling.tileCullingCluster[j], toolkit, lowestRenderedPlane);
		}
		for (@Pc(149) int k = 0; k < Culling.dynamicCullingClusterCount; k++) {
			Culling.processCullingCluster(Culling.aClass82Array4[k], toolkit, lowestRenderedPlane);
		}
		Culling.depthWriteCount = 0;
		if (Culling.activeOccluderCount > 0) {
			@Pc(169) int length = MipmapTextureSampler.textures.length;
			@Pc(176) int unrolledEnd = length - length & 0x7;
			@Pc(178) int index = 0;
			while (unrolledEnd > index) {
				MipmapTextureSampler.textures[index++] = Integer.MAX_VALUE;
				MipmapTextureSampler.textures[index++] = Integer.MAX_VALUE;
				MipmapTextureSampler.textures[index++] = Integer.MAX_VALUE;
				MipmapTextureSampler.textures[index++] = Integer.MAX_VALUE;
				MipmapTextureSampler.textures[index++] = Integer.MAX_VALUE;
				MipmapTextureSampler.textures[index++] = Integer.MAX_VALUE;
				MipmapTextureSampler.textures[index++] = Integer.MAX_VALUE;
				MipmapTextureSampler.textures[index++] = Integer.MAX_VALUE;
			}
			while (index < length) {
				MipmapTextureSampler.textures[index++] = Integer.MAX_VALUE;
			}
			Culling.occlusionState = 1;
			for (@Pc(245) int m = 0; m < Culling.activeOccluderCount; m++) {
				@Pc(251) CullingCluster locOccluder = Culling.members[m];
				Culling.cullTriangle(locOccluder.projectionZ[3], locOccluder.projectionY[0], locOccluder.projectionZ[1], locOccluder.projectionX[3], locOccluder.projectionX[1], locOccluder.projectionY[1], locOccluder.projectionX[0], locOccluder.projectionZ[0], locOccluder.projectionY[3]);
				Culling.cullTriangle(locOccluder.projectionZ[3], locOccluder.projectionY[1], locOccluder.projectionZ[2], locOccluder.projectionX[3], locOccluder.projectionX[2], locOccluder.projectionY[2], locOccluder.projectionX[1], locOccluder.projectionZ[1], locOccluder.projectionY[3]);
			}
			Culling.occlusionState = 2;
		}
		if (Culling.BENCHMARK_CULLING) {
			Culling.occludeCalcNanos = Scene.aClass158_1.nanoTime() - Culling.occludeCalcStartNanos;
		}
	}

	@OriginalMember(owner = "client!pu", name = "h", descriptor = "[I")
	public static final int[] occluderDebugCorner0 = new int[3];

	@OriginalMember(owner = "client!lea", name = "d", descriptor = "I")
	public static int occlusionBufferHeight = -1;

	@OriginalMember(owner = "client!ng", name = "a", descriptor = "(IIIIIII)Z")
	public static boolean isBoxOccluded(@OriginalArg(0) int sizeY, @OriginalArg(1) int sizeX, @OriginalArg(2) int z, @OriginalArg(3) int x, @OriginalArg(4) int y, @OriginalArg(5) int sizeZ) {
		@Pc(8) int maxX = x + sizeX;
		@Pc(12) int maxY = sizeY + y;
		@Pc(16) int maxZ = z + sizeZ;
		if (!Culling.isTriangleOccluded(x, maxX, z, x, maxZ, maxY, maxY, maxY, maxZ)) {
			return false;
		} else if (Culling.isTriangleOccluded(x, maxX, z, maxX, maxZ, maxY, maxY, maxY, z)) {
			if (WorldMapDisplay.sceneCameraX > x) {
				if (!Culling.isTriangleOccluded(x, x, maxZ, x, maxZ, maxY, maxY, y, z)) {
					return false;
				}
				if (!Culling.isTriangleOccluded(x, x, maxZ, x, z, maxY, y, y, z)) {
					return false;
				}
			} else if (!Culling.isTriangleOccluded(maxX, maxX, maxZ, maxX, maxZ, maxY, maxY, y, z)) {
				return false;
			} else if (!Culling.isTriangleOccluded(maxX, maxX, maxZ, maxX, z, maxY, y, y, z)) {
				return false;
			}
			if (z >= SceneCameraZ.sceneCameraZ) {
				if (!Culling.isTriangleOccluded(x, maxX, maxZ, x, maxZ, maxY, maxY, y, maxZ)) {
					return false;
				}
				if (!Culling.isTriangleOccluded(x, maxX, maxZ, maxX, maxZ, maxY, y, y, maxZ)) {
					return false;
				}
			} else if (!Culling.isTriangleOccluded(x, maxX, z, x, z, maxY, maxY, y, z)) {
				return false;
			} else if (!Culling.isTriangleOccluded(x, maxX, z, maxX, z, maxY, y, y, z)) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!ws", name = "a", descriptor = "I")
	public static int occlusionBufferWidth = -1;

	@OriginalMember(owner = "client!am", name = "d", descriptor = "[I")
	public static final int[] occluderDebugCorner1 = new int[3];

	@OriginalMember(owner = "client!hba", name = "b", descriptor = "I")
	public static int occluderMinCameraDistance;

	@OriginalMember(owner = "client!me", name = "c", descriptor = "[I")
	public static final int[] occluderDebugCorner3 = new int[3];

	@OriginalMember(owner = "client!oc", name = "g", descriptor = "I")
	public static int occlusionScaleX;

	@OriginalMember(owner = "client!rl", name = "a", descriptor = "(I)V")
	public static void rebuildCullingClusters() {
		Culling.rebuildTileCullingClusters();
	}

	@OriginalMember(owner = "client!te", name = "a", descriptor = "[I")
	public static final int[] occluderDebugCorner2 = new int[3];

	@OriginalMember(owner = "client!te", name = "a", descriptor = "()V")
	public static void drawOcclusionDebug() {
		if (W2DebugMode.sceneDebugMode == 0 || W2DebugMode.font == null) {
			return;
		}
		Scene.toolkit.K(SceneDrawer.clipBounds);
		for (@Pc(12) int i = 0; i < ScriptRunner.threadSplitX.length; i++) {
			Scene.toolkit.verticalLine(ScriptRunner.threadSplitX[i] + SceneDrawer.threadSplitOffsets[i], -256, SceneDrawer.clipBounds[3] - SceneDrawer.clipBounds[1], SceneDrawer.clipBounds[1]);
		}
		for (@Pc(41) int j = 0; j < Culling.activeOccluderCount; j++) {
			@Pc(46) CullingCluster locOccluder = Culling.members[j];
			Scene.toolkit.H(locOccluder.tileCullingsX[0], locOccluder.drawHeights[0], locOccluder.tileCullingsZ[0], Culling.occluderDebugCorner0);
			Scene.toolkit.H(locOccluder.tileCullingsX[1], locOccluder.drawHeights[1], locOccluder.tileCullingsZ[1], Culling.occluderDebugCorner1);
			Scene.toolkit.H(locOccluder.tileCullingsX[2], locOccluder.drawHeights[2], locOccluder.tileCullingsZ[2], occluderDebugCorner2);
			Scene.toolkit.H(locOccluder.tileCullingsX[3], locOccluder.drawHeights[3], locOccluder.tileCullingsZ[3], Culling.occluderDebugCorner3);
			if (Culling.occluderDebugCorner0[2] != -1 && Culling.occluderDebugCorner1[2] != -1 && occluderDebugCorner2[2] != -1 && Culling.occluderDebugCorner3[2] != -1) {
				@Pc(128) int colour = -65536;
				if (locOccluder.type == 4) {
					colour = -16776961;
				}
				Scene.toolkit.drawOpaqueLine(Culling.occluderDebugCorner0[1], Culling.occluderDebugCorner0[0], Culling.occluderDebugCorner1[0], colour, Culling.occluderDebugCorner1[1]);
				Scene.toolkit.drawOpaqueLine(Culling.occluderDebugCorner1[1], Culling.occluderDebugCorner1[0], occluderDebugCorner2[0], colour, occluderDebugCorner2[1]);
				Scene.toolkit.drawOpaqueLine(occluderDebugCorner2[1], occluderDebugCorner2[0], Culling.occluderDebugCorner3[0], colour, Culling.occluderDebugCorner3[1]);
				Scene.toolkit.drawOpaqueLine(Culling.occluderDebugCorner3[1], Culling.occluderDebugCorner3[0], Culling.occluderDebugCorner0[0], colour, Culling.occluderDebugCorner0[1]);
				Scene.toolkit.drawOpaqueLine(Culling.occluderDebugCorner0[1], Culling.occluderDebugCorner0[0], occluderDebugCorner2[0], colour, occluderDebugCorner2[1]);
			}
		}
		W2DebugMode.font.drawString("Dynamic: " + Scene.interactiveObjectCount + "/" + 5000, -256, 75, 10, -16777216);
		W2DebugMode.font.drawString("Total Opaque Onscreen: " + Scene.onscreenOpaqueCount + "/" + 10000, -256, 90, 10, -16777216);
		W2DebugMode.font.drawString("Total Trans Onscreen: " + Scene.transparentEntityCount + "/" + 5000, -256, 105, 10, -16777216);
		W2DebugMode.font.drawString("Occluders: " + (Culling.groundCullingClusterCount + Culling.tileCullingClusterCount) + " Active: " + Culling.activeOccluderCount, -256, 120, 10, -16777216);
		W2DebugMode.font.drawString("Occluded: Ground:" + Culling.occludedGroundCount + " Walls: " + OccludedWallCount.occludedWallCount + " CPs: " + Sprites.hintHeadId + " Pixels: " + Culling.depthWriteCount, -256, 135, 10, -16777216);
		W2DebugMode.font.drawString("Occlude Calc Took: " + Culling.occludeCalcNanos / 1000L + "us", -256, 150, 10, -16777216);
		if (W2DebugMode.sceneDebugMode != 2 || MipmapTextureSampler.textures == null) {
			return;
		}
		for (@Pc(355) int k = 0; k < MipmapTextureSampler.textures.length; k++) {
			@Pc(361) float intensity = (float) MipmapTextureSampler.textures[k];
			intensity /= 4194304.0F;
			if (intensity > 1.0F) {
				intensity = 1.0F;
			}
			intensity *= 255.0F;
			intensity = 255.0F - intensity;
			@Pc(382) int grey = (int) intensity;
			MipmapTextureSampler.textures[k] = grey | grey << 8 | grey << 16 | 0xFF000000;
		}
		@Pc(410) NativeSprite sprite = Scene.toolkit.createNativeSprite(Culling.occlusionBufferWidth, MipmapTextureSampler.textures, Culling.occlusionBufferWidth, Culling.occlusionBufferHeight);
		sprite.render(10, 170, 1, 0, 0);
	}
}
