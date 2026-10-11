package com.jagex.game.runetek5.config.gametip.component;

import com.jagex.game.runetek5.TipRenderDetails;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.gametip.ITipDetails;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!vr")
public class SpritePositioning implements ITipDetails {

	@OriginalMember(owner = "client!vr", name = "a", descriptor = "I")
	public final int y;

	@OriginalMember(owner = "client!vr", name = "i", descriptor = "I")
	public final int spriteId;

	@OriginalMember(owner = "client!vr", name = "b", descriptor = "I")
	public final int x;

	@OriginalMember(owner = "client!vr", name = "f", descriptor = "Lclient!sd;")
	public final LoadingScreenAlignmentX horizontalAnchor;

	@OriginalMember(owner = "client!vr", name = "c", descriptor = "Lclient!ec;")
	public final LoadingScreenAlignmentY verticalAnchor;

	@OriginalMember(owner = "client!vr", name = "<init>", descriptor = "(ILclient!sd;Lclient!ec;II)V")
	public SpritePositioning(@OriginalArg(0) int spriteId, @OriginalArg(1) LoadingScreenAlignmentX loadingScreenAlignmentX, @OriginalArg(2) LoadingScreenAlignmentY loadingScreenAlignmentY, @OriginalArg(3) int spriteOffsetX, @OriginalArg(4) int spriteOffsetY) {
		this.y = spriteOffsetX;
		this.spriteId = spriteId;
		this.x = spriteOffsetY;
		this.horizontalAnchor = loadingScreenAlignmentX;
		this.verticalAnchor = loadingScreenAlignmentY;
	}

	@OriginalMember(owner = "client!vr", name = "a", descriptor = "(B)Lclient!ad;")
	@Override
	public TipRenderDetails getRenderDetails() {
		return TipRenderDetails.aClass6_10;
	}
	@OriginalMember(owner = "client!wg", name = "a", descriptor = "(Lclient!cea;I)Lclient!vr;")
	public static SpritePositioning decode(@OriginalArg(0) Packet packet) {
		@Pc(7) int spriteId = packet.readUShort();
		@Pc(14) LoadingScreenAlignmentX loadingScreenAlignmentX = LoadingScreenAlignmentX.loadingScreenAlignmentXValues()[packet.readUByte()];
		@Pc(21) LoadingScreenAlignmentY loadingScreenAlignmentY = LoadingScreenAlignmentY.loadingScreenAlignmentYValues()[packet.readUByte()];
		@Pc(32) int spriteOffsetX = packet.readShort();
		@Pc(36) int spriteOffsetY = packet.readShort();
		return new SpritePositioning(spriteId, loadingScreenAlignmentX, loadingScreenAlignmentY, spriteOffsetX, spriteOffsetY);
	}
}
