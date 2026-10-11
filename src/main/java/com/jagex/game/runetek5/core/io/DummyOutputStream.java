package com.jagex.game.runetek5.core.io;

import java.io.IOException;
import java.io.OutputStream;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!tk")
public final class DummyOutputStream extends OutputStream {

	@OriginalMember(owner = "client!tk", name = "write", descriptor = "(I)V")
	@Override
	public void write(@OriginalArg(0) int value) throws IOException {
		throw new IOException();
	}
}
