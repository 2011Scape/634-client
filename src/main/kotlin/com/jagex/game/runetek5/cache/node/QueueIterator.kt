package com.jagex.game.runetek5.cache.node

import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!os")
class QueueIterator {

	@OriginalMember(owner = "client!os", name = "e", descriptor = "Lclient!vl;")
	private var node: CacheableNode? = null

	@OriginalMember(owner = "client!os", name = "h", descriptor = "Lclient!mn;")
	private var queue: Queue? = null

	@OriginalMember(owner = "client!os", name = "<init>", descriptor = "()V")
	private constructor() {
	}

	@OriginalMember(owner = "client!os", name = "<init>", descriptor = "(Lclient!mn;)V")
	constructor(@OriginalArg(0) queue: Queue?) {
		this.queue = queue
	}

	@OriginalMember(owner = "client!os", name = "b", descriptor = "(I)Lclient!vl;")
	fun first(): CacheableNode? {
		@Pc(15) val node2: CacheableNode? = this.queue!!.sentinel.secondaryNext
		if (node2 === this.queue!!.sentinel) {
			this.node = null
			return null
		} else {
			this.node = node2!!.secondaryNext
			return node2
		}
	}

	@OriginalMember(owner = "client!os", name = "a", descriptor = "(B)Lclient!vl;")
	fun next(): CacheableNode? {
		@Pc(11) val node2: CacheableNode? = this.node
		if (node2 === this.queue!!.sentinel) {
			this.node = null
			return null
		} else {
			this.node = node2!!.secondaryNext
			return node2
		}
	}
}
