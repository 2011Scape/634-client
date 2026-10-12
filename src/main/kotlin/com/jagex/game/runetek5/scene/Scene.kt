package com.jagex.game.runetek5.scene

import com.jagex.game.runetek5.statics.UnderwaterTileGrid
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.ScriptRunner
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity
import com.jagex.game.runetek5.entity.GroundDecor
import com.jagex.game.runetek5.entity.InteractiveEntityContainer
import com.jagex.game.runetek5.entity.Wall
import com.jagex.game.runetek5.entity.WallDecor
import com.jagex.game.runetek5.graphics.TileShadowLightSharing
import com.jagex.game.runetek5.scene.tile.Tile
import org.openrs2.deob.annotation.OriginalArg
import com.jagex.game.runetek5.AbstractNanoTimer
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.PickList
import com.jagex.game.runetek5.entity.EntityNode
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.RenderQueue
import com.jagex.game.runetek5.RenderWorker
import com.jagex.game.runetek5.core.constants.LocLayer
import com.jagex.game.runetek5.core.constants.LocShapes
import com.jagex.game.runetek5.entity.DynamicGroundDecor
import com.jagex.game.runetek5.entity.DynamicLocation
import com.jagex.game.runetek5.entity.DynamicWall
import com.jagex.game.runetek5.entity.DynamicWallDecor
import com.jagex.game.runetek5.entity.gameobject.GameObject
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.scene.culling.Culling
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.entity.grounditem.AbstractGroundItem
import com.jagex.game.runetek5.graphics.light.StaticPointLight
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.core.constants.TileFlag
import com.jagex.game.runetek5.entity.PathingEntity

object Scene {

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "()V")
	@JvmStatic
	fun destroy() {
		@Pc(3) var local3: Int
		@Pc(6) var local6: Int
		@Pc(9) var local9: Int
		if (Scene.aClass100ArrayArrayArray1 != null) {
			var level: Int = 0
			while (level < Scene.aClass100ArrayArrayArray1!!.size) {
				var tileX: Int = 0
				while (tileX < Scene.sceneWidth) {
					var tileZ: Int = 0
					while (tileZ < Camera.sceneLength) {
						if (Scene.aClass100ArrayArrayArray1!![level]!![tileX]!![tileZ] != null) {
							Scene.aClass100ArrayArrayArray1!![level]!![tileX]!![tileZ]!!.clearInteractives()
						}
						Scene.aClass100ArrayArrayArray1!![level]!![tileX]!![tileZ] = null
						tileZ++
					}
					tileX++
				}
				level++
			}
		}
		Scene.aClass100ArrayArrayArray1 = null
		Scene.aClass65Array2 = null
		if (UnderwaterTileGrid.aClass100ArrayArrayArray2 != null) {
			var level: Int = 0
			while (level < UnderwaterTileGrid.aClass100ArrayArrayArray2!!.size) {
				var tileX: Int = 0
				while (tileX < Scene.sceneWidth) {
					var tileZ: Int = 0
					while (tileZ < Camera.sceneLength) {
						if (UnderwaterTileGrid.aClass100ArrayArrayArray2!![level]!![tileX]!![tileZ] != null) {
							UnderwaterTileGrid.aClass100ArrayArrayArray2!![level]!![tileX]!![tileZ]!!.clearInteractives()
						}
						UnderwaterTileGrid.aClass100ArrayArrayArray2!![level]!![tileX]!![tileZ] = null
						tileZ++
					}
					tileX++
				}
				level++
			}
		}
		UnderwaterTileGrid.aClass100ArrayArrayArray2 = null
		Scene.aClass65Array3 = null
		Scene.aClass100ArrayArrayArray3 = null
		Scene.aClass65Array1 = null
		Scene.tileInFrustum = null
		Scene.tileOnScreen = null
		Scene.tileVisibility = null
		Scene.levelTileVisibility = null
		Scene.aBooleanArrayArrayArray1 = null
		Culling.reset()
		if (Scene.aClass3_Sub1_Sub3Array1 != null) {
			var objectIndex: Int = 0
			while (objectIndex < Scene.interactiveObjectCount) {
				Scene.aClass3_Sub1_Sub3Array1!![objectIndex] = null
				objectIndex++
			}
			Scene.interactiveObjectCount = 0
		}
		Scene.stringStack = null
		Scene.aClass3_Sub1Array129 = null
		Scene.aClass3_Sub1Array94 = null
		if (Scene.aClass3_Sub1Array143 != null) {
			var opaqueIndex: Int = 0
			while (opaqueIndex < Scene.aClass3_Sub1Array143!!.size) {
				Scene.aClass3_Sub1Array143!![opaqueIndex] = null
				opaqueIndex++
			}
			Scene.onscreenOpaqueCount = 0
		}
		if (Scene.aClass3_Sub1Array145 != null) {
			var transparentIndex: Int = 0
			while (transparentIndex < Scene.aClass3_Sub1Array145!!.size) {
				Scene.aClass3_Sub1Array145!![transparentIndex] = null
				transparentIndex++
			}
			Scene.transparentEntityCount = 0
		}
		if (Scene.aClass300Array1 != null) {
			var lightSlot: Int = 0
			while (lightSlot < Scene.lightSlotCount) {
				Scene.aClass300Array1!![lightSlot] = null
				lightSlot++
			}
			var level: Int = 0
			while (level < Scene.length) {
				var tileX: Int = 0
				while (tileX < Scene.sceneWidth) {
					@Pc(195) var i: Int = 0
					while (i < Camera.sceneLength) {
						Scene.tileLightUids!![level]!![tileX]!![i] = 0L
						i++
					}
					tileX++
				}
				level++
			}
			Scene.lightSlotCount = 0
		}
		EntityNode.resetPickableEntityPools()
		PickList.aClass182_1 = PickList.aClass182_2
		PickList.aClass182_1.clear()
		Scene.waterIntensities = null
		Scene.waterColors = null
		Scene.waterScales = null
		if (RenderWorker.aClass366Array1 != null) {
			Scene.stopRenderWorkers()
			Scene.toolkit!!.allocateThreads(1)
			Scene.toolkit!!.linkThreads(0)
		}
		if (Scene.aClass59Array1 != null) {
			Scene.aClass59Array1 = null
		}
		Scene.toolkit = null
	}

	@OriginalMember(owner = "client!pu", name = "a", descriptor = "(IIILjava/lang/Class;)Lclient!jb;")
	@JvmStatic
	fun getInteractiveOfType(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int, @OriginalArg(3) class2: Class<*>?): AbstractInteractiveEntity? {
		@Pc(7) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![level]!![x]!![z]
		if (tile == null) {
			return null
		}
		@Pc(14) var positionEntityNode: InteractiveEntityContainer? = tile.interactives
		while (positionEntityNode != null) {
			@Pc(18) val positionEntity: AbstractInteractiveEntity? = positionEntityNode.interactiveObject
			if (class2!!.isAssignableFrom(positionEntity!!.javaClass) && positionEntity!!.startX.toInt() == x && positionEntity!!.startZ.toInt() == z) {
				return positionEntity
			}
			positionEntityNode = positionEntityNode.child
		}
		return null
	}

	@OriginalMember(owner = "client!ik", name = "a", descriptor = "(IIILclient!un;Lclient!un;)V")
	@JvmStatic
	fun addWallDecoration(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int, @OriginalArg(3) wallDecor: WallDecor?, @OriginalArg(4) wallDecor2: WallDecor?) {
		@Pc(4) val tile: Tile? = Scene.getTile(level, x, z)
		if (tile == null) {
			return
		}
		tile.primaryWallDecoration = wallDecor
		tile.secondaryWallDecoration = wallDecor2
		@Pc(19) val listIndex: Int = if (Scene.aClass65Array1 === Scene.aClass65Array3) 1 else 0
		if (!wallDecor!!.isStationary()) {
			wallDecor.nextEntity = Scene.aClass3_Sub1Array94!![listIndex]
			Scene.aClass3_Sub1Array94!![listIndex] = wallDecor
		} else if (wallDecor.hasAlpha()) {
			wallDecor.nextEntity = Scene.aClass3_Sub1Array129!![listIndex]
			Scene.aClass3_Sub1Array129!![listIndex] = wallDecor
		} else {
			wallDecor.nextEntity = Scene.stringStack!![listIndex]
			Scene.stringStack!![listIndex] = wallDecor
			Scene.staticSceneDirty = true
		}
		if (wallDecor2 == null) {
			return
		}
		if (wallDecor2.isStationary()) {
			if (wallDecor2.hasAlpha()) {
				wallDecor2.nextEntity = Scene.aClass3_Sub1Array129!![listIndex]
				Scene.aClass3_Sub1Array129!![listIndex] = wallDecor2
				return
			}
			wallDecor2.nextEntity = Scene.stringStack!![listIndex]
			Scene.stringStack!![listIndex] = wallDecor2
			Scene.staticSceneDirty = true
			return
		}
		wallDecor2.nextEntity = Scene.aClass3_Sub1Array94!![listIndex]
		Scene.aClass3_Sub1Array94!![listIndex] = wallDecor2
	}

	@OriginalMember(owner = "client!hfa", name = "a", descriptor = "(II)V")
	@JvmStatic
	fun linkBelow(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int) {
		@Pc(7) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![0]!![x]!![z]
		@Pc(9) var i: Int = 0
		while (i < 3) {
			@Pc(28) val tile2: Tile? = Scene.aClass100ArrayArrayArray3!![i + 1]!![x]!![z]
			Scene.aClass100ArrayArrayArray3!![i]!![x]!![z] = tile2
			if (tile2 != null) {
				@Pc(33) var positionEntityNode: InteractiveEntityContainer? = tile2.interactives
				while (positionEntityNode != null) {
					@Pc(37) val positionEntity: AbstractInteractiveEntity? = positionEntityNode.interactiveObject
					if (positionEntity!!.startX.toInt() == x && positionEntity!!.startZ.toInt() == z) {
						positionEntity!!.level--
					}
					positionEntityNode = positionEntityNode.child
				}
				if (tile2.groundDecoration != null) {
					tile2.groundDecoration!!.level--
				}
				if (tile2.primaryWall != null) {
					tile2.primaryWall!!.level--
				}
				if (tile2.secondaryWall != null) {
					tile2.secondaryWall!!.level--
				}
				if (tile2.primaryWallDecoration != null) {
					tile2.primaryWallDecoration!!.level--
				}
				if (tile2.secondaryWallDecoration != null) {
					tile2.secondaryWallDecoration!!.level--
				}
			}
			i++
		}
		if (Scene.aClass100ArrayArrayArray3!![0]!![x]!![z] == null) {
			Scene.aClass100ArrayArrayArray3!![0]!![x]!![z] = Tile(0)
			Scene.aClass100ArrayArrayArray3!![0]!![x]!![z]!!.plane = 1
		}
		Scene.aClass100ArrayArrayArray3!![0]!![x]!![z]!!.bridge = tile
		Scene.aClass100ArrayArrayArray3!![3]!![x]!![z] = null
	}

	@OriginalMember(owner = "client!ma", name = "i", descriptor = "(III)Lclient!aba;")
	@JvmStatic
	fun getGroundDecoration(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int): GroundDecor? {
		@Pc(7) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![level]!![x]!![z]
		return if (tile == null || tile.groundDecoration == null) null else tile.groundDecoration
	}

	@OriginalMember(owner = "client!ffa", name = "a", descriptor = "(IIILclient!qfa;Lclient!qfa;)V")
	@JvmStatic
	fun addWall(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int, @OriginalArg(3) wall: Wall?, @OriginalArg(4) wall2: Wall?) {
		@Pc(4) val tile: Tile? = Scene.getTile(level, x, z)
		if (tile == null) {
			return
		}
		tile.primaryWall = wall
		tile.secondaryWall = wall2
		@Pc(19) val listIndex: Int = if (Scene.aClass65Array1 === Scene.aClass65Array3) 1 else 0
		if (!wall!!.isStationary()) {
			wall.nextEntity = Scene.aClass3_Sub1Array94!![listIndex]
			Scene.aClass3_Sub1Array94!![listIndex] = wall
		} else if (wall.hasAlpha()) {
			wall.nextEntity = Scene.aClass3_Sub1Array129!![listIndex]
			Scene.aClass3_Sub1Array129!![listIndex] = wall
		} else {
			wall.nextEntity = Scene.stringStack!![listIndex]
			Scene.stringStack!![listIndex] = wall
			Scene.staticSceneDirty = true
		}
		if (wall2 == null) {
			return
		}
		if (wall2.isStationary()) {
			if (wall2.hasAlpha()) {
				wall2.nextEntity = Scene.aClass3_Sub1Array129!![listIndex]
				Scene.aClass3_Sub1Array129!![listIndex] = wall2
				return
			}
			wall2.nextEntity = Scene.stringStack!![listIndex]
			Scene.stringStack!![listIndex] = wall2
			Scene.staticSceneDirty = true
			return
		}
		wall2.nextEntity = Scene.aClass3_Sub1Array94!![listIndex]
		Scene.aClass3_Sub1Array94!![listIndex] = wall2
	}

	@OriginalMember(owner = "client!ma", name = "j", descriptor = "(III)V")
	@JvmStatic
	fun removeGroundDecoration(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int) {
		@Pc(7) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![level]!![x]!![z]
		if (tile != null) {
			Scene.removeFromEntityLists(tile.groundDecoration)
			if (tile.groundDecoration != null) {
				tile.groundDecoration = null
			}
		}
	}

	@OriginalMember(owner = "client!eca", name = "a", descriptor = "()V")
	@JvmStatic
	fun applyShading() {
		@Pc(1) var i: Int = ScriptRunner.minimumDrawLevel
		while (i < Scene.length) {
			@Pc(4) var j: Int = 0
			while (j < Scene.sceneWidth) {
				@Pc(7) var k: Int = 0
				while (k < Camera.sceneLength) {
					@Pc(16) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![i]!![j]!![k]
					if (tile != null) {
						@Pc(21) val wall: Wall? = tile.primaryWall
						@Pc(24) val wall2: Wall? = tile.secondaryWall
						if (wall != null && wall.hasTileShadow()) {
							TileShadowLightSharing.castShadow(wall, i, j, k, 1, 1)
							if (wall2 != null && wall2.hasTileShadow()) {
								TileShadowLightSharing.castShadow(wall2, i, j, k, 1, 1)
								wall2.shareLight(Scene.toolkit, 0, false, wall, 0, 0)
								wall2.removeTileShadow()
							}
							wall.removeTileShadow()
						}
						@Pc(68) var positionEntityNode: InteractiveEntityContainer? = tile.interactives
						while (positionEntityNode != null) {
							@Pc(72) val positionEntity: AbstractInteractiveEntity? = positionEntityNode.interactiveObject
							if (positionEntity != null && positionEntity.hasTileShadow()) {
								TileShadowLightSharing.castShadow(positionEntity, i, j, k, positionEntity.endX + 1 - positionEntity.startX, positionEntity.endZ - positionEntity.startZ + 1)
								positionEntity.removeTileShadow()
							}
							positionEntityNode = positionEntityNode.child
						}
						@Pc(108) val groundDecor: GroundDecor? = tile.groundDecoration
						if (groundDecor != null && groundDecor.hasTileShadow()) {
							Scene.shareLightWithNeighbourGroundDecor(groundDecor, i, j, k)
							groundDecor.removeTileShadow()
						}
					}
					k++
				}
				j++
			}
			i++
		}
	}

	@OriginalMember(owner = "client!rba", name = "A", descriptor = "I")
	@JvmField
	var sceneWidth: Int = 0

	@OriginalMember(owner = "client!fg", name = "d", descriptor = "I")
	@JvmField
	var tileUnits: Int = 0

	@OriginalMember(owner = "client!eca", name = "p", descriptor = "[[I")
	@JvmField
	var waterColors: Array<IntArray?>? = null

	@OriginalMember(owner = "client!ffa", name = "a", descriptor = "(IIILjava/lang/Class;)V")
	@JvmStatic
	fun removeInteractiveOfType(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int, @OriginalArg(3) class2: Class<*>?) {
		@Pc(7) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![level]!![x]!![z]
		if (tile == null) {
			return
		}
		@Pc(13) var positionEntityNode: InteractiveEntityContainer? = tile.interactives
		while (positionEntityNode != null) {
			@Pc(17) val positionEntity: AbstractInteractiveEntity? = positionEntityNode.interactiveObject
			if (class2!!.isAssignableFrom(positionEntity!!.javaClass) && positionEntity!!.startX.toInt() == x && positionEntity!!.startZ.toInt() == z) {
				Scene.removeInteractive(positionEntity, false)
				return
			}
			positionEntityNode = positionEntityNode.child
		}
	}

	@OriginalMember(owner = "client!lba", name = "a", descriptor = "(III)Lclient!qfa;")
	@JvmStatic
	fun getSecondaryWall(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int): Wall? {
		@Pc(7) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![level]!![x]!![y]
		return if (tile == null) null else tile.secondaryWall
	}

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(III)V")
	@JvmStatic
	fun removeWallDecorations(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int) {
		@Pc(7) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![level]!![x]!![z]
		if (tile == null) {
			return
		}
		Scene.removeFromEntityLists(tile.primaryWallDecoration)
		Scene.removeFromEntityLists(tile.secondaryWallDecoration)
		if (tile.primaryWallDecoration != null) {
			tile.primaryWallDecoration = null
		}
		if (tile.secondaryWallDecoration != null) {
			tile.secondaryWallDecoration = null
		}
	}

	@OriginalMember(owner = "client!uc", name = "b", descriptor = "(III)Lclient!qfa;")
	@JvmStatic
	fun getWall(@OriginalArg(0) minLevel: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int): Wall? {
		@Pc(7) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![minLevel]!![x]!![z]
		return if (tile == null) null else tile.primaryWall
	}

	@OriginalMember(owner = "client!naa", name = "bb", descriptor = "I")
	@JvmField
	var halfTileOffset: Int = 0

	@OriginalMember(owner = "client!og", name = "i", descriptor = "[[[Lclient!es;")
	@JvmField
	var aClass100ArrayArrayArray3: Array<Array<Array<Tile?>?>?>? = null

	@OriginalMember(owner = "client!kj", name = "e", descriptor = "(B)V")
	@JvmStatic
	fun setRoofOverride() {
		@Pc(10) val roofMode: Int = GamePreferences.uid!!.lb!!.getValue()
		if (roofMode == 0) {
			MapRegion.roofStamps = null
			Scene.initializeRoofSettings(0)
		} else if (roofMode == 1) {
			Scene.fillRoofStamps(0.toByte())
			Scene.initializeRoofSettings(512)
			if (SettingsBits.tileFunctionMasks != null) {
				Scene.hideAllRoofs()
			}
		} else {
			Scene.fillRoofStamps(((SceneDrawer.timesSceneRendered - 4) and 0xFF).toByte())
			Scene.initializeRoofSettings(2)
		}
		PlayerUpdate.MY_PLAYER_PLANE = PlayerUpdate.currentLevel
	}

	@OriginalMember(owner = "client!o", name = "m", descriptor = "Lclient!io;")
	@JvmField
	var aClass158_1: AbstractNanoTimer? = null

	@OriginalMember(owner = "client!ta", name = "a", descriptor = "(IIII)V")
	@JvmStatic
	fun offsetWallDecoration(@OriginalArg(0) minLevel: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int, @OriginalArg(3) decorDisplacement: Int) {
		@Pc(7) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![minLevel]!![x]!![z]
		if (tile == null) {
			return
		}
		@Pc(13) val wallDecor: WallDecor? = tile.primaryWallDecoration
		@Pc(16) val wallDecor2: WallDecor? = tile.secondaryWallDecoration
		if (wallDecor != null) {
			wallDecor.offsetX = (wallDecor.offsetX * decorDisplacement / (0x10 shl (Scene.tileUnits - 7))).toShort()
			wallDecor.offsetZ = (wallDecor.offsetZ * decorDisplacement / (0x10 shl (Scene.tileUnits - 7))).toShort()
		}
		if (wallDecor2 != null) {
			wallDecor2.offsetX = (wallDecor2.offsetX * decorDisplacement / (0x10 shl (Scene.tileUnits - 7))).toShort()
			wallDecor2.offsetZ = (wallDecor2.offsetZ * decorDisplacement / (0x10 shl (Scene.tileUnits - 7))).toShort()
		}
	}

	@OriginalMember(owner = "client!bu", name = "a", descriptor = "(ILclient!s;)V")
	@JvmStatic
	fun setLocalGround(@OriginalArg(0) level: Int, @OriginalArg(1) ground: Ground?) {
		Scene.aClass65Array1!![level] = ground
	}

	@OriginalMember(owner = "client!qe", name = "a", descriptor = "(Lclient!ga;)V")
	@JvmStatic
	fun removeFromEntityLists(@OriginalArg(0) entity: Entity?) {
		if (entity == null) {
			return
		}
		@Pc(4) var i: Int = 0
		while (i < 2) {
			@Pc(7) var entity2: Entity? = null
			@Pc(11) var entity3: Entity? = Scene.stringStack!![i]
			while (entity3 != null) {
				if (entity3 === entity) {
					if (entity2 == null) {
						Scene.stringStack!![i] = entity3.nextEntity
					} else {
						entity2.nextEntity = entity3.nextEntity
					}
					Scene.staticSceneDirty = true
					return
				}
				entity2 = entity3
				entity3 = entity3.nextEntity
			}
			entity2 = null
			@Pc(43) var entity4: Entity? = Scene.aClass3_Sub1Array129!![i]
			while (entity4 != null) {
				if (entity4 === entity) {
					if (entity2 == null) {
						Scene.aClass3_Sub1Array129!![i] = entity4.nextEntity
					} else {
						entity2.nextEntity = entity4.nextEntity
					}
					Scene.staticSceneDirty = true
					return
				}
				entity2 = entity4
				entity4 = entity4.nextEntity
			}
			entity2 = null
			@Pc(75) var entity5: Entity? = Scene.aClass3_Sub1Array94!![i]
			while (entity5 != null) {
				if (entity5 === entity) {
					if (entity2 == null) {
						Scene.aClass3_Sub1Array94!![i] = entity5.nextEntity
					} else {
						entity2.nextEntity = entity5.nextEntity
					}
					Scene.staticSceneDirty = true
					return
				}
				entity2 = entity5
				entity5 = entity5.nextEntity
			}
			i++
		}
	}

	@OriginalMember(owner = "client!wl", name = "b", descriptor = "(III)Lclient!es;")
	@JvmStatic
	fun getTile(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int): Tile? {
		if (Scene.aClass100ArrayArrayArray3!![level]!![x]!![z] == null) {
			@Pc(28) val isBridge: Boolean = Scene.aClass100ArrayArrayArray3!![0]!![x]!![z] != null && Scene.aClass100ArrayArrayArray3!![0]!![x]!![z]!!.bridge != null
			if (isBridge && level >= Scene.length - 1) {
				return null
			}
			Scene.loadTiles(level, x, z)
		}
		return Scene.aClass100ArrayArrayArray3!![level]!![x]!![z]
	}

	@OriginalMember(owner = "client!wl", name = "a", descriptor = "(Lclient!jb;Z)V")
	@JvmStatic
	fun removeInteractive(@OriginalArg(0) positionEntity: AbstractInteractiveEntity?, @OriginalArg(1) clearFully: Boolean) {
		@Pc(2) var i: Int = positionEntity!!.startX.toInt()
		while (i <= positionEntity.endX) {
			@Pc(6) var j: Int = positionEntity.startZ.toInt()
			while (j <= positionEntity.endZ) {
				@Pc(16) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![positionEntity.level.toInt()]!![i]!![j]
				if (tile != null) {
					@Pc(21) var positionEntityNode: InteractiveEntityContainer? = tile.interactives
					@Pc(23) var positionEntityNode2: InteractiveEntityContainer? = null
					while (positionEntityNode != null) {
						if (positionEntityNode.interactiveObject === positionEntity) {
							if (positionEntityNode2 == null) {
								tile.interactives = positionEntityNode.child
							} else {
								positionEntityNode2.child = positionEntityNode.child
							}
							positionEntityNode.release()
							break
						}
						positionEntityNode2 = positionEntityNode
						positionEntityNode = positionEntityNode.child
					}
				}
				j++
			}
			i++
		}
		if (!clearFully) {
			Scene.removeFromEntityLists(positionEntity)
		}
	}

	@OriginalMember(owner = "client!pha", name = "a", descriptor = "(Lclient!r;III[Z)V")
	@JvmStatic
	fun removeShadow(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) virtualLevel: Int, @OriginalArg(2) x: Int, @OriginalArg(3) z: Int, @OriginalArg(4) castShadowAtPlane: BooleanArray?) {
		if (Scene.aClass65Array1 === Scene.aClass65Array3) {
			return
		}
		@Pc(10) val height: Int = Scene.aClass65Array2!![virtualLevel]!!.averageHeight(x, z)
		@Pc(12) var i: Int = 0
		while (i <= virtualLevel) {
			if (castShadowAtPlane == null || castShadowAtPlane[i]) {
				@Pc(23) val ground: Ground? = Scene.aClass65Array2!![i]
				if (ground != null) {
					ground.wa(shadow, x, height - ground.averageHeight(x, z), z, 0, false)
				}
			}
			i++
		}
	}

	@OriginalMember(owner = "client!ag", name = "d", descriptor = "[Lclient!s;")
	@JvmField
	var aClass65Array1: Array<Ground?>? = null

	@OriginalMember(owner = "client!wi", name = "d", descriptor = "(III)Lclient!un;")
	@JvmStatic
	fun getWallDecoration(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int): WallDecor? {
		@Pc(7) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![level]!![x]!![z]
		return if (tile == null) null else tile.primaryWallDecoration
	}

	@OriginalMember(owner = "client!ufa", name = "a", descriptor = "()V")
	@JvmStatic
	fun stopRenderWorkers() {
		@Pc(1) var i: Int = 0
		while (i < RenderWorker.aClass366Array1.size) {
			RenderWorker.aClass366Array1[i].stop()
			i++
		}
		RenderWorker.aClass366Array1 = null
	}

	@OriginalMember(owner = "client!iha", name = "C", descriptor = "Z")
	@JvmField
	var staticSceneDirty: Boolean = true

	@OriginalMember(owner = "client!dia", name = "h", descriptor = "I")
	@JvmField
	var lightSlotCount: Int = 0

	@OriginalMember(owner = "client!ww", name = "K", descriptor = "I")
	@JvmField
	var onscreenOpaqueCount: Int = 0

	@OriginalMember(owner = "client!lj", name = "x", descriptor = "[[[Z")
	@JvmField
	var aBooleanArrayArrayArray1: Array<Array<BooleanArray?>?>? = null

	@OriginalMember(owner = "client!mv", name = "d", descriptor = "[Lclient!ga;")
	@JvmField
	var aClass3_Sub1Array94: Array<Entity?>? = null

	@OriginalMember(owner = "client!dj", name = "e", descriptor = "[Lclient!s;")
	@JvmField
	var aClass65Array3: Array<Ground?>? = null

	@OriginalMember(owner = "client!ap", name = "d", descriptor = "[Lclient!jb;")
	@JvmField
	var aClass3_Sub1_Sub3Array1: Array<AbstractInteractiveEntity?>? = null

	@OriginalMember(owner = "client!gia", name = "q", descriptor = "[[Z")
	@JvmField
	var tileOnScreen: Array<BooleanArray?>? = null

	@OriginalMember(owner = "client!me", name = "a", descriptor = "[[Z")
	@JvmField
	var tileInFrustum: Array<BooleanArray?>? = null

	@OriginalMember(owner = "client!gs", name = "e", descriptor = "Lclient!ha;")
	@JvmField
	var toolkit: Toolkit? = null

	@OriginalMember(owner = "client!lea", name = "a", descriptor = "I")
	@JvmField
	var interactiveObjectCount: Int = 0

	@OriginalMember(owner = "client!mv", name = "a", descriptor = "[[[Z")
	@JvmField
	var levelTileVisibility: Array<Array<BooleanArray?>?>? = null

	@OriginalMember(owner = "client!lk", name = "C", descriptor = "I")
	@JvmField
	var transparentEntityCount: Int = 0

	@OriginalMember(owner = "client!qf", name = "a", descriptor = "(III)V")
	@JvmStatic
	fun loadTiles(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int) {
		@Pc(20) val hasBridge: Boolean = Scene.aClass100ArrayArrayArray3!![0]!![x]!![y] != null && Scene.aClass100ArrayArrayArray3!![0]!![x]!![y]!!.bridge != null
		@Pc(22) var i: Int = level
		while (i >= 0) {
			if (Scene.aClass100ArrayArrayArray3!![i]!![x]!![y] == null) {
				@Pc(44) val tile: Tile = Tile(i)
				Scene.aClass100ArrayArrayArray3!![i]!![x]!![y] = tile
				if (hasBridge) {
					tile.plane++
				}
			}
			i--
		}
	}

	@OriginalMember(owner = "client!ol", name = "a", descriptor = "(I[[[Lclient!es;)V")
	@JvmStatic
	fun uploadGameObjects(@OriginalArg(1) tiles: Array<Array<Array<Tile?>?>?>?) {
		@Pc(7) var i: Int = 0
		while (i < tiles!!.size) {
			@Pc(13) val tiles2: Array<Array<Tile?>?>? = tiles[i]
			@Pc(15) var j: Int = 0
			while (j < tiles2!!.size) {
				@Pc(19) var k: Int = 0
				while (k < tiles2[j]!!.size) {
					@Pc(27) val tile: Tile? = tiles2[j]!![k]
					if (tile != null) {
						if (tile.groundDecoration is GameObject) {
							(tile.groundDecoration as GameObject).upload()
						}
						if (tile.primaryWallDecoration is GameObject) {
							(tile.primaryWallDecoration as GameObject).upload()
						}
						if (tile.secondaryWallDecoration is GameObject) {
							(tile.secondaryWallDecoration as GameObject).upload()
						}
						if (tile.primaryWall is GameObject) {
							(tile.primaryWall as GameObject).upload()
						}
						if (tile.secondaryWall is GameObject) {
							(tile.secondaryWall as GameObject).upload()
						}
						@Pc(77) var positionEntityNode: InteractiveEntityContainer? = tile.interactives
						while (positionEntityNode != null) {
							@Pc(82) val positionEntity: AbstractInteractiveEntity? = positionEntityNode.interactiveObject
							if (positionEntity is GameObject) {
								(positionEntity as GameObject).upload()
							}
							positionEntityNode = positionEntityNode.child
						}
					}
					k++
				}
				j++
			}
			i++
		}
	}

	@OriginalMember(owner = "client!qj", name = "d", descriptor = "I")
	@JvmField
	var length: Int = 0

	@OriginalMember(owner = "client!lca", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun fillLevelTiles(@OriginalArg(0) level: Int) {
		ScriptRunner.minimumDrawLevel = level
		@Pc(3) var i: Int = 0
		while (i < Scene.sceneWidth) {
			@Pc(6) var j: Int = 0
			while (j < Camera.sceneLength) {
				if (Scene.aClass100ArrayArrayArray3!![level]!![i]!![j] == null) {
					Scene.aClass100ArrayArrayArray3!![level]!![i]!![j] = Tile(level)
				}
				j++
			}
			i++
		}
	}

	@OriginalMember(owner = "client!un", name = "a", descriptor = "(II)I")
	@JvmStatic
	fun getWaterScale(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int): Int {
		return if (Scene.waterScales == null) 0 else Scene.waterScales!![x]!![z].toInt() and 0xFFFF
	}

	@OriginalMember(owner = "client!eh", name = "i", descriptor = "[Lclient!ga;")
	@JvmField
	var stringStack: Array<Entity?>? = null

	@OriginalMember(owner = "client!dga", name = "N", descriptor = "[[B")
	@JvmField
	var waterIntensities: Array<ByteArray?>? = null

	@OriginalMember(owner = "client!vw", name = "s", descriptor = "[Lclient!ga;")
	@JvmField
	var aClass3_Sub1Array145: Array<Entity?>? = null

	@OriginalMember(owner = "client!kb", name = "k", descriptor = "[[[Lclient!es;")
	@JvmField
	var aClass100ArrayArrayArray1: Array<Array<Array<Tile?>?>?>? = null

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIIII)V")
	@JvmStatic
	fun animateObject(@OriginalArg(0) y: Int, @OriginalArg(1) animationId: Int, @OriginalArg(2) shape: Int, @OriginalArg(3) x: Int, @OriginalArg(4) rotation: Int, @OriginalArg(6) plane: Int, @OriginalArg(7) layer: Int) {
		var shape: Int = shape
		if (x < 0 || y < 0 || MapRegion.sceneSizeX - 1 <= x || MapRegion.sceneSizeY - 1 <= y) {
			return
		}
		if (Scene.aClass100ArrayArrayArray3 == null) {
			return
		}
		@Pc(37) var location: GameObject?
		if (layer == LocLayer.WALL) {
			location = Scene.getWall(plane, x, y) as GameObject?
			@Pc(43) val location2: GameObject? = Scene.getSecondaryWall(plane, x, y) as GameObject?
			if (location != null && shape != LocShapes.WALL_L) {
				if (location is DynamicWall) {
					(location as DynamicWall).locEntity.playSequence(animationId)
				} else {
					LocAction.replaceLoc(x, layer, animationId, location.getId(), shape, y, rotation, plane)
				}
			}
			if (location2 != null) {
				if (location2 is DynamicWall) {
					(location2 as DynamicWall).locEntity.playSequence(animationId)
				} else {
					LocAction.replaceLoc(x, layer, animationId, location2.getId(), shape, y, rotation, plane)
				}
			}
		} else if (layer == LocLayer.WALLDECOR) {
			location = Scene.getWallDecoration(plane, x, y) as GameObject?
			if (location != null) {
				if (location is DynamicWallDecor) {
					(location as DynamicWallDecor).locEntity.playSequence(animationId)
				} else {
					@Pc(215) val locId: Int = location.getId()
					if (shape == LocShapes.WALLDECOR_STRAIGHT_NOOFFSET || shape == LocShapes.WALLDECOR_STRAIGHT_OFFSET) {
						LocAction.replaceLoc(x, layer, animationId, locId, LocShapes.WALLDECOR_STRAIGHT_NOOFFSET, y, rotation, plane)
					} else if (shape == 6) {
						LocAction.replaceLoc(x, layer, animationId, locId, LocShapes.WALLDECOR_STRAIGHT_NOOFFSET, y, rotation + 4, plane)
					} else if (shape == 7) {
						LocAction.replaceLoc(x, layer, animationId, locId, LocShapes.WALLDECOR_STRAIGHT_NOOFFSET, y, ((rotation + 2) and 0x3) + 4, plane)
					} else if (shape == 8) {
						LocAction.replaceLoc(x, layer, animationId, locId, LocShapes.WALLDECOR_STRAIGHT_NOOFFSET, y, rotation + 4, plane)
						LocAction.replaceLoc(x, layer, animationId, locId, LocShapes.WALLDECOR_STRAIGHT_NOOFFSET, y, ((rotation + 2) and 0x3) + 4, plane)
					}
				}
			}
		} else if (layer == LocLayer.GROUND) {
			location = Scene.getInteractiveOfType(plane, x, y, GameObject::class.java) as GameObject?
			if (location != null) {
				if (shape == LocShapes.CENTREPIECE_DIAGONAL) {
					shape = LocShapes.CENTREPIECE_STRAIGHT
				}
				if (location is DynamicLocation) {
					(location as DynamicLocation).locEntity.playSequence(animationId)
				} else {
					LocAction.replaceLoc(x, layer, animationId, location.getId(), shape, y, rotation, plane)
				}
			}
		} else if (layer == LocLayer.GROUNDDECOR) {
			location = Scene.getGroundDecoration(plane, x, y) as GameObject?
			if (location != null) {
				if (location is DynamicGroundDecor) {
					(location as DynamicGroundDecor).locEntity.playSequence(animationId)
				} else {
					LocAction.replaceLoc(x, layer, animationId, location.getId(), shape, y, rotation, plane)
				}
			}
		}
	}

	@OriginalMember(owner = "client!vu", name = "x", descriptor = "[Lclient!ga;")
	@JvmField
	var aClass3_Sub1Array143: Array<Entity?>? = null

	@OriginalMember(owner = "client!sba", name = "t", descriptor = "I")
	@JvmField
	var renderCores: Int = 1

	@OriginalMember(owner = "client!tca", name = "a", descriptor = "(Z)V")
	@JvmStatic
	fun setUnderwater(@OriginalArg(0) underwater: Boolean) {
		if (underwater) {
			Scene.aClass100ArrayArrayArray3 = UnderwaterTileGrid.aClass100ArrayArrayArray2
			Scene.aClass65Array1 = Scene.aClass65Array3
		} else {
			Scene.aClass100ArrayArrayArray3 = Scene.aClass100ArrayArrayArray1
			Scene.aClass65Array1 = Scene.aClass65Array2
		}
		Scene.length = Scene.aClass100ArrayArrayArray3!!.size
	}

	@OriginalMember(owner = "client!rc", name = "a", descriptor = "(II)I")
	@JvmStatic
	fun getWaterIntensity(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int): Int {
		return if (Scene.waterIntensities == null) 0 else Scene.waterIntensities!![x]!![z].toInt() and 0xFF
	}

	@OriginalMember(owner = "client!pf", name = "J", descriptor = "[[[J")
	@JvmField
	var tileLightUids: Array<Array<LongArray?>?>? = null

	@OriginalMember(owner = "client!tm", name = "i", descriptor = "[Lclient!ga;")
	@JvmField
	var aClass3_Sub1Array129: Array<Entity?>? = null

	@OriginalMember(owner = "client!cn", name = "e", descriptor = "[Lclient!cia;")
	@JvmField
	var aClass59Array1: Array<RenderQueue?>? = null

	@OriginalMember(owner = "client!lha", name = "a", descriptor = "(Lclient!jb;Z)Z")
	@JvmStatic
	fun addInteractive(@OriginalArg(0) positionEntity: AbstractInteractiveEntity?, @OriginalArg(1) temporary: Boolean): Boolean {
		@Pc(6) val isUnderwater: Boolean = Scene.aClass65Array1 === Scene.aClass65Array3
		@Pc(8) var color: Int = 0
		@Pc(10) var scale: Short = 0
		@Pc(12) var intensity: Byte = 0
		positionEntity!!.updateBounds()
		if (positionEntity.startX < 0 || positionEntity.startZ < 0 || positionEntity.endX >= Scene.sceneWidth || positionEntity.endZ >= Camera.sceneLength) {
			return false
		}
		@Pc(33) var offsetY: Short = 0
		@Pc(40) var local40: Int
		@Pc(36) var i: Int = positionEntity.startX.toInt()
		while (i <= positionEntity.endX) {
			var tileZ: Int = positionEntity.startZ.toInt()
			while (tileZ <= positionEntity.endZ) {
				@Pc(47) val tile: Tile? = Scene.getTile(positionEntity.level.toInt(), i, tileZ)
				if (tile != null) {
					@Pc(53) val positionEntityNode: InteractiveEntityContainer = InteractiveEntityContainer.obtain(positionEntity)
					@Pc(56) var positionEntityNode2: InteractiveEntityContainer? = tile.interactives
					if (positionEntityNode2 == null) {
						tile.interactives = positionEntityNode
					} else {
						while (positionEntityNode2!!.child != null) {
							positionEntityNode2 = positionEntityNode2.child
						}
						positionEntityNode2.child = positionEntityNode
					}
					if (isUnderwater && (Scene.waterColors!![i]!![tileZ] and 0xFF000000.toInt()) != 0) {
						color = Scene.waterColors!![i]!![tileZ]
						scale = Scene.waterScales!![i]!![tileZ]
						intensity = Scene.waterIntensities!![i]!![tileZ]
					}
					if (!temporary && tile.groundDecoration != null && tile.groundDecoration!!.offsetY > offsetY) {
						offsetY = tile.groundDecoration!!.offsetY
					}
				}
				tileZ++
			}
			i++
		}
		if (isUnderwater && (color and 0xFF000000.toInt()) != 0) {
			var tileX: Int = positionEntity.startX.toInt()
			while (tileX <= positionEntity.endX) {
				@Pc(136) var j: Int = positionEntity.startZ.toInt()
				while (j <= positionEntity.endZ) {
					if ((Scene.waterColors!![tileX]!![j] and 0xFF000000.toInt()) == 0) {
						Scene.waterColors!![tileX]!![j] = color
						Scene.waterScales!![tileX]!![j] = scale
						Scene.waterIntensities!![tileX]!![j] = intensity
					}
					j++
				}
				tileX++
			}
		}
		if (temporary) {
			Scene.aClass3_Sub1_Sub3Array1!![Scene.interactiveObjectCount++] = positionEntity
		} else {
			val underwaterLayer: Int = if (Scene.aClass65Array1 === Scene.aClass65Array3) 1 else 0
			if (!positionEntity.isStationary()) {
				positionEntity.nextEntity = Scene.aClass3_Sub1Array94!![underwaterLayer]
				Scene.aClass3_Sub1Array94!![underwaterLayer] = positionEntity
			} else if (positionEntity.hasAlpha()) {
				positionEntity.nextEntity = Scene.aClass3_Sub1Array129!![underwaterLayer]
				Scene.aClass3_Sub1Array129!![underwaterLayer] = positionEntity
			} else {
				positionEntity.nextEntity = Scene.stringStack!![underwaterLayer]
				Scene.stringStack!![underwaterLayer] = positionEntity
				Scene.staticSceneDirty = true
			}
		}
		if (temporary) {
			positionEntity.y -= offsetY
		}
		return true
	}

	@OriginalMember(owner = "client!mw", name = "F", descriptor = "I")
	@JvmField
	var cameraTileZ: Int = 0

	@OriginalMember(owner = "client!pp", name = "z", descriptor = "[Lclient!ro;")
	@JvmField
	var aClass300Array1: Array<StaticPointLight?>? = null

	@OriginalMember(owner = "client!pl", name = "f", descriptor = "[I")
	@JvmField
	var tileVisibility: IntArray? = null

	@OriginalMember(owner = "client!pl", name = "a", descriptor = "()V")
	@JvmStatic
	fun addHdLight() {
		@Pc(1) var i: Int = 0
		while (i < Scene.lightSlotCount) {
			if (!Scene.pointLightApplied!![i]) {
				@Pc(10) val staticPointLight: StaticPointLight? = Scene.aClass300Array1!![i]
				@Pc(13) val pointLight: PointLight = staticPointLight!!.pointLight!!
				@Pc(16) val level: Int = staticPointLight.level
				@Pc(22) val offset: Int = pointLight.getRadius() - Scene.halfTileOffset
				@Pc(30) val dimension: Int = ((offset * 2) shr Scene.tileUnits) + 1
				@Pc(32) var rangeIndex: Int = 0
				@Pc(37) val lightDisplacement: IntArray = IntArray(dimension * dimension)
				@Pc(45) val sceneBaseX: Int = (pointLight.getX() - offset) shr Scene.tileUnits
				@Pc(53) var sceneStartY: Int = (pointLight.getZ() - offset) shr Scene.tileUnits
				@Pc(61) var sceneEndY: Int = (pointLight.getZ() + offset) shr Scene.tileUnits
				if (sceneStartY < 0) {
					rangeIndex = -sceneStartY
					sceneStartY = 0
				}
				if (sceneEndY >= Camera.sceneLength) {
					sceneEndY = Camera.sceneLength - 1
				}
				@Pc(78) var j: Int = sceneStartY
				while (j <= sceneEndY) {
					@Pc(84) val range: Short = staticPointLight.ranges!![rangeIndex]
					@Pc(88) val rangeStartOffset: Int = range.toInt() ushr 8
					@Pc(94) var displacementIndex: Int = rangeIndex * dimension + rangeStartOffset
					@Pc(100) var sceneStartX: Int = sceneBaseX + (range.toInt() ushr 8)
					@Pc(108) var sceneEndX: Int = sceneStartX + (range.toInt() and 0xFF) - 1
					if (sceneStartX < 0) {
						displacementIndex -= sceneStartX
						sceneStartX = 0
					}
					if (sceneEndX >= Scene.sceneWidth) {
						sceneEndX = Scene.sceneWidth - 1
					}
					@Pc(125) var k: Int = sceneStartX
					while (k <= sceneEndX) {
						@Pc(128) var displacement: Byte = 1
						@Pc(141) val positionEntity: AbstractInteractiveEntity? = Scene.getInteractiveOfType(level, k, j, AbstractInteractiveEntity::class.java)
						if (positionEntity != null && positionEntity.rotation.toInt() != 0) {
							@Pc(159) var westWithinScene: Boolean
							@Pc(168) var eastWithinScene: Boolean
							@Pc(182) var bitpackedRange: Short
							@Pc(188) var startX: Int
							@Pc(194) var endX: Int
							if (positionEntity.rotation.toInt() == 1) {
								westWithinScene = k - 1 >= sceneStartX
								eastWithinScene = k + 1 <= sceneEndX
								if (!westWithinScene && j + 1 <= sceneEndY) {
									bitpackedRange = staticPointLight.ranges!![rangeIndex + 1]
									startX = sceneBaseX + (bitpackedRange.toInt() ushr 8)
									endX = startX + (bitpackedRange.toInt() and 0xFF)
									westWithinScene = k > startX && k < endX
								}
								if (!eastWithinScene && j - 1 >= sceneStartY) {
									bitpackedRange = staticPointLight.ranges!![rangeIndex - 1]
									startX = sceneBaseX + (bitpackedRange.toInt() ushr 8)
									endX = startX + (bitpackedRange.toInt() and 0xFF)
									eastWithinScene = k > startX && k < endX
								}
								if (westWithinScene && !eastWithinScene) {
									displacement = 4
								} else if (eastWithinScene && !westWithinScene) {
									displacement = 2
								}
							} else {
								westWithinScene = k - 1 >= sceneStartX
								eastWithinScene = k + 1 <= sceneEndX
								if (!westWithinScene && j - 1 >= sceneStartY) {
									bitpackedRange = staticPointLight.ranges!![rangeIndex - 1]
									startX = sceneBaseX + (bitpackedRange.toInt() ushr 8)
									endX = startX + (bitpackedRange.toInt() and 0xFF)
									westWithinScene = k > startX && k < endX
								}
								if (!eastWithinScene && j + 1 <= sceneEndY) {
									bitpackedRange = staticPointLight.ranges!![rangeIndex + 1]
									startX = sceneBaseX + (bitpackedRange.toInt() ushr 8)
									endX = startX + (bitpackedRange.toInt() and 0xFF)
									eastWithinScene = k > startX && k < endX
								}
								if (westWithinScene && !eastWithinScene) {
									displacement = 3
								} else if (eastWithinScene && !westWithinScene) {
									displacement = 5
								}
							}
						}
						lightDisplacement[displacementIndex++] = displacement.toInt()
						k++
					}
					rangeIndex++
					j++
				}
				Scene.pointLightApplied!![i] = true
				Scene.aClass65Array1!![level]!!.addLight(pointLight, lightDisplacement)
			}
			i++
		}
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun hideAllRoofs() {
		@Pc(11) var viewIndex: Int = 0
		@Pc(13) var i: Int = 0
		while (i < MapRegion.sceneSizeX) {
			@Pc(17) var j: Int = 0
			while (j < MapRegion.sceneSizeY) {
				if (MapRegion.calculateVisibilityAdjustments(j, Scene.aClass100ArrayArrayArray3, i, viewIndex, true)) {
					viewIndex++
				}
				if (viewIndex >= 512) {
					return
				}
				j++
			}
			i++
		}
	}

	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(IIIILclient!fha;)V")
	@JvmStatic
	fun addGroundItem(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int, @OriginalArg(3) y: Int, @OriginalArg(4) groundItem: AbstractGroundItem?) {
		@Pc(4) val tile: Tile? = Scene.getTile(level, x, z)
		if (tile == null) {
			return
		}
		groundItem!!.x = (x shl Scene.tileUnits) + Scene.halfTileOffset
		groundItem.y = y
		groundItem.z = (z shl Scene.tileUnits) + Scene.halfTileOffset
		tile.groundItem = groundItem
		@Pc(33) val layerIndex: Int = if (Scene.aClass65Array1 === Scene.aClass65Array3) 1 else 0
		if (groundItem.isStationary()) {
			if (groundItem.hasAlpha()) {
				groundItem.nextEntity = Scene.aClass3_Sub1Array129!![layerIndex]
				Scene.aClass3_Sub1Array129!![layerIndex] = groundItem
				return
			}
			groundItem.nextEntity = Scene.stringStack!![layerIndex]
			Scene.stringStack!![layerIndex] = groundItem
			Scene.staticSceneDirty = true
			return
		}
		groundItem.nextEntity = Scene.aClass3_Sub1Array94!![layerIndex]
		Scene.aClass3_Sub1Array94!![layerIndex] = groundItem
	}

	@OriginalMember(owner = "client!kv", name = "c", descriptor = "(I)V")
	@JvmStatic
	fun uploadAllGameObjects() {
		if (Scene.gameObjectsUploaded) {
			return
		}
		Scene.uploadGameObjects(Scene.aClass100ArrayArrayArray1)
		if (UnderwaterTileGrid.aClass100ArrayArrayArray2 != null) {
			Scene.uploadGameObjects(UnderwaterTileGrid.aClass100ArrayArrayArray2)
		}
		Scene.gameObjectsUploaded = true
	}

	@OriginalMember(owner = "client!hl", name = "d", descriptor = "(III)V")
	@JvmStatic
	fun removeWalls(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int) {
		@Pc(7) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![level]!![x]!![z]
		if (tile == null) {
			return
		}
		Scene.removeFromEntityLists(tile.primaryWall)
		Scene.removeFromEntityLists(tile.secondaryWall)
		if (tile.primaryWall != null) {
			tile.primaryWall = null
		}
		if (tile.secondaryWall != null) {
			tile.secondaryWall = null
		}
	}

	@OriginalMember(owner = "client!bg", name = "H", descriptor = "[Lclient!s;")
	@JvmField
	var aClass65Array2: Array<Ground?>? = null

	@OriginalMember(owner = "client!pl", name = "a", descriptor = "(BIIIII)I")
	@JvmStatic
	fun getBridgedAverageHeight(@OriginalArg(1) x: Int, @OriginalArg(2) z: Int, @OriginalArg(3) centerTileX: Int, @OriginalArg(4) level: Int, @OriginalArg(5) centerTileZ: Int): Int {
		var level: Int = level
		if (Scene.aClass65Array1 == null) {
			return 0
		} else if (level < 3) {
			@Pc(22) val tileX: Int = x shr 9
			@Pc(26) val tileZ: Int = z shr 9
			if (centerTileX < 0 || centerTileZ < 0 || MapRegion.sceneSizeX - 1 < centerTileX || MapRegion.sceneSizeY - 1 < centerTileZ) {
				return 0
			} else if (tileX >= 1 && tileZ >= 1 && MapRegion.sceneSizeX - 1 >= tileX && tileZ <= MapRegion.sceneSizeY - 1) {
				@Pc(91) var bridged: Boolean = (SettingsBits.tileFunctionMasks!![1]!![x shr 9]!![z shr 9].toInt() and 0x2) != 0
				@Pc(116) var previousBridged: Boolean
				@Pc(132) var currentBridged: Boolean
				if ((x and 0x1FF) == 0) {
					previousBridged = (SettingsBits.tileFunctionMasks!![1]!![tileX - 1]!![z shr 9].toInt() and 0x2) != 0
					currentBridged = (SettingsBits.tileFunctionMasks!![1]!![tileX]!![z shr 9].toInt() and 0x2) != 0
					if (previousBridged != currentBridged) {
						bridged = (SettingsBits.tileFunctionMasks!![1]!![centerTileX]!![centerTileZ].toInt() and 0x2) != 0
					}
				}
				if ((z and 0x1FF) == 0) {
					previousBridged = (SettingsBits.tileFunctionMasks!![1]!![x shr 9]!![tileZ - 1].toInt() and 0x2) != 0
					currentBridged = (SettingsBits.tileFunctionMasks!![1]!![x shr 9]!![tileZ].toInt() and 0x2) != 0
					if (previousBridged != currentBridged) {
						bridged = (SettingsBits.tileFunctionMasks!![1]!![centerTileX]!![centerTileZ].toInt() and 0x2) != 0
					}
				}
				if (bridged) {
					level++
				}
				return Scene.aClass65Array1!![level]!!.averageHeight(x, z)
			} else {
				return 0
			}
		} else {
			return Scene.aClass65Array1!![level]!!.averageHeight(x, z)
		}
	}

	@OriginalMember(owner = "client!aj", name = "u", descriptor = "[[S")
	@JvmField
	var waterScales: Array<ShortArray?>? = null

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(II)I")
	@JvmStatic
	fun getWaterColor(@OriginalArg(0) tileX: Int, @OriginalArg(1) tileZ: Int): Int {
		return if (Scene.waterColors == null) 0 else Scene.waterColors!![tileX]!![tileZ] and 0xFFFFFF
	}

	@OriginalMember(owner = "client!qc", name = "I", descriptor = "Z")
	@JvmField
	var gameObjectsUploaded: Boolean = false

	@OriginalMember(owner = "client!r", name = "y", descriptor = "[Z")
	@JvmField
	var pointLightApplied: BooleanArray? = null

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(IB)V")
	@JvmStatic
	fun initializeRoofSettings(@OriginalArg(0) size: Int) {
		MapRegion.roofMaxX = IntArray(size)
		MapRegion.roofMaxZ = IntArray(size)
		MapRegion.roofMinX = IntArray(size)
		MapRegion.roofMinZ = IntArray(size)
		MapRegion.visibleBelowDrawheight = IntArray(size)
	}

	@OriginalMember(owner = "client!ug", name = "a", descriptor = "()V")
	@JvmStatic
	fun clearDynamicInteractives() {
		@Pc(1) var i: Int = 0
		while (i < Scene.interactiveObjectCount) {
			@Pc(6) val positionEntity: AbstractInteractiveEntity? = Scene.aClass3_Sub1_Sub3Array1!![i]
			Scene.removeInteractive(positionEntity, true)
			Scene.aClass3_Sub1_Sub3Array1!![i] = null
			i++
		}
		Scene.interactiveObjectCount = 0
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(IIII)Lclient!kda;")
	@JvmStatic
	fun getLargestPathingEntityOnTile(@OriginalArg(1) x: Int, @OriginalArg(2) level: Int, @OriginalArg(3) y: Int): PathingEntity? {
		@Pc(11) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![level]!![x]!![y]
		if (tile == null) {
			return null
		}
		@Pc(22) var pathingEntity: PathingEntity? = null
		@Pc(24) var bestArea: Int = -1
		@Pc(27) var positionEntityNode: InteractiveEntityContainer? = tile.interactives
		while (positionEntityNode != null) {
			@Pc(31) val positionEntity: AbstractInteractiveEntity? = positionEntityNode.interactiveObject
			if (positionEntity is PathingEntity) {
				@Pc(37) val pathingEntity2: PathingEntity = positionEntity as PathingEntity
				@Pc(47) val halfSize: Int = (pathingEntity2.getSize() - 1) * 256 + 252
				@Pc(55) val minX: Int = (pathingEntity2.x - halfSize) shr 9
				@Pc(63) val minY: Int = (pathingEntity2.z - halfSize) shr 9
				@Pc(70) val maxX: Int = (halfSize + pathingEntity2.x) shr 9
				@Pc(77) val maxY: Int = (pathingEntity2.z + halfSize) shr 9
				if (minX <= x && y >= minY && x <= maxX && y <= maxY) {
					@Pc(110) val area: Int = (maxY + 1 - y) * (-x + 1 + maxX)
					if (area > bestArea) {
						bestArea = area
						pathingEntity = pathingEntity2
					}
				}
			}
			positionEntityNode = positionEntityNode.child
		}
		return pathingEntity
	}

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(Lclient!ga;III)V")
	@JvmStatic
	fun shareLightWithNeighbourGroundDecor(@OriginalArg(0) entity: Entity?, @OriginalArg(1) level: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int) {
		@Pc(12) var tile: Tile?
		if (x < Scene.sceneWidth) {
			tile = Scene.aClass100ArrayArrayArray3!![level]!![x + 1]!![y]
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration!!.hasTileShadow()) {
				entity!!.shareLight(Scene.toolkit, 0, true, tile.groundDecoration, 0, Culling.fullTileDimension)
			}
		}
		if (y < Scene.sceneWidth) {
			tile = Scene.aClass100ArrayArrayArray3!![level]!![x]!![y + 1]
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration!!.hasTileShadow()) {
				entity!!.shareLight(Scene.toolkit, 0, true, tile.groundDecoration, Culling.fullTileDimension, 0)
			}
		}
		if (x < Scene.sceneWidth && y < Camera.sceneLength) {
			tile = Scene.aClass100ArrayArrayArray3!![level]!![x + 1]!![y + 1]
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration!!.hasTileShadow()) {
				entity!!.shareLight(Scene.toolkit, 0, true, tile.groundDecoration, Culling.fullTileDimension, Culling.fullTileDimension)
			}
		}
		if (x < Scene.sceneWidth && y > 0) {
			tile = Scene.aClass100ArrayArrayArray3!![level]!![x + 1]!![y - 1]
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration!!.hasTileShadow()) {
				entity!!.shareLight(Scene.toolkit, 0, true, tile.groundDecoration, -Culling.fullTileDimension, Culling.fullTileDimension)
			}
		}
	}

	@OriginalMember(owner = "client!qt", name = "a", descriptor = "(III)Lclient!oga;")
	@JvmStatic
	fun getTileInteractives(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int): InteractiveEntityContainer? {
		@Pc(7) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![level]!![x]!![z]
		return if (tile == null) null else tile.interactives
	}

	@OriginalMember(owner = "client!vb", name = "a", descriptor = "(IIII)I")
	@JvmStatic
	fun tileHeightForAbsolutePosition(@OriginalArg(0) absoluteX: Int, @OriginalArg(1) level: Int, @OriginalArg(3) absoluteY: Int): Int {
		if (Scene.aClass65Array1 == null) {
			return 0
		}
		@Pc(11) val tileX: Int = absoluteX shr 9
		@Pc(15) val tileY: Int = absoluteY shr 9
		if (tileX < 0 || tileY < 0 || tileX > MapRegion.sceneSizeX - 1 || MapRegion.sceneSizeY - 1 < tileY) {
			return 0
		}
		@Pc(43) var heightLevel: Int = level
		if (level < 3 && (SettingsBits.tileFunctionMasks!![1]!![tileX]!![tileY].toInt() and TileFlag.BRIDGE) != 0) {
			heightLevel = level + 1
		}
		return Scene.aClass65Array1!![heightLevel]!!.averageHeight(absoluteX, absoluteY)
	}

	@OriginalMember(owner = "client!ada", name = "P", descriptor = "I")
	@JvmField
	var renderThreadCount: Int = 1

	@OriginalMember(owner = "client!ada", name = "a", descriptor = "(IIILclient!aba;)V")
	@JvmStatic
	fun setGroundDecoration(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int, @OriginalArg(3) groundDecor: GroundDecor?) {
		@Pc(4) val tile: Tile? = Scene.getTile(level, x, z)
		if (tile == null) {
			return
		}
		tile.groundDecoration = groundDecor
		@Pc(16) val listIndex: Int = if (Scene.aClass65Array1 === Scene.aClass65Array3) 1 else 0
		if (groundDecor!!.isStationary()) {
			if (groundDecor.hasAlpha()) {
				groundDecor.nextEntity = Scene.aClass3_Sub1Array129!![listIndex]
				Scene.aClass3_Sub1Array129!![listIndex] = groundDecor
				return
			}
			groundDecor.nextEntity = Scene.stringStack!![listIndex]
			Scene.stringStack!![listIndex] = groundDecor
			Scene.staticSceneDirty = true
			return
		}
		groundDecor.nextEntity = Scene.aClass3_Sub1Array94!![listIndex]
		Scene.aClass3_Sub1Array94!![listIndex] = groundDecor
	}

	@OriginalMember(owner = "client!vga", name = "a", descriptor = "(III)Lclient!fha;")
	@JvmStatic
	fun removeGroundItem(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int): AbstractGroundItem? {
		@Pc(7) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![level]!![x]!![z]
		if (tile == null) {
			return null
		} else {
			@Pc(14) val groundItem: AbstractGroundItem? = tile.groundItem
			tile.groundItem = null
			Scene.removeFromEntityLists(groundItem)
			return groundItem
		}
	}

	@OriginalMember(owner = "client!cda", name = "a", descriptor = "(BI)V")
	@JvmStatic
	fun fillRoofStamps(@OriginalArg(0) value: Byte) {
		if (MapRegion.roofStamps == null) {
			MapRegion.roofStamps = Array<Array<ByteArray?>?>(4) { Array<ByteArray?>(MapRegion.sceneSizeX) { ByteArray(MapRegion.sceneSizeY) } }
		}
		@Pc(23) var i: Int = 0
		while (i < 4) {
			@Pc(27) var j: Int = 0
			while (j < MapRegion.sceneSizeX) {
				@Pc(31) var k: Int = 0
				while (k < MapRegion.sceneSizeY) {
					MapRegion.roofStamps[i][j][k] = value
					k++
				}
				j++
			}
			i++
		}
	}
}
