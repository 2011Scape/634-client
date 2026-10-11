package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.cache.node.Queue;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!os")
public final class QueueIterator {

	@OriginalMember(owner = "client!os", name = "e", descriptor = "Lclient!vl;")
	private CacheableNode node;

	@OriginalMember(owner = "client!os", name = "h", descriptor = "Lclient!mn;")
	private Queue queue;

	@OriginalMember(owner = "client!os", name = "<init>", descriptor = "()V")
	private QueueIterator() {
	}

	@OriginalMember(owner = "client!os", name = "<init>", descriptor = "(Lclient!mn;)V")
	public QueueIterator(@OriginalArg(0) Queue queue) {
		this.queue = queue;
	}

	@OriginalMember(owner = "client!os", name = "b", descriptor = "(I)Lclient!vl;")
	public CacheableNode first() {
		@Pc(15) CacheableNode node2 = this.queue.sentinel.secondaryNext;
		if (node2 == this.queue.sentinel) {
			this.node = null;
			return null;
		} else {
			this.node = node2.secondaryNext;
			return node2;
		}
	}

	@OriginalMember(owner = "client!os", name = "a", descriptor = "(B)Lclient!vl;")
	public CacheableNode next() {
		@Pc(11) CacheableNode node2 = this.node;
		if (node2 == this.queue.sentinel) {
			this.node = null;
			return null;
		} else {
			this.node = node2.secondaryNext;
			return node2;
		}
	}
}
