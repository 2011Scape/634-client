package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.toolkit.GlxToolkit
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import jaggl.OpenGL
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!qr")
class GlxArbProgram @OriginalMember(owner = "client!qr", name = "<init>", descriptor = "(Lclient!bga;II)V") constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) target: Int, @OriginalArg(2) programId: Int) {

	@OriginalMember(owner = "client!qr", name = "c", descriptor = "Lclient!bga;")
	private val glxToolkit: GlxToolkit?

	@OriginalMember(owner = "client!qr", name = "b", descriptor = "I")
	@JvmField
	val programId: Int

	init {
		this.glxToolkit = glxToolkit
		this.programId = programId
	}

	@OriginalMember(owner = "client!qr", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	fun finalize() {
		this.glxToolkit!!.destroyProgram(this.programId)
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	companion object {
		@OriginalMember(owner = "client!cu", name = "a", descriptor = "(I[BLclient!bga;I)Lclient!qr;")
		@JvmStatic
		fun createVertexProgram(@OriginalArg(1) programData: ByteArray?, @OriginalArg(2) glxToolkit: GlxToolkit?): GlxArbProgram? {
			if (programData == null) {
				return null
			}
			@Pc(9) val programId: Int = OpenGL.glGenProgramARB()
			OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, programId)
			OpenGL.glProgramRawARB(34336, 34933, programData)
			OpenGL.glGetIntegerv(OpenGLConstants.GL_PROGRAM_ERROR_POSITION_ARB, GlxArbProgram.PROGRAM_ERROR_POSITION, 0)
			if (GlxArbProgram.PROGRAM_ERROR_POSITION[0] == -1) {
				OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0)
				return GlxArbProgram(glxToolkit, 34336, programId)
			} else {
				OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0)
				return null
			}
		}

		@OriginalMember(owner = "client!mha", name = "q", descriptor = "[I")
		@JvmField
		val PROGRAM_ERROR_POSITION: IntArray = IntArray(1)
	}
}
