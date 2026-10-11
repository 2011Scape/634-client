package com.jagex.game.runetek5;

import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.statics.OutPlayerIndexCount;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.interaction.chat.ChatBox;
import com.jagex.game.runetek5.client.ClientState;

public final class ThreeDView {

	@OriginalMember(owner = "client!tq", name = "a", descriptor = "(IILjava/lang/String;)V")
	public static void sendPlayerOperation(@OriginalArg(0) int option, @OriginalArg(2) String playerName) {
		@Pc(7) int playerCount = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES;
		@Pc(9) int[] outPlayerIndices = PlayerUpdate.OUT_PLAYER_INDICES;
		@Pc(15) boolean found = false;
		for (@Pc(17) int i = 0; i < playerCount; i++) {
			@Pc(25) PlayerEntity playerEntity = PlayerUpdate.players[outPlayerIndices[i]];
			if (playerEntity != null && PlayerUpdate.localPlayer != playerEntity && playerEntity.displayName != null && playerEntity.displayName.equalsIgnoreCase(playerName)) {
				found = true;
				@Pc(54) TcpPacket packet;
				if (option == 1) {
					packet = TcpPacket.createPacket(ClientProt.OPPLAYER1, TcpPacket.isaac);
					packet.buffer.writeShortAddLittle(outPlayerIndices[i]);
					packet.buffer.writeByteInverse(0);
					BufferedConnectionContext.queuePacket(packet);
				} else if (option == 4) {
					packet = TcpPacket.createPacket(ClientProt.OPPLAYER4, TcpPacket.isaac);
					packet.buffer.writeShort(outPlayerIndices[i]);
					packet.buffer.writeByteAdd(0);
					BufferedConnectionContext.queuePacket(packet);
				} else if (option == 5) {
					packet = TcpPacket.createPacket(ClientProt.OPPLAYER5, TcpPacket.isaac);
					packet.buffer.writeShortAdd(outPlayerIndices[i]);
					packet.buffer.writeByte(0);
					BufferedConnectionContext.queuePacket(packet);
				} else if (option == 6) {
					packet = TcpPacket.createPacket(ClientProt.OPPLAYER6, TcpPacket.isaac);
					packet.buffer.writeByte(0);
					packet.buffer.writeShortAdd(outPlayerIndices[i]);
					BufferedConnectionContext.queuePacket(packet);
				} else if (option == 7) {
					packet = TcpPacket.createPacket(ClientProt.OPPLAYER7, TcpPacket.isaac);
					packet.buffer.writeShortAdd(outPlayerIndices[i]);
					packet.buffer.writeByteAdd(0);
					BufferedConnectionContext.queuePacket(packet);
				}
				break;
			}
		}
		if (!found) {
			ChatBox.writeGameMessage(LocalizedText.aClass351_23.get(ClientState.language) + playerName);
		}
	}
}
