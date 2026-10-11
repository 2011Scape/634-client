package com.jagex.game.runetek5;

import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.NpcUpdate;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.config.npc.NPCType;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.core.constants.AreaMode;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.sound.player.area.AmbientSound;
import com.jagex.game.runetek5.client.ClientState;

public final class NPCList {

	@OriginalMember(owner = "client!ri", name = "s", descriptor = "I")
	public static int newSize = 0;

	@OriginalMember(owner = "client!oba", name = "a", descriptor = "(I)V")
	public static void decodeAddRemove() {
		BufferedConnectionContext.receivedBuffer.enterBitMode();
		@Pc(15) int knownNpcCount = BufferedConnectionContext.receivedBuffer.gbit(8);
		@Pc(28) int i;
		if (NPCList.NPC_UPDATE_INDEX > knownNpcCount) {
			for (i = knownNpcCount; i < NPCList.NPC_UPDATE_INDEX; i++) {
				NPCList.NPC_REMOVAL_INDICES[NPCList.npcRemovalCount++] = NPCList.NPC_UPDATE_INDICES[i];
			}
		}
		if (NPCList.NPC_UPDATE_INDEX < knownNpcCount) {
			throw new RuntimeException("gnpov1");
		}
		NPCList.NPC_UPDATE_INDEX = 0;
		for (i = 0; i < knownNpcCount; i++) {
			@Pc(66) int npcIndex = NPCList.NPC_UPDATE_INDICES[i];
			@Pc(74) NPCEntity npcEntity = ((NPCEntityNode) NPCList.NPC_MAP.get((long) npcIndex)).npc;
			@Pc(79) int updateRequired = BufferedConnectionContext.receivedBuffer.gbit(1);
			if (updateRequired == 0) {
				NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcIndex;
				npcEntity.lastUpdate = NPCList.npcUpdateCycle;
			} else {
				@Pc(102) int updateType = BufferedConnectionContext.receivedBuffer.gbit(2);
				if (updateType == 0) {
					NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcIndex;
					npcEntity.lastUpdate = NPCList.npcUpdateCycle;
					NpcUpdate.NPC_MASK_UPDATE_INDICES[NPCList.npcMaskUpdateCount++] = npcIndex;
				} else {
					@Pc(146) int local146;
					@Pc(156) int local156;
					if (updateType == 1) {
						NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcIndex;
						npcEntity.lastUpdate = NPCList.npcUpdateCycle;
						int direction = BufferedConnectionContext.receivedBuffer.gbit(3);
						npcEntity.move(direction, 1);
						int maskUpdateRequired = BufferedConnectionContext.receivedBuffer.gbit(1);
						if (maskUpdateRequired == 1) {
							NpcUpdate.NPC_MASK_UPDATE_INDICES[NPCList.npcMaskUpdateCount++] = npcIndex;
						}
					} else if (updateType == NpcUpdate.RUN_OR_CRAWL) {
						NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcIndex;
						npcEntity.lastUpdate = NPCList.npcUpdateCycle;
						if (BufferedConnectionContext.receivedBuffer.gbit(1) == 1) {
							int walkDirection = BufferedConnectionContext.receivedBuffer.gbit(3);
							npcEntity.move(walkDirection, 2);
							int runDirection = BufferedConnectionContext.receivedBuffer.gbit(3);
							npcEntity.move(runDirection, 2);
						} else {
							int direction = BufferedConnectionContext.receivedBuffer.gbit(3);
							npcEntity.move(direction, 0);
						}
						int maskUpdateRequired = BufferedConnectionContext.receivedBuffer.gbit(1);
						if (maskUpdateRequired == 1) {
							NpcUpdate.NPC_MASK_UPDATE_INDICES[NPCList.npcMaskUpdateCount++] = npcIndex;
						}
					} else if (updateType == NpcUpdate.REMOVE) {
						NPCList.NPC_REMOVAL_INDICES[NPCList.npcRemovalCount++] = npcIndex;
					}
				}
			}
		}
	}
	@OriginalMember(owner = "client!ew", name = "a", descriptor = "I")
	public static int npcRemovalCount = 0;
	@OriginalMember(owner = "client!ww", name = "M", descriptor = "[I")
	public static final int[] NPC_REMOVAL_INDICES = new int[1000];

	@OriginalMember(owner = "client!lia", name = "a", descriptor = "(I)V")
	public static void decodeNpcSpawns() {
		@Pc(8) int length = MapRegion.npcSpawnBuffers.length;
		for (@Pc(14) int i = 0; i < length; i++) {
			if (MapRegion.npcSpawnBuffers[i] != null) {
				@Pc(22) int mapSquareIndex = -1;
				for (@Pc(24) int j = 0; j < NPCList.npcSpawnCursorPos; j++) {
					if (MapRegion.regionIds[i] == NPCList.npcSpawnRegionIds[j]) {
						mapSquareIndex = j;
						break;
					}
				}
				if (mapSquareIndex == -1) {
					NPCList.npcSpawnRegionIds[NPCList.npcSpawnCursorPos] = MapRegion.regionIds[i];
					mapSquareIndex = NPCList.npcSpawnCursorPos++;
				}
				@Pc(68) Packet packet = new Packet(MapRegion.npcSpawnBuffers[i]);
				@Pc(70) int npcCounter = 0;
				while (MapRegion.npcSpawnBuffers[i].length > packet.currentPosition && npcCounter < 511 && NPCList.NPC_UPDATE_INDEX < 1023) {
					@Pc(91) int npcIndex = mapSquareIndex | npcCounter++ << 6;
					@Pc(95) int packedCoord = packet.readUShort();
					@Pc(99) int level = packedCoord >> 14;
					@Pc(105) int localX = packedCoord >> 7 & 0x3F;
					@Pc(109) int localZ = packedCoord & 0x3F;
					@Pc(122) int sceneX = localX + (MapRegion.regionIds[i] >> 8) * 64 - MapRegion.sceneBaseX;
					@Pc(136) int sceneZ = (MapRegion.regionIds[i] & 0xFF) * 64 + localZ - MapRegion.sceneBaseY;
					@Pc(143) NPCType npcType = TypeLists.NPC.list(packet.readUShort());
					@Pc(150) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) npcIndex);
					if (npcEntityNode == null && (npcType.walkingFlags & 0x1) > 0 && level == MapRegion.mapBuildLevel && sceneX >= 0 && sceneX + npcType.size < MapRegion.sceneSizeX && sceneZ >= 0 && sceneZ + npcType.size < MapRegion.sceneSizeY) {
						@Pc(192) NPCEntity npcEntity = new NPCEntity();
						npcEntity.playerIndex = npcIndex;
						@Pc(200) NPCEntityNode npcEntityNode2 = new NPCEntityNode(npcEntity);
						NPCList.NPC_MAP.put((long) npcIndex, npcEntityNode2);
						NPCList.NPC_ARRAY[NPCList.npcCount++] = npcEntityNode2;
						NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcIndex;
						npcEntity.lastUpdate = ClientState.clientCycle;
						npcEntity.setNpcType(npcType);
						npcEntity.setSize(npcEntity.npcType.size);
						npcEntity.yawSpeed = npcEntity.npcType.turnDirection << 3;
						npcEntity.turn((npcEntity.npcType.respawnDirection + 4 & 0x9CE00007) << 11, true);
						npcEntity.clearPath(sceneZ, true, sceneX, npcEntity.getSize(), level);
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!vga", name = "d", descriptor = "Lclient!eq;")
	public static final IterableHashTable NPC_MAP = new IterableHashTable(64);
	@OriginalMember(owner = "client!bca", name = "e", descriptor = "I")
	public static int npcMaskUpdateCount = 0;

	@OriginalMember(owner = "client!qw", name = "r", descriptor = "I")
	public static int npcCount = 0;

	@OriginalMember(owner = "client!oha", name = "k", descriptor = "I")
	public static int NPC_UPDATE_INDEX = 0;

	@OriginalMember(owner = "client!go", name = "a", descriptor = "(I)V")
	public static void decodeNewNpcs() {
		while (true) {
			if (BufferedConnectionContext.receivedBuffer.bitsRemaining(BufferedConnectionContext.currentPacketSize) >= 15) {
				@Pc(16) int npcIndex = BufferedConnectionContext.receivedBuffer.gbit(15);
				if (npcIndex != 32767) {
					@Pc(23) boolean isNew = false;
					@Pc(30) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) npcIndex);
					@Pc(36) NPCEntity npcEntity;
					if (npcEntityNode == null) {
						npcEntity = new NPCEntity();
						npcEntity.playerIndex = npcIndex;
						npcEntityNode = new NPCEntityNode(npcEntity);
						NPCList.NPC_MAP.put((long) npcIndex, npcEntityNode);
						isNew = true;
						NPCList.NPC_ARRAY[NPCList.npcCount++] = npcEntityNode;
					}
					npcEntity = npcEntityNode.npc;
					NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcIndex;
					npcEntity.lastUpdate = NPCList.npcUpdateCycle;
					if (npcEntity.npcType != null && npcEntity.npcType.hasSounds()) {
						AmbientSound.unlinkEntitySound(npcEntity);
					}
					@Pc(90) int plane = BufferedConnectionContext.receivedBuffer.gbit(2);
					@Pc(95) int teleport = BufferedConnectionContext.receivedBuffer.gbit(1);
					@Pc(100) int offsetY = BufferedConnectionContext.receivedBuffer.gbit(5);
					if (offsetY > 15) {
						offsetY -= 32;
					}
					@Pc(111) int offsetX = BufferedConnectionContext.receivedBuffer.gbit(5);
					if (offsetX > 15) {
						offsetX -= 32;
					}
					@Pc(128) int rotation = BufferedConnectionContext.receivedBuffer.gbit(3) + 4 << 11 & 0x3D01;
					@Pc(133) int hasMaskUpdate = BufferedConnectionContext.receivedBuffer.gbit(1);
					if (hasMaskUpdate == 1) {
						NpcUpdate.NPC_MASK_UPDATE_INDICES[NPCList.npcMaskUpdateCount++] = npcIndex;
					}
					npcEntity.setNpcType(TypeLists.NPC.list(BufferedConnectionContext.receivedBuffer.gbit(14)));
					npcEntity.setSize(npcEntity.npcType.size);
					npcEntity.yawSpeed = npcEntity.npcType.turnDirection << 3;
					if (isNew) {
						npcEntity.turn(rotation, true);
					}
					npcEntity.clearPath(PlayerUpdate.localPlayer.movementBaseY[0] + offsetY, teleport == 1, PlayerUpdate.localPlayer.movementBaseX[0] + offsetX, npcEntity.getSize(), plane);
					if (npcEntity.npcType.hasSounds()) {
						AmbientSound.createAmbientSoundEffect(npcEntity, npcEntity.level, npcEntity.movementBaseY[0], npcEntity.movementBaseX[0], null, null, 0);
					}
					continue;
				}
			}
			BufferedConnectionContext.receivedBuffer.exitBitMode();
			return;
		}
	}

	@OriginalMember(owner = "client!pia", name = "b", descriptor = "(I)V")
	public static void decodeNpcMasks() {
		for (@Pc(8) int i = 0; i < NPCList.npcMaskUpdateCount; i++) {
			@Pc(14) int npcIndex = NpcUpdate.NPC_MASK_UPDATE_INDICES[i];
			@Pc(22) NPCEntity npcEntity = ((NPCEntityNode) NPCList.NPC_MAP.get((long) npcIndex)).npc;
			@Pc(26) int updateFlag = BufferedConnectionContext.receivedBuffer.readUByte();
			if ((updateFlag & 0x10) != 0) {
				updateFlag += BufferedConnectionContext.receivedBuffer.readUByte() << 8;
			}
			if ((updateFlag & 0x2) != 0) {
				if (npcEntity.npcType.hasSounds()) {
					AmbientSound.unlinkEntitySound(npcEntity);
				}
				npcEntity.setNpcType(TypeLists.NPC.list(BufferedConnectionContext.receivedBuffer.readUShortAdd()));
				npcEntity.setSize(npcEntity.npcType.size);
				npcEntity.yawSpeed = npcEntity.npcType.turnDirection << 3;
				if (npcEntity.npcType.hasSounds()) {
					AmbientSound.createAmbientSoundEffect(npcEntity, npcEntity.level, npcEntity.movementBaseY[0], npcEntity.movementBaseX[0], null, null, 0);
				}
			}
			@Pc(112) int local112;
			@Pc(139) int local139;
			if ((updateFlag & 0x8) != 0) {
				@Pc(110) int[] animations = new int[4];
				for (int animIndex = 0; animIndex < 4; animIndex++) {
					animations[animIndex] = BufferedConnectionContext.receivedBuffer.readUShort();
					if (animations[animIndex] == 65535) {
						animations[animIndex] = -1;
					}
				}
				int animSpeed = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
				NPCEntity.animateNpc(animSpeed, animations, npcEntity);
			}
			@Pc(171) int local171;
			@Pc(177) int local177;
			@Pc(152) int local152;
			if ((updateFlag & 0x20) != 0) {
				int spotAnimationId = BufferedConnectionContext.receivedBuffer.readUShortLittle();
				int packedDelayAndHeight = BufferedConnectionContext.receivedBuffer.readUIntMiddle();
				if (spotAnimationId == 65535) {
					spotAnimationId = -1;
				}
				int spotAnimSettings = BufferedConnectionContext.receivedBuffer.readUByte();
				int rotation = spotAnimSettings & 0x7;
				int adjustmentType = spotAnimSettings >> 3 & 0xF;
				if (adjustmentType == 15) {
					adjustmentType = -1;
				}
				npcEntity.playSpotAnimation(rotation, spotAnimationId, adjustmentType, false, packedDelayAndHeight);
			}
			@Pc(219) int local219;
			@Pc(205) int[] local205;
			@Pc(208) int[] local208;
			if ((updateFlag & 0x4000) != 0) {
				int slotCount = BufferedConnectionContext.receivedBuffer.readUByteInverse();
				int[] sequenceIds = new int[slotCount];
				int[] speeds = new int[slotCount];
				@Pc(211) int[] slotMasks = new int[slotCount];
				for (int slotIndex = 0; slotIndex < slotCount; slotIndex++) {
					int sequenceId = BufferedConnectionContext.receivedBuffer.readUShort();
					if (sequenceId == 65535) {
						sequenceId = -1;
					}
					sequenceIds[slotIndex] = sequenceId;
					speeds[slotIndex] = BufferedConnectionContext.receivedBuffer.readUByteInverse();
					slotMasks[slotIndex] = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
				}
				NPCEntity.animateNpcSlots(npcEntity, slotMasks, sequenceIds, speeds);
			}
			if ((updateFlag & 0x200) != 0) {
				int targetCount = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
				int[] targets = new int[targetCount];
				int[] targetFlags = new int[targetCount];
				for (int targetIndex = 0; targetIndex < targetCount; targetIndex++) {
					int wornTarget = BufferedConnectionContext.receivedBuffer.readUShortAdd();
					if ((wornTarget & 0xC000) == 49152) {
						int wornTargetLow = BufferedConnectionContext.receivedBuffer.readUShort();
						targets[targetIndex] = wornTargetLow | wornTarget << 16;
					} else {
						targets[targetIndex] = wornTarget;
					}
					targetFlags[targetIndex] = BufferedConnectionContext.receivedBuffer.readUShort();
				}
				npcEntity.updateWornTargets(targetFlags, targets);
			}
			if ((updateFlag & 0x4) != 0) {
				npcEntity.faceX = BufferedConnectionContext.receivedBuffer.readUShortAdd();
				npcEntity.faceY = BufferedConnectionContext.receivedBuffer.readUShortLittle();
			}
			if ((updateFlag & 0x1000) != 0) {
				npcEntity.forceMovementT1XOff = BufferedConnectionContext.receivedBuffer.readByteSubtract();
				npcEntity.forceMovementT1YOff = BufferedConnectionContext.receivedBuffer.readByteSubtract();
				npcEntity.forceMovementT2XOff = BufferedConnectionContext.receivedBuffer.readByte();
				npcEntity.forceMovementT2YOff = BufferedConnectionContext.receivedBuffer.readByteSubtract();
				npcEntity.forceMovementT1Delay = BufferedConnectionContext.receivedBuffer.readUShortLittle() + ClientState.clientCycle;
				npcEntity.forceMovementT2Delay = BufferedConnectionContext.receivedBuffer.readUShortAdd() + ClientState.clientCycle;
				npcEntity.forceMoveDirection = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
				npcEntity.forceMovementT1YOff += npcEntity.movementBaseY[0];
				npcEntity.previousMovementStepsLeft = 0;
				npcEntity.forceMovementT2YOff += npcEntity.movementBaseY[0];
				npcEntity.forceMovementT1XOff += npcEntity.movementBaseX[0];
				npcEntity.forceMovementT2XOff += npcEntity.movementBaseX[0];
				npcEntity.pathPointer = 1;
			}
			if ((updateFlag & 0x2000) != 0) {
				npcEntity.hue = BufferedConnectionContext.receivedBuffer.readByteInverse();
				npcEntity.saturation = BufferedConnectionContext.receivedBuffer.readByteSubtract();
				npcEntity.lightness = BufferedConnectionContext.receivedBuffer.readByteInverse();
				npcEntity.opacity = (byte) BufferedConnectionContext.receivedBuffer.readUByte();
				npcEntity.tintStartCycle = ClientState.clientCycle + BufferedConnectionContext.receivedBuffer.readUShort();
				npcEntity.tintEndCycle = ClientState.clientCycle + BufferedConnectionContext.receivedBuffer.readUShort();
			}
			if ((updateFlag & 0x40) != 0) {
				int hitCount = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
				if (hitCount > 0) {
					for (int hitIndex = 0; hitIndex < hitCount; hitIndex++) {
						int soakHitsplatId = -1;
						int hitAmount = -1;
						int soakAmount = -1;
						int hitsplatId = BufferedConnectionContext.receivedBuffer.readUSmart();
						if (hitsplatId == 32767) {
							hitsplatId = BufferedConnectionContext.receivedBuffer.readUSmart();
							hitAmount = BufferedConnectionContext.receivedBuffer.readUSmart();
							soakHitsplatId = BufferedConnectionContext.receivedBuffer.readUSmart();
							soakAmount = BufferedConnectionContext.receivedBuffer.readUSmart();
						} else if (hitsplatId == 32766) {
							hitsplatId = -1;
						} else {
							hitAmount = BufferedConnectionContext.receivedBuffer.readUSmart();
						}
						@Pc(530) int delay = BufferedConnectionContext.receivedBuffer.readUSmart();
						@Pc(534) int healthRatio = BufferedConnectionContext.receivedBuffer.readUByte();
						npcEntity.hit(hitAmount, ClientState.clientCycle, hitsplatId, soakAmount, soakHitsplatId, delay, healthRatio);
					}
				}
			}
			if ((updateFlag & 0x80) != 0) {
				npcEntity.faceEntity = BufferedConnectionContext.receivedBuffer.readUShort();
				if (npcEntity.faceEntity == 65535) {
					npcEntity.faceEntity = -1;
				}
			}
			if ((updateFlag & 0x1) != 0) {
				npcEntity.hintDetails.text = BufferedConnectionContext.receivedBuffer.readString();
				npcEntity.hintDetails.ticksLeft = 100;
			}
			if ((updateFlag & 0x800) != 0) {
				int secondaryBarData = BufferedConnectionContext.receivedBuffer.readUShort();
				npcEntity.secondaryBarHoldCycles = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
				npcEntity.secondaryBarStep = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
				npcEntity.secondaryBarDuration = secondaryBarData & 0x7FFF;
				npcEntity.secondaryBarAlternate = (secondaryBarData & 0x8000) != 0;
				npcEntity.secondaryBarEndCycle = npcEntity.secondaryBarHoldCycles + ClientState.clientCycle + npcEntity.secondaryBarDuration;
			}
			if ((updateFlag & 0x400) != 0) {
				int spotAnimationId = BufferedConnectionContext.receivedBuffer.readUShortLittle();
				int packedDelayAndHeight = BufferedConnectionContext.receivedBuffer.readIntLE();
				if (spotAnimationId == 65535) {
					spotAnimationId = -1;
				}
				int spotAnimSettings = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
				int rotation = spotAnimSettings & 0x7;
				int adjustmentType = spotAnimSettings >> 3 & 0xF;
				if (adjustmentType == 15) {
					adjustmentType = -1;
				}
				npcEntity.playSpotAnimation(rotation, spotAnimationId, adjustmentType, true, packedDelayAndHeight);
			}
		}
	}

	@OriginalMember(owner = "client!cc", name = "V", descriptor = "[Lclient!lo;")
	public static final NPCEntityNode[] NPC_ARRAY = new NPCEntityNode[1024];

	@OriginalMember(owner = "client!lc", name = "h", descriptor = "I")
	public static int previousAreaMode = 0;

	@OriginalMember(owner = "client!afa", name = "a", descriptor = "(B)V")
	public static void resetNpcsOnAreaChange() {
		if (MapRegion.areaMode == AreaMode.CLEAR_LOCAL_NPCS || MapRegion.areaMode == AreaMode.ALLOW_OUT_OF_BOUNDS || MapRegion.areaMode != NPCList.previousAreaMode && (MapRegion.areaMode == AreaMode.STATIC_AREA || NPCList.previousAreaMode == AreaMode.STATIC_AREA)) {
			NPCList.npcCount = 0;
			NPCList.NPC_UPDATE_INDEX = 0;
			NPCList.NPC_MAP.clear();
		}
		NPCList.previousAreaMode = MapRegion.areaMode;
	}
	@OriginalMember(owner = "client!bd", name = "v", descriptor = "Z")
	public static boolean hideNpcs = false;

	@OriginalMember(owner = "client!pk", name = "x", descriptor = "I")
	public static int npcUpdateCycle = 0;

	@OriginalMember(owner = "client!fi", name = "b", descriptor = "(Z)V")
	public static void moveNpcs() {
		for (@Pc(7) int i = 0; i < NPCList.NPC_UPDATE_INDEX; i++) {
			@Pc(18) int npcIndex = NPCList.NPC_UPDATE_INDICES[i];
			@Pc(25) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) npcIndex);
			if (npcEntityNode != null) {
				@Pc(30) NPCEntity npcEntity = npcEntityNode.npc;
				EntityTick.move(npcEntity.npcType.size, npcEntity);
			}
		}
	}

	@OriginalMember(owner = "client!kn", name = "a", descriptor = "[I")
	public static final int[] NPC_UPDATE_INDICES = new int[1024];
	@OriginalMember(owner = "client!eca", name = "m", descriptor = "[I")
	public static final int[] npcSpawnRegionIds = new int[64];

	@OriginalMember(owner = "client!ht", name = "b", descriptor = "I")
	public static int npcSpawnCursorPos = 0;
}
