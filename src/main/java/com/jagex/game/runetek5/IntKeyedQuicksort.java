package com.jagex.game.runetek5;

import com.jagex.game.runetek5.sound.Sound;
import com.jagex.game.runetek5.sound.SoundType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class IntKeyedQuicksort {

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(II[II[Ljava/lang/Object;)V")
	public static void quicksort(@OriginalArg(0) int endIndex, @OriginalArg(1) int startIndex, @OriginalArg(2) int[] keys, @OriginalArg(4) Object[] objects) {
		if (endIndex <= startIndex) {
			return;
		}
		@Pc(22) int midIndex = (endIndex + startIndex) / 2;
		@Pc(24) int partitionIndex = startIndex;
		@Pc(28) int pivotKey = keys[midIndex];
		keys[midIndex] = keys[endIndex];
		keys[endIndex] = pivotKey;
		@Pc(42) Object object = objects[midIndex];
		objects[midIndex] = objects[endIndex];
		objects[endIndex] = object;
		@Pc(59) int tieBreakMask = pivotKey == Integer.MAX_VALUE ? 0 : 1;
		for (@Pc(61) int i = startIndex; i < endIndex; i++) {
			if (keys[i] < (tieBreakMask & i) + pivotKey) {
				@Pc(76) int tempKey = keys[i];
				keys[i] = keys[partitionIndex];
				keys[partitionIndex] = tempKey;
				@Pc(90) Object object2 = objects[i];
				objects[i] = objects[partitionIndex];
				objects[partitionIndex++] = object2;
			}
		}
		keys[endIndex] = keys[partitionIndex];
		keys[partitionIndex] = pivotKey;
		objects[endIndex] = objects[partitionIndex];
		objects[partitionIndex] = object;
		quicksort(partitionIndex - 1, startIndex, keys, objects);
		quicksort(endIndex, partitionIndex + 1, keys, objects);
	}
	@OriginalMember(owner = "client!ifa", name = "a", descriptor = "([Ljava/lang/Object;B[I)V")
	public static void sort(@OriginalArg(0) Object[] objects, @OriginalArg(2) int[] keys) {
		IntKeyedQuicksort.quicksort(keys.length - 1, 0, keys, objects);
	}
}
