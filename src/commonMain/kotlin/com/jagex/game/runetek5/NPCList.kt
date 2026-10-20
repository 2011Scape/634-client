package com.jagex.game.runetek5

import com.jagex.game.runetek5.plugin.PluginRuntime

import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.NpcUpdate
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.npc.NPCType
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.core.constants.AreaMode
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.sound.player.area.AmbientSound
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object NPCList {

	@OriginalMember(owner = "client!ri", name = "s", descriptor = "I")
	@JvmField
	var newSize: Int = 0

	@OriginalMember(owner = "client!oba", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun decodeAddRemove() {
		BufferedConnectionContext.receivedBuffer.enterBitMode()
		@Pc(15) val knownNpcCount: Int = BufferedConnectionContext.receivedBuffer.gbit(8)
		@Pc(28) var i: Int
		if (NPCList.NPC_UPDATE_INDEX > knownNpcCount) {
			i = knownNpcCount
			while (i < NPCList.NPC_UPDATE_INDEX) {
				NPCList.NPC_REMOVAL_INDICES[NPCList.npcRemovalCount++] = NPCList.NPC_UPDATE_INDICES[i]
				i++
			}
		}
		if (NPCList.NPC_UPDATE_INDEX < knownNpcCount) {
			throw RuntimeException("gnpov1")
		}
		NPCList.NPC_UPDATE_INDEX = 0
		i = 0
		while (i < knownNpcCount) {
			@Pc(66) val npcIndex: Int = NPCList.NPC_UPDATE_INDICES[i]
			@Pc(74) val npcEntity: NPCEntity = (NPCList.NPC_MAP.get(npcIndex.toLong()) as NPCEntityNode?)!!.npc!!
			@Pc(79) val updateRequired: Int = BufferedConnectionContext.receivedBuffer.gbit(1)
			if (updateRequired == 0) {
				NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcIndex
				npcEntity.lastUpdate = NPCList.npcUpdateCycle
			} else {
				@Pc(102) val updateType: Int = BufferedConnectionContext.receivedBuffer.gbit(2)
				if (updateType == 0) {
					NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcIndex
					npcEntity.lastUpdate = NPCList.npcUpdateCycle
					NpcUpdate.NPC_MASK_UPDATE_INDICES[NPCList.npcMaskUpdateCount++] = npcIndex
				} else {
					@Pc(146) var local146: Int
					@Pc(156) var local156: Int
					if (updateType == 1) {
						NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcIndex
						npcEntity.lastUpdate = NPCList.npcUpdateCycle
						val direction: Int = BufferedConnectionContext.receivedBuffer.gbit(3)
						npcEntity.move(direction, 1)
						val maskUpdateRequired: Int = BufferedConnectionContext.receivedBuffer.gbit(1)
						if (maskUpdateRequired == 1) {
							NpcUpdate.NPC_MASK_UPDATE_INDICES[NPCList.npcMaskUpdateCount++] = npcIndex
						}
					} else if (updateType == NpcUpdate.RUN_OR_CRAWL) {
						NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcIndex
						npcEntity.lastUpdate = NPCList.npcUpdateCycle
						if (BufferedConnectionContext.receivedBuffer.gbit(1) == 1) {
							val walkDirection: Int = BufferedConnectionContext.receivedBuffer.gbit(3)
							npcEntity.move(walkDirection, 2)
							val runDirection: Int = BufferedConnectionContext.receivedBuffer.gbit(3)
							npcEntity.move(runDirection, 2)
						} else {
							val direction: Int = BufferedConnectionContext.receivedBuffer.gbit(3)
							npcEntity.move(direction, 0)
						}
						val maskUpdateRequired: Int = BufferedConnectionContext.receivedBuffer.gbit(1)
						if (maskUpdateRequired == 1) {
							NpcUpdate.NPC_MASK_UPDATE_INDICES[NPCList.npcMaskUpdateCount++] = npcIndex
						}
					} else if (updateType == NpcUpdate.REMOVE) {
						NPCList.NPC_REMOVAL_INDICES[NPCList.npcRemovalCount++] = npcIndex
					}
				}
			}
			i++
		}
	}
	@OriginalMember(owner = "client!ew", name = "a", descriptor = "I")
	@JvmField
	var npcRemovalCount: Int = 0
	@OriginalMember(owner = "client!ww", name = "M", descriptor = "[I")
	@JvmField
	val NPC_REMOVAL_INDICES: IntArray = IntArray(1000)

	@OriginalMember(owner = "client!lia", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun decodeNpcSpawns() {
		@Pc(8) val length: Int = MapRegion.npcSpawnBuffers!!.size
		@Pc(14) var i: Int = 0
		while (i < length) {
			if (MapRegion.npcSpawnBuffers!![i] != null) {
				@Pc(22) var mapSquareIndex: Int = -1
				@Pc(24) var j: Int = 0
				while (j < NPCList.npcSpawnCursorPos) {
					if (MapRegion.regionIds!![i] == NPCList.npcSpawnRegionIds[j]) {
						mapSquareIndex = j
						break
					}
					j++
				}
				if (mapSquareIndex == -1) {
					NPCList.npcSpawnRegionIds[NPCList.npcSpawnCursorPos] = MapRegion.regionIds!![i]
					mapSquareIndex = NPCList.npcSpawnCursorPos++
				}
				@Pc(68) val packet: Packet = Packet(MapRegion.npcSpawnBuffers!![i])
				@Pc(70) var npcCounter: Int = 0
				while (MapRegion.npcSpawnBuffers!![i]!!.size > packet.currentPosition && npcCounter < 511 && NPCList.NPC_UPDATE_INDEX < 1023) {
					@Pc(91) val npcIndex: Int = mapSquareIndex or (npcCounter++ shl 6)
					@Pc(95) val packedCoord: Int = packet.readUShort()
					@Pc(99) val level: Int = packedCoord shr 14
					@Pc(105) val localX: Int = (packedCoord shr 7) and 0x3F
					@Pc(109) val localZ: Int = packedCoord and 0x3F
					@Pc(122) val sceneX: Int = localX + (MapRegion.regionIds!![i] shr 8) * 64 - MapRegion.sceneBaseX
					@Pc(136) val sceneZ: Int = (MapRegion.regionIds!![i] and 0xFF) * 64 + localZ - MapRegion.sceneBaseY
					@Pc(143) val npcType: NPCType = TypeLists.NPC!!.list(packet.readUShort())
					@Pc(150) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(npcIndex.toLong()) as NPCEntityNode?
					if (npcEntityNode == null && (npcType.walkingFlags.toInt() and 0x1) > 0 && level == MapRegion.mapBuildLevel && sceneX >= 0 && sceneX + npcType.size < MapRegion.sceneSizeX && sceneZ >= 0 && sceneZ + npcType.size < MapRegion.sceneSizeY) {
						@Pc(192) val npcEntity: NPCEntity = NPCEntity()
						npcEntity.playerIndex = npcIndex
						@Pc(200) val npcEntityNode2: NPCEntityNode = NPCEntityNode(npcEntity)
						NPCList.NPC_MAP.put(npcIndex.toLong(), npcEntityNode2)
						NPCList.NPC_ARRAY[NPCList.npcCount++] = npcEntityNode2
						NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcIndex
						npcEntity.lastUpdate = ClientState.clientCycle
						npcEntity.setNpcType(npcType)
						npcEntity.setSize(npcEntity.npcType!!.size)
						npcEntity.yawSpeed = npcEntity.npcType!!.turnDirection shl 3
						npcEntity.turn(((npcEntity.npcType!!.respawnDirection + 4) and 0x9CE00007.toInt()) shl 11, true)
						npcEntity.clearPath(sceneZ, true, sceneX, npcEntity.getSize(), level)
						PluginRuntime.npcSpawned(npcEntity) // Not part of the original client: the plugins' NpcSpawned.
					}
				}
			}
			i++
		}
	}

	@OriginalMember(owner = "client!vga", name = "d", descriptor = "Lclient!eq;")
	@JvmField
	val NPC_MAP: IterableHashTable = IterableHashTable(64)
	@OriginalMember(owner = "client!bca", name = "e", descriptor = "I")
	@JvmField
	var npcMaskUpdateCount: Int = 0

	@OriginalMember(owner = "client!qw", name = "r", descriptor = "I")
	@JvmField
	var npcCount: Int = 0

	@OriginalMember(owner = "client!oha", name = "k", descriptor = "I")
	@JvmField
	var NPC_UPDATE_INDEX: Int = 0

	@OriginalMember(owner = "client!go", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun decodeNewNpcs() {
		while (true) {
			if (BufferedConnectionContext.receivedBuffer.bitsRemaining(BufferedConnectionContext.currentPacketSize) >= 15) {
				@Pc(16) val npcIndex: Int = BufferedConnectionContext.receivedBuffer.gbit(15)
				if (npcIndex != 32767) {
					@Pc(23) var isNew: Boolean = false
					@Pc(30) var npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(npcIndex.toLong()) as NPCEntityNode?
					@Pc(36) var npcEntity: NPCEntity
					if (npcEntityNode == null) {
						npcEntity = NPCEntity()
						npcEntity.playerIndex = npcIndex
						npcEntityNode = NPCEntityNode(npcEntity)
						NPCList.NPC_MAP.put(npcIndex.toLong(), npcEntityNode)
						isNew = true
						NPCList.NPC_ARRAY[NPCList.npcCount++] = npcEntityNode
					}
					npcEntity = npcEntityNode.npc!!
					NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcIndex
					npcEntity.lastUpdate = NPCList.npcUpdateCycle
					if (npcEntity.npcType != null && npcEntity.npcType!!.hasSounds()) {
						AmbientSound.unlinkEntitySound(npcEntity)
					}
					@Pc(90) val plane: Int = BufferedConnectionContext.receivedBuffer.gbit(2)
					@Pc(95) val teleport: Int = BufferedConnectionContext.receivedBuffer.gbit(1)
					@Pc(100) var offsetY: Int = BufferedConnectionContext.receivedBuffer.gbit(5)
					if (offsetY > 15) {
						offsetY -= 32
					}
					@Pc(111) var offsetX: Int = BufferedConnectionContext.receivedBuffer.gbit(5)
					if (offsetX > 15) {
						offsetX -= 32
					}
					@Pc(128) val rotation: Int = ((BufferedConnectionContext.receivedBuffer.gbit(3) + 4) shl 11) and 0x3D01
					@Pc(133) val hasMaskUpdate: Int = BufferedConnectionContext.receivedBuffer.gbit(1)
					if (hasMaskUpdate == 1) {
						NpcUpdate.NPC_MASK_UPDATE_INDICES[NPCList.npcMaskUpdateCount++] = npcIndex
					}
					npcEntity.setNpcType(TypeLists.NPC!!.list(BufferedConnectionContext.receivedBuffer.gbit(14)))
					npcEntity.setSize(npcEntity.npcType!!.size)
					npcEntity.yawSpeed = npcEntity.npcType!!.turnDirection shl 3
					if (isNew) {
						npcEntity.turn(rotation, true)
					}
					npcEntity.clearPath(PlayerUpdate.localPlayer!!.movementBaseY[0] + offsetY, teleport == 1, PlayerUpdate.localPlayer!!.movementBaseX[0] + offsetX, npcEntity.getSize(), plane)
					if (isNew) {
						PluginRuntime.npcSpawned(npcEntity) // Not part of the original client: the plugins' NpcSpawned.
					}
					if (npcEntity.npcType!!.hasSounds()) {
						AmbientSound.createAmbientSoundEffect(npcEntity, npcEntity.level.toInt(), npcEntity.movementBaseY[0], npcEntity.movementBaseX[0], null, null, 0)
					}
					continue
				}
			}
			BufferedConnectionContext.receivedBuffer.exitBitMode()
			return
		}
	}

	@OriginalMember(owner = "client!pia", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun decodeNpcMasks() {
		@Pc(8) var i: Int = 0
		while (i < NPCList.npcMaskUpdateCount) {
			@Pc(14) val npcIndex: Int = NpcUpdate.NPC_MASK_UPDATE_INDICES[i]
			@Pc(22) val npcEntity: NPCEntity = (NPCList.NPC_MAP.get(npcIndex.toLong()) as NPCEntityNode?)!!.npc!!
			@Pc(26) var updateFlag: Int = BufferedConnectionContext.receivedBuffer.readUByte()
			if ((updateFlag and 0x10) != 0) {
				updateFlag += BufferedConnectionContext.receivedBuffer.readUByte() shl 8
			}
			if ((updateFlag and 0x2) != 0) {
				if (npcEntity.npcType!!.hasSounds()) {
					AmbientSound.unlinkEntitySound(npcEntity)
				}
				npcEntity.setNpcType(TypeLists.NPC!!.list(BufferedConnectionContext.receivedBuffer.readUShortAdd()))
				npcEntity.setSize(npcEntity.npcType!!.size)
				npcEntity.yawSpeed = npcEntity.npcType!!.turnDirection shl 3
				if (npcEntity.npcType!!.hasSounds()) {
					AmbientSound.createAmbientSoundEffect(npcEntity, npcEntity.level.toInt(), npcEntity.movementBaseY[0], npcEntity.movementBaseX[0], null, null, 0)
				}
			}
			@Pc(112) var local112: Int
			@Pc(139) var local139: Int
			if ((updateFlag and 0x8) != 0) {
				@Pc(110) val animations: IntArray = IntArray(4)
				var animIndex: Int = 0
				while (animIndex < 4) {
					animations[animIndex] = BufferedConnectionContext.receivedBuffer.readUShort()
					if (animations[animIndex] == 65535) {
						animations[animIndex] = -1
					}
					animIndex++
				}
				val animSpeed: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
				NPCEntity.animateNpc(animSpeed, animations, npcEntity)
			}
			@Pc(171) var local171: Int
			@Pc(177) var local177: Int
			@Pc(152) var local152: Int
			if ((updateFlag and 0x20) != 0) {
				var spotAnimationId: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle()
				val packedDelayAndHeight: Int = BufferedConnectionContext.receivedBuffer.readUIntMiddle()
				if (spotAnimationId == 65535) {
					spotAnimationId = -1
				}
				val spotAnimSettings: Int = BufferedConnectionContext.receivedBuffer.readUByte()
				val rotation: Int = spotAnimSettings and 0x7
				var adjustmentType: Int = (spotAnimSettings shr 3) and 0xF
				if (adjustmentType == 15) {
					adjustmentType = -1
				}
				npcEntity.playSpotAnimation(rotation, spotAnimationId, adjustmentType, false, packedDelayAndHeight)
			}
			@Pc(219) var local219: Int
			@Pc(205) var local205: IntArray
			@Pc(208) var local208: IntArray
			if ((updateFlag and 0x4000) != 0) {
				val slotCount: Int = BufferedConnectionContext.receivedBuffer.readUByteInverse()
				val sequenceIds: IntArray = IntArray(slotCount)
				val speeds: IntArray = IntArray(slotCount)
				@Pc(211) val slotMasks: IntArray = IntArray(slotCount)
				var slotIndex: Int = 0
				while (slotIndex < slotCount) {
					var sequenceId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
					if (sequenceId == 65535) {
						sequenceId = -1
					}
					sequenceIds[slotIndex] = sequenceId
					speeds[slotIndex] = BufferedConnectionContext.receivedBuffer.readUByteInverse()
					slotMasks[slotIndex] = BufferedConnectionContext.receivedBuffer.readUShortAddLittle()
					slotIndex++
				}
				NPCEntity.animateNpcSlots(npcEntity, slotMasks, sequenceIds, speeds)
			}
			if ((updateFlag and 0x200) != 0) {
				val targetCount: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
				val targets: IntArray = IntArray(targetCount)
				val targetFlags: IntArray = IntArray(targetCount)
				var targetIndex: Int = 0
				while (targetIndex < targetCount) {
					val wornTarget: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
					if ((wornTarget and 0xC000) == 49152) {
						val wornTargetLow: Int = BufferedConnectionContext.receivedBuffer.readUShort()
						targets[targetIndex] = wornTargetLow or (wornTarget shl 16)
					} else {
						targets[targetIndex] = wornTarget
					}
					targetFlags[targetIndex] = BufferedConnectionContext.receivedBuffer.readUShort()
					targetIndex++
				}
				npcEntity.updateWornTargets(targetFlags, targets)
			}
			if ((updateFlag and 0x4) != 0) {
				npcEntity.faceX = BufferedConnectionContext.receivedBuffer.readUShortAdd()
				npcEntity.faceY = BufferedConnectionContext.receivedBuffer.readUShortLittle()
			}
			if ((updateFlag and 0x1000) != 0) {
				npcEntity.forceMovementT1XOff = BufferedConnectionContext.receivedBuffer.readByteSubtract().toInt()
				npcEntity.forceMovementT1YOff = BufferedConnectionContext.receivedBuffer.readByteSubtract().toInt()
				npcEntity.forceMovementT2XOff = BufferedConnectionContext.receivedBuffer.readByte().toInt()
				npcEntity.forceMovementT2YOff = BufferedConnectionContext.receivedBuffer.readByteSubtract().toInt()
				npcEntity.forceMovementT1Delay = BufferedConnectionContext.receivedBuffer.readUShortLittle() + ClientState.clientCycle
				npcEntity.forceMovementT2Delay = BufferedConnectionContext.receivedBuffer.readUShortAdd() + ClientState.clientCycle
				npcEntity.forceMoveDirection = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
				npcEntity.forceMovementT1YOff += npcEntity.movementBaseY[0]
				npcEntity.previousMovementStepsLeft = 0
				npcEntity.forceMovementT2YOff += npcEntity.movementBaseY[0]
				npcEntity.forceMovementT1XOff += npcEntity.movementBaseX[0]
				npcEntity.forceMovementT2XOff += npcEntity.movementBaseX[0]
				npcEntity.pathPointer = 1
			}
			if ((updateFlag and 0x2000) != 0) {
				npcEntity.hue = BufferedConnectionContext.receivedBuffer.readByteInverse()
				npcEntity.saturation = BufferedConnectionContext.receivedBuffer.readByteSubtract()
				npcEntity.lightness = BufferedConnectionContext.receivedBuffer.readByteInverse()
				npcEntity.opacity = BufferedConnectionContext.receivedBuffer.readUByte().toByte()
				npcEntity.tintStartCycle = ClientState.clientCycle + BufferedConnectionContext.receivedBuffer.readUShort()
				npcEntity.tintEndCycle = ClientState.clientCycle + BufferedConnectionContext.receivedBuffer.readUShort()
			}
			if ((updateFlag and 0x40) != 0) {
				val hitCount: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
				if (hitCount > 0) {
					var hitIndex: Int = 0
					while (hitIndex < hitCount) {
						var soakHitsplatId: Int = -1
						var hitAmount: Int = -1
						var soakAmount: Int = -1
						var hitsplatId: Int = BufferedConnectionContext.receivedBuffer.readUSmart()
						if (hitsplatId == 32767) {
							hitsplatId = BufferedConnectionContext.receivedBuffer.readUSmart()
							hitAmount = BufferedConnectionContext.receivedBuffer.readUSmart()
							soakHitsplatId = BufferedConnectionContext.receivedBuffer.readUSmart()
							soakAmount = BufferedConnectionContext.receivedBuffer.readUSmart()
						} else if (hitsplatId == 32766) {
							hitsplatId = -1
						} else {
							hitAmount = BufferedConnectionContext.receivedBuffer.readUSmart()
						}
						@Pc(530) val delay: Int = BufferedConnectionContext.receivedBuffer.readUSmart()
						@Pc(534) val healthRatio: Int = BufferedConnectionContext.receivedBuffer.readUByte()
						npcEntity.hit(hitAmount, ClientState.clientCycle, hitsplatId, soakAmount, soakHitsplatId, delay, healthRatio)
						hitIndex++
					}
				}
			}
			if ((updateFlag and 0x80) != 0) {
				npcEntity.faceEntity = BufferedConnectionContext.receivedBuffer.readUShort()
				if (npcEntity.faceEntity == 65535) {
					npcEntity.faceEntity = -1
				}
				PluginRuntime.interactingChanged(npcEntity) // Not part of the original client: the plugins' InteractingChanged.
			}
			if ((updateFlag and 0x1) != 0) {
				npcEntity.hintDetails.text = BufferedConnectionContext.receivedBuffer.readString()
				npcEntity.hintDetails.ticksLeft = 100
				PluginRuntime.overheadTextChanged(npcEntity, npcEntity.hintDetails.text) // Not part of the original client: the plugins' OverheadTextChanged.
			}
			if ((updateFlag and 0x800) != 0) {
				val secondaryBarData: Int = BufferedConnectionContext.receivedBuffer.readUShort()
				npcEntity.secondaryBarHoldCycles = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
				npcEntity.secondaryBarStep = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
				npcEntity.secondaryBarDuration = secondaryBarData and 0x7FFF
				npcEntity.secondaryBarAlternate = (secondaryBarData and 0x8000) != 0
				npcEntity.secondaryBarEndCycle = npcEntity.secondaryBarHoldCycles + ClientState.clientCycle + npcEntity.secondaryBarDuration
			}
			if ((updateFlag and 0x400) != 0) {
				var spotAnimationId: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle()
				val packedDelayAndHeight: Int = BufferedConnectionContext.receivedBuffer.readIntLE()
				if (spotAnimationId == 65535) {
					spotAnimationId = -1
				}
				val spotAnimSettings: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
				val rotation: Int = spotAnimSettings and 0x7
				var adjustmentType: Int = (spotAnimSettings shr 3) and 0xF
				if (adjustmentType == 15) {
					adjustmentType = -1
				}
				npcEntity.playSpotAnimation(rotation, spotAnimationId, adjustmentType, true, packedDelayAndHeight)
			}
			i++
		}
	}

	@OriginalMember(owner = "client!cc", name = "V", descriptor = "[Lclient!lo;")
	@JvmField
	val NPC_ARRAY: Array<NPCEntityNode?> = arrayOfNulls<NPCEntityNode>(1024)

	@OriginalMember(owner = "client!lc", name = "h", descriptor = "I")
	@JvmField
	var previousAreaMode: Int = 0

	@OriginalMember(owner = "client!afa", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun resetNpcsOnAreaChange() {
		if (MapRegion.areaMode == AreaMode.CLEAR_LOCAL_NPCS || MapRegion.areaMode == AreaMode.ALLOW_OUT_OF_BOUNDS || MapRegion.areaMode != NPCList.previousAreaMode && (MapRegion.areaMode == AreaMode.STATIC_AREA || NPCList.previousAreaMode == AreaMode.STATIC_AREA)) {
			// Not part of the original client: the plugins' NpcDespawned for every npc the area change drops.
			for (i in 0 until NPCList.npcCount) {
				PluginRuntime.npcDespawned(NPCList.NPC_ARRAY[i]?.npc)
			}
			NPCList.npcCount = 0
			NPCList.NPC_UPDATE_INDEX = 0
			NPCList.NPC_MAP.clear()
		}
		NPCList.previousAreaMode = MapRegion.areaMode
	}
	@OriginalMember(owner = "client!bd", name = "v", descriptor = "Z")
	@JvmField
	var hideNpcs: Boolean = false

	@OriginalMember(owner = "client!pk", name = "x", descriptor = "I")
	@JvmField
	var npcUpdateCycle: Int = 0

	@OriginalMember(owner = "client!fi", name = "b", descriptor = "(Z)V")
	@JvmStatic
	fun moveNpcs() {
		@Pc(7) var i: Int = 0
		while (i < NPCList.NPC_UPDATE_INDEX) {
			@Pc(18) val npcIndex: Int = NPCList.NPC_UPDATE_INDICES[i]
			@Pc(25) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(npcIndex.toLong()) as NPCEntityNode?
			if (npcEntityNode != null) {
				@Pc(30) val npcEntity: NPCEntity? = npcEntityNode.npc
				EntityTick.move(npcEntity!!.npcType!!.size, npcEntity)
			}
			i++
		}
	}

	@OriginalMember(owner = "client!kn", name = "a", descriptor = "[I")
	@JvmField
	val NPC_UPDATE_INDICES: IntArray = IntArray(1024)
	@OriginalMember(owner = "client!eca", name = "m", descriptor = "[I")
	@JvmField
	val npcSpawnRegionIds: IntArray = IntArray(64)

	@OriginalMember(owner = "client!ht", name = "b", descriptor = "I")
	@JvmField
	var npcSpawnCursorPos: Int = 0
}
