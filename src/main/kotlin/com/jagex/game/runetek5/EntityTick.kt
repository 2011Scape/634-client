package com.jagex.game.runetek5

import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.entity.PathingEntity
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.entity.pathingentity.EntityUpdating
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.statics.OutPlayerIndexCount
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object EntityTick {

	@OriginalMember(owner = "client!ic", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun movePlayers() {
		@Pc(7) val playerCount: Int = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES
		@Pc(9) val outPlayerIndices: IntArray = PlayerUpdate.OUT_PLAYER_INDICES
		for (@Pc(11) i in 0 until playerCount) {
			@Pc(19) val playerEntity: PlayerEntity? = PlayerUpdate.players[outPlayerIndices[i]]
			if (playerEntity != null) {
				EntityTick.move(playerEntity.getSize(), playerEntity)
			}
		}
	}

	@OriginalMember(owner = "client!afa", name = "a", descriptor = "(ZILclient!kda;)V")
	@JvmStatic
	fun move(@OriginalArg(1) size: Int, @OriginalArg(2) pathingEntity: PathingEntity?) {
		@Pc(7) var currentMovementType: Int = -1
		@Pc(9) var turnDirectionIndex: Int = 0
		if (pathingEntity!!.forceMovementT1Delay > ClientState.clientCycle) {
			PathingEntity.processForceMovementT1(pathingEntity)
		} else if (pathingEntity.forceMovementT2Delay >= ClientState.clientCycle) {
			PathingEntity.processForceMovementT2(pathingEntity)
		} else {
			EntityUpdating.updatePosition(false, pathingEntity)
			currentMovementType = EntityUpdating.currentMovementType
			turnDirectionIndex = EntityUpdating.turnDirectionIndex
		}
		if (pathingEntity.x < 512 || pathingEntity.z < 512 || pathingEntity.x >= MapRegion.sceneSizeX * 512 - 512 || pathingEntity.z >= MapRegion.sceneSizeY * 512 - 512) {
			pathingEntity.secondarySpotAnimation.spotAnimationId = -1
			pathingEntity.forceMovementT2Delay = 0
			turnDirectionIndex = 0
			currentMovementType = -1
			pathingEntity.currentAnimations = null
			pathingEntity.primarySpotAnimation.spotAnimationId = -1
			pathingEntity.currentSequence.sequenceId = -1
			pathingEntity.forceMovementT1Delay = 0
			pathingEntity.x = pathingEntity.movementBaseX[0] * 512 + pathingEntity.getSize() * 256
			pathingEntity.z = pathingEntity.movementBaseY[0] * 512 + pathingEntity.getSize() * 256
			pathingEntity.stopMoving()
		}
		if (PlayerUpdate.localPlayer === pathingEntity && (pathingEntity.x < 6144 || pathingEntity.z < 6144 || (MapRegion.sceneSizeX - 12) * 512 <= pathingEntity.x || (MapRegion.sceneSizeY - 12) * 512 <= pathingEntity.z)) {
			pathingEntity.secondarySpotAnimation.spotAnimationId = -1
			currentMovementType = -1
			pathingEntity.forceMovementT2Delay = 0
			pathingEntity.primarySpotAnimation.spotAnimationId = -1
			pathingEntity.forceMovementT1Delay = 0
			turnDirectionIndex = 0
			pathingEntity.currentAnimations = null
			pathingEntity.currentSequence.sequenceId = -1
			pathingEntity.x = pathingEntity.movementBaseX[0] * 512 + pathingEntity.getSize() * 256
			pathingEntity.z = pathingEntity.movementBaseY[0] * 512 + pathingEntity.getSize() * 256
			pathingEntity.stopMoving()
		}
		@Pc(225) val rotation: Int = PathingEntity.turnTo(pathingEntity)
		PathingEntity.updateEquipmentRotation(pathingEntity)
		PathingEntity.animateMovement(currentMovementType, rotation, pathingEntity, turnDirectionIndex)
		PlayerUpdate.animateMovement(pathingEntity, currentMovementType)
		PathingEntity.updateAnimations(pathingEntity)
	}
}
