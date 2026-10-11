package com.jagex.game.runetek5.config.midi.instrument;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.VorbisSound;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!fq")
public final class MusicSampleCodebook {

	@OriginalMember(owner = "client!fq", name = "f", descriptor = "[I")
	private int[] huffmanTree;

	@OriginalMember(owner = "client!fq", name = "a", descriptor = "I")
	public final int dimensions;

	@OriginalMember(owner = "client!fq", name = "e", descriptor = "I")
	private final int entries;

	@OriginalMember(owner = "client!fq", name = "c", descriptor = "[I")
	private final int[] lengths;

	@OriginalMember(owner = "client!fq", name = "d", descriptor = "[I")
	private int[] mults;

	@OriginalMember(owner = "client!fq", name = "b", descriptor = "[[F")
	private float[][] multiplicands;

	@OriginalMember(owner = "client!fq", name = "<init>", descriptor = "()V")
	public MusicSampleCodebook() {
		MusicSample.getBits(24);
		this.dimensions = MusicSample.getBits(16);
		this.entries = MusicSample.getBits(24);
		this.lengths = new int[this.entries];
		@Pc(23) boolean ordered = VorbisSound.getBit() != 0;
		@Pc(27) int local27;
		@Pc(32) int local32;
		@Pc(43) int local43;
		if (ordered) {
			int currentEntry = 0;
			int currentLength = MusicSample.getBits(5) + 1;
			while (currentEntry < this.entries) {
				@Pc(41) int runLength = MusicSample.getBits(Utils.bitLength(this.entries - currentEntry));
				for (int runIndex = 0; runIndex < runLength; runIndex++) {
					this.lengths[currentEntry++] = currentLength;
				}
				currentLength++;
			}
		} else {
			@Pc(66) boolean sparse = VorbisSound.getBit() != 0;
			for (int entryIndex = 0; entryIndex < this.entries; entryIndex++) {
				if (sparse && VorbisSound.getBit() == 0) {
					this.lengths[entryIndex] = 0;
				} else {
					this.lengths[entryIndex] = MusicSample.getBits(5) + 1;
				}
			}
		}
		this.computeAcceleratedHuffman();
		int lookupType = MusicSample.getBits(4);
		if (lookupType > 0) {
			@Pc(103) float minimumValue = VorbisSound.float32Unpack(MusicSample.getBits(32));
			@Pc(107) float deltaValue = VorbisSound.float32Unpack(MusicSample.getBits(32));
			int valueBits = MusicSample.getBits(4) + 1;
			@Pc(118) boolean sequenceP = VorbisSound.getBit() != 0;
			@Pc(127) int lookupValues;
			if (lookupType == 1) {
				lookupValues = MusicSampleCodebook.lookup1Values(this.entries, this.dimensions);
			} else {
				lookupValues = this.entries * this.dimensions;
			}
			this.mults = new int[lookupValues];
			for (@Pc(140) int i = 0; i < lookupValues; i++) {
				this.mults[i] = MusicSample.getBits(valueBits);
			}
			this.multiplicands = new float[this.entries][this.dimensions];
			@Pc(163) int entry;
			@Pc(166) float last;
			@Pc(168) int local168;
			@Pc(170) int dim;
			if (lookupType == 1) {
				for (entry = 0; entry < this.entries; entry++) {
					last = 0.0F;
					int divisor = 1;
					for (dim = 0; dim < this.dimensions; dim++) {
						@Pc(177) int lookupOffset = entry / divisor % lookupValues;
						@Pc(189) float value = (float) this.mults[lookupOffset] * deltaValue + minimumValue + last;
						this.multiplicands[entry][dim] = value;
						if (sequenceP) {
							last = value;
						}
						divisor *= lookupValues;
					}
				}
				return;
			}
			for (entry = 0; entry < this.entries; entry++) {
				last = 0.0F;
				int multIndex = entry * this.dimensions;
				for (dim = 0; dim < this.dimensions; dim++) {
					@Pc(240) float value = (float) this.mults[multIndex] * deltaValue + minimumValue + last;
					this.multiplicands[entry][dim] = value;
					if (sequenceP) {
						last = value;
					}
					multIndex++;
				}
			}
		}
	}

	@OriginalMember(owner = "client!fq", name = "a", descriptor = "()V")
	private void computeAcceleratedHuffman() {
		@Pc(3) int[] codewords = new int[this.entries];
		@Pc(6) int[] available = new int[33];
		@Pc(14) int local14;
		@Pc(22) int local22;
		@Pc(26) int local26;
		@Pc(40) int local40;
		@Pc(49) int local49;
		@Pc(54) int local54;
		@Pc(63) int mask;
		for (@Pc(8) int i = 0; i < this.entries; i++) {
			int length = this.lengths[i];
			if (length != 0) {
				int lengthBit = 0x1 << 32 - length;
				int code = available[length];
				codewords[i] = code;
				int nextCode;
				if ((code & lengthBit) == 0) {
					nextCode = code | lengthBit;
					for (int shorterLength = length - 1; shorterLength >= 1; shorterLength--) {
						int availableCode = available[shorterLength];
						if (availableCode != code) {
							break;
						}
						mask = 0x1 << 32 - shorterLength;
						if ((availableCode & mask) != 0) {
							available[shorterLength] = available[shorterLength - 1];
							break;
						}
						available[shorterLength] = availableCode | mask;
					}
				} else {
					nextCode = available[length - 1];
				}
				available[length] = nextCode;
				for (int longerLength = length + 1; longerLength <= 32; longerLength++) {
					int availableCode = available[longerLength];
					if (availableCode == code) {
						available[longerLength] = nextCode;
					}
				}
			}
		}
		this.huffmanTree = new int[8];
		int nextFreeNode = 0;
		for (int entryIndex = 0; entryIndex < this.entries; entryIndex++) {
			int length = this.lengths[entryIndex];
			if (length != 0) {
				int codeword = codewords[entryIndex];
				int node = 0;
				for (int bitIndex = 0; bitIndex < length; bitIndex++) {
					mask = Integer.MIN_VALUE >>> bitIndex;
					if ((codeword & mask) == 0) {
						node++;
					} else {
						if (this.huffmanTree[node] == 0) {
							this.huffmanTree[node] = nextFreeNode;
						}
						node = this.huffmanTree[node];
					}
					if (node >= this.huffmanTree.length) {
						@Pc(178) int[] grownTree = new int[this.huffmanTree.length * 2];
						for (@Pc(180) int j = 0; j < this.huffmanTree.length; j++) {
							grownTree[j] = this.huffmanTree[j];
						}
						this.huffmanTree = grownTree;
					}
				}
				this.huffmanTree[node] = ~entryIndex;
				if (node >= nextFreeNode) {
					nextFreeNode = node + 1;
				}
			}
		}
	}

	@OriginalMember(owner = "client!fq", name = "b", descriptor = "()I")
	public int decode() {
		@Pc(1) int i;
		for (i = 0; this.huffmanTree[i] >= 0; i = VorbisSound.getBit() == 0 ? i + 1 : this.huffmanTree[i]) {
		}
		return ~this.huffmanTree[i];
	}

	@OriginalMember(owner = "client!fq", name = "c", descriptor = "()[F")
	public float[] decodeVector() {
		return this.multiplicands[this.decode()];
	}

	@OriginalMember(owner = "client!fq", name = "a", descriptor = "(II)I")
	public static int lookup1Values(@OriginalArg(0) int entries, @OriginalArg(1) int dimensions) {
		@Pc(10) int values;
		for (values = (int) Math.pow((double) entries, 1.0D / (double) dimensions) + 1; Utils.intPow(dimensions, values) > entries; values--) {
		}
		return values;
	}
}
