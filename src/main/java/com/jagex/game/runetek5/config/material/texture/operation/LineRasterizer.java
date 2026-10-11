package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.config.material.texture.operation.TextureShapeRasterizer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.buffer.Packet;

@OriginalClass("client!o")
public final class LineRasterizer extends TextureShapeRasterizer {

	@OriginalMember(owner = "client!o", name = "i", descriptor = "I")
	private final int endY;

	@OriginalMember(owner = "client!o", name = "o", descriptor = "I")
	private final int endX;

	@OriginalMember(owner = "client!o", name = "f", descriptor = "I")
	private final int startX;

	@OriginalMember(owner = "client!o", name = "j", descriptor = "I")
	private final int startY;

	@OriginalMember(owner = "client!o", name = "<init>", descriptor = "(IIIIII)V")
	public LineRasterizer(@OriginalArg(0) int startX, @OriginalArg(1) int startY, @OriginalArg(2) int endX, @OriginalArg(3) int endY, @OriginalArg(4) int strokeColor, @OriginalArg(5) int lineWidth) {
		super(-1, strokeColor, lineWidth);
		this.endY = endY;
		this.endX = endX;
		this.startX = startX;
		this.startY = startY;
	}

	@OriginalMember(owner = "client!o", name = "a", descriptor = "(III)V")
	@Override
	public void drawFilledAndStroked(@OriginalArg(0) int width, @OriginalArg(1) int height) {
	}

	@OriginalMember(owner = "client!o", name = "c", descriptor = "(III)V")
	@Override
	public void drawStroked(@OriginalArg(0) int height, @OriginalArg(1) int width) {
		@Pc(10) int x0 = width * this.startX >> 12;
		@Pc(23) int x1 = width * this.endX >> 12;
		@Pc(30) int y0 = this.startY * height >> 12;
		@Pc(37) int y1 = this.endY * height >> 12;
		TextureShapeRasterizer.drawLine(x0, super.strokeColor, y0, y1, x1);
	}

	@OriginalMember(owner = "client!o", name = "b", descriptor = "(III)V")
	@Override
	public void drawFilled(@OriginalArg(1) int width, @OriginalArg(2) int height) {
	}
	@OriginalMember(owner = "client!qca", name = "a", descriptor = "(ILclient!cea;)Lclient!o;")
	public static LineRasterizer decode(@OriginalArg(1) Packet packet) {
		return new LineRasterizer(packet.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readUMedium(), packet.readUByte());
	}
}
