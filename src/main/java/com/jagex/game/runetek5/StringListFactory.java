package com.jagex.game.runetek5;

import com.jagex.game.runetek5.core.datastruct.StringList;
import org.openrs2.deob.annotation.OriginalMember;

public final class StringListFactory {

	@OriginalMember(owner = "client!mu", name = "a", descriptor = "(BI)Lclient!bo;")
	public static StringList createStringList() {
		return new StringList(1, false);
	}
	@OriginalMember(owner = "client!iia", name = "a", descriptor = "(B)Lclient!bo;")
	public static StringList create() {
		return StringListFactory.createStringList();
	}
}
