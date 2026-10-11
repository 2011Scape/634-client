package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.MemoryPool;
import jaclib.memory.heap.NativeHeap;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!cu")
public final class NativeMemoryPool extends MemoryPool {

	@OriginalMember(owner = "client!cu", name = "w", descriptor = "Lclient!jaclib/memory/heap/NativeHeap;")
	public final NativeHeap heap;

	@OriginalMember(owner = "client!cu", name = "<init>", descriptor = "(I)V")
	public NativeMemoryPool(@OriginalArg(0) int size) {
		this.heap = new NativeHeap(size);
	}

	@OriginalMember(owner = "client!cu", name = "b", descriptor = "(I)V")
	public void deallocate() {
		this.heap.b();
	}
}
