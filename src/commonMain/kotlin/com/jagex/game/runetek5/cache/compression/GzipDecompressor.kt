package com.jagex.game.runetek5.cache.compression

import com.jagex.game.runetek5.buffer.Packet
import util.zip.Inflater
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ol")
class GzipDecompressor {

	@OriginalMember(owner = "client!ol", name = "c", descriptor = "Ljava/util/zip/Inflater;")
	private var inflater: Inflater? = null

	@OriginalMember(owner = "client!ol", name = "<init>", descriptor = "()V")
	constructor() : this(-1, 1000000, 1000000)

	@OriginalMember(owner = "client!ol", name = "<init>", descriptor = "(III)V")
	private constructor(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

	@OriginalMember(owner = "client!ol", name = "a", descriptor = "(I[B)[B")
	fun decompress(@OriginalArg(1) data: ByteArray?): ByteArray {
		@Pc(8) val packet: Packet = Packet(data)
		packet.currentPosition = data!!.size - 4
		@Pc(18) val len: Int = packet.readUIntLittle()
		packet.currentPosition = 0
		@Pc(29) val out: ByteArray = ByteArray(len)
		this.decompress(out, packet)
		return out
	}

	@OriginalMember(owner = "client!ol", name = "a", descriptor = "([BILclient!cea;)V")
	fun decompress(@OriginalArg(0) output: ByteArray?, @OriginalArg(2) packet: Packet?) {
		if (packet!!.buffer!![packet.currentPosition].toInt() != 31 || packet.buffer!![packet.currentPosition + 1].toInt() != -117) {
			throw RuntimeException("Invalid GZIP header!")
		}
		if (this.inflater == null) {
			this.inflater = Inflater(true)
		}
		try {
			this.inflater!!.setInput(packet.buffer!!, packet.currentPosition + 10, -8 - (packet.currentPosition + 10 - packet.buffer!!.size))
			this.inflater!!.inflate(output!!)
		} catch (@Pc(67) ex: Exception) {
			this.inflater!!.reset()
			throw RuntimeException("Invalid GZIP compressed data!")
		}
		this.inflater!!.reset()
	}
}
