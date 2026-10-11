package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Queue;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.ReferenceFactory;

@OriginalClass("client!jr")
public final class LRUCache {

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "I")
	private final int capacity;

	@OriginalMember(owner = "client!jr", name = "c", descriptor = "I")
	private int capacityLeft;

	@OriginalMember(owner = "client!jr", name = "f", descriptor = "Lclient!mn;")
	private final Queue queue;

	@OriginalMember(owner = "client!jr", name = "q", descriptor = "Lclient!eq;")
	private final IterableHashTable nodeMap;

	@OriginalMember(owner = "client!jr", name = "<init>", descriptor = "(I)V")
	public LRUCache(@OriginalArg(0) int capacity) {
		this(capacity, capacity);
	}

	@OriginalMember(owner = "client!jr", name = "<init>", descriptor = "(II)V")
	public LRUCache(@OriginalArg(0) int big, @OriginalArg(1) int small) {
		this.queue = new Queue();
		this.capacityLeft = big;
		this.capacity = big;
		@Pc(14) int bucketCount;
		for (bucketCount = 1; bucketCount + bucketCount < big && small > bucketCount; bucketCount += bucketCount) {
		}
		this.nodeMap = new IterableHashTable(bucketCount);
	}

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "(I)I")
	public int hardCount() {
		@Pc(7) int count = 0;
		for (@Pc(13) AbstractLRUNode node = (AbstractLRUNode) this.queue.first(); node != null; node = (AbstractLRUNode) this.queue.next()) {
			if (!node.isSoft()) {
				count++;
			}
		}
		return count;
	}

	@OriginalMember(owner = "client!jr", name = "b", descriptor = "(I)I")
	public int capacity() {
		return this.capacity;
	}

	@OriginalMember(owner = "client!jr", name = "b", descriptor = "(II)V")
	public void clean(@OriginalArg(1) int maxAge) {
		if (LRUCache.aClass47_1 == null) {
			return;
		}
		for (@Pc(17) AbstractLRUNode node = (AbstractLRUNode) this.queue.first(); node != null; node = (AbstractLRUNode) this.queue.next()) {
			if (node.isSoft()) {
				if (node.get() == null) {
					node.unlink();
					node.unlinkSecondary();
					this.capacityLeft += node.capacityRequirement;
				}
			} else if (++node.indexReference > (long) maxAge) {
				@Pc(43) AbstractLRUNode newReference = LRUCache.aClass47_1.createSoftReference(node);
				this.nodeMap.put(node.key, newReference);
				CacheableNode.insertSecondaryAfter(newReference, node);
				node.unlink();
				node.unlinkSecondary();
			}
		}
	}

	@OriginalMember(owner = "client!jr", name = "c", descriptor = "(I)Ljava/lang/Object;")
	public Object next() {
		@Pc(11) AbstractLRUNode current = (AbstractLRUNode) this.nodeMap.next();
		while (current != null) {
			@Pc(17) Object object = current.get();
			if (object != null) {
				return object;
			}
			@Pc(23) AbstractLRUNode copy = current;
			current = (AbstractLRUNode) this.nodeMap.next();
			copy.unlink();
			copy.unlinkSecondary();
			this.capacityLeft += copy.capacityRequirement;
		}
		return null;
	}

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "(ILjava/lang/Object;JI)V")
	public void put(@OriginalArg(1) Object object, @OriginalArg(2) long key, @OriginalArg(3) int capacityRequirement) {
		if (capacityRequirement > this.capacity) {
			throw new IllegalStateException("s>cs");
		}
		this.removeByKey(key);
		this.capacityLeft -= capacityRequirement;
		while (this.capacityLeft < 0) {
			@Pc(34) AbstractLRUNode referenceNode = (AbstractLRUNode) this.queue.removeFirst();
			this.remove(referenceNode);
		}
		@Pc(52) HardReferenceNode hardReferenceNode = new HardReferenceNode(object, capacityRequirement);
		this.nodeMap.put(key, hardReferenceNode);
		this.queue.insert(hardReferenceNode);
		hardReferenceNode.indexReference = 0L;
	}

	@OriginalMember(owner = "client!jr", name = "d", descriptor = "(I)I")
	public int remaining() {
		return this.capacityLeft;
	}

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "(Ljava/lang/Object;JB)V")
	public void put(@OriginalArg(0) Object object, @OriginalArg(1) long key) {
		this.put(object, key, 1);
	}

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "(JI)Ljava/lang/Object;")
	public Object get(@OriginalArg(0) long key) {
		@Pc(18) AbstractLRUNode node = (AbstractLRUNode) this.nodeMap.get(key);
		if (node == null) {
			return null;
		}
		@Pc(26) Object object = node.get();
		if (object == null) {
			node.unlink();
			node.unlinkSecondary();
			this.capacityLeft += node.capacityRequirement;
			return null;
		}
		if (node.isSoft()) {
			@Pc(54) HardReferenceNode hardReference = new HardReferenceNode(object, node.capacityRequirement);
			this.nodeMap.put(node.key, hardReference);
			this.queue.insert(hardReference);
			hardReference.indexReference = 0L;
			node.unlink();
			node.unlinkSecondary();
		} else {
			this.queue.insert(node);
			node.indexReference = 0L;
		}
		return object;
	}

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "(Lclient!qia;I)V")
	private void remove(@OriginalArg(0) AbstractLRUNode referenceNode) {
		if (referenceNode != null) {
			referenceNode.unlink();
			referenceNode.unlinkSecondary();
			this.capacityLeft += referenceNode.capacityRequirement;
		}
	}

	@OriginalMember(owner = "client!jr", name = "b", descriptor = "(JI)V")
	private void removeByKey(@OriginalArg(0) long searchKey) {
		@Pc(15) AbstractLRUNode referenceNode = (AbstractLRUNode) this.nodeMap.get(searchKey);
		this.remove(referenceNode);
	}

	@OriginalMember(owner = "client!jr", name = "e", descriptor = "(I)V")
	public void removeSoftReferences() {
		for (@Pc(11) AbstractLRUNode node = (AbstractLRUNode) this.queue.first(); node != null; node = (AbstractLRUNode) this.queue.next()) {
			if (node.isSoft()) {
				node.unlink();
				node.unlinkSecondary();
				this.capacityLeft += node.capacityRequirement;
			}
		}
	}

	@OriginalMember(owner = "client!jr", name = "f", descriptor = "(I)Ljava/lang/Object;")
	public Object first() {
		@Pc(11) AbstractLRUNode current = (AbstractLRUNode) this.nodeMap.first();
		while (current != null) {
			@Pc(24) Object object = current.get();
			if (object != null) {
				return object;
			}
			@Pc(28) AbstractLRUNode copy = current;
			current = (AbstractLRUNode) this.nodeMap.next();
			copy.unlink();
			copy.unlinkSecondary();
			this.capacityLeft += copy.capacityRequirement;
		}
		return null;
	}

	@OriginalMember(owner = "client!jr", name = "g", descriptor = "(I)V")
	public void reset() {
		this.queue.clear();
		this.nodeMap.clear();
		this.capacityLeft = this.capacity;
	}
	@OriginalMember(owner = "client!gq", name = "B", descriptor = "Lclient!ca;")
	public static final ReferenceFactory aClass47_1 = ReferenceFactory.createSoftReferenceFactory();
}
