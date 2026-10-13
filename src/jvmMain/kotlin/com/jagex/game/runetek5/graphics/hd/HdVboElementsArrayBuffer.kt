/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlVboElementsArrayBuffer (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.buffer.ElementsBufferGL

class HdVboElementsArrayBuffer : HdAbstractArbBuffer, ElementsBufferGL {

	private var indexType: Int

	constructor(glToolkit: HdToolkit?, indexType: Int, data: ByteArray?, bufferSize: Int, streamDraw: Boolean) : super(glToolkit, 34963, data, bufferSize, streamDraw) {
		this.indexType = indexType
	}

	override fun setAsActiveBuffer() {
		super.toolkit!!.setElementsBuffer(this)
	}

	override fun getIndexType(): Int {
		return this.indexType
	}

	override fun putElements(data: ByteArray?, bufferSize: Int) {
		this.put(data, bufferSize)
		this.indexType = 5123
	}

	override fun getId(): Int {
		return super.id
	}

	override fun getAddress(): Long {
		return 0L
	}

	companion object {
		private val BASE64_CHARS: CharArray = CharArray(64)

		init {
			for (i in 0 until 26) {
				BASE64_CHARS[i] = (i + 65).toChar()
			}
			for (i in 26 until 52) {
				BASE64_CHARS[i] = (i + 71).toChar()
			}
			for (i in 52 until 62) {
				BASE64_CHARS[i] = (i - 4).toChar()
			}
			BASE64_CHARS[63] = '-'
			BASE64_CHARS[62] = '*'
		}
	}
}
