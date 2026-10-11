package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!fh")
public final class ModeWhere {

	@OriginalMember(owner = "client!fh", name = "a", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!fh", name = "<init>", descriptor = "(Ljava/lang/String;I)V")
	public ModeWhere(@OriginalArg(0) String string, @OriginalArg(1) int id) {
		this.id = id;
	}

	@OriginalMember(owner = "client!fh", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!mo", name = "b", descriptor = "Lclient!fh;")
	public static final ModeWhere aClass107_6 = new ModeWhere("WTWIP", 3);
	@OriginalMember(owner = "client!eca", name = "i", descriptor = "Lclient!fh;")
	public static final ModeWhere GAME = new ModeWhere("WTI", 5);

	@OriginalMember(owner = "client!hs", name = "j", descriptor = "Lclient!fh;")
	public static final ModeWhere MODE_WHERE_WTQA = new ModeWhere("WTQA", 2);

	@OriginalMember(owner = "client!gia", name = "a", descriptor = "(BI)Lclient!fh;")
	public static ModeWhere forId(@OriginalArg(1) int id) {
		@Pc(13) ModeWhere[] modeWheres = ModeWhere.values();
		for (@Pc(15) int i = 0; i < modeWheres.length; i++) {
			@Pc(21) ModeWhere modeWhere = modeWheres[i];
			if (modeWhere.id == id) {
				return modeWhere;
			}
		}
		return null;
	}
	@OriginalMember(owner = "client!v", name = "b", descriptor = "(B)[Lclient!fh;")
	public static ModeWhere[] values() {
		return new ModeWhere[] { ModeWhere.WTRC, ModeWhere.aClass107_4, ModeWhere.MODE_WHERE_WTQA, ModeWhere.aClass107_6, ModeWhere.aClass107_5, ModeWhere.GAME };
	}
	@OriginalMember(owner = "client!qc", name = "O", descriptor = "Lclient!fh;")
	public static final ModeWhere WTRC = new ModeWhere("LIVE", 0);

	@OriginalMember(owner = "client!ks", name = "b", descriptor = "Lclient!fh;")
	public static final ModeWhere aClass107_5 = new ModeWhere("LOCAL", 4);

	@OriginalMember(owner = "client!kl", name = "u", descriptor = "Lclient!fh;")
	public static final ModeWhere aClass107_4 = new ModeWhere("WTRC", 1);
	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(Lclient!fh;I)Z")
	public static boolean isHostedTestEnvironment(@OriginalArg(0) ModeWhere modeWhere) {
		return ModeWhere.aClass107_4 == modeWhere || ModeWhere.MODE_WHERE_WTQA == modeWhere || ModeWhere.aClass107_6 == modeWhere || modeWhere == ModeWhere.GAME;
	}
}
