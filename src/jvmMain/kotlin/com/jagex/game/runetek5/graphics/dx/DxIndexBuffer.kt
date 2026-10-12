package com.jagex.game.runetek5.graphics.dx

import com.jagex.game.runetek5.DataType
import com.jagex.game.runetek5.toolkit.D3DToolkit
import com.jagex.game.runetek5.graphics.GpuIndexBuffer
import jaclib.memory.Buffer
import jagdx.IDirect3DIndexBuffer
import jagdx.ue
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!oe")
class DxIndexBuffer : GpuIndexBuffer {

	@OriginalMember(owner = "client!oe", name = "b", descriptor = "I")
	private var size: Int = 0

	@OriginalMember(owner = "client!oe", name = "c", descriptor = "Lclient!jagdx/IDirect3DIndexBuffer;")
	@JvmField
	var indexBuffer: IDirect3DIndexBuffer? = null

	@OriginalMember(owner = "client!oe", name = "g", descriptor = "I")
	private var capacity: Int = 0

	@OriginalMember(owner = "client!oe", name = "f", descriptor = "Z")
	private var locked: Boolean = false

	@OriginalMember(owner = "client!oe", name = "a", descriptor = "Lclient!bfa;")
	private val d3DToolkit: D3DToolkit?

	@OriginalMember(owner = "client!oe", name = "e", descriptor = "Z")
	private val dynamic: Boolean

	@OriginalMember(owner = "client!oe", name = "d", descriptor = "Lclient!baa;")
	private val dataType: DataType?

	@OriginalMember(owner = "client!oe", name = "<init>", descriptor = "(Lclient!bfa;Lclient!baa;Z)V")
	constructor(@OriginalArg(0) d3DToolkit: D3DToolkit?, @OriginalArg(1) dataType: DataType?, @OriginalArg(2) dynamic: Boolean) {
		this.d3DToolkit = d3DToolkit
		this.dynamic = dynamic
		this.dataType = dataType
	}

	@OriginalMember(owner = "client!oe", name = "a", descriptor = "(B)Lclient!baa;")
	override fun getDataType(): DataType? {
		return this.dataType
	}

	@OriginalMember(owner = "client!oe", name = "a", descriptor = "(I)Z")
	override fun unlock(): Boolean {
		if (this.locked && ue.a(this.indexBuffer!!.Unlock(), false)) {
			this.locked = false
			return true
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!oe", name = "a", descriptor = "(II)V")
	override fun allocate(@OriginalArg(1) count: Int) {
		this.size = this.dataType!!.bytesPerEntry * count
		if (this.capacity >= this.size) {
			return
		}
		@Pc(20) var usage: Short = 8
		@Pc(29) val pool: Byte
		if (this.dynamic) {
			usage = 520
			pool = 0
		} else {
			pool = 1
		}
		if (this.indexBuffer != null) {
			this.indexBuffer!!.a(9275)
		}
		this.indexBuffer = this.d3DToolkit!!.iDirect3DDevice!!.a(this.size, usage.toInt(), if (this.dataType === DataType.aClass26_5) 101 else 102, pool.toInt(), this.indexBuffer as IDirect3DIndexBuffer?)
		this.capacity = this.size
	}

	@OriginalMember(owner = "client!oe", name = "a", descriptor = "(ZZ)Lclient!jaclib/memory/Buffer;")
	override fun lock(): Buffer? {
		if (this.indexBuffer == null) {
			return null
		}
		@Pc(17) val discard: Boolean = this.dynamic and true
		if (!this.locked && ue.a(this.indexBuffer!!.Lock(0, this.capacity, if (discard) 8192 else 0, this.d3DToolkit!!.geometryBuffer), false)) {
			this.locked = true
			return this.d3DToolkit.geometryBuffer
		} else {
			return null
		}
	}

	@OriginalMember(owner = "client!oe", name = "a", descriptor = "(Z)I")
	override fun getId(): Int {
		return this.size
	}

	@OriginalMember(owner = "client!oe", name = "b", descriptor = "(I)V")
	override fun release() {
		if (this.indexBuffer != null) {
			this.indexBuffer!!.a(9275)
			this.indexBuffer = null
		}
		this.capacity = 0
		this.size = 0
	}
}
