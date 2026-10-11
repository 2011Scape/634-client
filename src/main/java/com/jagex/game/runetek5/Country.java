package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!vh")
public final class Country {

	@OriginalMember(owner = "client!vh", name = "b", descriptor = "I")
	public int flag;

	@OriginalMember(owner = "client!vh", name = "h", descriptor = "Ljava/lang/String;")
	public String name;
	@OriginalMember(owner = "client!bd", name = "x", descriptor = "[Lclient!vh;")
	public static Country[] aClass359Array1;
}
