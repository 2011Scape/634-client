import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!m")
public final class JavaFont extends AbstractFontRenderer {

	@OriginalMember(owner = "client!m", name = "y", descriptor = "Lclient!ao;")
	private JavaToolkit toolkit;

	@OriginalMember(owner = "client!m", name = "z", descriptor = "[I")
	private final int[] glyphWidth;

	@OriginalMember(owner = "client!m", name = "w", descriptor = "[I")
	private final int[] glyphHeight;

	@OriginalMember(owner = "client!m", name = "D", descriptor = "[[B")
	private final byte[][] glyphRaster;

	@OriginalMember(owner = "client!m", name = "B", descriptor = "[I")
	private final int[] glyphOffsetY;

	@OriginalMember(owner = "client!m", name = "x", descriptor = "[I")
	private final int[] glyphOffsetX;

	@OriginalMember(owner = "client!m", name = "v", descriptor = "[I")
	private final int[] palette;

	@OriginalMember(owner = "client!m", name = "<init>", descriptor = "(Lclient!ao;Lclient!oea;[Lclient!rh;[I[I)V")
	public JavaFont(@OriginalArg(0) JavaToolkit javaToolkit, @OriginalArg(1) FontMetrics fontMetrics, @OriginalArg(2) SpriteType[] indexedImages, @OriginalArg(3) int[] glyphWidth, @OriginalArg(4) int[] glyphHeight) {
		super(javaToolkit, fontMetrics);
		this.toolkit = javaToolkit;
		this.toolkit = javaToolkit;
		this.glyphWidth = glyphWidth;
		this.glyphHeight = glyphHeight;
		this.glyphRaster = new byte[indexedImages.length][];
		this.glyphOffsetY = new int[indexedImages.length];
		this.glyphOffsetX = new int[indexedImages.length];
		for (@Pc(32) int i = 0; i < indexedImages.length; i++) {
			this.glyphRaster[i] = indexedImages[i].pixels;
			this.glyphOffsetY[i] = indexedImages[i].minY;
			this.glyphOffsetX[i] = indexedImages[i].minX;
		}
		this.palette = indexedImages[0].pallete;
	}

	@OriginalMember(owner = "client!m", name = "a", descriptor = "([B[IIIIIIII)V")
	private void blitSolid(@OriginalArg(0) byte[] src, @OriginalArg(1) int[] dst, @OriginalArg(2) int colour, @OriginalArg(3) int srcIndex, @OriginalArg(4) int dstIndex, @OriginalArg(5) int width, @OriginalArg(6) int height, @OriginalArg(7) int dstStep, @OriginalArg(8) int srcStep) {
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

	@OriginalMember(owner = "client!m", name = "a", descriptor = "([B[I[IIIIIII)V")
	private void blitPalette(@OriginalArg(0) byte[] src, @OriginalArg(1) int[] dst, @OriginalArg(2) int[] palette, @OriginalArg(3) int srcIndex, @OriginalArg(4) int dstIndex, @OriginalArg(5) int width, @OriginalArg(6) int height, @OriginalArg(7) int dstStep, @OriginalArg(8) int srcStep) {
		@Pc(4) int blocks = -(width >> 2);
		@Pc(9) int remainder = -(width & 0x3);
		for (@Pc(14) int row = -height; row < 0; row++) {
			for (@Pc(17) int block = blocks; block < 0; block++) {
				@Pc(24) byte pixel0;
				if ((pixel0 = src[srcIndex++]) == 0) {
					dstIndex++;
				} else {
					dst[dstIndex++] = palette[pixel0 & 0xFF];
				}
				@Pc(42) byte pixel1;
				if ((pixel1 = src[srcIndex++]) == 0) {
					dstIndex++;
				} else {
					dst[dstIndex++] = palette[pixel1 & 0xFF];
				}
				@Pc(60) byte pixel2;
				if ((pixel2 = src[srcIndex++]) == 0) {
					dstIndex++;
				} else {
					dst[dstIndex++] = palette[pixel2 & 0xFF];
				}
				@Pc(78) byte pixel3;
				if ((pixel3 = src[srcIndex++]) == 0) {
					dstIndex++;
				} else {
					dst[dstIndex++] = palette[pixel3 & 0xFF];
				}
			}
			for (@Pc(95) int column = remainder; column < 0; column++) {
				@Pc(102) byte pixel;
				if ((pixel = src[srcIndex++]) == 0) {
					dstIndex++;
				} else {
					dst[dstIndex++] = palette[pixel & 0xFF];
				}
			}
			dstIndex += dstStep;
			srcIndex += srcStep;
		}
	}

	@OriginalMember(owner = "client!m", name = "a", descriptor = "([B[IIIIIIIIIIILclient!aa;II)V")
	private void blitSolidMasked(@OriginalArg(0) byte[] src, @OriginalArg(1) int[] dst, @OriginalArg(2) int colour, @OriginalArg(3) int srcIndex, @OriginalArg(4) int dstIndex, @OriginalArg(5) int width, @OriginalArg(6) int height, @OriginalArg(7) int dstStep, @OriginalArg(8) int srcStep, @OriginalArg(9) int x, @OriginalArg(10) int y, @OriginalArg(11) int srcStride, @OriginalArg(12) ComponentSurface mask, @OriginalArg(13) int maskX, @OriginalArg(14) int maskY) {
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
		for (@Pc(58) int row = startY; row < endY; row++) {
			@Pc(67) int lineX = lineOffsets[row - maskY] + maskX;
			@Pc(73) int lineWidth = lineWidths[row - maskY];
			@Pc(75) int remaining = width;
			@Pc(82) int skip;
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
			for (@Pc(150) int column = -lineWidth; column < 0; column++) {
				if (src[srcIndex++] == 0) {
					dstIndex++;
				} else {
					dst[dstIndex++] = colour;
				}
			}
			srcIndex += skip + srcIndex;
			dstIndex += skip + dstStep;
		}
	}

	@OriginalMember(owner = "client!m", name = "a", descriptor = "(CIIIZLclient!aa;II)V")
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
		if (shadow) {
			this.blitSolidMasked(this.glyphRaster[c], this.toolkit.surfaceRaster, colour, srcIndex, dstIndex, width, height, dstStep, srcStep, x, y, this.glyphWidth[c], mask, offsetX, offsetY);
		} else {
			this.blitPaletteMasked(this.glyphRaster[c], this.toolkit.surfaceRaster, this.palette, colour, srcIndex, dstIndex, width, height, dstStep, srcStep, x, y, this.glyphWidth[c], mask, offsetX, offsetY);
		}
	}

	@OriginalMember(owner = "client!m", name = "fa", descriptor = "(CIIIZ)V")
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
		if (shadow) {
			this.blitSolid(this.glyphRaster[c], this.toolkit.surfaceRaster, colour, srcIndex, dstIndex, width, height, dstStep, srcStep);
		} else {
			this.blitPalette(this.glyphRaster[c], this.toolkit.surfaceRaster, this.palette, srcIndex, dstIndex, width, height, dstStep, srcStep);
		}
	}

	@OriginalMember(owner = "client!m", name = "a", descriptor = "([B[I[IIIIIIIIIIILclient!aa;II)V")
	private void blitPaletteMasked(@OriginalArg(0) byte[] src, @OriginalArg(1) int[] dst, @OriginalArg(2) int[] palette, @OriginalArg(3) int colour, @OriginalArg(4) int srcIndex, @OriginalArg(5) int dstIndex, @OriginalArg(6) int width, @OriginalArg(7) int height, @OriginalArg(8) int dstStep, @OriginalArg(9) int srcStep, @OriginalArg(10) int x, @OriginalArg(11) int y, @OriginalArg(12) int srcStride, @OriginalArg(13) ComponentSurface mask, @OriginalArg(14) int maskX, @OriginalArg(15) int maskY) {
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
		for (@Pc(60) int row = startY; row < endY; row++) {
			@Pc(69) int lineX = lineOffsets[row - maskY] + maskX;
			@Pc(75) int lineWidth = lineWidths[row - maskY];
			@Pc(77) int remaining = width;
			@Pc(84) int skip;
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
			for (@Pc(152) int column = -lineWidth; column < 0; column++) {
				@Pc(159) byte pixel;
				if ((pixel = src[srcIndex++]) == 0) {
					dstIndex++;
				} else {
					dst[dstIndex++] = palette[pixel & 0xFF];
				}
			}
			srcIndex += skip + srcStep;
			dstIndex += skip + dstStep;
		}
	}
}
