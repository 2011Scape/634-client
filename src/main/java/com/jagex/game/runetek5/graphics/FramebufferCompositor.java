package com.jagex.game.runetek5.graphics;

import com.jagex.game.runetek5.cache.node.lru.OpenGlRenderBuffer;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture;
import com.jagex.game.runetek5.graphics.gl.GlFramebuffer;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.PostProcessEffect;

@OriginalClass("client!hh")
public final class FramebufferCompositor {

	@OriginalMember(owner = "client!hh", name = "t", descriptor = "Z")
	private boolean isCompositionPending;

	@OriginalMember(owner = "client!hh", name = "A", descriptor = "Lclient!kw;")
	private OpenGlRenderBuffer hdrRenderBuffer;

	@OriginalMember(owner = "client!hh", name = "C", descriptor = "Lclient!kw;")
	private OpenGlRenderBuffer renderBuffer;

	@OriginalMember(owner = "client!hh", name = "F", descriptor = "Lclient!tp;")
	private OpenGl2DTexture activeTexture;

	@OriginalMember(owner = "client!hh", name = "f", descriptor = "I")
	private int t = 1;

	@OriginalMember(owner = "client!hh", name = "g", descriptor = "I")
	private int vertexCoordinateY = 0;

	@OriginalMember(owner = "client!hh", name = "r", descriptor = "I")
	private int vertexCoordinateX = 0;

	@OriginalMember(owner = "client!hh", name = "q", descriptor = "I")
	private int width = 1;

	@OriginalMember(owner = "client!hh", name = "w", descriptor = "Lclient!uh;")
	private final Deque effectsList = new Deque();

	@OriginalMember(owner = "client!hh", name = "x", descriptor = "Z")
	private boolean isDepthBufferDirty = true;

	@OriginalMember(owner = "client!hh", name = "z", descriptor = "[Lclient!tp;")
	private OpenGl2DTexture[] textures2d = new OpenGl2DTexture[2];

	@OriginalMember(owner = "client!hh", name = "B", descriptor = "Z")
	private boolean pixelFormatSet = true;

	@OriginalMember(owner = "client!hh", name = "y", descriptor = "Z")
	private boolean isFboAttachmentDirty = true;

	@OriginalMember(owner = "client!hh", name = "D", descriptor = "Z")
	private boolean isFramebufferDirty = true;

	@OriginalMember(owner = "client!hh", name = "I", descriptor = "I")
	private int texturedFaceCount = 0;

	@OriginalMember(owner = "client!hh", name = "H", descriptor = "I")
	private int s = -1;

	@OriginalMember(owner = "client!hh", name = "J", descriptor = "Z")
	private boolean loadTexture = false;

	@OriginalMember(owner = "client!hh", name = "u", descriptor = "Lclient!qo;")
	private final GlToolkit activeToolkit;

	@OriginalMember(owner = "client!hh", name = "l", descriptor = "Lclient!rg;")
	private GlFramebuffer resolveDisplay;

	@OriginalMember(owner = "client!hh", name = "o", descriptor = "Lclient!rg;")
	private GlFramebuffer activeDisplay;

	@OriginalMember(owner = "client!hh", name = "p", descriptor = "Lclient!rg;")
	private GlFramebuffer multisampleDisplay;

	@OriginalMember(owner = "client!hh", name = "<init>", descriptor = "(Lclient!qo;)V")
	public FramebufferCompositor(@OriginalArg(0) GlToolkit glToolkit) {
		this.activeToolkit = glToolkit;
		if (this.activeToolkit.extFramebufferObject && this.activeToolkit.arbRectangleTexturesSupported) {
			this.activeDisplay = this.resolveDisplay = new GlFramebuffer(this.activeToolkit);
			if (this.activeToolkit.multiSamplesPreference > 1 && this.activeToolkit.framebufferBlit && this.activeToolkit.framebufferMultisample) {
				this.activeDisplay = this.multisampleDisplay = new GlFramebuffer(this.activeToolkit);
				return;
			}
		}
	}

	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(I)V")
	public void compositeEffects() {
		if (!this.isCompositionPending) {
			return;
		}
		if (this.multisampleDisplay != null) {
			this.activeToolkit.popDrawTarget(this.multisampleDisplay);
			@Pc(20) short blitMask = 16384;
			this.activeToolkit.pushDrawTarget(this.resolveDisplay);
			this.multisampleDisplay.readBuffer();
			this.resolveDisplay.drawBuffer(0);
			if (this.loadTexture) {
				blitMask = 16640;
			}
			OpenGL.glBlitFramebufferEXT(0, 0, this.width, this.t, 0, 0, this.width, this.t, blitMask, OpenGLConstants.GL_NEAREST);
			this.activeToolkit.popReadTarget(this.multisampleDisplay);
			this.activeToolkit.pushReadTarget(this.resolveDisplay);
		}
		this.activeToolkit.setupSpriteRendering();
		this.activeToolkit.setBlendMode(0);
		this.activeToolkit.setColourOp(1);
		this.activeToolkit.la();
		@Pc(97) int textureIndex = 0;
		@Pc(99) int colorAttachmentSlot = 1;
		@Pc(113) PostProcessEffect glPostProcessFilter;
		for (@Pc(105) PostProcessEffect glPostProcessFilter2 = (PostProcessEffect) this.effectsList.first(); glPostProcessFilter2 != null; glPostProcessFilter2 = glPostProcessFilter) {
			glPostProcessFilter = (PostProcessEffect) this.effectsList.next();
			@Pc(117) int numTexturedFaces = glPostProcessFilter2.getPassCount();
			for (@Pc(119) int i = 0; i < numTexturedFaces; i++) {
				glPostProcessFilter2.draw(this.activeTexture, this.textures2d[textureIndex], i);
				if (glPostProcessFilter == null && i == numTexturedFaces - 1) {
					this.activeToolkit.popTarget(this.resolveDisplay);
					this.activeToolkit.setViewportOffset(0, 0);
					OpenGL.glBegin(OpenGLConstants.GL_QUADS);
					OpenGL.glTexCoord2f(0.0F, (float) this.t);
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0F, 1.0F);
					OpenGL.glVertex2i(this.vertexCoordinateX, this.vertexCoordinateY);
					OpenGL.glTexCoord2f(0.0F, 0.0F);
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0F, 0.0F);
					OpenGL.glVertex2i(this.vertexCoordinateX, this.vertexCoordinateY + this.t);
					OpenGL.glTexCoord2f((float) this.width, 0.0F);
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 1.0F, 0.0F);
					OpenGL.glVertex2i(this.vertexCoordinateX + this.width, this.vertexCoordinateY + this.t);
					OpenGL.glTexCoord2f((float) this.width, (float) this.t);
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 1.0F, 1.0F);
					OpenGL.glVertex2i(this.width + this.vertexCoordinateX, this.vertexCoordinateY);
					OpenGL.glEnd();
				} else {
					this.resolveDisplay.drawBuffer(colorAttachmentSlot);
					OpenGL.glBegin(OpenGLConstants.GL_QUADS);
					OpenGL.glTexCoord2f(0.0F, (float) this.t);
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0F, 1.0F);
					OpenGL.glVertex2i(0, 0);
					OpenGL.glTexCoord2f(0.0F, 0.0F);
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0F, 0.0F);
					OpenGL.glVertex2i(0, this.t);
					OpenGL.glTexCoord2f((float) this.width, 0.0F);
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 1.0F, 0.0F);
					OpenGL.glVertex2i(this.width, this.t);
					OpenGL.glTexCoord2f((float) this.width, (float) this.t);
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 1.0F, 1.0F);
					OpenGL.glVertex2i(this.width, 0);
					OpenGL.glEnd();
				}
				glPostProcessFilter2.endPass(i);
				textureIndex = textureIndex + 1 & 0x1;
				colorAttachmentSlot = colorAttachmentSlot + 1 & 0x1;
			}
		}
		this.isCompositionPending = false;
	}

	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(Lclient!fba;Z)Z")
	public boolean addEffect(@OriginalArg(0) PostProcessEffect glPostProcessFilter) {
		if (this.activeDisplay != null) {
			if (glPostProcessFilter.isLoaded() || glPostProcessFilter.createShaders()) {
				this.effectsList.addLast(glPostProcessFilter);
				this.recalculateFormat();
				if (this.rebuildFramebuffers()) {
					if (this.width != -1 && this.t != -1) {
						glPostProcessFilter.resize(this.t, this.width);
					}
					glPostProcessFilter.isActive = true;
					return true;
				}
			}
			this.removeEffect(glPostProcessFilter);
		}
		return false;
	}

	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(IIIII)Z")
	public boolean prepareForRendering(@OriginalArg(0) int width, @OriginalArg(1) int x, @OriginalArg(2) int height, @OriginalArg(3) int y) {
		if (this.activeDisplay == null || this.effectsList.isEmpty()) {
			return false;
		}
		if (width != this.width || height != this.t) {
			this.width = width;
			this.t = height;
			for (@Pc(45) Node node = this.effectsList.first(); node != this.effectsList.sentinel; node = node.next) {
				((PostProcessEffect) node).resize(this.t, this.width);
			}
			this.pixelFormatSet = true;
			this.isDepthBufferDirty = true;
			this.isFboAttachmentDirty = true;
		}
		if (!this.rebuildFramebuffers()) {
			return false;
		}
		this.isCompositionPending = true;
		this.vertexCoordinateY = y;
		this.vertexCoordinateX = x;
		this.activeToolkit.pushTarget(this.activeDisplay);
		this.activeDisplay.drawBuffer(0);
		this.activeToolkit.setViewportOffset(this.t + this.vertexCoordinateY - this.activeToolkit.viewportHeight, -this.vertexCoordinateX);
		return true;
	}

	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(Z)Z")
	private boolean rebuildFramebuffers() {
		if (this.isDepthBufferDirty) {
			if (this.renderBuffer != null) {
				this.renderBuffer.dispose();
				this.renderBuffer = null;
			}
			if (this.activeTexture != null) {
				this.activeTexture.delete();
				this.activeTexture = null;
			}
			if (this.multisampleDisplay != null) {
				this.renderBuffer = new OpenGlRenderBuffer(this.activeToolkit, 6402, this.width, this.t, this.activeToolkit.multiSamplesPreference);
			}
			if (this.loadTexture) {
				this.activeTexture = new OpenGl2DTexture(this.activeToolkit, 34037, 6402, this.width, this.t);
			} else if (this.renderBuffer == null) {
				this.renderBuffer = new OpenGlRenderBuffer(this.activeToolkit, 6402, this.width, this.t);
			}
			this.isDepthBufferDirty = false;
			this.isFboAttachmentDirty = true;
			this.isFramebufferDirty = true;
		}
		if (this.pixelFormatSet) {
			if (this.hdrRenderBuffer != null) {
				this.hdrRenderBuffer.dispose();
				this.hdrRenderBuffer = null;
			}
			if (this.textures2d[0] != null) {
				this.textures2d[0].delete();
				this.textures2d[0] = null;
			}
			if (this.textures2d[1] != null) {
				this.textures2d[1].delete();
				this.textures2d[1] = null;
			}
			if (this.multisampleDisplay != null) {
				this.hdrRenderBuffer = new OpenGlRenderBuffer(this.activeToolkit, this.s, this.width, this.t, this.activeToolkit.multiSamplesPreference);
			}
			this.textures2d[0] = new OpenGl2DTexture(this.activeToolkit, 34037, this.s, this.width, this.t);
			this.textures2d[1] = this.texturedFaceCount > 1 ? new OpenGl2DTexture(this.activeToolkit, 34037, this.s, this.width, this.t) : null;
			this.isFboAttachmentDirty = true;
			this.isFramebufferDirty = true;
			this.pixelFormatSet = false;
		}
		if (this.isFboAttachmentDirty) {
			if (this.multisampleDisplay == null) {
				this.activeToolkit.pushTarget(this.resolveDisplay);
				this.resolveDisplay.clearAttachment(0);
				this.resolveDisplay.clearAttachment(1);
				this.resolveDisplay.clearAttachment(8);
				this.resolveDisplay.attachTexture(this.textures2d[0], 0);
				if (this.texturedFaceCount > 1) {
					this.resolveDisplay.attachTexture(this.textures2d[1], 1);
				}
				if (this.loadTexture) {
					this.resolveDisplay.attachTexture(this.activeTexture, 8);
				} else {
					this.resolveDisplay.attachRenderbuffer(8, this.renderBuffer);
				}
				this.activeToolkit.popTarget(this.resolveDisplay);
			} else {
				this.activeToolkit.pushTarget(this.resolveDisplay);
				this.resolveDisplay.clearAttachment(0);
				this.resolveDisplay.clearAttachment(1);
				this.resolveDisplay.clearAttachment(8);
				this.resolveDisplay.attachTexture(this.textures2d[0], 0);
				if (this.texturedFaceCount > 1) {
					this.resolveDisplay.attachTexture(this.textures2d[1], 1);
				}
				if (this.loadTexture) {
					this.resolveDisplay.attachTexture(this.activeTexture, 8);
				}
				this.activeToolkit.popTarget(this.resolveDisplay);
				this.activeToolkit.pushTarget(this.multisampleDisplay);
				this.multisampleDisplay.clearAttachment(0);
				this.multisampleDisplay.clearAttachment(8);
				this.multisampleDisplay.attachRenderbuffer(0, this.hdrRenderBuffer);
				this.multisampleDisplay.attachRenderbuffer(8, this.renderBuffer);
				this.activeToolkit.popTarget(this.multisampleDisplay);
			}
			this.isFboAttachmentDirty = false;
			this.isFramebufferDirty = true;
		}
		if (this.isFramebufferDirty) {
			this.activeToolkit.pushTarget(this.activeDisplay);
			this.isFramebufferDirty = !this.activeDisplay.isFramebufferComplete();
			this.activeToolkit.popTarget(this.activeDisplay);
		}
		return !this.isFramebufferDirty;
	}

	@OriginalMember(owner = "client!hh", name = "b", descriptor = "(Lclient!fba;Z)V")
	public void removeEffect(@OriginalArg(0) PostProcessEffect glPostProcessFilter) {
		glPostProcessFilter.isActive = false;
		glPostProcessFilter.destroy();
		glPostProcessFilter.unlink();
		this.recalculateFormat();
	}

	@OriginalMember(owner = "client!hh", name = "b", descriptor = "(B)V")
	public void shutdown() {
		this.hdrRenderBuffer = null;
		this.activeDisplay = this.multisampleDisplay = this.resolveDisplay = null;
		this.activeTexture = null;
		this.renderBuffer = null;
		this.textures2d = null;
		if (!this.effectsList.isEmpty()) {
			for (@Pc(44) Node node = this.effectsList.first(); node != this.effectsList.sentinel; node = node.next) {
				((PostProcessEffect) node).destroy();
			}
		}
		this.width = this.t = 1;
	}

	@OriginalMember(owner = "client!hh", name = "b", descriptor = "(I)Z")
	public boolean isDisplayAvailable() {
		return this.activeDisplay != null;
	}

	@OriginalMember(owner = "client!hh", name = "c", descriptor = "(B)V")
	private void recalculateFormat() {
		@Pc(7) boolean loadTexture = false;
		@Pc(9) int maxFormat = 0;
		@Pc(11) int texturedFaceCount = 0;
		@Pc(31) int local31;
		for (@Pc(17) PostProcessEffect glPostProcessFilter = (PostProcessEffect) this.effectsList.first(); glPostProcessFilter != null; glPostProcessFilter = (PostProcessEffect) this.effectsList.next()) {
			int effectFormat = glPostProcessFilter.getFormat();
			texturedFaceCount += glPostProcessFilter.getPassCount();
			if (effectFormat > maxFormat) {
				maxFormat = effectFormat;
			}
			loadTexture |= glPostProcessFilter.requiresDepth();
		}
		int internalFormat;
		if (maxFormat == 2) {
			internalFormat = 34836;
		} else if (maxFormat == 1) {
			internalFormat = 34842;
		} else {
			internalFormat = 6408;
		}
		if (this.s != internalFormat) {
			this.pixelFormatSet = true;
			this.s = internalFormat;
		}
		@Pc(99) int currFaces = this.texturedFaceCount > 2 ? 2 : this.texturedFaceCount;
		@Pc(106) int newFaces = texturedFaceCount <= 2 ? texturedFaceCount : 2;
		this.texturedFaceCount = texturedFaceCount;
		if (loadTexture != this.loadTexture) {
			this.loadTexture = loadTexture;
			this.isDepthBufferDirty = true;
		}
		if (newFaces != currFaces) {
			this.isFboAttachmentDirty = this.pixelFormatSet = true;
		}
	}
}
