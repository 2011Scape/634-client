package com.jagex.game.runetek5.client.login;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!hv")
public final class LoginProt {

	@OriginalMember(owner = "client!hv", name = "a", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!hv", name = "<init>", descriptor = "(II)V")
	public LoginProt(@OriginalArg(0) int id, @OriginalArg(1) int size) {
		this.id = id;
	}

	@OriginalMember(owner = "client!hv", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!qa", name = "j", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_6 = new LoginProt(19, -2);

	@OriginalMember(owner = "client!qa", name = "m", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_9 = new LoginProt(22, -2);

	@OriginalMember(owner = "client!qa", name = "h", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_4 = new LoginProt(17, 0);

	@OriginalMember(owner = "client!qa", name = "l", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_8 = new LoginProt(21, 9);

	@OriginalMember(owner = "client!qa", name = "k", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_7 = new LoginProt(20, 6);

	@OriginalMember(owner = "client!qa", name = "d", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_1 = new LoginProt(14, 0);

	@OriginalMember(owner = "client!qa", name = "r", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_14 = new LoginProt(28, -2);

	@OriginalMember(owner = "client!qa", name = "g", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_3 = new LoginProt(16, -2);

	@OriginalMember(owner = "client!qa", name = "f", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_2 = new LoginProt(15, 4);

	@OriginalMember(owner = "client!qa", name = "p", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_12 = new LoginProt(26, 0);

	@OriginalMember(owner = "client!qa", name = "q", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_13 = new LoginProt(27, 0);

	@OriginalMember(owner = "client!qa", name = "o", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_11 = new LoginProt(24, -1);

	@OriginalMember(owner = "client!qa", name = "i", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_5 = new LoginProt(18, -2);

	@OriginalMember(owner = "client!qa", name = "n", descriptor = "Lclient!hv;")
	public static final LoginProt aClass141_10 = new LoginProt(23, 4);
	@OriginalMember(owner = "client!ce", name = "b", descriptor = "(B)[Lclient!hv;")
	public static LoginProt[] loginProtValues() {
		return new LoginProt[] { LoginProt.aClass141_1, LoginProt.aClass141_2, LoginProt.aClass141_3, LoginProt.aClass141_4, LoginProt.aClass141_5, LoginProt.aClass141_6, LoginProt.aClass141_7, LoginProt.aClass141_8, LoginProt.aClass141_9, LoginProt.aClass141_10, LoginProt.aClass141_11, LoginProt.aClass141_12, LoginProt.aClass141_13, LoginProt.aClass141_14 };
	}
}
