package com.jagex.game.runetek5.cache.node.lru;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!pf")
public final class HardReferenceNode extends AbstractLRUNode {

	@OriginalMember(owner = "client!pf", name = "D", descriptor = "Ljava/lang/Object;")
	private final Object object;

	@OriginalMember(owner = "client!pf", name = "<init>", descriptor = "(Ljava/lang/Object;I)V")
	public HardReferenceNode(@OriginalArg(0) Object object2, @OriginalArg(1) int capacityRequirement) {
		super(capacityRequirement);
		this.object = object2;
	}

	@OriginalMember(owner = "client!pf", name = "d", descriptor = "(I)Z")
	@Override
	public boolean isSoft() {
		return false;
	}

	@OriginalMember(owner = "client!pf", name = "a", descriptor = "(I)Ljava/lang/Object;")
	@Override
	public Object get() {
		return this.object;
	}
}
