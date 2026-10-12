package com.jagex.game.runetek5.buffer

import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.Synchronized

object CircularBuffer {
	@OriginalMember(owner = "client!ap", name = "e", descriptor = "[[B")
	@JvmField
	val UNUSED_1: Array<ByteArray?> = arrayOfNulls<ByteArray>(1000)

	@OriginalMember(owner = "client!pi", name = "o", descriptor = "I")
	@JvmField
	var UNUSED_2_CURSOR_POS: Int = 0

	@OriginalMember(owner = "client!ns", name = "g", descriptor = "[[B")
	@JvmField
	val LOGIN_PACKET_BUFFERS: Array<ByteArray?> = arrayOfNulls<ByteArray>(250)

	@OriginalMember(owner = "client!bp", name = "r", descriptor = "[I")
	@JvmField
	var SLOT_IN_USE: IntArray? = null

	@OriginalMember(owner = "client!vn", name = "Q", descriptor = "I")
	@JvmField
	var UNUSED_1_CURSOR_POS: Int = 0

	@OriginalMember(owner = "client!mw", name = "I", descriptor = "[[B")
	@JvmField
	val UNUSED_2: Array<ByteArray?> = arrayOfNulls<ByteArray>(50)

	@OriginalMember(owner = "client!wd", name = "L", descriptor = "[I")
	@JvmField
	var STASH_FOR_BUFFER_SIZE: IntArray? = null

	@OriginalMember(owner = "client!hr", name = "L", descriptor = "[[[B")
	@JvmField
	var STASH: Array<Array<ByteArray?>?>? = null

	@OriginalMember(owner = "client!rp", name = "W", descriptor = "I")
	@JvmField
	var LOGIN_PACKET_CURSOR_POS: Int = 0

	@OriginalMember(owner = "client!db", name = "a", descriptor = "([I[II)V")
	@JvmStatic
	fun initialize(@OriginalArg(0) stashForBufferSize: IntArray?, @OriginalArg(1) maxStashCapacities: IntArray?) {
		if (stashForBufferSize == null || maxStashCapacities == null) {
			CircularBuffer.STASH = null
			CircularBuffer.SLOT_IN_USE = null
			CircularBuffer.STASH_FOR_BUFFER_SIZE = null
			return
		}
		CircularBuffer.STASH_FOR_BUFFER_SIZE = stashForBufferSize
		CircularBuffer.SLOT_IN_USE = IntArray(stashForBufferSize.size)
		CircularBuffer.STASH = arrayOfNulls<Array<ByteArray?>>(stashForBufferSize.size)
		@Pc(28) var i: Int = 0
		while (i < CircularBuffer.STASH_FOR_BUFFER_SIZE!!.size) {
			CircularBuffer.STASH!![i] = arrayOfNulls<ByteArray>(maxStashCapacities[i])
			i++
		}
	}

	@OriginalMember(owner = "client!ida", name = "a", descriptor = "(I[B)V")
	@JvmStatic
	@Synchronized
	fun releasePacketBuffer(@OriginalArg(1) buffer: ByteArray?) {
		if (buffer!!.size == 100 && CircularBuffer.UNUSED_1_CURSOR_POS < 1000) {
			CircularBuffer.UNUSED_1[CircularBuffer.UNUSED_1_CURSOR_POS++] = buffer
		} else if (buffer.size == 5000 && CircularBuffer.LOGIN_PACKET_CURSOR_POS < 250) {
			CircularBuffer.LOGIN_PACKET_BUFFERS[CircularBuffer.LOGIN_PACKET_CURSOR_POS++] = buffer
		} else if (buffer.size == 30000 && CircularBuffer.UNUSED_2_CURSOR_POS < 50) {
			CircularBuffer.UNUSED_2[CircularBuffer.UNUSED_2_CURSOR_POS++] = buffer
		} else if (CircularBuffer.STASH != null) {
			@Pc(65) var i: Int = 0
			while (i < CircularBuffer.STASH_FOR_BUFFER_SIZE!!.size) {
				if (CircularBuffer.STASH_FOR_BUFFER_SIZE!![i] == buffer.size && CircularBuffer.STASH!![i]!!.size > CircularBuffer.SLOT_IN_USE!![i]) {
					CircularBuffer.STASH!![i]!![CircularBuffer.SLOT_IN_USE!![i]++] = buffer
					return
				}
				i++
			}
		}
	}

	@OriginalMember(owner = "client!ida", name = "b", descriptor = "(II)[B")
	@JvmStatic
	@Synchronized
	fun allocatePacketBuffer(@OriginalArg(0) size: Int): ByteArray? {
		@Pc(21) val buffer: ByteArray?
		if (size == 100 && CircularBuffer.UNUSED_1_CURSOR_POS > 0) {
			buffer = CircularBuffer.UNUSED_1[--CircularBuffer.UNUSED_1_CURSOR_POS]
			CircularBuffer.UNUSED_1[CircularBuffer.UNUSED_1_CURSOR_POS] = null
			return buffer
		} else if (size == 5000 && CircularBuffer.LOGIN_PACKET_CURSOR_POS > 0) {
			buffer = CircularBuffer.LOGIN_PACKET_BUFFERS[--CircularBuffer.LOGIN_PACKET_CURSOR_POS]
			CircularBuffer.LOGIN_PACKET_BUFFERS[CircularBuffer.LOGIN_PACKET_CURSOR_POS] = null
			return buffer
		} else if (size == 30000 && CircularBuffer.UNUSED_2_CURSOR_POS > 0) {
			buffer = CircularBuffer.UNUSED_2[--CircularBuffer.UNUSED_2_CURSOR_POS]
			CircularBuffer.UNUSED_2[CircularBuffer.UNUSED_2_CURSOR_POS] = null
			return buffer
		} else {
			if (CircularBuffer.STASH != null) {
				@Pc(79) var i: Int = 0
				while (i < CircularBuffer.STASH_FOR_BUFFER_SIZE!!.size) {
					if (CircularBuffer.STASH_FOR_BUFFER_SIZE!![i] == size && CircularBuffer.SLOT_IN_USE!![i] > 0) {
						@Pc(109) val stashedBuffer: ByteArray? = CircularBuffer.STASH!![i]!![--CircularBuffer.SLOT_IN_USE!![i]]
						CircularBuffer.STASH!![i]!![CircularBuffer.SLOT_IN_USE!![i]] = null
						return stashedBuffer
					}
					i++
				}
			}
			return ByteArray(size)
		}
	}
}
