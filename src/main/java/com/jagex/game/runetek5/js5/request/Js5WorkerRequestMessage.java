package com.jagex.game.runetek5.js5.request;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ffa")
public abstract class Js5WorkerRequestMessage extends CacheableNode {

	@OriginalMember(owner = "client!ffa", name = "y", descriptor = "Z")
	public boolean highPriority;

	@OriginalMember(owner = "client!ffa", name = "E", descriptor = "Z")
	public boolean isCompleted;

	@OriginalMember(owner = "client!ffa", name = "z", descriptor = "Z")
	public volatile boolean waiting = true;

	@OriginalMember(owner = "client!ffa", name = "e", descriptor = "(I)I")
	public abstract int completePercentage();

	@OriginalMember(owner = "client!ffa", name = "f", descriptor = "(I)[B")
	public abstract byte[] getData();
}
