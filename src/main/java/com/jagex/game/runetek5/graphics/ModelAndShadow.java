package com.jagex.game.runetek5.graphics;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.graphics.model.Model;

@OriginalClass("client!ak")
public final class ModelAndShadow {

	@OriginalMember(owner = "client!ak", name = "c", descriptor = "Lclient!r;")
	public Shadow shadow;

	@OriginalMember(owner = "client!ak", name = "d", descriptor = "Lclient!ka;")
	public Model model;
}
