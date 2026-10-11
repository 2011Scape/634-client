package com.jagex.game.runetek5.config.flu;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!hk")
public final class FluType {

	@OriginalMember(owner = "client!hk", name = "e", descriptor = "I")
	public int saturation;

	@OriginalMember(owner = "client!hk", name = "h", descriptor = "I")
	public int lightness;

	@OriginalMember(owner = "client!hk", name = "n", descriptor = "I")
	public int hue;

	@OriginalMember(owner = "client!hk", name = "p", descriptor = "I")
	public int hueMultiplier;

	@OriginalMember(owner = "client!hk", name = "d", descriptor = "I")
	public int texture = -1;

	@OriginalMember(owner = "client!hk", name = "i", descriptor = "I")
	public int scale = 512;

	@OriginalMember(owner = "client!hk", name = "j", descriptor = "I")
	private int rgb = 0;

	@OriginalMember(owner = "client!hk", name = "g", descriptor = "Z")
	public boolean occlude = true;

	@OriginalMember(owner = "client!hk", name = "o", descriptor = "Z")
	public boolean shadowed = true;

	@OriginalMember(owner = "client!hk", name = "a", descriptor = "(Lclient!cea;I)V")
	public void decode(@OriginalArg(0) Packet packet) {
		while (true) {
			@Pc(11) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(opcode, packet);
		}
	}

	@OriginalMember(owner = "client!hk", name = "a", descriptor = "(IILclient!cea;)V")
	private void decode(@OriginalArg(0) int opcode, @OriginalArg(2) Packet packet) {
		if (opcode == 1) {
			this.rgb = packet.readUMedium();
			this.computeHsl(this.rgb);
		} else if (opcode == 2) {
			this.texture = packet.readUShort();
			if (this.texture == 65535) {
				this.texture = -1;
			}
		} else if (opcode == 3) {
			this.scale = packet.readUShort() << 2;
		} else if (opcode == 4) {
			this.shadowed = false;
		} else if (opcode == 5) {
			this.occlude = false;
		}
	}

	@OriginalMember(owner = "client!hk", name = "a", descriptor = "(IB)V")
	private void computeHsl(@OriginalArg(0) int rgb) {
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
		@Pc(62) double saturation = 0.0D;
		@Pc(68) double lightness = (min + max) / 2.0D;
		if (max != min) {
			if (lightness < 0.5D) {
				saturation = (max - min) / (min + max);
			}
			if (red == max) {
				hue = (green - blue) / (max - min);
			} else if (max == green) {
				hue = (blue - red) / (max - min) + 2.0D;
			} else if (max == blue) {
				hue = (red - green) / (-min + max) + 4.0D;
			}
			if (lightness >= 0.5D) {
				saturation = (max - min) / ((2.0D - max) - min);
			}
		}
		this.saturation = (int) (saturation * 256.0D);
		hue /= 6.0D;
		this.lightness = (int) (lightness * 256.0D);
		if (this.saturation < 0) {
			this.saturation = 0;
		} else if (this.saturation > 255) {
			this.saturation = 255;
		}
		if (lightness > 0.5D) {
			this.hueMultiplier = (int) ((1.0D - lightness) * 512.0D * saturation);
		} else {
			this.hueMultiplier = (int) (lightness * 512.0D * saturation);
		}
		if (this.lightness < 0) {
			this.lightness = 0;
		} else if (this.lightness > 255) {
			this.lightness = 255;
		}
		if (this.hueMultiplier < 1) {
			this.hueMultiplier = 1;
		}
		this.hue = (int) (hue * (double) this.hueMultiplier);
	}
}
