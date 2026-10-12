package com.jagex.game.runetek5

import com.jagex.game.runetek5.core.constants.LocLayer
import com.jagex.game.runetek5.core.constants.LocShapes
import com.jagex.game.runetek5.core.constants.TileFlag
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.entity.gameobject.GameObject
import com.jagex.game.runetek5.scene.CollisionMap
import com.jagex.game.runetek5.config.loctype.LocOcclusionMode
import com.jagex.game.runetek5.graphics.light.StaticPointLight
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.statics.ZoneLocRotation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity
import com.jagex.game.runetek5.entity.DynamicGroundDecor
import com.jagex.game.runetek5.entity.DynamicLocation
import com.jagex.game.runetek5.entity.DynamicWall
import com.jagex.game.runetek5.entity.DynamicWallDecor
import com.jagex.game.runetek5.entity.GroundDecor
import com.jagex.game.runetek5.entity.StaticGroundDecor
import com.jagex.game.runetek5.entity.StaticLocation
import com.jagex.game.runetek5.entity.StaticWall
import com.jagex.game.runetek5.entity.StaticWallDecor
import com.jagex.game.runetek5.entity.Wall
import com.jagex.game.runetek5.entity.WallDecor
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.config.light.LightType
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.scene.environment.Environment
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.entity.InteractiveEntityContainer
import com.jagex.game.runetek5.scene.culling.Culling
import com.jagex.game.runetek5.scene.tile.Tile
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer
import com.jagex.game.runetek5.config.loctype.LocInteractivity
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.sound.player.area.AmbientSound
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.camera.CameraMode
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.jClass

@OriginalClass("client!vaa")
class MapRegion @OriginalMember(owner = "client!vaa", name = "<init>", descriptor = "(IIIZ)V") constructor(@OriginalArg(0) levels: Int, @OriginalArg(1) regionSizeX: Int, @OriginalArg(2) regionSizeZ: Int, @OriginalArg(3) underwater: Boolean) : Terrain(levels, regionSizeX, regionSizeZ, underwater, TypeLists.aClass49_3, TypeLists.FLU) {

	@OriginalMember(owner = "client!vaa", name = "N", descriptor = "I")
	@JvmField
	var minLevel: Int = 99

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(IIILclient!ha;IIIIIILclient!fe;)V")
	fun addObject(@OriginalArg(0) x: Int, @OriginalArg(1) minLevel: Int, @OriginalArg(2) locId: Int, @OriginalArg(3) toolkit: Toolkit?, @OriginalArg(4) plane: Int, @OriginalArg(6) shape: Int, @OriginalArg(7) z: Int, @OriginalArg(8) sequenceId: Int, @OriginalArg(9) rotation: Int, @OriginalArg(10) collisionMap: CollisionMap?) {
		if (GamePreferences.uid!!.animateBackgroundOverride!!.getValue() == 0 && !SettingsBits.isVisibleFromLevel(z, x, MapRegion.mapBuildLevel, plane)) {
			return
		}
		if (minLevel < this.minLevel) {
			this.minLevel = minLevel
		}
		@Pc(36) val locType: LocType = TypeLists.LOC!!.list(locId)
		if (GamePreferences.uid!!.textures!!.getValue() == 0 && locType.requiresTextures) {
			return
		}
		@Pc(64) val locSizeX: Int
		@Pc(61) val locSizeZ: Int
		if (rotation == 1 || rotation == 3) {
			locSizeZ = locType.sizeX
			locSizeX = locType.sizeY
		} else {
			locSizeZ = locType.sizeY
			locSizeX = locType.sizeX
		}
		@Pc(100) val x1: Int
		@Pc(108) val x2: Int
		if (this.regionSizeX >= x + locSizeX) {
			x1 = (locSizeX shr 1) + x
			x2 = x + ((locSizeX + 1) shr 1)
		} else {
			x1 = x
			x2 = x + 1
		}
		@Pc(128) val z1: Int
		@Pc(126) val z2: Int
		if (this.regionSizeZ < locSizeZ + z) {
			z2 = z + 1
			z1 = z
		} else {
			z2 = ((locSizeZ + 1) shr 1) + z
			z1 = (locSizeZ shr 1) + z
		}
		@Pc(148) val ground: Ground? = Scene.aClass65Array1!![plane]
		@Pc(175) val averageHeight: Int = (ground!!.getHeight(z1, x1) + ground.getHeight(z1, x2) + ground.getHeight(z2, x1) + ground.getHeight(z2, x2)) shr 2
		@Pc(184) val sceneX: Int = (x shl 9) + (locSizeX shl 8)
		@Pc(192) val sceneZ: Int = (locSizeZ shl 8) + (z shl 9)
		@Pc(204) val applyShading: Boolean = MapRegion.lowDetailShadows && !this.underwater && locType.delayShading
		if (locType.hasSounds()) {
			AmbientSound.createAmbientSoundEffect(null, minLevel, z, x, null, locType, rotation)
		}
		@Pc(238) val stationary: Boolean = sequenceId == -1 && locType.animationId == -1 && locType.animations == null && locType.alternativeIds == null && !locType.hasAnimation
		if (MapRegion.SKIP_OCCLUDER_LOCS && (LocShapes.isWallShape(shape) && locType.occlusionMode != LocOcclusionMode.ALL || !(!LocShapes.isRoof(shape) || locType.occlusionMode != 0))) {
			return
		}
		if (shape != 22) {
			@Pc(389) var positionEntity: AbstractInteractiveEntity?
			@Pc(357) var staticLocation: StaticLocation?
			@Pc(361) var local361: Int
			@Pc(455) var local455: Int
			if (shape == LocShapes.CENTREPIECE_STRAIGHT || shape == LocShapes.CENTREPIECE_DIAGONAL) {
				staticLocation = null
				var shadowIntensity: Int
				if (stationary) {
					@Pc(419) val staticLocation2: StaticLocation = StaticLocation(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, x, locSizeX + x - 1, z, locSizeZ + z - 1, shape, rotation, applyShading)
					positionEntity = staticLocation2
					staticLocation = staticLocation2
					shadowIntensity = staticLocation2.shadowValue()
				} else {
					shadowIntensity = 15
					positionEntity = DynamicLocation(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, x, locSizeX + x - 1, z, z + locSizeZ - 1, shape, rotation, sequenceId)
				}
				if (Scene.addInteractive(positionEntity, false)) {
					if (staticLocation != null && staticLocation.hardShadow()) {
						staticLocation.addShadow(toolkit)
					}
					if (locType.staticShadow && MapRegion.lowDetailShadows) {
						if (shadowIntensity > 30) {
							shadowIntensity = 30
						}
						var tileOffsetX: Int = 0
						while (tileOffsetX <= locSizeX) {
							@Pc(459) var i: Int = 0
							while (i <= locSizeZ) {
								ground.ka(tileOffsetX + x, z + i, shadowIntensity)
								i++
							}
							tileOffsetX++
						}
					}
				}
				if (locType.blocksMovement != 0 && collisionMap != null) {
					collisionMap.flagBlockedObject(!locType.ignoreClipOnAltRoute, locSizeX, z, x, locType.blocksProjectiles, locSizeZ)
				}
			} else if ((shape < 12 || shape > 17) && (shape < 18 || shape > 21)) {
				@Pc(701) var wall: Wall?
				if (shape == LocShapes.WALL_STRAIGHT) {
					@Pc(674) var occlusionMode: Int = locType.occlusionMode
					if (MapRegion.OCCLUDE_UNSPECIFIED_LOCS && locType.occlusionMode == -1) {
						occlusionMode = LocOcclusionMode.ALL
					}
					if (stationary) {
						@Pc(719) val staticWall: StaticWall = StaticWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, shape, rotation, applyShading)
						if (staticWall.hardShadow()) {
							staticWall.addShadow(toolkit)
						}
						wall = staticWall
					} else {
						wall = DynamicWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, shape, rotation, sequenceId)
					}
					Scene.addWall(minLevel, x, z, wall, null)
					if (rotation == 0) {
						if (MapRegion.lowDetailShadows && locType.staticShadow) {
							ground.ka(x, z, 50)
							ground.ka(x, z + 1, 50)
						}
						if (occlusionMode == LocOcclusionMode.ALL && !this.underwater) {
							Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z, minLevel, 1)
						}
					} else if (rotation == 1) {
						if (MapRegion.lowDetailShadows && locType.staticShadow) {
							ground.ka(x, z + 1, 50)
							ground.ka(x + 1, z + 1, 50)
						}
						if (occlusionMode == LocOcclusionMode.ALL && !this.underwater) {
							Culling.addWallCulling(x, -locType.cullXZ, locType.cullY, z + 1, minLevel, 2)
						}
					} else if (rotation == 2) {
						if (MapRegion.lowDetailShadows && locType.staticShadow) {
							ground.ka(x + 1, z, 50)
							ground.ka(x + 1, z + 1, 50)
						}
						if (occlusionMode == LocOcclusionMode.ALL && !this.underwater) {
							Culling.addWallCulling(x + 1, -locType.cullXZ, locType.cullY, z, minLevel, 1)
						}
					} else if (rotation == 3) {
						if (MapRegion.lowDetailShadows && locType.staticShadow) {
							ground.ka(x, z, 50)
							ground.ka(x + 1, z, 50)
						}
						if (occlusionMode == LocOcclusionMode.ALL && !this.underwater) {
							Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z, minLevel, 2)
						}
					}
					if (locType.blocksMovement != 0 && collisionMap != null) {
						collisionMap.flagBoundary(z, x, shape, rotation, !locType.ignoreClipOnAltRoute, locType.blocksProjectiles)
					}
					if (locType.decorDisplacement != 64) {
						Scene.offsetWallDecoration(minLevel, x, z, locType.decorDisplacement)
					}
				} else {
					@Pc(963) var wall2: Wall?
					@Pc(981) var staticWall2: StaticWall?
					if (shape == LocShapes.WALL_DIAGONALCORNER) {
						if (stationary) {
							staticWall2 = StaticWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, shape, rotation, applyShading)
							if (staticWall2.hardShadow()) {
								staticWall2.addShadow(toolkit)
							}
							wall2 = staticWall2
						} else {
							wall2 = DynamicWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, shape, rotation, sequenceId)
						}
						Scene.addWall(minLevel, x, z, wall2, null)
						if (locType.staticShadow && MapRegion.lowDetailShadows) {
							if (rotation == 0) {
								ground.ka(x, z + 1, 50)
							} else if (rotation == 1) {
								ground.ka(x + 1, z + 1, 50)
							} else if (rotation == 2) {
								ground.ka(x + 1, z, 50)
							} else if (rotation == 3) {
								ground.ka(x, z, 50)
							}
						}
						if (locType.blocksMovement != 0 && collisionMap != null) {
							collisionMap.flagBoundary(z, x, shape, rotation, !locType.ignoreClipOnAltRoute, locType.blocksProjectiles)
						}
					} else if (shape == 2) {
						val nextRotation: Int = (rotation + 1) and 0x3
						if (stationary) {
							@Pc(1104) val staticWall3: StaticWall = StaticWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, shape, rotation + 4, applyShading)
							@Pc(1120) val staticWall4: StaticWall = StaticWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, shape, nextRotation, applyShading)
							if (staticWall3.hardShadow()) {
								staticWall3.addShadow(toolkit)
							}
							wall = staticWall4
							wall2 = staticWall3
							if (staticWall4.hardShadow()) {
								staticWall4.addShadow(toolkit)
							}
						} else {
							wall2 = DynamicWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, shape, rotation + 4, sequenceId)
							wall = DynamicWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, shape, nextRotation, sequenceId)
						}
						Scene.addWall(minLevel, x, z, wall2, wall)
						if ((locType.occlusionMode == 1 || MapRegion.OCCLUDE_UNSPECIFIED_LOCS && locType.occlusionMode == -1) && !this.underwater) {
							if (rotation == 0) {
								Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z, minLevel, 1)
								Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z + 1, minLevel, 2)
							} else if (rotation == 1) {
								Culling.addWallCulling(x + 1, locType.cullXZ, locType.cullY, z, minLevel, 1)
								Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z + 1, minLevel, 2)
							} else if (rotation == 2) {
								Culling.addWallCulling(x + 1, locType.cullXZ, locType.cullY, z, minLevel, 1)
								Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z, minLevel, 2)
							} else if (rotation == 3) {
								Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z, minLevel, 1)
								Culling.addWallCulling(x, locType.cullXZ, locType.cullY, z, minLevel, 2)
							}
						}
						if (locType.blocksMovement != 0 && collisionMap != null) {
							collisionMap.flagBoundary(z, x, shape, rotation, !locType.ignoreClipOnAltRoute, locType.blocksProjectiles)
						}
						if (locType.decorDisplacement != 64) {
							Scene.offsetWallDecoration(minLevel, x, z, locType.decorDisplacement)
						}
					} else if (shape == 3) {
						if (stationary) {
							staticWall2 = StaticWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, shape, rotation, applyShading)
							if (staticWall2.hardShadow()) {
								staticWall2.addShadow(toolkit)
							}
							wall2 = staticWall2
						} else {
							wall2 = DynamicWall(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, shape, rotation, sequenceId)
						}
						Scene.addWall(minLevel, x, z, wall2, null)
						if (locType.staticShadow && MapRegion.lowDetailShadows) {
							if (rotation == 0) {
								ground.ka(x, z + 1, 50)
							} else if (rotation == 1) {
								ground.ka(x + 1, z + 1, 50)
							} else if (rotation == 2) {
								ground.ka(x + 1, z, 50)
							} else if (rotation == 3) {
								ground.ka(x, z, 50)
							}
						}
						if (locType.blocksMovement != 0 && collisionMap != null) {
							collisionMap.flagBoundary(z, x, shape, rotation, !locType.ignoreClipOnAltRoute, locType.blocksProjectiles)
						}
					} else if (shape == LocShapes.WALL_DIAGONAL) {
						if (stationary) {
							staticLocation = StaticLocation(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, x, x, z, z, shape, rotation, applyShading)
							if (staticLocation.hardShadow()) {
								staticLocation.addShadow(toolkit)
							}
							positionEntity = staticLocation
						} else {
							positionEntity = DynamicLocation(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, x, x + locSizeX - 1, z, z + locSizeZ - 1, shape, rotation, sequenceId)
						}
						Scene.addInteractive(positionEntity, false)
						if (locType.occlusionMode == LocOcclusionMode.ALL && !this.underwater) {
							@Pc(1570) val cullFlags: Byte
							if ((rotation and 0x1) == 0) {
								cullFlags = 8
							} else {
								cullFlags = 16
							}
							Culling.addWallCulling(x, 0, locType.cullY, z, minLevel, cullFlags.toInt())
						}
						if (locType.blocksMovement != 0 && collisionMap != null) {
							collisionMap.flagBlockedObject(!locType.ignoreClipOnAltRoute, locSizeX, z, x, locType.blocksProjectiles, locSizeZ)
						}
						if (locType.decorDisplacement != 64) {
							Scene.offsetWallDecoration(minLevel, x, z, locType.decorDisplacement)
						}
					} else {
						@Pc(1639) var wallDecor: WallDecor?
						if (shape == LocShapes.WALLDECOR_STRAIGHT_NOOFFSET) {
							if (stationary) {
								@Pc(1658) val staticWallDecor: StaticWallDecor = StaticWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, 0, 0, shape, rotation)
								if (staticWallDecor.hardShadow()) {
									staticWallDecor.addShadow(toolkit)
								}
								wallDecor = staticWallDecor
							} else {
								wallDecor = DynamicWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, 0, 0, shape, rotation, sequenceId)
							}
							Scene.addWallDecoration(minLevel, x, z, wallDecor, null)
						} else {
							@Pc(1684) var local1684: Int
							@Pc(1690) var location: GameObject?
							@Pc(1729) var staticWallDecor2: StaticWallDecor?
							if (shape == LocShapes.WALLDECOR_STRAIGHT_OFFSET) {
								var displacement: Int = 65
								location = Scene.getWall(minLevel, x, z) as GameObject?
								if (location != null) {
									displacement = TypeLists.LOC!!.list(location.getId()).decorDisplacement + 1
								}
								if (stationary) {
									staticWallDecor2 = StaticWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, displacement * MapRegion.WALL_DECOR_OFFSET_X[rotation], MapRegion.WALL_DECOR_OFFSET_Z[rotation] * displacement, shape, rotation)
									if (staticWallDecor2.hardShadow()) {
										staticWallDecor2.addShadow(toolkit)
									}
									wallDecor = staticWallDecor2
								} else {
									wallDecor = DynamicWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, MapRegion.WALL_DECOR_OFFSET_X[rotation] * displacement, displacement * MapRegion.WALL_DECOR_OFFSET_Z[rotation], shape, rotation, sequenceId)
								}
								Scene.addWallDecoration(minLevel, x, z, wallDecor, null)
							} else if (shape == LocShapes.WALLDECOR_DIAGONAL_OFFSET) {
								var diagonalDisplacement: Int = 33
								location = Scene.getWall(minLevel, x, z) as GameObject?
								if (location != null) {
									diagonalDisplacement = TypeLists.LOC!!.list(location.getId()).decorDisplacement / 2 + 1
								}
								if (stationary) {
									staticWallDecor2 = StaticWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, diagonalDisplacement * MapRegion.WALL_DECOR_OFFSET_X[rotation], MapRegion.WALL_DECOR_OFFSET_Z[rotation] * diagonalDisplacement, shape, rotation + 4)
									if (staticWallDecor2.hardShadow()) {
										staticWallDecor2.addShadow(toolkit)
									}
									wallDecor = staticWallDecor2
								} else {
									wallDecor = DynamicWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, MapRegion.DIAGONAL_WALL_DECOR_OFFSET_X[rotation] * diagonalDisplacement, diagonalDisplacement * MapRegion.DIAGONAL_WALL_DECOR_OFFSET_Z[rotation], shape, rotation + 4, sequenceId)
								}
								Scene.addWallDecoration(minLevel, x, z, wallDecor, null)
							} else if (shape == LocShapes.WALLDECOR_DIAGONAL_NOOFFSET) {
								val oppositeRotation: Int = (rotation + 2) and 0x3
								if (stationary) {
									@Pc(1929) val staticWallDecor3: StaticWallDecor = StaticWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, 0, 0, shape, oppositeRotation + 4)
									if (staticWallDecor3.hardShadow()) {
										staticWallDecor3.addShadow(toolkit)
									}
									wallDecor = staticWallDecor3
								} else {
									wallDecor = DynamicWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, 0, 0, shape, oppositeRotation + 4, sequenceId)
								}
								Scene.addWallDecoration(minLevel, x, z, wallDecor, null)
							} else if (shape == LocShapes.WALLDECOR_DIAGONAL_BOTH) {
								val oppositeRotation: Int = (rotation + 2) and 0x3
								var diagonalDisplacement: Int = 33
								@Pc(1965) val location2: GameObject? = Scene.getWall(minLevel, x, z) as GameObject?
								if (location2 != null) {
									diagonalDisplacement = TypeLists.LOC!!.list(location2.getId()).decorDisplacement / 2 + 1
								}
								@Pc(2011) val wallDecor2: WallDecor?
								@Pc(2031) val wallDecor3: WallDecor?
								if (stationary) {
									wallDecor2 = StaticWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, diagonalDisplacement * MapRegion.DIAGONAL_WALL_DECOR_OFFSET_X[rotation], MapRegion.DIAGONAL_WALL_DECOR_OFFSET_Z[rotation] * diagonalDisplacement, shape, rotation + 4)
									wallDecor3 = StaticWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, 0, 0, shape, oppositeRotation + 4)
									if ((wallDecor2 as StaticWallDecor).hardShadow()) {
										(wallDecor2 as StaticWallDecor).addShadow(toolkit)
									}
									if ((wallDecor3 as StaticWallDecor).hardShadow()) {
										(wallDecor3 as StaticWallDecor).addShadow(toolkit)
									}
								} else {
									wallDecor2 = DynamicWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, diagonalDisplacement * MapRegion.DIAGONAL_WALL_DECOR_OFFSET_X[rotation], diagonalDisplacement * MapRegion.DIAGONAL_WALL_DECOR_OFFSET_Z[rotation], shape, rotation + 4, sequenceId)
									wallDecor3 = DynamicWallDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, 0, 0, shape, oppositeRotation + 4, sequenceId)
								}
								Scene.addWallDecoration(minLevel, x, z, wallDecor2, wallDecor3)
							}
						}
					}
				}
			} else {
				if (stationary) {
					staticLocation = StaticLocation(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, x, locSizeX + x - 1, z, z + locSizeZ - 1, shape, rotation, applyShading)
					positionEntity = staticLocation
					if (staticLocation.hardShadow()) {
						staticLocation.addShadow(toolkit)
					}
				} else {
					positionEntity = DynamicLocation(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, x, x + locSizeX - 1, z, z + locSizeZ - 1, shape, rotation, sequenceId)
				}
				Scene.addInteractive(positionEntity, false)
				if (MapRegion.lowDetailShadows && !this.underwater && shape >= 12 && shape <= 17 && shape != 13 && minLevel > 0 && locType.occlusionMode != 0) {
					this.occluderFlags!![minLevel]!![x]!![z] = (this.occluderFlags!![minLevel]!![x]!![z].toInt() or 0x4).toByte()
				}
				if (locType.blocksMovement != 0 && collisionMap != null) {
					collisionMap.flagBlockedObject(!locType.ignoreClipOnAltRoute, locSizeX, z, x, locType.blocksProjectiles, locSizeZ)
				}
			}
		} else if (GamePreferences.uid!!.groundDecor!!.getValue() != 0 || locType.hasActions != 0 || locType.blocksMovement == 1 || locType.forceDisplayDecoration) {
			@Pc(301) val groundDecor: GroundDecor?
			if (stationary) {
				@Pc(318) val staticGroundDecor: StaticGroundDecor = StaticGroundDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, rotation, applyShading)
				groundDecor = staticGroundDecor
				if (staticGroundDecor.hardShadow()) {
					staticGroundDecor.addShadow(toolkit)
				}
			} else {
				groundDecor = DynamicGroundDecor(toolkit, locType, minLevel, plane, sceneX, averageHeight, sceneZ, this.underwater, rotation, sequenceId)
			}
			Scene.setGroundDecoration(minLevel, x, z, groundDecor)
			if (locType.blocksMovement == 1 && collisionMap != null) {
				collisionMap.flagFloorDecoration(z, x)
			}
		}
	}

	@OriginalMember(owner = "client!vaa", name = "b", descriptor = "(IIIII)Lclient!se;")
	private fun getGameObject(@OriginalArg(0) layer: Int, @OriginalArg(1) z: Int, @OriginalArg(2) x: Int, @OriginalArg(3) level: Int): GameObject? {
		@Pc(5) var location: GameObject? = null
		if (layer == LocLayer.WALL) {
			location = Scene.getWall(level, x, z) as GameObject?
		}
		if (layer == LocLayer.WALLDECOR) {
			location = Scene.getWallDecoration(level, x, z) as GameObject?
		}
		if (layer == LocLayer.GROUND) {
			location = Scene.getInteractiveOfType(level, x, z, GameObject::class.jClass) as GameObject?
		}
		if (layer == LocLayer.GROUNDDECOR) {
			location = Scene.getGroundDecoration(level, x, z) as GameObject?
		}
		return location
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(ILclient!ha;Lclient!cea;[IIB)V")
	fun decodeEffectsClient(@OriginalArg(0) regionStartZ: Int, @OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) packet: Packet?, @OriginalArg(3) skyboxData: IntArray?, @OriginalArg(4) regionStartX: Int) {
		if (this.underwater) {
			return
		}
		@Pc(11) var lightingDecoded: Boolean = false
		@Pc(13) var environment: Environment? = null
		if (skyboxData != null) {
			skyboxData[0] = -1
		}
		while (true) {
			@Pc(32) var local32: Int
			@Pc(83) var local83: Int
			@Pc(129) var local129: Int
			@Pc(133) var local133: Int
			@Pc(510) var local510: Int
			while (packet!!.buffer!!.size > packet.currentPosition) {
				val opcode: Int = packet.readUByte()
				if (opcode == 0) {
					environment = Environment(packet)
				} else {
					@Pc(168) var local168: Int
					@Pc(276) var local276: Int
					@Pc(135) var local135: Int
					if (opcode == 1) {
						val lightCount: Int = packet.readUByte()
						if (lightCount > 0) {
							var lightIndex: Int = 0
							while (lightIndex < lightCount) {
								@Pc(519) val staticPointLight: StaticPointLight = StaticPointLight(toolkit, packet, 2)
								if (staticPointLight.type == 31) {
									@Pc(530) val lightType: LightType? = TypeLists.LIGHT!!.list(packet.readUShort())
									staticPointLight.setType(lightType!!.surrounding, lightType.duration, lightType.effect, lightType.ticker)
								}
								if (toolkit!!.getMaxLights() > 0) {
									@Pc(547) val pointLight: PointLight? = staticPointLight.pointLight
									val lightX: Int = (regionStartX shl 9) + pointLight!!.getX()
									val lightZ: Int = pointLight.getZ() + (regionStartZ shl 9)
									val lightTileX: Int = lightX shr 9
									@Pc(571) val lightTileZ: Int = lightZ shr 9
									if (lightTileX >= 0 && lightTileZ >= 0 && this.regionSizeX > lightTileX && this.regionSizeZ > lightTileZ) {
										pointLight.setPosition(lightX, lightZ, this.tileHeights!![staticPointLight.level]!![lightTileX]!![lightTileZ] - pointLight.getY())
										MapRegion.addPointLight(staticPointLight)
									}
								}
								lightIndex++
							}
						}
					} else if (opcode == 2) {
						if (environment == null) {
							environment = Environment()
						}
						environment.decodeHdr(packet)
					} else if (opcode != 128) {
						if (opcode != 129) {
							throw IllegalStateException("")
						}
						if (this.cameraHeights == null) {
							this.cameraHeights = arrayOfNulls<Array<ByteArray?>>(4)
						}
						var level: Int = 0
						while (level < 4) {
							@Pc(89) val lightingDecodeType: Byte = packet.readByte()
							if (lightingDecodeType.toInt() == 0 && this.cameraHeights!![level] != null) {
								var tileX: Int = regionStartX
								var endX: Int = regionStartX + 64
								var tileZ: Int = regionStartZ
								if (regionStartZ < 0) {
									tileZ = 0
								} else if (regionStartZ >= this.regionSizeZ) {
									tileZ = this.regionSizeZ
								}
								if (regionStartX < 0) {
									tileX = 0
								} else if (regionStartX >= this.regionSizeX) {
									tileX = this.regionSizeX
								}
								var endZ: Int = regionStartZ + 64
								if (endX < 0) {
									endX = 0
								} else if (endX >= this.regionSizeX) {
									endX = this.regionSizeX
								}
								if (endZ < 0) {
									endZ = 0
								} else if (endZ >= this.regionSizeZ) {
									endZ = this.regionSizeZ
								}
								while (tileX < endX) {
									while (endZ > tileZ) {
										this.cameraHeights!![level]!![tileX]!![tileZ] = 0
										tileZ++
									}
									tileX++
								}
							} else if (lightingDecodeType.toInt() == 1) {
								if (this.cameraHeights!![level] == null) {
									this.cameraHeights!![level] = Array<ByteArray?>(this.regionSizeX + 1) { ByteArray(this.regionSizeZ + 1) }
								}
								var blockX: Int = 0
								while (blockX < 64) {
									var blockZ: Int = 0
									while (blockZ < 64) {
										@Pc(266) val lightIntensity: Byte = packet.readByte()
										var tileX: Int = blockX + regionStartX
										while (tileX < blockX + regionStartX + 4) {
											var tileZ: Int = regionStartZ + blockZ
											while (tileZ < regionStartZ + blockZ + 4) {
												if (tileX >= 0 && tileX < this.regionSizeX && tileZ >= 0 && tileZ < this.regionSizeZ) {
													this.cameraHeights!![level]!![tileX]!![tileZ] = lightIntensity
												}
												tileZ++
											}
											tileX++
										}
										blockZ += 4
									}
									blockX += 4
								}
							} else if (lightingDecodeType.toInt() == 2) {
								if (this.cameraHeights!![level] == null) {
									this.cameraHeights!![level] = Array<ByteArray?>(this.regionSizeX + 1) { ByteArray(this.regionSizeZ + 1) }
								}
								if (level > 0) {
									var tileX: Int = regionStartX
									var endX: Int = regionStartX + 64
									var tileZ: Int = regionStartZ
									if (endX < 0) {
										endX = 0
									} else if (this.regionSizeX <= endX) {
										endX = this.regionSizeX
									}
									if (regionStartZ < 0) {
										tileZ = 0
									} else if (this.regionSizeZ <= regionStartZ) {
										tileZ = this.regionSizeZ
									}
									var endZ: Int = regionStartZ + 64
									if (regionStartX < 0) {
										tileX = 0
									} else if (regionStartX >= this.regionSizeX) {
										tileX = this.regionSizeX
									}
									if (endZ < 0) {
										endZ = 0
									} else if (endZ >= this.regionSizeZ) {
										endZ = this.regionSizeZ
									}
									while (tileX < endX) {
										while (tileZ < endZ) {
											this.cameraHeights!![level]!![tileX]!![tileZ] = this.cameraHeights!![level - 1]!![tileX]!![tileZ]
											tileZ++
										}
										tileX++
									}
								}
							}
							level++
						}
						lightingDecoded = true
					} else if (skyboxData == null) {
						packet.currentPosition += 10
					} else {
						skyboxData[0] = packet.readUShort()
						skyboxData[1] = packet.readShort()
						skyboxData[2] = packet.readShort()
						skyboxData[3] = packet.readShort()
						skyboxData[4] = packet.readUShort()
					}
				}
			}
			if (environment != null) {
				var chunkOffsetX: Int = 0
				while (chunkOffsetX < 8) {
					var chunkOffsetZ: Int = 0
					while (chunkOffsetZ < 8) {
						val chunkX: Int = chunkOffsetX + (regionStartX shr 3)
						val chunkZ: Int = (regionStartZ shr 3) + chunkOffsetZ
						if (chunkX >= 0 && chunkX < this.regionSizeX shr 3 && chunkZ >= 0 && this.regionSizeZ shr 3 > chunkZ) {
							EnvironmentManager.addChunkEnvironment(environment, chunkX, chunkZ)
						}
						chunkOffsetZ++
					}
					chunkOffsetX++
				}
			}
			if (!lightingDecoded && this.cameraHeights != null) {
				var level: Int = 0
				while (level < 4) {
					if (this.cameraHeights!![level] != null) {
						var cellOffsetX: Int = 0
						while (cellOffsetX < 16) {
							var cellOffsetZ: Int = 0
							while (cellOffsetZ < 16) {
								val cellX: Int = (regionStartX shr 2) + cellOffsetX
								val cellZ: Int = cellOffsetZ + (regionStartZ shr 2)
								if (cellX >= 0 && cellX < 26 && cellZ >= 0 && cellZ < 26) {
									this.cameraHeights!![level]!![cellX]!![cellZ] = 0
								}
								cellOffsetZ++
							}
							cellOffsetX++
						}
					}
					level++
				}
				return
			}
			return
		}
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(II[BIILclient!ha;III[Lclient!fe;I)V")
	fun decodeLocMapServer(@OriginalArg(0) mapX: Int, @OriginalArg(1) rotation: Int, @OriginalArg(2) src: ByteArray?, @OriginalArg(3) sourceLevel: Int, @OriginalArg(4) mapZ: Int, @OriginalArg(5) toolkit: Toolkit?, @OriginalArg(6) sceneZ: Int, @OriginalArg(7) sceneX: Int, @OriginalArg(9) collisionMaps: Array<CollisionMap?>?, @OriginalArg(10) level: Int) {
		@Pc(17) val packet: Packet = Packet(src)
		@Pc(19) var locId: Int = -1
		while (true) {
			@Pc(23) val locIdOffset: Int = packet.readSum()
			if (locIdOffset == 0) {
				return
			}
			locId += locIdOffset
			@Pc(34) var bitpackedPosition: Int = 0
			while (true) {
				@Pc(38) val positionOffset: Int = packet.readUSmart()
				if (positionOffset == 0) {
					break
				}
				bitpackedPosition += positionOffset - 1
				@Pc(50) val tileZ: Int = bitpackedPosition and 0x3F
				@Pc(56) val tileX: Int = (bitpackedPosition shr 6) and 0x3F
				@Pc(60) val tileLevel: Int = bitpackedPosition shr 12
				@Pc(64) val bitpackedDescription: Int = packet.readUByte()
				@Pc(68) val shape: Int = bitpackedDescription shr 2
				@Pc(72) val locRotation: Int = bitpackedDescription and 0x3
				if (sourceLevel == tileLevel && tileX >= mapX && mapX + 8 > tileX && tileZ >= mapZ && tileZ < mapZ + 8) {
					@Pc(108) val locType: LocType = TypeLists.LOC!!.list(locId)
					@Pc(125) val locX: Int = ZoneLocRotation.localOffsetX(tileZ and 0x7, tileX and 0x7, rotation, locType.sizeY, locType.sizeX, locRotation) + sceneX
					@Pc(142) val locZ: Int = sceneZ + ZoneLocRotation.localOffsetZ(tileX and 0x7, locType.sizeY, locRotation, locType.sizeX, tileZ and 0x7, rotation)
					if (locX > 0 && locZ > 0 && locX < this.regionSizeX - 1 && this.regionSizeZ - 1 > locZ) {
						@Pc(160) var collisionMap: CollisionMap? = null
						if (!this.underwater) {
							@Pc(165) var clipLevel: Int = level
							if ((SettingsBits.tileFunctionMasks!![1]!![locX]!![locZ].toInt() and TileFlag.BRIDGE) == 2) {
								clipLevel = level - 1
							}
							if (clipLevel >= 0) {
								collisionMap = collisionMaps!![clipLevel]
							}
						}
						this.addObject(locX, level, locId, toolkit, level, shape, locZ, -1, (locRotation + rotation) and 0x3, collisionMap)
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(Lclient!fe;IILclient!ha;IIB)V")
	fun removeGameObject(@OriginalArg(0) collisionMap: CollisionMap?, @OriginalArg(1) layer: Int, @OriginalArg(2) level: Int, @OriginalArg(3) toolkit: Toolkit?, @OriginalArg(4) x: Int, @OriginalArg(5) z: Int) {
		@Pc(11) val location: GameObject? = this.getGameObject(layer, z, x, level)
		if (location == null) {
			return
		}
		@Pc(25) val locType: LocType = TypeLists.LOC!!.list(location.getId())
		@Pc(29) val shape: Int = location.getShape()
		@Pc(33) val rotation: Int = location.getRotation()
		if (locType.hasSounds()) {
			AmbientSound.unlinkLocSound(locType, x, z, level)
		}
		if (location.hardShadow()) {
			location.removeShadow(toolkit)
		}
		if (layer == LocLayer.WALL) {
			Scene.removeWalls(level, x, z)
			if (locType.blocksMovement != 0) {
				collisionMap!!.unflagBoundary(locType.blocksProjectiles, rotation, x, shape, z, !locType.ignoreClipOnAltRoute)
			}
			if (locType.occlusionMode == LocOcclusionMode.ALL) {
				if (rotation != 0) {
					if (rotation != 1) {
						if (rotation == 2) {
							Culling.removeCullingCluster(level, 1, z, x + 1)
						} else if (rotation == 3) {
							Culling.removeCullingCluster(level, 2, z, x)
							return
						}
						return
					}
					Culling.removeCullingCluster(level, 2, z + 1, x)
					return
				}
				Culling.removeCullingCluster(level, 1, z, x)
				return
			}
		} else if (layer == LocLayer.WALLDECOR) {
			Scene.removeWallDecorations(level, x, z)
		} else if (layer == LocLayer.GROUND) {
			Scene.removeInteractiveOfType(level, x, z, GameObject::class.jClass)
			if (locType.blocksMovement != 0 && locType.sizeX + x < this.regionSizeX && z + locType.sizeX < this.regionSizeZ && locType.sizeY + x < this.regionSizeX && this.regionSizeZ > z + locType.sizeY) {
				collisionMap!!.unflagBlockedObject(rotation, !locType.ignoreClipOnAltRoute, locType.blocksProjectiles, x, z, locType.sizeY, locType.sizeX)
			}
			if (shape == LocShapes.WALL_DIAGONAL) {
				if ((rotation and 0x1) != 0) {
					Culling.removeCullingCluster(level, 16, z, x)
					return
				}
				Culling.removeCullingCluster(level, 8, z, x)
				return
			}
		} else {
			if (layer == LocLayer.GROUNDDECOR) {
				Scene.removeGroundDecoration(level, x, z)
				if (locType.blocksMovement == 1) {
					collisionMap!!.unflagFloorDecoration(x, z)
					return
				}
			}
			return
		}
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "([B[Lclient!fe;ILclient!ha;II)V")
	fun decodeLocMapClient(@OriginalArg(0) src: ByteArray?, @OriginalArg(1) collisionMaps: Array<CollisionMap?>?, @OriginalArg(2) offsetZ: Int, @OriginalArg(3) toolkit: Toolkit?, @OriginalArg(4) offsetX: Int) {
		@Pc(14) val packet: Packet = Packet(src)
		@Pc(16) var locId: Int = -1
		while (true) {
			@Pc(20) val locIdOffset: Int = packet.readSum()
			if (locIdOffset == 0) {
				return
			}
			locId += locIdOffset
			@Pc(28) var bitpackedPosition: Int = 0
			while (true) {
				@Pc(32) val positionOffset: Int = packet.readUSmart()
				if (positionOffset == 0) {
					break
				}
				bitpackedPosition += positionOffset - 1
				@Pc(47) val localZ: Int = bitpackedPosition and 0x3F
				@Pc(53) val localX: Int = (bitpackedPosition shr 6) and 0x3F
				@Pc(57) val level: Int = bitpackedPosition shr 12
				@Pc(61) val bitpackedDescription: Int = packet.readUByte()
				@Pc(65) val shape: Int = bitpackedDescription shr 2
				@Pc(69) val rotation: Int = bitpackedDescription and 0x3
				@Pc(73) val sceneX: Int = offsetX + localX
				@Pc(77) val sceneZ: Int = offsetZ + localZ
				if (sceneX > 0 && sceneZ > 0 && this.regionSizeX - 1 > sceneX && sceneZ < this.regionSizeZ - 1) {
					@Pc(102) var collisionMap: CollisionMap? = null
					if (!this.underwater) {
						@Pc(107) var clipLevel: Int = level
						if ((SettingsBits.tileFunctionMasks!![1]!![sceneX]!![sceneZ].toInt() and TileFlag.BRIDGE) == 2) {
							clipLevel = level - 1
						}
						if (clipLevel >= 0) {
							collisionMap = collisionMaps!![clipLevel]
						}
					}
					this.addObject(sceneX, level, locId, toolkit, level, shape, sceneZ, -1, rotation, collisionMap)
				}
			}
		}
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(Lclient!cea;ZIIIIIILclient!ha;[II)V")
	fun decodeEffectsServer(@OriginalArg(0) packet: Packet?, @OriginalArg(2) sourceChunkZ: Int, @OriginalArg(3) sourceChunkX: Int, @OriginalArg(4) sceneStartX: Int, @OriginalArg(5) level: Int, @OriginalArg(6) rotation: Int, @OriginalArg(7) sceneStartZ: Int, @OriginalArg(8) toolkit: Toolkit?, @OriginalArg(9) skyboxData: IntArray?, @OriginalArg(10) sourceLevel: Int) {
		if (this.underwater) {
			return
		}
		@Pc(13) var environment: Environment? = null
		if (skyboxData != null) {
			skyboxData[0] = -1
		}
		@Pc(25) val chunkStartTileX: Int = (sourceChunkX and 0x7) * 8
		@Pc(31) val chunkStartTileZ: Int = (sourceChunkZ and 0x7) * 8
		while (true) {
			@Pc(48) var local48: Int
			@Pc(104) var local104: Int
			@Pc(158) var local158: Int
			@Pc(176) var local176: Int
			@Pc(52) var local52: Int
			@Pc(98) var local98: Int
			label233@ do {
				while (true) {
					@Pc(36) var local36: Int
					@Pc(327) var local327: Int
					while (packet!!.buffer!!.size > packet.currentPosition) {
						val opcode: Int = packet.readUByte()
						if (opcode == 0) {
							environment = Environment(packet)
						} else {
							if (opcode == 1) {
								local48 = packet.readUByte()
								continue@label233
							}
							if (opcode == 2) {
								if (environment == null) {
									environment = Environment()
								}
								environment.decodeHdr(packet)
							} else if (opcode != 128) {
								if (opcode != 129) {
									throw IllegalStateException("")
								}
								if (this.cameraHeights == null) {
									this.cameraHeights = arrayOfNulls<Array<ByteArray?>>(4)
								}
								local48 = 0
								while (local48 < 4) {
									@Pc(312) val lightingDecodeType: Byte = packet.readByte()
									@Pc(331) var local331: Int
									if (lightingDecodeType.toInt() == 0 && this.cameraHeights!![level] != null) {
										if (sourceLevel >= local48) {
											var tileX: Int = sceneStartX
											var endX: Int = sceneStartX + 7
											var tileZ: Int = sceneStartZ
											if (endX < 0) {
												endX = 0
											} else if (this.regionSizeX <= endX) {
												endX = this.regionSizeX
											}
											if (sceneStartX < 0) {
												tileX = 0
											} else if (this.regionSizeX <= sceneStartX) {
												tileX = this.regionSizeX
											}
											var endZ: Int = sceneStartZ + 7
											if (sceneStartZ < 0) {
												tileZ = 0
											} else if (sceneStartZ >= this.regionSizeZ) {
												tileZ = this.regionSizeZ
											}
											if (endZ < 0) {
												endZ = 0
											} else if (endZ >= this.regionSizeZ) {
												endZ = this.regionSizeZ
											}
											while (endX > tileX) {
												while (tileZ < endZ) {
													this.cameraHeights!![level]!![tileX]!![tileZ] = 0
													tileZ++
												}
												tileX++
											}
										}
									} else if (lightingDecodeType.toInt() == 1) {
										if (this.cameraHeights!![level] == null) {
											this.cameraHeights!![level] = Array<ByteArray?>(this.regionSizeX + 1) { ByteArray(this.regionSizeZ + 1) }
										}
										var blockX: Int = 0
										while (blockX < 64) {
											var blockZ: Int = 0
											while (blockZ < 64) {
												@Pc(466) val lightIntensity: Byte = packet.readByte()
												if (local48 <= sourceLevel) {
													var tileX: Int = blockX
													while (tileX < blockX + 4) {
														var tileZ: Int = blockZ
														while (tileZ < blockZ + 4) {
															if (chunkStartTileX <= tileX && tileX < chunkStartTileX + 8 && chunkStartTileZ <= tileZ && chunkStartTileZ < chunkStartTileZ + 8) {
																val lightSceneX: Int = sceneStartX + Terrain.rotateTerrainBlockX(tileZ and 0x7, rotation, tileX and 0x7)
																@Pc(526) val lightSceneZ: Int = sceneStartZ + TerrainBlockRotationY.rotateTerrainBlockY(tileX and 0x7, tileZ and 0x7, rotation)
																if (lightSceneX >= 0 && lightSceneX < this.regionSizeX && lightSceneZ >= 0 && this.regionSizeZ > lightSceneZ) {
																	this.cameraHeights!![level]!![lightSceneX]!![lightSceneZ] = lightIntensity
																}
															}
															tileZ++
														}
														tileX++
													}
												}
												blockZ += 4
											}
											blockX += 4
										}
									}
									local48++
								}
							} else if (skyboxData == null) {
								packet.currentPosition += 10
							} else {
								skyboxData[0] = packet.readUShort()
								skyboxData[1] = packet.readShort()
								skyboxData[2] = packet.readShort()
								skyboxData[3] = packet.readShort()
								skyboxData[4] = packet.readUShort()
							}
						}
					}
					if (environment != null) {
						EnvironmentManager.addChunkEnvironment(environment, sceneStartX shr 3, sceneStartZ shr 3)
					}
					if (this.cameraHeights != null && this.cameraHeights!![level] != null) {
						val endX: Int = sceneStartX + 7
						local48 = sceneStartZ + 7
						var tileX: Int = sceneStartX
						while (tileX < endX) {
							var tileZ: Int = sceneStartZ
							while (tileZ < local48) {
								this.cameraHeights!![level]!![tileX]!![tileZ] = 0
								tileZ++
							}
							tileX++
						}
						return
					}
					return
				}
			} while (local48 <= 0)
			var lightIndex: Int = 0
			while (lightIndex < local48) {
				@Pc(61) val staticPointLight: StaticPointLight = StaticPointLight(toolkit, packet, 2)
				if (staticPointLight.type == 31) {
					@Pc(72) val lightType: LightType? = TypeLists.LIGHT!!.list(packet!!.readUShort())
					staticPointLight.setType(lightType!!.surrounding, lightType.duration, lightType.effect, lightType.ticker)
				}
				if (toolkit!!.getMaxLights() > 0) {
					@Pc(92) val pointLight: PointLight? = staticPointLight.pointLight
					var lightTileX: Int = pointLight!!.getX() shr 9
					var lightTileZ: Int = pointLight.getZ() shr 9
					if (staticPointLight.level == sourceLevel && lightTileX >= chunkStartTileX && lightTileX < chunkStartTileX + 8 && chunkStartTileZ <= lightTileZ && lightTileZ < chunkStartTileZ + 8) {
						val lightX: Int = (sceneStartX shl 9) + EnvironmentManager.rotatedX(pointLight.getZ() and 0xFFF, pointLight.getX() and 0xFFF, rotation)
						val lightZ: Int = (sceneStartZ shl 9) + EnvironmentManager.rotatedZ(rotation, pointLight.getZ() and 0xFFF, pointLight.getX() and 0xFFF)
						lightTileX = lightX shr 9
						lightTileZ = lightZ shr 9
						if (lightTileX >= 0 && lightTileZ >= 0 && lightTileX < this.regionSizeX && lightTileZ < this.regionSizeZ) {
							pointLight.setPosition(lightX, lightZ, this.tileHeights!![sourceLevel]!![lightTileX]!![lightTileZ] - pointLight.getY())
							MapRegion.addPointLight(staticPointLight)
						}
					}
				}
				lightIndex++
			}
		}
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(ZLclient!ha;I)V")
	fun calculateVisibility(@OriginalArg(0) underwater: Boolean, @OriginalArg(1) toolkit: Toolkit?) {
		Scene.applyShading()
		if (!underwater) {
			@Pc(16) var local16: Int
			@Pc(20) var z: Int
			if (this.levels > 1) {
				var tileX: Int = 0
				while (this.regionSizeX > tileX) {
					z = 0
					while (this.regionSizeZ > z) {
						if ((SettingsBits.tileFunctionMasks!![1]!![tileX]!![z].toInt() and TileFlag.BRIDGE) == 2) {
							Scene.linkBelow(tileX, z)
						}
						z++
					}
					tileX++
				}
			}
			var level: Int = 0
			while (level < this.levels) {
				z = 0
				while (this.regionSizeZ >= z) {
					@Pc(56) var i: Int = 0
					while (i <= this.regionSizeX) {
						if ((this.occluderFlags!![level]!![i]!![z].toInt() and TileFlag.REMOVE_ROOF) != 0) {
							@Pc(74) var minX: Int = i
							@Pc(76) var maxX: Int = i
							@Pc(78) var minZ: Int = z
							@Pc(80) var maxZ: Int = z
							while (minZ > 0 && (this.occluderFlags!![level]!![i]!![minZ - 1].toInt() and TileFlag.REMOVE_ROOF) != 0 && z - minZ < 10) {
								minZ--
							}
							while (this.regionSizeZ > maxZ && (this.occluderFlags!![level]!![i]!![maxZ + 1].toInt() and TileFlag.REMOVE_ROOF) != 0 && maxZ - minZ < 10) {
								maxZ++
							}
							@Pc(138) var local138: Int
							label111@ while (minX > 0 && i - minX < 10) {
								var edgeZ: Int = minZ
								while (edgeZ <= maxZ) {
									if ((this.occluderFlags!![level]!![minX - 1]!![edgeZ].toInt() and TileFlag.REMOVE_ROOF) == 0) {
										break@label111
									}
									edgeZ++
								}
								minX--
							}
							label98@ while (maxX < this.regionSizeX && maxX - minX < 10) {
								var edgeZ: Int = minZ
								while (edgeZ <= maxZ) {
									if ((this.occluderFlags!![level]!![maxX + 1]!![edgeZ].toInt() and TileFlag.REMOVE_ROOF) == 0) {
										break@label98
									}
									edgeZ++
								}
								maxX++
							}
							if ((maxZ + 1 - minZ) * (maxX + 1 - minX) >= 4) {
								val clusterHeight: Int = this.tileHeights!![level]!![minX]!![minZ]
								Culling.addGroundCullingCluster((maxX shl 9) + 512, clusterHeight, minZ shl 9, clusterHeight, (maxZ shl 9) + 512, level, minX shl 9)
								@Pc(266) var j: Int = minX
								while (j <= maxX) {
									@Pc(270) var k: Int = minZ
									while (k <= maxZ) {
										this.occluderFlags!![level]!![j]!![k] = (this.occluderFlags!![level]!![j]!![k].toInt() and 0xFFFFFFFB.toInt()).toByte()
										k++
									}
									j++
								}
							}
						}
						i++
					}
					z++
				}
				level++
			}
			Culling.rebuildCullingClusters()
		}
		this.occluderFlags = null
	}

	companion object {

		@OriginalMember(owner = "client!jk", name = "a", descriptor = "(Lclient!ro;)V")
		@JvmStatic
		fun addPointLight(@OriginalArg(0) staticPointLight: StaticPointLight?) {
			if (Scene.lightSlotCount >= 65535) {
				return
			}
			@Pc(6) val pointLight: PointLight? = staticPointLight!!.pointLight
			Scene.aClass300Array1!![Scene.lightSlotCount] = staticPointLight
			Scene.pointLightApplied!![Scene.lightSlotCount] = false
			Scene.lightSlotCount++
			@Pc(21) var level: Int = staticPointLight.level
			if (staticPointLight.growsDownwards) {
				level = 0
			}
			@Pc(29) var maxLevel: Int = staticPointLight.level
			if (staticPointLight.growsUpwards) {
				maxLevel = Scene.length - 1
			}
			@Pc(38) var i: Int = level
			while (i <= maxLevel) {
				@Pc(41) var rangeIndex: Int = 0
				@Pc(53) var minZ: Int = (pointLight!!.getZ() + Scene.halfTileOffset - pointLight.getRadius()) shr Scene.tileUnits
				if (minZ < 0) {
					rangeIndex = -minZ
					minZ = 0
				}
				@Pc(73) var maxZ: Int = (pointLight.getZ() + pointLight.getRadius() - Scene.halfTileOffset) shr Scene.tileUnits
				if (maxZ >= Camera.sceneLength) {
					maxZ = Camera.sceneLength - 1
				}
				@Pc(82) var j: Int = minZ
				while (j <= maxZ) {
					@Pc(89) val range: Short = staticPointLight.ranges!![rangeIndex++]
					@Pc(105) var minX: Int = ((pointLight.getX() + Scene.halfTileOffset - pointLight.getRadius()) shr Scene.tileUnits) + (range.toInt() ushr 8)
					@Pc(113) var maxX: Int = minX + (range.toInt() and 0xFF) - 1
					if (minX < 0) {
						minX = 0
					}
					if (maxX >= Scene.sceneWidth) {
						maxX = Scene.sceneWidth - 1
					}
					@Pc(126) var k: Int = minX
					while (k <= maxX) {
						@Pc(135) val lightSlots: Long = Scene.tileLightUids!![i]!![k]!![j]
						if ((lightSlots and 0xFFFFL) == 0L) {
							Scene.tileLightUids!![i]!![k]!![j] = lightSlots or Scene.lightSlotCount.toLong()
						} else if ((lightSlots and 0xFFFF0000L) == 0L) {
							Scene.tileLightUids!![i]!![k]!![j] = lightSlots or (Scene.lightSlotCount.toLong() shl 16)
						} else if ((lightSlots and 0xFFFF00000000L) == 0L) {
							Scene.tileLightUids!![i]!![k]!![j] = lightSlots or (Scene.lightSlotCount.toLong() shl 32)
						} else if ((lightSlots and -0x1000000000000L) == 0L) {
							Scene.tileLightUids!![i]!![k]!![j] = lightSlots or (Scene.lightSlotCount.toLong() shl 48)
						}
						k++
					}
					j++
				}
				i++
			}
		}
		@OriginalMember(owner = "client!hga", name = "P", descriptor = "I")
		@JvmField
		var sceneSizeY: Int = 104
		@OriginalMember(owner = "client!bk", name = "a", descriptor = "(I[[[Lclient!es;IIIZ)Z")
		@JvmStatic
		fun calculateVisibilityAdjustments(@OriginalArg(0) baseZ: Int, @OriginalArg(1) tiles: Array<Array<Array<Tile?>?>?>?, @OriginalArg(2) baseX: Int, @OriginalArg(4) viewIndex: Int, @OriginalArg(5) hideRoofs: Boolean): Boolean {
			@Pc(14) val setting: Byte = if (hideRoofs) 1.toByte() else (SceneDrawer.timesSceneRendered and 0xFF).toByte()
			if (MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![baseX]!![baseZ] == setting) {
				return false
			} else if ((SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![baseX]!![baseZ].toInt() and TileFlag.REMOVE_ROOF) == 0) {
				return false
			} else {
				@Pc(43) val seedIndex: Byte = 0
				@Pc(45) var readIndex: Int = 0
				MapRegion.roofQueueX[0] = baseX
				@Pc(52) var writeIndex: Int = seedIndex + 1
				MapRegion.roofQueueZ[0] = baseZ
				MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![baseX]!![baseZ] = setting
				while (readIndex != writeIndex) {
					@Pc(70) val tileX: Int = MapRegion.roofQueueX[readIndex] and 0xFFFF
					@Pc(78) val packedPlacementA: Int = (MapRegion.roofQueueX[readIndex] shr 16) and 0xFF
					@Pc(86) val packedPlacementB: Int = (MapRegion.roofQueueX[readIndex] shr 24) and 0xFF
					@Pc(92) var tileZ: Int = MapRegion.roofQueueZ[readIndex] and 0xFFFF
					@Pc(100) val packedPlacementC: Int = (MapRegion.roofQueueZ[readIndex] shr 16) and 0xFF
					readIndex = (readIndex + 1) and 0xFFF
					@Pc(108) var outsideRoofArea: Boolean = false
					if ((SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![tileX]!![tileZ].toInt() and TileFlag.REMOVE_ROOF) == 0) {
						outsideRoofArea = true
					}
					@Pc(122) var visibilityUpdate: Boolean = false
					@Pc(128) var local128: Int
					@Pc(170) var local170: Int
					@Pc(220) var local220: Int
					if (tiles != null) {
						label237@ for (level in PlayerUpdate.currentLevel + 1..3) {
							if (tiles[level] != null && (SettingsBits.tileFunctionMasks!![level]!![tileX]!![tileZ].toInt() and TileFlag.ZERO_LEVEL) == 0) {
								@Pc(323) var positionEntity: AbstractInteractiveEntity?
								@Pc(333) var local333: Int
								@Pc(312) var tile: Tile?
								@Pc(318) var positionEntityNode: InteractiveEntityContainer?
								if (outsideRoofArea && tiles[level]!![tileX]!![tileZ] != null) {
									if (tiles[level]!![tileX]!![tileZ]!!.primaryWall != null) {
										val sideMaskA: Int = LocShapes.getRoofEdgeSideMask(packedPlacementA)
										if (sideMaskA == tiles[level]!![tileX]!![tileZ]!!.primaryWall!!.sideMask.toInt() || tiles[level]!![tileX]!![tileZ]!!.secondaryWall != null && tiles[level]!![tileX]!![tileZ]!!.secondaryWall!!.sideMask.toInt() == sideMaskA) {
											continue
										}
										if (packedPlacementB != 0) {
											val sideMaskB: Int = LocShapes.getRoofEdgeSideMask(packedPlacementB)
											if (sideMaskB == tiles[level]!![tileX]!![tileZ]!!.primaryWall!!.sideMask.toInt() || tiles[level]!![tileX]!![tileZ]!!.secondaryWall != null && sideMaskB == tiles[level]!![tileX]!![tileZ]!!.secondaryWall!!.sideMask.toInt()) {
												continue
											}
										}
										if (packedPlacementC != 0) {
											val sideMaskC: Int = LocShapes.getRoofEdgeSideMask(packedPlacementC)
											if (tiles[level]!![tileX]!![tileZ]!!.primaryWall!!.sideMask.toInt() == sideMaskC || tiles[level]!![tileX]!![tileZ]!!.secondaryWall != null && tiles[level]!![tileX]!![tileZ]!!.secondaryWall!!.sideMask.toInt() == sideMaskC) {
												continue
											}
										}
									}
									tile = tiles[level]!![tileX]!![tileZ]
									if (tile!!.interactives != null) {
										positionEntityNode = tile.interactives
										while (positionEntityNode != null) {
											positionEntity = positionEntityNode.interactiveObject
											if (positionEntity is GameObject) {
												@Pc(329) val location: GameObject = positionEntity as GameObject
												var shape: Int = location.getShape()
												@Pc(337) val rotation: Int = location.getRotation()
												if (shape == LocShapes.ROOFEDGE_SQUARECORNER) {
													shape = LocShapes.ROOFEDGE_DIAGONALCORNER
												}
												@Pc(348) val packedPlacement: Int = (rotation shl 6) or shape
												if (packedPlacement == packedPlacementA || packedPlacementB != 0 && packedPlacementB == packedPlacement || packedPlacementC != 0 && packedPlacementC == packedPlacement) {
													continue@label237
												}
											}
											positionEntityNode = positionEntityNode.child
										}
									}
								}
								tile = tiles[level]!![tileX]!![tileZ]
								if (tile != null && tile.interactives != null) {
									positionEntityNode = tile.interactives
									while (positionEntityNode != null) {
										positionEntity = positionEntityNode.interactiveObject
										if (positionEntity!!.startX != positionEntity.endX || positionEntity.endZ != positionEntity.startZ) {
											@Pc(406) var i: Int = positionEntity.startX.toInt()
											while (i <= positionEntity.endX) {
												var stampZ: Int = positionEntity.startZ.toInt()
												while (stampZ <= positionEntity.endZ) {
													MapRegion.roofStamps!![level]!![i]!![stampZ] = setting
													stampZ++
												}
												i++
											}
										}
										positionEntityNode = positionEntityNode.child
									}
								}
								MapRegion.roofStamps!![level]!![tileX]!![tileZ] = setting
								visibilityUpdate = true
							}
						}
					}
					if (visibilityUpdate) {
						val groundHeight: Int = Scene.aClass65Array1!![PlayerUpdate.currentLevel + 1]!!.getHeight(tileZ, tileX)
						if (groundHeight > MapRegion.visibleBelowDrawheight[viewIndex]) {
							MapRegion.visibleBelowDrawheight[viewIndex] = groundHeight
						}
						val sceneX: Int = tileX shl 9
						if (MapRegion.roofMinX[viewIndex] > sceneX) {
							MapRegion.roofMinX[viewIndex] = sceneX
						} else if (sceneX > MapRegion.roofMaxX[viewIndex]) {
							MapRegion.roofMaxX[viewIndex] = sceneX
						}
						val sceneZ: Int = tileZ shl 9
						if (MapRegion.roofMinZ[viewIndex] > sceneZ) {
							MapRegion.roofMinZ[viewIndex] = sceneZ
						} else if (sceneZ > MapRegion.roofMaxZ[viewIndex]) {
							MapRegion.roofMaxZ[viewIndex] = sceneZ
						}
					}
					if (!outsideRoofArea) {
						if (tileX >= 1 && MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX - 1]!![tileZ] != setting) {
							MapRegion.roofQueueX[writeIndex] = (tileX - 1) or 0x120000 or 0xD3000000.toInt()
							MapRegion.roofQueueZ[writeIndex] = tileZ or 0x130000
							writeIndex = (writeIndex + 1) and 0xFFF
							MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX - 1]!![tileZ] = setting
						}
						@Pc(592) val sceneSizeYComplement: Int = MapRegion.sceneSizeY.inv()
						tileZ++
						if (sceneSizeYComplement < tileZ.inv()) {
							if (tileX - 1 >= 0 && MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX - 1]!![tileZ] != setting && (SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![tileX]!![tileZ].toInt() and TileFlag.REMOVE_ROOF) == 0 && (SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![tileX - 1]!![tileZ - 1].toInt() and TileFlag.REMOVE_ROOF) == 0) {
								MapRegion.roofQueueX[writeIndex] = (tileX - 1) or 0x52000000 or 0x120000
								MapRegion.roofQueueZ[writeIndex] = tileZ or 0x130000
								MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX - 1]!![tileZ] = setting
								writeIndex = (writeIndex + 1) and 0xFFF
							}
							if (setting != MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX]!![tileZ]) {
								MapRegion.roofQueueX[writeIndex] = tileX or 0x13000000 or 0x520000
								MapRegion.roofQueueZ[writeIndex] = tileZ or 0x530000
								MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX]!![tileZ] = setting
								writeIndex = (writeIndex + 1) and 0xFFF
							}
							if (sceneSizeX > tileX + 1 && setting != MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX + 1]!![tileZ] && (SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![tileX]!![tileZ].toInt() and TileFlag.REMOVE_ROOF) == 0 && (SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![tileX + 1]!![tileZ - 1].toInt() and TileFlag.REMOVE_ROOF) == 0) {
								MapRegion.roofQueueX[writeIndex] = (tileX + 1) or 0x92000000.toInt() or 0x520000
								MapRegion.roofQueueZ[writeIndex] = tileZ or 0x530000
								writeIndex = (writeIndex + 1) and 0xFFF
								MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX + 1]!![tileZ] = setting
							}
						}
						tileZ--
						if (sceneSizeX > tileX + 1 && MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX + 1]!![tileZ] != setting) {
							MapRegion.roofQueueX[writeIndex] = 0x53000000 or 0x920000 or (tileX + 1)
							MapRegion.roofQueueZ[writeIndex] = tileZ or 0x930000
							writeIndex = (writeIndex + 1) and 0xFFF
							MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX + 1]!![tileZ] = setting
						}
						tileZ--
						if (tileZ >= 0) {
							if (tileX - 1 >= 0 && MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX - 1]!![tileZ] != setting && (SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![tileX]!![tileZ].toInt() and TileFlag.REMOVE_ROOF) == 0 && (SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![tileX - 1]!![tileZ + 1].toInt() and TileFlag.REMOVE_ROOF) == 0) {
								MapRegion.roofQueueX[writeIndex] = 0x12000000 or 0xD20000 or (tileX - 1)
								MapRegion.roofQueueZ[writeIndex] = tileZ or 0xD30000
								MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX - 1]!![tileZ] = setting
								writeIndex = (writeIndex + 1) and 0xFFF
							}
							if (setting != MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX]!![tileZ]) {
								MapRegion.roofQueueX[writeIndex] = tileX or 0xD20000 or 0x93000000.toInt()
								MapRegion.roofQueueZ[writeIndex] = tileZ or 0xD30000
								MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX]!![tileZ] = setting
								writeIndex = (writeIndex + 1) and 0xFFF
							}
							if (tileX + 1 < sceneSizeX && MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX + 1]!![tileZ] != setting && (SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![tileX]!![tileZ].toInt() and TileFlag.REMOVE_ROOF) == 0 && (SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![tileX + 1]!![tileZ + 1].toInt() and TileFlag.REMOVE_ROOF) == 0) {
								MapRegion.roofQueueX[writeIndex] = (tileX + 1) or 0xD2000000.toInt() or 0x920000
								MapRegion.roofQueueZ[writeIndex] = tileZ or 0x930000
								MapRegion.roofStamps!![PlayerUpdate.currentLevel]!![tileX + 1]!![tileZ] = setting
								writeIndex = (writeIndex + 1) and 0xFFF
							}
						}
					}
				}
				if (MapRegion.visibleBelowDrawheight[viewIndex] != -1000000) {
					MapRegion.visibleBelowDrawheight[viewIndex] += 40
					MapRegion.roofMinX[viewIndex] -= 512
					MapRegion.roofMaxX[viewIndex] += 512
					MapRegion.roofMaxZ[viewIndex] += 512
					MapRegion.roofMinZ[viewIndex] -= 512
				}
				return true
			}
		}

		@OriginalMember(owner = "client!bk", name = "t", descriptor = "I")
		@JvmField
		var sceneSizeX: Int = 104
		@OriginalMember(owner = "client!kia", name = "o", descriptor = "[[I")
		@JvmField
		var xteas: Array<IntArray?>? = null

		@OriginalMember(owner = "client!bc", name = "e", descriptor = "J")
		@JvmField
		var debugRegionLoadTimeStart: Long = 0L

		@OriginalMember(owner = "client!pk", name = "y", descriptor = "[I")
		@JvmField
		var regionIds: IntArray? = null
		@OriginalMember(owner = "client!bu", name = "u", descriptor = "[Lclient!fe;")
		@JvmField
		val aClass104Array3: Array<CollisionMap?> = arrayOfNulls<CollisionMap>(4)

		@OriginalMember(owner = "client!km", name = "L", descriptor = "[I")
		@JvmField
		var surfaceLocArchives: IntArray? = null

		@OriginalMember(owner = "client!cd", name = "a", descriptor = "(B)V")
		@JvmStatic
		fun calculateViewRange() {
			MapRegion.viewRangeEnd = (MapRegion.sceneSizeX.toDouble() * 34.46).toInt()
			MapRegion.viewRangeStart = 200
			MapRegion.viewRangeEnd = MapRegion.viewRangeEnd shl 0x2
			if (ClientState.activeToolkit!!.extendBuildArea()) {
				MapRegion.viewRangeEnd += 512
			}
			MapRegion.applyViewDepthRange(false)
			if (ClientFeatures.RENDER_DISTANCE != 1.0) {
				// Not part of the original client: the fog moves out with the draw distance, a little less far so the
				// edge of the scene stays hidden.
				MapRegion.viewRangeEnd = (MapRegion.viewRangeEnd.toDouble() * (1.0 + (ClientFeatures.RENDER_DISTANCE - 1.0) * 0.8)).toInt()
			}
		}

		@OriginalMember(owner = "client!ag", name = "e", descriptor = "[I")
		@JvmField
		var surfaceMapArchives: IntArray? = null
		@OriginalMember(owner = "client!ik", name = "a", descriptor = "(ZLclient!vaa;[[B)V")
		@JvmStatic
		fun decodeMapTilesServer(@OriginalArg(1) mapRegion: MapRegion?, @OriginalArg(2) regionData: Array<ByteArray?>?) {
			@Pc(12) val skyboxParams: IntArray = intArrayOf(-1, 0, 0, 0, 0)
			@Pc(20) var local20: Int
			@Pc(24) var local24: Int
			@Pc(34) var local34: Int
			@Pc(44) var local44: Int
			@Pc(14) var i: Int = 0
			while (i < mapRegion!!.levels) {
				SoundEffectPlayer.updatePlayers()
				var chunkX: Int = 0
				while (chunkX < MapRegion.sceneSizeX shr 3) {
					var chunkZ: Int = 0
					while (chunkZ < MapRegion.sceneSizeY shr 3) {
						val packedChunk: Int = MapRegion.chunkBitpackedPosition!![i]!![chunkX]!![chunkZ]
						if (packedChunk != -1) {
							val sourceLevel: Int = (packedChunk shr 24) and 0x3
							if (!mapRegion.underwater || sourceLevel == 0) {
								@Pc(55) val rotation: Int = (packedChunk shr 1) and 0x3
								@Pc(61) val sourceChunkX: Int = (packedChunk shr 14) and 0x3FF
								@Pc(67) val sourceChunkZ: Int = (packedChunk shr 3) and 0x7FF
								@Pc(78) val mapSquareId: Int = ((sourceChunkX / 8) shl 8) + sourceChunkZ / 8
								@Pc(80) var j: Int = 0
								while (j < MapRegion.regionIds!!.size) {
									if (MapRegion.regionIds!![j] == mapSquareId && regionData!![j] != null) {
										@Pc(102) val packet: Packet = Packet(regionData[j])
										mapRegion.decodeTilesServer(chunkX * 8, i, packet, sourceChunkZ, chunkZ * 8, sourceChunkX, MapRegion.aClass104Array3, rotation, sourceLevel)
										mapRegion.decodeEffectsServer(packet, sourceChunkZ, sourceChunkX, chunkX * 8, i, rotation, chunkZ * 8, ClientState.activeToolkit, if (skyboxParams[0] == -1) skyboxParams else null, sourceLevel)
										break
									}
									j++
								}
							}
						}
						chunkZ++
					}
					chunkX++
				}
				i++
			}
			var level: Int = 0
			while (level < mapRegion.levels) {
				SoundEffectPlayer.updatePlayers()
				var chunkX: Int = 0
				while (chunkX < MapRegion.sceneSizeX shr 3) {
					var chunkZ: Int = 0
					while (chunkZ < MapRegion.sceneSizeY shr 3) {
						val packedChunk: Int = MapRegion.chunkBitpackedPosition!![level]!![chunkX]!![chunkZ]
						if (packedChunk == -1) {
							mapRegion.setTileHeights(chunkX * 8, 8, chunkZ * 8, 8, level)
						}
						chunkZ++
					}
					chunkX++
				}
				level++
			}
			if (skyboxParams[0] != -1) {
				Skybox.configNpcJs5 = TypeLists.SKYBOX!!.generateSkybox(skyboxParams[2], skyboxParams[3], skyboxParams[1], TypeLists.SUN, skyboxParams[0])
				Skybox.skyboxAngle = skyboxParams[4]
			}
		}

		@OriginalMember(owner = "client!nga", name = "a", descriptor = "([[BILclient!vaa;)V")
		@JvmStatic
		fun decodeLocMapsClient(@OriginalArg(0) locData: Array<ByteArray?>?, @OriginalArg(2) mapRegion: MapRegion?) {
			@Pc(8) val length: Int = MapRegion.surfaceMapBuffer!!.size
			@Pc(14) var i: Int = 0
			while (i < length) {
				@Pc(20) val data: ByteArray? = locData!![i]
				if (data != null) {
					@Pc(33) val baseX: Int = (MapRegion.regionIds!![i] shr 8) * 64 - MapRegion.sceneBaseX
					@Pc(43) val baseZ: Int = (MapRegion.regionIds!![i] and 0xFF) * 64 - MapRegion.sceneBaseY
					SoundEffectPlayer.updatePlayers()
					mapRegion!!.decodeLocMapClient(data, MapRegion.aClass104Array3, baseZ, ClientState.activeToolkit, baseX)
				}
				i++
			}
		}

		@OriginalMember(owner = "client!jw", name = "g", descriptor = "[[[I")
		@JvmField
		var chunkBitpackedPosition: Array<Array<IntArray?>?>? = null

		@OriginalMember(owner = "client!wc", name = "i", descriptor = "[I")
		@JvmField
		var underwaterMapArchives: IntArray? = null
		@OriginalMember(owner = "client!cu", name = "u", descriptor = "I")
		@JvmField
		var sceneBaseX: Int = 0

		@OriginalMember(owner = "client!afa", name = "n", descriptor = "[I")
		@JvmField
		var npcSpawnArchives: IntArray? = null

		@OriginalMember(owner = "client!r", name = "E", descriptor = "[I")
		@JvmField
		var underwaterLocArchives: IntArray? = null

		@OriginalMember(owner = "client!sc", name = "h", descriptor = "[I")
		@JvmField
		val DIAGONAL_WALL_DECOR_OFFSET_X: IntArray = intArrayOf(1, -1, -1, 1)

		@OriginalMember(owner = "client!pia", name = "f", descriptor = "Lclient!vaa;")
		@JvmField
		var aClass322_Sub1_2: MapRegion? = null

		@OriginalMember(owner = "client!kk", name = "i", descriptor = "[I")
		@JvmField
		val MAP_SIZES: IntArray = intArrayOf(104, 120, 136, 168)

		@OriginalMember(owner = "client!lk", name = "w", descriptor = "Z")
		@JvmField
		var lowDetailShadows: Boolean = false

		@OriginalMember(owner = "client!nn", name = "o", descriptor = "[[B")
		@JvmField
		var underwaterMapBuffer: Array<ByteArray?>? = null

		@OriginalMember(owner = "client!sl", name = "e", descriptor = "(II)V")
		@JvmStatic
		fun setMapSize(@OriginalArg(1) mapSize: Int) {
			if (mapSize == MapRegion.currentMapSize) {
				return
			}
			MapRegion.sceneSizeY = MapRegion.MAP_SIZES[mapSize]
			MapRegion.sceneSizeX = MapRegion.sceneSizeY
			MapRegion.calculateViewRange()
			MapRegion.chunkBitpackedPosition = Array<Array<IntArray?>?>(4) { Array<IntArray?>(MapRegion.sceneSizeX shr 3) { IntArray(MapRegion.sceneSizeY shr 3) } }
			EntitySceneBuilder.tileDrawPrioritySharedBy = Array<IntArray?>(MapRegion.sceneSizeX) { IntArray(MapRegion.sceneSizeY) }
			EntitySceneBuilder.tileDrawPriority = Array<IntArray?>(MapRegion.sceneSizeX) { IntArray(MapRegion.sceneSizeY) }
			@Pc(41) var i: Int = 0
			while (i < 4) {
				MapRegion.aClass104Array3[i] = CollisionMap.create(MapRegion.sceneSizeY, MapRegion.sceneSizeX)
				i++
			}
			MapRegion.roofStamps = Array<Array<ByteArray?>?>(4) { Array<ByteArray?>(MapRegion.sceneSizeX) { ByteArray(MapRegion.sceneSizeY) } }
			SettingsBits.allocateTileFunctionMasks(MapRegion.sceneSizeY, MapRegion.sceneSizeX)
			EnvironmentManager.initialize(MapRegion.sceneSizeX shr 3, ClientState.activeToolkit, MapRegion.sceneSizeY shr 3)
			MapRegion.currentMapSize = mapSize
		}

		@OriginalMember(owner = "client!uba", name = "a", descriptor = "(IIII[BB)Z")
		@JvmStatic
		fun areMapLocTypesLoaded(@OriginalArg(0) sizeX: Int, @OriginalArg(1) baseX: Int, @OriginalArg(2) sizeY: Int, @OriginalArg(3) baseY: Int, @OriginalArg(4) src: ByteArray?): Boolean {
			@Pc(12) var allLoaded: Boolean = true
			@Pc(17) val packet: Packet = Packet(src)
			@Pc(19) var locId: Int = -1
			label54@ while (true) {
				@Pc(23) val idDelta: Int = packet.readSum()
				if (idDelta == 0) {
					return allLoaded
				}
				locId += idDelta
				@Pc(31) var position: Int = 0
				@Pc(33) var found: Boolean = false
				while (true) {
					@Pc(39) var positionDelta: Int
					while (!found) {
						positionDelta = packet.readUSmart()
						if (positionDelta == 0) {
							continue@label54
						}
						position += positionDelta - 1
						@Pc(65) val localY: Int = position and 0x3F
						@Pc(71) val localX: Int = (position shr 6) and 0x3F
						@Pc(77) val shape: Int = packet.readUByte() shr 2
						@Pc(82) val x: Int = localX + baseX
						@Pc(86) val y: Int = baseY + localY
						if (x > 0 && y > 0 && x < sizeX - 1 && y < sizeY - 1) {
							@Pc(109) val locType: LocType = TypeLists.LOC!!.list(locId)
							if (shape != LocShapes.GROUNDDECOR || GamePreferences.uid!!.groundDecor!!.getValue() != 0 || locType.hasActions != LocInteractivity.NONINTERACTIVE || locType.blocksMovement == 1 || locType.forceDisplayDecoration) {
								found = true
								if (!locType.isLoaded()) {
									UnloadedLocCount.unloadedLocCount++
									allLoaded = false
								}
							}
						}
					}
					positionDelta = packet.readUSmart()
					if (positionDelta == 0) {
						break
					}
					packet.readUByte()
				}
			}
		}

		@OriginalMember(owner = "client!uw", name = "a", descriptor = "[I")
		@JvmField
		val DIAGONAL_WALL_DECOR_OFFSET_Z: IntArray = intArrayOf(-1, -1, 1, 1)
		@OriginalMember(owner = "client!kq", name = "S", descriptor = "Z")
		const val OCCLUDE_UNSPECIFIED_LOCS: Boolean = false

		@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(I[[BLclient!vaa;)V")
		@JvmStatic
		fun decodeLocMapsServer(@OriginalArg(1) locMapData: Array<ByteArray?>?, @OriginalArg(2) mapRegion: MapRegion?) {
			@Pc(14) var i: Int = 0
			while (i < mapRegion!!.levels) {
				SoundEffectPlayer.updatePlayers()
				@Pc(20) var j: Int = 0
				while (j < MapRegion.sceneSizeX shr 3) {
					@Pc(24) var k: Int = 0
					while (k < MapRegion.sceneSizeY shr 3) {
						@Pc(34) val packedChunk: Int = MapRegion.chunkBitpackedPosition!![i]!![j]!![k]
						if (packedChunk != -1) {
							@Pc(44) val chunkLevel: Int = (packedChunk shr 24) and 0x3
							if (!mapRegion.underwater || chunkLevel == 0) {
								@Pc(55) val chunkRotation: Int = (packedChunk shr 1) and 0x3
								@Pc(61) val chunkX: Int = (packedChunk shr 14) and 0x3FF
								@Pc(67) val chunkZ: Int = (packedChunk shr 3) and 0x7FF
								@Pc(77) val regionId: Int = chunkZ / 8 + ((chunkX / 8) shl 8)
								@Pc(79) var m: Int = 0
								while (m < MapRegion.regionIds!!.size) {
									if (MapRegion.regionIds!![m] == regionId && locMapData!![m] != null) {
										mapRegion.decodeLocMapServer((chunkX and 0x7) * 8, chunkRotation, locMapData[m], chunkLevel, (chunkZ and 0x7) * 8, ClientState.activeToolkit, k * 8, j * 8, MapRegion.aClass104Array3, i)
										break
									}
									m++
								}
							}
						}
						k++
					}
					j++
				}
				i++
			}
		}

		@OriginalMember(owner = "client!ea", name = "q", descriptor = "Lclient!vaa;")
		@JvmField
		var aClass322_Sub1_1: MapRegion? = null

		@OriginalMember(owner = "client!bc", name = "f", descriptor = "I")
		@JvmField
		var sceneBaseY: Int = 0

		@OriginalMember(owner = "client!oga", name = "c", descriptor = "[I")
		@JvmField
		val WALL_DECOR_OFFSET_X: IntArray = intArrayOf(1, 0, -1, 0)

		@OriginalMember(owner = "client!jm", name = "D", descriptor = "[[B")
		@JvmField
		var underwaterLocBuffer: Array<ByteArray?>? = null

		@OriginalMember(owner = "client!eg", name = "d", descriptor = "[[B")
		@JvmField
		var surfaceMapBuffer: Array<ByteArray?>? = null

		@OriginalMember(owner = "client!bg", name = "a", descriptor = "(BLclient!vaa;[[B)V")
		@JvmStatic
		fun decodeMapTilesClient(@OriginalArg(1) mapRegion: MapRegion?, @OriginalArg(2) mapData: Array<ByteArray?>?) {
			@Pc(12) val skyboxData: IntArray = intArrayOf(-1, 0, 0, 0, 0)
			@Pc(23) val length: Int = mapData!!.size
			@Pc(44) var local44: Int
			@Pc(25) var i: Int = 0
			while (i < length) {
				@Pc(31) val data: ByteArray? = mapData[i]
				if (data != null) {
					@Pc(38) val packet: Packet = Packet(data)
					val mapSquareX: Int = MapRegion.regionIds!![i] shr 8
					@Pc(50) val mapSquareZ: Int = MapRegion.regionIds!![i] and 0xFF
					@Pc(57) val offsetX: Int = mapSquareX * 64 - MapRegion.sceneBaseX
					@Pc(64) val offsetZ: Int = mapSquareZ * 64 - MapRegion.sceneBaseY
					SoundEffectPlayer.updatePlayers()
					mapRegion!!.decodeTilesClient(packet, offsetX, MapRegion.aClass104Array3, MapRegion.sceneBaseY, MapRegion.sceneBaseX, offsetZ)
					mapRegion.decodeEffectsClient(offsetZ, ClientState.activeToolkit, packet, skyboxData, offsetX)
					if (!mapRegion.underwater && mapSquareX == MapRegion.mapCenterChunkX / 8 && MapRegion.mapBaseChunkY / 8 == mapSquareZ && skyboxData[0] != -1) {
						Skybox.configNpcJs5 = TypeLists.SKYBOX!!.generateSkybox(skyboxData[2], skyboxData[3], skyboxData[1], TypeLists.SUN, skyboxData[0])
						Skybox.skyboxAngle = skyboxData[4]
					}
				}
				i++
			}
			@Pc(133) var j: Int = 0
			while (j < length) {
				@Pc(145) val regionOffsetX: Int = (MapRegion.regionIds!![j] shr 8) * 64 - MapRegion.sceneBaseX
				val regionOffsetZ: Int = (MapRegion.regionIds!![j] and 0xFF) * 64 - MapRegion.sceneBaseY
				@Pc(160) val regionData: ByteArray? = mapData[j]
				if (regionData == null && MapRegion.mapBaseChunkY < 800) {
					SoundEffectPlayer.updatePlayers()
					mapRegion!!.setMapSquareHeights(regionOffsetZ, regionOffsetX)
				}
				j++
			}
		}

		@OriginalMember(owner = "client!pda", name = "Q", descriptor = "[I")
		@JvmField
		val WALL_DECOR_OFFSET_Z: IntArray = intArrayOf(0, -1, 0, 1)
		@OriginalMember(owner = "client!il", name = "p", descriptor = "I")
		@JvmField
		var currentMapSize: Int = -1

		@OriginalMember(owner = "client!qu", name = "O", descriptor = "[I")
		@JvmField
		var roofMaxX: IntArray = IntArray(2)

		@OriginalMember(owner = "client!raa", name = "y", descriptor = "I")
		@JvmField
		var viewRangeStart: Int = 0

		@OriginalMember(owner = "client!tm", name = "j", descriptor = "I")
		@JvmField
		var inTutorialIsland: Int = 0

		@OriginalMember(owner = "client!tt", name = "s", descriptor = "I")
		@JvmField
		var viewRangeEnd: Int = 0

		@OriginalMember(owner = "client!tw", name = "G", descriptor = "[[B")
		@JvmField
		var locMapBuffers: Array<ByteArray?>? = null

		@OriginalMember(owner = "client!ha", name = "u", descriptor = "I")
		@JvmField
		var mapCenterChunkX: Int = 0

		@OriginalMember(owner = "client!ka", name = "d", descriptor = "[I")
		@JvmField
		var roofMinZ: IntArray = IntArray(2)

		@OriginalMember(owner = "client!wn", name = "b", descriptor = "[I")
		@JvmField
		val roofQueueZ: IntArray = IntArray(4096)

		@OriginalMember(owner = "client!uv", name = "i", descriptor = "[I")
		@JvmField
		val FOG_VISIBLE_TILES: IntArray = intArrayOf(32, 39, 44, 47)

		@OriginalMember(owner = "client!jj", name = "H", descriptor = "[I")
		@JvmField
		var roofMaxZ: IntArray = IntArray(2)

		@OriginalMember(owner = "client!iha", name = "u", descriptor = "[I")
		@JvmField
		val roofQueueX: IntArray = IntArray(4096)

		@OriginalMember(owner = "client!sga", name = "a", descriptor = "(IZ)V")
		@JvmStatic
		fun applyViewDepthRange(@OriginalArg(1) applyOrtho: Boolean) {
			@Pc(5) var zFar: Int = MapRegion.viewRangeEnd
			@Pc(7) var zNear: Int = MapRegion.viewRangeStart
			if (applyOrtho && OrthoMode.orthographicProjection) {
				zFar = zFar shl 0x1
				zNear = -zFar
			}
			ClientState.activeToolkit!!.f(zNear, zFar)
		}

		@OriginalMember(owner = "client!ug", name = "a", descriptor = "(I)V")
		@JvmStatic
		fun handleRoofDisplay() {
			if (GamePreferences.uid!!.lb!!.getValue() != 2) {
				return
			}
			@Pc(19) val roofValue: Byte = ((SceneDrawer.timesSceneRendered - 4) and 0xFF).toByte()
			@Pc(23) val column: Int = SceneDrawer.timesSceneRendered % MapRegion.sceneSizeX
			@Pc(29) var local29: Int
			@Pc(25) var i: Int = 0
			while (i < 4) {
				var tileZ: Int = 0
				while (tileZ < MapRegion.sceneSizeY) {
					MapRegion.roofStamps!![i]!![column]!![tileZ] = roofValue
					tileZ++
				}
				i++
			}
			if (PlayerUpdate.currentLevel == 3) {
				return
			}
			var viewIndex: Int = 0
			while (viewIndex < 2) {
				MapRegion.visibleBelowDrawheight[viewIndex] = -1000000
				MapRegion.roofMinX[viewIndex] = 1000000
				MapRegion.roofMaxX[viewIndex] = 0
				MapRegion.roofMinZ[viewIndex] = 1000000
				MapRegion.roofMaxZ[viewIndex] = 0
				viewIndex++
			}
			@Pc(84) var targetX: Int = PlayerUpdate.localPlayer!!.x
			@Pc(87) var targetY: Int = PlayerUpdate.localPlayer!!.z
			@Pc(141) var local141: Int
			if (Camera.cameraCalculationType != CameraMode.MODE_DEFAULT && Camera.roofRemovalTargetX == -1) {
				val cameraGroundHeight: Int = Scene.tileHeightForAbsolutePosition(Camera.cameraPositionAbsoluteX, PlayerUpdate.currentLevel, Camera.cameraPositionAbsoluteY)
				if (cameraGroundHeight - Camera.cameraPositionAbsoluteZ < 3200 && (SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![Camera.cameraPositionAbsoluteX shr 9]!![Camera.cameraPositionAbsoluteY shr 9].toInt() and TileFlag.REMOVE_ROOF) != 0) {
					MapRegion.calculateVisibilityAdjustments(Camera.cameraPositionAbsoluteY shr 9, Scene.aClass100ArrayArrayArray3, Camera.cameraPositionAbsoluteX shr 9, 1, false)
					return
				}
				return
			}
			if (Camera.cameraCalculationType != CameraMode.MODE_DEFAULT) {
				targetY = Camera.roofRemovalTargetY
				targetX = Camera.roofRemovalTargetX
			}
			if ((SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![targetX shr 9]!![targetY shr 9].toInt() and TileFlag.REMOVE_ROOF) != 0) {
				MapRegion.calculateVisibilityAdjustments(targetY shr 9, Scene.aClass100ArrayArrayArray3, targetX shr 9, 0, false)
			}
			if (Camera.cameraPitch >= 2560) {
				return
			}
			var cameraTileX: Int = Camera.cameraPositionAbsoluteX shr 9
			@Pc(145) var cameraTileY: Int = Camera.cameraPositionAbsoluteY shr 9
			@Pc(149) val targetTileX: Int = targetX shr 9
			@Pc(153) val targetTileY: Int = targetY shr 9
			@Pc(161) val deltaX: Int
			if (targetTileX > cameraTileX) {
				deltaX = targetTileX - cameraTileX
			} else {
				deltaX = cameraTileX - targetTileX
			}
			@Pc(176) val deltaY: Int
			if (targetTileY <= cameraTileY) {
				deltaY = cameraTileY - targetTileY
			} else {
				deltaY = targetTileY - cameraTileY
			}
			if ((deltaX != 0 || deltaY != 0) && -MapRegion.sceneSizeX < deltaX && deltaX < MapRegion.sceneSizeX && -MapRegion.sceneSizeY < deltaY && MapRegion.sceneSizeY > deltaY) {
				@Pc(256) val slope: Int
				@Pc(258) var accumulator: Int
				if (deltaY >= deltaX) {
					slope = deltaX * 65536 / deltaY
					accumulator = 32768
					while (targetTileY != cameraTileY) {
						if (targetTileY > cameraTileY) {
							cameraTileY++
						} else if (cameraTileY > targetTileY) {
							cameraTileY--
						}
						if ((SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![cameraTileX]!![cameraTileY].toInt() and TileFlag.REMOVE_ROOF) != 0) {
							MapRegion.calculateVisibilityAdjustments(cameraTileY, Scene.aClass100ArrayArrayArray3, cameraTileX, 1, false)
							return
						}
						accumulator += slope
						if (accumulator >= 65536) {
							if (cameraTileX < targetTileX) {
								cameraTileX++
							} else if (targetTileX < cameraTileX) {
								cameraTileX--
							}
							accumulator -= 65536
							if ((SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![cameraTileX]!![cameraTileY].toInt() and 0x4) != 0) {
								MapRegion.calculateVisibilityAdjustments(cameraTileY, Scene.aClass100ArrayArrayArray3, cameraTileX, 1, false)
								return
							}
						}
					}
					return
				}
				slope = deltaY * 65536 / deltaX
				accumulator = 32768
				while (cameraTileX != targetTileX) {
					if (cameraTileX < targetTileX) {
						cameraTileX++
					} else if (cameraTileX > targetTileX) {
						cameraTileX--
					}
					if ((SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![cameraTileX]!![cameraTileY].toInt() and TileFlag.REMOVE_ROOF) != 0) {
						MapRegion.calculateVisibilityAdjustments(cameraTileY, Scene.aClass100ArrayArrayArray3, cameraTileX, 1, false)
						return
					}
					accumulator += slope
					if (accumulator >= 65536) {
						if (targetTileY > cameraTileY) {
							cameraTileY++
						} else if (targetTileY < cameraTileY) {
							cameraTileY--
						}
						accumulator -= 65536
						if ((SettingsBits.tileFunctionMasks!![PlayerUpdate.currentLevel]!![cameraTileX]!![cameraTileY].toInt() and TileFlag.REMOVE_ROOF) != 0) {
							MapRegion.calculateVisibilityAdjustments(cameraTileY, Scene.aClass100ArrayArrayArray3, cameraTileX, 1, false)
							return
						}
					}
				}
				return
			}
			ExplicitRuntimeException.reportError("RC: " + cameraTileX + "," + cameraTileY + " " + targetTileX + "," + targetTileY + " " + MapRegion.sceneBaseX + "," + MapRegion.sceneBaseY, null)
			return
		}

		@OriginalMember(owner = "client!os", name = "g", descriptor = "[I")
		@JvmField
		var roofMinX: IntArray = IntArray(2)

		@OriginalMember(owner = "client!cga", name = "j", descriptor = "I")
		@JvmField
		var areaMode: Int = 0

		@OriginalMember(owner = "client!cv", name = "q", descriptor = "[[B")
		@JvmField
		var npcSpawnBuffers: Array<ByteArray?>? = null

		@OriginalMember(owner = "client!gs", name = "b", descriptor = "[I")
		@JvmField
		val DRAW_DISTANCES_NO_FOG: IntArray = intArrayOf(28, 35, 40, 44)

		@OriginalMember(owner = "client!it", name = "j", descriptor = "Z")
		const val SKIP_OCCLUDER_LOCS: Boolean = false

		@OriginalMember(owner = "client!de", name = "f", descriptor = "I")
		@JvmField
		var mapBaseChunkY: Int = 0

		@OriginalMember(owner = "client!de", name = "h", descriptor = "[I")
		@JvmField
		var visibleBelowDrawheight: IntArray = IntArray(2)

		@OriginalMember(owner = "client!id", name = "a", descriptor = "(I)V")
		@JvmStatic
		fun enableLowDetailShadows() {
			MapRegion.lowDetailShadows = true
		}

		@OriginalMember(owner = "client!pga", name = "n", descriptor = "I")
		@JvmField
		var mapBuildLevel: Int = 0

		@OriginalMember(owner = "client!vga", name = "a", descriptor = "(II)Z")
		@JvmStatic
		fun allowsOutOfBounds(@OriginalArg(1) areaMode: Int): Boolean {
			return areaMode == 3 || areaMode == 4
		}

		@OriginalMember(owner = "client!vs", name = "e", descriptor = "[[[B")
		@JvmField
		var roofStamps: Array<Array<ByteArray?>?>? = null

		@OriginalMember(owner = "client!vs", name = "a", descriptor = "(B)V")
		@JvmStatic
		fun updateInTutorialIsland() {
			MapRegion.inTutorialIsland = 0
			@Pc(13) val absoluteX: Int = (PlayerUpdate.localPlayer!!.x shr 9) + MapRegion.sceneBaseX
			@Pc(20) val absoluteZ: Int = MapRegion.sceneBaseY + (PlayerUpdate.localPlayer!!.z shr 9)
			if (absoluteX >= 3053 && absoluteX <= 3156 && absoluteZ >= 3056 && absoluteZ <= 3136) {
				MapRegion.inTutorialIsland = 1
			}
			if (absoluteX >= 3072 && absoluteX <= 3118 && absoluteZ >= 9492 && absoluteZ <= 9535) {
				MapRegion.inTutorialIsland = 1
			}
			if (MapRegion.inTutorialIsland == 1 && absoluteX >= 3139 && absoluteX <= 3199 && absoluteZ >= 3008 && absoluteZ <= 3062) {
				MapRegion.inTutorialIsland = 0
			}
		}
	}
}
