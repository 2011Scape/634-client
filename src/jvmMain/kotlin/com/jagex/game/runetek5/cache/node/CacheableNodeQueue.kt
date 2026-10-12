package com.jagex.game.runetek5.cache.node

import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!wu")
class CacheableNodeQueue {

	@OriginalMember(owner = "client!wu", name = "i", descriptor = "Lclient!vl;")
	private var head: CacheableNode? = CacheableNode()

	@OriginalMember(owner = "client!wu", name = "k", descriptor = "Lclient!mn;")
	private val queue: Queue = Queue()

	@OriginalMember(owner = "client!wu", name = "m", descriptor = "I")
	private var capacityLeft: Int = 0

	@OriginalMember(owner = "client!wu", name = "l", descriptor = "I")
	private val initialCapacity: Int

	@OriginalMember(owner = "client!wu", name = "j", descriptor = "Lclient!eq;")
	private val nodeMap: IterableHashTable

	@OriginalMember(owner = "client!wu", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) size: Int) {
		this.capacityLeft = size
		this.initialCapacity = size
		@Pc(19) var bucketCount: Int
		bucketCount = 1
		while (bucketCount + bucketCount < size) {
			bucketCount += bucketCount
		}
		this.nodeMap = IterableHashTable(bucketCount)
	}

	@OriginalMember(owner = "client!wu", name = "a", descriptor = "(JB)Lclient!vl;")
	fun get(@OriginalArg(0) searchKey: Long): CacheableNode? {
		@Pc(16) val node2: CacheableNode? = this.nodeMap.get(searchKey) as CacheableNode?
		if (node2 != null) {
			this.queue.insert(node2)
		}
		return node2
	}

	@OriginalMember(owner = "client!wu", name = "a", descriptor = "(Z)V")
	fun clear() {
		this.queue.clear()
		this.nodeMap.clear()
		this.head = CacheableNode()
		this.capacityLeft = this.initialCapacity
	}

	@OriginalMember(owner = "client!wu", name = "a", descriptor = "(IJ)V")
	fun remove(@OriginalArg(1) key: Long) {
		@Pc(10) val node: CacheableNode? = this.nodeMap.get(key) as CacheableNode?
		if (node != null) {
			node.unlink()
			node.unlinkSecondary()
			this.capacityLeft++
		}
	}

	@OriginalMember(owner = "client!wu", name = "a", descriptor = "(JLclient!vl;I)V")
	fun put(@OriginalArg(0) pointer: Long, @OriginalArg(1) node2: CacheableNode?) {
		if (this.capacityLeft == 0) {
			@Pc(14) var node22: CacheableNode? = this.queue.removeFirst()
			node22!!.unlink()
			node22.unlinkSecondary()
			if (node22 === this.head) {
				node22 = this.queue.removeFirst()
				node22!!.unlink()
				node22.unlinkSecondary()
			}
		} else {
			this.capacityLeft--
		}
		this.nodeMap.put(pointer, node2)
		this.queue.insert(node2)
	}
}
