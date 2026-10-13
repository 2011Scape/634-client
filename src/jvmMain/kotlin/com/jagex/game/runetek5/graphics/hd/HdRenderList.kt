/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlRenderList (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext

class HdRenderList {

	private val reservedRange: Int

	constructor(glToolkit: HdToolkit?, range: Int) {
		this.reservedRange = HdGl.glGenLists(range)
	}

	fun endList() {
		HdGl.glEndList()
	}

	fun callList(offset: Char) {
		HdGl.glCallList(this.reservedRange + offset.code)
	}

	fun newList(offset: Int) {
		HdGl.glNewList(offset + this.reservedRange, OpenGLConstants.GL_COMPILE)
	}
}
