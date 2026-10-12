package com.jagex.game.runetek5.graphics

import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity
import com.jagex.game.runetek5.entity.InteractiveEntityContainer
import com.jagex.game.runetek5.scene.tile.Tile
import com.jagex.game.runetek5.entity.Wall
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.culling.Culling

object TileShadowLightSharing {

	@OriginalMember(owner = "client!jc", name = "a", descriptor = "(Lclient!ga;IIIII)V")
	@JvmStatic
	fun castShadow(@OriginalArg(0) entity: Entity?, @OriginalArg(1) basePlane: Int, @OriginalArg(2) startX: Int, @OriginalArg(3) startZ: Int, @OriginalArg(4) width: Int, @OriginalArg(5) length: Int) {
		@Pc(1) var isSamePlane: Boolean = true
		@Pc(3) var lowestX: Int = startX
		@Pc(7) val highestX: Int = startX + width
		@Pc(11) val lowestZ: Int = startZ - 1
		@Pc(15) val highestZ: Int = startZ + length
		@Pc(17) var i: Int = basePlane
		while (i <= basePlane + 1) {
			if (i != Scene.length) {
				@Pc(23) var j: Int = lowestX
				while (j <= highestX) {
					if (j >= 0 && j < Scene.sceneWidth) {
						@Pc(32) var k: Int = lowestZ
						while (k <= highestZ) {
							if (k >= 0 && k < Camera.sceneLength && (!isSamePlane || j >= highestX || k >= highestZ || k < startZ && j != startX)) {
								@Pc(62) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![i]!![j]!![k]
								if (tile != null) {
									@Pc(148) val heightDelta: Int = (Scene.aClass65Array1!![i]!!.getHeight(k, j) + Scene.aClass65Array1!![i]!!.getHeight(k, j + 1) + Scene.aClass65Array1!![i]!!.getHeight(k + 1, j) + Scene.aClass65Array1!![i]!!.getHeight(k + 1, j + 1)) / 4 - (Scene.aClass65Array1!![basePlane]!!.getHeight(startZ, startX) + Scene.aClass65Array1!![basePlane]!!.getHeight(startZ, startX + 1) + Scene.aClass65Array1!![basePlane]!!.getHeight(startZ + 1, startX) + Scene.aClass65Array1!![basePlane]!!.getHeight(startZ + 1, startX + 1)) / 4
									@Pc(151) val wall: Wall? = tile.primaryWall
									@Pc(154) val wall2: Wall? = tile.secondaryWall
									if (wall != null && wall.hasTileShadow()) {
										entity!!.shareLight(Scene.toolkit, heightDelta, isSamePlane, wall, (k - startZ) * Culling.fullTileDimension + (1 - length) * Scene.halfTileOffset, (j - startX) * Culling.fullTileDimension + (1 - width) * Scene.halfTileOffset)
									}
									if (wall2 != null && wall2.hasTileShadow()) {
										entity!!.shareLight(Scene.toolkit, heightDelta, isSamePlane, wall2, (k - startZ) * Culling.fullTileDimension + (1 - length) * Scene.halfTileOffset, (j - startX) * Culling.fullTileDimension + (1 - width) * Scene.halfTileOffset)
									}
									@Pc(227) var positionEntityNode: InteractiveEntityContainer? = tile.interactives
									while (positionEntityNode != null) {
										@Pc(231) val positionEntity: AbstractInteractiveEntity? = positionEntityNode.interactiveObject
										if (positionEntity != null && positionEntity.hasTileShadow() && (j == positionEntity.startX.toInt() || j == lowestX) && (k == positionEntity.startZ.toInt() || k == lowestZ)) {
											@Pc(260) val sizeX: Int = positionEntity.endX + 1 - positionEntity.startX
											@Pc(268) val sizeZ: Int = positionEntity.endZ + 1 - positionEntity.startZ
											entity!!.shareLight(Scene.toolkit, heightDelta, isSamePlane, positionEntity, (positionEntity.startZ - startZ) * Culling.fullTileDimension + (sizeZ - length) * Scene.halfTileOffset, (positionEntity.startX - startX) * Culling.fullTileDimension + (sizeX - width) * Scene.halfTileOffset)
										}
										positionEntityNode = positionEntityNode.child
									}
								}
							}
							k++
						}
					}
					j++
				}
				lowestX--
				isSamePlane = false
			}
			i++
		}
	}
}
