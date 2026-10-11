package com.jagex.game.runetek5.graphics.texture.sampler;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.graphics.GpuTexture;
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.DataType;
import com.jagex.game.runetek5.PixelFormat;
import com.jagex.game.runetek5.toolkit.GlxToolkit;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;

@OriginalClass("client!bm")
public final class GlTexture2D extends MipmapTextureSampler implements GpuTexture {

	@OriginalMember(owner = "client!bm", name = "F", descriptor = "I")
	private final int width;

	@OriginalMember(owner = "client!bm", name = "x", descriptor = "I")
	private final int height;

	@OriginalMember(owner = "client!bm", name = "<init>", descriptor = "(Lclient!bga;IIZ[III)V")
	public GlTexture2D(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) boolean mipmapping, @OriginalArg(4) int[] pixels, @OriginalArg(5) int offset, @OriginalArg(6) int rowLength) {
		super(glxToolkit, OpenGLConstants.GL_TEXTURE_2D, GLFormat.HDR, GLFormat.RGBA, width * height, mipmapping);
		this.width = width;
		this.height = height;
		super.activeToolkit.setTextureSampler(this);
		if (mipmapping && rowLength == 0) {
			this.generateMipmaps(width, height, pixels, super.target);
		} else {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, rowLength);
			OpenGL.glTexImage2Di(super.target, 0, OpenGLConstants.GL_RGBA, this.width, this.height, 0, OpenGLConstants.GL_BGRA, super.activeToolkit.pixelType, pixels, offset * 4);
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, 0);
		}
	}

	@OriginalMember(owner = "client!bm", name = "<init>", descriptor = "(Lclient!bga;Lclient!wq;IIZ[BII)V")
	public GlTexture2D(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) PixelFormat pixelFormat, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) boolean mipmapping, @OriginalArg(5) byte[] data, @OriginalArg(6) int offset, @OriginalArg(7) int rowLength) {
		super(glxToolkit, 3553, pixelFormat, GLFormat.RGBA, height * width, mipmapping);
		this.width = width;
		this.height = height;
		super.activeToolkit.setTextureSampler(this);
		OpenGL.glPixelStorei(3317, 1);
		if (mipmapping) {
			this.generateMipmaps(width, data, height, super.target);
		} else {
			OpenGL.glPixelStorei(3314, rowLength);
			OpenGL.glTexImage2Dub(super.target, 0, this.getInternalFormat(), width, height, 0, MipmapTextureSampler.getGlFormat(super.pixelFormat), 5121, data, offset);
			OpenGL.glPixelStorei(3314, 0);
		}
		OpenGL.glPixelStorei(3317, 4);
	}

	@OriginalMember(owner = "client!bm", name = "<init>", descriptor = "(Lclient!bga;Lclient!wq;Lclient!baa;II)V")
	public GlTexture2D(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) PixelFormat pixelFormat, @OriginalArg(2) DataType pixelActionFormat, @OriginalArg(3) int width, @OriginalArg(4) int height) {
		super(glxToolkit, 3553, pixelFormat, pixelActionFormat, height * width, false);
		this.width = width;
		this.height = height;
		super.activeToolkit.setTextureSampler(this);
		OpenGL.glTexImage2Dub(super.target, 0, this.getInternalFormat(), width, height, 0, MipmapTextureSampler.getGlFormat(super.pixelFormat), DataType.getGlType(super.pixelActionFormat), null, 0);
	}

	@OriginalMember(owner = "client!bm", name = "<init>", descriptor = "(Lclient!bga;Lclient!wq;IIZ[FII)V")
	public GlTexture2D(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) PixelFormat pixelFormat, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) boolean mipmapping, @OriginalArg(5) float[] data, @OriginalArg(6) int offset, @OriginalArg(7) int rowLength) {
		super(glxToolkit, 3553, pixelFormat, DataType.aClass26_8, width * height, mipmapping);
		this.width = width;
		this.height = height;
		super.activeToolkit.setTextureSampler(this);
		if (mipmapping) {
			OpenGL.glPixelStorei(3314, rowLength);
			OpenGL.glTexImage2Df(super.target, 0, this.getInternalFormat(), width, height, 0, MipmapTextureSampler.getGlFormat(super.pixelFormat), 5126, data, offset * 4);
			OpenGL.glPixelStorei(3314, 0);
		} else {
			this.generateMipmaps(super.target, width, data, height);
		}
	}

	@OriginalMember(owner = "client!bm", name = "a", descriptor = "(BF)F")
	@Override
	public float toTextureCoordT(@OriginalArg(1) float y) {
		return y / (float) this.height;
	}

	@OriginalMember(owner = "client!bm", name = "a", descriptor = "(FI)F")
	@Override
	public float toTextureCoordS(@OriginalArg(0) float x) {
		return x / (float) this.width;
	}

	@OriginalMember(owner = "client!bm", name = "a", descriptor = "(IB[FLclient!wq;IIIII)V")
	public void updateFloatData(@OriginalArg(2) float[] data, @OriginalArg(3) PixelFormat pixelFormat, @OriginalArg(7) int width, @OriginalArg(8) int height) {
		super.activeToolkit.setTextureSampler(this);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, 0);
		OpenGL.glTexSubImage2Df(super.target, 0, 0, 0, width, height, MipmapTextureSampler.getGlFormat(pixelFormat), OpenGLConstants.GL_UNSIGNED_BYTE, data, 0);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, 0);
	}

	@OriginalMember(owner = "client!bm", name = "a", descriptor = "(IIBIIII[BLclient!wq;)V")
	@Override
	public void upload(@OriginalArg(3) int rowLength, @OriginalArg(4) int height, @OriginalArg(5) int width, @OriginalArg(7) byte[] data, @OriginalArg(8) PixelFormat pixelFormat) {
		super.activeToolkit.setTextureSampler(this);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, 1);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, rowLength);
		OpenGL.glTexSubImage2Dub(super.target, 0, 0, 0, width, height, MipmapTextureSampler.getGlFormat(pixelFormat), OpenGLConstants.GL_UNSIGNED_BYTE, data, 0);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, 0);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, 4);
	}

	@OriginalMember(owner = "client!bm", name = "a", descriptor = "(I[IIIIBI)V")
	@Override
	public void readPixels(@OriginalArg(1) int[] dest, @OriginalArg(2) int length, @OriginalArg(4) int height) {
		@Pc(12) int[] pixels = new int[this.width * this.height];
		super.activeToolkit.setTextureSampler(this);
		OpenGL.glGetTexImagei(super.target, 0, OpenGLConstants.GL_BGRA, OpenGLConstants.GL_UNSIGNED_BYTE, pixels, 0);
		for (@Pc(31) int i = 0; i < height; i++) {
			Arrays.copy(pixels, (height - i - 1) * this.width, dest, length * i, length);
		}
	}

	@OriginalMember(owner = "client!bm", name = "a", descriptor = "(Z)I")
	@Override
	public int getTextureWidth() {
		return this.width;
	}

	@OriginalMember(owner = "client!bm", name = "a", descriptor = "(I)I")
	@Override
	public int getTextureHeight() {
		return this.height;
	}

	@OriginalMember(owner = "client!bm", name = "b", descriptor = "(I)Z")
	@Override
	public boolean supportsTextureRepeat() {
		return true;
	}

	@OriginalMember(owner = "client!bm", name = "a", descriptor = "(II[IIIIII)V")
	@Override
	public void updateTextureRegion(@OriginalArg(0) int rowLength, @OriginalArg(2) int[] pixels, @OriginalArg(3) int x, @OriginalArg(5) int width, @OriginalArg(6) int y, @OriginalArg(7) int height) {
		super.activeToolkit.setTextureSampler(this);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, rowLength);
		OpenGL.glTexSubImage2Di(super.target, 0, x, y, width, height, OpenGLConstants.GL_BGRA, super.activeToolkit.pixelType, pixels, 0);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, 0);
	}

	@OriginalMember(owner = "client!bm", name = "a", descriptor = "(ZZI)V")
	@Override
	public void setWarp(@OriginalArg(0) boolean repeatS, @OriginalArg(1) boolean repeatT) {
		super.activeToolkit.setTextureSampler(this);
		OpenGL.glTexParameteri(super.target, OpenGLConstants.GL_TEXTURE_WRAP_S, repeatS ? OpenGLConstants.GL_REPEAT : 33071);
		OpenGL.glTexParameteri(super.target, OpenGLConstants.GL_TEXTURE_WRAP_T, repeatT ? OpenGLConstants.GL_REPEAT : 33071);
	}
}
