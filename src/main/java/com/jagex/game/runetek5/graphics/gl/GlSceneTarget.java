package com.jagex.game.runetek5.graphics.gl;

import com.jagex.game.runetek5.cache.node.lru.OpenGlRenderBuffer;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;

/**
 * Not part of the original client: the framebuffer the OpenGL renderer draws the 3D view into with
 * client.msaa, client.smaa or client.renderScale. It is the view's size times the render scale,
 * multisampled when there are samples, and resolved into a texture that is drawn into the frame.
 */
public final class GlSceneTarget {

	public final int width;
	public final int height;
	public final int samples;
	public final GlFramebuffer resolve;
	public final OpenGl2DTexture colour;
	public GlFramebuffer multisample;
	private OpenGlRenderBuffer multisampleColour;
	private OpenGlRenderBuffer multisampleDepth;
	private OpenGlRenderBuffer depth;
	public boolean complete;

	public GlSceneTarget(GlToolkit toolkit, int width, int height, int samples, boolean linear) {
		this.width = width;
		this.height = height;
		this.samples = samples;
		this.resolve = new GlFramebuffer(toolkit);
		this.colour = new OpenGl2DTexture(toolkit, OpenGLConstants.GL_TEXTURE_2D, OpenGLConstants.GL_RGBA, width, height);
		this.colour.setLinearFilter(linear);
		this.resolve.bind();
		this.resolve.attachTexture(this.colour, 0);
		this.resolve.drawBuffer(0);
		if (samples > 0) {
			this.multisample = new GlFramebuffer(toolkit);
			this.multisampleColour = new OpenGlRenderBuffer(toolkit, OpenGLConstants.GL_RGBA, width, height, samples);
			this.multisampleDepth = new OpenGlRenderBuffer(toolkit, OpenGLConstants.GL_DEPTH_COMPONENT, width, height, samples);
			this.multisample.bind();
			this.multisample.attachRenderbuffer(0, this.multisampleColour);
			this.multisample.attachRenderbuffer(8, this.multisampleDepth);
			this.multisample.drawBuffer(0);
			this.complete = this.multisample.isFramebufferComplete() && this.resolve.isFramebufferComplete();
		} else {
			this.depth = new OpenGlRenderBuffer(toolkit, OpenGLConstants.GL_DEPTH_COMPONENT, width, height);
			this.resolve.attachRenderbuffer(8, this.depth);
			this.complete = this.resolve.isFramebufferComplete();
		}
	}

	/** The framebuffer the view is drawn into: the multisampled one when there are samples. */
	public GlFramebuffer target() {
		return this.multisample == null ? this.resolve : this.multisample;
	}

	/** Lets go of the framebuffers, textures and renderbuffers. */
	public void release() {
		if (this.multisample != null) {
			this.multisample.bind();
			this.multisample.clearAttachment(0);
			this.multisample.clearAttachment(8);
			this.multisampleColour.dispose();
			this.multisampleDepth.dispose();
			this.multisample = null;
		}
		this.resolve.bind();
		this.resolve.clearAttachment(0);
		this.resolve.clearAttachment(8);
		if (this.depth != null) {
			this.depth.dispose();
		}
		this.colour.delete();
	}
}
