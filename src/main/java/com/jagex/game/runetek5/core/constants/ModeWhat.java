package com.jagex.game.runetek5.core.constants;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!sk")
public final class ModeWhat {

	@OriginalMember(owner = "client!sk", name = "f", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!sk", name = "<init>", descriptor = "(Ljava/lang/String;I)V")
	public ModeWhat(@OriginalArg(0) String string, @OriginalArg(1) int id) {
		this.id = id;
	}

	@OriginalMember(owner = "client!sk", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!sk", name = "a", descriptor = "(I)I")
	public int getId() {
		return this.id;
	}
	@OriginalMember(owner = "client!rn", name = "K", descriptor = "Lclient!sk;")
	public static final ModeWhat aClass317_3 = new ModeWhat("RC", 1);

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(II)Lclient!sk;")
	public static ModeWhat forId(@OriginalArg(1) int id) {
		@Pc(8) ModeWhat[] modeWhats = ModeWhat.values();
		for (@Pc(15) int i = 0; i < modeWhats.length; i++) {
			@Pc(21) ModeWhat modeWhat = modeWhats[i];
			if (modeWhat.id == id) {
				return modeWhat;
			}
		}
		return null;
	}
	@OriginalMember(owner = "client!baa", name = "l", descriptor = "Lclient!sk;")
	public static final ModeWhat aClass317_1 = new ModeWhat("LIVE", 0);
	@OriginalMember(owner = "client!vr", name = "d", descriptor = "Lclient!sk;")
	public static final ModeWhat WIP = new ModeWhat("WIP", 2);

	@OriginalMember(owner = "client!su", name = "a", descriptor = "(I)[Lclient!sk;")
	public static ModeWhat[] values() {
		return new ModeWhat[] { ModeWhat.aClass317_1, ModeWhat.aClass317_3, ModeWhat.WIP };
	}
}
