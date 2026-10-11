package com.jagex.game.runetek5.cache.node;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;

@OriginalClass("client!aga")
public final class HashTableIterator {

	@OriginalMember(owner = "client!aga", name = "c", descriptor = "Lclient!eq;")
	private IterableHashTable table;

	@OriginalMember(owner = "client!aga", name = "g", descriptor = "Lclient!eh;")
	private Node pointer;

	@OriginalMember(owner = "client!aga", name = "e", descriptor = "I")
	private int bucket = 0;

	@OriginalMember(owner = "client!aga", name = "<init>", descriptor = "()V")
	private HashTableIterator() {
	}

	@OriginalMember(owner = "client!aga", name = "<init>", descriptor = "(Lclient!eq;)V")
	public HashTableIterator(@OriginalArg(0) IterableHashTable iterableHashTable) {
		this.table = iterableHashTable;
	}

	@OriginalMember(owner = "client!aga", name = "a", descriptor = "(B)Lclient!eh;")
	public Node first() {
		this.bucket = 0;
		return this.next();
	}

	@OriginalMember(owner = "client!aga", name = "b", descriptor = "(B)Lclient!eh;")
	public Node next() {
		@Pc(28) Node node;
		if (this.bucket > 0 && this.table.buckets[this.bucket - 1] != this.pointer) {
			node = this.pointer;
			this.pointer = node.next;
			return node;
		}
		while (this.table.bucketCount > this.bucket) {
			node = this.table.buckets[this.bucket++].next;
			if (this.table.buckets[this.bucket - 1] != node) {
				this.pointer = node.next;
				return node;
			}
		}
		return null;
	}
}
