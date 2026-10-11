package com.jagex.game.runetek5.sound;

import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.sound.jagtheora.MidiPcmStream;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.midi.SoundBankCache;
import com.jagex.game.runetek5.config.midi.VorbisNode;
import com.jagex.game.runetek5.sound.jagtheora.MusicTrack;
import com.jagex.game.runetek5.sound.jagtheora.OggKateDecoder;
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer;
import com.jagex.game.runetek5.sound.player.song.SongPlayer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.sound.player.PcmPlayer;
import com.jagex.game.runetek5.client.ClientState;

public final class SoundResourceManager {
	@OriginalMember(owner = "client!mda", name = "a", descriptor = "Lclient!ma;")
	public static MidiPcmStream midiPcmStream;

	@OriginalMember(owner = "client!pp", name = "A", descriptor = "I")
	public static int musicFadeStep;

	@OriginalMember(owner = "client!aga", name = "d", descriptor = "Lclient!in;")
	public static Js5ResourceProvider MIDI_INSTRUMENT_PROVIDER;

	@OriginalMember(owner = "client!iia", name = "a", descriptor = "Lclient!in;")
	public static Js5ResourceProvider SOUND_EFFECT_PROVIDER;

	@OriginalMember(owner = "client!vi", name = "i", descriptor = "Lclient!ma;")
	public static MidiPcmStream pendingMidiStream;

	@OriginalMember(owner = "client!av", name = "z", descriptor = "J")
	public static long songStartPosition;

	@OriginalMember(owner = "client!ko", name = "n", descriptor = "Lclient!in;")
	public static Js5ResourceProvider SOUND_MIDI_PROVIDER;

	private SoundResourceManager() {
		/* empty */
	}
	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(Z)Lclient!ma;")
	public static MidiPcmStream getMidiPcmStream() {
		return SoundResourceManager.midiPcmStream;
	}
	@OriginalMember(owner = "client!er", name = "a", descriptor = "(II)Ljava/lang/String;")
	public static String getVideoSubtitleText(@OriginalArg(0) int videoId) {
		@Pc(17) VorbisNode videoType = (VorbisNode) VorbisNode.INDEX36_FILE_CACHE.get((long) videoId);
		if (videoType != null) {
			@Pc(26) OggKateDecoder oggKateStream = videoType.vfReference.getKateDecoder();
			if (oggKateStream != null) {
				@Pc(35) double time = videoType.vfReference.getTime();
				if ((double) oggKateStream.getStartTime() <= time && time <= (double) oggKateStream.getEndTime()) {
					return oggKateStream.getSubtitle();
				}
			}
		}
		return null;
	}

	@OriginalMember(owner = "client!mha", name = "b", descriptor = "(I)V")
	public static void stopMusic() {
		SoundResourceManager.midiPcmStream.turnOffSound();
		SoundResourceManager.pendingMidiStream = null;
		SoundResourceManager.songLoadState = 1;
		AreaSoundPlayer.MUSIC_RESOURCE = null;
	}

	@OriginalMember(owner = "client!tia", name = "b", descriptor = "(Z)Z")
	public static boolean loadPendingSong() {
		try {
			if (SoundResourceManager.songLoadState == 2) {
				if (SoundResourceManager.pendingMusicTrack == null) {
					SoundResourceManager.pendingMusicTrack = MusicTrack.loadMusictrack(AreaSoundPlayer.MUSIC_RESOURCE, SongPlayer.musicArchiveId, SongPlayer.musicFileId);
					if (SoundResourceManager.pendingMusicTrack == null) {
						return false;
					}
				}
				if (SoundResourceManager.pendingSoundBankCache == null) {
					SoundResourceManager.pendingSoundBankCache = new SoundBankCache(SoundResourceManager.SOUND_EFFECT_PROVIDER, SoundResourceManager.MIDI_INSTRUMENT_PROVIDER);
				}
				@Pc(31) MidiPcmStream mixBuss = SoundResourceManager.midiPcmStream;
				if (SoundResourceManager.pendingMidiStream != null) {
					mixBuss = SoundResourceManager.pendingMidiStream;
				}
				if (mixBuss.ready(SoundResourceManager.pendingMusicTrack, SoundResourceManager.pendingSoundBankCache, SoundResourceManager.SOUND_MIDI_PROVIDER)) {
					SoundResourceManager.midiPcmStream = mixBuss;
					SoundResourceManager.midiPcmStream.resetMusicPatches();
					@Pc(61) int channel;
					if (SoundResourceManager.songFadeInRate <= 0) {
						SoundResourceManager.songLoadState = 0;
						SoundResourceManager.midiPcmStream.setVolume(SoundResourceManager.SOUND_LEVEL);
						for (channel = 0; channel < SongPlayer.channelVolumes.length; channel++) {
							SoundResourceManager.midiPcmStream.setChannelVolume(SongPlayer.channelVolumes[channel], channel);
							SongPlayer.channelVolumes[channel] = 255;
						}
					} else {
						SoundResourceManager.songLoadState = 3;
						SoundResourceManager.midiPcmStream.setVolume(SoundResourceManager.songFadeInRate > SoundResourceManager.SOUND_LEVEL ? SoundResourceManager.SOUND_LEVEL : SoundResourceManager.songFadeInRate);
						for (channel = 0; channel < SongPlayer.channelVolumes.length; channel++) {
							SoundResourceManager.midiPcmStream.setChannelVolume(SongPlayer.channelVolumes[channel], channel);
							SongPlayer.channelVolumes[channel] = 255;
						}
					}
					if (SoundResourceManager.pendingMidiStream == null) {
						if (SoundResourceManager.songStartPosition <= 0L) {
							SoundResourceManager.midiPcmStream.setMusicTrack(SoundResourceManager.pendingMusicTrack, SongPlayer.songLooping);
						} else {
							SoundResourceManager.midiPcmStream.setMusicTrackAtPosition(SoundResourceManager.pendingMusicTrack, SoundResourceManager.songStartPosition, SongPlayer.songLooping);
						}
					}
					if (SongPlayer.pcmPlayer != null) {
						SongPlayer.pcmPlayer.setCurrentPcmStream(SoundResourceManager.midiPcmStream);
					}
					AreaSoundPlayer.MUSIC_RESOURCE = null;
					SoundResourceManager.pendingMusicTrack = null;
					SoundResourceManager.songStartPosition = 0L;
					SoundResourceManager.pendingSoundBankCache = null;
					SoundResourceManager.pendingMidiStream = null;
					return true;
				}
			}
		} catch (@Pc(167) Exception ex) {
			ex.printStackTrace();
			SoundResourceManager.midiPcmStream.turnOffSound();
			SoundResourceManager.pendingMidiStream = null;
			SoundResourceManager.pendingSoundBankCache = null;
			SoundResourceManager.pendingMusicTrack = null;
			AreaSoundPlayer.MUSIC_RESOURCE = null;
			SoundResourceManager.songLoadState = 0;
		}
		return false;
	}
	@OriginalMember(owner = "client!de", name = "b", descriptor = "(B)Z")
	public static boolean isSongTransitioning() {
		return SoundResourceManager.songLoadState != 0;
	}

	@OriginalMember(owner = "client!rs", name = "a", descriptor = "(I)V")
	public static void updateSongFade() {
		try {
			@Pc(16) int volume;
			if (SoundResourceManager.songLoadState == 1) {
				volume = SoundResourceManager.midiPcmStream.getVolume();
				if (volume > 0 && SoundResourceManager.midiPcmStream.isPlaying()) {
					volume -= SoundResourceManager.musicFadeStep;
					if (volume < 0) {
						volume = 0;
					}
					SoundResourceManager.midiPcmStream.setVolume(volume);
					return;
				}
				SoundResourceManager.midiPcmStream.turnOffSound();
				SoundResourceManager.midiPcmStream.unlinkMusicPatches();
				SoundResourceManager.pendingSoundBankCache = null;
				if (AreaSoundPlayer.MUSIC_RESOURCE == null) {
					SoundResourceManager.songLoadState = 0;
				} else {
					SoundResourceManager.songLoadState = 2;
				}
				SoundResourceManager.pendingMusicTrack = null;
			}
			if (SoundResourceManager.songLoadState == 3) {
				volume = SoundResourceManager.midiPcmStream.getVolume();
				if (volume < SoundResourceManager.SOUND_LEVEL && SoundResourceManager.midiPcmStream.isPlaying()) {
					volume += SoundResourceManager.songFadeInRate;
					if (volume > SoundResourceManager.SOUND_LEVEL) {
						volume = SoundResourceManager.SOUND_LEVEL;
					}
					SoundResourceManager.midiPcmStream.setVolume(volume);
				} else {
					SoundResourceManager.songFadeInRate = 0;
					SoundResourceManager.songLoadState = 0;
				}
			}
		} catch (@Pc(101) Exception ex) {
			ex.printStackTrace();
			SoundResourceManager.midiPcmStream.turnOffSound();
			AreaSoundPlayer.MUSIC_RESOURCE = null;
			SoundResourceManager.pendingMusicTrack = null;
			SoundResourceManager.pendingMidiStream = null;
			SoundResourceManager.pendingSoundBankCache = null;
			SoundResourceManager.songLoadState = 0;
		}
	}

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "(Z)Z")
	public static boolean isSongLoading() {
		return SoundResourceManager.songLoadState == 0 ? SoundResourceManager.midiPcmStream.isPlaying() : true;
	}
	@OriginalMember(owner = "client!oda", name = "o", descriptor = "Lclient!hs;")
	public static SoundBankCache pendingSoundBankCache;

	@OriginalMember(owner = "client!vk", name = "c", descriptor = "Lclient!dl;")
	public static MusicTrack pendingMusicTrack;

	@OriginalMember(owner = "client!rba", name = "a", descriptor = "(Lclient!in;Lclient!ma;Lclient!in;ZLclient!cba;Lclient!in;)Z")
	public static boolean initializePcmResources(@OriginalArg(0) Js5ResourceProvider soundEffectProvider, @OriginalArg(1) MidiPcmStream mixBuss, @OriginalArg(2) Js5ResourceProvider midiInstrumentProvider, @OriginalArg(4) PcmPlayer pcmPlayer, @OriginalArg(5) Js5ResourceProvider soundMidiProvider) {
		SongPlayer.pcmPlayer = pcmPlayer;
		SoundResourceManager.midiPcmStream = mixBuss;
		SoundResourceManager.SOUND_EFFECT_PROVIDER = soundEffectProvider;
		SoundResourceManager.SOUND_MIDI_PROVIDER = soundMidiProvider;
		SoundResourceManager.MIDI_INSTRUMENT_PROVIDER = midiInstrumentProvider;
		SongPlayer.channelVolumes = new int[16];
		for (@Pc(25) int i = 0; i < 16; i++) {
			SongPlayer.channelVolumes[i] = 255;
		}
		return true;
	}

	@OriginalMember(owner = "client!jl", name = "d", descriptor = "I")
	public static int SOUND_LEVEL;

	@OriginalMember(owner = "client!dda", name = "N", descriptor = "I")
	public static int songFadeInRate;
	@OriginalMember(owner = "client!vv", name = "r", descriptor = "I")
	public static int songLoadState = 0;
}
