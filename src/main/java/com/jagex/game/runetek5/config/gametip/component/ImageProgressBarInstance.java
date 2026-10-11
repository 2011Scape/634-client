package com.jagex.game.runetek5.config.gametip.component;

import com.jagex.game.runetek5.TipRenderDetails;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!kh")
public final class ImageProgressBarInstance extends AnchoredElement {

	@OriginalMember(owner = "client!kh", name = "q", descriptor = "I")
	public final int colour;

	@OriginalMember(owner = "client!kh", name = "s", descriptor = "I")
	public final int sprite;

	@OriginalMember(owner = "client!kh", name = "<init>", descriptor = "(Lclient!sd;Lclient!ec;IIIIIIIIII)V")
	public ImageProgressBarInstance(@OriginalArg(0) LoadingScreenAlignmentX loadingScreenAlignmentX, @OriginalArg(1) LoadingScreenAlignmentY loadingScreenAlignmentY, @OriginalArg(2) int fileId, @OriginalArg(3) int offsetY, @OriginalArg(4) int width, @OriginalArg(5) int height, @OriginalArg(6) int textOffsetY, @OriginalArg(7) int fontId, @OriginalArg(8) int textColor, @OriginalArg(9) int fillColor, @OriginalArg(10) int colour, @OriginalArg(11) int sprite) {
		super(loadingScreenAlignmentX, loadingScreenAlignmentY, fileId, offsetY, width, height, textOffsetY, fontId, textColor);
		this.colour = colour;
		this.sprite = sprite;
	}

	@OriginalMember(owner = "client!kh", name = "a", descriptor = "(B)Lclient!ad;")
	@Override
	public TipRenderDetails getRenderDetails() {
		return TipRenderDetails.aClass6_2;
	}
	@OriginalMember(owner = "client!gl", name = "a", descriptor = "(ILclient!cea;)Lclient!kh;")
	public static ImageProgressBarInstance decode(@OriginalArg(1) Packet packet) {
		@Pc(7) AnchoredElement progressBarInstance = AnchoredElement.decode(packet);
		@Pc(11) int fillColor = packet.readInt();
		@Pc(15) int outlineColour = packet.readInt();
		@Pc(24) int sprite = packet.readUShort();
		return new ImageProgressBarInstance(progressBarInstance.horizontalAnchor, progressBarInstance.verticalAnchor, progressBarInstance.offsetX, progressBarInstance.offsetY, progressBarInstance.width, progressBarInstance.height, progressBarInstance.textOffsetY, progressBarInstance.fileId, progressBarInstance.textColor, fillColor, outlineColour, sprite);
	}
}
