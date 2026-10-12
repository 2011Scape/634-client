package com.jagex.game.runetek5

import com.jagex.game.runetek5.sound.player.area.AmbientSound
import com.jagex.game.runetek5.sound.Sound
import com.jagex.game.runetek5.sound.jagtheora.AreaSoundStream
import com.jagex.game.runetek5.sound.SoundType
import com.jagex.game.runetek5.sound.AreaSoundSampleMixer
import com.jagex.game.runetek5.config.midi.SoundEffect
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.midi.instrument.MusicSample
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.sound.jagtheora.MidiPcmStream
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.sound.SoundResourceManager
import com.jagex.game.runetek5.sound.player.PcmPlayerManager
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.sound.Resampler
import com.jagex.game.runetek5.sound.jagtheora.PcmStreamMixer

object SoundManager {

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(IIZBIII)V")
	@JvmStatic
	fun playSoundVorbis(@OriginalArg(0) loops: Int, @OriginalArg(1) rate: Int, @OriginalArg(2) voiceOver: Boolean, @OriginalArg(4) volume: Int, @OriginalArg(5) delay: Int, @OriginalArg(6) soundId: Int) {
		if ((if (voiceOver) GamePreferences.uid!!.voiceOverVolume!!.getValue() else GamePreferences.uid!!.soundEffectVolume!!.getValue()) != 0 && loops != 0 && AreaSoundPlayer.AREA_SOUND_CACHE_SIZE < 50 && soundId != -1) {
			AreaSoundPlayer.AREA_SOUND_CACHE[AreaSoundPlayer.AREA_SOUND_CACHE_SIZE++] = Sound((if (voiceOver) SoundType.VORBIS_VOICE_OVER else SoundType.VORBIS).toByte(), soundId, loops, delay, volume, 0, rate, null)
		}
	}

	@OriginalMember(owner = "client!gea", name = "a", descriptor = "(IIIIBLclient!hf;)V")
	@JvmStatic
	fun updateAmbientSound(@OriginalArg(0) step: Int, @OriginalArg(1) absX: Int, @OriginalArg(2) level: Int, @OriginalArg(3) absZ: Int, @OriginalArg(5) positionedSound: AmbientSound?) {
		if (positionedSound!!.soundId == -1 && positionedSound.ambientSoundGroupIds == null) {
			return
		}
		@Pc(15) var hearDistance: Int = 0
		@Pc(25) val preferredVolume: Int = (positionedSound.ambientSoundVolume * GamePreferences.uid!!.ambientSoundVolume!!.getValue()) shr 8
		if (positionedSound.absStartX < absX) {
			hearDistance = absX - positionedSound.absStartX
		} else if (absX < positionedSound.minX) {
			hearDistance = positionedSound.minX - absX
		}
		if (positionedSound.plane < absZ) {
			hearDistance += absZ - positionedSound.plane
		} else if (positionedSound.absEndX > absZ) {
			hearDistance += positionedSound.absEndX - absZ
		}
		if (positionedSound.ambientSoundMinHearDistance == 0 || positionedSound.ambientSoundMinHearDistance < hearDistance - 256 || GamePreferences.uid!!.ambientSoundVolume!!.getValue() == 0 || positionedSound.level != level) {
			if (positionedSound.soundEffect != null) {
				AmbientSound.gameSounds!!.remove(positionedSound.soundEffect)
				positionedSound.soundEffectBuffer = null
				positionedSound.soundEffect = null
				positionedSound.instrumentEffect = null
			}
			if (positionedSound.ambientSoundGroupStream != null) {
				AmbientSound.gameSounds!!.remove(positionedSound.ambientSoundGroupStream)
				positionedSound.ambientSoundGroupStream = null
				positionedSound.ambientSoundInstrument = null
				positionedSound.soundGroupBuffer = null
			}
			return
		}
		hearDistance -= 256
		if (hearDistance < 0) {
			hearDistance = 0
		}
		@Pc(162) val volume: Int = (positionedSound.ambientSoundMinHearDistance - hearDistance) * preferredVolume / positionedSound.ambientSoundMinHearDistance
		PlayerUpdate.localPlayer!!.getSize()
		@Pc(172) var panValue: Int = 8192
		@Pc(183) val deltaX: Int = (positionedSound.minX + positionedSound.absStartX) / 2 - absX
		@Pc(195) val deltaZ: Int = (positionedSound.plane + positionedSound.absEndX) / 2 - absZ
		@Pc(219) var local219: Int
		@Pc(230) var local230: Int
		if (deltaX != 0 || deltaZ != 0) {
			var relativeAngle: Int = (-Camera.cameraYaw - (Math.atan2(deltaX.toDouble(), deltaZ.toDouble()) * 2607.5945876176133).toInt() - 4096) and 0x3FFF
			if (relativeAngle > 8192) {
				relativeAngle = 16384 - relativeAngle
			}
			val panSpread: Int
			if (hearDistance <= 0) {
				panSpread = 8192
			} else if (hearDistance < 4096) {
				panSpread = hearDistance * 8192 / 4096 + 8192
			} else {
				panSpread = 16384
			}
			panValue = ((16384 - panSpread) shr 1) + panSpread * relativeAngle / 8192
		}
		@Pc(372) var soundStream: AreaSoundStream?
		if (positionedSound.soundEffect != null) {
			positionedSound.soundEffect!!.setVolume(volume)
			positionedSound.soundEffect!!.setPanPosition(panValue)
		} else if (positionedSound.soundId >= 0) {
			val rate: Int = if (positionedSound.minDelay == 256 && positionedSound.ambientSoundMaxDelay == 256) 256 else Utils.randomLogUniform(positionedSound.minDelay, positionedSound.ambientSoundMaxDelay)
			if (positionedSound.vorbis) {
				if (positionedSound.instrumentEffect == null) {
					positionedSound.instrumentEffect = MusicSample.create(Resource.MIDI_INSTRUMENT, positionedSound.soundId)
				}
				if (positionedSound.instrumentEffect != null) {
					if (positionedSound.soundEffectBuffer == null) {
						positionedSound.soundEffectBuffer = positionedSound.instrumentEffect!!.toPcmSound(intArrayOf(22050))
					}
					if (positionedSound.soundEffectBuffer != null) {
						soundStream = AreaSoundSampleMixer.create(positionedSound.soundEffectBuffer, rate, volume shl 6, panValue)
						soundStream!!.setNumLoops(-1)
						AmbientSound.gameSounds!!.addSubStream(soundStream)
						positionedSound.soundEffect = soundStream
					}
				}
			} else {
				@Pc(308) val synthSound: SoundEffect? = SoundEffectPlayer.getSoundEffect(Resource.SOUND_EFFECT, positionedSound.soundId, 0)
				if (synthSound != null) {
					@Pc(315) val variableRateSoundPacket: VariableRateSoundPacket = synthSound.getMixedAudio().resample(SoundEffectPlayer.resampler)
					@Pc(323) val soundStream2: AreaSoundStream? = AreaSoundSampleMixer.create(variableRateSoundPacket, rate, volume shl 6, panValue)
					soundStream2!!.setNumLoops(-1)
					AmbientSound.gameSounds!!.addSubStream(soundStream2)
					positionedSound.soundEffect = soundStream2
				}
			}
		}
		if (positionedSound.ambientSoundGroupStream != null) {
			positionedSound.ambientSoundGroupStream!!.setVolume(volume)
			positionedSound.ambientSoundGroupStream!!.setPanPosition(panValue)
			if (positionedSound.ambientSoundGroupStream!!.isLinked()) {
				return
			}
			positionedSound.ambientSoundInstrument = null
			positionedSound.soundGroupBuffer = null
			positionedSound.ambientSoundGroupStream = null
		} else if (positionedSound.ambientSoundGroupIds != null && run { positionedSound.stepsUntilNextSound -= step; positionedSound.stepsUntilNextSound } <= 0) {
			val rate: Int = if (positionedSound.minDelay == 256 && positionedSound.ambientSoundMaxDelay == 256) 256 else positionedSound.ambientSoundMaxDelay + (Math.random() * (positionedSound.minDelay - positionedSound.ambientSoundMaxDelay).toDouble()).toInt()
			if (positionedSound.random) {
				if (positionedSound.ambientSoundInstrument == null) {
					val soundIndex: Int = (positionedSound.ambientSoundGroupIds!!.size.toDouble() * Math.random()).toInt()
					positionedSound.ambientSoundInstrument = MusicSample.create(Resource.MIDI_INSTRUMENT, positionedSound.ambientSoundGroupIds!![soundIndex])
				}
				if (positionedSound.ambientSoundInstrument != null) {
					if (positionedSound.soundGroupBuffer == null) {
						positionedSound.soundGroupBuffer = positionedSound.ambientSoundInstrument!!.toPcmSound(intArrayOf(22050))
					}
					if (positionedSound.soundGroupBuffer != null) {
						soundStream = AreaSoundSampleMixer.create(positionedSound.soundGroupBuffer, rate, volume shl 6, panValue)
						soundStream!!.setNumLoops(0)
						AmbientSound.gameSounds!!.addSubStream(soundStream)
						positionedSound.ambientSoundGroupStream = soundStream
						positionedSound.stepsUntilNextSound = ((positionedSound.soundMaxStep - positionedSound.soundMinStep).toDouble() * Math.random()).toInt() + positionedSound.soundMinStep
						return
					}
				}
				return
			}
			val soundIndex: Int = (Math.random() * positionedSound.ambientSoundGroupIds!!.size.toDouble()).toInt()
			@Pc(510) val synthSound2: SoundEffect? = SoundEffectPlayer.getSoundEffect(Resource.SOUND_EFFECT, positionedSound.ambientSoundGroupIds!![soundIndex], 0)
			if (synthSound2 != null) {
				@Pc(517) val variableRateSoundPacket2: VariableRateSoundPacket = synthSound2.getMixedAudio().resample(SoundEffectPlayer.resampler)
				@Pc(525) val soundStream3: AreaSoundStream? = AreaSoundSampleMixer.create(variableRateSoundPacket2, rate, volume shl 6, panValue)
				soundStream3!!.setNumLoops(0)
				AmbientSound.gameSounds!!.addSubStream(soundStream3)
				positionedSound.stepsUntilNextSound = ((positionedSound.soundMaxStep - positionedSound.soundMinStep).toDouble() * Math.random()).toInt() + positionedSound.soundMinStep
				positionedSound.ambientSoundGroupStream = soundStream3
				return
			}
		}
	}

	@OriginalMember(owner = "client!cb", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun resetSounds() {
		@Pc(7) var i: Int = 0
		while (i < AreaSoundPlayer.AREA_SOUND_CACHE_SIZE) {
			@Pc(13) val sound: Sound? = AreaSoundPlayer.AREA_SOUND_CACHE[i]
			if (sound!!.type.toInt() == SoundType.VORBIS_VOICE_OVER) {
				if (sound.aClass2_Sub8_Sub5_1 == null) {
					sound.delay = Int.MIN_VALUE
				} else {
					AmbientSound.gameSounds!!.remove(sound.aClass2_Sub8_Sub5_1)
				}
			}
			i++
		}
	}
	@OriginalMember(owner = "client!as", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun reloadSoundSystem() {
		SoundEffectPlayer.shutdownPlayers()
		PcmPlayerManager.init(GamePreferences.uid!!.monoStereo!!.getValue() == 1)
		SoundEffectPlayer.jinglePlayer = PcmPlayerManager.createPcmPlayer(22050, GameShell.signLink, GameShell.gameCanvas, 0)
		SoundEffectPlayer.setJingleStream(MidiPcmStream.getStream(null))
		GameSoundsPcmPlayer.aClass33_31 = PcmPlayerManager.createPcmPlayer(2048, GameShell.signLink, GameShell.gameCanvas, 1)
		GameSoundsPcmPlayer.aClass33_31!!.setCurrentPcmStream(AmbientSound.gameSounds)
	}
	@OriginalMember(owner = "client!vp", name = "a", descriptor = "(Lclient!ga;ILclient!hg;I)V")
	@JvmStatic
	fun playSequenceSound(@OriginalArg(0) entity: Entity?, @OriginalArg(1) frameId: Int, @OriginalArg(2) seqType: SeqType?) {
		if (AreaSoundPlayer.AREA_SOUND_CACHE_SIZE >= 50 || (seqType == null || seqType.frameSounds == null || frameId >= seqType.frameSounds!!.size || seqType.frameSounds!![frameId] == null)) {
			return
		}
		@Pc(34) val bitpacked: Int = seqType.frameSounds!![frameId]!![0]
		@Pc(38) var soundId: Int = bitpacked shr 8
		@Pc(44) val timesRepeat: Int = (bitpacked shr 5) and 0x7
		@Pc(68) var local68: Int
		if (seqType.frameSounds!![frameId]!!.size > 1) {
			val soundIndex: Int = (seqType.frameSounds!![frameId]!!.size.toDouble() * Math.random()).toInt()
			if (soundIndex > 0) {
				soundId = seqType.frameSounds!![frameId]!![soundIndex]
			}
		}
		@Pc(81) val positionOffset: Int = bitpacked and 0x1F
		var rate: Int = 256
		if (seqType.soundMaxDelay != null && seqType.soundMinDelay != null) {
			rate = (Math.random() * (seqType.soundMinDelay!![frameId] - seqType.soundMaxDelay!![frameId]).toDouble()).toInt() + seqType.soundMaxDelay!![frameId]
		}
		@Pc(119) val volume: Int = if (seqType.frameSoundVolume == null) 255 else seqType.frameSoundVolume!![frameId]
		if (positionOffset == 0) {
			if (PlayerUpdate.localPlayer === entity) {
				if (!seqType.vorbis) {
					AreaSoundPlayer.playSoundSynth(volume, soundId, timesRepeat, 0, rate)
					return
				}
				SoundManager.playSoundVorbis(timesRepeat, rate, false, volume, 0, soundId)
			}
		} else if (GamePreferences.uid!!.ambientSoundVolume!!.getValue() != 0) {
			@Pc(160) val sceneX: Int = (entity!!.x - 256) shr 9
			@Pc(167) val sceneZ: Int = (entity.z - 256) shr 9
			@Pc(187) val bitpackedPosition: Int = if (PlayerUpdate.localPlayer === entity) 0 else (entity.level.toInt() shl 24) + (sceneX shl 16) + (sceneZ shl 8) + positionOffset
			AreaSoundPlayer.AREA_SOUND_CACHE[AreaSoundPlayer.AREA_SOUND_CACHE_SIZE++] = Sound((if (seqType.vorbis) 2 else 1).toByte(), soundId, timesRepeat, 0, volume, bitpackedPosition, rate, entity)
		}
	}

	@OriginalMember(owner = "client!ui", name = "a", descriptor = "(ILclient!hg;I)V")
	@JvmStatic
	fun playInterfaceSequenceSound(@OriginalArg(0) animation: Int, @OriginalArg(1) seqType: SeqType?) {
		if (AreaSoundPlayer.AREA_SOUND_CACHE_SIZE >= 50 || (seqType == null || seqType.frameSounds == null || animation >= seqType.frameSounds!!.size || seqType.frameSounds!![animation] == null)) {
			return
		}
		@Pc(34) val bitpacked: Int = seqType.frameSounds!![animation]!![0]
		@Pc(38) var soundId: Int = bitpacked shr 8
		@Pc(57) var local57: Int
		if (seqType.frameSounds!![animation]!!.size > 1) {
			val soundIndex: Int = (Math.random() * seqType.frameSounds!![animation]!!.size.toDouble()).toInt()
			if (soundIndex > 0) {
				soundId = seqType.frameSounds!![animation]!![soundIndex]
			}
		}
		@Pc(75) val timesRepeated: Int = (bitpacked shr 5) and 0x7
		var rate: Int = 256
		if (seqType.soundMaxDelay != null && seqType.soundMinDelay != null) {
			rate = Utils.randomLogUniform(seqType.soundMinDelay!![animation], seqType.soundMaxDelay!![animation])
		}
		if (seqType.vorbis) {
			SoundManager.playSoundVorbis(timesRepeated, rate, false, 255, 0, soundId)
		} else {
			AreaSoundPlayer.playSoundSynth(255, soundId, timesRepeated, 0, rate)
		}
	}

	@OriginalMember(owner = "client!wda", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun updateSound() {
		SoundResourceManager.updateSongFade()
		SoundEffectPlayer.updatePlayers()
	}
	@OriginalMember(owner = "client!fc", name = "e", descriptor = "(I)V")
	@JvmStatic
	fun initializeAudio() {
		PcmPlayerManager.init(GamePreferences.uid!!.monoStereo!!.getValue() == 1)
		SoundEffectPlayer.jinglePlayer = PcmPlayerManager.createPcmPlayer(22050, GameShell.signLink, GameShell.gameCanvas, 0)
		SoundEffectPlayer.setJingleStream(MidiPcmStream.getStream(null))
		GameSoundsPcmPlayer.aClass33_31 = PcmPlayerManager.createPcmPlayer(2048, GameShell.signLink, GameShell.gameCanvas, 1)
		AmbientSound.gameSounds = PcmStreamMixer()
		GameSoundsPcmPlayer.aClass33_31!!.setCurrentPcmStream(AmbientSound.gameSounds)
		SoundEffectPlayer.resampler = Resampler(22050, PcmPlayerManager.sampleRate)
		MidiChannelVolumeReset.resetChannelVolumes()
	}
}
