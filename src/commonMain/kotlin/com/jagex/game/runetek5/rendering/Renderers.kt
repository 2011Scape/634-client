package com.jagex.game.runetek5.rendering

import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.core.datastruct.key.Deque
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object Renderers {
	@OriginalMember(owner = "client!be", name = "o", descriptor = "Lclient!ha;")
	@JvmField
	var GPU_ITEM_RENDERER: Toolkit? = null

	@OriginalMember(owner = "client!ta", name = "m", descriptor = "Lclient!da;")
	@JvmField
	var FONT_RENDERER: AbstractFontRenderer? = null
	@OriginalMember(owner = "client!kj", name = "P", descriptor = "Lclient!da;")
	@JvmField
	var GPU_ITEM_FONT_RENDERER: AbstractFontRenderer? = null
	@OriginalMember(owner = "client!uc", name = "S", descriptor = "Lclient!uh;")
	@JvmField
	val activeToolkit: Deque = Deque()
	@OriginalMember(owner = "client!cea", name = "i", descriptor = "(I)V")
	@JvmStatic
	fun freeGpuItemRenderer() {
		if (Renderers.GPU_ITEM_RENDERER != null) {
			Renderers.GPU_ITEM_RENDERER!!.free()
			Renderers.GPU_ITEM_FONT_RENDERER = null
			Renderers.GPU_ITEM_RENDERER = null
		}
	}
}
