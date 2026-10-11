package com.jagex.game.runetek5.network;

import java.io.IOException;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.core.io.BufferedSocket;

@OriginalClass("client!ss")
public abstract class Connection {

	@OriginalMember(owner = "client!ss", name = "<init>", descriptor = "()V")
	public Connection() {
	}

	@OriginalMember(owner = "client!ss", name = "a", descriptor = "(B)V")
	public abstract void close();

	@OriginalMember(owner = "client!ss", name = "a", descriptor = "(IIB[B)I")
	public abstract int read(@OriginalArg(0) int length, @OriginalArg(1) int offset, @OriginalArg(3) byte[] buffer) throws IOException;

	@OriginalMember(owner = "client!ss", name = "a", descriptor = "(I)V")
	public abstract void breakConnection();

	@OriginalMember(owner = "client!ss", name = "a", descriptor = "(II)Z")
	public abstract boolean hasAvailable(@OriginalArg(0) int length) throws IOException;

	@OriginalMember(owner = "client!ss", name = "a", descriptor = "(III[B)V")
	public abstract void write(@OriginalArg(2) int length, @OriginalArg(3) byte[] buffer) throws IOException;
	@OriginalMember(owner = "client!qr", name = "g", descriptor = "Lclient!re;")
	public static BufferedSocket updateConnection;
	@OriginalMember(owner = "client!jq", name = "Z", descriptor = "Lclient!ss;")
	public static Connection connection;
}
