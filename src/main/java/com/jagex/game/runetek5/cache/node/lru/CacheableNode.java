package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;

@OriginalClass("client!vl")
public class CacheableNode extends Node {

	@OriginalMember(owner = "client!vl", name = "o", descriptor = "J")
	public long indexReference;

	@OriginalMember(owner = "client!vl", name = "r", descriptor = "Lclient!vl;")
	public CacheableNode secondaryPrev;

	@OriginalMember(owner = "client!vl", name = "u", descriptor = "Lclient!vl;")
	public CacheableNode secondaryNext;

	@OriginalMember(owner = "client!vl", name = "a", descriptor = "(Z)V")
	public final void unlinkSecondary() {
		if (this.secondaryPrev != null) {
			this.secondaryPrev.secondaryNext = this.secondaryNext;
			this.secondaryNext.secondaryPrev = this.secondaryPrev;
			this.secondaryPrev = null;
			this.secondaryNext = null;
		}
	}

	@OriginalMember(owner = "client!vl", name = "b", descriptor = "(B)Z")
	public final boolean islinkedSecondary() {
		return this.secondaryPrev != null;
	}
	@OriginalMember(owner = "client!wd", name = "a", descriptor = "(Lclient!vl;Lclient!vl;B)V")
	public static void insertSecondaryAfter(@OriginalArg(0) CacheableNode node2, @OriginalArg(1) CacheableNode node22) {
		if (node2.secondaryPrev != null) {
			node2.unlinkSecondary();
		}
		node2.secondaryPrev = node22;
		node2.secondaryNext = node22.secondaryNext;
		node2.secondaryPrev.secondaryNext = node2;
		node2.secondaryNext.secondaryPrev = node2;
	}
	@OriginalMember(owner = "client!kea", name = "a", descriptor = "(Lclient!vl;Lclient!vl;I)V")
	public static void insertSecondaryBefore(@OriginalArg(0) CacheableNode node2, @OriginalArg(1) CacheableNode node22) {
		if (node22.secondaryPrev != null) {
			node22.unlinkSecondary();
		}
		node22.secondaryNext = node2;
		node22.secondaryPrev = node2.secondaryPrev;
		node22.secondaryPrev.secondaryNext = node22;
		node22.secondaryNext.secondaryPrev = node22;
	}
}
