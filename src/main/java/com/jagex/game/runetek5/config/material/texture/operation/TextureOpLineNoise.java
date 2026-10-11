package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import java.util.Random;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!v")
public final class TextureOpLineNoise extends TextureOperation {

	@OriginalMember(owner = "client!v", name = "M", descriptor = "I")
	private int angleSpread = 4096;

	@OriginalMember(owner = "client!v", name = "Q", descriptor = "I")
	private int lineLength = 16;

	@OriginalMember(owner = "client!v", name = "R", descriptor = "I")
	private int angleOffset = 0;

	@OriginalMember(owner = "client!v", name = "K", descriptor = "I")
	private int lineCount = 2000;

	@OriginalMember(owner = "client!v", name = "P", descriptor = "I")
	private int randomSeed = 0;

	@OriginalMember(owner = "client!v", name = "<init>", descriptor = "()V")
	public TextureOpLineNoise() {
		super(0, true);
	}

	@OriginalMember(owner = "client!v", name = "e", descriptor = "(I)V")
	@Override
	public void postDecode() {
		TextureRaster.calculateTrigTables();
	}

	@OriginalMember(owner = "client!v", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.randomSeed = packet.readUByte();
		} else if (opcode == 1) {
			this.lineCount = packet.readUShort();
		} else if (opcode == 2) {
			this.lineLength = packet.readUByte();
		} else if (opcode == 3) {
			this.angleOffset = packet.readUShort();
		} else if (opcode == 4) {
			this.angleSpread = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!v", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(17) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(26) int halfSpread = this.angleSpread >> 1;
			@Pc(31) int[][] pixelRows = super.monochromeImageCache.getAllPaletteData();
			@Pc(38) Random random = new Random((long) this.randomSeed);
			for (@Pc(40) int i = 0; i < this.lineCount; i++) {
				@Pc(63) int angle = this.angleSpread > 0 ? this.angleOffset + Utils.randomInt(this.angleSpread, random) - halfSpread : this.angleOffset;
				@Pc(69) int angleIndex = angle >> 4 & 0xFF;
				@Pc(74) int startX = Utils.randomInt(EnvironmentLight.textureOpWidth, random);
				@Pc(79) int startY = Utils.randomInt(TextureOperation.currentHeight, random);
				@Pc(91) int endX = startX + (this.lineLength * TextureRaster.PALETTE_COS[angleIndex] >> 12);
				@Pc(102) int endY = (TextureRaster.PALETTE_SIN[angleIndex] * this.lineLength >> 12) + startY;
				@Pc(107) int lineDeltaY = endY - startY;
				@Pc(112) int lineDeltaX = endX - startX;
				if (lineDeltaX != 0 || lineDeltaY != 0) {
					if (lineDeltaY < 0) {
						lineDeltaY = -lineDeltaY;
					}
					if (lineDeltaX < 0) {
						lineDeltaX = -lineDeltaX;
					}
					@Pc(139) boolean steep = lineDeltaX < lineDeltaY;
					@Pc(143) int local143;
					@Pc(145) int local145;
					if (steep) {
						int savedStartX = startX;
						int savedEndX = endX;
						startX = startY;
						startY = savedStartX;
						endX = endY;
						endY = savedEndX;
					}
					if (startX > endX) {
						int savedStartX = startX;
						startX = endX;
						int savedStartY = startY;
						endX = savedStartX;
						startY = endY;
						endY = savedStartY;
					}
					int currentY = startY;
					int deltaX = endX - startX;
					@Pc(184) int deltaY = endY - startY;
					@Pc(189) int error = -deltaX / 2;
					@Pc(193) int brightnessStep = 2048 / deltaX;
					@Pc(202) int baseBrightness = 1024 - (Utils.randomInt(4096, random) >> 2);
					if (deltaY < 0) {
						deltaY = -deltaY;
					}
					@Pc(218) int yStep = startY >= endY ? -1 : 1;
					for (@Pc(220) int j = startX; j < endX; j++) {
						@Pc(232) int brightness = (j - startX) * brightnessStep + baseBrightness + 1024;
						@Pc(236) int wrappedX = TextureRaster.widthMask & j;
						@Pc(240) int wrappedY = currentY & TextureOperation.columnEnd;
						if (steep) {
							pixelRows[wrappedY][wrappedX] = brightness;
						} else {
							pixelRows[wrappedX][wrappedY] = brightness;
						}
						error += deltaY;
						if (error > 0) {
							currentY -= -yStep;
							error += -deltaX;
						}
					}
				}
			}
		}
		return output;
	}
}
