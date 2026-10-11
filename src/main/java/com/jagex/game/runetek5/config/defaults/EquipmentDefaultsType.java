package com.jagex.game.runetek5.config.defaults;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.config.item.ItemType;
import org.openrs2.deob.annotation.Pc;

public final class EquipmentDefaultsType {
	@OriginalMember(owner = "client!hq", name = "j", descriptor = "[I")
	public static int[] customizableObjSlots;

	private EquipmentDefaultsType() {
		/* empty */
	}
	@OriginalMember(owner = "client!oha", name = "a", descriptor = "(I)V")
	public static void initCustomizableObjSlots() {
		@Pc(9) int[] objIds = new int[TypeLists.OBJ.num];
		@Pc(11) int count = 0;
		for (@Pc(13) int i = 0; i < TypeLists.OBJ.num; i++) {
			@Pc(20) ItemType objType = TypeLists.OBJ.list(i);
			if (objType.maleEquip1 >= 0 || objType.femaleEquip1 >= 0) {
				objIds[count++] = i;
			}
		}
		EquipmentDefaultsType.customizableObjSlots = new int[count];
		for (@Pc(44) int j = 0; j < count; j++) {
			EquipmentDefaultsType.customizableObjSlots[j] = objIds[j];
		}
	}
}
