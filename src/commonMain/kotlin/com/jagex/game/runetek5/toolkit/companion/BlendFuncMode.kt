package com.jagex.game.runetek5.toolkit.companion

import com.jagex.game.runetek5.BlendMode
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object BlendFuncMode {
	@OriginalMember(owner = "client!uw", name = "e", descriptor = "Lclient!pn;")
	@JvmField
	val COLOR_BLENDING: BlendMode = BlendMode()

	@OriginalMember(owner = "client!sg", name = "S", descriptor = "Lclient!pn;")
	@JvmField
	val OPAQUE: BlendMode = BlendMode()

	@OriginalMember(owner = "client!fa", name = "z", descriptor = "Lclient!pn;")
	@JvmField
	val ALPHA_BLENDING: BlendMode = BlendMode()

	@OriginalMember(owner = "client!kea", name = "k", descriptor = "Lclient!pn;")
	@JvmField
	val OPAQUE_BLENDING: BlendMode = BlendMode()
}
