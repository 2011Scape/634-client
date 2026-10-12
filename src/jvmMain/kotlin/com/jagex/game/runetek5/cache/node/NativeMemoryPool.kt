package com.jagex.game.runetek5.cache.node

import jaclib.memory.heap.NativeHeap
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!cu")
class NativeMemoryPool : MemoryPool {

	@OriginalMember(owner = "client!cu", name = "w", descriptor = "Lclient!jaclib/memory/heap/NativeHeap;")
	@JvmField
	val heap: NativeHeap

	@OriginalMember(owner = "client!cu", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) size: Int) {
		this.heap = NativeHeap(size)
	}

	@OriginalMember(owner = "client!cu", name = "b", descriptor = "(I)V")
	fun deallocate() {
		this.heap.b()
	}
}
