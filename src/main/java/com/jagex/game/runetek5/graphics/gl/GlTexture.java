package com.jagex.game.runetek5.graphics.gl;

import com.jagex.game.runetek5.toolkit.companion.Binding;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler;

@OriginalClass("client!ud")
public abstract class GlTexture implements Binding {

	@OriginalMember(owner = "client!ud", name = "j", descriptor = "Z")
	private boolean linearFilter = false;

	@OriginalMember(owner = "client!ud", name = "n", descriptor = "Z")
	private boolean mipmapped;

	@OriginalMember(owner = "client!ud", name = "c", descriptor = "I")
	public final int target;

	@OriginalMember(owner = "client!ud", name = "a", descriptor = "I")
	private final int pixelCount;

	@OriginalMember(owner = "client!ud", name = "l", descriptor = "I")
	public final int internalFormat;

	@OriginalMember(owner = "client!ud", name = "e", descriptor = "Lclient!qo;")
	public final GlToolkit glToolkit;

	@OriginalMember(owner = "client!ud", name = "m", descriptor = "I")
	public int id;

	@OriginalMember(owner = "client!ud", name = "<init>", descriptor = "(Lclient!qo;IIIZ)V")
	public GlTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int target, @OriginalArg(2) int internalFormat, @OriginalArg(3) int pixelCount, @OriginalArg(4) boolean mipmapped) {
		this.mipmapped = mipmapped;
		this.target = target;
		this.pixelCount = pixelCount;
		this.internalFormat = internalFormat;
		this.glToolkit = glToolkit;
		OpenGL.glGenTextures(1, GlTexture.TEXTURE_ID_BUFFER, 0);
		this.id = GlTexture.TEXTURE_ID_BUFFER[0];
		this.updateOccupiedSize(0);
	}

	@OriginalMember(owner = "client!ud", name = "b", descriptor = "(I)Z")
	public final boolean generateMipmaps() {
		if (!this.glToolkit.extFramebufferObject) {
			return false;
		}
		@Pc(19) int previousSize = this.getSizeInBytes();
		this.glToolkit.setActiveTexture(this);
		OpenGL.glGenerateMipmapEXT(this.target);
		this.mipmapped = true;
		this.applyFilter();
		this.updateOccupiedSize(previousSize);
		return true;
	}

	@OriginalMember(owner = "client!ud", name = "c", descriptor = "(I)I")
	private int getSizeInBytes() {
		@Pc(19) int size = this.glToolkit.getBytesPerPixel(this.internalFormat) * this.pixelCount;
		return this.mipmapped ? size * 4 / 3 : size;
	}

	@OriginalMember(owner = "client!ud", name = "d", descriptor = "(I)V")
	public final void delete() {
		if (this.id > 0) {
			this.glToolkit.requestTextureCleanup(this.id, this.getSizeInBytes());
			this.id = 0;
		}
	}

	@OriginalMember(owner = "client!ud", name = "e", descriptor = "(I)I")
	public final int getId() {
		return this.id;
	}

	@OriginalMember(owner = "client!ud", name = "a", descriptor = "(II)V")
	private void updateOccupiedSize(@OriginalArg(0) int previousSize) {
		this.glToolkit.textureBuffersSize -= previousSize;
		this.glToolkit.textureBuffersSize += this.getSizeInBytes();
	}

	@OriginalMember(owner = "client!ud", name = "a", descriptor = "(ZB)V")
	public final void setMipmapped(@OriginalArg(0) boolean mipmapped) {
		if (this.mipmapped != mipmapped) {
			@Pc(18) int previousSize = this.getSizeInBytes();
			this.mipmapped = true;
			this.applyFilter();
			this.updateOccupiedSize(previousSize);
		}
	}

	@OriginalMember(owner = "client!ud", name = "a", descriptor = "(Z)V")
	private void applyFilter() {
		this.glToolkit.setActiveTexture(this);
		if (this.linearFilter) {
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MIN_FILTER, this.mipmapped ? OpenGLConstants.GL_LINEAR_MIPMAP_LINEAR : OpenGLConstants.GL_LINEAR);
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MAG_FILTER, OpenGLConstants.GL_LINEAR);
		} else {
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MIN_FILTER, this.mipmapped ? OpenGLConstants.GL_NEAREST_MIPMAP_NEAREST : OpenGLConstants.GL_NEAREST);
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MAG_FILTER, OpenGLConstants.GL_NEAREST);
		}
	}

	@OriginalMember(owner = "client!ud", name = "a", descriptor = "(IZ)V")
	public final void setLinearFilter(@OriginalArg(1) boolean linear) {
		if (this.linearFilter != linear) {
			this.linearFilter = linear;
			this.applyFilter();
		}
	}

	@OriginalMember(owner = "client!ud", name = "finalize", descriptor = "()V")
	@Override
	public final void finalize() throws Throwable {
		this.delete();
		super.finalize();
	}

	@OriginalMember(owner = "client!ud", name = "a", descriptor = "(I)V")
	public abstract void detach();
	@OriginalMember(owner = "client!en", name = "a", descriptor = "(IIIII[III)V")
	public static void uploadMipmapped(@OriginalArg(0) int target, @OriginalArg(1) int internalFormat, @OriginalArg(2) int height, @OriginalArg(3) int pixelType, @OriginalArg(4) int width, @OriginalArg(5) int[] pixels) {
		if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
			throw new IllegalArgumentException("");
		} else if (height <= 0 || MipmapTextureSampler.isPowerOfTwo(height)) {
			@Pc(37) int mipmapLevel = 0;
			@Pc(44) int minDimension = height > width ? width : height;
			@Pc(53) int halfWidth = width >> 1;
			@Pc(57) int halfHeight = height >> 1;
			@Pc(59) int[] argbPixels = pixels;
			@Pc(64) int[] replacementPixels = new int[halfHeight * halfWidth];
			while (true) {
				OpenGL.glTexImage2Di(target, mipmapLevel, internalFormat, width, height, 0, OpenGLConstants.GL_BGRA, pixelType, argbPixels, 0);
				if (minDimension <= 1) {
					return;
				}
				@Pc(80) int dstPixelIndex = 0;
				@Pc(82) int srcPixelIndex = 0;
				@Pc(86) int srcPixelIndexOffset = width;
				@Pc(88) int[] swapTemp = replacementPixels;
				for (@Pc(90) int i = 0; i < halfHeight; i++) {
					for (@Pc(94) int j = 0; j < halfWidth; j++) {
						@Pc(101) int colorArgbA = argbPixels[srcPixelIndex++];
						@Pc(106) int colorArgbB = argbPixels[srcPixelIndex++];
						@Pc(111) int colorArgbC = argbPixels[srcPixelIndexOffset++];
						@Pc(115) int blueA = colorArgbA & 0xFF;
						@Pc(121) int redA = colorArgbA >> 16 & 0xFF;
						@Pc(127) int alphaA = colorArgbA >> 24 & 0xFF;
						@Pc(133) int greenA = colorArgbA >> 8 & 0xFF;
						@Pc(138) int colorArgbD = argbPixels[srcPixelIndexOffset++];
						@Pc(146) int alphaAB = alphaA + (colorArgbB >> 24 & 0xFF);
						@Pc(154) int redAB = redA + (colorArgbB >> 16 & 0xFF);
						@Pc(160) int blueAB = blueA + (colorArgbB & 0xFF);
						@Pc(168) int greenAB = greenA + (colorArgbB >> 8 & 0xFF);
						@Pc(176) int alphaABC = alphaAB + (colorArgbC >> 24 & 0xFF);
						@Pc(184) int redABC = redAB + (colorArgbC >> 16 & 0xFF);
						@Pc(192) int greenABC = greenAB + (colorArgbC >> 8 & 0xFF);
						@Pc(198) int blueABC = blueAB + (colorArgbC & 0xFF);
						@Pc(204) int blueSum = blueABC + (colorArgbD & 0xFF);
						@Pc(212) int greenSum = greenABC + (colorArgbD >> 8 & 0xFF);
						@Pc(220) int alphaSum = alphaABC + (colorArgbD >> 24 & 0xFF);
						@Pc(228) int redSum = redABC + (colorArgbD >> 16 & 0xFF);
						replacementPixels[dstPixelIndex++] = (greenSum & 0x3FC) << 6 | (redSum & 0x3FC) << 14 | (alphaSum & 0x3FC) << 22 | blueSum >> 2 & 0xFF;
					}
					srcPixelIndex += width;
					srcPixelIndexOffset += width;
				}
				replacementPixels = argbPixels;
				height = halfHeight;
				width = halfWidth;
				argbPixels = swapTemp;
				mipmapLevel++;
				halfWidth >>= 0x1;
				minDimension >>= 0x1;
				halfHeight >>= 0x1;
			}
		} else {
			throw new IllegalArgumentException("");
		}
	}

	@OriginalMember(owner = "client!gn", name = "r", descriptor = "[I")
	public static final int[] TEXTURE_ID_BUFFER = new int[1];
	@OriginalMember(owner = "client!hf", name = "a", descriptor = "(ZI)I")
	public static int getBaseFormat(@OriginalArg(1) int internalFormat) {
		if (internalFormat == 6407 || internalFormat == 34843 || internalFormat == 34837) {
			return 6407;
		} else if (internalFormat == 6408 || internalFormat == 34842 || internalFormat == 34836) {
			return 6408;
		} else if (internalFormat == 6406 || internalFormat == 34844) {
			return 6406;
		} else if (internalFormat == 6409 || internalFormat == 34846) {
			return 6409;
		} else if (internalFormat == 6410 || internalFormat == 34847) {
			return 6410;
		} else if (internalFormat == 6402) {
			return 6402;
		} else {
			throw new IllegalArgumentException("");
		}
	}
}
