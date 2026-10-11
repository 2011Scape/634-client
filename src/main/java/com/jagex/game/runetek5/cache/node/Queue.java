package com.jagex.game.runetek5.cache.node;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.lru.CacheableNode;

@OriginalClass("client!mn")
public final class Queue {

	@OriginalMember(owner = "client!mn", name = "k", descriptor = "Lclient!vl;")
	private CacheableNode pointer;

	@OriginalMember(owner = "client!mn", name = "f", descriptor = "Lclient!vl;")
	public final CacheableNode sentinel = new CacheableNode();

	@OriginalMember(owner = "client!mn", name = "<init>", descriptor = "()V")
	public Queue() {
		this.sentinel.secondaryPrev = this.sentinel;
		this.sentinel.secondaryNext = this.sentinel;
	}

	@OriginalMember(owner = "client!mn", name = "a", descriptor = "(I)I")
	public int size() {
		@Pc(7) int size = 0;
		for (@Pc(11) CacheableNode current = this.sentinel.secondaryNext; current != this.sentinel; current = current.secondaryNext) {
			size++;
		}
		return size;
	}

	@OriginalMember(owner = "client!mn", name = "a", descriptor = "(B)Lclient!vl;")
	public CacheableNode next() {
		@Pc(11) CacheableNode node2 = this.pointer;
		if (node2 == this.sentinel) {
			this.pointer = null;
			return null;
		} else {
			this.pointer = node2.secondaryNext;
			return node2;
		}
	}

	@OriginalMember(owner = "client!mn", name = "a", descriptor = "(ZLclient!vl;)V")
	public void insert(@OriginalArg(1) CacheableNode node2) {
		if (node2.secondaryPrev != null) {
			node2.unlinkSecondary();
		}
		node2.secondaryNext = this.sentinel;
		node2.secondaryPrev = this.sentinel.secondaryPrev;
		node2.secondaryPrev.secondaryNext = node2;
		node2.secondaryNext.secondaryPrev = node2;
	}

	@OriginalMember(owner = "client!mn", name = "b", descriptor = "(I)Lclient!vl;")
	public CacheableNode removeFirst() {
		@Pc(13) CacheableNode node2 = this.sentinel.secondaryNext;
		if (node2 == this.sentinel) {
			return null;
		} else {
			node2.unlinkSecondary();
			return node2;
		}
	}

	@OriginalMember(owner = "client!mn", name = "c", descriptor = "(I)V")
	public void clear() {
		while (true) {
			@Pc(13) CacheableNode current = this.sentinel.secondaryNext;
			if (this.sentinel == current) {
				this.pointer = null;
				return;
			}
			current.unlinkSecondary();
		}
	}

	@OriginalMember(owner = "client!mn", name = "d", descriptor = "(I)Lclient!vl;")
	public CacheableNode first() {
		@Pc(13) CacheableNode node2 = this.sentinel.secondaryNext;
		if (node2 == this.sentinel) {
			this.pointer = null;
			return null;
		} else {
			this.pointer = node2.secondaryNext;
			return node2;
		}
	}
}
