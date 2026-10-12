package com.jagex.game.runetek5.cache.node.lru

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Queue
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.ReferenceFactory
import kotlin.jvm.JvmField

@OriginalClass("client!jr")
class LRUCache {

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "I")
	private val capacity: Int

	@OriginalMember(owner = "client!jr", name = "c", descriptor = "I")
	private var capacityLeft: Int

	@OriginalMember(owner = "client!jr", name = "f", descriptor = "Lclient!mn;")
	private val queue: Queue

	@OriginalMember(owner = "client!jr", name = "q", descriptor = "Lclient!eq;")
	private val nodeMap: IterableHashTable

	@OriginalMember(owner = "client!jr", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) capacity: Int) : this(capacity, capacity)

	@OriginalMember(owner = "client!jr", name = "<init>", descriptor = "(II)V")
	constructor(@OriginalArg(0) big: Int, @OriginalArg(1) small: Int) {
		this.queue = Queue()
		this.capacityLeft = big
		this.capacity = big
		@Pc(14) var bucketCount: Int
		bucketCount = 1
		while (bucketCount + bucketCount < big && small > bucketCount) {
			bucketCount += bucketCount
		}
		this.nodeMap = IterableHashTable(bucketCount)
	}

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "(I)I")
	fun hardCount(): Int {
		@Pc(7) var count: Int = 0
		@Pc(13) var node: AbstractLRUNode? = this.queue.first() as AbstractLRUNode?
		while (node != null) {
			if (!node.isSoft()) {
				count++
			}
			node = this.queue.next() as AbstractLRUNode?
		}
		return count
	}

	@OriginalMember(owner = "client!jr", name = "b", descriptor = "(I)I")
	fun capacity(): Int {
		return this.capacity
	}

	@OriginalMember(owner = "client!jr", name = "b", descriptor = "(II)V")
	fun clean(@OriginalArg(1) maxAge: Int) {
		if (LRUCache.aClass47_1 == null) {
			return
		}
		@Pc(17) var node: AbstractLRUNode? = this.queue.first() as AbstractLRUNode?
		while (node != null) {
			if (node.isSoft()) {
				if (node.get() == null) {
					node.unlink()
					node.unlinkSecondary()
					this.capacityLeft += node.capacityRequirement
				}
			} else if (++node.indexReference > maxAge.toLong()) {
				@Pc(43) val newReference: AbstractLRUNode? = LRUCache.aClass47_1.createSoftReference(node)
				this.nodeMap.put(node.key, newReference)
				CacheableNode.insertSecondaryAfter(newReference, node)
				node.unlink()
				node.unlinkSecondary()
			}
			node = this.queue.next() as AbstractLRUNode?
		}
	}

	@OriginalMember(owner = "client!jr", name = "c", descriptor = "(I)Ljava/lang/Object;")
	fun next(): Any? {
		@Pc(11) var current: AbstractLRUNode? = this.nodeMap.next() as AbstractLRUNode?
		while (current != null) {
			@Pc(17) val `object`: Any? = current.get()
			if (`object` != null) {
				return `object`
			}
			@Pc(23) val copy: AbstractLRUNode = current
			current = this.nodeMap.next() as AbstractLRUNode?
			copy.unlink()
			copy.unlinkSecondary()
			this.capacityLeft += copy.capacityRequirement
		}
		return null
	}

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "(ILjava/lang/Object;JI)V")
	fun put(@OriginalArg(1) `object`: Any?, @OriginalArg(2) key: Long, @OriginalArg(3) capacityRequirement: Int) {
		if (capacityRequirement > this.capacity) {
			throw IllegalStateException("s>cs")
		}
		this.removeByKey(key)
		this.capacityLeft -= capacityRequirement
		while (this.capacityLeft < 0) {
			@Pc(34) val referenceNode: AbstractLRUNode? = this.queue.removeFirst() as AbstractLRUNode?
			this.remove(referenceNode)
		}
		@Pc(52) val hardReferenceNode: HardReferenceNode = HardReferenceNode(`object`, capacityRequirement)
		this.nodeMap.put(key, hardReferenceNode)
		this.queue.insert(hardReferenceNode)
		hardReferenceNode.indexReference = 0L
	}

	@OriginalMember(owner = "client!jr", name = "d", descriptor = "(I)I")
	fun remaining(): Int {
		return this.capacityLeft
	}

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "(Ljava/lang/Object;JB)V")
	fun put(@OriginalArg(0) `object`: Any?, @OriginalArg(1) key: Long) {
		this.put(`object`, key, 1)
	}

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "(JI)Ljava/lang/Object;")
	fun get(@OriginalArg(0) key: Long): Any? {
		@Pc(18) val node: AbstractLRUNode? = this.nodeMap.get(key) as AbstractLRUNode?
		if (node == null) {
			return null
		}
		@Pc(26) val `object`: Any? = node.get()
		if (`object` == null) {
			node.unlink()
			node.unlinkSecondary()
			this.capacityLeft += node.capacityRequirement
			return null
		}
		if (node.isSoft()) {
			@Pc(54) val hardReference: HardReferenceNode = HardReferenceNode(`object`, node.capacityRequirement)
			this.nodeMap.put(node.key, hardReference)
			this.queue.insert(hardReference)
			hardReference.indexReference = 0L
			node.unlink()
			node.unlinkSecondary()
		} else {
			this.queue.insert(node)
			node.indexReference = 0L
		}
		return `object`
	}

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "(Lclient!qia;I)V")
	private fun remove(@OriginalArg(0) referenceNode: AbstractLRUNode?) {
		if (referenceNode != null) {
			referenceNode.unlink()
			referenceNode.unlinkSecondary()
			this.capacityLeft += referenceNode.capacityRequirement
		}
	}

	@OriginalMember(owner = "client!jr", name = "b", descriptor = "(JI)V")
	private fun removeByKey(@OriginalArg(0) searchKey: Long) {
		@Pc(15) val referenceNode: AbstractLRUNode? = this.nodeMap.get(searchKey) as AbstractLRUNode?
		this.remove(referenceNode)
	}

	@OriginalMember(owner = "client!jr", name = "e", descriptor = "(I)V")
	fun removeSoftReferences() {
		@Pc(11) var node: AbstractLRUNode? = this.queue.first() as AbstractLRUNode?
		while (node != null) {
			if (node.isSoft()) {
				node.unlink()
				node.unlinkSecondary()
				this.capacityLeft += node.capacityRequirement
			}
			node = this.queue.next() as AbstractLRUNode?
		}
	}

	@OriginalMember(owner = "client!jr", name = "f", descriptor = "(I)Ljava/lang/Object;")
	fun first(): Any? {
		@Pc(11) var current: AbstractLRUNode? = this.nodeMap.first() as AbstractLRUNode?
		while (current != null) {
			@Pc(24) val `object`: Any? = current.get()
			if (`object` != null) {
				return `object`
			}
			@Pc(28) val copy: AbstractLRUNode = current
			current = this.nodeMap.next() as AbstractLRUNode?
			copy.unlink()
			copy.unlinkSecondary()
			this.capacityLeft += copy.capacityRequirement
		}
		return null
	}

	@OriginalMember(owner = "client!jr", name = "g", descriptor = "(I)V")
	fun reset() {
		this.queue.clear()
		this.nodeMap.clear()
		this.capacityLeft = this.capacity
	}

	companion object {
		@OriginalMember(owner = "client!gq", name = "B", descriptor = "Lclient!ca;")
		@JvmField
		val aClass47_1: ReferenceFactory? = ReferenceFactory.createSoftReferenceFactory()
	}
}
