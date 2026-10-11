package com.jagex.game.runetek5.config.quickchat;

import com.jagex.game.runetek5.QuickChatValueType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!bq")
public interface QuickchatFillerList {

	@OriginalMember(owner = "client!bq", name = "a", descriptor = "(IJLclient!oaa;[I)Ljava/lang/String;")
	public String decode(@OriginalArg(1) long data, @OriginalArg(2) QuickChatValueType valueType, @OriginalArg(3) int[] configs);
}
