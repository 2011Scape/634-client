package com.jagex.game.runetek5.utils

import com.jagex.game.runetek5.cache.node.CacheableNodeQueue
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.core.util.Arrays
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.lru.CacheableNodeByteArray
import util.Random
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object ArrayUtils {
	@OriginalMember(owner = "client!dga", name = "M", descriptor = "Lclient!wu;")
	@JvmField
	val BYTEARRAY_NODES: CacheableNodeQueue = CacheableNodeQueue(16)

	@OriginalMember(owner = "client!dia", name = "a", descriptor = "(B[SI)[S")
	@JvmStatic
	fun copyOf(@OriginalArg(1) src: ShortArray?, @OriginalArg(2) length: Int): ShortArray {
		@Pc(14) val copy: ShortArray = ShortArray(length)
		Arrays.copy(src, 0, copy, 0, length)
		return copy
	}

	@OriginalMember(owner = "client!ffa", name = "a", descriptor = "(II[F)[F")
	@JvmStatic
	fun copyOf(@OriginalArg(1) length: Int, @OriginalArg(2) src: FloatArray?): FloatArray {
		@Pc(6) val copy: FloatArray = FloatArray(length)
		Arrays.copy(src, 0, copy, 0, length)
		return copy
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "([I[JI)V")
	@JvmStatic
	fun quicksort(@OriginalArg(0) values: IntArray?, @OriginalArg(1) keys: LongArray?) {
		ArrayUtils.quicksort(0, keys, keys!!.size - 1, values)
	}

	@OriginalMember(owner = "client!eaa", name = "a", descriptor = "([Ljava/lang/Object;[JI)V")
	@JvmStatic
	fun quicksort(@OriginalArg(0) objects: Array<Any?>?, @OriginalArg(1) keys: LongArray?) {
		ArrayUtils.quicksort(0, objects, keys!!.size - 1, keys)
	}

	@OriginalMember(owner = "client!ha", name = "c", descriptor = "(II)[B")
	@JvmStatic
	fun getPermutationTable(@OriginalArg(0) seed: Int): ByteArray? {
		@Pc(17) var node: CacheableNodeByteArray? = ArrayUtils.BYTEARRAY_NODES.get(seed.toLong()) as CacheableNodeByteArray?
		if (node == null) {
			@Pc(22) val permutation: ByteArray = ByteArray(512)
			@Pc(28) val random: Random = Random(seed.toLong())
			for (@Pc(30) i in 0 until 255) {
				permutation[i] = i.toByte()
			}
			for (@Pc(45) j in 0 until 255) {
				@Pc(52) val index: Int = 255 - j
				@Pc(57) val swapIndex: Int = Utils.randomInt(index, random)
				@Pc(61) val temp: Byte = permutation[swapIndex]
				permutation[swapIndex] = permutation[index]
				// Java: permutation[index] = permutation[511 - j] = temp
				permutation[511 - j] = temp
				permutation[index] = temp
			}
			node = CacheableNodeByteArray(permutation)
			ArrayUtils.BYTEARRAY_NODES.put(seed.toLong(), node)
		}
		return node.value
	}

	@OriginalMember(owner = "client!ic", name = "a", descriptor = "([I[IIII)V")
	@JvmStatic
	fun quicksort(@OriginalArg(0) keys: IntArray?, @OriginalArg(1) values: IntArray?, @OriginalArg(2) start: Int, @OriginalArg(4) end: Int) {
		if (start >= end) {
			return
		}
		@Pc(20) val mid: Int = (start + end) / 2
		@Pc(22) var storeIndex: Int = start
		@Pc(26) val pivot: Int = keys!![mid]
		keys[mid] = keys[end]
		keys[end] = pivot
		@Pc(40) val pivotValue: Int = values!![mid]
		values[mid] = values[end]
		values[end] = pivotValue
		@Pc(59) val tieBreak: Int = if (pivot.inv() == Int.MIN_VALUE) 0 else 1
		for (@Pc(61) i in start until end) {
			if (keys[i] < (i and tieBreak) + pivot) {
				@Pc(76) val tempKey: Int = keys[i]
				keys[i] = keys[storeIndex]
				keys[storeIndex] = tempKey
				@Pc(90) val tempValue: Int = values[i]
				values[i] = values[storeIndex]
				values[storeIndex++] = tempValue
			}
		}
		keys[end] = keys[storeIndex]
		keys[storeIndex] = pivot
		values[end] = values[storeIndex]
		values[storeIndex] = pivotValue
		quicksort(keys, values, start, storeIndex - 1)
		quicksort(keys, values, storeIndex + 1, end)
	}

	@OriginalMember(owner = "client!lda", name = "a", descriptor = "(IIII)I")
	@JvmStatic
	fun clamp(@OriginalArg(0) max: Int, @OriginalArg(1) value: Int, @OriginalArg(2) min: Int): Int {
		if (value < min) {
			return min
		} else if (max >= value) {
			return value
		} else {
			return max
		}
	}

	@OriginalMember(owner = "client!gv", name = "a", descriptor = "(II[JI[I)V")
	@JvmStatic
	fun quicksort(@OriginalArg(0) start: Int, @OriginalArg(2) keys: LongArray?, @OriginalArg(3) end: Int, @OriginalArg(4) values: IntArray?) {
		if (start >= end) {
			return
		}
		@Pc(18) val mid: Int = (end + start) / 2
		@Pc(20) var storeIndex: Int = start
		@Pc(24) val pivot: Long = keys!![mid]
		keys[mid] = keys[end]
		keys[end] = pivot
		@Pc(38) val pivotValue: Int = values!![mid]
		values[mid] = values[end]
		values[end] = pivotValue
		@Pc(56) val tieBreak: Int = if (pivot == Long.MAX_VALUE) 0 else 1
		for (@Pc(58) i in start until end) {
			if (keys[i] < pivot + (tieBreak and i).toLong()) {
				@Pc(80) val tempKey: Long = keys[i]
				keys[i] = keys[storeIndex]
				keys[storeIndex] = tempKey
				@Pc(94) val tempValue: Int = values[i]
				values[i] = values[storeIndex]
				values[storeIndex++] = tempValue
			}
		}
		keys[end] = keys[storeIndex]
		keys[storeIndex] = pivot
		values[end] = values[storeIndex]
		values[storeIndex] = pivotValue
		quicksort(start, keys, storeIndex - 1, values)
		quicksort(storeIndex + 1, keys, end, values)
	}

	@OriginalMember(owner = "client!kaa", name = "a", descriptor = "(II[III)V")
	@JvmStatic
	fun setArrayValuesTo(@OriginalArg(1) end: Int, @OriginalArg(2) dest: IntArray?, @OriginalArg(3) start: Int, @OriginalArg(4) value: Int) {
		var start: Int = start
		start--
		@Pc(7) val lastIndex: Int = end - 1
		@Pc(11) val unrolledEnd: Int = lastIndex - 7
		while (unrolledEnd > start) {
			@Pc(17) val index1: Int = start + 1
			dest!![index1] = value
			@Pc(22) val index2: Int = index1 + 1
			dest[index2] = value
			@Pc(27) val index3: Int = index2 + 1
			dest[index3] = value
			@Pc(32) val index4: Int = index3 + 1
			dest[index4] = value
			@Pc(37) val index5: Int = index4 + 1
			dest[index5] = value
			@Pc(42) val index6: Int = index5 + 1
			dest[index6] = value
			@Pc(47) val index7: Int = index6 + 1
			dest[index7] = value
			start = index7 + 1
			dest[start] = value
		}
		while (lastIndex > start) {
			start++
			dest!![start] = value
		}
	}

	/** The same as copyOf(short[], int): a new array of the first length elements of src. */
	@OriginalMember(owner = "client!wv", name = "a", descriptor = "([SII)[S")
	@JvmStatic
	fun method9229(@OriginalArg(0) src: ShortArray?, @OriginalArg(1) length: Int): ShortArray {
		@Pc(14) val copy: ShortArray = ShortArray(length)
		Arrays.copy(src, 0, copy, 0, length)
		return copy
	}

	@OriginalMember(owner = "client!tk", name = "a", descriptor = "(B[BI)[B")
	@JvmStatic
	fun copyOf(@OriginalArg(1) src: ByteArray?, @OriginalArg(2) length: Int): ByteArray {
		@Pc(11) val copy: ByteArray = ByteArray(length)
		Arrays.copy(src, 0, copy, 0, length)
		return copy
	}

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(I[Ljava/lang/String;[SIZ)V")
	@JvmStatic
	fun quicksort(@OriginalArg(0) endIndex: Int, @OriginalArg(1) strings: Array<String?>?, @OriginalArg(2) ids: ShortArray?, @OriginalArg(3) startIndex: Int) {
		if (endIndex <= startIndex) {
			return
		}
		@Pc(16) val midIndex: Int = (startIndex + endIndex) / 2
		@Pc(18) var partitionIndex: Int = startIndex
		@Pc(22) val string: String? = strings!![midIndex]
		strings[midIndex] = strings[endIndex]
		strings[endIndex] = string
		@Pc(36) val pivotId: Short = ids!![midIndex]
		ids[midIndex] = ids[endIndex]
		ids[endIndex] = pivotId
		for (@Pc(48) i in startIndex until endIndex) {
			if (string == null || strings[i] != null && (i and 0x1) > strings[i]!!.compareTo(string)) {
				@Pc(73) val string2: String? = strings[i]
				strings[i] = strings[partitionIndex]
				strings[partitionIndex] = string2
				@Pc(87) val tempId: Short = ids[i]
				ids[i] = ids[partitionIndex]
				ids[partitionIndex++] = tempId
			}
		}
		strings[endIndex] = strings[partitionIndex]
		strings[partitionIndex] = string
		ids[endIndex] = ids[partitionIndex]
		ids[partitionIndex] = pivotId
		quicksort(partitionIndex - 1, strings, ids, startIndex)
		quicksort(endIndex, strings, ids, partitionIndex + 1)
	}

	@OriginalMember(owner = "client!en", name = "a", descriptor = "(I[Ljava/lang/Object;I[JI)V")
	@JvmStatic
	fun quicksort(@OriginalArg(0) startIndex: Int, @OriginalArg(1) objects: Array<Any?>?, @OriginalArg(2) endIndex: Int, @OriginalArg(3) keys: LongArray?) {
		if (startIndex >= endIndex) {
			return
		}
		@Pc(18) val midIndex: Int = (startIndex + endIndex) / 2
		@Pc(20) var partitionIndex: Int = startIndex
		@Pc(24) val pivot: Long = keys!![midIndex]
		keys[midIndex] = keys[endIndex]
		keys[endIndex] = pivot
		@Pc(38) val obj: Any? = objects!![midIndex]
		objects[midIndex] = objects[endIndex]
		objects[endIndex] = obj
		@Pc(58) val mask: Int = if (pivot.inv() == Long.MIN_VALUE) 0 else 1
		for (@Pc(60) i in startIndex until endIndex) {
			if ((i and mask).toLong() + pivot > keys[i]) {
				@Pc(77) val tempKey: Long = keys[i]
				keys[i] = keys[partitionIndex]
				keys[partitionIndex] = tempKey
				@Pc(91) val object2: Any? = objects[i]
				objects[i] = objects[partitionIndex]
				objects[partitionIndex++] = object2
			}
		}
		keys[endIndex] = keys[partitionIndex]
		keys[partitionIndex] = pivot
		objects[endIndex] = objects[partitionIndex]
		objects[partitionIndex] = obj
		quicksort(startIndex, objects, partitionIndex - 1, keys)
		quicksort(partitionIndex + 1, objects, endIndex, keys)
	}

	/** The same as copyOf(int, float[]): a new array of the first length elements of src. */
	@OriginalMember(owner = "client!sk", name = "a", descriptor = "(I[FI)[F")
	@JvmStatic
	fun method7862(@OriginalArg(0) length: Int, @OriginalArg(1) src: FloatArray?): FloatArray {
		@Pc(11) val copy: FloatArray = FloatArray(length)
		Arrays.copy(src, 0, copy, 0, length)
		return copy
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([BI)[B")
	@JvmStatic
	fun copyOf(@OriginalArg(0) src: ByteArray?): ByteArray {
		@Pc(6) val length: Int = src!!.size
		@Pc(9) val copy: ByteArray = ByteArray(length)
		Arrays.copy(src, 0, copy, 0, length)
		return copy
	}
}
