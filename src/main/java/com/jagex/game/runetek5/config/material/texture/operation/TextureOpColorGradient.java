package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!aw")
public final class TextureOpColorGradient extends TextureOperation {

	@OriginalMember(owner = "client!aw", name = "N", descriptor = "[[I")
	private int[][] colorStops;

	@OriginalMember(owner = "client!aw", name = "S", descriptor = "[I")
	private final int[] gradientLookup = new int[257];

	@OriginalMember(owner = "client!aw", name = "<init>", descriptor = "()V")
	public TextureOpColorGradient() {
		super(1, false);
	}

	@OriginalMember(owner = "client!aw", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode != 0) {
			return;
		}
		@Pc(7) int preset = packet.readUByte();
		if (preset != 0) {
			this.setPreset(preset);
			return;
		}
		this.colorStops = new int[packet.readUByte()][4];
		for (@Pc(29) int i = 0; i < this.colorStops.length; i++) {
			this.colorStops[i][0] = packet.readUShort();
			this.colorStops[i][1] = packet.readUByte() << 4;
			this.colorStops[i][2] = packet.readUByte() << 4;
			this.colorStops[i][3] = packet.readUByte() << 4;
		}
	}

	@OriginalMember(owner = "client!aw", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int row) {
		@Pc(13) int[][] output = super.colorImageCache.get(row);
		if (super.colorImageCache.dirty) {
			@Pc(23) int[] input = this.getChildMonochromeOutput(row, 0);
			@Pc(27) int[] outputRed = output[0];
			@Pc(31) int[] outputGreen = output[1];
			@Pc(35) int[] outputBlue = output[2];
			for (@Pc(37) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(45) int local45 = input[i] >> 4;
				if (local45 < 0) {
					local45 = 0;
				}
				if (local45 > 256) {
					local45 = 256;
				}
				local45 = this.gradientLookup[local45];
				outputRed[i] = local45 >> 12 & 0xFF0;
				outputGreen[i] = local45 >> 4 & 0xFF0;
				outputBlue[i] = (local45 & 0xFF) << 4;
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!aw", name = "e", descriptor = "(I)V")
	@Override
	public void postDecode() {
		if (this.colorStops == null) {
			this.setPreset(1);
		}
		this.buildRamp();
	}

	@OriginalMember(owner = "client!aw", name = "e", descriptor = "(II)V")
	private void setPreset(@OriginalArg(0) int preset) {
		if (preset == 0) {
			return;
		}
		if (preset == 1) {
			this.colorStops = new int[2][4];
			this.colorStops[0][1] = 0;
			this.colorStops[0][2] = 0;
			this.colorStops[0][0] = 0;
			this.colorStops[0][3] = 0;
			this.colorStops[1][3] = 4096;
			this.colorStops[1][2] = 4096;
			this.colorStops[1][1] = 4096;
			this.colorStops[1][0] = 4096;
		} else if (preset == 2) {
			this.colorStops = new int[8][4];
			this.colorStops[0][3] = 2361;
			this.colorStops[0][1] = 2650;
			this.colorStops[0][0] = 0;
			this.colorStops[0][2] = 2602;
			this.colorStops[1][2] = 1799;
			this.colorStops[1][1] = 2313;
			this.colorStops[1][3] = 1558;
			this.colorStops[1][0] = 2867;
			this.colorStops[2][2] = 1734;
			this.colorStops[2][1] = 2618;
			this.colorStops[2][3] = 1413;
			this.colorStops[2][0] = 3072;
			this.colorStops[3][2] = 1220;
			this.colorStops[3][0] = 3276;
			this.colorStops[3][3] = 947;
			this.colorStops[3][1] = 2296;
			this.colorStops[4][2] = 963;
			this.colorStops[4][3] = 722;
			this.colorStops[4][0] = 3481;
			this.colorStops[4][1] = 2072;
			this.colorStops[5][3] = 1766;
			this.colorStops[5][2] = 2152;
			this.colorStops[5][0] = 3686;
			this.colorStops[5][1] = 2730;
			this.colorStops[6][2] = 1060;
			this.colorStops[6][0] = 3891;
			this.colorStops[6][1] = 2232;
			this.colorStops[6][3] = 915;
			this.colorStops[7][2] = 1413;
			this.colorStops[7][3] = 1140;
			this.colorStops[7][1] = 1686;
			this.colorStops[7][0] = 4096;
		} else if (preset == 3) {
			this.colorStops = new int[7][4];
			this.colorStops[0][0] = 0;
			this.colorStops[0][1] = 0;
			this.colorStops[0][2] = 0;
			this.colorStops[0][3] = 4096;
			this.colorStops[1][3] = 4096;
			this.colorStops[1][1] = 0;
			this.colorStops[1][0] = 663;
			this.colorStops[1][2] = 4096;
			this.colorStops[2][0] = 1363;
			this.colorStops[2][2] = 4096;
			this.colorStops[2][1] = 0;
			this.colorStops[2][3] = 0;
			this.colorStops[3][0] = 2048;
			this.colorStops[3][2] = 4096;
			this.colorStops[3][3] = 0;
			this.colorStops[3][1] = 4096;
			this.colorStops[4][0] = 2727;
			this.colorStops[4][1] = 4096;
			this.colorStops[4][2] = 0;
			this.colorStops[4][3] = 0;
			this.colorStops[5][0] = 3411;
			this.colorStops[5][3] = 4096;
			this.colorStops[5][2] = 0;
			this.colorStops[5][1] = 4096;
			this.colorStops[6][2] = 0;
			this.colorStops[6][3] = 4096;
			this.colorStops[6][0] = 4096;
			this.colorStops[6][1] = 0;
		} else if (preset == 4) {
			this.colorStops = new int[6][4];
			this.colorStops[0][2] = 0;
			this.colorStops[0][1] = 0;
			this.colorStops[0][3] = 0;
			this.colorStops[0][0] = 0;
			this.colorStops[1][3] = 1493;
			this.colorStops[1][1] = 0;
			this.colorStops[1][2] = 0;
			this.colorStops[1][0] = 1843;
			this.colorStops[2][3] = 2939;
			this.colorStops[2][2] = 0;
			this.colorStops[2][1] = 0;
			this.colorStops[2][0] = 2457;
			this.colorStops[3][3] = 3565;
			this.colorStops[3][1] = 0;
			this.colorStops[3][2] = 1124;
			this.colorStops[3][0] = 2781;
			this.colorStops[4][2] = 3084;
			this.colorStops[4][0] = 3481;
			this.colorStops[4][1] = 546;
			this.colorStops[4][3] = 4031;
			this.colorStops[5][0] = 4096;
			this.colorStops[5][3] = 4096;
			this.colorStops[5][2] = 4096;
			this.colorStops[5][1] = 4096;
		} else if (preset == 5) {
			this.colorStops = new int[16][4];
			this.colorStops[0][0] = 0;
			this.colorStops[0][3] = 321;
			this.colorStops[0][2] = 192;
			this.colorStops[0][1] = 80;
			this.colorStops[1][0] = 155;
			this.colorStops[1][2] = 449;
			this.colorStops[1][3] = 562;
			this.colorStops[1][1] = 321;
			this.colorStops[2][0] = 389;
			this.colorStops[2][3] = 803;
			this.colorStops[2][1] = 578;
			this.colorStops[2][2] = 690;
			this.colorStops[3][1] = 947;
			this.colorStops[3][3] = 1140;
			this.colorStops[3][0] = 671;
			this.colorStops[3][2] = 995;
			this.colorStops[4][1] = 1285;
			this.colorStops[4][3] = 1509;
			this.colorStops[4][2] = 1397;
			this.colorStops[4][0] = 897;
			this.colorStops[5][2] = 1429;
			this.colorStops[5][3] = 1413;
			this.colorStops[5][0] = 1175;
			this.colorStops[5][1] = 1525;
			this.colorStops[6][0] = 1368;
			this.colorStops[6][2] = 1461;
			this.colorStops[6][1] = 1734;
			this.colorStops[6][3] = 1333;
			this.colorStops[7][2] = 1525;
			this.colorStops[7][1] = 1413;
			this.colorStops[7][0] = 1507;
			this.colorStops[7][3] = 1702;
			this.colorStops[8][3] = 2056;
			this.colorStops[8][1] = 1108;
			this.colorStops[8][2] = 1590;
			this.colorStops[8][0] = 1736;
			this.colorStops[9][2] = 2056;
			this.colorStops[9][3] = 2666;
			this.colorStops[9][0] = 2088;
			this.colorStops[9][1] = 1766;
			this.colorStops[10][0] = 2355;
			this.colorStops[10][2] = 2586;
			this.colorStops[10][1] = 2409;
			this.colorStops[10][3] = 3276;
			this.colorStops[11][2] = 3148;
			this.colorStops[11][3] = 3228;
			this.colorStops[11][1] = 3116;
			this.colorStops[11][0] = 2691;
			this.colorStops[12][3] = 3196;
			this.colorStops[12][0] = 3031;
			this.colorStops[12][1] = 3806;
			this.colorStops[12][2] = 3710;
			this.colorStops[13][3] = 3019;
			this.colorStops[13][1] = 3437;
			this.colorStops[13][2] = 3421;
			this.colorStops[13][0] = 3522;
			this.colorStops[14][1] = 3116;
			this.colorStops[14][2] = 3148;
			this.colorStops[14][3] = 3228;
			this.colorStops[14][0] = 3727;
			this.colorStops[15][2] = 2505;
			this.colorStops[15][3] = 2746;
			this.colorStops[15][1] = 2377;
			this.colorStops[15][0] = 4096;
		} else if (preset == 6) {
			this.colorStops = new int[4][4];
			this.colorStops[0][1] = 0;
			this.colorStops[0][2] = 4096;
			this.colorStops[0][3] = 0;
			this.colorStops[0][0] = 2048;
			this.colorStops[1][0] = 2867;
			this.colorStops[1][3] = 0;
			this.colorStops[1][1] = 4096;
			this.colorStops[1][2] = 4096;
			this.colorStops[2][0] = 3276;
			this.colorStops[2][1] = 4096;
			this.colorStops[2][3] = 0;
			this.colorStops[2][2] = 4096;
			this.colorStops[3][0] = 4096;
			this.colorStops[3][2] = 0;
			this.colorStops[3][1] = 4096;
			this.colorStops[3][3] = 0;
		} else {
			throw new RuntimeException("Invalid gradient preset");
		}
	}

	@OriginalMember(owner = "client!aw", name = "f", descriptor = "(I)V")
	private void buildRamp() {
		@Pc(15) int stopCount = this.colorStops.length;
		if (stopCount <= 0) {
			return;
		}
		for (@Pc(22) int index = 0; index < 257; index++) {
			@Pc(26) int upperIndex = 0;
			@Pc(30) int position = index << 4;
			for (@Pc(32) int i = 0; i < stopCount && this.colorStops[i][0] <= position; i++) {
				upperIndex++;
			}
			@Pc(61) int red;
			@Pc(65) int green;
			@Pc(69) int blue;
			@Pc(57) int[] upper;
			if (upperIndex >= stopCount) {
				upper = this.colorStops[stopCount - 1];
				red = upper[1];
				green = upper[2];
				blue = upper[3];
			} else {
				upper = this.colorStops[upperIndex];
				if (upperIndex <= 0) {
					red = upper[1];
					blue = upper[3];
					green = upper[2];
				} else {
					@Pc(102) int[] lower = this.colorStops[upperIndex - 1];
					@Pc(119) int weight = (position - lower[0] << 12) / (upper[0] - lower[0]);
					@Pc(124) int inverse = 4096 - weight;
					red = lower[1] * inverse + upper[1] * weight >> 12;
					blue = weight * upper[3] + inverse * lower[3] >> 12;
					green = lower[2] * inverse + upper[2] * weight >> 12;
				}
			}
			green >>= 0x4;
			red >>= 0x4;
			blue >>= 0x4;
			if (red < 0) {
				red = 0;
			} else if (red > 255) {
				red = 255;
			}
			if (blue < 0) {
				blue = 0;
			} else if (blue > 255) {
				blue = 255;
			}
			if (green < 0) {
				green = 0;
			} else if (green > 255) {
				green = 255;
			}
			this.gradientLookup[index] = blue | red << 16 | green << 8;
		}
	}
}
