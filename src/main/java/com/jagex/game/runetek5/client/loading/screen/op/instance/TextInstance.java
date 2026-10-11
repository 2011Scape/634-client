package com.jagex.game.runetek5.client.loading.screen.op.instance;

import com.jagex.game.runetek5.TipRenderDetails;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.gametip.ITipDetails;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ot")
public final class TextInstance implements ITipDetails {

	@OriginalMember(owner = "client!ot", name = "p", descriptor = "I")
	public final int textColour;

	@OriginalMember(owner = "client!ot", name = "b", descriptor = "I")
	public final int textHorizontalAlignment;

	@OriginalMember(owner = "client!ot", name = "m", descriptor = "I")
	public final int shadowColour;

	@OriginalMember(owner = "client!ot", name = "n", descriptor = "I")
	public final int font;

	@OriginalMember(owner = "client!ot", name = "l", descriptor = "Ljava/lang/String;")
	public final String text;

	@OriginalMember(owner = "client!ot", name = "r", descriptor = "Lclient!sd;")
	public final LoadingScreenAlignmentX horizontalAlignment;

	@OriginalMember(owner = "client!ot", name = "a", descriptor = "I")
	public final int verticalSpacing;

	@OriginalMember(owner = "client!ot", name = "j", descriptor = "I")
	public final int width;

	@OriginalMember(owner = "client!ot", name = "q", descriptor = "I")
	public final int x;

	@OriginalMember(owner = "client!ot", name = "o", descriptor = "Lclient!ec;")
	public final LoadingScreenAlignmentY verticalAlignment;

	@OriginalMember(owner = "client!ot", name = "f", descriptor = "I")
	public final int textVerticalAlignment;

	@OriginalMember(owner = "client!ot", name = "e", descriptor = "I")
	public final int height;

	@OriginalMember(owner = "client!ot", name = "g", descriptor = "I")
	public final int y;

	@OriginalMember(owner = "client!ot", name = "<init>", descriptor = "(Ljava/lang/String;Lclient!sd;Lclient!ec;IIIIIIIIII)V")
	public TextInstance(@OriginalArg(0) String string, @OriginalArg(1) LoadingScreenAlignmentX loadingScreenAlignmentX, @OriginalArg(2) LoadingScreenAlignmentY loadingScreenAlignmentY, @OriginalArg(3) int x, @OriginalArg(4) int textHorizontalAlignment, @OriginalArg(5) int shadowColour, @OriginalArg(6) int textColour, @OriginalArg(7) int y, @OriginalArg(8) int verticalSpacing, @OriginalArg(9) int textVerticalAlignment, @OriginalArg(10) int font, @OriginalArg(11) int height, @OriginalArg(12) int width) {
		this.textColour = textColour;
		this.textHorizontalAlignment = textHorizontalAlignment;
		this.shadowColour = shadowColour;
		this.font = font;
		this.text = string;
		this.horizontalAlignment = loadingScreenAlignmentX;
		this.verticalSpacing = verticalSpacing;
		this.width = width;
		this.x = x;
		this.verticalAlignment = loadingScreenAlignmentY;
		this.textVerticalAlignment = textVerticalAlignment;
		this.height = height;
		this.y = y;
	}

	@OriginalMember(owner = "client!ot", name = "a", descriptor = "(B)Lclient!ad;")
	@Override
	public TipRenderDetails getRenderDetails() {
		return TipRenderDetails.aClass6_7;
	}
	@OriginalMember(owner = "client!o", name = "a", descriptor = "(ILclient!cea;)Lclient!ot;")
	public static TextInstance decode(@OriginalArg(1) Packet packet) {
		@Pc(7) String string = packet.readString();
		@Pc(16) LoadingScreenAlignmentX loadingScreenAlignmentX = LoadingScreenAlignmentX.loadingScreenAlignmentXValues()[packet.readUByte()];
		@Pc(30) LoadingScreenAlignmentY loadingScreenAlignmentY = LoadingScreenAlignmentY.loadingScreenAlignmentYValues()[packet.readUByte()];
		@Pc(34) int offsetX = packet.readShort();
		@Pc(38) int offsetY = packet.readShort();
		@Pc(42) int textAlignment = packet.readUByte();
		@Pc(46) int textVerticalAlignment = packet.readUByte();
		@Pc(50) int lineSpacing = packet.readUByte();
		@Pc(56) int width = packet.readUShort();
		@Pc(60) int height = packet.readUShort();
		@Pc(64) int fontId = packet.readInt();
		@Pc(68) int textColor = packet.readInt();
		@Pc(72) int shadowColor = packet.readInt();
		return new TextInstance(string, loadingScreenAlignmentX, loadingScreenAlignmentY, offsetX, offsetY, textAlignment, textVerticalAlignment, lineSpacing, width, height, fontId, textColor, shadowColor);
	}
}
