package com.jagex.game.runetek5.toolkit.opengl.buffer;

import jaclib.memory.Buffer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.buffer.ArrayBufferGL;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

@OriginalClass("client!cw")
public final class GlArrayBuffer extends GlBuffer implements ArrayBufferGL {

	@OriginalMember(owner = "client!cw", name = "c", descriptor = "I")
	private int stride;

	@OriginalMember(owner = "client!cw", name = "<init>", descriptor = "(Lclient!qo;I[BI)V")
	public GlArrayBuffer(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int stride, @OriginalArg(2) byte[] buffer, @OriginalArg(3) int initialCapacity) {
		super(glToolkit, buffer, initialCapacity);
		this.stride = stride;
	}

	@OriginalMember(owner = "client!cw", name = "<init>", descriptor = "(Lclient!qo;ILclient!jaclib/memory/Buffer;)V")
	public GlArrayBuffer(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int stride, @OriginalArg(2) Buffer buffer) {
		super(glToolkit, buffer);
		this.stride = stride;
	}

	@OriginalMember(owner = "client!cw", name = "a", descriptor = "(B)J")
	@Override
	public long getAddress() {
		return super.buffer.getAddress();
	}

	@OriginalMember(owner = "client!cw", name = "b", descriptor = "(B)I")
	@Override
	public int getStride() {
		return this.stride;
	}

	@OriginalMember(owner = "client!cw", name = "a", descriptor = "(II[BI)V")
	@Override
	public void putArray(@OriginalArg(0) int stride, @OriginalArg(1) int requiredSize, @OriginalArg(2) byte[] data) {
		this.put(data, requiredSize);
		this.stride = stride;
	}

	@OriginalMember(owner = "client!cw", name = "a", descriptor = "(Z)I")
	@Override
	public int getId() {
		return 0;
	}
}
