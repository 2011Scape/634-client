package com.jagex.game.runetek5

import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.statics.OutPlayerIndexCount
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.interaction.chat.ChatBox
import com.jagex.game.runetek5.client.ClientState

object ThreeDView {

	@OriginalMember(owner = "client!tq", name = "a", descriptor = "(IILjava/lang/String;)V")
	@JvmStatic
	fun sendPlayerOperation(@OriginalArg(0) option: Int, @OriginalArg(2) playerName: String?) {
		@Pc(7) val playerCount: Int = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES
		@Pc(9) val outPlayerIndices: IntArray = PlayerUpdate.OUT_PLAYER_INDICES
		@Pc(15) var found: Boolean = false
		@Pc(17) var i: Int = 0
		while (i < playerCount) {
			@Pc(25) val playerEntity: PlayerEntity? = PlayerUpdate.players[outPlayerIndices[i]]
			if (playerEntity != null && PlayerUpdate.localPlayer !== playerEntity && playerEntity.displayName != null && playerEntity.displayName!!.equals(playerName, ignoreCase = true)) {
				found = true
				@Pc(54) var packet: TcpPacket
				if (option == 1) {
					packet = TcpPacket.createPacket(ClientProt.OPPLAYER1, TcpPacket.isaac)
					packet.buffer!!.writeShortAddLittle(outPlayerIndices[i])
					packet.buffer!!.writeByteInverse(0)
					BufferedConnectionContext.queuePacket(packet)
				} else if (option == 4) {
					packet = TcpPacket.createPacket(ClientProt.OPPLAYER4, TcpPacket.isaac)
					packet.buffer!!.writeShort(outPlayerIndices[i])
					packet.buffer!!.writeByteAdd(0)
					BufferedConnectionContext.queuePacket(packet)
				} else if (option == 5) {
					packet = TcpPacket.createPacket(ClientProt.OPPLAYER5, TcpPacket.isaac)
					packet.buffer!!.writeShortAdd(outPlayerIndices[i])
					packet.buffer!!.writeByte(0)
					BufferedConnectionContext.queuePacket(packet)
				} else if (option == 6) {
					packet = TcpPacket.createPacket(ClientProt.OPPLAYER6, TcpPacket.isaac)
					packet.buffer!!.writeByte(0)
					packet.buffer!!.writeShortAdd(outPlayerIndices[i])
					BufferedConnectionContext.queuePacket(packet)
				} else if (option == 7) {
					packet = TcpPacket.createPacket(ClientProt.OPPLAYER7, TcpPacket.isaac)
					packet.buffer!!.writeShortAdd(outPlayerIndices[i])
					packet.buffer!!.writeByteAdd(0)
					BufferedConnectionContext.queuePacket(packet)
				}
				break
			}
			i++
		}
		if (!found) {
			ChatBox.writeGameMessage(LocalizedText.aClass351_23.get(ClientState.language) + playerName)
		}
	}
}
