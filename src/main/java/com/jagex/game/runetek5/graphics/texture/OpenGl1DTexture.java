package com.jagex.game.runetek5.graphics.texture;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.graphics.gl.GlTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!wha")
public final class OpenGl1DTexture extends GlTexture {

	@OriginalMember(owner = "client!wha", name = "v", descriptor = "I")
	private final int width;

	@OriginalMember(owner = "client!wha", name = "<init>", descriptor = "(Lclient!qo;II[BI)V")
	public OpenGl1DTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int internalFormat, @OriginalArg(2) int width, @OriginalArg(3) byte[] data, @OriginalArg(4) int format) {
		super(glToolkit, 3552, internalFormat, width, false);
		this.width = width;
		super.glToolkit.setActiveTexture(this);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_LINES);
		OpenGL.glTexImage1Dub(super.target, 0, super.internalFormat, this.width, 0, format, OpenGLConstants.GL_UNSIGNED_BYTE, data, 0);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_TRIANGLES);
		this.setLinearFilter(true);
	}

	@OriginalMember(owner = "client!wha", name = "a", descriptor = "(I)V")
	@Override
	public void detach() {
	}

	@OriginalMember(owner = "client!wha", name = "a", descriptor = "(BZ)V")
	public void setClampToEdge() {
		super.glToolkit.setActiveTexture(this);
		OpenGL.glTexParameteri(super.target, OpenGLConstants.GL_TEXTURE_WRAP_S, 33071);
	}
}
