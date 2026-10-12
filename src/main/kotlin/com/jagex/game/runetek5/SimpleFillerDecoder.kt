package com.jagex.game.runetek5

import com.jagex.game.runetek5.QuickChatValueType
import com.jagex.game.runetek5.config.enumtype.EnumType
import com.jagex.game.runetek5.config.item.ItemType
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.quickchat.QuickchatFillerList
import com.jagex.game.runetek5.config.TypeLists

@OriginalClass("client!hu")
class SimpleFillerDecoder : QuickchatFillerList {

	@OriginalMember(owner = "client!hu", name = "a", descriptor = "(IJLclient!oaa;[I)Ljava/lang/String;")
	override fun decode(@OriginalArg(1) value: Long, @OriginalArg(2) command: QuickChatValueType?, @OriginalArg(3) vars: IntArray?): String? {
		if (QuickChatValueType.aClass234_23 === command) {
			@Pc(13) val type: EnumType? = TypeLists.ENUM!!.list(vars!![0])
			return type!!.getString(value.toInt())
		} else if (QuickChatValueType.aClass234_20 === command || QuickChatValueType.aClass234_26 === command) {
			@Pc(33) val objType: ItemType = TypeLists.OBJ!!.list(value.toInt())
			return objType.name
		} else if (command === QuickChatValueType.aClass234_16 || command === QuickChatValueType.aClass234_11 || QuickChatValueType.aClass234_14 === command) {
			return TypeLists.ENUM!!.list(vars!![0])!!.getString(value.toInt())
		} else {
			return null
		}
	}
}
