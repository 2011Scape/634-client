package com.jagex.game.runetek5.toolkit.opengl.buffer

import com.jagex.game.runetek5.buffer.Packet
import jaclib.memory.Stream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!gh")
class GlBitBuffer : Packet {

	@OriginalMember(owner = "client!gh", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) size: Int) : super(size)

	@OriginalMember(owner = "client!gh", name = "a", descriptor = "(IF)V")
	fun writeFloatLittleEndian(@OriginalArg(1) value: Float) {
		@Pc(6) val rawIntBits: Int = Stream.floatToRawIntBits(value)
		this.buffer!![this.currentPosition++] = rawIntBits.toByte()
		this.buffer!![this.currentPosition++] = (rawIntBits shr 8).toByte()
		this.buffer!![this.currentPosition++] = (rawIntBits shr 16).toByte()
		this.buffer!![this.currentPosition++] = (rawIntBits shr 24).toByte()
	}

	@OriginalMember(owner = "client!gh", name = "a", descriptor = "(FB)V")
	fun writeFloatBigEndian(@OriginalArg(0) value: Float) {
		@Pc(6) val rawIntBits: Int = Stream.floatToRawIntBits(value)
		this.buffer!![this.currentPosition++] = (rawIntBits shr 24).toByte()
		this.buffer!![this.currentPosition++] = (rawIntBits shr 16).toByte()
		this.buffer!![this.currentPosition++] = (rawIntBits shr 8).toByte()
		this.buffer!![this.currentPosition++] = rawIntBits.toByte()
	}
}
