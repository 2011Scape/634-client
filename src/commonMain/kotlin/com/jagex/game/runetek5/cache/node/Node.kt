package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!eh")
open class Node {

	@OriginalMember(owner = "client!eh", name = "g", descriptor = "J")
	@JvmField
	var key: Long = 0L

	@OriginalMember(owner = "client!eh", name = "j", descriptor = "Lclient!eh;")
	@JvmField
	var next: Node? = null

	@OriginalMember(owner = "client!eh", name = "k", descriptor = "Lclient!eh;")
	@JvmField
	var previous: Node? = null

	@OriginalMember(owner = "client!eh", name = "c", descriptor = "(B)Z")
	fun isLinked(): Boolean {
		return this.previous != null
	}

	@OriginalMember(owner = "client!eh", name = "d", descriptor = "(B)V")
	fun unlink() {
		if (this.previous != null) {
			this.previous!!.next = this.next
			this.next!!.previous = this.previous
			this.next = null
			this.previous = null
		}
	}

	companion object {
		@OriginalMember(owner = "client!qh", name = "a", descriptor = "(ILclient!eh;Lclient!eh;)V")
		@JvmStatic
		fun insertBefore(@OriginalArg(1) node: Node?, @OriginalArg(2) node2: Node?) {
			if (node!!.previous != null) {
				node.unlink()
			}
			node.previous = node2!!.previous
			node.next = node2
			node.previous!!.next = node
			node.next!!.previous = node
		}
	}
}
