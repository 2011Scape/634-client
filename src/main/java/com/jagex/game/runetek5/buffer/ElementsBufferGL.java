package com.jagex.game.runetek5.buffer;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ni")
public interface ElementsBufferGL {

	@OriginalMember(owner = "client!ni", name = "a", descriptor = "(I)I")
	public int getId();

	@OriginalMember(owner = "client!ni", name = "b", descriptor = "(I)I")
	public int getIndexType();

	@OriginalMember(owner = "client!ni", name = "a", descriptor = "([BIII)V")
	public void putElements(@OriginalArg(0) byte[] data, @OriginalArg(3) int size);

	@OriginalMember(owner = "client!ni", name = "a", descriptor = "(B)J")
	public long getAddress();
}
