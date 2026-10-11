package com.jagex.game.runetek5.client;

import com.jagex.game.runetek5.ModeWhere;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class ServerEnvironment {
	@OriginalMember(owner = "client!gr", name = "g", descriptor = "Lclient!fh;")
	public static ModeWhere LIVE;

	@OriginalMember(owner = "client!pea", name = "b", descriptor = "I")
	public static int flashCycleCounter;

	private ServerEnvironment() {
		/* empty */
	}
	@OriginalMember(owner = "client!tia", name = "a", descriptor = "(Z)Ljava/lang/String;")
	public static String getUrl() {
		@Pc(12) String string = "www";
		if (ModeWhere.aClass107_4 == ServerEnvironment.LIVE) {
			string = "www-wtrc";
		} else if (ServerEnvironment.LIVE == ModeWhere.MODE_WHERE_WTQA) {
			string = "www-wtqa";
		} else if (ServerEnvironment.LIVE == ModeWhere.aClass107_6) {
			string = "www-wtwip";
		}
		@Pc(33) String string2 = "";
		if (GameShell.siteSettings != null) {
			string2 = "/p=" + GameShell.siteSettings;
		}
		return "http://" + string + "." + Game.CURRENT_GAME.domainName + ".com/l=" + ClientState.language + "/a=" + GameShell.AFFILIATE + string2 + "/";
	}
}
