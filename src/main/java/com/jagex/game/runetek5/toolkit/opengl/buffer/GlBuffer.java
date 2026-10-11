package com.jagex.game.runetek5.toolkit.opengl.buffer;

import jaclib.memory.Buffer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

@OriginalClass("client!nf")
public class GlBuffer {

	@OriginalMember(owner = "client!nf", name = "b", descriptor = "Lclient!qo;")
	private final GlToolkit toolkit;

	@OriginalMember(owner = "client!nf", name = "a", descriptor = "Lclient!jaclib/memory/Buffer;")
	public Buffer buffer;

	@OriginalMember(owner = "client!nf", name = "<init>", descriptor = "(Lclient!qo;[BI)V")
	public GlBuffer(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) byte[] data, @OriginalArg(2) int initialCapacity) {
		this.toolkit = glToolkit;
		this.buffer = this.toolkit.nativeHeap.a(initialCapacity, false);
		if (data != null) {
			this.buffer.a(data, 0, 0, initialCapacity);
		}
	}

	@OriginalMember(owner = "client!nf", name = "<init>", descriptor = "(Lclient!qo;Lclient!jaclib/memory/Buffer;)V")
	public GlBuffer(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) Buffer buffer) {
		this.toolkit = glToolkit;
		this.buffer = buffer;
	}

	@OriginalMember(owner = "client!nf", name = "a", descriptor = "([BI)V")
	public final void put(@OriginalArg(0) byte[] data, @OriginalArg(1) int requiredSize) {
		if (this.buffer == null || this.buffer.getSize() < requiredSize) {
			this.buffer = this.toolkit.nativeHeap.a(requiredSize, false);
		}
		this.buffer.a(data, 0, 0, requiredSize);
	}
}
