package com.jagex.game.runetek5.cache.node

import jaclib.memory.heap.NativeHeap
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!ai")
class GlMemoryPool : MemoryPool {

	@OriginalMember(owner = "client!ai", name = "r", descriptor = "Lclient!jaclib/memory/heap/NativeHeap;")
	@JvmField
	val heap: NativeHeap

	@OriginalMember(owner = "client!ai", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) size: Int) {
		this.heap = NativeHeap(size)
	}

	@OriginalMember(owner = "client!ai", name = "b", descriptor = "(I)V")
	fun deallocate() {
		this.heap.b()
	}
}
