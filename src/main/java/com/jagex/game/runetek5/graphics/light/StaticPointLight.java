package com.jagex.game.runetek5.graphics.light;

import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.ColorUtil;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpPerlinNoise;

@OriginalClass("client!ro")
public final class StaticPointLight {

	@OriginalMember(owner = "client!ro", name = "a", descriptor = "I")
	public int level;

	@OriginalMember(owner = "client!ro", name = "d", descriptor = "I")
	public int type;

	@OriginalMember(owner = "client!ro", name = "e", descriptor = "I")
	private int duration;

	@OriginalMember(owner = "client!ro", name = "g", descriptor = "Z")
	public boolean growsUpwards;

	@OriginalMember(owner = "client!ro", name = "h", descriptor = "I")
	private int surrounding;

	@OriginalMember(owner = "client!ro", name = "i", descriptor = "I")
	private int rotationOffset;

	@OriginalMember(owner = "client!ro", name = "j", descriptor = "I")
	private int effect;

	@OriginalMember(owner = "client!ro", name = "l", descriptor = "[S")
	public short[] ranges;

	@OriginalMember(owner = "client!ro", name = "m", descriptor = "I")
	private int ticker;

	@OriginalMember(owner = "client!ro", name = "o", descriptor = "Lclient!ab;")
	public PointLight pointLight;

	@OriginalMember(owner = "client!ro", name = "s", descriptor = "Z")
	public boolean growsDownwards;

	@OriginalMember(owner = "client!ro", name = "<init>", descriptor = "()V")
	private StaticPointLight() {
		if (StaticPointLight.flickerNoise == null) {
			StaticPointLight.initFlickerNoise();
		}
		this.updateConstants();
	}

	@OriginalMember(owner = "client!ro", name = "<init>", descriptor = "(Lclient!ha;Lclient!cea;I)V")
	public StaticPointLight(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) Packet packet, @OriginalArg(2) int sizeShift) {
		if (StaticPointLight.flickerNoise == null) {
			StaticPointLight.initFlickerNoise();
		}
		this.level = packet.readUByte();
		this.growsUpwards = (this.level & 0x8) != 0;
		this.growsDownwards = (this.level & 0x10) != 0;
		this.level &= 0x7;
		@Pc(47) int x = packet.readUShort() << sizeShift;
		@Pc(53) int z = packet.readUShort() << sizeShift;
		@Pc(59) int y = packet.readUShort() << sizeShift;
		@Pc(63) int radius = packet.readUByte();
		@Pc(69) int rangeCount = radius * 2 + 1;
		this.ranges = new short[rangeCount];
		@Pc(86) int local86;
		for (@Pc(75) int i = 0; i < this.ranges.length; i++) {
			@Pc(82) short packedRange = (short) packet.readUShort();
			int rangeStart = packedRange >>> 8;
			if (rangeStart >= rangeCount) {
				rangeStart = rangeCount - 1;
			}
			@Pc(101) int rangeLength = packedRange & 0xFF;
			if (rangeCount - rangeStart < rangeLength) {
				rangeLength = rangeCount - rangeStart;
			}
			this.ranges[i] = (short) (rangeLength | rangeStart << 8);
		}
		radius = (radius << Scene.tileUnits) + Scene.halfTileOffset;
		@Pc(155) int colour = ColorUtil.hslToRgb == null ? ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(packet.readUShort()) & 0xFFFF] : ColorUtil.hslToRgb[packet.readUShort()];
		int packedType = packet.readUByte();
		this.rotationOffset = (packedType & 0xE0) << 3;
		this.type = packedType & 0x1F;
		if (this.type != 31) {
			this.updateConstants();
		}
		this.generateLight(y, toolkit, z, x, colour, radius);
	}

	@OriginalMember(owner = "client!ro", name = "a", descriptor = "(IBIII)V")
	public void setType(@OriginalArg(0) int surrounding, @OriginalArg(2) int duration, @OriginalArg(3) int effect, @OriginalArg(4) int ticker) {
		this.duration = duration;
		this.surrounding = surrounding;
		this.ticker = ticker;
		this.effect = effect;
	}

	@OriginalMember(owner = "client!ro", name = "a", descriptor = "(ILclient!ha;IIIII)V")
	private void generateLight(@OriginalArg(0) int y, @OriginalArg(1) Toolkit toolkit, @OriginalArg(2) int z, @OriginalArg(3) int x, @OriginalArg(5) int colour, @OriginalArg(6) int radius) {
		this.pointLight = toolkit.createPointLight(x, y, z, radius, colour, 1.0F);
	}

	@OriginalMember(owner = "client!ro", name = "a", descriptor = "(I)V")
	private void updateConstants() {
		@Pc(4) int type = this.type;
		if (type == 2) {
			this.ticker = 2048;
			this.surrounding = 0;
			this.duration = 2048;
			this.effect = 1;
		} else if (type == 3) {
			this.surrounding = 0;
			this.ticker = 2048;
			this.effect = 1;
			this.duration = 4096;
		} else if (type == 4) {
			this.surrounding = 0;
			this.effect = 4;
			this.ticker = 2048;
			this.duration = 2048;
		} else if (type == 5) {
			this.duration = 8192;
			this.effect = 4;
			this.ticker = 2048;
			this.surrounding = 0;
		} else if (type == 12) {
			this.duration = 2048;
			this.effect = 2;
			this.surrounding = 0;
			this.ticker = 2048;
		} else if (type == 13) {
			this.ticker = 2048;
			this.surrounding = 0;
			this.effect = 2;
			this.duration = 8192;
		} else if (type == 10) {
			this.ticker = 512;
			this.effect = 3;
			this.surrounding = 1536;
			this.duration = 2048;
		} else if (type == 11) {
			this.effect = 3;
			this.ticker = 512;
			this.surrounding = 1536;
			this.duration = 4096;
		} else if (type == 6) {
			this.surrounding = 1280;
			this.effect = 3;
			this.ticker = 768;
			this.duration = 2048;
		} else if (type == 7) {
			this.surrounding = 1280;
			this.effect = 3;
			this.duration = 4096;
			this.ticker = 768;
		} else if (type == 8) {
			this.surrounding = 1024;
			this.duration = 2048;
			this.ticker = 1024;
			this.effect = 3;
		} else if (type == 9) {
			this.ticker = 1024;
			this.duration = 4096;
			this.effect = 3;
			this.surrounding = 1024;
		} else if (type == 14) {
			this.effect = 1;
			this.surrounding = 1280;
			this.duration = 2048;
			this.ticker = 768;
		} else if (type == 15) {
			this.duration = 4096;
			this.ticker = 512;
			this.effect = 1;
			this.surrounding = 1536;
		} else if (type == 16) {
			this.duration = 8192;
			this.effect = 1;
			this.ticker = 256;
			this.surrounding = 1792;
		} else {
			this.duration = 2048;
			this.ticker = 2048;
			this.surrounding = 0;
			this.effect = 0;
		}
	}

	@OriginalMember(owner = "client!ro", name = "a", descriptor = "(IZI)V")
	public void applyEffect(@OriginalArg(1) boolean disableFlickering, @OriginalArg(2) int cycle) {
		@Pc(17) int flicker;
		if (disableFlickering) {
			flicker = 2048;
		} else {
			@Pc(31) int phase = this.duration * cycle / 50 + this.rotationOffset & 0x7FF;
			@Pc(34) int effectType = this.effect;
			if (effectType == 1) {
				flicker = (TextureCombineSource.SINE[phase << 3] >> 4) + 1024;
			} else if (effectType == 3) {
				flicker = StaticPointLight.flickerNoise[phase] >> 1;
			} else if (effectType == 4) {
				flicker = phase >> 10 << 11;
			} else if (effectType == 2) {
				flicker = phase;
			} else if (effectType == 5) {
				flicker = (phase >= 1024 ? 2048 - phase : phase) << 1;
			} else {
				flicker = 2048;
			}
		}
		this.pointLight.setIntensity((float) ((this.ticker * flicker >> 11) + this.surrounding) / 2048.0F);
	}
	@OriginalMember(owner = "client!vw", name = "h", descriptor = "[I")
	public static int[] flickerNoise;
	@OriginalMember(owner = "client!oda", name = "c", descriptor = "(B)V")
	public static void initFlickerNoise() {
		StaticPointLight.flickerNoise = TextureOpPerlinNoise.generateNoiseRow(0.4F);
	}
}
