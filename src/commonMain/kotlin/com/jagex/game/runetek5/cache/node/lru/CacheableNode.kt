package com.jagex.game.runetek5.cache.node.lru

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.OriginalArg
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!vl")
open class CacheableNode : Node() {

	@OriginalMember(owner = "client!vl", name = "o", descriptor = "J")
	@JvmField
	var indexReference: Long = 0L

	@OriginalMember(owner = "client!vl", name = "r", descriptor = "Lclient!vl;")
	@JvmField
	var secondaryPrev: CacheableNode? = null

	@OriginalMember(owner = "client!vl", name = "u", descriptor = "Lclient!vl;")
	@JvmField
	var secondaryNext: CacheableNode? = null

	@OriginalMember(owner = "client!vl", name = "a", descriptor = "(Z)V")
	fun unlinkSecondary() {
		if (this.secondaryPrev != null) {
			this.secondaryPrev!!.secondaryNext = this.secondaryNext
			this.secondaryNext!!.secondaryPrev = this.secondaryPrev
			this.secondaryPrev = null
			this.secondaryNext = null
		}
	}

	@OriginalMember(owner = "client!vl", name = "b", descriptor = "(B)Z")
	fun islinkedSecondary(): Boolean {
		return this.secondaryPrev != null
	}

	companion object {
		@OriginalMember(owner = "client!wd", name = "a", descriptor = "(Lclient!vl;Lclient!vl;B)V")
		@JvmStatic
		fun insertSecondaryAfter(@OriginalArg(0) node2: CacheableNode?, @OriginalArg(1) node22: CacheableNode?) {
			if (node2!!.secondaryPrev != null) {
				node2.unlinkSecondary()
			}
			node2.secondaryPrev = node22
			node2.secondaryNext = node22!!.secondaryNext
			node2.secondaryPrev!!.secondaryNext = node2
			node2.secondaryNext!!.secondaryPrev = node2
		}

		@OriginalMember(owner = "client!kea", name = "a", descriptor = "(Lclient!vl;Lclient!vl;I)V")
		@JvmStatic
		fun insertSecondaryBefore(@OriginalArg(0) node2: CacheableNode?, @OriginalArg(1) node22: CacheableNode?) {
			if (node22!!.secondaryPrev != null) {
				node22.unlinkSecondary()
			}
			node22.secondaryNext = node2
			node22.secondaryPrev = node2!!.secondaryPrev
			node22.secondaryPrev!!.secondaryNext = node22
			node22.secondaryNext!!.secondaryPrev = node22
		}
	}
}
