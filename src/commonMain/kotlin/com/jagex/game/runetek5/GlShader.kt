package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.gl.GlToolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import jaggl.OpenGL
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!tba")
class GlShader @OriginalMember(owner = "client!tba", name = "<init>", descriptor = "(Lclient!qo;JI)V") constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) handle: Long, @OriginalArg(2) type: Int) {

	@OriginalMember(owner = "client!tba", name = "c", descriptor = "Lclient!qo;")
	private val glToolkit: GlToolkit?

	@OriginalMember(owner = "client!tba", name = "f", descriptor = "J")
	@JvmField
	val handle: Long

	init {
		this.glToolkit = glToolkit
		this.handle = handle
	}

	@OriginalMember(owner = "client!tba", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	fun finalize() {
		this.glToolkit!!.requestShaderProgramCleanup(this.handle)
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	companion object {
		@OriginalMember(owner = "client!et", name = "a", descriptor = "(IILclient!qo;Ljava/lang/String;)Lclient!tba;")
		@JvmStatic
		fun compile(@OriginalArg(0) shaderType: Int, @OriginalArg(2) glToolkit: GlToolkit?, @OriginalArg(3) string: String?): GlShader? {
			@Pc(14) val shaderHandle: Long = OpenGL.glCreateShaderObjectARB(shaderType)
			OpenGL.glShaderSourceARB(shaderHandle, string)
			OpenGL.glCompileShaderARB(shaderHandle)
			OpenGL.glGetObjectParameterivARB(shaderHandle, OpenGLConstants.GL_COMPILE_STATUS, GlShader.queryResults, 0)
			if (GlShader.queryResults[0] == 0) {
				if (GlShader.queryResults[0] == 0) {
					println("Shader compile failed:")
				}
				OpenGL.glGetObjectParameterivARB(shaderHandle, OpenGLConstants.GL_INFO_LOG_LENGTH, GlShader.queryResults, 1)
				if (GlShader.queryResults[1] > 1) {
					@Pc(55) val infoLog: ByteArray = ByteArray(GlShader.queryResults[1])
					OpenGL.glGetInfoLogARB(shaderHandle, GlShader.queryResults[1], GlShader.queryResults, 0, infoLog, 0)
					println(lang.decodeDefaultCharset(infoLog))
				}
				if (GlShader.queryResults[0] == 0) {
					OpenGL.glDeleteObjectARB(shaderHandle)
					return null
				}
			}
			return GlShader(glToolkit, shaderHandle, shaderType)
		}

		@OriginalMember(owner = "client!lm", name = "F", descriptor = "[I")
		@JvmField
		val queryResults: IntArray = IntArray(2)
	}
}
