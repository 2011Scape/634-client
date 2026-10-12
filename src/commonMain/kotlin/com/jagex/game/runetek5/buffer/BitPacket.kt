package com.jagex.game.runetek5.buffer

import com.jagex.game.runetek5.tools.Isaac
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!ua")
class BitPacket : Packet {

	@OriginalMember(owner = "client!ua", name = "Lb", descriptor = "I")
	private var bitPosition: Int = 0

	@OriginalMember(owner = "client!ua", name = "Nb", descriptor = "Lclient!ku;")
	private var random: Isaac? = null

	@OriginalMember(owner = "client!ua", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) size: Int) : super(size)

	@OriginalMember(owner = "client!ua", name = "w", descriptor = "(I)Z")
	fun largeOpcode(): Boolean {
		@Pc(21) val v: Int = (super.buffer!![super.currentPosition].toInt() - this.random!!.peek()) and 0xFF
		return v >= 128
	}

	@OriginalMember(owner = "client!ua", name = "a", descriptor = "(I[I)V")
	fun initIsaac(@OriginalArg(1) seed: IntArray?) {
		this.random = Isaac(seed)
	}

	@OriginalMember(owner = "client!ua", name = "x", descriptor = "(I)I")
	fun readOpcode(): Int {
		@Pc(21) val v: Int = (super.buffer!![super.currentPosition++].toInt() - this.random!!.next()) and 0xFF
		return if (v < 128) v else ((super.buffer!![super.currentPosition++].toInt() - this.random!!.next()) and 0xFF) + ((v - 128) shl 8)
	}

	@OriginalMember(owner = "client!ua", name = "q", descriptor = "(II)V")
	fun startPacket(@OriginalArg(0) op: Int) {
		super.buffer!![super.currentPosition++] = (op + this.random!!.next()).toByte()
	}

	@OriginalMember(owner = "client!ua", name = "a", descriptor = "(I[BII)V")
	fun readEncryptedBytes(@OriginalArg(0) length: Int, @OriginalArg(1) bytes: ByteArray?) {
		@Pc(7) var i: Int = 0
		while (i < length) {
			bytes!![i] = (super.buffer!![super.currentPosition++].toInt() - this.random!!.next()).toByte()
			i++
		}
	}

	@OriginalMember(owner = "client!ua", name = "f", descriptor = "(BI)I")
	fun gbit(@OriginalArg(1) n: Int): Int {
		var n: Int = n
		@Pc(15) var index: Int = this.bitPosition shr 3
		@Pc(23) var interest: Int = 8 - (this.bitPosition and 0x7)
		this.bitPosition += n
		@Pc(31) var v: Int = 0
		while (interest < n) {
			v += (super.buffer!![index++].toInt() and BitPacket.BIT_MASKS[interest]) shl (n - interest)
			n -= interest
			interest = 8
		}
		if (interest == n) {
			v += BitPacket.BIT_MASKS[interest] and super.buffer!![index].toInt()
		} else {
			v += (super.buffer!![index].toInt() shr (interest - n)) and BitPacket.BIT_MASKS[n]
		}
		return v
	}

	@OriginalMember(owner = "client!ua", name = "b", descriptor = "(Z)V")
	fun exitBitMode() {
		super.currentPosition = (this.bitPosition + 7) / 8
	}

	@OriginalMember(owner = "client!ua", name = "r", descriptor = "(II)I")
	fun bitsRemaining(@OriginalArg(1) pos: Int): Int {
		return pos * 8 - this.bitPosition
	}

	@OriginalMember(owner = "client!ua", name = "a", descriptor = "(ILclient!ku;)V")
	fun setRandom(@OriginalArg(1) isaac: Isaac?) {
		this.random = isaac
	}

	@OriginalMember(owner = "client!ua", name = "z", descriptor = "(I)V")
	fun enterBitMode() {
		this.bitPosition = super.currentPosition * 8
	}

	companion object {
		@OriginalMember(owner = "client!be", name = "s", descriptor = "[I")
		@JvmField
		val BIT_MASKS: IntArray = intArrayOf(0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727, 268435455, 536870911, 1073741823, Int.MAX_VALUE, -1)
	}
}
