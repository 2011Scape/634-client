package com.jagex.game.runetek5.core.io;

import com.jagex.game.runetek5.ByteArrayWrapper;
import java.nio.ByteBuffer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!lg")
public final class ByteBufferArrayWrapper extends ByteArrayWrapper {

	@OriginalMember(owner = "client!lg", name = "g", descriptor = "Ljava/nio/ByteBuffer;")
	private ByteBuffer buffer;

	@OriginalMember(owner = "client!lg", name = "a", descriptor = "(III)[B")
	@Override
	public byte[] readBlock(@OriginalArg(2) int offset) {
		@Pc(2) byte[] chunk = new byte[32768];
		this.buffer.position(offset);
		this.buffer.get(chunk, 0, 32768);
		return chunk;
	}

	@OriginalMember(owner = "client!lg", name = "b", descriptor = "(I)[B")
	@Override
	public byte[] get() {
		@Pc(4) byte[] bytes = new byte[this.buffer.capacity()];
		this.buffer.position(0);
		this.buffer.get(bytes);
		return bytes;
	}

	@OriginalMember(owner = "client!lg", name = "a", descriptor = "(B[B)V")
	@Override
	public void set(@OriginalArg(1) byte[] bytes) {
		this.buffer = ByteBuffer.allocateDirect(bytes.length);
		this.buffer.position(0);
		this.buffer.put(bytes);
	}
}
