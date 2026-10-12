package com.jagex.game.runetek5.network.newsitem

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!wc")
class NewsItem {

	@OriginalMember(owner = "client!wc", name = "d", descriptor = "Ljava/lang/String;")
	@JvmField
	val line1: String?

	@OriginalMember(owner = "client!wc", name = "h", descriptor = "Ljava/lang/String;")
	@JvmField
	val line2: String?

	@OriginalMember(owner = "client!wc", name = "f", descriptor = "Ljava/lang/String;")
	@JvmField
	val line3: String?

	@OriginalMember(owner = "client!wc", name = "<init>", descriptor = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
	constructor(@OriginalArg(0) string: String?, @OriginalArg(1) string2: String?, @OriginalArg(2) string3: String?) {
		this.line1 = string2
		this.line2 = string3
		this.line3 = string
	}
}
