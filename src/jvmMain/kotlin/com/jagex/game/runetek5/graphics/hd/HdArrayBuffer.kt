/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlArrayBuffer (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import jaclib.memory.Buffer
import com.jagex.game.runetek5.buffer.ArrayBufferGL

class HdArrayBuffer : HdBuffer, ArrayBufferGL {

	private var stride: Int

	constructor(glToolkit: HdToolkit?, stride: Int, buffer: ByteArray?, initialCapacity: Int) : super(glToolkit, buffer, initialCapacity) {
		this.stride = stride
	}

	constructor(glToolkit: HdToolkit?, stride: Int, buffer: Buffer?) : super(glToolkit, buffer) {
		this.stride = stride
	}

	override fun getAddress(): Long {
		return super.buffer!!.getAddress()
	}

	override fun getStride(): Int {
		return this.stride
	}

	override fun putArray(stride: Int, requiredSize: Int, data: ByteArray?) {
		this.put(data, requiredSize)
		this.stride = stride
	}

	override fun getId(): Int {
		return 0
	}
}
