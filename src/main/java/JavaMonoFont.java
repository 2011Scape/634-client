import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!dv")
public final class JavaMonoFont extends AbstractFontRenderer {

	@OriginalMember(owner = "client!dv", name = "v", descriptor = "Lclient!ao;")
	private final JavaToolkit toolkit;

	@OriginalMember(owner = "client!dv", name = "y", descriptor = "[I")
	private final int[] glyphWidth;

	@OriginalMember(owner = "client!dv", name = "x", descriptor = "[I")
	private final int[] glyphHeight;

	@OriginalMember(owner = "client!dv", name = "z", descriptor = "[[B")
	private final byte[][] glyphRaster;

	@OriginalMember(owner = "client!dv", name = "w", descriptor = "[I")
	private final int[] glyphOffsetY;

	@OriginalMember(owner = "client!dv", name = "A", descriptor = "[I")
	private final int[] glyphOffsetX;

	@OriginalMember(owner = "client!dv", name = "<init>", descriptor = "(Lclient!ao;Lclient!oea;[Lclient!rh;[I[I)V")
	public JavaMonoFont(@OriginalArg(0) JavaToolkit javaToolkit, @OriginalArg(1) FontMetrics fontMetrics, @OriginalArg(2) SpriteType[] indexedImages, @OriginalArg(3) int[] glyphWidth, @OriginalArg(4) int[] glyphHeight) {
		super(javaToolkit, fontMetrics);
		this.toolkit = javaToolkit;
		this.glyphWidth = glyphWidth;
		this.glyphHeight = glyphHeight;
		this.glyphRaster = new byte[indexedImages.length][];
		this.glyphOffsetY = new int[indexedImages.length];
		this.glyphOffsetX = new int[indexedImages.length];
		for (@Pc(29) int i = 0; i < indexedImages.length; i++) {
			this.glyphRaster[i] = indexedImages[i].pixels;
			this.glyphOffsetY[i] = indexedImages[i].minY;
			this.glyphOffsetX[i] = indexedImages[i].minX;
		}
	}

	@OriginalMember(owner = "client!dv", name = "fa", descriptor = "(CIIIZ)V")
	@Override
	public void fa(@OriginalArg(0) char c, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int colour, @OriginalArg(4) boolean shadow) {
		x += this.glyphOffsetX[c];
		y += this.glyphOffsetY[c];
		@Pc(18) int width = this.glyphWidth[c];
		@Pc(23) int height = this.glyphHeight[c];
		@Pc(27) int surfaceWidth = this.toolkit.surfaceWidth;
		@Pc(33) int dstIndex = x + y * surfaceWidth;
		@Pc(37) int dstStep = surfaceWidth - width;
		@Pc(39) int srcStep = 0;
		@Pc(41) int srcIndex = 0;
		@Pc(52) int clip;
		if (y < this.toolkit.clipY1) {
			clip = this.toolkit.clipY1 - y;
			height -= clip;
			y = this.toolkit.clipY1;
			srcIndex += clip * width;
			dstIndex += clip * surfaceWidth;
		}
		if (y + height > this.toolkit.clipY2) {
			height -= y + height - this.toolkit.clipY2;
		}
		if (x < this.toolkit.clipX1) {
			clip = this.toolkit.clipX1 - x;
			width -= clip;
			x = this.toolkit.clipX1;
			srcIndex += clip;
			dstIndex += clip;
			srcStep += clip;
			dstStep += clip;
		}
		if (x + width > this.toolkit.clipX2) {
			clip = x + width - this.toolkit.clipX2;
			width -= clip;
			srcStep += clip;
			dstStep += clip;
		}
		if (width <= 0 || height <= 0) {
			return;
		}
		if ((colour & -16777216) == -16777216) {
			this.blitOpaque(this.glyphRaster[c], this.toolkit.surfaceRaster, colour, srcIndex, dstIndex, width, height, dstStep, srcStep);
		} else if ((colour & 0xFF000000) != 0) {
			this.blendImage(this.glyphRaster[c], this.toolkit.surfaceRaster, colour, srcIndex, dstIndex, width, height, dstStep, srcStep);
		}
	}

	@OriginalMember(owner = "client!dv", name = "a", descriptor = "([B[IIIIIIII)V")
	private void blendImage(@OriginalArg(0) byte[] glyphData, @OriginalArg(1) int[] pixels, @OriginalArg(2) int color, @OriginalArg(3) int glyphOffset, @OriginalArg(4) int pixelOffset, @OriginalArg(5) int width, @OriginalArg(6) int height, @OriginalArg(7) int dstRowPadding, @OriginalArg(8) int srcRowPadding) {
		@Pc(3) int alpha = color >>> 24;
		@Pc(7) int invAlpha = 255 - alpha;
		for (@Pc(10) int row = -height; row < 0; row++) {
			for (@Pc(14) int column = -width; column < 0; column++) {
				if (glyphData[glyphOffset++] == 0) {
					pixelOffset++;
				} else {
					@Pc(38) int blendedColour = ((color & 0xFF00FF) * alpha & 0xFF00FF00) + ((color & 0xFF00) * alpha & 0xFF0000) >> 8;
					@Pc(42) int background = pixels[pixelOffset];
					pixels[pixelOffset++] = (((background & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((background & 0xFF00) * invAlpha & 0xFF0000) >> 8) + blendedColour;
				}
			}
			pixelOffset += dstRowPadding;
			glyphOffset += srcRowPadding;
		}
	}

	@OriginalMember(owner = "client!dv", name = "a", descriptor = "([B[IIIIIIIIIIILclient!aa;II)V")
	private void drawGlyphOpaqueMasked(@OriginalArg(0) byte[] glyphData, @OriginalArg(1) int[] pixels, @OriginalArg(2) int color, @OriginalArg(3) int glyphOffset, @OriginalArg(4) int pixelOffset, @OriginalArg(5) int clippedWidth, @OriginalArg(6) int clippedHeight, @OriginalArg(7) int dstRowPadding, @OriginalArg(8) int srcRowPadding, @OriginalArg(9) int drawX, @OriginalArg(10) int drawY, @OriginalArg(11) int fullGlyphWidth, @OriginalArg(12) ComponentSurface surface, @OriginalArg(13) int surfaceOffsetX, @OriginalArg(14) int surfaceOffsetY) {
		@Pc(2) JavaClippingMask javaMask = (JavaClippingMask) surface;
		@Pc(5) int[] lineOffsets = javaMask.lineWidths;
		@Pc(8) int[] lineWidths = javaMask.lineOffsets;
		@Pc(10) int startY = drawY;
		if (surfaceOffsetY > drawY) {
			startY = surfaceOffsetY;
			pixelOffset += (surfaceOffsetY - drawY) * this.toolkit.surfaceWidth;
			glyphOffset += (surfaceOffsetY - drawY) * fullGlyphWidth;
		}
		@Pc(50) int endY = surfaceOffsetY + lineOffsets.length < drawY + clippedHeight ? surfaceOffsetY + lineOffsets.length : drawY + clippedHeight;
		for (@Pc(52) int row = startY; row < endY; row++) {
			@Pc(61) int lineX = surfaceOffsetX + lineOffsets[row - surfaceOffsetY];
			@Pc(67) int lineWidth = lineWidths[row - surfaceOffsetY];
			@Pc(69) int remaining = clippedWidth;
			@Pc(76) int skip;
			if (drawX > lineX) {
				skip = drawX - lineX;
				if (skip >= lineWidth) {
					glyphOffset += clippedWidth + srcRowPadding;
					pixelOffset += clippedWidth + dstRowPadding;
					continue;
				}
				lineWidth -= skip;
			} else {
				skip = lineX - drawX;
				if (skip >= clippedWidth) {
					glyphOffset += clippedWidth + srcRowPadding;
					pixelOffset += clippedWidth + dstRowPadding;
					continue;
				}
				glyphOffset += skip;
				remaining = clippedWidth - skip;
				pixelOffset += skip;
			}
			skip = 0;
			if (remaining < lineWidth) {
				lineWidth = remaining;
			} else {
				skip = remaining - lineWidth;
			}
			for (@Pc(143) int column = 0; column < lineWidth; column++) {
				if (glyphData[glyphOffset++] == 0) {
					pixelOffset++;
				} else {
					pixels[pixelOffset++] = color;
				}
			}
			glyphOffset += skip + srcRowPadding;
			pixelOffset += skip + dstRowPadding;
		}
	}

	@OriginalMember(owner = "client!dv", name = "b", descriptor = "([B[IIIIIIII)V")
	private void blitOpaque(@OriginalArg(0) byte[] src, @OriginalArg(1) int[] dst, @OriginalArg(2) int colour, @OriginalArg(3) int srcIndex, @OriginalArg(4) int dstIndex, @OriginalArg(5) int width, @OriginalArg(6) int height, @OriginalArg(7) int dstStep, @OriginalArg(8) int srcStep) {
		@Pc(4) int blocks = -(width >> 2);
		@Pc(9) int remainder = -(width & 0x3);
		for (@Pc(12) int row = -height; row < 0; row++) {
			for (@Pc(15) int block = blocks; block < 0; block++) {
				if (src[srcIndex++] == 0) {
					dstIndex++;
				} else {
					dst[dstIndex++] = colour;
				}
				if (src[srcIndex++] == 0) {
					dstIndex++;
				} else {
					dst[dstIndex++] = colour;
				}
				if (src[srcIndex++] == 0) {
					dstIndex++;
				} else {
					dst[dstIndex++] = colour;
				}
				if (src[srcIndex++] == 0) {
					dstIndex++;
				} else {
					dst[dstIndex++] = colour;
				}
			}
			for (@Pc(69) int column = remainder; column < 0; column++) {
				if (src[srcIndex++] == 0) {
					dstIndex++;
				} else {
					dst[dstIndex++] = colour;
				}
			}
			dstIndex += dstStep;
			srcIndex += srcStep;
		}
	}

	@OriginalMember(owner = "client!dv", name = "a", descriptor = "(CIIIZLclient!aa;II)V")
	@Override
	public void renderSymbol(@OriginalArg(0) char c, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int colour, @OriginalArg(4) boolean shadow, @OriginalArg(5) ComponentSurface mask, @OriginalArg(6) int offsetX, @OriginalArg(7) int offsetY) {
		if (mask == null) {
			this.fa(c, x, y, colour, shadow);
			return;
		}
		x += this.glyphOffsetX[c];
		y += this.glyphOffsetY[c];
		@Pc(28) int width = this.glyphWidth[c];
		@Pc(33) int height = this.glyphHeight[c];
		@Pc(37) int dstStride = this.toolkit.surfaceWidth;
		@Pc(43) int dstIndex = x + y * dstStride;
		@Pc(47) int dstStep = dstStride - width;
		@Pc(49) int srcStep = 0;
		@Pc(51) int srcIndex = 0;
		@Pc(62) int clip;
		if (y < this.toolkit.clipY1) {
			clip = this.toolkit.clipY1 - y;
			height -= clip;
			y = this.toolkit.clipY1;
			srcIndex = clip * width;
			dstIndex += clip * dstStride;
		}
		if (y + height > this.toolkit.clipY2) {
			height -= y + height - this.toolkit.clipY2;
		}
		if (x < this.toolkit.clipX1) {
			clip = this.toolkit.clipX1 - x;
			width -= clip;
			x = this.toolkit.clipX1;
			srcIndex += clip;
			dstIndex += clip;
			srcStep = clip;
			dstStep += clip;
		}
		if (x + width > this.toolkit.clipX2) {
			clip = x + width - this.toolkit.clipX2;
			width -= clip;
			srcStep += clip;
			dstStep += clip;
		}
		if (width <= 0 || height <= 0) {
			return;
		}
		if ((colour & -16777216) == -16777216) {
			this.drawGlyphOpaqueMasked(this.glyphRaster[c], this.toolkit.surfaceRaster, colour, srcIndex, dstIndex, width, height, dstStep, srcStep, x, y, this.glyphWidth[c], mask, offsetX, offsetY);
		} else {
			this.blitBlendedMasked(this.glyphRaster[c], this.toolkit.surfaceRaster, colour, srcIndex, dstIndex, width, height, dstStep, srcStep, x, y, this.glyphWidth[c], mask, offsetX, offsetY);
		}
	}

	@OriginalMember(owner = "client!dv", name = "b", descriptor = "([B[IIIIIIIIIIILclient!aa;II)V")
	private void blitBlendedMasked(@OriginalArg(0) byte[] src, @OriginalArg(1) int[] dst, @OriginalArg(2) int colour, @OriginalArg(3) int srcIndex, @OriginalArg(4) int dstIndex, @OriginalArg(5) int width, @OriginalArg(6) int height, @OriginalArg(7) int dstStep, @OriginalArg(8) int srcStep, @OriginalArg(9) int x, @OriginalArg(10) int y, @OriginalArg(11) int srcStride, @OriginalArg(12) ComponentSurface mask, @OriginalArg(13) int maskX, @OriginalArg(14) int maskY) {
		@Pc(2) JavaClippingMask javaMask = (JavaClippingMask) mask;
		@Pc(5) int[] lineOffsets = javaMask.lineWidths;
		@Pc(8) int[] lineWidths = javaMask.lineOffsets;
		@Pc(14) int clipOffsetX = x - this.toolkit.clipX1;
		@Pc(16) int startY = y;
		if (maskY > y) {
			startY = maskY;
			dstIndex += (maskY - y) * this.toolkit.surfaceWidth;
			srcIndex += (maskY - y) * srcStride;
		}
		@Pc(56) int endY = maskY + lineOffsets.length < y + height ? maskY + lineOffsets.length : y + height;
		@Pc(60) int alpha = colour >>> 24;
		@Pc(64) int invAlpha = 255 - alpha;
		for (@Pc(66) int row = startY; row < endY; row++) {
			@Pc(75) int lineX = lineOffsets[row - maskY] + maskX;
			@Pc(81) int lineWidth = lineWidths[row - maskY];
			@Pc(83) int remaining = width;
			@Pc(90) int skip;
			if (clipOffsetX > lineX) {
				skip = clipOffsetX - lineX;
				if (skip >= lineWidth) {
					srcIndex += width + srcStep;
					dstIndex += width + dstStep;
					continue;
				}
				lineWidth -= skip;
			} else {
				skip = lineX - clipOffsetX;
				if (skip >= width) {
					srcIndex += width + srcStep;
					dstIndex += width + dstStep;
					continue;
				}
				srcIndex += skip;
				remaining = width - skip;
				dstIndex += skip;
			}
			skip = 0;
			if (remaining < lineWidth) {
				lineWidth = remaining;
			} else {
				skip = remaining - lineWidth;
			}
			for (@Pc(158) int column = -lineWidth; column < 0; column++) {
				if (src[srcIndex++] == 0) {
					dstIndex++;
				} else {
					@Pc(182) int blendedColour = ((colour & 0xFF00FF) * alpha & 0xFF00FF00) + ((colour & 0xFF00) * alpha & 0xFF0000) >> 8;
					@Pc(186) int background = dst[dstIndex];
					dst[dstIndex++] = (((background & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((background & 0xFF00) * invAlpha & 0xFF0000) >> 8) + blendedColour;
				}
			}
			srcIndex += skip + srcStep;
			dstIndex += skip + dstStep;
		}
	}
}
