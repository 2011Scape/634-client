/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlBuffer (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import jaclib.memory.Buffer
import kotlin.jvm.JvmField

open class HdBuffer {

	private val toolkit: HdToolkit?

	@JvmField
	var buffer: Buffer?

	constructor(glToolkit: HdToolkit?, data: ByteArray?, initialCapacity: Int) {
		this.toolkit = glToolkit
		this.buffer = this.toolkit!!.nativeHeap!!.a(initialCapacity, false)
		if (data != null) {
			this.buffer!!.a(data, 0, 0, initialCapacity)
		}
	}

	constructor(glToolkit: HdToolkit?, buffer: Buffer?) {
		this.toolkit = glToolkit
		this.buffer = buffer
	}

	fun put(data: ByteArray?, requiredSize: Int) {
		if (this.buffer == null || this.buffer!!.getSize() < requiredSize) {
			this.buffer = this.toolkit!!.nativeHeap!!.a(requiredSize, false)
		}
		this.buffer!!.a(data, 0, 0, requiredSize)
	}
}
