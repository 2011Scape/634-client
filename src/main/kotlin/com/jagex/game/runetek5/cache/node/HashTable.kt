package com.jagex.game.runetek5.cache.node

import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ib")
class HashTable {

	@OriginalMember(owner = "client!ib", name = "g", descriptor = "J")
	private var searchKey: Long = 0L

	@OriginalMember(owner = "client!ib", name = "h", descriptor = "Lclient!vl;")
	private var searchPointer: CacheableNode? = null

	@OriginalMember(owner = "client!ib", name = "f", descriptor = "[Lclient!vl;")
	private val buckets: Array<CacheableNode?>

	@OriginalMember(owner = "client!ib", name = "b", descriptor = "I")
	private val bucketCount: Int

	@OriginalMember(owner = "client!ib", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) size: Int) {
		this.buckets = arrayOfNulls<CacheableNode>(size)
		this.bucketCount = size
		@Pc(10) var i: Int = 0
		while (i < size) {
			@Pc(20) val node2: CacheableNode = CacheableNode()
			this.buckets[i] = node2
			node2.secondaryNext = node2
			node2.secondaryPrev = node2
			i++
		}
	}

	@OriginalMember(owner = "client!ib", name = "a", descriptor = "(JB)Lclient!vl;")
	fun get(@OriginalArg(0) searchKey: Long): CacheableNode? {
		this.searchKey = searchKey
		@Pc(20) val node2: CacheableNode? = this.buckets[((this.bucketCount - 1).toLong() and searchKey).toInt()]
		this.searchPointer = node2!!.secondaryNext
		while (this.searchPointer !== node2) {
			if (searchKey == this.searchPointer!!.indexReference) {
				@Pc(39) val node22: CacheableNode? = this.searchPointer
				this.searchPointer = this.searchPointer!!.secondaryNext
				return node22
			}
			this.searchPointer = this.searchPointer!!.secondaryNext
		}
		this.searchPointer = null
		return null
	}

	@OriginalMember(owner = "client!ib", name = "a", descriptor = "(Lclient!vl;IJ)V")
	fun put(@OriginalArg(0) node2: CacheableNode?, @OriginalArg(2) indexReference: Long) {
		if (node2!!.secondaryPrev != null) {
			node2.unlinkSecondary()
		}
		@Pc(27) val node22: CacheableNode? = this.buckets[(indexReference and (this.bucketCount - 1).toLong()).toInt()]
		node2.secondaryPrev = node22!!.secondaryPrev
		node2.secondaryNext = node22
		node2.secondaryPrev!!.secondaryNext = node2
		node2.secondaryNext!!.secondaryPrev = node2
		node2.indexReference = indexReference
	}

	@OriginalMember(owner = "client!ib", name = "a", descriptor = "(Z)Lclient!vl;")
	fun nextInBucket(): CacheableNode? {
		if (this.searchPointer == null) {
			return null
		}
		@Pc(28) val node2: CacheableNode? = this.buckets[(this.searchKey and (this.bucketCount - 1).toLong()).toInt()]
		while (node2 !== this.searchPointer) {
			if (this.searchPointer!!.indexReference == this.searchKey) {
				@Pc(44) val node22: CacheableNode? = this.searchPointer
				this.searchPointer = this.searchPointer!!.secondaryNext
				return node22
			}
			this.searchPointer = this.searchPointer!!.secondaryNext
		}
		this.searchPointer = null
		return null
	}
}
