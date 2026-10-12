package com.jagex.game.runetek5.js5

import com.jagex.game.runetek5.DroppedInputStream
import com.jagex.game.runetek5.core.util.Arrays
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import emulatedLong
import kotlin.jvm.JvmStatic

@OriginalClass("client!lda")
class Whirlpool {

	@OriginalMember(owner = "client!lda", name = "e", descriptor = "[B")
	private val bitLength: ByteArray = ByteArray(32)

	@OriginalMember(owner = "client!lda", name = "k", descriptor = "[J")
	private val L: LongArray = LongArray(8)

	@OriginalMember(owner = "client!lda", name = "h", descriptor = "I")
	private var bufferPosition: Int = 0

	@OriginalMember(owner = "client!lda", name = "c", descriptor = "I")
	private var bufferBits: Int = 0

	@OriginalMember(owner = "client!lda", name = "l", descriptor = "[J")
	private val K: LongArray = LongArray(8)

	@OriginalMember(owner = "client!lda", name = "m", descriptor = "[J")
	private val block: LongArray = LongArray(8)

	@OriginalMember(owner = "client!lda", name = "o", descriptor = "[B")
	private val buffer: ByteArray = ByteArray(64)

	@OriginalMember(owner = "client!lda", name = "i", descriptor = "[J")
	private val state: LongArray = LongArray(8)

	@OriginalMember(owner = "client!lda", name = "n", descriptor = "[J")
	private val hash: LongArray = LongArray(8)

	@OriginalMember(owner = "client!lda", name = "a", descriptor = "(I)V")
	private fun processBuffer() {
		@Pc(7) var blockIndex: Int = 0
		@Pc(9) var bufferOffset: Int = 0
		while (blockIndex < 8) {
			this.block[blockIndex] = Whirlpool.xor(Whirlpool.xor(Whirlpool.bitAnd(255L, this.buffer[bufferOffset + 6].toLong()) shl 8, Whirlpool.xor(Whirlpool.bitAnd(this.buffer[bufferOffset + 5].toLong() shl 16, 0xFFL shl 16), Whirlpool.xor(Whirlpool.xor(Whirlpool.bitAnd(0xFFL shl 32, this.buffer[bufferOffset + 3].toLong() shl 32), Whirlpool.xor(Whirlpool.xor(Whirlpool.bitAnd(0xFFL shl 48, this.buffer[bufferOffset + 1].toLong() shl 48), this.buffer[bufferOffset].toLong() shl 56), Whirlpool.bitAnd(this.buffer[bufferOffset + 2].toLong() shl 40, 0xFFL shl 40))), Whirlpool.bitAnd(this.buffer[bufferOffset + 4].toLong(), 255L) shl 24))), Whirlpool.bitAnd(255L, this.buffer[bufferOffset + 7].toLong()))
			bufferOffset += 8
			blockIndex++
		}
		@Pc(121) var i: Int = 0
		while (i < 8) {
			this.K[i] = this.hash[i]
			this.state[i] = Whirlpool.xor(this.block[i], this.K[i])
			i++
		}
		@Pc(151) var row: Int
		@Pc(147) var j: Int = 1
		while (j <= 10) {
			@Pc(160) var local160: Int
			@Pc(162) var local162: Int
			row = 0
			while (row < 8) {
				this.L[row] = 0L
				var keyColumn: Int = 0
				var keyShift: Int = 56
				while (keyColumn < 8) {
					this.L[row] = Whirlpool.xor(this.L[row], DroppedInputStream.CIRCULANT_TABLE[keyColumn][(this.K[(row - keyColumn) and 0x7] ushr keyShift).toInt() and 0xFF])
					keyShift -= 8
					keyColumn++
				}
				row++
			}
			var keyWord: Int = 0
			while (keyWord < 8) {
				this.K[keyWord] = this.L[keyWord]
				keyWord++
			}
			this.K[0] = Whirlpool.xor(this.K[0], DroppedInputStream.ROUND_CONSTANTS[j])
			@Pc(245) var local245: Int
			var stateRow: Int = 0
			while (stateRow < 8) {
				this.L[stateRow] = this.K[stateRow]
				var stateColumn: Int = 0
				@Pc(247) var shift: Int = 56
				while (stateColumn < 8) {
					this.L[stateRow] = Whirlpool.xor(this.L[stateRow], DroppedInputStream.CIRCULANT_TABLE[stateColumn][(this.state[(stateRow - stateColumn) and 0x7] ushr shift).toInt() and 0xFF])
					shift -= 8
					stateColumn++
				}
				stateRow++
			}
			var stateWord: Int = 0
			while (stateWord < 8) {
				this.state[stateWord] = this.L[stateWord]
				stateWord++
			}
			j++
		}
		row = 0
		while (row < 8) {
			this.hash[row] = Whirlpool.xor(this.hash[row], Whirlpool.xor(this.block[row], this.state[row]))
			row++
		}
	}

	@OriginalMember(owner = "client!lda", name = "b", descriptor = "(I)V")
	fun NESSIEinit() {
		@Pc(15) var i: Int = 0
		while (i < 32) {
			this.bitLength[i] = 0
			i++
		}
		this.buffer[0] = 0
		this.bufferPosition = 0
		this.bufferBits = 0
		@Pc(39) var j: Int = 0
		while (j < 8) {
			this.hash[j] = 0L
			j++
		}
	}

	@OriginalMember(owner = "client!lda", name = "a", descriptor = "(J[BI)V")
	fun NESSIEadd(@OriginalArg(0) sourceBits: Long, @OriginalArg(1) source: ByteArray?) {
		var sourceBits: Long = sourceBits
		@Pc(7) var sourcePos: Int = 0
		@Pc(16) val sourceGap: Int = (8 - (sourceBits.toInt() and 0x7)) and 0x7
		@Pc(21) val bufferRem: Int = this.bufferBits and 0x7
		@Pc(23) var value: Long = sourceBits
		@Pc(25) var lengthIndex: Int = 31
		@Pc(27) var carry: Int = 0
		while (lengthIndex >= 0) {
			carry += (this.bitLength[lengthIndex].toInt() and 0xFF) + (value.toInt() and 0xFF)
			this.bitLength[lengthIndex] = carry.toByte()
			value = value ushr 0x8
			carry = carry ushr 0x8
			lengthIndex--
		}
		@Pc(92) var b: Int
		while (sourceBits > 8L) {
			b = ((source!![sourcePos].toInt() shl sourceGap) and 0xFF) or ((source[sourcePos + 1].toInt() and 0xFF) ushr (8 - sourceGap))
			if (b < 0 || b >= 256) {
				throw RuntimeException("LOGIC ERROR")
			}
			this.buffer[this.bufferPosition] = (this.buffer[this.bufferPosition].toInt() or (b ushr bufferRem)).toByte()
			this.bufferPosition++
			this.bufferBits += 8 - bufferRem
			if (this.bufferBits == 512) {
				this.processBuffer()
				this.bufferPosition = 0
				this.bufferBits = 0
			}
			this.buffer[this.bufferPosition] = ((b shl (8 - bufferRem)) and 0xFF).toByte()
			sourcePos++
			sourceBits -= 8L
			this.bufferBits += bufferRem
		}
		if (sourceBits > 0L) {
			b = (source!![sourcePos].toInt() shl sourceGap) and 0xFF
			this.buffer[this.bufferPosition] = (this.buffer[this.bufferPosition].toInt() or (b ushr bufferRem)).toByte()
		} else {
			b = 0
		}
		if (sourceBits + bufferRem.toLong() < 8L) {
			this.bufferBits = (this.bufferBits.toLong() + sourceBits).toInt()
			return
		}
		this.bufferBits += 8 - bufferRem
		sourceBits -= (8 - bufferRem).toLong()
		this.bufferPosition++
		if (this.bufferBits == 512) {
			this.processBuffer()
			this.bufferPosition = 0
			this.bufferBits = 0
		}
		this.buffer[this.bufferPosition] = ((b shl (8 - bufferRem)) and 0xFF).toByte()
		this.bufferBits += sourceBits.toInt()
	}

	@OriginalMember(owner = "client!lda", name = "a", descriptor = "(ZI[B)V")
	fun NESSIEfinalize(@OriginalArg(2) digest: ByteArray?) {
		this.buffer[this.bufferPosition] = (this.buffer[this.bufferPosition].toInt() or (0x80 ushr (this.bufferBits and 0x7))).toByte()
		this.bufferPosition++
		if (this.bufferPosition > 32) {
			while (true) {
				if (this.bufferPosition >= 64) {
					this.processBuffer()
					this.bufferPosition = 0
					break
				}
				this.buffer[this.bufferPosition++] = 0
			}
		}
		while (this.bufferPosition < 32) {
			this.buffer[this.bufferPosition++] = 0
		}
		Arrays.copy(this.bitLength, 0, this.buffer, 32, 32)
		this.processBuffer()
		@Pc(90) var i: Int = 0
		@Pc(101) var offset: Int = 0
		while (i < 8) {
			@Pc(108) val value: Long = this.hash[i]
			digest!![offset] = (value ushr 56).toByte()
			digest[offset + 1] = (value ushr 48).toByte()
			digest[offset + 2] = (value ushr 40).toByte()
			digest[offset + 3] = (value ushr 32).toByte()
			digest[offset + 4] = (value ushr 24).toByte()
			digest[offset + 5] = (value ushr 16).toByte()
			digest[offset + 6] = (value ushr 8).toByte()
			digest[offset + 7] = value.toByte()
			i++
			offset += 8
		}
	}

	companion object {
		@OriginalMember(owner = "client!dga", name = "a", descriptor = "(II[BI)[B")
		@JvmStatic
		fun digest(@OriginalArg(1) srcPosition: Int, @OriginalArg(2) src: ByteArray?, @OriginalArg(3) size: Int): ByteArray {
			if (emulatedLong) {
				// Not part of the original client: the same digest without Long (IntWhirlpool)
				return IntWhirlpool.digest(srcPosition, src!!, size)
			}
			@Pc(22) val dst: ByteArray?
			if (srcPosition > 0) {
				dst = ByteArray(size)
				@Pc(24) var i: Int = 0
				while (i < size) {
					dst[i] = src!![srcPosition + i]
					i++
				}
			} else {
				dst = src
			}
			@Pc(50) val whirlpool: Whirlpool = Whirlpool()
			whirlpool.NESSIEinit()
			whirlpool.NESSIEadd((size * 8).toLong(), dst)
			@Pc(66) val digest: ByteArray = ByteArray(64)
			whirlpool.NESSIEfinalize(digest)
			return digest
		}

		@OriginalMember(owner = "client!nda", name = "a", descriptor = "(JJ)J")
		@JvmStatic
		fun xor(@OriginalArg(0) lhs: Long, @OriginalArg(1) rhs: Long): Long {
			return lhs xor rhs
		}

		@OriginalMember(owner = "client!vd", name = "a", descriptor = "(JJ)J")
		@JvmStatic
		fun or(@OriginalArg(0) a: Long, @OriginalArg(1) b: Long): Long {
			return a or b
		}

		@OriginalMember(owner = "client!gia", name = "a", descriptor = "(JJ)J")
		@JvmStatic
		fun bitAnd(@OriginalArg(0) a: Long, @OriginalArg(1) b: Long): Long {
			return a and b
		}
	}
}
