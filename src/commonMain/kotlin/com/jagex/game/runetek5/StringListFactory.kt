package com.jagex.game.runetek5

import com.jagex.game.runetek5.core.datastruct.StringList
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmStatic

object StringListFactory {

	@OriginalMember(owner = "client!mu", name = "a", descriptor = "(BI)Lclient!bo;")
	@JvmStatic
	fun createStringList(): StringList {
		return StringList(1, false)
	}
	@OriginalMember(owner = "client!iia", name = "a", descriptor = "(B)Lclient!bo;")
	@JvmStatic
	fun create(): StringList {
		return StringListFactory.createStringList()
	}
}
