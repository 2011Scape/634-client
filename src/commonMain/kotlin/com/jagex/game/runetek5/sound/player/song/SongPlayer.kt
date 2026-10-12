package com.jagex.game.runetek5.sound.player.song

import com.jagex.game.runetek5.sound.player.PcmPlayer
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.MidiChannelVolumeReset
import com.jagex.game.runetek5.PlayMusic
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.sound.SoundResourceManager
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.sound.jagtheora.MidiPcmStream
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object SongPlayer {
	@OriginalMember(owner = "client!tea", name = "j", descriptor = "[I")
	@JvmField
	var channelVolumes: IntArray? = null

	@OriginalMember(owner = "client!ge", name = "V", descriptor = "I")
	@JvmField
	var musicArchiveId: Int = 0
	@OriginalMember(owner = "client!ok", name = "a", descriptor = "(IIII)V")
	@JvmStatic
	fun playSong(@OriginalArg(0) volume: Int, @OriginalArg(1) currentMusicArchive: Int, @OriginalArg(3) fadeDuration: Int) {
		@Pc(12) val scaledVolume: Int = (GamePreferences.uid!!.ingameMusicVolume!!.getValue() * volume) shr 8
		if (currentMusicArchive == -1 && !SoundEffectPlayer.soundJinglePlaying) {
			SoundResourceManager.stopMusic()
		} else if (currentMusicArchive != -1 && (currentMusicArchive != AreaSoundPlayer.currentMusicArchive || !SoundResourceManager.isSongLoading()) && scaledVolume != 0 && !SoundEffectPlayer.soundJinglePlaying) {
			SongPlayer.playMusicWithFade(scaledVolume, currentMusicArchive, Resource.MUSIC, fadeDuration)
			MidiChannelVolumeReset.resetChannelVolumes()
		}
		if (AreaSoundPlayer.currentMusicArchive != currentMusicArchive) {
			SongPlayer.aClass2_Sub8_Sub3_6 = null
		}
		AreaSoundPlayer.currentMusicArchive = currentMusicArchive
	}

	@OriginalMember(owner = "client!ig", name = "a", descriptor = "(IIZILclient!in;II)V")
	@JvmStatic
	fun playMusicWithFade(@OriginalArg(0) volume: Int, @OriginalArg(3) musicArchiveId: Int, @OriginalArg(4) musicResource: Js5ResourceProvider?, @OriginalArg(6) fadeDuration: Int) {
		if (fadeDuration <= 0) {
			PlayMusic.playMusic(musicArchiveId, musicResource, volume)
			return
		}
		SongPlayer.songLooping = false
		SoundResourceManager.songLoadState = 1
		AreaSoundPlayer.MUSIC_RESOURCE = musicResource
		SongPlayer.musicArchiveId = musicArchiveId
		SoundResourceManager.pendingMidiStream = null
		SongPlayer.musicFileId = 0
		SoundResourceManager.SOUND_LEVEL = volume
		SoundResourceManager.musicFadeStep = SoundResourceManager.midiPcmStream!!.getVolume() / fadeDuration
		if (SoundResourceManager.musicFadeStep < 1) {
			SoundResourceManager.musicFadeStep = 1
			return
		}
	}

	@OriginalMember(owner = "client!fba", name = "b", descriptor = "(III)V")
	@JvmStatic
	fun setChannelVolume(@OriginalArg(1) volume: Int, @OriginalArg(2) channel: Int) {
		if (SoundResourceManager.songLoadState != 0) {
			if (channel >= 0) {
				SongPlayer.channelVolumes!![channel] = volume
			} else {
				@Pc(23) var i: Int = 0
				while (i < 16) {
					SongPlayer.channelVolumes!![i] = volume
					i++
				}
			}
		}
		SoundResourceManager.midiPcmStream!!.setChannelVolume(volume, channel)
	}
	@OriginalMember(owner = "client!qfa", name = "G", descriptor = "Lclient!cba;")
	@JvmField
	var pcmPlayer: PcmPlayer? = null
	@OriginalMember(owner = "client!ria", name = "c", descriptor = "Lclient!ma;")
	@JvmField
	var aClass2_Sub8_Sub3_6: MidiPcmStream? = null

	@OriginalMember(owner = "client!cm", name = "b", descriptor = "I")
	@JvmField
	var musicFileId: Int = 0

	@OriginalMember(owner = "client!efa", name = "b", descriptor = "Z")
	@JvmField
	var songLooping: Boolean = false
	@OriginalMember(owner = "client!pi", name = "c", descriptor = "(II)V")
	@JvmStatic
	fun fadeOutMusic() {
		SongPlayer.musicArchiveId = -1
		SoundResourceManager.pendingMidiStream = null
		SongPlayer.songLooping = false
		AreaSoundPlayer.MUSIC_RESOURCE = null
		SongPlayer.musicFileId = -1
		SoundResourceManager.musicFadeStep = 2
		SoundResourceManager.songLoadState = 1
		SoundResourceManager.SOUND_LEVEL = 0
	}

	@OriginalMember(owner = "client!dq", name = "a", descriptor = "(IIJIZLclient!in;BI)V")
	@JvmStatic
	fun playMusicFromPosition(@OriginalArg(0) archiveId: Int, @OriginalArg(2) songStartPosition: Long, @OriginalArg(5) musicResource: Js5ResourceProvider?, @OriginalArg(7) volume: Int) {
		SoundResourceManager.musicFadeStep = 10000
		AreaSoundPlayer.MUSIC_RESOURCE = musicResource
		SoundResourceManager.SOUND_LEVEL = volume
		SongPlayer.musicArchiveId = archiveId
		SongPlayer.songLooping = false
		SoundResourceManager.pendingMidiStream = null
		SongPlayer.musicFileId = 0
		SoundResourceManager.songStartPosition = songStartPosition
		SoundResourceManager.songLoadState = 1
		SoundResourceManager.songFadeInRate = 0
	}
	@OriginalMember(owner = "client!dfa", name = "b", descriptor = "(III)V")
	@JvmStatic
	fun setSongVolume(@OriginalArg(0) channel: Int, @OriginalArg(1) volume: Int) {
		SongPlayer.setChannelVolume(volume, channel)
	}
}
