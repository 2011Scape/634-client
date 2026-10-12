package com.jagex.game.runetek5.sound.player.area

import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.sound.Sound
import org.openrs2.deob.annotation.OriginalArg
import com.jagex.game.runetek5.sound.SoundResourceManager
import com.jagex.game.runetek5.sound.player.song.SongPlayer
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.client.ClientState

object AreaSoundPlayer {
	@OriginalMember(owner = "client!og", name = "a", descriptor = "I")
	@JvmField
	var AREA_SOUND_CACHE_SIZE: Int = 0

	@OriginalMember(owner = "client!pi", name = "z", descriptor = "I")
	@JvmField
	var loadingSpritesCrc: Int = 0

	@OriginalMember(owner = "client!bh", name = "H", descriptor = "Lclient!in;")
	@JvmField
	var MUSIC_RESOURCE: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!up", name = "M", descriptor = "I")
	@JvmField
	var currentMusicArchive: Int = -1

	@OriginalMember(owner = "client!q", name = "s", descriptor = "[Lclient!gt;")
	@JvmField
	var AREA_SOUND_CACHE: Array<Sound?> = arrayOfNulls<Sound>(50)
	@OriginalMember(owner = "client!jf", name = "a", descriptor = "(Z)V")
	@JvmStatic
	fun resetAreaSounds() {
		AreaSoundPlayer.AREA_SOUND_CACHE = arrayOfNulls<Sound>(50)
		AreaSoundPlayer.AREA_SOUND_CACHE_SIZE = 0
	}
	@OriginalMember(owner = "client!jn", name = "a", descriptor = "(ZIIIII)V")
	@JvmStatic
	fun playSoundSynth(@OriginalArg(1) volume: Int, @OriginalArg(2) soundId: Int, @OriginalArg(3) loops: Int, @OriginalArg(4) delay: Int, @OriginalArg(5) rate: Int) {
		if (GamePreferences.uid!!.soundEffectVolume!!.getValue() != 0 && loops != 0 && AreaSoundPlayer.AREA_SOUND_CACHE_SIZE < 50 && soundId != -1) {
			AreaSoundPlayer.AREA_SOUND_CACHE[AreaSoundPlayer.AREA_SOUND_CACHE_SIZE++] = Sound(1.toByte(), soundId, loops, delay, volume, 0, rate, null)
		}
	}
	@OriginalMember(owner = "client!od", name = "a", descriptor = "(IIZIBIII)V")
	@JvmStatic
	fun playAreaSoundVorbis(@OriginalArg(0) soundId: Int, @OriginalArg(1) volume: Int, @OriginalArg(3) coord: Int, @OriginalArg(5) loops: Int, @OriginalArg(6) rate: Int, @OriginalArg(7) delay: Int) {
		if (GamePreferences.uid!!.soundEffectVolume!!.getValue() != 0 && loops != 0 && AreaSoundPlayer.AREA_SOUND_CACHE_SIZE < 50 && soundId != -1) {
			AreaSoundPlayer.AREA_SOUND_CACHE[AreaSoundPlayer.AREA_SOUND_CACHE_SIZE++] = Sound(2.toByte(), soundId, loops, delay, volume, coord, rate, null)
		}
	}
	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(IBZLclient!in;III)V")
	@JvmStatic
	fun musicSourceUpdate(@OriginalArg(0) soundLevel: Int, @OriginalArg(3) musicResource: Js5ResourceProvider?, @OriginalArg(4) musicArchiveId: Int) {
		SoundResourceManager.SOUND_LEVEL = soundLevel
		SongPlayer.songLooping = false
		SoundResourceManager.pendingMidiStream = null
		SongPlayer.musicArchiveId = musicArchiveId
		SoundResourceManager.musicFadeStep = 2
		SoundResourceManager.songLoadState = 1
		SongPlayer.musicFileId = 0
		AreaSoundPlayer.MUSIC_RESOURCE = musicResource
	}
	@OriginalMember(owner = "client!ida", name = "a", descriptor = "(IIIIIII)V")
	@JvmStatic
	fun playAreaSoundSynth(@OriginalArg(0) loops: Int, @OriginalArg(1) coord: Int, @OriginalArg(2) delay: Int, @OriginalArg(3) volume: Int, @OriginalArg(4) rate: Int, @OriginalArg(5) soundId: Int) {
		if (GamePreferences.uid!!.soundEffectVolume!!.getValue() != 0 && loops != 0 && AreaSoundPlayer.AREA_SOUND_CACHE_SIZE < 50 && soundId != -1) {
			AreaSoundPlayer.AREA_SOUND_CACHE[AreaSoundPlayer.AREA_SOUND_CACHE_SIZE++] = Sound(1.toByte(), soundId, loops, delay, volume, coord, rate, null)
		}
	}

	@OriginalMember(owner = "client!pm", name = "b", descriptor = "(I)I")
	@JvmStatic
	fun getLoadingSpritesIndexCrc(): Int {
		return AreaSoundPlayer.loadingSpritesCrc
	}
}
