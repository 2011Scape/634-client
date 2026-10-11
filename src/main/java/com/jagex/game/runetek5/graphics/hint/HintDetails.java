package com.jagex.game.runetek5.graphics.hint;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.statics.OutPlayerIndexCount;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

/**
 * Not part of the original client: fields the jar keeps loose on their owner, grouped in one
 * class.
 */
public final class HintDetails {

	public int colorType = 0;

	public int drawType = 0;

	public int ticksLeft = 100;

	public String text = null;
	@OriginalMember(owner = "client!qg", name = "b", descriptor = "(I)V")
	public static void tickHintTimers() {
		@Pc(7) int count = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES;
		@Pc(9) int[] outPlayerIndices = PlayerUpdate.OUT_PLAYER_INDICES;
		for (@Pc(11) int i = 0; i < count; i++) {
			@Pc(19) PlayerEntity playerEntity = PlayerUpdate.players[outPlayerIndices[i]];
			if (playerEntity != null && playerEntity.hintDetails.ticksLeft > 0) {
				playerEntity.hintDetails.ticksLeft--;
				if (playerEntity.hintDetails.ticksLeft == 0) {
					playerEntity.hintDetails.text = null;
				}
			}
		}
		for (@Pc(49) int j = 0; j < NPCList.NPC_UPDATE_INDEX; j++) {
			@Pc(56) long key = (long) NPCList.NPC_UPDATE_INDICES[j];
			@Pc(62) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get(key);
			if (npcEntityNode != null) {
				@Pc(67) NPCEntity npcEntity = npcEntityNode.npc;
				if (npcEntity.hintDetails.ticksLeft > 0) {
					npcEntity.hintDetails.ticksLeft--;
					if (npcEntity.hintDetails.ticksLeft == 0) {
						npcEntity.hintDetails.text = null;
					}
				}
			}
		}
	}
}
