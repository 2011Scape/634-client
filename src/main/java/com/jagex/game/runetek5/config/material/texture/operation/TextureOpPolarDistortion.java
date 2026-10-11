package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;

@OriginalClass("client!hr")
public final class TextureOpPolarDistortion extends TextureOperation {

	@OriginalMember(owner = "client!hr", name = "K", descriptor = "I")
	private int distortionStrength = 32768;

	@OriginalMember(owner = "client!hr", name = "<init>", descriptor = "()V")
	public TextureOpPolarDistortion() {
		super(3, false);
	}

	@OriginalMember(owner = "client!hr", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int y) {
		@Pc(11) int[] output = super.monochromeImageCache.get(y);
		if (super.monochromeImageCache.dirty) {
			@Pc(31) int[] angles = this.getChildMonochromeOutput(y, 1);
			@Pc(39) int[] lengths = this.getChildMonochromeOutput(y, 2);
			for (@Pc(41) int x = 0; x < EnvironmentLight.textureOpWidth; x++) {
				@Pc(51) int angle = angles[x] >> 4 & 0xFF;
				@Pc(60) int length = this.distortionStrength * lengths[x] >> 12;
				@Pc(68) int offsetX = TextureRaster.PALETTE_COS[angle] * length >> 12;
				@Pc(76) int offsetY = TextureRaster.PALETTE_SIN[angle] * length >> 12;
				@Pc(85) int sampleX = x + (offsetX >> 12) & TextureRaster.widthMask;
				@Pc(94) int sampleY = y + (offsetY >> 12) & TextureOperation.columnEnd;
				@Pc(100) int[] source = this.getChildMonochromeOutput(sampleY, 0);
				output[x] = source[sampleX];
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!hr", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.distortionStrength = packet.readUShort() << 4;
		} else if (opcode == 1) {
			super.monochrome = packet.readUByte() == 1;
		}
	}

	@OriginalMember(owner = "client!hr", name = "e", descriptor = "(I)V")
	@Override
	public void postDecode() {
		TextureRaster.calculateTrigTables();
	}

	@OriginalMember(owner = "client!hr", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int y) {
		@Pc(13) int[][] output = super.colorImageCache.get(y);
		if (super.colorImageCache.dirty) {
			@Pc(28) int[] angles = this.getChildMonochromeOutput(y, 1);
			@Pc(34) int[] lengths = this.getChildMonochromeOutput(y, 2);
			@Pc(38) int[] outputRed = output[0];
			@Pc(42) int[] outputGreen = output[1];
			@Pc(46) int[] outputBlue = output[2];
			for (@Pc(48) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(60) int angle = angles[i] * 255 >> 12 & 0xFF;
				@Pc(69) int length = this.distortionStrength * lengths[i] >> 12;
				@Pc(77) int offsetX = length * TextureRaster.PALETTE_COS[angle] >> 12;
				@Pc(85) int offsetY = length * TextureRaster.PALETTE_SIN[angle] >> 12;
				@Pc(93) int sampleX = i + (offsetX >> 12) & TextureRaster.widthMask;
				@Pc(101) int sampleY = (offsetY >> 12) + y & TextureOperation.columnEnd;
				@Pc(107) int[][] source = this.getChildColorOutput(sampleY, 0);
				outputRed[i] = source[0][sampleX];
				outputGreen[i] = source[1][sampleX];
				outputBlue[i] = source[2][sampleX];
			}
		}
		return output;
	}
}
