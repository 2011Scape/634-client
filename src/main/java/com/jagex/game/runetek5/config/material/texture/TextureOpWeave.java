package com.jagex.game.runetek5.config.material.texture;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!oca")
public final class TextureOpWeave extends TextureOperation {

	@OriginalMember(owner = "client!oca", name = "K", descriptor = "I")
	private int threadWidth = 585;

	@OriginalMember(owner = "client!oca", name = "<init>", descriptor = "()V")
	public TextureOpWeave() {
		super(0, true);
	}

	@OriginalMember(owner = "client!oca", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(11) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(24) int y = TextureRaster.verticalTable[row];
			for (@Pc(26) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(32) int x = EnvironmentLight.horizontalTable[i];
				@Pc(76) int distance;
				if (x > this.threadWidth && x < 4096 - this.threadWidth && 2048 - this.threadWidth < y && this.threadWidth + 2048 > y) {
					distance = 2048 - x;
					distance = distance < 0 ? -distance : distance;
					distance <<= 0xC;
					distance /= 2048 - this.threadWidth;
					output[i] = 4096 - distance;
				} else if (x > 2048 - this.threadWidth && x < this.threadWidth + 2048) {
					distance = y - 2048;
					distance = distance < 0 ? -distance : distance;
					distance -= this.threadWidth;
					distance <<= 0xC;
					output[i] = distance / (2048 - this.threadWidth);
				} else if (this.threadWidth > y || y > 4096 - this.threadWidth) {
					distance = x - 2048;
					@Pc(176) int absDistance = distance >= 0 ? distance : -distance;
					@Pc(181) int edgeDistance = absDistance - this.threadWidth;
					@Pc(185) int scaledDistance = edgeDistance << 12;
					output[i] = scaledDistance / (2048 - this.threadWidth);
				} else if (x >= this.threadWidth && x <= 4096 - this.threadWidth) {
					output[i] = 0;
				} else {
					distance = 2048 - y;
					distance = distance < 0 ? -distance : distance;
					distance <<= 0xC;
					distance /= 2048 - this.threadWidth;
					output[i] = 4096 - distance;
				}
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!oca", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.threadWidth = packet.readUShort();
		}
	}
}
