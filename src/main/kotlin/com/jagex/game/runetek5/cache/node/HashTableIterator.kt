package com.jagex.game.runetek5.cache.node

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!aga")
class HashTableIterator {

	@OriginalMember(owner = "client!aga", name = "c", descriptor = "Lclient!eq;")
	private var table: IterableHashTable? = null

	@OriginalMember(owner = "client!aga", name = "g", descriptor = "Lclient!eh;")
	private var pointer: Node? = null

	@OriginalMember(owner = "client!aga", name = "e", descriptor = "I")
	private var bucket: Int = 0

	@OriginalMember(owner = "client!aga", name = "<init>", descriptor = "()V")
	private constructor() {
	}

	@OriginalMember(owner = "client!aga", name = "<init>", descriptor = "(Lclient!eq;)V")
	constructor(@OriginalArg(0) iterableHashTable: IterableHashTable?) {
		this.table = iterableHashTable
	}

	@OriginalMember(owner = "client!aga", name = "a", descriptor = "(B)Lclient!eh;")
	fun first(): Node? {
		this.bucket = 0
		return this.next()
	}

	@OriginalMember(owner = "client!aga", name = "b", descriptor = "(B)Lclient!eh;")
	fun next(): Node? {
		@Pc(28) var node: Node?
		if (this.bucket > 0 && this.table!!.buckets[this.bucket - 1] !== this.pointer) {
			node = this.pointer
			this.pointer = node!!.next
			return node
		}
		while (this.table!!.bucketCount > this.bucket) {
			node = this.table!!.buckets[this.bucket++]!!.next
			if (this.table!!.buckets[this.bucket - 1] !== node) {
				this.pointer = node!!.next
				return node
			}
		}
		return null
	}
}
