package com.jagex.game.runetek5.toolkit.opengl.buffer

import jaclib.memory.Buffer
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import kotlin.jvm.JvmField

@OriginalClass("client!bi")
abstract class AbstractArbBufferGL {

	@OriginalMember(owner = "client!bi", name = "c", descriptor = "Z")
	private val streamDraw: Boolean

	@OriginalMember(owner = "client!bi", name = "g", descriptor = "Lclient!qo;")
	@JvmField
	val toolkit: GlToolkit?

	@OriginalMember(owner = "client!bi", name = "j", descriptor = "I")
	private val bufferTarget: Int

	@OriginalMember(owner = "client!bi", name = "a", descriptor = "I")
	private var bufferSize: Int

	@OriginalMember(owner = "client!bi", name = "i", descriptor = "I")
	@JvmField
	val id: Int

	@OriginalMember(owner = "client!bi", name = "<init>", descriptor = "(Lclient!qo;I[BIZ)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) bufferTarget: Int, @OriginalArg(2) data: ByteArray?, @OriginalArg(3) bufferSize: Int, @OriginalArg(4) streamDraw: Boolean) {
		this.streamDraw = streamDraw
		this.toolkit = glToolkit
		this.bufferTarget = bufferTarget
		this.bufferSize = bufferSize
		OpenGL.glGenBuffersARB(1, AbstractArbBufferGL.HANDLE, 0)
		this.id = AbstractArbBufferGL.HANDLE[0]
		this.setAsActiveBuffer()
		OpenGL.glBufferDataARBub(bufferTarget, this.bufferSize, data, 0, if (this.streamDraw) 35040 else 35044)
		this.toolkit!!.vboBuffersSize += this.bufferSize
	}

	@OriginalMember(owner = "client!bi", name = "<init>", descriptor = "(Lclient!qo;ILclient!jaclib/memory/Buffer;IZ)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) bufferTarget: Int, @OriginalArg(2) buffer: Buffer?, @OriginalArg(3) bufferSize: Int, @OriginalArg(4) streamDraw: Boolean) {
		this.bufferTarget = bufferTarget
		this.bufferSize = bufferSize
		this.toolkit = glToolkit
		this.streamDraw = streamDraw
		OpenGL.glGenBuffersARB(1, AbstractArbBufferGL.HANDLE, 0)
		this.id = AbstractArbBufferGL.HANDLE[0]
		this.setAsActiveBuffer()
		OpenGL.glBufferDataARBa(bufferTarget, this.bufferSize, buffer!!.getAddress(), if (this.streamDraw) 35040 else 35044)
		this.toolkit!!.vboBuffersSize += this.bufferSize
	}

	@OriginalMember(owner = "client!bi", name = "a", descriptor = "([BII)V")
	fun put(@OriginalArg(0) data: ByteArray?, @OriginalArg(2) bufferSize: Int) {
		this.setAsActiveBuffer()
		if (this.bufferSize >= bufferSize) {
			OpenGL.glBufferSubDataARBub(this.bufferTarget, 0, bufferSize, data, 0)
		} else {
			OpenGL.glBufferDataARBub(this.bufferTarget, bufferSize, data, 0, if (this.streamDraw) 35040 else 35044)
			this.toolkit!!.vboBuffersSize += bufferSize - this.bufferSize
			this.bufferSize = bufferSize
		}
	}

	@OriginalMember(owner = "client!bi", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	fun finalize() {
		this.toolkit!!.requestArbBufferCleanup(this.bufferSize, this.id)
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	@OriginalMember(owner = "client!bi", name = "d", descriptor = "(I)V")
	abstract fun setAsActiveBuffer()

	companion object {
		@OriginalMember(owner = "client!fc", name = "S", descriptor = "[I")
		@JvmField
		val HANDLE: IntArray = IntArray(1)
	}
}
