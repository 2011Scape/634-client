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

@OriginalClass("client!tw")
public final class GlRectangleTexture extends MipmapTextureSampler implements GpuTexture {

	@OriginalMember(owner = "client!tw", name = "E", descriptor = "I")
	private final int width;

	@OriginalMember(owner = "client!tw", name = "y", descriptor = "I")
	private final int height;

	@OriginalMember(owner = "client!tw", name = "<init>", descriptor = "(Lclient!bga;II[III)V")
	public GlRectangleTexture(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) int[] pixels, @OriginalArg(4) int offset, @OriginalArg(5) int rowLength) {
		super(glxToolkit, 34037, GLFormat.HDR, GLFormat.RGBA, width * height, false);
		this.width = width;
		this.height = height;
		super.activeToolkit.setTextureSampler(this);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, rowLength);
		OpenGL.glTexImage2Di(super.target, 0, OpenGLConstants.GL_RGBA, this.width, this.height, 0, OpenGLConstants.GL_BGRA, super.activeToolkit.pixelType, pixels, offset * 4);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, 0);
	}

	@OriginalMember(owner = "client!tw", name = "<init>", descriptor = "(Lclient!bga;Lclient!wq;II[BII)V")
	public GlRectangleTexture(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) PixelFormat pixelFormat, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) byte[] data, @OriginalArg(5) int offset, @OriginalArg(6) int rowLength) {
		super(glxToolkit, 34037, pixelFormat, GLFormat.RGBA, width * height, false);
		this.height = height;
		this.width = width;
		super.activeToolkit.setTextureSampler(this);
		OpenGL.glPixelStorei(3317, 1);
		OpenGL.glPixelStorei(3314, rowLength);
		OpenGL.glTexImage2Dub(super.target, 0, this.getInternalFormat(), width, height, 0, MipmapTextureSampler.getGlFormat(super.pixelFormat), 5121, data, offset);
		OpenGL.glPixelStorei(3314, 0);
		OpenGL.glPixelStorei(3317, 4);
	}

	@OriginalMember(owner = "client!tw", name = "<init>", descriptor = "(Lclient!bga;Lclient!wq;Lclient!baa;II)V")
	public GlRectangleTexture(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) PixelFormat pixelFormat, @OriginalArg(2) DataType pixelActionFormat, @OriginalArg(3) int width, @OriginalArg(4) int height) {
		super(glxToolkit, 34037, pixelFormat, pixelActionFormat, height * width, false);
		this.height = height;
		this.width = width;
		super.activeToolkit.setTextureSampler(this);
		OpenGL.glTexImage2Dub(super.target, 0, this.getInternalFormat(), width, height, 0, MipmapTextureSampler.getGlFormat(super.pixelFormat), DataType.getGlType(super.pixelActionFormat), null, 0);
	}

	@OriginalMember(owner = "client!tw", name = "<init>", descriptor = "(Lclient!bga;Lclient!wq;II[FII)V")
	public GlRectangleTexture(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) PixelFormat pixelFormat, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) float[] data, @OriginalArg(5) int offset, @OriginalArg(6) int rowLength) {
		super(glxToolkit, 34037, pixelFormat, DataType.aClass26_8, height * width, false);
		this.width = width;
		this.height = height;
		super.activeToolkit.setTextureSampler(this);
		OpenGL.glPixelStorei(3314, rowLength);
		OpenGL.glTexImage2Df(super.target, 0, this.getInternalFormat(), width, height, 0, MipmapTextureSampler.getGlFormat(super.pixelFormat), 5126, data, offset * 4);
		OpenGL.glPixelStorei(3314, 0);
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(I)I")
	@Override
	public int getTextureHeight() {
		return this.height;
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(ZZI)V")
	@Override
	public void setWarp(@OriginalArg(0) boolean repeatS, @OriginalArg(1) boolean repeatT) {
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(I[IIIIBI)V")
	@Override
	public void readPixels(@OriginalArg(1) int[] dest, @OriginalArg(2) int length, @OriginalArg(4) int height) {
		@Pc(18) int[] pixels = new int[this.height * this.width];
		super.activeToolkit.setTextureSampler(this);
		OpenGL.glGetTexImagei(super.target, 0, OpenGLConstants.GL_BGRA, OpenGLConstants.GL_UNSIGNED_BYTE, pixels, 0);
		for (@Pc(33) int i = 0; i < height; i++) {
			Arrays.copy(pixels, this.width * (height - i - 1), dest, i * length, length);
		}
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(FI)F")
	@Override
	public float toTextureCoordS(@OriginalArg(0) float x) {
		return x;
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(BF)F")
	@Override
	public float toTextureCoordT(@OriginalArg(1) float y) {
		return y;
	}

	@OriginalMember(owner = "client!tw", name = "b", descriptor = "(I)Z")
	@Override
	public boolean supportsTextureRepeat() {
		return false;
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(Z)I")
	@Override
	public int getTextureWidth() {
		return this.width;
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(II[IIIIII)V")
	@Override
	public void updateTextureRegion(@OriginalArg(0) int rowLength, @OriginalArg(2) int[] pixels, @OriginalArg(3) int x, @OriginalArg(5) int width, @OriginalArg(6) int y, @OriginalArg(7) int height) {
		super.activeToolkit.setTextureSampler(this);
		if (rowLength == 0) {
			rowLength = width;
		}
		if (width != rowLength) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, rowLength);
		}
		OpenGL.glTexSubImage2Di(super.target, 0, x, y, width, height, OpenGLConstants.GL_BGRA, super.activeToolkit.pixelType, pixels, 0);
		if (width != rowLength) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, 0);
		}
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(IIBIIII[BLclient!wq;)V")
	@Override
	public void upload(@OriginalArg(3) int rowLength, @OriginalArg(4) int height, @OriginalArg(5) int width, @OriginalArg(7) byte[] data, @OriginalArg(8) PixelFormat pixelFormat) {
		super.activeToolkit.setTextureSampler(this);
		if (rowLength == 0) {
			rowLength = width;
		}
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, 1);
		if (width != rowLength) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, rowLength);
		}
		OpenGL.glTexSubImage2Dub(super.target, 0, 0, 0, width, height, MipmapTextureSampler.getGlFormat(pixelFormat), OpenGLConstants.GL_UNSIGNED_BYTE, data, 0);
		if (rowLength != width) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, 0);
		}
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, 4);
	}
}
