package com.jagex.game.runetek5.network;

import com.jagex.game.runetek5.network.Connection;
import java.io.IOException;
import java.net.Socket;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!gea")
public final class AsyncConnection extends Connection {

	@OriginalMember(owner = "client!gea", name = "k", descriptor = "Ljava/net/Socket;")
	private final Socket socket;

	@OriginalMember(owner = "client!gea", name = "i", descriptor = "Lclient!gha;")
	private final AsyncInputStream inputStream;

	@OriginalMember(owner = "client!gea", name = "l", descriptor = "Lclient!rha;")
	private final AsyncOutputStream outputStream;

	@OriginalMember(owner = "client!gea", name = "<init>", descriptor = "(Ljava/net/Socket;I)V")
	public AsyncConnection(@OriginalArg(0) Socket socket2, @OriginalArg(1) int capacity) throws IOException {
		this.socket = socket2;
		this.socket.setSoTimeout(30000);
		this.socket.setTcpNoDelay(true);
		this.inputStream = new AsyncInputStream(this.socket.getInputStream(), capacity);
		this.outputStream = new AsyncOutputStream(this.socket.getOutputStream(), capacity);
	}

	@OriginalMember(owner = "client!gea", name = "a", descriptor = "(III[B)V")
	@Override
	public void write(@OriginalArg(2) int length, @OriginalArg(3) byte[] buffer) throws IOException {
		this.outputStream.write(length, buffer);
	}

	@OriginalMember(owner = "client!gea", name = "a", descriptor = "(I)V")
	@Override
	public void breakConnection() {
		this.inputStream.drop();
		this.outputStream.reset();
	}

	@OriginalMember(owner = "client!gea", name = "a", descriptor = "(B)V")
	@Override
	public void close() {
		try {
			this.socket.close();
		} catch (@Pc(8) IOException ignored) {
		}
		this.inputStream.shutdown();
		this.outputStream.stop();
	}

	@OriginalMember(owner = "client!gea", name = "a", descriptor = "(IIB[B)I")
	@Override
	public int read(@OriginalArg(0) int length, @OriginalArg(1) int offset, @OriginalArg(3) byte[] buffer) throws IOException {
		return this.inputStream.read(length, offset, buffer);
	}

	@OriginalMember(owner = "client!gea", name = "a", descriptor = "(II)Z")
	@Override
	public boolean hasAvailable(@OriginalArg(0) int n) throws IOException {
		return this.inputStream.available(n);
	}

	@OriginalMember(owner = "client!gea", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() {
		this.close();
	}
	@OriginalMember(owner = "client!pk", name = "a", descriptor = "(Ljava/net/Socket;BI)Lclient!ss;")
	public static Connection create(@OriginalArg(0) Socket socket) throws IOException {
		return new AsyncConnection(socket, 7500);
	}
}
