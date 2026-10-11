package com.jagex.game.runetek5.client.loading.screen.op.instance;

import com.jagex.game.runetek5.TipRenderDetails;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.gametip.ITipDetails;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!nfa")
public final class BackgroundImageInstance implements ITipDetails {

	@OriginalMember(owner = "client!nfa", name = "e", descriptor = "I")
	public final int image;

	@OriginalMember(owner = "client!nfa", name = "<init>", descriptor = "(I)V")
	public BackgroundImageInstance(@OriginalArg(0) int image) {
		this.image = image;
	}

	@OriginalMember(owner = "client!nfa", name = "a", descriptor = "(B)Lclient!ad;")
	@Override
	public TipRenderDetails getRenderDetails() {
		return TipRenderDetails.aClass6_6;
	}
	@OriginalMember(owner = "client!hm", name = "a", descriptor = "(Lclient!cea;Z)Lclient!nfa;")
	public static BackgroundImageInstance decode(@OriginalArg(0) Packet packet) {
		@Pc(7) int image = packet.readUShort();
		return new BackgroundImageInstance(image);
	}
}
