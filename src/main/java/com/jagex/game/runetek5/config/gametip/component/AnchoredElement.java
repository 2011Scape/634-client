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

@OriginalClass("client!fn")
public class AnchoredElement implements ITipDetails {

	@OriginalMember(owner = "client!fn", name = "c", descriptor = "I")
	public final int textColor;

	@OriginalMember(owner = "client!fn", name = "m", descriptor = "I")
	public final int width;

	@OriginalMember(owner = "client!fn", name = "g", descriptor = "I")
	public final int fileId;

	@OriginalMember(owner = "client!fn", name = "h", descriptor = "I")
	public final int textOffsetY;

	@OriginalMember(owner = "client!fn", name = "a", descriptor = "I")
	public final int offsetY;

	@OriginalMember(owner = "client!fn", name = "o", descriptor = "Lclient!ec;")
	public final LoadingScreenAlignmentY verticalAnchor;

	@OriginalMember(owner = "client!fn", name = "l", descriptor = "I")
	public final int offsetX;

	@OriginalMember(owner = "client!fn", name = "j", descriptor = "Lclient!sd;")
	public final LoadingScreenAlignmentX horizontalAnchor;

	@OriginalMember(owner = "client!fn", name = "e", descriptor = "I")
	public final int height;

	@OriginalMember(owner = "client!fn", name = "<init>", descriptor = "(Lclient!sd;Lclient!ec;IIIIIII)V")
	public AnchoredElement(@OriginalArg(0) LoadingScreenAlignmentX loadingScreenAlignmentX, @OriginalArg(1) LoadingScreenAlignmentY loadingScreenAlignmentY, @OriginalArg(2) int fileId, @OriginalArg(3) int offsetY, @OriginalArg(4) int width, @OriginalArg(5) int height, @OriginalArg(6) int textOffsetY, @OriginalArg(7) int fontId, @OriginalArg(8) int textColor) {
		this.textColor = textColor;
		this.width = width;
		this.fileId = fontId;
		this.textOffsetY = textOffsetY;
		this.offsetY = offsetY;
		this.verticalAnchor = loadingScreenAlignmentY;
		this.offsetX = fileId;
		this.horizontalAnchor = loadingScreenAlignmentX;
		this.height = height;
	}

	@OriginalMember(owner = "client!fn", name = "a", descriptor = "(B)Lclient!ad;")
	@Override
	public TipRenderDetails getRenderDetails() {
		return null;
	}
	@OriginalMember(owner = "client!iha", name = "a", descriptor = "(BLclient!cea;)Lclient!fn;")
	public static AnchoredElement decode(@OriginalArg(1) Packet packet) {
		@Pc(10) LoadingScreenAlignmentX loadingScreenAlignmentX = LoadingScreenAlignmentX.loadingScreenAlignmentXValues()[packet.readUByte()];
		@Pc(22) LoadingScreenAlignmentY loadingScreenAlignmentY = LoadingScreenAlignmentY.loadingScreenAlignmentYValues()[packet.readUByte()];
		@Pc(26) int offsetX = packet.readShort();
		@Pc(30) int offsetY = packet.readShort();
		@Pc(34) int width = packet.readUShort();
		@Pc(38) int height = packet.readUShort();
		@Pc(42) int textOffsetY = packet.readShort();
		@Pc(46) int fileId = packet.readInt();
		@Pc(50) int textColor = packet.readInt();
		return new AnchoredElement(loadingScreenAlignmentX, loadingScreenAlignmentY, offsetX, offsetY, width, height, textOffsetY, fileId, textColor);
	}
}
