package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.GpuIndexBuffer;
import jaclib.memory.Buffer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.toolkit.GlxToolkit;
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArbBuffer;

@OriginalClass("client!mw")
public final class OpenGlArbElementArrayBuffer extends OpenGlArbBuffer implements GpuIndexBuffer {

	@OriginalMember(owner = "client!mw", name = "H", descriptor = "Lclient!baa;")
	private final DataType actionFormat;

	@OriginalMember(owner = "client!mw", name = "<init>", descriptor = "(Lclient!bga;Lclient!baa;Z)V")
	public OpenGlArbElementArrayBuffer(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) DataType actionFormat, @OriginalArg(2) boolean drawStream) {
		super(glxToolkit, 34963, drawStream);
		this.actionFormat = actionFormat;
	}

	@OriginalMember(owner = "client!mw", name = "a", descriptor = "(II)V")
	@Override
	public void allocate(@OriginalArg(1) int count) {
		super.create(count * this.actionFormat.bytesPerEntry);
	}

	@OriginalMember(owner = "client!mw", name = "a", descriptor = "(ZZ)Lclient!jaclib/memory/Buffer;")
	@Override
	public Buffer lock() {
		return super.map(super.nativeToolkit.elementArrayMapBuffer);
	}

	@OriginalMember(owner = "client!mw", name = "a", descriptor = "(Z)I")
	@Override
	public int getId() {
		return super.getId();
	}

	@OriginalMember(owner = "client!mw", name = "a", descriptor = "(I)Z")
	@Override
	public boolean unlock() {
		return super.unmap(super.nativeToolkit.elementArrayMapBuffer);
	}

	@OriginalMember(owner = "client!mw", name = "a", descriptor = "(B)Lclient!baa;")
	@Override
	public DataType getDataType() {
		return this.actionFormat;
	}

	@OriginalMember(owner = "client!mw", name = "b", descriptor = "(I)V")
	@Override
	public void release() {
		super.deallocate();
	}
}
