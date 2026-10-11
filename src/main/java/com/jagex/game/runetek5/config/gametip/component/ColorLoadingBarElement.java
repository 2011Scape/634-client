package com.jagex.game.runetek5.config.gametip.component;

import com.jagex.game.runetek5.TipRenderDetails;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ou")
public final class ColorLoadingBarElement extends AnchoredElement {

	@OriginalMember(owner = "client!ou", name = "q", descriptor = "I")
	public final int borderColour;

	@OriginalMember(owner = "client!ou", name = "w", descriptor = "I")
	public final int backgroundColour;

	@OriginalMember(owner = "client!ou", name = "<init>", descriptor = "(Lclient!sd;Lclient!ec;IIIIIIIII)V")
	public ColorLoadingBarElement(@OriginalArg(0) LoadingScreenAlignmentX loadingScreenAlignmentX, @OriginalArg(1) LoadingScreenAlignmentY loadingScreenAlignmentY, @OriginalArg(2) int offsetX, @OriginalArg(3) int offsetY, @OriginalArg(4) int width, @OriginalArg(5) int height, @OriginalArg(6) int textOffsetY, @OriginalArg(7) int fileId, @OriginalArg(8) int textColor, @OriginalArg(9) int fillColor, @OriginalArg(10) int outlineColor) {
		super(loadingScreenAlignmentX, loadingScreenAlignmentY, offsetX, offsetY, width, height, textOffsetY, fileId, textColor);
		this.borderColour = outlineColor;
		this.backgroundColour = fillColor;
	}

	@OriginalMember(owner = "client!ou", name = "a", descriptor = "(B)Lclient!ad;")
	@Override
	public TipRenderDetails getRenderDetails() {
		return TipRenderDetails.aClass6_12;
	}
	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(ILclient!cea;)Lclient!ou;")
	public static ColorLoadingBarElement decode(@OriginalArg(1) Packet packet) {
		@Pc(7) AnchoredElement progressBarInstance = AnchoredElement.decode(packet);
		@Pc(19) int fillColor = packet.readInt();
		@Pc(23) int outlineColor = packet.readInt();
		return new ColorLoadingBarElement(progressBarInstance.horizontalAnchor, progressBarInstance.verticalAnchor, progressBarInstance.offsetX, progressBarInstance.offsetY, progressBarInstance.width, progressBarInstance.height, progressBarInstance.textOffsetY, progressBarInstance.fileId, progressBarInstance.textColor, fillColor, outlineColor);
	}
}
