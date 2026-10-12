package com.jagex.game.runetek5.core.compress

import com.jagex.game.runetek5.BZip2InputStream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ig")
class BzipDecompressor {

	@OriginalMember(owner = "client!ig", name = "c", descriptor = "I")
	@JvmField
	var strmTotalOutLo32: Int = 0

	@OriginalMember(owner = "client!ig", name = "e", descriptor = "I")
	@JvmField
	var blockSize100k: Int = 0

	@OriginalMember(owner = "client!ig", name = "f", descriptor = "[B")
	@JvmField
	var output: ByteArray? = null

	@OriginalMember(owner = "client!ig", name = "i", descriptor = "I")
	@JvmField
	var origPtr: Int = 0

	@OriginalMember(owner = "client!ig", name = "l", descriptor = "I")
	@JvmField
	var stateOutLen: Int = 0

	@OriginalMember(owner = "client!ig", name = "m", descriptor = "[B")
	@JvmField
	var input: ByteArray? = null

	@OriginalMember(owner = "client!ig", name = "o", descriptor = "I")
	@JvmField
	var strmTotalInLo32: Int = 0

	@OriginalMember(owner = "client!ig", name = "p", descriptor = "I")
	@JvmField
	var k0: Int = 0

	@OriginalMember(owner = "client!ig", name = "q", descriptor = "I")
	@JvmField
	var availableOut: Int = 0

	@OriginalMember(owner = "client!ig", name = "r", descriptor = "B")
	@JvmField
	var stateOutCh: Byte = 0

	@OriginalMember(owner = "client!ig", name = "t", descriptor = "I")
	@JvmField
	var nInUse: Int = 0

	@OriginalMember(owner = "client!ig", name = "y", descriptor = "I")
	@JvmField
	var bsLive: Int = 0

	@OriginalMember(owner = "client!ig", name = "D", descriptor = "I")
	@JvmField
	var saveNblock: Int = 0

	@OriginalMember(owner = "client!ig", name = "F", descriptor = "I")
	@JvmField
	var tPos: Int = 0

	@OriginalMember(owner = "client!ig", name = "H", descriptor = "I")
	@JvmField
	var bsBuff: Int = 0

	@OriginalMember(owner = "client!ig", name = "I", descriptor = "I")
	@JvmField
	var nblockused: Int = 0

	@OriginalMember(owner = "client!ig", name = "d", descriptor = "[I")
	@JvmField
	val minLens: IntArray = IntArray(6)

	@OriginalMember(owner = "client!ig", name = "j", descriptor = "[B")
	@JvmField
	val selector: ByteArray = ByteArray(18002)

	@OriginalMember(owner = "client!ig", name = "a", descriptor = "I")
	@JvmField
	var strmNextInPtr: Int = 0

	@OriginalMember(owner = "client!ig", name = "h", descriptor = "[[I")
	@JvmField
	val perm: Array<IntArray> = Array(6) { IntArray(258) }

	@OriginalMember(owner = "client!ig", name = "n", descriptor = "[I")
	@JvmField
	val unzftab: IntArray = IntArray(256)

	@OriginalMember(owner = "client!ig", name = "w", descriptor = "[B")
	@JvmField
	val selectorMtf: ByteArray = ByteArray(18002)

	@OriginalMember(owner = "client!ig", name = "b", descriptor = "[Z")
	@JvmField
	val inUse: BooleanArray = BooleanArray(256)

	@OriginalMember(owner = "client!ig", name = "g", descriptor = "[B")
	@JvmField
	val seqToUnseq: ByteArray = ByteArray(256)

	@OriginalMember(owner = "client!ig", name = "B", descriptor = "I")
	@JvmField
	var strmNextOutPtr: Int = 0

	@OriginalMember(owner = "client!ig", name = "C", descriptor = "[[B")
	@JvmField
	val len: Array<ByteArray> = Array(6) { ByteArray(258) }

	@OriginalMember(owner = "client!ig", name = "x", descriptor = "[B")
	@JvmField
	val mtfa: ByteArray = ByteArray(4096)

	@OriginalMember(owner = "client!ig", name = "s", descriptor = "[I")
	@JvmField
	val mtfbase: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ig", name = "k", descriptor = "[Z")
	@JvmField
	val inUse16: BooleanArray = BooleanArray(16)

	@OriginalMember(owner = "client!ig", name = "G", descriptor = "[[I")
	@JvmField
	val base: Array<IntArray> = Array(6) { IntArray(258) }

	@OriginalMember(owner = "client!ig", name = "J", descriptor = "[[I")
	@JvmField
	val limit: Array<IntArray> = Array(6) { IntArray(258) }

	@OriginalMember(owner = "client!ig", name = "K", descriptor = "[I")
	@JvmField
	val cftab: IntArray = IntArray(257)

	companion object {

		@OriginalMember(owner = "client!bs", name = "a", descriptor = "([BI[BII)I")
		@JvmStatic
		fun bunzip(@OriginalArg(0) output: ByteArray?, @OriginalArg(1) outputLength: Int, @OriginalArg(2) input: ByteArray?, @OriginalArg(3) inLength: Int): Int {
			@Pc(1) val bzipDecompressor: BzipDecompressor? = BZip2InputStream.INSTANCE
			synchronized(BZip2InputStream.INSTANCE) {
				BZip2InputStream.INSTANCE.input = input
				BZip2InputStream.INSTANCE.strmNextInPtr = 9
				BZip2InputStream.INSTANCE.output = output
				BZip2InputStream.INSTANCE.strmNextOutPtr = 0
				BZip2InputStream.INSTANCE.availableOut = outputLength
				BZip2InputStream.INSTANCE.bsLive = 0
				BZip2InputStream.INSTANCE.bsBuff = 0
				BZip2InputStream.INSTANCE.strmTotalInLo32 = 0
				BZip2InputStream.INSTANCE.strmTotalOutLo32 = 0
				BZip2InputStream.decompress(BZip2InputStream.INSTANCE)
				@Pc(37) val size: Int = outputLength - BZip2InputStream.INSTANCE.availableOut
				BZip2InputStream.INSTANCE.input = null
				BZip2InputStream.INSTANCE.output = null
				return size
			}
		}

		@OriginalMember(owner = "client!bs", name = "a", descriptor = "([I[I[I[BIII)V")
		@JvmStatic
		fun buildHuffmanTable(@OriginalArg(0) limitTable: IntArray?, @OriginalArg(1) baseTable: IntArray?, @OriginalArg(2) permTable: IntArray?, @OriginalArg(3) lengthTable: ByteArray?, @OriginalArg(4) minLength: Int, @OriginalArg(5) maxLength: Int, @OriginalArg(6) symbolCount: Int) {
			@Pc(1) var permIndex: Int = 0
			@Pc(3) val local3: Int
			var codeLength: Int = minLength
			while (codeLength <= maxLength) {
				@Pc(6) var i: Int = 0
				while (i < symbolCount) {
					if (lengthTable!![i].toInt() == codeLength) {
						permTable!![permIndex] = i
						permIndex++
					}
					i++
				}
				codeLength++
			}
			codeLength = 0
			while (codeLength < 23) {
				baseTable!![codeLength] = 0
				codeLength++
			}
			var symbol: Int = 0
			while (symbol < symbolCount) {
				baseTable!![lengthTable!![symbol].toInt() + 1]++
				symbol++
			}
			codeLength = 1
			while (codeLength < 23) {
				baseTable!![codeLength] += baseTable[codeLength - 1]
				codeLength++
			}
			codeLength = 0
			while (codeLength < 23) {
				limitTable!![codeLength] = 0
				codeLength++
			}
			@Pc(85) var code: Int = 0
			codeLength = minLength
			while (codeLength <= maxLength) {
				code += baseTable!![codeLength + 1] - baseTable[codeLength]
				limitTable!![codeLength] = code - 1
				code = code shl 0x1
				codeLength++
			}
			codeLength = minLength + 1
			while (codeLength <= maxLength) {
				baseTable!![codeLength] = ((limitTable!![codeLength - 1] + 1) shl 1) - baseTable[codeLength]
				codeLength++
			}
		}
	}
}
