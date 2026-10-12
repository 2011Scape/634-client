package com.jagex.game.runetek5.graphics.hint
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.statics.OutPlayerIndexCount
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: fields the jar keeps loose on their owner, grouped in one
 * class.
 */
class HintDetails {

	@JvmField
	var colorType: Int = 0

	@JvmField
	var drawType: Int = 0

	@JvmField
	var ticksLeft: Int = 100

	@JvmField
	var text: String? = null

	companion object {
		@OriginalMember(owner = "client!qg", name = "b", descriptor = "(I)V")
		@JvmStatic
		fun tickHintTimers() {
			@Pc(7) val count: Int = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES
			@Pc(9) val outPlayerIndices: IntArray = PlayerUpdate.OUT_PLAYER_INDICES
			@Pc(11) var i: Int = 0
			while (i < count) {
				@Pc(19) val playerEntity: PlayerEntity? = PlayerUpdate.players[outPlayerIndices[i]]
				if (playerEntity != null && playerEntity.hintDetails.ticksLeft > 0) {
					playerEntity.hintDetails.ticksLeft--
					if (playerEntity.hintDetails.ticksLeft == 0) {
						playerEntity.hintDetails.text = null
					}
				}
				i++
			}
			@Pc(49) var j: Int = 0
			while (j < NPCList.NPC_UPDATE_INDEX) {
				@Pc(56) val key: Long = NPCList.NPC_UPDATE_INDICES[j].toLong()
				@Pc(62) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(key) as NPCEntityNode?
				if (npcEntityNode != null) {
					@Pc(67) val npcEntity: NPCEntity? = npcEntityNode.npc
					if (npcEntity!!.hintDetails.ticksLeft > 0) {
						npcEntity.hintDetails.ticksLeft--
						if (npcEntity.hintDetails.ticksLeft == 0) {
							npcEntity.hintDetails.text = null
						}
					}
				}
				j++
			}
		}
	}
}
