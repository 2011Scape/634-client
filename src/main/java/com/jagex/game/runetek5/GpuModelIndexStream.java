package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.GpuIndexBuffer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!qq")
public final class GpuModelIndexStream {

	@OriginalMember(owner = "client!qq", name = "a", descriptor = "Lclient!uq;")
	public GpuIndexBuffer activeBuffer;

	@OriginalMember(owner = "client!qq", name = "e", descriptor = "Lclient!uq;")
	public GpuIndexBuffer buffer;

	@OriginalMember(owner = "client!qq", name = "f", descriptor = "Z")
	public boolean uploaded;

	@OriginalMember(owner = "client!qq", name = "c", descriptor = "Z")
	public boolean dynamic;

	@OriginalMember(owner = "client!qq", name = "<init>", descriptor = "(Z)V")
	public GpuModelIndexStream(@OriginalArg(0) boolean dynamic) {
		this.dynamic = dynamic;
	}

	@OriginalMember(owner = "client!qq", name = "a", descriptor = "(B)V")
	public void release() {
		if (this.buffer != null) {
			this.buffer.release();
		}
		this.uploaded = false;
	}

	@OriginalMember(owner = "client!qq", name = "b", descriptor = "(B)Z")
	public boolean canReleaseSourceData() {
		return this.uploaded && !this.dynamic;
	}
}
