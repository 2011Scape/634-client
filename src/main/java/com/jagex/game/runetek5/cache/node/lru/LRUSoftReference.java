package com.jagex.game.runetek5.cache.node.lru;

import java.lang.ref.SoftReference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!eia")
public final class LRUSoftReference extends AbstractLRUNode {

	@OriginalMember(owner = "client!eia", name = "D", descriptor = "Ljava/lang/ref/SoftReference;")
	private final SoftReference softReference;

	@OriginalMember(owner = "client!eia", name = "<init>", descriptor = "(Ljava/lang/Object;I)V")
	public LRUSoftReference(@OriginalArg(0) Object object, @OriginalArg(1) int capacity) {
		super(capacity);
		this.softReference = new SoftReference(object);
	}

	@OriginalMember(owner = "client!eia", name = "d", descriptor = "(I)Z")
	@Override
	public boolean isSoft() {
		return true;
	}

	@OriginalMember(owner = "client!eia", name = "a", descriptor = "(I)Ljava/lang/Object;")
	@Override
	public Object get() {
		return this.softReference.get();
	}
}
