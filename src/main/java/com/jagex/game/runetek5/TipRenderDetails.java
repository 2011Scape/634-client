package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ad")
public final class TipRenderDetails {

	@OriginalMember(owner = "client!ad", name = "d", descriptor = "I")
	public final int version;

	@OriginalMember(owner = "client!ad", name = "<init>", descriptor = "(II)V")
	public TipRenderDetails(@OriginalArg(0) int id, @OriginalArg(1) int version) {
		this.version = version;
	}

	@OriginalMember(owner = "client!ad", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!hu", name = "b", descriptor = "Lclient!ad;")
	public static final TipRenderDetails aClass6_5 = new TipRenderDetails(6, 1);

	@OriginalMember(owner = "client!gq", name = "P", descriptor = "Lclient!ad;")
	public static final TipRenderDetails aClass6_4 = new TipRenderDetails(4, 1);

	@OriginalMember(owner = "client!hi", name = "a", descriptor = "(I)[Lclient!ad;")
	public static TipRenderDetails[] values() {
		return new TipRenderDetails[] { TipRenderDetails.aClass6_11, TipRenderDetails.aClass6_12, TipRenderDetails.aClass6_2, TipRenderDetails.OPPLAYER5, TipRenderDetails.aClass6_4, TipRenderDetails.aClass6_10, TipRenderDetails.aClass6_5, TipRenderDetails.aClass6_7, TipRenderDetails.aClass6_6, TipRenderDetails.aClass6_9 };
	}
	@OriginalMember(owner = "client!ch", name = "b", descriptor = "Lclient!ad;")
	public static final TipRenderDetails aClass6_2 = new TipRenderDetails(2, 2);

	@OriginalMember(owner = "client!oh", name = "b", descriptor = "Lclient!ad;")
	public static final TipRenderDetails aClass6_6 = new TipRenderDetails(8, 1);
	@OriginalMember(owner = "client!qf", name = "k", descriptor = "Lclient!ad;")
	public static final TipRenderDetails OPPLAYER5 = new TipRenderDetails(3, 2);

	@OriginalMember(owner = "client!rp", name = "Q", descriptor = "Lclient!ad;")
	public static final TipRenderDetails aClass6_10 = new TipRenderDetails(5, 1);

	@OriginalMember(owner = "client!sb", name = "g", descriptor = "Lclient!ad;")
	public static final TipRenderDetails aClass6_11 = new TipRenderDetails(0, 1);

	@OriginalMember(owner = "client!cea", name = "ab", descriptor = "Lclient!ad;")
	public static final TipRenderDetails aClass6_12 = new TipRenderDetails(1, 2);

	@OriginalMember(owner = "client!qga", name = "j", descriptor = "Lclient!ad;")
	public static final TipRenderDetails aClass6_9 = new TipRenderDetails(9, 2);
	@OriginalMember(owner = "client!po", name = "u", descriptor = "Lclient!ad;")
	public static final TipRenderDetails aClass6_7 = new TipRenderDetails(7, 2);
}
