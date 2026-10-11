package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.sound.jagtheora.PcmStream;
import com.jagex.game.runetek5.sound.jagtheora.MusicPatchInfo;
import com.jagex.game.runetek5.cache.node.MidiProgramNode;
import com.jagex.game.runetek5.sound.jagtheora.MidiFileReader;
import com.jagex.game.runetek5.sound.jagtheora.MusicTrack;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket;
import com.jagex.game.runetek5.config.midi.SoundBankCache;
import com.jagex.game.runetek5.sound.AreaSoundSampleMixer;
import com.jagex.game.runetek5.sound.player.PcmPlayerManager;

@OriginalClass("client!ma")
public final class MidiPcmStream extends PcmStream {

	@OriginalMember(owner = "client!ma", name = "Hb", descriptor = "I")
	private int prioritizedTrackLength;

	@OriginalMember(owner = "client!ma", name = "Ib", descriptor = "J")
	private long nextEventTickTime;

	@OriginalMember(owner = "client!ma", name = "Kb", descriptor = "J")
	private long currentTickTime;

	@OriginalMember(owner = "client!ma", name = "Lb", descriptor = "I")
	private int prioritizedTrack;

	@OriginalMember(owner = "client!ma", name = "Mb", descriptor = "Z")
	private boolean isLooping;

	@OriginalMember(owner = "client!ma", name = "Nb", descriptor = "I")
	private int queuedTrackTick;

	@OriginalMember(owner = "client!ma", name = "Ob", descriptor = "Lclient!dl;")
	private MusicTrack queuedTrack;

	@OriginalMember(owner = "client!ma", name = "Pb", descriptor = "Z")
	private boolean queuedAllSoundOff;

	@OriginalMember(owner = "client!ma", name = "z", descriptor = "[I")
	private final int[] expression = new int[16];

	@OriginalMember(owner = "client!ma", name = "S", descriptor = "[[Lclient!vv;")
	private final MusicPatchNode[][] loopGroupNotes = new MusicPatchNode[16][128];

	@OriginalMember(owner = "client!ma", name = "H", descriptor = "[I")
	private final int[] parameters = new int[16];

	@OriginalMember(owner = "client!ma", name = "R", descriptor = "[I")
	public final int[] startOffsets = new int[16];

	@OriginalMember(owner = "client!ma", name = "J", descriptor = "[I")
	public final int[] effectSpeedScaled = new int[16];

	@OriginalMember(owner = "client!ma", name = "x", descriptor = "[I")
	private final int[] pan = new int[16];

	@OriginalMember(owner = "client!ma", name = "C", descriptor = "[I")
	private final int[] modulation = new int[16];

	@OriginalMember(owner = "client!ma", name = "cb", descriptor = "[I")
	private final int[] channelVolume = new int[16];

	@OriginalMember(owner = "client!ma", name = "F", descriptor = "[I")
	private final int[] bendRanges = new int[16];

	@OriginalMember(owner = "client!ma", name = "y", descriptor = "[I")
	public final int[] switches = new int[16];

	@OriginalMember(owner = "client!ma", name = "fb", descriptor = "[[Lclient!vv;")
	private final MusicPatchNode[][] activeNotes = new MusicPatchNode[16][128];

	@OriginalMember(owner = "client!ma", name = "ob", descriptor = "[I")
	private final int[] pitchBend = new int[16];

	@OriginalMember(owner = "client!ma", name = "qb", descriptor = "[I")
	private final int[] portamento = new int[16];

	@OriginalMember(owner = "client!ma", name = "Y", descriptor = "I")
	private final int microsecondsPerSecond = 1000000;

	@OriginalMember(owner = "client!ma", name = "tb", descriptor = "[I")
	private final int[] retriggerRates = new int[16];

	@OriginalMember(owner = "client!ma", name = "wb", descriptor = "[I")
	private final int[] defaultPrograms = new int[16];

	@OriginalMember(owner = "client!ma", name = "T", descriptor = "[I")
	private final int[] programChange = new int[16];

	@OriginalMember(owner = "client!ma", name = "kb", descriptor = "I")
	private int musicVolume = 256;

	@OriginalMember(owner = "client!ma", name = "Eb", descriptor = "[I")
	private final int[] volume = new int[16];

	@OriginalMember(owner = "client!ma", name = "yb", descriptor = "[I")
	private final int[] banks = new int[16];

	@OriginalMember(owner = "client!ma", name = "vb", descriptor = "Lclient!rf;")
	private final MidiFileReader fileReader = new MidiFileReader();

	@OriginalMember(owner = "client!ma", name = "Jb", descriptor = "Lclient!bea;")
	private final MusicPatchPcmStream patchStream = new MusicPatchPcmStream(this);

	@OriginalMember(owner = "client!ma", name = "u", descriptor = "Lclient!eq;")
	private final IterableHashTable musicPatches;

	@OriginalMember(owner = "client!ma", name = "<init>", descriptor = "()V")
	public MidiPcmStream() {
		this.musicPatches = new IterableHashTable(128);
		this.setChannelVolume(256, -1);
		this.reset(true);
	}

	@OriginalMember(owner = "client!ma", name = "<init>", descriptor = "(Lclient!ma;)V")
	public MidiPcmStream(@OriginalArg(0) MidiPcmStream mixBuss) {
		this.musicPatches = mixBuss.musicPatches;
		this.setChannelVolume(256, -1);
		this.reset(true);
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "()Lclient!jba;")
	@Override
	public synchronized PcmStream getNextStream() {
		return null;
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(ZB)V")
	private synchronized void stop(@OriginalArg(0) boolean allSoundOff) {
		this.fileReader.clear();
		this.queuedTrack = null;
		this.reset(allSoundOff);
	}

	@OriginalMember(owner = "client!ma", name = "c", descriptor = "(III)V")
	public synchronized void initialize() {
		this.initDrumChannel();
	}

	@OriginalMember(owner = "client!ma", name = "c", descriptor = "(II)V")
	private void handleEvent(@OriginalArg(1) int event) {
		@Pc(9) int status = event & 0xF0;
		@Pc(16) int channel;
		@Pc(22) int data1;
		@Pc(28) int data2;
		if (status == 128) {
			channel = event & 0xF;
			data1 = event >> 8 & 0x7F;
			data2 = event >> 16 & 0x7F;
			this.noteOff(data2, data1, channel);
		} else if (status == 144) {
			channel = event & 0xF;
			data1 = event >> 8 & 0x7F;
			data2 = event >> 16 & 0x7F;
			if (data2 <= 0) {
				this.noteOff(64, data1, channel);
			} else {
				this.noteOn(data2, data1, channel);
			}
		} else if (status == 160) {
			channel = event & 0xF;
			data1 = event >> 8 & 0x7F;
			data2 = event >> 16 & 0x7F;
			this.keyPressure(data2, channel, data1);
		} else if (status == 176) {
			channel = event & 0xF;
			data1 = event >> 8 & 0x7F;
			data2 = event >> 16 & 0x7F;
			if (data1 == 0) {
				this.banks[channel] = (data2 << 14) + (this.banks[channel] & 0xFFE03FFF);
			}
			if (data1 == 32) {
				this.banks[channel] = (data2 << 7) + (this.banks[channel] & 0xFFFFC07F);
			}
			if (data1 == 1) {
				this.modulation[channel] = (this.modulation[channel] & 0xFFFFC07F) + (data2 << 7);
			}
			if (data1 == 33) {
				this.modulation[channel] = (this.modulation[channel] & 0xFFFFFF80) + data2;
			}
			if (data1 == 5) {
				this.portamento[channel] = (this.portamento[channel] & 0xFFFFC07F) + (data2 << 7);
			}
			if (data1 == 37) {
				this.portamento[channel] = data2 + (this.portamento[channel] & 0xFFFFFF80);
			}
			if (data1 == 7) {
				this.volume[channel] = (data2 << 7) + (this.volume[channel] & 0xFFFFC07F);
			}
			if (data1 == 39) {
				this.volume[channel] = data2 + (this.volume[channel] & 0xFFFFFF80);
			}
			if (data1 == 10) {
				this.pan[channel] = (data2 << 7) + (this.pan[channel] & 0xFFFFC07F);
			}
			if (data1 == 42) {
				this.pan[channel] = data2 + (this.pan[channel] & 0xFFFFFF80);
			}
			if (data1 == 11) {
				this.expression[channel] = (data2 << 7) + (this.expression[channel] & 0xFFFFC07F);
			}
			if (data1 == 43) {
				this.expression[channel] = (this.expression[channel] & 0xFFFFFF80) + data2;
			}
			if (data1 == 64) {
				if (data2 < 64) {
					this.switches[channel] &= 0xFFFFFFFE;
				} else {
					this.switches[channel] |= 0x1;
				}
			}
			if (data1 == 65) {
				if (data2 >= 64) {
					this.switches[channel] |= 0x2;
				} else {
					this.releaseUnheldVoices(channel);
					this.switches[channel] &= 0xFFFFFFFD;
				}
			}
			if (data1 == 99) {
				this.parameters[channel] = (data2 << 7) + (this.parameters[channel] & 0x7F);
			}
			if (data1 == 98) {
				this.parameters[channel] = (this.parameters[channel] & 0x3F80) + data2;
			}
			if (data1 == 101) {
				this.parameters[channel] = (this.parameters[channel] & 0x7F) + (data2 << 7) + 16384;
			}
			if (data1 == 100) {
				this.parameters[channel] = data2 + (this.parameters[channel] & 0x3F80) + 16384;
			}
			if (data1 == 120) {
				this.stopVoices(channel);
			}
			if (data1 == 121) {
				this.resetChannel(channel);
			}
			if (data1 == 123) {
				this.releaseVoices(channel);
			}
			@Pc(504) int parameter;
			if (data1 == 6) {
				parameter = this.parameters[channel];
				if (parameter == 16384) {
					this.bendRanges[channel] = (data2 << 7) + (this.bendRanges[channel] & 0xFFFFC07F);
				}
			}
			if (data1 == 38) {
				parameter = this.parameters[channel];
				if (parameter == 16384) {
					this.bendRanges[channel] = data2 + (this.bendRanges[channel] & 0xFFFFFF80);
				}
			}
			if (data1 == 16) {
				this.startOffsets[channel] = (this.startOffsets[channel] & 0xFFFFC07F) + (data2 << 7);
			}
			if (data1 == 48) {
				this.startOffsets[channel] = (this.startOffsets[channel] & 0xFFFFFF80) + data2;
			}
			if (data1 == 81) {
				if (data2 >= 64) {
					this.switches[channel] |= 0x4;
				} else {
					this.resetEffectSwitch(channel);
					this.switches[channel] &= 0xFFFFFFFB;
				}
			}
			if (data1 == 17) {
				this.setRetriggerRate(channel, (data2 << 7) + (this.retriggerRates[channel] & 0xFFFFC07F));
			}
			if (data1 == 49) {
				this.setRetriggerRate(channel, data2 + (this.retriggerRates[channel] & 0xFFFFFF80));
			}
		} else if (status == 192) {
			channel = event & 0xF;
			data1 = event >> 8 & 0x7F;
			this.setProgram(this.banks[channel] + data1, channel);
		} else if (status == 208) {
			channel = event & 0xF;
			data1 = event >> 8 & 0x7F;
			this.channelPressure(channel, data1);
		} else if (status == 224) {
			channel = event & 0xF;
			data1 = (event >> 8 & 0x7F) + ((event & 0x7F0064) >> 9);
			this.setPitchBend(channel, data1);
		} else {
			status = event & 0xFF;
			if (status == 255) {
				this.reset(true);
			}
		}
	}

	@OriginalMember(owner = "client!ma", name = "d", descriptor = "(III)V")
	public synchronized void setChannelVolume(@OriginalArg(0) int volume, @OriginalArg(1) int channel) {
		if (channel < 0) {
			for (@Pc(5) int i = 0; i < 16; i++) {
				this.channelVolume[i] = volume;
			}
		} else {
			this.channelVolume[channel] = volume;
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(B)I")
	public int getVolume() {
		return this.musicVolume;
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(ILclient!vv;)I")
	private int calculatePitch(@OriginalArg(1) MusicPatchNode musicPatchNode) {
		@Pc(20) int pitch = musicPatchNode.notePitchAdjustment + (musicPatchNode.portamentoPitchOffset * musicPatchNode.portamentoFactor >> 12);
		pitch += (this.pitchBend[musicPatchNode.currentTrack] - 8192) * this.bendRanges[musicPatchNode.currentTrack] >> 12;
		@Pc(41) MusicPatchInfo musicPatchEnvelope = musicPatchNode.musicPatchInfo;
		@Pc(61) int local61;
		if (musicPatchEnvelope.volumeEnvelopeRelease > 0 && (musicPatchEnvelope.vibratoLfoPitch > 0 || this.modulation[musicPatchNode.currentTrack] > 0)) {
			int vibratoDepth = musicPatchEnvelope.vibratoLfoPitch << 2;
			@Pc(66) int vibratoDelay = musicPatchEnvelope.vibratoDelay << 1;
			if (musicPatchNode.tickCounter < vibratoDelay) {
				vibratoDepth = vibratoDepth * musicPatchNode.tickCounter / vibratoDelay;
			}
			vibratoDepth += this.modulation[musicPatchNode.currentTrack] >> 7;
			@Pc(100) double vibratoWave = Math.sin((double) (musicPatchNode.vibratoPhase & 0x1FF) * 0.01227184630308513D);
			pitch += (int) (vibratoWave * (double) vibratoDepth);
		}
		int playbackRate = (int) ((double) (musicPatchNode.audioFile.sampleRate * 256) * Math.pow(2.0D, (double) pitch * 3.255208333333333E-4D) / (double) PcmPlayerManager.sampleRate + 0.5D);
		return playbackRate < 1 ? 1 : playbackRate;
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(ZZLclient!dl;Z)V")
	private synchronized void loadAndPlayTrack(@OriginalArg(1) boolean allSoundOff, @OriginalArg(2) MusicTrack midiSong, @OriginalArg(3) boolean isLooping) {
		this.stop(allSoundOff);
		this.fileReader.decode(midiSong.midi);
		this.currentTickTime = 0L;
		this.isLooping = isLooping;
		@Pc(24) int trackCount = this.fileReader.getTrackCount();
		for (@Pc(26) int track = 0; track < trackCount; track++) {
			this.fileReader.getGotoTrack(track);
			this.fileReader.getTrackLength(track);
			this.fileReader.markTracePosition(track);
		}
		this.prioritizedTrack = this.fileReader.getPrioritizedTrack();
		this.prioritizedTrackLength = this.fileReader.trackLengths[this.prioritizedTrack];
		this.nextEventTickTime = this.fileReader.getTickTimestamp(this.prioritizedTrackLength);
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "(ILclient!vv;)Z")
	public boolean isPatchNodeDead(@OriginalArg(1) MusicPatchNode musicPatchNode) {
		if (musicPatchNode.stream != null) {
			return false;
		}
		if (musicPatchNode.releasePhase >= 0) {
			musicPatchNode.unlink();
			if (musicPatchNode.loopVariable > 0 && musicPatchNode == this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable]) {
				this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable] = null;
			}
		}
		return true;
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(Lclient!vv;B)I")
	private int calculatePan(@OriginalArg(0) MusicPatchNode musicPatchNode) {
		@Pc(15) int channelPan = this.pan[musicPatchNode.currentTrack];
		return channelPan >= 8192 ? 16384 - ((128 - musicPatchNode.currentPanValue) * (16384 - channelPan) + 32 >> 6) : musicPatchNode.currentPanValue * channelPan - -32 >> 6;
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(IIBI)V")
	private void noteOff(@OriginalArg(0) int velocity, @OriginalArg(1) int note, @OriginalArg(3) int channel) {
		@Pc(12) MusicPatchNode voice = this.activeNotes[channel][note];
		if (voice == null) {
			return;
		}
		this.activeNotes[channel][note] = null;
		if ((this.switches[channel] & 0x2) == 0) {
			voice.releasePhase = 0;
			return;
		}
		for (@Pc(47) MusicPatchNode other = (MusicPatchNode) this.patchStream.queue.first(); other != null; other = (MusicPatchNode) this.patchStream.queue.next()) {
			if (other.currentTrack == voice.currentTrack && other.releasePhase < 0 && voice != other) {
				voice.releasePhase = 0;
				return;
			}
		}
	}

	@OriginalMember(owner = "client!ma", name = "d", descriptor = "(II)V")
	private void resetEffectSwitch(@OriginalArg(0) int channel) {
		if ((this.switches[channel] & 0x4) == 0) {
			return;
		}
		for (@Pc(18) MusicPatchNode musicPatchNode = (MusicPatchNode) this.patchStream.queue.first(); musicPatchNode != null; musicPatchNode = (MusicPatchNode) this.patchStream.queue.next()) {
			if (channel == musicPatchNode.currentTrack) {
				musicPatchNode.retriggerPhase = 0;
			}
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(ZZ)V")
	private void reset(@OriginalArg(1) boolean allSoundOff) {
		if (allSoundOff) {
			this.stopVoices(-1);
		} else {
			this.releaseVoices(-1);
		}
		this.resetChannel(-1);
		for (@Pc(29) int channel = 0; channel < 16; channel++) {
			this.programChange[channel] = this.defaultPrograms[channel];
		}
		for (@Pc(47) int i = 0; i < 16; i++) {
			this.banks[i] = this.defaultPrograms[i] & 0xFFFFFF80;
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "([IILclient!vv;IB)Z")
	public boolean updatePatchNode(@OriginalArg(0) int[] mix, @OriginalArg(1) int length, @OriginalArg(2) MusicPatchNode musicPatchNode, @OriginalArg(3) int offset) {
		musicPatchNode.tickSampleLength = PcmPlayerManager.sampleRate / 100;
		if (musicPatchNode.releasePhase >= 0 && (musicPatchNode.stream == null || musicPatchNode.stream.isFinished())) {
			musicPatchNode.clear();
			musicPatchNode.unlink();
			if (musicPatchNode.loopVariable > 0 && musicPatchNode == this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable]) {
				this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable] = null;
			}
			return true;
		}
		@Pc(53) int portamentoFactor = musicPatchNode.portamentoFactor;
		if (portamentoFactor > 0) {
			portamentoFactor -= (int) (Math.pow(2.0D, (double) this.portamento[musicPatchNode.currentTrack] * 4.921259842519685E-4D) * 16.0D + 0.5D);
			if (portamentoFactor < 0) {
				portamentoFactor = 0;
			}
			musicPatchNode.portamentoFactor = portamentoFactor;
		}
		musicPatchNode.stream.setPlaybackRate(this.calculatePitch(musicPatchNode));
		@Pc(101) MusicPatchInfo musicPatchEnvelope = musicPatchNode.musicPatchInfo;
		@Pc(103) boolean isDead = false;
		musicPatchNode.vibratoPhase += musicPatchEnvelope.volumeEnvelopeRelease;
		musicPatchNode.tickCounter++;
		@Pc(135) double pitchScalar = (double) ((musicPatchNode.currentNotePitch - 60 << 8) + (musicPatchNode.portamentoPitchOffset * musicPatchNode.portamentoFactor >> 12)) * 5.086263020833333E-6D;
		if (musicPatchEnvelope.volumeEnvelopeDecay > 0) {
			if (musicPatchEnvelope.decayKeyScaling <= 0) {
				musicPatchNode.volumeEnvelopeTime += 128;
			} else {
				musicPatchNode.volumeEnvelopeTime += (int) (Math.pow(2.0D, (double) musicPatchEnvelope.decayKeyScaling * pitchScalar) * 128.0D + 0.5D);
			}
			if (musicPatchEnvelope.volumeEnvelopeDecay * musicPatchNode.volumeEnvelopeTime >= 819200) {
				isDead = true;
			}
		}
		if (musicPatchEnvelope.volumeEnvelopeData != null) {
			if (musicPatchEnvelope.volumeEnvelopeKeyScaling > 0) {
				musicPatchNode.volumeEnvelopePosition += (int) (Math.pow(2.0D, pitchScalar * (double) musicPatchEnvelope.volumeEnvelopeKeyScaling) * 128.0D + 0.5D);
			} else {
				musicPatchNode.volumeEnvelopePosition += 128;
			}
			while (musicPatchEnvelope.volumeEnvelopeData.length - 2 > musicPatchNode.volumeEnvelopeIndex && musicPatchNode.volumeEnvelopePosition > (musicPatchEnvelope.volumeEnvelopeData[musicPatchNode.volumeEnvelopeIndex + 2] & 0xFF) << 8) {
				musicPatchNode.volumeEnvelopeIndex += 2;
			}
			if (musicPatchNode.volumeEnvelopeIndex == musicPatchEnvelope.volumeEnvelopeData.length - 2 && musicPatchEnvelope.volumeEnvelopeData[musicPatchNode.volumeEnvelopeIndex + 1] == 0) {
				isDead = true;
			}
		}
		if (musicPatchNode.releasePhase >= 0 && musicPatchEnvelope.releaseEnvelopeData != null && (this.switches[musicPatchNode.currentTrack] & 0x1) == 0 && (musicPatchNode.loopVariable < 0 || this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable] != musicPatchNode)) {
			if (musicPatchEnvelope.releaseEnvelopeKeyScaling > 0) {
				musicPatchNode.releasePhase += (int) (Math.pow(2.0D, (double) musicPatchEnvelope.releaseEnvelopeKeyScaling * pitchScalar) * 128.0D + 0.5D);
			} else {
				musicPatchNode.releasePhase += 128;
			}
			while (musicPatchEnvelope.releaseEnvelopeData.length - 2 > musicPatchNode.releaseEnvelopeIndex && musicPatchNode.releasePhase > (musicPatchEnvelope.releaseEnvelopeData[musicPatchNode.releaseEnvelopeIndex + 2] & 0xFF) << 8) {
				musicPatchNode.releaseEnvelopeIndex += 2;
			}
			if (musicPatchEnvelope.releaseEnvelopeData.length - 2 == musicPatchNode.releaseEnvelopeIndex) {
				isDead = true;
			}
		}
		if (!isDead) {
			musicPatchNode.stream.fadeToVolumeAndPan(musicPatchNode.tickSampleLength, this.calculateVolume(musicPatchNode), this.calculatePan(musicPatchNode));
			return false;
		}
		musicPatchNode.stream.fadeOutAndStop(musicPatchNode.tickSampleLength);
		if (mix == null) {
			musicPatchNode.stream.skip(length);
		} else {
			musicPatchNode.stream.fill(mix, offset, length);
		}
		if (musicPatchNode.stream.isFading()) {
			this.patchStream.fadeOutScheduler.addSubStream(musicPatchNode.stream);
		}
		musicPatchNode.clear();
		if (musicPatchNode.releasePhase >= 0) {
			musicPatchNode.unlink();
			if (musicPatchNode.loopVariable > 0 && musicPatchNode == this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable]) {
				this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable] = null;
			}
		}
		return true;
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(IIII)V")
	private void noteOn(@OriginalArg(1) int key, @OriginalArg(2) int velocity, @OriginalArg(3) int currentTrack) {
		this.noteOff(64, velocity, currentTrack);
		if ((this.switches[currentTrack] & 0x2) != 0) {
			for (@Pc(30) MusicPatchNode held = (MusicPatchNode) this.patchStream.queue.last(); held != null; held = (MusicPatchNode) this.patchStream.queue.previous()) {
				if (currentTrack == held.currentTrack && held.releasePhase < 0) {
					this.activeNotes[currentTrack][held.currentNotePitch] = null;
					this.activeNotes[currentTrack][velocity] = held;
					@Pc(72) int pitch = (held.portamentoFactor * held.portamentoPitchOffset >> 12) + held.notePitchAdjustment;
					held.notePitchAdjustment += velocity - held.currentNotePitch << 8;
					held.portamentoPitchOffset = pitch - held.notePitchAdjustment;
					held.currentNotePitch = velocity;
					held.portamentoFactor = 4096;
					return;
				}
			}
		}
		@Pc(118) MusicPatch patch = (MusicPatch) this.musicPatches.get((long) this.programChange[currentTrack]);
		if (patch == null) {
			return;
		}
		@Pc(126) VariableRateSoundPacket sound = patch.audioFiles[velocity];
		if (sound == null) {
			return;
		}
		@Pc(133) MusicPatchNode voice = new MusicPatchNode();
		voice.currentTrack = currentTrack;
		voice.patch = patch;
		voice.audioFile = sound;
		voice.musicPatchInfo = patch.musicPatchNodes2[velocity];
		voice.loopVariable = patch.loopMode[velocity];
		voice.currentNotePitch = velocity;
		voice.maxVolumeLevel = patch.volumeOffset[velocity] * patch.baseVelocity * key * key + 1024 >> 11;
		voice.currentPanValue = patch.panOffset[velocity] & 0xFF;
		voice.notePitchAdjustment = (velocity << 8) - (patch.pitchOffset[velocity] & 0x7FFF);
		voice.releasePhase = -1;
		voice.volumeEnvelopePosition = 0;
		voice.volumeEnvelopeIndex = 0;
		voice.volumeEnvelopeTime = 0;
		voice.releaseEnvelopeIndex = 0;
		if (this.startOffsets[currentTrack] == 0) {
			voice.stream = AreaSoundSampleMixer.create(sound, this.calculatePitch(voice), this.calculateVolume(voice), this.calculatePan(voice));
		} else {
			voice.stream = AreaSoundSampleMixer.create(sound, this.calculatePitch(voice), 0, this.calculatePan(voice));
			this.applyInitialSampleOffset(patch.pitchOffset[velocity] < 0, voice);
		}
		if (patch.pitchOffset[velocity] < 0) {
			voice.stream.setNumLoops(-1);
		}
		if (voice.loopVariable >= 0) {
			@Pc(288) MusicPatchNode previous = this.loopGroupNotes[currentTrack][voice.loopVariable];
			if (previous != null && previous.releasePhase < 0) {
				this.activeNotes[currentTrack][previous.currentNotePitch] = null;
				previous.releasePhase = 0;
			}
			this.loopGroupNotes[currentTrack][voice.loopVariable] = voice;
		}
		this.patchStream.queue.addLast(voice);
		this.activeNotes[currentTrack][velocity] = voice;
	}

	@OriginalMember(owner = "client!ma", name = "e", descriptor = "(III)V")
	private void initDrumChannel() {
		this.defaultPrograms[9] = 128;
		this.banks[9] = 128;
		this.setProgram(128, 9);
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(IB)V")
	private void releaseVoices(@OriginalArg(0) int channel) {
		for (@Pc(16) MusicPatchNode voice = (MusicPatchNode) this.patchStream.queue.first(); voice != null; voice = (MusicPatchNode) this.patchStream.queue.next()) {
			if ((channel < 0 || voice.currentTrack == channel) && voice.releasePhase < 0) {
				this.activeNotes[voice.currentTrack][voice.currentNotePitch] = null;
				voice.releasePhase = 0;
			}
		}
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "(B)V")
	private void processNextMidiEvents() {
		@Pc(8) int track = this.prioritizedTrack;
		@Pc(11) int tick = this.prioritizedTrackLength;
		@Pc(14) long time = this.nextEventTickTime;
		if (this.queuedTrack != null && this.queuedTrackTick == tick) {
			this.loadAndPlayTrack(this.queuedAllSoundOff, this.queuedTrack, this.isLooping);
			this.processNextMidiEvents();
			return;
		}
		while (this.prioritizedTrackLength == tick) {
			while (this.fileReader.trackLengths[track] == tick) {
				this.fileReader.getGotoTrack(track);
				@Pc(43) int event = this.fileReader.readMessage(track);
				if (event == 1) {
					this.fileReader.setTrackDone();
					this.fileReader.markTracePosition(track);
					if (this.fileReader.isDone()) {
						if (this.queuedTrack != null) {
							this.setMusicTrack(this.queuedTrack, this.isLooping);
							this.processNextMidiEvents();
							return;
						}
						if (!this.isLooping || tick == 0) {
							this.reset(true);
							this.fileReader.clear();
							return;
						}
						this.fileReader.resetTracksFromTimestamp(time);
					}
					break;
				}
				if ((event & 0x80) != 0) {
					this.handleEvent(event);
				}
				this.fileReader.getTrackLength(track);
				this.fileReader.markTracePosition(track);
			}
			track = this.fileReader.getPrioritizedTrack();
			tick = this.fileReader.trackLengths[track];
			time = this.fileReader.getTickTimestamp(tick);
		}
		this.nextEventTickTime = time;
		this.prioritizedTrackLength = tick;
		this.prioritizedTrack = track;
		if (this.queuedTrack != null && tick > this.queuedTrackTick) {
			this.prioritizedTrack = -1;
			this.prioritizedTrackLength = this.queuedTrackTick;
			this.nextEventTickTime = this.fileReader.getTickTimestamp(this.prioritizedTrackLength);
		}
	}

	@OriginalMember(owner = "client!ma", name = "f", descriptor = "(III)V")
	private void setRetriggerRate(@OriginalArg(0) int rate, @OriginalArg(2) int rawRate) {
		this.retriggerRates[rate] = rawRate;
		this.effectSpeedScaled[rate] = (int) (Math.pow(2.0D, (double) rawRate * 5.4931640625E-4D) * 2097152.0D + 0.5D);
	}

	@OriginalMember(owner = "client!ma", name = "e", descriptor = "(II)V")
	private void releaseUnheldVoices(@OriginalArg(1) int channel) {
		if ((this.switches[channel] & 0x2) == 0) {
			return;
		}
		for (@Pc(28) MusicPatchNode voice = (MusicPatchNode) this.patchStream.queue.first(); voice != null; voice = (MusicPatchNode) this.patchStream.queue.next()) {
			if (channel == voice.currentTrack && this.activeNotes[channel][voice.currentNotePitch] == null && voice.releasePhase < 0) {
				voice.releasePhase = 0;
			}
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(I)V")
	@Override
	public synchronized void skip(@OriginalArg(0) int length) {
		if (this.fileReader.dataLoaded()) {
			@Pc(18) int timePerSample = this.fileReader.division * this.microsecondsPerSecond / PcmPlayerManager.sampleRate;
			do {
				@Pc(27) long time = this.currentTickTime + (long) timePerSample * (long) length;
				if (this.nextEventTickTime - time >= 0L) {
					this.currentTickTime = time;
					break;
				}
				@Pc(56) int count = (int) ((this.nextEventTickTime + (long) timePerSample - this.currentTickTime - 1L) / (long) timePerSample);
				this.currentTickTime += (long) count * (long) timePerSample;
				this.patchStream.skip(count);
				length -= count;
				this.processNextMidiEvents();
			} while (this.fileReader.dataLoaded());
		}
		this.patchStream.skip(length);
	}

	@OriginalMember(owner = "client!ma", name = "g", descriptor = "(III)V")
	private void channelPressure(@OriginalArg(0) int channel, @OriginalArg(1) int pressure) {
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "(I)Z")
	public synchronized boolean isPlaying() {
		return this.fileReader.dataLoaded();
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "([III)V")
	@Override
	public synchronized void fill(@OriginalArg(0) int[] mix, @OriginalArg(1) int offset, @OriginalArg(2) int length) {
		if (this.fileReader.dataLoaded()) {
			@Pc(18) int timePerSample = this.microsecondsPerSecond * this.fileReader.division / PcmPlayerManager.sampleRate;
			do {
				@Pc(27) long time = this.currentTickTime + (long) length * (long) timePerSample;
				if (this.nextEventTickTime - time >= 0L) {
					this.currentTickTime = time;
					break;
				}
				@Pc(56) int count = (int) (((long) timePerSample + this.nextEventTickTime - this.currentTickTime - 1L) / (long) timePerSample);
				this.currentTickTime += (long) timePerSample * (long) count;
				this.patchStream.fill(mix, offset, count);
				offset += count;
				length -= count;
				this.processNextMidiEvents();
			} while (this.fileReader.dataLoaded());
		}
		this.patchStream.fill(mix, offset, length);
	}

	@OriginalMember(owner = "client!ma", name = "f", descriptor = "(II)V")
	public synchronized void setVolume(@OriginalArg(1) int musicVolume) {
		this.musicVolume = musicVolume;
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "(IIII)V")
	private void keyPressure(@OriginalArg(0) int pressure, @OriginalArg(2) int key, @OriginalArg(3) int note) {
	}

	@OriginalMember(owner = "client!ma", name = "e", descriptor = "(B)V")
	public synchronized void resetMusicPatches() {
		for (@Pc(17) MusicPatch musicPatch = (MusicPatch) this.musicPatches.first(); musicPatch != null; musicPatch = (MusicPatch) this.musicPatches.next()) {
			musicPatch.clearSoundIds();
		}
	}

	@OriginalMember(owner = "client!ma", name = "g", descriptor = "(II)V")
	private void resetChannel(@OriginalArg(1) int channel) {
		if (channel < 0) {
			for (@Pc(9) int c = 0; c < 16; c++) {
				this.resetChannel(c);
			}
			return;
		}
		this.volume[channel] = 12800;
		this.pan[channel] = 8192;
		this.expression[channel] = 16383;
		this.pitchBend[channel] = 8192;
		this.modulation[channel] = 0;
		this.portamento[channel] = 8192;
		this.releaseUnheldVoices(channel);
		this.resetEffectSwitch(channel);
		this.switches[channel] = 0;
		this.parameters[channel] = 32767;
		this.bendRanges[channel] = 256;
		this.startOffsets[channel] = 0;
		this.setRetriggerRate(channel, 8192);
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(Lclient!dl;ILclient!hs;Lclient!in;I)Z")
	public synchronized boolean ready(@OriginalArg(0) MusicTrack midiSong, @OriginalArg(2) SoundBankCache soundCache, @OriginalArg(3) Js5ResourceProvider resource) {
		midiSong.buildPatchTable();
		@Pc(9) boolean isReady = true;
		@Pc(20) int[] sampleRates = new int[] { 22050 };
		for (@Pc(26) MidiProgramNode midiProgramNode = (MidiProgramNode) midiSong.table.first(); midiProgramNode != null; midiProgramNode = (MidiProgramNode) midiSong.table.next()) {
			@Pc(32) int patchId = (int) midiProgramNode.key;
			@Pc(40) MusicPatch musicPatch = (MusicPatch) this.musicPatches.get((long) patchId);
			if (musicPatch == null) {
				musicPatch = MidiPcmStream.loadMusicPatch(resource, patchId);
				if (musicPatch == null) {
					isReady = false;
					continue;
				}
				this.musicPatches.put((long) patchId, musicPatch);
			}
			if (!musicPatch.ready(soundCache, sampleRates, midiProgramNode.notes)) {
				isReady = false;
			}
		}
		if (isReady) {
			midiSong.clear();
		}
		return isReady;
	}

	@OriginalMember(owner = "client!ma", name = "d", descriptor = "(I)V")
	public synchronized void unlinkMusicPatches() {
		for (@Pc(15) MusicPatch musicPatch = (MusicPatch) this.musicPatches.first(); musicPatch != null; musicPatch = (MusicPatch) this.musicPatches.next()) {
			musicPatch.unlink();
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(Lclient!vv;I)I")
	private int calculateVolume(@OriginalArg(0) MusicPatchNode musicPatchNode) {
		if (this.channelVolume[musicPatchNode.currentTrack] == 0) {
			return 0;
		}
		@Pc(21) MusicPatchInfo musicPatchEnvelope = musicPatchNode.musicPatchInfo;
		@Pc(37) int gain = this.volume[musicPatchNode.currentTrack] * this.expression[musicPatchNode.currentTrack] + 4096 >> 13;
		@Pc(45) int gainSquared = gain * gain + 16384 >> 15;
		@Pc(54) int noteGain = gainSquared * musicPatchNode.maxVolumeLevel + 16384 >> 15;
		@Pc(63) int scaledGain = noteGain * this.musicVolume + 128 >> 8;
		gain = this.channelVolume[musicPatchNode.currentTrack] * scaledGain + 128 >> 8;
		if (musicPatchEnvelope.volumeEnvelopeDecay > 0) {
			gain = (int) ((double) gain * Math.pow(0.5D, (double) musicPatchNode.volumeEnvelopeTime * 1.953125E-5D * (double) musicPatchEnvelope.volumeEnvelopeDecay) + 0.5D);
		}
		@Pc(105) int envelopePosition;
		@Pc(113) int envelopeValue;
		@Pc(131) int envelopeKeyStart;
		@Pc(143) int envelopeKeyEnd;
		if (musicPatchEnvelope.volumeEnvelopeData != null) {
			envelopePosition = musicPatchNode.volumeEnvelopePosition;
			envelopeValue = musicPatchEnvelope.volumeEnvelopeData[musicPatchNode.volumeEnvelopeIndex + 1];
			if (musicPatchNode.volumeEnvelopeIndex < musicPatchEnvelope.volumeEnvelopeData.length - 2) {
				envelopeKeyStart = (musicPatchEnvelope.volumeEnvelopeData[musicPatchNode.volumeEnvelopeIndex] & 0xFF) << 8;
				envelopeKeyEnd = (musicPatchEnvelope.volumeEnvelopeData[musicPatchNode.volumeEnvelopeIndex + 2] & 0xFF) << 8;
				envelopeValue += (musicPatchEnvelope.volumeEnvelopeData[musicPatchNode.volumeEnvelopeIndex + 3] - envelopeValue) * (envelopePosition + -envelopeKeyStart) / (envelopeKeyEnd - envelopeKeyStart);
			}
			gain = gain * envelopeValue + 32 >> 6;
		}
		if (musicPatchNode.releasePhase > 0 && musicPatchEnvelope.releaseEnvelopeData != null) {
			envelopePosition = musicPatchNode.releasePhase;
			envelopeValue = musicPatchEnvelope.releaseEnvelopeData[musicPatchNode.releaseEnvelopeIndex + 1];
			if (musicPatchEnvelope.releaseEnvelopeData.length - 2 > musicPatchNode.releaseEnvelopeIndex) {
				envelopeKeyStart = (musicPatchEnvelope.releaseEnvelopeData[musicPatchNode.releaseEnvelopeIndex] & 0xFF) << 8;
				envelopeKeyEnd = (musicPatchEnvelope.releaseEnvelopeData[musicPatchNode.releaseEnvelopeIndex + 2] & 0xFF) << 8;
				envelopeValue += (musicPatchEnvelope.releaseEnvelopeData[musicPatchNode.releaseEnvelopeIndex + 3] - envelopeValue) * (envelopePosition + -envelopeKeyStart) / (envelopeKeyEnd - envelopeKeyStart);
			}
			gain = envelopeValue * gain + 32 >> 6;
		}
		return gain;
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(Lclient!dl;ZZ)V")
	public synchronized void setMusicTrack(@OriginalArg(0) MusicTrack midiSong, @OriginalArg(1) boolean isLooping) {
		this.loadAndPlayTrack(true, midiSong, isLooping);
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(Lclient!dl;JZZB)V")
	public synchronized void setMusicTrackAtPosition(@OriginalArg(0) MusicTrack midiSong, @OriginalArg(1) long tickPosition, @OriginalArg(2) boolean isLooping) {
		this.loadAndPlayTrack(true, midiSong, isLooping);
		this.fastForward((long) this.fileReader.division * tickPosition);
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "()Lclient!jba;")
	@Override
	public synchronized PcmStream getFirstStream() {
		return this.patchStream;
	}

	@OriginalMember(owner = "client!ma", name = "h", descriptor = "(II)V")
	private void stopVoices(@OriginalArg(1) int channel) {
		for (@Pc(14) MusicPatchNode voice = (MusicPatchNode) this.patchStream.queue.first(); voice != null; voice = (MusicPatchNode) this.patchStream.queue.next()) {
			if (channel < 0 || channel == voice.currentTrack) {
				if (voice.stream != null) {
					voice.stream.fadeOutAndStop(PcmPlayerManager.sampleRate / 100);
					if (voice.stream.isFading()) {
						this.patchStream.fadeOutScheduler.addSubStream(voice.stream);
					}
					voice.clear();
				}
				if (voice.releasePhase < 0) {
					this.activeNotes[voice.currentTrack][voice.currentNotePitch] = null;
				}
				voice.unlink();
			}
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(BII)V")
	private void setProgram(@OriginalArg(1) int program, @OriginalArg(2) int channel) {
		if (program != this.programChange[channel]) {
			this.programChange[channel] = program;
			for (@Pc(22) int group = 0; group < 128; group++) {
				this.loopGroupNotes[channel][group] = null;
			}
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(IZLclient!vv;)V")
	public void applyInitialSampleOffset(@OriginalArg(1) boolean reverseLoop, @OriginalArg(2) MusicPatchNode musicPatchNode) {
		@Pc(8) int length = musicPatchNode.audioFile.data.length;
		@Pc(36) int sampleOffset;
		if (reverseLoop && musicPatchNode.audioFile.pingPong) {
			@Pc(23) int bounceLength = length + length - musicPatchNode.audioFile.nominalBitRate;
			sampleOffset = (int) ((long) this.startOffsets[musicPatchNode.currentTrack] * (long) bounceLength >> 6);
			length <<= 0x8;
			if (length <= sampleOffset) {
				sampleOffset = length + length - sampleOffset - 1;
				musicPatchNode.stream.playBackward();
			}
		} else {
			sampleOffset = (int) ((long) length * (long) this.startOffsets[musicPatchNode.currentTrack] >> 6);
		}
		musicPatchNode.stream.setPlaybackPosition(sampleOffset);
	}

	@OriginalMember(owner = "client!ma", name = "h", descriptor = "(III)V")
	private void setPitchBend(@OriginalArg(0) int channel, @OriginalArg(2) int value) {
		this.pitchBend[channel] = value;
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(JI)V")
	private void fastForward(@OriginalArg(0) long limit) {
		while (this.nextEventTickTime <= limit) {
			@Pc(12) int track = this.prioritizedTrack;
			@Pc(15) int tick = this.prioritizedTrackLength;
			@Pc(18) long time = this.nextEventTickTime;
			while (this.prioritizedTrackLength == tick) {
				while (this.fileReader.trackLengths[track] == tick) {
					this.fileReader.getGotoTrack(track);
					@Pc(29) int event = this.fileReader.readMessage(track);
					if (event == 1) {
						this.fileReader.setTrackDone();
						this.fileReader.markTracePosition(track);
						if (this.fileReader.isDone()) {
							if (!this.isLooping || tick == 0) {
								this.reset(true);
								this.fileReader.clear();
								return;
							}
							this.fileReader.resetTracksFromTimestamp(time);
						}
						break;
					}
					if ((event & 0x80) != 0 && (event & 0xF0) != 144) {
						this.handleEvent(event);
					}
					this.fileReader.getTrackLength(track);
					this.fileReader.markTracePosition(track);
				}
				this.currentTickTime = time;
				track = this.fileReader.getPrioritizedTrack();
				tick = this.fileReader.trackLengths[track];
				time = this.fileReader.getTickTimestamp(tick);
			}
			this.prioritizedTrack = track;
			this.nextEventTickTime = time;
			this.prioritizedTrackLength = tick;
		}
	}

	@OriginalMember(owner = "client!ma", name = "f", descriptor = "(I)V")
	public synchronized void turnOffSound() {
		this.stop(true);
	}

	@OriginalMember(owner = "client!ma", name = "d", descriptor = "()I")
	@Override
	public synchronized int getActiveVoiceCount() {
		return 0;
	}
	@OriginalMember(owner = "client!qi", name = "a", descriptor = "(ILclient!in;I)Lclient!jf;")
	public static MusicPatch loadMusicPatch(@OriginalArg(1) Js5ResourceProvider patchesResource, @OriginalArg(2) int id) {
		@Pc(12) byte[] data = patchesResource.getFile(id);
		return data == null ? null : new MusicPatch(data);
	}
	@OriginalMember(owner = "client!pm", name = "a", descriptor = "(Lclient!ma;I)Lclient!ma;")
	public static MidiPcmStream getStream(@OriginalArg(0) MidiPcmStream mixBuss) {
		@Pc(14) MidiPcmStream mixBuss2 = mixBuss == null ? new MidiPcmStream() : new MidiPcmStream(mixBuss);
		mixBuss2.initialize();
		return mixBuss2;
	}
}
