package com.jagex.game.runetek5.core.datastruct.key;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.Node;

@OriginalClass("client!cga")
public final class DequeIterator {

	@OriginalMember(owner = "client!cga", name = "a", descriptor = "Lclient!eh;")
	private Node next;

	@OriginalMember(owner = "client!cga", name = "b", descriptor = "Lclient!uh;")
	private Deque deque;

	@OriginalMember(owner = "client!cga", name = "<init>", descriptor = "()V")
	public DequeIterator() {
	}

	@OriginalMember(owner = "client!cga", name = "<init>", descriptor = "(Lclient!uh;)V")
	public DequeIterator(@OriginalArg(0) Deque deque) {
		this.deque = deque;
	}

	@OriginalMember(owner = "client!cga", name = "a", descriptor = "(B)Lclient!eh;")
	public Node first() {
		@Pc(8) Node node = this.deque.sentinel.next;
		if (this.deque.sentinel == node) {
			this.next = null;
			return null;
		} else {
			this.next = node.next;
			return node;
		}
	}

	@OriginalMember(owner = "client!cga", name = "a", descriptor = "(Lclient!uh;I)V")
	public void setDeque(@OriginalArg(0) Deque deque) {
		this.deque = deque;
	}

	@OriginalMember(owner = "client!cga", name = "a", descriptor = "(I)Lclient!eh;")
	public Node next() {
		@Pc(11) Node node = this.next;
		if (this.deque.sentinel == node) {
			this.next = null;
			return null;
		} else {
			this.next = node.next;
			return node;
		}
	}
}
