package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.cache.node.Queue;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!wu")
public final class CacheableNodeQueue {

	@OriginalMember(owner = "client!wu", name = "i", descriptor = "Lclient!vl;")
	private CacheableNode head = new CacheableNode();

	@OriginalMember(owner = "client!wu", name = "k", descriptor = "Lclient!mn;")
	private final Queue queue = new Queue();

	@OriginalMember(owner = "client!wu", name = "m", descriptor = "I")
	private int capacityLeft;

	@OriginalMember(owner = "client!wu", name = "l", descriptor = "I")
	private final int initialCapacity;

	@OriginalMember(owner = "client!wu", name = "j", descriptor = "Lclient!eq;")
	private final IterableHashTable nodeMap;

	@OriginalMember(owner = "client!wu", name = "<init>", descriptor = "(I)V")
	public CacheableNodeQueue(@OriginalArg(0) int size) {
		this.capacityLeft = size;
		this.initialCapacity = size;
		@Pc(19) int bucketCount;
		for (bucketCount = 1; bucketCount + bucketCount < size; bucketCount += bucketCount) {
		}
		this.nodeMap = new IterableHashTable(bucketCount);
	}

	@OriginalMember(owner = "client!wu", name = "a", descriptor = "(JB)Lclient!vl;")
	public CacheableNode get(@OriginalArg(0) long searchKey) {
		@Pc(16) CacheableNode node2 = (CacheableNode) this.nodeMap.get(searchKey);
		if (node2 != null) {
			this.queue.insert(node2);
		}
		return node2;
	}

	@OriginalMember(owner = "client!wu", name = "a", descriptor = "(Z)V")
	public void clear() {
		this.queue.clear();
		this.nodeMap.clear();
		this.head = new CacheableNode();
		this.capacityLeft = this.initialCapacity;
	}

	@OriginalMember(owner = "client!wu", name = "a", descriptor = "(IJ)V")
	public void remove(@OriginalArg(1) long key) {
		@Pc(10) CacheableNode node = (CacheableNode) this.nodeMap.get(key);
		if (node != null) {
			node.unlink();
			node.unlinkSecondary();
			this.capacityLeft++;
		}
	}

	@OriginalMember(owner = "client!wu", name = "a", descriptor = "(JLclient!vl;I)V")
	public void put(@OriginalArg(0) long pointer, @OriginalArg(1) CacheableNode node2) {
		if (this.capacityLeft == 0) {
			@Pc(14) CacheableNode node22 = this.queue.removeFirst();
			node22.unlink();
			node22.unlinkSecondary();
			if (node22 == this.head) {
				node22 = this.queue.removeFirst();
				node22.unlink();
				node22.unlinkSecondary();
			}
		} else {
			this.capacityLeft--;
		}
		this.nodeMap.put(pointer, node2);
		this.queue.insert(node2);
	}
}
