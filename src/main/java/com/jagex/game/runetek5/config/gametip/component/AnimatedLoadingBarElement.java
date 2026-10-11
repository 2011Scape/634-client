package com.jagex.game.runetek5.config.gametip.component;

import com.jagex.game.runetek5.TipRenderDetails;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.gametip.component.MultiSpriteLoadingBarElement;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!fu")
public final class AnimatedLoadingBarElement extends MultiSpriteLoadingBarElement {

	@OriginalMember(owner = "client!fu", name = "D", descriptor = "I")
	public final int scrollSpeed;

	@OriginalMember(owner = "client!fu", name = "<init>", descriptor = "(Lclient!sd;Lclient!ec;IIIIIIIIIIIIII)V")
	public AnimatedLoadingBarElement(@OriginalArg(0) LoadingScreenAlignmentX loadingScreenAlignmentX, @OriginalArg(1) LoadingScreenAlignmentY loadingScreenAlignmentY, @OriginalArg(2) int offsetX, @OriginalArg(3) int offsetY, @OriginalArg(4) int width, @OriginalArg(5) int height, @OriginalArg(6) int textOffsetY, @OriginalArg(7) int fileId, @OriginalArg(8) int textColor, @OriginalArg(9) int fillSpriteId, @OriginalArg(10) int emptySpriteId, @OriginalArg(11) int leftCapSpriteId, @OriginalArg(12) int rightCapSpriteId, @OriginalArg(13) int topBorderSpriteId, @OriginalArg(14) int bottomBorderSpriteId, @OriginalArg(15) int scrollSpeed) {
		super(loadingScreenAlignmentX, loadingScreenAlignmentY, offsetX, offsetY, width, height, textOffsetY, fileId, textColor, fillSpriteId, emptySpriteId, leftCapSpriteId, rightCapSpriteId, topBorderSpriteId, bottomBorderSpriteId);
		this.scrollSpeed = scrollSpeed;
	}

	@OriginalMember(owner = "client!fu", name = "a", descriptor = "(B)Lclient!ad;")
	@Override
	public TipRenderDetails getRenderDetails() {
		return TipRenderDetails.aClass6_9;
	}
	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(ILclient!cea;)Lclient!fu;")
	public static AnimatedLoadingBarElement decode(@OriginalArg(1) Packet packet) {
		@Pc(9) MultiSpriteLoadingBarElement fancyProgressBarInstance = MultiSpriteLoadingBarElement.decode(packet);
		@Pc(13) int scrollSpeed = packet.readShort();
		return new AnimatedLoadingBarElement(fancyProgressBarInstance.horizontalAnchor, fancyProgressBarInstance.verticalAnchor, fancyProgressBarInstance.offsetX, fancyProgressBarInstance.offsetY, fancyProgressBarInstance.width, fancyProgressBarInstance.height, fancyProgressBarInstance.textOffsetY, fancyProgressBarInstance.fileId, fancyProgressBarInstance.textColor, fancyProgressBarInstance.spriteD, fancyProgressBarInstance.spriteF, fancyProgressBarInstance.spriteC, fancyProgressBarInstance.spriteB, fancyProgressBarInstance.spriteA, fancyProgressBarInstance.spriteE, scrollSpeed);
	}
}
