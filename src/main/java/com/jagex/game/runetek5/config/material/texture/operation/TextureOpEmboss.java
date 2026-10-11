package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;

@OriginalClass("client!dda")
public final class TextureOpEmboss extends TextureOperation {

	@OriginalMember(owner = "client!dda", name = "O", descriptor = "I")
	private int lightElevation = 3216;

	@OriginalMember(owner = "client!dda", name = "Q", descriptor = "I")
	private int lightAzimuth = 3216;

	@OriginalMember(owner = "client!dda", name = "R", descriptor = "I")
	private int embossDepth = 4096;

	@OriginalMember(owner = "client!dda", name = "S", descriptor = "[I")
	private final int[] lightDirection = new int[3];

	@OriginalMember(owner = "client!dda", name = "<init>", descriptor = "()V")
	public TextureOpEmboss() {
		super(1, true);
	}

	@OriginalMember(owner = "client!dda", name = "f", descriptor = "(I)V")
	private void computeLightDirection() {
		@Pc(17) double cosElevation = Math.cos((double) ((float) this.lightElevation / 4096.0F));
		this.lightDirection[0] = (int) (cosElevation * Math.sin((double) ((float) this.lightAzimuth / 4096.0F)) * 4096.0D);
		this.lightDirection[1] = (int) (cosElevation * Math.cos((double) ((float) this.lightAzimuth / 4096.0F)) * 4096.0D);
		this.lightDirection[2] = (int) (Math.sin((double) ((float) this.lightElevation / 4096.0F)) * 4096.0D);
		@Pc(75) int xSquared = this.lightDirection[0] * this.lightDirection[0] >> 12;
		@Pc(87) int ySquared = this.lightDirection[1] * this.lightDirection[1] >> 12;
		@Pc(99) int zSquared = this.lightDirection[2] * this.lightDirection[2] >> 12;
		@Pc(112) int magnitude = (int) (Math.sqrt((double) (ySquared + xSquared + zSquared >> 12)) * 4096.0D);
		if (magnitude != 0) {
			this.lightDirection[1] = (this.lightDirection[1] << 12) / magnitude;
			this.lightDirection[0] = (this.lightDirection[0] << 12) / magnitude;
			this.lightDirection[2] = (this.lightDirection[2] << 12) / magnitude;
		}
	}

	@OriginalMember(owner = "client!dda", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(11) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(22) int scaledDepth = TextureRaster.scaledWidth * this.embossDepth >> 12;
			@Pc(32) int[] prevRow = this.getChildMonochromeOutput(TextureOperation.columnEnd & row - 1, 0);
			@Pc(38) int[] currentRow = this.getChildMonochromeOutput(row, 0);
			@Pc(50) int[] nextRow = this.getChildMonochromeOutput(TextureOperation.columnEnd & row + 1, 0);
			for (@Pc(52) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(66) int gradientY = scaledDepth * (nextRow[i] - prevRow[i]) >> 12;
				@Pc(87) int gradientX = scaledDepth * (currentRow[i - 1 & TextureRaster.widthMask] - currentRow[TextureRaster.widthMask & i + 1]) >> 12;
				@Pc(91) int absGradientX = gradientX >> 4;
				@Pc(95) int absGradientY = gradientY >> 4;
				if (absGradientX < 0) {
					absGradientX = -absGradientX;
				}
				if (absGradientY < 0) {
					absGradientY = -absGradientY;
				}
				if (absGradientX > 255) {
					absGradientX = 255;
				}
				if (absGradientY > 255) {
					absGradientY = 255;
				}
				@Pc(136) int normalScale = Component.normalizeTable[absGradientX + (absGradientY * (absGradientY + 1) >> 1)] & 0xFF;
				@Pc(142) int normalZ = normalScale * 4096 >> 8;
				@Pc(148) int normalX = normalScale * gradientX >> 8;
				@Pc(154) int normalY = gradientY * normalScale >> 8;
				@Pc(163) int dotY = normalY * this.lightDirection[1] >> 12;
				@Pc(172) int dotX = normalX * this.lightDirection[0] >> 12;
				@Pc(181) int dotZ = this.lightDirection[2] * normalZ >> 12;
				output[i] = dotZ + dotX + dotY;
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!dda", name = "e", descriptor = "(I)V")
	@Override
	public void postDecode() {
		this.computeLightDirection();
	}

	@OriginalMember(owner = "client!dda", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.embossDepth = packet.readUShort();
		} else if (opcode == 1) {
			this.lightAzimuth = packet.readUShort();
		} else if (opcode == 2) {
			this.lightElevation = packet.readUShort();
		}
	}
}
