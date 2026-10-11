package com.jagex.game.runetek5;

import com.jagex.game.runetek5.toolkit.companion.NativeStream;
import jaclib.memory.Buffer;
import jaclib.memory.Source;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.toolkit.GlxToolkit;
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArbBuffer;

@OriginalClass("client!bba")
public final class OpenGlArbArrayBuffer extends OpenGlArbBuffer implements NativeStream {

	@OriginalMember(owner = "client!bba", name = "H", descriptor = "B")
	private byte stride;

	@OriginalMember(owner = "client!bba", name = "<init>", descriptor = "(Lclient!bga;Z)V")
	public OpenGlArbArrayBuffer(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) boolean drawStream) {
		super(glxToolkit, 34962, drawStream);
	}

	@OriginalMember(owner = "client!bba", name = "b", descriptor = "(I)V")
	@Override
	public void deallocate() {
		super.deallocate();
	}

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(I)Z")
	@Override
	public boolean unlock() {
		return super.unmap(super.nativeToolkit.arrayMapBuffer);
	}

	@OriginalMember(owner = "client!bba", name = "f", descriptor = "(I)I")
	public int getStride() {
		return this.stride;
	}

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(ZI)Lclient!jaclib/memory/Buffer;")
	@Override
	public Buffer lock() {
		return super.map(super.nativeToolkit.arrayMapBuffer);
	}

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(Z)I")
	@Override
	public int getId() {
		return super.getId();
	}

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(Lclient!jaclib/memory/Source;IIB)Z")
	@Override
	public boolean createFromSource(@OriginalArg(0) Source source, @OriginalArg(1) int stride, @OriginalArg(2) int size) {
		this.stride = (byte) stride;
		super.createFromSource(source, size);
		return true;
	}

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(IBI)Z")
	@Override
	public boolean create(@OriginalArg(0) int size, @OriginalArg(2) int stride) {
		this.stride = (byte) stride;
		super.create(size);
		return true;
	}
}
