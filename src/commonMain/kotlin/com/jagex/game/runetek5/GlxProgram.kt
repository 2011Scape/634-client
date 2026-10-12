package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.toolkit.GlxToolkit
import jaggl.OpenGL
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!lha")
class GlxProgram @OriginalMember(owner = "client!lha", name = "<init>", descriptor = "(Lclient!bga;J[Lclient!ifa;)V") constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) handle: Long, @OriginalArg(2) shaders: Array<GlxShader?>?) {

	@OriginalMember(owner = "client!lha", name = "e", descriptor = "J")
	@JvmField
	val handle: Long

	@OriginalMember(owner = "client!lha", name = "a", descriptor = "Lclient!bga;")
	private val glxToolkit: GlxToolkit?

	init {
		this.handle = handle
		this.glxToolkit = glxToolkit
	}

	@OriginalMember(owner = "client!lha", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	fun finalize() {
		this.glxToolkit!!.requestShaderDeletion(this.handle)
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	companion object {
		@OriginalMember(owner = "client!lha", name = "c", descriptor = "[I")
		@JvmField
		val CRC_TABLE: IntArray = IntArray(256)

		init {
			@Pc(4) var i: Int = 0
			while (i < 256) {
				@Pc(7) var crc: Int = i
				@Pc(9) var bit: Int = 0
				while (bit < 8) {
					if ((crc and 0x1) == 1) {
						crc = (crc ushr 1) xor 0xEDB88320.toInt()
					} else {
						crc = crc ushr 0x1
					}
					bit++
				}
				CRC_TABLE[i] = crc
				i++
			}
		}

		@OriginalMember(owner = "client!da", name = "a", descriptor = "(Lclient!bga;[Lclient!ifa;I)Lclient!lha;")
		@JvmStatic
		fun create(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) shaders: Array<GlxShader?>?): GlxProgram? {
			@Pc(12) var i: Int = 0
			while (i < shaders!!.size) {
				if (shaders[i] == null || shaders[i]!!.handle <= 0L) {
					return null
				}
				i++
			}
			@Pc(36) val programHandle: Long = OpenGL.glCreateProgramObjectARB()
			@Pc(38) var j: Int = 0
			while (j < shaders.size) {
				OpenGL.glAttachObjectARB(programHandle, shaders[j]!!.handle)
				j++
			}
			OpenGL.glLinkProgramARB(programHandle)
			OpenGL.glGetObjectParameterivARB(programHandle, 35714, GlxProgram.glStatusParams, 0)
			if (GlxProgram.glStatusParams[0] == 0) {
				if (GlxProgram.glStatusParams[0] == 0) {
					println("Shader linking failed:")
				}
				OpenGL.glGetObjectParameterivARB(programHandle, 35716, GlxProgram.glStatusParams, 1)
				if (GlxProgram.glStatusParams[1] > 1) {
					@Pc(94) val infoLog: ByteArray = ByteArray(GlxProgram.glStatusParams[1])
					OpenGL.glGetInfoLogARB(programHandle, GlxProgram.glStatusParams[1], GlxProgram.glStatusParams, 0, infoLog, 0)
					println(lang.decodeDefaultCharset(infoLog))
				}
				if (GlxProgram.glStatusParams[0] == 0) {
					@Pc(115) var k: Int = 0
					while (k < shaders.size) {
						OpenGL.glDetachObjectARB(programHandle, shaders[k]!!.handle)
						k++
					}
					OpenGL.glDeleteObjectARB(programHandle)
					return null
				}
			}
			return GlxProgram(glxToolkit, programHandle, shaders)
		}

		@OriginalMember(owner = "client!ro", name = "n", descriptor = "[I")
		@JvmField
		val glStatusParams: IntArray = IntArray(2)
	}
}
