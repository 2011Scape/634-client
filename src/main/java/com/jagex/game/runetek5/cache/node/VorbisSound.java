package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.midi.instrument.MusicSamepleMapping;
import com.jagex.game.runetek5.config.midi.instrument.MusicSampleCodebook;
import com.jagex.game.runetek5.config.midi.instrument.MusicSampleFloor;
import com.jagex.game.runetek5.config.midi.instrument.MusicSampleResidue;
import com.jagex.game.runetek5.config.midi.instrument.MusicSample;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!hp")
public final class VorbisSound extends Node {

	@OriginalMember(owner = "client!hp", name = "p", descriptor = "[F")
	private float[] previousWindow;

	@OriginalMember(owner = "client!hp", name = "q", descriptor = "I")
	private int sampleRate;

	@OriginalMember(owner = "client!hp", name = "r", descriptor = "[[B")
	private byte[][] packets;

	@OriginalMember(owner = "client!hp", name = "u", descriptor = "Z")
	private boolean previousNoDecodeFloor;

	@OriginalMember(owner = "client!hp", name = "E", descriptor = "I")
	private int maxBitRate;

	@OriginalMember(owner = "client!hp", name = "F", descriptor = "I")
	private int minBitRate;

	@OriginalMember(owner = "client!hp", name = "L", descriptor = "I")
	private int nominalBitRate;

	@OriginalMember(owner = "client!hp", name = "P", descriptor = "I")
	private int previousWindowEnd;

	@OriginalMember(owner = "client!hp", name = "Q", descriptor = "Z")
	private boolean negativeBitRate;

	@OriginalMember(owner = "client!hp", name = "S", descriptor = "I")
	private int previousWindowSize;

	@OriginalMember(owner = "client!hp", name = "T", descriptor = "[B")
	private byte[] packetData;

	@OriginalMember(owner = "client!hp", name = "U", descriptor = "I")
	private int currentPacket;

	@OriginalMember(owner = "client!hp", name = "V", descriptor = "I")
	private int currentOff;

	@OriginalMember(owner = "client!hp", name = "<init>", descriptor = "([B)V")
	public VorbisSound(@OriginalArg(0) byte[] bytes) {
		this.decode(bytes);
	}

	@OriginalMember(owner = "client!hp", name = "a", descriptor = "([B)V")
	private void decode(@OriginalArg(0) byte[] data) {
		@Pc(4) Packet buffer = new Packet(data);
		this.sampleRate = buffer.readInt();
		this.maxBitRate = buffer.readInt();
		this.nominalBitRate = buffer.readInt();
		this.minBitRate = buffer.readInt();
		if (this.minBitRate < 0) {
			this.minBitRate = ~this.minBitRate;
			this.negativeBitRate = true;
		}
		@Pc(40) int count = buffer.readInt();
		this.packets = new byte[count][];
		for (@Pc(46) int i = 0; i < count; i++) {
			@Pc(49) int totalSize = 0;
			@Pc(53) int size;
			do {
				size = buffer.readUByte();
				totalSize += size;
			} while (size >= 255);
			@Pc(64) byte[] packet = new byte[totalSize];
			buffer.readBytes(0, totalSize, packet);
			this.packets[i] = packet;
		}
	}

	@OriginalMember(owner = "client!hp", name = "a", descriptor = "([I)Lclient!ob;")
	public VariableRateSoundPacket toPcmSound(@OriginalArg(0) int[] sampleBudget) {
		if (sampleBudget != null && sampleBudget[0] <= 0) {
			return null;
		}
		if (this.packetData == null) {
			this.previousWindowSize = 0;
			this.previousWindow = new float[VorbisSound.blockSize1];
			this.packetData = new byte[this.maxBitRate];
			this.currentOff = 0;
			this.currentPacket = 0;
		}
		while (this.currentPacket < this.packets.length) {
			if (sampleBudget != null && sampleBudget[0] <= 0) {
				return null;
			}
			@Pc(42) float[] samples = this.decode(this.currentPacket);
			if (samples != null) {
				@Pc(47) int currentOff = this.currentOff;
				@Pc(50) int count = samples.length;
				if (count > this.maxBitRate - currentOff) {
					count = this.maxBitRate - currentOff;
				}
				for (@Pc(63) int i = 0; i < count; i++) {
					@Pc(73) int sample = (int) (samples[i] * 128.0F + 128.0F);
					if ((sample & 0xFFFFFF00) != 0) {
						sample = ~sample >> 31;
					}
					this.packetData[currentOff++] = (byte) (sample - 128);
				}
				if (sampleBudget != null) {
					sampleBudget[0] -= currentOff - this.currentOff;
				}
				this.currentOff = currentOff;
			}
			this.currentPacket++;
		}
		this.previousWindow = null;
		@Pc(129) byte[] packetData = this.packetData;
		this.packetData = null;
		return new VariableRateSoundPacket(this.sampleRate, packetData, this.nominalBitRate, this.minBitRate, this.negativeBitRate);
	}

	@OriginalMember(owner = "client!hp", name = "d", descriptor = "(I)[F")
	private float[] decode(@OriginalArg(0) int packet) {
		VorbisSound.setData(this.packets[packet]);
		VorbisSound.getBit();
		@Pc(15) int mode = MusicSample.getBits(Utils.bitLength(VorbisSound.modes.length - 1));
		@Pc(19) boolean blockFlag = VorbisSound.noResidues[mode];
		@Pc(25) int blockSize = blockFlag ? VorbisSound.blockSize1 : VorbisSound.blockSize0;
		@Pc(27) boolean prevBlockFlag = false;
		@Pc(29) boolean nextBlockFlag = false;
		if (blockFlag) {
			prevBlockFlag = VorbisSound.getBit() != 0;
			nextBlockFlag = VorbisSound.getBit() != 0;
		}
		@Pc(47) int halfBlockSize = blockSize >> 1;
		@Pc(59) int leftWindowStart;
		@Pc(67) int leftWindowEnd;
		@Pc(71) int leftWindowSize;
		if (blockFlag && !prevBlockFlag) {
			leftWindowStart = (blockSize >> 2) - (VorbisSound.blockSize0 >> 2);
			leftWindowEnd = (blockSize >> 2) + (VorbisSound.blockSize0 >> 2);
			leftWindowSize = VorbisSound.blockSize0 >> 1;
		} else {
			leftWindowStart = 0;
			leftWindowEnd = halfBlockSize;
			leftWindowSize = blockSize >> 1;
		}
		@Pc(94) int rightWindowStart;
		@Pc(104) int rightWindowEnd;
		@Pc(108) int rightWindowSize;
		if (blockFlag && !nextBlockFlag) {
			rightWindowStart = blockSize - (blockSize >> 2) - (VorbisSound.blockSize0 >> 2);
			rightWindowEnd = blockSize + (VorbisSound.blockSize0 >> 2) - (blockSize >> 2);
			rightWindowSize = VorbisSound.blockSize0 >> 1;
		} else {
			rightWindowStart = halfBlockSize;
			rightWindowEnd = blockSize;
			rightWindowSize = blockSize >> 1;
		}
		@Pc(123) MusicSamepleMapping vorbisMapping = MusicSample.mappings[VorbisSound.modes[mode]];
		@Pc(126) int mux = vorbisMapping.mux;
		@Pc(131) int floorIndex = vorbisMapping.submapFloors[mux];
		@Pc(140) boolean floorUnused = !MusicSample.floors[floorIndex].decode();
		@Pc(154) float[] local154;
		for (@Pc(144) int i = 0; i < vorbisMapping.submaps; i++) {
			@Pc(152) MusicSampleResidue vorbisResidue = MusicSample.residues[vorbisMapping.submapResidues[i]];
			float[] residueWindow = VorbisSound.window;
			vorbisResidue.decode(residueWindow, blockSize >> 1, floorUnused);
		}
		@Pc(171) int local171;
		@Pc(176) int local176;
		if (!floorUnused) {
			int mappingMux = vorbisMapping.mux;
			int floorId = vorbisMapping.submapFloors[mappingMux];
			MusicSample.floors[floorId].computeFloor(VorbisSound.window, blockSize >> 1);
		}
		@Pc(212) int local212;
		if (floorUnused) {
			for (int sampleIndex = blockSize >> 1; sampleIndex < blockSize; sampleIndex++) {
				VorbisSound.window[sampleIndex] = 0.0F;
			}
		} else {
			int halfLength = blockSize >> 1;
			int quarterLength = blockSize >> 2;
			int eighthLength = blockSize >> 3;
			@Pc(214) float[] window = VorbisSound.window;
			for (@Pc(216) int j = 0; j < halfLength; j++) {
				window[j] *= 0.5F;
			}
			for (@Pc(230) int k = halfLength; k < blockSize; k++) {
				window[k] = -window[blockSize - k - 1];
			}
			@Pc(252) float[] tableA = blockFlag ? VorbisSound.A_1 : VorbisSound.A_0;
			@Pc(258) float[] tableB = blockFlag ? VorbisSound.B_1 : VorbisSound.B_0;
			@Pc(264) float[] tableC = blockFlag ? VorbisSound.C_1 : VorbisSound.C_0;
			@Pc(270) int[] bitReverse = blockFlag ? VorbisSound.BITREVERSE_1 : VorbisSound.BITREVERSE_0;
			@Pc(307) float local307;
			@Pc(313) float local313;
			@Pc(321) float local321;
			for (@Pc(272) int m = 0; m < quarterLength; m++) {
				@Pc(289) float diff = window[m * 4] - window[blockSize - m * 4 - 1];
				float diff2 = window[m * 4 + 2] - window[blockSize - m * 4 - 3];
				float tableCos = tableA[m * 2];
				float tableSin = tableA[m * 2 + 1];
				window[blockSize - m * 4 - 1] = diff * tableCos - diff2 * tableSin;
				window[blockSize - m * 4 - 3] = diff * tableSin + diff2 * tableCos;
			}
			for (@Pc(359) int butterflyIndex = 0; butterflyIndex < eighthLength; butterflyIndex++) {
				float high3 = window[halfLength + butterflyIndex * 4 + 3];
				float high1 = window[halfLength + butterflyIndex * 4 + 1];
				float low3 = window[butterflyIndex * 4 + 3];
				@Pc(396) float low1 = window[butterflyIndex * 4 + 1];
				window[halfLength + butterflyIndex * 4 + 3] = high3 + low3;
				window[halfLength + butterflyIndex * 4 + 1] = high1 + low1;
				@Pc(430) float cosA = tableA[halfLength - butterflyIndex * 4 - 4];
				@Pc(440) float sinA = tableA[halfLength - butterflyIndex * 4 - 3];
				window[butterflyIndex * 4 + 3] = (high3 - low3) * cosA - (high1 - low1) * sinA;
				window[butterflyIndex * 4 + 1] = (high1 - low1) * cosA + (high3 - low3) * sinA;
			}
			@Pc(486) int log2BlockSize = Utils.bitLength(blockSize - 1);
			@Pc(520) int local520;
			@Pc(522) int local522;
			@Pc(527) int local527;
			@Pc(535) float local535;
			@Pc(543) float local543;
			@Pc(551) float local551;
			@Pc(559) float local559;
			@Pc(585) float local585;
			@Pc(495) int local495;
			@Pc(499) int local499;
			@Pc(501) int local501;
			@Pc(510) int local510;
			for (@Pc(488) int stage = 0; stage < log2BlockSize - 3; stage++) {
				int stageSpan = blockSize >> stage + 2;
				int twiddleStep = 0x8 << stage;
				for (int groupIndex = 0; groupIndex < 0x2 << stage; groupIndex++) {
					int upperBase = blockSize - stageSpan * 2 * groupIndex;
					int lowerBase = blockSize - stageSpan * (groupIndex * 2 + 1);
					for (int pairIndex = 0; pairIndex < blockSize >> stage + 4; pairIndex++) {
						int pairOffset = pairIndex * 4;
						float upper1 = window[upperBase - pairOffset - 1];
						float upper3 = window[upperBase - pairOffset - 3];
						float lower1 = window[lowerBase - pairOffset - 1];
						float lower3 = window[lowerBase - pairOffset - 3];
						window[upperBase - pairOffset - 1] = upper1 + lower1;
						window[upperBase - pairOffset - 3] = upper3 + lower3;
						float twiddleCos = tableA[pairIndex * twiddleStep];
						@Pc(593) float twiddleSin = tableA[pairIndex * twiddleStep + 1];
						window[lowerBase - pairOffset - 1] = (upper1 - lower1) * twiddleCos - (upper3 - lower3) * twiddleSin;
						window[lowerBase - pairOffset - 3] = (upper3 - lower3) * twiddleCos + (upper1 - lower1) * twiddleSin;
					}
				}
			}
			@Pc(673) float local673;
			for (int bitIndex = 1; bitIndex < eighthLength - 1; bitIndex++) {
				int reversedIndex = bitReverse[bitIndex];
				if (bitIndex < reversedIndex) {
					int offsetA = bitIndex * 8;
					int offsetB = reversedIndex * 8;
					float swap1 = window[offsetA + 1];
					window[offsetA + 1] = window[offsetB + 1];
					window[offsetB + 1] = swap1;
					@Pc(695) float swap3 = window[offsetA + 3];
					window[offsetA + 3] = window[offsetB + 3];
					window[offsetB + 3] = swap3;
					@Pc(717) float swap5 = window[offsetA + 5];
					window[offsetA + 5] = window[offsetB + 5];
					window[offsetB + 5] = swap5;
					@Pc(739) float swap7 = window[offsetA + 7];
					window[offsetA + 7] = window[offsetB + 7];
					window[offsetB + 7] = swap7;
				}
			}
			for (int compactIndex = 0; compactIndex < halfLength; compactIndex++) {
				window[compactIndex] = window[compactIndex * 2 + 1];
			}
			for (int unpackIndex = 0; unpackIndex < eighthLength; unpackIndex++) {
				window[blockSize - unpackIndex * 2 - 1] = window[unpackIndex * 4];
				window[blockSize - unpackIndex * 2 - 2] = window[unpackIndex * 4 + 1];
				window[blockSize - quarterLength - unpackIndex * 2 - 1] = window[unpackIndex * 4 + 2];
				window[blockSize - quarterLength - unpackIndex * 2 - 2] = window[unpackIndex * 4 + 3];
			}
			for (int twiddleIndex = 0; twiddleIndex < eighthLength; twiddleIndex++) {
				float tableCCos = tableC[twiddleIndex * 2];
				@Pc(868) float tableCSin = tableC[twiddleIndex * 2 + 1];
				@Pc(876) float valueA = window[halfLength + twiddleIndex * 2];
				float valueB = window[halfLength + twiddleIndex * 2 + 1];
				float valueC = window[blockSize - twiddleIndex * 2 - 2];
				float valueD = window[blockSize - twiddleIndex * 2 - 1];
				float rotated = tableCSin * (valueA - valueC) + tableCCos * (valueB + valueD);
				window[halfLength + twiddleIndex * 2] = (valueA + valueC + rotated) * 0.5F;
				window[blockSize - twiddleIndex * 2 - 2] = (valueA + valueC - rotated) * 0.5F;
				rotated = tableCSin * (valueB + valueD) - tableCCos * (valueA - valueC);
				window[halfLength + twiddleIndex * 2 + 1] = (valueB + rotated - valueD) * 0.5F;
				window[blockSize - twiddleIndex * 2 - 1] = (valueD + rotated - valueB) * 0.5F;
			}
			for (int rotateIndex = 0; rotateIndex < quarterLength; rotateIndex++) {
				window[rotateIndex] = window[rotateIndex * 2 + halfLength] * tableB[rotateIndex * 2] + window[rotateIndex * 2 + halfLength + 1] * tableB[rotateIndex * 2 + 1];
				window[halfLength - rotateIndex - 1] = window[rotateIndex * 2 + halfLength] * tableB[rotateIndex * 2 + 1] - window[rotateIndex * 2 + halfLength + 1] * tableB[rotateIndex * 2];
			}
			for (int negateIndex = 0; negateIndex < quarterLength; negateIndex++) {
				window[blockSize + negateIndex - quarterLength] = -window[negateIndex];
			}
			for (int shiftIndex = 0; shiftIndex < quarterLength; shiftIndex++) {
				window[shiftIndex] = window[quarterLength + shiftIndex];
			}
			for (@Pc(1111) int reflectIndex = 0; reflectIndex < quarterLength; reflectIndex++) {
				window[quarterLength + reflectIndex] = -window[quarterLength - reflectIndex - 1];
			}
			for (@Pc(1131) int copyIndex = 0; copyIndex < quarterLength; copyIndex++) {
				window[halfLength + copyIndex] = window[blockSize - copyIndex - 1];
			}
			for (@Pc(1150) int leftIndex = leftWindowStart; leftIndex < leftWindowEnd; leftIndex++) {
				float leftCurve = (float) Math.sin(((double) (leftIndex - leftWindowStart) + 0.5D) / (double) leftWindowSize * 0.5D * 3.141592653589793D);
				VorbisSound.window[leftIndex] *= (float) Math.sin((double) leftCurve * 1.5707963267948966D * (double) leftCurve);
			}
			for (@Pc(1188) int rightIndex = rightWindowStart; rightIndex < rightWindowEnd; rightIndex++) {
				float rightCurve = (float) Math.sin(((double) (rightIndex - rightWindowStart) + 0.5D) / (double) rightWindowSize * 0.5D * 3.141592653589793D + 1.5707963267948966D);
				VorbisSound.window[rightIndex] *= (float) Math.sin((double) rightCurve * 1.5707963267948966D * (double) rightCurve);
			}
		}
		@Pc(1228) float[] output = null;
		if (this.previousWindowSize > 0) {
			int outputLength = this.previousWindowSize + blockSize >> 2;
			output = new float[outputLength];
			@Pc(1254) int local1254;
			if (!this.previousNoDecodeFloor) {
				for (int prevIndex = 0; prevIndex < this.previousWindowEnd; prevIndex++) {
					int prevWindowIndex = (this.previousWindowSize >> 1) + prevIndex;
					output[prevIndex] += this.previousWindow[prevWindowIndex];
				}
			}
			if (!floorUnused) {
				for (int sampleIndex = leftWindowStart; sampleIndex < blockSize >> 1; sampleIndex++) {
					int outputIndex = output.length + sampleIndex - (blockSize >> 1);
					output[outputIndex] += VorbisSound.window[sampleIndex];
				}
			}
		}
		float[] recycledWindow = this.previousWindow;
		this.previousWindow = VorbisSound.window;
		VorbisSound.window = recycledWindow;
		this.previousWindowSize = blockSize;
		this.previousWindowEnd = rightWindowEnd - (blockSize >> 1);
		this.previousNoDecodeFloor = floorUnused;
		return output;
	}

	@OriginalMember(owner = "client!hp", name = "A", descriptor = "[F")
	public static float[] A_1;

	@OriginalMember(owner = "client!hp", name = "B", descriptor = "[Z")
	public static boolean[] noResidues;

	@OriginalMember(owner = "client!hp", name = "C", descriptor = "[B")
	public static byte[] instrumentData;

	@OriginalMember(owner = "client!hp", name = "H", descriptor = "[I")
	public static int[] BITREVERSE_0;

	@OriginalMember(owner = "client!hp", name = "I", descriptor = "[F")
	public static float[] B_0;

	@OriginalMember(owner = "client!hp", name = "J", descriptor = "I")
	public static int blockSize1;

	@OriginalMember(owner = "client!hp", name = "K", descriptor = "[F")
	public static float[] A_0;

	@OriginalMember(owner = "client!hp", name = "M", descriptor = "Z")
	public static boolean fullyCached = false;

	@OriginalMember(owner = "client!hp", name = "N", descriptor = "I")
	public static int blockSize0;

	@OriginalMember(owner = "client!hp", name = "O", descriptor = "[F")
	public static float[] B_1;

	@OriginalMember(owner = "client!hp", name = "R", descriptor = "I")
	public static int pos;

	@OriginalMember(owner = "client!hp", name = "a", descriptor = "(I)F")
	public static float float32Unpack(@OriginalArg(0) int v) {
		@Pc(3) int mantissa = v & 0x1FFFFF;
		@Pc(7) int sign = v & Integer.MIN_VALUE;
		@Pc(13) int exponent = v >> 21 & 0x3FF;
		if (sign != 0) {
			mantissa = -mantissa;
		}
		return (float) ((double) mantissa * Math.pow(2.0D, (double) (exponent - 788)));
	}

	@OriginalMember(owner = "client!hp", name = "a", descriptor = "([BI)V")
	public static void setData(@OriginalArg(0) byte[] data) {
		instrumentData = data;
		pos = 0;
		bitPos = 0;
	}

	@OriginalMember(owner = "client!hp", name = "b", descriptor = "()I")
	public static int getBit() {
		@Pc(7) int value = instrumentData[pos] >> bitPos & 0x1;
		bitPos++;
		pos += bitPos >> 3;
		bitPos &= 0x7;
		return value;
	}

	@OriginalMember(owner = "client!hp", name = "b", descriptor = "([B)V")
	public static void decodeHeader(@OriginalArg(0) byte[] header) {
		setData(header);
		blockSize0 = 0x1 << MusicSample.getBits(4);
		blockSize1 = 0x1 << MusicSample.getBits(4);
		window = new float[blockSize1];
		@Pc(24) int blockSize;
		@Pc(28) int halfSize;
		@Pc(32) int quarterSize;
		@Pc(36) int eighthSize;
		@Pc(41) int indexA;
		@Pc(86) int indexB;
		@Pc(138) int indexC;
		@Pc(191) int bitCount;
		for (@Pc(17) int i = 0; i < 2; i++) {
			blockSize = i == 0 ? blockSize0 : blockSize1;
			halfSize = blockSize >> 1;
			quarterSize = blockSize >> 2;
			eighthSize = blockSize >> 3;
			@Pc(39) float[] tableA = new float[halfSize];
			for (indexA = 0; indexA < quarterSize; indexA++) {
				tableA[indexA * 2] = (float) Math.cos((double) (indexA * 4) * 3.141592653589793D / (double) blockSize);
				tableA[indexA * 2 + 1] = -((float) Math.sin((double) (indexA * 4) * 3.141592653589793D / (double) blockSize));
			}
			@Pc(84) float[] tableB = new float[halfSize];
			for (indexB = 0; indexB < quarterSize; indexB++) {
				tableB[indexB * 2] = (float) Math.cos((double) (indexB * 2 + 1) * 3.141592653589793D / (double) (blockSize * 2));
				tableB[indexB * 2 + 1] = (float) Math.sin((double) (indexB * 2 + 1) * 3.141592653589793D / (double) (blockSize * 2));
			}
			@Pc(136) float[] tableC = new float[quarterSize];
			for (indexC = 0; indexC < eighthSize; indexC++) {
				tableC[indexC * 2] = (float) Math.cos((double) (indexC * 4 + 2) * 3.141592653589793D / (double) blockSize);
				tableC[indexC * 2 + 1] = -((float) Math.sin((double) (indexC * 4 + 2) * 3.141592653589793D / (double) blockSize));
			}
			@Pc(185) int[] bitReverse = new int[eighthSize];
			bitCount = Utils.bitLength(eighthSize - 1);
			for (@Pc(193) int j = 0; j < eighthSize; j++) {
				bitReverse[j] = MusicSample.reverseBits(bitCount, j);
			}
			if (i == 0) {
				A_0 = tableA;
				B_0 = tableB;
				C_0 = tableC;
				BITREVERSE_0 = bitReverse;
			} else {
				A_1 = tableA;
				B_1 = tableB;
				C_1 = tableC;
				BITREVERSE_1 = bitReverse;
			}
		}
		blockSize = MusicSample.getBits(8) + 1;
		MusicSample.codebooks = new MusicSampleCodebook[blockSize];
		for (halfSize = 0; halfSize < blockSize; halfSize++) {
			MusicSample.codebooks[halfSize] = new MusicSampleCodebook();
		}
		quarterSize = MusicSample.getBits(6) + 1;
		for (eighthSize = 0; eighthSize < quarterSize; eighthSize++) {
			MusicSample.getBits(16);
		}
		@Pc(269) int floorCount = MusicSample.getBits(6) + 1;
		MusicSample.floors = new MusicSampleFloor[floorCount];
		for (indexA = 0; indexA < floorCount; indexA++) {
			MusicSample.floors[indexA] = new MusicSampleFloor();
		}
		@Pc(290) int residueCount = MusicSample.getBits(6) + 1;
		MusicSample.residues = new MusicSampleResidue[residueCount];
		for (indexB = 0; indexB < residueCount; indexB++) {
			MusicSample.residues[indexB] = new MusicSampleResidue();
		}
		@Pc(311) int mappingCount = MusicSample.getBits(6) + 1;
		MusicSample.mappings = new MusicSamepleMapping[mappingCount];
		for (indexC = 0; indexC < mappingCount; indexC++) {
			MusicSample.mappings[indexC] = new MusicSamepleMapping();
		}
		@Pc(332) int modeCount = MusicSample.getBits(6) + 1;
		noResidues = new boolean[modeCount];
		modes = new int[modeCount];
		for (bitCount = 0; bitCount < modeCount; bitCount++) {
			noResidues[bitCount] = getBit() != 0;
			MusicSample.getBits(16);
			MusicSample.getBits(16);
			modes[bitCount] = MusicSample.getBits(8);
		}
		fullyCached = true;
	}

	@OriginalMember(owner = "client!hp", name = "o", descriptor = "[F")
	public static float[] C_1;

	@OriginalMember(owner = "client!hp", name = "t", descriptor = "[I")
	public static int[] BITREVERSE_1;

	@OriginalMember(owner = "client!hp", name = "v", descriptor = "[F")
	public static float[] window;

	@OriginalMember(owner = "client!hp", name = "w", descriptor = "[F")
	public static float[] C_0;

	@OriginalMember(owner = "client!hp", name = "x", descriptor = "[I")
	public static int[] modes;

	@OriginalMember(owner = "client!hp", name = "z", descriptor = "I")
	public static int bitPos;
}
