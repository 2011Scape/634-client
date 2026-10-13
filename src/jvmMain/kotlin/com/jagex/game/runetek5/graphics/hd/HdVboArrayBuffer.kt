/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlVboArrayBuffer (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import jaclib.memory.Buffer
import com.jagex.game.runetek5.buffer.ArrayBufferGL

class HdVboArrayBuffer : HdAbstractArbBuffer, ArrayBufferGL {

	private var _stride: Int

	constructor(glToolkit: HdToolkit?, stride: Int, data: ByteArray?, bufferSize: Int, streamDraw: Boolean) : super(glToolkit, 34962, data, bufferSize, streamDraw) {
		this._stride = stride
	}

	constructor(glToolkit: HdToolkit?, _stride: Int, buffer: Buffer?, bufferSize: Int, streamDraw: Boolean) : super(glToolkit, 34962, buffer, bufferSize, streamDraw) {
		this._stride = _stride
	}

	override fun getStride(): Int {
		return this._stride
	}

	override fun setAsActiveBuffer() {
		super.toolkit!!.setArrayBuffer(this)
	}

	override fun putArray(_stride: Int, bufferSize: Int, data: ByteArray?) {
		this.put(data, bufferSize)
		this._stride = _stride
	}

	override fun getId(): Int {
		return super.id
	}

	override fun getAddress(): Long {
		return 0L
	}
}
