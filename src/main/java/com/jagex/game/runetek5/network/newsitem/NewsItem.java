package com.jagex.game.runetek5.network.newsitem;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!wc")
public final class NewsItem {

	@OriginalMember(owner = "client!wc", name = "d", descriptor = "Ljava/lang/String;")
	public final String line1;

	@OriginalMember(owner = "client!wc", name = "h", descriptor = "Ljava/lang/String;")
	public final String line2;

	@OriginalMember(owner = "client!wc", name = "f", descriptor = "Ljava/lang/String;")
	public final String line3;

	@OriginalMember(owner = "client!wc", name = "<init>", descriptor = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
	public NewsItem(@OriginalArg(0) String string, @OriginalArg(1) String string2, @OriginalArg(2) String string3) {
		this.line1 = string2;
		this.line2 = string3;
		this.line3 = string;
	}
}
