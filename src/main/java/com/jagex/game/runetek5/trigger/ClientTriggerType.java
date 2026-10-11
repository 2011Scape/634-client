package com.jagex.game.runetek5.trigger;

import com.jagex.game.runetek5.ClientTrigger;
import org.openrs2.deob.annotation.OriginalMember;

public final class ClientTriggerType {
	@OriginalMember(owner = "client!bc", name = "a", descriptor = "Lclient!uw;")
	public static final ClientTrigger MEC_MOUSE_ENTER = new ClientTrigger("", 15);

	@OriginalMember(owner = "client!ku", name = "j", descriptor = "Lclient!uw;")
	public static final ClientTrigger MEC_MOUSE_LEAVE = new ClientTrigger("", 16);

	@OriginalMember(owner = "client!sv", name = "T", descriptor = "Lclient!uw;")
	public static final ClientTrigger MEC_MOUSE_OVER = new ClientTrigger("", 17);

	private ClientTriggerType() {
		/* empty */
	}
}
