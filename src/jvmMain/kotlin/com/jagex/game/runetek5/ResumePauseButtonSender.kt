package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.network.packet.ClientProt

object ResumePauseButtonSender {

	@OriginalMember(owner = "client!jia", name = "a", descriptor = "(IZI)V")
	@JvmStatic
	fun continueInterfacePressed(@OriginalArg(0) idHash: Int, @OriginalArg(2) slotId: Int) {
		@Pc(19) val packet: TcpPacket = TcpPacket.createPacket(ClientProt.RESUME_PAUSEBUTTON, TcpPacket.isaac)
		packet.buffer!!.writeShortAdd(slotId)
		packet.buffer!!.writeIntMiddle(idHash)
		BufferedConnectionContext.queuePacket(packet)
	}
}
