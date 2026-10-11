package com.jagex.game.runetek5.utils;

import com.jagex.game.runetek5.cache.node.CacheableNodeQueue;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.core.util.Arrays;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.lru.CacheableNodeByteArray;
import java.util.Random;

public final class ArrayUtils {
	@OriginalMember(owner = "client!dga", name = "M", descriptor = "Lclient!wu;")
	public static final CacheableNodeQueue BYTEARRAY_NODES = new CacheableNodeQueue(16);

	private ArrayUtils() {
		/* empty */
	}
	@OriginalMember(owner = "client!dia", name = "a", descriptor = "(B[SI)[S")
	public static short[] copyOf(@OriginalArg(1) short[] src, @OriginalArg(2) int length) {
		@Pc(14) short[] copy = new short[length];
		Arrays.copy(src, 0, copy, 0, length);
		return copy;
	}

	@OriginalMember(owner = "client!ffa", name = "a", descriptor = "(II[F)[F")
	public static float[] copyOf(@OriginalArg(1) int length, @OriginalArg(2) float[] src) {
		@Pc(6) float[] copy = new float[length];
		Arrays.copy(src, 0, copy, 0, length);
		return copy;
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "([I[JI)V")
	public static void quicksort(@OriginalArg(0) int[] values, @OriginalArg(1) long[] keys) {
		ArrayUtils.quicksort(0, keys, keys.length - 1, values);
	}

	@OriginalMember(owner = "client!eaa", name = "a", descriptor = "([Ljava/lang/Object;[JI)V")
	public static void quicksort(@OriginalArg(0) Object[] objects, @OriginalArg(1) long[] keys) {
		ArrayUtils.quicksort(0, objects, keys.length - 1, keys);
	}
	@OriginalMember(owner = "client!ha", name = "c", descriptor = "(II)[B")
	public static byte[] getPermutationTable(@OriginalArg(0) int seed) {
		@Pc(17) CacheableNodeByteArray node = (CacheableNodeByteArray) ArrayUtils.BYTEARRAY_NODES.get((long) seed);
		if (node == null) {
			@Pc(22) byte[] permutation = new byte[512];
			@Pc(28) Random random = new Random((long) seed);
			for (@Pc(30) int i = 0; i < 255; i++) {
				permutation[i] = (byte) i;
			}
			for (@Pc(45) int j = 0; j < 255; j++) {
				@Pc(52) int index = 255 - j;
				@Pc(57) int swapIndex = Utils.randomInt(index, random);
				@Pc(61) byte temp = permutation[swapIndex];
				permutation[swapIndex] = permutation[index];
				permutation[index] = permutation[511 - j] = temp;
			}
			node = new CacheableNodeByteArray(permutation);
			ArrayUtils.BYTEARRAY_NODES.put((long) seed, node);
		}
		return node.value;
	}

	@OriginalMember(owner = "client!ic", name = "a", descriptor = "([I[IIII)V")
	public static void quicksort(@OriginalArg(0) int[] keys, @OriginalArg(1) int[] values, @OriginalArg(2) int start, @OriginalArg(4) int end) {
		if (start >= end) {
			return;
		}
		@Pc(20) int mid = (start + end) / 2;
		@Pc(22) int storeIndex = start;
		@Pc(26) int pivot = keys[mid];
		keys[mid] = keys[end];
		keys[end] = pivot;
		@Pc(40) int pivotValue = values[mid];
		values[mid] = values[end];
		values[end] = pivotValue;
		@Pc(59) int tieBreak = ~pivot == Integer.MIN_VALUE ? 0 : 1;
		for (@Pc(61) int i = start; i < end; i++) {
			if (keys[i] < (i & tieBreak) + pivot) {
				@Pc(76) int tempKey = keys[i];
				keys[i] = keys[storeIndex];
				keys[storeIndex] = tempKey;
				@Pc(90) int tempValue = values[i];
				values[i] = values[storeIndex];
				values[storeIndex++] = tempValue;
			}
		}
		keys[end] = keys[storeIndex];
		keys[storeIndex] = pivot;
		values[end] = values[storeIndex];
		values[storeIndex] = pivotValue;
		quicksort(keys, values, start, storeIndex - 1);
		quicksort(keys, values, storeIndex + 1, end);
	}

	@OriginalMember(owner = "client!lda", name = "a", descriptor = "(IIII)I")
	public static int clamp(@OriginalArg(0) int max, @OriginalArg(1) int value, @OriginalArg(2) int min) {
		if (value < min) {
			return min;
		} else if (max >= value) {
			return value;
		} else {
			return max;
		}
	}

	@OriginalMember(owner = "client!gv", name = "a", descriptor = "(II[JI[I)V")
	public static void quicksort(@OriginalArg(0) int start, @OriginalArg(2) long[] keys, @OriginalArg(3) int end, @OriginalArg(4) int[] values) {
		if (start >= end) {
			return;
		}
		@Pc(18) int mid = (end + start) / 2;
		@Pc(20) int storeIndex = start;
		@Pc(24) long pivot = keys[mid];
		keys[mid] = keys[end];
		keys[end] = pivot;
		@Pc(38) int pivotValue = values[mid];
		values[mid] = values[end];
		values[end] = pivotValue;
		@Pc(56) int tieBreak = pivot == Long.MAX_VALUE ? 0 : 1;
		for (@Pc(58) int i = start; i < end; i++) {
			if (keys[i] < pivot + (long) (tieBreak & i)) {
				@Pc(80) long tempKey = keys[i];
				keys[i] = keys[storeIndex];
				keys[storeIndex] = tempKey;
				@Pc(94) int tempValue = values[i];
				values[i] = values[storeIndex];
				values[storeIndex++] = tempValue;
			}
		}
		keys[end] = keys[storeIndex];
		keys[storeIndex] = pivot;
		values[end] = values[storeIndex];
		values[storeIndex] = pivotValue;
		quicksort(start, keys, storeIndex - 1, values);
		quicksort(storeIndex + 1, keys, end, values);
	}
	@OriginalMember(owner = "client!kaa", name = "a", descriptor = "(II[III)V")
	public static void setArrayValuesTo(@OriginalArg(1) int end, @OriginalArg(2) int[] dest, @OriginalArg(3) int start, @OriginalArg(4) int value) {
		start--;
		@Pc(7) int lastIndex = end - 1;
		@Pc(11) int unrolledEnd = lastIndex - 7;
		while (unrolledEnd > start) {
			@Pc(17) int index1 = start + 1;
			dest[index1] = value;
			@Pc(22) int index2 = index1 + 1;
			dest[index2] = value;
			@Pc(27) int index3 = index2 + 1;
			dest[index3] = value;
			@Pc(32) int index4 = index3 + 1;
			dest[index4] = value;
			@Pc(37) int index5 = index4 + 1;
			dest[index5] = value;
			@Pc(42) int index6 = index5 + 1;
			dest[index6] = value;
			@Pc(47) int index7 = index6 + 1;
			dest[index7] = value;
			start = index7 + 1;
			dest[start] = value;
		}
		while (lastIndex > start) {
			start++;
			dest[start] = value;
		}
	}
	/** The same as copyOf(short[], int): a new array of the first length elements of src. */
	@OriginalMember(owner = "client!wv", name = "a", descriptor = "([SII)[S")
	public static short[] method9229(@OriginalArg(0) short[] src, @OriginalArg(1) int length) {
		@Pc(14) short[] copy = new short[length];
		Arrays.copy(src, 0, copy, 0, length);
		return copy;
	}

	@OriginalMember(owner = "client!tk", name = "a", descriptor = "(B[BI)[B")
	public static byte[] copyOf(@OriginalArg(1) byte[] src, @OriginalArg(2) int length) {
		@Pc(11) byte[] copy = new byte[length];
		Arrays.copy(src, 0, copy, 0, length);
		return copy;
	}

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(I[Ljava/lang/String;[SIZ)V")
	public static void quicksort(@OriginalArg(0) int endIndex, @OriginalArg(1) String[] strings, @OriginalArg(2) short[] ids, @OriginalArg(3) int startIndex) {
		if (endIndex <= startIndex) {
			return;
		}
		@Pc(16) int midIndex = (startIndex + endIndex) / 2;
		@Pc(18) int partitionIndex = startIndex;
		@Pc(22) String string = strings[midIndex];
		strings[midIndex] = strings[endIndex];
		strings[endIndex] = string;
		@Pc(36) short pivotId = ids[midIndex];
		ids[midIndex] = ids[endIndex];
		ids[endIndex] = pivotId;
		for (@Pc(48) int i = startIndex; i < endIndex; i++) {
			if (string == null || strings[i] != null && (i & 0x1) > strings[i].compareTo(string)) {
				@Pc(73) String string2 = strings[i];
				strings[i] = strings[partitionIndex];
				strings[partitionIndex] = string2;
				@Pc(87) short tempId = ids[i];
				ids[i] = ids[partitionIndex];
				ids[partitionIndex++] = tempId;
			}
		}
		strings[endIndex] = strings[partitionIndex];
		strings[partitionIndex] = string;
		ids[endIndex] = ids[partitionIndex];
		ids[partitionIndex] = pivotId;
		quicksort(partitionIndex - 1, strings, ids, startIndex);
		quicksort(endIndex, strings, ids, partitionIndex + 1);
	}

	@OriginalMember(owner = "client!en", name = "a", descriptor = "(I[Ljava/lang/Object;I[JI)V")
	public static void quicksort(@OriginalArg(0) int startIndex, @OriginalArg(1) Object[] objects, @OriginalArg(2) int endIndex, @OriginalArg(3) long[] keys) {
		if (startIndex >= endIndex) {
			return;
		}
		@Pc(18) int midIndex = (startIndex + endIndex) / 2;
		@Pc(20) int partitionIndex = startIndex;
		@Pc(24) long pivot = keys[midIndex];
		keys[midIndex] = keys[endIndex];
		keys[endIndex] = pivot;
		@Pc(38) Object object = objects[midIndex];
		objects[midIndex] = objects[endIndex];
		objects[endIndex] = object;
		@Pc(58) int mask = ~pivot == Long.MIN_VALUE ? 0 : 1;
		for (@Pc(60) int i = startIndex; i < endIndex; i++) {
			if ((long) (i & mask) + pivot > keys[i]) {
				@Pc(77) long tempKey = keys[i];
				keys[i] = keys[partitionIndex];
				keys[partitionIndex] = tempKey;
				@Pc(91) Object object2 = objects[i];
				objects[i] = objects[partitionIndex];
				objects[partitionIndex++] = object2;
			}
		}
		keys[endIndex] = keys[partitionIndex];
		keys[partitionIndex] = pivot;
		objects[endIndex] = objects[partitionIndex];
		objects[partitionIndex] = object;
		quicksort(startIndex, objects, partitionIndex - 1, keys);
		quicksort(partitionIndex + 1, objects, endIndex, keys);
	}

	/** The same as copyOf(int, float[]): a new array of the first length elements of src. */
	@OriginalMember(owner = "client!sk", name = "a", descriptor = "(I[FI)[F")
	public static float[] method7862(@OriginalArg(0) int length, @OriginalArg(1) float[] src) {
		@Pc(11) float[] copy = new float[length];
		Arrays.copy(src, 0, copy, 0, length);
		return copy;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([BI)[B")
	public static byte[] copyOf(@OriginalArg(0) byte[] src) {
		@Pc(6) int length = src.length;
		@Pc(9) byte[] copy = new byte[length];
		Arrays.copy(src, 0, copy, 0, length);
		return copy;
	}
}
