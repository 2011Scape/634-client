package com.jagex.game.runetek5.config.rsinterface.cs2;

import com.jagex.game.runetek5.ClientPacket;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.objtype.ObjTypeList;
import org.openrs2.deob.annotation.OriginalArg;

public final class CS2Executor {
	@OriginalMember(owner = "client!lk", name = "D", descriptor = "[I")
	public static final int[] projectionBuffer = new int[3];

	@OriginalMember(owner = "client!aba", name = "C", descriptor = "Lclient!ej;")
	public static final ClientPacket FRIENDCHAT_JOIN_LEAVE = new ClientPacket(50, -1);

	private CS2Executor() {
		/* empty */
	}
	@OriginalMember(owner = "client!aj", name = "a", descriptor = "(ZLjava/lang/String;Ljava/lang/String;II)V")
	public static void queryItemParamString(@OriginalArg(0) boolean stockMarketOnly, @OriginalArg(1) String string, @OriginalArg(2) String string2, @OriginalArg(3) int paramId) {
		ObjTypeList.queryItem(true, string2, -1, paramId, stockMarketOnly, string);
	}
}
