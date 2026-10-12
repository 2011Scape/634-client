package com.jagex.game.runetek5

import com.jagex.game.runetek5.sound.Sound
import com.jagex.game.runetek5.sound.SoundType
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object IntKeyedQuicksort {

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(II[II[Ljava/lang/Object;)V")
	@JvmStatic
	fun quicksort(@OriginalArg(0) endIndex: Int, @OriginalArg(1) startIndex: Int, @OriginalArg(2) keys: IntArray?, @OriginalArg(4) objects: Array<Any?>?) {
		if (endIndex <= startIndex) {
			return
		}
		@Pc(22) val midIndex: Int = (endIndex + startIndex) / 2
		@Pc(24) var partitionIndex: Int = startIndex
		@Pc(28) val pivotKey: Int = keys!![midIndex]
		keys[midIndex] = keys[endIndex]
		keys[endIndex] = pivotKey
		@Pc(42) val `object`: Any? = objects!![midIndex]
		objects[midIndex] = objects[endIndex]
		objects[endIndex] = `object`
		@Pc(59) val tieBreakMask: Int = if (pivotKey == Int.MAX_VALUE) 0 else 1
		@Pc(61) var i: Int = startIndex
		while (i < endIndex) {
			if (keys[i] < (tieBreakMask and i) + pivotKey) {
				@Pc(76) val tempKey: Int = keys[i]
				keys[i] = keys[partitionIndex]
				keys[partitionIndex] = tempKey
				@Pc(90) val object2: Any? = objects[i]
				objects[i] = objects[partitionIndex]
				objects[partitionIndex++] = object2
			}
			i++
		}
		keys[endIndex] = keys[partitionIndex]
		keys[partitionIndex] = pivotKey
		objects[endIndex] = objects[partitionIndex]
		objects[partitionIndex] = `object`
		quicksort(partitionIndex - 1, startIndex, keys, objects)
		quicksort(endIndex, partitionIndex + 1, keys, objects)
	}

	@OriginalMember(owner = "client!ifa", name = "a", descriptor = "([Ljava/lang/Object;B[I)V")
	@JvmStatic
	fun sort(@OriginalArg(0) objects: Array<Any?>?, @OriginalArg(2) keys: IntArray?) {
		IntKeyedQuicksort.quicksort(keys!!.size - 1, 0, keys, objects)
	}
}
