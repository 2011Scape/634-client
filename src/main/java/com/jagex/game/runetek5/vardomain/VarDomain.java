package com.jagex.game.runetek5.vardomain;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!dd")
public interface VarDomain {

	@OriginalMember(owner = "client!dd", name = "a", descriptor = "(IB)I")
	public int getVarValueInt(@OriginalArg(0) int id);

	@OriginalMember(owner = "client!dd", name = "a", descriptor = "(II)I")
	public int getVarBitValue(@OriginalArg(0) int id);
}
