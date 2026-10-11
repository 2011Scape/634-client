package com.jagex.game.runetek5.config.gametip.component;

import com.jagex.game.runetek5.TipRenderDetails;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!wg")
public final class RotatingImageInstance extends SpritePositioning {

	@OriginalMember(owner = "client!wg", name = "m", descriptor = "I")
	public final int utAngle;

	@OriginalMember(owner = "client!wg", name = "<init>", descriptor = "(ILclient!sd;Lclient!ec;III)V")
	public RotatingImageInstance(@OriginalArg(0) int spriteId, @OriginalArg(1) LoadingScreenAlignmentX loadingScreenAlignmentX, @OriginalArg(2) LoadingScreenAlignmentY loadingScreenAlignmentY, @OriginalArg(3) int spriteOffsetX, @OriginalArg(4) int spriteOffsetY, @OriginalArg(5) int utAngle) {
		super(spriteId, loadingScreenAlignmentX, loadingScreenAlignmentY, spriteOffsetX, spriteOffsetY);
		this.utAngle = utAngle;
	}

	@OriginalMember(owner = "client!wg", name = "a", descriptor = "(B)Lclient!ad;")
	@Override
	public TipRenderDetails getRenderDetails() {
		return TipRenderDetails.aClass6_5;
	}
	@OriginalMember(owner = "client!dw", name = "a", descriptor = "(BLclient!cea;)Lclient!wg;")
	public static RotatingImageInstance decode(@OriginalArg(1) Packet packet) {
		@Pc(14) SpritePositioning imageInstance = SpritePositioning.decode(packet);
		@Pc(18) int angleStep = packet.readMedium();
		return new RotatingImageInstance(imageInstance.spriteId, imageInstance.horizontalAnchor, imageInstance.verticalAnchor, imageInstance.y, imageInstance.x, angleStep);
	}
}
