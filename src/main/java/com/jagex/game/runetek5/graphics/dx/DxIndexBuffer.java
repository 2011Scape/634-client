package com.jagex.game.runetek5.graphics.dx;

import com.jagex.game.runetek5.DataType;
import com.jagex.game.runetek5.toolkit.D3DToolkit;
import com.jagex.game.runetek5.graphics.GpuIndexBuffer;
import jaclib.memory.Buffer;
import jagdx.IDirect3DIndexBuffer;
import jagdx.ue;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!oe")
public final class DxIndexBuffer implements GpuIndexBuffer {

	@OriginalMember(owner = "client!oe", name = "b", descriptor = "I")
	private int size;

	@OriginalMember(owner = "client!oe", name = "c", descriptor = "Lclient!jagdx/IDirect3DIndexBuffer;")
	public IDirect3DIndexBuffer indexBuffer;

	@OriginalMember(owner = "client!oe", name = "g", descriptor = "I")
	private int capacity;

	@OriginalMember(owner = "client!oe", name = "f", descriptor = "Z")
	private boolean locked = false;

	@OriginalMember(owner = "client!oe", name = "a", descriptor = "Lclient!bfa;")
	private final D3DToolkit d3DToolkit;

	@OriginalMember(owner = "client!oe", name = "e", descriptor = "Z")
	private final boolean dynamic;

	@OriginalMember(owner = "client!oe", name = "d", descriptor = "Lclient!baa;")
	private final DataType dataType;

	@OriginalMember(owner = "client!oe", name = "<init>", descriptor = "(Lclient!bfa;Lclient!baa;Z)V")
	public DxIndexBuffer(@OriginalArg(0) D3DToolkit d3DToolkit, @OriginalArg(1) DataType dataType, @OriginalArg(2) boolean dynamic) {
		this.d3DToolkit = d3DToolkit;
		this.dynamic = dynamic;
		this.dataType = dataType;
	}

	@OriginalMember(owner = "client!oe", name = "a", descriptor = "(B)Lclient!baa;")
	@Override
	public DataType getDataType() {
		return this.dataType;
	}

	@OriginalMember(owner = "client!oe", name = "a", descriptor = "(I)Z")
	@Override
	public boolean unlock() {
		if (this.locked && ue.a(this.indexBuffer.Unlock(), false)) {
			this.locked = false;
			return true;
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!oe", name = "a", descriptor = "(II)V")
	@Override
	public void allocate(@OriginalArg(1) int count) {
		this.size = this.dataType.bytesPerEntry * count;
		if (this.capacity >= this.size) {
			return;
		}
		@Pc(20) short usage = 8;
		@Pc(29) byte pool;
		if (this.dynamic) {
			usage = 520;
			pool = 0;
		} else {
			pool = 1;
		}
		if (this.indexBuffer != null) {
			this.indexBuffer.a(9275);
		}
		this.indexBuffer = this.d3DToolkit.iDirect3DDevice.a(this.size, usage, this.dataType == DataType.aClass26_5 ? 101 : 102, pool, (IDirect3DIndexBuffer) this.indexBuffer);
		this.capacity = this.size;
	}

	@OriginalMember(owner = "client!oe", name = "a", descriptor = "(ZZ)Lclient!jaclib/memory/Buffer;")
	@Override
	public Buffer lock() {
		if (this.indexBuffer == null) {
			return null;
		}
		@Pc(17) boolean discard = this.dynamic & true;
		if (!this.locked && ue.a(this.indexBuffer.Lock(0, this.capacity, discard ? 8192 : 0, this.d3DToolkit.geometryBuffer), false)) {
			this.locked = true;
			return this.d3DToolkit.geometryBuffer;
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!oe", name = "a", descriptor = "(Z)I")
	@Override
	public int getId() {
		return this.size;
	}

	@OriginalMember(owner = "client!oe", name = "b", descriptor = "(I)V")
	@Override
	public void release() {
		if (this.indexBuffer != null) {
			this.indexBuffer.a(9275);
			this.indexBuffer = null;
		}
		this.capacity = 0;
		this.size = 0;
	}
}
