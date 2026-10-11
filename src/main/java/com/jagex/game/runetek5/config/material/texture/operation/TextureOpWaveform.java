package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;

@OriginalClass("client!dn")
public final class TextureOpWaveform extends TextureOperation {

	@OriginalMember(owner = "client!dn", name = "J", descriptor = "I")
	private int waveType = 0;

	@OriginalMember(owner = "client!dn", name = "L", descriptor = "I")
	private int frequency = 1;

	@OriginalMember(owner = "client!dn", name = "N", descriptor = "I")
	private int waveShape = 0;

	@OriginalMember(owner = "client!dn", name = "<init>", descriptor = "()V")
	public TextureOpWaveform() {
		super(0, true);
	}

	@OriginalMember(owner = "client!dn", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.waveType = packet.readUByte();
		} else if (opcode == 1) {
			this.waveShape = packet.readUByte();
		} else if (opcode == 3) {
			this.frequency = packet.readUByte();
		}
	}

	@OriginalMember(owner = "client!dn", name = "e", descriptor = "(I)V")
	@Override
	public void postDecode() {
		TextureRaster.calculateTrigTables();
	}

	@OriginalMember(owner = "client!dn", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(11) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(25) int rowCoord = TextureRaster.verticalTable[row];
			@Pc(31) int centreOffsetY = rowCoord - 2048 >> 1;
			for (@Pc(33) int x = 0; x < EnvironmentLight.textureOpWidth; x++) {
				@Pc(39) int columnCoord = EnvironmentLight.horizontalTable[x];
				@Pc(45) int centreOffsetX = columnCoord - 2048 >> 1;
				@Pc(59) int value;
				if (this.waveType == 0) {
					value = this.frequency * (columnCoord - rowCoord);
				} else {
					@Pc(71) int squareDistance = centreOffsetX * centreOffsetX + centreOffsetY * centreOffsetY >> 12;
					value = (int) (Math.sqrt((double) ((float) squareDistance / 4096.0F)) * 4096.0D);
					value = (int) ((double) (this.frequency * value) * 3.141592653589793D);
				}
				value -= value & 0xFFFFF000;
				if (this.waveShape == 0) {
					value = TextureRaster.PALETTE_SIN[value >> 4 & 0xFF] + 4096 >> 1;
				} else if (this.waveShape == 2) {
					value -= 2048;
					if (value < 0) {
						value = -value;
					}
					value = 2048 - value << 1;
				}
				output[x] = value;
			}
		}
		return output;
	}
}
