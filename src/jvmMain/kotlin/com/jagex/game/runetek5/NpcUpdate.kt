package com.jagex.game.runetek5
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.entity.NPCEntity
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.sound.player.area.AmbientSound
import com.jagex.game.runetek5.cache.node.Node

object NpcUpdate {

	const val RUN_OR_CRAWL: Int = 2

	const val REMOVE: Int = 3

	@OriginalMember(owner = "client!li", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun decodeNpcUpdate() {
		NPCList.npcRemovalCount = 0
		NPCList.npcMaskUpdateCount = 0
		NPCList.npcUpdateCycle++
		NPCList.decodeAddRemove()
		NPCList.decodeNewNpcs()
		NPCList.decodeNpcMasks()
		@Pc(21) var isUnlinked: Boolean = false
		@Pc(29) var local29: Int
		@Pc(23) var i: Int = 0
		while (i < NPCList.npcRemovalCount) {
			val npcIndex: Int = NPCList.NPC_REMOVAL_INDICES[i]
			@Pc(36) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(npcIndex.toLong()) as NPCEntityNode?
			@Pc(39) val npcEntity: NPCEntity? = npcEntityNode!!.npc
			if (MiniMenu.isOpen && MiniMenu.containsEntryForNpc(npcIndex)) {
				MiniMenu.close()
			}
			if (npcEntity!!.lastUpdate != NPCList.npcUpdateCycle) {
				if (npcEntity.npcType!!.hasSounds()) {
					AmbientSound.unlinkEntitySound(npcEntity)
				}
				npcEntity.setNpcType(null)
				npcEntityNode.unlink()
				isUnlinked = true
			}
			i++
		}
		if (isUnlinked) {
			NPCList.npcCount = NPCList.NPC_MAP.size()
			@Suppress("UNCHECKED_CAST")
			NPCList.NPC_MAP.toArray(NPCList.NPC_ARRAY as Array<Node?>)
		}
		if (BufferedConnectionContext.currentPacketSize != BufferedConnectionContext.receivedBuffer.currentPosition) {
			throw RuntimeException("gnp1 pos:" + BufferedConnectionContext.receivedBuffer.currentPosition + " psize:" + BufferedConnectionContext.currentPacketSize)
		}
		var updateIndex: Int = 0
		while (updateIndex < NPCList.NPC_UPDATE_INDEX) {
			if (NPCList.NPC_MAP.get(NPCList.NPC_UPDATE_INDICES[updateIndex].toLong()) == null) {
				throw RuntimeException("gnp2 pos:" + updateIndex + " size:" + NPCList.NPC_UPDATE_INDEX)
			}
			updateIndex++
		}
		if (NPCList.npcCount - NPCList.NPC_UPDATE_INDEX != 0) {
			throw RuntimeException("gnp3 mis:" + (NPCList.npcCount - NPCList.NPC_UPDATE_INDEX))
		}
		@Pc(165) var j: Int = 0
		while (j < NPCList.npcCount) {
			if (NPCList.NPC_ARRAY[j]!!.npc!!.lastUpdate != NPCList.npcUpdateCycle) {
				throw RuntimeException("gnp4 uk:" + NPCList.NPC_ARRAY[j]!!.npc!!.playerIndex)
			}
			j++
		}
	}
	@OriginalMember(owner = "client!am", name = "f", descriptor = "[I")
	@JvmField
	val NPC_MASK_UPDATE_INDICES: IntArray = IntArray(250)
}
