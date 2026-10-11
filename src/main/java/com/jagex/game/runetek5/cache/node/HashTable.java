package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ib")
public final class HashTable {

	@OriginalMember(owner = "client!ib", name = "g", descriptor = "J")
	private long searchKey;

	@OriginalMember(owner = "client!ib", name = "h", descriptor = "Lclient!vl;")
	private CacheableNode searchPointer;

	@OriginalMember(owner = "client!ib", name = "f", descriptor = "[Lclient!vl;")
	private final CacheableNode[] buckets;

	@OriginalMember(owner = "client!ib", name = "b", descriptor = "I")
	private final int bucketCount;

	@OriginalMember(owner = "client!ib", name = "<init>", descriptor = "(I)V")
	public HashTable(@OriginalArg(0) int size) {
		this.buckets = new CacheableNode[size];
		this.bucketCount = size;
		for (@Pc(10) int i = 0; i < size; i++) {
			@Pc(20) CacheableNode node2 = this.buckets[i] = new CacheableNode();
			node2.secondaryNext = node2;
			node2.secondaryPrev = node2;
		}
	}

	@OriginalMember(owner = "client!ib", name = "a", descriptor = "(JB)Lclient!vl;")
	public CacheableNode get(@OriginalArg(0) long searchKey) {
		this.searchKey = searchKey;
		@Pc(20) CacheableNode node2 = this.buckets[(int) ((long) (this.bucketCount - 1) & searchKey)];
		for (this.searchPointer = node2.secondaryNext; this.searchPointer != node2; this.searchPointer = this.searchPointer.secondaryNext) {
			if (searchKey == this.searchPointer.indexReference) {
				@Pc(39) CacheableNode node22 = this.searchPointer;
				this.searchPointer = this.searchPointer.secondaryNext;
				return node22;
			}
		}
		this.searchPointer = null;
		return null;
	}

	@OriginalMember(owner = "client!ib", name = "a", descriptor = "(Lclient!vl;IJ)V")
	public void put(@OriginalArg(0) CacheableNode node2, @OriginalArg(2) long indexReference) {
		if (node2.secondaryPrev != null) {
			node2.unlinkSecondary();
		}
		@Pc(27) CacheableNode node22 = this.buckets[(int) (indexReference & (long) (this.bucketCount - 1))];
		node2.secondaryPrev = node22.secondaryPrev;
		node2.secondaryNext = node22;
		node2.secondaryPrev.secondaryNext = node2;
		node2.secondaryNext.secondaryPrev = node2;
		node2.indexReference = indexReference;
	}

	@OriginalMember(owner = "client!ib", name = "a", descriptor = "(Z)Lclient!vl;")
	public CacheableNode nextInBucket() {
		if (this.searchPointer == null) {
			return null;
		}
		@Pc(28) CacheableNode node2 = this.buckets[(int) (this.searchKey & (long) (this.bucketCount - 1))];
		while (node2 != this.searchPointer) {
			if (this.searchPointer.indexReference == this.searchKey) {
				@Pc(44) CacheableNode node22 = this.searchPointer;
				this.searchPointer = this.searchPointer.secondaryNext;
				return node22;
			}
			this.searchPointer = this.searchPointer.secondaryNext;
		}
		this.searchPointer = null;
		return null;
	}
}
