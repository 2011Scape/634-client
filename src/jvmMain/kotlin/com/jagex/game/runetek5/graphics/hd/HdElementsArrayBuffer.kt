/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlElementsArrayBuffer (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.buffer.ElementsBufferGL

class HdElementsArrayBuffer : HdBuffer, ElementsBufferGL {

	private var indexType: Int

	constructor(glToolkit: HdToolkit?, indexType: Int, data: ByteArray?, initialCapacity: Int) : super(glToolkit, data, initialCapacity) {
		this.indexType = indexType
	}

	override fun getId(): Int {
		return 0
	}

	override fun getAddress(): Long {
		return super.buffer!!.getAddress()
	}

	override fun getIndexType(): Int {
		return this.indexType
	}

	override fun putElements(data: ByteArray?, requiredSize: Int) {
		this.put(data, requiredSize)
		this.indexType = 5123
	}
}
