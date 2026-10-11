package com.jagex.game.runetek5.graphics.sw;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!e")
public interface SoftwareObject {

	@OriginalMember(owner = "client!e", name = "w", descriptor = "(Z)V")
	public void w(@OriginalArg(0) boolean arg0);
}
