package com.jagex.game.runetek5.toolkit.opengl

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jv")
class GlRenderList {

	@OriginalMember(owner = "client!jv", name = "f", descriptor = "I")
	private val reservedRange: Int

	@OriginalMember(owner = "client!jv", name = "<init>", descriptor = "(Lclient!qo;I)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) range: Int) {
		this.reservedRange = OpenGL.glGenLists(range)
	}

	@OriginalMember(owner = "client!jv", name = "a", descriptor = "(I)V")
	fun endList() {
		OpenGL.glEndList()
	}

	@OriginalMember(owner = "client!jv", name = "a", descriptor = "(CI)V")
	fun callList(@OriginalArg(0) offset: Char) {
		OpenGL.glCallList(this.reservedRange + offset.code)
	}

	@OriginalMember(owner = "client!jv", name = "a", descriptor = "(BI)V")
	fun newList(@OriginalArg(1) offset: Int) {
		OpenGL.glNewList(offset + this.reservedRange, OpenGLConstants.GL_COMPILE)
	}
}
