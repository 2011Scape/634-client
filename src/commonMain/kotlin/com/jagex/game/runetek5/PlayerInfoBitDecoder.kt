package com.jagex.game.runetek5

import com.jagex.game.runetek5.buffer.BitPacket
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.statics.OutPlayerIndexCount
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

object PlayerInfoBitDecoder {

	@OriginalMember(owner = "client!qha", name = "a", descriptor = "(ILclient!ua;)V")
	@JvmStatic
	fun decodeUpdate(@OriginalArg(1) bitPacket: BitPacket?) {
		@Pc(7) var skipCount: Int = 0
		bitPacket!!.enterBitMode()
		@Pc(18) var local18: Int
		@Pc(46) var local46: Int
		@Pc(12) var i: Int = 0
		while (i < OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES) {
			val playerIndex: Int = PlayerUpdate.OUT_PLAYER_INDICES[i]
			if ((PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() and 0x1) == 0) {
				if (skipCount > 0) {
					PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() or 0x2).toByte()
					skipCount--
				} else {
					val hasUpdate: Int = bitPacket.gbit(1)
					if (hasUpdate == 0) {
						skipCount = PlayerInfoBitDecoder.decodeSkipCount(bitPacket)
						PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() or 0x2).toByte()
					} else {
						PlayerList.decodePositionUpdate(playerIndex, bitPacket)
					}
				}
			}
			i++
		}
		bitPacket.exitBitMode()
		if (skipCount != 0) {
			throw RuntimeException("nsn0")
		}
		bitPacket.enterBitMode()
		@Pc(130) var local130: Int
		var listIndex: Int = 0
		while (listIndex < OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES) {
			val playerIndex: Int = PlayerUpdate.OUT_PLAYER_INDICES[listIndex]
			if ((PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() and 0x1) != 0) {
				if (skipCount > 0) {
					PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() or 0x2).toByte()
					skipCount--
				} else {
					val hasUpdate: Int = bitPacket.gbit(1)
					if (hasUpdate == 0) {
						skipCount = PlayerInfoBitDecoder.decodeSkipCount(bitPacket)
						PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() or 0x2).toByte()
					} else {
						PlayerList.decodePositionUpdate(playerIndex, bitPacket)
					}
				}
			}
			listIndex++
		}
		bitPacket.exitBitMode()
		if (skipCount != 0) {
			throw RuntimeException("nsn1")
		}
		bitPacket.enterBitMode()
		@Pc(208) var local208: Int
		listIndex = 0
		while (listIndex < WorldMap.numInactivePlayerIndices) {
			val playerIndex: Int = PlayerUpdate.INACTIVE_PLAYER_INDICES[listIndex]
			if ((PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() and 0x1) != 0) {
				if (skipCount > 0) {
					PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() or 0x2).toByte()
					skipCount--
				} else {
					val hasUpdate: Int = bitPacket.gbit(1)
					if (hasUpdate == 0) {
						skipCount = PlayerInfoBitDecoder.decodeSkipCount(bitPacket)
						PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() or 0x2).toByte()
					} else if (PlayerList.decodeMovementUpdate(playerIndex, bitPacket)) {
						PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() or 0x2).toByte()
					}
				}
			}
			listIndex++
		}
		bitPacket.exitBitMode()
		if (skipCount != 0) {
			throw RuntimeException("nsn2")
		}
		bitPacket.enterBitMode()
		listIndex = 0
		while (listIndex < WorldMap.numInactivePlayerIndices) {
			val playerIndex: Int = PlayerUpdate.INACTIVE_PLAYER_INDICES[listIndex]
			if ((PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() and 0x1) == 0) {
				if (skipCount > 0) {
					PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() or 0x2).toByte()
					skipCount--
				} else {
					@Pc(299) val hasUpdate: Int = bitPacket.gbit(1)
					if (hasUpdate == 0) {
						skipCount = PlayerInfoBitDecoder.decodeSkipCount(bitPacket)
						PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() or 0x2).toByte()
					} else if (PlayerList.decodeMovementUpdate(playerIndex, bitPacket)) {
						PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() or 0x2).toByte()
					}
				}
			}
			listIndex++
		}
		bitPacket.exitBitMode()
		if (skipCount != 0) {
			throw RuntimeException("nsn3")
		}
		OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES = 0
		WorldMap.numInactivePlayerIndices = 0
		var playerIndex: Int = 1
		while (playerIndex < 2048) {
			PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex] = (PlayerUpdate.OUT_PLAYER_UPDATE_STATE[playerIndex].toInt() shr 1).toByte()
			@Pc(374) val playerEntity: PlayerEntity? = PlayerUpdate.players[playerIndex]
			if (playerEntity == null) {
				PlayerUpdate.INACTIVE_PLAYER_INDICES[WorldMap.numInactivePlayerIndices++] = playerIndex
			} else {
				PlayerUpdate.OUT_PLAYER_INDICES[OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES++] = playerIndex
			}
			playerIndex++
		}
	}

	@OriginalMember(owner = "client!jb", name = "a", descriptor = "(ILclient!ua;)I")
	@JvmStatic
	fun decodeSkipCount(@OriginalArg(1) bitPacket: BitPacket?): Int {
		@Pc(10) val skipType: Int = bitPacket!!.gbit(2)
		@Pc(25) val skipCount: Int
		if (skipType == 0) {
			skipCount = 0
		} else if (skipType == 1) {
			skipCount = bitPacket.gbit(5)
		} else if (skipType == 2) {
			skipCount = bitPacket.gbit(8)
		} else {
			skipCount = bitPacket.gbit(11)
		}
		return skipCount
	}
}
