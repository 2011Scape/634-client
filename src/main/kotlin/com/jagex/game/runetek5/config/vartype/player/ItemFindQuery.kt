package com.jagex.game.runetek5.config.vartype.player

import com.jagex.game.runetek5.cache.node.lru.LRUCache
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.config.objtype.ObjTypeList

object ItemFindQuery {

	@OriginalMember(owner = "client!rv", name = "a", descriptor = "(ZBLjava/lang/String;)V")
	@JvmStatic
	fun queryItemNoParams(@OriginalArg(0) tradeableOnly: Boolean, @OriginalArg(2) string: String?) {
		ItemFindQuery.queryItemParamInt(-1, tradeableOnly, string, -1)
	}

	@OriginalMember(owner = "client!hfa", name = "a", descriptor = "(IIZLjava/lang/String;I)V")
	@JvmStatic
	fun queryItemParamInt(@OriginalArg(0) paramValue: Int, @OriginalArg(2) tradeableOnly: Boolean, @OriginalArg(3) string: String?, @OriginalArg(4) paramId: Int) {
		ObjTypeList.queryItem(false, null, paramValue, paramId, tradeableOnly, string)
	}
}
