package com.jagex.game.runetek5;

import com.jagex.game.runetek5.QuickChatValueType;
import com.jagex.game.runetek5.config.enumtype.EnumType;
import com.jagex.game.runetek5.config.item.ItemType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.quickchat.QuickchatFillerList;
import com.jagex.game.runetek5.config.TypeLists;

@OriginalClass("client!hu")
public final class SimpleFillerDecoder implements QuickchatFillerList {

	@OriginalMember(owner = "client!hu", name = "a", descriptor = "(IJLclient!oaa;[I)Ljava/lang/String;")
	@Override
	public String decode(@OriginalArg(1) long value, @OriginalArg(2) QuickChatValueType command, @OriginalArg(3) int[] vars) {
		if (QuickChatValueType.aClass234_23 == command) {
			@Pc(13) EnumType type = TypeLists.ENUM.list(vars[0]);
			return type.getString((int) value);
		} else if (QuickChatValueType.aClass234_20 == command || QuickChatValueType.aClass234_26 == command) {
			@Pc(33) ItemType objType = TypeLists.OBJ.list((int) value);
			return objType.name;
		} else if (command == QuickChatValueType.aClass234_16 || command == QuickChatValueType.aClass234_11 || QuickChatValueType.aClass234_14 == command) {
			return TypeLists.ENUM.list(vars[0]).getString((int) value);
		} else {
			return null;
		}
	}
}
