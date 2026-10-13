/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlShader (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

class HdShader constructor(glToolkit: HdToolkit?, handle: Long, type: Int) {

	private val glToolkit: HdToolkit?

	@JvmField
	val handle: Long

	init {
		this.glToolkit = glToolkit
		this.handle = handle
	}

	@Throws(Throwable::class)
	fun finalize() {
		this.glToolkit!!.requestShaderProgramCleanup(this.handle)
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	companion object {
		@JvmStatic
		fun compile(shaderType: Int, glToolkit: HdToolkit?, string: String?): HdShader? {
			val shaderHandle: Long = HdGl.glCreateShaderObjectARB(shaderType)
			HdGl.glShaderSourceARB(shaderHandle, string)
			HdGl.glCompileShaderARB(shaderHandle)
			HdGl.glGetObjectParameterivARB(shaderHandle, OpenGLConstants.GL_COMPILE_STATUS, HdShader.queryResults, 0)
			if (HdShader.queryResults[0] == 0) {
				if (HdShader.queryResults[0] == 0) {
					println("Shader compile failed:")
				}
				HdGl.glGetObjectParameterivARB(shaderHandle, OpenGLConstants.GL_INFO_LOG_LENGTH, HdShader.queryResults, 1)
				if (HdShader.queryResults[1] > 1) {
					val infoLog: ByteArray = ByteArray(HdShader.queryResults[1])
					HdGl.glGetInfoLogARB(shaderHandle, HdShader.queryResults[1], HdShader.queryResults, 0, infoLog, 0)
					println(lang.decodeDefaultCharset(infoLog))
				}
				if (HdShader.queryResults[0] == 0) {
					HdGl.glDeleteObjectARB(shaderHandle)
					return null
				}
			}
			return HdShader(glToolkit, shaderHandle, shaderType)
		}

		@JvmField
		val queryResults: IntArray = IntArray(2)
	}
}
