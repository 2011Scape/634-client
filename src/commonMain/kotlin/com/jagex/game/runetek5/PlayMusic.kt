package com.jagex.game.runetek5

import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.sound.SoundResourceManager
import com.jagex.game.runetek5.sound.jagtheora.MidiPcmStream
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer
import com.jagex.game.runetek5.sound.player.song.SongPlayer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

object PlayMusic {

	@OriginalMember(owner = "client!gu", name = "a", descriptor = "(IIZILclient!in;I)V")
	@JvmStatic
	fun playMusic(@OriginalArg(1) musicArchiveId: Int, @OriginalArg(4) musicResource: Js5ResourceProvider?, @OriginalArg(5) volume: Int) {
		PlayMusic.playMusicFromPosition(musicResource, 0L, volume, musicArchiveId)
	}

	@OriginalMember(owner = "client!pu", name = "a", descriptor = "(IIIB)V")
	@JvmStatic
	fun playJingle(@OriginalArg(0) jingleId: Int, @OriginalArg(1) volume: Int, @OriginalArg(2) arg2: Int) {
		@Pc(17) val scaledVolume: Int = (volume * GamePreferences.uid!!.ingameMusicVolume!!.getValue()) shr 8
		if (scaledVolume == 0 || jingleId == -1) {
			return
		}
		if (!SoundEffectPlayer.soundJinglePlaying && AreaSoundPlayer.currentMusicArchive != -1 && SoundResourceManager.isSongLoading() && !SoundResourceManager.isSongTransitioning()) {
			SongPlayer.aClass2_Sub8_Sub3_6 = SoundResourceManager.getMidiPcmStream()
			@Pc(43) val mixBuss: MidiPcmStream? = MidiPcmStream.getStream(SongPlayer.aClass2_Sub8_Sub3_6)
			SoundEffectPlayer.setJingleStream(mixBuss)
		}
		PlayMusic.playMusic(jingleId, Resource.JINGLE, scaledVolume)
		SongPlayer.setChannelVolume(255, -1)
		SoundEffectPlayer.soundJinglePlaying = true
	}

	@OriginalMember(owner = "client!pba", name = "a", descriptor = "(ILclient!ma;ILclient!in;ZII)V")
	@JvmStatic
	fun playMusicOnStream(@OriginalArg(1) mixBuss: MidiPcmStream?, @OriginalArg(2) volume: Int, @OriginalArg(3) musicResource: Js5ResourceProvider?, @OriginalArg(6) musicArchiveId: Int) {
		PlayMusic.playMusic(musicArchiveId, musicResource, volume)
		SoundResourceManager.pendingMidiStream = mixBuss
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(Lclient!in;IZJIII)V")
	@JvmStatic
	fun playMusicFromPosition(@OriginalArg(0) musicResource: Js5ResourceProvider?, @OriginalArg(3) songStartPosition: Long, @OriginalArg(4) volume: Int, @OriginalArg(6) musicArchiveId: Int) {
		SongPlayer.playMusicFromPosition(musicArchiveId, songStartPosition, musicResource, volume)
	}
}
