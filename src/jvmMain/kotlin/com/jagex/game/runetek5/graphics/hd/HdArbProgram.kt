/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlArbProgram (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.toolkit.opengl.ProgramGL
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

class HdArbProgram constructor(glToolkit: HdToolkit?, target: Int, programId: Int) {

	private val glToolkit: HdToolkit?

	@JvmField
	val programId: Int

	init {
		this.glToolkit = glToolkit
		this.programId = programId
	}

	@Throws(Throwable::class)
	fun finalize() {
		this.glToolkit!!.requestArbProgramCleanup(this.programId)
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	companion object {
		@JvmStatic
		fun create(glToolkit: HdToolkit?, string: String?): HdArbProgram? {
			val programId: Int = HdGl.glGenProgramARB()
			HdGl.glBindProgramARB(34336, programId)
			HdGl.glProgramStringARB(34336, OpenGLConstants.GL_PROGRAM_FORMAT_ASCII_ARB, string)
			HdGl.glGetIntegerv(OpenGLConstants.GL_PROGRAM_ERROR_POSITION_ARB, ProgramGL.HANDLE, 0)
			if (ProgramGL.HANDLE[0] == -1) {
				HdGl.glBindProgramARB(34336, 0)
				return HdArbProgram(glToolkit, 34336, programId)
			} else {
				HdGl.glBindProgramARB(34336, 0)
				return null
			}
		}
	}
}
