package com.jagex.game.runetek5.network;

import java.io.IOException;
import java.net.Socket;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!uu")
public abstract class SocketManager {

	@OriginalMember(owner = "client!uu", name = "a", descriptor = "I")
	public int sitePort;

	@OriginalMember(owner = "client!uu", name = "g", descriptor = "Ljava/lang/String;")
	public String siteHostName;

	@OriginalMember(owner = "client!uu", name = "a", descriptor = "(B)Ljava/net/Socket;")
	public final Socket createSocket() throws IOException {
		return new Socket(this.siteHostName, this.sitePort);
	}

	@OriginalMember(owner = "client!uu", name = "c", descriptor = "(I)Ljava/net/Socket;")
	public abstract Socket open() throws IOException;
	@OriginalMember(owner = "client!jv", name = "a", descriptor = "(IBLjava/lang/String;)Lclient!uu;")
	public static SocketManager create(@OriginalArg(0) int sitePort, @OriginalArg(2) String string) {
		@Pc(8) SocketManager abstractSocket;
		try {
			abstractSocket = (SocketManager) Class.forName("com.jagex.game.runetek5.network.ProxyManager").getDeclaredConstructor().newInstance();
		} catch (@Pc(10) Throwable ex) {
			abstractSocket = new DefaultSocketFactory();
		}
		abstractSocket.siteHostName = string;
		abstractSocket.sitePort = sitePort;
		return abstractSocket;
	}
}
