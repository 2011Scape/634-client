package com.jagex.game.runetek5.entity;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!we")
public interface ComparableGraphNode {

	@OriginalMember(owner = "client!we", name = "a", descriptor = "(B)J")
	public long toLong();

	@OriginalMember(owner = "client!we", name = "a", descriptor = "(ILclient!we;)Z")
	public boolean matches(@OriginalArg(1) ComparableGraphNode other);
}
