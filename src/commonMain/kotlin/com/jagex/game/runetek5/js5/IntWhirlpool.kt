package com.jagex.game.runetek5.js5

import com.jagex.game.runetek5.DroppedInputStream
import kotlin.jvm.JvmStatic

/*
 * Not part of the original client: Whirlpool.digest with each 64-bit word held as two Ints (high
 * and low halves), for where Long is emulated (emulatedLong, PlatformLoop.kt). Kotlin/JS allocates
 * for every Long operation, and Whirlpool, which checks every downloaded cache group, made downloads
 * hitch. Same tables (DroppedInputStream's), same padding, same digest.
 */
object IntWhirlpool {

	/* CIRCULANT_TABLE[column][value] split into halves, at column * 256 + value. */
	private var tableHi: IntArray? = null
	private var tableLo: IntArray? = null

	private var constantsHi: IntArray? = null
	private var constantsLo: IntArray? = null

	private val hashHi: IntArray = IntArray(8)
	private val hashLo: IntArray = IntArray(8)
	private val blockHi: IntArray = IntArray(8)
	private val blockLo: IntArray = IntArray(8)
	private val keyHi: IntArray = IntArray(8)
	private val keyLo: IntArray = IntArray(8)
	private val stateHi: IntArray = IntArray(8)
	private val stateLo: IntArray = IntArray(8)
	private val tempHi: IntArray = IntArray(8)
	private val tempLo: IntArray = IntArray(8)

	/* The 64-byte digest of size bytes of src from srcPosition (from 0 when it is 0 or less), as Whirlpool.digest. */
	@JvmStatic
	fun digest(srcPosition: Int, src: ByteArray, size: Int): ByteArray {
		if (tableHi == null) {
			val hi: IntArray = IntArray(8 * 256)
			val lo: IntArray = IntArray(8 * 256)
			for (column in 0 until 8) {
				for (value in 0 until 256) {
					val word: Long = DroppedInputStream.CIRCULANT_TABLE[column][value]
					hi[column * 256 + value] = (word ushr 32).toInt()
					lo[column * 256 + value] = word.toInt()
				}
			}
			val roundHi: IntArray = IntArray(11)
			val roundLo: IntArray = IntArray(11)
			for (round in 0 until 11) {
				roundHi[round] = (DroppedInputStream.ROUND_CONSTANTS[round] ushr 32).toInt()
				roundLo[round] = DroppedInputStream.ROUND_CONSTANTS[round].toInt()
			}
			constantsHi = roundHi
			constantsLo = roundLo
			tableLo = lo
			tableHi = hi
		}
		val start: Int = if (srcPosition > 0) srcPosition else 0
		for (i in 0 until 8) {
			hashHi[i] = 0
			hashLo[i] = 0
		}
		// the whole blocks, then the padded tail: 0x80, zeros to byte 32 of a block, the 256-bit bit length
		val whole: Int = size / 64
		for (b in 0 until whole) {
			processBlock(src, start + b * 64)
		}
		val tail: Int = size - whole * 64
		val last: ByteArray = ByteArray(if (tail >= 32) 128 else 64)
		for (i in 0 until tail) {
			last[i] = src[start + whole * 64 + i]
		}
		last[tail] = 0x80.toByte()
		val bits: Long = size.toLong() * 8L
		val end: Int = last.size
		for (i in 0 until 8) {
			last[end - 1 - i] = (bits ushr (i * 8)).toByte()
		}
		processBlock(last, 0)
		if (last.size == 128) {
			processBlock(last, 64)
		}
		val digest: ByteArray = ByteArray(64)
		for (i in 0 until 8) {
			val h: Int = hashHi[i]
			val l: Int = hashLo[i]
			digest[i * 8] = (h ushr 24).toByte()
			digest[i * 8 + 1] = (h ushr 16).toByte()
			digest[i * 8 + 2] = (h ushr 8).toByte()
			digest[i * 8 + 3] = h.toByte()
			digest[i * 8 + 4] = (l ushr 24).toByte()
			digest[i * 8 + 5] = (l ushr 16).toByte()
			digest[i * 8 + 6] = (l ushr 8).toByte()
			digest[i * 8 + 7] = l.toByte()
		}
		return digest
	}

	private fun processBlock(data: ByteArray, offset: Int) {
		val tHi: IntArray = tableHi!!
		val tLo: IntArray = tableLo!!
		for (i in 0 until 8) {
			val p: Int = offset + i * 8
			blockHi[i] = ((data[p].toInt() and 0xFF) shl 24) or ((data[p + 1].toInt() and 0xFF) shl 16) or ((data[p + 2].toInt() and 0xFF) shl 8) or (data[p + 3].toInt() and 0xFF)
			blockLo[i] = ((data[p + 4].toInt() and 0xFF) shl 24) or ((data[p + 5].toInt() and 0xFF) shl 16) or ((data[p + 6].toInt() and 0xFF) shl 8) or (data[p + 7].toInt() and 0xFF)
			keyHi[i] = hashHi[i]
			keyLo[i] = hashLo[i]
			stateHi[i] = blockHi[i] xor keyHi[i]
			stateLo[i] = blockLo[i] xor keyLo[i]
		}
		for (round in 1..10) {
			mix(keyHi, keyLo, tHi, tLo)
			for (i in 0 until 8) {
				keyHi[i] = tempHi[i]
				keyLo[i] = tempLo[i]
			}
			keyHi[0] = keyHi[0] xor constantsHi!![round]
			keyLo[0] = keyLo[0] xor constantsLo!![round]
			mix(stateHi, stateLo, tHi, tLo)
			for (i in 0 until 8) {
				stateHi[i] = tempHi[i] xor keyHi[i]
				stateLo[i] = tempLo[i] xor keyLo[i]
			}
		}
		for (i in 0 until 8) {
			hashHi[i] = hashHi[i] xor blockHi[i] xor stateHi[i]
			hashLo[i] = hashLo[i] xor blockLo[i] xor stateLo[i]
		}
	}

	/* temp[row] = the xor over columns of CIRCULANT_TABLE[column][byte column of words[row - column]]. */
	private fun mix(wordsHi: IntArray, wordsLo: IntArray, tHi: IntArray, tLo: IntArray) {
		for (row in 0 until 8) {
			var hi: Int = 0
			var lo: Int = 0
			for (column in 0 until 8) {
				val source: Int = (row - column) and 0x7
				val value: Int = if (column < 4) (wordsHi[source] ushr (24 - column * 8)) and 0xFF else (wordsLo[source] ushr (56 - column * 8)) and 0xFF
				val index: Int = column * 256 + value
				hi = hi xor tHi[index]
				lo = lo xor tLo[index]
			}
			tempHi[row] = hi
			tempLo[row] = lo
		}
	}
}
