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

@OriginalClass("client!ifa")
class GlxShader @OriginalMember(owner = "client!ifa", name = "<init>", descriptor = "(Lclient!bga;JI)V") constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) handle: Long, @OriginalArg(2) type: Int) {

	@OriginalMember(owner = "client!ifa", name = "d", descriptor = "J")
	@JvmField
	val handle: Long

	@OriginalMember(owner = "client!ifa", name = "f", descriptor = "Lclient!bga;")
	private val glxToolkit: GlxToolkit?

	init {
		this.handle = handle
		this.glxToolkit = glxToolkit
	}

	@OriginalMember(owner = "client!ifa", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	fun finalize() {
		this.glxToolkit!!.requestShaderDeletion(this.handle)
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	companion object {
		@OriginalMember(owner = "client!dea", name = "a", descriptor = "(Lclient!bga;[BII)Lclient!ifa;")
		@JvmStatic
		fun compile(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) source: ByteArray?, @OriginalArg(3) shaderType: Int): GlxShader? {
			if (source == null || source.size == 0) {
				return null
			}
			@Pc(16) val shaderId: Long = OpenGL.glCreateShaderObjectARB(shaderType)
			OpenGL.glShaderSourceRawARB(shaderId, source)
			OpenGL.glCompileShaderARB(shaderId)
			OpenGL.glGetObjectParameterivARB(shaderId, OpenGLConstants.GL_COMPILE_STATUS, GlxShaderCompileStatus.SHADER_PARAMS, 0)
			if (GlxShaderCompileStatus.SHADER_PARAMS[0] == 0) {
				if (GlxShaderCompileStatus.SHADER_PARAMS[0] == 0) {
					println("Shader compile failed:")
				}
				OpenGL.glGetObjectParameterivARB(shaderId, OpenGLConstants.GL_INFO_LOG_LENGTH, GlxShaderCompileStatus.SHADER_PARAMS, 1)
				if (GlxShaderCompileStatus.SHADER_PARAMS[1] > 1) {
					@Pc(64) val infoLog: ByteArray = ByteArray(GlxShaderCompileStatus.SHADER_PARAMS[1])
					OpenGL.glGetInfoLogARB(shaderId, GlxShaderCompileStatus.SHADER_PARAMS[1], GlxShaderCompileStatus.SHADER_PARAMS, 0, infoLog, 0)
					println(String(infoLog, java.nio.charset.Charset.defaultCharset()))
				}
				if (GlxShaderCompileStatus.SHADER_PARAMS[0] == 0) {
					OpenGL.glDeleteObjectARB(shaderId)
					return null
				}
			}
			return GlxShader(glxToolkit, shaderId, shaderType)
		}
	}
}
