package com.jagex.game.runetek5.config.quickchat

import com.jagex.game.runetek5.QuickChatValueType
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!bq")
interface QuickchatFillerList {

	@OriginalMember(owner = "client!bq", name = "a", descriptor = "(IJLclient!oaa;[I)Ljava/lang/String;")
	fun decode(@OriginalArg(1) data: Long, @OriginalArg(2) valueType: QuickChatValueType?, @OriginalArg(3) configs: IntArray?): String?
}
