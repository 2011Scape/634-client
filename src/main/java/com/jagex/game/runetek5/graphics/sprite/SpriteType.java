package com.jagex.game.runetek5.graphics.sprite;

import com.jagex.game.runetek5.core.util.Arrays;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.graphics.SpriteDataProvider;

@OriginalClass("client!rh")
public final class SpriteType {

	@OriginalMember(owner = "client!rh", name = "a", descriptor = "[B")
	public byte[] alpha;

	@OriginalMember(owner = "client!rh", name = "b", descriptor = "I")
	public int height;

	@OriginalMember(owner = "client!rh", name = "c", descriptor = "[I")
	public int[] pallete;

	@OriginalMember(owner = "client!rh", name = "d", descriptor = "I")
	public int borderX;

	@OriginalMember(owner = "client!rh", name = "e", descriptor = "[B")
	public byte[] pixels;

	@OriginalMember(owner = "client!rh", name = "f", descriptor = "I")
	public int minY;

	@OriginalMember(owner = "client!rh", name = "g", descriptor = "I")
	public int borderY;

	@OriginalMember(owner = "client!rh", name = "h", descriptor = "I")
	public int width;

	@OriginalMember(owner = "client!rh", name = "i", descriptor = "I")
	public int minX;

	@OriginalMember(owner = "client!rh", name = "a", descriptor = "()I")
	public int widthSum() {
		return this.width + this.minX + this.borderX;
	}

	@OriginalMember(owner = "client!rh", name = "a", descriptor = "(I)V")
	public void outline(@OriginalArg(0) int outlineColor) {
		@Pc(1) int outlineIndex = -1;
		@Pc(8) int local8;
		@Pc(69) int local69;
		@Pc(63) int local63;
		if (this.pallete.length < 255) {
			for (int paletteIndex = 0; paletteIndex < this.pallete.length; paletteIndex++) {
				if (this.pallete[paletteIndex] == outlineColor) {
					outlineIndex = paletteIndex;
					break;
				}
			}
			if (outlineIndex == -1) {
				outlineIndex = this.pallete.length;
				@Pc(38) int[] newPalette = new int[this.pallete.length + 1];
				Arrays.copy(this.pallete, 0, newPalette, 0, this.pallete.length);
				this.pallete = newPalette;
				newPalette[outlineIndex] = outlineColor;
			}
		} else {
			int bestDistance = Integer.MAX_VALUE;
			int targetRed = outlineColor >> 16 & 0xFF;
			int targetGreen = outlineColor >> 8 & 0xFF;
			@Pc(73) int targetBlue = outlineColor & 0xFF;
			for (@Pc(75) int i = 0; i < this.pallete.length; i++) {
				@Pc(81) int paletteColor = this.pallete[i];
				@Pc(87) int red = paletteColor >> 16 & 0xFF;
				@Pc(93) int green = paletteColor >> 8 & 0xFF;
				@Pc(97) int blue = paletteColor & 0xFF;
				@Pc(101) int redDiff = targetRed - red;
				if (redDiff < 0) {
					redDiff = -redDiff;
				}
				@Pc(110) int greenDiff = targetGreen - green;
				if (greenDiff < 0) {
					greenDiff = -greenDiff;
				}
				@Pc(119) int blueDiff = targetBlue - blue;
				if (blueDiff < 0) {
					blueDiff = -blueDiff;
				}
				@Pc(130) int distance = redDiff + greenDiff + blueDiff;
				if (distance < bestDistance) {
					bestDistance = distance;
					outlineIndex = i;
				}
			}
		}
		for (int row = this.height - 1; row > 0; row--) {
			int rowOffset = row * this.width;
			for (int column = this.width - 1; column > 0; column--) {
				if (this.pallete[this.pixels[column + rowOffset] & 0xFF] == 0 && this.pallete[this.pixels[column + rowOffset - this.width - 1] & 0xFF] != 0) {
					this.pixels[column + rowOffset] = (byte) outlineIndex;
				}
			}
		}
	}

	@OriginalMember(owner = "client!rh", name = "b", descriptor = "(I)V")
	public void drawSmallBorder(@OriginalArg(0) int borderThickness) {
		@Pc(2) int fullWidth = this.widthSum();
		@Pc(5) int fullHeight = this.heightSum();
		if (this.width == fullWidth && this.height == fullHeight) {
			return;
		}
		@Pc(16) int padLeft = borderThickness;
		if (borderThickness > this.minX) {
			padLeft = this.minX;
		}
		@Pc(25) int padRight = borderThickness;
		if (borderThickness + this.minX + this.width > fullWidth) {
			padRight = fullWidth - this.minX - this.width;
		}
		@Pc(44) int padTop = borderThickness;
		if (borderThickness > this.minY) {
			padTop = this.minY;
		}
		@Pc(53) int padBottom = borderThickness;
		if (borderThickness + this.minY + this.height > fullHeight) {
			padBottom = fullHeight - this.minY - this.height;
		}
		@Pc(77) int newWidth = this.width + padLeft + padRight;
		@Pc(84) int newHeight = this.height + padTop + padBottom;
		@Pc(89) byte[] newPixels = new byte[newWidth * newHeight];
		@Pc(100) int local100;
		@Pc(108) int local108;
		@Pc(110) int local110;
		if (this.alpha == null) {
			for (@Pc(94) int i = 0; i < this.height; i++) {
				int srcIndex = i * this.width;
				int dstIndex = (i + padTop) * newWidth + padLeft;
				for (int column = 0; column < this.width; column++) {
					newPixels[dstIndex++] = this.pixels[srcIndex++];
				}
			}
		} else {
			@Pc(136) byte[] newAlpha = new byte[newWidth * newHeight];
			for (int row = 0; row < this.height; row++) {
				int srcIndex = row * this.width;
				int dstIndex = (row + padTop) * newWidth + padLeft;
				for (@Pc(154) int j = 0; j < this.width; j++) {
					newAlpha[dstIndex] = this.alpha[srcIndex];
					newPixels[dstIndex++] = this.pixels[srcIndex++];
				}
			}
			this.alpha = newAlpha;
		}
		this.minX -= padLeft;
		this.minY -= padTop;
		this.borderX -= padRight;
		this.borderY -= padBottom;
		this.width = newWidth;
		this.height = newHeight;
		this.pixels = newPixels;
	}

	@OriginalMember(owner = "client!rh", name = "b", descriptor = "()V")
	public void flipVertical() {
		@Pc(2) byte[] pixels = this.pixels;
		@Pc(37) byte tmp;
		@Pc(12) int local12;
		@Pc(18) int local18;
		@Pc(28) int local28;
		@Pc(32) int local32;
		if (this.alpha == null) {
			for (int row = (this.height >> 1) - 1; row >= 0; row--) {
				int topIndex = row * this.width;
				int bottomIndex = (this.height - row - 1) * this.width;
				for (int column = -this.width; column < 0; column++) {
					tmp = pixels[topIndex];
					pixels[topIndex] = pixels[bottomIndex];
					pixels[bottomIndex] = tmp;
					topIndex++;
					bottomIndex++;
				}
			}
		} else {
			@Pc(59) byte[] alpha = this.alpha;
			for (int row = (this.height >> 1) - 1; row >= 0; row--) {
				int topIndex = row * this.width;
				int bottomIndex = (this.height - row - 1) * this.width;
				for (@Pc(86) int i = -this.width; i < 0; i++) {
					tmp = pixels[topIndex];
					pixels[topIndex] = pixels[bottomIndex];
					pixels[bottomIndex] = tmp;
					tmp = alpha[topIndex];
					alpha[topIndex] = alpha[bottomIndex];
					alpha[bottomIndex] = tmp;
					topIndex++;
					bottomIndex++;
				}
			}
		}
		int oldMinY = this.minY;
		this.minY = this.borderY;
		this.borderY = oldMinY;
	}

	@OriginalMember(owner = "client!rh", name = "c", descriptor = "(I)V")
	public void drawThickBorder(@OriginalArg(0) int color) {
		@Pc(1) int borderIndex = -1;
		@Pc(8) int local8;
		@Pc(69) int local69;
		@Pc(73) int local73;
		@Pc(75) int local75;
		if (this.pallete.length < 255) {
			for (int paletteIndex = 0; paletteIndex < this.pallete.length; paletteIndex++) {
				if (this.pallete[paletteIndex] == color) {
					borderIndex = paletteIndex;
					break;
				}
			}
			if (borderIndex == -1) {
				borderIndex = this.pallete.length;
				@Pc(38) int[] newPalette = new int[this.pallete.length + 1];
				Arrays.copy(this.pallete, 0, newPalette, 0, this.pallete.length);
				this.pallete = newPalette;
				newPalette[borderIndex] = color;
			}
		} else {
			int bestDistance = Integer.MAX_VALUE;
			@Pc(63) int targetRed = color >> 16 & 0xFF;
			int targetGreen = color >> 8 & 0xFF;
			int targetBlue = color & 0xFF;
			for (int paletteIndex = 0; paletteIndex < this.pallete.length; paletteIndex++) {
				@Pc(81) int paletteColor = this.pallete[paletteIndex];
				@Pc(87) int red = paletteColor >> 16 & 0xFF;
				@Pc(93) int green = paletteColor >> 8 & 0xFF;
				@Pc(97) int blue = paletteColor & 0xFF;
				@Pc(101) int redDiff = targetRed - red;
				if (redDiff < 0) {
					redDiff = -redDiff;
				}
				@Pc(110) int greenDiff = targetGreen - green;
				if (greenDiff < 0) {
					greenDiff = -greenDiff;
				}
				@Pc(119) int blueDiff = targetBlue - blue;
				if (blueDiff < 0) {
					blueDiff = -blueDiff;
				}
				@Pc(130) int distance = redDiff + greenDiff + blueDiff;
				if (distance < bestDistance) {
					bestDistance = distance;
					borderIndex = paletteIndex;
				}
			}
		}
		int pixelIndex = 0;
		@Pc(152) byte[] newPixels = new byte[this.width * this.height];
		for (int row = 0; row < this.height; row++) {
			for (int column = 0; column < this.width; column++) {
				int paletteIndex = this.pixels[pixelIndex] & 0xFF;
				if (this.pallete[paletteIndex] == 0) {
					if (column > 0 && this.pallete[this.pixels[pixelIndex - 1] & 0xFF] != 0) {
						paletteIndex = borderIndex;
					} else if (row > 0 && this.pallete[this.pixels[pixelIndex - this.width] & 0xFF] != 0) {
						paletteIndex = borderIndex;
					} else if (column < this.width - 1 && this.pallete[this.pixels[pixelIndex + 1] & 0xFF] != 0) {
						paletteIndex = borderIndex;
					} else if (row < this.height - 1 && this.pallete[this.pixels[pixelIndex + this.width] & 0xFF] != 0) {
						paletteIndex = borderIndex;
					}
				}
				newPixels[pixelIndex++] = (byte) paletteIndex;
			}
		}
		this.pixels = newPixels;
	}

	@OriginalMember(owner = "client!rh", name = "c", descriptor = "()[I")
	public int[] getPixels() {
		@Pc(2) int fullWidth = this.widthSum();
		@Pc(8) int[] argb = new int[fullWidth * this.heightSum()];
		@Pc(13) int y;
		@Pc(19) int srcIndex;
		@Pc(29) int dstIndex;
		@Pc(31) int x;
		if (this.alpha == null) {
			for (y = 0; y < this.height; y++) {
				srcIndex = y * this.width;
				dstIndex = this.minX + (y + this.minY) * fullWidth;
				for (x = 0; x < this.width; x++) {
					@Pc(96) int colour = this.pallete[this.pixels[srcIndex++] & 0xFF];
					if (colour == 0) {
						argb[dstIndex++] = 0;
					} else {
						argb[dstIndex++] = colour | 0xFF000000;
					}
				}
			}
		} else {
			for (y = 0; y < this.height; y++) {
				srcIndex = y * this.width;
				dstIndex = this.minX + (y + this.minY) * fullWidth;
				for (x = 0; x < this.width; x++) {
					argb[dstIndex++] = this.alpha[srcIndex] << 24 | this.pallete[this.pixels[srcIndex] & 0xFF];
					srcIndex++;
				}
			}
		}
		return argb;
	}

	@OriginalMember(owner = "client!rh", name = "d", descriptor = "()V")
	public void flipHorizontal() {
		@Pc(2) byte[] pixels = this.pixels;
		@Pc(29) byte tmp;
		@Pc(10) int local10;
		@Pc(16) int local16;
		@Pc(23) int local23;
		if (this.alpha == null) {
			for (int row = this.height - 1; row >= 0; row--) {
				int leftIndex = row * this.width;
				int rightIndex = (row + 1) * this.width;
				while (leftIndex < rightIndex) {
					rightIndex--;
					tmp = pixels[leftIndex];
					pixels[leftIndex] = pixels[rightIndex];
					pixels[rightIndex] = tmp;
					leftIndex++;
				}
			}
		} else {
			@Pc(50) byte[] alpha = this.alpha;
			for (int row = this.height - 1; row >= 0; row--) {
				int leftIndex = row * this.width;
				@Pc(68) int rowEnd = (row + 1) * this.width;
				while (leftIndex < rowEnd) {
					rowEnd--;
					tmp = pixels[leftIndex];
					pixels[leftIndex] = pixels[rowEnd];
					pixels[rowEnd] = tmp;
					tmp = alpha[leftIndex];
					alpha[leftIndex] = alpha[rowEnd];
					alpha[rowEnd] = tmp;
					leftIndex++;
				}
			}
		}
		int oldMinX = this.minX;
		this.minX = this.borderX;
		this.borderX = oldMinX;
	}

	@OriginalMember(owner = "client!rh", name = "e", descriptor = "()V")
	public void rotate90DegLeft() {
		@Pc(6) byte[] newPixels = new byte[this.width * this.height];
		@Pc(8) int dstIndex = 0;
		@Pc(13) int local13;
		@Pc(19) int local19;
		if (this.alpha == null) {
			for (int column = 0; column < this.width; column++) {
				for (int row = this.height - 1; row >= 0; row--) {
					newPixels[dstIndex++] = this.pixels[column + row * this.width];
				}
			}
			this.pixels = newPixels;
		} else {
			@Pc(52) byte[] newAlpha = new byte[this.width * this.height];
			for (int column = 0; column < this.width; column++) {
				for (@Pc(60) int i = this.height - 1; i >= 0; i--) {
					newPixels[dstIndex] = this.pixels[column + i * this.width];
					newAlpha[dstIndex++] = this.alpha[column + i * this.width];
				}
			}
			this.pixels = newPixels;
			this.alpha = newAlpha;
		}
		int swap = this.minY;
		this.minY = this.minX;
		this.minX = this.borderY;
		this.borderY = this.borderX;
		this.borderX = this.minY;
		swap = this.height;
		this.height = this.width;
		this.width = swap;
	}

	@OriginalMember(owner = "client!rh", name = "f", descriptor = "()I")
	public int heightSum() {
		return this.height + this.minY + this.borderY;
	}

	@OriginalMember(owner = "client!rh", name = "g", descriptor = "()V")
	public void expandToFullSize() {
		@Pc(2) int fullWidth = this.widthSum();
		@Pc(5) int fullHeight = this.heightSum();
		if (this.width == fullWidth && this.height == fullHeight) {
			return;
		}
		@Pc(19) byte[] newPixels = new byte[fullWidth * fullHeight];
		@Pc(29) int local29;
		@Pc(35) int local35;
		@Pc(45) int local45;
		if (this.alpha == null) {
			for (@Pc(80) int i = 0; i < this.height; i++) {
				int srcIndex = i * this.width;
				int dstIndex = (i + this.minY) * fullWidth + this.minX;
				for (int column = 0; column < this.width; column++) {
					newPixels[dstIndex++] = this.pixels[srcIndex++];
				}
			}
		} else {
			@Pc(27) byte[] newAlpha = new byte[fullWidth * fullHeight];
			for (int row = 0; row < this.height; row++) {
				int srcIndex = row * this.width;
				int dstIndex = (row + this.minY) * fullWidth + this.minX;
				for (@Pc(47) int j = 0; j < this.width; j++) {
					newPixels[dstIndex] = this.pixels[srcIndex];
					newAlpha[dstIndex++] = this.alpha[srcIndex++];
				}
			}
			this.alpha = newAlpha;
		}
		this.minX = this.borderX = this.minY = this.borderY = 0;
		this.width = fullWidth;
		this.height = fullHeight;
		this.pixels = newPixels;
	}
	@OriginalMember(owner = "client!rh", name = "a", descriptor = "(Lclient!in;II)[Lclient!rh;")
	public static SpriteType[] decodeGroup(@OriginalArg(0) Js5ResourceProvider resource, @OriginalArg(1) int archive) {
		@Pc(5) byte[] data = resource.getFile(archive, 0);
		return data == null ? null : SpriteDataProvider.load(data);
	}

	@OriginalMember(owner = "client!rh", name = "b", descriptor = "(Lclient!in;I)[Lclient!rh;")
	public static SpriteType[] listGroup(@OriginalArg(0) Js5ResourceProvider resource, @OriginalArg(1) int fileId) {
		@Pc(4) byte[] data = resource.getFile(fileId);
		return data == null ? null : SpriteDataProvider.load(data);
	}
}
