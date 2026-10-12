package com.jagex.game.runetek5.core.datastruct.key

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!cga")
class DequeIterator {

	@OriginalMember(owner = "client!cga", name = "a", descriptor = "Lclient!eh;")
	private var next: Node? = null

	@OriginalMember(owner = "client!cga", name = "b", descriptor = "Lclient!uh;")
	private var deque: Deque? = null

	@OriginalMember(owner = "client!cga", name = "<init>", descriptor = "()V")
	constructor() {
	}

	@OriginalMember(owner = "client!cga", name = "<init>", descriptor = "(Lclient!uh;)V")
	constructor(@OriginalArg(0) deque: Deque?) {
		this.deque = deque
	}

	@OriginalMember(owner = "client!cga", name = "a", descriptor = "(B)Lclient!eh;")
	fun first(): Node? {
		@Pc(8) val node: Node? = this.deque!!.sentinel.next
		if (this.deque!!.sentinel === node) {
			this.next = null
			return null
		} else {
			this.next = node!!.next
			return node
		}
	}

	@OriginalMember(owner = "client!cga", name = "a", descriptor = "(Lclient!uh;I)V")
	fun setDeque(@OriginalArg(0) deque: Deque?) {
		this.deque = deque
	}

	@OriginalMember(owner = "client!cga", name = "a", descriptor = "(I)Lclient!eh;")
	fun next(): Node? {
		@Pc(11) val node: Node? = this.next
		if (this.deque!!.sentinel === node) {
			this.next = null
			return null
		} else {
			this.next = node!!.next
			return node
		}
	}
}
