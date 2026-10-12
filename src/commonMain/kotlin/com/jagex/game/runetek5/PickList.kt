package com.jagex.game.runetek5

import com.jagex.game.runetek5.entity.BoundingBoxPolygon
import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.entity.EntityNode
import com.jagex.game.runetek5.entity.EntityNodeList
import com.jagex.game.runetek5.entity.PickableEntity
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!ks")
class PickList @OriginalMember(owner = "client!ks", name = "<init>", descriptor = "(Z)V") constructor(@OriginalArg(0) retained: Boolean) {

	@OriginalMember(owner = "client!ks", name = "e", descriptor = "Lclient!tca;")
	@JvmField
	val entities: EntityNodeList = EntityNodeList()

	@OriginalMember(owner = "client!ks", name = "f", descriptor = "Z")
	@JvmField
	var retained: Boolean = false

	init {
		this.retained = retained
	}

	@OriginalMember(owner = "client!ks", name = "a", descriptor = "(Lclient!jda;I)V")
	fun add(@OriginalArg(0) pickableEntity: PickableEntity?) {
		@Pc(8) val entity: Entity? = pickableEntity!!.entity
		@Pc(10) var noneCalculated: Boolean = true
		@Pc(13) val pickingCylinders: Array<BoundingBoxPolygon?>? = pickableEntity.boundingBoxPolygons
		@Pc(15) var i: Int = 0
		while (i < pickingCylinders!!.size) {
			if (pickingCylinders[i]!!.positionCalculated) {
				noneCalculated = false
				break
			}
			i++
		}
		if (noneCalculated) {
			return
		}
		@Pc(43) var pickableEntity2: PickableEntity?
		if (this.retained) {
			pickableEntity2 = this.entities.getFirst() as PickableEntity?
			while (pickableEntity2 != null) {
				if (entity === pickableEntity2.entity) {
					pickableEntity2.unlinkNode()
					PickableEntity.recycle(pickableEntity2)
				}
				pickableEntity2 = this.entities.getNext() as PickableEntity?
			}
		}
		pickableEntity2 = this.entities.getFirst() as PickableEntity?
		while (pickableEntity2 != null) {
			if (entity!!.screenDepth >= pickableEntity2.entity!!.screenDepth) {
				EntityNode.insertBefore(pickableEntity2, pickableEntity)
				return
			}
			pickableEntity2 = this.entities.getNext() as PickableEntity?
		}
		this.entities.offer(pickableEntity)
	}

	@OriginalMember(owner = "client!ks", name = "b", descriptor = "(B)V")
	fun clear() {
		while (true) {
			@Pc(7) val entity: PickableEntity? = this.entities.removeFirst() as PickableEntity?
			if (entity == null) {
				return
			}
			entity.unlinkNode()
			PickableEntity.recycle(entity)
		}
	}

	companion object {
		@OriginalMember(owner = "client!kga", name = "c", descriptor = "Lclient!ks;")
		@JvmField
		val aClass182_2: PickList = PickList(false)

		@OriginalMember(owner = "client!pe", name = "a", descriptor = "(Lclient!ks;)V")
		@JvmStatic
		fun setActive(@OriginalArg(0) pickList: PickList?) {
			PickList.aClass182_1 = pickList
		}

		@OriginalMember(owner = "client!kga", name = "a", descriptor = "Lclient!ks;")
		@JvmField
		var aClass182_1: PickList? = aClass182_2

		@OriginalMember(owner = "client!pf", name = "P", descriptor = "Lclient!ks;")
		@JvmField
		val aClass182_3: PickList = PickList(true)

		@OriginalMember(owner = "client!ut", name = "a", descriptor = "()V")
		@JvmStatic
		fun resetActive() {
			PickList.aClass182_1 = PickList.aClass182_2
		}
	}
}
