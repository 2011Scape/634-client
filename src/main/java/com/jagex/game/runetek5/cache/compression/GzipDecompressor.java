package com.jagex.game.runetek5.cache.compression;

import com.jagex.game.runetek5.buffer.Packet;
import java.util.zip.Inflater;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ol")
public final class GzipDecompressor {

	@OriginalMember(owner = "client!ol", name = "c", descriptor = "Ljava/util/zip/Inflater;")
	private Inflater inflater;

	@OriginalMember(owner = "client!ol", name = "<init>", descriptor = "()V")
	public GzipDecompressor() {
		this(-1, 1000000, 1000000);
	}

	@OriginalMember(owner = "client!ol", name = "<init>", descriptor = "(III)V")
	private GzipDecompressor(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2) {
	}

	@OriginalMember(owner = "client!ol", name = "a", descriptor = "(I[B)[B")
	public byte[] decompress(@OriginalArg(1) byte[] data) {
		@Pc(8) Packet packet = new Packet(data);
		packet.currentPosition = data.length - 4;
		@Pc(18) int len = packet.readUIntLittle();
		packet.currentPosition = 0;
		@Pc(29) byte[] out = new byte[len];
		this.decompress(out, packet);
		return out;
	}

	@OriginalMember(owner = "client!ol", name = "a", descriptor = "([BILclient!cea;)V")
	public void decompress(@OriginalArg(0) byte[] output, @OriginalArg(2) Packet packet) {
		if (packet.buffer[packet.currentPosition] != 31 || packet.buffer[packet.currentPosition + 1] != -117) {
			throw new RuntimeException("Invalid GZIP header!");
		}
		if (this.inflater == null) {
			this.inflater = new Inflater(true);
		}
		try {
			this.inflater.setInput(packet.buffer, packet.currentPosition + 10, -8 - (packet.currentPosition + 10 - packet.buffer.length));
			this.inflater.inflate(output);
		} catch (@Pc(67) Exception ex) {
			this.inflater.reset();
			throw new RuntimeException("Invalid GZIP compressed data!");
		}
		this.inflater.reset();
	}
}
