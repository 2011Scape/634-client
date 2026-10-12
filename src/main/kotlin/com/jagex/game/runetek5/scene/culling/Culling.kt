package com.jagex.game.runetek5.scene.culling

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.Sprites
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.tile.Tile
import com.jagex.game.runetek5.sound.SceneCullingActive
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.SceneCameraZ
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.SceneVisibleTiles
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.config.meltype.OccludedWallCount
import com.jagex.game.runetek5.entity.Wall
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.ScriptRunner
import com.jagex.game.runetek5.W2DebugMode
import com.jagex.game.runetek5.graphics.sprite.NativeSprite

object Culling {
	@OriginalMember(owner = "client!wf", name = "d", descriptor = "Z")
	@JvmField
	var wallCullingEnabled: Boolean = false

	@OriginalMember(owner = "client!vu", name = "a", descriptor = "(IIIIIII)V")
	@JvmStatic
	fun addWallCulling(@OriginalArg(0) x: Int, @OriginalArg(1) cullXZ: Int, @OriginalArg(2) cullY: Int, @OriginalArg(3) z: Int, @OriginalArg(5) level: Int, @OriginalArg(6) displacement: Int) {
		if (displacement != 8 && displacement != 16) {
			@Pc(17) var tile: Tile? = Scene.aClass100ArrayArrayArray3!![level]!![x]!![z]
			if (tile == null) {
				tile = Tile(level)
			}
			if (displacement == 1) {
				tile.cullOffsetX = cullXZ.toShort()
				tile.cullHeightX = cullY.toShort()
			} else if (displacement == 2) {
				tile.cullHeightZ = cullY.toShort()
				tile.cullOffsetZ = cullXZ.toShort()
			}
			if (Culling.tileCullingClustersBuilt) {
				Culling.rebuildTileCullingClusters()
			}
			return
		}
		@Pc(67) var startX: Int
		@Pc(72) var endX: Int
		@Pc(76) var startZ: Int
		@Pc(80) var endZ: Int
		@Pc(90) var startHeight: Int
		@Pc(100) var endHeight: Int
		if (displacement != 8) {
			startX = Culling.fullTileDimension + (x shl Scene.tileUnits)
			endX = startX - Culling.fullTileDimension
			startZ = z shl Scene.tileUnits
			endZ = startZ + Culling.fullTileDimension
			startHeight = Scene.aClass65Array2!![level]!!.getHeight(z, x + 1)
			endHeight = Scene.aClass65Array2!![level]!!.getHeight(z + 1, x)
			Culling.aClass82Array4!![Culling.dynamicCullingClusterCount++] = CullingCluster(displacement, level, startX, endX, endX, startX, startHeight, endHeight, endHeight - cullY, startHeight - cullY, startZ, endZ, endZ, startZ)
			return
		}
		startX = x shl Scene.tileUnits
		endX = Culling.fullTileDimension + startX
		startZ = z shl Scene.tileUnits
		endZ = Culling.fullTileDimension + startZ
		startHeight = Scene.aClass65Array2!![level]!!.getHeight(z, x)
		endHeight = Scene.aClass65Array2!![level]!!.getHeight(z + 1, x - -1)
		Culling.aClass82Array4!![Culling.dynamicCullingClusterCount++] = CullingCluster(displacement, level, startX, endX, endX, startX, startHeight, endHeight, endHeight - cullY, -cullY + startHeight, startZ, endZ, endZ, startZ)
	}

	@OriginalMember(owner = "client!vo", name = "a", descriptor = "(IIIIIBI)Z")
	@JvmStatic
	fun hideTile(@OriginalArg(0) level: Int, @OriginalArg(1) endZ: Int, @OriginalArg(2) startZ: Int, @OriginalArg(3) minY: Int, @OriginalArg(4) startX: Int, @OriginalArg(6) endX: Int): Boolean {
		if (!Culling.cullingEnabled || !SceneCullingActive.cullingActive) {
			return false
		} else if (Culling.depthWriteCount < 100) {
			return false
		} else {
			@Pc(40) var x: Int
			@Pc(44) var local44: Int
			if (endX != startX || startZ != endZ) {
				x = startX
				while (x <= endX) {
					var tileZ: Int = startZ
					while (tileZ <= endZ) {
						if (Culling.tileDrawCycles!![level]!![x]!![tileZ] == -Culling.cullingCycle) {
							return false
						}
						tileZ++
					}
					x++
				}
				val sceneX: Int = (startX shl Scene.tileUnits) + 1
				@Pc(127) val z: Int = (startZ shl Scene.tileUnits) + 2
				if (Culling.isBoxOccluded(minY, (endX + 1 - startX) * Culling.fullTileDimension, z, sceneX, Scene.aClass65Array1!![level]!!.getHeight(startZ, startX), Culling.fullTileDimension * (endZ + 1 - startZ))) {
					Sprites.hintHeadId++
					return true
				} else {
					return false
				}
			} else if (Culling.isTileHidden(level, startX, startZ)) {
				x = startX shl Scene.tileUnits
				val sceneZ: Int = startZ shl Scene.tileUnits
				if (Culling.isBoxOccluded(minY, Culling.fullTileDimension, sceneZ, x, Scene.aClass65Array1!![level]!!.getHeight(startZ, startX), Culling.fullTileDimension)) {
					Sprites.hintHeadId++
					return true
				} else {
					return false
				}
			} else {
				return false
			}
		}
	}

	@OriginalMember(owner = "client!lf", name = "T", descriptor = "[Lclient!ea;")
	@JvmField
	var tileCullingCluster: Array<CullingCluster?>? = null

	@OriginalMember(owner = "client!og", name = "n", descriptor = "[Lclient!ea;")
	@JvmField
	var aClass82Array3: Array<CullingCluster?>? = null

	@OriginalMember(owner = "client!nha", name = "a", descriptor = "(IIIBI)Z")
	@JvmStatic
	fun isTileHidden(@OriginalArg(0) tileZ: Int, @OriginalArg(1) minY: Int, @OriginalArg(2) level: Int, @OriginalArg(4) tileX: Int): Boolean {
		if (!Culling.cullingEnabled || !SceneCullingActive.cullingActive) {
			return false
		} else if (Culling.depthWriteCount < 100) {
			return false
		} else if (Culling.isTileHidden(level, tileX, tileZ)) {
			@Pc(26) val x: Int = tileX shl Scene.tileUnits
			@Pc(30) val z: Int = tileZ shl Scene.tileUnits
			if (Culling.isBoxOccluded(minY, Culling.fullTileDimension, z, x, Scene.aClass65Array1!![level]!!.getHeight(tileZ, tileX), Culling.fullTileDimension)) {
				Sprites.hintHeadId++
				return true
			} else {
				return false
			}
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!kea", name = "j", descriptor = "I")
	@JvmField
	var tileCullingClusterCount: Int = 0

	@OriginalMember(owner = "client!ts", name = "a", descriptor = "(IIIII)V")
	@JvmStatic
	fun removeCullingCluster(@OriginalArg(0) level: Int, @OriginalArg(1) type: Int, @OriginalArg(2) z: Int, @OriginalArg(4) x: Int) {
		if (type != 8 && type != 16) {
			@Pc(21) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![level]!![x]!![z]
			if (tile != null) {
				if (type == 1) {
					tile.cullHeightX = 0
				} else if (type == 2) {
					tile.cullHeightZ = 0
				}
			}
			Culling.rebuildTileCullingClusters()
			return
		}
		@Pc(45) var i: Int = 0
		while (i < Culling.dynamicCullingClusterCount) {
			@Pc(51) val locOccluder: CullingCluster? = Culling.aClass82Array4!![i]
			if (locOccluder!!.type.toInt() == type && x == locOccluder.startX.toInt() && z == locOccluder.startY.toInt() || x == locOccluder.endX.toInt() && z == locOccluder.startY.toInt()) {
				if (Culling.dynamicCullingClusterCount != i) {
					Arrays.copy(Culling.aClass82Array4, i + 1, Culling.aClass82Array4, i, Culling.aClass82Array4!!.size - i - 1)
				}
				Culling.dynamicCullingClusterCount--
				return
			}
			i++
		}
	}

	@OriginalMember(owner = "client!wba", name = "O", descriptor = "[Lclient!ea;")
	@JvmField
	var aClass82Array4: Array<CullingCluster?>? = null

	@OriginalMember(owner = "client!qo", name = "pe", descriptor = "I")
	@JvmField
	var fullTileDimensionY: Int = 0

	@OriginalMember(owner = "client!vo", name = "a", descriptor = "(Lclient!ea;Lclient!ha;BI)V")
	@JvmStatic
	fun processCullingCluster(@OriginalArg(0) locOccluder: CullingCluster?, @OriginalArg(1) toolkit: Toolkit?, @OriginalArg(3) plane: Int) {
		@Pc(22) var local22: Int
		if (SceneDrawer.visibleBelowDrawheight != null && plane <= locOccluder!!.plane) {
			var index: Int = 0
			while (index < SceneDrawer.visibleBelowDrawheight.size) {
				if (SceneDrawer.visibleBelowDrawheight[index] != -1000000 && (locOccluder.drawHeights[0] <= SceneDrawer.visibleBelowDrawheight[index] || locOccluder.drawHeights[1] <= SceneDrawer.visibleBelowDrawheight[index] || locOccluder.drawHeights[2] <= SceneDrawer.visibleBelowDrawheight[index] || SceneDrawer.visibleBelowDrawheight[index] >= locOccluder.drawHeights[3]) && (locOccluder.tileCullingsX[0] <= SceneDrawer.roofMaxX[index] || locOccluder.tileCullingsX[1] <= SceneDrawer.roofMaxX[index] || locOccluder.tileCullingsX[2] <= SceneDrawer.roofMaxX[index] || SceneDrawer.roofMaxX[index] >= locOccluder.tileCullingsX[3]) && (locOccluder.tileCullingsX[0] >= SceneDrawer.roofMinX[index] || SceneDrawer.roofMinX[index] <= locOccluder.tileCullingsX[1] || locOccluder.tileCullingsX[2] >= SceneDrawer.roofMinX[index] || SceneDrawer.roofMinX[index] <= locOccluder.tileCullingsX[3]) && (SceneDrawer.roofMaxZ[index] >= locOccluder.tileCullingsZ[0] || locOccluder.tileCullingsZ[1] <= SceneDrawer.roofMaxZ[index] || locOccluder.tileCullingsZ[2] <= SceneDrawer.roofMaxZ[index] || SceneDrawer.roofMaxZ[index] >= locOccluder.tileCullingsZ[3]) && (locOccluder.tileCullingsZ[0] >= SceneDrawer.roofMinZ[index] || locOccluder.tileCullingsZ[1] >= SceneDrawer.roofMinZ[index] || locOccluder.tileCullingsZ[2] >= SceneDrawer.roofMinZ[index] || locOccluder.tileCullingsZ[3] >= SceneDrawer.roofMinZ[index])) {
					return
				}
				index++
			}
		}
		@Pc(233) var local233: Int
		@Pc(261) var local261: Int
		@Pc(282) var onScreen: Boolean
		@Pc(313) var cameraOffset: Float
		if (locOccluder!!.type.toInt() == 1) {
			val tileX: Int = SceneVisibleTiles.visibleTiles + locOccluder.startX - SceneDrawer.cameraGridPositionX
			if (tileX >= 0 && tileX <= SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
				var startZ: Int = SceneVisibleTiles.visibleTiles + locOccluder.startY - Scene.cameraTileZ
				if (startZ < 0) {
					startZ = 0
				} else if (startZ > SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
					return
				}
				var endZ: Int = locOccluder.endY + SceneVisibleTiles.visibleTiles - Scene.cameraTileZ
				if (SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles < endZ) {
					endZ = SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles
				} else if (endZ < 0) {
					return
				}
				onScreen = false
				while (startZ <= endZ) {
					if (Scene.tileInFrustum!![tileX]!![startZ++]) {
						onScreen = true
						break
					}
				}
				if (onScreen) {
					cameraOffset = (WorldMapDisplay.sceneCameraX - locOccluder.tileCullingsX[0]).toFloat()
					if (cameraOffset < 0.0f) {
						cameraOffset *= -1.0f
					}
					if (!(cameraOffset < Culling.occluderMinCameraDistance.toFloat()) && (CullingCluster.projectCorner(0, locOccluder) && (CullingCluster.projectCorner(1, locOccluder) && (CullingCluster.projectCorner(2, locOccluder) && CullingCluster.projectCorner(3, locOccluder))))) {
						Culling.members!![Culling.activeOccluderCount++] = locOccluder
					}
				}
			}
		} else if (locOccluder.type.toInt() == 2) {
			val tileZ: Int = SceneVisibleTiles.visibleTiles + locOccluder.startY - Scene.cameraTileZ
			if (tileZ >= 0 && tileZ <= SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
				var startX: Int = locOccluder.startX + SceneVisibleTiles.visibleTiles - SceneDrawer.cameraGridPositionX
				if (startX < 0) {
					startX = 0
				} else if (startX > SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
					return
				}
				var endX: Int = locOccluder.endX + SceneVisibleTiles.visibleTiles - SceneDrawer.cameraGridPositionX
				if (SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles < endX) {
					endX = SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles
				} else if (endX < 0) {
					return
				}
				onScreen = false
				while (endX >= startX) {
					if (Scene.tileInFrustum!![startX++]!![tileZ]) {
						onScreen = true
						break
					}
				}
				if (onScreen) {
					cameraOffset = (SceneCameraZ.sceneCameraZ - locOccluder.tileCullingsZ[0]).toFloat()
					if (cameraOffset < 0.0f) {
						cameraOffset *= -1.0f
					}
					if (!(cameraOffset < Culling.occluderMinCameraDistance.toFloat()) && (CullingCluster.projectCorner(0, locOccluder) && (CullingCluster.projectCorner(1, locOccluder) && (CullingCluster.projectCorner(2, locOccluder) && CullingCluster.projectCorner(3, locOccluder))))) {
						Culling.members!![Culling.activeOccluderCount++] = locOccluder
					}
				}
			}
		} else if (locOccluder.type.toInt() == 16 || locOccluder.type.toInt() == 8) {
			val tileX: Int = SceneVisibleTiles.visibleTiles + locOccluder.startX - SceneDrawer.cameraGridPositionX
			if (tileX >= 0 && tileX <= SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
				val tileZ: Int = locOccluder.startY + SceneVisibleTiles.visibleTiles - Scene.cameraTileZ
				if (tileZ >= 0 && tileZ <= SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles && Scene.tileInFrustum!![tileX]!![tileZ]) {
					@Pc(569) var cameraOffsetX: Float = (WorldMapDisplay.sceneCameraX - locOccluder.tileCullingsX[0]).toFloat()
					if (cameraOffsetX < 0.0f) {
						cameraOffsetX *= -1.0f
					}
					@Pc(586) var cameraOffsetZ: Float = (SceneCameraZ.sceneCameraZ - locOccluder.tileCullingsZ[0]).toFloat()
					if (cameraOffsetZ < 0.0f) {
						cameraOffsetZ *= -1.0f
					}
					if ((!(cameraOffsetX < Culling.occluderMinCameraDistance.toFloat()) || !(Culling.occluderMinCameraDistance.toFloat() > cameraOffsetZ)) && (CullingCluster.projectCorner(0, locOccluder) && (CullingCluster.projectCorner(1, locOccluder) && (CullingCluster.projectCorner(2, locOccluder) && CullingCluster.projectCorner(3, locOccluder))))) {
						Culling.members!![Culling.activeOccluderCount++] = locOccluder
					}
				}
			}
		} else if (locOccluder.type.toInt() == 4) {
			@Pc(653) val cameraOffsetY: Float = (locOccluder.drawHeights[0] - Culling.cameraY).toFloat()
			if (!(cameraOffsetY <= Culling.fullTileDimensionY.toFloat())) {
				var startZ: Int = SceneVisibleTiles.visibleTiles + locOccluder.startY - Scene.cameraTileZ
				if (startZ < 0) {
					startZ = 0
				} else if (SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles < startZ) {
					return
				}
				var endZ: Int = SceneVisibleTiles.visibleTiles + locOccluder.endY - Scene.cameraTileZ
				if (SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles < endZ) {
					endZ = SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles
				} else if (endZ < 0) {
					return
				}
				@Pc(716) var startX: Int = locOccluder.startX + SceneVisibleTiles.visibleTiles - SceneDrawer.cameraGridPositionX
				if (startX < 0) {
					startX = 0
				} else if (startX > SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
					return
				}
				@Pc(738) var endX: Int = SceneVisibleTiles.visibleTiles + locOccluder.endX - SceneDrawer.cameraGridPositionX
				if (SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles < endX) {
					endX = SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles
				} else if (endX < 0) {
					return
				}
				@Pc(758) var anyVisible: Boolean = false
				@Pc(760) var i: Int = startX
				label283@ while (i <= endX) {
					@Pc(764) var j: Int = startZ
					while (j <= endZ) {
						if (Scene.tileInFrustum!![i]!![j]) {
							anyVisible = true
							break@label283
						}
						j++
					}
					i++
				}
				if (anyVisible && (CullingCluster.projectCorner(0, locOccluder) && (CullingCluster.projectCorner(1, locOccluder) && (CullingCluster.projectCorner(2, locOccluder) && CullingCluster.projectCorner(3, locOccluder))))) {
					Culling.members!![Culling.activeOccluderCount++] = locOccluder
				}
			}
		}
	}

	@OriginalMember(owner = "client!tia", name = "s", descriptor = "J")
	@JvmField
	var occludeCalcNanos: Long = 0L

	@OriginalMember(owner = "client!va", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun reset() {
		@Pc(5) var index: Int
		if (Culling.aClass82Array3 != null) {
			index = 0
			while (index < Culling.groundCullingClusterCount) {
				Culling.aClass82Array3!![index] = null
				index++
			}
			Culling.aClass82Array3 = null
		}
		if (Culling.tileCullingCluster != null) {
			index = 0
			while (index < Culling.tileCullingClusterCount) {
				Culling.tileCullingCluster!![index] = null
				index++
			}
			Culling.tileCullingCluster = null
		}
		if (Culling.aClass82Array4 != null) {
			index = 0
			while (index < Culling.dynamicCullingClusterCount) {
				Culling.aClass82Array4!![index] = null
				index++
			}
			Culling.aClass82Array4 = null
		}
		Culling.tileDrawCycles = null
		MipmapTextureSampler.textures = null
		Culling.members = null
		Culling.occlusionBufferHeight = -1
		Culling.occlusionBufferWidth = -1
	}

	@OriginalMember(owner = "client!sg", name = "U", descriptor = "I")
	@JvmField
	var depthWriteCount: Int = 0

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(IIIIBIIII)V")
	@JvmStatic
	fun addGroundCullingCluster(@OriginalArg(0) maxX: Int, @OriginalArg(1) heightNorth: Int, @OriginalArg(2) minZ: Int, @OriginalArg(3) heightSouth: Int, @OriginalArg(6) maxZ: Int, @OriginalArg(7) plane: Int, @OriginalArg(8) minX: Int) {
		Culling.aClass82Array3!![Culling.groundCullingClusterCount++] = CullingCluster(4, plane, minX, maxX, maxX, minX, heightSouth, heightNorth, heightNorth, heightSouth, minZ, minZ, maxZ, maxZ)
	}

	@OriginalMember(owner = "client!vf", name = "a", descriptor = "(IIIIIBIIII)Z")
	@JvmStatic
	fun cullTriangle(@OriginalArg(0) xC: Int, @OriginalArg(1) yA: Int, @OriginalArg(2) xB: Int, @OriginalArg(3) zC: Int, @OriginalArg(4) zB: Int, @OriginalArg(6) yB: Int, @OriginalArg(7) zA: Int, @OriginalArg(8) xA: Int, @OriginalArg(9) yC: Int): Boolean {
		var xC: Int = xC
		var yA: Int = yA
		var xB: Int = xB
		var zC: Int = zC
		var zB: Int = zB
		var yB: Int = yB
		var zA: Int = zA
		var xA: Int = xA
		var yC: Int = yC
		if (yA > 2000 || yB > 2000 || yC > 2000 || xA > 2000 || xB > 2000 || xC > 2000) {
			return false
		} else if (yA >= -2000 && yB >= -2000 && yC >= -2000 && xA >= -2000 && xB >= -2000 && xC >= -2000) {
			@Pc(66) var local66: Int
			if (Culling.occlusionState == 2) {
				var bufferIndex: Int = xA + yA * Culling.occlusionBufferWidth
				if (bufferIndex >= 0 && MipmapTextureSampler.textures!!.size > bufferIndex && MipmapTextureSampler.textures!![bufferIndex] > (zA shl 8) - 38400) {
					return false
				}
				bufferIndex = xB + yB * Culling.occlusionBufferWidth
				if (bufferIndex >= 0 && bufferIndex < MipmapTextureSampler.textures!!.size && MipmapTextureSampler.textures!![bufferIndex] > (zB shl 8) - 38400) {
					return false
				}
				bufferIndex = xC + yC * Culling.occlusionBufferWidth
				if (bufferIndex >= 0 && MipmapTextureSampler.textures!!.size > bufferIndex && (zC shl 8) - 38400 < MipmapTextureSampler.textures!![bufferIndex]) {
					return false
				}
			}
			val slopeXBa: Int = xB - xA
			@Pc(150) val slopeYBa: Int = yB - yA
			@Pc(155) val slopeXCa: Int = xC - xA
			@Pc(160) val slopeYCa: Int = yC - yA
			@Pc(165) val slopeZBa: Int = zB - zA
			if (yA < yB && yC > yC) {
				yA--
				if (yB > yC) {
					yB++
				} else {
					yC++
				}
			} else if (yC <= yB) {
				yC--
				if (yB < yA) {
					yA++
				} else {
					yB++
				}
			} else {
				yB--
				if (yC < yA) {
					yA++
				} else {
					yC++
				}
			}
			@Pc(219) val slopeZCa: Int = zC - zA
			@Pc(221) var perspectiveBa: Int = 0
			if (yA != yB) {
				perspectiveBa = ((xB - xA) shl 12) / (yB - yA)
			}
			@Pc(240) var perspectiveCb: Int = 0
			if (yC != yB) {
				perspectiveCb = ((xC - xB) shl 12) / (yC - yB)
			}
			@Pc(266) var perspectiveAc: Int = 0
			if (yC != yA) {
				perspectiveAc = ((xA - xC) shl 12) / (yA - yC)
			}
			@Pc(289) val size2d: Int = slopeYCa * slopeXBa - slopeYBa * slopeXCa
			if (size2d == 0) {
				return false
			}
			@Pc(309) val depthGradientX: Int = ((slopeZBa * slopeYCa - slopeZCa * slopeYBa) shl 8) / size2d
			@Pc(322) val depthGradientY: Int = ((slopeXBa * slopeZCa - slopeZBa * slopeXCa) shl 8) / size2d
			if (yA <= yB && yA <= yC) {
				if (yA >= Culling.occlusionBufferHeight) {
					return true
				}
				zA = (zA shl 8) - (xA * depthGradientX - depthGradientX)
				if (yC > Culling.occlusionBufferHeight) {
					yC = Culling.occlusionBufferHeight
				}
				if (yB > Culling.occlusionBufferHeight) {
					yB = Culling.occlusionBufferHeight
				}
				if (yB < yC) {
					xA = xA shl 0xC
					xC = xA
					if (yA < 0) {
						xA -= perspectiveBa * yA
						zA -= yA * depthGradientY
						xC -= yA * perspectiveAc
						yA = 0
					}
					xB = xB shl 0xC
					if (yB < 0) {
						xB -= yB * perspectiveCb
						yB = 0
					}
					if (yB != yA && perspectiveAc < perspectiveBa || yB == yA && perspectiveAc > perspectiveCb) {
						yC -= yB
						yB -= yA
						yA *= Culling.occlusionBufferWidth
						while (true) {
							yB--
							if (yB < 0) {
								while (true) {
									yC--
									if (yC < 0) {
										return true
									}
									if (!Culling.processDepthScanline(depthGradientX, zA, (xB shr 12) + 1, (xC shr 12) + -1, MipmapTextureSampler.textures, yA)) {
										return false
									}
									xB += perspectiveCb
									xC += perspectiveAc
									zA += depthGradientY
									yA += Culling.occlusionBufferWidth
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zA, (xA shr 12) + 1, (xC shr 12) + -1, MipmapTextureSampler.textures, yA)) {
								return false
							}
							zA += depthGradientY
							yA += Culling.occlusionBufferWidth
							xA += perspectiveBa
							xC += perspectiveAc
						}
					} else {
						yC -= yB
						yB -= yA
						yA *= Culling.occlusionBufferWidth
						while (true) {
							yB--
							if (yB < 0) {
								while (true) {
									yC--
									if (yC < 0) {
										return true
									}
									if (!Culling.processDepthScanline(depthGradientX, zA, (xC shr 12) + 1, (xB shr 12) + -1, MipmapTextureSampler.textures, yA)) {
										return false
									}
									xB += perspectiveCb
									yA += Culling.occlusionBufferWidth
									zA += depthGradientY
									xC += perspectiveAc
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zA, (xC shr 12) + 1, (xA shr 12) + -1, MipmapTextureSampler.textures, yA)) {
								return false
							}
							yA += Culling.occlusionBufferWidth
							zA += depthGradientY
							xC += perspectiveAc
							xA += perspectiveBa
						}
					}
				} else {
					xA = xA shl 0xC
					xB = xA
					if (yA < 0) {
						xB -= perspectiveAc * yA
						zA -= yA * depthGradientY
						xA -= perspectiveBa * yA
						yA = 0
					}
					xC = xC shl 0xC
					if (yC < 0) {
						xC -= yC * perspectiveCb
						yC = 0
					}
					if ((yA == yC || perspectiveBa <= perspectiveAc) && (yA != yC || perspectiveBa >= perspectiveCb)) {
						yB -= yC
						yC -= yA
						yA = Culling.occlusionBufferWidth * yA
						while (true) {
							yC--
							if (yC < 0) {
								while (true) {
									yB--
									if (yB < 0) {
										return true
									}
									if (!Culling.processDepthScanline(depthGradientX, zA, (xC shr 12) + 1, (xA shr 12) + -1, MipmapTextureSampler.textures, yA)) {
										return false
									}
									xA += perspectiveBa
									zA += depthGradientY
									xC += perspectiveCb
									yA += Culling.occlusionBufferWidth
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zA, (xB shr 12) + 1, (xA shr 12) + -1, MipmapTextureSampler.textures, yA)) {
								return false
							}
							xB += perspectiveAc
							xA += perspectiveBa
							zA += depthGradientY
							yA += Culling.occlusionBufferWidth
						}
					} else {
						yB -= yC
						yC -= yA
						yA *= Culling.occlusionBufferWidth
						while (true) {
							yC--
							if (yC < 0) {
								while (true) {
									yB--
									if (yB < 0) {
										return true
									}
									if (!Culling.processDepthScanline(depthGradientX, zA, (xA shr 12) + 1, (xC shr 12) - 1, MipmapTextureSampler.textures, yA)) {
										return false
									}
									yA += Culling.occlusionBufferWidth
									xC += perspectiveCb
									zA += depthGradientY
									xA += perspectiveBa
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zA, (xA shr 12) + 1, (xB shr 12) + -1, MipmapTextureSampler.textures, yA)) {
								return false
							}
							yA += Culling.occlusionBufferWidth
							xA += perspectiveBa
							zA += depthGradientY
							xB += perspectiveAc
						}
					}
				}
			} else if (yC < yB) {
				if (yC >= Culling.occlusionBufferHeight) {
					return true
				}
				zC = (zC shl 8) + depthGradientX - depthGradientX * xC
				if (yA > Culling.occlusionBufferHeight) {
					yA = Culling.occlusionBufferHeight
				}
				if (Culling.occlusionBufferHeight < yB) {
					yB = Culling.occlusionBufferHeight
				}
				if (yA < yB) {
					xC = xC shl 0xC
					xB = xC
					xA = xA shl 0xC
					if (yC < 0) {
						zC -= yC * depthGradientY
						xC -= perspectiveAc * yC
						xB -= yC * perspectiveCb
						yC = 0
					}
					if (yA < 0) {
						xA -= yA * perspectiveBa
						yA = 0
					}
					if (perspectiveAc > perspectiveCb) {
						yB -= yA
						yA -= yC
						yC *= Culling.occlusionBufferWidth
						while (true) {
							yA--
							if (yA < 0) {
								while (true) {
									yB--
									if (yB < 0) {
										return true
									}
									if (!Culling.processDepthScanline(depthGradientX, zC, (xA shr 12) + 1, (xB shr 12) + -1, MipmapTextureSampler.textures, yC)) {
										return false
									}
									yC += Culling.occlusionBufferWidth
									xA += perspectiveBa
									zC += depthGradientY
									xB += perspectiveCb
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zC, (xC shr 12) + 1, (xB shr 12) - 1, MipmapTextureSampler.textures, yC)) {
								return false
							}
							yC += Culling.occlusionBufferWidth
							xC += perspectiveAc
							xB += perspectiveCb
							zC += depthGradientY
						}
					} else {
						yB -= yA
						yA -= yC
						yC *= Culling.occlusionBufferWidth
						while (true) {
							yA--
							if (yA < 0) {
								while (true) {
									yB--
									if (yB < 0) {
										return true
									}
									if (!Culling.processDepthScanline(depthGradientX, zC, (xB shr 12) + 1, (xA shr 12) - 1, MipmapTextureSampler.textures, yC)) {
										return false
									}
									zC += depthGradientY
									xB += perspectiveCb
									yC += Culling.occlusionBufferWidth
									xA += perspectiveBa
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zC, (xB shr 12) + 1, (xC shr 12) + -1, MipmapTextureSampler.textures, yC)) {
								return false
							}
							zC += depthGradientY
							xB += perspectiveCb
							yC += Culling.occlusionBufferWidth
							xC += perspectiveAc
						}
					}
				} else {
					xC = xC shl 0xC
					xA = xC
					if (yC < 0) {
						xA -= perspectiveCb * yC
						xC -= perspectiveAc * yC
						zC -= yC * depthGradientY
						yC = 0
					}
					xB = xB shl 0xC
					if (yB < 0) {
						xB -= yB * perspectiveBa
						yB = 0
					}
					if (perspectiveAc <= perspectiveCb) {
						yA -= yB
						yB -= yC
						yC *= Culling.occlusionBufferWidth
						while (true) {
							yB--
							if (yB < 0) {
								while (true) {
									yA--
									if (yA < 0) {
										return true
									}
									if (!Culling.processDepthScanline(depthGradientX, zC, (xB shr 12) + 1, (xC shr 12) + -1, MipmapTextureSampler.textures, yC)) {
										return false
									}
									xC += perspectiveAc
									xB += perspectiveBa
									zC += depthGradientY
									yC += Culling.occlusionBufferWidth
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zC, (xA shr 12) + 1, (xC shr 12) - 1, MipmapTextureSampler.textures, yC)) {
								return false
							}
							zC += depthGradientY
							yC += Culling.occlusionBufferWidth
							xA += perspectiveCb
							xC += perspectiveAc
						}
					} else {
						yA -= yB
						yB -= yC
						yC = Culling.occlusionBufferWidth * yC
						while (true) {
							yB--
							if (yB < 0) {
								while (true) {
									yA--
									if (yA < 0) {
										return true
									}
									if (!Culling.processDepthScanline(depthGradientX, zC, (xC shr 12) + 1, (xB shr 12) + -1, MipmapTextureSampler.textures, yC)) {
										return false
									}
									zC += depthGradientY
									xB += perspectiveBa
									yC += Culling.occlusionBufferWidth
									xC += perspectiveAc
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zC, (xC shr 12) + 1, (xA shr 12) + -1, MipmapTextureSampler.textures, yC)) {
								return false
							}
							xC += perspectiveAc
							yC += Culling.occlusionBufferWidth
							xA += perspectiveCb
							zC += depthGradientY
						}
					}
				}
			} else if (yB >= Culling.occlusionBufferHeight) {
				return true
			} else {
				zB = depthGradientX + (zB shl 8) - xB * depthGradientX
				if (Culling.occlusionBufferHeight < yC) {
					yC = Culling.occlusionBufferHeight
				}
				if (Culling.occlusionBufferHeight < yA) {
					yA = Culling.occlusionBufferHeight
				}
				if (yC >= yA) {
					xB = xB shl 0xC
					xC = xB
					xA = xA shl 0xC
					if (yB < 0) {
						xC -= perspectiveBa * yB
						zB -= yB * depthGradientY
						xB -= perspectiveCb * yB
						yB = 0
					}
					if (yA < 0) {
						xA -= perspectiveAc * yA
						yA = 0
					}
					if (perspectiveBa >= perspectiveCb) {
						yC -= yA
						yA -= yB
						yB = Culling.occlusionBufferWidth * yB
						while (true) {
							yA--
							if (yA < 0) {
								while (true) {
									yC--
									if (yC < 0) {
										return true
									}
									if (!Culling.processDepthScanline(depthGradientX, zB, (xA shr 12) + 1, (xB shr 12) + -1, MipmapTextureSampler.textures, yB)) {
										return false
									}
									zB += depthGradientY
									yB += Culling.occlusionBufferWidth
									xB += perspectiveCb
									xA += perspectiveAc
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zB, (xC shr 12) + 1, (xB shr 12) - 1, MipmapTextureSampler.textures, yB)) {
								return false
							}
							xB += perspectiveCb
							zB += depthGradientY
							yB += Culling.occlusionBufferWidth
							xC += perspectiveBa
						}
					} else {
						yC -= yA
						yA -= yB
						yB = Culling.occlusionBufferWidth * yB
						while (true) {
							yA--
							if (yA < 0) {
								while (true) {
									yC--
									if (yC < 0) {
										return true
									}
									if (!Culling.processDepthScanline(depthGradientX, zB, (xB shr 12) + 1, (xA shr 12) - 1, MipmapTextureSampler.textures, yB)) {
										return false
									}
									xB += perspectiveCb
									xA += perspectiveAc
									zB += depthGradientY
									yB += Culling.occlusionBufferWidth
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zB, (xB shr 12) + 1, (xC shr 12) - 1, MipmapTextureSampler.textures, yB)) {
								return false
							}
							yB += Culling.occlusionBufferWidth
							xB += perspectiveCb
							zB += depthGradientY
							xC += perspectiveBa
						}
					}
				} else {
					xB = xB shl 0xC
					xA = xB
					xC = xC shl 0xC
					if (yB < 0) {
						xA -= perspectiveBa * yB
						xB -= yB * perspectiveCb
						zB -= depthGradientY * yB
						yB = 0
					}
					if (yC < 0) {
						xC -= perspectiveAc * yC
						yC = 0
					}
					if (yB != yC && perspectiveCb > perspectiveBa || yB == yC && perspectiveBa > perspectiveAc) {
						yA -= yC
						yC -= yB
						yB = Culling.occlusionBufferWidth * yB
						while (true) {
							yC--
							if (yC < 0) {
								while (true) {
									yA--
									if (yA < 0) {
										return true
									}
									if (!Culling.processDepthScanline(depthGradientX, zB, (xC shr 12) + 1, (xA shr 12) + -1, MipmapTextureSampler.textures, yB)) {
										return false
									}
									xA += perspectiveBa
									xC += perspectiveAc
									zB += depthGradientY
									yB += Culling.occlusionBufferWidth
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zB, (xB shr 12) + 1, (xA shr 12) + -1, MipmapTextureSampler.textures, yB)) {
								return false
							}
							xA += perspectiveBa
							zB += depthGradientY
							yB += Culling.occlusionBufferWidth
							xB += perspectiveCb
						}
					} else {
						yA -= yC
						yC -= yB
						yB = Culling.occlusionBufferWidth * yB
						while (true) {
							yC--
							if (yC < 0) {
								while (true) {
									yA--
									if (yA < 0) {
										return true
									}
									if (!Culling.processDepthScanline(depthGradientX, zB, (xA shr 12) + 1, (xC shr 12) + -1, MipmapTextureSampler.textures, yB)) {
										return false
									}
									zB += depthGradientY
									yB += Culling.occlusionBufferWidth
									xA += perspectiveBa
									xC += perspectiveAc
								}
							}
							if (!Culling.processDepthScanline(depthGradientX, zB, (xA shr 12) + 1, (xB shr 12) - 1, MipmapTextureSampler.textures, yB)) {
								return false
							}
							xB += perspectiveCb
							xA += perspectiveBa
							yB += Culling.occlusionBufferWidth
							zB += depthGradientY
						}
					}
				}
			}
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!cc", name = "J", descriptor = "Z")
	@JvmField
	var cullingEnabled: Boolean = true

	@OriginalMember(owner = "client!sm", name = "i", descriptor = "I")
	@JvmField
	var cameraY: Int = 0

	@OriginalMember(owner = "client!fca", name = "p", descriptor = "I")
	@JvmField
	var groundCullingClusterCount: Int = 0

	@OriginalMember(owner = "client!jk", name = "J", descriptor = "[Lclient!ea;")
	@JvmField
	var members: Array<CullingCluster?>? = null

	@OriginalMember(owner = "client!gb", name = "w", descriptor = "Z")
	@JvmField
	var tileCullingClustersBuilt: Boolean = false

	@OriginalMember(owner = "client!lp", name = "a", descriptor = "(IIBI)Z")
	@JvmStatic
	fun isTileHidden(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(3) z: Int): Boolean {
		if (!Culling.cullingEnabled || !SceneCullingActive.cullingActive) {
			return false
		} else if (Culling.depthWriteCount < 100) {
			return false
		} else {
			@Pc(24) val cachedVisibility: Int = Culling.tileDrawCycles!![level]!![x]!![z]
			if (-Culling.cullingCycle == cachedVisibility) {
				return false
			} else if (Culling.cullingCycle == cachedVisibility) {
				return true
			} else if (Scene.aClass65Array3 === Scene.aClass65Array1) {
				return false
			} else {
				@Pc(48) val absX: Int = x shl Scene.tileUnits
				@Pc(52) val absZ: Int = z shl Scene.tileUnits
				if (Culling.isTriangleOccluded(absX + 1, absX - (-Culling.fullTileDimension - -1), absZ + 1, absX + 1, absZ + Culling.fullTileDimension - 1, Scene.aClass65Array1!![level]!!.getHeight(z + 1, x), Scene.aClass65Array1!![level]!!.getHeight(z + 1, x + 1), Scene.aClass65Array1!![level]!!.getHeight(z, x), Culling.fullTileDimension + absZ - 1) && Culling.isTriangleOccluded(absX + 1, Culling.fullTileDimension + absX + -1, absZ + 1, Culling.fullTileDimension + (absX - 1), absZ + Culling.fullTileDimension - 1, Scene.aClass65Array1!![level]!!.getHeight(z - -1, x + 1), Scene.aClass65Array1!![level]!!.getHeight(z, x + 1), Scene.aClass65Array1!![level]!!.getHeight(z, x), absZ + 1)) {
					Culling.occludedGroundCount++
					Culling.tileDrawCycles!![level]!![x]!![z] = Culling.cullingCycle
					return true
				} else {
					Culling.tileDrawCycles!![level]!![x]!![z] = -Culling.cullingCycle
					return false
				}
			}
		}
	}

	@OriginalMember(owner = "client!em", name = "a", descriptor = "(Z)V")
	@JvmStatic
	fun rebuildTileCullingClusters() {
		@Pc(3) var i: Int = 0
		while (i < Culling.tileCullingClusterCount) {
			Culling.tileCullingCluster!![i] = null
			i++
		}
		Culling.tileCullingClusterCount = 0
		@Pc(29) var plan: Int
		@Pc(33) var local33: Int
		@Pc(25) var j: Int = 0
		while (j < Scene.length) {
			plan = 0
			while (plan < Scene.sceneWidth) {
				var tileX: Int = 0
				while (tileX < Camera.sceneLength) {
					@Pc(43) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![j]!![tileX]!![plan]
					if (tile != null) {
						if (tile.cullHeightZ > 0) {
							tile.cullHeightZ = (tile.cullHeightZ * -1).toShort()
						}
						if (tile.cullHeightX > 0) {
							tile.cullHeightX = (tile.cullHeightX * -1).toShort()
						}
					}
					tileX++
				}
				plan++
			}
			j++
		}
		plan = 0
		while (plan < Scene.length) {
			var tileZ: Int = 0
			while (tileZ < Scene.sceneWidth) {
				@Pc(99) var k: Int = 0
				while (k < Camera.sceneLength) {
					@Pc(109) val tile2: Tile? = Scene.aClass100ArrayArrayArray3!![plan]!![k]!![tileZ]
					if (tile2 != null) {
						@Pc(132) val hasBridge: Boolean = Scene.aClass100ArrayArrayArray3!![0]!![k]!![tileZ] != null && Scene.aClass100ArrayArrayArray3!![0]!![k]!![tileZ]!!.bridge != null
						@Pc(140) var runStart: Int
						@Pc(142) var runEnd: Int
						@Pc(156) var tile3: Tile?
						@Pc(164) var baseHeight: Int
						@Pc(319) var levelSpan: Int
						@Pc(333) var heightStart: Int
						@Pc(340) var topHeightStart: Int
						@Pc(356) var heightEnd: Int
						@Pc(364) var topHeightEnd: Int
						@Pc(368) var xEnd: Int
						@Pc(372) var xStart: Int
						@Pc(378) var local378: Int
						@Pc(416) var cullLevel: Int
						@Pc(420) var runIndex: Int
						if (tile2.cullHeightX < 0) {
							runStart = tileZ
							runEnd = tileZ
							tile3 = Scene.aClass100ArrayArrayArray3!![plan]!![k]!![tileZ - 1]
							baseHeight = Scene.aClass65Array2!![plan]!!.getHeight(tileZ, k)
							while (runStart > 0 && tile3 != null && tile3.cullHeightX < 0 && tile2.cullHeightX == tile3.cullHeightX && tile2.cullOffsetX == tile3.cullOffsetX && baseHeight == Scene.aClass65Array2!![plan]!!.getHeight(runStart - 1, k) && (runStart - 1 <= 0 || baseHeight == Scene.aClass65Array2!![plan]!!.getHeight(runStart - 2, k))) {
								runStart--
								tile3 = Scene.aClass100ArrayArrayArray3!![plan]!![k]!![runStart - 1]
							}
							tile3 = Scene.aClass100ArrayArrayArray3!![plan]!![k]!![tileZ + 1]
							while (runEnd < Camera.sceneLength && tile3 != null && tile3.cullHeightX < 0 && tile2.cullHeightX == tile3.cullHeightX && tile2.cullOffsetX == tile3.cullOffsetX && Scene.aClass65Array2!![plan]!!.getHeight(runEnd + 1, k) == baseHeight && (runEnd + 1 >= Camera.sceneLength || baseHeight == Scene.aClass65Array2!![plan]!!.getHeight(runEnd + 2, k))) {
								runEnd++
								tile3 = Scene.aClass100ArrayArrayArray3!![plan]!![k]!![runEnd + 1]
							}
							levelSpan = plan + 1 - plan
							heightStart = Scene.aClass65Array2!![if (hasBridge) plan + 1 else plan]!!.getHeight(runStart, k)
							topHeightStart = tile2.cullHeightX * levelSpan + heightStart
							heightEnd = Scene.aClass65Array2!![if (hasBridge) plan + 1 else plan]!!.getHeight(runEnd + 1, k)
							topHeightEnd = heightEnd + levelSpan * tile2.cullHeightX
							xEnd = k shl Scene.tileUnits
							xStart = runStart shl Scene.tileUnits
							val zEnd: Int = Culling.fullTileDimension + (runEnd shl Scene.tileUnits)
							Culling.tileCullingCluster!![Culling.tileCullingClusterCount++] = CullingCluster(1, plan, tile2.cullOffsetX + xEnd, tile2.cullOffsetX + xEnd, tile2.cullOffsetX + xEnd, tile2.cullOffsetX + xEnd, heightStart, heightEnd, topHeightEnd, topHeightStart, xStart, zEnd, zEnd, xStart)
							cullLevel = plan
							while (cullLevel <= plan) {
								runIndex = runStart
								while (runIndex <= runEnd) {
									Scene.aClass100ArrayArrayArray3!![cullLevel]!![k]!![runIndex]!!.cullHeightX = (Scene.aClass100ArrayArrayArray3!![cullLevel]!![k]!![runIndex]!!.cullHeightX * -1).toShort()
									runIndex++
								}
								cullLevel++
							}
						}
						if (tile2.cullHeightZ < 0) {
							runStart = k
							runEnd = k
							tile3 = Scene.aClass100ArrayArrayArray3!![plan]!![k - 1]!![tileZ]
							baseHeight = Scene.aClass65Array2!![plan]!!.getHeight(tileZ, k)
							while (runStart > 0 && tile3 != null && tile3.cullHeightZ < 0 && tile3.cullHeightZ == tile2.cullHeightZ && tile2.cullOffsetZ == tile3.cullOffsetZ && Scene.aClass65Array2!![plan]!!.getHeight(tileZ, runStart - 1) == baseHeight && (runStart - 1 <= 0 || Scene.aClass65Array2!![plan]!!.getHeight(tileZ, runStart - 2) == baseHeight)) {
								runStart--
								tile3 = Scene.aClass100ArrayArrayArray3!![plan]!![runStart - 1]!![tileZ]
							}
							tile3 = Scene.aClass100ArrayArrayArray3!![plan]!![k + 1]!![tileZ]
							while (runEnd < Scene.sceneWidth && tile3 != null && tile3.cullHeightZ < 0 && tile3.cullHeightZ == tile2.cullHeightZ && tile3.cullOffsetZ == tile2.cullOffsetZ && Scene.aClass65Array2!![plan]!!.getHeight(tileZ, runEnd + 1) == baseHeight && (Scene.sceneWidth <= runEnd + 1 || Scene.aClass65Array2!![plan]!!.getHeight(tileZ, runEnd + 2) == baseHeight)) {
								runEnd++
								tile3 = Scene.aClass100ArrayArrayArray3!![plan]!![runEnd + 1]!![tileZ]
							}
							levelSpan = plan + 1 - plan
							heightStart = Scene.aClass65Array2!![if (hasBridge) plan + 1 else plan]!!.getHeight(tileZ, runStart)
							topHeightStart = heightStart + levelSpan * tile2.cullHeightZ
							heightEnd = Scene.aClass65Array2!![if (hasBridge) plan + 1 else plan]!!.getHeight(tileZ, runEnd + 1)
							topHeightEnd = tile2.cullHeightZ * levelSpan + heightEnd
							xEnd = runStart shl Scene.tileUnits
							xStart = (runEnd shl Scene.tileUnits) + Culling.fullTileDimension
							val sceneZ: Int = tileZ shl Scene.tileUnits
							Culling.tileCullingCluster!![Culling.tileCullingClusterCount++] = CullingCluster(2, plan, xEnd, xStart, xStart, xEnd, heightStart, heightEnd, topHeightEnd, topHeightStart, sceneZ + tile2.cullOffsetZ, sceneZ + tile2.cullOffsetZ, tile2.cullOffsetZ + sceneZ, sceneZ - -tile2.cullOffsetZ)
							cullLevel = plan
							while (cullLevel <= plan) {
								runIndex = runStart
								while (runIndex <= runEnd) {
									Scene.aClass100ArrayArrayArray3!![cullLevel]!![runIndex]!![tileZ]!!.cullHeightZ = (Scene.aClass100ArrayArrayArray3!![cullLevel]!![runIndex]!![tileZ]!!.cullHeightZ * -1).toShort()
									runIndex++
								}
								cullLevel++
							}
						}
					}
					k++
				}
				tileZ++
			}
			plan++
		}
		Culling.tileCullingClustersBuilt = true
	}

	@OriginalMember(owner = "client!eo", name = "e", descriptor = "[[[I")
	@JvmField
	var tileDrawCycles: Array<Array<IntArray?>?>? = null

	@OriginalMember(owner = "client!ks", name = "a", descriptor = "(IBIIII[II)Z")
	@JvmStatic
	fun processDepthScanline(@OriginalArg(0) depthStep: Int, @OriginalArg(2) depth: Int, @OriginalArg(4) endX: Int, @OriginalArg(5) startX: Int, @OriginalArg(6) depthBuffer: IntArray?, @OriginalArg(7) offset: Int): Boolean {
		var depth: Int = depth
		var endX: Int = endX
		var startX: Int = startX
		var offset: Int = offset
		if (Culling.occlusionBufferWidth < endX) {
			endX = Culling.occlusionBufferWidth
		}
		if (startX < 0) {
			startX = 0
		}
		if (startX >= endX) {
			return true
		}
		offset += startX - 1
		depth += depthStep * startX
		@Pc(50) var remaining: Int = (endX - startX) shr 2
		@Pc(64) var local64: Int
		@Pc(77) var nextDepth: Int
		@Pc(66) var index: Int
		if (Culling.occlusionState == 1) {
			Culling.depthWriteCount += remaining
			while (true) {
				remaining--
				if (remaining < 0) {
					remaining = (endX - startX) and 0x3
					while (true) {
						remaining--
						if (remaining < 0) {
							return true
						}
						val invertedDepth: Int = depth.inv()
						offset++
						if (invertedDepth > depthBuffer!![offset].inv()) {
							depthBuffer[offset] = depth
						}
						depth += depthStep
					}
				}
				var invertedDepth: Int = depth.inv()
				index = offset + 1
				if (invertedDepth > depthBuffer!![index].inv()) {
					depthBuffer[index] = depth
				}
				nextDepth = depth + depthStep
				invertedDepth = nextDepth.inv()
				index++
				if (invertedDepth > depthBuffer[index].inv()) {
					depthBuffer[index] = nextDepth
				}
				nextDepth += depthStep
				index++
				if (nextDepth < depthBuffer[index]) {
					depthBuffer[index] = nextDepth
				}
				nextDepth += depthStep
				invertedDepth = nextDepth.inv()
				offset = index + 1
				if (invertedDepth > depthBuffer[offset].inv()) {
					depthBuffer[offset] = nextDepth
				}
				depth = nextDepth + depthStep
			}
		} else {
			depth -= 38400
			while (true) {
				remaining--
				if (remaining < 0) {
					remaining = (endX - startX) and 0x3
					while (true) {
						remaining--
						if (remaining < 0) {
							return true
						}
						offset++
						if (depth < depthBuffer!![offset]) {
							return false
						}
						depth += depthStep
					}
				}
				val invertedDepth: Int = depth.inv()
				index = offset + 1
				if (invertedDepth > depthBuffer!![index].inv()) {
					return false
				}
				nextDepth = depth + depthStep
				index++
				if (depthBuffer[index] > nextDepth) {
					return false
				}
				nextDepth += depthStep
				index++
				if (nextDepth < depthBuffer[index]) {
					return false
				}
				nextDepth += depthStep
				offset = index + 1
				if (depthBuffer[offset] > nextDepth) {
					return false
				}
				depth = nextDepth + depthStep
			}
		}
	}

	@OriginalMember(owner = "client!hl", name = "L", descriptor = "Lclient!bca;")
	@JvmField
	var aClass30_7: Matrix? = null

	@OriginalMember(owner = "client!naa", name = "db", descriptor = "I")
	@JvmField
	var occludedGroundCount: Int = 0

	@OriginalMember(owner = "client!ek", name = "a", descriptor = "(IIILclient!qfa;B)Z")
	@JvmStatic
	fun hideWall(@OriginalArg(0) tileZ: Int, @OriginalArg(1) tileX: Int, @OriginalArg(2) level: Int, @OriginalArg(3) wall: Wall?): Boolean {
		if (!Culling.cullingEnabled || !SceneCullingActive.cullingActive) {
			return false
		} else if (Culling.depthWriteCount < 100) {
			return false
		} else if (Culling.isTileHidden(level, tileX, tileZ)) {
			@Pc(28) val x: Int = tileX shl Scene.tileUnits
			@Pc(32) val z: Int = tileZ shl Scene.tileUnits
			@Pc(47) val bottomY: Int = Scene.aClass65Array1!![level]!!.getHeight(tileZ, tileX) - 1
			@Pc(53) val topY: Int = bottomY + wall!!.getModelMinY()
			if (wall.sideMask.toInt() == 1) {
				if (!Culling.isTriangleOccluded(x, x, z, x, Culling.fullTileDimension + z, topY, topY, bottomY, z)) {
					return false
				} else if (Culling.isTriangleOccluded(x, x, z, x, Culling.fullTileDimension + z, bottomY, topY, bottomY, z + Culling.fullTileDimension)) {
					OccludedWallCount.occludedWallCount++
					return true
				} else {
					return false
				}
			} else if (wall.sideMask.toInt() == 2) {
				if (!Culling.isTriangleOccluded(x, x + Culling.fullTileDimension, z - -Culling.fullTileDimension, x, Culling.fullTileDimension + z, topY, topY, bottomY, z + Culling.fullTileDimension)) {
					return false
				} else if (Culling.isTriangleOccluded(x, Culling.fullTileDimension + x, z + Culling.fullTileDimension, Culling.fullTileDimension + x, Culling.fullTileDimension + z, topY, bottomY, bottomY, z + Culling.fullTileDimension)) {
					OccludedWallCount.occludedWallCount++
					return true
				} else {
					return false
				}
			} else if (wall.sideMask.toInt() == 4) {
				if (!Culling.isTriangleOccluded(x + Culling.fullTileDimension, Culling.fullTileDimension + x, z, Culling.fullTileDimension + x, z - -Culling.fullTileDimension, topY, topY, bottomY, z)) {
					return false
				} else if (Culling.isTriangleOccluded(x + Culling.fullTileDimension, x - -Culling.fullTileDimension, z, Culling.fullTileDimension + x, z - -Culling.fullTileDimension, bottomY, topY, bottomY, Culling.fullTileDimension + z)) {
					OccludedWallCount.occludedWallCount++
					return true
				} else {
					return false
				}
			} else if (wall.sideMask.toInt() == 8) {
				if (!Culling.isTriangleOccluded(x, Culling.fullTileDimension + x, z, x, z, topY, topY, bottomY, z)) {
					return false
				} else if (Culling.isTriangleOccluded(x, x + Culling.fullTileDimension, z, Culling.fullTileDimension + x, z, topY, bottomY, bottomY, z)) {
					OccludedWallCount.occludedWallCount++
					return true
				} else {
					return false
				}
			} else if (wall.sideMask.toInt() == 16) {
				if (Culling.isBoxOccluded(topY, Scene.halfTileOffset, Scene.halfTileOffset + z, x, bottomY, Scene.halfTileOffset)) {
					OccludedWallCount.occludedWallCount++
					return true
				} else {
					return false
				}
			} else if (wall.sideMask.toInt() == 32) {
				if (Culling.isBoxOccluded(topY, Scene.halfTileOffset, Scene.halfTileOffset + z, x + Scene.halfTileOffset, bottomY, Scene.halfTileOffset)) {
					OccludedWallCount.occludedWallCount++
					return true
				} else {
					return false
				}
			} else if (wall.sideMask.toInt() == 64) {
				if (Culling.isBoxOccluded(topY, Scene.halfTileOffset, z, Scene.halfTileOffset + x, bottomY, Scene.halfTileOffset)) {
					OccludedWallCount.occludedWallCount++
					return true
				} else {
					return false
				}
			} else if (wall.sideMask.toInt() != 128) {
				return true
			} else if (Culling.isBoxOccluded(topY, Scene.halfTileOffset, z, x, bottomY, Scene.halfTileOffset)) {
				OccludedWallCount.occludedWallCount++
				return true
			} else {
				return false
			}
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!mw", name = "B", descriptor = "I")
	@JvmField
	var occlusionScaleY: Int = 0

	@OriginalMember(owner = "client!pw", name = "nb", descriptor = "[I")
	@JvmField
	val occlusionProjection: IntArray = IntArray(3)

	@OriginalMember(owner = "client!bp", name = "a", descriptor = "(IIII)Z")
	@JvmStatic
	fun projectOcclusionPoint(@OriginalArg(0) y: Int, @OriginalArg(1) z: Int, @OriginalArg(2) x: Int): Boolean {
		Culling.aClass30_7!!.method4652(x, y, z, Culling.occlusionProjection)
		@Pc(13) val depth: Int = Culling.occlusionProjection[2]
		if (depth < 50) {
			return false
		} else {
			Culling.occlusionProjection[2] = depth
			Culling.occlusionProjection[0] = Culling.occlusionScaleX * Culling.occlusionProjection[0] / depth + Culling.occlusionCenterX
			Culling.occlusionProjection[1] = Culling.occlusionCenterY + Culling.occlusionProjection[1] * Culling.occlusionScaleY / depth
			return true
		}
	}

	@OriginalMember(owner = "client!uf", name = "Q", descriptor = "Z")
	const val BENCHMARK_CULLING: Boolean = true

	@OriginalMember(owner = "client!cfa", name = "N", descriptor = "[I")
	@JvmField
	val cullingViewportSize: IntArray = IntArray(2)

	@OriginalMember(owner = "client!ei", name = "m", descriptor = "I")
	@JvmField
	var occlusionState: Int = 0

	@OriginalMember(owner = "client!us", name = "c", descriptor = "I")
	@JvmField
	var fullTileDimension: Int = 0

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(IIIIIIIIIZ)Z")
	@JvmStatic
	fun isTriangleOccluded(@OriginalArg(0) x0: Int, @OriginalArg(1) x1: Int, @OriginalArg(2) z0: Int, @OriginalArg(3) x2: Int, @OriginalArg(4) z2: Int, @OriginalArg(5) y2: Int, @OriginalArg(6) y1: Int, @OriginalArg(7) y0: Int, @OriginalArg(8) z1: Int): Boolean {
		if (!Culling.projectOcclusionPoint(y0, z0, x0)) {
			return false
		}
		@Pc(15) val screenY0: Int = Culling.occlusionProjection[1]
		@Pc(19) val screenX0: Int = Culling.occlusionProjection[0]
		@Pc(23) val depth0: Int = Culling.occlusionProjection[2]
		if (!Culling.projectOcclusionPoint(y1, z1, x1)) {
			return false
		}
		@Pc(35) val screenX1: Int = Culling.occlusionProjection[0]
		@Pc(39) val depth1: Int = Culling.occlusionProjection[2]
		@Pc(43) val screenY1: Int = Culling.occlusionProjection[1]
		if (Culling.projectOcclusionPoint(y2, z2, x2)) {
			@Pc(55) val depth2: Int = Culling.occlusionProjection[2]
			@Pc(64) val screenY2: Int = Culling.occlusionProjection[1]
			@Pc(68) val screenX2: Int = Culling.occlusionProjection[0]
			return Culling.cullTriangle(screenX2, screenY0, screenX1, depth2, depth1, screenY1, depth0, screenX0, screenY2)
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!kea", name = "l", descriptor = "I")
	@JvmField
	var occlusionCenterX: Int = 0

	@OriginalMember(owner = "client!wb", name = "y", descriptor = "I")
	@JvmField
	var dynamicCullingClusterCount: Int = 0

	@OriginalMember(owner = "client!rq", name = "M", descriptor = "J")
	@JvmField
	var occludeCalcStartNanos: Long = 0L

	@OriginalMember(owner = "client!td", name = "N", descriptor = "I")
	@JvmField
	var activeOccluderCount: Int = 0

	@OriginalMember(owner = "client!so", name = "n", descriptor = "I")
	@JvmField
	var cullingCycle: Int = 0

	@OriginalMember(owner = "client!ua", name = "Qb", descriptor = "I")
	@JvmField
	var occlusionCenterY: Int = 0

	@OriginalMember(owner = "client!dfa", name = "a", descriptor = "(IBLclient!ha;)V")
	@JvmStatic
	fun calculateOcclusion(@OriginalArg(0) lowestRenderedPlane: Int, @OriginalArg(2) toolkit: Toolkit?) {
		if (!Culling.cullingEnabled || !SceneCullingActive.cullingActive) {
			Culling.activeOccluderCount = 0
			return
		}
		if (Culling.BENCHMARK_CULLING) {
			Culling.occludeCalcStartNanos = Scene.aClass158_1!!.nanoTime()
		}
		OccludedWallCount.occludedWallCount = 0
		Culling.occludedGroundCount = 0
		Sprites.hintHeadId = 0
		@Pc(27) val viewport: IntArray = toolkit!!.Y()!!
		Culling.occlusionScaleY = (viewport[3].toFloat() / 3.0f).toInt()
		Culling.occlusionScaleX = (viewport[2].toFloat() / 3.0f).toInt()
		toolkit.accumulateViewportDimensions(Culling.cullingViewportSize)
		if (Culling.occlusionBufferWidth != (Culling.cullingViewportSize[0].toFloat() / 3.0f).toInt() || (Culling.cullingViewportSize[1].toFloat() / 3.0f).toInt() != Culling.occlusionBufferHeight) {
			Culling.occlusionBufferHeight = (Culling.cullingViewportSize[1].toFloat() / 3.0f).toInt()
			Culling.occlusionBufferWidth = (Culling.cullingViewportSize[0].toFloat() / 3.0f).toInt()
			Culling.occlusionCenterY = Culling.occlusionBufferHeight shr 1
			MipmapTextureSampler.textures = IntArray(Culling.occlusionBufferHeight * Culling.occlusionBufferWidth)
			Culling.occlusionCenterX = Culling.occlusionBufferWidth shr 1
		}
		Culling.aClass30_7 = toolkit.camera()
		Culling.activeOccluderCount = 0
		@Pc(104) var i: Int = 0
		while (i < Culling.groundCullingClusterCount) {
			Culling.processCullingCluster(Culling.aClass82Array3!![i], toolkit, lowestRenderedPlane)
			i++
		}
		@Pc(130) var j: Int = 0
		while (j < Culling.tileCullingClusterCount) {
			Culling.processCullingCluster(Culling.tileCullingCluster!![j], toolkit, lowestRenderedPlane)
			j++
		}
		@Pc(149) var k: Int = 0
		while (k < Culling.dynamicCullingClusterCount) {
			Culling.processCullingCluster(Culling.aClass82Array4!![k], toolkit, lowestRenderedPlane)
			k++
		}
		Culling.depthWriteCount = 0
		if (Culling.activeOccluderCount > 0) {
			@Pc(169) val length: Int = MipmapTextureSampler.textures!!.size
			@Pc(176) val unrolledEnd: Int = (length - length) and 0x7
			@Pc(178) var index: Int = 0
			while (unrolledEnd > index) {
				MipmapTextureSampler.textures!![index++] = Int.MAX_VALUE
				MipmapTextureSampler.textures!![index++] = Int.MAX_VALUE
				MipmapTextureSampler.textures!![index++] = Int.MAX_VALUE
				MipmapTextureSampler.textures!![index++] = Int.MAX_VALUE
				MipmapTextureSampler.textures!![index++] = Int.MAX_VALUE
				MipmapTextureSampler.textures!![index++] = Int.MAX_VALUE
				MipmapTextureSampler.textures!![index++] = Int.MAX_VALUE
				MipmapTextureSampler.textures!![index++] = Int.MAX_VALUE
			}
			while (index < length) {
				MipmapTextureSampler.textures!![index++] = Int.MAX_VALUE
			}
			Culling.occlusionState = 1
			@Pc(245) var m: Int = 0
			while (m < Culling.activeOccluderCount) {
				@Pc(251) val locOccluder: CullingCluster? = Culling.members!![m]
				Culling.cullTriangle(locOccluder!!.projectionZ[3].toInt(), locOccluder.projectionY[0].toInt(), locOccluder.projectionZ[1].toInt(), locOccluder.projectionX[3].toInt(), locOccluder.projectionX[1].toInt(), locOccluder.projectionY[1].toInt(), locOccluder.projectionX[0].toInt(), locOccluder.projectionZ[0].toInt(), locOccluder.projectionY[3].toInt())
				Culling.cullTriangle(locOccluder.projectionZ[3].toInt(), locOccluder.projectionY[1].toInt(), locOccluder.projectionZ[2].toInt(), locOccluder.projectionX[3].toInt(), locOccluder.projectionX[2].toInt(), locOccluder.projectionY[2].toInt(), locOccluder.projectionX[1].toInt(), locOccluder.projectionZ[1].toInt(), locOccluder.projectionY[3].toInt())
				m++
			}
			Culling.occlusionState = 2
		}
		if (Culling.BENCHMARK_CULLING) {
			Culling.occludeCalcNanos = Scene.aClass158_1!!.nanoTime() - Culling.occludeCalcStartNanos
		}
	}

	@OriginalMember(owner = "client!pu", name = "h", descriptor = "[I")
	@JvmField
	val occluderDebugCorner0: IntArray = IntArray(3)

	@OriginalMember(owner = "client!lea", name = "d", descriptor = "I")
	@JvmField
	var occlusionBufferHeight: Int = -1

	@OriginalMember(owner = "client!ng", name = "a", descriptor = "(IIIIIII)Z")
	@JvmStatic
	fun isBoxOccluded(@OriginalArg(0) sizeY: Int, @OriginalArg(1) sizeX: Int, @OriginalArg(2) z: Int, @OriginalArg(3) x: Int, @OriginalArg(4) y: Int, @OriginalArg(5) sizeZ: Int): Boolean {
		@Pc(8) val maxX: Int = x + sizeX
		@Pc(12) val maxY: Int = sizeY + y
		@Pc(16) val maxZ: Int = z + sizeZ
		if (!Culling.isTriangleOccluded(x, maxX, z, x, maxZ, maxY, maxY, maxY, maxZ)) {
			return false
		} else if (Culling.isTriangleOccluded(x, maxX, z, maxX, maxZ, maxY, maxY, maxY, z)) {
			if (WorldMapDisplay.sceneCameraX > x) {
				if (!Culling.isTriangleOccluded(x, x, maxZ, x, maxZ, maxY, maxY, y, z)) {
					return false
				}
				if (!Culling.isTriangleOccluded(x, x, maxZ, x, z, maxY, y, y, z)) {
					return false
				}
			} else if (!Culling.isTriangleOccluded(maxX, maxX, maxZ, maxX, maxZ, maxY, maxY, y, z)) {
				return false
			} else if (!Culling.isTriangleOccluded(maxX, maxX, maxZ, maxX, z, maxY, y, y, z)) {
				return false
			}
			if (z >= SceneCameraZ.sceneCameraZ) {
				if (!Culling.isTriangleOccluded(x, maxX, maxZ, x, maxZ, maxY, maxY, y, maxZ)) {
					return false
				}
				if (!Culling.isTriangleOccluded(x, maxX, maxZ, maxX, maxZ, maxY, y, y, maxZ)) {
					return false
				}
			} else if (!Culling.isTriangleOccluded(x, maxX, z, x, z, maxY, maxY, y, z)) {
				return false
			} else if (!Culling.isTriangleOccluded(x, maxX, z, maxX, z, maxY, y, y, z)) {
				return false
			}
			return true
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!ws", name = "a", descriptor = "I")
	@JvmField
	var occlusionBufferWidth: Int = -1

	@OriginalMember(owner = "client!am", name = "d", descriptor = "[I")
	@JvmField
	val occluderDebugCorner1: IntArray = IntArray(3)

	@OriginalMember(owner = "client!hba", name = "b", descriptor = "I")
	@JvmField
	var occluderMinCameraDistance: Int = 0

	@OriginalMember(owner = "client!me", name = "c", descriptor = "[I")
	@JvmField
	val occluderDebugCorner3: IntArray = IntArray(3)

	@OriginalMember(owner = "client!oc", name = "g", descriptor = "I")
	@JvmField
	var occlusionScaleX: Int = 0

	@OriginalMember(owner = "client!rl", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun rebuildCullingClusters() {
		Culling.rebuildTileCullingClusters()
	}

	@OriginalMember(owner = "client!te", name = "a", descriptor = "[I")
	@JvmField
	val occluderDebugCorner2: IntArray = IntArray(3)

	@OriginalMember(owner = "client!te", name = "a", descriptor = "()V")
	@JvmStatic
	fun drawOcclusionDebug() {
		if (W2DebugMode.sceneDebugMode == 0 || W2DebugMode.font == null) {
			return
		}
		Scene.toolkit!!.K(SceneDrawer.clipBounds)
		@Pc(12) var i: Int = 0
		while (i < ScriptRunner.threadSplitX.size) {
			Scene.toolkit!!.verticalLine(ScriptRunner.threadSplitX[i] + SceneDrawer.threadSplitOffsets[i], -256, SceneDrawer.clipBounds[3] - SceneDrawer.clipBounds[1], SceneDrawer.clipBounds[1])
			i++
		}
		@Pc(41) var j: Int = 0
		while (j < Culling.activeOccluderCount) {
			@Pc(46) val locOccluder: CullingCluster? = Culling.members!![j]
			Scene.toolkit!!.H(locOccluder!!.tileCullingsX[0], locOccluder.drawHeights[0], locOccluder.tileCullingsZ[0], Culling.occluderDebugCorner0)
			Scene.toolkit!!.H(locOccluder.tileCullingsX[1], locOccluder.drawHeights[1], locOccluder.tileCullingsZ[1], Culling.occluderDebugCorner1)
			Scene.toolkit!!.H(locOccluder.tileCullingsX[2], locOccluder.drawHeights[2], locOccluder.tileCullingsZ[2], occluderDebugCorner2)
			Scene.toolkit!!.H(locOccluder.tileCullingsX[3], locOccluder.drawHeights[3], locOccluder.tileCullingsZ[3], Culling.occluderDebugCorner3)
			if (Culling.occluderDebugCorner0[2] != -1 && Culling.occluderDebugCorner1[2] != -1 && occluderDebugCorner2[2] != -1 && Culling.occluderDebugCorner3[2] != -1) {
				@Pc(128) var colour: Int = -65536
				if (locOccluder.type.toInt() == 4) {
					colour = -16776961
				}
				Scene.toolkit!!.drawOpaqueLine(Culling.occluderDebugCorner0[1], Culling.occluderDebugCorner0[0], Culling.occluderDebugCorner1[0], colour, Culling.occluderDebugCorner1[1])
				Scene.toolkit!!.drawOpaqueLine(Culling.occluderDebugCorner1[1], Culling.occluderDebugCorner1[0], occluderDebugCorner2[0], colour, occluderDebugCorner2[1])
				Scene.toolkit!!.drawOpaqueLine(occluderDebugCorner2[1], occluderDebugCorner2[0], Culling.occluderDebugCorner3[0], colour, Culling.occluderDebugCorner3[1])
				Scene.toolkit!!.drawOpaqueLine(Culling.occluderDebugCorner3[1], Culling.occluderDebugCorner3[0], Culling.occluderDebugCorner0[0], colour, Culling.occluderDebugCorner0[1])
				Scene.toolkit!!.drawOpaqueLine(Culling.occluderDebugCorner0[1], Culling.occluderDebugCorner0[0], occluderDebugCorner2[0], colour, occluderDebugCorner2[1])
			}
			j++
		}
		W2DebugMode.font.drawString("Dynamic: " + Scene.interactiveObjectCount + "/" + 5000, -256, 75, 10, -16777216)
		W2DebugMode.font.drawString("Total Opaque Onscreen: " + Scene.onscreenOpaqueCount + "/" + 10000, -256, 90, 10, -16777216)
		W2DebugMode.font.drawString("Total Trans Onscreen: " + Scene.transparentEntityCount + "/" + 5000, -256, 105, 10, -16777216)
		W2DebugMode.font.drawString("Occluders: " + (Culling.groundCullingClusterCount + Culling.tileCullingClusterCount) + " Active: " + Culling.activeOccluderCount, -256, 120, 10, -16777216)
		W2DebugMode.font.drawString("Occluded: Ground:" + Culling.occludedGroundCount + " Walls: " + OccludedWallCount.occludedWallCount + " CPs: " + Sprites.hintHeadId + " Pixels: " + Culling.depthWriteCount, -256, 135, 10, -16777216)
		W2DebugMode.font.drawString("Occlude Calc Took: " + Culling.occludeCalcNanos / 1000L + "us", -256, 150, 10, -16777216)
		if (W2DebugMode.sceneDebugMode != 2 || MipmapTextureSampler.textures == null) {
			return
		}
		@Pc(355) var k: Int = 0
		while (k < MipmapTextureSampler.textures!!.size) {
			@Pc(361) var intensity: Float = MipmapTextureSampler.textures!![k].toFloat()
			intensity /= 4194304.0f
			if (intensity > 1.0f) {
				intensity = 1.0f
			}
			intensity *= 255.0f
			intensity = 255.0f - intensity
			@Pc(382) val grey: Int = intensity.toInt()
			MipmapTextureSampler.textures!![k] = grey or (grey shl 8) or (grey shl 16) or 0xFF000000.toInt()
			k++
		}
		@Pc(410) val sprite: NativeSprite = Scene.toolkit!!.createNativeSprite(Culling.occlusionBufferWidth, MipmapTextureSampler.textures, Culling.occlusionBufferWidth, Culling.occlusionBufferHeight)!!
		sprite.render(10, 170, 1, 0, 0)
	}
}
