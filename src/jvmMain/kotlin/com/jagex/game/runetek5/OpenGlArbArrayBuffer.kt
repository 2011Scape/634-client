package com.jagex.game.runetek5

import com.jagex.game.runetek5.toolkit.companion.NativeStream
import jaclib.memory.Buffer
import jaclib.memory.Source
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.toolkit.GlxToolkit
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArbBuffer

@OriginalClass("client!bba")
class OpenGlArbArrayBuffer : OpenGlArbBuffer, NativeStream {

	@OriginalMember(owner = "client!bba", name = "H", descriptor = "B")
	private var stride: Byte = 0

	@OriginalMember(owner = "client!bba", name = "<init>", descriptor = "(Lclient!bga;Z)V")
	constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) drawStream: Boolean) : super(glxToolkit, 34962, drawStream)

	@OriginalMember(owner = "client!bba", name = "b", descriptor = "(I)V")
	override fun deallocate() {
		super.deallocate()
	}

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(I)Z")
	override fun unlock(): Boolean {
		return super.unmap(super.nativeToolkit!!.arrayMapBuffer)
	}

	@OriginalMember(owner = "client!bba", name = "f", descriptor = "(I)I")
	fun getStride(): Int {
		return this.stride.toInt()
	}

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(ZI)Lclient!jaclib/memory/Buffer;")
	override fun lock(): Buffer? {
		return super.map(super.nativeToolkit!!.arrayMapBuffer)
	}

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(Z)I")
	override fun getId(): Int {
		return super.getId()
	}

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(Lclient!jaclib/memory/Source;IIB)Z")
	override fun createFromSource(@OriginalArg(0) source: Source?, @OriginalArg(1) stride: Int, @OriginalArg(2) size: Int): Boolean {
		this.stride = stride.toByte()
		super.createFromSource(source, size)
		return true
	}

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(IBI)Z")
	override fun create(@OriginalArg(0) size: Int, @OriginalArg(2) stride: Int): Boolean {
		this.stride = stride.toByte()
		super.create(size)
		return true
	}
}
