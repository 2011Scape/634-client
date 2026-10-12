package com.jagex.game.runetek5.core.datastruct.key

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!eq")
class IterableHashTable {

	@OriginalMember(owner = "client!eq", name = "l", descriptor = "J")
	private var searchKey: Long = 0L

	@OriginalMember(owner = "client!eq", name = "p", descriptor = "Lclient!eh;")
	private var searchPointer: Node? = null

	@OriginalMember(owner = "client!eq", name = "q", descriptor = "Lclient!eh;")
	private var next: Node? = null

	@OriginalMember(owner = "client!eq", name = "r", descriptor = "I")
	private var iteratorPosition: Int = 0

	@OriginalMember(owner = "client!eq", name = "d", descriptor = "I")
	@JvmField
	val bucketCount: Int

	@OriginalMember(owner = "client!eq", name = "a", descriptor = "[Lclient!eh;")
	@JvmField
	val buckets: Array<Node?>

	@OriginalMember(owner = "client!eq", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) bucketCount: Int) {
		this.bucketCount = bucketCount
		this.buckets = arrayOfNulls<Node>(bucketCount)
		@Pc(13) var i: Int = 0
		while (i < bucketCount) {
			@Pc(23) val node: Node = Node()
			this.buckets[i] = node
			node.next = node
			node.previous = node
			i++
		}
	}

	@OriginalMember(owner = "client!eq", name = "a", descriptor = "(I)I")
	fun size(): Int {
		@Pc(13) var n: Int = 0
		@Pc(15) var i: Int = 0
		while (i < this.bucketCount) {
			@Pc(22) val bucket: Node? = this.buckets[i]
			@Pc(25) var current: Node? = bucket!!.next
			while (current !== bucket) {
				n++
				current = current!!.next
			}
			i++
		}
		return n
	}

	@OriginalMember(owner = "client!eq", name = "a", descriptor = "(Z)I")
	fun getBucketCount(): Int {
		return this.bucketCount
	}

	@OriginalMember(owner = "client!eq", name = "b", descriptor = "(Z)Lclient!eh;")
	fun nextWithKey(): Node? {
		if (this.searchPointer == null) {
			return null
		}
		@Pc(23) val node: Node? = this.buckets[((this.bucketCount - 1).toLong() and this.searchKey).toInt()]
		while (this.searchPointer !== node) {
			if (this.searchPointer!!.key == this.searchKey) {
				@Pc(41) val node2: Node? = this.searchPointer
				this.searchPointer = this.searchPointer!!.next
				return node2
			}
			this.searchPointer = this.searchPointer!!.next
		}
		this.searchPointer = null
		return null
	}

	@OriginalMember(owner = "client!eq", name = "a", descriptor = "(I[Lclient!eh;)I")
	fun toArray(@OriginalArg(1) nodes: Array<Node?>?): Int {
		@Pc(12) var count: Int = 0
		@Pc(14) var i: Int = 0
		while (i < this.bucketCount) {
			@Pc(21) val node: Node? = this.buckets[i]
			@Pc(24) var node2: Node? = node!!.next
			while (node2 !== node) {
				nodes!![count++] = node2
				node2 = node2!!.next
			}
			i++
		}
		return count
	}

	@OriginalMember(owner = "client!eq", name = "a", descriptor = "(JI)Lclient!eh;")
	fun get(@OriginalArg(0) searchKey: Long): Node? {
		this.searchKey = searchKey
		@Pc(20) val node: Node? = this.buckets[(searchKey and (this.bucketCount - 1).toLong()).toInt()]
		this.searchPointer = node!!.next
		while (this.searchPointer !== node) {
			if (this.searchPointer!!.key == searchKey) {
				@Pc(46) val node2: Node? = this.searchPointer
				this.searchPointer = this.searchPointer!!.next
				return node2
			}
			this.searchPointer = this.searchPointer!!.next
		}
		this.searchPointer = null
		return null
	}

	@OriginalMember(owner = "client!eq", name = "c", descriptor = "(I)V")
	fun clear() {
		@Pc(7) var i: Int = 0
		while (i < this.bucketCount) {
			@Pc(14) val bucket: Node? = this.buckets[i]
			while (true) {
				@Pc(17) val current: Node? = bucket!!.next
				if (current === bucket) {
					break
				}
				current!!.unlink()
			}
			i++
		}
		this.searchPointer = null
		this.next = null
	}

	@OriginalMember(owner = "client!eq", name = "d", descriptor = "(I)Lclient!eh;")
	fun next(): Node? {
		@Pc(20) var node: Node?
		if (this.iteratorPosition > 0 && this.next !== this.buckets[this.iteratorPosition - 1]) {
			node = this.next
			this.next = node!!.next
			return node
		}
		while (this.iteratorPosition < this.bucketCount) {
			node = this.buckets[this.iteratorPosition++]!!.next
			if (this.buckets[this.iteratorPosition - 1] !== node) {
				this.next = node!!.next
				return node
			}
		}
		return null
	}

	@OriginalMember(owner = "client!eq", name = "a", descriptor = "(BJLclient!eh;)V")
	fun put(@OriginalArg(1) pointer: Long, @OriginalArg(2) node: Node?) {
		if (node!!.previous != null) {
			node.unlink()
		}
		@Pc(27) val node2: Node? = this.buckets[(pointer and (this.bucketCount - 1).toLong()).toInt()]
		node.next = node2
		node.previous = node2!!.previous
		node.previous!!.next = node
		node.next!!.previous = node
		node.key = pointer
	}

	@OriginalMember(owner = "client!eq", name = "e", descriptor = "(I)Lclient!eh;")
	fun first(): Node? {
		this.iteratorPosition = 0
		return this.next()
	}
}
