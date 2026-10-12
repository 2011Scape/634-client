package com.jagex.game.runetek5.network

import com.jagex.game.runetek5.network.SocketManager
import io.IOException
import net.Socket
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!im")
class DefaultSocketFactory : SocketManager() {

	@OriginalMember(owner = "client!im", name = "c", descriptor = "(I)Ljava/net/Socket;")
	@Throws(IOException::class)
	override fun open(): Socket? {
		return this.createSocket()
	}
}
