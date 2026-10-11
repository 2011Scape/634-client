package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;

@OriginalClass("client!rp")
public final class TextureOpDiagonalWaves extends TextureOperation {

	@OriginalMember(owner = "client!rp", name = "Z", descriptor = "[J")
	public static final long[] CRC64_TABLE = new long[256];

	@OriginalMember(owner = "client!rp", name = "I", descriptor = "I")
	private int pattern2OffsetX = 0;

	@OriginalMember(owner = "client!rp", name = "L", descriptor = "I")
	private int pattern1OffsetX = 2048;

	@OriginalMember(owner = "client!rp", name = "S", descriptor = "I")
	private int waveAmplitude = 4096;

	@OriginalMember(owner = "client!rp", name = "U", descriptor = "I")
	private int pattern1OffsetY = 0;

	@OriginalMember(owner = "client!rp", name = "V", descriptor = "I")
	private int waveFrequency = 12288;

	@OriginalMember(owner = "client!rp", name = "T", descriptor = "I")
	private int waveDecay = 8192;

	@OriginalMember(owner = "client!rp", name = "X", descriptor = "I")
	private int pattern2OffsetY = 2048;

	static {
		for (@Pc(15) int i = 0; i < 256; i++) {
			@Pc(19) long crc = (long) i;
			for (@Pc(21) int bit = 0; bit < 8; bit++) {
				if ((crc & 0x1L) == 1L) {
					crc = crc >>> 1 ^ 0xC96C5795D7870F42L;
				} else {
					crc >>>= 0x1;
				}
			}
			CRC64_TABLE[i] = crc;
		}
	}

	@OriginalMember(owner = "client!rp", name = "<init>", descriptor = "()V")
	public TextureOpDiagonalWaves() {
		super(0, true);
	}

	@OriginalMember(owner = "client!rp", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(11) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(30) int y = TextureRaster.verticalTable[row] - 2048;
			for (@Pc(32) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(40) int x = EnvironmentLight.horizontalTable[i] - 2048;
				@Pc(46) int offsetX1 = x + this.pattern1OffsetX;
				@Pc(57) int wrappedX1 = offsetX1 < -2048 ? offsetX1 + 4096 : offsetX1;
				@Pc(66) int x1 = wrappedX1 <= 2048 ? wrappedX1 : wrappedX1 - 4096;
				@Pc(71) int offsetY1 = this.pattern1OffsetY + y;
				@Pc(80) int wrappedY1 = offsetY1 < -2048 ? offsetY1 + 4096 : offsetY1;
				@Pc(91) int y1 = wrappedY1 <= 2048 ? wrappedY1 : wrappedY1 - 4096;
				@Pc(97) int offsetX2 = x + this.pattern2OffsetX;
				@Pc(106) int wrappedX2 = offsetX2 < -2048 ? offsetX2 + 4096 : offsetX2;
				@Pc(115) int x2 = wrappedX2 > 2048 ? wrappedX2 - 4096 : wrappedX2;
				@Pc(120) int offsetY2 = this.pattern2OffsetY + y;
				@Pc(129) int wrappedY2 = offsetY2 < -2048 ? offsetY2 + 4096 : offsetY2;
				@Pc(138) int y2 = wrappedY2 > 2048 ? wrappedY2 - 4096 : wrappedY2;
				output[i] = this.testDiagonalWave(y1, x1) || this.testAntiDiagonalWave(y2, x2) ? 4096 : 0;
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!rp", name = "e", descriptor = "(I)V")
	@Override
	public void postDecode() {
		TextureRaster.calculateTrigTables();
	}

	@OriginalMember(owner = "client!rp", name = "d", descriptor = "(III)Z")
	private boolean testAntiDiagonalWave(@OriginalArg(0) int y, @OriginalArg(2) int x) {
		@Pc(12) int phase = (x + y) * this.waveFrequency >> 12;
		@Pc(22) int wave = TextureRaster.PALETTE_COS[phase * 255 >> 12 & 0xFF];
		@Pc(35) int normalized = (wave << 12) / this.waveFrequency;
		@Pc(42) int decayed = (normalized << 12) / this.waveDecay;
		@Pc(49) int envelope = decayed * this.waveAmplitude >> 12;
		return envelope > y - x && -envelope < y - x;
	}

	@OriginalMember(owner = "client!rp", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.pattern1OffsetX = packet.readUShort();
		} else if (opcode == 1) {
			this.pattern1OffsetY = packet.readUShort();
		} else if (opcode == 2) {
			this.pattern2OffsetX = packet.readUShort();
		} else if (opcode == 3) {
			this.pattern2OffsetY = packet.readUShort();
		} else if (opcode == 4) {
			this.waveFrequency = packet.readUShort();
		} else if (opcode == 5) {
			this.waveAmplitude = packet.readUShort();
		} else if (opcode == 6) {
			this.waveDecay = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!rp", name = "e", descriptor = "(III)Z")
	private boolean testDiagonalWave(@OriginalArg(0) int y, @OriginalArg(2) int x) {
		@Pc(17) int phase = this.waveFrequency * (y - x) >> 12;
		@Pc(27) int wave = TextureRaster.PALETTE_COS[phase * 255 >> 12 & 0xFF];
		@Pc(34) int normalized = (wave << 12) / this.waveFrequency;
		@Pc(41) int decayed = (normalized << 12) / this.waveDecay;
		@Pc(48) int envelope = this.waveAmplitude * decayed >> 12;
		return x + y < envelope && y + x > -envelope;
	}
}
