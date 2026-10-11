package com.jagex.game.runetek5.config.midi;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!eha")
public final class Filter {

	@OriginalMember(owner = "client!eha", name = "a", descriptor = "[I")
	public final int[] numPairs = new int[2];

	@OriginalMember(owner = "client!eha", name = "f", descriptor = "[I")
	private final int[] unity = new int[2];

	@OriginalMember(owner = "client!eha", name = "d", descriptor = "[[[I")
	private final int[][][] pairPhase = new int[2][2][4];

	@OriginalMember(owner = "client!eha", name = "b", descriptor = "[[[I")
	private final int[][][] pairMagnitude = new int[2][2][4];

	@OriginalMember(owner = "client!eha", name = "a", descriptor = "(Lclient!cea;Lclient!ra;)V")
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) Envelope envelope) {
		@Pc(3) int count = packet.readUByte();
		this.numPairs[0] = count >> 4;
		this.numPairs[1] = count & 0xF;
		if (count == 0) {
			this.unity[0] = this.unity[1] = 0;
			return;
		}
		this.unity[0] = packet.readUShort();
		this.unity[1] = packet.readUShort();
		@Pc(37) int varyingMask = packet.readUByte();
		@Pc(42) int local42;
		for (@Pc(39) int i = 0; i < 2; i++) {
			for (int pairIndex = 0; pairIndex < this.numPairs[i]; pairIndex++) {
				this.pairPhase[i][0][pairIndex] = packet.readUShort();
				this.pairMagnitude[i][0][pairIndex] = packet.readUShort();
			}
		}
		for (int direction = 0; direction < 2; direction++) {
			for (@Pc(81) int j = 0; j < this.numPairs[direction]; j++) {
				if ((varyingMask & 0x1 << direction * 4 << j) == 0) {
					this.pairPhase[direction][1][j] = this.pairPhase[direction][0][j];
					this.pairMagnitude[direction][1][j] = this.pairMagnitude[direction][0][j];
				} else {
					this.pairPhase[direction][1][j] = packet.readUShort();
					this.pairMagnitude[direction][1][j] = packet.readUShort();
				}
			}
		}
		if (varyingMask != 0 || this.unity[1] != this.unity[0]) {
			envelope.decodeShape(packet);
		}
	}

	@OriginalMember(owner = "client!eha", name = "a", descriptor = "(IIF)F")
	private float adaptPhase(@OriginalArg(0) int direction, @OriginalArg(1) int phase, @OriginalArg(2) float off) {
		@Pc(30) float f1 = (float) this.pairPhase[direction][0][phase] + off * (float) (this.pairPhase[direction][1][phase] - this.pairPhase[direction][0][phase]);
		@Pc(34) float f2 = f1 * 1.2207031E-4F;
		return Filter.normalize(f2);
	}

	@OriginalMember(owner = "client!eha", name = "b", descriptor = "(IIF)F")
	private float adaptMagnitude(@OriginalArg(0) int direction, @OriginalArg(1) int phase, @OriginalArg(2) float off) {
		@Pc(30) float apha1 = (float) this.pairMagnitude[direction][0][phase] + off * (float) (this.pairMagnitude[direction][1][phase] - this.pairMagnitude[direction][0][phase]);
		@Pc(34) float alpha2 = apha1 * 0.0015258789F;
		return 1.0F - (float) Math.pow(10.0D, (double) (-alpha2 / 20.0F));
	}

	@OriginalMember(owner = "client!eha", name = "a", descriptor = "(IF)I")
	public int compute(@OriginalArg(0) int direction, @OriginalArg(1) float off) {
		@Pc(20) float local20;
		if (direction == 0) {
			float unityValue = (float) this.unity[0] + (float) (this.unity[1] - this.unity[0]) * off;
			@Pc(24) float unityDb = unityValue * 0.0030517578F;
			Filter.invUnityFloat = (float) Math.pow(0.1D, (double) (unityDb / 20.0F));
			Filter.invUnityInt = (int) (Filter.invUnityFloat * 65536.0F);
		}
		if (this.numPairs[direction] == 0) {
			return 0;
		}
		float magnitude = this.adaptMagnitude(direction, 0, off);
		Filter.COEFFICIENT_FLOAT[direction][0] = -2.0F * magnitude * (float) Math.cos((double) this.adaptPhase(direction, 0, off));
		Filter.COEFFICIENT_FLOAT[direction][1] = magnitude * magnitude;
		for (@Pc(77) int phase = 1; phase < this.numPairs[direction]; phase++) {
			magnitude = this.adaptMagnitude(direction, phase, off);
			@Pc(97) float coeff1 = -2.0F * magnitude * (float) Math.cos((double) this.adaptPhase(direction, phase, off));
			@Pc(101) float coeff2 = magnitude * magnitude;
			Filter.COEFFICIENT_FLOAT[direction][phase * 2 + 1] = Filter.COEFFICIENT_FLOAT[direction][phase * 2 - 1] * coeff2;
			Filter.COEFFICIENT_FLOAT[direction][phase * 2] = Filter.COEFFICIENT_FLOAT[direction][phase * 2 - 1] * coeff1 + Filter.COEFFICIENT_FLOAT[direction][phase * 2 - 2] * coeff2;
			for (@Pc(157) int i = phase * 2 - 1; i >= 2; i--) {
				Filter.COEFFICIENT_FLOAT[direction][i] += Filter.COEFFICIENT_FLOAT[direction][i - 1] * coeff1 + Filter.COEFFICIENT_FLOAT[direction][i - 2] * coeff2;
			}
			Filter.COEFFICIENT_FLOAT[direction][1] += Filter.COEFFICIENT_FLOAT[direction][0] * coeff1 + coeff2;
			Filter.COEFFICIENT_FLOAT[direction][0] += coeff1;
		}
		@Pc(226) int coefIndex;
		if (direction == 0) {
			for (coefIndex = 0; coefIndex < this.numPairs[0] * 2; coefIndex++) {
				Filter.COEFFICIENT_FLOAT[0][coefIndex] *= Filter.invUnityFloat;
			}
		}
		for (coefIndex = 0; coefIndex < this.numPairs[direction] * 2; coefIndex++) {
			Filter.COEFFICIENT_INT[direction][coefIndex] = (int) (Filter.COEFFICIENT_FLOAT[direction][coefIndex] * 65536.0F);
		}
		return this.numPairs[direction] * 2;
	}

	@OriginalMember(owner = "client!eha", name = "a", descriptor = "(F)F")
	public static float normalize(@OriginalArg(0) float octaves) {
		@Pc(7) float _f = (float) Math.pow(2.0D, (double) octaves) * 32.703197F;
		return _f * 3.1415927F / 11025.0F;
	}

	@OriginalMember(owner = "client!eha", name = "c", descriptor = "[[I")
	public static final int[][] COEFFICIENT_INT = new int[2][8];

	@OriginalMember(owner = "client!eha", name = "e", descriptor = "I")
	public static int invUnityInt;

	@OriginalMember(owner = "client!eha", name = "g", descriptor = "F")
	public static float invUnityFloat;

	@OriginalMember(owner = "client!eha", name = "h", descriptor = "[[F")
	public static final float[][] COEFFICIENT_FLOAT = new float[2][8];
}
