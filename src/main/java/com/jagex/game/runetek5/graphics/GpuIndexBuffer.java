package com.jagex.game.runetek5.graphics;

import com.jagex.game.runetek5.DataType;
import com.jagex.game.runetek5.config.Identifiable;
import jaclib.memory.Buffer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!uq")
public interface GpuIndexBuffer extends Identifiable {

	@OriginalMember(owner = "client!uq", name = "a", descriptor = "(I)Z")
	public boolean unlock();

	@OriginalMember(owner = "client!uq", name = "b", descriptor = "(I)V")
	public void release();

	@OriginalMember(owner = "client!uq", name = "a", descriptor = "(II)V")
	public void allocate(@OriginalArg(1) int count);

	@OriginalMember(owner = "client!uq", name = "a", descriptor = "(ZZ)Lclient!jaclib/memory/Buffer;")
	public Buffer lock();

	@OriginalMember(owner = "client!uq", name = "a", descriptor = "(B)Lclient!baa;")
	public DataType getDataType();
}
