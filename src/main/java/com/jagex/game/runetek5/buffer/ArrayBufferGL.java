package com.jagex.game.runetek5.buffer;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!hca")
public interface ArrayBufferGL {

	@OriginalMember(owner = "client!hca", name = "a", descriptor = "(Z)I")
	public int getId();

	@OriginalMember(owner = "client!hca", name = "a", descriptor = "(II[BI)V")
	public void putArray(@OriginalArg(0) int stride, @OriginalArg(1) int size, @OriginalArg(2) byte[] data);

	@OriginalMember(owner = "client!hca", name = "a", descriptor = "(B)J")
	public long getAddress();

	@OriginalMember(owner = "client!hca", name = "b", descriptor = "(B)I")
	public int getStride();
}
