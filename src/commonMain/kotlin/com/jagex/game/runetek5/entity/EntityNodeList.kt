package com.jagex.game.runetek5.entity

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!tca")
class EntityNodeList {

	@OriginalMember(owner = "client!tca", name = "k", descriptor = "Lclient!cm;")
	private var pointer: EntityNode? = null

	@OriginalMember(owner = "client!tca", name = "j", descriptor = "Lclient!cm;")
	private val head: EntityNode = EntityNode()

	@OriginalMember(owner = "client!tca", name = "<init>", descriptor = "()V")
	constructor() {
		this.head.previous = this.head
		this.head.next = this.head
	}

	@OriginalMember(owner = "client!tca", name = "a", descriptor = "(ILclient!cm;)V")
	fun offer(@OriginalArg(1) node: EntityNode?) {
		if (node!!.previous != null) {
			node.unlinkNode()
		}
		node.previous = this.head.previous
		node.next = this.head
		node.previous!!.next = node
		node.next!!.previous = node
	}

	@OriginalMember(owner = "client!tca", name = "a", descriptor = "(I)Lclient!cm;")
	fun getLast(): EntityNode? {
		@Pc(13) val node: EntityNode? = this.head.previous
		if (this.head === node) {
			this.pointer = null
			return null
		} else {
			this.pointer = node!!.previous
			return node
		}
	}

	@OriginalMember(owner = "client!tca", name = "a", descriptor = "(B)Z")
	fun isEmpty(): Boolean {
		return this.head === this.head.next
	}

	@OriginalMember(owner = "client!tca", name = "b", descriptor = "(I)Lclient!cm;")
	fun getFirst(): EntityNode? {
		@Pc(7) val node: EntityNode? = this.head.next
		if (node === this.head) {
			this.pointer = null
			return null
		} else {
			this.pointer = node!!.next
			return node
		}
	}

	@OriginalMember(owner = "client!tca", name = "c", descriptor = "(I)I")
	fun size(): Int {
		@Pc(7) var count: Int = 0
		@Pc(11) var node: EntityNode? = this.head.next
		while (node !== this.head) {
			count++
			node = node!!.next
		}
		return count
	}

	@OriginalMember(owner = "client!tca", name = "d", descriptor = "(I)Lclient!cm;")
	fun removeFirst(): EntityNode? {
		@Pc(7) val node: EntityNode? = this.head.next
		if (node === this.head) {
			return null
		} else {
			node!!.unlinkNode()
			return node
		}
	}

	@OriginalMember(owner = "client!tca", name = "c", descriptor = "(B)V")
	fun clear() {
		while (true) {
			@Pc(13) val node: EntityNode? = this.head.next
			if (node === this.head) {
				this.pointer = null
				return
			}
			node!!.unlinkNode()
		}
	}

	@OriginalMember(owner = "client!tca", name = "d", descriptor = "(B)Lclient!cm;")
	fun getNext(): EntityNode? {
		@Pc(6) val node: EntityNode? = this.pointer
		if (node === this.head) {
			this.pointer = null
			return null
		} else {
			this.pointer = node!!.next
			return node
		}
	}
}
