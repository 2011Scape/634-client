package com.jagex.game.runetek5

import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.midi.instrument.MusicSample
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.sound.AreaSoundSampleMixer
import com.jagex.game.runetek5.sound.Sound
import com.jagex.game.runetek5.sound.SoundResourceManager
import com.jagex.game.runetek5.sound.SoundType
import com.jagex.game.runetek5.sound.jagtheora.AreaSoundStream
import com.jagex.game.runetek5.sound.player.area.AmbientSound
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer
import com.jagex.game.runetek5.sound.player.song.SongPlayer
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

object AudioRenderer {

	@OriginalMember(owner = "client!rg", name = "g", descriptor = "(I)V")
	@JvmStatic
	fun updateAreaSounds() {
		@Pc(11) var i: Int = 0
		while (i < AreaSoundPlayer.AREA_SOUND_CACHE_SIZE) {
			@Pc(17) val sound: Sound = AreaSoundPlayer.AREA_SOUND_CACHE[i]!!
			@Pc(19) var shouldRemove: Boolean = false
			@Pc(151) var volume: Int
			if (sound.aClass2_Sub8_Sub5_1 == null) {
				sound.delay--
				if (sound.delay >= (if (sound.isVorbis()) -1500 else -10)) {
					if (sound.type.toInt() == SoundType.SYNTH && sound.aClass61_1 == null) {
						sound.aClass61_1 = SoundEffectPlayer.getSoundEffect(Resource.SOUND_EFFECT, sound.id, 0)
						if (sound.aClass61_1 == null) {
							i++
							continue
						}
						sound.delay += sound.aClass61_1!!.getDelay()
					} else if (sound.isVorbis() && (sound.aClass2_Sub22_1 == null || sound.aClass2_Sub29_Sub1_1 == null)) {
						if (sound.aClass2_Sub22_1 == null) {
							sound.aClass2_Sub22_1 = MusicSample.create(Resource.MIDI_INSTRUMENT, sound.id)
						}
						if (sound.aClass2_Sub22_1 == null) {
							i++
							continue
						}
						if (sound.aClass2_Sub29_Sub1_1 == null) {
							sound.aClass2_Sub29_Sub1_1 = sound.aClass2_Sub22_1!!.toPcmSound(intArrayOf(22050))
							if (sound.aClass2_Sub29_Sub1_1 == null) {
								i++
								continue
							}
						}
					}
					if (sound.delay < 0) {
						@Pc(129) var panValue: Int = 8192
						if (sound.coord == 0) {
							volume = (sound.volume * (if (sound.type.toInt() == SoundType.VORBIS_VOICE_OVER) GamePreferences.uid!!.voiceOverVolume!!.getValue() else GamePreferences.uid!!.soundEffectVolume!!.getValue())) shr 2
						} else {
							@Pc(160) val plane: Int = (sound.coord shr 24) and 0x3
							if (plane == PlayerUpdate.localPlayer!!.level.toInt()) {
								@Pc(175) val maxHearDistance: Int = (sound.coord and 0xFF) shl 9
								@Pc(181) val entitySize: Int = PlayerUpdate.localPlayer!!.getSize() shl 8
								@Pc(188) val tileX: Int = (sound.coord shr 16) and 0xFF
								@Pc(200) val deltaX: Int = (tileX shl 9) + entitySize + 256 - PlayerUpdate.localPlayer!!.x
								@Pc(207) val tileZ: Int = (sound.coord shr 8) and 0xFF
								@Pc(218) val deltaZ: Int = (tileZ shl 9) + entitySize + 256 - PlayerUpdate.localPlayer!!.z
								@Pc(226) var distance: Int = kotlin.math.abs(deltaX) + kotlin.math.abs(deltaZ) - 512
								if (distance > maxHearDistance) {
									sound.delay = -99999
									i++
									continue
								}
								if (distance < 0) {
									distance = 0
								}
								volume = ((maxHearDistance - distance) * GamePreferences.uid!!.ambientSoundVolume!!.getValue() * sound.volume / maxHearDistance) shr 2
								if (sound.entity != null && sound.entity is AbstractInteractiveEntity) {
									@Pc(270) val positionEntity: AbstractInteractiveEntity = sound.entity as AbstractInteractiveEntity
									@Pc(273) val startX: Short = positionEntity.startX
									@Pc(276) val startZ: Short = positionEntity.startZ
								}
								if (deltaX != 0 || deltaZ != 0) {
									@Pc(297) var angle: Int = (-Camera.cameraYaw - (kotlin.math.atan2(deltaX.toDouble(), deltaZ.toDouble()) * 2607.5945876176133).toInt() - 4096) and 0x3FFF
									if (angle > 8192) {
										angle = 16384 - angle
									}
									@Pc(314) val stereoWidth: Int
									if (distance <= 0) {
										stereoWidth = 8192
									} else if (distance >= 4096) {
										stereoWidth = 16384
									} else {
										stereoWidth = (8192 - distance) / 4096 + 8192
									}
									panValue = ((16384 - stereoWidth) shr 1) + stereoWidth * angle / 8192
								}
							} else {
								volume = 0
							}
						}
						if (volume > 0) {
							@Pc(358) var variableRateSoundPacket: VariableRateSoundPacket? = null
							if (sound.type.toInt() == SoundType.SYNTH) {
								variableRateSoundPacket = sound.aClass61_1!!.getMixedAudio().resample(SoundEffectPlayer.resampler)
							} else if (sound.isVorbis()) {
								variableRateSoundPacket = sound.aClass2_Sub29_Sub1_1
							}
							@Pc(388) val soundStream: AreaSoundStream? = AreaSoundSampleMixer.create(variableRateSoundPacket, sound.rate, volume, panValue)
							sound.aClass2_Sub8_Sub5_1 = soundStream
							soundStream!!.setNumLoops(sound.loops - 1)
							AmbientSound.gameSounds!!.addSubStream(soundStream)
						}
					}
				} else {
					shouldRemove = true
				}
			} else if (!sound.aClass2_Sub8_Sub5_1!!.isLinked()) {
				shouldRemove = true
			}
			if (shouldRemove) {
				AreaSoundPlayer.AREA_SOUND_CACHE_SIZE--
				var slot: Int = i
				while (slot < AreaSoundPlayer.AREA_SOUND_CACHE_SIZE) {
					AreaSoundPlayer.AREA_SOUND_CACHE[slot] = AreaSoundPlayer.AREA_SOUND_CACHE[slot + 1]
					slot++
				}
				i--
			}
			i++
		}
		if (SoundEffectPlayer.soundJinglePlaying && !SoundResourceManager.isSongLoading()) {
			if (GamePreferences.uid!!.ingameMusicVolume!!.getValue() != 0 && AreaSoundPlayer.currentMusicArchive != -1) {
				if (SongPlayer.aClass2_Sub8_Sub3_6 == null) {
					PlayMusic.playMusic(AreaSoundPlayer.currentMusicArchive, Resource.MUSIC, GamePreferences.uid!!.ingameMusicVolume!!.getValue())
				} else {
					PlayMusic.playMusicOnStream(SongPlayer.aClass2_Sub8_Sub3_6, GamePreferences.uid!!.ingameMusicVolume!!.getValue(), Resource.MUSIC, AreaSoundPlayer.currentMusicArchive)
				}
			}
			SoundEffectPlayer.soundJinglePlaying = false
			SongPlayer.aClass2_Sub8_Sub3_6 = null
		} else if (GamePreferences.uid!!.ingameMusicVolume!!.getValue() != 0 && AreaSoundPlayer.currentMusicArchive != -1 && !SoundResourceManager.isSongLoading()) {
			@Pc(506) val musicEndedPacket: TcpPacket = TcpPacket.createPacket(GlToolkit.SOUND_EFFECT_MUSIC_ENDED, TcpPacket.isaac)
			musicEndedPacket.buffer!!.writeInt(AreaSoundPlayer.currentMusicArchive)
			BufferedConnectionContext.queuePacket(musicEndedPacket)
			AreaSoundPlayer.currentMusicArchive = -1
		}
	}
}
