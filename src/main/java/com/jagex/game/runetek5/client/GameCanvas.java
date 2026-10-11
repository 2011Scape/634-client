package com.jagex.game.runetek5.client;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.features.ClientFeatures;

@OriginalClass("client!vg")
public final class GameCanvas extends Canvas {

	@OriginalMember(owner = "client!vg", name = "e", descriptor = "Ljava/awt/Component;")
	private final Component compont;

	@OriginalMember(owner = "client!vg", name = "<init>", descriptor = "(Ljava/awt/Component;)V")
	public GameCanvas(@OriginalArg(0) Component component2) {
		this.compont = component2;
	}

	@OriginalMember(owner = "client!vg", name = "update", descriptor = "(Ljava/awt/Graphics;)V")
	@Override
	public void update(@OriginalArg(0) Graphics graphics) {
		this.compont.update(ClientFeatures.scaleGraphics(graphics));
	}

	@OriginalMember(owner = "client!vg", name = "paint", descriptor = "(Ljava/awt/Graphics;)V")
	@Override
	public void paint(@OriginalArg(0) Graphics graphics) {
		this.compont.paint(ClientFeatures.scaleGraphics(graphics));
	}

	// Not part of the original client: with client.scale the canvas is that many times larger on screen
	// than the game, which draws, sizes and reads the mouse in its own pixels.
	@Override
	public Graphics getGraphics() {
		return ClientFeatures.scaleGraphics(super.getGraphics());
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(ClientFeatures.scale(width), ClientFeatures.scale(height));
	}

	@Override
	public Dimension getSize() {
		Dimension size = super.getSize();
		return new Dimension(ClientFeatures.unscale(size.width), ClientFeatures.unscale(size.height));
	}

	@Override
	protected void processMouseEvent(MouseEvent event) {
		super.processMouseEvent(ClientFeatures.unscaleEvent(event));
	}

	@Override
	protected void processMouseMotionEvent(MouseEvent event) {
		super.processMouseMotionEvent(ClientFeatures.unscaleEvent(event));
	}

	@Override
	protected void processMouseWheelEvent(MouseWheelEvent event) {
		super.processMouseWheelEvent((MouseWheelEvent) ClientFeatures.unscaleEvent(event));
	}
}
