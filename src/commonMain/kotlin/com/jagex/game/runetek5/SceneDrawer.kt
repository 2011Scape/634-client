package com.jagex.game.runetek5

import com.jagex.game.runetek5.entity.EntityNodeList
import com.jagex.game.runetek5.sound.SceneCullingActive
import com.jagex.game.runetek5.statics.UnderwaterTileGrid
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.entity.PickableEntity
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.entity.GroundRenderTask
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager
import com.jagex.game.runetek5.scene.culling.Culling
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity
import com.jagex.game.runetek5.entity.BoundingBoxPolygon
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import withLock

object SceneDrawer {

	@OriginalMember(owner = "client!gj", name = "a", descriptor = "(IIII[[[B[I[I[I[I[IIBIIZZIIZ)V")
	@JvmStatic
	fun drawScene(@OriginalArg(0) cycle: Int, @OriginalArg(1) cameraX: Int, @OriginalArg(2) cameraY: Int, @OriginalArg(3) cameraZ: Int, @OriginalArg(4) settingBits: Array<Array<ByteArray?>?>?, @OriginalArg(5) visibleBelowDrawheight: IntArray?, @OriginalArg(6) roofMinX: IntArray?, @OriginalArg(7) roofMaxX: IntArray?, @OriginalArg(8) roofMaxZ: IntArray?, @OriginalArg(9) roofMinZ: IntArray?, @OriginalArg(10) lowestRenderedPlane: Int, @OriginalArg(11) roofPreference: Byte, @OriginalArg(12) playerTileX: Int, @OriginalArg(13) playerTileZ: Int, @OriginalArg(14) disableFlickeringEffects: Boolean, @OriginalArg(16) orthoZoom: Int, @OriginalArg(17) skipFlags: Int, @OriginalArg(18) markDirtyOnRender: Boolean) {
		SceneCullingActive.cullingActive = true
		SceneDrawer.copyLights = Scene.toolkit!!.getMaxLights() > 0
		SceneDrawer.calculateTilesOnScreen = true
		SceneDrawer.cameraGridPositionX = cameraX shr Scene.tileUnits
		Scene.cameraTileZ = cameraZ shr Scene.tileUnits
		WorldMapDisplay.sceneCameraX = cameraX
		SceneCameraZ.sceneCameraZ = cameraZ
		Culling.cameraY = cameraY
		SceneDrawer.visibleToXnegative = SceneDrawer.cameraGridPositionX - SceneVisibleTiles.visibleTiles
		if (SceneDrawer.visibleToXnegative < 0) {
			SceneDrawer.visibleToInvertedXnegative = -SceneDrawer.visibleToXnegative
			SceneDrawer.visibleToXnegative = 0
		} else {
			SceneDrawer.visibleToInvertedXnegative = 0
		}
		SceneDrawer.visibleToYnegative = Scene.cameraTileZ - SceneVisibleTiles.visibleTiles
		if (SceneDrawer.visibleToYnegative < 0) {
			SceneDrawer.visibleToInvertedYnegative = -SceneDrawer.visibleToYnegative
			SceneDrawer.visibleToYnegative = 0
		} else {
			SceneDrawer.visibleToInvertedYnegative = 0
		}
		SceneDrawer.visibleToXpositive = SceneDrawer.cameraGridPositionX + SceneVisibleTiles.visibleTiles
		if (SceneDrawer.visibleToXpositive > Scene.sceneWidth) {
			SceneDrawer.visibleToXpositive = Scene.sceneWidth
		}
		SceneDrawer.visibleToYpositive = Scene.cameraTileZ + SceneVisibleTiles.visibleTiles
		if (SceneDrawer.visibleToYpositive > Camera.sceneLength) {
			SceneDrawer.visibleToYpositive = Camera.sceneLength
		}
		@Pc(72) val unadjustedAdjacentTilesOnScreen: Array<BooleanArray?>? = Scene.tileInFrustum
		@Pc(74) val unadjustedTilesOnScreen: Array<BooleanArray?>? = Scene.tileOnScreen
		@Pc(78) var local78: Int
		@Pc(81) var local81: Int
		@Pc(83) var local83: Int
		if (SceneDrawer.calculateTilesOnScreen) {
			var gridX: Int = 0
			while (gridX < SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 2) {
				var prevTileClip: Int = 0
				var tileClip: Int = 0
				@Pc(85) var i: Int = 0
				while (i < SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 2) {
					if (i > 1) {
						Scene.tileVisibility!![i - 2] = prevTileClip
					}
					prevTileClip = tileClip
					@Pc(103) val sceneX: Int = SceneDrawer.cameraGridPositionX + gridX - SceneVisibleTiles.visibleTiles
					@Pc(109) val sceneZ: Int = Scene.cameraTileZ + i - SceneVisibleTiles.visibleTiles
					@Pc(123) var local123: Int
					if (sceneX >= 0 && sceneZ >= 0 && sceneX < Scene.sceneWidth && sceneZ < Camera.sceneLength) {
						val worldX: Int = sceneX shl Scene.tileUnits
						@Pc(127) val worldZ: Int = sceneZ shl Scene.tileUnits
						@Pc(144) val lowY: Int = Scene.aClass65Array2!![Scene.aClass65Array2!!.size - 1]!!.getHeight(sceneZ, sceneX) - (0x3E8 shl (Scene.tileUnits - 7))
						@Pc(166) val highY: Int = if (Scene.aClass65Array3 == null) Scene.aClass65Array2!![0]!!.getHeight(sceneZ, sceneX) + Culling.fullTileDimension else Scene.aClass65Array3!![0]!!.getHeight(sceneZ, sceneX) + Culling.fullTileDimension
						tileClip = if (orthoZoom >= 0) Scene.toolkit!!.r(worldX, lowY, worldZ, worldX, highY, worldZ, orthoZoom) else Scene.toolkit!!.JA(worldX, lowY, worldZ, worldX, highY, worldZ)
						Scene.tileOnScreen!![gridX]!![i] = tileClip == 0
					} else {
						tileClip = -1
						Scene.tileOnScreen!![gridX]!![i] = false
					}
					if (gridX > 0 && i > 0) {
						val combinedClip: Int = Scene.tileVisibility!![i - 1] and Scene.tileVisibility!![i] and prevTileClip and tileClip
						Scene.tileInFrustum!![gridX - 1]!![i - 1] = combinedClip == 0
					}
					i++
				}
				Scene.tileVisibility!![SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles] = prevTileClip
				Scene.tileVisibility!![SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 1] = tileClip
				gridX++
			}
			if (orthoZoom >= 0) {
				SceneCullingActive.cullingActive = false
			} else {
				SceneDrawer.visibleBelowDrawheight = visibleBelowDrawheight
				SceneDrawer.roofMinX = roofMinX
				SceneDrawer.roofMaxX = roofMaxX
				SceneDrawer.roofMaxZ = roofMaxZ
				SceneDrawer.roofMinZ = roofMinZ
				Culling.calculateOcclusion(lowestRenderedPlane, Scene.toolkit)
			}
		} else {
			if (SceneDrawer.allTilesVisibleFallback == null) {
				SceneDrawer.allTilesVisibleFallback = Array<BooleanArray?>(Scene.sceneWidth + Scene.sceneWidth + 1) { BooleanArray(Camera.sceneLength + Scene.sceneWidth + 1) }
			}
			var gridX: Int = 0
			while (gridX < SceneDrawer.allTilesVisibleFallback!!.size) {
				var gridZ: Int = 0
				while (gridZ < SceneDrawer.allTilesVisibleFallback!![0]!!.size) {
					SceneDrawer.allTilesVisibleFallback!![gridX]!![gridZ] = true
					gridZ++
				}
				gridX++
			}
			Scene.tileOnScreen = SceneDrawer.allTilesVisibleFallback
			Scene.tileInFrustum = SceneDrawer.allTilesVisibleFallback
			SceneDrawer.visibleToXnegative = 0
			SceneDrawer.visibleToYnegative = 0
			SceneDrawer.visibleToXpositive = Scene.sceneWidth
			SceneDrawer.visibleToYpositive = Camera.sceneLength
			SceneCullingActive.cullingActive = false
		}
		ParticleSystemManager.processParticleCycle(Scene.toolkit)
		if (!PickList.aClass182_1!!.retained) {
			@Pc(349) val linkedList: EntityNodeList = PickList.aClass182_1!!.entities
			@Pc(354) var pickableEntity: PickableEntity? = linkedList.getFirst() as PickableEntity?
			while (pickableEntity != null) {
				pickableEntity.unlinkNode()
				PickableEntity.recycle(pickableEntity)
				pickableEntity = linkedList.getNext() as PickableEntity?
			}
		}
		if (SceneDrawer.copyLights) {
			var lightIndex: Int = 0
			while (lightIndex < Scene.lightSlotCount) {
				Scene.aClass300Array1!![lightIndex]!!.applyEffect(disableFlickeringEffects, cycle)
				lightIndex++
			}
		}
		if (SceneDrawer.multithreadedRendering) {
			SceneDrawer.projectionParams = Scene.toolkit!!.Y()
			Scene.toolkit!!.K(SceneDrawer.clipBounds)
			val sliceWidth: Int = (SceneDrawer.clipBounds[2] - SceneDrawer.clipBounds[0]) / Scene.renderThreadCount
			var threadIndex: Int = 0
			while (threadIndex < Scene.renderThreadCount - 1) {
				ScriptRunner.threadSplitX!![threadIndex] = sliceWidth * (threadIndex + 1) + SceneDrawer.threadSplitOffsets!![threadIndex]
				threadIndex++
			}
			var workerIndex: Int = 0
			while (workerIndex < RenderWorker.aClass366Array1!!.size) {
				RenderWorker.aClass366Array1!![workerIndex]!!.resume()
				workerIndex++
			}
		}
		if (UnderwaterTileGrid.aClass100ArrayArrayArray2 != null) {
			if (SceneDrawer.multithreadedRendering) {
				SceneDrawer.assignRenderQueues(0)
			}
			Scene.setUnderwater(true)
			Scene.toolkit!!.ra(-1, 1583160, 40, 127)
			SceneDrawer.drawTiles(true, settingBits, lowestRenderedPlane, roofPreference, orthoZoom, skipFlags, markDirtyOnRender)
			if (SceneDrawer.multithreadedRendering) {
				RenderWorker.waitForWorkers()
			}
			Scene.toolkit!!.pa()
			Scene.setUnderwater(false)
		}
		SceneDrawer.drawTiles(false, settingBits, lowestRenderedPlane, roofPreference, orthoZoom, skipFlags, markDirtyOnRender)
		if (SceneDrawer.multithreadedRendering) {
			var level: Int = 0
			while (level < Scene.length) {
				Scene.aBooleanArrayArrayArray1!![level] = Scene.levelTileVisibility!![level]
				level++
			}
			SceneDrawer.assignRenderQueues(0)
			var workerIndex: Int = 0
			while (workerIndex < RenderWorker.aClass366Array1!!.size) {
				RenderWorker.aClass366Array1!![workerIndex]!!.resume()
				workerIndex++
			}
		}
		if (SceneDrawer.multithreadedRendering) {
			RenderWorker.waitForWorkers()
			var level: Int = 0
			while (level < Scene.length) {
				Scene.levelTileVisibility!![level] = Scene.aBooleanArrayArrayArray1!![level]
				level++
			}
			if (SceneDrawer.renderThreadCount == 2) {
				@Pc(543) var local543: Int
				if (SceneDrawer.workerCompletionTimes[0] < SceneDrawer.workerCompletionTimes[1]) {
					if (ScriptRunner.threadSplitX!![0] + SceneDrawer.threadSplitOffsets!![0] > SceneDrawer.clipBounds[0]) {
						val previousOffset: Int = SceneDrawer.threadSplitOffsets!![0]++
					}
				} else if (SceneDrawer.workerCompletionTimes[0] > SceneDrawer.workerCompletionTimes[1] && ScriptRunner.threadSplitX!![0] + SceneDrawer.threadSplitOffsets!![0] < SceneDrawer.clipBounds[2]) {
					val previousOffset: Int = SceneDrawer.threadSplitOffsets!![0]--
				}
			}
		}
		if (!SceneDrawer.calculateTilesOnScreen) {
			Scene.tileInFrustum = unadjustedAdjacentTilesOnScreen
			Scene.tileOnScreen = unadjustedTilesOnScreen
		}
		Culling.drawOcclusionDebug()
	}
	@OriginalMember(owner = "client!am", name = "a", descriptor = "(Lclient!ga;ZZ)V")
	@JvmStatic
	fun submitEntity(@OriginalArg(0) entity: Entity?, @OriginalArg(2) markDirtyOnRender: Boolean) {
		entity!!.markDirtyOnRender = markDirtyOnRender
		if (SceneDrawer.multithreadedRendering) {
			Scene.aClass59Array1!![Scene.aClass59Array1!!.size - 1]!!.addEntity(entity)
		} else {
			SceneDrawer.renderSceneEntity(entity, SceneDrawer.aClass2_Sub1Array4)
		}
	}

	@OriginalMember(owner = "client!be", name = "u", descriptor = "I")
	@JvmField
	var visibleToYnegative: Int = 0

	@OriginalMember(owner = "client!jca", name = "m", descriptor = "[I")
	@JvmField
	var projectionParams: IntArray? = IntArray(4)

	@OriginalMember(owner = "client!ida", name = "c", descriptor = "I")
	@JvmField
	var visibleToXnegative: Int = 0

	@OriginalMember(owner = "client!ht", name = "a", descriptor = "I")
	@JvmField
	var visibleToXpositive: Int = 0
	@OriginalMember(owner = "client!ta", name = "o", descriptor = "I")
	@JvmField
	var visibleToInvertedYnegative: Int = 0

	@OriginalMember(owner = "client!rga", name = "G", descriptor = "I")
	@JvmField
	var visibleToYpositive: Int = 0

	@OriginalMember(owner = "client!um", name = "r", descriptor = "[[Z")
	@JvmField
	var allTilesVisibleFallback: Array<BooleanArray?>? = null

	@OriginalMember(owner = "client!gh", name = "a", descriptor = "(Z[[[BIBIIZ)V")
	@JvmStatic
	fun drawTiles(@OriginalArg(0) underwater: Boolean, @OriginalArg(1) settingBits: Array<Array<ByteArray?>?>?, @OriginalArg(2) lowestRenderedPlane: Int, @OriginalArg(3) roofPreference: Byte, @OriginalArg(4) orthoZoom: Int, @OriginalArg(5) skipFlags: Int, @OriginalArg(6) markDirtyOnRender: Boolean) {
		@Pc(5) val listIndex: Int = if (underwater) 1 else 0
		Scene.onscreenOpaqueCount = 0
		Scene.transparentEntityCount = 0
		Culling.cullingCycle++
		@Pc(21) var entity: Entity?
		if ((skipFlags and 0x2) == 0) {
			entity = Scene.stringStack!![listIndex]
			while (entity != null) {
				if (!SceneDrawer.isEntityCulled(entity, underwater, settingBits, lowestRenderedPlane, roofPreference)) {
					Entity.computeScreenPosition(entity)
					if (entity.screenDepth != -1) {
						Scene.aClass3_Sub1Array143!![Scene.onscreenOpaqueCount++] = entity
					}
				}
				entity = entity.nextEntity
			}
		}
		@Pc(137) var length: Int
		if ((skipFlags and 0x1) == 0) {
			entity = Scene.aClass3_Sub1Array129!![listIndex]
			while (entity != null) {
				if (!SceneDrawer.isEntityCulled(entity, underwater, settingBits, lowestRenderedPlane, roofPreference)) {
					Entity.computeScreenPosition(entity)
					if (entity.screenDepth != -1) {
						Scene.aClass3_Sub1Array145!![Scene.transparentEntityCount++] = entity
					}
				}
				entity = entity.nextEntity
			}
			@Pc(87) var entity2: Entity? = Scene.aClass3_Sub1Array94!![listIndex]
			while (entity2 != null) {
				if (!SceneDrawer.isEntityCulled(entity2, underwater, settingBits, lowestRenderedPlane, roofPreference)) {
					if (entity2.hasAlpha()) {
						Entity.computeScreenPosition(entity2)
						if (entity2.screenDepth != -1) {
							Scene.aClass3_Sub1Array145!![Scene.transparentEntityCount++] = entity2
						}
					} else {
						Entity.computeScreenPosition(entity2)
						if (entity2.screenDepth != -1) {
							Scene.aClass3_Sub1Array143!![Scene.onscreenOpaqueCount++] = entity2
						}
					}
				}
				entity2 = entity2.nextEntity
			}
			if (!underwater) {
				length = 0
				while (length < Scene.interactiveObjectCount) {
					if (!SceneDrawer.isEntityCulled(Scene.aClass3_Sub1_Sub3Array1!![length], underwater, settingBits, lowestRenderedPlane, roofPreference)) {
						Entity.computeScreenPosition(Scene.aClass3_Sub1_Sub3Array1!![length])
						if (Scene.aClass3_Sub1_Sub3Array1!![length]!!.screenDepth != -1) {
							if (Scene.aClass3_Sub1_Sub3Array1!![length]!!.hasAlpha()) {
								Scene.aClass3_Sub1Array145!![Scene.transparentEntityCount++] = Scene.aClass3_Sub1_Sub3Array1!![length]
							} else {
								Scene.aClass3_Sub1Array143!![Scene.onscreenOpaqueCount++] = Scene.aClass3_Sub1_Sub3Array1!![length]
							}
						}
					}
					length++
				}
			}
		}
		@Pc(198) var local198: Int
		if (Scene.onscreenOpaqueCount > 0) {
			SceneDrawer.sortEntitiesByDepth(Scene.aClass3_Sub1Array143, 0, Scene.onscreenOpaqueCount - 1)
			var entityIndex: Int = 0
			while (entityIndex < Scene.onscreenOpaqueCount) {
				SceneDrawer.submitEntity(Scene.aClass3_Sub1Array143!![entityIndex], markDirtyOnRender)
				entityIndex++
			}
		}
		if (SceneDrawer.copyLights) {
			Scene.toolkit!!.copyLights(0, null)
		}
		if ((skipFlags and 0x2) == 0) {
			var level: Int = ScriptRunner.minimumDrawLevel
			while (level < Scene.length) {
				@Pc(271) var visibility: Array<BooleanArray?>?
				@Pc(281) var local281: Int
				@Pc(288) var tileX: Int
				@Pc(290) var z: Int
				@Pc(230) var endX: Int
				if (level < lowestRenderedPlane || settingBits == null) {
					endX = Scene.tileInFrustum!!.size
					if (SceneDrawer.visibleToXnegative + Scene.tileInFrustum!!.size > Scene.sceneWidth) {
						endX -= SceneDrawer.visibleToXnegative + Scene.tileInFrustum!!.size - Scene.sceneWidth
					}
					length = Scene.tileInFrustum!![0]!!.size
					if (SceneDrawer.visibleToYnegative + Scene.tileInFrustum!![0]!!.size > Camera.sceneLength) {
						length -= SceneDrawer.visibleToYnegative + Scene.tileInFrustum!![0]!!.size - Camera.sceneLength
					}
					visibility = Scene.tileOnScreen
					if (SceneDrawer.calculateTilesOnScreen) {
						if (SceneDrawer.multithreadedRendering) {
							visibility = Scene.levelTileVisibility!![level]
						}
						var gridX: Int = SceneDrawer.visibleToInvertedXnegative
						while (gridX < endX) {
							tileX = gridX + SceneDrawer.visibleToXnegative - SceneDrawer.visibleToInvertedXnegative
							z = SceneDrawer.visibleToInvertedYnegative
							while (z < length) {
								if (Scene.tileInFrustum!![gridX]!![z] && !Culling.isTileHidden(level, tileX, z + SceneDrawer.visibleToYnegative - SceneDrawer.visibleToInvertedYnegative)) {
									visibility!![gridX]!![z] = true
								} else {
									visibility!![gridX]!![z] = false
								}
								z++
							}
							gridX++
						}
					}
					if (SceneDrawer.multithreadedRendering) {
						if (orthoZoom >= 0) {
							Scene.aClass65Array1!![level]!!.renderFloor(0, 0, 0, null, false, orthoZoom, skipFlags)
						} else {
							Scene.aClass65Array1!![level]!!.renderTiles(0, 0, 0, null, false, skipFlags)
						}
						var threadIndex: Int = 0
						while (threadIndex < SceneDrawer.renderThreadCount) {
							Scene.aClass59Array1!![threadIndex]!!.addGroundTask(GroundRenderTask(level + 1))
							threadIndex++
						}
					} else if (orthoZoom >= 0) {
						Scene.aClass65Array1!![level]!!.renderFloor(SceneDrawer.cameraGridPositionX, Scene.cameraTileZ, SceneVisibleTiles.visibleTiles, Scene.tileOnScreen, true, orthoZoom, skipFlags)
					} else {
						Scene.aClass65Array1!![level]!!.renderTiles(SceneDrawer.cameraGridPositionX, Scene.cameraTileZ, SceneVisibleTiles.visibleTiles, Scene.tileOnScreen, true, skipFlags)
					}
				} else {
					endX = Scene.tileInFrustum!!.size
					if (SceneDrawer.visibleToXnegative + Scene.tileInFrustum!!.size > Scene.sceneWidth) {
						endX -= SceneDrawer.visibleToXnegative + Scene.tileInFrustum!!.size - Scene.sceneWidth
					}
					length = Scene.tileInFrustum!![0]!!.size
					if (SceneDrawer.visibleToYnegative + Scene.tileInFrustum!![0]!!.size > Camera.sceneLength) {
						length -= SceneDrawer.visibleToYnegative + Scene.tileInFrustum!![0]!!.size - Camera.sceneLength
					}
					visibility = Scene.tileOnScreen
					if (SceneDrawer.calculateTilesOnScreen) {
						if (SceneDrawer.multithreadedRendering) {
							visibility = Scene.levelTileVisibility!![level]
						}
						var gridX: Int = SceneDrawer.visibleToInvertedXnegative
						while (gridX < endX) {
							tileX = gridX + SceneDrawer.visibleToXnegative - SceneDrawer.visibleToInvertedXnegative
							z = SceneDrawer.visibleToInvertedYnegative
							while (z < length) {
								visibility!![gridX]!![z] = false
								if (Scene.tileInFrustum!![gridX]!![z]) {
									@Pc(309) val tileZ: Int = z + SceneDrawer.visibleToYnegative - SceneDrawer.visibleToInvertedYnegative
									@Pc(311) var i: Int = level
									while (i >= 0) {
										if (Scene.aClass100ArrayArrayArray3!![i]!![tileX]!![tileZ] != null && Scene.aClass100ArrayArrayArray3!![i]!![tileX]!![tileZ]!!.plane.toInt() == level) {
											if ((i < lowestRenderedPlane || settingBits[i]!![tileX]!![tileZ] != roofPreference) && !Culling.isTileHidden(level, tileX, tileZ)) {
												visibility[gridX]!![z] = true
												break
											}
											visibility[gridX]!![z] = false
											break
										}
										i--
									}
								}
								z++
							}
							gridX++
						}
					}
					if (SceneDrawer.multithreadedRendering) {
						if (orthoZoom >= 0) {
							Scene.aClass65Array1!![level]!!.renderFloor(0, 0, 0, null, false, orthoZoom, skipFlags)
						} else {
							Scene.aClass65Array1!![level]!!.renderTiles(0, 0, 0, null, false, skipFlags)
						}
						var threadIndex: Int = 0
						while (threadIndex < SceneDrawer.renderThreadCount) {
							Scene.aClass59Array1!![threadIndex]!!.addGroundTask(GroundRenderTask(level + 1))
							threadIndex++
						}
					} else if (orthoZoom >= 0) {
						Scene.aClass65Array1!![level]!!.renderFloor(SceneDrawer.cameraGridPositionX, Scene.cameraTileZ, SceneVisibleTiles.visibleTiles, Scene.tileOnScreen, false, orthoZoom, skipFlags)
					} else {
						Scene.aClass65Array1!![level]!!.renderTiles(SceneDrawer.cameraGridPositionX, Scene.cameraTileZ, SceneVisibleTiles.visibleTiles, Scene.tileOnScreen, false, skipFlags)
					}
				}
				level++
			}
		}
		if (Scene.transparentEntityCount > 0) {
			Entity.quicksortAbove(Scene.aClass3_Sub1Array145, 0, Scene.transparentEntityCount - 1)
			var entityIndex: Int = 0
			while (entityIndex < Scene.transparentEntityCount) {
				SceneDrawer.submitEntity(Scene.aClass3_Sub1Array145!![entityIndex], markDirtyOnRender)
				entityIndex++
			}
		}
	}

	@OriginalMember(owner = "client!eea", name = "A", descriptor = "I")
	@JvmField
	var cameraGridPositionX: Int = 0

	@OriginalMember(owner = "client!sv", name = "V", descriptor = "[I")
	@JvmField
	var roofMaxX: IntArray? = null
	@OriginalMember(owner = "client!pj", name = "T", descriptor = "Z")
	@JvmField
	var calculateTilesOnScreen: Boolean = false

	@OriginalMember(owner = "client!hn", name = "N", descriptor = "Z")
	@JvmField
	var multithreadedRendering: Boolean = false

	@OriginalMember(owner = "client!ff", name = "zd", descriptor = "I")
	@JvmField
	var renderThreadCount: Int = 0

	@OriginalMember(owner = "client!pr", name = "y", descriptor = "I")
	@JvmField
	var timesSceneRendered: Int = 0

	@OriginalMember(owner = "client!jd", name = "w", descriptor = "I")
	@JvmField
	var visibleToInvertedXnegative: Int = 0

	@OriginalMember(owner = "client!ef", name = "d", descriptor = "[I")
	@JvmField
	var threadSplitOffsets: IntArray? = null

	@OriginalMember(owner = "client!bba", name = "g", descriptor = "(I)V")
	@JvmStatic
	fun assignRenderQueues(@OriginalArg(0) stage: Int) {
		if (stage == 0) {
			if (SceneDrawer.renderThreadCount == 2) {
				RenderWorker.aClass366Array1!![0]!!.setQueue(Scene.aClass59Array1!![0])
				RenderWorker.aClass366Array1!![1]!!.setQueue(Scene.aClass59Array1!![1])
			} else if (SceneDrawer.renderThreadCount == 3) {
				RenderWorker.aClass366Array1!![0]!!.setQueue(Scene.aClass59Array1!![0])
				RenderWorker.aClass366Array1!![1]!!.setQueue(Scene.aClass59Array1!![1])
				RenderWorker.aClass366Array1!![2]!!.setQueue(Scene.aClass59Array1!![2])
			} else {
				RenderWorker.aClass366Array1!![0]!!.setQueue(Scene.aClass59Array1!![0])
				RenderWorker.aClass366Array1!![1]!!.setQueue(Scene.aClass59Array1!![1])
				RenderWorker.aClass366Array1!![2]!!.setQueue(Scene.aClass59Array1!![2])
				RenderWorker.aClass366Array1!![3]!!.setQueue(Scene.aClass59Array1!![3])
			}
		} else if (stage == 1) {
			if (SceneDrawer.renderThreadCount == 2) {
				RenderWorker.aClass366Array1!![0]!!.setQueue(Scene.aClass59Array1!![2])
			} else if (SceneDrawer.renderThreadCount == 3) {
				RenderWorker.aClass366Array1!![0]!!.setQueue(Scene.aClass59Array1!![3])
				RenderWorker.aClass366Array1!![1]!!.setQueue(Scene.aClass59Array1!![4])
			} else {
				RenderWorker.aClass366Array1!![0]!!.setQueue(Scene.aClass59Array1!![4])
				RenderWorker.aClass366Array1!![1]!!.setQueue(Scene.aClass59Array1!![5])
				RenderWorker.aClass366Array1!![2]!!.setQueue(Scene.aClass59Array1!![6])
			}
		} else if (stage == 2) {
			if (SceneDrawer.renderThreadCount == 2) {
				RenderWorker.aClass366Array1!![0]!!.setQueue(Scene.aClass59Array1!![3])
				return
			}
			if (SceneDrawer.renderThreadCount == 3) {
				RenderWorker.aClass366Array1!![0]!!.setQueue(Scene.aClass59Array1!![5])
				return
			}
			RenderWorker.aClass366Array1!![0]!!.setQueue(Scene.aClass59Array1!![7])
		}
	}

	@OriginalMember(owner = "client!bia", name = "b", descriptor = "[J")
	@JvmField
	val workerCompletionTimes: LongArray = LongArray(10)

	@OriginalMember(owner = "client!kq", name = "R", descriptor = "[I")
	@JvmField
	val clipBounds: IntArray = IntArray(4)

	@OriginalMember(owner = "client!kv", name = "m", descriptor = "[I")
	@JvmField
	var roofMaxZ: IntArray? = null

	@OriginalMember(owner = "client!ke", name = "ne", descriptor = "[I")
	@JvmField
	var visibleBelowDrawheight: IntArray? = null

	@OriginalMember(owner = "client!kda", name = "mb", descriptor = "Z")
	@JvmField
	var copyLights: Boolean = false
	@OriginalMember(owner = "client!hf", name = "a", descriptor = "(Lclient!ga;Z[[[BIB)Z")
	@JvmStatic
	fun isEntityCulled(@OriginalArg(0) entity: Entity?, @OriginalArg(1) underwater: Boolean, @OriginalArg(2) settingBits: Array<Array<ByteArray?>?>?, @OriginalArg(3) lowestRenderedPlane: Int, @OriginalArg(4) roofPreference: Byte): Boolean {
		if (!SceneDrawer.calculateTilesOnScreen) {
			return false
		}
		@Pc(8) var minX: Int = entity!!.x shr Scene.tileUnits
		@Pc(10) var maxX: Int = minX
		@Pc(15) var minZ: Int = entity.z shr Scene.tileUnits
		@Pc(17) var maxZ: Int = minZ
		if (entity is AbstractInteractiveEntity) {
			maxX = (entity as AbstractInteractiveEntity).endX.toInt()
			maxZ = (entity as AbstractInteractiveEntity).endZ.toInt()
			minX = (entity as AbstractInteractiveEntity).startX.toInt()
			minZ = (entity as AbstractInteractiveEntity).startZ.toInt()
		}
		@Pc(38) var i: Int = minX
		while (i <= maxX) {
			@Pc(41) var j: Int = minZ
			while (j <= maxZ) {
				if (entity.virtualLevel.toInt() < Scene.length && i >= SceneDrawer.visibleToXnegative && i < SceneDrawer.visibleToXpositive && j >= SceneDrawer.visibleToYnegative && j < SceneDrawer.visibleToYpositive) {
					if ((settingBits == null || entity.level.toInt() < lowestRenderedPlane || settingBits[entity.level.toInt()]!![i]!![j] != roofPreference) && entity.canRender() && !entity.isOccluded()) {
						return false
					}
					if (!underwater && i >= SceneDrawer.cameraGridPositionX - 16 && i <= SceneDrawer.cameraGridPositionX + 16 && j >= Scene.cameraTileZ - 16 && j <= Scene.cameraTileZ + 16) {
						if (SceneDrawer.multithreadedRendering) {
							Scene.aClass59Array1!![SceneDrawer.particleWorkerIndex++]!!.addParticleEntity(entity)
							SceneDrawer.particleWorkerIndex %= Scene.renderThreadCount
						} else {
							entity.particleCycle(Scene.toolkit)
						}
					}
				}
				j++
			}
			i++
		}
		return true
	}

	@OriginalMember(owner = "client!oba", name = "b", descriptor = "I")
	@JvmField
	var particleWorkerIndex: Int = 0

	@OriginalMember(owner = "client!iea", name = "a", descriptor = "(Lclient!ga;[Lclient!ab;)V")
	@JvmStatic
	fun renderSceneEntity(@OriginalArg(0) entity: Entity?, @OriginalArg(1) pointLights: Array<PointLight?>?) {
		@Pc(6) var local6: Int
		if (SceneDrawer.copyLights) {
			val lightCount: Int = entity!!.findLights(pointLights)
			Scene.toolkit!!.copyLights(lightCount, pointLights)
		}
		if (Scene.aClass65Array3 === Scene.aClass65Array1) {
			@Pc(28) val tileZ: Int
			val tileX: Int
			if (entity is AbstractInteractiveEntity) {
				tileX = (entity as AbstractInteractiveEntity).startX.toInt()
				tileZ = (entity as AbstractInteractiveEntity).startZ.toInt()
			} else {
				tileX = entity!!.x shr Scene.tileUnits
				tileZ = entity.z shr Scene.tileUnits
			}
			Scene.toolkit!!.EA(Scene.aClass65Array2!![0]!!.averageHeight(entity.x, entity.z), Scene.getWaterColor(tileX, tileZ), Scene.getWaterScale(tileX, tileZ), Scene.getWaterIntensity(tileX, tileZ))
		}
		@Pc(64) val pickableEntity: PickableEntity? = entity!!.render(Scene.toolkit)
		if (pickableEntity == null) {
			return
		}
		if (entity.markDirtyOnRender) {
			@Pc(72) val pickingCylinders: Array<BoundingBoxPolygon?>? = pickableEntity.boundingBoxPolygons
			@Pc(74) var i: Int = 0
			while (i < pickingCylinders!!.size) {
				@Pc(79) val pickingCylinder: BoundingBoxPolygon? = pickingCylinders[i]
				if (pickingCylinder!!.positionCalculated) {
					OrthoMode.markOrthoTilesDirty(pickingCylinder.left - pickingCylinder.size, pickingCylinder.right + pickingCylinder.size, pickingCylinder.bottom - pickingCylinder.size, pickingCylinder.top + pickingCylinder.size)
				}
				i++
			}
		}
		if (pickableEntity.interactive) {
			pickableEntity.entity = entity
			if (SceneDrawer.multithreadedRendering) {
				@Pc(119) val pickList: PickList? = PickList.aClass182_1
				withLock(PickList.aClass182_1!!) {
					PickList.aClass182_1!!.add(pickableEntity)
					return
				}
			}
			PickList.aClass182_1!!.add(pickableEntity)
			return
		}
		PickableEntity.recycle(pickableEntity)
	}

	@OriginalMember(owner = "client!mv", name = "b", descriptor = "[Lclient!ab;")
	@JvmField
	val aClass2_Sub1Array4: Array<PointLight?> = arrayOfNulls<PointLight>(8)

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "([Lclient!ga;II)V")
	@JvmStatic
	fun sortEntitiesByDepth(@OriginalArg(0) entitys: Array<Entity?>?, @OriginalArg(1) start: Int, @OriginalArg(2) end: Int) {
		if (start >= end) {
			return
		}
		@Pc(8) val pivotIndex: Int = (start + end) / 2
		@Pc(10) var storeIndex: Int = start
		@Pc(14) val entity: Entity? = entitys!![pivotIndex]
		entitys[pivotIndex] = entitys[end]
		entitys[end] = entity
		@Pc(27) val screenDepth: Int = entity!!.screenDepth
		@Pc(29) var i: Int = start
		while (i < end) {
			if (entitys[i]!!.screenDepth < screenDepth + (i and 0x1)) {
				@Pc(44) val entity2: Entity? = entitys[i]
				entitys[i] = entitys[storeIndex]
				entitys[storeIndex++] = entity2
			}
			i++
		}
		entitys[end] = entitys[storeIndex]
		entitys[storeIndex] = entity
		sortEntitiesByDepth(entitys, start, storeIndex - 1)
		sortEntitiesByDepth(entitys, storeIndex + 1, end)
	}
	@OriginalMember(owner = "client!tm", name = "c", descriptor = "[I")
	@JvmField
	var roofMinX: IntArray? = null

	@OriginalMember(owner = "client!client", name = "nb", descriptor = "[I")
	@JvmField
	var roofMinZ: IntArray? = null
}
