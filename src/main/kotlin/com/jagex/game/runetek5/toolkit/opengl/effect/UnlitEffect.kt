package com.jagex.game.runetek5.toolkit.opengl.effect

import com.jagex.game.runetek5.graphics.gl.GlTexture
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!di")
class UnlitEffect : MaterialEffectGL {

	@OriginalMember(owner = "client!di", name = "<init>", descriptor = "(Lclient!qo;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?) : super(glToolkit)

	@OriginalMember(owner = "client!di", name = "a", descriptor = "(I)V")
	override fun disable() {
		super.toolkit!!.setHideLights(false)
	}

	@OriginalMember(owner = "client!di", name = "a", descriptor = "(Lclient!ud;BI)V")
	override fun bindTexture(@OriginalArg(0) texture: GlTexture?, @OriginalArg(2) combineMode: Int) {
		super.toolkit!!.setActiveTexture(texture)
		super.toolkit!!.setColourOp(combineMode)
	}

	@OriginalMember(owner = "client!di", name = "a", descriptor = "(IIB)V")
	override fun setEffectParams(@OriginalArg(0) effectParam1: Int, @OriginalArg(1) effectParam2: Int) {
	}

	@OriginalMember(owner = "client!di", name = "c", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!di", name = "a", descriptor = "(ZZ)V")
	override fun enable(@OriginalArg(0) lit: Boolean) {
		super.toolkit!!.setHideLights(true)
	}

	@OriginalMember(owner = "client!di", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
	}
}
