package com.jagex.game.runetek5;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.entity.NPCEntity;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.sound.player.area.AmbientSound;

public final class NpcUpdate {

	public static final int RUN_OR_CRAWL = 2;

	public static final int REMOVE = 3;

	private NpcUpdate() {
		/* empty */
	}
	@OriginalMember(owner = "client!li", name = "a", descriptor = "(B)V")
	public static void decodeNpcUpdate() {
		NPCList.npcRemovalCount = 0;
		NPCList.npcMaskUpdateCount = 0;
		NPCList.npcUpdateCycle++;
		NPCList.decodeAddRemove();
		NPCList.decodeNewNpcs();
		NPCList.decodeNpcMasks();
		@Pc(21) boolean isUnlinked = false;
		@Pc(29) int local29;
		for (@Pc(23) int i = 0; i < NPCList.npcRemovalCount; i++) {
			int npcIndex = NPCList.NPC_REMOVAL_INDICES[i];
			@Pc(36) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) npcIndex);
			@Pc(39) NPCEntity npcEntity = npcEntityNode.npc;
			if (MiniMenu.isOpen && MiniMenu.containsEntryForNpc(npcIndex)) {
				MiniMenu.close();
			}
			if (npcEntity.lastUpdate != NPCList.npcUpdateCycle) {
				if (npcEntity.npcType.hasSounds()) {
					AmbientSound.unlinkEntitySound(npcEntity);
				}
				npcEntity.setNpcType(null);
				npcEntityNode.unlink();
				isUnlinked = true;
			}
		}
		if (isUnlinked) {
			NPCList.npcCount = NPCList.NPC_MAP.size();
			NPCList.NPC_MAP.toArray(NPCList.NPC_ARRAY);
		}
		if (BufferedConnectionContext.currentPacketSize != BufferedConnectionContext.receivedBuffer.currentPosition) {
			throw new RuntimeException("gnp1 pos:" + BufferedConnectionContext.receivedBuffer.currentPosition + " psize:" + BufferedConnectionContext.currentPacketSize);
		}
		for (int updateIndex = 0; updateIndex < NPCList.NPC_UPDATE_INDEX; updateIndex++) {
			if (NPCList.NPC_MAP.get((long) NPCList.NPC_UPDATE_INDICES[updateIndex]) == null) {
				throw new RuntimeException("gnp2 pos:" + updateIndex + " size:" + NPCList.NPC_UPDATE_INDEX);
			}
		}
		if (NPCList.npcCount - NPCList.NPC_UPDATE_INDEX != 0) {
			throw new RuntimeException("gnp3 mis:" + (NPCList.npcCount - NPCList.NPC_UPDATE_INDEX));
		}
		for (@Pc(165) int j = 0; j < NPCList.npcCount; j++) {
			if (NPCList.NPC_ARRAY[j].npc.lastUpdate != NPCList.npcUpdateCycle) {
				throw new RuntimeException("gnp4 uk:" + NPCList.NPC_ARRAY[j].npc.playerIndex);
			}
		}
	}
	@OriginalMember(owner = "client!am", name = "f", descriptor = "[I")
	public static final int[] NPC_MASK_UPDATE_INDICES = new int[250];
}
