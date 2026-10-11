package com.jagex.game.runetek5.graphics.gl;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler;

@OriginalClass("client!tp")
public class OpenGl2DTexture extends GlTexture {

	@OriginalMember(owner = "client!tp", name = "B", descriptor = "I")
	private int framebufferTarget = -1;

	@OriginalMember(owner = "client!tp", name = "x", descriptor = "I")
	private int framebufferAttachment = -1;

	@OriginalMember(owner = "client!tp", name = "z", descriptor = "I")
	public final int width;

	@OriginalMember(owner = "client!tp", name = "D", descriptor = "I")
	public final int length;

	@OriginalMember(owner = "client!tp", name = "<init>", descriptor = "(Lclient!qo;IIII)V")
	public OpenGl2DTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int target, @OriginalArg(2) int internalFormat, @OriginalArg(3) int width, @OriginalArg(4) int length) {
		super(glToolkit, target, internalFormat, width * length, false);
		this.width = width;
		this.length = length;
		super.glToolkit.setActiveTexture(this);
		OpenGL.glTexImage2Dub(super.target, 0, super.internalFormat, width, length, 0, GlTexture.getBaseFormat(super.internalFormat), OpenGLConstants.GL_UNSIGNED_BYTE, null, 0);
		this.setLinearFilter(true);
	}

	@OriginalMember(owner = "client!tp", name = "<init>", descriptor = "(Lclient!qo;IIIIZ[FI)V")
	public OpenGl2DTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int target, @OriginalArg(2) int internalFormat, @OriginalArg(3) int width, @OriginalArg(4) int length, @OriginalArg(5) boolean mipmapped, @OriginalArg(6) float[] pixels, @OriginalArg(7) int format) {
		super(glToolkit, target, internalFormat, length * width, mipmapped);
		this.width = width;
		this.length = length;
		super.glToolkit.setActiveTexture(this);
		if (mipmapped && super.target != 34037) {
			OpenGl2DTexture.uploadMipmaps(width, format, target, pixels, length, internalFormat);
			this.setMipmapped(true);
		} else {
			OpenGL.glTexImage2Df(super.target, 0, super.internalFormat, this.width, this.length, 0, format, OpenGLConstants.GL_FLOAT, pixels, 0);
			this.setMipmapped(false);
		}
		this.setLinearFilter(true);
	}

	@OriginalMember(owner = "client!tp", name = "<init>", descriptor = "(Lclient!qo;IIIIZ[IIIZ)V")
	public OpenGl2DTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int target, @OriginalArg(2) int internalFormat, @OriginalArg(3) int width, @OriginalArg(4) int length, @OriginalArg(5) boolean mipmapped, @OriginalArg(6) int[] pixels, @OriginalArg(7) int rowLength, @OriginalArg(8) int offset, @OriginalArg(9) boolean flipVertical) {
		super(glToolkit, target, internalFormat, length * width, mipmapped);
		this.width = width;
		this.length = length;
		if (flipVertical) {
			@Pc(28) int[] flipped = new int[pixels.length];
			for (@Pc(30) int i = 0; i < length; i++) {
				@Pc(36) int dst = i * width;
				@Pc(45) int src = width * (length - i - 1);
				for (@Pc(47) int j = 0; j < width; j++) {
					flipped[dst++] = pixels[src++];
				}
			}
			pixels = flipped;
		}
		super.glToolkit.setActiveTexture(this);
		if (super.target != 34037 && mipmapped && rowLength == 0) {
			GlTexture.uploadMipmapped(super.target, super.internalFormat, this.length, super.glToolkit.pixelType, this.width, pixels);
			this.setMipmapped(true);
		} else {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, rowLength);
			OpenGL.glTexImage2Di(super.target, 0, super.internalFormat, this.width, this.length, 0, OpenGLConstants.GL_BGRA, super.glToolkit.pixelType, pixels, offset * 4);
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, OpenGLConstants.GL_POINTS);
			this.setMipmapped(false);
		}
		this.setLinearFilter(true);
	}

	@OriginalMember(owner = "client!tp", name = "<init>", descriptor = "(Lclient!qo;IIIIZ[BIZ)V")
	public OpenGl2DTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int target, @OriginalArg(2) int internalFormat, @OriginalArg(3) int width, @OriginalArg(4) int length, @OriginalArg(5) boolean mipmapped, @OriginalArg(6) byte[] pixels, @OriginalArg(7) int format, @OriginalArg(8) boolean flipVertical) {
		super(glToolkit, target, internalFormat, width * length, mipmapped);
		this.width = width;
		this.length = length;
		if (flipVertical) {
			@Pc(28) byte[] flipped = new byte[pixels.length];
			for (@Pc(30) int i = 0; i < length; i++) {
				@Pc(36) int dst = width * i;
				@Pc(45) int src = width * (length - i - 1);
				for (@Pc(47) int j = 0; j < width; j++) {
					flipped[dst++] = pixels[src++];
				}
			}
			pixels = flipped;
		}
		super.glToolkit.setActiveTexture(this);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_LINES);
		if (mipmapped && super.target != 34037) {
			OpenGl2DTexture.uploadMipmaps(length, width, pixels, format, target, internalFormat);
			this.setMipmapped(true);
		} else {
			OpenGL.glTexImage2Dub(super.target, 0, super.internalFormat, this.width, this.length, 0, format, OpenGLConstants.GL_UNSIGNED_BYTE, pixels, 0);
			this.setMipmapped(false);
		}
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_TRIANGLES);
		this.setLinearFilter(true);
	}

	@OriginalMember(owner = "client!tp", name = "<init>", descriptor = "(Lclient!qo;IIIII)V")
	public OpenGl2DTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int target, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int width, @OriginalArg(5) int length) {
		super(glToolkit, target, 6407, length * width, false);
		this.width = width;
		this.length = length;
		@Pc(30) int flippedY = super.glToolkit.viewportHeight - y - length;
		super.glToolkit.setActiveTexture(this);
		super.glToolkit.beginReadFrame(x, flippedY, width, length); // Not part of the original client: see GlToolkit.beginReadFrame.
		OpenGL.glCopyTexImage2D(super.target, 0, super.internalFormat, x, flippedY, width, length, 0);
		super.glToolkit.endReadFrame();
		this.setLinearFilter(true);
	}

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(IIII)V")
	public final void attachToFramebuffer(@OriginalArg(0) int framebufferTarget, @OriginalArg(3) int attachment) {
		OpenGL.glFramebufferTexture2DEXT(framebufferTarget, attachment, super.target, super.id, 0);
		this.framebufferTarget = framebufferTarget;
		this.framebufferAttachment = attachment;
	}

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(ZII[IIBIII)V")
	public final void updateTextureRegion(@OriginalArg(1) int height, @OriginalArg(3) int[] pixels, @OriginalArg(6) int width) {
		@Pc(16) int[] flipped = new int[width * height];
		for (@Pc(18) int i = 0; i < height; i++) {
			@Pc(24) int dst = width * i;
			@Pc(34) int src = (height - i - 1) * width;
			for (@Pc(36) int j = 0; j < width; j++) {
				flipped[dst++] = pixels[src++];
			}
		}
		super.glToolkit.setActiveTexture(this);
		if (width != width) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, width);
		}
		OpenGL.glTexSubImage2Di(super.target, OpenGLConstants.GL_POINTS, 0, 0, width, height, OpenGLConstants.GL_BGRA, super.glToolkit.pixelType, flipped, 0);
		if (width != width) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, OpenGLConstants.GL_POINTS);
		}
	}

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(ZZI)V")
	public final void setRepeat(@OriginalArg(0) boolean repeatT, @OriginalArg(1) boolean repeatS) {
		if (super.target == 3553) {
			super.glToolkit.setActiveTexture(this);
			OpenGL.glTexParameteri(super.target, OpenGLConstants.GL_TEXTURE_WRAP_S, repeatS ? OpenGLConstants.GL_REPEAT : 33071);
			OpenGL.glTexParameteri(super.target, OpenGLConstants.GL_TEXTURE_WRAP_T, repeatT ? OpenGLConstants.GL_REPEAT : 33071);
		}
	}

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(IIIIIBI)V")
	public final void copyTextureSubImage(@OriginalArg(0) int offsetX, @OriginalArg(1) int height, @OriginalArg(2) int y, @OriginalArg(3) int offsetY, @OriginalArg(4) int x, @OriginalArg(6) int width) {
		@Pc(12) int flippedY = super.glToolkit.viewportHeight - y - height;
		super.glToolkit.setActiveTexture(this);
		super.glToolkit.beginReadFrame(x, flippedY, width, height); // Not part of the original client: see GlToolkit.beginReadFrame.
		OpenGL.glCopyTexSubImage2D(super.target, OpenGLConstants.GL_POINTS, offsetX, this.length - height - offsetY, x, flippedY, width, height);
		super.glToolkit.endReadFrame();
		OpenGL.glFlush();
	}

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(I)V")
	@Override
	public final void detach() {
		OpenGL.glFramebufferTexture2DEXT(this.framebufferTarget, this.framebufferAttachment, super.target, 0, 0);
		this.framebufferAttachment = -1;
		this.framebufferTarget = -1;
	}

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "(IIIZIIII[BI)V")
	public final void uploadTextureData(@OriginalArg(2) int width, @OriginalArg(3) boolean flipVertical, @OriginalArg(5) int height, @OriginalArg(6) int format, @OriginalArg(8) byte[] data) {
		if (flipVertical) {
			@Pc(15) int bytesPerPixel = OpenGl2DTexture.getBytesPerPixel(6406);
			@Pc(19) int rowSize = bytesPerPixel * width;
			@Pc(23) int srcRowSize = width * bytesPerPixel;
			@Pc(28) byte[] flipped = new byte[height * rowSize];
			for (@Pc(30) int i = 0; i < height; i++) {
				@Pc(36) int dst = rowSize * i;
				@Pc(46) int src = (height - i - 1) * srcRowSize;
				for (@Pc(48) int j = 0; j < rowSize; j++) {
					flipped[dst++] = data[src++];
				}
			}
			data = flipped;
		}
		super.glToolkit.setActiveTexture(this);
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_LINES);
		if (width != width) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, width);
		}
		OpenGL.glTexSubImage2Dub(super.target, OpenGLConstants.GL_POINTS, OpenGLConstants.GL_POINTS, OpenGLConstants.GL_POINTS, width, height, OpenGLConstants.GL_ALPHA, OpenGLConstants.GL_UNSIGNED_BYTE, data, 0);
		if (width != width) {
			OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ROW_LENGTH, OpenGLConstants.GL_POINTS);
		}
		OpenGL.glPixelStorei(OpenGLConstants.GL_UNPACK_ALIGNMENT, OpenGLConstants.GL_TRIANGLES);
	}
	@OriginalMember(owner = "client!la", name = "a", descriptor = "(II[BZIII)V")
	public static void uploadMipmaps(@OriginalArg(0) int height, @OriginalArg(1) int width, @OriginalArg(2) byte[] data, @OriginalArg(4) int format, @OriginalArg(5) int target, @OriginalArg(6) int internalFormat) {
		if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
			throw new IllegalArgumentException("");
		} else if (height <= 0 || MipmapTextureSampler.isPowerOfTwo(height)) {
			@Pc(34) int bytesPerPixel = OpenGl2DTexture.getBytesPerPixel(format);
			@Pc(36) int mipmapLevel = 0;
			@Pc(43) int minDimension = width < height ? width : height;
			@Pc(52) int halfWidth = width >> 1;
			@Pc(56) int halfHeight = height >> 1;
			@Pc(58) byte[] sourceData = data;
			@Pc(65) byte[] destData = new byte[halfWidth * halfHeight * bytesPerPixel];
			while (true) {
				OpenGL.glTexImage2Dub(target, mipmapLevel, internalFormat, width, height, 0, format, 5121, sourceData, 0);
				if (minDimension <= 1) {
					return;
				}
				@Pc(84) int rowBytes = width * bytesPerPixel;
				@Pc(86) byte[] swapTemp = destData;
				for (@Pc(88) int i = 0; i < bytesPerPixel; i++) {
					@Pc(92) int destIndex = i;
					@Pc(94) int srcTopIndex = i;
					@Pc(98) int srcBottomIndex = rowBytes + i;
					for (@Pc(100) int j = 0; j < halfHeight; j++) {
						for (@Pc(104) int k = 0; k < halfWidth; k++) {
							@Pc(110) byte topLeftSample = sourceData[srcTopIndex];
							srcTopIndex += bytesPerPixel;
							@Pc(120) int topSum = topLeftSample + sourceData[srcTopIndex];
							@Pc(126) int partialSum = topSum + sourceData[srcBottomIndex];
							srcTopIndex += bytesPerPixel;
							srcBottomIndex += bytesPerPixel;
							@Pc(140) int sampleSum = partialSum + sourceData[srcBottomIndex];
							destData[destIndex] = (byte) (sampleSum >> 2);
							srcBottomIndex += bytesPerPixel;
							destIndex += bytesPerPixel;
						}
						srcBottomIndex += rowBytes;
						srcTopIndex += rowBytes;
					}
				}
				destData = sourceData;
				sourceData = swapTemp;
				height = halfHeight;
				width = halfWidth;
				halfHeight >>= 0x1;
				mipmapLevel++;
				minDimension >>= 0x1;
				halfWidth >>= 0x1;
			}
		} else {
			throw new IllegalArgumentException("");
		}
	}
	@OriginalMember(owner = "client!qe", name = "a", descriptor = "(II)I")
	public static int getBytesPerPixel(@OriginalArg(0) int format) {
		if (format == 6406) {
			return 1;
		} else if (format == 6409) {
			return 1;
		} else if (format == 32841) {
			return 1;
		} else if (format == 6410) {
			return 2;
		} else if (format == 6407) {
			return 3;
		} else if (format == 6408) {
			return 4;
		} else {
			throw new IllegalArgumentException("");
		}
	}

	@OriginalMember(owner = "client!rga", name = "a", descriptor = "(III[FIII)V")
	public static void uploadMipmaps(@OriginalArg(0) int width, @OriginalArg(1) int format, @OriginalArg(2) int target, @OriginalArg(3) float[] pixels, @OriginalArg(5) int height, @OriginalArg(6) int internalFormat) {
		if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
			throw new IllegalArgumentException("");
		} else if (height <= 0 || MipmapTextureSampler.isPowerOfTwo(height)) {
			@Pc(34) int componentCount = OpenGl2DTexture.getBytesPerPixel(6408);
			@Pc(36) int level = 0;
			@Pc(47) int minDimension = width < height ? width : height;
			@Pc(51) int nextWidth = width >> 1;
			@Pc(55) int nextHeight = height >> 1;
			@Pc(57) float[] levelPixels = pixels;
			@Pc(64) float[] scratch = new float[componentCount * nextWidth * nextHeight];
			while (true) {
				OpenGL.glTexImage2Df(OpenGLConstants.GL_TEXTURE_2D, level, OpenGLConstants.GL_RGBA16F, width, height, 0, OpenGLConstants.GL_RGBA, OpenGLConstants.GL_FLOAT, levelPixels, 0);
				if (minDimension <= 1) {
					return;
				}
				@Pc(85) int rowStride = width * componentCount;
				@Pc(87) float[] downsampled = scratch;
				for (@Pc(89) int i = 0; i < componentCount; i++) {
					@Pc(93) int dstIndex = i;
					@Pc(95) int srcIndex0 = i;
					@Pc(99) int srcIndex1 = rowStride + i;
					for (@Pc(101) int j = 0; j < nextHeight; j++) {
						for (@Pc(105) int k = 0; k < nextWidth; k++) {
							@Pc(111) float topLeft = levelPixels[srcIndex0];
							srcIndex0 += componentCount;
							@Pc(121) float topSum = topLeft + levelPixels[srcIndex0];
							srcIndex0 += componentCount;
							@Pc(131) float partialSum = topSum + levelPixels[srcIndex1];
							srcIndex1 += componentCount;
							@Pc(141) float sum = partialSum + levelPixels[srcIndex1];
							srcIndex1 += componentCount;
							scratch[dstIndex] = sum * 0.25F;
							dstIndex += componentCount;
						}
						srcIndex0 += rowStride;
						srcIndex1 += rowStride;
					}
				}
				scratch = levelPixels;
				levelPixels = downsampled;
				width = nextWidth;
				height = nextHeight;
				level++;
				minDimension >>= 0x1;
				nextWidth >>= 0x1;
				nextHeight >>= 0x1;
			}
		} else {
			throw new IllegalArgumentException("");
		}
	}
}
