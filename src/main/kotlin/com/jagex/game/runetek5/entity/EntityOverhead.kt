package com.jagex.game.runetek5.entity

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.EntityOverheadAllocator
import org.openrs2.deob.annotation.OriginalArg

@OriginalClass("client!ql")
class EntityOverhead : EntityNode() {

	@OriginalMember(owner = "client!ql", name = "j", descriptor = "I")
	@JvmField
	var maxY: Int = 0

	@OriginalMember(owner = "client!ql", name = "k", descriptor = "I")
	@JvmField
	var maxX: Int = 0

	@OriginalMember(owner = "client!ql", name = "l", descriptor = "I")
	@JvmField
	var minY: Int = 0

	@OriginalMember(owner = "client!ql", name = "n", descriptor = "I")
	@JvmField
	var minX: Int = 0

	@OriginalMember(owner = "client!ql", name = "p", descriptor = "Lclient!kda;")
	@JvmField
	var pathingEntity: PathingEntity? = null

	companion object {
		@OriginalMember(owner = "client!lf", name = "a", descriptor = "(Lclient!ql;Z)V")
		@JvmStatic
		fun release(@OriginalArg(0) node: EntityOverhead?) {
			node!!.pathingEntity = null
			if (EntityOverheadAllocator.poolSize < 20) {
				EntityOverheadAllocator.aClass325_3.offer(node)
				EntityOverheadAllocator.poolSize++
			}
		}
	}
}
