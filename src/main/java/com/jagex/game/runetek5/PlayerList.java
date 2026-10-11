package com.jagex.game.runetek5;

import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.buffer.BitPacket;
import com.jagex.game.runetek5.UnloadedLocCount;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.sound.player.area.AmbientSound;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.scene.SettingsBits;
import com.jagex.game.runetek5.client.ClientState;

public final class PlayerList {

	@OriginalMember(owner = "client!hr", name = "M", descriptor = "[I")
	public static int[] underlaySaturation;

	@OriginalMember(owner = "client!ro", name = "a", descriptor = "(IBLclient!ua;)Z")
	public static boolean decodeMovementUpdate(@OriginalArg(0) int playerIndex, @OriginalArg(2) BitPacket bitPacket) {
		@Pc(8) int updateType = bitPacket.gbit(2);
		@Pc(28) int local28;
		@Pc(33) int local33;
		@Pc(102) int local102;
		@Pc(106) int local106;
		@Pc(112) int local112;
		if (updateType == LowResolutionPlayerUpdate.TRANSITION) {
			if (bitPacket.gbit(1) != 0) {
				decodeMovementUpdate(playerIndex, bitPacket);
			}
			int localX = bitPacket.gbit(6);
			int localY = bitPacket.gbit(6);
			@Pc(45) boolean hasUpdateMask = bitPacket.gbit(1) == 1;
			if (hasUpdateMask) {
				PlayerUpdate.UPDATE_MASK_PLAYER_INDICES[PlayerUpdate.numUpdateMaskPlayers++] = playerIndex;
			}
			if (PlayerUpdate.players[playerIndex] != null) {
				throw new RuntimeException("hr:lr");
			}
			@Pc(68) PlayerUpdateEntry update = PlayerUpdate.PLAYER_UPDATES[playerIndex];
			@Pc(76) PlayerEntity playerEntity = PlayerUpdate.players[playerIndex] = new PlayerEntity();
			playerEntity.playerIndex = playerIndex;
			if (PlayerUpdate.APPEARANCE_BUFFERS[playerIndex] != null) {
				playerEntity.decodeAppearance(PlayerUpdate.APPEARANCE_BUFFERS[playerIndex]);
			}
			playerEntity.turn(update.rotation, true);
			playerEntity.faceEntity = update.faceEntity;
			int packedCoordinates = update.packedCoordinates;
			int level = packedCoordinates >> 28;
			int regionX = packedCoordinates >> 14 & 0xFF;
			@Pc(116) int regionY = packedCoordinates & 0xFF;
			@Pc(125) int baseX = localX + (regionX << 6) - MapRegion.sceneBaseX;
			playerEntity.pMapdot = update.pMapdot;
			@Pc(138) int baseY = localY + (regionY << 6) - MapRegion.sceneBaseY;
			playerEntity.movementQueueSpeed[0] = PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex];
			playerEntity.level = playerEntity.virtualLevel = (byte) level;
			if (SettingsBits.isBridge(baseY, baseX)) {
				playerEntity.virtualLevel++;
			}
			playerEntity.move(baseY, baseX);
			playerEntity.hasPendingPositionUpdate = false;
			PlayerUpdate.PLAYER_UPDATES[playerIndex] = null;
			return true;
		} else if (updateType == LowResolutionPlayerUpdate.CHANGE_LEVEL) {
			int levelDelta = bitPacket.gbit(2);
			int packedCoordinates = PlayerUpdate.PLAYER_UPDATES[playerIndex].packedCoordinates;
			PlayerUpdate.PLAYER_UPDATES[playerIndex].packedCoordinates = (packedCoordinates & 0xFFFFFFF) + (((packedCoordinates >> 28) + levelDelta & 0x3) << 28);
			return false;
		} else {
			@Pc(228) int local228;
			@Pc(233) int local233;
			@Pc(241) int local241;
			if (updateType != 2) {
				int packedRegionDelta = bitPacket.gbit(18);
				int levelDelta = packedRegionDelta >> 16;
				int regionDeltaX = packedRegionDelta >> 8 & 0xFF;
				int regionDeltaY = packedRegionDelta & 0xFF;
				int packedCoordinates = PlayerUpdate.PLAYER_UPDATES[playerIndex].packedCoordinates;
				int newLevel = (packedCoordinates >> 28) + levelDelta & 0x3;
				int newRegionX = regionDeltaX + (packedCoordinates >> 14) & 0xFF;
				int newRegionY = packedCoordinates + regionDeltaY & 0xFF;
				PlayerUpdate.PLAYER_UPDATES[playerIndex].packedCoordinates = newRegionY + (newLevel << 28) + (newRegionX << 14);
				return false;
			}
			int packedDirection = bitPacket.gbit(5);
			int levelDelta = packedDirection >> 3;
			int direction = packedDirection & 0x7;
			int packedCoordinates = PlayerUpdate.PLAYER_UPDATES[playerIndex].packedCoordinates;
			int newLevel = levelDelta + (packedCoordinates >> 28) & 0x3;
			int newRegionX = packedCoordinates >> 14 & 0xFF;
			int newRegionY = packedCoordinates & 0xFF;
			if (direction == 0) {
				newRegionX--;
				newRegionY--;
			}
			if (direction == 1) {
				newRegionY--;
			}
			if (direction == 2) {
				newRegionY--;
				newRegionX++;
			}
			if (direction == 3) {
				newRegionX--;
			}
			if (direction == 4) {
				newRegionX++;
			}
			if (direction == 5) {
				newRegionX--;
				newRegionY++;
			}
			if (direction == 6) {
				newRegionY++;
			}
			if (direction == 7) {
				newRegionX++;
				newRegionY++;
			}
			PlayerUpdate.PLAYER_UPDATES[playerIndex].packedCoordinates = newRegionY + (newLevel << 28) + (newRegionX << 14);
			return false;
		}
	}

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(IILclient!ua;)V")
	public static void decodePositionUpdate(@OriginalArg(1) int playerIndex, @OriginalArg(2) BitPacket bitPacket) {
		@Pc(17) boolean hasUpdateMask = bitPacket.gbit(1) == 1;
		if (hasUpdateMask) {
			PlayerUpdate.UPDATE_MASK_PLAYER_INDICES[PlayerUpdate.numUpdateMaskPlayers++] = playerIndex;
		}
		@Pc(32) int updateType = bitPacket.gbit(2);
		@Pc(36) PlayerEntity playerEntity = PlayerUpdate.players[playerIndex];
		if (updateType != 0) {
			@Pc(147) int local147;
			@Pc(152) int local152;
			@Pc(157) int local157;
			if (updateType == HighResolutionPlayerUpdate.WALK) {
				int direction = bitPacket.gbit(3);
				int nextX = playerEntity.movementBaseX[0];
				int nextY = playerEntity.movementBaseY[0];
				if (direction == 0) {
					nextY--;
					nextX--;
				} else if (direction == 1) {
					nextY--;
				} else if (direction == 2) {
					nextX++;
					nextY--;
				} else if (direction == 3) {
					nextX--;
				} else if (direction == 4) {
					nextX++;
				} else if (direction == 5) {
					nextY++;
					nextX--;
				} else if (direction == 6) {
					nextY++;
				} else if (direction == 7) {
					nextY++;
					nextX++;
				}
				if (hasUpdateMask) {
					playerEntity.basePositionY = nextY;
					playerEntity.hasPendingPositionUpdate = true;
					playerEntity.basePositionX = nextX;
				} else {
					playerEntity.move(nextY, PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex], nextX);
				}
			} else if (updateType == HighResolutionPlayerUpdate.RUN) {
				int direction = bitPacket.gbit(4);
				int nextX = playerEntity.movementBaseX[0];
				int nextY = playerEntity.movementBaseY[0];
				if (direction == 0) {
					nextX -= 2;
					nextY -= 2;
				} else if (direction == 1) {
					nextY -= 2;
					nextX--;
				} else if (direction == 2) {
					nextY -= 2;
				} else if (direction == 3) {
					nextY -= 2;
					nextX++;
				} else if (direction == 4) {
					nextX += 2;
					nextY -= 2;
				} else if (direction == 5) {
					nextX -= 2;
					nextY--;
				} else if (direction == 6) {
					nextY--;
					nextX += 2;
				} else if (direction == 7) {
					nextX -= 2;
				} else if (direction == 8) {
					nextX += 2;
				} else if (direction == 9) {
					nextY++;
					nextX -= 2;
				} else if (direction == 10) {
					nextY++;
					nextX += 2;
				} else if (direction == 11) {
					nextY += 2;
					nextX -= 2;
				} else if (direction == 12) {
					nextY += 2;
					nextX--;
				} else if (direction == 13) {
					nextY += 2;
				} else if (direction == 14) {
					nextY += 2;
					nextX++;
				} else if (direction == 15) {
					nextX += 2;
					nextY += 2;
				}
				if (hasUpdateMask) {
					playerEntity.hasPendingPositionUpdate = true;
					playerEntity.basePositionX = nextX;
					playerEntity.basePositionY = nextY;
				} else {
					playerEntity.move(nextY, PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex], nextX);
				}
			} else {
				int teleportType = bitPacket.gbit(1);
				@Pc(427) int deltaX;
				@Pc(435) int deltaY;
				@Pc(446) int baseX;
				@Pc(453) int baseY;
				if (teleportType == 0) {
					int packedOffset = bitPacket.gbit(12);
					int levelDelta = packedOffset >> 10;
					deltaX = packedOffset >> 5 & 0x1F;
					if (deltaX > 15) {
						deltaX -= 32;
					}
					deltaY = packedOffset & 0x1F;
					if (deltaY > 15) {
						deltaY -= 32;
					}
					baseX = deltaX + playerEntity.movementBaseX[0];
					baseY = deltaY + playerEntity.movementBaseY[0];
					if (hasUpdateMask) {
						playerEntity.basePositionY = baseY;
						playerEntity.basePositionX = baseX;
						playerEntity.hasPendingPositionUpdate = true;
					} else {
						playerEntity.move(baseY, PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex], baseX);
					}
					playerEntity.level = playerEntity.virtualLevel = (byte) (playerEntity.level + levelDelta & 0x3);
					if (SettingsBits.isBridge(baseY, baseX)) {
						playerEntity.virtualLevel++;
					}
					if (PlayerUpdate.myPlayerIndex == playerIndex) {
						if (playerEntity.level != PlayerUpdate.currentLevel) {
							Scene.staticSceneDirty = true;
						}
						PlayerUpdate.currentLevel = playerEntity.level;
					}
				} else {
					int packedPosition = bitPacket.gbit(30);
					int levelDelta = packedPosition >> 28;
					deltaX = packedPosition >> 14 & 0x3FFF;
					deltaY = packedPosition & 0x3FFF;
					baseX = (deltaX + playerEntity.movementBaseX[0] + MapRegion.sceneBaseX & 0x3FFF) - MapRegion.sceneBaseX;
					baseY = (deltaY + MapRegion.sceneBaseY + playerEntity.movementBaseY[0] & 0x3FFF) - MapRegion.sceneBaseY;
					if (hasUpdateMask) {
						playerEntity.hasPendingPositionUpdate = true;
						playerEntity.basePositionY = baseY;
						playerEntity.basePositionX = baseX;
					} else {
						playerEntity.move(baseY, PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex], baseX);
					}
					playerEntity.level = playerEntity.virtualLevel = (byte) (playerEntity.level + levelDelta & 0x3);
					if (SettingsBits.isBridge(baseY, baseX)) {
						playerEntity.virtualLevel++;
					}
					if (playerIndex == PlayerUpdate.myPlayerIndex) {
						PlayerUpdate.currentLevel = playerEntity.level;
					}
				}
			}
		} else if (hasUpdateMask) {
			playerEntity.hasPendingPositionUpdate = false;
		} else if (playerIndex == PlayerUpdate.myPlayerIndex) {
			throw new RuntimeException("s:lr");
		} else {
			@Pc(64) PlayerUpdateEntry update = PlayerUpdate.PLAYER_UPDATES[playerIndex] = new PlayerUpdateEntry();
			update.packedCoordinates = (MapRegion.sceneBaseY + playerEntity.movementBaseY[0] >> 6) + (playerEntity.movementBaseX[0] + MapRegion.sceneBaseX >> 6 << 14) + (playerEntity.level << 28);
			if (playerEntity.faceDirection == -1) {
				update.rotation = playerEntity.yaw.getRotation();
			} else {
				update.rotation = playerEntity.faceDirection;
			}
			update.pMapdot = playerEntity.pMapdot;
			update.faceEntity = playerEntity.faceEntity;
			if (playerEntity.isNpc > 0) {
				AmbientSound.unlinkPlayerSound(playerEntity);
			}
			PlayerUpdate.players[playerIndex] = null;
			if (bitPacket.gbit(1) != 0) {
				PlayerList.decodeMovementUpdate(playerIndex, bitPacket);
			}
		}
	}
}
