package com.jagex.game.runetek5.buffer

import com.jagex.game.runetek5.cache.node.Node
import java.math.BigInteger
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.GlxProgram

@OriginalClass("client!cea")
open class Packet : Node {

	@OriginalMember(owner = "client!cea", name = "wb", descriptor = "I")
	@JvmField
	var currentPosition: Int = 0

	@OriginalMember(owner = "client!cea", name = "F", descriptor = "[B")
	@JvmField
	var buffer: ByteArray? = null

	@OriginalMember(owner = "client!cea", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) size: Int) {
		this.currentPosition = 0
		this.buffer = CircularBuffer.allocatePacketBuffer(size)
	}

	@OriginalMember(owner = "client!cea", name = "<init>", descriptor = "([B)V")
	constructor(@OriginalArg(0) src: ByteArray?) {
		this.buffer = src
		this.currentPosition = 0
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(B)I")
	fun readUByteInverse(): Int {
		return -this.buffer!![this.currentPosition++].toInt() and 0xFF
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(I)I")
	fun readUShort(): Int {
		this.currentPosition += 2
		return (this.buffer!![this.currentPosition - 1].toInt() and 0xFF) + ((this.buffer!![this.currentPosition - 2].toInt() and 0xFF) shl 8)
	}

	@OriginalMember(owner = "client!cea", name = "b", descriptor = "(B)J")
	fun readULongLittle(): Long {
		@Pc(15) val low: Long = this.readUIntLittle().toLong() and 0xFFFFFFFFL
		@Pc(22) val high: Long = this.readUIntLittle().toLong() and 0xFFFFFFFFL
		return (high shl 32) + low
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(BLjava/lang/String;)V")
	fun writeString(@OriginalArg(1) string: String?) {
		@Pc(13) val nulIndex: Int = string!!.indexOf('\u0000')
		if (nulIndex >= 0) {
			throw IllegalArgumentException("NUL character at " + nulIndex + " - cannot pjstr")
		}
		this.currentPosition += Utils.encodeCp1252(this.buffer!!, string.length, string, this.currentPosition)
		this.buffer!![this.currentPosition++] = 0
	}

	@OriginalMember(owner = "client!cea", name = "b", descriptor = "(I)I")
	fun readSum(): Int {
		@Pc(7) var total: Int = 0
		@Pc(11) var part: Int
		part = this.readUSmart()
		while (part == 32767) {
			total += 32767
			part = this.readUSmart()
		}
		return total + part
	}

	@OriginalMember(owner = "client!cea", name = "c", descriptor = "(II)V")
	fun writeShortAdd(@OriginalArg(1) value: Int) {
		this.buffer!![this.currentPosition++] = (value shr 8).toByte()
		this.buffer!![this.currentPosition++] = (value + 128).toByte()
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(IB)V")
	fun writeIntMiddle(@OriginalArg(0) value: Int) {
		this.buffer!![this.currentPosition++] = (value shr 8).toByte()
		this.buffer!![this.currentPosition++] = value.toByte()
		this.buffer!![this.currentPosition++] = (value shr 24).toByte()
		this.buffer!![this.currentPosition++] = (value shr 16).toByte()
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(BI)V")
	fun writeShort(@OriginalArg(1) value: Int) {
		this.buffer!![this.currentPosition++] = (value shr 8).toByte()
		this.buffer!![this.currentPosition++] = value.toByte()
	}

	@OriginalMember(owner = "client!cea", name = "d", descriptor = "(II)V")
	fun writeIntLength(@OriginalArg(1) length: Int) {
		this.buffer!![this.currentPosition - length - 4] = (length shr 24).toByte()
		this.buffer!![this.currentPosition - length - 3] = (length shr 16).toByte()
		this.buffer!![this.currentPosition - length - 2] = (length shr 8).toByte()
		this.buffer!![this.currentPosition - length - 1] = length.toByte()
	}

	@OriginalMember(owner = "client!cea", name = "e", descriptor = "(II)V")
	fun writeIndex(@OriginalArg(1) length: Int) {
		this.buffer!![this.currentPosition - length - 1] = length.toByte()
	}

	@OriginalMember(owner = "client!cea", name = "f", descriptor = "(II)V")
	fun writeMedium(@OriginalArg(1) value: Int) {
		this.buffer!![this.currentPosition++] = (value shr 16).toByte()
		this.buffer!![this.currentPosition++] = (value shr 8).toByte()
		this.buffer!![this.currentPosition++] = value.toByte()
	}

	@OriginalMember(owner = "client!cea", name = "d", descriptor = "(I)B")
	fun readByteAdd(): Byte {
		return (this.buffer!![this.currentPosition++].toInt() - 128).toByte()
	}

	@OriginalMember(owner = "client!cea", name = "e", descriptor = "(B)I")
	fun readUByteSubtract(): Int {
		return (128 - this.buffer!![this.currentPosition++].toInt()) and 0xFF
	}

	@OriginalMember(owner = "client!cea", name = "f", descriptor = "(B)I")
	fun readUIntMiddle(): Int {
		this.currentPosition += 4
		return ((this.buffer!![this.currentPosition - 1].toInt() and 0xFF) shl 16) + ((this.buffer!![this.currentPosition - 2].toInt() shl 24) and 0xFF000000.toInt()) + ((this.buffer!![this.currentPosition + -4].toInt() and 0xFF) shl 8) + (this.buffer!![this.currentPosition - 3].toInt() and 0xFF)
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(IZ)I")
	fun writeCrc(@OriginalArg(0) start: Int): Int {
		@Pc(17) val crc: Int = Packet.getCrc32(this.currentPosition, this.buffer, start)
		this.writeInt(crc)
		return crc
	}

	@OriginalMember(owner = "client!cea", name = "e", descriptor = "(I)B")
	fun readByteSubtract(): Byte {
		return (128 - this.buffer!![this.currentPosition++].toInt()).toByte()
	}

	@OriginalMember(owner = "client!cea", name = "f", descriptor = "(I)I")
	fun readUIntInverseMiddle(): Int {
		this.currentPosition += 4
		return ((this.buffer!![this.currentPosition - 1].toInt() and 0xFF) shl 8) + ((this.buffer!![this.currentPosition - 3].toInt() and 0xFF) shl 24) + ((this.buffer!![this.currentPosition - 4].toInt() and 0xFF) shl 16) + (this.buffer!![this.currentPosition - 2].toInt() and 0xFF)
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "([BIIB)V")
	fun readBytesReversed(@OriginalArg(0) bytes: ByteArray?, @OriginalArg(2) length: Int) {
		@Pc(11) var i: Int = length - 1
		while (i >= 0) {
			bytes!![i] = this.buffer!![this.currentPosition++]
			i--
		}
	}

	@OriginalMember(owner = "client!cea", name = "g", descriptor = "(II)V")
	fun writeIntLittle(@OriginalArg(1) value: Int) {
		this.buffer!![this.currentPosition++] = value.toByte()
		this.buffer!![this.currentPosition++] = (value shr 8).toByte()
		this.buffer!![this.currentPosition++] = (value shr 16).toByte()
		this.buffer!![this.currentPosition++] = (value shr 24).toByte()
	}

	@OriginalMember(owner = "client!cea", name = "h", descriptor = "(II)V")
	fun writeShortAddLittle(@OriginalArg(1) value: Int) {
		this.buffer!![this.currentPosition++] = (value + 128).toByte()
		this.buffer!![this.currentPosition++] = (value shr 8).toByte()
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(IZ[II)V")
	fun encryptXtea(@OriginalArg(0) start: Int, @OriginalArg(2) keys: IntArray?, @OriginalArg(3) end: Int) {
		@Pc(8) val savedPosition: Int = this.currentPosition
		this.currentPosition = start
		@Pc(18) val blockCount: Int = (end - start) / 8
		@Pc(20) var i: Int = 0
		while (i < blockCount) {
			@Pc(26) var v0: Int = this.readInt()
			@Pc(30) var v1: Int = this.readInt()
			@Pc(32) var sum: Int = 0
			@Pc(36) var rounds: Int = 32
			while (rounds-- > 0) {
				v0 += (sum + keys!![sum and 0x3]) xor (((v1 ushr 5) xor (v1 shl 4)) - -v1)
				sum += -1640531527
				v1 += (sum + keys!![(sum ushr 11) and 0x3]) xor (v0 + ((v0 ushr 5) xor (v0 shl 4)))
			}
			this.currentPosition -= 8
			this.writeInt(v0)
			this.writeInt(v1)
			i++
		}
		this.currentPosition = savedPosition
	}

	@OriginalMember(owner = "client!cea", name = "g", descriptor = "(I)I")
	fun readUMedium(): Int {
		this.currentPosition += 3
		return ((this.buffer!![this.currentPosition - 2].toInt() and 0xFF) shl 8) + (((this.buffer!![this.currentPosition - 3].toInt() and 0xFF) shl 16) + (this.buffer!![this.currentPosition - 1].toInt() and 0xFF))
	}

	@OriginalMember(owner = "client!cea", name = "h", descriptor = "(I)Z")
	fun checkCrc(): Boolean {
		this.currentPosition -= 4
		@Pc(24) val calculatedCrc: Int = Packet.getCrc32(this.currentPosition, this.buffer, 0)
		@Pc(28) val expectedCrc: Int = this.readInt()
		return calculatedCrc == expectedCrc
	}

	@OriginalMember(owner = "client!cea", name = "b", descriptor = "(IB)V")
	fun writeShortLittle(@OriginalArg(0) value: Int) {
		this.buffer!![this.currentPosition++] = value.toByte()
		this.buffer!![this.currentPosition++] = (value shr 8).toByte()
	}

	@OriginalMember(owner = "client!cea", name = "j", descriptor = "(I)I")
	fun readUShortAdd(): Int {
		this.currentPosition += 2
		return ((this.buffer!![this.currentPosition - 2].toInt() and 0xFF) shl 8) + ((this.buffer!![this.currentPosition - 1].toInt() - 128) and 0xFF)
	}

	@OriginalMember(owner = "client!cea", name = "i", descriptor = "(II)V")
	fun writeIntInverseMiddle(@OriginalArg(0) value: Int) {
		this.buffer!![this.currentPosition++] = (value shr 16).toByte()
		this.buffer!![this.currentPosition++] = (value shr 24).toByte()
		this.buffer!![this.currentPosition++] = value.toByte()
		this.buffer!![this.currentPosition++] = (value shr 8).toByte()
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(IJB)V")
	fun writeVariableLengthLong(@OriginalArg(0) size: Int, @OriginalArg(1) value: Long) {
		@Pc(6) val lastByte: Int = size - 1
		if (lastByte < 0 || lastByte > 7) {
			throw IllegalArgumentException()
		}
		@Pc(32) var i: Int = lastByte * 8
		while (i >= 0) {
			this.buffer!![this.currentPosition++] = (value shr i).toByte()
			i -= 8
		}
	}

	@OriginalMember(owner = "client!cea", name = "k", descriptor = "(I)I")
	fun readUShortAddLittle(): Int {
		this.currentPosition += 2
		return ((this.buffer!![this.currentPosition - 2].toInt() - 128) and 0xFF) + ((this.buffer!![this.currentPosition - 1].toInt() and 0xFF) shl 8)
	}

	@OriginalMember(owner = "client!cea", name = "l", descriptor = "(I)I")
	fun readUIntLittle(): Int {
		this.currentPosition += 4
		return ((this.buffer!![this.currentPosition - 3].toInt() and 0xFF) shl 8) + ((this.buffer!![this.currentPosition - 1].toInt() shl 24) and 0xFF000000.toInt()) + ((this.buffer!![this.currentPosition + -2].toInt() and 0xFF) shl 16) + (this.buffer!![this.currentPosition + -4].toInt() and 0xFF)
	}

	@OriginalMember(owner = "client!cea", name = "g", descriptor = "(B)V")
	fun flush() {
		if (this.buffer != null) {
			CircularBuffer.releasePacketBuffer(this.buffer)
		}
		this.buffer = null
	}

	@OriginalMember(owner = "client!cea", name = "m", descriptor = "(I)B")
	fun readByteInverse(): Byte {
		return (-this.buffer!![this.currentPosition++].toInt()).toByte()
	}

	@OriginalMember(owner = "client!cea", name = "h", descriptor = "(B)I")
	fun readSmart(): Int {
		@Pc(11) val peek: Int = this.buffer!![this.currentPosition].toInt() and 0xFF
		return if (peek < 128) this.readUByte() - 64 else this.readUShort() - 49152
	}

	@OriginalMember(owner = "client!cea", name = "n", descriptor = "(I)I")
	fun readUMediumLittle(): Int {
		this.currentPosition += 3
		return (this.buffer!![this.currentPosition - 3].toInt() and 0xFF) + ((this.buffer!![this.currentPosition - 2].toInt() and 0xFF) shl 8) + ((this.buffer!![this.currentPosition + -1].toInt() and 0xFF) shl 16)
	}

	@OriginalMember(owner = "client!cea", name = "i", descriptor = "(B)I")
	fun readIntLE(): Int {
		this.currentPosition += 4
		return (this.buffer!![this.currentPosition - 4].toInt() and 0xFF) + ((this.buffer!![this.currentPosition - 3].toInt() and 0xFF) shl 8) + ((this.buffer!![this.currentPosition - 1].toInt() and 0xFF) shl 24) + ((this.buffer!![this.currentPosition - 2].toInt() and 0xFF) shl 16)
	}

	@OriginalMember(owner = "client!cea", name = "o", descriptor = "(I)J")
	fun readLong(): Long {
		@Pc(15) val high: Long = this.readInt().toLong() and 0xFFFFFFFFL
		@Pc(22) val low: Long = this.readInt().toLong() and 0xFFFFFFFFL
		return low + (high shl 32)
	}

	@OriginalMember(owner = "client!cea", name = "j", descriptor = "(B)I")
	fun readVarInt(): Int {
		@Pc(14) var current: Byte = this.buffer!![this.currentPosition++]
		@Pc(16) var value: Int = 0
		while (current < 0) {
			value = ((current.toInt() and 0x7F) or value) shl 7
			current = this.buffer!![this.currentPosition++]
		}
		return value or current.toInt()
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(I[III)V")
	fun decryptXtea(@OriginalArg(1) keys: IntArray?, @OriginalArg(3) length: Int) {
		@Pc(8) val currentPosition: Int = this.currentPosition
		this.currentPosition = 5
		@Pc(18) val blockCount: Int = (length - 5) / 8
		@Pc(20) var i: Int = 0
		while (i < blockCount) {
			@Pc(26) var v0: Int = this.readInt()
			@Pc(30) var v1: Int = this.readInt()
			@Pc(32) var sum: Int = -957401312
			@Pc(36) var rounds: Int = 32
			while (rounds-- > 0) {
				v1 -= (((v0 shl 4) xor (v0 ushr 5)) + v0) xor (sum - -keys!![(sum ushr 11) and 0x4D000003])
				sum -= -1640531527
				v0 -= (sum + keys!![sum and 0x3]) xor (((v1 shl 4) xor (v1 ushr 5)) - -v1)
			}
			this.currentPosition -= 8
			this.writeInt(v0)
			this.writeInt(v1)
			i++
		}
		this.currentPosition = currentPosition
	}

	@OriginalMember(owner = "client!cea", name = "j", descriptor = "(II)J")
	fun bitpackLong(@OriginalArg(0) size: Int): Long {
		@Pc(2) val lastByte: Int = size - 1
		if (lastByte < 0 || lastByte > 7) {
			throw IllegalArgumentException()
		}
		@Pc(26) var shift: Int = lastByte * 8
		@Pc(28) var value: Long = 0L
		while (shift >= 0) {
			value = value or ((this.buffer!![this.currentPosition++].toLong() and 0xFFL) shl shift)
			shift -= 8
		}
		return value
	}

	@OriginalMember(owner = "client!cea", name = "k", descriptor = "(B)I")
	fun readMedium(): Int {
		this.currentPosition += 3
		@Pc(48) var value: Int = (this.buffer!![this.currentPosition - 1].toInt() and 0xFF) + ((this.buffer!![this.currentPosition - 3].toInt() and 0xFF) shl 16) + ((this.buffer!![this.currentPosition + -2].toInt() and 0xFF) shl 8)
		if (value > 8388607) {
			value -= 16777216
		}
		return value
	}

	@OriginalMember(owner = "client!cea", name = "b", descriptor = "(BI)V")
	fun writeByteSubtract(@OriginalArg(1) value: Int) {
		this.buffer!![this.currentPosition++] = (128 - value).toByte()
	}

	@OriginalMember(owner = "client!cea", name = "p", descriptor = "(I)Ljava/lang/String;")
	fun readJagString(): String? {
		@Pc(14) val version: Byte = this.buffer!![this.currentPosition++]
		if (version.toInt() != 0) {
			throw IllegalStateException("Bad version number in gjstr2")
		}
		@Pc(24) val currentPosition: Int = this.currentPosition
		while (this.buffer!![this.currentPosition++].toInt() != 0) {
		}
		@Pc(46) val length: Int = this.currentPosition - currentPosition - 1
		return if (length == 0) "" else Utils.readString(this.buffer!!, length, currentPosition)
	}

	@OriginalMember(owner = "client!cea", name = "q", descriptor = "(I)I")
	fun readShort(): Int {
		this.currentPosition += 2
		@Pc(40) var value: Int = (this.buffer!![this.currentPosition - 1].toInt() and 0xFF) + ((this.buffer!![this.currentPosition - 2].toInt() and 0xFF) shl 8)
		if (value > 32767) {
			value -= 65536
		}
		return value
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(Z)I")
	fun readUShortLittle(): Int {
		this.currentPosition += 2
		return ((this.buffer!![this.currentPosition - 1].toInt() and 0xFF) shl 8) + (this.buffer!![this.currentPosition - 2].toInt() and 0xFF)
	}

	@OriginalMember(owner = "client!cea", name = "c", descriptor = "(BI)V")
	fun writeByteInverse(@OriginalArg(1) value: Int) {
		this.buffer!![this.currentPosition++] = (-value).toByte()
	}

	@OriginalMember(owner = "client!cea", name = "l", descriptor = "(B)I")
	fun readShortAdd(): Int {
		this.currentPosition += 2
		@Pc(33) var value: Int = ((this.buffer!![this.currentPosition - 1].toInt() - 128) and 0xFF) + ((this.buffer!![this.currentPosition - 2].toInt() and 0xFF) shl 8)
		if (value > 32767) {
			value -= 65536
		}
		return value
	}

	@OriginalMember(owner = "client!cea", name = "m", descriptor = "(B)Ljava/lang/String;")
	fun readString(): String? {
		@Pc(14) val currentPosition: Int = this.currentPosition
		while (this.buffer!![this.currentPosition++].toInt() != 0) {
		}
		@Pc(36) val length: Int = this.currentPosition - currentPosition - 1
		return if (length == 0) "" else Utils.readString(this.buffer!!, length, currentPosition)
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(ZI)V")
	fun writeByte(@OriginalArg(1) value: Int) {
		this.buffer!![this.currentPosition++] = value.toByte()
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(II[BI)V")
	fun writeBytes(@OriginalArg(0) length: Int, @OriginalArg(1) offset: Int, @OriginalArg(2) bytes: ByteArray?) {
		@Pc(3) var i: Int = offset
		while (i < offset + length) {
			this.buffer!![this.currentPosition++] = bytes!![i]
			i++
		}
	}

	@OriginalMember(owner = "client!cea", name = "l", descriptor = "(II)V")
	fun writeSmart(@OriginalArg(1) data: Int) {
		if (data >= 0 && data < 128) {
			this.writeByte(data)
		} else if (data >= 0 && data < 32768) {
			this.writeShort(data + 32768)
		} else {
			throw IllegalArgumentException()
		}
	}

	@OriginalMember(owner = "client!cea", name = "s", descriptor = "(I)I")
	fun readUSmart(): Int {
		@Pc(16) val peek: Int = this.buffer!![this.currentPosition].toInt() and 0xFF
		return if (peek < 128) this.readUByte() else this.readUShort() - 32768
	}

	@OriginalMember(owner = "client!cea", name = "m", descriptor = "(II)V")
	fun writeShortLength(@OriginalArg(1) length: Int) {
		this.buffer!![this.currentPosition - length - 2] = (length shr 8).toByte()
		this.buffer!![this.currentPosition - length - 1] = length.toByte()
	}

	@OriginalMember(owner = "client!cea", name = "t", descriptor = "(I)Ljava/lang/String;")
	fun readNullString(): String? {
		if (this.buffer!![this.currentPosition].toInt() == 0) {
			this.currentPosition++
			return null
		} else {
			return this.readString()
		}
	}

	@OriginalMember(owner = "client!cea", name = "n", descriptor = "(B)I")
	fun readInt(): Int {
		this.currentPosition += 4
		return (this.buffer!![this.currentPosition - 1].toInt() and 0xFF) + ((this.buffer!![this.currentPosition - 4].toInt() and 0xFF) shl 24) + ((this.buffer!![this.currentPosition + -3].toInt() and 0xFF) shl 16) + ((this.buffer!![this.currentPosition + -2].toInt() and 0xFF) shl 8)
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(Ljava/lang/String;I)V")
	fun writeJagString(@OriginalArg(0) string: String?) {
		@Pc(15) val nulIndex: Int = string!!.indexOf('\u0000')
		if (nulIndex >= 0) {
			throw IllegalArgumentException("NUL character at " + nulIndex + " - cannot pjstr2")
		}
		this.buffer!![this.currentPosition++] = 0
		this.currentPosition += Utils.encodeCp1252(this.buffer!!, string.length, string, this.currentPosition)
		this.buffer!![this.currentPosition++] = 0
	}

	@OriginalMember(owner = "client!cea", name = "u", descriptor = "(I)I")
	fun readUByte(): Int {
		return this.buffer!![this.currentPosition++].toInt() and 0xFF
	}

	@OriginalMember(owner = "client!cea", name = "v", descriptor = "(I)B")
	fun readByte(): Byte {
		return this.buffer!![this.currentPosition++]
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(III[B)V")
	fun readBytes(@OriginalArg(1) offset: Int, @OriginalArg(2) length: Int, @OriginalArg(3) bytes: ByteArray?) {
		@Pc(7) var i: Int = offset
		while (i < length + offset) {
			bytes!![i] = this.buffer!![this.currentPosition++]
			i++
		}
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(Ljava/math/BigInteger;BLjava/math/BigInteger;)V")
	fun applyRSA(@OriginalArg(0) modulus: BigInteger?, @OriginalArg(2) exponent: BigInteger?) {
		@Pc(6) val currentPosition: Int = this.currentPosition
		this.currentPosition = 0
		@Pc(12) val data: ByteArray = ByteArray(currentPosition)
		this.readBytes(0, currentPosition, data)
		@Pc(28) val bigInteger3: BigInteger = BigInteger(data)
		@Pc(33) val bigInteger4: BigInteger = bigInteger3.modPow(exponent, modulus)
		@Pc(36) val encrypted: ByteArray = bigInteger4.toByteArray()
		this.currentPosition = 0
		this.writeShort(encrypted.size)
		this.writeBytes(encrypted.size, 0, encrypted)
	}

	@OriginalMember(owner = "client!cea", name = "d", descriptor = "(BI)V")
	fun writeInt(@OriginalArg(1) value: Int) {
		this.buffer!![this.currentPosition++] = (value shr 24).toByte()
		this.buffer!![this.currentPosition++] = (value shr 16).toByte()
		this.buffer!![this.currentPosition++] = (value shr 8).toByte()
		this.buffer!![this.currentPosition++] = value.toByte()
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(JB)V")
	fun writeLong(@OriginalArg(0) value: Long) {
		this.buffer!![this.currentPosition++] = (value shr 56).toByte()
		this.buffer!![this.currentPosition++] = (value shr 48).toByte()
		this.buffer!![this.currentPosition++] = (value shr 40).toByte()
		this.buffer!![this.currentPosition++] = (value shr 32).toByte()
		this.buffer!![this.currentPosition++] = (value shr 24).toByte()
		this.buffer!![this.currentPosition++] = (value shr 16).toByte()
		this.buffer!![this.currentPosition++] = (value shr 8).toByte()
		this.buffer!![this.currentPosition++] = value.toByte()
	}

	@OriginalMember(owner = "client!cea", name = "o", descriptor = "(B)I")
	fun readUByteAdd(): Int {
		return (this.buffer!![this.currentPosition++].toInt() - 128) and 0xFF
	}

	@OriginalMember(owner = "client!cea", name = "n", descriptor = "(II)V")
	fun writeIntLE(@OriginalArg(1) value: Int) {
		this.buffer!![this.currentPosition++] = value.toByte()
		this.buffer!![this.currentPosition++] = (value shr 8).toByte()
		this.buffer!![this.currentPosition++] = (value shr 16).toByte()
		this.buffer!![this.currentPosition++] = (value shr 24).toByte()
	}

	@OriginalMember(owner = "client!cea", name = "e", descriptor = "(BI)V")
	fun writeByteAdd(@OriginalArg(1) value: Int) {
		this.buffer!![this.currentPosition++] = (value + 128).toByte()
	}

	@OriginalMember(owner = "client!cea", name = "o", descriptor = "(II)V")
	fun writeFlags(@OriginalArg(0) flags: Int) {
		if ((flags and 0xFFFFFF80.toInt()) != 0) {
			if ((flags and 0xFFFFC000.toInt()) != 0) {
				if ((flags and 0xFFE00000.toInt()) != 0) {
					if ((flags and 0xF0000000.toInt()) != 0) {
						this.writeByte((flags ushr 28) or 0x80)
					}
					this.writeByte((flags ushr 21) or 0x80)
				}
				this.writeByte((flags ushr 14) or 0x80)
			}
			this.writeByte((flags ushr 7) or 0x80)
		}
		this.writeByte(flags and 0x7F)
	}

	@OriginalMember(owner = "client!cea", name = "p", descriptor = "(II)V")
	fun writeShortLE(@OriginalArg(1) value: Int) {
		this.buffer!![this.currentPosition++] = value.toByte()
		this.buffer!![this.currentPosition++] = (value shr 8).toByte()
	}

	companion object {
		@OriginalMember(owner = "client!qh", name = "K", descriptor = "I")
		@JvmField
		var mediumPos: Int = 0

		@OriginalMember(owner = "client!pl", name = "a", descriptor = "(IZ[BI)I")
		@JvmStatic
		fun getCrc32(@OriginalArg(0) currentPosition: Int, @OriginalArg(2) buffer: ByteArray?, @OriginalArg(3) start: Int): Int {
			@Pc(12) var crc: Int = -1
			@Pc(14) var i: Int = start
			while (i < currentPosition) {
				crc = (crc ushr 8) xor GlxProgram.CRC_TABLE[(crc xor buffer!![i].toInt()) and 0xFF]
				i++
			}
			return crc.inv()
		}

		@OriginalMember(owner = "client!rga", name = "a", descriptor = "(II[B)I")
		@JvmStatic
		fun crc32(@OriginalArg(1) currentPosition: Int, @OriginalArg(2) buffer: ByteArray?): Int {
			return Packet.getCrc32(currentPosition, buffer, 0)
		}
	}
}
