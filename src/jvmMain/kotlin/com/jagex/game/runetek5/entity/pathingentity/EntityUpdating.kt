package com.jagex.game.runetek5.entity.pathingentity

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.EntityMoveFlag
import com.jagex.game.runetek5.MoveSpeed
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.bas.BasType
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.PathingEntity
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.ClientState

object EntityUpdating {
	@OriginalMember(owner = "client!bv", name = "m", descriptor = "I")
	@JvmField
	var currentMovementType: Int = 0

	@OriginalMember(owner = "client!ep", name = "u", descriptor = "I")
	@JvmField
	var turnDirectionIndex: Int = 0

	@OriginalMember(owner = "client!sb", name = "a", descriptor = "(ZILclient!kda;)V")
	@JvmStatic
	fun updatePosition(@OriginalArg(0) cutscene: Boolean, @OriginalArg(2) pathingEntity: PathingEntity) {
		@Pc(9) val basType: BasType? = pathingEntity.getBasType()
		if (pathingEntity.pathPointer == 0) {
			pathingEntity.stalledStepCount = 0
			EntityUpdating.currentMovementType = -1
			EntityUpdating.turnDirectionIndex = 0
			return
		}
		if (pathingEntity.currentSequence.sequenceId != -1 && pathingEntity.currentSequence.speed == 0) {
			@Pc(40) val seqType: SeqType = TypeLists.SEQ!!.list(pathingEntity.currentSequence.sequenceId)
			if (pathingEntity.previousMovementStepsLeft > 0 && seqType.interruptType == 0) {
				EntityUpdating.currentMovementType = -1
				EntityUpdating.turnDirectionIndex = 0
				pathingEntity.stalledStepCount++
				return
			}
			if (pathingEntity.previousMovementStepsLeft <= 0 && seqType.moveType == 0) {
				EntityUpdating.turnDirectionIndex = 0
				EntityUpdating.currentMovementType = -1
				pathingEntity.stalledStepCount++
				return
			}
		}
		@Pc(109) var seqType2: SeqType
		@Pc(95) var spotAnimationType: SpotAnimationType
		if (pathingEntity.primarySpotAnimation.spotAnimationId != -1 && pathingEntity.primarySpotAnimation.startCycle <= ClientState.clientCycle) {
			spotAnimationType = TypeLists.SPOT_ANIM!!.list(pathingEntity.primarySpotAnimation.spotAnimationId)
			if (spotAnimationType.replay && spotAnimationType.sequenceId != -1) {
				seqType2 = TypeLists.SEQ!!.list(spotAnimationType.sequenceId)
				if (pathingEntity.previousMovementStepsLeft > 0 && seqType2.interruptType == 0) {
					EntityUpdating.turnDirectionIndex = 0
					pathingEntity.stalledStepCount++
					EntityUpdating.currentMovementType = -1
					return
				}
				if (pathingEntity.previousMovementStepsLeft <= 0 && seqType2.moveType == 0) {
					EntityUpdating.currentMovementType = -1
					EntityUpdating.turnDirectionIndex = 0
					pathingEntity.stalledStepCount++
					return
				}
			}
		}
		if (pathingEntity.primarySpotAnimation.spotAnimationId != -1 && ClientState.clientCycle >= pathingEntity.primarySpotAnimation.startCycle) {
			spotAnimationType = TypeLists.SPOT_ANIM!!.list(pathingEntity.primarySpotAnimation.spotAnimationId)
			if (spotAnimationType.replay && spotAnimationType.sequenceId != -1) {
				seqType2 = TypeLists.SEQ!!.list(spotAnimationType.sequenceId)
				if (pathingEntity.previousMovementStepsLeft > 0 && seqType2.interruptType == 0) {
					EntityUpdating.turnDirectionIndex = 0
					pathingEntity.stalledStepCount++
					EntityUpdating.currentMovementType = -1
					return
				}
				if (pathingEntity.previousMovementStepsLeft <= 0 && seqType2.moveType == 0) {
					EntityUpdating.currentMovementType = -1
					EntityUpdating.turnDirectionIndex = 0
					pathingEntity.stalledStepCount++
					return
				}
			}
		}
		@Pc(232) val startX: Int = pathingEntity.x
		@Pc(235) val startZ: Int = pathingEntity.z
		@Pc(251) val endX: Int = pathingEntity.movementBaseX[pathingEntity.pathPointer - 1] * 512 + pathingEntity.getSize() * 256
		@Pc(268) val endZ: Int = pathingEntity.movementBaseY[pathingEntity.pathPointer - 1] * 512 + pathingEntity.getSize() * 256
		if (startX >= endX) {
			if (endX < startX) {
				if (startZ < endZ) {
					pathingEntity.turn(6144)
				} else if (startZ > endZ) {
					pathingEntity.turn(2048)
				} else {
					pathingEntity.turn(4096)
				}
			} else if (endZ > startZ) {
				pathingEntity.turn(8192)
			} else if (endZ < startZ) {
				pathingEntity.turn(0)
			}
		} else if (endZ > startZ) {
			pathingEntity.turn(10240)
		} else if (startZ > endZ) {
			pathingEntity.turn(14336)
		} else {
			pathingEntity.turn(12288)
		}
		@Pc(366) val moveType: Byte = pathingEntity.movementQueueSpeed[pathingEntity.pathPointer - 1]
		if (!cutscene && (endX - startX > 1024 || endX - startX < -1024 || endZ - startZ > 1024 || endZ - startZ < -1024)) {
			pathingEntity.z = endZ
			pathingEntity.x = endX
			pathingEntity.turn(pathingEntity.lastYawRotation, false)
			EntityUpdating.currentMovementType = -1
			pathingEntity.pathPointer--
			if (pathingEntity.previousMovementStepsLeft > 0) {
				pathingEntity.previousMovementStepsLeft--
			}
			EntityUpdating.turnDirectionIndex = 0
			return
		}
		@Pc(432) var positionDelta: Int = 16
		@Pc(434) var delayMovement: Boolean = true
		if (pathingEntity is NPCEntity) {
			delayMovement = (pathingEntity as NPCEntity).npcType!!.delayMovement
		}
		@Pc(451) var local451: Int
		if (delayMovement) {
			val yawDelta: Int = pathingEntity.lastYawRotation - pathingEntity.yaw.rotation
			if (yawDelta != 0 && pathingEntity.faceEntity == -1 && pathingEntity.yawSpeed != 0) {
				positionDelta = 8
			}
			if (!cutscene && pathingEntity.pathPointer > 2) {
				positionDelta = 24
			}
			if (!cutscene && pathingEntity.pathPointer > 3) {
				positionDelta = 32
			}
		} else {
			if (!cutscene && pathingEntity.pathPointer > 1) {
				positionDelta = 24
			}
			if (!cutscene && pathingEntity.pathPointer > 2) {
				positionDelta = 32
			}
		}
		if (pathingEntity.stalledStepCount > 0 && pathingEntity.pathPointer > 1) {
			positionDelta = 32
			pathingEntity.stalledStepCount--
		}
		if (moveType.toInt() == MoveSpeed.RUNNING) {
			positionDelta = positionDelta shl 0x1
		} else if (moveType.toInt() == MoveSpeed.CRAWL) {
			positionDelta = positionDelta shr 0x1
		}
		EntityUpdating.turnDirectionIndex = 0
		if (basType!!.unused != -1) {
			positionDelta = positionDelta shl 0x9
			if (pathingEntity.pathPointer == 1) {
				val speedSquared: Int = pathingEntity.walkSpeedAccumulator * pathingEntity.walkSpeedAccumulator
				@Pc(618) val sizeX: Int = (if (endX >= pathingEntity.x) endX - pathingEntity.x else pathingEntity.x + -endX) shl 9
				@Pc(635) val sizeZ: Int = (if (pathingEntity.z <= endZ) endZ - pathingEntity.z else pathingEntity.z + -endZ) shl 9
				@Pc(642) val maxSize: Int = if (sizeZ >= sizeX) sizeZ else sizeX
				@Pc(649) val decelerationThreshold: Int = basType!!.unused * 2 * maxSize
				if (speedSquared > decelerationThreshold) {
					pathingEntity.walkSpeedAccumulator /= 2
				} else if (maxSize < speedSquared / 2) {
					pathingEntity.walkSpeedAccumulator -= basType!!.unused
					if (pathingEntity.walkSpeedAccumulator < 0) {
						pathingEntity.walkSpeedAccumulator = 0
					}
				} else if (pathingEntity.walkSpeedAccumulator < positionDelta) {
					pathingEntity.walkSpeedAccumulator += basType!!.unused
					if (pathingEntity.walkSpeedAccumulator > positionDelta) {
						pathingEntity.walkSpeedAccumulator = positionDelta
					}
				}
			} else if (positionDelta > pathingEntity.walkSpeedAccumulator) {
				pathingEntity.walkSpeedAccumulator += basType!!.unused
				if (positionDelta < pathingEntity.walkSpeedAccumulator) {
					pathingEntity.walkSpeedAccumulator = positionDelta
				}
			} else if (pathingEntity.walkSpeedAccumulator > 0) {
				pathingEntity.walkSpeedAccumulator -= basType!!.unused
				if (pathingEntity.walkSpeedAccumulator < 0) {
					pathingEntity.walkSpeedAccumulator = 0
				}
			}
			positionDelta = pathingEntity.walkSpeedAccumulator shr 9
			if (positionDelta < 1) {
				positionDelta = 1
			}
		}
		if (startX == endX && endZ == startZ) {
			EntityUpdating.currentMovementType = -1
		} else {
			if (startX < endX) {
				EntityUpdating.turnDirectionIndex = EntityUpdating.turnDirectionIndex or EntityMoveFlag.EAST
				pathingEntity.x += positionDelta
				if (endX < pathingEntity.x) {
					pathingEntity.x = endX
				}
			} else if (endX < startX) {
				EntityUpdating.turnDirectionIndex = EntityUpdating.turnDirectionIndex or EntityMoveFlag.WEST
				pathingEntity.x -= positionDelta
				if (pathingEntity.x < endX) {
					pathingEntity.x = endX
				}
			}
			if (positionDelta >= 32) {
				EntityUpdating.currentMovementType = MoveSpeed.RUNNING
			} else {
				EntityUpdating.currentMovementType = moveType.toInt()
			}
			if (endZ > startZ) {
				EntityUpdating.turnDirectionIndex = EntityUpdating.turnDirectionIndex or EntityMoveFlag.NORTH
				pathingEntity.z += positionDelta
				if (pathingEntity.z > endZ) {
					pathingEntity.z = endZ
				}
			} else if (endZ < startZ) {
				EntityUpdating.turnDirectionIndex = EntityUpdating.turnDirectionIndex or 0x2
				pathingEntity.z -= positionDelta
				if (endZ > pathingEntity.z) {
					pathingEntity.z = endZ
				}
			}
		}
		if (pathingEntity.x == endX && endZ == pathingEntity.z) {
			if (pathingEntity.previousMovementStepsLeft > 0) {
				pathingEntity.previousMovementStepsLeft--
			}
			pathingEntity.pathPointer--
		}
	}
}
