package com.jagex.game.runetek5.graphics.model.particle

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!lh")
class ParticleBinaryTree {

	@OriginalMember(owner = "client!lh", name = "n", descriptor = "Lclient!taa;")
	private var cursor: ParticleNode? = null

	@OriginalMember(owner = "client!lh", name = "l", descriptor = "Lclient!taa;")
	@JvmField
	val current: ParticleNode = ParticleNode()

	@OriginalMember(owner = "client!lh", name = "<init>", descriptor = "()V")
	constructor() {
		this.current.prev2 = this.current
		this.current.next2 = this.current
	}

	@OriginalMember(owner = "client!lh", name = "a", descriptor = "(I)I")
	fun size(): Int {
		@Pc(7) var size: Int = 0
		@Pc(11) var node: ParticleNode? = this.current.next2
		while (this.current !== node) {
			node = node!!.next2
			size++
		}
		return size
	}

	@OriginalMember(owner = "client!lh", name = "a", descriptor = "(Lclient!taa;Z)V")
	fun insert(@OriginalArg(0) node2: ParticleNode?) {
		if (node2!!.prev2 != null) {
			node2.unlink2()
		}
		node2.next2 = this.current
		node2.prev2 = this.current.prev2
		node2.prev2!!.next2 = node2
		node2.next2!!.prev2 = node2
	}

	@OriginalMember(owner = "client!lh", name = "b", descriptor = "(B)V")
	fun clear() {
		while (true) {
			@Pc(5) val node: ParticleNode? = this.current.next2
			if (node === this.current) {
				this.cursor = null
				return
			}
			node!!.unlink2()
		}
	}

	@OriginalMember(owner = "client!lh", name = "c", descriptor = "(B)Lclient!taa;")
	fun next(): ParticleNode? {
		@Pc(6) val node2: ParticleNode? = this.cursor
		if (node2 === this.current) {
			this.cursor = null
			return null
		} else {
			this.cursor = node2!!.next2
			return node2
		}
	}

	@OriginalMember(owner = "client!lh", name = "c", descriptor = "(I)Lclient!taa;")
	fun first(): ParticleNode? {
		@Pc(12) val node2: ParticleNode? = this.current.next2
		if (node2 === this.current) {
			this.cursor = null
			return null
		} else {
			this.cursor = node2!!.next2
			return node2
		}
	}
}
