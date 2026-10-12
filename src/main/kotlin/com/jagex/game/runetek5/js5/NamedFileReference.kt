package com.jagex.game.runetek5.js5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!cj")
class NamedFileReference {

	@OriginalMember(owner = "client!cj", name = "d", descriptor = "[I")
	private val nameHashes: IntArray

	@OriginalMember(owner = "client!cj", name = "<init>", descriptor = "([I)V")
	constructor(@OriginalArg(0) hashes: IntArray?) {
		@Pc(5) var capacity: Int = 1
		while (capacity <= hashes!!.size + (hashes.size shr 1)) {
			capacity = capacity shl 0x1
		}
		this.nameHashes = IntArray(capacity + capacity)
		@Pc(29) var i: Int = 0
		while (i < capacity + capacity) {
			this.nameHashes[i] = -1
			i++
		}
		@Pc(48) var index: Int = 0
		while (index < hashes.size) {
			@Pc(58) var position: Int = hashes[index] and (capacity - 1)
			while (this.nameHashes[position + position + 1] != -1) {
				position = (capacity - 1) and (position + 1)
			}
			this.nameHashes[position + position] = hashes[index]
			this.nameHashes[position + position + 1] = index++
		}
	}

	@OriginalMember(owner = "client!cj", name = "a", descriptor = "(II)I")
	fun find(@OriginalArg(1) name: Int): Int {
		@Pc(19) val size: Int = (this.nameHashes.size shr 1) - 1
		@Pc(23) var position: Int = size and name
		while (true) {
			@Pc(32) val index: Int = this.nameHashes[position + position + 1]
			if (index == -1) {
				return -1
			}
			if (name == this.nameHashes[position + position]) {
				return index
			}
			position = size and (position + 1)
		}
	}
}
