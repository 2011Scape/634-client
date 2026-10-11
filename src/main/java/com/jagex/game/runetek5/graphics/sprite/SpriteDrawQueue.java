package com.jagex.game.runetek5.graphics.sprite;

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Queue;
import com.jagex.game.runetek5.entity.ComparableGraphNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.impl.KeyedHardReferenceNode;
import com.jagex.game.runetek5.cache.node.impl.SpriteRenderNode;
import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.graphics.texture.water.KeyedReferenceFactory;

@OriginalClass("client!pq")
public final class SpriteDrawQueue {

	@OriginalMember(owner = "client!pq", name = "g", descriptor = "Lclient!mn;")
	private final Queue spriteDrawQueue = new Queue();

	@OriginalMember(owner = "client!pq", name = "b", descriptor = "I")
	private int currentCapacity;

	@OriginalMember(owner = "client!pq", name = "o", descriptor = "I")
	private final int maxCapacity;

	@OriginalMember(owner = "client!pq", name = "c", descriptor = "Lclient!eq;")
	private final IterableHashTable spriteRenderNodes;

	@OriginalMember(owner = "client!pq", name = "<init>", descriptor = "(I)V")
	public SpriteDrawQueue(@OriginalArg(0) int capacity) {
		this.currentCapacity = capacity;
		this.maxCapacity = capacity;
		@Pc(16) int bucketCount;
		for (bucketCount = 1; bucketCount + bucketCount < capacity; bucketCount += bucketCount) {
		}
		this.spriteRenderNodes = new IterableHashTable(bucketCount);
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(ILclient!we;)V")
	private void discard(@OriginalArg(1) ComparableGraphNode cacheKey) {
		@Pc(9) long uid = cacheKey.toLong();
		for (@Pc(16) SpriteRenderNode keyedReferenceNode = (SpriteRenderNode) this.spriteRenderNodes.get(uid); keyedReferenceNode != null; keyedReferenceNode = (SpriteRenderNode) this.spriteRenderNodes.nextWithKey()) {
			if (keyedReferenceNode.cacheKey.matches(cacheKey)) {
				this.discardNode(keyedReferenceNode);
				break;
			}
		}
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(I)I")
	public int capacity() {
		return this.maxCapacity;
	}

	@OriginalMember(owner = "client!pq", name = "b", descriptor = "(ILclient!we;)Ljava/lang/Object;")
	public Object get(@OriginalArg(1) ComparableGraphNode cacheKey) {
		@Pc(9) long uid = cacheKey.toLong();
		for (@Pc(16) SpriteRenderNode keyedReferenceNode = (SpriteRenderNode) this.spriteRenderNodes.get(uid); keyedReferenceNode != null; keyedReferenceNode = (SpriteRenderNode) this.spriteRenderNodes.nextWithKey()) {
			if (keyedReferenceNode.cacheKey.matches(cacheKey)) {
				@Pc(28) Object object = keyedReferenceNode.get();
				if (object != null) {
					if (keyedReferenceNode.isSoft()) {
						@Pc(57) KeyedHardReferenceNode keyedHardReferenceNode = new KeyedHardReferenceNode(cacheKey, object, keyedReferenceNode.size);
						this.spriteRenderNodes.put(keyedReferenceNode.key, keyedHardReferenceNode);
						this.spriteDrawQueue.insert(keyedHardReferenceNode);
						keyedHardReferenceNode.indexReference = 0L;
						keyedReferenceNode.unlink();
						keyedReferenceNode.unlinkSecondary();
					} else {
						this.spriteDrawQueue.insert(keyedReferenceNode);
						keyedReferenceNode.indexReference = 0L;
					}
					return object;
				}
				keyedReferenceNode.unlink();
				keyedReferenceNode.unlinkSecondary();
				this.currentCapacity += keyedReferenceNode.size;
			}
		}
		return null;
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(Ljava/lang/Object;Lclient!we;II)V")
	private void insert(@OriginalArg(0) Object object, @OriginalArg(1) ComparableGraphNode cacheKey) {
		if (this.maxCapacity < 1) {
			throw new IllegalStateException("s>cs");
		}
		this.discard(cacheKey);
		this.currentCapacity--;
		while (this.currentCapacity < 0) {
			@Pc(40) SpriteRenderNode keyedReferenceNode = (SpriteRenderNode) this.spriteDrawQueue.removeFirst();
			this.discardNode(keyedReferenceNode);
		}
		@Pc(54) KeyedHardReferenceNode keyedHardReferenceNode = new KeyedHardReferenceNode(cacheKey, object, 1);
		this.spriteRenderNodes.put(cacheKey.toLong(), keyedHardReferenceNode);
		this.spriteDrawQueue.insert(keyedHardReferenceNode);
		keyedHardReferenceNode.indexReference = 0L;
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(Lclient!rd;B)V")
	private void discardNode(@OriginalArg(0) SpriteRenderNode keyedReferenceNode) {
		if (keyedReferenceNode != null) {
			keyedReferenceNode.unlink();
			keyedReferenceNode.unlinkSecondary();
			this.currentCapacity += keyedReferenceNode.size;
		}
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(B)V")
	public void removeSoftReferences() {
		for (@Pc(11) SpriteRenderNode node = (SpriteRenderNode) this.spriteDrawQueue.first(); node != null; node = (SpriteRenderNode) this.spriteDrawQueue.next()) {
			if (node.isSoft()) {
				node.unlink();
				node.unlinkSecondary();
				this.currentCapacity += node.size;
			}
		}
	}

	@OriginalMember(owner = "client!pq", name = "b", descriptor = "(B)V")
	public void reset() {
		this.spriteDrawQueue.clear();
		this.spriteRenderNodes.clear();
		this.currentCapacity = this.maxCapacity;
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(II)V")
	public void processCycle() {
		if (SpriteDrawQueue.aClass89_1 == null) {
			return;
		}
		for (@Pc(13) SpriteRenderNode keyedReferenceNode = (SpriteRenderNode) this.spriteDrawQueue.first(); keyedReferenceNode != null; keyedReferenceNode = (SpriteRenderNode) this.spriteDrawQueue.next()) {
			if (keyedReferenceNode.isSoft()) {
				if (keyedReferenceNode.get() == null) {
					keyedReferenceNode.unlink();
					keyedReferenceNode.unlinkSecondary();
					this.currentCapacity += keyedReferenceNode.size;
				}
			} else if ((long) 5 < ++keyedReferenceNode.indexReference) {
				@Pc(60) SpriteRenderNode keyedReferenceNode2 = SpriteDrawQueue.aClass89_1.createSoftReference(keyedReferenceNode);
				this.spriteRenderNodes.put(keyedReferenceNode.key, keyedReferenceNode2);
				CacheableNode.insertSecondaryAfter(keyedReferenceNode2, keyedReferenceNode);
				keyedReferenceNode.unlink();
				keyedReferenceNode.unlinkSecondary();
			}
		}
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(ILjava/lang/Object;Lclient!we;)V")
	public void put(@OriginalArg(1) Object object, @OriginalArg(2) ComparableGraphNode cacheKey) {
		this.insert(object, cacheKey);
	}

	@OriginalMember(owner = "client!pq", name = "d", descriptor = "(B)I")
	public int remaining() {
		return this.currentCapacity;
	}
	@OriginalMember(owner = "client!mh", name = "a", descriptor = "Lclient!ef;")
	public static final KeyedReferenceFactory aClass89_1 = KeyedReferenceFactory.create();
}
