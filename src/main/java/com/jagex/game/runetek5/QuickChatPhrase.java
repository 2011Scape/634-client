package com.jagex.game.runetek5;

import com.jagex.game.runetek5.config.quickchat.QuickchatMessageType;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!iia")
public final class QuickChatPhrase {

	@OriginalMember(owner = "client!iia", name = "d", descriptor = "Lclient!td;")
	public QuickchatMessageType type;

	@OriginalMember(owner = "client!iia", name = "e", descriptor = "I")
	public int id;

	@OriginalMember(owner = "client!iia", name = "g", descriptor = "[I")
	public int[] values;
}
