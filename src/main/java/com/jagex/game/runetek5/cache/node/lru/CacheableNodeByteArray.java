package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!lk")
public final class CacheableNodeByteArray extends CacheableNode {

	@OriginalMember(owner = "client!lk", name = "x", descriptor = "[B")
	public final byte[] value;

	@OriginalMember(owner = "client!lk", name = "<init>", descriptor = "([B)V")
	public CacheableNodeByteArray(@OriginalArg(0) byte[] data) {
		this.value = data;
	}
}
