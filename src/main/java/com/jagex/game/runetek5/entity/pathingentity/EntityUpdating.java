package com.jagex.game.runetek5.entity.pathingentity;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.EntityMoveFlag;
import com.jagex.game.runetek5.MoveSpeed;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.config.bas.BasType;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.PathingEntity;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;

public final class EntityUpdating {
	@OriginalMember(owner = "client!bv", name = "m", descriptor = "I")
	public static int currentMovementType;

	@OriginalMember(owner = "client!ep", name = "u", descriptor = "I")
	public static int turnDirectionIndex;

	private EntityUpdating() {
		/* empty */
	}
	@OriginalMember(owner = "client!sb", name = "a", descriptor = "(ZILclient!kda;)V")
	public static void updatePosition(@OriginalArg(0) boolean cutscene, @OriginalArg(2) PathingEntity pathingEntity) {
		@Pc(9) BasType basType = pathingEntity.getBasType();
		if (pathingEntity.pathPointer == 0) {
			pathingEntity.stalledStepCount = 0;
			EntityUpdating.currentMovementType = -1;
			EntityUpdating.turnDirectionIndex = 0;
			return;
		}
		if (pathingEntity.currentSequence.sequenceId != -1 && pathingEntity.currentSequence.speed == 0) {
			@Pc(40) SeqType seqType = TypeLists.SEQ.list(pathingEntity.currentSequence.sequenceId);
			if (pathingEntity.previousMovementStepsLeft > 0 && seqType.interruptType == 0) {
				EntityUpdating.currentMovementType = -1;
				EntityUpdating.turnDirectionIndex = 0;
				pathingEntity.stalledStepCount++;
				return;
			}
			if (pathingEntity.previousMovementStepsLeft <= 0 && seqType.moveType == 0) {
				EntityUpdating.turnDirectionIndex = 0;
				EntityUpdating.currentMovementType = -1;
				pathingEntity.stalledStepCount++;
				return;
			}
		}
		@Pc(109) SeqType seqType2;
		@Pc(95) SpotAnimationType spotAnimationType;
		if (pathingEntity.primarySpotAnimation.spotAnimationId != -1 && pathingEntity.primarySpotAnimation.startCycle <= ClientState.clientCycle) {
			spotAnimationType = TypeLists.SPOT_ANIM.list(pathingEntity.primarySpotAnimation.spotAnimationId);
			if (spotAnimationType.replay && spotAnimationType.sequenceId != -1) {
				seqType2 = TypeLists.SEQ.list(spotAnimationType.sequenceId);
				if (pathingEntity.previousMovementStepsLeft > 0 && seqType2.interruptType == 0) {
					EntityUpdating.turnDirectionIndex = 0;
					pathingEntity.stalledStepCount++;
					EntityUpdating.currentMovementType = -1;
					return;
				}
				if (pathingEntity.previousMovementStepsLeft <= 0 && seqType2.moveType == 0) {
					EntityUpdating.currentMovementType = -1;
					EntityUpdating.turnDirectionIndex = 0;
					pathingEntity.stalledStepCount++;
					return;
				}
			}
		}
		if (pathingEntity.primarySpotAnimation.spotAnimationId != -1 && ClientState.clientCycle >= pathingEntity.primarySpotAnimation.startCycle) {
			spotAnimationType = TypeLists.SPOT_ANIM.list(pathingEntity.primarySpotAnimation.spotAnimationId);
			if (spotAnimationType.replay && spotAnimationType.sequenceId != -1) {
				seqType2 = TypeLists.SEQ.list(spotAnimationType.sequenceId);
				if (pathingEntity.previousMovementStepsLeft > 0 && seqType2.interruptType == 0) {
					EntityUpdating.turnDirectionIndex = 0;
					pathingEntity.stalledStepCount++;
					EntityUpdating.currentMovementType = -1;
					return;
				}
				if (pathingEntity.previousMovementStepsLeft <= 0 && seqType2.moveType == 0) {
					EntityUpdating.currentMovementType = -1;
					EntityUpdating.turnDirectionIndex = 0;
					pathingEntity.stalledStepCount++;
					return;
				}
			}
		}
		@Pc(232) int startX = pathingEntity.x;
		@Pc(235) int startZ = pathingEntity.z;
		@Pc(251) int endX = pathingEntity.movementBaseX[pathingEntity.pathPointer - 1] * 512 + pathingEntity.getSize() * 256;
		@Pc(268) int endZ = pathingEntity.movementBaseY[pathingEntity.pathPointer - 1] * 512 + pathingEntity.getSize() * 256;
		if (startX >= endX) {
			if (endX < startX) {
				if (startZ < endZ) {
					pathingEntity.turn(6144);
				} else if (startZ > endZ) {
					pathingEntity.turn(2048);
				} else {
					pathingEntity.turn(4096);
				}
			} else if (endZ > startZ) {
				pathingEntity.turn(8192);
			} else if (endZ < startZ) {
				pathingEntity.turn(0);
			}
		} else if (endZ > startZ) {
			pathingEntity.turn(10240);
		} else if (startZ > endZ) {
			pathingEntity.turn(14336);
		} else {
			pathingEntity.turn(12288);
		}
		@Pc(366) byte moveType = pathingEntity.movementQueueSpeed[pathingEntity.pathPointer - 1];
		if (!cutscene && (endX - startX > 1024 || endX - startX < -1024 || endZ - startZ > 1024 || endZ - startZ < -1024)) {
			pathingEntity.z = endZ;
			pathingEntity.x = endX;
			pathingEntity.turn(pathingEntity.lastYawRotation, false);
			EntityUpdating.currentMovementType = -1;
			pathingEntity.pathPointer--;
			if (pathingEntity.previousMovementStepsLeft > 0) {
				pathingEntity.previousMovementStepsLeft--;
			}
			EntityUpdating.turnDirectionIndex = 0;
			return;
		}
		@Pc(432) int positionDelta = 16;
		@Pc(434) boolean delayMovement = true;
		if (pathingEntity instanceof NPCEntity) {
			delayMovement = ((NPCEntity) pathingEntity).npcType.delayMovement;
		}
		@Pc(451) int local451;
		if (delayMovement) {
			int yawDelta = pathingEntity.lastYawRotation - pathingEntity.yaw.rotation;
			if (yawDelta != 0 && pathingEntity.faceEntity == -1 && pathingEntity.yawSpeed != 0) {
				positionDelta = 8;
			}
			if (!cutscene && pathingEntity.pathPointer > 2) {
				positionDelta = 24;
			}
			if (!cutscene && pathingEntity.pathPointer > 3) {
				positionDelta = 32;
			}
		} else {
			if (!cutscene && pathingEntity.pathPointer > 1) {
				positionDelta = 24;
			}
			if (!cutscene && pathingEntity.pathPointer > 2) {
				positionDelta = 32;
			}
		}
		if (pathingEntity.stalledStepCount > 0 && pathingEntity.pathPointer > 1) {
			positionDelta = 32;
			pathingEntity.stalledStepCount--;
		}
		if (moveType == MoveSpeed.RUNNING) {
			positionDelta <<= 0x1;
		} else if (moveType == MoveSpeed.CRAWL) {
			positionDelta >>= 0x1;
		}
		EntityUpdating.turnDirectionIndex = 0;
		if (basType.unused != -1) {
			positionDelta <<= 0x9;
			if (pathingEntity.pathPointer == 1) {
				int speedSquared = pathingEntity.walkSpeedAccumulator * pathingEntity.walkSpeedAccumulator;
				@Pc(618) int sizeX = (endX >= pathingEntity.x ? endX - pathingEntity.x : pathingEntity.x + -endX) << 9;
				@Pc(635) int sizeZ = (pathingEntity.z <= endZ ? endZ - pathingEntity.z : pathingEntity.z + -endZ) << 9;
				@Pc(642) int maxSize = sizeZ >= sizeX ? sizeZ : sizeX;
				@Pc(649) int decelerationThreshold = basType.unused * 2 * maxSize;
				if (speedSquared > decelerationThreshold) {
					pathingEntity.walkSpeedAccumulator /= 2;
				} else if (maxSize < speedSquared / 2) {
					pathingEntity.walkSpeedAccumulator -= basType.unused;
					if (pathingEntity.walkSpeedAccumulator < 0) {
						pathingEntity.walkSpeedAccumulator = 0;
					}
				} else if (pathingEntity.walkSpeedAccumulator < positionDelta) {
					pathingEntity.walkSpeedAccumulator += basType.unused;
					if (pathingEntity.walkSpeedAccumulator > positionDelta) {
						pathingEntity.walkSpeedAccumulator = positionDelta;
					}
				}
			} else if (positionDelta > pathingEntity.walkSpeedAccumulator) {
				pathingEntity.walkSpeedAccumulator += basType.unused;
				if (positionDelta < pathingEntity.walkSpeedAccumulator) {
					pathingEntity.walkSpeedAccumulator = positionDelta;
				}
			} else if (pathingEntity.walkSpeedAccumulator > 0) {
				pathingEntity.walkSpeedAccumulator -= basType.unused;
				if (pathingEntity.walkSpeedAccumulator < 0) {
					pathingEntity.walkSpeedAccumulator = 0;
				}
			}
			positionDelta = pathingEntity.walkSpeedAccumulator >> 9;
			if (positionDelta < 1) {
				positionDelta = 1;
			}
		}
		if (startX == endX && endZ == startZ) {
			EntityUpdating.currentMovementType = -1;
		} else {
			if (startX < endX) {
				EntityUpdating.turnDirectionIndex |= EntityMoveFlag.EAST;
				pathingEntity.x += positionDelta;
				if (endX < pathingEntity.x) {
					pathingEntity.x = endX;
				}
			} else if (endX < startX) {
				EntityUpdating.turnDirectionIndex |= EntityMoveFlag.WEST;
				pathingEntity.x -= positionDelta;
				if (pathingEntity.x < endX) {
					pathingEntity.x = endX;
				}
			}
			if (positionDelta >= 32) {
				EntityUpdating.currentMovementType = MoveSpeed.RUNNING;
			} else {
				EntityUpdating.currentMovementType = moveType;
			}
			if (endZ > startZ) {
				EntityUpdating.turnDirectionIndex |= EntityMoveFlag.NORTH;
				pathingEntity.z += positionDelta;
				if (pathingEntity.z > endZ) {
					pathingEntity.z = endZ;
				}
			} else if (endZ < startZ) {
				EntityUpdating.turnDirectionIndex |= 0x2;
				pathingEntity.z -= positionDelta;
				if (endZ > pathingEntity.z) {
					pathingEntity.z = endZ;
				}
			}
		}
		if (pathingEntity.x == endX && endZ == pathingEntity.z) {
			if (pathingEntity.previousMovementStepsLeft > 0) {
				pathingEntity.previousMovementStepsLeft--;
			}
			pathingEntity.pathPointer--;
		}
	}
}
