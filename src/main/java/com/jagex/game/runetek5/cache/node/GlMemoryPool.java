package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.MemoryPool;
import jaclib.memory.heap.NativeHeap;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ai")
public final class GlMemoryPool extends MemoryPool {

	@OriginalMember(owner = "client!ai", name = "r", descriptor = "Lclient!jaclib/memory/heap/NativeHeap;")
	public final NativeHeap heap;

	@OriginalMember(owner = "client!ai", name = "<init>", descriptor = "(I)V")
	public GlMemoryPool(@OriginalArg(0) int size) {
		this.heap = new NativeHeap(size);
	}

	@OriginalMember(owner = "client!ai", name = "b", descriptor = "(I)V")
	public void deallocate() {
		this.heap.b();
	}
}
