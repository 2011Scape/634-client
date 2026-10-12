package com.jagex.game.runetek5.js5

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.cache.compression.GzipDecompressor
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.core.compress.BzipDecompressor
import com.jagex.game.runetek5.core.constants.CompressionType
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc

object Js5Compression {
	@OriginalMember(owner = "client!in", name = "O", descriptor = "I")
	@JvmField
	var maxSize: Int = 100

	@OriginalMember(owner = "client!raa", name = "p", descriptor = "Lclient!ol;")
	@JvmField
	val GZIP: GzipDecompressor = GzipDecompressor()

	@OriginalMember(owner = "client!uba", name = "a", descriptor = "([BI)[B")
	@JvmStatic
	fun decompress(@OriginalArg(0) src: ByteArray?): ByteArray {
		@Pc(8) val packet: Packet = Packet(src)
		@Pc(12) val compressionType: Int = packet.readUByte()
		@Pc(23) val compressedLength: Int = packet.readInt()
		if (compressedLength < 0 || Js5Compression.MAX_SIZE != 0 && Js5Compression.MAX_SIZE < compressedLength) {
			throw RuntimeException()
		} else if (compressionType == CompressionType.NONE) {
			@Pc(97) val data: ByteArray = ByteArray(compressedLength)
			packet.readBytes(0, compressedLength, data)
			return data
		} else {
			@Pc(44) val uncompressedLength: Int = packet.readInt()
			if (uncompressedLength < 0 || Js5Compression.MAX_SIZE != 0 && Js5Compression.MAX_SIZE < uncompressedLength) {
				throw RuntimeException()
			}
			@Pc(65) val uncompressed: ByteArray = ByteArray(uncompressedLength)
			if (compressionType == CompressionType.BZIP2) {
				BzipDecompressor.bunzip(uncompressed, uncompressedLength, src, compressedLength)
			} else {
				@Pc(79) val gzipDecompressor: GzipDecompressor = Js5Compression.GZIP
				synchronized(Js5Compression.GZIP) {
					Js5Compression.GZIP.decompress(uncompressed, packet)
				}
			}
			return uncompressed
		}
	}

	@OriginalMember(owner = "client!hv", name = "b", descriptor = "I")
	const val MAX_SIZE: Int = 0
}
