package com.jagex.game.runetek5.graphics.dx;

import com.jagex.game.runetek5.toolkit.D3DToolkit;
import com.jagex.game.runetek5.toolkit.companion.NativeStream;
import jaclib.memory.Buffer;
import jaclib.memory.Source;
import jagdx.IDirect3DVertexBuffer;
import jagdx.ue;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!aja")
public final class DxVertexBuffer implements NativeStream {

	@OriginalMember(owner = "client!aja", name = "a", descriptor = "I")
	private int size;

	@OriginalMember(owner = "client!aja", name = "d", descriptor = "Lclient!jagdx/IDirect3DVertexBuffer;")
	public IDirect3DVertexBuffer vertexBuffer;

	@OriginalMember(owner = "client!aja", name = "f", descriptor = "B")
	private byte stride;

	@OriginalMember(owner = "client!aja", name = "g", descriptor = "I")
	private int capacity;

	@OriginalMember(owner = "client!aja", name = "c", descriptor = "Z")
	private boolean locked = false;

	@OriginalMember(owner = "client!aja", name = "e", descriptor = "Lclient!bfa;")
	private final D3DToolkit renderer;

	@OriginalMember(owner = "client!aja", name = "b", descriptor = "Z")
	private final boolean dynamic;

	@OriginalMember(owner = "client!aja", name = "<init>", descriptor = "(Lclient!bfa;Z)V")
	public DxVertexBuffer(@OriginalArg(0) D3DToolkit d3DToolkit, @OriginalArg(1) boolean dynamic) {
		this.renderer = d3DToolkit;
		this.dynamic = dynamic;
	}

	@OriginalMember(owner = "client!aja", name = "a", descriptor = "(Z)I")
	@Override
	public int getId() {
		return this.size;
	}

	@OriginalMember(owner = "client!aja", name = "b", descriptor = "(I)V")
	@Override
	public void deallocate() {
		if (this.vertexBuffer != null) {
			this.vertexBuffer.a(9275);
			this.vertexBuffer = null;
		}
		this.capacity = 0;
		this.size = 0;
	}

	@OriginalMember(owner = "client!aja", name = "a", descriptor = "(IBI)Z")
	@Override
	public boolean create(@OriginalArg(0) int size, @OriginalArg(2) int stride) {
		this.stride = (byte) stride;
		this.size = size;
		if (this.size > this.capacity) {
			@Pc(23) short usage = 8;
			@Pc(32) byte pool;
			if (this.dynamic) {
				usage = 520;
				pool = 0;
			} else {
				pool = 1;
			}
			if (this.vertexBuffer != null) {
				this.vertexBuffer.a(9275);
			}
			this.vertexBuffer = this.renderer.iDirect3DDevice.a(this.size, usage, 0, pool, (IDirect3DVertexBuffer) this.vertexBuffer);
			this.capacity = this.size;
		}
		return this.vertexBuffer != null;
	}

	@OriginalMember(owner = "client!aja", name = "a", descriptor = "(ZI)Lclient!jaclib/memory/Buffer;")
	@Override
	public Buffer lock() {
		if (this.vertexBuffer == null) {
			return null;
		}
		@Pc(9) boolean discard = this.dynamic & true;
		if (!this.locked && ue.a(this.vertexBuffer.Lock(0, this.capacity, discard ? 8192 : 0, this.renderer.geometryBuffer), false)) {
			this.locked = true;
			return this.renderer.geometryBuffer;
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!aja", name = "a", descriptor = "(Lclient!jaclib/memory/Source;IIB)Z")
	@Override
	public boolean createFromSource(@OriginalArg(0) Source source, @OriginalArg(1) int stride, @OriginalArg(2) int size) {
		return this.create(size, stride) ? this.vertexBuffer.a(source, 0, 0, this.size, this.dynamic ? 8192 : 0) : false;
	}

	@OriginalMember(owner = "client!aja", name = "a", descriptor = "(I)Z")
	@Override
	public boolean unlock() {
		if (this.locked && ue.a(this.vertexBuffer.Unlock(), false)) {
			this.locked = false;
			return true;
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!aja", name = "c", descriptor = "(I)I")
	public int getStride() {
		return this.stride;
	}
}
