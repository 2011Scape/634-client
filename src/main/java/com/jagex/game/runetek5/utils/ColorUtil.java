package com.jagex.game.runetek5.utils;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.Chat;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;

public final class ColorUtil {
	@OriginalMember(owner = "client!gt", name = "h", descriptor = "[I")
	public static int[] hslToRgb;

	private ColorUtil() {
		/* empty */
	}
	@OriginalMember(owner = "client!nia", name = "c", descriptor = "[I")
	public static int[] hsvToRgbTable;
	@OriginalMember(owner = "client!uv", name = "a", descriptor = "(ZIZ)V")
	public static void releaseColorTables(@OriginalArg(0) boolean releaseHslTable, @OriginalArg(2) boolean releaseHsvTable) {
		if (releaseHslTable) {
			ColorUtil.hslToRgbUsers--;
			if (ColorUtil.hslToRgbUsers == 0) {
				ColorUtil.hslToRgb = null;
			}
		}
		if (!releaseHsvTable) {
			return;
		}
		Chat.hsvToRgbUsers--;
		if (Chat.hsvToRgbUsers == 0) {
			ColorUtil.hsvToRgbTable = null;
			return;
		}
	}

	@OriginalMember(owner = "client!gh", name = "a", descriptor = "(IIB)I")
	public static int scaleHslLightness(@OriginalArg(0) int hsl) {
		if (hsl == -1) {
			return 12345678;
		}
		@Pc(21) int lightness = (hsl & 0x7F) * 96 >> 7;
		if (lightness < 2) {
			lightness = 2;
		} else if (lightness > 126) {
			lightness = 126;
		}
		return lightness + (hsl & 0xFF80);
	}

	@OriginalMember(owner = "client!hq", name = "c", descriptor = "(II)S")
	public static short hslToHsv(@OriginalArg(0) int hsl) {
		@Pc(9) int hue = hsl >> 10 & 0x3F;
		@Pc(15) int saturation = hsl >> 3 & 0x70;
		@Pc(19) int lightness = hsl & 0x7F;
		@Pc(39) int delta = lightness <= 64 ? lightness * saturation >> 7 : saturation * (127 - lightness) >> 7;
		@Pc(43) int value = delta + lightness;
		@Pc(51) int hsvSaturation;
		if (value == 0) {
			hsvSaturation = delta << 1;
		} else {
			hsvSaturation = (delta << 8) / value;
		}
		return (short) (value | hsvSaturation >> 4 << 7 | hue << 10);
	}

	@OriginalMember(owner = "client!gba", name = "b", descriptor = "(I)V")
	public static void initHslToRgbTable() {
		if (ColorUtil.hslToRgb != null) {
			return;
		}
		ColorUtil.hslToRgb = new int[65536];
		@Pc(20) double gammaCorrection = Math.random() * 0.03D + 0.7D - 0.015D;
		for (@Pc(22) int i = 0; i < 65536; i++) {
			@Pc(36) double hue = (double) (i >> 10 & 0x3F) / 64.0D + 0.0078125D;
			@Pc(47) double saturation = (double) (i >> 7 & 0x7) / 8.0D + 0.0625D;
			@Pc(54) double lightness = (double) (i & 0x7F) / 128.0D;
			@Pc(56) double red = lightness;
			@Pc(58) double green = lightness;
			@Pc(60) double blue = lightness;
			if (saturation != 0.0D) {
				@Pc(74) double chroma;
				if (lightness < 0.5D) {
					chroma = lightness * (saturation + 1.0D);
				} else {
					chroma = lightness + saturation - lightness * saturation;
				}
				@Pc(91) double minComponent = lightness * 2.0D - chroma;
				@Pc(95) double hueRed = hue + 0.3333333333333333D;
				if (hueRed > 1.0D) {
					hueRed--;
				}
				@Pc(109) double hueBlue = hue - 0.3333333333333333D;
				if (hueBlue < 0.0D) {
					hueBlue++;
				}
				if (hueRed * 6.0D < 1.0D) {
					red = minComponent + (chroma - minComponent) * 6.0D * hueRed;
				} else if (hueRed * 2.0D < 1.0D) {
					red = chroma;
				} else if (hueRed * 3.0D < 2.0D) {
					red = (chroma - minComponent) * 6.0D * (0.6666666666666666D - hueRed) + minComponent;
				} else {
					red = minComponent;
				}
				if (hue * 6.0D < 1.0D) {
					green = minComponent + hue * 6.0D * (chroma - minComponent);
				} else if (hue * 2.0D < 1.0D) {
					green = chroma;
				} else if (hue * 3.0D < 2.0D) {
					green = (0.6666666666666666D - hue) * (chroma - minComponent) * 6.0D + minComponent;
				} else {
					green = minComponent;
				}
				if (hueBlue * 6.0D < 1.0D) {
					blue = minComponent + (chroma - minComponent) * 6.0D * hueBlue;
				} else if (hueBlue * 2.0D < 1.0D) {
					blue = chroma;
				} else if (hueBlue * 3.0D < 2.0D) {
					blue = minComponent + (chroma - minComponent) * (0.6666666666666666D - hueBlue) * 6.0D;
				} else {
					blue = minComponent;
				}
			}
			red = Math.pow(red, gammaCorrection);
			green = Math.pow(green, gammaCorrection);
			blue = Math.pow(blue, gammaCorrection);
			@Pc(288) int redInt = (int) (red * 256.0D);
			@Pc(293) int greenInt = (int) (green * 256.0D);
			@Pc(298) int blueInt = (int) (blue * 256.0D);
			@Pc(309) int rgb = (greenInt << 8) + (redInt << 16) + blueInt;
			ColorUtil.hslToRgb[i] = rgb;
		}
	}
	@OriginalMember(owner = "client!eea", name = "d", descriptor = "(I)V")
	public static void initHsvToRgbTable() {
		if (ColorUtil.hsvToRgbTable != null) {
			return;
		}
		ColorUtil.hsvToRgbTable = new int[65536];
		@Pc(20) double gammaCorrection = Math.random() * 0.03D + 0.7D - 0.015D;
		@Pc(22) int tableIndex = 0;
		for (@Pc(28) int i = 0; i < 512; i++) {
			@Pc(42) float hueAngle = ((float) (i >> 3) / 64.0F + 0.0078125F) * 360.0F;
			@Pc(51) float saturation = (float) (i & 0x7) / 8.0F + 0.0625F;
			for (@Pc(53) int j = 0; j < 128; j++) {
				@Pc(60) float value = (float) j / 128.0F;
				@Pc(62) float red = 0.0F;
				@Pc(64) float green = 0.0F;
				@Pc(66) float blue = 0.0F;
				@Pc(70) float hueSegment = hueAngle / 60.0F;
				@Pc(73) int hueSegmentInt = (int) hueSegment;
				@Pc(77) int hueRegion = hueSegmentInt % 6;
				@Pc(83) float fractionalHue = (float) -hueSegmentInt + hueSegment;
				@Pc(90) float minComponent = value * (1.0F - saturation);
				@Pc(98) float fallingComponent = value * (1.0F - fractionalHue * saturation);
				@Pc(108) float risingComponent = (1.0F - (1.0F - fractionalHue) * saturation) * value;
				if (hueRegion == 0) {
					red = value;
					green = risingComponent;
					blue = minComponent;
				} else if (hueRegion == 1) {
					green = value;
					red = fallingComponent;
					blue = minComponent;
				} else if (hueRegion == 2) {
					blue = risingComponent;
					red = minComponent;
					green = value;
				} else if (hueRegion == 3) {
					blue = value;
					green = fallingComponent;
					red = minComponent;
				} else if (hueRegion == 4) {
					blue = value;
					green = minComponent;
					red = risingComponent;
				} else if (hueRegion == 5) {
					red = value;
					green = minComponent;
					blue = fallingComponent;
				}
				red = (float) Math.pow((double) red, gammaCorrection);
				green = (float) Math.pow((double) green, gammaCorrection);
				blue = (float) Math.pow((double) blue, gammaCorrection);
				@Pc(203) int redInt = (int) (red * 256.0F);
				@Pc(208) int greenInt = (int) (green * 256.0F);
				@Pc(213) int blueInt = (int) (blue * 256.0F);
				@Pc(225) int rgb = (greenInt << 8) + (redInt << 16) + blueInt - 16777216;
				ColorUtil.hsvToRgbTable[tableIndex++] = rgb;
			}
		}
	}
	@OriginalMember(owner = "client!go", name = "a", descriptor = "(III)I")
	public static int alphaBlend(@OriginalArg(0) int background, @OriginalArg(1) int foreground) {
		@Pc(7) int alpha = foreground >>> 24;
		@Pc(12) int inverseAlpha = 255 - alpha;
		@Pc(30) int blendedForeground = ((foreground & 0xFF00) * alpha & 0xFF0000 | (foreground & 0xFF00FF) * alpha & 0xFF00FF00) >>> 8;
		return blendedForeground + (((background & 0xFF00) * inverseAlpha & 0xFF0000 | (background & 0xFF00FF) * inverseAlpha & 0xFF00FF00) >>> 8);
	}
	@OriginalMember(owner = "client!dk", name = "a", descriptor = "(IIII)I")
	public static int blendHsl(@OriginalArg(0) int fromHsl, @OriginalArg(1) int toHsl, @OriginalArg(3) int blend) {
		if (fromHsl == toHsl) {
			return fromHsl;
		}
		@Pc(13) int inverseBlend = 128 - blend;
		@Pc(32) int lightness = (toHsl & 0x7F) * blend + inverseBlend * (fromHsl & 0x7F) >> 7;
		@Pc(46) int saturation = (toHsl & 0x380) * blend + (fromHsl & 0x380) * inverseBlend >> 7;
		@Pc(60) int hue = (toHsl & 0xFC00) * blend + inverseBlend * (fromHsl & 0xFC00) >> 7;
		return hue & 0xFC00 | saturation & 0x380 | lightness & 0x7F;
	}

	@OriginalMember(owner = "client!dha", name = "a", descriptor = "(ZZB)V")
	public static void initColorTables(@OriginalArg(0) boolean initHsvTable, @OriginalArg(1) boolean initHslTable) {
		if (initHsvTable) {
			Chat.hsvToRgbUsers++;
			ColorUtil.initHsvToRgbTable();
		}
		if (initHslTable) {
			ColorUtil.hslToRgbUsers++;
			ColorUtil.initHslToRgbTable();
		}
	}
	@OriginalMember(owner = "client!hs", name = "k", descriptor = "I")
	public static int hslToRgbUsers = 0;
}
