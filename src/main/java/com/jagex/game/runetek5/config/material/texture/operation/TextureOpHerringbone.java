package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;

@OriginalClass("client!gd")
public final class TextureOpHerringbone extends TextureOperation {

	@OriginalMember(owner = "client!gd", name = "I", descriptor = "I")
	private int scaleY = 1;

	@OriginalMember(owner = "client!gd", name = "L", descriptor = "I")
	private int threshold = 204;

	@OriginalMember(owner = "client!gd", name = "M", descriptor = "I")
	private int scaleX = 1;

	@OriginalMember(owner = "client!gd", name = "<init>", descriptor = "()V")
	public TextureOpHerringbone() {
		super(0, true);
	}

	@OriginalMember(owner = "client!gd", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.scaleX = packet.readUByte();
		} else if (opcode == 1) {
			this.scaleY = packet.readUByte();
		} else if (opcode == 2) {
			this.threshold = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!gd", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(11) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			for (@Pc(17) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(23) int horizontalPos = EnvironmentLight.horizontalTable[i];
				@Pc(27) int verticalPos = TextureRaster.verticalTable[row];
				@Pc(34) int local34 = this.scaleX * horizontalPos >> 12;
				@Pc(41) int tileY = this.scaleY * verticalPos >> 12;
				@Pc(51) int offsetX = horizontalPos % (4096 / this.scaleX) * this.scaleX;
				@Pc(61) int offsetY = verticalPos % (4096 / this.scaleY) * this.scaleY;
				if (this.threshold > offsetY) {
					for (local34 -= tileY; local34 < 0; local34 += 4) {
					}
					while (local34 > 3) {
						local34 -= 4;
					}
					if (local34 != 1) {
						output[i] = 0;
						continue;
					}
					if (this.threshold > offsetX) {
						output[i] = 0;
						continue;
					}
				}
				if (this.threshold > offsetX) {
					for (local34 -= tileY; local34 < 0; local34 += 4) {
					}
					while (local34 > 3) {
						local34 -= 4;
					}
					if (local34 > 0) {
						output[i] = 0;
						continue;
					}
				}
				output[i] = 4096;
			}
		}
		return output;
	}
}
