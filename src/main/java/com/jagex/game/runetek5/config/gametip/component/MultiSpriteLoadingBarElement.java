package com.jagex.game.runetek5.config.gametip.component;

import com.jagex.game.runetek5.TipRenderDetails;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!um")
public class MultiSpriteLoadingBarElement extends AnchoredElement {

	@OriginalMember(owner = "client!um", name = "v", descriptor = "I")
	public final int spriteA;

	@OriginalMember(owner = "client!um", name = "x", descriptor = "I")
	public final int spriteB;

	@OriginalMember(owner = "client!um", name = "y", descriptor = "I")
	public final int spriteC;

	@OriginalMember(owner = "client!um", name = "u", descriptor = "I")
	public final int spriteD;

	@OriginalMember(owner = "client!um", name = "s", descriptor = "I")
	public final int spriteE;

	@OriginalMember(owner = "client!um", name = "q", descriptor = "I")
	public final int spriteF;

	@OriginalMember(owner = "client!um", name = "<init>", descriptor = "(Lclient!sd;Lclient!ec;IIIIIIIIIIIII)V")
	public MultiSpriteLoadingBarElement(@OriginalArg(0) LoadingScreenAlignmentX loadingScreenAlignmentX, @OriginalArg(1) LoadingScreenAlignmentY loadingScreenAlignmentY, @OriginalArg(2) int offsetX, @OriginalArg(3) int offsetY, @OriginalArg(4) int width, @OriginalArg(5) int height, @OriginalArg(6) int textOffsetY, @OriginalArg(7) int fileId, @OriginalArg(8) int textColor, @OriginalArg(9) int spriteD, @OriginalArg(10) int spriteF, @OriginalArg(11) int spriteC, @OriginalArg(12) int spriteB, @OriginalArg(13) int spriteA, @OriginalArg(14) int spriteE) {
		super(loadingScreenAlignmentX, loadingScreenAlignmentY, offsetX, offsetY, width, height, textOffsetY, fileId, textColor);
		this.spriteA = spriteA;
		this.spriteB = spriteB;
		this.spriteC = spriteC;
		this.spriteD = spriteD;
		this.spriteE = spriteE;
		this.spriteF = spriteF;
	}

	@OriginalMember(owner = "client!um", name = "a", descriptor = "(B)Lclient!ad;")
	@Override
	public TipRenderDetails getRenderDetails() {
		return TipRenderDetails.OPPLAYER5;
	}
	@OriginalMember(owner = "client!wl", name = "a", descriptor = "(ILclient!cea;)Lclient!um;")
	public static MultiSpriteLoadingBarElement decode(@OriginalArg(1) Packet packet) {
		@Pc(7) AnchoredElement progressBarInstance = AnchoredElement.decode(packet);
		@Pc(13) int spriteD = packet.readUShort();
		@Pc(17) int spriteF = packet.readUShort();
		@Pc(25) int spriteC = packet.readUShort();
		@Pc(31) int spriteB = packet.readUShort();
		@Pc(35) int spriteA = packet.readUShort();
		@Pc(39) int spriteE = packet.readUShort();
		return new MultiSpriteLoadingBarElement(progressBarInstance.horizontalAnchor, progressBarInstance.verticalAnchor, progressBarInstance.offsetX, progressBarInstance.offsetY, progressBarInstance.width, progressBarInstance.height, progressBarInstance.textOffsetY, progressBarInstance.fileId, progressBarInstance.textColor, spriteD, spriteF, spriteC, spriteB, spriteA, spriteE);
	}
}
