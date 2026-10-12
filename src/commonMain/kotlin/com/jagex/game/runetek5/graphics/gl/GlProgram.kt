package com.jagex.game.runetek5.graphics.gl

import com.jagex.game.runetek5.GlShader
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.graphics.shader.OpenGLShaderManager
import jaggl.OpenGL
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!dw")
class GlProgram {

	@OriginalMember(owner = "client!dw", name = "a", descriptor = "Lclient!qo;")
	private val glToolkit: GlToolkit?

	@OriginalMember(owner = "client!dw", name = "f", descriptor = "J")
	@JvmField
	val programHandle: Long

	@OriginalMember(owner = "client!dw", name = "<init>", descriptor = "(Lclient!qo;J[Lclient!tba;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) programHandle: Long, @OriginalArg(2) shaders: Array<GlShader?>?) {
		this.glToolkit = glToolkit
		this.programHandle = programHandle
	}

	@OriginalMember(owner = "client!dw", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	fun finalize() {
		this.glToolkit!!.requestShaderProgramCleanup(this.programHandle)
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	companion object {
		@OriginalMember(owner = "client!pw", name = "a", descriptor = "(Lclient!qo;I[Lclient!tba;)Lclient!dw;")
		@JvmStatic
		fun link(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(2) shaders: Array<GlShader?>?): GlProgram? {
			for (@Pc(7) i in 0 until shaders!!.size) {
				if (shaders[i] == null || shaders[i]!!.handle <= 0L) {
					return null
				}
			}
			@Pc(33) val program: Long = OpenGL.glCreateProgramObjectARB()
			for (@Pc(35) j in 0 until shaders.size) {
				OpenGL.glAttachObjectARB(program, shaders[j]!!.handle)
			}
			OpenGL.glLinkProgramARB(program)
			OpenGL.glGetObjectParameterivARB(program, 35714, OpenGLShaderManager.BUFFER, 0)
			if (OpenGLShaderManager.BUFFER[0] == 0) {
				if (OpenGLShaderManager.BUFFER[0] == 0) {
					println("Shader linking failed:")
				}
				OpenGL.glGetObjectParameterivARB(program, 35716, OpenGLShaderManager.BUFFER, 1)
				if (OpenGLShaderManager.BUFFER[1] > 1) {
					@Pc(88) val infoLog: ByteArray = ByteArray(OpenGLShaderManager.BUFFER[1])
					OpenGL.glGetInfoLogARB(program, OpenGLShaderManager.BUFFER[1], OpenGLShaderManager.BUFFER, 0, infoLog, 0)
					// Java's new String(byte[]) decodes with the default charset.
					println(lang.decodeDefaultCharset(infoLog))
				}
				if (OpenGLShaderManager.BUFFER[0] == 0) {
					for (@Pc(109) k in 0 until shaders.size) {
						OpenGL.glDetachObjectARB(program, shaders[k]!!.handle)
					}
					OpenGL.glDeleteObjectARB(program)
					return null
				}
			}
			return GlProgram(glToolkit, program, shaders)
		}
	}
}
