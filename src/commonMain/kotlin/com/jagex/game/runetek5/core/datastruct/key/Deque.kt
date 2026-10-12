package com.jagex.game.runetek5.core.datastruct.key

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!uh")
class Deque {

	@OriginalMember(owner = "client!uh", name = "t", descriptor = "Lclient!eh;")
	private var pointer: Node? = null

	@OriginalMember(owner = "client!uh", name = "l", descriptor = "Lclient!eh;")
	@JvmField
	val sentinel: Node = Node()

	@OriginalMember(owner = "client!uh", name = "<init>", descriptor = "()V")
	constructor() {
		this.sentinel.previous = this.sentinel
		this.sentinel.next = this.sentinel
	}

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(Lclient!uh;B)V")
	fun transferAllTo(@OriginalArg(0) deque: Deque?) {
		this.spliceInto(this.sentinel.next, deque)
	}

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(Lclient!eh;ZLclient!uh;)V")
	private fun spliceInto(@OriginalArg(0) node: Node?, @OriginalArg(2) deque: Deque?) {
		@Pc(7) val node2: Node? = this.sentinel.previous
		this.sentinel.previous = node!!.previous
		node.previous!!.next = this.sentinel
		if (this.sentinel !== node) {
			node.previous = deque!!.sentinel.previous
			node.previous!!.next = node
			deque.sentinel.previous = node2
			node2!!.next = deque.sentinel
		}
	}

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(B)Lclient!eh;")
	fun next(): Node? {
		@Pc(12) val node: Node? = this.pointer
		if (this.sentinel === node) {
			this.pointer = null
			return null
		} else {
			this.pointer = node!!.next
			return node
		}
	}

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(I)Lclient!eh;")
	fun last(): Node? {
		@Pc(7) val node: Node? = this.sentinel.previous
		if (this.sentinel === node) {
			this.pointer = null
			return null
		} else {
			this.pointer = node!!.previous
			return node
		}
	}

	@OriginalMember(owner = "client!uh", name = "c", descriptor = "(I)Lclient!eh;")
	fun first(): Node? {
		@Pc(14) val node: Node? = this.sentinel.next
		if (node === this.sentinel) {
			this.pointer = null
			return null
		} else {
			this.pointer = node!!.next
			return node
		}
	}

	@OriginalMember(owner = "client!uh", name = "d", descriptor = "(I)V")
	fun clear() {
		while (true) {
			@Pc(13) val current: Node? = this.sentinel.next
			if (this.sentinel === current) {
				this.pointer = null
				return
			}
			current!!.unlink()
		}
	}

	@OriginalMember(owner = "client!uh", name = "e", descriptor = "(I)Lclient!eh;")
	fun removeFirst(): Node? {
		@Pc(12) val node: Node? = this.sentinel.next
		if (this.sentinel === node) {
			return null
		} else {
			node!!.unlink()
			return node
		}
	}

	@OriginalMember(owner = "client!uh", name = "f", descriptor = "(I)I")
	fun size(): Int {
		@Pc(7) var size: Int = 0
		@Pc(11) var node: Node? = this.sentinel.next
		while (node !== this.sentinel) {
			node = node!!.next
			size++
		}
		return size
	}

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(Lclient!eh;I)V")
	fun addLast(@OriginalArg(0) node: Node?) {
		if (node!!.previous != null) {
			node.unlink()
		}
		node.next = this.sentinel
		node.previous = this.sentinel.previous
		node.previous!!.next = node
		node.next!!.previous = node
	}

	@OriginalMember(owner = "client!uh", name = "b", descriptor = "(Lclient!eh;I)V")
	fun addFirst(@OriginalArg(0) node: Node?) {
		if (node!!.previous != null) {
			node.unlink()
		}
		node.previous = this.sentinel
		node.next = this.sentinel.next
		node.previous!!.next = node
		node.next!!.previous = node
	}

	@OriginalMember(owner = "client!uh", name = "b", descriptor = "(B)Z")
	fun isEmpty(): Boolean {
		return this.sentinel === this.sentinel.next
	}

	@OriginalMember(owner = "client!uh", name = "g", descriptor = "(I)Lclient!eh;")
	fun previous(): Node? {
		@Pc(6) val node: Node? = this.pointer
		if (node === this.sentinel) {
			this.pointer = null
			return null
		} else {
			this.pointer = node!!.previous
			return node
		}
	}
}
