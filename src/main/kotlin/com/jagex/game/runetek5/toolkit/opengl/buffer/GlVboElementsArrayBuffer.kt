package com.jagex.game.runetek5.toolkit.opengl.buffer

import com.jagex.game.runetek5.buffer.ElementsBufferGL
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!kaa")
class GlVboElementsArrayBuffer : AbstractArbBufferGL, ElementsBufferGL {

	@OriginalMember(owner = "client!kaa", name = "o", descriptor = "I")
	private var indexType: Int

	@OriginalMember(owner = "client!kaa", name = "<init>", descriptor = "(Lclient!qo;I[BIZ)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) indexType: Int, @OriginalArg(2) data: ByteArray?, @OriginalArg(3) bufferSize: Int, @OriginalArg(4) streamDraw: Boolean) : super(glToolkit, 34963, data, bufferSize, streamDraw) {
		this.indexType = indexType
	}

	@OriginalMember(owner = "client!kaa", name = "d", descriptor = "(I)V")
	override fun setAsActiveBuffer() {
		super.toolkit!!.setElementsBuffer(this)
	}

	@OriginalMember(owner = "client!kaa", name = "b", descriptor = "(I)I")
	override fun getIndexType(): Int {
		return this.indexType
	}

	@OriginalMember(owner = "client!kaa", name = "a", descriptor = "([BIII)V")
	override fun putElements(@OriginalArg(0) data: ByteArray?, @OriginalArg(3) bufferSize: Int) {
		this.put(data, bufferSize)
		this.indexType = 5123
	}

	@OriginalMember(owner = "client!kaa", name = "a", descriptor = "(I)I")
	override fun getId(): Int {
		return super.id
	}

	@OriginalMember(owner = "client!kaa", name = "a", descriptor = "(B)J")
	override fun getAddress(): Long {
		return 0L
	}

	companion object {
		@OriginalMember(owner = "client!kaa", name = "w", descriptor = "[C")
		private val BASE64_CHARS: CharArray = CharArray(64)

		init {
			for (@Pc(8) i in 0 until 26) {
				BASE64_CHARS[i] = (i + 65).toChar()
			}
			for (@Pc(24) i in 26 until 52) {
				BASE64_CHARS[i] = (i + 71).toChar()
			}
			for (@Pc(40) i in 52 until 62) {
				BASE64_CHARS[i] = (i - 4).toChar()
			}
			BASE64_CHARS[63] = '-'
			BASE64_CHARS[62] = '*'
		}
	}
}
