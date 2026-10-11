package com.jagex.game.runetek5.graphics.gl;

import com.jagex.game.runetek5.cache.node.lru.OpenGlRenderBuffer;
import com.jagex.game.runetek5.toolkit.companion.Binding;
import com.jagex.game.runetek5.GlRenderTarget;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.client.GlColorAttachments;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!rg")
public final class GlFramebuffer implements GlRenderTarget {

	@OriginalMember(owner = "client!rg", name = "s", descriptor = "I")
	private int width;

	@OriginalMember(owner = "client!rg", name = "u", descriptor = "I")
	private int occupiedSlots;

	@OriginalMember(owner = "client!rg", name = "w", descriptor = "I")
	private int height;

	@OriginalMember(owner = "client!rg", name = "l", descriptor = "[Lclient!pca;")
	private final Binding[] attachments = new Binding[9];

	@OriginalMember(owner = "client!rg", name = "i", descriptor = "I")
	private int bindFlags = 0;

	@OriginalMember(owner = "client!rg", name = "j", descriptor = "I")
	private int bindTarget = -1;

	@OriginalMember(owner = "client!rg", name = "t", descriptor = "Lclient!qo;")
	private final GlToolkit glToolkit;

	@OriginalMember(owner = "client!rg", name = "B", descriptor = "I")
	private final int handle;

	@OriginalMember(owner = "client!rg", name = "<init>", descriptor = "(Lclient!qo;)V")
	public GlFramebuffer(@OriginalArg(0) GlToolkit glToolkit) {
		if (!glToolkit.extFramebufferObject) {
			throw new IllegalStateException("");
		}
		this.glToolkit = glToolkit;
		OpenGL.glGenFramebuffersEXT(1, GlFramebuffer.GEN_FRAMEBUFFER_IDS, 0);
		this.handle = GlFramebuffer.GEN_FRAMEBUFFER_IDS[0];
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(B)V")
	@Override
	public void unbindRead() {
		// Not part of the original client: the window's framebuffer is the scaled target with client.scale.
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.glToolkit.defaultFramebuffer());
		this.bindFlags &= 0xFFFFFFFE;
		this.bindTarget = this.getBindTarget();
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(IIILclient!q;)V")
	public void attachCubeFace(@OriginalArg(2) int faceTarget, @OriginalArg(3) GlCubeTexture glCubeTexture) {
		this.bindCubeFaceAttachment(glCubeTexture, faceTarget);
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(I)V")
	@Override
	public void bind() {
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.handle);
		this.bindFlags |= 0x4;
		this.bindTarget = this.getBindTarget();
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(II)V")
	public void clearAttachment(@OriginalArg(1) int slot) {
		if (this.attachments[slot] != null) {
			this.attachments[slot].detach();
		}
		this.occupiedSlots &= ~(0x1 << slot);
		this.attachments[slot] = null;
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(IBLclient!q;II)V")
	private void bindCubeFaceAttachment(@OriginalArg(2) GlCubeTexture glCubeTexture, @OriginalArg(4) int faceTarget) {
		if (this.bindTarget == -1) {
			throw new RuntimeException();
		}
		if ((this.occupiedSlots & 0xFFFFFFFE) == 0) {
			this.width = glCubeTexture.size;
			this.height = glCubeTexture.size;
		} else if (glCubeTexture.size != this.width || this.height != glCubeTexture.size) {
			throw new RuntimeException();
		}
		glCubeTexture.attachToFramebuffer(this.bindTarget, faceTarget, GlColorAttachments.COLOR_ATTACHMENTS[0]);
		this.attachments[0] = glCubeTexture;
		this.occupiedSlots |= 0x1;
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(IB)V")
	public void drawBuffer(@OriginalArg(0) int slot) {
		if (this.bindTarget == -1) {
			throw new RuntimeException();
		}
		OpenGL.glDrawBuffer(GlColorAttachments.COLOR_ATTACHMENTS[slot]);
	}

	@OriginalMember(owner = "client!rg", name = "c", descriptor = "(I)V")
	@Override
	public void unbind() {
		// Not part of the original client: the window's framebuffer is the scaled target with client.scale.
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.glToolkit.defaultFramebuffer());
		this.bindFlags &= 0xFFFFFFFB;
		this.bindTarget = this.getBindTarget();
	}

	@OriginalMember(owner = "client!rg", name = "h", descriptor = "(I)I")
	private int getBindTarget() {
		if ((this.bindFlags & 0x4) != 0) {
			return 36160;
		} else if ((this.bindFlags & 0x2) == 0) {
			return (this.bindFlags & 0x1) == 0 ? -1 : 36008;
		} else {
			return 36009;
		}
	}

	@OriginalMember(owner = "client!rg", name = "e", descriptor = "(I)V")
	@Override
	public void unbindDraw() {
		// Not part of the original client: the window's framebuffer is the scaled target with client.scale.
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.glToolkit.defaultFramebuffer());
		this.bindFlags &= 0xFFFFFFFD;
		this.bindTarget = this.getBindTarget();
	}

	@OriginalMember(owner = "client!rg", name = "b", descriptor = "(II)V")
	public void readBuffer() {
		if (this.bindTarget == -1) {
			throw new RuntimeException();
		}
		OpenGL.glReadBuffer(GlColorAttachments.COLOR_ATTACHMENTS[0]);
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(ILclient!tp;II)V")
	private void bindTextureAttachment(@OriginalArg(1) OpenGl2DTexture texture, @OriginalArg(2) int slot) {
		if (this.bindTarget == -1) {
			throw new RuntimeException();
		}
		@Pc(16) int slotBit = 0x1 << slot;
		if ((~slotBit & this.occupiedSlots) == 0) {
			this.width = texture.width;
			this.height = texture.length;
		} else if (texture.width != this.width || texture.length != this.height) {
			throw new RuntimeException();
		}
		texture.attachToFramebuffer(this.bindTarget, GlColorAttachments.COLOR_ATTACHMENTS[slot]);
		this.attachments[slot] = texture;
		this.occupiedSlots |= slotBit;
	}

	@OriginalMember(owner = "client!rg", name = "b", descriptor = "(I)V")
	@Override
	public void bindDraw() {
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.handle);
		this.bindFlags |= 0x2;
		this.bindTarget = this.getBindTarget();
	}

	// Not part of the original client: the GL name of this framebuffer, for the scaled target.
	public int framebufferId() {
		return this.handle;
	}

	@OriginalMember(owner = "client!rg", name = "i", descriptor = "(I)Z")
	public boolean isFramebufferComplete() {
		@Pc(7) int status = OpenGL.glCheckFramebufferStatusEXT(this.bindTarget);
		return status == OpenGLConstants.GL_FRAMEBUFFER_COMPLETE;
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(ILclient!kw;I)V")
	public void attachRenderbuffer(@OriginalArg(0) int slot, @OriginalArg(1) OpenGlRenderBuffer renderbuffer) {
		if (this.bindTarget == -1) {
			throw new RuntimeException();
		}
		@Pc(15) int slotBit = 0x1 << slot;
		if ((this.occupiedSlots & ~slotBit) == 0) {
			this.height = renderbuffer.height;
			this.width = renderbuffer.width;
		} else if (this.width != renderbuffer.width || this.height != renderbuffer.height) {
			throw new RuntimeException();
		}
		renderbuffer.attach(GlColorAttachments.COLOR_ATTACHMENTS[slot], this.bindTarget);
		this.attachments[slot] = renderbuffer;
		this.occupiedSlots |= slotBit;
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(Lclient!tp;II)V")
	public void attachTexture(@OriginalArg(0) OpenGl2DTexture texture, @OriginalArg(2) int slot) {
		this.bindTextureAttachment(texture, slot);
	}

	@OriginalMember(owner = "client!rg", name = "d", descriptor = "(I)V")
	@Override
	public void bindRead() {
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.handle);
		this.bindFlags |= 0x1;
		this.bindTarget = this.getBindTarget();
	}

	@OriginalMember(owner = "client!rg", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() throws Throwable {
		this.glToolkit.requestFrameBufferCleanup(this.handle);
		super.finalize();
	}
	@OriginalMember(owner = "client!mia", name = "e", descriptor = "[I")
	public static final int[] GEN_FRAMEBUFFER_IDS = new int[16];
}
