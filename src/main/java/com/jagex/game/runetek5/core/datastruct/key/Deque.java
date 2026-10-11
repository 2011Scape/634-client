package com.jagex.game.runetek5.core.datastruct.key;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.Node;

@OriginalClass("client!uh")
public final class Deque {

	@OriginalMember(owner = "client!uh", name = "t", descriptor = "Lclient!eh;")
	private Node pointer;

	@OriginalMember(owner = "client!uh", name = "l", descriptor = "Lclient!eh;")
	public final Node sentinel = new Node();

	@OriginalMember(owner = "client!uh", name = "<init>", descriptor = "()V")
	public Deque() {
		this.sentinel.previous = this.sentinel;
		this.sentinel.next = this.sentinel;
	}

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(Lclient!uh;B)V")
	public void transferAllTo(@OriginalArg(0) Deque deque) {
		this.spliceInto(this.sentinel.next, deque);
	}

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(Lclient!eh;ZLclient!uh;)V")
	private void spliceInto(@OriginalArg(0) Node node, @OriginalArg(2) Deque deque) {
		@Pc(7) Node node2 = this.sentinel.previous;
		this.sentinel.previous = node.previous;
		node.previous.next = this.sentinel;
		if (this.sentinel != node) {
			node.previous = deque.sentinel.previous;
			node.previous.next = node;
			deque.sentinel.previous = node2;
			node2.next = deque.sentinel;
		}
	}

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(B)Lclient!eh;")
	public Node next() {
		@Pc(12) Node node = this.pointer;
		if (this.sentinel == node) {
			this.pointer = null;
			return null;
		} else {
			this.pointer = node.next;
			return node;
		}
	}

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(I)Lclient!eh;")
	public Node last() {
		@Pc(7) Node node = this.sentinel.previous;
		if (this.sentinel == node) {
			this.pointer = null;
			return null;
		} else {
			this.pointer = node.previous;
			return node;
		}
	}

	@OriginalMember(owner = "client!uh", name = "c", descriptor = "(I)Lclient!eh;")
	public Node first() {
		@Pc(14) Node node = this.sentinel.next;
		if (node == this.sentinel) {
			this.pointer = null;
			return null;
		} else {
			this.pointer = node.next;
			return node;
		}
	}

	@OriginalMember(owner = "client!uh", name = "d", descriptor = "(I)V")
	public void clear() {
		while (true) {
			@Pc(13) Node current = this.sentinel.next;
			if (this.sentinel == current) {
				this.pointer = null;
				return;
			}
			current.unlink();
		}
	}

	@OriginalMember(owner = "client!uh", name = "e", descriptor = "(I)Lclient!eh;")
	public Node removeFirst() {
		@Pc(12) Node node = this.sentinel.next;
		if (this.sentinel == node) {
			return null;
		} else {
			node.unlink();
			return node;
		}
	}

	@OriginalMember(owner = "client!uh", name = "f", descriptor = "(I)I")
	public int size() {
		@Pc(7) int size = 0;
		@Pc(11) Node node = this.sentinel.next;
		while (node != this.sentinel) {
			node = node.next;
			size++;
		}
		return size;
	}

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(Lclient!eh;I)V")
	public void addLast(@OriginalArg(0) Node node) {
		if (node.previous != null) {
			node.unlink();
		}
		node.next = this.sentinel;
		node.previous = this.sentinel.previous;
		node.previous.next = node;
		node.next.previous = node;
	}

	@OriginalMember(owner = "client!uh", name = "b", descriptor = "(Lclient!eh;I)V")
	public void addFirst(@OriginalArg(0) Node node) {
		if (node.previous != null) {
			node.unlink();
		}
		node.previous = this.sentinel;
		node.next = this.sentinel.next;
		node.previous.next = node;
		node.next.previous = node;
	}

	@OriginalMember(owner = "client!uh", name = "b", descriptor = "(B)Z")
	public boolean isEmpty() {
		return this.sentinel == this.sentinel.next;
	}

	@OriginalMember(owner = "client!uh", name = "g", descriptor = "(I)Lclient!eh;")
	public Node previous() {
		@Pc(6) Node node = this.pointer;
		if (node == this.sentinel) {
			this.pointer = null;
			return null;
		} else {
			this.pointer = node.previous;
			return node;
		}
	}
}
