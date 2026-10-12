package com.jagex.game.runetek5.graphics.dx

import com.jagex.game.runetek5.toolkit.D3DToolkit
import com.jagex.game.runetek5.toolkit.companion.NativeStream
import jaclib.memory.Buffer
import jaclib.memory.Source
import jagdx.IDirect3DVertexBuffer
import jagdx.ue
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!aja")
class DxVertexBuffer : NativeStream {

	@OriginalMember(owner = "client!aja", name = "a", descriptor = "I")
	private var size: Int = 0

	@OriginalMember(owner = "client!aja", name = "d", descriptor = "Lclient!jagdx/IDirect3DVertexBuffer;")
	@JvmField
	var vertexBuffer: IDirect3DVertexBuffer? = null

	@OriginalMember(owner = "client!aja", name = "f", descriptor = "B")
	private var stride: Byte = 0

	@OriginalMember(owner = "client!aja", name = "g", descriptor = "I")
	private var capacity: Int = 0

	@OriginalMember(owner = "client!aja", name = "c", descriptor = "Z")
	private var locked: Boolean = false

	@OriginalMember(owner = "client!aja", name = "e", descriptor = "Lclient!bfa;")
	private val renderer: D3DToolkit?

	@OriginalMember(owner = "client!aja", name = "b", descriptor = "Z")
	private val dynamic: Boolean

	@OriginalMember(owner = "client!aja", name = "<init>", descriptor = "(Lclient!bfa;Z)V")
	constructor(@OriginalArg(0) d3DToolkit: D3DToolkit?, @OriginalArg(1) dynamic: Boolean) {
		this.renderer = d3DToolkit
		this.dynamic = dynamic
	}

	@OriginalMember(owner = "client!aja", name = "a", descriptor = "(Z)I")
	override fun getId(): Int {
		return this.size
	}

	@OriginalMember(owner = "client!aja", name = "b", descriptor = "(I)V")
	override fun deallocate() {
		if (this.vertexBuffer != null) {
			this.vertexBuffer!!.a(9275)
			this.vertexBuffer = null
		}
		this.capacity = 0
		this.size = 0
	}

	@OriginalMember(owner = "client!aja", name = "a", descriptor = "(IBI)Z")
	override fun create(@OriginalArg(0) size: Int, @OriginalArg(2) stride: Int): Boolean {
		this.stride = stride.toByte()
		this.size = size
		if (this.size > this.capacity) {
			@Pc(23) var usage: Short = 8
			@Pc(32) val pool: Byte
			if (this.dynamic) {
				usage = 520
				pool = 0
			} else {
				pool = 1
			}
			if (this.vertexBuffer != null) {
				this.vertexBuffer!!.a(9275)
			}
			this.vertexBuffer = this.renderer!!.iDirect3DDevice!!.a(this.size, usage.toInt(), 0, pool.toInt(), this.vertexBuffer as IDirect3DVertexBuffer?)
			this.capacity = this.size
		}
		return this.vertexBuffer != null
	}

	@OriginalMember(owner = "client!aja", name = "a", descriptor = "(ZI)Lclient!jaclib/memory/Buffer;")
	override fun lock(): Buffer? {
		if (this.vertexBuffer == null) {
			return null
		}
		@Pc(9) val discard: Boolean = this.dynamic and true
		if (!this.locked && ue.a(this.vertexBuffer!!.Lock(0, this.capacity, if (discard) 8192 else 0, this.renderer!!.geometryBuffer), false)) {
			this.locked = true
			return this.renderer.geometryBuffer
		} else {
			return null
		}
	}

	@OriginalMember(owner = "client!aja", name = "a", descriptor = "(Lclient!jaclib/memory/Source;IIB)Z")
	override fun createFromSource(@OriginalArg(0) source: Source?, @OriginalArg(1) stride: Int, @OriginalArg(2) size: Int): Boolean {
		return if (this.create(size, stride)) this.vertexBuffer!!.a(source, 0, 0, this.size, if (this.dynamic) 8192 else 0) else false
	}

	@OriginalMember(owner = "client!aja", name = "a", descriptor = "(I)Z")
	override fun unlock(): Boolean {
		if (this.locked && ue.a(this.vertexBuffer!!.Unlock(), false)) {
			this.locked = false
			return true
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!aja", name = "c", descriptor = "(I)I")
	fun getStride(): Int {
		return this.stride.toInt()
	}
}
