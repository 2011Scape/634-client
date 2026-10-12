package com.jagex.game.runetek5.core.datastruct

import com.jagex.game.runetek5.core.util.Arrays
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!bo")
class StringList {

	@OriginalMember(owner = "client!bo", name = "f", descriptor = "Z")
	private var exponential: Boolean = false

	@OriginalMember(owner = "client!bo", name = "j", descriptor = "I")
	private var last: Int = -1

	@OriginalMember(owner = "client!bo", name = "m", descriptor = "[Ljava/lang/String;")
	private var elements: Array<String?> = arrayOfNulls<String>(0)

	@OriginalMember(owner = "client!bo", name = "k", descriptor = "I")
	private val growthFactor: Int

	@OriginalMember(owner = "client!bo", name = "<init>", descriptor = "(IZ)V")
	constructor(@OriginalArg(0) growthFactor: Int, @OriginalArg(1) exponential: Boolean) {
		this.growthFactor = growthFactor
		this.exponential = exponential
	}

	@OriginalMember(owner = "client!bo", name = "a", descriptor = "(II)I")
	private fun nextSize(@OriginalArg(0) count: Int): Int {
		@Pc(17) var currentCount: Int = this.elements.size
		while (currentCount <= count) {
			if (!this.exponential) {
				currentCount += this.growthFactor
			} else if (currentCount == 0) {
				currentCount = 1
			} else {
				currentCount *= this.growthFactor
			}
		}
		return currentCount
	}

	@OriginalMember(owner = "client!bo", name = "a", descriptor = "(I)[Ljava/lang/String;")
	fun toArray(): Array<String?> {
		@Pc(17) val array: Array<String?> = arrayOfNulls<String>(this.last + 1)
		@Suppress("UNCHECKED_CAST")
		Arrays.copy(this.elements as Array<Any?>, 0, array as Array<Any?>, 0, this.last + 1)
		return array
	}

	@OriginalMember(owner = "client!bo", name = "a", descriptor = "(Ljava/lang/String;II)V")
	private fun set(@OriginalArg(0) string: String?, @OriginalArg(2) last: Int) {
		if (this.last < last) {
			this.last = last
		}
		if (last >= this.elements.size) {
			this.expand(last)
		}
		this.elements[last] = string
	}

	@OriginalMember(owner = "client!bo", name = "b", descriptor = "(II)V")
	private fun expand(@OriginalArg(0) count: Int) {
		@Pc(17) val strings: Array<String?> = arrayOfNulls<String>(this.nextSize(count))
		@Suppress("UNCHECKED_CAST")
		Arrays.copy(this.elements as Array<Any?>, 0, strings as Array<Any?>, 0, this.elements.size)
		this.elements = strings
	}

	@OriginalMember(owner = "client!bo", name = "a", descriptor = "(ILjava/lang/String;)V")
	fun add(@OriginalArg(1) string: String?) {
		this.set(string, this.last + 1)
	}

	@OriginalMember(owner = "client!bo", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		@Pc(9) val stringBuffer: StringBuffer = StringBuffer()
		stringBuffer.append("[")
		@Pc(15) var i: Int = 0
		while (i < this.last) {
			if (i != 0) {
				stringBuffer.append(", ")
			}
			stringBuffer.append(this.elements[i])
			i++
		}
		stringBuffer.append("]")
		return stringBuffer.toString()
	}
}
