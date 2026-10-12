package com.jagex.game.runetek5

import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.entity.EntityOverhead
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.entity.PathingEntity
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.statics.OutPlayerIndexCount
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object EntitySceneBuilder {

	@OriginalMember(owner = "client!client", name = "d", descriptor = "()V")
	@JvmStatic
	fun calculateEntityPriorities() {
		@Pc(1) val playerCount: Int = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES
		@Pc(3) val outPlayerIndices: IntArray = PlayerUpdate.OUT_PLAYER_INDICES
		@Pc(8) val idleAnimationsSetting: Int = GamePreferences.uid!!.idleAnimations!!.getValue()
		@Pc(23) val suppressMovingAnimations: Boolean = idleAnimationsSetting == 1 && playerCount > 200 || idleAnimationsSetting == 0 && playerCount > 50
		@Pc(85) val local85: Int
		for (@Pc(25) i in 0 until playerCount) {
			@Pc(32) val playerEntity: PlayerEntity = PlayerUpdate.players[outPlayerIndices[i]]!!
			if (!playerEntity.hasAppearance()) {
				playerEntity.worldPriority = -1
			} else if (playerEntity.hidden) {
				playerEntity.worldPriority = -1
			} else {
				playerEntity.updateBounds()
				if (playerEntity.startX >= 0 && playerEntity.startZ >= 0 && playerEntity.endX < MapRegion.sceneSizeX && playerEntity.endZ < MapRegion.sceneSizeY) {
					playerEntity.shouldSuppressMovingAnimation = if (playerEntity.movingSequence.hasType) suppressMovingAnimations else false
					if (playerEntity === PlayerUpdate.localPlayer) {
						playerEntity.worldPriority = Int.MAX_VALUE
					} else {
						var drawPriority: Int = 0
						if (!playerEntity.isHidden) {
							drawPriority++
						}
						if (playerEntity.healthBarEndCycle > ClientState.clientCycle) {
							drawPriority += 2
						}
						drawPriority += (5 - playerEntity.getSize()) shl 2
						if (playerEntity.pMapdot) {
							drawPriority += 512
						} else {
							if (EntitySceneBuilder.entityDrawOrder == 0) {
								drawPriority += 32
							} else {
								drawPriority += 128
							}
							drawPriority += 256
						}
						playerEntity.worldPriority = drawPriority + 1
					}
				} else {
					playerEntity.worldPriority = -1
				}
			}
		}
		for (@Pc(126) j in 0 until NPCList.NPC_UPDATE_INDEX) {
			@Pc(137) val npcEntity: NPCEntity = (NPCList.NPC_MAP.get(NPCList.NPC_UPDATE_INDICES[j].toLong()) as NPCEntityNode).npc!!
			if (npcEntity.hasType() && npcEntity.npcType!!.resolvesToNpc(PlayerVarDomain.playerVarProvider)) {
				npcEntity.updateBounds()
				if (npcEntity.startX >= 0 && npcEntity.startZ >= 0 && npcEntity.endX < MapRegion.sceneSizeX && npcEntity.endZ < MapRegion.sceneSizeY) {
					@Pc(174) var drawPriority: Int = 0
					if (!npcEntity.isHidden) {
						drawPriority++
					}
					if (npcEntity.healthBarEndCycle > ClientState.clientCycle) {
						drawPriority += 2
					}
					drawPriority += (5 - npcEntity.getSize()) shl 2
					if (EntitySceneBuilder.entityDrawOrder == 0) {
						if (npcEntity.npcType!!.lowPriority) {
							drawPriority += 64
						} else {
							drawPriority += 128
						}
					} else if (EntitySceneBuilder.entityDrawOrder == 1) {
						if (npcEntity.npcType!!.lowPriority) {
							drawPriority += 32
						} else {
							drawPriority += 64
						}
					}
					if (npcEntity.npcType!!.highPriority) {
						drawPriority += 1024
					} else if (!npcEntity.npcType!!.mediumPriority) {
						drawPriority += 256
					}
					npcEntity.worldPriority = drawPriority + 1
				} else {
					npcEntity.worldPriority = -1
				}
			} else {
				npcEntity.worldPriority = -1
			}
		}
		for (hintArrowIndex in 0 until PlayerUpdate.HINT_ARROWS.size) {
			@Pc(240) val hintArrow: HintArrow? = PlayerUpdate.HINT_ARROWS[hintArrowIndex]
			if (hintArrow != null) {
				if (hintArrow.targetType == 1) {
					@Pc(254) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(hintArrow.targetIndex.toLong()) as NPCEntityNode?
					if (npcEntityNode != null) {
						@Pc(259) val npcEntity2: NPCEntity = npcEntityNode.npc!!
						if (npcEntity2.worldPriority >= 0) {
							npcEntity2.worldPriority += 2048
						}
					}
				} else if (hintArrow.targetType == 10) {
					@Pc(278) val playerEntity2: PlayerEntity? = PlayerUpdate.players[hintArrow.targetIndex]
					if (playerEntity2 != null && playerEntity2 !== PlayerUpdate.localPlayer && playerEntity2.worldPriority >= 0) {
						playerEntity2.worldPriority += 2048
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!client", name = "j", descriptor = "(I)V")
	@JvmStatic
	fun positionEntitiesOnPlane(@OriginalArg(0) level: Int) {
		@Pc(1) val playerCount: Int = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES
		@Pc(3) val outPlayerIndices: IntArray = PlayerUpdate.OUT_PLAYER_INDICES
		@Pc(11) val totalIndices: Int = if (NPCList.hideNpcs) playerCount else playerCount + NPCList.NPC_UPDATE_INDEX
		for (@Pc(13) i in 0 until totalIndices) {
			@Pc(23) val pathingEntity: PathingEntity
			if (i < playerCount) {
				pathingEntity = PlayerUpdate.players[outPlayerIndices[i]]!!
			} else {
				pathingEntity = (NPCList.NPC_MAP.get(NPCList.NPC_UPDATE_INDICES[i - playerCount].toLong()) as NPCEntityNode).npc!!
			}
			if (pathingEntity.level.toInt() == level) {
				pathingEntity.overheadStackCount = 0
				if (pathingEntity.worldPriority < 0) {
					pathingEntity.isHidden = false
				} else {
					@Pc(54) val size: Int = pathingEntity.getSize()
					if ((size and 0x1) == 0) {
						if ((pathingEntity.x and 0x1FF) != 0 || (pathingEntity.z and 0x1FF) != 0) {
							pathingEntity.isHidden = false
							continue
						}
					} else if ((pathingEntity.x and 0x1FF) != 256 || (pathingEntity.z and 0x1FF) != 256) {
						pathingEntity.isHidden = false
						continue
					}
					@Pc(96) val local96: Int
					@Pc(101) val local101: Int
					@Pc(126) var previousCount: Int
					if (size == 1) {
						val tileX: Int = pathingEntity.x shr 9
						val tileZ: Int = pathingEntity.z shr 9
						if (pathingEntity.worldPriority != EntitySceneBuilder.tileDrawPriority!![tileX]!![tileZ]) {
							pathingEntity.isHidden = true
							continue
						}
						if (EntitySceneBuilder.tileDrawPrioritySharedBy!![tileX]!![tileZ] > 1) {
							previousCount = EntitySceneBuilder.tileDrawPrioritySharedBy!![tileX]!![tileZ]--
							pathingEntity.isHidden = true
							continue
						}
					} else {
						val sizeOffset: Int = (size - 1) * 256 + 252
						val startX: Int = (pathingEntity.x - sizeOffset) shr 9
						@Pc(155) val startZ: Int = (pathingEntity.z - sizeOffset) shr 9
						@Pc(162) val endX: Int = (pathingEntity.x + sizeOffset) shr 9
						@Pc(169) val endZ: Int = (pathingEntity.z + sizeOffset) shr 9
						if (!EntitySceneBuilder.hasExclusiveDrawPriorityTile(startZ, endX, pathingEntity.worldPriority, startX, endZ)) {
							for (@Pc(180) j in startX..endX) {
								for (@Pc(183) k in startZ..endZ) {
									if (pathingEntity.worldPriority == EntitySceneBuilder.tileDrawPriority!![j]!![k]) {
										previousCount = EntitySceneBuilder.tileDrawPrioritySharedBy!![j]!![k]--
									}
								}
							}
							pathingEntity.isHidden = true
							continue
						}
					}
					pathingEntity.isHidden = false
					pathingEntity.y = Scene.tileHeightForAbsolutePosition(pathingEntity.x, pathingEntity.level.toInt(), pathingEntity.z)
					Scene.addInteractive(pathingEntity, true)
				}
			}
		}
	}

	@OriginalMember(owner = "client!client", name = "m", descriptor = "(I)V")
	@JvmStatic
	fun updateDrawPriority(@OriginalArg(0) level: Int) {
		@Pc(1) val playerCount: Int = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES
		@Pc(3) val outPlayerIndices: IntArray = PlayerUpdate.OUT_PLAYER_INDICES
		@Pc(5) var i: Int = 0
		while (i < playerCount + NPCList.NPC_UPDATE_INDEX) {
			@Pc(15) val pathingEntity: PathingEntity
			if (i < playerCount) {
				pathingEntity = PlayerUpdate.players[outPlayerIndices[i]]!!
			} else {
				pathingEntity = (NPCList.NPC_MAP.get(NPCList.NPC_UPDATE_INDICES[i - playerCount].toLong()) as NPCEntityNode).npc!!
			}
			if (pathingEntity.level.toInt() == level && pathingEntity.worldPriority >= 0) {
				@Pc(39) val size: Int = pathingEntity.getSize()
				if ((size and 0x1) == 0) {
					if ((pathingEntity.x and 0x1FF) != 0 || (pathingEntity.z and 0x1FF) != 0) {
						i++
						continue
					}
				} else if ((pathingEntity.x and 0x1FF) != 256 || (pathingEntity.z and 0x1FF) != 256) {
					i++
					continue
				}
				@Pc(75) val local75: Int
				@Pc(80) val local80: Int
				@Pc(116) var previousCount: Int
				if (size == 1) {
					val tileX: Int = pathingEntity.x shr 9
					val tileZ: Int = pathingEntity.z shr 9
					if (pathingEntity.worldPriority > EntitySceneBuilder.tileDrawPriority!![tileX]!![tileZ]) {
						EntitySceneBuilder.tileDrawPriority!![tileX]!![tileZ] = pathingEntity.worldPriority
						EntitySceneBuilder.tileDrawPrioritySharedBy!![tileX]!![tileZ] = 1
					} else if (pathingEntity.worldPriority == EntitySceneBuilder.tileDrawPriority!![tileX]!![tileZ]) {
						previousCount = EntitySceneBuilder.tileDrawPrioritySharedBy!![tileX]!![tileZ]++
					}
				} else {
					val sizeOffset: Int = (size - 1) * 256 + 60
					val startX: Int = (pathingEntity.x - sizeOffset) shr 9
					@Pc(142) val startZ: Int = (pathingEntity.z - sizeOffset) shr 9
					@Pc(149) val endX: Int = (pathingEntity.x + sizeOffset) shr 9
					@Pc(156) val endZ: Int = (pathingEntity.z + sizeOffset) shr 9
					for (@Pc(158) j in startX..endX) {
						for (@Pc(161) k in startZ..endZ) {
							if (pathingEntity.worldPriority > EntitySceneBuilder.tileDrawPriority!![j]!![k]) {
								EntitySceneBuilder.tileDrawPriority!![j]!![k] = pathingEntity.worldPriority
								EntitySceneBuilder.tileDrawPrioritySharedBy!![j]!![k] = 1
							} else if (pathingEntity.worldPriority == EntitySceneBuilder.tileDrawPriority!![j]!![k]) {
								previousCount = EntitySceneBuilder.tileDrawPrioritySharedBy!![j]!![k]++
							}
						}
					}
				}
			}
			i++
		}
	}

	@OriginalMember(owner = "client!ida", name = "f", descriptor = "I")
	@JvmField
	var entityDrawOrder: Int = 0

	@OriginalMember(owner = "client!qq", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun positionAllEntities() {
		@Pc(10) var overhead: EntityOverhead? = EntityOverheadAllocator.aClass325_7.removeFirst() as EntityOverhead?
		while (overhead != null) {
			EntityOverhead.release(overhead)
			overhead = EntityOverheadAllocator.aClass325_7.removeFirst() as EntityOverhead?
		}
		@Pc(32) val startLevel: Int
		@Pc(31) val endLevel: Int
		if (GamePreferences.uid!!.animateBackgroundOverride!!.getValue() == 1) {
			startLevel = 0
			endLevel = 3
		} else {
			endLevel = MapRegion.mapBuildLevel
			startLevel = MapRegion.mapBuildLevel
		}
		EntitySceneBuilder.calculateEntityPriorities()
		for (@Pc(41) i in startLevel..endLevel) {
			EntitySceneBuilder.resetDrawPriorities()
			EntitySceneBuilder.updateDrawPriority(i)
			EntitySceneBuilder.positionEntitiesOnPlane(i)
		}
		PathingEntity.buildOverlappingEntityList()
		PlayerUpdate.positionMisalignedEntities()
	}

	@OriginalMember(owner = "client!mfa", name = "y", descriptor = "[[I")
	@JvmField
	var tileDrawPrioritySharedBy: Array<IntArray?>? = null

	@OriginalMember(owner = "client!gw", name = "F", descriptor = "[[I")
	@JvmField
	var tileDrawPriority: Array<IntArray?>? = null

	@OriginalMember(owner = "client!mh", name = "a", descriptor = "(IIIBII)Z")
	@JvmStatic
	fun hasExclusiveDrawPriorityTile(@OriginalArg(0) startZ: Int, @OriginalArg(1) endX: Int, @OriginalArg(2) priority: Int, @OriginalArg(4) startX: Int, @OriginalArg(5) endZ: Int): Boolean {
		for (@Pc(12) i in startX..endX) {
			for (@Pc(16) j in startZ..endZ) {
				if (priority == EntitySceneBuilder.tileDrawPriority!![i]!![j] && EntitySceneBuilder.tileDrawPrioritySharedBy!![i]!![j] <= 1) {
					return true
				}
			}
		}
		return false
	}

	@OriginalMember(owner = "client!client", name = "b", descriptor = "()V")
	@JvmStatic
	fun resetDrawPriorities() {
		@Pc(1) var i: Int = 0
		while (i < MapRegion.sceneSizeX) {
			@Pc(6) val column: IntArray = EntitySceneBuilder.tileDrawPriority!![i]!!
			@Pc(8) var j: Int = 0
			while (j < MapRegion.sceneSizeY) {
				column[j] = 0
				j++
			}
			i++
		}
	}
}
