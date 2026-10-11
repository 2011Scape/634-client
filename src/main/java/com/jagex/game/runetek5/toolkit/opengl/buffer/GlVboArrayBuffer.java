package com.jagex.game.runetek5.toolkit.opengl.buffer;

import jaclib.memory.Buffer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.buffer.ArrayBufferGL;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

@OriginalClass("client!jt")
public final class GlVboArrayBuffer extends AbstractArbBufferGL implements ArrayBufferGL {

	@OriginalMember(owner = "client!jt", name = "m", descriptor = "I")
	private int _stride;

	@OriginalMember(owner = "client!jt", name = "<init>", descriptor = "(Lclient!qo;I[BIZ)V")
	public GlVboArrayBuffer(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int stride, @OriginalArg(2) byte[] data, @OriginalArg(3) int bufferSize, @OriginalArg(4) boolean streamDraw) {
		super(glToolkit, 34962, data, bufferSize, streamDraw);
		this._stride = stride;
	}

	@OriginalMember(owner = "client!jt", name = "<init>", descriptor = "(Lclient!qo;ILclient!jaclib/memory/Buffer;IZ)V")
	public GlVboArrayBuffer(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int _stride, @OriginalArg(2) Buffer buffer, @OriginalArg(3) int bufferSize, @OriginalArg(4) boolean streamDraw) {
		super(glToolkit, 34962, buffer, bufferSize, streamDraw);
		this._stride = _stride;
	}

	@OriginalMember(owner = "client!jt", name = "b", descriptor = "(B)I")
	@Override
	public int getStride() {
		return this._stride;
	}

	@OriginalMember(owner = "client!jt", name = "d", descriptor = "(I)V")
	@Override
	public void setAsActiveBuffer() {
		super.toolkit.setArrayBuffer(this);
	}

	@OriginalMember(owner = "client!jt", name = "a", descriptor = "(II[BI)V")
	@Override
	public void putArray(@OriginalArg(0) int _stride, @OriginalArg(1) int bufferSize, @OriginalArg(2) byte[] data) {
		this.put(data, bufferSize);
		this._stride = _stride;
	}

	@OriginalMember(owner = "client!jt", name = "a", descriptor = "(Z)I")
	@Override
	public int getId() {
		return super.id;
	}

	@OriginalMember(owner = "client!jt", name = "a", descriptor = "(B)J")
	@Override
	public long getAddress() {
		return 0L;
	}
}
