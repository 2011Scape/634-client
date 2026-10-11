package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.sound.jagtheora.PcmStream;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.sound.AreaSoundSampleMixer;
import com.jagex.game.runetek5.sound.player.PcmPlayerManager;

@OriginalClass("client!bea")
public final class MusicPatchPcmStream extends PcmStream {

	@OriginalMember(owner = "client!bea", name = "v", descriptor = "Lclient!uh;")
	public final Deque queue = new Deque();

	@OriginalMember(owner = "client!bea", name = "C", descriptor = "Lclient!sq;")
	public final PcmStreamMixer fadeOutScheduler = new PcmStreamMixer();

	@OriginalMember(owner = "client!bea", name = "z", descriptor = "Lclient!ma;")
	private final MidiPcmStream midiPcmStream;

	@OriginalMember(owner = "client!bea", name = "<init>", descriptor = "(Lclient!ma;)V")
	public MusicPatchPcmStream(@OriginalArg(0) MidiPcmStream mixBuss) {
		this.midiPcmStream = mixBuss;
	}

	@OriginalMember(owner = "client!bea", name = "b", descriptor = "()Lclient!jba;")
	@Override
	public PcmStream getNextStream() {
		@Pc(9) MusicPatchNode musicPatchNode;
		do {
			musicPatchNode = (MusicPatchNode) this.queue.next();
			if (musicPatchNode == null) {
				return null;
			}
		} while (musicPatchNode.stream == null);
		return musicPatchNode.stream;
	}

	@OriginalMember(owner = "client!bea", name = "b", descriptor = "([III)V")
	@Override
	public void fill(@OriginalArg(0) int[] mix, @OriginalArg(1) int offset, @OriginalArg(2) int length) {
		this.fadeOutScheduler.fill(mix, offset, length);
		for (@Pc(17) MusicPatchNode voice = (MusicPatchNode) this.queue.first(); voice != null; voice = (MusicPatchNode) this.queue.next()) {
			if (!this.midiPcmStream.isPatchNodeDead(voice)) {
				@Pc(27) int cursor = offset;
				@Pc(29) int remaining = length;
				do {
					if (voice.tickSampleLength >= remaining) {
						this.fillPatchNode(cursor + remaining, voice, cursor, remaining, mix);
						voice.tickSampleLength -= remaining;
						break;
					}
					this.fillPatchNode(cursor + remaining, voice, cursor, voice.tickSampleLength, mix);
					cursor += voice.tickSampleLength;
					remaining -= voice.tickSampleLength;
				} while (!this.midiPcmStream.updatePatchNode(mix, remaining, voice, cursor));
			}
		}
	}

	@OriginalMember(owner = "client!bea", name = "a", descriptor = "(IBLclient!vv;)V")
	private void skipPatchNode(@OriginalArg(0) int amount, @OriginalArg(2) MusicPatchNode musicPatchNode) {
		if ((this.midiPcmStream.switches[musicPatchNode.currentTrack] & 0x4) != 0 && musicPatchNode.releasePhase < 0) {
			@Pc(23) int step = this.midiPcmStream.effectSpeedScaled[musicPatchNode.currentTrack] / PcmPlayerManager.sampleRate;
			@Pc(33) int loopCount = (step + 1048575 - musicPatchNode.retriggerPhase) / step;
			musicPatchNode.retriggerPhase = step * amount + musicPatchNode.retriggerPhase & 0xFFFFF;
			if (amount >= loopCount) {
				if (this.midiPcmStream.startOffsets[musicPatchNode.currentTrack] == 0) {
					musicPatchNode.stream = AreaSoundSampleMixer.create(musicPatchNode.audioFile, musicPatchNode.stream.getAbsolutePlaybackRate(), musicPatchNode.stream.getTargetVolume(), musicPatchNode.stream.getPanPosition());
				} else {
					musicPatchNode.stream = AreaSoundSampleMixer.create(musicPatchNode.audioFile, musicPatchNode.stream.getAbsolutePlaybackRate(), 0, musicPatchNode.stream.getPanPosition());
					this.midiPcmStream.applyInitialSampleOffset(musicPatchNode.patch.pitchOffset[musicPatchNode.currentNotePitch] < 0, musicPatchNode);
				}
				if (musicPatchNode.patch.pitchOffset[musicPatchNode.currentNotePitch] < 0) {
					musicPatchNode.stream.setNumLoops(-1);
				}
				amount = musicPatchNode.retriggerPhase / step;
			}
		}
		musicPatchNode.stream.skip(amount);
	}

	@OriginalMember(owner = "client!bea", name = "d", descriptor = "()I")
	@Override
	public int getActiveVoiceCount() {
		return 0;
	}

	@OriginalMember(owner = "client!bea", name = "a", descriptor = "(I)V")
	@Override
	public void skip(@OriginalArg(0) int length) {
		this.fadeOutScheduler.skip(length);
		for (@Pc(15) MusicPatchNode voice = (MusicPatchNode) this.queue.first(); voice != null; voice = (MusicPatchNode) this.queue.next()) {
			if (!this.midiPcmStream.isPatchNodeDead(voice)) {
				@Pc(25) int remaining = length;
				do {
					if (remaining <= voice.tickSampleLength) {
						this.skipPatchNode(remaining, voice);
						voice.tickSampleLength -= remaining;
						break;
					}
					this.skipPatchNode(voice.tickSampleLength, voice);
					remaining -= voice.tickSampleLength;
				} while (!this.midiPcmStream.updatePatchNode(null, remaining, voice, 0));
			}
		}
	}

	@OriginalMember(owner = "client!bea", name = "a", descriptor = "(ILclient!vv;IBI[I)V")
	private void fillPatchNode(@OriginalArg(0) int endOffset, @OriginalArg(1) MusicPatchNode musicPatchNode, @OriginalArg(2) int offset, @OriginalArg(4) int length, @OriginalArg(5) int[] mix) {
		if ((this.midiPcmStream.switches[musicPatchNode.currentTrack] & 0x4) != 0 && musicPatchNode.releasePhase < 0) {
			@Pc(35) int step = this.midiPcmStream.effectSpeedScaled[musicPatchNode.currentTrack] / PcmPlayerManager.sampleRate;
			while (true) {
				@Pc(45) int loopCount = (step + 1048575 - musicPatchNode.retriggerPhase) / step;
				if (length < loopCount) {
					musicPatchNode.retriggerPhase += length * step;
					break;
				}
				musicPatchNode.stream.fill(mix, offset, loopCount);
				offset += loopCount;
				musicPatchNode.retriggerPhase += step * loopCount - 1048576;
				length -= loopCount;
				@Pc(80) int fadeLength = PcmPlayerManager.sampleRate / 100;
				@Pc(84) int maxFadeLength = 262144 / step;
				if (maxFadeLength < fadeLength) {
					fadeLength = maxFadeLength;
				}
				@Pc(92) AreaSoundStream soundStream = musicPatchNode.stream;
				if (this.midiPcmStream.startOffsets[musicPatchNode.currentTrack] == 0) {
					musicPatchNode.stream = AreaSoundSampleMixer.create(musicPatchNode.audioFile, soundStream.getAbsolutePlaybackRate(), soundStream.getTargetVolume(), soundStream.getPanPosition());
				} else {
					musicPatchNode.stream = AreaSoundSampleMixer.create(musicPatchNode.audioFile, soundStream.getAbsolutePlaybackRate(), 0, soundStream.getPanPosition());
					this.midiPcmStream.applyInitialSampleOffset(musicPatchNode.patch.pitchOffset[musicPatchNode.currentNotePitch] < 0, musicPatchNode);
					musicPatchNode.stream.fadeToVolume(fadeLength, soundStream.getTargetVolume());
				}
				if (musicPatchNode.patch.pitchOffset[musicPatchNode.currentNotePitch] < 0) {
					musicPatchNode.stream.setNumLoops(-1);
				}
				soundStream.fadeOutAndStop(fadeLength);
				soundStream.fill(mix, offset, endOffset - offset);
				if (soundStream.isFading()) {
					this.fadeOutScheduler.addSubStream(soundStream);
				}
			}
		}
		musicPatchNode.stream.fill(mix, offset, length);
	}

	@OriginalMember(owner = "client!bea", name = "a", descriptor = "()Lclient!jba;")
	@Override
	public PcmStream getFirstStream() {
		@Pc(9) MusicPatchNode musicPatchNode = (MusicPatchNode) this.queue.first();
		if (musicPatchNode == null) {
			return null;
		} else if (musicPatchNode.stream == null) {
			return this.getNextStream();
		} else {
			return musicPatchNode.stream;
		}
	}
}
