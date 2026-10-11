package com.jagex.game.runetek5.graphics.texture.sampler;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.CubeTextureSampler;
import com.jagex.game.runetek5.toolkit.GlxToolkit;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;

@OriginalClass("client!le")
public final class GlCubeMapTexture extends MipmapTextureSampler implements CubeTextureSampler {

	@OriginalMember(owner = "client!le", name = "<init>", descriptor = "(Lclient!bga;IZ[[I)V")
	public GlCubeMapTexture(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) int size, @OriginalArg(2) boolean mipmapping, @OriginalArg(3) int[][] faces) {
		super(glxToolkit, 34067, GLFormat.HDR, GLFormat.RGBA, size * 6 * size, mipmapping);
		super.activeToolkit.setTextureSampler(this);
		@Pc(22) int face;
		if (mipmapping) {
			for (face = 0; face < 6; face++) {
				this.generateMipmaps(size, size, faces[face], face + 34069);
			}
		} else {
			for (face = 0; face < 6; face++) {
				OpenGL.glTexImage2Di(face + OpenGLConstants.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, this.getInternalFormat(), size, size, 0, MipmapTextureSampler.getGlFormat(super.pixelFormat), super.activeToolkit.pixelType, faces[face], 0);
			}
		}
	}
}
