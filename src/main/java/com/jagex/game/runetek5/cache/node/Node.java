package com.jagex.game.runetek5.cache.node;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;

@OriginalClass("client!eh")
public class Node {

	@OriginalMember(owner = "client!eh", name = "g", descriptor = "J")
	public long key;

	@OriginalMember(owner = "client!eh", name = "j", descriptor = "Lclient!eh;")
	public Node next;

	@OriginalMember(owner = "client!eh", name = "k", descriptor = "Lclient!eh;")
	public Node previous;

	@OriginalMember(owner = "client!eh", name = "c", descriptor = "(B)Z")
	public final boolean isLinked() {
		return this.previous != null;
	}

	@OriginalMember(owner = "client!eh", name = "d", descriptor = "(B)V")
	public final void unlink() {
		if (this.previous != null) {
			this.previous.next = this.next;
			this.next.previous = this.previous;
			this.next = null;
			this.previous = null;
		}
	}
	@OriginalMember(owner = "client!qh", name = "a", descriptor = "(ILclient!eh;Lclient!eh;)V")
	public static void insertBefore(@OriginalArg(1) Node node, @OriginalArg(2) Node node2) {
		if (node.previous != null) {
			node.unlink();
		}
		node.previous = node2.previous;
		node.next = node2;
		node.previous.next = node;
		node.next.previous = node;
	}
}
