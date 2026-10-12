package com.jagex.game.runetek5.cache.node

import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!mn")
class Queue {

	@OriginalMember(owner = "client!mn", name = "k", descriptor = "Lclient!vl;")
	private var pointer: CacheableNode? = null

	@OriginalMember(owner = "client!mn", name = "f", descriptor = "Lclient!vl;")
	@JvmField
	val sentinel: CacheableNode = CacheableNode()

	@OriginalMember(owner = "client!mn", name = "<init>", descriptor = "()V")
	constructor() {
		this.sentinel.secondaryPrev = this.sentinel
		this.sentinel.secondaryNext = this.sentinel
	}

	@OriginalMember(owner = "client!mn", name = "a", descriptor = "(I)I")
	fun size(): Int {
		@Pc(7) var size: Int = 0
		@Pc(11) var current: CacheableNode? = this.sentinel.secondaryNext
		while (current !== this.sentinel) {
			size++
			current = current!!.secondaryNext
		}
		return size
	}

	@OriginalMember(owner = "client!mn", name = "a", descriptor = "(B)Lclient!vl;")
	fun next(): CacheableNode? {
		@Pc(11) val node2: CacheableNode? = this.pointer
		if (node2 === this.sentinel) {
			this.pointer = null
			return null
		} else {
			this.pointer = node2!!.secondaryNext
			return node2
		}
	}

	@OriginalMember(owner = "client!mn", name = "a", descriptor = "(ZLclient!vl;)V")
	fun insert(@OriginalArg(1) node2: CacheableNode?) {
		if (node2!!.secondaryPrev != null) {
			node2.unlinkSecondary()
		}
		node2.secondaryNext = this.sentinel
		node2.secondaryPrev = this.sentinel.secondaryPrev
		node2.secondaryPrev!!.secondaryNext = node2
		node2.secondaryNext!!.secondaryPrev = node2
	}

	@OriginalMember(owner = "client!mn", name = "b", descriptor = "(I)Lclient!vl;")
	fun removeFirst(): CacheableNode? {
		@Pc(13) val node2: CacheableNode? = this.sentinel.secondaryNext
		if (node2 === this.sentinel) {
			return null
		} else {
			node2!!.unlinkSecondary()
			return node2
		}
	}

	@OriginalMember(owner = "client!mn", name = "c", descriptor = "(I)V")
	fun clear() {
		while (true) {
			@Pc(13) val current: CacheableNode? = this.sentinel.secondaryNext
			if (this.sentinel === current) {
				this.pointer = null
				return
			}
			current!!.unlinkSecondary()
		}
	}

	@OriginalMember(owner = "client!mn", name = "d", descriptor = "(I)Lclient!vl;")
	fun first(): CacheableNode? {
		@Pc(13) val node2: CacheableNode? = this.sentinel.secondaryNext
		if (node2 === this.sentinel) {
			this.pointer = null
			return null
		} else {
			this.pointer = node2!!.secondaryNext
			return node2
		}
	}
}
