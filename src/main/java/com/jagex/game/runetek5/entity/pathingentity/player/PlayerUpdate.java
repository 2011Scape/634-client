package com.jagex.game.runetek5.entity.pathingentity.player;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.entity.PathingEntity;
import com.jagex.game.runetek5.statics.OutPlayerIndexCount;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.PlayerUpdateEntry;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.buffer.BitPacket;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.config.sequence.PathingEntitySequence;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.entity.PlayerEntity;
import org.openrs2.deob.annotation.OriginalArg;
import com.jagex.game.runetek5.PlayerInfoBitDecoder;
import com.jagex.game.runetek5.ChatLineWriter;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.SoundManager;
import com.jagex.game.runetek5.scene.SettingsBits;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.client.ClientState;

public final class PlayerUpdate {
	@OriginalMember(owner = "client!jt", name = "n", descriptor = "[I")
	public static final int[] INACTIVE_PLAYER_INDICES = new int[2048];

	@OriginalMember(owner = "client!to", name = "vb", descriptor = "I")
	public static int MY_PLAYER_PLANE = -1;

	@OriginalMember(owner = "client!fba", name = "u", descriptor = "[B")
	public static final byte[] OUT_PLAYER_UPDATE_STATE = new byte[2048];

	@OriginalMember(owner = "client!ug", name = "I", descriptor = "I")
	public static int myPlayerIndex = -1;

	@OriginalMember(owner = "client!wea", name = "g", descriptor = "Ljava/lang/String;")
	public static String javaVendor;

	@OriginalMember(owner = "client!tn", name = "r", descriptor = "[I")
	public static final int[] OUT_PLAYER_INDICES = new int[2048];

	private PlayerUpdate() {
		/* empty */
	}
	@OriginalMember(owner = "client!client", name = "c", descriptor = "()V")
	public static void positionMisalignedEntities() {
		@Pc(1) int playerCount = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES;
		@Pc(3) int[] outPlayerIndices = PlayerUpdate.OUT_PLAYER_INDICES;
		@Pc(11) int entityCount = NPCList.hideNpcs ? playerCount : playerCount + NPCList.NPC_UPDATE_INDEX;
		for (@Pc(13) int i = 0; i < entityCount; i++) {
			@Pc(23) PathingEntity pathingEntity;
			if (i < playerCount) {
				pathingEntity = PlayerUpdate.players[outPlayerIndices[i]];
			} else {
				pathingEntity = ((NPCEntityNode) NPCList.NPC_MAP.get((long) NPCList.NPC_UPDATE_INDICES[i - playerCount])).npc;
			}
			if (pathingEntity.worldPriority >= 0) {
				@Pc(43) int size = pathingEntity.getSize();
				if ((size & 0x1) == 0) {
					if ((pathingEntity.x & 0x1FF) == 0 && (pathingEntity.z & 0x1FF) == 0) {
						continue;
					}
				} else if ((pathingEntity.x & 0x1FF) == 256 && (pathingEntity.z & 0x1FF) == 256) {
					continue;
				}
				pathingEntity.y = Scene.tileHeightForAbsolutePosition(pathingEntity.x, pathingEntity.level, pathingEntity.z);
				Scene.addInteractive(pathingEntity, true);
			}
		}
	}
	@OriginalMember(owner = "client!jf", name = "C", descriptor = "[Lclient!f;")
	public static final PlayerUpdateEntry[] PLAYER_UPDATES = new PlayerUpdateEntry[2048];

	@OriginalMember(owner = "client!mk", name = "t", descriptor = "[Lclient!wo;")
	public static final HintArrow[] HINT_ARROWS = new HintArrow[8];

	@OriginalMember(owner = "client!oo", name = "p", descriptor = "[Lclient!cea;")
	public static final Packet[] APPEARANCE_BUFFERS = new Packet[2048];
	@OriginalMember(owner = "client!iaa", name = "D", descriptor = "I")
	public static int numUpdateMaskPlayers = 0;
	@OriginalMember(owner = "client!nr", name = "c", descriptor = "Lclient!ke;")
	public static PlayerEntity localPlayer;

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "(BLclient!ke;[I[I[I)V")
	public static void animateSlots(@OriginalArg(1) PlayerEntity playerEntity, @OriginalArg(2) int[] slotMasks, @OriginalArg(3) int[] animationIds, @OriginalArg(4) int[] speeds) {
		for (@Pc(12) int i = 0; i < animationIds.length; i++) {
			@Pc(18) int animationId = animationIds[i];
			@Pc(22) int slotMask = slotMasks[i];
			@Pc(26) int speed = speeds[i];
			@Pc(28) int slot = 0;
			while (slotMask != 0 && slot < playerEntity.sequences.length) {
				if ((slotMask & 0x1) != 0) {
					if (animationId == -1) {
						playerEntity.sequences[slot] = null;
					} else {
						@Pc(43) SeqType seqType = TypeLists.SEQ.list(animationId);
						@Pc(46) int loopType = seqType.loopType;
						@Pc(51) PathingEntitySequence pathingEntitySequence = playerEntity.sequences[slot];
						if (pathingEntitySequence != null) {
							if (animationId == pathingEntitySequence.sequenceId) {
								if (loopType == 0) {
									pathingEntitySequence = playerEntity.sequences[slot] = null;
								} else if (loopType == 1) {
									pathingEntitySequence.speed = speed;
									pathingEntitySequence.nextFrameId = 1;
									pathingEntitySequence.currentFrameDuration = 0;
									pathingEntitySequence.currentFrameId = 0;
									pathingEntitySequence.currentLoopCount = 0;
									if (!playerEntity.isHidden) {
										SoundManager.playSequenceSound(playerEntity, 0, seqType);
									}
								} else if (loopType == 2) {
									pathingEntitySequence.currentLoopCount = 0;
								}
							} else if (seqType.priority >= TypeLists.SEQ.list(pathingEntitySequence.sequenceId).priority) {
								pathingEntitySequence = playerEntity.sequences[slot] = null;
							}
						}
						if (pathingEntitySequence == null) {
							pathingEntitySequence = playerEntity.sequences[slot] = new PathingEntitySequence();
							pathingEntitySequence.currentLoopCount = 0;
							pathingEntitySequence.nextFrameId = 1;
							pathingEntitySequence.speed = speed;
							pathingEntitySequence.currentFrameDuration = 0;
							pathingEntitySequence.sequenceId = animationId;
							pathingEntitySequence.currentFrameId = 0;
							if (!playerEntity.isHidden) {
								SoundManager.playSequenceSound(playerEntity, 0, seqType);
							}
						}
					}
				}
				slot++;
				slotMask >>>= 0x1;
			}
		}
	}

	@OriginalMember(owner = "client!wba", name = "M", descriptor = "[Lclient!ke;")
	public static final PlayerEntity[] players = new PlayerEntity[2048];

	@OriginalMember(owner = "client!ns", name = "a", descriptor = "(Lclient!ua;IIILclient!ke;)V")
	public static void decodePlayerUpdateMask(@OriginalArg(0) BitPacket bitPacket, @OriginalArg(1) int playerIndex, @OriginalArg(2) int flags, @OriginalArg(4) PlayerEntity playerEntity) {
		@Pc(11) byte movementTypeOverride = -1;
		@Pc(22) int local22;
		if ((flags & 0x1) != 0) {
			int faceEntityIndex = bitPacket.readUShort();
			if (faceEntityIndex == 65535) {
				faceEntityIndex = -1;
			}
			playerEntity.faceEntity = faceEntityIndex;
		}
		@Pc(46) int[] local46;
		@Pc(49) int[] local49;
		@Pc(51) int local51;
		@Pc(57) int local57;
		@Pc(68) int local68;
		if ((flags & 0x10000) != 0) {
			int wornTargetCount = bitPacket.readUByte();
			int[] wornTargets = new int[wornTargetCount];
			int[] wornTargetFlags = new int[wornTargetCount];
			for (int targetIndex = 0; targetIndex < wornTargetCount; targetIndex++) {
				int wornTarget = bitPacket.readUShortAddLittle();
				if ((wornTarget & 0xC000) == 49152) {
					int wornTargetLow = bitPacket.readUShortLittle();
					wornTargets[targetIndex] = wornTarget << 16 | wornTargetLow;
				} else {
					wornTargets[targetIndex] = wornTarget;
				}
				wornTargetFlags[targetIndex] = bitPacket.readUShortLittle();
			}
			playerEntity.updateWornTargets(wornTargetFlags, wornTargets);
		}
		if ((flags & 0x400) != 0) {
			int packedBarDuration = bitPacket.readUShortLittle();
			playerEntity.secondaryBarHoldCycles = bitPacket.readUByteSubtract();
			playerEntity.secondaryBarStep = bitPacket.readUByteSubtract();
			playerEntity.secondaryBarDuration = packedBarDuration & 0x7FFF;
			playerEntity.secondaryBarAlternate = (packedBarDuration & 0x8000) != 0;
			playerEntity.secondaryBarEndCycle = playerEntity.secondaryBarDuration + ClientState.clientCycle + playerEntity.secondaryBarHoldCycles;
		}
		if ((flags & 0x1000) != 0) {
			playerEntity.hintDetails.text = bitPacket.readString();
			if (playerEntity.hintDetails.text.charAt(0) == '~') {
				playerEntity.hintDetails.text = playerEntity.hintDetails.text.substring(1);
				ChatLineWriter.writeMessage(playerEntity.getDisplayName(), 2, 0, playerEntity.hintDetails.text, playerEntity.getUsernameWithTitle(), playerEntity.username);
			} else if (PlayerUpdate.localPlayer == playerEntity) {
				ChatLineWriter.writeMessage(playerEntity.getDisplayName(), 2, 0, playerEntity.hintDetails.text, playerEntity.getUsernameWithTitle(), playerEntity.username);
			}
			playerEntity.hintDetails.ticksLeft = 150;
			playerEntity.hintDetails.drawType = 0;
			playerEntity.hintDetails.colorType = 0;
		}
		@Pc(226) int local226;
		@Pc(236) int local236;
		if ((flags & 0x4) != 0) {
			int hitCount = bitPacket.readUByteInverse();
			if (hitCount > 0) {
				for (int hitIndex = 0; hitIndex < hitCount; hitIndex++) {
					int soakHitsplatId = -1;
					int hitAmount = -1;
					int hitsplatId = bitPacket.readUSmart();
					int soakAmount = -1;
					if (hitsplatId == 32767) {
						hitsplatId = bitPacket.readUSmart();
						hitAmount = bitPacket.readUSmart();
						soakHitsplatId = bitPacket.readUSmart();
						soakAmount = bitPacket.readUSmart();
					} else if (hitsplatId == 32766) {
						hitsplatId = -1;
					} else {
						hitAmount = bitPacket.readUSmart();
					}
					@Pc(276) int delay = bitPacket.readUSmart();
					@Pc(280) int healthRatio = bitPacket.readUByteAdd();
					playerEntity.hit(hitAmount, ClientState.clientCycle, hitsplatId, soakAmount, soakHitsplatId, delay, healthRatio);
				}
			}
		}
		if ((flags & 0x2) != 0) {
			playerEntity.faceDirection = bitPacket.readUShort();
			if (playerEntity.pathPointer == 0) {
				playerEntity.turn(playerEntity.faceDirection);
				playerEntity.faceDirection = -1;
			}
		}
		if ((flags & 0x8000) != 0) {
			playerEntity.pMapdot = bitPacket.readUByteAdd() == 1;
		}
		if ((flags & 0x100) != 0) {
			int slotAnimationCount = bitPacket.readUByteInverse();
			int[] slotAnimationIds = new int[slotAnimationCount];
			int[] slotSpeeds = new int[slotAnimationCount];
			@Pc(351) int[] slotMasks = new int[slotAnimationCount];
			for (int entryIndex = 0; entryIndex < slotAnimationCount; entryIndex++) {
				int animationId = bitPacket.readUShort();
				if (animationId == 65535) {
					animationId = -1;
				}
				slotAnimationIds[entryIndex] = animationId;
				slotSpeeds[entryIndex] = bitPacket.readUByteInverse();
				slotMasks[entryIndex] = bitPacket.readUShort();
			}
			PlayerUpdate.animateSlots(playerEntity, slotMasks, slotAnimationIds, slotSpeeds);
		}
		if ((flags & 0x2000) != 0) {
			playerEntity.forceMovementT1XOff = bitPacket.readByte();
			playerEntity.forceMovementT1YOff = bitPacket.readByteSubtract();
			playerEntity.forceMovementT2XOff = bitPacket.readByteInverse();
			playerEntity.forceMovementT2YOff = bitPacket.readByteInverse();
			playerEntity.forceMovementT1Delay = bitPacket.readUShortAddLittle() + ClientState.clientCycle;
			playerEntity.forceMovementT2Delay = bitPacket.readUShortAdd() + ClientState.clientCycle;
			playerEntity.forceMoveDirection = bitPacket.readUByte();
			playerEntity.previousMovementStepsLeft = 0;
			if (playerEntity.hasPendingPositionUpdate) {
				playerEntity.forceMovementT2YOff += playerEntity.basePositionY;
				playerEntity.forceMovementT1XOff += playerEntity.basePositionX;
				playerEntity.forceMovementT1YOff += playerEntity.basePositionY;
				playerEntity.forceMovementT2XOff += playerEntity.basePositionX;
				playerEntity.pathPointer = 0;
			} else {
				playerEntity.forceMovementT2XOff += playerEntity.movementBaseX[0];
				playerEntity.forceMovementT2YOff += playerEntity.movementBaseY[0];
				playerEntity.pathPointer = 1;
				playerEntity.forceMovementT1YOff += playerEntity.movementBaseY[0];
				playerEntity.forceMovementT1XOff += playerEntity.movementBaseX[0];
			}
		}
		if ((flags & 0x200) != 0) {
			int spotAnimationId = bitPacket.readUShortAddLittle();
			if (spotAnimationId == 65535) {
				spotAnimationId = -1;
			}
			int packedDelayAndHeight = bitPacket.readIntLE();
			int packedSettings = bitPacket.readUByteSubtract();
			int rotation = packedSettings & 0x7;
			int adjustmentType = packedSettings >> 3 & 0xF;
			if (adjustmentType == 15) {
				adjustmentType = -1;
			}
			playerEntity.playSpotAnimation(rotation, spotAnimationId, adjustmentType, true, packedDelayAndHeight);
		}
		if ((flags & 0x40000) != 0) {
			playerEntity.hue = bitPacket.readByteInverse();
			playerEntity.saturation = bitPacket.readByte();
			playerEntity.lightness = bitPacket.readByte();
			playerEntity.opacity = (byte) bitPacket.readUByte();
			playerEntity.tintStartCycle = ClientState.clientCycle + bitPacket.readUShortAddLittle();
			playerEntity.tintEndCycle = ClientState.clientCycle + bitPacket.readUShortAdd();
		}
		if ((flags & 0x80) != 0) {
			PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex] = bitPacket.readByteSubtract();
		}
		if ((flags & 0x20) != 0) {
			int spotAnimationId = bitPacket.readUShortAddLittle();
			if (spotAnimationId == 65535) {
				spotAnimationId = -1;
			}
			int packedDelayAndHeight = bitPacket.readUIntInverseMiddle();
			int packedSettings = bitPacket.readUByteAdd();
			int rotation = packedSettings & 0x7;
			int adjustmentType = packedSettings >> 3 & 0xF;
			if (adjustmentType == 15) {
				adjustmentType = -1;
			}
			playerEntity.playSpotAnimation(rotation, spotAnimationId, adjustmentType, false, packedDelayAndHeight);
		}
		if ((flags & 0x8) != 0) {
			@Pc(675) int[] animationIds = new int[4];
			for (int animationIndex = 0; animationIndex < 4; animationIndex++) {
				animationIds[animationIndex] = bitPacket.readUShortAdd();
				if (animationIds[animationIndex] == 65535) {
					animationIds[animationIndex] = -1;
				}
			}
			int animationSpeed = bitPacket.readUByteAdd();
			PlayerUpdate.animate(animationIds, animationSpeed, playerEntity);
		}
		if ((flags & 0x10) != 0) {
			int appearanceSize = bitPacket.readUByte();
			@Pc(725) byte[] appearanceData = new byte[appearanceSize];
			@Pc(730) Packet packet = new Packet(appearanceData);
			bitPacket.readBytesReversed(appearanceData, appearanceSize);
			PlayerUpdate.APPEARANCE_BUFFERS[playerIndex] = packet;
			playerEntity.decodeAppearance(packet);
		}
		if ((flags & 0x800) != 0) {
			movementTypeOverride = bitPacket.readByteInverse();
		}
		if (!playerEntity.hasPendingPositionUpdate) {
			return;
		}
		if (movementTypeOverride == 127) {
			playerEntity.move(playerEntity.basePositionY, playerEntity.basePositionX);
			return;
		}
		@Pc(763) byte movementType;
		if (movementTypeOverride == -1) {
			movementType = PlayerUpdate.PLAYER_MOVEMENT_TYPES[playerIndex];
		} else {
			movementType = movementTypeOverride;
		}
		PlayerUpdate.animateMovement(playerEntity, movementType);
		playerEntity.move(playerEntity.basePositionY, movementType, playerEntity.basePositionX);
	}
	@OriginalMember(owner = "client!bea", name = "a", descriptor = "(ILclient!ua;I)V")
	public static void decodePlayerUpdate(@OriginalArg(1) BitPacket bitPacket, @OriginalArg(2) int packetSize) {
		PlayerUpdate.numUpdateMaskPlayers = 0;
		PlayerUpdate.playerUpdateDebug = false;
		PlayerInfoBitDecoder.decodeUpdate(bitPacket);
		PlayerUpdate.decodeUpdateMasks(bitPacket);
		if (PlayerUpdate.playerUpdateDebug) {
			System.out.println("---endgpp---");
		}
		if (packetSize != bitPacket.currentPosition) {
			throw new RuntimeException("gpi1 pos:" + bitPacket.currentPosition + " psize:" + packetSize);
		}
	}

	@OriginalMember(owner = "client!gf", name = "a", descriptor = "(ILclient!ua;)V")
	public static void decodeUpdateMasks(@OriginalArg(1) BitPacket bitPacket) {
		for (@Pc(12) int i = 0; i < PlayerUpdate.numUpdateMaskPlayers; i++) {
			@Pc(18) int playerIndex = PlayerUpdate.UPDATE_MASK_PLAYER_INDICES[i];
			@Pc(22) PlayerEntity playerEntity = PlayerUpdate.players[playerIndex];
			@Pc(26) int flags = bitPacket.readUByte();
			if ((flags & 0x40) != 0) {
				flags += bitPacket.readUByte() << 8;
			}
			if ((flags & 0x4000) != 0) {
				flags += bitPacket.readUByte() << 16;
			}
			PlayerUpdate.decodePlayerUpdateMask(bitPacket, playerIndex, flags, playerEntity);
		}
	}
	@OriginalMember(owner = "client!uea", name = "b", descriptor = "[B")
	public static final byte[] PLAYER_MOVEMENT_TYPES = new byte[2048];

	@OriginalMember(owner = "client!sba", name = "a", descriptor = "(I[IILclient!ke;)V")
	public static void animate(@OriginalArg(1) int[] animationIds, @OriginalArg(2) int speed, @OriginalArg(3) PlayerEntity playerEntity) {
		@Pc(10) boolean local10;
		@Pc(12) int index;
		if (playerEntity.currentAnimations != null) {
			boolean animationsMatch = true;
			for (index = 0; index < playerEntity.currentAnimations.length; index++) {
				if (animationIds[index] != playerEntity.currentAnimations[index]) {
					animationsMatch = false;
					break;
				}
			}
			if (animationsMatch && playerEntity.currentSequence.sequenceId != -1) {
				@Pc(49) SeqType seqType = TypeLists.SEQ.list(playerEntity.currentSequence.sequenceId);
				@Pc(52) int loopType = seqType.loopType;
				if (loopType == 1) {
					playerEntity.currentSequence.currentFrameDuration = 0;
					playerEntity.currentSequence.currentFrameId = 0;
					playerEntity.currentSequence.currentLoopCount = 0;
					playerEntity.currentSequence.speed = speed;
					playerEntity.currentSequence.nextFrameId = 1;
					if (!playerEntity.isHidden) {
						SoundManager.playSequenceSound(playerEntity, playerEntity.currentSequence.currentFrameId, seqType);
					}
				}
				if (loopType == 2) {
					playerEntity.currentSequence.currentLoopCount = 0;
				}
			}
		}
		boolean allCleared = true;
		for (index = 0; index < animationIds.length; index++) {
			if (animationIds[index] != -1) {
				allCleared = false;
			}
			if (playerEntity.currentAnimations == null || playerEntity.currentAnimations[index] == -1 || TypeLists.SEQ.list(animationIds[index]).priority >= TypeLists.SEQ.list(playerEntity.currentAnimations[index]).priority) {
				playerEntity.currentSequence.speed = speed;
				playerEntity.currentAnimations = animationIds;
				break;
			}
		}
		if (allCleared) {
			playerEntity.currentAnimations = animationIds;
			playerEntity.currentSequence.speed = speed;
		}
	}

	@OriginalMember(owner = "client!hh", name = "j", descriptor = "[I")
	public static final int[] UPDATE_MASK_PLAYER_INDICES = new int[2048];

	@OriginalMember(owner = "client!ct", name = "a", descriptor = "(Lclient!kda;II)V")
	public static void animateMovement(@OriginalArg(0) PathingEntity pathingEntity, @OriginalArg(1) int movementType) {
		if (pathingEntity.currentAnimations == null) {
			return;
		}
		@Pc(13) int sequenceId = pathingEntity.currentAnimations[movementType + 1];
		if (pathingEntity.currentSequence.sequenceId == sequenceId) {
			return;
		}
		pathingEntity.previousMovementStepsLeft = pathingEntity.pathPointer;
		pathingEntity.currentSequence.sequenceId = sequenceId;
		pathingEntity.currentSequence.nextFrameId = 1;
		pathingEntity.currentSequence.currentFrameDuration = 0;
		pathingEntity.currentSequence.currentLoopCount = 0;
		pathingEntity.currentSequence.currentFrameId = 0;
		if (pathingEntity.currentSequence.sequenceId != -1) {
			SoundManager.playSequenceSound(pathingEntity, pathingEntity.currentSequence.currentFrameId, TypeLists.SEQ.list(pathingEntity.currentSequence.sequenceId));
		}
	}
	@OriginalMember(owner = "client!kfa", name = "a", descriptor = "(Lclient!ua;B)V")
	public static void decodeOwnPlayer(@OriginalArg(0) BitPacket bitPacket) {
		bitPacket.enterBitMode();
		@Pc(10) int myPlayerIndex = PlayerUpdate.myPlayerIndex;
		@Pc(20) PlayerEntity playerEntity = PlayerUpdate.localPlayer = PlayerUpdate.players[myPlayerIndex] = new PlayerEntity();
		playerEntity.playerIndex = myPlayerIndex;
		@Pc(28) int packedPosition = bitPacket.gbit(30);
		@Pc(38) byte level = (byte) (packedPosition >> 28);
		@Pc(44) int absX = packedPosition >> 14 & 0x3FFF;
		playerEntity.movementBaseX[0] = absX - MapRegion.sceneBaseX;
		@Pc(56) int absY = packedPosition & 0x3FFF;
		playerEntity.x = (playerEntity.movementBaseX[0] << 9) + (playerEntity.getSize() << 8);
		playerEntity.movementBaseY[0] = absY - MapRegion.sceneBaseY;
		playerEntity.z = (playerEntity.movementBaseY[0] << 9) + (playerEntity.getSize() << 8);
		PlayerUpdate.currentLevel = playerEntity.level = playerEntity.virtualLevel = level;
		if (SettingsBits.isBridge(playerEntity.movementBaseY[0], playerEntity.movementBaseX[0])) {
			playerEntity.virtualLevel++;
		}
		if (PlayerUpdate.APPEARANCE_BUFFERS[myPlayerIndex] != null) {
			playerEntity.decodeAppearance(PlayerUpdate.APPEARANCE_BUFFERS[myPlayerIndex]);
		}
		OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES = 0;
		PlayerUpdate.OUT_PLAYER_INDICES[OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES++] = myPlayerIndex;
		PlayerUpdate.OUT_PLAYER_UPDATE_STATE[myPlayerIndex] = 0;
		WorldMap.numInactivePlayerIndices = 0;
		for (@Pc(147) int i = 1; i < 2048; i++) {
			if (myPlayerIndex != i) {
				@Pc(161) int packedRegion = bitPacket.gbit(18);
				@Pc(165) int regionLevel = packedRegion >> 16;
				@Pc(171) int regionX = packedRegion >> 8 & 0xFF;
				@Pc(175) int regionY = packedRegion & 0xFF;
				@Pc(183) PlayerUpdateEntry playerUpdateEntry = PlayerUpdate.PLAYER_UPDATES[i] = new PlayerUpdateEntry();
				playerUpdateEntry.pMapdot = false;
				playerUpdateEntry.packedCoordinates = (regionX << 14) + (regionLevel << 28) + regionY;
				playerUpdateEntry.rotation = 0;
				playerUpdateEntry.faceEntity = -1;
				PlayerUpdate.INACTIVE_PLAYER_INDICES[WorldMap.numInactivePlayerIndices++] = i;
				PlayerUpdate.OUT_PLAYER_UPDATE_STATE[i] = 0;
			}
		}
		bitPacket.exitBitMode();
	}
	@OriginalMember(owner = "client!vl", name = "a", descriptor = "(B)V")
	public static void resetPlayerUpdateState() {
		OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES = 0;
		for (@Pc(13) int i = 0; i < 2048; i++) {
			PlayerUpdate.APPEARANCE_BUFFERS[i] = null;
			PlayerUpdate.PLAYER_MOVEMENT_TYPES[i] = 1;
			PlayerUpdate.PLAYER_UPDATES[i] = null;
		}
	}

	@OriginalMember(owner = "client!ed", name = "P", descriptor = "Ljava/lang/String;")
	public static String localUsername;
	@OriginalMember(owner = "client!jfa", name = "b", descriptor = "Z")
	public static boolean playerUpdateDebug = false;
	@OriginalMember(owner = "client!aha", name = "k", descriptor = "I")
	public static int currentLevel;
}
