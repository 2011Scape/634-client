package com.jagex.game.runetek5.config.flo;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.flo.FloTypeList;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.MaterialType;
import com.jagex.game.runetek5.graphics.Toolkit;

@OriginalClass("client!vd")
public final class FloType {

	@OriginalMember(owner = "client!vd", name = "e", descriptor = "Lclient!cb;")
	public FloTypeList floTypeList;

	@OriginalMember(owner = "client!vd", name = "p", descriptor = "I")
	public int overlayId;

	@OriginalMember(owner = "client!vd", name = "a", descriptor = "I")
	public int waterFogDepth = 64;

	@OriginalMember(owner = "client!vd", name = "d", descriptor = "Z")
	public boolean occlude = true;

	@OriginalMember(owner = "client!vd", name = "g", descriptor = "I")
	public int slot = 8;

	@OriginalMember(owner = "client!vd", name = "f", descriptor = "I")
	public int tileColorHsl = 0;

	@OriginalMember(owner = "client!vd", name = "i", descriptor = "Z")
	public boolean shadowed = true;

	@OriginalMember(owner = "client!vd", name = "c", descriptor = "Z")
	public boolean blendsWithUnderlay = false;

	@OriginalMember(owner = "client!vd", name = "l", descriptor = "I")
	public int minimapColorHsl = -1;

	@OriginalMember(owner = "client!vd", name = "m", descriptor = "I")
	public int textureScale = 512;

	@OriginalMember(owner = "client!vd", name = "b", descriptor = "I")
	public int waterColor = 1190717;

	@OriginalMember(owner = "client!vd", name = "r", descriptor = "I")
	public int texture = -1;

	@OriginalMember(owner = "client!vd", name = "q", descriptor = "I")
	public int waterIntensity = 127;

	@OriginalMember(owner = "client!vd", name = "a", descriptor = "(I)V")
	public void postDecode() {
		this.slot = this.slot << 8 | this.overlayId;
	}

	@OriginalMember(owner = "client!vd", name = "a", descriptor = "(ILclient!cea;I)V")
	private void decode(@OriginalArg(1) Packet packet, @OriginalArg(2) int opcode) {
		if (opcode == 1) {
			this.tileColorHsl = FloType.rgbToHslOrTransparent(packet.readUMedium());
		} else if (opcode == 2) {
			this.texture = packet.readUByte();
			return;
		} else if (opcode == 3) {
			this.texture = packet.readUShort();
			if (this.texture == 65535) {
				this.texture = -1;
				return;
			}
		} else if (opcode == 5) {
			this.occlude = false;
			return;
		} else if (opcode == 7) {
			this.minimapColorHsl = FloType.rgbToHslOrTransparent(packet.readUMedium());
			return;
		} else if (opcode == 8) {
			this.floTypeList.lastLoadedOverlayId = this.overlayId;
			return;
		} else if (opcode == 9) {
			this.textureScale = packet.readUShort() << 2;
			return;
		} else if (opcode == 10) {
			this.shadowed = false;
			return;
		} else if (opcode == 11) {
			this.slot = packet.readUByte();
			return;
		} else if (opcode == 12) {
			this.blendsWithUnderlay = true;
			return;
		} else if (opcode == 13) {
			this.waterColor = packet.readUMedium();
			return;
		} else {
			if (opcode == 14) {
				this.waterFogDepth = packet.readUByte() << 2;
			} else if (opcode == 16) {
				this.waterIntensity = packet.readUByte();
				return;
			}
			return;
		}
	}

	@OriginalMember(owner = "client!vd", name = "a", descriptor = "(ZLclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(11) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}
	@OriginalMember(owner = "client!aa", name = "a", descriptor = "(II)I")
	public static int rgbToHsl(@OriginalArg(1) int rgb) {
		@Pc(14) double red = (double) (rgb >> 16 & 0xFF) / 256.0D;
		@Pc(23) double green = (double) (rgb >> 8 & 0xFF) / 256.0D;
		@Pc(30) double blue = (double) (rgb & 0xFF) / 256.0D;
		@Pc(32) double min = red;
		if (green < red) {
			min = green;
		}
		if (blue < min) {
			min = blue;
		}
		@Pc(46) double max = red;
		if (green > red) {
			max = green;
		}
		if (blue > max) {
			max = blue;
		}
		@Pc(60) double hue = 0.0D;
		@Pc(67) double saturation = 0.0D;
		@Pc(73) double lightness = (min + max) / 2.0D;
		if (max != min) {
			if (lightness < 0.5D) {
				saturation = (max - min) / (min + max);
			}
			if (max == red) {
				hue = (green - blue) / (-min + max);
			} else if (green == max) {
				hue = (blue - red) / (max - min) + 2.0D;
			} else if (blue == max) {
				hue = (red - green) / (max - min) + 4.0D;
			}
			if (lightness >= 0.5D) {
				saturation = (max - min) / (2.0D - min - max);
			}
		}
		hue /= 6.0D;
		@Pc(164) int scaledHue = (int) (hue * 256.0D);
		@Pc(169) int scaledSaturation = (int) (saturation * 256.0D);
		@Pc(174) int scaledLightness = (int) (lightness * 256.0D);
		if (scaledSaturation < 0) {
			scaledSaturation = 0;
		} else if (scaledSaturation > 255) {
			scaledSaturation = 255;
		}
		if (scaledLightness < 0) {
			scaledLightness = 0;
		} else if (scaledLightness > 255) {
			scaledLightness = 255;
		}
		if (scaledLightness > 243) {
			scaledSaturation >>= 0x4;
		} else if (scaledLightness > 217) {
			scaledSaturation >>= 0x3;
		} else if (scaledLightness > 192) {
			scaledSaturation >>= 0x2;
		} else if (scaledLightness > 179) {
			scaledSaturation >>= 0x1;
		}
		return (scaledLightness >> 1) + ((scaledHue & 0xFF) >> 2 << 10) + (scaledSaturation >> 5 << 7);
	}
	@OriginalMember(owner = "client!cfa", name = "e", descriptor = "(II)I")
	public static int rgbToHslOrTransparent(@OriginalArg(1) int rgb) {
		return rgb == 16711935 ? -1 : FloType.rgbToHsl(rgb);
	}
	@OriginalMember(owner = "client!gw", name = "a", descriptor = "(Lclient!ha;ILclient!vd;)I")
	public static int getMinimapColorHsl(@OriginalArg(0) Toolkit toolkit, @OriginalArg(2) FloType spotAnimType) {
		if (spotAnimType.minimapColorHsl != -1) {
			return spotAnimType.minimapColorHsl;
		}
		if (spotAnimType.texture != -1) {
			@Pc(19) MaterialType textureMetrics = toolkit.materialProvider.getMetrics(spotAnimType.texture);
			if (!textureMetrics.detailsOnly) {
				return textureMetrics.colorHsl;
			}
		}
		return spotAnimType.tileColorHsl;
	}
}
