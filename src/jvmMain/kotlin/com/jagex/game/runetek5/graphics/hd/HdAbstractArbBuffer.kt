/*
 * Not part of the original client: 634HD (toolkit 6), copied from AbstractArbBufferGL (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import jaclib.memory.Buffer
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField

abstract class HdAbstractArbBuffer {

	private val streamDraw: Boolean

	@JvmField
	val toolkit: HdToolkit?

	private val bufferTarget: Int

	private var bufferSize: Int

	@JvmField
	val id: Int

	constructor(glToolkit: HdToolkit?, bufferTarget: Int, data: ByteArray?, bufferSize: Int, streamDraw: Boolean) {
		this.streamDraw = streamDraw
		this.toolkit = glToolkit
		this.bufferTarget = bufferTarget
		this.bufferSize = bufferSize
		HdGl.glGenBuffersARB(1, HdAbstractArbBuffer.HANDLE, 0)
		this.id = HdAbstractArbBuffer.HANDLE[0]
		this.setAsActiveBuffer()
		HdGl.glBufferDataARBub(bufferTarget, this.bufferSize, data, 0, if (this.streamDraw) 35040 else 35044)
		this.toolkit!!.vboBuffersSize += this.bufferSize
	}

	constructor(glToolkit: HdToolkit?, bufferTarget: Int, buffer: Buffer?, bufferSize: Int, streamDraw: Boolean) {
		this.bufferTarget = bufferTarget
		this.bufferSize = bufferSize
		this.toolkit = glToolkit
		this.streamDraw = streamDraw
		HdGl.glGenBuffersARB(1, HdAbstractArbBuffer.HANDLE, 0)
		this.id = HdAbstractArbBuffer.HANDLE[0]
		this.setAsActiveBuffer()
		HdGl.glBufferDataARBa(bufferTarget, this.bufferSize, buffer!!.getAddress(), if (this.streamDraw) 35040 else 35044)
		this.toolkit!!.vboBuffersSize += this.bufferSize
	}

	fun put(data: ByteArray?, bufferSize: Int) {
		this.setAsActiveBuffer()
		if (this.bufferSize >= bufferSize) {
			HdGl.glBufferSubDataARBub(this.bufferTarget, 0, bufferSize, data, 0)
		} else {
			HdGl.glBufferDataARBub(this.bufferTarget, bufferSize, data, 0, if (this.streamDraw) 35040 else 35044)
			this.toolkit!!.vboBuffersSize += bufferSize - this.bufferSize
			this.bufferSize = bufferSize
		}
	}

	@Throws(Throwable::class)
	fun finalize() {
		this.toolkit!!.requestArbBufferCleanup(this.bufferSize, this.id)
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	abstract fun setAsActiveBuffer()

	companion object {
		@JvmField
		val HANDLE: IntArray = IntArray(1)
	}
}
