package com.jagex.game.runetek5.graphics.sprite;

import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.GpuTexture;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.NativeMatrix;
import com.jagex.game.runetek5.toolkit.MaskedTextureSurface;
import com.jagex.game.runetek5.graphics.texture.sampler.SampleMode;
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.TextureCombineFunction;
import com.jagex.game.runetek5.FilterMode;
import com.jagex.game.runetek5.TextureTransformMode;
import com.jagex.game.runetek5.PixelFormat;

@OriginalClass("client!jd")
public final class GpuSprite extends NativeSprite {

	@OriginalMember(owner = "client!jd", name = "h", descriptor = "I")
	private int trimBottom = 0;

	@OriginalMember(owner = "client!jd", name = "A", descriptor = "I")
	private int trimRight = 0;

	@OriginalMember(owner = "client!jd", name = "D", descriptor = "I")
	private int translateY = 0;

	@OriginalMember(owner = "client!jd", name = "C", descriptor = "Z")
	private boolean stretched = false;

	@OriginalMember(owner = "client!jd", name = "k", descriptor = "I")
	private int translateX = 0;

	@OriginalMember(owner = "client!jd", name = "B", descriptor = "I")
	private final int height;

	@OriginalMember(owner = "client!jd", name = "v", descriptor = "I")
	private final int width;

	@OriginalMember(owner = "client!jd", name = "r", descriptor = "Lclient!wga;")
	private final AbstractNativeToolkit nativeToolkit;

	@OriginalMember(owner = "client!jd", name = "n", descriptor = "Lclient!wfa;")
	private final GpuTexture texture;

	@OriginalMember(owner = "client!jd", name = "u", descriptor = "Z")
	private final boolean widthPadded;

	@OriginalMember(owner = "client!jd", name = "s", descriptor = "Z")
	private final boolean heightPadded;

	@OriginalMember(owner = "client!jd", name = "g", descriptor = "Z")
	private final boolean repeatX;

	@OriginalMember(owner = "client!jd", name = "b", descriptor = "Z")
	private final boolean repeatY;

	@OriginalMember(owner = "client!jd", name = "<init>", descriptor = "(Lclient!wga;IIZ)V")
	public GpuSprite(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) boolean hasAlpha) {
		this.height = height;
		this.width = width;
		this.nativeToolkit = nativeToolkit;
		this.texture = nativeToolkit.createNativeTexture2D(height, width, GLFormat.RGBA, hasAlpha ? GLFormat.HDR : PixelFormat.aClass375_38);
		this.texture.setWarp(true, true);
		this.widthPadded = this.texture.getTextureWidth() != width;
		this.heightPadded = this.texture.getTextureHeight() != height;
		this.repeatX = !this.widthPadded && this.texture.supportsTextureRepeat();
		this.repeatY = !this.heightPadded && this.texture.supportsTextureRepeat();
	}

	@OriginalMember(owner = "client!jd", name = "<init>", descriptor = "(Lclient!wga;II[III)V")
	public GpuSprite(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) int[] pixels, @OriginalArg(4) int pixelOffset, @OriginalArg(5) int rowLength) {
		this.nativeToolkit = nativeToolkit;
		this.width = width;
		this.height = height;
		this.texture = nativeToolkit.createTextureSampler(width, pixels, false, pixelOffset, rowLength, height);
		this.texture.setWarp(true, true);
		this.widthPadded = this.texture.getTextureWidth() != width;
		this.heightPadded = this.texture.getTextureHeight() != height;
		this.repeatX = !this.widthPadded && this.texture.supportsTextureRepeat();
		this.repeatY = !this.heightPadded && this.texture.supportsTextureRepeat();
	}

	@OriginalMember(owner = "client!jd", name = "d", descriptor = "()I")
	@Override
	public int scaleHeight() {
		return this.height + this.translateY + this.trimBottom;
	}

	@OriginalMember(owner = "client!jd", name = "c", descriptor = "(IIII)V")
	@Override
	public void setOffsets(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int x2, @OriginalArg(3) int y2) {
		this.translateY = y1;
		this.trimBottom = y2;
		this.translateX = x1;
		this.trimRight = x2;
		this.stretched = this.translateX != 0 || this.translateY != 0 || this.trimRight != 0 || this.trimBottom != 0;
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(IIII[III)V")
	private void updateTextureRegion(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int[] pixels, @OriginalArg(6) int rowLength) {
		this.texture.updateTextureRegion(rowLength, pixels, x, width, y, height);
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "()I")
	@Override
	public int scaleWidth() {
		return this.trimRight + this.translateX + this.width;
	}

	@OriginalMember(owner = "client!jd", name = "b", descriptor = "()I")
	@Override
	public int getHeight() {
		return this.height;
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(FFFFFFIIII)V")
	@Override
	public void method6201(@OriginalArg(0) float topLeftX, @OriginalArg(1) float topLeftY, @OriginalArg(2) float topRightX, @OriginalArg(3) float topRightY, @OriginalArg(4) float bottomLeftX, @OriginalArg(5) float bottomLeftY, @OriginalArg(6) int combineMode, @OriginalArg(7) int colorArgb) {
		@Pc(8) NativeMatrix modelMatrix = this.nativeToolkit.getModelMatrix();
		@Pc(13) NativeMatrix textureMatrix = this.nativeToolkit.getCurrentTextureMatrix();
		this.texture.setFilterMode(this.widthPadded || this.heightPadded ? FilterMode.aClass298_10 : SampleMode.LINEAR);
		this.nativeToolkit.setupUiRenderState();
		this.nativeToolkit.setTextureSampler(this.texture);
		this.nativeToolkit.toggleBlending(1);
		this.nativeToolkit.setColourType(combineMode);
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.CONSTANT_COLOR);
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.CONSTANT_COLOR);
		this.nativeToolkit.setColourValue(colorArgb);
		if (this.stretched) {
			@Pc(77) float spriteWidth = (float) this.scaleWidth();
			@Pc(81) float spriteHeight = (float) this.scaleHeight();
			@Pc(88) float uX = (topRightX - topLeftX) / spriteWidth;
			@Pc(95) float uY = (topRightY - topLeftY) / spriteWidth;
			@Pc(101) float vX = (bottomLeftX - topLeftX) / spriteHeight;
			@Pc(107) float vY = (bottomLeftY - topLeftY) / spriteHeight;
			@Pc(113) float padTopX = (float) this.translateY * vX;
			@Pc(119) float padTopY = (float) this.translateY * vY;
			@Pc(125) float padLeftX = uX * (float) this.translateX;
			@Pc(131) float padLeftY = uY * (float) this.translateX;
			@Pc(138) float trimRightX = -uX * (float) this.trimRight;
			@Pc(145) float trimRightY = -uY * (float) this.trimRight;
			@Pc(152) float trimBottomX = -vX * (float) this.trimBottom;
			@Pc(159) float trimBottomY = (float) this.trimBottom * -vY;
			topLeftX = padTopX + topLeftX + padLeftX;
			bottomLeftX = padLeftX + bottomLeftX + trimBottomX;
			topRightY = topRightY + trimRightY + padTopY;
			topRightX = padTopX + topRightX + trimRightX;
			topLeftY = padTopY + topLeftY + padLeftY;
			bottomLeftY = bottomLeftY + padLeftY + trimBottomY;
		}
		modelMatrix.setRotation(1.0F, topRightY - topLeftY, -topLeftX + bottomLeftX, 0.0F, 0.0F, bottomLeftY - topLeftY, 0.0F, 0.0F, topRightX - topLeftX);
		modelMatrix.translate(topLeftY, 0.0F, topLeftX);
		textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) this.height), this.texture.toTextureCoordS((float) this.width));
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
		this.nativeToolkit.commitModelMatrix();
		this.nativeToolkit.drawQuadTriangleFan();
		this.nativeToolkit.resetCurrentTextureMatrix();
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.PREVIOUS);
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.PREVIOUS);
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(IIIIIII)V")
	@Override
	public void drawScaledColored(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int op, @OriginalArg(5) int colour, @OriginalArg(6) int mode) {
		@Pc(10) NativeMatrix modelMatrix = this.nativeToolkit.getModelMatrix();
		@Pc(15) NativeMatrix textureMatrix = this.nativeToolkit.getCurrentTextureMatrix();
		this.texture.setFilterMode(FilterMode.aClass298_10);
		this.nativeToolkit.setupUiRenderState();
		this.nativeToolkit.setTextureSampler(this.texture);
		this.nativeToolkit.toggleBlending(mode);
		this.nativeToolkit.setColourType(op);
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.CONSTANT_COLOR);
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.CONSTANT_COLOR);
		this.nativeToolkit.setColourValue(colour);
		@Pc(73) boolean tileVertically = this.repeatY && this.translateY == 0 && this.trimBottom == 0;
		@Pc(92) boolean tileHorizontally = this.repeatX && this.translateX == 0 && this.trimRight == 0;
		if (tileHorizontally & tileVertically) {
			textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) height), this.texture.toTextureCoordS((float) width));
			modelMatrix.setScale(0.0F, (float) height, (float) width);
			modelMatrix.translate(x, y, 0);
			this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
			this.nativeToolkit.commitModelMatrix();
			this.nativeToolkit.drawQuadTriangleFan();
		} else {
			@Pc(102) int end;
			@Pc(105) int local105;
			@Pc(132) int local132;
			@Pc(138) int local138;
			@Pc(184) int local184;
			if (tileHorizontally) {
				end = height + y;
				int stepY = this.scaleHeight();
				textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) this.height), this.texture.toTextureCoordS((float) width));
				this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
				int tileY = this.translateY + y;
				int tileEndY = tileY + this.height;
				while (tileEndY <= end) {
					modelMatrix.setScale(0.0F, (float) this.height, (float) width);
					modelMatrix.translate(x, tileY, 0);
					this.nativeToolkit.commitModelMatrix();
					tileEndY += stepY;
					tileY += stepY;
					this.nativeToolkit.drawQuadTriangleFan();
				}
				if (tileY < end) {
					int remainingHeight = end - tileY;
					textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) remainingHeight), this.texture.toTextureCoordS((float) width));
					this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
					modelMatrix.setScale(0.0F, (float) remainingHeight, (float) width);
					modelMatrix.translate(x, tileY, 0);
					this.nativeToolkit.commitModelMatrix();
					this.nativeToolkit.drawQuadTriangleFan();
				}
			} else if (tileVertically) {
				end = x + width;
				int stepX = this.scaleWidth();
				textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) height), this.texture.toTextureCoordS((float) this.width));
				this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
				int tileX = x + this.translateX;
				for (int tileEndX = this.width + tileX; tileEndX <= end; tileEndX += stepX) {
					modelMatrix.setScale(0.0F, (float) height, (float) this.width);
					modelMatrix.translate(tileX, y, 0);
					this.nativeToolkit.commitModelMatrix();
					tileX += stepX;
					this.nativeToolkit.drawQuadTriangleFan();
				}
				if (tileX < end) {
					int remainingWidth = end - tileX;
					textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) height), this.texture.toTextureCoordS((float) remainingWidth));
					this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
					modelMatrix.setScale(0.0F, (float) height, (float) remainingWidth);
					modelMatrix.translate(tileX, y, 0);
					this.nativeToolkit.commitModelMatrix();
					this.nativeToolkit.drawQuadTriangleFan();
				}
			} else {
				end = height + y;
				int endX = x + width;
				int stepX = this.scaleWidth();
				int stepY = this.scaleHeight();
				int tileY = this.translateY + y;
				@Pc(254) int tileEndY = tileY + this.height;
				@Pc(284) int local284;
				@Pc(290) int local290;
				@Pc(338) int local338;
				while (end >= tileEndY) {
					textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) this.height), this.texture.toTextureCoordS((float) this.width));
					this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
					int tileX = this.translateX + x;
					for (int tileEndX = tileX + this.width; tileEndX <= endX; tileEndX += stepX) {
						modelMatrix.setScale(0.0F, (float) this.height, (float) this.width);
						modelMatrix.translate(tileX, tileY, 0);
						this.nativeToolkit.commitModelMatrix();
						tileX += stepX;
						this.nativeToolkit.drawQuadTriangleFan();
					}
					if (endX > tileX) {
						int remainingWidth = endX - tileX;
						textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) this.height), this.texture.toTextureCoordS((float) remainingWidth));
						this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
						modelMatrix.setScale(0.0F, (float) this.height, (float) remainingWidth);
						modelMatrix.translate(tileX, tileY, 0);
						this.nativeToolkit.commitModelMatrix();
						this.nativeToolkit.drawQuadTriangleFan();
					}
					tileEndY += stepY;
					tileY += stepY;
				}
				if (end > tileY) {
					int remainingHeight = end - tileY;
					textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) remainingHeight), this.texture.toTextureCoordS((float) this.width));
					this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
					int tileX = this.translateX + x;
					int tileEndX = this.width + tileX;
					while (endX >= tileEndX) {
						modelMatrix.setScale(0.0F, (float) remainingHeight, (float) this.width);
						modelMatrix.translate(tileX, tileY, 0);
						this.nativeToolkit.commitModelMatrix();
						tileX += stepX;
						tileEndX += stepX;
						this.nativeToolkit.drawQuadTriangleFan();
					}
					if (tileX < endX) {
						@Pc(483) int remainingWidth = endX - tileX;
						textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) remainingHeight), this.texture.toTextureCoordS((float) remainingWidth));
						this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
						modelMatrix.setScale(0.0F, (float) remainingHeight, (float) remainingWidth);
						modelMatrix.translate(tileX, tileY, 0);
						this.nativeToolkit.commitModelMatrix();
						this.nativeToolkit.drawQuadTriangleFan();
					}
				}
			}
		}
		this.nativeToolkit.resetCurrentTextureMatrix();
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.PREVIOUS);
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.PREVIOUS);
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(FFFFFFILclient!aa;II)V")
	@Override
	public void renderParallelogramImpl(@OriginalArg(0) float topLeftX, @OriginalArg(1) float topLeftY, @OriginalArg(2) float topRightX, @OriginalArg(3) float topRightY, @OriginalArg(4) float bottomLeftX, @OriginalArg(5) float bottomLeftY, @OriginalArg(7) ComponentSurface mask, @OriginalArg(8) int maskX, @OriginalArg(9) int maskY) {
		@Pc(8) NativeMatrix modelMatrix = this.nativeToolkit.getModelMatrix();
		@Pc(13) NativeMatrix textureMatrix = this.nativeToolkit.getCurrentTextureMatrix();
		@Pc(16) MaskedTextureSurface maskSurface = (MaskedTextureSurface) mask;
		@Pc(19) GpuTexture gpuTexture = maskSurface.nativeTextureSampler;
		this.texture.setFilterMode(this.widthPadded || this.heightPadded ? FilterMode.aClass298_10 : SampleMode.LINEAR);
		this.nativeToolkit.setupUiRenderState();
		this.nativeToolkit.setTextureSampler(this.texture);
		this.nativeToolkit.toggleBlending(1);
		this.nativeToolkit.setColourType(1);
		if (this.stretched) {
			@Pc(67) float scaleX = (float) this.width / (float) this.scaleWidth();
			@Pc(75) float scaleY = (float) this.height / (float) this.scaleHeight();
			modelMatrix.setRotation(1.0F, scaleX * (topRightY - topLeftY), scaleY * (bottomLeftX - topLeftX), 0.0F, 0.0F, scaleY * (bottomLeftY - topLeftY), 0.0F, 0.0F, (topRightX - topLeftX) * scaleX);
			modelMatrix.translate(((float) this.translateY + topLeftY) * scaleY, 0.0F, ((float) this.translateX + topLeftX) * scaleX);
		} else {
			modelMatrix.setRotation(1.0F, topRightY - topLeftY, bottomLeftX - topLeftX, 0.0F, 0.0F, bottomLeftY - topLeftY, 0.0F, 0.0F, topRightX - topLeftX);
			modelMatrix.translate(topLeftY, 0.0F, topLeftX);
		}
		textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) this.height), this.texture.toTextureCoordS((float) this.width));
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
		this.nativeToolkit.setTextureSlot(1);
		this.nativeToolkit.setTextureSampler(gpuTexture);
		this.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineFunction.aClass315_4);
		this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS);
		@Pc(203) NativeMatrix maskTextureMatrix = this.nativeToolkit.getCurrentTextureMatrix();
		maskTextureMatrix.apply(modelMatrix);
		maskTextureMatrix.translate(-maskX, -maskY, 0);
		maskTextureMatrix.scale(gpuTexture.toTextureCoordT(1.0F), 1.0F, gpuTexture.toTextureCoordS(1.0F));
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
		this.nativeToolkit.commitModelMatrix();
		this.nativeToolkit.drawQuadTriangleFan();
		this.nativeToolkit.resetCurrentTextureMatrix();
		this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
		this.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE);
		this.nativeToolkit.setTextureSampler(null);
		this.nativeToolkit.setTextureSlot(0);
		this.nativeToolkit.resetCurrentTextureMatrix();
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(III)V")
	@Override
	public void copyAlpha(@OriginalArg(2) int channel) {
		@Pc(15) int[] screenPixels = this.nativeToolkit.na(0, 0, this.width, this.height);
		@Pc(22) int[] pixels = new int[this.height * this.width];
		this.texture.readPixels(pixels, this.width, this.height);
		@Pc(41) int y;
		@Pc(48) int rowOffset;
		@Pc(50) int x;
		if (channel == 0) {
			for (y = 0; y < this.height; y++) {
				rowOffset = y * this.width;
				for (x = 0; x < this.width; x++) {
					pixels[rowOffset + x] = (screenPixels[rowOffset + x] & 0x6CFF0000) << 8 | pixels[rowOffset + x] & 0xFFFFFF;
				}
			}
		} else if (channel == 3) {
			for (y = 0; y < this.height; y++) {
				rowOffset = y * this.width;
				for (x = 0; x < this.width; x++) {
					pixels[x + rowOffset] = (screenPixels[x + rowOffset] == 0 ? 0 : -16777216) | pixels[x + rowOffset] & 0xFFFFFF;
				}
			}
		}
		this.updateTextureRegion(0, 0, this.width, this.height, pixels, this.width);
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(IIIIIIII)V")
	@Override
	public void renderImpl(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int combineMode, @OriginalArg(5) int colorArgb, @OriginalArg(6) int blendMode) {
		@Pc(8) NativeMatrix modelMatrix = this.nativeToolkit.getModelMatrix();
		@Pc(13) NativeMatrix textureMatrix = this.nativeToolkit.getCurrentTextureMatrix();
		this.texture.setFilterMode(this.widthPadded || this.heightPadded ? FilterMode.aClass298_10 : SampleMode.LINEAR);
		this.nativeToolkit.setupUiRenderState();
		this.nativeToolkit.setTextureSampler(this.texture);
		this.nativeToolkit.toggleBlending(blendMode);
		this.nativeToolkit.setColourType(combineMode);
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.CONSTANT_COLOR);
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.CONSTANT_COLOR);
		this.nativeToolkit.setColourValue(colorArgb);
		textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) this.height), this.texture.toTextureCoordS((float) this.width));
		if (this.stretched) {
			width = width * this.width / this.scaleWidth();
			height = this.height * height / this.scaleHeight();
			x += this.translateX * width / this.width;
			y += this.translateY * height / this.height;
		}
		modelMatrix.setScale(0.0F, (float) height, (float) width);
		modelMatrix.translate(x, y, 0);
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
		this.nativeToolkit.commitModelMatrix();
		this.nativeToolkit.drawQuadTriangleFan();
		this.nativeToolkit.resetCurrentTextureMatrix();
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.PREVIOUS);
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.PREVIOUS);
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "([I)V")
	@Override
	public void projectOffsets(@OriginalArg(0) int[] destination) {
		destination[0] = this.translateX;
		destination[1] = this.translateY;
		destination[3] = this.trimBottom;
		destination[2] = this.trimRight;
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(IIIII)V")
	@Override
	public void render(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int op, @OriginalArg(3) int color, @OriginalArg(4) int mode) {
		@Pc(8) NativeMatrix modelMatrix = this.nativeToolkit.getModelMatrix();
		@Pc(13) NativeMatrix textureMatrix = this.nativeToolkit.getCurrentTextureMatrix();
		@Pc(18) int drawX = x + this.translateX;
		@Pc(23) int drawY = y + this.translateY;
		this.texture.setFilterMode(FilterMode.aClass298_10);
		this.nativeToolkit.setupUiRenderState();
		this.nativeToolkit.setTextureSampler(this.texture);
		this.nativeToolkit.toggleBlending(mode);
		this.nativeToolkit.setColourType(op);
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.CONSTANT_COLOR);
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.CONSTANT_COLOR);
		this.nativeToolkit.setColourValue(color);
		modelMatrix.setScale(0.0F, (float) this.height, (float) this.width);
		modelMatrix.translate(drawX, drawY, 0);
		textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) this.height), this.texture.toTextureCoordS((float) this.width));
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
		this.nativeToolkit.commitModelMatrix();
		this.nativeToolkit.drawQuadTriangleFan();
		this.nativeToolkit.resetCurrentTextureMatrix();
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.PREVIOUS);
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.PREVIOUS);
	}

	@OriginalMember(owner = "client!jd", name = "c", descriptor = "()I")
	@Override
	public int getWidth() {
		return this.width;
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(IIIIII)V")
	@Override
	public void captureScreenRegion(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int op, @OriginalArg(5) int colour) {
		@Pc(13) int[] pixels = this.nativeToolkit.na(op, colour, width, height);
		if (pixels != null) {
			for (@Pc(17) int i = 0; i < pixels.length; i++) {
				pixels[i] |= 0xFF000000;
			}
			this.updateTextureRegion(x, y, width, height, pixels, width);
		}
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(IILclient!aa;II)V")
	@Override
	public void drawWithSurface(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) ComponentSurface clippingMask, @OriginalArg(3) int surfaceOffsetX, @OriginalArg(4) int surfaceOffsetY) {
		@Pc(6) MaskedTextureSurface maskSurface = (MaskedTextureSurface) clippingMask;
		@Pc(11) int drawY = y + this.translateY;
		@Pc(16) int drawX = x + this.translateX;
		@Pc(19) GpuTexture gpuTexture = maskSurface.nativeTextureSampler;
		this.texture.setFilterMode(FilterMode.aClass298_10);
		this.nativeToolkit.setupUiRenderState();
		this.nativeToolkit.setTextureSampler(this.texture);
		this.nativeToolkit.toggleBlending(1);
		this.nativeToolkit.setColourType(1);
		@Pc(49) NativeMatrix modelMatrix = this.nativeToolkit.getModelMatrix();
		modelMatrix.setScale(0.0F, (float) this.height, (float) this.width);
		modelMatrix.translate(drawX, drawY, 0);
		this.nativeToolkit.commitModelMatrix();
		@Pc(73) NativeMatrix textureMatrix = this.nativeToolkit.getCurrentTextureMatrix();
		textureMatrix.setScale(1.0F, this.texture.toTextureCoordT((float) this.height), this.texture.toTextureCoordS((float) this.width));
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
		this.nativeToolkit.setTextureSlot(1);
		this.nativeToolkit.setTextureSampler(gpuTexture);
		this.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineFunction.aClass315_4);
		this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS);
		@Pc(123) NativeMatrix maskTextureMatrix = this.nativeToolkit.getCurrentTextureMatrix();
		maskTextureMatrix.setScale(1.0F, gpuTexture.toTextureCoordT((float) this.height), gpuTexture.toTextureCoordS((float) this.width));
		maskTextureMatrix.translate(gpuTexture.toTextureCoordT((float) (drawY - surfaceOffsetY)), 0.0F, gpuTexture.toTextureCoordS((float) (drawX - surfaceOffsetX)));
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
		this.nativeToolkit.drawQuadTriangleFan();
		this.nativeToolkit.resetCurrentTextureMatrix();
		this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
		this.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE);
		this.nativeToolkit.setTextureSampler(null);
		this.nativeToolkit.setTextureSlot(0);
		this.nativeToolkit.resetCurrentTextureMatrix();
	}
}
