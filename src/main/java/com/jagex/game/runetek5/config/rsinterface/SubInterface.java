package com.jagex.game.runetek5.config.rsinterface;

import com.jagex.game.runetek5.config.iftype.SubInterfaceType;
import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!uba")
public final class SubInterface extends Node {

	@OriginalMember(owner = "client!uba", name = "s", descriptor = "I")
	public int interfaceId;

	@OriginalMember(owner = "client!uba", name = "v", descriptor = "I")
	public SubInterfaceType openType;
}
