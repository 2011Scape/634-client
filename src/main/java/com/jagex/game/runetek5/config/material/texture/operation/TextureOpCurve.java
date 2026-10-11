package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;

@OriginalClass("client!oj")
public final class TextureOpCurve extends TextureOperation {

	@OriginalMember(owner = "client!oj", name = "L", descriptor = "[I")
	private int[] lastMarker;

	@OriginalMember(owner = "client!oj", name = "N", descriptor = "[[I")
	private int[][] controlPoints;

	@OriginalMember(owner = "client!oj", name = "R", descriptor = "[I")
	private int[] extrapolatedEnd;

	@OriginalMember(owner = "client!oj", name = "O", descriptor = "I")
	private int interpolationMode = 0;

	@OriginalMember(owner = "client!oj", name = "S", descriptor = "[S")
	private final short[] curveLookup = new short[257];

	@OriginalMember(owner = "client!oj", name = "<init>", descriptor = "()V")
	public TextureOpCurve() {
		super(1, true);
	}

	@OriginalMember(owner = "client!oj", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode != 0) {
			return;
		}
		this.interpolationMode = packet.readUByte();
		this.controlPoints = new int[packet.readUByte()][2];
		for (@Pc(19) int i = 0; i < this.controlPoints.length; i++) {
			this.controlPoints[i][0] = packet.readUShort();
			this.controlPoints[i][1] = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!oj", name = "f", descriptor = "(I)V")
	private void buildLookup() {
		@Pc(4) int interpolationMode = this.interpolationMode;
		@Pc(26) int index;
		@Pc(24) int input;
		@Pc(53) int[] start;
		@Pc(58) int[] end;
		@Pc(67) int local67;
		@Pc(71) int local71;
		@Pc(75) int local75;
		@Pc(84) int local84;
		if (interpolationMode == 2) {
			for (interpolationMode = 0; interpolationMode < 257; interpolationMode++) {
				input = interpolationMode << 4;
				for (index = 1; this.controlPoints.length - 1 > index && this.controlPoints[index][0] <= input; index++) {
				}
				start = this.controlPoints[index - 1];
				end = this.controlPoints[index];
				int prevValue = this.markerAt(index - 2)[1];
				int startValue = start[1];
				int endValue = end[1];
				int nextValue = this.markerAt(index + 1)[1];
				@Pc(102) int fraction = (input - start[0] << 12) / (end[0] - start[0]);
				@Pc(108) int fractionSquared = fraction * fraction >> 12;
				@Pc(118) int cubic = startValue + nextValue - endValue - prevValue;
				@Pc(125) int quadratic = prevValue - cubic - startValue;
				@Pc(130) int linear = endValue - prevValue;
				@Pc(142) int cubicTerm = (fraction * cubic >> 12) * fractionSquared >> 12;
				@Pc(148) int quadraticTerm = quadratic * fractionSquared >> 12;
				@Pc(154) int linearTerm = fraction * linear >> 12;
				@Pc(162) int value = startValue + linearTerm + quadraticTerm + cubicTerm;
				if (value <= -32768) {
					value = -32767;
				}
				if (value >= 32768) {
					value = 32767;
				}
				this.curveLookup[interpolationMode] = (short) value;
			}
		} else if (interpolationMode == 1) {
			for (interpolationMode = 0; interpolationMode < 257; interpolationMode++) {
				input = interpolationMode << 4;
				for (index = 1; this.controlPoints.length - 1 > index && this.controlPoints[index][0] <= input; index++) {
				}
				start = this.controlPoints[index - 1];
				end = this.controlPoints[index];
				int fraction = (input - start[0] << 12) / (end[0] - start[0]);
				int endWeight = 4096 - TextureRaster.PALETTE_COS[fraction >> 5 & 0xFF] >> 1;
				int startWeight = 4096 - endWeight;
				int value = start[1] * startWeight + end[1] * endWeight >> 12;
				if (value <= -32768) {
					value = -32767;
				}
				if (value >= 32768) {
					value = 32767;
				}
				this.curveLookup[interpolationMode] = (short) value;
			}
		} else {
			for (interpolationMode = 0; interpolationMode < 257; interpolationMode++) {
				input = interpolationMode << 4;
				for (index = 1; index < this.controlPoints.length - 1 && input >= this.controlPoints[index][0]; index++) {
				}
				start = this.controlPoints[index - 1];
				end = this.controlPoints[index];
				int fraction = (input - start[0] << 12) / (end[0] - start[0]);
				int startWeight = 4096 - fraction;
				int value = end[1] * fraction + startWeight * start[1] >> 12;
				if (value <= -32768) {
					value = -32767;
				}
				if (value >= 32768) {
					value = 32767;
				}
				this.curveLookup[interpolationMode] = (short) value;
			}
		}
	}

	@OriginalMember(owner = "client!oj", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(11) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(27) int[] source = this.getChildMonochromeOutput(row, 0);
			for (@Pc(29) int x = 0; x < EnvironmentLight.textureOpWidth; x++) {
				@Pc(37) int index = source[x] >> 4;
				if (index < 0) {
					index = 0;
				}
				if (index > 256) {
					index = 256;
				}
				output[x] = this.curveLookup[index];
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!oj", name = "b", descriptor = "(B)V")
	private void computeExtrapolatedEndpoints() {
		@Pc(8) int[] first = this.controlPoints[0];
		@Pc(13) int[] second = this.controlPoints[1];
		@Pc(22) int[] penultimate = this.controlPoints[this.controlPoints.length - 2];
		@Pc(31) int[] last = this.controlPoints[this.controlPoints.length - 1];
		this.lastMarker = new int[] { penultimate[0] + penultimate[0] - last[0], penultimate[1] - (-penultimate[1] + last[1]) };
		this.extrapolatedEnd = new int[] { first[0] + first[0] - second[0], first[1] - (-first[1] - -second[1]) };
	}

	@OriginalMember(owner = "client!oj", name = "e", descriptor = "(I)V")
	@Override
	public void postDecode() {
		if (this.controlPoints == null) {
			this.controlPoints = new int[][] { new int[2], { 4096, 4096 } };
		}
		if (this.controlPoints.length < 2) {
			throw new RuntimeException("Curve operation requires at least two markers");
		}
		if (this.interpolationMode == 2) {
			this.computeExtrapolatedEndpoints();
		}
		TextureRaster.calculateTrigTables();
		this.buildLookup();
	}

	@OriginalMember(owner = "client!oj", name = "a", descriptor = "(IB)[I")
	private int[] markerAt(@OriginalArg(0) int index) {
		if (index < 0) {
			return this.extrapolatedEnd;
		} else if (index >= this.controlPoints.length) {
			return this.lastMarker;
		} else {
			return this.controlPoints[index];
		}
	}
}
