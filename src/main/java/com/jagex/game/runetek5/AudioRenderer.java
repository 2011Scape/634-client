package com.jagex.game.runetek5;

import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity;
import com.jagex.game.runetek5.PlayMusic;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import com.jagex.game.runetek5.sound.Sound;
import com.jagex.game.runetek5.sound.jagtheora.AreaSoundStream;
import com.jagex.game.runetek5.sound.SoundType;
import com.jagex.game.runetek5.sound.AreaSoundSampleMixer;
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.config.midi.instrument.MusicSample;
import com.jagex.game.runetek5.sound.player.area.AmbientSound;
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer;
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.sound.SoundResourceManager;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.sound.player.song.SongPlayer;

public final class AudioRenderer {

	@OriginalMember(owner = "client!rg", name = "g", descriptor = "(I)V")
	public static void updateAreaSounds() {
		for (@Pc(11) int i = 0; i < AreaSoundPlayer.AREA_SOUND_CACHE_SIZE; i++) {
			@Pc(17) Sound sound = AreaSoundPlayer.AREA_SOUND_CACHE[i];
			@Pc(19) boolean shouldRemove = false;
			@Pc(151) int volume;
			if (sound.aClass2_Sub8_Sub5_1 == null) {
				sound.delay--;
				if (sound.delay >= (sound.isVorbis() ? -1500 : -10)) {
					if (sound.type == SoundType.SYNTH && sound.aClass61_1 == null) {
						sound.aClass61_1 = SoundEffectPlayer.getSoundEffect(Resource.SOUND_EFFECT, sound.id, 0);
						if (sound.aClass61_1 == null) {
							continue;
						}
						sound.delay += sound.aClass61_1.getDelay();
					} else if (sound.isVorbis() && (sound.aClass2_Sub22_1 == null || sound.aClass2_Sub29_Sub1_1 == null)) {
						if (sound.aClass2_Sub22_1 == null) {
							sound.aClass2_Sub22_1 = MusicSample.create(Resource.MIDI_INSTRUMENT, sound.id);
						}
						if (sound.aClass2_Sub22_1 == null) {
							continue;
						}
						if (sound.aClass2_Sub29_Sub1_1 == null) {
							sound.aClass2_Sub29_Sub1_1 = sound.aClass2_Sub22_1.toPcmSound(new int[] { 22050 });
							if (sound.aClass2_Sub29_Sub1_1 == null) {
								continue;
							}
						}
					}
					if (sound.delay < 0) {
						@Pc(129) int panValue = 8192;
						if (sound.coord == 0) {
							volume = sound.volume * (sound.type == SoundType.VORBIS_VOICE_OVER ? GamePreferences.uid.voiceOverVolume.getValue() : GamePreferences.uid.soundEffectVolume.getValue()) >> 2;
						} else {
							@Pc(160) int plane = sound.coord >> 24 & 0x3;
							if (plane == PlayerUpdate.localPlayer.level) {
								@Pc(175) int maxHearDistance = (sound.coord & 0xFF) << 9;
								@Pc(181) int entitySize = PlayerUpdate.localPlayer.getSize() << 8;
								@Pc(188) int tileX = sound.coord >> 16 & 0xFF;
								@Pc(200) int deltaX = (tileX << 9) + entitySize + 256 - PlayerUpdate.localPlayer.x;
								@Pc(207) int tileZ = sound.coord >> 8 & 0xFF;
								@Pc(218) int deltaZ = (tileZ << 9) + entitySize + 256 - PlayerUpdate.localPlayer.z;
								@Pc(226) int distance = Math.abs(deltaX) + Math.abs(deltaZ) - 512;
								if (distance > maxHearDistance) {
									sound.delay = -99999;
									continue;
								}
								if (distance < 0) {
									distance = 0;
								}
								volume = (maxHearDistance - distance) * GamePreferences.uid.ambientSoundVolume.getValue() * sound.volume / maxHearDistance >> 2;
								if (sound.entity != null && sound.entity instanceof AbstractInteractiveEntity) {
									@Pc(270) AbstractInteractiveEntity positionEntity = (AbstractInteractiveEntity) sound.entity;
									@Pc(273) short startX = positionEntity.startX;
									@Pc(276) short startZ = positionEntity.startZ;
								}
								if (deltaX != 0 || deltaZ != 0) {
									@Pc(297) int angle = -Camera.cameraYaw - (int) (Math.atan2((double) deltaX, (double) deltaZ) * 2607.5945876176133D) - 4096 & 0x3FFF;
									if (angle > 8192) {
										angle = 16384 - angle;
									}
									@Pc(314) int stereoWidth;
									if (distance <= 0) {
										stereoWidth = 8192;
									} else if (distance >= 4096) {
										stereoWidth = 16384;
									} else {
										stereoWidth = (8192 - distance) / 4096 + 8192;
									}
									panValue = (16384 - stereoWidth >> 1) + stereoWidth * angle / 8192;
								}
							} else {
								volume = 0;
							}
						}
						if (volume > 0) {
							@Pc(358) VariableRateSoundPacket variableRateSoundPacket = null;
							if (sound.type == SoundType.SYNTH) {
								variableRateSoundPacket = sound.aClass61_1.getMixedAudio().resample(SoundEffectPlayer.resampler);
							} else if (sound.isVorbis()) {
								variableRateSoundPacket = sound.aClass2_Sub29_Sub1_1;
							}
							@Pc(388) AreaSoundStream soundStream = sound.aClass2_Sub8_Sub5_1 = AreaSoundSampleMixer.create(variableRateSoundPacket, sound.rate, volume, panValue);
							soundStream.setNumLoops(sound.loops - 1);
							AmbientSound.gameSounds.addSubStream(soundStream);
						}
					}
				} else {
					shouldRemove = true;
				}
			} else if (!sound.aClass2_Sub8_Sub5_1.isLinked()) {
				shouldRemove = true;
			}
			if (shouldRemove) {
				AreaSoundPlayer.AREA_SOUND_CACHE_SIZE--;
				for (int slot = i; slot < AreaSoundPlayer.AREA_SOUND_CACHE_SIZE; slot++) {
					AreaSoundPlayer.AREA_SOUND_CACHE[slot] = AreaSoundPlayer.AREA_SOUND_CACHE[slot + 1];
				}
				i--;
			}
		}
		if (SoundEffectPlayer.soundJinglePlaying && !SoundResourceManager.isSongLoading()) {
			if (GamePreferences.uid.ingameMusicVolume.getValue() != 0 && AreaSoundPlayer.currentMusicArchive != -1) {
				if (SongPlayer.aClass2_Sub8_Sub3_6 == null) {
					PlayMusic.playMusic(AreaSoundPlayer.currentMusicArchive, Resource.MUSIC, GamePreferences.uid.ingameMusicVolume.getValue());
				} else {
					PlayMusic.playMusicOnStream(SongPlayer.aClass2_Sub8_Sub3_6, GamePreferences.uid.ingameMusicVolume.getValue(), Resource.MUSIC, AreaSoundPlayer.currentMusicArchive);
				}
			}
			SoundEffectPlayer.soundJinglePlaying = false;
			SongPlayer.aClass2_Sub8_Sub3_6 = null;
		} else if (GamePreferences.uid.ingameMusicVolume.getValue() != 0 && AreaSoundPlayer.currentMusicArchive != -1 && !SoundResourceManager.isSongLoading()) {
			@Pc(506) TcpPacket musicEndedPacket = TcpPacket.createPacket(GlToolkit.SOUND_EFFECT_MUSIC_ENDED, TcpPacket.isaac);
			musicEndedPacket.buffer.writeInt(AreaSoundPlayer.currentMusicArchive);
			BufferedConnectionContext.queuePacket(musicEndedPacket);
			AreaSoundPlayer.currentMusicArchive = -1;
		}
	}
}
