package com.jagex.game.runetek5.config.vartype.player;

import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.objtype.ObjTypeList;

public final class ItemFindQuery {

	@OriginalMember(owner = "client!rv", name = "a", descriptor = "(ZBLjava/lang/String;)V")
	public static void queryItemNoParams(@OriginalArg(0) boolean tradeableOnly, @OriginalArg(2) String string) {
		ItemFindQuery.queryItemParamInt(-1, tradeableOnly, string, -1);
	}
	@OriginalMember(owner = "client!hfa", name = "a", descriptor = "(IIZLjava/lang/String;I)V")
	public static void queryItemParamInt(@OriginalArg(0) int paramValue, @OriginalArg(2) boolean tradeableOnly, @OriginalArg(3) String string, @OriginalArg(4) int paramId) {
		ObjTypeList.queryItem(false, null, paramValue, paramId, tradeableOnly, string);
	}
}
