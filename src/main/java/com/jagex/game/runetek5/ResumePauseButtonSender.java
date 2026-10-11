package com.jagex.game.runetek5;

import com.jagex.game.runetek5.config.rsinterface.SubInterface;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.menu.MiniMenuEntry;
import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.network.packet.ClientProt;

public final class ResumePauseButtonSender {

	@OriginalMember(owner = "client!jia", name = "a", descriptor = "(IZI)V")
	public static void continueInterfacePressed(@OriginalArg(0) int idHash, @OriginalArg(2) int slotId) {
		@Pc(19) TcpPacket packet = TcpPacket.createPacket(ClientProt.RESUME_PAUSEBUTTON, TcpPacket.isaac);
		packet.buffer.writeShortAdd(slotId);
		packet.buffer.writeIntMiddle(idHash);
		BufferedConnectionContext.queuePacket(packet);
	}
}
