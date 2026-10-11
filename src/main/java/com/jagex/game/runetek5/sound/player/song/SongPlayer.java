package com.jagex.game.runetek5.sound.player.song;

import com.jagex.game.runetek5.sound.player.PcmPlayer;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.MidiChannelVolumeReset;
import com.jagex.game.runetek5.PlayMusic;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.sound.SoundResourceManager;
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer;
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.sound.jagtheora.MidiPcmStream;
import com.jagex.game.runetek5.client.ClientState;

public final class SongPlayer {
	@OriginalMember(owner = "client!tea", name = "j", descriptor = "[I")
	public static int[] channelVolumes;

	private SongPlayer() {
		/* empty */
	}
	@OriginalMember(owner = "client!ge", name = "V", descriptor = "I")
	public static int musicArchiveId;
	@OriginalMember(owner = "client!ok", name = "a", descriptor = "(IIII)V")
	public static void playSong(@OriginalArg(0) int volume, @OriginalArg(1) int currentMusicArchive, @OriginalArg(3) int fadeDuration) {
		@Pc(12) int scaledVolume = GamePreferences.uid.ingameMusicVolume.getValue() * volume >> 8;
		if (currentMusicArchive == -1 && !SoundEffectPlayer.soundJinglePlaying) {
			SoundResourceManager.stopMusic();
		} else if (currentMusicArchive != -1 && (currentMusicArchive != AreaSoundPlayer.currentMusicArchive || !SoundResourceManager.isSongLoading()) && scaledVolume != 0 && !SoundEffectPlayer.soundJinglePlaying) {
			SongPlayer.playMusicWithFade(scaledVolume, currentMusicArchive, Resource.MUSIC, fadeDuration);
			MidiChannelVolumeReset.resetChannelVolumes();
		}
		if (AreaSoundPlayer.currentMusicArchive != currentMusicArchive) {
			SongPlayer.aClass2_Sub8_Sub3_6 = null;
		}
		AreaSoundPlayer.currentMusicArchive = currentMusicArchive;
	}

	@OriginalMember(owner = "client!ig", name = "a", descriptor = "(IIZILclient!in;II)V")
	public static void playMusicWithFade(@OriginalArg(0) int volume, @OriginalArg(3) int musicArchiveId, @OriginalArg(4) Js5ResourceProvider musicResource, @OriginalArg(6) int fadeDuration) {
		if (fadeDuration <= 0) {
			PlayMusic.playMusic(musicArchiveId, musicResource, volume);
			return;
		}
		SongPlayer.songLooping = false;
		SoundResourceManager.songLoadState = 1;
		AreaSoundPlayer.MUSIC_RESOURCE = musicResource;
		SongPlayer.musicArchiveId = musicArchiveId;
		SoundResourceManager.pendingMidiStream = null;
		SongPlayer.musicFileId = 0;
		SoundResourceManager.SOUND_LEVEL = volume;
		SoundResourceManager.musicFadeStep = SoundResourceManager.midiPcmStream.getVolume() / fadeDuration;
		if (SoundResourceManager.musicFadeStep < 1) {
			SoundResourceManager.musicFadeStep = 1;
			return;
		}
	}

	@OriginalMember(owner = "client!fba", name = "b", descriptor = "(III)V")
	public static void setChannelVolume(@OriginalArg(1) int volume, @OriginalArg(2) int channel) {
		if (SoundResourceManager.songLoadState != 0) {
			if (channel >= 0) {
				SongPlayer.channelVolumes[channel] = volume;
			} else {
				for (@Pc(23) int i = 0; i < 16; i++) {
					SongPlayer.channelVolumes[i] = volume;
				}
			}
		}
		SoundResourceManager.midiPcmStream.setChannelVolume(volume, channel);
	}
	@OriginalMember(owner = "client!qfa", name = "G", descriptor = "Lclient!cba;")
	public static PcmPlayer pcmPlayer;
	@OriginalMember(owner = "client!ria", name = "c", descriptor = "Lclient!ma;")
	public static MidiPcmStream aClass2_Sub8_Sub3_6;

	@OriginalMember(owner = "client!cm", name = "b", descriptor = "I")
	public static int musicFileId;

	@OriginalMember(owner = "client!efa", name = "b", descriptor = "Z")
	public static boolean songLooping;
	@OriginalMember(owner = "client!pi", name = "c", descriptor = "(II)V")
	public static void fadeOutMusic() {
		SongPlayer.musicArchiveId = -1;
		SoundResourceManager.pendingMidiStream = null;
		SongPlayer.songLooping = false;
		AreaSoundPlayer.MUSIC_RESOURCE = null;
		SongPlayer.musicFileId = -1;
		SoundResourceManager.musicFadeStep = 2;
		SoundResourceManager.songLoadState = 1;
		SoundResourceManager.SOUND_LEVEL = 0;
	}

	@OriginalMember(owner = "client!dq", name = "a", descriptor = "(IIJIZLclient!in;BI)V")
	public static void playMusicFromPosition(@OriginalArg(0) int archiveId, @OriginalArg(2) long songStartPosition, @OriginalArg(5) Js5ResourceProvider musicResource, @OriginalArg(7) int volume) {
		SoundResourceManager.musicFadeStep = 10000;
		AreaSoundPlayer.MUSIC_RESOURCE = musicResource;
		SoundResourceManager.SOUND_LEVEL = volume;
		SongPlayer.musicArchiveId = archiveId;
		SongPlayer.songLooping = false;
		SoundResourceManager.pendingMidiStream = null;
		SongPlayer.musicFileId = 0;
		SoundResourceManager.songStartPosition = songStartPosition;
		SoundResourceManager.songLoadState = 1;
		SoundResourceManager.songFadeInRate = 0;
	}
	@OriginalMember(owner = "client!dfa", name = "b", descriptor = "(III)V")
	public static void setSongVolume(@OriginalArg(0) int channel, @OriginalArg(1) int volume) {
		SongPlayer.setChannelVolume(volume, channel);
	}
}
