package com.jagex.game.runetek5.toolkit.opengl.effect

import com.jagex.game.runetek5.graphics.gl.GlTexture
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!vk")
abstract class MaterialEffectGL {

	@OriginalMember(owner = "client!vk", name = "d", descriptor = "Lclient!qo;")
	@JvmField
	val toolkit: GlToolkit?

	@OriginalMember(owner = "client!vk", name = "<init>", descriptor = "(Lclient!qo;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?) {
		this.toolkit = glToolkit
	}

	@OriginalMember(owner = "client!vk", name = "a", descriptor = "(I)V")
	abstract fun disable()

	@OriginalMember(owner = "client!vk", name = "a", descriptor = "(ZZ)V")
	abstract fun enable(@OriginalArg(0) lit: Boolean)

	@OriginalMember(owner = "client!vk", name = "a", descriptor = "(IIB)V")
	abstract fun setEffectParams(@OriginalArg(0) effectParam1: Int, @OriginalArg(1) effectParam2: Int)

	@OriginalMember(owner = "client!vk", name = "c", descriptor = "(I)Z")
	abstract fun isSupported(): Boolean

	@OriginalMember(owner = "client!vk", name = "a", descriptor = "(ZB)V")
	abstract fun applyTextureCombine(@OriginalArg(0) lit: Boolean)

	@OriginalMember(owner = "client!vk", name = "a", descriptor = "(Lclient!ud;BI)V")
	abstract fun bindTexture(@OriginalArg(0) texture: GlTexture?, @OriginalArg(2) combineMode: Int)
}
