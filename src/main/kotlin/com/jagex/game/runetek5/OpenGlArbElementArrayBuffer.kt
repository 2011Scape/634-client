package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.GpuIndexBuffer
import jaclib.memory.Buffer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.toolkit.GlxToolkit
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArbBuffer

@OriginalClass("client!mw")
class OpenGlArbElementArrayBuffer : OpenGlArbBuffer, GpuIndexBuffer {

	@OriginalMember(owner = "client!mw", name = "H", descriptor = "Lclient!baa;")
	private val actionFormat: DataType?

	@OriginalMember(owner = "client!mw", name = "<init>", descriptor = "(Lclient!bga;Lclient!baa;Z)V")
	constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) actionFormat: DataType?, @OriginalArg(2) drawStream: Boolean) : super(glxToolkit, 34963, drawStream) {
		this.actionFormat = actionFormat
	}

	@OriginalMember(owner = "client!mw", name = "a", descriptor = "(II)V")
	override fun allocate(@OriginalArg(1) count: Int) {
		super.create(count * this.actionFormat!!.bytesPerEntry)
	}

	@OriginalMember(owner = "client!mw", name = "a", descriptor = "(ZZ)Lclient!jaclib/memory/Buffer;")
	override fun lock(): Buffer? {
		return super.map(super.nativeToolkit!!.elementArrayMapBuffer)
	}

	@OriginalMember(owner = "client!mw", name = "a", descriptor = "(Z)I")
	override fun getId(): Int {
		return super.getId()
	}

	@OriginalMember(owner = "client!mw", name = "a", descriptor = "(I)Z")
	override fun unlock(): Boolean {
		return super.unmap(super.nativeToolkit!!.elementArrayMapBuffer)
	}

	@OriginalMember(owner = "client!mw", name = "a", descriptor = "(B)Lclient!baa;")
	override fun getDataType(): DataType? {
		return this.actionFormat
	}

	@OriginalMember(owner = "client!mw", name = "b", descriptor = "(I)V")
	override fun release() {
		super.deallocate()
	}
}
