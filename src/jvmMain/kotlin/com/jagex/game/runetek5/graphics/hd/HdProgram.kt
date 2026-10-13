/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlProgram (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.graphics.shader.OpenGLShaderManager
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

class HdProgram {

	private val glToolkit: HdToolkit?

	@JvmField
	val programHandle: Long

	constructor(glToolkit: HdToolkit?, programHandle: Long, shaders: Array<HdShader?>?) {
		this.glToolkit = glToolkit
		this.programHandle = programHandle
	}

	@Throws(Throwable::class)
	fun finalize() {
		this.glToolkit!!.requestShaderProgramCleanup(this.programHandle)
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	companion object {
		@JvmStatic
		fun link(glToolkit: HdToolkit?, shaders: Array<HdShader?>?): HdProgram? {
			for (i in 0 until shaders!!.size) {
				if (shaders[i] == null || shaders[i]!!.handle <= 0L) {
					return null
				}
			}
			val program: Long = HdGl.glCreateProgramObjectARB()
			for (j in 0 until shaders.size) {
				HdGl.glAttachObjectARB(program, shaders[j]!!.handle)
			}
			HdGl.glLinkProgramARB(program)
			HdGl.glGetObjectParameterivARB(program, 35714, OpenGLShaderManager.BUFFER, 0)
			if (OpenGLShaderManager.BUFFER[0] == 0) {
				if (OpenGLShaderManager.BUFFER[0] == 0) {
					println("Shader linking failed:")
				}
				HdGl.glGetObjectParameterivARB(program, 35716, OpenGLShaderManager.BUFFER, 1)
				if (OpenGLShaderManager.BUFFER[1] > 1) {
					val infoLog: ByteArray = ByteArray(OpenGLShaderManager.BUFFER[1])
					HdGl.glGetInfoLogARB(program, OpenGLShaderManager.BUFFER[1], OpenGLShaderManager.BUFFER, 0, infoLog, 0)
					// Java's new String(byte[]) decodes with the default charset.
					println(lang.decodeDefaultCharset(infoLog))
				}
				if (OpenGLShaderManager.BUFFER[0] == 0) {
					for (k in 0 until shaders.size) {
						HdGl.glDetachObjectARB(program, shaders[k]!!.handle)
					}
					HdGl.glDeleteObjectARB(program)
					return null
				}
			}
			return HdProgram(glToolkit, program, shaders)
		}
	}
}
