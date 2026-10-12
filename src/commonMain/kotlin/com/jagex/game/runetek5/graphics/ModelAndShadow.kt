package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.graphics.model.Model
import kotlin.jvm.JvmField

@OriginalClass("client!ak")
class ModelAndShadow {

	@OriginalMember(owner = "client!ak", name = "c", descriptor = "Lclient!r;")
	@JvmField
	var shadow: Shadow? = null

	@OriginalMember(owner = "client!ak", name = "d", descriptor = "Lclient!ka;")
	@JvmField
	var model: Model? = null
}
