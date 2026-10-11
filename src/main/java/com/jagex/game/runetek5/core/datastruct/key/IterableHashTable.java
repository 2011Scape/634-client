package com.jagex.game.runetek5.core.datastruct.key;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.Node;

@OriginalClass("client!eq")
public final class IterableHashTable {

	@OriginalMember(owner = "client!eq", name = "l", descriptor = "J")
	private long searchKey;

	@OriginalMember(owner = "client!eq", name = "p", descriptor = "Lclient!eh;")
	private Node searchPointer;

	@OriginalMember(owner = "client!eq", name = "q", descriptor = "Lclient!eh;")
	private Node next;

	@OriginalMember(owner = "client!eq", name = "r", descriptor = "I")
	private int iteratorPosition = 0;

	@OriginalMember(owner = "client!eq", name = "d", descriptor = "I")
	public final int bucketCount;

	@OriginalMember(owner = "client!eq", name = "a", descriptor = "[Lclient!eh;")
	public final Node[] buckets;

	@OriginalMember(owner = "client!eq", name = "<init>", descriptor = "(I)V")
	public IterableHashTable(@OriginalArg(0) int bucketCount) {
		this.bucketCount = bucketCount;
		this.buckets = new Node[bucketCount];
		for (@Pc(13) int i = 0; i < bucketCount; i++) {
			@Pc(23) Node node = this.buckets[i] = new Node();
			node.next = node;
			node.previous = node;
		}
	}

	@OriginalMember(owner = "client!eq", name = "a", descriptor = "(I)I")
	public int size() {
		@Pc(13) int n = 0;
		for (@Pc(15) int i = 0; i < this.bucketCount; i++) {
			@Pc(22) Node bucket = this.buckets[i];
			for (@Pc(25) Node current = bucket.next; current != bucket; current = current.next) {
				n++;
			}
		}
		return n;
	}

	@OriginalMember(owner = "client!eq", name = "a", descriptor = "(Z)I")
	public int getBucketCount() {
		return this.bucketCount;
	}

	@OriginalMember(owner = "client!eq", name = "b", descriptor = "(Z)Lclient!eh;")
	public Node nextWithKey() {
		if (this.searchPointer == null) {
			return null;
		}
		@Pc(23) Node node = this.buckets[(int) ((long) (this.bucketCount - 1) & this.searchKey)];
		while (this.searchPointer != node) {
			if (this.searchPointer.key == this.searchKey) {
				@Pc(41) Node node2 = this.searchPointer;
				this.searchPointer = this.searchPointer.next;
				return node2;
			}
			this.searchPointer = this.searchPointer.next;
		}
		this.searchPointer = null;
		return null;
	}

	@OriginalMember(owner = "client!eq", name = "a", descriptor = "(I[Lclient!eh;)I")
	public int toArray(@OriginalArg(1) Node[] nodes) {
		@Pc(12) int count = 0;
		for (@Pc(14) int i = 0; i < this.bucketCount; i++) {
			@Pc(21) Node node = this.buckets[i];
			for (@Pc(24) Node node2 = node.next; node2 != node; node2 = node2.next) {
				nodes[count++] = node2;
			}
		}
		return count;
	}

	@OriginalMember(owner = "client!eq", name = "a", descriptor = "(JI)Lclient!eh;")
	public Node get(@OriginalArg(0) long searchKey) {
		this.searchKey = searchKey;
		@Pc(20) Node node = this.buckets[(int) (searchKey & (long) (this.bucketCount - 1))];
		for (this.searchPointer = node.next; this.searchPointer != node; this.searchPointer = this.searchPointer.next) {
			if (this.searchPointer.key == searchKey) {
				@Pc(46) Node node2 = this.searchPointer;
				this.searchPointer = this.searchPointer.next;
				return node2;
			}
		}
		this.searchPointer = null;
		return null;
	}

	@OriginalMember(owner = "client!eq", name = "c", descriptor = "(I)V")
	public void clear() {
		for (@Pc(7) int i = 0; i < this.bucketCount; i++) {
			@Pc(14) Node bucket = this.buckets[i];
			while (true) {
				@Pc(17) Node current = bucket.next;
				if (current == bucket) {
					break;
				}
				current.unlink();
			}
		}
		this.searchPointer = null;
		this.next = null;
	}

	@OriginalMember(owner = "client!eq", name = "d", descriptor = "(I)Lclient!eh;")
	public Node next() {
		@Pc(20) Node node;
		if (this.iteratorPosition > 0 && this.next != this.buckets[this.iteratorPosition - 1]) {
			node = this.next;
			this.next = node.next;
			return node;
		}
		while (this.iteratorPosition < this.bucketCount) {
			node = this.buckets[this.iteratorPosition++].next;
			if (this.buckets[this.iteratorPosition - 1] != node) {
				this.next = node.next;
				return node;
			}
		}
		return null;
	}

	@OriginalMember(owner = "client!eq", name = "a", descriptor = "(BJLclient!eh;)V")
	public void put(@OriginalArg(1) long pointer, @OriginalArg(2) Node node) {
		if (node.previous != null) {
			node.unlink();
		}
		@Pc(27) Node node2 = this.buckets[(int) (pointer & (long) (this.bucketCount - 1))];
		node.next = node2;
		node.previous = node2.previous;
		node.previous.next = node;
		node.next.previous = node;
		node.key = pointer;
	}

	@OriginalMember(owner = "client!eq", name = "e", descriptor = "(I)Lclient!eh;")
	public Node first() {
		this.iteratorPosition = 0;
		return this.next();
	}
}
