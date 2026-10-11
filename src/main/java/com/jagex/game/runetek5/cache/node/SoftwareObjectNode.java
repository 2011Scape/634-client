package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.sw.SoftwareObject;

@OriginalClass("client!ms")
public final class SoftwareObjectNode extends Node {

	@OriginalMember(owner = "client!ms", name = "o", descriptor = "Lclient!e;")
	public SoftwareObject object;
}
