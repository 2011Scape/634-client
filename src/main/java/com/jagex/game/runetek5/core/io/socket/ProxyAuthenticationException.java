package com.jagex.game.runetek5.core.io.socket;

import java.io.IOException;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!gv")
public final class ProxyAuthenticationException extends IOException {

	@OriginalMember(owner = "client!gv", name = "<init>", descriptor = "(Ljava/lang/String;)V")
	public ProxyAuthenticationException(@OriginalArg(0) String string) {
		super(string);
	}
}
