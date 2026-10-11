package com.jagex.game.runetek5.load;

import com.jagex.game.runetek5.TipRenderDetails;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.gametip.ITipDetails;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!jca")
public final class LoadingScreenClearConfig implements ITipDetails {

	@OriginalMember(owner = "client!jca", name = "f", descriptor = "I")
	public final int colour;

	@OriginalMember(owner = "client!jca", name = "<init>", descriptor = "(I)V")
	public LoadingScreenClearConfig(@OriginalArg(0) int colour) {
		this.colour = colour;
	}

	@OriginalMember(owner = "client!jca", name = "a", descriptor = "(B)Lclient!ad;")
	@Override
	public TipRenderDetails getRenderDetails() {
		return TipRenderDetails.aClass6_11;
	}
	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(BLclient!cea;)Lclient!jca;")
	public static LoadingScreenClearConfig decode(@OriginalArg(1) Packet packet) {
		@Pc(7) int colour = packet.readInt();
		return new LoadingScreenClearConfig(colour);
	}
}
