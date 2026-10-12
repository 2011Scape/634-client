package com.jagex.game.runetek5.graphics.texture.sampler

import com.jagex.game.runetek5.CubeTextureSampler
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.toolkit.GlxToolkit
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!le")
class GlCubeMapTexture : MipmapTextureSampler, CubeTextureSampler {

	@OriginalMember(owner = "client!le", name = "<init>", descriptor = "(Lclient!bga;IZ[[I)V")
	constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) size: Int, @OriginalArg(2) mipmapping: Boolean, @OriginalArg(3) faces: Array<IntArray?>?) : super(glxToolkit, 34067, GLFormat.HDR, GLFormat.RGBA, size * 6 * size, mipmapping) {
		super.activeToolkit!!.setTextureSampler(this)
		@Pc(22) var face: Int
		if (mipmapping) {
			face = 0
			while (face < 6) {
				this.generateMipmaps(size, size, faces!![face], face + 34069)
				face++
			}
		} else {
			face = 0
			while (face < 6) {
				OpenGL.glTexImage2Di(face + OpenGLConstants.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, this.getInternalFormat(), size, size, 0, MipmapTextureSampler.getGlFormat(super.pixelFormat), super.activeToolkit!!.pixelType, faces!![face], 0)
				face++
			}
		}
	}
}
