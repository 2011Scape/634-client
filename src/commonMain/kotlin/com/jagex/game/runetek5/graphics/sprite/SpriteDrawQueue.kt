package com.jagex.game.runetek5.graphics.sprite

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Queue
import com.jagex.game.runetek5.entity.ComparableGraphNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.impl.KeyedHardReferenceNode
import com.jagex.game.runetek5.cache.node.impl.SpriteRenderNode
import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import com.jagex.game.runetek5.graphics.texture.water.KeyedReferenceFactory
import kotlin.jvm.JvmField

@OriginalClass("client!pq")
class SpriteDrawQueue {

	@OriginalMember(owner = "client!pq", name = "g", descriptor = "Lclient!mn;")
	private val spriteDrawQueue: Queue = Queue()

	@OriginalMember(owner = "client!pq", name = "b", descriptor = "I")
	private var currentCapacity: Int = 0

	@OriginalMember(owner = "client!pq", name = "o", descriptor = "I")
	private val maxCapacity: Int

	@OriginalMember(owner = "client!pq", name = "c", descriptor = "Lclient!eq;")
	private val spriteRenderNodes: IterableHashTable

	@OriginalMember(owner = "client!pq", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) capacity: Int) {
		this.currentCapacity = capacity
		this.maxCapacity = capacity
		@Pc(16) var bucketCount: Int
		bucketCount = 1
		while (bucketCount + bucketCount < capacity) {
			bucketCount += bucketCount
		}
		this.spriteRenderNodes = IterableHashTable(bucketCount)
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(ILclient!we;)V")
	private fun discard(@OriginalArg(1) cacheKey: ComparableGraphNode?) {
		@Pc(9) val uid: Long = cacheKey!!.toLong()
		@Pc(16) var keyedReferenceNode: SpriteRenderNode? = this.spriteRenderNodes.get(uid) as SpriteRenderNode?
		while (keyedReferenceNode != null) {
			if (keyedReferenceNode.cacheKey!!.matches(cacheKey)) {
				this.discardNode(keyedReferenceNode)
				break
			}
			keyedReferenceNode = this.spriteRenderNodes.nextWithKey() as SpriteRenderNode?
		}
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(I)I")
	fun capacity(): Int {
		return this.maxCapacity
	}

	@OriginalMember(owner = "client!pq", name = "b", descriptor = "(ILclient!we;)Ljava/lang/Object;")
	fun get(@OriginalArg(1) cacheKey: ComparableGraphNode?): Any? {
		@Pc(9) val uid: Long = cacheKey!!.toLong()
		@Pc(16) var keyedReferenceNode: SpriteRenderNode? = this.spriteRenderNodes.get(uid) as SpriteRenderNode?
		while (keyedReferenceNode != null) {
			if (keyedReferenceNode.cacheKey!!.matches(cacheKey)) {
				@Pc(28) val obj: Any? = keyedReferenceNode.get()
				if (obj != null) {
					if (keyedReferenceNode.isSoft()) {
						@Pc(57) val keyedHardReferenceNode: KeyedHardReferenceNode = KeyedHardReferenceNode(cacheKey, obj, keyedReferenceNode.size)
						this.spriteRenderNodes.put(keyedReferenceNode.key, keyedHardReferenceNode)
						this.spriteDrawQueue.insert(keyedHardReferenceNode)
						keyedHardReferenceNode.indexReference = 0L
						keyedReferenceNode.unlink()
						keyedReferenceNode.unlinkSecondary()
					} else {
						this.spriteDrawQueue.insert(keyedReferenceNode)
						keyedReferenceNode.indexReference = 0L
					}
					return obj
				}
				keyedReferenceNode.unlink()
				keyedReferenceNode.unlinkSecondary()
				this.currentCapacity += keyedReferenceNode.size
			}
			keyedReferenceNode = this.spriteRenderNodes.nextWithKey() as SpriteRenderNode?
		}
		return null
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(Ljava/lang/Object;Lclient!we;II)V")
	private fun insert(@OriginalArg(0) obj: Any?, @OriginalArg(1) cacheKey: ComparableGraphNode?) {
		if (this.maxCapacity < 1) {
			throw IllegalStateException("s>cs")
		}
		this.discard(cacheKey)
		this.currentCapacity--
		while (this.currentCapacity < 0) {
			@Pc(40) val keyedReferenceNode: SpriteRenderNode? = this.spriteDrawQueue.removeFirst() as SpriteRenderNode?
			this.discardNode(keyedReferenceNode)
		}
		@Pc(54) val keyedHardReferenceNode: KeyedHardReferenceNode = KeyedHardReferenceNode(cacheKey, obj, 1)
		this.spriteRenderNodes.put(cacheKey!!.toLong(), keyedHardReferenceNode)
		this.spriteDrawQueue.insert(keyedHardReferenceNode)
		keyedHardReferenceNode.indexReference = 0L
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(Lclient!rd;B)V")
	private fun discardNode(@OriginalArg(0) keyedReferenceNode: SpriteRenderNode?) {
		if (keyedReferenceNode != null) {
			keyedReferenceNode.unlink()
			keyedReferenceNode.unlinkSecondary()
			this.currentCapacity += keyedReferenceNode.size
		}
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(B)V")
	fun removeSoftReferences() {
		@Pc(11) var node: SpriteRenderNode? = this.spriteDrawQueue.first() as SpriteRenderNode?
		while (node != null) {
			if (node.isSoft()) {
				node.unlink()
				node.unlinkSecondary()
				this.currentCapacity += node.size
			}
			node = this.spriteDrawQueue.next() as SpriteRenderNode?
		}
	}

	@OriginalMember(owner = "client!pq", name = "b", descriptor = "(B)V")
	fun reset() {
		this.spriteDrawQueue.clear()
		this.spriteRenderNodes.clear()
		this.currentCapacity = this.maxCapacity
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(II)V")
	fun processCycle() {
		if (SpriteDrawQueue.aClass89_1 == null) {
			return
		}
		@Pc(13) var keyedReferenceNode: SpriteRenderNode? = this.spriteDrawQueue.first() as SpriteRenderNode?
		while (keyedReferenceNode != null) {
			if (keyedReferenceNode.isSoft()) {
				if (keyedReferenceNode.get() == null) {
					keyedReferenceNode.unlink()
					keyedReferenceNode.unlinkSecondary()
					this.currentCapacity += keyedReferenceNode.size
				}
			} else if (5L < ++keyedReferenceNode.indexReference) {
				@Pc(60) val keyedReferenceNode2: SpriteRenderNode? = SpriteDrawQueue.aClass89_1!!.createSoftReference(keyedReferenceNode)
				this.spriteRenderNodes.put(keyedReferenceNode.key, keyedReferenceNode2)
				CacheableNode.insertSecondaryAfter(keyedReferenceNode2, keyedReferenceNode)
				keyedReferenceNode.unlink()
				keyedReferenceNode.unlinkSecondary()
			}
			keyedReferenceNode = this.spriteDrawQueue.next() as SpriteRenderNode?
		}
	}

	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(ILjava/lang/Object;Lclient!we;)V")
	fun put(@OriginalArg(1) obj: Any?, @OriginalArg(2) cacheKey: ComparableGraphNode?) {
		this.insert(obj, cacheKey)
	}

	@OriginalMember(owner = "client!pq", name = "d", descriptor = "(B)I")
	fun remaining(): Int {
		return this.currentCapacity
	}

	companion object {
		@OriginalMember(owner = "client!mh", name = "a", descriptor = "Lclient!ef;")
		@JvmField
		val aClass89_1: KeyedReferenceFactory? = KeyedReferenceFactory.create()
	}
}
