package com.jagex.game.runetek5.graphics.loginscreen;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!dca")
public interface LoginScreenComponent {

	@OriginalMember(owner = "client!dca", name = "a", descriptor = "(B)I")
	public int getFadeDuration();

	@OriginalMember(owner = "client!dca", name = "a", descriptor = "(BJ)Z")
	public boolean finished(@OriginalArg(1) long initTime);

	@OriginalMember(owner = "client!dca", name = "a", descriptor = "(I)V")
	public void init();

	@OriginalMember(owner = "client!dca", name = "a", descriptor = "(ZI)V")
	public void render(@OriginalArg(0) boolean fullRepaint);

	@OriginalMember(owner = "client!dca", name = "b", descriptor = "(I)V")
	public void cleanup();

	@OriginalMember(owner = "client!dca", name = "c", descriptor = "(I)I")
	public int percentage();
}
