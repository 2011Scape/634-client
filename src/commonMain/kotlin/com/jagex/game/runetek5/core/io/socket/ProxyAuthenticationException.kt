package com.jagex.game.runetek5.core.io.socket

import io.IOException
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!gv")
class ProxyAuthenticationException : IOException {

	@OriginalMember(owner = "client!gv", name = "<init>", descriptor = "(Ljava/lang/String;)V")
	constructor(@OriginalArg(0) string: String?) : super(string)
}
