/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlSceneTarget (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import kotlin.jvm.JvmField

/**
 * Not part of the original client: the framebuffer the HdContext renderer draws the 3D view into with
 * client.msaa, client.smaa or client.renderScale. It is the view's size times the render scale,
 * multisampled when there are samples, and resolved into a texture that is drawn into the frame.
 */
class HdSceneTarget {

	@JvmField
	val width: Int
	@JvmField
	val height: Int
	@JvmField
	val samples: Int
	@JvmField
	val resolve: HdFramebuffer
	@JvmField
	val colour: Hd2DTexture
	@JvmField
	var multisample: HdFramebuffer? = null
	private var multisampleColour: HdRenderBuffer? = null
	private var multisampleDepth: HdRenderBuffer? = null
	private var depth: HdRenderBuffer? = null
	@JvmField
	var complete: Boolean = false

	constructor(toolkit: HdToolkit?, width: Int, height: Int, samples: Int, linear: Boolean) {
		this.width = width
		this.height = height
		this.samples = samples
		this.resolve = HdFramebuffer(toolkit)
		this.colour = Hd2DTexture(toolkit, OpenGLConstants.GL_TEXTURE_2D, OpenGLConstants.GL_RGBA, width, height)
		this.colour.setLinearFilter(linear)
		this.resolve.bind()
		this.resolve.attachTexture(this.colour, 0)
		this.resolve.drawBuffer(0)
		if (samples > 0) {
			this.multisample = HdFramebuffer(toolkit)
			this.multisampleColour = HdRenderBuffer(toolkit, OpenGLConstants.GL_RGBA, width, height, samples)
			this.multisampleDepth = HdRenderBuffer(toolkit, OpenGLConstants.GL_DEPTH_COMPONENT, width, height, samples)
			this.multisample!!.bind()
			this.multisample!!.attachRenderbuffer(0, this.multisampleColour)
			this.multisample!!.attachRenderbuffer(8, this.multisampleDepth)
			this.multisample!!.drawBuffer(0)
			this.complete = this.multisample!!.isFramebufferComplete() && this.resolve.isFramebufferComplete()
		} else {
			this.depth = HdRenderBuffer(toolkit, OpenGLConstants.GL_DEPTH_COMPONENT, width, height)
			this.resolve.attachRenderbuffer(8, this.depth)
			this.complete = this.resolve.isFramebufferComplete()
		}
	}

	/** The framebuffer the view is drawn into: the multisampled one when there are samples. */
	fun target(): HdFramebuffer? {
		return if (this.multisample == null) this.resolve else this.multisample
	}

	/** Lets go of the framebuffers, textures and renderbuffers. */
	fun release() {
		if (this.multisample != null) {
			this.multisample!!.bind()
			this.multisample!!.clearAttachment(0)
			this.multisample!!.clearAttachment(8)
			this.multisampleColour!!.dispose()
			this.multisampleDepth!!.dispose()
			this.multisample = null
		}
		this.resolve.bind()
		this.resolve.clearAttachment(0)
		this.resolve.clearAttachment(8)
		if (this.depth != null) {
			this.depth!!.dispose()
		}
		this.colour.delete()
	}
}
