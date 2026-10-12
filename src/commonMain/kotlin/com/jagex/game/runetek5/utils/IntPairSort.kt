package com.jagex.game.runetek5.utils

import kotlin.jvm.JvmStatic

/*
 * Not part of the original client: ArrayUtils.quicksort(IntArray, LongArray) over keys held as Int
 * pairs, for where Long is emulated (emulatedLong, PlatformLoop.kt): Kotlin/JS allocates for every
 * Long operation, and JavaModel's face sort made a region build hitch. A key is hi * 2^32 + lo, hi
 * signed and lo unsigned, as the Long (hi.toLong() shl 32) or (lo.toLong() and 0xFFFFFFFF). The
 * partitioning, the pivot choice, the tie-break and the Long.MAX_VALUE rule are the original's, so
 * the order comes out the same, ties included.
 */
object IntPairSort {

	/* Sorts values by the keys (keysHi, keysLo), both moved along with them. */
	@JvmStatic
	fun quicksort(values: IntArray, keysHi: IntArray, keysLo: IntArray) {
		quicksort(0, keysHi.size - 1, keysHi, keysLo, values)
	}

	/* Whether key a is less than key b. */
	private fun less(aHi: Int, aLo: Int, bHi: Int, bLo: Int): Boolean {
		return aHi < bHi || aHi == bHi && (aLo xor Int.MIN_VALUE) < (bLo xor Int.MIN_VALUE)
	}

	private fun quicksort(start: Int, end: Int, keysHi: IntArray, keysLo: IntArray, values: IntArray) {
		if (start >= end) {
			return
		}
		val mid: Int = (end + start) / 2
		var storeIndex: Int = start
		val pivotHi: Int = keysHi[mid]
		val pivotLo: Int = keysLo[mid]
		keysHi[mid] = keysHi[end]
		keysLo[mid] = keysLo[end]
		keysHi[end] = pivotHi
		keysLo[end] = pivotLo
		val pivotValue: Int = values[mid]
		values[mid] = values[end]
		values[end] = pivotValue
		val tieBreak: Int = if (pivotHi == Int.MAX_VALUE && pivotLo == -1) 0 else 1
		for (i in start until end) {
			// keys[i] < pivot + (tieBreak and i), the sum carried into hi as the Long sum is
			val add: Int = tieBreak and i
			val boundLo: Int = pivotLo + add
			val boundHi: Int = if ((boundLo xor Int.MIN_VALUE) < (pivotLo xor Int.MIN_VALUE)) pivotHi + 1 else pivotHi
			if (less(keysHi[i], keysLo[i], boundHi, boundLo)) {
				val tempHi: Int = keysHi[i]
				val tempLo: Int = keysLo[i]
				keysHi[i] = keysHi[storeIndex]
				keysLo[i] = keysLo[storeIndex]
				keysHi[storeIndex] = tempHi
				keysLo[storeIndex] = tempLo
				val tempValue: Int = values[i]
				values[i] = values[storeIndex]
				values[storeIndex++] = tempValue
			}
		}
		keysHi[end] = keysHi[storeIndex]
		keysLo[end] = keysLo[storeIndex]
		keysHi[storeIndex] = pivotHi
		keysLo[storeIndex] = pivotLo
		values[end] = values[storeIndex]
		values[storeIndex] = pivotValue
		quicksort(start, storeIndex - 1, keysHi, keysLo, values)
		quicksort(storeIndex + 1, end, keysHi, keysLo, values)
	}
}
