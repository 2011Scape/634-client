package com.jagex.game.runetek5.config.defaults

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.item.ItemType
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object EquipmentDefaultsType {
	@OriginalMember(owner = "client!hq", name = "j", descriptor = "[I")
	@JvmField
	var customizableObjSlots: IntArray? = null

	@OriginalMember(owner = "client!oha", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun initCustomizableObjSlots() {
		@Pc(9) val objIds: IntArray = IntArray(TypeLists.OBJ!!.num)
		@Pc(11) var count: Int = 0
		@Pc(13) var i: Int = 0
		while (i < TypeLists.OBJ!!.num) {
			@Pc(20) val objType: ItemType? = TypeLists.OBJ!!.list(i)
			if (objType!!.maleEquip1 >= 0 || objType.femaleEquip1 >= 0) {
				objIds[count++] = i
			}
			i++
		}
		EquipmentDefaultsType.customizableObjSlots = IntArray(count)
		@Pc(44) var j: Int = 0
		while (j < count) {
			EquipmentDefaultsType.customizableObjSlots!![j] = objIds[j]
			j++
		}
	}
}
