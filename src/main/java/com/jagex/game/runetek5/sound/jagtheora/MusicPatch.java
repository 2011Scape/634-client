package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.sound.jagtheora.MusicPatchInfo;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket;
import com.jagex.game.runetek5.config.midi.SoundBankCache;

@OriginalClass("client!jf")
public final class MusicPatch extends Node {

	@OriginalMember(owner = "client!jf", name = "s", descriptor = "[B")
	public byte[] panOffset;

	@OriginalMember(owner = "client!jf", name = "t", descriptor = "[I")
	private int[] sampleOffsets;

	@OriginalMember(owner = "client!jf", name = "v", descriptor = "[S")
	public short[] pitchOffset;

	@OriginalMember(owner = "client!jf", name = "w", descriptor = "[Lclient!ho;")
	public MusicPatchInfo[] musicPatchNodes2;

	@OriginalMember(owner = "client!jf", name = "x", descriptor = "I")
	public int baseVelocity;

	@OriginalMember(owner = "client!jf", name = "y", descriptor = "[B")
	public byte[] loopMode;

	@OriginalMember(owner = "client!jf", name = "z", descriptor = "[B")
	public byte[] volumeOffset;

	@OriginalMember(owner = "client!jf", name = "A", descriptor = "[Lclient!ob;")
	public VariableRateSoundPacket[] audioFiles;

	@OriginalMember(owner = "client!jf", name = "<init>", descriptor = "()V")
	private MusicPatch() {
	}

	@OriginalMember(owner = "client!jf", name = "<init>", descriptor = "([B)V")
	public MusicPatch(@OriginalArg(0) byte[] fileData) {
		this.pitchOffset = new short[128];
		this.panOffset = new byte[128];
		this.loopMode = new byte[128];
		this.volumeOffset = new byte[128];
		this.audioFiles = new VariableRateSoundPacket[128];
		this.musicPatchNodes2 = new MusicPatchInfo[128];
		this.sampleOffsets = new int[128];
		@Pc(36) Packet packet = new Packet(fileData);
		@Pc(38) int loopModeRunCount;
		for (loopModeRunCount = 0; packet.buffer[loopModeRunCount + packet.currentPosition] != 0; loopModeRunCount++) {
		}
		@Pc(52) byte[] loopModeRuns = new byte[loopModeRunCount];
		for (@Pc(54) int i = 0; i < loopModeRunCount; i++) {
			loopModeRuns[i] = packet.readByte();
		}
		packet.currentPosition++;
		loopModeRunCount++;
		@Pc(76) int currentPosition = packet.currentPosition;
		packet.currentPosition += loopModeRunCount;
		@Pc(84) int panRunCount;
		for (panRunCount = 0; packet.buffer[panRunCount + packet.currentPosition] != 0; panRunCount++) {
		}
		@Pc(101) byte[] panRuns = new byte[panRunCount];
		for (@Pc(103) int j = 0; j < panRunCount; j++) {
			panRuns[j] = packet.readByte();
		}
		panRunCount++;
		packet.currentPosition++;
		@Pc(129) int panValuePosition = packet.currentPosition;
		packet.currentPosition += panRunCount;
		@Pc(137) int patchInfoRunCount;
		for (patchInfoRunCount = 0; packet.buffer[packet.currentPosition + patchInfoRunCount] != 0; patchInfoRunCount++) {
		}
		@Pc(151) byte[] patchInfoRuns = new byte[patchInfoRunCount];
		for (@Pc(153) int k = 0; k < patchInfoRunCount; k++) {
			patchInfoRuns[k] = packet.readByte();
		}
		packet.currentPosition++;
		patchInfoRunCount++;
		@Pc(175) byte[] runPatchInfoIndices = new byte[patchInfoRunCount];
		@Pc(184) int patchInfoCount;
		@Pc(188) int index;
		@Pc(194) int local194;
		if (patchInfoRunCount > 1) {
			runPatchInfoIndices[1] = 1;
			patchInfoCount = 2;
			@Pc(186) int patchInfoIndex = 1;
			for (index = 2; index < patchInfoRunCount; index++) {
				int encodedPatchInfoIndex = packet.readUByte();
				if (encodedPatchInfoIndex == 0) {
					patchInfoIndex = patchInfoCount++;
				} else {
					if (encodedPatchInfoIndex <= patchInfoIndex) {
						encodedPatchInfoIndex--;
					}
					patchInfoIndex = encodedPatchInfoIndex;
				}
				runPatchInfoIndices[index] = (byte) patchInfoIndex;
			}
		} else {
			patchInfoCount = patchInfoRunCount;
		}
		@Pc(226) MusicPatchInfo[] musicPatchEnvelopes = new MusicPatchInfo[patchInfoCount];
		for (index = 0; index < musicPatchEnvelopes.length; index++) {
			@Pc(238) MusicPatchInfo musicPatchEnvelope = musicPatchEnvelopes[index] = new MusicPatchInfo();
			@Pc(242) int segmentCount = packet.readUByte();
			if (segmentCount > 0) {
				musicPatchEnvelope.volumeEnvelopeData = new byte[segmentCount * 2];
			}
			segmentCount = packet.readUByte();
			if (segmentCount > 0) {
				musicPatchEnvelope.releaseEnvelopeData = new byte[segmentCount * 2 + 2];
				musicPatchEnvelope.releaseEnvelopeData[1] = 64;
			}
		}
		int curvePointCount = packet.readUByte();
		@Pc(297) byte[] volumeCurve = curvePointCount > 0 ? new byte[curvePointCount * 2] : null;
		curvePointCount = packet.readUByte();
		@Pc(313) byte[] panCurve = curvePointCount > 0 ? new byte[curvePointCount * 2] : null;
		@Pc(315) int keyRunCount;
		for (keyRunCount = 0; packet.buffer[packet.currentPosition + keyRunCount] != 0; keyRunCount++) {
		}
		@Pc(330) byte[] keyRuns = new byte[keyRunCount];
		for (@Pc(332) int m = 0; m < keyRunCount; m++) {
			keyRuns[m] = packet.readByte();
		}
		packet.currentPosition++;
		keyRunCount++;
		@Pc(357) int accumulator = 0;
		for (@Pc(359) int key = 0; key < 128; key++) {
			accumulator += packet.readUByte();
			this.pitchOffset[key] = (short) accumulator;
		}
		accumulator = 0;
		for (@Pc(381) int key = 0; key < 128; key++) {
			accumulator += packet.readUByte();
			this.pitchOffset[key] = (short) (this.pitchOffset[key] + (accumulator << 8));
		}
		@Pc(408) int runRemaining = 0;
		@Pc(410) int runIndex = 0;
		@Pc(412) int sampleOffset = 0;
		for (@Pc(414) int key = 0; key < 128; key++) {
			if (runRemaining == 0) {
				if (keyRuns.length > runIndex) {
					runRemaining = keyRuns[runIndex++];
				} else {
					runRemaining = -1;
				}
				sampleOffset = packet.readVarInt();
			}
			this.pitchOffset[key] = (short) (this.pitchOffset[key] + ((sampleOffset - 1 & 0x2) << 14));
			runRemaining--;
			this.sampleOffsets[key] = sampleOffset;
		}
		runIndex = 0;
		runRemaining = 0;
		@Pc(472) int loopModeValue = 0;
		for (@Pc(474) int key = 0; key < 128; key++) {
			if (this.sampleOffsets[key] != 0) {
				if (runRemaining == 0) {
					loopModeValue = packet.buffer[currentPosition++] - 1;
					if (runIndex < loopModeRuns.length) {
						runRemaining = loopModeRuns[runIndex++];
					} else {
						runRemaining = -1;
					}
				}
				runRemaining--;
				this.loopMode[key] = (byte) loopModeValue;
			}
		}
		runIndex = 0;
		runRemaining = 0;
		@Pc(524) int panValue = 0;
		for (@Pc(526) int key = 0; key < 128; key++) {
			if (this.sampleOffsets[key] != 0) {
				if (runRemaining == 0) {
					if (runIndex >= panRuns.length) {
						runRemaining = -1;
					} else {
						runRemaining = panRuns[runIndex++];
					}
					panValue = packet.buffer[panValuePosition++] + 16 << 2;
				}
				runRemaining--;
				this.panOffset[key] = (byte) panValue;
			}
		}
		runRemaining = 0;
		runIndex = 0;
		@Pc(580) MusicPatchInfo musicPatchEnvelope2 = null;
		for (@Pc(582) int key = 0; key < 128; key++) {
			if (this.sampleOffsets[key] != 0) {
				if (runRemaining == 0) {
					musicPatchEnvelope2 = musicPatchEnvelopes[runPatchInfoIndices[runIndex]];
					if (runIndex < patchInfoRuns.length) {
						runRemaining = patchInfoRuns[runIndex++];
					} else {
						runRemaining = -1;
					}
				}
				runRemaining--;
				this.musicPatchNodes2[key] = musicPatchEnvelope2;
			}
		}
		runRemaining = 0;
		runIndex = 0;
		@Pc(630) int volumeValue = 0;
		for (@Pc(632) int key = 0; key < 128; key++) {
			if (runRemaining == 0) {
				if (keyRuns.length > runIndex) {
					runRemaining = keyRuns[runIndex++];
				} else {
					runRemaining = -1;
				}
				if (this.sampleOffsets[key] > 0) {
					volumeValue = packet.readUByte() + 1;
				}
			}
			this.volumeOffset[key] = (byte) volumeValue;
			runRemaining--;
		}
		this.baseVelocity = packet.readUByte() + 1;
		@Pc(699) int local699;
		for (@Pc(688) int envelopeIndex = 0; envelopeIndex < patchInfoCount; envelopeIndex++) {
			@Pc(694) MusicPatchInfo musicPatchEnvelope3 = musicPatchEnvelopes[envelopeIndex];
			if (musicPatchEnvelope3.volumeEnvelopeData != null) {
				for (int dataIndex = 1; dataIndex < musicPatchEnvelope3.volumeEnvelopeData.length; dataIndex += 2) {
					musicPatchEnvelope3.volumeEnvelopeData[dataIndex] = packet.readByte();
				}
			}
			if (musicPatchEnvelope3.releaseEnvelopeData != null) {
				for (int dataIndex = 3; dataIndex < musicPatchEnvelope3.releaseEnvelopeData.length - 2; dataIndex += 2) {
					musicPatchEnvelope3.releaseEnvelopeData[dataIndex] = packet.readByte();
				}
			}
		}
		@Pc(744) int local744;
		if (volumeCurve != null) {
			for (int curveIndex = 1; curveIndex < volumeCurve.length; curveIndex += 2) {
				volumeCurve[curveIndex] = packet.readByte();
			}
		}
		if (panCurve != null) {
			for (int curveIndex = 1; curveIndex < panCurve.length; curveIndex += 2) {
				panCurve[curveIndex] = packet.readByte();
			}
		}
		@Pc(793) int local793;
		for (int envelopeIndex = 0; envelopeIndex < patchInfoCount; envelopeIndex++) {
			@Pc(786) MusicPatchInfo musicPatchEnvelope4 = musicPatchEnvelopes[envelopeIndex];
			if (musicPatchEnvelope4.releaseEnvelopeData != null) {
				accumulator = 0;
				for (int dataIndex = 2; dataIndex < musicPatchEnvelope4.releaseEnvelopeData.length; dataIndex += 2) {
					accumulator = accumulator + packet.readUByte() + 1;
					musicPatchEnvelope4.releaseEnvelopeData[dataIndex] = (byte) accumulator;
				}
			}
		}
		@Pc(835) int local835;
		for (int envelopeIndex = 0; envelopeIndex < patchInfoCount; envelopeIndex++) {
			@Pc(828) MusicPatchInfo musicPatchEnvelope5 = musicPatchEnvelopes[envelopeIndex];
			if (musicPatchEnvelope5.volumeEnvelopeData != null) {
				accumulator = 0;
				for (int dataIndex = 2; dataIndex < musicPatchEnvelope5.volumeEnvelopeData.length; dataIndex += 2) {
					accumulator = accumulator + packet.readUByte() + 1;
					musicPatchEnvelope5.volumeEnvelopeData[dataIndex] = (byte) accumulator;
				}
			}
		}
		@Pc(910) int local910;
		@Pc(933) int local933;
		@Pc(939) byte endKey;
		@Pc(959) int interpAccumulator;
		@Pc(961) int interpKey;
		@Pc(971) int interpValue;
		@Pc(904) byte startKey;
		@Pc(1016) int tailKey;
		if (volumeCurve != null) {
			accumulator = packet.readUByte();
			volumeCurve[0] = (byte) accumulator;
			for (int curveIndex = 2; curveIndex < volumeCurve.length; curveIndex += 2) {
				accumulator = accumulator + packet.readUByte() + 1;
				volumeCurve[curveIndex] = (byte) accumulator;
			}
			startKey = volumeCurve[0];
			@Pc(908) byte startVolume = volumeCurve[1];
			for (int key = 0; key < startKey; key++) {
				this.volumeOffset[key] = (byte) (startVolume * this.volumeOffset[key] + 32 >> 6);
			}
			int curveIndex = 2;
			while (curveIndex < volumeCurve.length) {
				endKey = volumeCurve[curveIndex];
				@Pc(945) byte endVolume = volumeCurve[curveIndex + 1];
				interpAccumulator = (endKey - startKey) / 2 + (endKey - startKey) * startVolume;
				for (interpKey = startKey; interpKey < endKey; interpKey++) {
					interpValue = MusicPatch.floorDivide(endKey - startKey, interpAccumulator);
					this.volumeOffset[interpKey] = (byte) (this.volumeOffset[interpKey] * interpValue + 32 >> 6);
					interpAccumulator += endVolume - startVolume;
				}
				startKey = endKey;
				curveIndex += 2;
				startVolume = endVolume;
			}
			for (tailKey = startKey; tailKey < 128; tailKey++) {
				this.volumeOffset[tailKey] = (byte) (startVolume * this.volumeOffset[tailKey] + 32 >> 6);
			}
		}
		@Pc(1084) int local1084;
		if (panCurve != null) {
			accumulator = packet.readUByte();
			panCurve[0] = (byte) accumulator;
			for (int curveIndex = 2; curveIndex < panCurve.length; curveIndex += 2) {
				accumulator = packet.readUByte() + accumulator + 1;
				panCurve[curveIndex] = (byte) accumulator;
			}
			startKey = panCurve[0];
			int startPan = panCurve[1] << 1;
			for (int key = 0; key < startKey; key++) {
				int keyPan = (this.panOffset[key] & 0xFF) + startPan;
				if (keyPan < 0) {
					keyPan = 0;
				}
				if (keyPan > 128) {
					keyPan = 128;
				}
				this.panOffset[key] = (byte) keyPan;
			}
			@Pc(1141) int local1141;
			for (int curveIndex = 2; curveIndex < panCurve.length; curveIndex += 2) {
				endKey = panCurve[curveIndex];
				int endPan = panCurve[curveIndex + 1] << 1;
				interpAccumulator = (endKey - startKey) * startPan + (endKey - startKey) / 2;
				for (interpKey = startKey; interpKey < endKey; interpKey++) {
					interpValue = MusicPatch.floorDivide(endKey - startKey, interpAccumulator);
					@Pc(1176) int adjustedPan = (this.panOffset[interpKey] & 0xFF) + interpValue;
					if (adjustedPan < 0) {
						adjustedPan = 0;
					}
					if (adjustedPan > 128) {
						adjustedPan = 128;
					}
					this.panOffset[interpKey] = (byte) adjustedPan;
					interpAccumulator += endPan - startPan;
				}
				startPan = endPan;
				startKey = endKey;
			}
			for (tailKey = startKey; tailKey < 128; tailKey++) {
				int clampedPan = startPan + (this.panOffset[tailKey] & 0xFF);
				if (clampedPan < 0) {
					clampedPan = 0;
				}
				if (clampedPan > 128) {
					clampedPan = 128;
				}
				this.panOffset[tailKey] = (byte) clampedPan;
			}
		}
		for (int envelopeIndex = 0; envelopeIndex < patchInfoCount; envelopeIndex++) {
			musicPatchEnvelopes[envelopeIndex].volumeEnvelopeDecay = packet.readUByte();
		}
		for (int envelopeIndex = 0; envelopeIndex < patchInfoCount; envelopeIndex++) {
			@Pc(1288) MusicPatchInfo musicPatchEnvelope6 = musicPatchEnvelopes[envelopeIndex];
			if (musicPatchEnvelope6.volumeEnvelopeData != null) {
				musicPatchEnvelope6.volumeEnvelopeKeyScaling = packet.readUByte();
			}
			if (musicPatchEnvelope6.releaseEnvelopeData != null) {
				musicPatchEnvelope6.releaseEnvelopeKeyScaling = packet.readUByte();
			}
			if (musicPatchEnvelope6.volumeEnvelopeDecay > 0) {
				musicPatchEnvelope6.decayKeyScaling = packet.readUByte();
			}
		}
		for (int envelopeIndex = 0; envelopeIndex < patchInfoCount; envelopeIndex++) {
			musicPatchEnvelopes[envelopeIndex].volumeEnvelopeRelease = packet.readUByte();
		}
		for (int envelopeIndex = 0; envelopeIndex < patchInfoCount; envelopeIndex++) {
			@Pc(1346) MusicPatchInfo musicPatchEnvelope7 = musicPatchEnvelopes[envelopeIndex];
			if (musicPatchEnvelope7.volumeEnvelopeRelease > 0) {
				musicPatchEnvelope7.vibratoLfoPitch = packet.readUByte();
			}
		}
		for (int envelopeIndex = 0; envelopeIndex < patchInfoCount; envelopeIndex++) {
			@Pc(1366) MusicPatchInfo musicPatchEnvelope8 = musicPatchEnvelopes[envelopeIndex];
			if (musicPatchEnvelope8.vibratoLfoPitch > 0) {
				musicPatchEnvelope8.vibratoDelay = packet.readUByte();
			}
		}
	}

	@OriginalMember(owner = "client!jf", name = "a", descriptor = "(Lclient!hs;[I[BI)Z")
	public boolean ready(@OriginalArg(0) SoundBankCache soundCache, @OriginalArg(1) int[] sampleRates, @OriginalArg(2) byte[] notes) {
		@Pc(7) boolean isReady = true;
		@Pc(9) int previousSampleOffset = 0;
		@Pc(11) VariableRateSoundPacket variableRateSoundPacket = null;
		for (@Pc(13) int i = 0; i < 128; i++) {
			if (notes == null || notes[i] != 0) {
				@Pc(28) int sampleOffset = this.sampleOffsets[i];
				if (sampleOffset != 0) {
					if (sampleOffset != previousSampleOffset) {
						previousSampleOffset = sampleOffset--;
						if ((sampleOffset & 0x1) == 0) {
							variableRateSoundPacket = soundCache.listEffect(sampleOffset >> 2, sampleRates);
						} else {
							variableRateSoundPacket = soundCache.listMusic(sampleRates, sampleOffset >> 2);
						}
						if (variableRateSoundPacket == null) {
							isReady = false;
						}
					}
					if (variableRateSoundPacket != null) {
						this.audioFiles[i] = variableRateSoundPacket;
						this.sampleOffsets[i] = 0;
					}
				}
			}
		}
		return isReady;
	}

	@OriginalMember(owner = "client!jf", name = "b", descriptor = "(B)V")
	public void clearSoundIds() {
		this.sampleOffsets = null;
	}
	@OriginalMember(owner = "client!uaa", name = "a", descriptor = "(III)I")
	public static int floorDivide(@OriginalArg(0) int divisor, @OriginalArg(2) int dividend) {
		@Pc(7) int signBit = dividend >>> 31;
		return (dividend + signBit) / divisor - signBit;
	}
}
