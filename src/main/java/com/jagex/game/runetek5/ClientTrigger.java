package com.jagex.game.runetek5;

import com.jagex.game.runetek5.trigger.TriggerType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!uw")
public final class ClientTrigger implements TriggerType {

	@OriginalMember(owner = "client!uw", name = "c", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!uw", name = "<init>", descriptor = "(Ljava/lang/String;I)V")
	public ClientTrigger(@OriginalArg(0) String string, @OriginalArg(1) int id) {
		this.id = id;
	}

	@OriginalMember(owner = "client!uw", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!de", name = "b", descriptor = "Lclient!uw;")
	public static final ClientTrigger aClass350_3 = new ClientTrigger("", 10);
	@OriginalMember(owner = "client!lj", name = "s", descriptor = "Lclient!uw;")
	public static final ClientTrigger aClass350_6 = new ClientTrigger("", 11);
	@OriginalMember(owner = "client!ti", name = "o", descriptor = "Lclient!uw;")
	public static final ClientTrigger aClass350_9 = new ClientTrigger("", 14);

	@OriginalMember(owner = "client!oda", name = "d", descriptor = "Lclient!uw;")
	public static final ClientTrigger aClass350_7 = new ClientTrigger("", 18);
	@OriginalMember(owner = "client!ar", name = "x", descriptor = "Lclient!uw;")
	public static final ClientTrigger aClass350_1 = new ClientTrigger("", 13);

	@OriginalMember(owner = "client!wi", name = "J", descriptor = "Lclient!uw;")
	public static final ClientTrigger aClass350_10 = new ClientTrigger("", 12);
}
