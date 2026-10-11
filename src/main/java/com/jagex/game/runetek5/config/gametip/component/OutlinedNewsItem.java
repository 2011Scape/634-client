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

@OriginalClass("client!nha")
public final class OutlinedNewsItem implements ITipDetails {

	@OriginalMember(owner = "client!nha", name = "b", descriptor = "Z")
	public final boolean drawOutline;

	@OriginalMember(owner = "client!nha", name = "k", descriptor = "I")
	public final int height;

	@OriginalMember(owner = "client!nha", name = "f", descriptor = "I")
	public final int item;

	@OriginalMember(owner = "client!nha", name = "l", descriptor = "I")
	public final int x;

	@OriginalMember(owner = "client!nha", name = "a", descriptor = "I")
	public final int textColor;

	@OriginalMember(owner = "client!nha", name = "q", descriptor = "I")
	public final int width;

	@OriginalMember(owner = "client!nha", name = "h", descriptor = "I")
	public final int y;

	@OriginalMember(owner = "client!nha", name = "e", descriptor = "I")
	public final int lineColour;

	@OriginalMember(owner = "client!nha", name = "m", descriptor = "Lclient!ec;")
	public final LoadingScreenAlignmentY verticalAlignment;

	@OriginalMember(owner = "client!nha", name = "p", descriptor = "I")
	public final int backgroundColor;

	@OriginalMember(owner = "client!nha", name = "j", descriptor = "Lclient!sd;")
	public final LoadingScreenAlignmentX horizontalAlignment;

	@OriginalMember(owner = "client!nha", name = "<init>", descriptor = "(ILclient!sd;Lclient!ec;IIIIIIIZ)V")
	public OutlinedNewsItem(@OriginalArg(0) int item, @OriginalArg(1) LoadingScreenAlignmentX loadingScreenAlignmentX, @OriginalArg(2) LoadingScreenAlignmentY loadingScreenAlignmentY, @OriginalArg(3) int offsetX, @OriginalArg(4) int offsetY, @OriginalArg(5) int width, @OriginalArg(6) int height, @OriginalArg(7) int textColor, @OriginalArg(8) int shadeColor, @OriginalArg(9) int lineColour, @OriginalArg(10) boolean drawOutline) {
		this.drawOutline = drawOutline;
		this.height = height;
		this.item = item;
		this.x = offsetX;
		this.textColor = textColor;
		this.width = width;
		this.y = offsetY;
		this.lineColour = lineColour;
		this.verticalAlignment = loadingScreenAlignmentY;
		this.backgroundColor = shadeColor;
		this.horizontalAlignment = loadingScreenAlignmentX;
	}

	@OriginalMember(owner = "client!nha", name = "a", descriptor = "(B)Lclient!ad;")
	@Override
	public TipRenderDetails getRenderDetails() {
		return TipRenderDetails.aClass6_4;
	}
	@OriginalMember(owner = "client!wp", name = "a", descriptor = "(Lclient!cea;B)Lclient!nha;")
	public static OutlinedNewsItem decode(@OriginalArg(0) Packet packet) {
		@Pc(7) int itemIndex = packet.readUByte();
		@Pc(14) LoadingScreenAlignmentX loadingScreenAlignmentX = LoadingScreenAlignmentX.loadingScreenAlignmentXValues()[packet.readUByte()];
		@Pc(21) LoadingScreenAlignmentY loadingScreenAlignmentY = LoadingScreenAlignmentY.loadingScreenAlignmentYValues()[packet.readUByte()];
		@Pc(25) int offsetX = packet.readShort();
		@Pc(29) int offsetY = packet.readShort();
		@Pc(33) int width = packet.readUShort();
		@Pc(37) int height = packet.readUShort();
		@Pc(41) int textColor = packet.readInt();
		@Pc(45) int shadeColor = packet.readInt();
		@Pc(49) int lineColour = packet.readInt();
		@Pc(66) boolean drawOutline = packet.readUByte() == 1;
		return new OutlinedNewsItem(itemIndex, loadingScreenAlignmentX, loadingScreenAlignmentY, offsetX, offsetY, width, height, textColor, shadeColor, lineColour, drawOutline);
	}
}
