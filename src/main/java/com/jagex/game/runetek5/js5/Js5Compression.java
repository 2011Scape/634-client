package com.jagex.game.runetek5.js5;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.compression.GzipDecompressor;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.core.compress.BzipDecompressor;
import com.jagex.game.runetek5.core.constants.CompressionType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

public final class Js5Compression {
	@OriginalMember(owner = "client!in", name = "O", descriptor = "I")
	public static int maxSize = 100;

	private Js5Compression() {
		/* empty */
	}
	@OriginalMember(owner = "client!raa", name = "p", descriptor = "Lclient!ol;")
	public static final GzipDecompressor GZIP = new GzipDecompressor();
	@OriginalMember(owner = "client!uba", name = "a", descriptor = "([BI)[B")
	public static byte[] decompress(@OriginalArg(0) byte[] src) {
		@Pc(8) Packet packet = new Packet(src);
		@Pc(12) int compressionType = packet.readUByte();
		@Pc(23) int compressedLength = packet.readInt();
		if (compressedLength < 0 || Js5Compression.MAX_SIZE != 0 && Js5Compression.MAX_SIZE < compressedLength) {
			throw new RuntimeException();
		} else if (compressionType == CompressionType.NONE) {
			@Pc(97) byte[] data = new byte[compressedLength];
			packet.readBytes(0, compressedLength, data);
			return data;
		} else {
			@Pc(44) int uncompressedLength = packet.readInt();
			if (uncompressedLength < 0 || Js5Compression.MAX_SIZE != 0 && Js5Compression.MAX_SIZE < uncompressedLength) {
				throw new RuntimeException();
			}
			@Pc(65) byte[] uncompressed = new byte[uncompressedLength];
			if (compressionType == CompressionType.BZIP2) {
				BzipDecompressor.bunzip(uncompressed, uncompressedLength, src, compressedLength);
			} else {
				@Pc(79) GzipDecompressor gzipDecompressor = Js5Compression.GZIP;
				synchronized (Js5Compression.GZIP) {
					Js5Compression.GZIP.decompress(uncompressed, packet);
				}
			}
			return uncompressed;
		}
	}
	@OriginalMember(owner = "client!hv", name = "b", descriptor = "I")
	public static final int MAX_SIZE = 0;
}
