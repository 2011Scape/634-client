package com.jagex.game.runetek5

import com.jagex.game.runetek5.network.packet.TcpPacket
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.preference.GamePreferences
import kotlin.jvm.JvmStatic

object ServerConnectionReader {

	@OriginalMember(owner = "client!di", name = "e", descriptor = "(I)V")
	@JvmStatic
	fun sendWindowmodePreferenceToServer() {
		@Pc(12) val packet: TcpPacket = TcpPacket.createPacket(ClientProt.SCREEN_SIZE, TcpPacket.isaac)
		packet.buffer!!.writeByte(WindowMode.getWindowMode()!!.id)
		packet.buffer!!.writeShort(GameShell.screenCanvasWidth)
		packet.buffer!!.writeShort(GameShell.screenCanvasHeight)
		packet.buffer!!.writeByte(GamePreferences.uid!!.antiAliasingPreview!!.getValue())
		BufferedConnectionContext.queuePacket(packet)
	}
}
