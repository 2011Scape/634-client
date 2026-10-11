package com.jagex.game.runetek5.config.material.texture;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!wj")
public final class TextureOpKaleidoscope extends TextureOperation {

	@OriginalMember(owner = "client!wj", name = "<init>", descriptor = "()V")
	public TextureOpKaleidoscope() {
		super(1, false);
	}

	@OriginalMember(owner = "client!wj", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int row) {
		@Pc(11) int[][] output = super.colorImageCache.get(row);
		if (super.colorImageCache.dirty) {
			@Pc(24) int[] outputRed = output[0];
			@Pc(28) int[] outputGreen = output[1];
			@Pc(32) int[] outputBlue = output[2];
			for (@Pc(34) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				this.reflect(i, row);
				@Pc(47) int[][] input = this.getChildColorOutput(TextureOpKaleidoscope.mirroredY, 0);
				outputRed[i] = input[0][TextureOpKaleidoscope.mirroredX];
				outputGreen[i] = input[1][TextureOpKaleidoscope.mirroredX];
				outputBlue[i] = input[2][TextureOpKaleidoscope.mirroredX];
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!wj", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(16) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			for (@Pc(22) int x = 0; x < EnvironmentLight.textureOpWidth; x++) {
				this.reflect(x, row);
				@Pc(37) int[] source = this.getChildMonochromeOutput(TextureOpKaleidoscope.mirroredY, 0);
				output[x] = source[TextureOpKaleidoscope.mirroredX];
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!wj", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			super.monochrome = packet.readUByte() == 1;
		}
	}

	@OriginalMember(owner = "client!wj", name = "a", descriptor = "(IIB)V")
	private void reflect(@OriginalArg(0) int x, @OriginalArg(1) int y) {
		@Pc(9) int columnCoord = EnvironmentLight.horizontalTable[x];
		@Pc(13) int rowCoord = TextureRaster.verticalTable[y];
		@Pc(32) float angle = (float) Math.atan2((double) (columnCoord - 2048), (double) (rowCoord - 2048));
		if ((double) angle >= -3.141592653589793D && (double) angle <= -2.356194490192345D) {
			TextureOpKaleidoscope.mirroredY = y;
			TextureOpKaleidoscope.mirroredX = x;
		} else if ((double) angle <= -1.5707963267948966D && (double) angle >= -2.356194490192345D) {
			TextureOpKaleidoscope.mirroredY = x;
			TextureOpKaleidoscope.mirroredX = y;
		} else if ((double) angle <= -0.7853981633974483D && (double) angle >= -1.5707963267948966D) {
			TextureOpKaleidoscope.mirroredX = EnvironmentLight.textureOpWidth - y;
			TextureOpKaleidoscope.mirroredY = x;
		} else if (angle <= 0.0F && (double) angle >= -0.7853981633974483D) {
			TextureOpKaleidoscope.mirroredX = x;
			TextureOpKaleidoscope.mirroredY = TextureOperation.currentHeight - y;
		} else if (angle >= 0.0F && (double) angle <= 0.7853981633974483D) {
			TextureOpKaleidoscope.mirroredX = EnvironmentLight.textureOpWidth - x;
			TextureOpKaleidoscope.mirroredY = TextureOperation.currentHeight - y;
		} else if ((double) angle >= 0.7853981633974483D && (double) angle <= 1.5707963267948966D) {
			TextureOpKaleidoscope.mirroredX = EnvironmentLight.textureOpWidth - y;
			TextureOpKaleidoscope.mirroredY = TextureOperation.currentHeight - x;
		} else if ((double) angle >= 1.5707963267948966D && (double) angle <= 2.356194490192345D) {
			TextureOpKaleidoscope.mirroredY = TextureOperation.currentHeight - x;
			TextureOpKaleidoscope.mirroredX = y;
		} else if ((double) angle >= 2.356194490192345D && (double) angle <= 3.141592653589793D) {
			TextureOpKaleidoscope.mirroredY = y;
			TextureOpKaleidoscope.mirroredX = EnvironmentLight.textureOpWidth - x;
		}
		TextureOpKaleidoscope.mirroredY &= TextureOperation.columnEnd;
		TextureOpKaleidoscope.mirroredX &= TextureRaster.widthMask;
	}
	@OriginalMember(owner = "client!ng", name = "c", descriptor = "I")
	public static int mirroredX = 0;

	@OriginalMember(owner = "client!ah", name = "d", descriptor = "I")
	public static int mirroredY = 0;
}
