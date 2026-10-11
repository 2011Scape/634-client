package com.jagex.game.runetek5.sound.player.area;

import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.sound.Sound;
import org.openrs2.deob.annotation.OriginalArg;
import com.jagex.game.runetek5.sound.SoundResourceManager;
import com.jagex.game.runetek5.sound.player.song.SongPlayer;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.client.ClientState;

public final class AreaSoundPlayer {
	@OriginalMember(owner = "client!og", name = "a", descriptor = "I")
	public static int AREA_SOUND_CACHE_SIZE = 0;

	@OriginalMember(owner = "client!pi", name = "z", descriptor = "I")
	public static int loadingSpritesCrc;

	@OriginalMember(owner = "client!bh", name = "H", descriptor = "Lclient!in;")
	public static Js5ResourceProvider MUSIC_RESOURCE;

	@OriginalMember(owner = "client!up", name = "M", descriptor = "I")
	public static int currentMusicArchive = -1;

	private AreaSoundPlayer() {
		/* empty */
	}
	@OriginalMember(owner = "client!q", name = "s", descriptor = "[Lclient!gt;")
	public static Sound[] AREA_SOUND_CACHE = new Sound[50];
	@OriginalMember(owner = "client!jf", name = "a", descriptor = "(Z)V")
	public static void resetAreaSounds() {
		AreaSoundPlayer.AREA_SOUND_CACHE = new Sound[50];
		AreaSoundPlayer.AREA_SOUND_CACHE_SIZE = 0;
	}
	@OriginalMember(owner = "client!jn", name = "a", descriptor = "(ZIIIII)V")
	public static void playSoundSynth(@OriginalArg(1) int volume, @OriginalArg(2) int soundId, @OriginalArg(3) int loops, @OriginalArg(4) int delay, @OriginalArg(5) int rate) {
		if (GamePreferences.uid.soundEffectVolume.getValue() != 0 && loops != 0 && AreaSoundPlayer.AREA_SOUND_CACHE_SIZE < 50 && soundId != -1) {
			AreaSoundPlayer.AREA_SOUND_CACHE[AreaSoundPlayer.AREA_SOUND_CACHE_SIZE++] = new Sound((byte) 1, soundId, loops, delay, volume, 0, rate, null);
		}
	}
	@OriginalMember(owner = "client!od", name = "a", descriptor = "(IIZIBIII)V")
	public static void playAreaSoundVorbis(@OriginalArg(0) int soundId, @OriginalArg(1) int volume, @OriginalArg(3) int coord, @OriginalArg(5) int loops, @OriginalArg(6) int rate, @OriginalArg(7) int delay) {
		if (GamePreferences.uid.soundEffectVolume.getValue() != 0 && loops != 0 && AreaSoundPlayer.AREA_SOUND_CACHE_SIZE < 50 && soundId != -1) {
			AreaSoundPlayer.AREA_SOUND_CACHE[AreaSoundPlayer.AREA_SOUND_CACHE_SIZE++] = new Sound((byte) 2, soundId, loops, delay, volume, coord, rate, null);
		}
	}
	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(IBZLclient!in;III)V")
	public static void musicSourceUpdate(@OriginalArg(0) int soundLevel, @OriginalArg(3) Js5ResourceProvider musicResource, @OriginalArg(4) int musicArchiveId) {
		SoundResourceManager.SOUND_LEVEL = soundLevel;
		SongPlayer.songLooping = false;
		SoundResourceManager.pendingMidiStream = null;
		SongPlayer.musicArchiveId = musicArchiveId;
		SoundResourceManager.musicFadeStep = 2;
		SoundResourceManager.songLoadState = 1;
		SongPlayer.musicFileId = 0;
		AreaSoundPlayer.MUSIC_RESOURCE = musicResource;
	}
	@OriginalMember(owner = "client!ida", name = "a", descriptor = "(IIIIIII)V")
	public static void playAreaSoundSynth(@OriginalArg(0) int loops, @OriginalArg(1) int coord, @OriginalArg(2) int delay, @OriginalArg(3) int volume, @OriginalArg(4) int rate, @OriginalArg(5) int soundId) {
		if (GamePreferences.uid.soundEffectVolume.getValue() != 0 && loops != 0 && AreaSoundPlayer.AREA_SOUND_CACHE_SIZE < 50 && soundId != -1) {
			AreaSoundPlayer.AREA_SOUND_CACHE[AreaSoundPlayer.AREA_SOUND_CACHE_SIZE++] = new Sound((byte) 1, soundId, loops, delay, volume, coord, rate, null);
		}
	}

	@OriginalMember(owner = "client!pm", name = "b", descriptor = "(I)I")
	public static int getLoadingSpritesIndexCrc() {
		return AreaSoundPlayer.loadingSpritesCrc;
	}
}
