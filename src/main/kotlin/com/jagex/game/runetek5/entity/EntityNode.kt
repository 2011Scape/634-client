package com.jagex.game.runetek5.entity

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import org.openrs2.deob.annotation.OriginalArg

@OriginalClass("client!cm")
open class EntityNode {

	@OriginalMember(owner = "client!cm", name = "a", descriptor = "Lclient!cm;")
	@JvmField
	var next: EntityNode? = null

	@OriginalMember(owner = "client!cm", name = "g", descriptor = "Lclient!cm;")
	@JvmField
	var previous: EntityNode? = null

	@OriginalMember(owner = "client!cm", name = "a", descriptor = "(Z)V")
	fun unlinkNode() {
		if (this.previous != null) {
			this.previous!!.next = this.next
			this.next!!.previous = this.previous
			this.next = null
			this.previous = null
		}
	}

	companion object {
		@OriginalMember(owner = "client!cm", name = "e", descriptor = "[Lclient!tca;")
		@JvmField
		val PICKABLE_ENTITY_POOLS: Array<EntityNodeList?> = arrayOfNulls<EntityNodeList>(5)

		init {
			@Pc(4) var i: Int = 0
			while (i < PICKABLE_ENTITY_POOLS.size) {
				PICKABLE_ENTITY_POOLS[i] = EntityNodeList()
				i++
			}
		}

		@OriginalMember(owner = "client!fga", name = "a", descriptor = "(I)V")
		@JvmStatic
		fun resetPickableEntityPools() {
			@Pc(3) val linkedLists: Array<EntityNodeList?> = EntityNode.PICKABLE_ENTITY_POOLS
			synchronized(EntityNode.PICKABLE_ENTITY_POOLS) {
				@Pc(7) var i: Int = 0
				while (i < EntityNode.PICKABLE_ENTITY_POOLS.size) {
					EntityNode.PICKABLE_ENTITY_POOLS[i] = EntityNodeList()
					EntityNode.PICKABLE_ENTITY_POOL_SIZES[i] = 0
					i++
				}
			}
		}

		@OriginalMember(owner = "client!dia", name = "a", descriptor = "[I")
		@JvmField
		val PICKABLE_ENTITY_POOL_SIZES: IntArray = IntArray(5)

		@OriginalMember(owner = "client!te", name = "a", descriptor = "(Lclient!cm;Lclient!cm;Z)V")
		@JvmStatic
		fun insertBefore(@OriginalArg(0) node: EntityNode?, @OriginalArg(1) node2: EntityNode?) {
			if (node2!!.previous != null) {
				node2.unlinkNode()
			}
			node2.next = node
			node2.previous = node!!.previous
			node2.previous!!.next = node2
			node2.next!!.previous = node2
		}
	}
}
