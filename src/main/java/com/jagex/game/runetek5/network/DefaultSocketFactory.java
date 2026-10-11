package com.jagex.game.runetek5.network;

import com.jagex.game.runetek5.network.SocketManager;
import java.io.IOException;
import java.net.Socket;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!im")
public final class DefaultSocketFactory extends SocketManager {

	@OriginalMember(owner = "client!im", name = "c", descriptor = "(I)Ljava/net/Socket;")
	@Override
	public Socket open() throws IOException {
		return this.createSocket();
	}
}
