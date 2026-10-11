package com.jagex.game.runetek5;

import com.jagex.game.runetek5.toolkit.companion.NativeStream;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!cd")
public final class GpuModelVertexStream {

	@OriginalMember(owner = "client!cd", name = "c", descriptor = "Lclient!tc;")
	public NativeStream buffer;

	@OriginalMember(owner = "client!cd", name = "f", descriptor = "Lclient!tc;")
	public NativeStream activeBuffer;

	@OriginalMember(owner = "client!cd", name = "i", descriptor = "Z")
	public boolean uploaded;

	@OriginalMember(owner = "client!cd", name = "d", descriptor = "Z")
	public boolean dynamic;

	@OriginalMember(owner = "client!cd", name = "<init>", descriptor = "(Z)V")
	public GpuModelVertexStream(@OriginalArg(0) boolean dynamic) {
		this.dynamic = dynamic;
	}

	@OriginalMember(owner = "client!cd", name = "a", descriptor = "(Z)V")
	public void release() {
		if (this.buffer != null) {
			this.buffer.deallocate();
		}
		this.uploaded = false;
	}

	@OriginalMember(owner = "client!cd", name = "b", descriptor = "(B)Z")
	public boolean canReleaseSourceData() {
		return this.uploaded && !this.dynamic;
	}
}
