package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.gl.GlToolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.toolkit.opengl.ProgramGL
import jaggl.OpenGL
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!pl")
class GlArbProgram @OriginalMember(owner = "client!pl", name = "<init>", descriptor = "(Lclient!qo;II)V") constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) target: Int, @OriginalArg(2) programId: Int) {

	@OriginalMember(owner = "client!pl", name = "b", descriptor = "Lclient!qo;")
	private val glToolkit: GlToolkit?

	@OriginalMember(owner = "client!pl", name = "d", descriptor = "I")
	@JvmField
	val programId: Int

	init {
		this.glToolkit = glToolkit
		this.programId = programId
	}

	@OriginalMember(owner = "client!pl", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	fun finalize() {
		this.glToolkit!!.requestArbProgramCleanup(this.programId)
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	companion object {
		@OriginalMember(owner = "client!iu", name = "a", descriptor = "(ILclient!qo;Ljava/lang/String;Z)Lclient!pl;")
		@JvmStatic
		fun create(@OriginalArg(1) glToolkit: GlToolkit?, @OriginalArg(2) string: String?): GlArbProgram? {
			@Pc(5) val programId: Int = OpenGL.glGenProgramARB()
			OpenGL.glBindProgramARB(34336, programId)
			OpenGL.glProgramStringARB(34336, OpenGLConstants.GL_PROGRAM_FORMAT_ASCII_ARB, string)
			OpenGL.glGetIntegerv(OpenGLConstants.GL_PROGRAM_ERROR_POSITION_ARB, ProgramGL.HANDLE, 0)
			if (ProgramGL.HANDLE[0] == -1) {
				OpenGL.glBindProgramARB(34336, 0)
				return GlArbProgram(glToolkit, 34336, programId)
			} else {
				OpenGL.glBindProgramARB(34336, 0)
				return null
			}
		}
	}
}
