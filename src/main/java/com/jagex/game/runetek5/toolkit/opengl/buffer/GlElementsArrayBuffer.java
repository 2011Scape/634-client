package com.jagex.game.runetek5.toolkit.opengl.buffer;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.buffer.ElementsBufferGL;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

@OriginalClass("client!wv")
public final class GlElementsArrayBuffer extends GlBuffer implements ElementsBufferGL {

	@OriginalMember(owner = "client!wv", name = "d", descriptor = "I")
	private int indexType;

	@OriginalMember(owner = "client!wv", name = "<init>", descriptor = "(Lclient!qo;I[BI)V")
	public GlElementsArrayBuffer(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int indexType, @OriginalArg(2) byte[] data, @OriginalArg(3) int initialCapacity) {
		super(glToolkit, data, initialCapacity);
		this.indexType = indexType;
	}

	@OriginalMember(owner = "client!wv", name = "a", descriptor = "(I)I")
	@Override
	public int getId() {
		return 0;
	}

	@OriginalMember(owner = "client!wv", name = "a", descriptor = "(B)J")
	@Override
	public long getAddress() {
		return super.buffer.getAddress();
	}

	@OriginalMember(owner = "client!wv", name = "b", descriptor = "(I)I")
	@Override
	public int getIndexType() {
		return this.indexType;
	}

	@OriginalMember(owner = "client!wv", name = "a", descriptor = "([BIII)V")
	@Override
	public void putElements(@OriginalArg(0) byte[] data, @OriginalArg(3) int requiredSize) {
		this.put(data, requiredSize);
		this.indexType = 5123;
	}
}
