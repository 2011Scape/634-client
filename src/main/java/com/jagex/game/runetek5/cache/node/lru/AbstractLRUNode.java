package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!qia")
public abstract class AbstractLRUNode extends CacheableNode {

	@OriginalMember(owner = "client!qia", name = "w", descriptor = "I")
	public final int capacityRequirement;

	@OriginalMember(owner = "client!qia", name = "<init>", descriptor = "(I)V")
	public AbstractLRUNode(@OriginalArg(0) int capacityRequirement) {
		this.capacityRequirement = capacityRequirement;
	}

	@OriginalMember(owner = "client!qia", name = "a", descriptor = "(I)Ljava/lang/Object;")
	public abstract Object get();

	@OriginalMember(owner = "client!qia", name = "d", descriptor = "(I)Z")
	public abstract boolean isSoft();
}
