package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;

@OriginalClass("client!kj")
public final class TextureOpMonochromeEdgeDetector extends TextureOperation {

	@OriginalMember(owner = "client!kj", name = "J", descriptor = "I")
	private int strength = 4096;

	@OriginalMember(owner = "client!kj", name = "<init>", descriptor = "()V")
	public TextureOpMonochromeEdgeDetector() {
		super(1, true);
	}

	@OriginalMember(owner = "client!kj", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.strength = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!kj", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int y) {
		@Pc(13) int[] output = super.monochromeImageCache.get(y);
		if (super.monochromeImageCache.dirty) {
			@Pc(27) int[] above = this.getChildMonochromeOutput(TextureOperation.columnEnd & y - 1, 0);
			@Pc(33) int[] source = this.getChildMonochromeOutput(y, 0);
			@Pc(43) int[] below = this.getChildMonochromeOutput(y + 1 & TextureOperation.columnEnd, 0);
			for (@Pc(45) int x = 0; x < EnvironmentLight.textureOpWidth; x++) {
				@Pc(59) int scaledGradientY = this.strength * (below[x] - above[x]);
				@Pc(79) int scaledGradientX = this.strength * (source[TextureRaster.widthMask & x + 1] - source[TextureRaster.widthMask & x - 1]);
				@Pc(83) int gradientX = scaledGradientX >> 12;
				@Pc(87) int gradientY = scaledGradientY >> 12;
				@Pc(93) int squareX = gradientX * gradientX >> 12;
				@Pc(99) int squareY = gradientY * gradientY >> 12;
				@Pc(113) int normalLength = (int) (Math.sqrt((double) ((float) (squareY + squareX + 4096) / 4096.0F)) * 4096.0D);
				@Pc(121) int flatness = normalLength == 0 ? 0 : 16777216 / normalLength;
				output[x] = 4096 - flatness;
			}
		}
		return output;
	}
}
