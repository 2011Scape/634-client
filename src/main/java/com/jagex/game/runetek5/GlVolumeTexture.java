package com.jagex.game.runetek5;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.graphics.gl.GlTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!gj")
public final class GlVolumeTexture extends GlTexture {

	@OriginalMember(owner = "client!gj", name = "u", descriptor = "I")
	private int framebufferAttachment = -1;

	@OriginalMember(owner = "client!gj", name = "x", descriptor = "I")
	private int framebufferTarget = -1;

	@OriginalMember(owner = "client!gj", name = "s", descriptor = "I")
	public final int width;

	@OriginalMember(owner = "client!gj", name = "r", descriptor = "I")
	public final int depth;

	@OriginalMember(owner = "client!gj", name = "y", descriptor = "I")
	public final int height;

	@OriginalMember(owner = "client!gj", name = "<init>", descriptor = "(Lclient!qo;IIII[BI)V")
	public GlVolumeTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int internalFormat, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int depth, @OriginalArg(5) byte[] pixels, @OriginalArg(6) int format) {
		super(glToolkit, 32879, internalFormat, width * height * depth, false);
		this.width = width;
		this.depth = depth;
		this.height = height;
		super.glToolkit.setActiveTexture(this);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_LINES);
		OpenGL.glTexImage3Dub(super.target, 0, super.internalFormat, this.width, this.height, this.depth, 0, format, OpenGLConstants.GL_UNSIGNED_BYTE, pixels, 0);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_TRIANGLES);
		this.setLinearFilter(true);
	}

	@OriginalMember(owner = "client!gj", name = "<init>", descriptor = "(Lclient!qo;IIII)V")
	public GlVolumeTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int internalFormat, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int depth) {
		super(glToolkit, 32879, internalFormat, depth * width * height, false);
		this.width = width;
		this.height = height;
		this.depth = depth;
		super.glToolkit.setActiveTexture(this);
		OpenGL.glTexImage3Dub(super.target, 0, super.internalFormat, this.width, this.height, this.depth, 0, GlTexture.getBaseFormat(super.internalFormat), OpenGLConstants.GL_UNSIGNED_BYTE, null, 0);
		this.setLinearFilter(true);
	}

	@OriginalMember(owner = "client!gj", name = "a", descriptor = "(IIIIIIII)V")
	public void copyFromFramebuffer(@OriginalArg(2) int width, @OriginalArg(5) int zOffset, @OriginalArg(6) int height) {
		super.glToolkit.setActiveTexture(this);
		OpenGL.glCopyTexSubImage3D(super.target, OpenGLConstants.GL_POINTS, OpenGLConstants.GL_POINTS, OpenGLConstants.GL_POINTS, zOffset, 0, 0, width, height);
		OpenGL.glFlush();
	}

	@OriginalMember(owner = "client!gj", name = "a", descriptor = "(I)V")
	@Override
	public void detach() {
		OpenGL.glFramebufferTexture3DEXT(this.framebufferTarget, this.framebufferAttachment, super.target, 0, 0, 0);
		this.framebufferAttachment = -1;
		this.framebufferTarget = -1;
	}
}
