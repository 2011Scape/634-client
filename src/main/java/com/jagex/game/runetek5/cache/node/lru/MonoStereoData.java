package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!lm")
public final class MonoStereoData extends CacheableNode {

	@OriginalMember(owner = "client!lm", name = "y", descriptor = "D")
	public double timestamp;

	@OriginalMember(owner = "client!lm", name = "E", descriptor = "[[S")
	public final short[][] samples;

	@OriginalMember(owner = "client!lm", name = "<init>", descriptor = "([[SD)V")
	public MonoStereoData(@OriginalArg(0) short[][] samples, @OriginalArg(1) double timestamp) {
		this.timestamp = timestamp;
		this.samples = samples;
	}

	@OriginalMember(owner = "client!lm", name = "a", descriptor = "(I)J")
	public long getCacheKey() {
		return (long) (this.samples.length << 0 | this.samples[0].length);
	}
}
