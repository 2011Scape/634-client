package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;

@OriginalClass("client!st")
public final class TextureOpSquareWaveform extends TextureOperation {

	@OriginalMember(owner = "client!st", name = "R", descriptor = "[I")
	private int[] stepStartBoundaries;

	@OriginalMember(owner = "client!st", name = "S", descriptor = "[I")
	private int[] pulseEndBoundaries;

	@OriginalMember(owner = "client!st", name = "K", descriptor = "I")
	private int waveAxis = 0;

	@OriginalMember(owner = "client!st", name = "O", descriptor = "I")
	private int stepCount = 10;

	@OriginalMember(owner = "client!st", name = "T", descriptor = "I")
	private int dutyCycle = 2048;

	@OriginalMember(owner = "client!st", name = "<init>", descriptor = "()V")
	public TextureOpSquareWaveform() {
		super(0, true);
	}

	@OriginalMember(owner = "client!st", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(17) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(25) int verticalPos = TextureRaster.verticalTable[row];
			@Pc(34) int local34;
			if (this.waveAxis == 0) {
				@Pc(134) short variableToSet = 0;
				for (int stepIndex = 0; stepIndex < this.stepCount; stepIndex++) {
					if (this.stepStartBoundaries[stepIndex] <= verticalPos && this.stepStartBoundaries[stepIndex + 1] > verticalPos) {
						if (this.pulseEndBoundaries[stepIndex] > verticalPos) {
							variableToSet = 4096;
						}
						break;
					}
				}
				Arrays.setVariables(output, 0, EnvironmentLight.textureOpWidth, variableToSet);
			} else {
				for (@Pc(30) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
					int wavePosition = 0;
					@Pc(36) short value = 0;
					@Pc(40) int horizontalPos = EnvironmentLight.horizontalTable[i];
					@Pc(43) int waveAxis = this.waveAxis;
					if (waveAxis == 1) {
						wavePosition = horizontalPos;
					} else if (waveAxis == 2) {
						wavePosition = (horizontalPos + verticalPos - 4096 >> 1) + 2048;
					} else if (waveAxis == 3) {
						wavePosition = (horizontalPos - verticalPos >> 1) + 2048;
					}
					for (waveAxis = 0; waveAxis < this.stepCount; waveAxis++) {
						if (wavePosition >= this.stepStartBoundaries[waveAxis] && wavePosition < this.stepStartBoundaries[waveAxis + 1]) {
							if (this.pulseEndBoundaries[waveAxis] > wavePosition) {
								value = 4096;
							}
							break;
						}
					}
					output[i] = value;
				}
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!st", name = "e", descriptor = "(I)V")
	@Override
	public void postDecode() {
		this.computeStepBoundaries();
	}

	@OriginalMember(owner = "client!st", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.stepCount = packet.readUByte();
		} else if (opcode == 1) {
			this.dutyCycle = packet.readUShort();
		} else if (opcode == 2) {
			this.waveAxis = packet.readUByte();
		}
	}

	@OriginalMember(owner = "client!st", name = "b", descriptor = "(B)V")
	private void computeStepBoundaries() {
		@Pc(7) int position = 0;
		this.pulseEndBoundaries = new int[this.stepCount + 1];
		this.stepStartBoundaries = new int[this.stepCount + 1];
		@Pc(26) int stepSize = 4096 / this.stepCount;
		@Pc(33) int pulseWidth = stepSize * this.dutyCycle >> 12;
		for (@Pc(35) int i = 0; i < this.stepCount; i++) {
			this.stepStartBoundaries[i] = position;
			this.pulseEndBoundaries[i] = pulseWidth + position;
			position += stepSize;
		}
		this.stepStartBoundaries[this.stepCount] = 4096;
		this.pulseEndBoundaries[this.stepCount] = this.pulseEndBoundaries[0] + 4096;
	}
}
