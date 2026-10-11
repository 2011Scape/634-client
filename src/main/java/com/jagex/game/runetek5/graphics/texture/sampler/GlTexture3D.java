package com.jagex.game.runetek5.graphics.texture.sampler;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.PixelFormat;
import com.jagex.game.runetek5.Texture3DSampler;
import com.jagex.game.runetek5.toolkit.GlxToolkit;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;

@OriginalClass("client!qba")
public final class GlTexture3D extends MipmapTextureSampler implements Texture3DSampler {

	@OriginalMember(owner = "client!qba", name = "z", descriptor = "I")
	private final int depth;

	@OriginalMember(owner = "client!qba", name = "G", descriptor = "I")
	private final int width;

	@OriginalMember(owner = "client!qba", name = "E", descriptor = "I")
	private final int height;

	@OriginalMember(owner = "client!qba", name = "<init>", descriptor = "(Lclient!bga;Lclient!wq;III[B)V")
	public GlTexture3D(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) PixelFormat pixelFormat, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int depth, @OriginalArg(5) byte[] data) {
		super(glxToolkit, 32879, pixelFormat, GLFormat.RGBA, height * width * depth, false);
		this.depth = depth;
		this.width = width;
		this.height = height;
		super.activeToolkit.setTextureSampler(this);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, 1);
		OpenGL.glTexImage3Dub(super.target, 0, this.getInternalFormat(), this.width, this.height, this.depth, 0, MipmapTextureSampler.getGlFormat(super.pixelFormat), OpenGLConstants.GL_UNSIGNED_BYTE, data, 0);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, 4);
	}
}
