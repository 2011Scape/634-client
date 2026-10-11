package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;

@OriginalClass("client!cfa")
public final class TextureOpColorEdgeDetector extends TextureOperation {

	@OriginalMember(owner = "client!cfa", name = "P", descriptor = "I")
	private int strength = 4096;

	@OriginalMember(owner = "client!cfa", name = "Q", descriptor = "Z")
	private boolean normalized = true;

	@OriginalMember(owner = "client!cfa", name = "<init>", descriptor = "()V")
	public TextureOpColorEdgeDetector() {
		super(1, false);
	}

	@OriginalMember(owner = "client!cfa", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.strength = packet.readUShort();
		} else if (opcode == 1) {
			this.normalized = packet.readUByte() == 1;
		}
	}

	@OriginalMember(owner = "client!cfa", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int row) {
		@Pc(11) int[][] output = super.colorImageCache.get(row);
		if (super.colorImageCache.dirty) {
			@Pc(32) int[] prevRow = this.getChildMonochromeOutput(row - 1 & TextureOperation.columnEnd, 0);
			@Pc(40) int[] currentRow = this.getChildMonochromeOutput(row, 0);
			@Pc(50) int[] nextRow = this.getChildMonochromeOutput(TextureOperation.columnEnd & row + 1, 0);
			@Pc(54) int[] outputRed = output[0];
			@Pc(58) int[] outputGreen = output[1];
			@Pc(62) int[] outputBlue = output[2];
			for (@Pc(64) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(78) int deltaY = (nextRow[i] - prevRow[i]) * this.strength;
				@Pc(97) int deltaX = this.strength * (currentRow[i + 1 & TextureRaster.widthMask] - currentRow[TextureRaster.widthMask & i - 1]);
				@Pc(101) int gradientX = deltaX >> 12;
				@Pc(105) int gradientY = deltaY >> 12;
				@Pc(111) int gradientXSquared = gradientX * gradientX >> 12;
				@Pc(117) int gradientYSquared = gradientY * gradientY >> 12;
				@Pc(132) int magnitude = (int) (Math.sqrt((double) ((float) (gradientXSquared + gradientYSquared + 4096) / 4096.0F)) * 4096.0D);
				@Pc(138) int normalX;
				@Pc(142) int normalY;
				@Pc(146) int normalZ;
				if (magnitude == 0) {
					normalY = 0;
					normalX = 0;
					normalZ = 0;
				} else {
					normalX = deltaX / magnitude;
					normalY = deltaY / magnitude;
					normalZ = 16777216 / magnitude;
				}
				if (this.normalized) {
					normalZ = (normalZ >> 1) + 2048;
					normalX = (normalX >> 1) + 2048;
					normalY = (normalY >> 1) + 2048;
				}
				outputRed[i] = normalX;
				outputGreen[i] = normalY;
				outputBlue[i] = normalZ;
			}
		}
		return output;
	}
}
