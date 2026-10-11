package com.jagex.game.runetek5;

import com.jagex.game.runetek5.buffer.BitPacket;
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationTypeList;
import com.jagex.game.runetek5.statics.OutPlayerIndexCount;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;

public final class PlayerInfoBitDecoder {

	@OriginalMember(owner = "client!qha", name = "a", descriptor = "(ILclient!ua;)V")
	public static void decodeUpdate(@OriginalArg(1) BitPacket bitPacket) {
		@Pc(7) int skipCount = 0;
		bitPacket.enterBitMode();
		@Pc(18) int local18;
		@Pc(46) int local46;
		for (@Pc(12) int i = 0; i < OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES; i++) {
			int playerIndex = PlayerUpdate.OUT_PLAYER_INDICES[i];
			if ((PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] & 0x1) == 0) {
				if (skipCount > 0) {
					PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (byte) (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] | 0x2);
					skipCount--;
				} else {
					int hasUpdate = bitPacket.gbit(1);
					if (hasUpdate == 0) {
						skipCount = PlayerInfoBitDecoder.decodeSkipCount(bitPacket);
						PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (byte) (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] | 0x2);
					} else {
						PlayerList.decodePositionUpdate(playerIndex, bitPacket);
					}
				}
			}
		}
		bitPacket.exitBitMode();
		if (skipCount != 0) {
			throw new RuntimeException("nsn0");
		}
		bitPacket.enterBitMode();
		@Pc(130) int local130;
		for (int listIndex = 0; listIndex < OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES; listIndex++) {
			int playerIndex = PlayerUpdate.OUT_PLAYER_INDICES[listIndex];
			if ((PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] & 0x1) != 0) {
				if (skipCount > 0) {
					PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (byte) (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] | 0x2);
					skipCount--;
				} else {
					int hasUpdate = bitPacket.gbit(1);
					if (hasUpdate == 0) {
						skipCount = PlayerInfoBitDecoder.decodeSkipCount(bitPacket);
						PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (byte) (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] | 0x2);
					} else {
						PlayerList.decodePositionUpdate(playerIndex, bitPacket);
					}
				}
			}
		}
		bitPacket.exitBitMode();
		if (skipCount != 0) {
			throw new RuntimeException("nsn1");
		}
		bitPacket.enterBitMode();
		@Pc(208) int local208;
		for (int listIndex = 0; listIndex < WorldMap.numInactivePlayerIndices; listIndex++) {
			int playerIndex = PlayerUpdate.INACTIVE_PLAYER_INDICES[listIndex];
			if ((PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] & 0x1) != 0) {
				if (skipCount > 0) {
					PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (byte) (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] | 0x2);
					skipCount--;
				} else {
					int hasUpdate = bitPacket.gbit(1);
					if (hasUpdate == 0) {
						skipCount = PlayerInfoBitDecoder.decodeSkipCount(bitPacket);
						PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (byte) (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] | 0x2);
					} else if (PlayerList.decodeMovementUpdate(playerIndex, bitPacket)) {
						PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (byte) (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] | 0x2);
					}
				}
			}
		}
		bitPacket.exitBitMode();
		if (skipCount != 0) {
			throw new RuntimeException("nsn2");
		}
		bitPacket.enterBitMode();
		for (int listIndex = 0; listIndex < WorldMap.numInactivePlayerIndices; listIndex++) {
			int playerIndex = PlayerUpdate.INACTIVE_PLAYER_INDICES[listIndex];
			if ((PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] & 0x1) == 0) {
				if (skipCount > 0) {
					PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (byte) (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] | 0x2);
					skipCount--;
				} else {
					@Pc(299) int hasUpdate = bitPacket.gbit(1);
					if (hasUpdate == 0) {
						skipCount = PlayerInfoBitDecoder.decodeSkipCount(bitPacket);
						PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (byte) (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] | 0x2);
					} else if (PlayerList.decodeMovementUpdate(playerIndex, bitPacket)) {
						PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (byte) (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] | 0x2);
					}
				}
			}
		}
		bitPacket.exitBitMode();
		if (skipCount != 0) {
			throw new RuntimeException("nsn3");
		}
		OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES = 0;
		WorldMap.numInactivePlayerIndices = 0;
		for (int playerIndex = 1; playerIndex < 2048; playerIndex++) {
			PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (byte) (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] >> 1);
			@Pc(374) PlayerEntity playerEntity = PlayerUpdate.players[playerIndex];
			if (playerEntity == null) {
				PlayerUpdate.INACTIVE_PLAYER_INDICES[WorldMap.numInactivePlayerIndices++] = playerIndex;
			} else {
				PlayerUpdate.OUT_PLAYER_INDICES[OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES++] = playerIndex;
			}
		}
	}
	@OriginalMember(owner = "client!jb", name = "a", descriptor = "(ILclient!ua;)I")
	public static int decodeSkipCount(@OriginalArg(1) BitPacket bitPacket) {
		@Pc(10) int skipType = bitPacket.gbit(2);
		@Pc(25) int skipCount;
		if (skipType == 0) {
			skipCount = 0;
		} else if (skipType == 1) {
			skipCount = bitPacket.gbit(5);
		} else if (skipType == 2) {
			skipCount = bitPacket.gbit(8);
		} else {
			skipCount = bitPacket.gbit(11);
		}
		return skipCount;
	}
}
