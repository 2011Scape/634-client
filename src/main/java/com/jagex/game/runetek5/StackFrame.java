package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.lru.ClientScript;

@OriginalClass("client!qea")
public final class StackFrame {

	@OriginalMember(owner = "client!qea", name = "a", descriptor = "[I")
	public int[] intVars;

	@OriginalMember(owner = "client!qea", name = "f", descriptor = "[Ljava/lang/String;")
	public String[] stringVars;

	@OriginalMember(owner = "client!qea", name = "g", descriptor = "Lclient!jk;")
	public ClientScript script;

	@OriginalMember(owner = "client!qea", name = "j", descriptor = "I")
	public int pc = -1;
}
