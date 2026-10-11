package com.jagex.game.runetek5.config.midi.instrument;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.VorbisSound;

@OriginalClass("client!eda")
public final class MusicSampleResidue {

	@OriginalMember(owner = "client!eda", name = "c", descriptor = "I")
	private final int residueType = MusicSample.getBits(16);

	@OriginalMember(owner = "client!eda", name = "d", descriptor = "I")
	private final int residueBegin = MusicSample.getBits(24);

	@OriginalMember(owner = "client!eda", name = "e", descriptor = "I")
	private final int residueEnd = MusicSample.getBits(24);

	@OriginalMember(owner = "client!eda", name = "b", descriptor = "I")
	private final int partitionSize = MusicSample.getBits(24) + 1;

	@OriginalMember(owner = "client!eda", name = "a", descriptor = "I")
	private final int classifications = MusicSample.getBits(6) + 1;

	@OriginalMember(owner = "client!eda", name = "g", descriptor = "I")
	private final int classbook = MusicSample.getBits(8);

	@OriginalMember(owner = "client!eda", name = "f", descriptor = "[I")
	private final int[] residueBooks;

	@OriginalMember(owner = "client!eda", name = "<init>", descriptor = "()V")
	public MusicSampleResidue() {
		@Pc(33) int[] residueCascade = new int[this.classifications];
		@Pc(38) int local38;
		for (@Pc(35) int i = 0; i < this.classifications; i++) {
			int highBits = 0;
			@Pc(41) int lowBits = MusicSample.getBits(3);
			@Pc(47) boolean hasHighBits = VorbisSound.getBit() != 0;
			if (hasHighBits) {
				highBits = MusicSample.getBits(5);
			}
			residueCascade[i] = highBits << 3 | lowBits;
		}
		this.residueBooks = new int[this.classifications * 8];
		for (int bookIndex = 0; bookIndex < this.classifications * 8; bookIndex++) {
			this.residueBooks[bookIndex] = (residueCascade[bookIndex >> 3] & 0x1 << (bookIndex & 0x7)) == 0 ? -1 : MusicSample.getBits(8);
		}
	}

	@OriginalMember(owner = "client!eda", name = "a", descriptor = "([FIZ)V")
	public void decode(@OriginalArg(0) float[] v, @OriginalArg(1) int outputSize, @OriginalArg(2) boolean decode) {
		for (@Pc(1) int i = 0; i < outputSize; i++) {
			v[i] = 0.0F;
		}
		if (decode) {
			return;
		}
		@Pc(19) int dimensions = MusicSample.codebooks[this.classbook].dimensions;
		@Pc(25) int windowSize = this.residueEnd - this.residueBegin;
		@Pc(30) int numWindows = windowSize / this.partitionSize;
		@Pc(33) int[] partitionClasses = new int[numWindows];
		for (@Pc(35) int j = 0; j < 8; j++) {
			@Pc(38) int partitionIndex = 0;
			while (partitionIndex < numWindows) {
				@Pc(47) int local47;
				@Pc(51) int local51;
				if (j == 0) {
					int classWord = MusicSample.codebooks[this.classbook].decode();
					for (int classSlot = dimensions - 1; classSlot >= 0; classSlot--) {
						if (partitionIndex + classSlot < numWindows) {
							partitionClasses[partitionIndex + classSlot] = classWord % this.classifications;
						}
						classWord /= this.classifications;
					}
				}
				for (int partitionStep = 0; partitionStep < dimensions; partitionStep++) {
					int partitionClass = partitionClasses[partitionIndex];
					@Pc(90) int bookIndex = this.residueBooks[partitionClass * 8 + j];
					if (bookIndex >= 0) {
						@Pc(100) int offset = this.residueBegin + partitionIndex * this.partitionSize;
						@Pc(104) MusicSampleCodebook vorbisCodebook = MusicSample.codebooks[bookIndex];
						@Pc(113) int local113;
						if (this.residueType == 0) {
							int stride = this.partitionSize / vorbisCodebook.dimensions;
							for (@Pc(115) int k = 0; k < stride; k++) {
								@Pc(119) float[] values = vorbisCodebook.decodeVector();
								for (@Pc(121) int m = 0; m < vorbisCodebook.dimensions; m++) {
									v[offset + k + m * stride] += values[m];
								}
							}
						} else {
							int position = 0;
							while (position < this.partitionSize) {
								@Pc(153) float[] values = vorbisCodebook.decodeVector();
								for (@Pc(155) int dim = 0; dim < vorbisCodebook.dimensions; dim++) {
									v[offset + position] += values[dim];
									position++;
								}
							}
						}
					}
					partitionIndex++;
					if (partitionIndex >= numWindows) {
						break;
					}
				}
			}
		}
	}
}
