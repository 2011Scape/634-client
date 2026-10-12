package com.jagex.game.runetek5.core.io

import com.jagex.game.runetek5.ByteArrayWrapper
import java.nio.ByteBuffer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!lg")
class ByteBufferArrayWrapper : ByteArrayWrapper() {

	@OriginalMember(owner = "client!lg", name = "g", descriptor = "Ljava/nio/ByteBuffer;")
	private var buffer: ByteBuffer? = null

	@OriginalMember(owner = "client!lg", name = "a", descriptor = "(III)[B")
	override fun readBlock(@OriginalArg(2) offset: Int): ByteArray? {
		@Pc(2) val chunk: ByteArray = ByteArray(32768)
		this.buffer!!.position(offset)
		this.buffer!!.get(chunk, 0, 32768)
		return chunk
	}

	@OriginalMember(owner = "client!lg", name = "b", descriptor = "(I)[B")
	override fun get(): ByteArray? {
		@Pc(4) val bytes: ByteArray = ByteArray(this.buffer!!.capacity())
		this.buffer!!.position(0)
		this.buffer!!.get(bytes)
		return bytes
	}

	@OriginalMember(owner = "client!lg", name = "a", descriptor = "(B[B)V")
	override fun set(@OriginalArg(1) bytes: ByteArray?) {
		this.buffer = ByteBuffer.allocateDirect(bytes!!.size)
		this.buffer!!.position(0)
		this.buffer!!.put(bytes)
	}
}
