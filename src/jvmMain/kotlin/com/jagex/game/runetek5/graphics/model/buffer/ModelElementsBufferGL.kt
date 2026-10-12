package com.jagex.game.runetek5.graphics.model.buffer

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.buffer.ElementsBufferGL

@OriginalClass("client!us")
class ModelElementsBufferGL {

	@OriginalMember(owner = "client!us", name = "a", descriptor = "Lclient!ni;")
	@JvmField
	var openglElementsBuffer: ElementsBufferGL? = null
}
