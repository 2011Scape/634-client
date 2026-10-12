package com.jagex.game.runetek5.sound

import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.sound.jagtheora.MidiPcmStream
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.config.midi.SoundBankCache
import com.jagex.game.runetek5.config.midi.VorbisNode
import com.jagex.game.runetek5.sound.jagtheora.MusicTrack
import com.jagex.game.runetek5.sound.jagtheora.OggKateDecoder
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.sound.player.song.SongPlayer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.sound.player.PcmPlayer
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object SoundResourceManager {
	@OriginalMember(owner = "client!mda", name = "a", descriptor = "Lclient!ma;")
	@JvmField
	var midiPcmStream: MidiPcmStream? = null

	@OriginalMember(owner = "client!pp", name = "A", descriptor = "I")
	@JvmField
	var musicFadeStep: Int = 0

	@OriginalMember(owner = "client!aga", name = "d", descriptor = "Lclient!in;")
	@JvmField
	var MIDI_INSTRUMENT_PROVIDER: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!iia", name = "a", descriptor = "Lclient!in;")
	@JvmField
	var SOUND_EFFECT_PROVIDER: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!vi", name = "i", descriptor = "Lclient!ma;")
	@JvmField
	var pendingMidiStream: MidiPcmStream? = null

	@OriginalMember(owner = "client!av", name = "z", descriptor = "J")
	@JvmField
	var songStartPosition: Long = 0L

	@OriginalMember(owner = "client!ko", name = "n", descriptor = "Lclient!in;")
	@JvmField
	var SOUND_MIDI_PROVIDER: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(Z)Lclient!ma;")
	@JvmStatic
	fun getMidiPcmStream(): MidiPcmStream? {
		return SoundResourceManager.midiPcmStream
	}
	@OriginalMember(owner = "client!er", name = "a", descriptor = "(II)Ljava/lang/String;")
	@JvmStatic
	fun getVideoSubtitleText(@OriginalArg(0) videoId: Int): String? {
		@Pc(17) val videoType: VorbisNode? = VorbisNode.INDEX36_FILE_CACHE.get(videoId.toLong()) as VorbisNode?
		if (videoType != null) {
			@Pc(26) val oggKateStream: OggKateDecoder? = videoType.vfReference!!.getKateDecoder()
			if (oggKateStream != null) {
				@Pc(35) val time: Double = videoType.vfReference!!.getTime()
				if (oggKateStream.getStartTime().toDouble() <= time && time <= oggKateStream.getEndTime().toDouble()) {
					return oggKateStream.getSubtitle()
				}
			}
		}
		return null
	}

	@OriginalMember(owner = "client!mha", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun stopMusic() {
		SoundResourceManager.midiPcmStream!!.turnOffSound()
		SoundResourceManager.pendingMidiStream = null
		SoundResourceManager.songLoadState = 1
		AreaSoundPlayer.MUSIC_RESOURCE = null
	}

	@OriginalMember(owner = "client!tia", name = "b", descriptor = "(Z)Z")
	@JvmStatic
	fun loadPendingSong(): Boolean {
		try {
			if (SoundResourceManager.songLoadState == 2) {
				if (SoundResourceManager.pendingMusicTrack == null) {
					SoundResourceManager.pendingMusicTrack = MusicTrack.loadMusictrack(AreaSoundPlayer.MUSIC_RESOURCE, SongPlayer.musicArchiveId, SongPlayer.musicFileId)
					if (SoundResourceManager.pendingMusicTrack == null) {
						return false
					}
				}
				if (SoundResourceManager.pendingSoundBankCache == null) {
					SoundResourceManager.pendingSoundBankCache = SoundBankCache(SoundResourceManager.SOUND_EFFECT_PROVIDER, SoundResourceManager.MIDI_INSTRUMENT_PROVIDER)
				}
				@Pc(31) var mixBuss: MidiPcmStream? = SoundResourceManager.midiPcmStream
				if (SoundResourceManager.pendingMidiStream != null) {
					mixBuss = SoundResourceManager.pendingMidiStream
				}
				if (mixBuss!!.ready(SoundResourceManager.pendingMusicTrack, SoundResourceManager.pendingSoundBankCache, SoundResourceManager.SOUND_MIDI_PROVIDER)) {
					SoundResourceManager.midiPcmStream = mixBuss
					SoundResourceManager.midiPcmStream!!.resetMusicPatches()
					@Pc(61) var channel: Int
					if (SoundResourceManager.songFadeInRate <= 0) {
						SoundResourceManager.songLoadState = 0
						SoundResourceManager.midiPcmStream!!.setVolume(SoundResourceManager.SOUND_LEVEL)
						channel = 0
						while (channel < SongPlayer.channelVolumes!!.size) {
							SoundResourceManager.midiPcmStream!!.setChannelVolume(SongPlayer.channelVolumes!![channel], channel)
							SongPlayer.channelVolumes!![channel] = 255
							channel++
						}
					} else {
						SoundResourceManager.songLoadState = 3
						SoundResourceManager.midiPcmStream!!.setVolume(if (SoundResourceManager.songFadeInRate > SoundResourceManager.SOUND_LEVEL) SoundResourceManager.SOUND_LEVEL else SoundResourceManager.songFadeInRate)
						channel = 0
						while (channel < SongPlayer.channelVolumes!!.size) {
							SoundResourceManager.midiPcmStream!!.setChannelVolume(SongPlayer.channelVolumes!![channel], channel)
							SongPlayer.channelVolumes!![channel] = 255
							channel++
						}
					}
					if (SoundResourceManager.pendingMidiStream == null) {
						if (SoundResourceManager.songStartPosition <= 0L) {
							SoundResourceManager.midiPcmStream!!.setMusicTrack(SoundResourceManager.pendingMusicTrack, SongPlayer.songLooping)
						} else {
							SoundResourceManager.midiPcmStream!!.setMusicTrackAtPosition(SoundResourceManager.pendingMusicTrack, SoundResourceManager.songStartPosition, SongPlayer.songLooping)
						}
					}
					if (SongPlayer.pcmPlayer != null) {
						SongPlayer.pcmPlayer!!.setCurrentPcmStream(SoundResourceManager.midiPcmStream)
					}
					AreaSoundPlayer.MUSIC_RESOURCE = null
					SoundResourceManager.pendingMusicTrack = null
					SoundResourceManager.songStartPosition = 0L
					SoundResourceManager.pendingSoundBankCache = null
					SoundResourceManager.pendingMidiStream = null
					return true
				}
			}
		} catch (@Pc(167) ex: Exception) {
			ex.printStackTrace()
			SoundResourceManager.midiPcmStream!!.turnOffSound()
			SoundResourceManager.pendingMidiStream = null
			SoundResourceManager.pendingSoundBankCache = null
			SoundResourceManager.pendingMusicTrack = null
			AreaSoundPlayer.MUSIC_RESOURCE = null
			SoundResourceManager.songLoadState = 0
		}
		return false
	}
	@OriginalMember(owner = "client!de", name = "b", descriptor = "(B)Z")
	@JvmStatic
	fun isSongTransitioning(): Boolean {
		return SoundResourceManager.songLoadState != 0
	}

	@OriginalMember(owner = "client!rs", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun updateSongFade() {
		try {
			@Pc(16) var volume: Int
			if (SoundResourceManager.songLoadState == 1) {
				volume = SoundResourceManager.midiPcmStream!!.getVolume()
				if (volume > 0 && SoundResourceManager.midiPcmStream!!.isPlaying()) {
					volume -= SoundResourceManager.musicFadeStep
					if (volume < 0) {
						volume = 0
					}
					SoundResourceManager.midiPcmStream!!.setVolume(volume)
					return
				}
				SoundResourceManager.midiPcmStream!!.turnOffSound()
				SoundResourceManager.midiPcmStream!!.unlinkMusicPatches()
				SoundResourceManager.pendingSoundBankCache = null
				if (AreaSoundPlayer.MUSIC_RESOURCE == null) {
					SoundResourceManager.songLoadState = 0
				} else {
					SoundResourceManager.songLoadState = 2
				}
				SoundResourceManager.pendingMusicTrack = null
			}
			if (SoundResourceManager.songLoadState == 3) {
				volume = SoundResourceManager.midiPcmStream!!.getVolume()
				if (volume < SoundResourceManager.SOUND_LEVEL && SoundResourceManager.midiPcmStream!!.isPlaying()) {
					volume += SoundResourceManager.songFadeInRate
					if (volume > SoundResourceManager.SOUND_LEVEL) {
						volume = SoundResourceManager.SOUND_LEVEL
					}
					SoundResourceManager.midiPcmStream!!.setVolume(volume)
				} else {
					SoundResourceManager.songFadeInRate = 0
					SoundResourceManager.songLoadState = 0
				}
			}
		} catch (@Pc(101) ex: Exception) {
			ex.printStackTrace()
			SoundResourceManager.midiPcmStream!!.turnOffSound()
			AreaSoundPlayer.MUSIC_RESOURCE = null
			SoundResourceManager.pendingMusicTrack = null
			SoundResourceManager.pendingMidiStream = null
			SoundResourceManager.pendingSoundBankCache = null
			SoundResourceManager.songLoadState = 0
		}
	}

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "(Z)Z")
	@JvmStatic
	fun isSongLoading(): Boolean {
		return if (SoundResourceManager.songLoadState == 0) SoundResourceManager.midiPcmStream!!.isPlaying() else true
	}
	@OriginalMember(owner = "client!oda", name = "o", descriptor = "Lclient!hs;")
	@JvmField
	var pendingSoundBankCache: SoundBankCache? = null

	@OriginalMember(owner = "client!vk", name = "c", descriptor = "Lclient!dl;")
	@JvmField
	var pendingMusicTrack: MusicTrack? = null

	@OriginalMember(owner = "client!rba", name = "a", descriptor = "(Lclient!in;Lclient!ma;Lclient!in;ZLclient!cba;Lclient!in;)Z")
	@JvmStatic
	fun initializePcmResources(@OriginalArg(0) soundEffectProvider: Js5ResourceProvider?, @OriginalArg(1) mixBuss: MidiPcmStream?, @OriginalArg(2) midiInstrumentProvider: Js5ResourceProvider?, @OriginalArg(4) pcmPlayer: PcmPlayer?, @OriginalArg(5) soundMidiProvider: Js5ResourceProvider?): Boolean {
		SongPlayer.pcmPlayer = pcmPlayer
		SoundResourceManager.midiPcmStream = mixBuss
		SoundResourceManager.SOUND_EFFECT_PROVIDER = soundEffectProvider
		SoundResourceManager.SOUND_MIDI_PROVIDER = soundMidiProvider
		SoundResourceManager.MIDI_INSTRUMENT_PROVIDER = midiInstrumentProvider
		SongPlayer.channelVolumes = IntArray(16)
		@Pc(25) var i: Int = 0
		while (i < 16) {
			SongPlayer.channelVolumes!![i] = 255
			i++
		}
		return true
	}

	@OriginalMember(owner = "client!jl", name = "d", descriptor = "I")
	@JvmField
	var SOUND_LEVEL: Int = 0

	@OriginalMember(owner = "client!dda", name = "N", descriptor = "I")
	@JvmField
	var songFadeInRate: Int = 0
	@OriginalMember(owner = "client!vv", name = "r", descriptor = "I")
	@JvmField
	var songLoadState: Int = 0
}
