package com.jagex.game.runetek5;

import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.sound.SoundResourceManager;
import com.jagex.game.runetek5.sound.jagtheora.MidiPcmStream;
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer;
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer;
import com.jagex.game.runetek5.sound.player.song.SongPlayer;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.preference.GamePreferences;

public final class PlayMusic {

	@OriginalMember(owner = "client!gu", name = "a", descriptor = "(IIZILclient!in;I)V")
	public static void playMusic(@OriginalArg(1) int musicArchiveId, @OriginalArg(4) Js5ResourceProvider musicResource, @OriginalArg(5) int volume) {
		PlayMusic.playMusicFromPosition(musicResource, 0L, volume, musicArchiveId);
	}
	@OriginalMember(owner = "client!pu", name = "a", descriptor = "(IIIB)V")
	public static void playJingle(@OriginalArg(0) int jingleId, @OriginalArg(1) int volume, @OriginalArg(2) int arg2) {
		@Pc(17) int scaledVolume = volume * GamePreferences.uid.ingameMusicVolume.getValue() >> 8;
		if (scaledVolume == 0 || jingleId == -1) {
			return;
		}
		if (!SoundEffectPlayer.soundJinglePlaying && AreaSoundPlayer.currentMusicArchive != -1 && SoundResourceManager.isSongLoading() && !SoundResourceManager.isSongTransitioning()) {
			SongPlayer.aClass2_Sub8_Sub3_6 = SoundResourceManager.getMidiPcmStream();
			@Pc(43) MidiPcmStream mixBuss = MidiPcmStream.getStream(SongPlayer.aClass2_Sub8_Sub3_6);
			SoundEffectPlayer.setJingleStream(mixBuss);
		}
		PlayMusic.playMusic(jingleId, Resource.JINGLE, scaledVolume);
		SongPlayer.setChannelVolume(255, -1);
		SoundEffectPlayer.soundJinglePlaying = true;
	}
	@OriginalMember(owner = "client!pba", name = "a", descriptor = "(ILclient!ma;ILclient!in;ZII)V")
	public static void playMusicOnStream(@OriginalArg(1) MidiPcmStream mixBuss, @OriginalArg(2) int volume, @OriginalArg(3) Js5ResourceProvider musicResource, @OriginalArg(6) int musicArchiveId) {
		PlayMusic.playMusic(musicArchiveId, musicResource, volume);
		SoundResourceManager.pendingMidiStream = mixBuss;
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(Lclient!in;IZJIII)V")
	public static void playMusicFromPosition(@OriginalArg(0) Js5ResourceProvider musicResource, @OriginalArg(3) long songStartPosition, @OriginalArg(4) int volume, @OriginalArg(6) int musicArchiveId) {
		SongPlayer.playMusicFromPosition(musicArchiveId, songStartPosition, musicResource, volume);
	}
}
