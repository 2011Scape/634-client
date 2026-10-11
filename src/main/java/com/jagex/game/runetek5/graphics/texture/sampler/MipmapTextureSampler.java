package com.jagex.game.runetek5.graphics.texture.sampler;

import com.jagex.game.runetek5.DataType;
import com.jagex.game.runetek5.FilterMode;
import com.jagex.game.runetek5.PixelFormat;
import com.jagex.game.runetek5.toolkit.GlxToolkit;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.toolkit.opengl.GLActionFormat;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;

@OriginalClass("client!hda")
public abstract class MipmapTextureSampler implements TextureSampler {

	@OriginalMember(owner = "client!hda", name = "t", descriptor = "Lclient!ria;")
	private FilterMode filter = SampleMode.LINEAR;

	@OriginalMember(owner = "client!hda", name = "i", descriptor = "I")
	private final int texelCount;

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "Lclient!bga;")
	public final GlxToolkit activeToolkit;

	@OriginalMember(owner = "client!hda", name = "c", descriptor = "Lclient!wq;")
	public final PixelFormat pixelFormat;

	@OriginalMember(owner = "client!hda", name = "g", descriptor = "Lclient!baa;")
	public final DataType pixelActionFormat;

	@OriginalMember(owner = "client!hda", name = "r", descriptor = "Z")
	private final boolean mipmapping;

	@OriginalMember(owner = "client!hda", name = "l", descriptor = "I")
	public final int target;

	@OriginalMember(owner = "client!hda", name = "o", descriptor = "I")
	private int texture;

	@OriginalMember(owner = "client!hda", name = "<init>", descriptor = "(Lclient!bga;ILclient!wq;Lclient!baa;IZ)V")
	public MipmapTextureSampler(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) int textureTarget, @OriginalArg(2) PixelFormat pixelFormat, @OriginalArg(3) DataType pixelActionFormat, @OriginalArg(4) int pixelCount, @OriginalArg(5) boolean mipmapping) {
		this.texelCount = pixelCount;
		this.activeToolkit = glxToolkit;
		this.pixelFormat = pixelFormat;
		this.pixelActionFormat = pixelActionFormat;
		this.mipmapping = mipmapping;
		this.target = textureTarget;
		OpenGL.glGenTextures(1, MipmapTextureSampler.TEXTURE_ID_BUFFER, 0);
		this.texture = MipmapTextureSampler.TEXTURE_ID_BUFFER[0];
		this.applyFilter();
		this.trackAllocation();
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(B)V")
	@Override
	public final void bindTexture() {
		@Pc(8) int currentTextureSlot = this.activeToolkit.getCurrentTextureSlot();
		@Pc(20) int boundTarget = this.activeToolkit.textureSlots[currentTextureSlot];
		if (boundTarget != this.target) {
			if (boundTarget != 0) {
				OpenGL.glBindTexture(boundTarget, 0);
				OpenGL.glDisable(boundTarget);
			}
			OpenGL.glEnable(this.target);
			this.activeToolkit.textureSlots[currentTextureSlot] = this.target;
		}
		OpenGL.glBindTexture(this.target, this.texture);
	}

	@OriginalMember(owner = "client!hda", name = "c", descriptor = "(I)I")
	public final int getInternalFormat() {
		if (GLFormat.RGBA == this.pixelActionFormat) {
			if (this.pixelFormat == PixelFormat.aClass375_38) {
				return 6407;
			}
			if (this.pixelFormat == GLFormat.HDR) {
				return OpenGLConstants.GL_RGBA;
			}
			if (this.pixelFormat == GLActionFormat.PIXEL_FORMAT) {
				return OpenGLConstants.GL_ALPHA;
			}
			if (PixelFormat.aClass375_44 == this.pixelFormat) {
				return 6409;
			}
			if (this.pixelFormat == PixelFormat.aClass375_39) {
				return 6410;
			}
			if (this.pixelFormat == PixelFormat.aClass375_49) {
				return 6145;
			}
		} else if (DataType.aClass26_7 == this.pixelActionFormat) {
			if (this.pixelFormat == PixelFormat.aClass375_38) {
				return 34843;
			}
			if (GLFormat.HDR == this.pixelFormat) {
				return OpenGLConstants.GL_RGBA16F;
			}
			if (this.pixelFormat == GLActionFormat.PIXEL_FORMAT) {
				return 34844;
			}
			if (this.pixelFormat == PixelFormat.aClass375_44) {
				return 34846;
			}
			if (PixelFormat.aClass375_39 == this.pixelFormat) {
				return 34847;
			}
			if (this.pixelFormat == PixelFormat.aClass375_49) {
				return 6145;
			}
		} else if (this.pixelActionFormat == DataType.aClass26_8) {
			if (PixelFormat.aClass375_38 == this.pixelFormat) {
				return 34837;
			}
			if (this.pixelFormat == GLFormat.HDR) {
				return 34836;
			}
			if (this.pixelFormat == GLActionFormat.PIXEL_FORMAT) {
				return 34838;
			}
			if (PixelFormat.aClass375_44 == this.pixelFormat) {
				return 34840;
			}
			if (PixelFormat.aClass375_39 == this.pixelFormat) {
				return 34841;
			}
			if (PixelFormat.aClass375_49 == this.pixelFormat) {
				return 6145;
			}
		}
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!hda", name = "b", descriptor = "(Z)V")
	private void applyFilter() {
		this.activeToolkit.setTextureSampler(this);
		if (this.filter == SampleMode.LINEAR) {
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MIN_FILTER, this.mipmapping ? OpenGLConstants.GL_LINEAR_MIPMAP_LINEAR : OpenGLConstants.GL_LINEAR);
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MAG_FILTER, OpenGLConstants.GL_LINEAR);
		} else {
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MIN_FILTER, this.mipmapping ? OpenGLConstants.GL_NEAREST_MIPMAP_NEAREST : OpenGLConstants.GL_NEAREST);
			OpenGL.glTexParameteri(this.target, OpenGLConstants.GL_TEXTURE_MAG_FILTER, OpenGLConstants.GL_NEAREST);
		}
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(II[BII)V")
	public final void generateMipmaps(@OriginalArg(1) int width, @OriginalArg(2) byte[] pixels, @OriginalArg(3) int height, @OriginalArg(4) int target) {
		if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
			throw new IllegalArgumentException("");
		} else if (height <= 0 || MipmapTextureSampler.isPowerOfTwo(height)) {
			@Pc(36) int bytesPerPixel = this.pixelFormat.componentCount;
			@Pc(38) int level = 0;
			@Pc(45) int minDimension = width < height ? width : height;
			@Pc(49) int halfWidth = width >> 1;
			@Pc(60) int halfHeight = height >> 1;
			@Pc(62) byte[] source = pixels;
			@Pc(69) byte[] dest = new byte[halfHeight * halfWidth * bytesPerPixel];
			while (true) {
				OpenGL.glTexImage2Dub(target, level, this.getInternalFormat(), width, height, 0, MipmapTextureSampler.getGlFormat(this.pixelFormat), 5121, source, 0);
				if (minDimension <= 1) {
					return;
				}
				@Pc(94) int rowSize = width * bytesPerPixel;
				for (@Pc(96) int i = 0; i < bytesPerPixel; i++) {
					@Pc(100) int destIndex = i;
					@Pc(102) int srcTopIndex = i;
					@Pc(107) int srcBottomIndex = i + rowSize;
					for (@Pc(109) int j = 0; j < halfHeight; j++) {
						for (@Pc(113) int k = 0; k < halfWidth; k++) {
							@Pc(119) byte sample = source[srcTopIndex];
							srcTopIndex += bytesPerPixel;
							@Pc(129) int topSum = sample + source[srcTopIndex];
							@Pc(135) int partialSum = topSum + source[srcBottomIndex];
							srcTopIndex += bytesPerPixel;
							srcBottomIndex += bytesPerPixel;
							@Pc(149) int sum = partialSum + source[srcBottomIndex];
							dest[destIndex] = (byte) (sum >> 2);
							srcBottomIndex += bytesPerPixel;
							destIndex += bytesPerPixel;
						}
						srcTopIndex += rowSize;
						srcBottomIndex += rowSize;
					}
				}
				@Pc(190) byte[] swap = dest;
				dest = source;
				width = halfWidth;
				height = halfHeight;
				source = swap;
				halfWidth >>= 0x1;
				halfHeight >>= 0x1;
				level++;
				minDimension >>= 0x1;
			}
		} else {
			throw new IllegalArgumentException("");
		}
	}

	@OriginalMember(owner = "client!hda", name = "d", descriptor = "(I)I")
	private int getSizeInBytes() {
		@Pc(14) int size = this.texelCount * this.pixelActionFormat.bytesPerEntry * this.pixelFormat.componentCount;
		return this.mipmapping ? size * 4 / 3 : size;
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(II[III)V")
	public final void generateMipmaps(@OriginalArg(0) int width, @OriginalArg(1) int height, @OriginalArg(2) int[] pixels, @OriginalArg(3) int target) {
		if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
			throw new IllegalArgumentException("");
		} else if (height > 0 && !MipmapTextureSampler.isPowerOfTwo(height)) {
			throw new IllegalArgumentException("");
		} else if (GLFormat.HDR == this.pixelFormat) {
			@Pc(46) int level = 0;
			@Pc(57) int minDimension = height <= width ? height : width;
			@Pc(61) int halfWidth = width >> 1;
			@Pc(65) int halfHeight = height >> 1;
			@Pc(67) int[] source = pixels;
			@Pc(72) int[] dest = new int[halfHeight * halfWidth];
			while (true) {
				OpenGL.glTexImage2Di(target, level, this.getInternalFormat(), width, height, 0, OpenGLConstants.GL_BGRA, this.activeToolkit.pixelType, source, 0);
				if (minDimension <= 1) {
					return;
				}
				@Pc(99) int destIndex = 0;
				@Pc(101) int srcIndex = 0;
				@Pc(105) int srcNextRowIndex = width;
				for (@Pc(107) int i = 0; i < halfHeight; i++) {
					for (@Pc(111) int j = 0; j < halfWidth; j++) {
						@Pc(118) int colourA = source[srcIndex++];
						@Pc(123) int colourB = source[srcIndex++];
						@Pc(128) int colourC = source[srcNextRowIndex++];
						@Pc(134) int red = colourA >> 16 & 0xFF;
						@Pc(140) int alpha = colourA >> 24 & 0xFF;
						@Pc(144) int blue = colourA & 0xFF;
						@Pc(150) int green = colourA >> 8 & 0xFF;
						@Pc(155) int colourD = source[srcNextRowIndex++];
						@Pc(161) int blueAB = blue + (colourB & 0xFF);
						@Pc(169) int redAB = red + (colourB >> 16 & 0xFF);
						@Pc(177) int alphaAB = alpha + (colourB >> 24 & 0xFF);
						@Pc(185) int greenAB = green + (colourB >> 8 & 0xFF);
						@Pc(193) int alphaABC = alphaAB + (colourC >> 24 & 0xFF);
						@Pc(199) int blueABC = blueAB + (colourC & 0xFF);
						@Pc(207) int greenABC = greenAB + (colourC >> 8 & 0xFF);
						@Pc(215) int redABC = redAB + (colourC >> 16 & 0xFF);
						@Pc(223) int alphaSum = alphaABC + (colourD >> 24 & 0xFF);
						@Pc(231) int redSum = redABC + (colourD >> 16 & 0xFF);
						@Pc(239) int greenSum = greenABC + (colourD >> 8 & 0xFF);
						@Pc(245) int blueSum = blueABC + (colourD & 0xFF);
						dest[destIndex++] = (greenSum & 0x3FC) << 6 | (alphaSum & 0x3FC) << 22 | (redSum & 0x3FC) << 14 | blueSum >> 2 & 0xFF;
					}
					srcIndex += width;
					srcNextRowIndex += width;
				}
				@Pc(294) int[] swap = dest;
				dest = source;
				height = halfHeight;
				source = swap;
				width = halfWidth;
				halfWidth >>= 0x1;
				level++;
				minDimension >>= 0x1;
				halfHeight >>= 0x1;
			}
		} else {
			throw new IllegalArgumentException("");
		}
	}

	@OriginalMember(owner = "client!hda", name = "e", descriptor = "(I)V")
	private void delete() {
		if (this.texture > 0) {
			this.activeToolkit.requestTextureDeletion(this.getSizeInBytes(), this.texture);
			this.texture = 0;
		}
	}

	@OriginalMember(owner = "client!hda", name = "b", descriptor = "(IB)V")
	private void trackAllocation() {
		this.activeToolkit.texturesSizeOccupied += this.getSizeInBytes();
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(Lclient!ria;I)V")
	@Override
	public final void setFilterMode(@OriginalArg(0) FilterMode filter) {
		if (filter != this.filter) {
			this.filter = filter;
			this.applyFilter();
		}
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(III[FI)V")
	public final void generateMipmaps(@OriginalArg(0) int target, @OriginalArg(2) int width, @OriginalArg(3) float[] pixels, @OriginalArg(4) int height) {
		if (width > 0 && !MipmapTextureSampler.isPowerOfTwo(width)) {
			throw new IllegalArgumentException("");
		} else if (height <= 0 || MipmapTextureSampler.isPowerOfTwo(height)) {
			@Pc(36) int componentCount = this.pixelFormat.componentCount;
			@Pc(38) int level = 0;
			@Pc(56) int minDimension = width < height ? width : height;
			@Pc(60) int halfWidth = width >> 1;
			@Pc(64) int halfHeight = height >> 1;
			@Pc(66) float[] source = pixels;
			@Pc(73) float[] dest = new float[halfWidth * halfHeight * componentCount];
			while (true) {
				OpenGL.glTexImage2Df(target, level, this.getInternalFormat(), width, height, 0, MipmapTextureSampler.getGlFormat(this.pixelFormat), OpenGLConstants.GL_FLOAT, source, 0);
				if (minDimension <= 1) {
					return;
				}
				@Pc(97) int rowSize = width * componentCount;
				for (@Pc(99) int i = 0; i < componentCount; i++) {
					@Pc(103) int destIndex = i;
					@Pc(105) int srcTopIndex = i;
					@Pc(109) int srcBottomIndex = i + rowSize;
					for (@Pc(111) int j = 0; j < halfHeight; j++) {
						for (@Pc(115) int k = 0; k < halfWidth; k++) {
							@Pc(121) float sample = source[srcTopIndex];
							srcTopIndex += componentCount;
							@Pc(131) float topSum = sample + source[srcTopIndex];
							@Pc(137) float partialSum = topSum + source[srcBottomIndex];
							srcTopIndex += componentCount;
							srcBottomIndex += componentCount;
							@Pc(151) float sum = partialSum + source[srcBottomIndex];
							srcBottomIndex += componentCount;
							dest[destIndex] = sum * 0.25F;
							destIndex += componentCount;
						}
						srcBottomIndex += rowSize;
						srcTopIndex += rowSize;
					}
				}
				@Pc(187) float[] swap = dest;
				dest = source;
				width = halfWidth;
				height = halfHeight;
				source = swap;
				halfWidth >>= 0x1;
				halfHeight >>= 0x1;
				minDimension >>= 0x1;
				level++;
			}
		} else {
			throw new IllegalArgumentException("");
		}
	}

	@OriginalMember(owner = "client!hda", name = "finalize", descriptor = "()V")
	@Override
	public final void finalize() throws Throwable {
		this.delete();
		super.finalize();
	}
	@OriginalMember(owner = "client!hda", name = "j", descriptor = "[I")
	public static int[] textures;
	@OriginalMember(owner = "client!qq", name = "a", descriptor = "(II)Z")
	public static boolean isPowerOfTwo(@OriginalArg(1) int value) {
		return (value & -value) == value;
	}

	@OriginalMember(owner = "client!oda", name = "m", descriptor = "[I")
	public static final int[] TEXTURE_ID_BUFFER = new int[1];
	@OriginalMember(owner = "client!hga", name = "a", descriptor = "(ILclient!wq;)I")
	public static int getGlFormat(@OriginalArg(1) PixelFormat pixelFormat) {
		if (pixelFormat == PixelFormat.aClass375_38) {
			return 6407;
		} else if (GLFormat.HDR == pixelFormat) {
			return 6408;
		} else if (pixelFormat == GLActionFormat.PIXEL_FORMAT) {
			return 6406;
		} else if (PixelFormat.aClass375_44 == pixelFormat) {
			return 6409;
		} else if (pixelFormat == PixelFormat.aClass375_39) {
			return 6410;
		} else if (pixelFormat == PixelFormat.aClass375_49) {
			return 6145;
		} else {
			throw new IllegalStateException();
		}
	}
}
