package com.jagex.game.runetek5.graphics.loginscreen;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!aaa")
public interface DrawableItem {

	@OriginalMember(owner = "client!aaa", name = "a", descriptor = "(I)V")
	public void init();

	@OriginalMember(owner = "client!aaa", name = "a", descriptor = "(B)Z")
	public boolean ready();

	@OriginalMember(owner = "client!aaa", name = "a", descriptor = "(BZ)V")
	public void execute();
}
