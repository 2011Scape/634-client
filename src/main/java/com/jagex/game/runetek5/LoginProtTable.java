package com.jagex.game.runetek5;

import com.jagex.game.runetek5.client.login.LoginProt;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!qa")
public final class LoginProtTable {

	@OriginalMember(owner = "client!qa", name = "s", descriptor = "[Lclient!hv;")
	private static final LoginProt[] OUTGOING_LOGIN_PACKETS = new LoginProt[32];

	static {
		@Pc(95) LoginProt[] packets = LoginProt.loginProtValues();
		for (@Pc(97) int i = 0; i < packets.length; i++) {
			OUTGOING_LOGIN_PACKETS[packets[i].id] = packets[i];
		}
	}
}
