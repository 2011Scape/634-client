package com.jagex.game.runetek5

import com.jagex.game.runetek5.buffer.BitPacket
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.sound.player.area.AmbientSound
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object PlayerList {

	@OriginalMember(owner = "client!hr", name = "M", descriptor = "[I")
	@JvmField
	var underlaySaturation: IntArray? = null

	@OriginalMember(owner = "client!ro", name = "a", descriptor = "(IBLclient!ua;)Z")
	@JvmStatic
	fun decodeMovementUpdate(@OriginalArg(0) playerIndex: Int, @OriginalArg(2) bitPacket: BitPacket?): Boolean {
		@Pc(8) val updateType: Int = bitPacket!!.gbit(2)
		@Pc(28) var local28: Int
		@Pc(33) var local33: Int
		@Pc(102) var local102: Int
		@Pc(106) var local106: Int
		@Pc(112) var local112: Int
		if (updateType == LowResolutionPlayerUpdate.TRANSITION) {
			if (bitPacket.gbit(1) != 0) {
				decodeMovementUpdate(playerIndex, bitPacket)
			}
			val localX: Int = bitPacket.gbit(6)
			val localY: Int = bitPacket.gbit(6)
			@Pc(45) val hasUpdateMask: Boolean = bitPacket.gbit(1) == 1
			if (hasUpdateMask) {
				PlayerUpdate.UPDATE_MASK_PLAYER_INDICES[PlayerUpdate.numUpdateMaskPlayers++] = playerIndex
			}
			if (PlayerUpdate.players[playerIndex] != null) {
				throw RuntimeException("hr:lr")
			}
			@Pc(68) val update: PlayerUpdateEntry? = PlayerUpdate.PLAYER_UPDATES[playerIndex]
			@Pc(76) val playerEntity: PlayerEntity = PlayerEntity()
			PlayerUpdate.players[playerIndex] = playerEntity
			playerEntity.playerIndex = playerIndex
			if (PlayerUpdate.APPEARANCE_BUFFERS[playerIndex] != null) {
				playerEntity.decodeAppearance(PlayerUpdate.APPEARANCE_BUFFERS[playerIndex])
			}
			playerEntity.turn(update!!.rotation, true)
			playerEntity.faceEntity = update.faceEntity
			val packedCoordinates: Int = update.packedCoordinates
			val level: Int = packedCoordinates shr 28
			val regionX: Int = (packedCoordinates shr 14) and 0xFF
			@Pc(116) val regionY: Int = packedCoordinates and 0xFF
			@Pc(125) val baseX: Int = localX + (regionX shl 6) - MapRegion.sceneBaseX
			playerEntity.pMapdot = update.pMapdot
			@Pc(138) val baseY: Int = localY + (regionY shl 6) - MapRegion.sceneBaseY
			playerEntity.movementQueueSpeed[0] = PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex]
			playerEntity.virtualLevel = level.toByte()
			playerEntity.level = playerEntity.virtualLevel
			if (SettingsBits.isBridge(baseY, baseX)) {
				playerEntity.virtualLevel++
			}
			playerEntity.move(baseY, baseX)
			playerEntity.hasPendingPositionUpdate = false
			PlayerUpdate.PLAYER_UPDATES[playerIndex] = null
			return true
		} else if (updateType == LowResolutionPlayerUpdate.CHANGE_LEVEL) {
			val levelDelta: Int = bitPacket.gbit(2)
			val packedCoordinates: Int = PlayerUpdate.PLAYER_UPDATES[playerIndex]!!.packedCoordinates
			PlayerUpdate.PLAYER_UPDATES[playerIndex]!!.packedCoordinates = (packedCoordinates and 0xFFFFFFF) + ((((packedCoordinates shr 28) + levelDelta) and 0x3) shl 28)
			return false
		} else {
			@Pc(228) var local228: Int
			@Pc(233) var local233: Int
			@Pc(241) var local241: Int
			if (updateType != 2) {
				val packedRegionDelta: Int = bitPacket.gbit(18)
				val levelDelta: Int = packedRegionDelta shr 16
				val regionDeltaX: Int = (packedRegionDelta shr 8) and 0xFF
				val regionDeltaY: Int = packedRegionDelta and 0xFF
				val packedCoordinates: Int = PlayerUpdate.PLAYER_UPDATES[playerIndex]!!.packedCoordinates
				val newLevel: Int = ((packedCoordinates shr 28) + levelDelta) and 0x3
				val newRegionX: Int = (regionDeltaX + (packedCoordinates shr 14)) and 0xFF
				val newRegionY: Int = (packedCoordinates + regionDeltaY) and 0xFF
				PlayerUpdate.PLAYER_UPDATES[playerIndex]!!.packedCoordinates = newRegionY + (newLevel shl 28) + (newRegionX shl 14)
				return false
			}
			val packedDirection: Int = bitPacket.gbit(5)
			val levelDelta: Int = packedDirection shr 3
			val direction: Int = packedDirection and 0x7
			val packedCoordinates: Int = PlayerUpdate.PLAYER_UPDATES[playerIndex]!!.packedCoordinates
			val newLevel: Int = (levelDelta + (packedCoordinates shr 28)) and 0x3
			var newRegionX: Int = (packedCoordinates shr 14) and 0xFF
			var newRegionY: Int = packedCoordinates and 0xFF
			if (direction == 0) {
				newRegionX--
				newRegionY--
			}
			if (direction == 1) {
				newRegionY--
			}
			if (direction == 2) {
				newRegionY--
				newRegionX++
			}
			if (direction == 3) {
				newRegionX--
			}
			if (direction == 4) {
				newRegionX++
			}
			if (direction == 5) {
				newRegionX--
				newRegionY++
			}
			if (direction == 6) {
				newRegionY++
			}
			if (direction == 7) {
				newRegionX++
				newRegionY++
			}
			PlayerUpdate.PLAYER_UPDATES[playerIndex]!!.packedCoordinates = newRegionY + (newLevel shl 28) + (newRegionX shl 14)
			return false
		}
	}

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(IILclient!ua;)V")
	@JvmStatic
	fun decodePositionUpdate(@OriginalArg(1) playerIndex: Int, @OriginalArg(2) bitPacket: BitPacket?) {
		@Pc(17) val hasUpdateMask: Boolean = bitPacket!!.gbit(1) == 1
		if (hasUpdateMask) {
			PlayerUpdate.UPDATE_MASK_PLAYER_INDICES[PlayerUpdate.numUpdateMaskPlayers++] = playerIndex
		}
		@Pc(32) val updateType: Int = bitPacket.gbit(2)
		@Pc(36) val playerEntity: PlayerEntity? = PlayerUpdate.players[playerIndex]
		if (updateType != 0) {
			@Pc(147) var local147: Int
			@Pc(152) var local152: Int
			@Pc(157) var local157: Int
			if (updateType == HighResolutionPlayerUpdate.WALK) {
				val direction: Int = bitPacket.gbit(3)
				var nextX: Int = playerEntity!!.movementBaseX[0]
				var nextY: Int = playerEntity.movementBaseY[0]
				if (direction == 0) {
					nextY--
					nextX--
				} else if (direction == 1) {
					nextY--
				} else if (direction == 2) {
					nextX++
					nextY--
				} else if (direction == 3) {
					nextX--
				} else if (direction == 4) {
					nextX++
				} else if (direction == 5) {
					nextY++
					nextX--
				} else if (direction == 6) {
					nextY++
				} else if (direction == 7) {
					nextY++
					nextX++
				}
				if (hasUpdateMask) {
					playerEntity.basePositionY = nextY
					playerEntity.hasPendingPositionUpdate = true
					playerEntity.basePositionX = nextX
				} else {
					playerEntity.move(nextY, PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex], nextX)
				}
			} else if (updateType == HighResolutionPlayerUpdate.RUN) {
				val direction: Int = bitPacket.gbit(4)
				var nextX: Int = playerEntity!!.movementBaseX[0]
				var nextY: Int = playerEntity.movementBaseY[0]
				if (direction == 0) {
					nextX -= 2
					nextY -= 2
				} else if (direction == 1) {
					nextY -= 2
					nextX--
				} else if (direction == 2) {
					nextY -= 2
				} else if (direction == 3) {
					nextY -= 2
					nextX++
				} else if (direction == 4) {
					nextX += 2
					nextY -= 2
				} else if (direction == 5) {
					nextX -= 2
					nextY--
				} else if (direction == 6) {
					nextY--
					nextX += 2
				} else if (direction == 7) {
					nextX -= 2
				} else if (direction == 8) {
					nextX += 2
				} else if (direction == 9) {
					nextY++
					nextX -= 2
				} else if (direction == 10) {
					nextY++
					nextX += 2
				} else if (direction == 11) {
					nextY += 2
					nextX -= 2
				} else if (direction == 12) {
					nextY += 2
					nextX--
				} else if (direction == 13) {
					nextY += 2
				} else if (direction == 14) {
					nextY += 2
					nextX++
				} else if (direction == 15) {
					nextX += 2
					nextY += 2
				}
				if (hasUpdateMask) {
					playerEntity.hasPendingPositionUpdate = true
					playerEntity.basePositionX = nextX
					playerEntity.basePositionY = nextY
				} else {
					playerEntity.move(nextY, PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex], nextX)
				}
			} else {
				val teleportType: Int = bitPacket.gbit(1)
				@Pc(427) var deltaX: Int
				@Pc(435) var deltaY: Int
				@Pc(446) val baseX: Int
				@Pc(453) val baseY: Int
				if (teleportType == 0) {
					val packedOffset: Int = bitPacket.gbit(12)
					val levelDelta: Int = packedOffset shr 10
					deltaX = (packedOffset shr 5) and 0x1F
					if (deltaX > 15) {
						deltaX -= 32
					}
					deltaY = packedOffset and 0x1F
					if (deltaY > 15) {
						deltaY -= 32
					}
					baseX = deltaX + playerEntity!!.movementBaseX[0]
					baseY = deltaY + playerEntity.movementBaseY[0]
					if (hasUpdateMask) {
						playerEntity.basePositionY = baseY
						playerEntity.basePositionX = baseX
						playerEntity.hasPendingPositionUpdate = true
					} else {
						playerEntity.move(baseY, PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex], baseX)
					}
					playerEntity.virtualLevel = ((playerEntity.level + levelDelta) and 0x3).toByte()
					playerEntity.level = playerEntity.virtualLevel
					if (SettingsBits.isBridge(baseY, baseX)) {
						playerEntity.virtualLevel++
					}
					if (PlayerUpdate.myPlayerIndex == playerIndex) {
						if (playerEntity.level.toInt() != PlayerUpdate.currentLevel) {
							Scene.staticSceneDirty = true
						}
						PlayerUpdate.currentLevel = playerEntity.level.toInt()
					}
				} else {
					val packedPosition: Int = bitPacket.gbit(30)
					val levelDelta: Int = packedPosition shr 28
					deltaX = (packedPosition shr 14) and 0x3FFF
					deltaY = packedPosition and 0x3FFF
					baseX = ((deltaX + playerEntity!!.movementBaseX[0] + MapRegion.sceneBaseX) and 0x3FFF) - MapRegion.sceneBaseX
					baseY = ((deltaY + MapRegion.sceneBaseY + playerEntity.movementBaseY[0]) and 0x3FFF) - MapRegion.sceneBaseY
					if (hasUpdateMask) {
						playerEntity.hasPendingPositionUpdate = true
						playerEntity.basePositionY = baseY
						playerEntity.basePositionX = baseX
					} else {
						playerEntity.move(baseY, PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex], baseX)
					}
					playerEntity.virtualLevel = ((playerEntity.level + levelDelta) and 0x3).toByte()
					playerEntity.level = playerEntity.virtualLevel
					if (SettingsBits.isBridge(baseY, baseX)) {
						playerEntity.virtualLevel++
					}
					if (playerIndex == PlayerUpdate.myPlayerIndex) {
						PlayerUpdate.currentLevel = playerEntity.level.toInt()
					}
				}
			}
		} else if (hasUpdateMask) {
			playerEntity!!.hasPendingPositionUpdate = false
		} else if (playerIndex == PlayerUpdate.myPlayerIndex) {
			throw RuntimeException("s:lr")
		} else {
			@Pc(64) val update: PlayerUpdateEntry = PlayerUpdateEntry()
			PlayerUpdate.PLAYER_UPDATES[playerIndex] = update
			update.packedCoordinates = ((MapRegion.sceneBaseY + playerEntity!!.movementBaseY[0]) shr 6) + (((playerEntity.movementBaseX[0] + MapRegion.sceneBaseX) shr 6) shl 14) + (playerEntity.level.toInt() shl 28)
			if (playerEntity.faceDirection == -1) {
				update.rotation = playerEntity.yaw.getRotation()
			} else {
				update.rotation = playerEntity.faceDirection
			}
			update.pMapdot = playerEntity.pMapdot
			update.faceEntity = playerEntity.faceEntity
			if (playerEntity.isNpc > 0) {
				AmbientSound.unlinkPlayerSound(playerEntity)
			}
			PlayerUpdate.players[playerIndex] = null
			if (bitPacket.gbit(1) != 0) {
				PlayerList.decodeMovementUpdate(playerIndex, bitPacket)
			}
		}
	}
}
