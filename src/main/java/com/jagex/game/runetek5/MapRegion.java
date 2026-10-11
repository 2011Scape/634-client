package com.jagex.game.runetek5;

import com.jagex.game.runetek5.core.constants.LocLayer;
import com.jagex.game.runetek5.core.constants.LocShapes;
import com.jagex.game.runetek5.core.constants.TileFlag;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.entity.gameobject.GameObject;
import com.jagex.game.runetek5.scene.CollisionMap;
import com.jagex.game.runetek5.config.loctype.LocOcclusionMode;
import com.jagex.game.runetek5.graphics.light.StaticPointLight;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.statics.ZoneLocRotation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity;
import com.jagex.game.runetek5.entity.DynamicGroundDecor;
import com.jagex.game.runetek5.entity.DynamicLocation;
import com.jagex.game.runetek5.entity.DynamicWall;
import com.jagex.game.runetek5.entity.DynamicWallDecor;
import com.jagex.game.runetek5.entity.GroundDecor;
import com.jagex.game.runetek5.entity.StaticGroundDecor;
import com.jagex.game.runetek5.entity.StaticLocation;
import com.jagex.game.runetek5.entity.StaticWall;
import com.jagex.game.runetek5.entity.StaticWallDecor;
import com.jagex.game.runetek5.entity.Wall;
import com.jagex.game.runetek5.entity.WallDecor;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.scene.SettingsBits;
import com.jagex.game.runetek5.config.light.LightType;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.scene.environment.Environment;
import com.jagex.game.runetek5.scene.environment.EnvironmentManager;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.entity.InteractiveEntityContainer;
import com.jagex.game.runetek5.scene.culling.Culling;
import com.jagex.game.runetek5.scene.tile.Tile;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer;
import com.jagex.game.runetek5.config.loctype.LocInteractivity;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.sound.player.area.AmbientSound;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.camera.CameraMode;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;

@OriginalClass("client!vaa")
public final class MapRegion extends Terrain {

	@OriginalMember(owner = "client!vaa", name = "N", descriptor = "I")
	public int minLevel = 99;

	@OriginalMember(owner = "client!vaa", name = "<init>", descriptor = "(IIIZ)V")
	public MapRegion(@OriginalArg(0) int levels, @OriginalArg(1) int regionSizeX, @OriginalArg(2) int regionSizeZ, @OriginalArg(3) boolean underwater) {
		super(levels, regionSizeX, regionSizeZ, underwater, TypeLists.aClass49_3, TypeLists.FLU);
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(IIILclient!ha;IIIIIILclient!fe;)V")
	public void addObject(@OriginalArg(0) int x, @OriginalArg(1) int minLevel, @OriginalArg(2) int locId, @OriginalArg(3) Toolkit toolkit, @OriginalArg(4) int plane, @OriginalArg(6) int shape, @OriginalArg(7) int z, @OriginalArg(8) int sequenceId, @OriginalArg(9) int rotation, @OriginalArg(10) CollisionMap collisionMap) {
		if (GamePreferences.uid.animateBackgroundOverride.getValue() == 0 && !SettingsBits.isVisibleFromLevel(z, x, MapRegion.mapBuildLevel, plane)) {
			return;
		}
		if (minLevel < this.minLevel) {
			this.minLevel = minLevel;
		}
		@Pc(36) LocType locType = TypeLists.LOC.list(locId);
		if (GamePreferences.uid.textures.getValue() == 0 && locType.requiresTextures) {
			return;
		}
		@Pc(64) int locSizeX;
		@Pc(61) int locSizeZ;
		if (rotation == 1 || rotation == 3) {
			locSizeZ = locType.sizeX;
			locSizeX = locType.sizeY;
		} else {
			locSizeZ = locType.sizeY;
			locSizeX = locType.sizeX;
		}
		@Pc(100) int x1;
		@Pc(108) int x2;
		if (super.regionSizeX >= x + locSizeX) {
			x1 = (locSizeX >> 1) + x;
			x2 = x + (locSizeX + 1 >> 1);
		} else {
			x1 = x;
			x2 = x + 1;
		}
		@Pc(128) int z1;
		@Pc(126) int z2;
		if (super.regionSizeZ < locSizeZ + z) {
			z2 = z + 1;
			z1 = z;
		} else {
			z2 = (locSizeZ + 1 >> 1) + z;
			z1 = (locSizeZ >> 1) + z;
		}
		@Pc(148) Ground ground = Scene.aClass65Array1[plane];
		@Pc(175) int averageHeight = ground.getHeight(z1, x1) + ground.getHeight(z1, x2) + ground.getHeight(z2, x1) + ground.getHeight(z2, x2) >> 2;
		@Pc(184) int sceneX = (x << 9) + (locSizeX << 8);
		@Pc(192) int sceneZ = (locSizeZ << 8) + (z << 9);
		@Pc(204) boolean applyShading = MapRegion.lowDetailShadows && !super.underwater && locType.delayShading;
		if (locType.hasSounds()) {
			AmbientSound.createAmbientSoundEffect(null, minLevel, z, x, null, locType, rotation);
		}
		@Pc(238) boolean stationary = sequenceId == -1 && locType.animationId == -1 && locType.animations == null && locType.alternativeIds == null && !locType.hasAnimation;
		if (MapRegion.SKIP_OCCLUDER_LOCS && (LocShapes.isWallShape(shape) && locType.occlusionMode != LocOcclusionMode.ALL || !(!LocShapes.isRoof(shape) || locType.occlusionMode != 0))) {
			return;
		}
		if (shape != 22) {
			@Pc(389) AbstractInteractiveEntity positionEntity;
			@Pc(357) StaticLocation staticLocation;
			@Pc(361) int local361;
			@Pc(455) int local455;
			if (shape == LocShapes.CENTREPIECE_STRAIGHT || shape == LocShapes.CENTREPIECE_DIAGONAL) {
				staticLocation = null;
				int shadowIntensity;
				if (stationary) {
					@Pc(419) StaticLocation staticLocation2 = new StaticLocation(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, x, locSizeX + x - 1, z, locSizeZ + z - 1, shape, rotation, applyShading);
					positionEntity = staticLocation2;
					staticLocation = staticLocation2;
					shadowIntensity = staticLocation2.shadowValue();
				} else {
					shadowIntensity = 15;
					positionEntity = new DynamicLocation(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, x, locSizeX + x - 1, z, z + locSizeZ - 1, shape, rotation, sequenceId);
				}
				if (Scene.addInteractive(positionEntity, false)) {
					if (staticLocation != null && staticLocation.hardShadow()) {
						staticLocation.addShadow(toolkit);
					}
					if (locType.staticShadow && MapRegion.lowDetailShadows) {
						if (shadowIntensity > 30) {
							shadowIntensity = 30;
						}
						for (int tileOffsetX = 0; tileOffsetX <= locSizeX; tileOffsetX++) {
							for (@Pc(459) int i = 0; i <= locSizeZ; i++) {
								ground.ka(tileOffsetX + x, z + i, shadowIntensity);
							}
						}
					}
				}
				if (locType.blocksMovement != 0 && collisionMap != null) {
					collisionMap.flagBlockedObject(!locType.ignoreClipOnAltRoute, locSizeX, z, x, locType.blocksProjectiles, locSizeZ);
				}
			} else if ((shape < 12 || shape > 17) && (shape < 18 || shape > 21)) {
				@Pc(701) Wall wall;
				if (shape == LocShapes.WALL_STRAIGHT) {
					@Pc(674) int occlusionMode = locType.occlusionMode;
					if (MapRegion.OCCLUDE_UNSPECIFIED_LOCS && locType.occlusionMode == -1) {
						occlusionMode = LocOcclusionMode.ALL;
					}
					if (stationary) {
						@Pc(719) StaticWall staticWall = new StaticWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, shape, rotation, applyShading);
						if (staticWall.hardShadow()) {
							staticWall.addShadow(toolkit);
						}
						wall = staticWall;
					} else {
						wall = new DynamicWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, shape, rotation, sequenceId);
					}
					Scene.addWall(minLevel, x, z, wall, null);
					if (rotation == 0) {
						if (MapRegion.lowDetailShadows && locType.staticShadow) {
							ground.ka(x, z, 50);
							ground.ka(x, z + 1, 50);
						}
						if (occlusionMode == LocOcclusionMode.ALL && !super.underwater) {
							Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z, minLevel, 1);
						}
					} else if (rotation == 1) {
						if (MapRegion.lowDetailShadows && locType.staticShadow) {
							ground.ka(x, z + 1, 50);
							ground.ka(x + 1, z + 1, 50);
						}
						if (occlusionMode == LocOcclusionMode.ALL && !super.underwater) {
							Culling.addWallCulling(x, -locType.cullXZ, locType.cullY, z + 1, minLevel, 2);
						}
					} else if (rotation == 2) {
						if (MapRegion.lowDetailShadows && locType.staticShadow) {
							ground.ka(x + 1, z, 50);
							ground.ka(x + 1, z + 1, 50);
						}
						if (occlusionMode == LocOcclusionMode.ALL && !super.underwater) {
							Culling.addWallCulling(x + 1, -locType.cullXZ, locType.cullY, z, minLevel, 1);
						}
					} else if (rotation == 3) {
						if (MapRegion.lowDetailShadows && locType.staticShadow) {
							ground.ka(x, z, 50);
							ground.ka(x + 1, z, 50);
						}
						if (occlusionMode == LocOcclusionMode.ALL && !super.underwater) {
							Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z, minLevel, 2);
						}
					}
					if (locType.blocksMovement != 0 && collisionMap != null) {
						collisionMap.flagBoundary(z, x, shape, rotation, !locType.ignoreClipOnAltRoute, locType.blocksProjectiles);
					}
					if (locType.decorDisplacement != 64) {
						Scene.offsetWallDecoration(minLevel, x, z, locType.decorDisplacement);
					}
				} else {
					@Pc(963) Wall wall2;
					@Pc(981) StaticWall staticWall2;
					if (shape == LocShapes.WALL_DIAGONALCORNER) {
						if (stationary) {
							staticWall2 = new StaticWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, shape, rotation, applyShading);
							if (staticWall2.hardShadow()) {
								staticWall2.addShadow(toolkit);
							}
							wall2 = staticWall2;
						} else {
							wall2 = new DynamicWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, shape, rotation, sequenceId);
						}
						Scene.addWall(minLevel, x, z, wall2, null);
						if (locType.staticShadow && MapRegion.lowDetailShadows) {
							if (rotation == 0) {
								ground.ka(x, z + 1, 50);
							} else if (rotation == 1) {
								ground.ka(x + 1, z + 1, 50);
							} else if (rotation == 2) {
								ground.ka(x + 1, z, 50);
							} else if (rotation == 3) {
								ground.ka(x, z, 50);
							}
						}
						if (locType.blocksMovement != 0 && collisionMap != null) {
							collisionMap.flagBoundary(z, x, shape, rotation, !locType.ignoreClipOnAltRoute, locType.blocksProjectiles);
						}
					} else if (shape == 2) {
						int nextRotation = rotation + 1 & 0x3;
						if (stationary) {
							@Pc(1104) StaticWall staticWall3 = new StaticWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, shape, rotation + 4, applyShading);
							@Pc(1120) StaticWall staticWall4 = new StaticWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, shape, nextRotation, applyShading);
							if (staticWall3.hardShadow()) {
								staticWall3.addShadow(toolkit);
							}
							wall = staticWall4;
							wall2 = staticWall3;
							if (staticWall4.hardShadow()) {
								staticWall4.addShadow(toolkit);
							}
						} else {
							wall2 = new DynamicWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, shape, rotation + 4, sequenceId);
							wall = new DynamicWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, shape, nextRotation, sequenceId);
						}
						Scene.addWall(minLevel, x, z, wall2, wall);
						if ((locType.occlusionMode == 1 || MapRegion.OCCLUDE_UNSPECIFIED_LOCS && locType.occlusionMode == -1) && !super.underwater) {
							if (rotation == 0) {
								Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z, minLevel, 1);
								Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z + 1, minLevel, 2);
							} else if (rotation == 1) {
								Culling.addWallCulling(x + 1, locType.cullXZ, locType.cullY, z, minLevel, 1);
								Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z + 1, minLevel, 2);
							} else if (rotation == 2) {
								Culling.addWallCulling(x + 1, locType.cullXZ, locType.cullY, z, minLevel, 1);
								Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z, minLevel, 2);
							} else if (rotation == 3) {
								Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z, minLevel, 1);
								Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z, minLevel, 2);
							}
						}
						if (locType.blocksMovement != 0 && collisionMap != null) {
							collisionMap.flagBoundary(z, x, shape, rotation, !locType.ignoreClipOnAltRoute, locType.blocksProjectiles);
						}
						if (locType.decorDisplacement != 64) {
							Scene.offsetWallDecoration(minLevel, x, z, locType.decorDisplacement);
						}
					} else if (shape == 3) {
						if (stationary) {
							staticWall2 = new StaticWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, shape, rotation, applyShading);
							if (staticWall2.hardShadow()) {
								staticWall2.addShadow(toolkit);
							}
							wall2 = staticWall2;
						} else {
							wall2 = new DynamicWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, shape, rotation, sequenceId);
						}
						Scene.addWall(minLevel, x, z, wall2, null);
						if (locType.staticShadow && MapRegion.lowDetailShadows) {
							if (rotation == 0) {
								ground.ka(x, z + 1, 50);
							} else if (rotation == 1) {
								ground.ka(x + 1, z + 1, 50);
							} else if (rotation == 2) {
								ground.ka(x + 1, z, 50);
							} else if (rotation == 3) {
								ground.ka(x, z, 50);
							}
						}
						if (locType.blocksMovement != 0 && collisionMap != null) {
							collisionMap.flagBoundary(z, x, shape, rotation, !locType.ignoreClipOnAltRoute, locType.blocksProjectiles);
						}
					} else if (shape == LocShapes.WALL_DIAGONAL) {
						if (stationary) {
							staticLocation = new StaticLocation(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, x, x, z, z, shape, rotation, applyShading);
							if (staticLocation.hardShadow()) {
								staticLocation.addShadow(toolkit);
							}
							positionEntity = staticLocation;
						} else {
							positionEntity = new DynamicLocation(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, x, x + locSizeX - 1, z, z + locSizeZ - 1, shape, rotation, sequenceId);
						}
						Scene.addInteractive(positionEntity, false);
						if (locType.occlusionMode == LocOcclusionMode.ALL && !super.underwater) {
							@Pc(1570) byte cullFlags;
							if ((rotation & 0x1) == 0) {
								cullFlags = 8;
							} else {
								cullFlags = 16;
							}
							Culling.addWallCulling(x, 0, locType.cullY, z, minLevel, cullFlags);
						}
						if (locType.blocksMovement != 0 && collisionMap != null) {
							collisionMap.flagBlockedObject(!locType.ignoreClipOnAltRoute, locSizeX, z, x, locType.blocksProjectiles, locSizeZ);
						}
						if (locType.decorDisplacement != 64) {
							Scene.offsetWallDecoration(minLevel, x, z, locType.decorDisplacement);
						}
					} else {
						@Pc(1639) WallDecor wallDecor;
						if (shape == LocShapes.WALLDECOR_STRAIGHT_NOOFFSET) {
							if (stationary) {
								@Pc(1658) StaticWallDecor staticWallDecor = new StaticWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, 0, 0, shape, rotation);
								if (staticWallDecor.hardShadow()) {
									staticWallDecor.addShadow(toolkit);
								}
								wallDecor = staticWallDecor;
							} else {
								wallDecor = new DynamicWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, 0, 0, shape, rotation, sequenceId);
							}
							Scene.addWallDecoration(minLevel, x, z, wallDecor, null);
						} else {
							@Pc(1684) int local1684;
							@Pc(1690) GameObject location;
							@Pc(1729) StaticWallDecor staticWallDecor2;
							if (shape == LocShapes.WALLDECOR_STRAIGHT_OFFSET) {
								int displacement = 65;
								location = (GameObject) Scene.getWall(minLevel, x, z);
								if (location != null) {
									displacement = TypeLists.LOC.list(location.getId()).decorDisplacement + 1;
								}
								if (stationary) {
									staticWallDecor2 = new StaticWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, displacement * MapRegion.WALL_DECOR_OFFSET_X[rotation], MapRegion.WALL_DECOR_OFFSET_Z[rotation] * displacement, shape, rotation);
									if (staticWallDecor2.hardShadow()) {
										staticWallDecor2.addShadow(toolkit);
									}
									wallDecor = staticWallDecor2;
								} else {
									wallDecor = new DynamicWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, MapRegion.WALL_DECOR_OFFSET_X[rotation] * displacement, displacement * MapRegion.WALL_DECOR_OFFSET_Z[rotation], shape, rotation, sequenceId);
								}
								Scene.addWallDecoration(minLevel, x, z, wallDecor, null);
							} else if (shape == LocShapes.WALLDECOR_DIAGONAL_OFFSET) {
								int diagonalDisplacement = 33;
								location = (GameObject) Scene.getWall(minLevel, x, z);
								if (location != null) {
									diagonalDisplacement = TypeLists.LOC.list(location.getId()).decorDisplacement / 2 + 1;
								}
								if (stationary) {
									staticWallDecor2 = new StaticWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, diagonalDisplacement * MapRegion.WALL_DECOR_OFFSET_X[rotation], MapRegion.WALL_DECOR_OFFSET_Z[rotation] * diagonalDisplacement, shape, rotation + 4);
									if (staticWallDecor2.hardShadow()) {
										staticWallDecor2.addShadow(toolkit);
									}
									wallDecor = staticWallDecor2;
								} else {
									wallDecor = new DynamicWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, MapRegion.DIAGONAL_WALL_DECOR_OFFSET_X[rotation] * diagonalDisplacement, diagonalDisplacement * MapRegion.DIAGONAL_WALL_DECOR_OFFSET_Z[rotation], shape, rotation + 4, sequenceId);
								}
								Scene.addWallDecoration(minLevel, x, z, wallDecor, null);
							} else if (shape == LocShapes.WALLDECOR_DIAGONAL_NOOFFSET) {
								int oppositeRotation = rotation + 2 & 0x3;
								if (stationary) {
									@Pc(1929) StaticWallDecor staticWallDecor3 = new StaticWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, 0, 0, shape, oppositeRotation + 4);
									if (staticWallDecor3.hardShadow()) {
										staticWallDecor3.addShadow(toolkit);
									}
									wallDecor = staticWallDecor3;
								} else {
									wallDecor = new DynamicWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, 0, 0, shape, oppositeRotation + 4, sequenceId);
								}
								Scene.addWallDecoration(minLevel, x, z, wallDecor, null);
							} else if (shape == LocShapes.WALLDECOR_DIAGONAL_BOTH) {
								int oppositeRotation = rotation + 2 & 0x3;
								int diagonalDisplacement = 33;
								@Pc(1965) GameObject location2 = (GameObject) Scene.getWall(minLevel, x, z);
								if (location2 != null) {
									diagonalDisplacement = TypeLists.LOC.list(location2.getId()).decorDisplacement / 2 + 1;
								}
								@Pc(2011) WallDecor wallDecor2;
								@Pc(2031) WallDecor wallDecor3;
								if (stationary) {
									wallDecor2 = new StaticWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, diagonalDisplacement * MapRegion.DIAGONAL_WALL_DECOR_OFFSET_X[rotation], MapRegion.DIAGONAL_WALL_DECOR_OFFSET_Z[rotation] * diagonalDisplacement, shape, rotation + 4);
									wallDecor3 = new StaticWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, 0, 0, shape, oppositeRotation + 4);
									if (((StaticWallDecor) wallDecor2).hardShadow()) {
										((StaticWallDecor) wallDecor2).addShadow(toolkit);
									}
									if (((StaticWallDecor) wallDecor3).hardShadow()) {
										((StaticWallDecor) wallDecor3).addShadow(toolkit);
									}
								} else {
									wallDecor2 = new DynamicWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, diagonalDisplacement * MapRegion.DIAGONAL_WALL_DECOR_OFFSET_X[rotation], diagonalDisplacement * MapRegion.DIAGONAL_WALL_DECOR_OFFSET_Z[rotation], shape, rotation + 4, sequenceId);
									wallDecor3 = new DynamicWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, 0, 0, shape, oppositeRotation + 4, sequenceId);
								}
								Scene.addWallDecoration(minLevel, x, z, wallDecor2, wallDecor3);
							}
						}
					}
				}
			} else {
				if (stationary) {
					staticLocation = new StaticLocation(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, x, locSizeX + x - 1, z, z + locSizeZ - 1, shape, rotation, applyShading);
					positionEntity = staticLocation;
					if (staticLocation.hardShadow()) {
						staticLocation.addShadow(toolkit);
					}
				} else {
					positionEntity = new DynamicLocation(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, x, x + locSizeX - 1, z, z + locSizeZ - 1, shape, rotation, sequenceId);
				}
				Scene.addInteractive(positionEntity, false);
				if (MapRegion.lowDetailShadows && !super.underwater && shape >= 12 && shape <= 17 && shape != 13 && minLevel > 0 && locType.occlusionMode != 0) {
					super.occluderFlags[minLevel][x][z] = (byte) (super.occluderFlags[minLevel][x][z] | 0x4);
				}
				if (locType.blocksMovement != 0 && collisionMap != null) {
					collisionMap.flagBlockedObject(!locType.ignoreClipOnAltRoute, locSizeX, z, x, locType.blocksProjectiles, locSizeZ);
				}
			}
		} else if (GamePreferences.uid.groundDecor.getValue() != 0 || locType.hasActions != 0 || locType.blocksMovement == 1 || locType.forceDisplayDecoration) {
			@Pc(301) GroundDecor groundDecor;
			if (stationary) {
				@Pc(318) StaticGroundDecor staticGroundDecor = new StaticGroundDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, rotation, applyShading);
				groundDecor = staticGroundDecor;
				if (staticGroundDecor.hardShadow()) {
					staticGroundDecor.addShadow(toolkit);
				}
			} else {
				groundDecor = new DynamicGroundDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, super.underwater, rotation, sequenceId);
			}
			Scene.setGroundDecoration(minLevel, x, z, groundDecor);
			if (locType.blocksMovement == 1 && collisionMap != null) {
				collisionMap.flagFloorDecoration(z, x);
			}
		}
	}

	@OriginalMember(owner = "client!vaa", name = "b", descriptor = "(IIIII)Lclient!se;")
	private GameObject getGameObject(@OriginalArg(0) int layer, @OriginalArg(1) int z, @OriginalArg(2) int x, @OriginalArg(3) int level) {
		@Pc(5) GameObject location = null;
		if (layer == LocLayer.WALL) {
			location = (GameObject) Scene.getWall(level, x, z);
		}
		if (layer == LocLayer.WALLDECOR) {
			location = (GameObject) Scene.getWallDecoration(level, x, z);
		}
		if (layer == LocLayer.GROUND) {
			location = (GameObject) Scene.getInteractiveOfType(level, x, z, GameObject.class);
		}
		if (layer == LocLayer.GROUNDDECOR) {
			location = (GameObject) Scene.getGroundDecoration(level, x, z);
		}
		return location;
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(ILclient!ha;Lclient!cea;[IIB)V")
	public void decodeEffectsClient(@OriginalArg(0) int regionStartZ, @OriginalArg(1) Toolkit toolkit, @OriginalArg(2) Packet packet, @OriginalArg(3) int[] skyboxData, @OriginalArg(4) int regionStartX) {
		if (super.underwater) {
			return;
		}
		@Pc(11) boolean lightingDecoded = false;
		@Pc(13) Environment environment = null;
		if (skyboxData != null) {
			skyboxData[0] = -1;
		}
		while (true) {
			@Pc(32) int local32;
			@Pc(83) int local83;
			@Pc(129) int local129;
			@Pc(133) int local133;
			@Pc(510) int local510;
			while (packet.buffer.length > packet.currentPosition) {
				int opcode = packet.readUByte();
				if (opcode == 0) {
					environment = new Environment(packet);
				} else {
					@Pc(168) int local168;
					@Pc(276) int local276;
					@Pc(135) int local135;
					if (opcode == 1) {
						int lightCount = packet.readUByte();
						if (lightCount > 0) {
							for (int lightIndex = 0; lightIndex < lightCount; lightIndex++) {
								@Pc(519) StaticPointLight staticPointLight = new StaticPointLight(toolkit, packet, 2);
								if (staticPointLight.type == 31) {
									@Pc(530) LightType lightType = TypeLists.LIGHT.list(packet.readUShort());
									staticPointLight.setType(lightType.surrounding, lightType.duration, lightType.effect, lightType.ticker);
								}
								if (toolkit.getMaxLights() > 0) {
									@Pc(547) PointLight pointLight = staticPointLight.pointLight;
									int lightX = (regionStartX << 9) + pointLight.getX();
									int lightZ = pointLight.getZ() + (regionStartZ << 9);
									int lightTileX = lightX >> 9;
									@Pc(571) int lightTileZ = lightZ >> 9;
									if (lightTileX >= 0 && lightTileZ >= 0 && super.regionSizeX > lightTileX && super.regionSizeZ > lightTileZ) {
										pointLight.setPosition(lightX, lightZ, super.tileHeights[staticPointLight.level][lightTileX][lightTileZ] - pointLight.getY());
										MapRegion.addPointLight(staticPointLight);
									}
								}
							}
						}
					} else if (opcode == 2) {
						if (environment == null) {
							environment = new Environment();
						}
						environment.decodeHdr(packet);
					} else if (opcode != 128) {
						if (opcode != 129) {
							throw new IllegalStateException("");
						}
						if (super.cameraHeights == null) {
							super.cameraHeights = new byte[4][][];
						}
						for (int level = 0; level < 4; level++) {
							@Pc(89) byte lightingDecodeType = packet.readByte();
							if (lightingDecodeType == 0 && super.cameraHeights[level] != null) {
								int tileX = regionStartX;
								int endX = regionStartX + 64;
								int tileZ = regionStartZ;
								if (regionStartZ < 0) {
									tileZ = 0;
								} else if (regionStartZ >= super.regionSizeZ) {
									tileZ = super.regionSizeZ;
								}
								if (regionStartX < 0) {
									tileX = 0;
								} else if (regionStartX >= super.regionSizeX) {
									tileX = super.regionSizeX;
								}
								int endZ = regionStartZ + 64;
								if (endX < 0) {
									endX = 0;
								} else if (endX >= super.regionSizeX) {
									endX = super.regionSizeX;
								}
								if (endZ < 0) {
									endZ = 0;
								} else if (endZ >= super.regionSizeZ) {
									endZ = super.regionSizeZ;
								}
								while (tileX < endX) {
									while (endZ > tileZ) {
										super.cameraHeights[level][tileX][tileZ] = 0;
										tileZ++;
									}
									tileX++;
								}
							} else if (lightingDecodeType == 1) {
								if (super.cameraHeights[level] == null) {
									super.cameraHeights[level] = new byte[super.regionSizeX + 1][super.regionSizeZ + 1];
								}
								for (int blockX = 0; blockX < 64; blockX += 4) {
									for (int blockZ = 0; blockZ < 64; blockZ += 4) {
										@Pc(266) byte lightIntensity = packet.readByte();
										for (int tileX = blockX + regionStartX; tileX < blockX + regionStartX + 4; tileX++) {
											for (int tileZ = regionStartZ + blockZ; tileZ < regionStartZ + blockZ + 4; tileZ++) {
												if (tileX >= 0 && tileX < super.regionSizeX && tileZ >= 0 && tileZ < super.regionSizeZ) {
													super.cameraHeights[level][tileX][tileZ] = lightIntensity;
												}
											}
										}
									}
								}
							} else if (lightingDecodeType == 2) {
								if (super.cameraHeights[level] == null) {
									super.cameraHeights[level] = new byte[super.regionSizeX + 1][super.regionSizeZ + 1];
								}
								if (level > 0) {
									int tileX = regionStartX;
									int endX = regionStartX + 64;
									int tileZ = regionStartZ;
									if (endX < 0) {
										endX = 0;
									} else if (super.regionSizeX <= endX) {
										endX = super.regionSizeX;
									}
									if (regionStartZ < 0) {
										tileZ = 0;
									} else if (super.regionSizeZ <= regionStartZ) {
										tileZ = super.regionSizeZ;
									}
									int endZ = regionStartZ + 64;
									if (regionStartX < 0) {
										tileX = 0;
									} else if (regionStartX >= super.regionSizeX) {
										tileX = super.regionSizeX;
									}
									if (endZ < 0) {
										endZ = 0;
									} else if (endZ >= super.regionSizeZ) {
										endZ = super.regionSizeZ;
									}
									while (tileX < endX) {
										while (tileZ < endZ) {
											super.cameraHeights[level][tileX][tileZ] = super.cameraHeights[level - 1][tileX][tileZ];
											tileZ++;
										}
										tileX++;
									}
								}
							}
						}
						lightingDecoded = true;
					} else if (skyboxData == null) {
						packet.currentPosition += 10;
					} else {
						skyboxData[0] = packet.readUShort();
						skyboxData[1] = packet.readShort();
						skyboxData[2] = packet.readShort();
						skyboxData[3] = packet.readShort();
						skyboxData[4] = packet.readUShort();
					}
				}
			}
			if (environment != null) {
				for (int chunkOffsetX = 0; chunkOffsetX < 8; chunkOffsetX++) {
					for (int chunkOffsetZ = 0; chunkOffsetZ < 8; chunkOffsetZ++) {
						int chunkX = chunkOffsetX + (regionStartX >> 3);
						int chunkZ = (regionStartZ >> 3) + chunkOffsetZ;
						if (chunkX >= 0 && chunkX < super.regionSizeX >> 3 && chunkZ >= 0 && super.regionSizeZ >> 3 > chunkZ) {
							EnvironmentManager.addChunkEnvironment(environment, chunkX, chunkZ);
						}
					}
				}
			}
			if (!lightingDecoded && super.cameraHeights != null) {
				for (int level = 0; level < 4; level++) {
					if (super.cameraHeights[level] != null) {
						for (int cellOffsetX = 0; cellOffsetX < 16; cellOffsetX++) {
							for (int cellOffsetZ = 0; cellOffsetZ < 16; cellOffsetZ++) {
								int cellX = (regionStartX >> 2) + cellOffsetX;
								int cellZ = cellOffsetZ + (regionStartZ >> 2);
								if (cellX >= 0 && cellX < 26 && cellZ >= 0 && cellZ < 26) {
									super.cameraHeights[level][cellX][cellZ] = 0;
								}
							}
						}
					}
				}
				return;
			}
			return;
		}
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(II[BIILclient!ha;III[Lclient!fe;I)V")
	public void decodeLocMapServer(@OriginalArg(0) int mapX, @OriginalArg(1) int rotation, @OriginalArg(2) byte[] src, @OriginalArg(3) int sourceLevel, @OriginalArg(4) int mapZ, @OriginalArg(5) Toolkit toolkit, @OriginalArg(6) int sceneZ, @OriginalArg(7) int sceneX, @OriginalArg(9) CollisionMap[] collisionMaps, @OriginalArg(10) int level) {
		@Pc(17) Packet packet = new Packet(src);
		@Pc(19) int locId = -1;
		while (true) {
			@Pc(23) int locIdOffset = packet.readSum();
			if (locIdOffset == 0) {
				return;
			}
			locId += locIdOffset;
			@Pc(34) int bitpackedPosition = 0;
			while (true) {
				@Pc(38) int positionOffset = packet.readUSmart();
				if (positionOffset == 0) {
					break;
				}
				bitpackedPosition += positionOffset - 1;
				@Pc(50) int tileZ = bitpackedPosition & 0x3F;
				@Pc(56) int tileX = bitpackedPosition >> 6 & 0x3F;
				@Pc(60) int tileLevel = bitpackedPosition >> 12;
				@Pc(64) int bitpackedDescription = packet.readUByte();
				@Pc(68) int shape = bitpackedDescription >> 2;
				@Pc(72) int locRotation = bitpackedDescription & 0x3;
				if (sourceLevel == tileLevel && tileX >= mapX && mapX + 8 > tileX && tileZ >= mapZ && tileZ < mapZ + 8) {
					@Pc(108) LocType locType = TypeLists.LOC.list(locId);
					@Pc(125) int locX = ZoneLocRotation.localOffsetX(tileZ & 0x7, tileX & 0x7, rotation, locType.sizeY, locType.sizeX, locRotation) + sceneX;
					@Pc(142) int locZ = sceneZ + ZoneLocRotation.localOffsetZ(tileX & 0x7, locType.sizeY, locRotation, locType.sizeX, tileZ & 0x7, rotation);
					if (locX > 0 && locZ > 0 && locX < super.regionSizeX - 1 && super.regionSizeZ - 1 > locZ) {
						@Pc(160) CollisionMap collisionMap = null;
						if (!super.underwater) {
							@Pc(165) int clipLevel = level;
							if ((SettingsBits.tileFunctionMasks[1][locX][locZ] & TileFlag.BRIDGE) == 2) {
								clipLevel = level - 1;
							}
							if (clipLevel >= 0) {
								collisionMap = collisionMaps[clipLevel];
							}
						}
						this.addObject(locX, level, locId, toolkit, level, shape, locZ, -1, locRotation + rotation & 0x3, collisionMap);
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(Lclient!fe;IILclient!ha;IIB)V")
	public void removeGameObject(@OriginalArg(0) CollisionMap collisionMap, @OriginalArg(1) int layer, @OriginalArg(2) int level, @OriginalArg(3) Toolkit toolkit, @OriginalArg(4) int x, @OriginalArg(5) int z) {
		@Pc(11) GameObject location = this.getGameObject(layer, z, x, level);
		if (location == null) {
			return;
		}
		@Pc(25) LocType locType = TypeLists.LOC.list(location.getId());
		@Pc(29) int shape = location.getShape();
		@Pc(33) int rotation = location.getRotation();
		if (locType.hasSounds()) {
			AmbientSound.unlinkLocSound(locType, x, z, level);
		}
		if (location.hardShadow()) {
			location.removeShadow(toolkit);
		}
		if (layer == LocLayer.WALL) {
			Scene.removeWalls(level, x, z);
			if (locType.blocksMovement != 0) {
				collisionMap.unflagBoundary(locType.blocksProjectiles, rotation, x, shape, z, !locType.ignoreClipOnAltRoute);
			}
			if (locType.occlusionMode == LocOcclusionMode.ALL) {
				if (rotation != 0) {
					if (rotation != 1) {
						if (rotation == 2) {
							Culling.removeCullingCluster(level, 1, z, x + 1);
						} else if (rotation == 3) {
							Culling.removeCullingCluster(level, 2, z, x);
							return;
						}
						return;
					}
					Culling.removeCullingCluster(level, 2, z + 1, x);
					return;
				}
				Culling.removeCullingCluster(level, 1, z, x);
				return;
			}
		} else if (layer == LocLayer.WALLDECOR) {
			Scene.removeWallDecorations(level, x, z);
		} else if (layer == LocLayer.GROUND) {
			Scene.removeInteractiveOfType(level, x, z, GameObject.class);
			if (locType.blocksMovement != 0 && locType.sizeX + x < super.regionSizeX && z + locType.sizeX < super.regionSizeZ && locType.sizeY + x < super.regionSizeX && super.regionSizeZ > z + locType.sizeY) {
				collisionMap.unflagBlockedObject(rotation, !locType.ignoreClipOnAltRoute, locType.blocksProjectiles, x, z, locType.sizeY, locType.sizeX);
			}
			if (shape == LocShapes.WALL_DIAGONAL) {
				if ((rotation & 0x1) != 0) {
					Culling.removeCullingCluster(level, 16, z, x);
					return;
				}
				Culling.removeCullingCluster(level, 8, z, x);
				return;
			}
		} else {
			if (layer == LocLayer.GROUNDDECOR) {
				Scene.removeGroundDecoration(level, x, z);
				if (locType.blocksMovement == 1) {
					collisionMap.unflagFloorDecoration(x, z);
					return;
				}
			}
			return;
		}
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "([B[Lclient!fe;ILclient!ha;II)V")
	public void decodeLocMapClient(@OriginalArg(0) byte[] src, @OriginalArg(1) CollisionMap[] collisionMaps, @OriginalArg(2) int offsetZ, @OriginalArg(3) Toolkit toolkit, @OriginalArg(4) int offsetX) {
		@Pc(14) Packet packet = new Packet(src);
		@Pc(16) int locId = -1;
		while (true) {
			@Pc(20) int locIdOffset = packet.readSum();
			if (locIdOffset == 0) {
				return;
			}
			locId += locIdOffset;
			@Pc(28) int bitpackedPosition = 0;
			while (true) {
				@Pc(32) int positionOffset = packet.readUSmart();
				if (positionOffset == 0) {
					break;
				}
				bitpackedPosition += positionOffset - 1;
				@Pc(47) int localZ = bitpackedPosition & 0x3F;
				@Pc(53) int localX = bitpackedPosition >> 6 & 0x3F;
				@Pc(57) int level = bitpackedPosition >> 12;
				@Pc(61) int bitpackedDescription = packet.readUByte();
				@Pc(65) int shape = bitpackedDescription >> 2;
				@Pc(69) int rotation = bitpackedDescription & 0x3;
				@Pc(73) int sceneX = offsetX + localX;
				@Pc(77) int sceneZ = offsetZ + localZ;
				if (sceneX > 0 && sceneZ > 0 && super.regionSizeX - 1 > sceneX && sceneZ < super.regionSizeZ - 1) {
					@Pc(102) CollisionMap collisionMap = null;
					if (!super.underwater) {
						@Pc(107) int clipLevel = level;
						if ((SettingsBits.tileFunctionMasks[1][sceneX][sceneZ] & TileFlag.BRIDGE) == 2) {
							clipLevel = level - 1;
						}
						if (clipLevel >= 0) {
							collisionMap = collisionMaps[clipLevel];
						}
					}
					this.addObject(sceneX, level, locId, toolkit, level, shape, sceneZ, -1, rotation, collisionMap);
				}
			}
		}
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(Lclient!cea;ZIIIIIILclient!ha;[II)V")
	public void decodeEffectsServer(@OriginalArg(0) Packet packet, @OriginalArg(2) int sourceChunkZ, @OriginalArg(3) int sourceChunkX, @OriginalArg(4) int sceneStartX, @OriginalArg(5) int level, @OriginalArg(6) int rotation, @OriginalArg(7) int sceneStartZ, @OriginalArg(8) Toolkit toolkit, @OriginalArg(9) int[] skyboxData, @OriginalArg(10) int sourceLevel) {
		if (super.underwater) {
			return;
		}
		@Pc(13) Environment environment = null;
		if (skyboxData != null) {
			skyboxData[0] = -1;
		}
		@Pc(25) int chunkStartTileX = (sourceChunkX & 0x7) * 8;
		@Pc(31) int chunkStartTileZ = (sourceChunkZ & 0x7) * 8;
		while (true) {
			@Pc(48) int local48;
			@Pc(104) int local104;
			@Pc(158) int local158;
			@Pc(176) int local176;
			@Pc(52) int local52;
			@Pc(98) int local98;
			label233: do {
				while (true) {
					@Pc(36) int local36;
					@Pc(327) int local327;
					while (packet.buffer.length > packet.currentPosition) {
						int opcode = packet.readUByte();
						if (opcode == 0) {
							environment = new Environment(packet);
						} else {
							if (opcode == 1) {
								local48 = packet.readUByte();
								continue label233;
							}
							if (opcode == 2) {
								if (environment == null) {
									environment = new Environment();
								}
								environment.decodeHdr(packet);
							} else if (opcode != 128) {
								if (opcode != 129) {
									throw new IllegalStateException("");
								}
								if (super.cameraHeights == null) {
									super.cameraHeights = new byte[4][][];
								}
								for (local48 = 0; local48 < 4; local48++) {
									@Pc(312) byte lightingDecodeType = packet.readByte();
									@Pc(331) int local331;
									if (lightingDecodeType == 0 && super.cameraHeights[level] != null) {
										if (sourceLevel >= local48) {
											int tileX = sceneStartX;
											int endX = sceneStartX + 7;
											int tileZ = sceneStartZ;
											if (endX < 0) {
												endX = 0;
											} else if (super.regionSizeX <= endX) {
												endX = super.regionSizeX;
											}
											if (sceneStartX < 0) {
												tileX = 0;
											} else if (super.regionSizeX <= sceneStartX) {
												tileX = super.regionSizeX;
											}
											int endZ = sceneStartZ + 7;
											if (sceneStartZ < 0) {
												tileZ = 0;
											} else if (sceneStartZ >= super.regionSizeZ) {
												tileZ = super.regionSizeZ;
											}
											if (endZ < 0) {
												endZ = 0;
											} else if (endZ >= super.regionSizeZ) {
												endZ = super.regionSizeZ;
											}
											while (endX > tileX) {
												while (tileZ < endZ) {
													super.cameraHeights[level][tileX][tileZ] = 0;
													tileZ++;
												}
												tileX++;
											}
										}
									} else if (lightingDecodeType == 1) {
										if (super.cameraHeights[level] == null) {
											super.cameraHeights[level] = new byte[super.regionSizeX + 1][super.regionSizeZ + 1];
										}
										for (int blockX = 0; blockX < 64; blockX += 4) {
											for (int blockZ = 0; blockZ < 64; blockZ += 4) {
												@Pc(466) byte lightIntensity = packet.readByte();
												if (local48 <= sourceLevel) {
													for (int tileX = blockX; tileX < blockX + 4; tileX++) {
														for (int tileZ = blockZ; tileZ < blockZ + 4; tileZ++) {
															if (chunkStartTileX <= tileX && tileX < chunkStartTileX + 8 && chunkStartTileZ <= tileZ && chunkStartTileZ < chunkStartTileZ + 8) {
																int lightSceneX = sceneStartX + Terrain.rotateTerrainBlockX(tileZ & 0x7, rotation, tileX & 0x7);
																@Pc(526) int lightSceneZ = sceneStartZ + TerrainBlockRotationY.rotateTerrainBlockY(tileX & 0x7, tileZ & 0x7, rotation);
																if (lightSceneX >= 0 && lightSceneX < super.regionSizeX && lightSceneZ >= 0 && super.regionSizeZ > lightSceneZ) {
																	super.cameraHeights[level][lightSceneX][lightSceneZ] = lightIntensity;
																}
															}
														}
													}
												}
											}
										}
									}
								}
							} else if (skyboxData == null) {
								packet.currentPosition += 10;
							} else {
								skyboxData[0] = packet.readUShort();
								skyboxData[1] = packet.readShort();
								skyboxData[2] = packet.readShort();
								skyboxData[3] = packet.readShort();
								skyboxData[4] = packet.readUShort();
							}
						}
					}
					if (environment != null) {
						EnvironmentManager.addChunkEnvironment(environment, sceneStartX >> 3, sceneStartZ >> 3);
					}
					if (super.cameraHeights != null && super.cameraHeights[level] != null) {
						int endX = sceneStartX + 7;
						local48 = sceneStartZ + 7;
						for (int tileX = sceneStartX; tileX < endX; tileX++) {
							for (int tileZ = sceneStartZ; tileZ < local48; tileZ++) {
								super.cameraHeights[level][tileX][tileZ] = 0;
							}
						}
						return;
					}
					return;
				}
			} while (local48 <= 0);
			for (int lightIndex = 0; lightIndex < local48; lightIndex++) {
				@Pc(61) StaticPointLight staticPointLight = new StaticPointLight(toolkit, packet, 2);
				if (staticPointLight.type == 31) {
					@Pc(72) LightType lightType = TypeLists.LIGHT.list(packet.readUShort());
					staticPointLight.setType(lightType.surrounding, lightType.duration, lightType.effect, lightType.ticker);
				}
				if (toolkit.getMaxLights() > 0) {
					@Pc(92) PointLight pointLight = staticPointLight.pointLight;
					int lightTileX = pointLight.getX() >> 9;
					int lightTileZ = pointLight.getZ() >> 9;
					if (staticPointLight.level == sourceLevel && lightTileX >= chunkStartTileX && lightTileX < chunkStartTileX + 8 && chunkStartTileZ <= lightTileZ && lightTileZ < chunkStartTileZ + 8) {
						int lightX = (sceneStartX << 9) + EnvironmentManager.rotatedX(pointLight.getZ() & 0xFFF, pointLight.getX() & 0xFFF, rotation);
						int lightZ = (sceneStartZ << 9) + EnvironmentManager.rotatedZ(rotation, pointLight.getZ() & 0xFFF, pointLight.getX() & 0xFFF);
						lightTileX = lightX >> 9;
						lightTileZ = lightZ >> 9;
						if (lightTileX >= 0 && lightTileZ >= 0 && lightTileX < super.regionSizeX && lightTileZ < super.regionSizeZ) {
							pointLight.setPosition(lightX, lightZ, super.tileHeights[sourceLevel][lightTileX][lightTileZ] - pointLight.getY());
							MapRegion.addPointLight(staticPointLight);
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(ZLclient!ha;I)V")
	public void calculateVisibility(@OriginalArg(0) boolean underwater, @OriginalArg(1) Toolkit toolkit) {
		Scene.applyShading();
		if (!underwater) {
			@Pc(16) int local16;
			@Pc(20) int z;
			if (super.levels > 1) {
				for (int tileX = 0; super.regionSizeX > tileX; tileX++) {
					for (z = 0; super.regionSizeZ > z; z++) {
						if ((SettingsBits.tileFunctionMasks[1][tileX][z] & TileFlag.BRIDGE) == 2) {
							Scene.linkBelow(tileX, z);
						}
					}
				}
			}
			for (int level = 0; level < super.levels; level++) {
				for (z = 0; super.regionSizeZ >= z; z++) {
					for (@Pc(56) int i = 0; i <= super.regionSizeX; i++) {
						if ((super.occluderFlags[level][i][z] & TileFlag.REMOVE_ROOF) != 0) {
							@Pc(74) int minX = i;
							@Pc(76) int maxX = i;
							@Pc(78) int minZ = z;
							@Pc(80) int maxZ = z;
							while (minZ > 0 && (super.occluderFlags[level][i][minZ - 1] & TileFlag.REMOVE_ROOF) != 0 && z - minZ < 10) {
								minZ--;
							}
							while (super.regionSizeZ > maxZ && (super.occluderFlags[level][i][maxZ + 1] & TileFlag.REMOVE_ROOF) != 0 && maxZ - minZ < 10) {
								maxZ++;
							}
							@Pc(138) int local138;
							label111: while (minX > 0 && i - minX < 10) {
								for (int edgeZ = minZ; edgeZ <= maxZ; edgeZ++) {
									if ((super.occluderFlags[level][minX - 1][edgeZ] & TileFlag.REMOVE_ROOF) == 0) {
										break label111;
									}
								}
								minX--;
							}
							label98: while (maxX < super.regionSizeX && maxX - minX < 10) {
								for (int edgeZ = minZ; edgeZ <= maxZ; edgeZ++) {
									if ((super.occluderFlags[level][maxX + 1][edgeZ] & TileFlag.REMOVE_ROOF) == 0) {
										break label98;
									}
								}
								maxX++;
							}
							if ((maxZ + 1 - minZ) * (maxX + 1 - minX) >= 4) {
								int clusterHeight = super.tileHeights[level][minX][minZ];
								Culling.addGroundCullingCluster((maxX << 9) + 512, clusterHeight, minZ << 9, clusterHeight, (maxZ << 9) + 512, level, minX << 9);
								for (@Pc(266) int j = minX; j <= maxX; j++) {
									for (@Pc(270) int k = minZ; k <= maxZ; k++) {
										super.occluderFlags[level][j][k] &= 0xFFFFFFFB;
									}
								}
							}
						}
					}
				}
			}
			Culling.rebuildCullingClusters();
		}
		super.occluderFlags = null;
	}

	@OriginalMember(owner = "client!jk", name = "a", descriptor = "(Lclient!ro;)V")
	public static void addPointLight(@OriginalArg(0) StaticPointLight staticPointLight) {
		if (Scene.lightSlotCount >= 65535) {
			return;
		}
		@Pc(6) PointLight pointLight = staticPointLight.pointLight;
		Scene.aClass300Array1[Scene.lightSlotCount] = staticPointLight;
		Scene.pointLightApplied[Scene.lightSlotCount] = false;
		Scene.lightSlotCount++;
		@Pc(21) int level = staticPointLight.level;
		if (staticPointLight.growsDownwards) {
			level = 0;
		}
		@Pc(29) int maxLevel = staticPointLight.level;
		if (staticPointLight.growsUpwards) {
			maxLevel = Scene.length - 1;
		}
		for (@Pc(38) int i = level; i <= maxLevel; i++) {
			@Pc(41) int rangeIndex = 0;
			@Pc(53) int minZ = pointLight.getZ() + Scene.halfTileOffset - pointLight.getRadius() >> Scene.tileUnits;
			if (minZ < 0) {
				rangeIndex = -minZ;
				minZ = 0;
			}
			@Pc(73) int maxZ = pointLight.getZ() + pointLight.getRadius() - Scene.halfTileOffset >> Scene.tileUnits;
			if (maxZ >= Camera.sceneLength) {
				maxZ = Camera.sceneLength - 1;
			}
			for (@Pc(82) int j = minZ; j <= maxZ; j++) {
				@Pc(89) short range = staticPointLight.ranges[rangeIndex++];
				@Pc(105) int minX = (pointLight.getX() + Scene.halfTileOffset - pointLight.getRadius() >> Scene.tileUnits) + (range >>> 8);
				@Pc(113) int maxX = minX + (range & 0xFF) - 1;
				if (minX < 0) {
					minX = 0;
				}
				if (maxX >= Scene.sceneWidth) {
					maxX = Scene.sceneWidth - 1;
				}
				for (@Pc(126) int k = minX; k <= maxX; k++) {
					@Pc(135) long lightSlots = Scene.tileLightUids[i][k][j];
					if ((lightSlots & 0xFFFFL) == 0L) {
						Scene.tileLightUids[i][k][j] = lightSlots | (long) Scene.lightSlotCount;
					} else if ((lightSlots & 0xFFFF0000L) == 0L) {
						Scene.tileLightUids[i][k][j] = lightSlots | (long) Scene.lightSlotCount << 16;
					} else if ((lightSlots & 0xFFFF00000000L) == 0L) {
						Scene.tileLightUids[i][k][j] = lightSlots | (long) Scene.lightSlotCount << 32;
					} else if ((lightSlots & 0xFFFF000000000000L) == 0L) {
						Scene.tileLightUids[i][k][j] = lightSlots | (long) Scene.lightSlotCount << 48;
					}
				}
			}
		}
	}
	@OriginalMember(owner = "client!hga", name = "P", descriptor = "I")
	public static int sceneSizeY = 104;
	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(I[[[Lclient!es;IIIZ)Z")
	public static boolean calculateVisibilityAdjustments(@OriginalArg(0) int baseZ, @OriginalArg(1) Tile[][][] tiles, @OriginalArg(2) int baseX, @OriginalArg(4) int viewIndex, @OriginalArg(5) boolean hideRoofs) {
		@Pc(14) byte setting = hideRoofs ? 1 : (byte) (SceneDrawer.timesSceneRendered & 0xFF);
		if (MapRegion.roofStamps[PlayerUpdate.currentLevel][baseX][baseZ] == setting) {
			return false;
		} else if ((SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][baseX][baseZ] & TileFlag.REMOVE_ROOF) == 0) {
			return false;
		} else {
			@Pc(43) byte seedIndex = 0;
			@Pc(45) int readIndex = 0;
			MapRegion.roofQueueX[0] = baseX;
			@Pc(52) int writeIndex = seedIndex + 1;
			MapRegion.roofQueueZ[0] = baseZ;
			MapRegion.roofStamps[PlayerUpdate.currentLevel][baseX][baseZ] = setting;
			while (readIndex != writeIndex) {
				@Pc(70) int tileX = MapRegion.roofQueueX[readIndex] & 0xFFFF;
				@Pc(78) int packedPlacementA = MapRegion.roofQueueX[readIndex] >> 16 & 0xFF;
				@Pc(86) int packedPlacementB = MapRegion.roofQueueX[readIndex] >> 24 & 0xFF;
				@Pc(92) int tileZ = MapRegion.roofQueueZ[readIndex] & 0xFFFF;
				@Pc(100) int packedPlacementC = MapRegion.roofQueueZ[readIndex] >> 16 & 0xFF;
				readIndex = readIndex + 1 & 0xFFF;
				@Pc(108) boolean outsideRoofArea = false;
				if ((SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][tileX][tileZ] & TileFlag.REMOVE_ROOF) == 0) {
					outsideRoofArea = true;
				}
				@Pc(122) boolean visibilityUpdate = false;
				@Pc(128) int local128;
				@Pc(170) int local170;
				@Pc(220) int local220;
				if (tiles != null) {
					label237: for (int level = PlayerUpdate.currentLevel + 1; level <= 3; level++) {
						if (tiles[level] != null && (SettingsBits.tileFunctionMasks[level][tileX][tileZ] & TileFlag.ZERO_LEVEL) == 0) {
							@Pc(323) AbstractInteractiveEntity positionEntity;
							@Pc(333) int local333;
							@Pc(312) Tile tile;
							@Pc(318) InteractiveEntityContainer positionEntityNode;
							if (outsideRoofArea && tiles[level][tileX][tileZ] != null) {
								if (tiles[level][tileX][tileZ].primaryWall != null) {
									int sideMaskA = LocShapes.getRoofEdgeSideMask(packedPlacementA);
									if (sideMaskA == tiles[level][tileX][tileZ].primaryWall.sideMask || tiles[level][tileX][tileZ].secondaryWall != null && tiles[level][tileX][tileZ].secondaryWall.sideMask == sideMaskA) {
										continue;
									}
									if (packedPlacementB != 0) {
										int sideMaskB = LocShapes.getRoofEdgeSideMask(packedPlacementB);
										if (sideMaskB == tiles[level][tileX][tileZ].primaryWall.sideMask || tiles[level][tileX][tileZ].secondaryWall != null && sideMaskB == tiles[level][tileX][tileZ].secondaryWall.sideMask) {
											continue;
										}
									}
									if (packedPlacementC != 0) {
										int sideMaskC = LocShapes.getRoofEdgeSideMask(packedPlacementC);
										if (tiles[level][tileX][tileZ].primaryWall.sideMask == sideMaskC || tiles[level][tileX][tileZ].secondaryWall != null && tiles[level][tileX][tileZ].secondaryWall.sideMask == sideMaskC) {
											continue;
										}
									}
								}
								tile = tiles[level][tileX][tileZ];
								if (tile.interactives != null) {
									for (positionEntityNode = tile.interactives; positionEntityNode != null; positionEntityNode = positionEntityNode.child) {
										positionEntity = positionEntityNode.interactiveObject;
										if (positionEntity instanceof GameObject) {
											@Pc(329) GameObject location = (GameObject) positionEntity;
											int shape = location.getShape();
											@Pc(337) int rotation = location.getRotation();
											if (shape == LocShapes.ROOFEDGE_SQUARECORNER) {
												shape = LocShapes.ROOFEDGE_DIAGONALCORNER;
											}
											@Pc(348) int packedPlacement = rotation << 6 | shape;
											if (packedPlacement == packedPlacementA || packedPlacementB != 0 && packedPlacementB == packedPlacement || packedPlacementC != 0 && packedPlacementC == packedPlacement) {
												continue label237;
											}
										}
									}
								}
							}
							tile = tiles[level][tileX][tileZ];
							if (tile != null && tile.interactives != null) {
								for (positionEntityNode = tile.interactives; positionEntityNode != null; positionEntityNode = positionEntityNode.child) {
									positionEntity = positionEntityNode.interactiveObject;
									if (positionEntity.startX != positionEntity.endX || positionEntity.endZ != positionEntity.startZ) {
										for (@Pc(406) int i = positionEntity.startX; i <= positionEntity.endX; i++) {
											for (int stampZ = positionEntity.startZ; stampZ <= positionEntity.endZ; stampZ++) {
												MapRegion.roofStamps[level][i][stampZ] = setting;
											}
										}
									}
								}
							}
							MapRegion.roofStamps[level][tileX][tileZ] = setting;
							visibilityUpdate = true;
						}
					}
				}
				if (visibilityUpdate) {
					int groundHeight = Scene.aClass65Array1[PlayerUpdate.currentLevel + 1].getHeight(tileZ, tileX);
					if (groundHeight > MapRegion.visibleBelowDrawheight[viewIndex]) {
						MapRegion.visibleBelowDrawheight[viewIndex] = groundHeight;
					}
					int sceneX = tileX << 9;
					if (MapRegion.roofMinX[viewIndex] > sceneX) {
						MapRegion.roofMinX[viewIndex] = sceneX;
					} else if (sceneX > MapRegion.roofMaxX[viewIndex]) {
						MapRegion.roofMaxX[viewIndex] = sceneX;
					}
					int sceneZ = tileZ << 9;
					if (MapRegion.roofMinZ[viewIndex] > sceneZ) {
						MapRegion.roofMinZ[viewIndex] = sceneZ;
					} else if (sceneZ > MapRegion.roofMaxZ[viewIndex]) {
						MapRegion.roofMaxZ[viewIndex] = sceneZ;
					}
				}
				if (!outsideRoofArea) {
					if (tileX >= 1 && MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX - 1][tileZ] != setting) {
						MapRegion.roofQueueX[writeIndex] = tileX - 1 | 0x120000 | 0xD3000000;
						MapRegion.roofQueueZ[writeIndex] = tileZ | 0x130000;
						writeIndex = writeIndex + 1 & 0xFFF;
						MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX - 1][tileZ] = setting;
					}
					@Pc(592) int sceneSizeYComplement = ~MapRegion.sceneSizeY;
					tileZ++;
					if (sceneSizeYComplement < ~tileZ) {
						if (tileX - 1 >= 0 && MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX - 1][tileZ] != setting && (SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][tileX][tileZ] & TileFlag.REMOVE_ROOF) == 0 && (SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][tileX - 1][tileZ - 1] & TileFlag.REMOVE_ROOF) == 0) {
							MapRegion.roofQueueX[writeIndex] = tileX - 1 | 0x52000000 | 0x120000;
							MapRegion.roofQueueZ[writeIndex] = tileZ | 0x130000;
							MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX - 1][tileZ] = setting;
							writeIndex = writeIndex + 1 & 0xFFF;
						}
						if (setting != MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX][tileZ]) {
							MapRegion.roofQueueX[writeIndex] = tileX | 0x13000000 | 0x520000;
							MapRegion.roofQueueZ[writeIndex] = tileZ | 0x530000;
							MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX][tileZ] = setting;
							writeIndex = writeIndex + 1 & 0xFFF;
						}
						if (sceneSizeX > tileX + 1 && setting != MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX + 1][tileZ] && (SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][tileX][tileZ] & TileFlag.REMOVE_ROOF) == 0 && (SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][tileX + 1][tileZ - 1] & TileFlag.REMOVE_ROOF) == 0) {
							MapRegion.roofQueueX[writeIndex] = tileX + 1 | 0x92000000 | 0x520000;
							MapRegion.roofQueueZ[writeIndex] = tileZ | 0x530000;
							writeIndex = writeIndex + 1 & 0xFFF;
							MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX + 1][tileZ] = setting;
						}
					}
					tileZ--;
					if (sceneSizeX > tileX + 1 && MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX + 1][tileZ] != setting) {
						MapRegion.roofQueueX[writeIndex] = 0x53000000 | 0x920000 | tileX + 1;
						MapRegion.roofQueueZ[writeIndex] = tileZ | 0x930000;
						writeIndex = writeIndex + 1 & 0xFFF;
						MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX + 1][tileZ] = setting;
					}
					tileZ--;
					if (tileZ >= 0) {
						if (tileX - 1 >= 0 && MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX - 1][tileZ] != setting && (SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][tileX][tileZ] & TileFlag.REMOVE_ROOF) == 0 && (SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][tileX - 1][tileZ + 1] & TileFlag.REMOVE_ROOF) == 0) {
							MapRegion.roofQueueX[writeIndex] = 0x12000000 | 0xD20000 | tileX - 1;
							MapRegion.roofQueueZ[writeIndex] = tileZ | 0xD30000;
							MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX - 1][tileZ] = setting;
							writeIndex = writeIndex + 1 & 0xFFF;
						}
						if (setting != MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX][tileZ]) {
							MapRegion.roofQueueX[writeIndex] = tileX | 0xD20000 | 0x93000000;
							MapRegion.roofQueueZ[writeIndex] = tileZ | 0xD30000;
							MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX][tileZ] = setting;
							writeIndex = writeIndex + 1 & 0xFFF;
						}
						if (tileX + 1 < sceneSizeX && MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX + 1][tileZ] != setting && (SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][tileX][tileZ] & TileFlag.REMOVE_ROOF) == 0 && (SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][tileX + 1][tileZ + 1] & TileFlag.REMOVE_ROOF) == 0) {
							MapRegion.roofQueueX[writeIndex] = tileX + 1 | 0xD2000000 | 0x920000;
							MapRegion.roofQueueZ[writeIndex] = tileZ | 0x930000;
							MapRegion.roofStamps[PlayerUpdate.currentLevel][tileX + 1][tileZ] = setting;
							writeIndex = writeIndex + 1 & 0xFFF;
						}
					}
				}
			}
			if (MapRegion.visibleBelowDrawheight[viewIndex] != -1000000) {
				MapRegion.visibleBelowDrawheight[viewIndex] += 40;
				MapRegion.roofMinX[viewIndex] -= 512;
				MapRegion.roofMaxX[viewIndex] += 512;
				MapRegion.roofMaxZ[viewIndex] += 512;
				MapRegion.roofMinZ[viewIndex] -= 512;
			}
			return true;
		}
	}

	@OriginalMember(owner = "client!bk", name = "t", descriptor = "I")
	public static int sceneSizeX = 104;
	@OriginalMember(owner = "client!kia", name = "o", descriptor = "[[I")
	public static int[][] xteas;

	@OriginalMember(owner = "client!bc", name = "e", descriptor = "J")
	public static long debugRegionLoadTimeStart;

	@OriginalMember(owner = "client!pk", name = "y", descriptor = "[I")
	public static int[] regionIds;
	@OriginalMember(owner = "client!bu", name = "u", descriptor = "[Lclient!fe;")
	public static final CollisionMap[] aClass104Array3 = new CollisionMap[4];

	@OriginalMember(owner = "client!km", name = "L", descriptor = "[I")
	public static int[] surfaceLocArchives;

	@OriginalMember(owner = "client!cd", name = "a", descriptor = "(B)V")
	public static void calculateViewRange() {
		MapRegion.viewRangeEnd = (int) ((double) MapRegion.sceneSizeX * 34.46D);
		MapRegion.viewRangeStart = 200;
		MapRegion.viewRangeEnd <<= 0x2;
		if (ClientState.activeToolkit.extendBuildArea()) {
			MapRegion.viewRangeEnd += 512;
		}
		MapRegion.applyViewDepthRange(false);
		if (ClientFeatures.RENDER_DISTANCE != 1.0D) {
			// Not part of the original client: the fog moves out with the draw distance, a little less far so the
			// edge of the scene stays hidden.
			MapRegion.viewRangeEnd = (int) ((double) MapRegion.viewRangeEnd * (1.0D + (ClientFeatures.RENDER_DISTANCE - 1.0D) * 0.8D));
		}
	}

	@OriginalMember(owner = "client!ag", name = "e", descriptor = "[I")
	public static int[] surfaceMapArchives;
	@OriginalMember(owner = "client!ik", name = "a", descriptor = "(ZLclient!vaa;[[B)V")
	public static void decodeMapTilesServer(@OriginalArg(1) MapRegion mapRegion, @OriginalArg(2) byte[][] regionData) {
		@Pc(12) int[] skyboxParams = new int[] { -1, 0, 0, 0, 0 };
		@Pc(20) int local20;
		@Pc(24) int local24;
		@Pc(34) int local34;
		@Pc(44) int local44;
		for (@Pc(14) int i = 0; i < mapRegion.levels; i++) {
			SoundEffectPlayer.updatePlayers();
			for (int chunkX = 0; chunkX < MapRegion.sceneSizeX >> 3; chunkX++) {
				for (int chunkZ = 0; chunkZ < MapRegion.sceneSizeY >> 3; chunkZ++) {
					int packedChunk = MapRegion.chunkBitpackedPosition[i][chunkX][chunkZ];
					if (packedChunk != -1) {
						int sourceLevel = packedChunk >> 24 & 0x3;
						if (!mapRegion.underwater || sourceLevel == 0) {
							@Pc(55) int rotation = packedChunk >> 1 & 0x3;
							@Pc(61) int sourceChunkX = packedChunk >> 14 & 0x3FF;
							@Pc(67) int sourceChunkZ = packedChunk >> 3 & 0x7FF;
							@Pc(78) int mapSquareId = (sourceChunkX / 8 << 8) + (sourceChunkZ / 8);
							for (@Pc(80) int j = 0; j < MapRegion.regionIds.length; j++) {
								if (MapRegion.regionIds[j] == mapSquareId && regionData[j] != null) {
									@Pc(102) Packet packet = new Packet(regionData[j]);
									mapRegion.decodeTilesServer(chunkX * 8, i, packet, sourceChunkZ, chunkZ * 8, sourceChunkX, MapRegion.aClass104Array3, rotation, sourceLevel);
									mapRegion.decodeEffectsServer(packet, sourceChunkZ, sourceChunkX, chunkX * 8, i, rotation, chunkZ * 8, ClientState.activeToolkit, skyboxParams[0] == -1 ? skyboxParams : null, sourceLevel);
									break;
								}
							}
						}
					}
				}
			}
		}
		for (int level = 0; level < mapRegion.levels; level++) {
			SoundEffectPlayer.updatePlayers();
			for (int chunkX = 0; chunkX < MapRegion.sceneSizeX >> 3; chunkX++) {
				for (int chunkZ = 0; chunkZ < MapRegion.sceneSizeY >> 3; chunkZ++) {
					int packedChunk = MapRegion.chunkBitpackedPosition[level][chunkX][chunkZ];
					if (packedChunk == -1) {
						mapRegion.setTileHeights(chunkX * 8, 8, chunkZ * 8, 8, level);
					}
				}
			}
		}
		if (skyboxParams[0] != -1) {
			Skybox.configNpcJs5 = TypeLists.SKYBOX.generateSkybox(skyboxParams[2], skyboxParams[3], skyboxParams[1], TypeLists.SUN, skyboxParams[0]);
			Skybox.skyboxAngle = skyboxParams[4];
		}
	}

	@OriginalMember(owner = "client!nga", name = "a", descriptor = "([[BILclient!vaa;)V")
	public static void decodeLocMapsClient(@OriginalArg(0) byte[][] locData, @OriginalArg(2) MapRegion mapRegion) {
		@Pc(8) int length = MapRegion.surfaceMapBuffer.length;
		for (@Pc(14) int i = 0; i < length; i++) {
			@Pc(20) byte[] data = locData[i];
			if (data != null) {
				@Pc(33) int baseX = (MapRegion.regionIds[i] >> 8) * 64 - MapRegion.sceneBaseX;
				@Pc(43) int baseZ = (MapRegion.regionIds[i] & 0xFF) * 64 - MapRegion.sceneBaseY;
				SoundEffectPlayer.updatePlayers();
				mapRegion.decodeLocMapClient(data, MapRegion.aClass104Array3, baseZ, ClientState.activeToolkit, baseX);
			}
		}
	}

	@OriginalMember(owner = "client!jw", name = "g", descriptor = "[[[I")
	public static int[][][] chunkBitpackedPosition;

	@OriginalMember(owner = "client!wc", name = "i", descriptor = "[I")
	public static int[] underwaterMapArchives;
	@OriginalMember(owner = "client!cu", name = "u", descriptor = "I")
	public static int sceneBaseX;

	@OriginalMember(owner = "client!afa", name = "n", descriptor = "[I")
	public static int[] npcSpawnArchives;

	@OriginalMember(owner = "client!r", name = "E", descriptor = "[I")
	public static int[] underwaterLocArchives;

	@OriginalMember(owner = "client!sc", name = "h", descriptor = "[I")
	public static final int[] DIAGONAL_WALL_DECOR_OFFSET_X = new int[] { 1, -1, -1, 1 };

	@OriginalMember(owner = "client!pia", name = "f", descriptor = "Lclient!vaa;")
	public static MapRegion aClass322_Sub1_2;

	@OriginalMember(owner = "client!kk", name = "i", descriptor = "[I")
	public static final int[] MAP_SIZES = new int[] { 104, 120, 136, 168 };

	@OriginalMember(owner = "client!lk", name = "w", descriptor = "Z")
	public static boolean lowDetailShadows;

	@OriginalMember(owner = "client!nn", name = "o", descriptor = "[[B")
	public static byte[][] underwaterMapBuffer;

	@OriginalMember(owner = "client!sl", name = "e", descriptor = "(II)V")
	public static void setMapSize(@OriginalArg(1) int mapSize) {
		if (mapSize == MapRegion.currentMapSize) {
			return;
		}
		MapRegion.sceneSizeX = MapRegion.sceneSizeY = MapRegion.MAP_SIZES[mapSize];
		MapRegion.calculateViewRange();
		MapRegion.chunkBitpackedPosition = new int[4][MapRegion.sceneSizeX >> 3][MapRegion.sceneSizeY >> 3];
		EntitySceneBuilder.tileDrawPrioritySharedBy = new int[MapRegion.sceneSizeX][MapRegion.sceneSizeY];
		EntitySceneBuilder.tileDrawPriority = new int[MapRegion.sceneSizeX][MapRegion.sceneSizeY];
		for (@Pc(41) int i = 0; i < 4; i++) {
			MapRegion.aClass104Array3[i] = CollisionMap.create(MapRegion.sceneSizeY, MapRegion.sceneSizeX);
		}
		MapRegion.roofStamps = new byte[4][MapRegion.sceneSizeX][MapRegion.sceneSizeY];
		SettingsBits.allocateTileFunctionMasks(MapRegion.sceneSizeY, MapRegion.sceneSizeX);
		EnvironmentManager.initialize(MapRegion.sceneSizeX >> 3, ClientState.activeToolkit, MapRegion.sceneSizeY >> 3);
		MapRegion.currentMapSize = mapSize;
	}

	@OriginalMember(owner = "client!uba", name = "a", descriptor = "(IIII[BB)Z")
	public static boolean areMapLocTypesLoaded(@OriginalArg(0) int sizeX, @OriginalArg(1) int baseX, @OriginalArg(2) int sizeY, @OriginalArg(3) int baseY, @OriginalArg(4) byte[] src) {
		@Pc(12) boolean allLoaded = true;
		@Pc(17) Packet packet = new Packet(src);
		@Pc(19) int locId = -1;
		label54: while (true) {
			@Pc(23) int idDelta = packet.readSum();
			if (idDelta == 0) {
				return allLoaded;
			}
			locId += idDelta;
			@Pc(31) int position = 0;
			@Pc(33) boolean found = false;
			while (true) {
				@Pc(39) int positionDelta;
				while (!found) {
					positionDelta = packet.readUSmart();
					if (positionDelta == 0) {
						continue label54;
					}
					position += positionDelta - 1;
					@Pc(65) int localY = position & 0x3F;
					@Pc(71) int localX = position >> 6 & 0x3F;
					@Pc(77) int shape = packet.readUByte() >> 2;
					@Pc(82) int x = localX + baseX;
					@Pc(86) int y = baseY + localY;
					if (x > 0 && y > 0 && x < sizeX - 1 && y < sizeY - 1) {
						@Pc(109) LocType locType = TypeLists.LOC.list(locId);
						if (shape != LocShapes.GROUNDDECOR || GamePreferences.uid.groundDecor.getValue() != 0 || locType.hasActions != LocInteractivity.NONINTERACTIVE || locType.blocksMovement == 1 || locType.forceDisplayDecoration) {
							found = true;
							if (!locType.isLoaded()) {
								UnloadedLocCount.unloadedLocCount++;
								allLoaded = false;
							}
						}
					}
				}
				positionDelta = packet.readUSmart();
				if (positionDelta == 0) {
					break;
				}
				packet.readUByte();
			}
		}
	}

	@OriginalMember(owner = "client!uw", name = "a", descriptor = "[I")
	public static final int[] DIAGONAL_WALL_DECOR_OFFSET_Z = new int[] { -1, -1, 1, 1 };
	@OriginalMember(owner = "client!kq", name = "S", descriptor = "Z")
	public static final boolean OCCLUDE_UNSPECIFIED_LOCS = false;

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(I[[BLclient!vaa;)V")
	public static void decodeLocMapsServer(@OriginalArg(1) byte[][] locMapData, @OriginalArg(2) MapRegion mapRegion) {
		for (@Pc(14) int i = 0; i < mapRegion.levels; i++) {
			SoundEffectPlayer.updatePlayers();
			for (@Pc(20) int j = 0; j < MapRegion.sceneSizeX >> 3; j++) {
				for (@Pc(24) int k = 0; k < MapRegion.sceneSizeY >> 3; k++) {
					@Pc(34) int packedChunk = MapRegion.chunkBitpackedPosition[i][j][k];
					if (packedChunk != -1) {
						@Pc(44) int chunkLevel = packedChunk >> 24 & 0x3;
						if (!mapRegion.underwater || chunkLevel == 0) {
							@Pc(55) int chunkRotation = packedChunk >> 1 & 0x3;
							@Pc(61) int chunkX = packedChunk >> 14 & 0x3FF;
							@Pc(67) int chunkZ = packedChunk >> 3 & 0x7FF;
							@Pc(77) int regionId = chunkZ / 8 + (chunkX / 8 << 8);
							for (@Pc(79) int m = 0; m < MapRegion.regionIds.length; m++) {
								if (MapRegion.regionIds[m] == regionId && locMapData[m] != null) {
									mapRegion.decodeLocMapServer((chunkX & 0x7) * 8, chunkRotation, locMapData[m], chunkLevel, (chunkZ & 0x7) * 8, ClientState.activeToolkit, k * 8, j * 8, MapRegion.aClass104Array3, i);
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!ea", name = "q", descriptor = "Lclient!vaa;")
	public static MapRegion aClass322_Sub1_1;

	@OriginalMember(owner = "client!bc", name = "f", descriptor = "I")
	public static int sceneBaseY;

	@OriginalMember(owner = "client!oga", name = "c", descriptor = "[I")
	public static final int[] WALL_DECOR_OFFSET_X = new int[] { 1, 0, -1, 0 };

	@OriginalMember(owner = "client!jm", name = "D", descriptor = "[[B")
	public static byte[][] underwaterLocBuffer;

	@OriginalMember(owner = "client!eg", name = "d", descriptor = "[[B")
	public static byte[][] surfaceMapBuffer;

	@OriginalMember(owner = "client!bg", name = "a", descriptor = "(BLclient!vaa;[[B)V")
	public static void decodeMapTilesClient(@OriginalArg(1) MapRegion mapRegion, @OriginalArg(2) byte[][] mapData) {
		@Pc(12) int[] skyboxData = new int[] { -1, 0, 0, 0, 0 };
		@Pc(23) int length = mapData.length;
		@Pc(44) int local44;
		for (@Pc(25) int i = 0; i < length; i++) {
			@Pc(31) byte[] data = mapData[i];
			if (data != null) {
				@Pc(38) Packet packet = new Packet(data);
				int mapSquareX = MapRegion.regionIds[i] >> 8;
				@Pc(50) int mapSquareZ = MapRegion.regionIds[i] & 0xFF;
				@Pc(57) int offsetX = mapSquareX * 64 - MapRegion.sceneBaseX;
				@Pc(64) int offsetZ = mapSquareZ * 64 - MapRegion.sceneBaseY;
				SoundEffectPlayer.updatePlayers();
				mapRegion.decodeTilesClient(packet, offsetX, MapRegion.aClass104Array3, MapRegion.sceneBaseY, MapRegion.sceneBaseX, offsetZ);
				mapRegion.decodeEffectsClient(offsetZ, ClientState.activeToolkit, packet, skyboxData, offsetX);
				if (!mapRegion.underwater && mapSquareX == MapRegion.mapCenterChunkX / 8 && MapRegion.mapBaseChunkY / 8 == mapSquareZ && skyboxData[0] != -1) {
					Skybox.configNpcJs5 = TypeLists.SKYBOX.generateSkybox(skyboxData[2], skyboxData[3], skyboxData[1], TypeLists.SUN, skyboxData[0]);
					Skybox.skyboxAngle = skyboxData[4];
				}
			}
		}
		for (@Pc(133) int j = 0; j < length; j++) {
			@Pc(145) int regionOffsetX = (MapRegion.regionIds[j] >> 8) * 64 - MapRegion.sceneBaseX;
			int regionOffsetZ = (MapRegion.regionIds[j] & 0xFF) * 64 - MapRegion.sceneBaseY;
			@Pc(160) byte[] regionData = mapData[j];
			if (regionData == null && MapRegion.mapBaseChunkY < 800) {
				SoundEffectPlayer.updatePlayers();
				mapRegion.setMapSquareHeights(regionOffsetZ, regionOffsetX);
			}
		}
	}

	@OriginalMember(owner = "client!pda", name = "Q", descriptor = "[I")
	public static final int[] WALL_DECOR_OFFSET_Z = new int[] { 0, -1, 0, 1 };
	@OriginalMember(owner = "client!il", name = "p", descriptor = "I")
	public static int currentMapSize = -1;

	@OriginalMember(owner = "client!qu", name = "O", descriptor = "[I")
	public static int[] roofMaxX = new int[2];

	@OriginalMember(owner = "client!raa", name = "y", descriptor = "I")
	public static int viewRangeStart;

	@OriginalMember(owner = "client!tm", name = "j", descriptor = "I")
	public static int inTutorialIsland = 0;

	@OriginalMember(owner = "client!tt", name = "s", descriptor = "I")
	public static int viewRangeEnd;

	@OriginalMember(owner = "client!tw", name = "G", descriptor = "[[B")
	public static byte[][] locMapBuffers;

	@OriginalMember(owner = "client!ha", name = "u", descriptor = "I")
	public static int mapCenterChunkX;

	@OriginalMember(owner = "client!ka", name = "d", descriptor = "[I")
	public static int[] roofMinZ = new int[2];

	@OriginalMember(owner = "client!wn", name = "b", descriptor = "[I")
	public static final int[] roofQueueZ = new int[4096];

	@OriginalMember(owner = "client!uv", name = "i", descriptor = "[I")
	public static final int[] FOG_VISIBLE_TILES = new int[] { 32, 39, 44, 47 };

	@OriginalMember(owner = "client!jj", name = "H", descriptor = "[I")
	public static int[] roofMaxZ = new int[2];

	@OriginalMember(owner = "client!iha", name = "u", descriptor = "[I")
	public static final int[] roofQueueX = new int[4096];

	@OriginalMember(owner = "client!sga", name = "a", descriptor = "(IZ)V")
	public static void applyViewDepthRange(@OriginalArg(1) boolean applyOrtho) {
		@Pc(5) int zFar = MapRegion.viewRangeEnd;
		@Pc(7) int zNear = MapRegion.viewRangeStart;
		if (applyOrtho && OrthoMode.orthographicProjection) {
			zFar <<= 0x1;
			zNear = -zFar;
		}
		ClientState.activeToolkit.f(zNear, zFar);
	}

	@OriginalMember(owner = "client!ug", name = "a", descriptor = "(I)V")
	public static void handleRoofDisplay() {
		if (GamePreferences.uid.lb.getValue() != 2) {
			return;
		}
		@Pc(19) byte roofValue = (byte) (SceneDrawer.timesSceneRendered - 4 & 0xFF);
		@Pc(23) int column = SceneDrawer.timesSceneRendered % MapRegion.sceneSizeX;
		@Pc(29) int local29;
		for (@Pc(25) int i = 0; i < 4; i++) {
			for (int tileZ = 0; tileZ < MapRegion.sceneSizeY; tileZ++) {
				MapRegion.roofStamps[i][column][tileZ] = roofValue;
			}
		}
		if (PlayerUpdate.currentLevel == 3) {
			return;
		}
		for (int viewIndex = 0; viewIndex < 2; viewIndex++) {
			MapRegion.visibleBelowDrawheight[viewIndex] = -1000000;
			MapRegion.roofMinX[viewIndex] = 1000000;
			MapRegion.roofMaxX[viewIndex] = 0;
			MapRegion.roofMinZ[viewIndex] = 1000000;
			MapRegion.roofMaxZ[viewIndex] = 0;
		}
		@Pc(84) int targetX = PlayerUpdate.localPlayer.x;
		@Pc(87) int targetY = PlayerUpdate.localPlayer.z;
		@Pc(141) int local141;
		if (Camera.cameraCalculationType != CameraMode.MODE_DEFAULT && Camera.roofRemovalTargetX == -1) {
			int cameraGroundHeight = Scene.tileHeightForAbsolutePosition(Camera.cameraPositionAbsoluteX, PlayerUpdate.currentLevel, Camera.cameraPositionAbsoluteY);
			if (cameraGroundHeight - Camera.cameraPositionAbsoluteZ < 3200 && (SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][Camera.cameraPositionAbsoluteX >> 9][Camera.cameraPositionAbsoluteY >> 9] & TileFlag.REMOVE_ROOF) != 0) {
				MapRegion.calculateVisibilityAdjustments(Camera.cameraPositionAbsoluteY >> 9, Scene.aClass100ArrayArrayArray3, Camera.cameraPositionAbsoluteX >> 9, 1, false);
				return;
			}
			return;
		}
		if (Camera.cameraCalculationType != CameraMode.MODE_DEFAULT) {
			targetY = Camera.roofRemovalTargetY;
			targetX = Camera.roofRemovalTargetX;
		}
		if ((SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][targetX >> 9][targetY >> 9] & TileFlag.REMOVE_ROOF) != 0) {
			MapRegion.calculateVisibilityAdjustments(targetY >> 9, Scene.aClass100ArrayArrayArray3, targetX >> 9, 0, false);
		}
		if (Camera.cameraPitch >= 2560) {
			return;
		}
		int cameraTileX = Camera.cameraPositionAbsoluteX >> 9;
		@Pc(145) int cameraTileY = Camera.cameraPositionAbsoluteY >> 9;
		@Pc(149) int targetTileX = targetX >> 9;
		@Pc(153) int targetTileY = targetY >> 9;
		@Pc(161) int deltaX;
		if (targetTileX > cameraTileX) {
			deltaX = targetTileX - cameraTileX;
		} else {
			deltaX = cameraTileX - targetTileX;
		}
		@Pc(176) int deltaY;
		if (targetTileY <= cameraTileY) {
			deltaY = cameraTileY - targetTileY;
		} else {
			deltaY = targetTileY - cameraTileY;
		}
		if ((deltaX != 0 || deltaY != 0) && -MapRegion.sceneSizeX < deltaX && deltaX < MapRegion.sceneSizeX && -MapRegion.sceneSizeY < deltaY && MapRegion.sceneSizeY > deltaY) {
			@Pc(256) int slope;
			@Pc(258) int accumulator;
			if (deltaY >= deltaX) {
				slope = deltaX * 65536 / deltaY;
				accumulator = 32768;
				while (targetTileY != cameraTileY) {
					if (targetTileY > cameraTileY) {
						cameraTileY++;
					} else if (cameraTileY > targetTileY) {
						cameraTileY--;
					}
					if ((SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][cameraTileX][cameraTileY] & TileFlag.REMOVE_ROOF) != 0) {
						MapRegion.calculateVisibilityAdjustments(cameraTileY, Scene.aClass100ArrayArrayArray3, cameraTileX, 1, false);
						return;
					}
					accumulator += slope;
					if (accumulator >= 65536) {
						if (cameraTileX < targetTileX) {
							cameraTileX++;
						} else if (targetTileX < cameraTileX) {
							cameraTileX--;
						}
						accumulator -= 65536;
						if ((SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][cameraTileX][cameraTileY] & 0x4) != 0) {
							MapRegion.calculateVisibilityAdjustments(cameraTileY, Scene.aClass100ArrayArrayArray3, cameraTileX, 1, false);
							return;
						}
					}
				}
				return;
			}
			slope = deltaY * 65536 / deltaX;
			accumulator = 32768;
			while (cameraTileX != targetTileX) {
				if (cameraTileX < targetTileX) {
					cameraTileX++;
				} else if (cameraTileX > targetTileX) {
					cameraTileX--;
				}
				if ((SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][cameraTileX][cameraTileY] & TileFlag.REMOVE_ROOF) != 0) {
					MapRegion.calculateVisibilityAdjustments(cameraTileY, Scene.aClass100ArrayArrayArray3, cameraTileX, 1, false);
					return;
				}
				accumulator += slope;
				if (accumulator >= 65536) {
					if (targetTileY > cameraTileY) {
						cameraTileY++;
					} else if (targetTileY < cameraTileY) {
						cameraTileY--;
					}
					accumulator -= 65536;
					if ((SettingsBits.tileFunctionMasks[PlayerUpdate.currentLevel][cameraTileX][cameraTileY] & TileFlag.REMOVE_ROOF) != 0) {
						MapRegion.calculateVisibilityAdjustments(cameraTileY, Scene.aClass100ArrayArrayArray3, cameraTileX, 1, false);
						return;
					}
				}
			}
			return;
		}
		ExplicitRuntimeException.reportError("RC: " + cameraTileX + "," + cameraTileY + " " + targetTileX + "," + targetTileY + " " + MapRegion.sceneBaseX + "," + MapRegion.sceneBaseY, null);
		return;
	}

	@OriginalMember(owner = "client!os", name = "g", descriptor = "[I")
	public static int[] roofMinX = new int[2];

	@OriginalMember(owner = "client!cga", name = "j", descriptor = "I")
	public static int areaMode = 0;

	@OriginalMember(owner = "client!cv", name = "q", descriptor = "[[B")
	public static byte[][] npcSpawnBuffers;

	@OriginalMember(owner = "client!gs", name = "b", descriptor = "[I")
	public static final int[] DRAW_DISTANCES_NO_FOG = new int[] { 28, 35, 40, 44 };

	@OriginalMember(owner = "client!it", name = "j", descriptor = "Z")
	public static final boolean SKIP_OCCLUDER_LOCS = false;

	@OriginalMember(owner = "client!de", name = "f", descriptor = "I")
	public static int mapBaseChunkY;

	@OriginalMember(owner = "client!de", name = "h", descriptor = "[I")
	public static int[] visibleBelowDrawheight = new int[2];

	@OriginalMember(owner = "client!id", name = "a", descriptor = "(I)V")
	public static void enableLowDetailShadows() {
		MapRegion.lowDetailShadows = true;
	}

	@OriginalMember(owner = "client!pga", name = "n", descriptor = "I")
	public static int mapBuildLevel = 0;

	@OriginalMember(owner = "client!vga", name = "a", descriptor = "(II)Z")
	public static boolean allowsOutOfBounds(@OriginalArg(1) int areaMode) {
		return areaMode == 3 || areaMode == 4;
	}

	@OriginalMember(owner = "client!vs", name = "e", descriptor = "[[[B")
	public static byte[][][] roofStamps;

	@OriginalMember(owner = "client!vs", name = "a", descriptor = "(B)V")
	public static void updateInTutorialIsland() {
		MapRegion.inTutorialIsland = 0;
		@Pc(13) int absoluteX = (PlayerUpdate.localPlayer.x >> 9) + MapRegion.sceneBaseX;
		@Pc(20) int absoluteZ = MapRegion.sceneBaseY + (PlayerUpdate.localPlayer.z >> 9);
		if (absoluteX >= 3053 && absoluteX <= 3156 && absoluteZ >= 3056 && absoluteZ <= 3136) {
			MapRegion.inTutorialIsland = 1;
		}
		if (absoluteX >= 3072 && absoluteX <= 3118 && absoluteZ >= 9492 && absoluteZ <= 9535) {
			MapRegion.inTutorialIsland = 1;
		}
		if (MapRegion.inTutorialIsland == 1 && absoluteX >= 3139 && absoluteX <= 3199 && absoluteZ >= 3008 && absoluteZ <= 3062) {
			MapRegion.inTutorialIsland = 0;
		}
	}
}
