package com.jagex.game.runetek5.network

import io.IOException
import net.Socket
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.Class
import lang.getDeclaredConstructor

@OriginalClass("client!uu")
abstract class SocketManager {

	@OriginalMember(owner = "client!uu", name = "a", descriptor = "I")
	@JvmField
	var sitePort: Int = 0

	@OriginalMember(owner = "client!uu", name = "g", descriptor = "Ljava/lang/String;")
	@JvmField
	var siteHostName: String? = null

	@OriginalMember(owner = "client!uu", name = "a", descriptor = "(B)Ljava/net/Socket;")
	@Throws(IOException::class)
	fun createSocket(): Socket {
		return Socket(this.siteHostName, this.sitePort)
	}

	@OriginalMember(owner = "client!uu", name = "c", descriptor = "(I)Ljava/net/Socket;")
	@Throws(IOException::class)
	abstract fun open(): Socket?

	companion object {
		@OriginalMember(owner = "client!jv", name = "a", descriptor = "(IBLjava/lang/String;)Lclient!uu;")
		@JvmStatic
		fun create(@OriginalArg(0) sitePort: Int, @OriginalArg(2) string: String?): SocketManager {
			@Pc(8) var abstractSocket: SocketManager
			try {
				abstractSocket = lang.forName("com.jagex.game.runetek5.network.ProxyManager").getDeclaredConstructor().newInstance() as SocketManager
			} catch (@Pc(10) ex: Throwable) {
				abstractSocket = DefaultSocketFactory()
			}
			abstractSocket.siteHostName = string
			abstractSocket.sitePort = sitePort
			return abstractSocket
		}
	}
}
