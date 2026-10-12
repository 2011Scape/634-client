package com.jagex.game.runetek5.entity.pathingentity.player

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.entity.PathingEntity
import com.jagex.game.runetek5.statics.OutPlayerIndexCount
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.PlayerUpdateEntry
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.buffer.BitPacket
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.sequence.PathingEntitySequence
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.entity.PlayerEntity
import org.openrs2.deob.annotation.OriginalArg
import com.jagex.game.runetek5.PlayerInfoBitDecoder
import com.jagex.game.runetek5.ChatLineWriter
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.SoundManager
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.client.ClientState

object PlayerUpdate {
	@OriginalMember(owner = "client!jt", name = "n", descriptor = "[I")
	@JvmField
	val INACTIVE_PLAYER_INDICES: IntArray = IntArray(2048)

	@OriginalMember(owner = "client!to", name = "vb", descriptor = "I")
	@JvmField
	var MY_PLAYER_PLANE: Int = -1

	@OriginalMember(owner = "client!fba", name = "u", descriptor = "[B")
	@JvmField
	val OUT_PLAYER_UPDATE_STATE: ByteArray = ByteArray(2048)

	@OriginalMember(owner = "client!ug", name = "I", descriptor = "I")
	@JvmField
	var myPlayerIndex: Int = -1

	@OriginalMember(owner = "client!wea", name = "g", descriptor = "Ljava/lang/String;")
	@JvmField
	var javaVendor: String? = null

	@OriginalMember(owner = "client!tn", name = "r", descriptor = "[I")
	@JvmField
	val OUT_PLAYER_INDICES: IntArray = IntArray(2048)

	@OriginalMember(owner = "client!client", name = "c", descriptor = "()V")
	@JvmStatic
	fun positionMisalignedEntities() {
		@Pc(1) val playerCount: Int = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES
		@Pc(3) val outPlayerIndices: IntArray = PlayerUpdate.OUT_PLAYER_INDICES
		@Pc(11) val entityCount: Int = if (NPCList.hideNpcs) playerCount else playerCount + NPCList.NPC_UPDATE_INDEX
		@Pc(13) var i: Int = 0
		while (i < entityCount) {
			@Pc(23) val pathingEntity: PathingEntity?
			if (i < playerCount) {
				pathingEntity = PlayerUpdate.players[outPlayerIndices[i]]
			} else {
				pathingEntity = (NPCList.NPC_MAP.get(NPCList.NPC_UPDATE_INDICES[i - playerCount].toLong()) as NPCEntityNode?)!!.npc
			}
			if (pathingEntity!!.worldPriority >= 0) {
				@Pc(43) val size: Int = pathingEntity.getSize()
				if ((size and 0x1) == 0) {
					if ((pathingEntity.x and 0x1FF) == 0 && (pathingEntity.z and 0x1FF) == 0) {
						i++
						continue
					}
				} else if ((pathingEntity.x and 0x1FF) == 256 && (pathingEntity.z and 0x1FF) == 256) {
					i++
					continue
				}
				pathingEntity.y = Scene.tileHeightForAbsolutePosition(pathingEntity.x, pathingEntity.level.toInt(), pathingEntity.z)
				Scene.addInteractive(pathingEntity, true)
			}
			i++
		}
	}

	@OriginalMember(owner = "client!jf", name = "C", descriptor = "[Lclient!f;")
	@JvmField
	val PLAYER_UPDATES: Array<PlayerUpdateEntry?> = arrayOfNulls<PlayerUpdateEntry>(2048)

	@OriginalMember(owner = "client!mk", name = "t", descriptor = "[Lclient!wo;")
	@JvmField
	val HINT_ARROWS: Array<HintArrow?> = arrayOfNulls<HintArrow>(8)

	@OriginalMember(owner = "client!oo", name = "p", descriptor = "[Lclient!cea;")
	@JvmField
	val APPEARANCE_BUFFERS: Array<Packet?> = arrayOfNulls<Packet>(2048)

	@OriginalMember(owner = "client!iaa", name = "D", descriptor = "I")
	@JvmField
	var numUpdateMaskPlayers: Int = 0

	@OriginalMember(owner = "client!nr", name = "c", descriptor = "Lclient!ke;")
	@JvmField
	var localPlayer: PlayerEntity? = null

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "(BLclient!ke;[I[I[I)V")
	@JvmStatic
	fun animateSlots(@OriginalArg(1) playerEntity: PlayerEntity?, @OriginalArg(2) slotMasks: IntArray?, @OriginalArg(3) animationIds: IntArray, @OriginalArg(4) speeds: IntArray?) {
		@Pc(12) var i: Int = 0
		while (i < animationIds.size) {
			@Pc(18) val animationId: Int = animationIds[i]
			@Pc(22) var slotMask: Int = slotMasks!![i]
			@Pc(26) val speed: Int = speeds!![i]
			@Pc(28) var slot: Int = 0
			while (slotMask != 0 && slot < playerEntity!!.sequences.size) {
				if ((slotMask and 0x1) != 0) {
					if (animationId == -1) {
						playerEntity.sequences[slot] = null
					} else {
						@Pc(43) val seqType: SeqType = TypeLists.SEQ!!.list(animationId)
						@Pc(46) val loopType: Int = seqType.loopType
						@Pc(51) var pathingEntitySequence: PathingEntitySequence? = playerEntity.sequences[slot]
						if (pathingEntitySequence != null) {
							if (animationId == pathingEntitySequence.sequenceId) {
								if (loopType == 0) {
									playerEntity.sequences[slot] = null
									pathingEntitySequence = null
								} else if (loopType == 1) {
									pathingEntitySequence.speed = speed
									pathingEntitySequence.nextFrameId = 1
									pathingEntitySequence.currentFrameDuration = 0
									pathingEntitySequence.currentFrameId = 0
									pathingEntitySequence.currentLoopCount = 0
									if (!playerEntity.isHidden) {
										SoundManager.playSequenceSound(playerEntity, 0, seqType)
									}
								} else if (loopType == 2) {
									pathingEntitySequence.currentLoopCount = 0
								}
							} else if (seqType.priority >= TypeLists.SEQ!!.list(pathingEntitySequence.sequenceId).priority) {
								playerEntity.sequences[slot] = null
								pathingEntitySequence = null
							}
						}
						if (pathingEntitySequence == null) {
							pathingEntitySequence = PathingEntitySequence()
							playerEntity.sequences[slot] = pathingEntitySequence
							pathingEntitySequence.currentLoopCount = 0
							pathingEntitySequence.nextFrameId = 1
							pathingEntitySequence.speed = speed
							pathingEntitySequence.currentFrameDuration = 0
							pathingEntitySequence.sequenceId = animationId
							pathingEntitySequence.currentFrameId = 0
							if (!playerEntity.isHidden) {
								SoundManager.playSequenceSound(playerEntity, 0, seqType)
							}
						}
					}
				}
				slot++
				slotMask = slotMask ushr 0x1
			}
			i++
		}
	}

	@OriginalMember(owner = "client!wba", name = "M", descriptor = "[Lclient!ke;")
	@JvmField
	val players: Array<PlayerEntity?> = arrayOfNulls<PlayerEntity>(2048)

	@OriginalMember(owner = "client!ns", name = "a", descriptor = "(Lclient!ua;IIILclient!ke;)V")
	@JvmStatic
	fun decodePlayerUpdateMask(@OriginalArg(0) bitPacket: BitPacket?, @OriginalArg(1) playerIndex: Int, @OriginalArg(2) flags: Int, @OriginalArg(4) playerEntity: PlayerEntity?) {
		@Pc(11) var movementTypeOverride: Byte = -1
		@Pc(22) var local22: Int
		if ((flags and 0x1) != 0) {
			var faceEntityIndex: Int = bitPacket!!.readUShort()
			if (faceEntityIndex == 65535) {
				faceEntityIndex = -1
			}
			playerEntity!!.faceEntity = faceEntityIndex
		}
		@Pc(46) var local46: IntArray?
		@Pc(49) var local49: IntArray?
		@Pc(51) var local51: Int
		@Pc(57) var local57: Int
		@Pc(68) var local68: Int
		if ((flags and 0x10000) != 0) {
			val wornTargetCount: Int = bitPacket!!.readUByte()
			val wornTargets: IntArray = IntArray(wornTargetCount)
			val wornTargetFlags: IntArray = IntArray(wornTargetCount)
			var targetIndex: Int = 0
			while (targetIndex < wornTargetCount) {
				val wornTarget: Int = bitPacket.readUShortAddLittle()
				if ((wornTarget and 0xC000) == 49152) {
					val wornTargetLow: Int = bitPacket.readUShortLittle()
					wornTargets[targetIndex] = (wornTarget shl 16) or wornTargetLow
				} else {
					wornTargets[targetIndex] = wornTarget
				}
				wornTargetFlags[targetIndex] = bitPacket.readUShortLittle()
				targetIndex++
			}
			playerEntity!!.updateWornTargets(wornTargetFlags, wornTargets)
		}
		if ((flags and 0x400) != 0) {
			val packedBarDuration: Int = bitPacket!!.readUShortLittle()
			playerEntity!!.secondaryBarHoldCycles = bitPacket.readUByteSubtract()
			playerEntity.secondaryBarStep = bitPacket.readUByteSubtract()
			playerEntity.secondaryBarDuration = packedBarDuration and 0x7FFF
			playerEntity.secondaryBarAlternate = (packedBarDuration and 0x8000) != 0
			playerEntity.secondaryBarEndCycle = playerEntity.secondaryBarDuration + ClientState.clientCycle + playerEntity.secondaryBarHoldCycles
		}
		if ((flags and 0x1000) != 0) {
			playerEntity!!.hintDetails.text = bitPacket!!.readString()
			if (playerEntity.hintDetails.text!![0] == '~') {
				playerEntity.hintDetails.text = playerEntity.hintDetails.text!!.substring(1)
				ChatLineWriter.writeMessage(playerEntity.getDisplayName(), 2, 0, playerEntity.hintDetails.text, playerEntity.getUsernameWithTitle(), playerEntity.username)
			} else if (PlayerUpdate.localPlayer === playerEntity) {
				ChatLineWriter.writeMessage(playerEntity.getDisplayName(), 2, 0, playerEntity.hintDetails.text, playerEntity.getUsernameWithTitle(), playerEntity.username)
			}
			playerEntity.hintDetails.ticksLeft = 150
			playerEntity.hintDetails.drawType = 0
			playerEntity.hintDetails.colorType = 0
		}
		@Pc(226) var local226: Int
		@Pc(236) var local236: Int
		if ((flags and 0x4) != 0) {
			val hitCount: Int = bitPacket!!.readUByteInverse()
			if (hitCount > 0) {
				var hitIndex: Int = 0
				while (hitIndex < hitCount) {
					var soakHitsplatId: Int = -1
					var hitAmount: Int = -1
					var hitsplatId: Int = bitPacket.readUSmart()
					var soakAmount: Int = -1
					if (hitsplatId == 32767) {
						hitsplatId = bitPacket.readUSmart()
						hitAmount = bitPacket.readUSmart()
						soakHitsplatId = bitPacket.readUSmart()
						soakAmount = bitPacket.readUSmart()
					} else if (hitsplatId == 32766) {
						hitsplatId = -1
					} else {
						hitAmount = bitPacket.readUSmart()
					}
					@Pc(276) val delay: Int = bitPacket.readUSmart()
					@Pc(280) val healthRatio: Int = bitPacket.readUByteAdd()
					playerEntity!!.hit(hitAmount, ClientState.clientCycle, hitsplatId, soakAmount, soakHitsplatId, delay, healthRatio)
					hitIndex++
				}
			}
		}
		if ((flags and 0x2) != 0) {
			playerEntity!!.faceDirection = bitPacket!!.readUShort()
			if (playerEntity.pathPointer == 0) {
				playerEntity.turn(playerEntity.faceDirection)
				playerEntity.faceDirection = -1
			}
		}
		if ((flags and 0x8000) != 0) {
			playerEntity!!.pMapdot = bitPacket!!.readUByteAdd() == 1
		}
		if ((flags and 0x100) != 0) {
			val slotAnimationCount: Int = bitPacket!!.readUByteInverse()
			val slotAnimationIds: IntArray = IntArray(slotAnimationCount)
			val slotSpeeds: IntArray = IntArray(slotAnimationCount)
			@Pc(351) val slotMasks: IntArray = IntArray(slotAnimationCount)
			var entryIndex: Int = 0
			while (entryIndex < slotAnimationCount) {
				var animationId: Int = bitPacket.readUShort()
				if (animationId == 65535) {
					animationId = -1
				}
				slotAnimationIds[entryIndex] = animationId
				slotSpeeds[entryIndex] = bitPacket.readUByteInverse()
				slotMasks[entryIndex] = bitPacket.readUShort()
				entryIndex++
			}
			PlayerUpdate.animateSlots(playerEntity, slotMasks, slotAnimationIds, slotSpeeds)
		}
		if ((flags and 0x2000) != 0) {
			playerEntity!!.forceMovementT1XOff = bitPacket!!.readByte().toInt()
			playerEntity.forceMovementT1YOff = bitPacket.readByteSubtract().toInt()
			playerEntity.forceMovementT2XOff = bitPacket.readByteInverse().toInt()
			playerEntity.forceMovementT2YOff = bitPacket.readByteInverse().toInt()
			playerEntity.forceMovementT1Delay = bitPacket.readUShortAddLittle() + ClientState.clientCycle
			playerEntity.forceMovementT2Delay = bitPacket.readUShortAdd() + ClientState.clientCycle
			playerEntity.forceMoveDirection = bitPacket.readUByte()
			playerEntity.previousMovementStepsLeft = 0
			if (playerEntity.hasPendingPositionUpdate) {
				playerEntity.forceMovementT2YOff += playerEntity.basePositionY
				playerEntity.forceMovementT1XOff += playerEntity.basePositionX
				playerEntity.forceMovementT1YOff += playerEntity.basePositionY
				playerEntity.forceMovementT2XOff += playerEntity.basePositionX
				playerEntity.pathPointer = 0
			} else {
				playerEntity.forceMovementT2XOff += playerEntity.movementBaseX[0]
				playerEntity.forceMovementT2YOff += playerEntity.movementBaseY[0]
				playerEntity.pathPointer = 1
				playerEntity.forceMovementT1YOff += playerEntity.movementBaseY[0]
				playerEntity.forceMovementT1XOff += playerEntity.movementBaseX[0]
			}
		}
		if ((flags and 0x200) != 0) {
			var spotAnimationId: Int = bitPacket!!.readUShortAddLittle()
			if (spotAnimationId == 65535) {
				spotAnimationId = -1
			}
			val packedDelayAndHeight: Int = bitPacket.readIntLE()
			val packedSettings: Int = bitPacket.readUByteSubtract()
			val rotation: Int = packedSettings and 0x7
			var adjustmentType: Int = (packedSettings shr 3) and 0xF
			if (adjustmentType == 15) {
				adjustmentType = -1
			}
			playerEntity!!.playSpotAnimation(rotation, spotAnimationId, adjustmentType, true, packedDelayAndHeight)
		}
		if ((flags and 0x40000) != 0) {
			playerEntity!!.hue = bitPacket!!.readByteInverse()
			playerEntity.saturation = bitPacket.readByte()
			playerEntity.lightness = bitPacket.readByte()
			playerEntity.opacity = bitPacket.readUByte().toByte()
			playerEntity.tintStartCycle = ClientState.clientCycle + bitPacket.readUShortAddLittle()
			playerEntity.tintEndCycle = ClientState.clientCycle + bitPacket.readUShortAdd()
		}
		if ((flags and 0x80) != 0) {
			PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex] = bitPacket!!.readByteSubtract()
		}
		if ((flags and 0x20) != 0) {
			var spotAnimationId: Int = bitPacket!!.readUShortAddLittle()
			if (spotAnimationId == 65535) {
				spotAnimationId = -1
			}
			val packedDelayAndHeight: Int = bitPacket.readUIntInverseMiddle()
			val packedSettings: Int = bitPacket.readUByteAdd()
			val rotation: Int = packedSettings and 0x7
			var adjustmentType: Int = (packedSettings shr 3) and 0xF
			if (adjustmentType == 15) {
				adjustmentType = -1
			}
			playerEntity!!.playSpotAnimation(rotation, spotAnimationId, adjustmentType, false, packedDelayAndHeight)
		}
		if ((flags and 0x8) != 0) {
			@Pc(675) val animationIds: IntArray = IntArray(4)
			var animationIndex: Int = 0
			while (animationIndex < 4) {
				animationIds[animationIndex] = bitPacket!!.readUShortAdd()
				if (animationIds[animationIndex] == 65535) {
					animationIds[animationIndex] = -1
				}
				animationIndex++
			}
			val animationSpeed: Int = bitPacket!!.readUByteAdd()
			PlayerUpdate.animate(animationIds, animationSpeed, playerEntity!!)
		}
		if ((flags and 0x10) != 0) {
			val appearanceSize: Int = bitPacket!!.readUByte()
			@Pc(725) val appearanceData: ByteArray = ByteArray(appearanceSize)
			@Pc(730) val packet: Packet = Packet(appearanceData)
			bitPacket.readBytesReversed(appearanceData, appearanceSize)
			PlayerUpdate.APPEARANCE_BUFFERS[playerIndex] = packet
			playerEntity!!.decodeAppearance(packet)
		}
		if ((flags and 0x800) != 0) {
			movementTypeOverride = bitPacket!!.readByteInverse()
		}
		if (!playerEntity!!.hasPendingPositionUpdate) {
			return
		}
		if (movementTypeOverride.toInt() == 127) {
			playerEntity.move(playerEntity.basePositionY, playerEntity.basePositionX)
			return
		}
		@Pc(763) val movementType: Byte
		if (movementTypeOverride.toInt() == -1) {
			movementType = PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex]
		} else {
			movementType = movementTypeOverride
		}
		PlayerUpdate.animateMovement(playerEntity, movementType.toInt())
		playerEntity.move(playerEntity.basePositionY, movementType, playerEntity.basePositionX)
	}

	@OriginalMember(owner = "client!bea", name = "a", descriptor = "(ILclient!ua;I)V")
	@JvmStatic
	fun decodePlayerUpdate(@OriginalArg(1) bitPacket: BitPacket?, @OriginalArg(2) packetSize: Int) {
		PlayerUpdate.numUpdateMaskPlayers = 0
		PlayerUpdate.playerUpdateDebug = false
		PlayerInfoBitDecoder.decodeUpdate(bitPacket)
		PlayerUpdate.decodeUpdateMasks(bitPacket)
		if (PlayerUpdate.playerUpdateDebug) {
			System.out.println("---endgpp---")
		}
		if (packetSize != bitPacket!!.currentPosition) {
			throw RuntimeException("gpi1 pos:" + bitPacket.currentPosition + " psize:" + packetSize)
		}
	}

	@OriginalMember(owner = "client!gf", name = "a", descriptor = "(ILclient!ua;)V")
	@JvmStatic
	fun decodeUpdateMasks(@OriginalArg(1) bitPacket: BitPacket?) {
		@Pc(12) var i: Int = 0
		while (i < PlayerUpdate.numUpdateMaskPlayers) {
			@Pc(18) val playerIndex: Int = PlayerUpdate.UPDATE_MASK_PLAYER_INDICES[i]
			@Pc(22) val playerEntity: PlayerEntity? = PlayerUpdate.players[playerIndex]
			@Pc(26) var flags: Int = bitPacket!!.readUByte()
			if ((flags and 0x40) != 0) {
				flags += bitPacket.readUByte() shl 8
			}
			if ((flags and 0x4000) != 0) {
				flags += bitPacket.readUByte() shl 16
			}
			PlayerUpdate.decodePlayerUpdateMask(bitPacket, playerIndex, flags, playerEntity)
			i++
		}
	}

	@OriginalMember(owner = "client!uea", name = "b", descriptor = "[B")
	@JvmField
	val PLAYER_MOVEMENT_TYPES: ByteArray = ByteArray(2048)

	@OriginalMember(owner = "client!sba", name = "a", descriptor = "(I[IILclient!ke;)V")
	@JvmStatic
	fun animate(@OriginalArg(1) animationIds: IntArray?, @OriginalArg(2) speed: Int, @OriginalArg(3) playerEntity: PlayerEntity) {
		@Pc(10) var local10: Boolean
		@Pc(12) var index: Int
		if (playerEntity.currentAnimations != null) {
			var animationsMatch: Boolean = true
			index = 0
			while (index < playerEntity.currentAnimations!!.size) {
				if (animationIds!![index] != playerEntity.currentAnimations!![index]) {
					animationsMatch = false
					break
				}
				index++
			}
			if (animationsMatch && playerEntity.currentSequence.sequenceId != -1) {
				@Pc(49) val seqType: SeqType = TypeLists.SEQ!!.list(playerEntity.currentSequence.sequenceId)
				@Pc(52) val loopType: Int = seqType.loopType
				if (loopType == 1) {
					playerEntity.currentSequence.currentFrameDuration = 0
					playerEntity.currentSequence.currentFrameId = 0
					playerEntity.currentSequence.currentLoopCount = 0
					playerEntity.currentSequence.speed = speed
					playerEntity.currentSequence.nextFrameId = 1
					if (!playerEntity.isHidden) {
						SoundManager.playSequenceSound(playerEntity, playerEntity.currentSequence.currentFrameId, seqType)
					}
				}
				if (loopType == 2) {
					playerEntity.currentSequence.currentLoopCount = 0
				}
			}
		}
		var allCleared: Boolean = true
		index = 0
		while (index < animationIds!!.size) {
			if (animationIds[index] != -1) {
				allCleared = false
			}
			if (playerEntity.currentAnimations == null || playerEntity.currentAnimations!![index] == -1 || TypeLists.SEQ!!.list(animationIds[index]).priority >= TypeLists.SEQ!!.list(playerEntity.currentAnimations!![index]).priority) {
				playerEntity.currentSequence.speed = speed
				playerEntity.currentAnimations = animationIds
				break
			}
			index++
		}
		if (allCleared) {
			playerEntity.currentAnimations = animationIds
			playerEntity.currentSequence.speed = speed
		}
	}

	@OriginalMember(owner = "client!hh", name = "j", descriptor = "[I")
	@JvmField
	val UPDATE_MASK_PLAYER_INDICES: IntArray = IntArray(2048)

	@OriginalMember(owner = "client!ct", name = "a", descriptor = "(Lclient!kda;II)V")
	@JvmStatic
	fun animateMovement(@OriginalArg(0) pathingEntity: PathingEntity, @OriginalArg(1) movementType: Int) {
		if (pathingEntity.currentAnimations == null) {
			return
		}
		@Pc(13) val sequenceId: Int = pathingEntity.currentAnimations!![movementType + 1]
		if (pathingEntity.currentSequence.sequenceId == sequenceId) {
			return
		}
		pathingEntity.previousMovementStepsLeft = pathingEntity.pathPointer
		pathingEntity.currentSequence.sequenceId = sequenceId
		pathingEntity.currentSequence.nextFrameId = 1
		pathingEntity.currentSequence.currentFrameDuration = 0
		pathingEntity.currentSequence.currentLoopCount = 0
		pathingEntity.currentSequence.currentFrameId = 0
		if (pathingEntity.currentSequence.sequenceId != -1) {
			SoundManager.playSequenceSound(pathingEntity, pathingEntity.currentSequence.currentFrameId, TypeLists.SEQ!!.list(pathingEntity.currentSequence.sequenceId))
		}
	}

	@OriginalMember(owner = "client!kfa", name = "a", descriptor = "(Lclient!ua;B)V")
	@JvmStatic
	fun decodeOwnPlayer(@OriginalArg(0) bitPacket: BitPacket) {
		bitPacket.enterBitMode()
		@Pc(10) val myPlayerIndex: Int = PlayerUpdate.myPlayerIndex
		val newPlayer: PlayerEntity = PlayerEntity()
		PlayerUpdate.players[myPlayerIndex] = newPlayer
		PlayerUpdate.localPlayer = newPlayer
		@Pc(20) val playerEntity: PlayerEntity = newPlayer
		playerEntity.playerIndex = myPlayerIndex
		@Pc(28) val packedPosition: Int = bitPacket.gbit(30)
		@Pc(38) val level: Byte = (packedPosition shr 28).toByte()
		@Pc(44) val absX: Int = (packedPosition shr 14) and 0x3FFF
		playerEntity.movementBaseX[0] = absX - MapRegion.sceneBaseX
		@Pc(56) val absY: Int = packedPosition and 0x3FFF
		playerEntity.x = (playerEntity.movementBaseX[0] shl 9) + (playerEntity.getSize() shl 8)
		playerEntity.movementBaseY[0] = absY - MapRegion.sceneBaseY
		playerEntity.z = (playerEntity.movementBaseY[0] shl 9) + (playerEntity.getSize() shl 8)
		playerEntity.virtualLevel = level
		playerEntity.level = playerEntity.virtualLevel
		PlayerUpdate.currentLevel = playerEntity.level.toInt()
		if (SettingsBits.isBridge(playerEntity.movementBaseY[0], playerEntity.movementBaseX[0])) {
			playerEntity.virtualLevel++
		}
		if (PlayerUpdate.APPEARANCE_BUFFERS[myPlayerIndex] != null) {
			playerEntity.decodeAppearance(PlayerUpdate.APPEARANCE_BUFFERS[myPlayerIndex])
		}
		OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES = 0
		PlayerUpdate.OUT_PLAYER_INDICES[OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES++] = myPlayerIndex
		PlayerUpdate.OUT_PLAYER_UPDATE_STATE[myPlayerIndex] = 0
		WorldMap.numInactivePlayerIndices = 0
		@Pc(147) var i: Int = 1
		while (i < 2048) {
			if (myPlayerIndex != i) {
				@Pc(161) val packedRegion: Int = bitPacket.gbit(18)
				@Pc(165) val regionLevel: Int = packedRegion shr 16
				@Pc(171) val regionX: Int = (packedRegion shr 8) and 0xFF
				@Pc(175) val regionY: Int = packedRegion and 0xFF
				val newEntry: PlayerUpdateEntry = PlayerUpdateEntry()
				PlayerUpdate.PLAYER_UPDATES[i] = newEntry
				@Pc(183) val playerUpdateEntry: PlayerUpdateEntry = newEntry
				playerUpdateEntry.pMapdot = false
				playerUpdateEntry.packedCoordinates = (regionX shl 14) + (regionLevel shl 28) + regionY
				playerUpdateEntry.rotation = 0
				playerUpdateEntry.faceEntity = -1
				PlayerUpdate.INACTIVE_PLAYER_INDICES[WorldMap.numInactivePlayerIndices++] = i
				PlayerUpdate.OUT_PLAYER_UPDATE_STATE[i] = 0
			}
			i++
		}
		bitPacket.exitBitMode()
	}

	@OriginalMember(owner = "client!vl", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun resetPlayerUpdateState() {
		OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES = 0
		@Pc(13) var i: Int = 0
		while (i < 2048) {
			PlayerUpdate.APPEARANCE_BUFFERS[i] = null
			PlayerUpdate.PLAYER_MOVEMENT_TYPES[i] = 1
			PlayerUpdate.PLAYER_UPDATES[i] = null
			i++
		}
	}

	@OriginalMember(owner = "client!ed", name = "P", descriptor = "Ljava/lang/String;")
	@JvmField
	var localUsername: String? = null

	@OriginalMember(owner = "client!jfa", name = "b", descriptor = "Z")
	@JvmField
	var playerUpdateDebug: Boolean = false

	@OriginalMember(owner = "client!aha", name = "k", descriptor = "I")
	@JvmField
	var currentLevel: Int = 0
}
