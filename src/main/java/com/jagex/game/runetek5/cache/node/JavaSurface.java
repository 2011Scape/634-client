package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.Node;
import java.awt.Canvas;
import java.awt.Graphics;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!pi")
public abstract class JavaSurface extends Node {

	@OriginalMember(owner = "client!pi", name = "r", descriptor = "[I")
	public int[] raster;

	@OriginalMember(owner = "client!pi", name = "s", descriptor = "I")
	public int width;

	@OriginalMember(owner = "client!pi", name = "v", descriptor = "I")
	public int height;

	@OriginalMember(owner = "client!pi", name = "a", descriptor = "(Ljava/awt/Canvas;III)V")
	public abstract void initialize(@OriginalArg(0) Canvas canvas, @OriginalArg(1) int width, @OriginalArg(3) int height);

	@OriginalMember(owner = "client!pi", name = "a", descriptor = "(IIILjava/awt/Graphics;IIII)V")
	public abstract void clip(@OriginalArg(0) int dstY, @OriginalArg(1) int srcX, @OriginalArg(2) int height, @OriginalArg(3) Graphics graphics, @OriginalArg(5) int dstX, @OriginalArg(6) int width, @OriginalArg(7) int srcY);
}
