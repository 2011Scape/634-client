package com.jagex.game.runetek5.client

import java.awt.Canvas
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.features.ClientFeatures

@OriginalClass("client!vg")
class GameCanvas @OriginalMember(owner = "client!vg", name = "<init>", descriptor = "(Ljava/awt/Component;)V") constructor(@OriginalArg(0) component2: Component?) : Canvas() {

	@OriginalMember(owner = "client!vg", name = "e", descriptor = "Ljava/awt/Component;")
	private val compont: Component?

	init {
		this.compont = component2
	}

	@OriginalMember(owner = "client!vg", name = "update", descriptor = "(Ljava/awt/Graphics;)V")
	override fun update(@OriginalArg(0) graphics: Graphics?) {
		this.compont!!.update(ClientFeatures.scaleGraphics(graphics))
	}

	@OriginalMember(owner = "client!vg", name = "paint", descriptor = "(Ljava/awt/Graphics;)V")
	override fun paint(@OriginalArg(0) graphics: Graphics?) {
		this.compont!!.paint(ClientFeatures.scaleGraphics(graphics))
	}

	// Not part of the original client: with client.scale the canvas is that many times larger on screen
	// than the game, which draws, sizes and reads the mouse in its own pixels.
	override fun getGraphics(): Graphics? {
		return ClientFeatures.scaleGraphics(super.getGraphics())
	}

	override fun setSize(width: Int, height: Int) {
		super.setSize(ClientFeatures.scale(width), ClientFeatures.scale(height))
	}

	override fun getSize(): Dimension {
		val size: Dimension = super.getSize()
		return Dimension(ClientFeatures.unscale(size.width), ClientFeatures.unscale(size.height))
	}

	override fun processMouseEvent(event: MouseEvent?) {
		super.processMouseEvent(ClientFeatures.unscaleEvent(event))
	}

	override fun processMouseMotionEvent(event: MouseEvent?) {
		super.processMouseMotionEvent(ClientFeatures.unscaleEvent(event))
	}

	override fun processMouseWheelEvent(event: MouseWheelEvent?) {
		super.processMouseWheelEvent(ClientFeatures.unscaleEvent(event) as MouseWheelEvent?)
	}
}
