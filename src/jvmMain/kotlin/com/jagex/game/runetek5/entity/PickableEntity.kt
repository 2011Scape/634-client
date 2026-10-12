package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!jda")
class PickableEntity : EntityNode() {

	@OriginalMember(owner = "client!jda", name = "k", descriptor = "Z")
	@JvmField
	var interactive: Boolean = false

	@OriginalMember(owner = "client!jda", name = "l", descriptor = "Lclient!ga;")
	@JvmField
	var entity: Entity? = null

	@OriginalMember(owner = "client!jda", name = "p", descriptor = "[Lclient!iw;")
	@JvmField
	var boundingBoxPolygons: Array<BoundingBoxPolygon?>? = null

	@OriginalMember(owner = "client!jda", name = "a", descriptor = "(IILclient!ha;I)Z")
	fun hovered(@OriginalArg(0) mouseY: Int, @OriginalArg(1) mouseX: Int, @OriginalArg(2) toolkit: Toolkit?): Boolean {
		@Pc(17) val pickSizeShift: Int = this.entity!!.getPickSizeShift()
		if (this.boundingBoxPolygons != null) {
			@Pc(22) var i: Int = 0
			while (i < this.boundingBoxPolygons!!.size) {
				this.boundingBoxPolygons!![i]!!.size = this.boundingBoxPolygons!![i]!!.size shl pickSizeShift
				if (this.boundingBoxPolygons!![i]!!.hovered(mouseX, mouseY) && this.entity!!.picked(toolkit, mouseY, mouseX)) {
					this.boundingBoxPolygons!![i]!!.size = this.boundingBoxPolygons!![i]!!.size shr pickSizeShift
					return true
				}
				this.boundingBoxPolygons!![i]!!.size = this.boundingBoxPolygons!![i]!!.size shr pickSizeShift
				i++
			}
		}
		return false
	}

	companion object {
		@OriginalMember(owner = "client!tk", name = "a", descriptor = "(IZZ)Lclient!jda;")
		@JvmStatic
		fun obtain(@OriginalArg(0) length: Int, @OriginalArg(1) interactive: Boolean): PickableEntity {
			@Pc(7) val linkedLists: Array<EntityNodeList?> = EntityNode.PICKABLE_ENTITY_POOLS
			synchronized(EntityNode.PICKABLE_ENTITY_POOLS) {
				@Pc(26) val pickableEntity: PickableEntity
				if (EntityNode.PICKABLE_ENTITY_POOLS.size <= length || EntityNode.PICKABLE_ENTITY_POOLS[length]!!.isEmpty()) {
					pickableEntity = PickableEntity()
					pickableEntity.boundingBoxPolygons = arrayOfNulls<BoundingBoxPolygon>(length)
					@Pc(48) var i: Int = 0
					while (i < length) {
						pickableEntity.boundingBoxPolygons!![i] = BoundingBoxPolygon()
						i++
					}
				} else {
					pickableEntity = EntityNode.PICKABLE_ENTITY_POOLS[length]!!.getLast() as PickableEntity
					pickableEntity.unlinkNode()
					@Pc(33) val poolCount: Int = EntityNode.PICKABLE_ENTITY_POOL_SIZES[length]--
				}
				pickableEntity.interactive = interactive
				return pickableEntity
			}
		}

		@OriginalMember(owner = "client!hha", name = "a", descriptor = "(Lclient!jda;I)V")
		@JvmStatic
		fun recycle(@OriginalArg(0) pickableEntity: PickableEntity?) {
			pickableEntity!!.entity = null
			@Pc(12) val length: Int = pickableEntity.boundingBoxPolygons!!.size
			@Pc(22) var i: Int = 0
			while (i < length) {
				pickableEntity.boundingBoxPolygons!![i]!!.positionCalculated = false
				i++
			}
			@Pc(40) val linkedLists: Array<EntityNodeList?> = EntityNode.PICKABLE_ENTITY_POOLS
			synchronized(EntityNode.PICKABLE_ENTITY_POOLS) {
				if (length < EntityNode.PICKABLE_ENTITY_POOLS.size && EntityNode.PICKABLE_ENTITY_POOL_SIZES[length] < 200) {
					EntityNode.PICKABLE_ENTITY_POOLS[length]!!.offer(pickableEntity)
					@Pc(67) val previousCount: Int = EntityNode.PICKABLE_ENTITY_POOL_SIZES[length]++
				}
			}
		}
	}
}
