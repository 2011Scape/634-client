package com.jagex.game.runetek5;

import com.jagex.game.runetek5.sound.player.area.AmbientSound;
import com.jagex.game.runetek5.sound.Sound;
import com.jagex.game.runetek5.sound.jagtheora.AreaSoundStream;
import com.jagex.game.runetek5.sound.SoundType;
import com.jagex.game.runetek5.sound.AreaSoundSampleMixer;
import com.jagex.game.runetek5.config.midi.SoundEffect;
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.config.midi.instrument.MusicSample;
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer;
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.sound.jagtheora.MidiPcmStream;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.entity.Entity;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.sound.SoundResourceManager;
import com.jagex.game.runetek5.sound.player.PcmPlayerManager;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.sound.Resampler;
import com.jagex.game.runetek5.sound.jagtheora.PcmStreamMixer;

public final class SoundManager {

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(IIZBIII)V")
	public static void playSoundVorbis(@OriginalArg(0) int loops, @OriginalArg(1) int rate, @OriginalArg(2) boolean voiceOver, @OriginalArg(4) int volume, @OriginalArg(5) int delay, @OriginalArg(6) int soundId) {
		if ((voiceOver ? GamePreferences.uid.voiceOverVolume.getValue() : GamePreferences.uid.soundEffectVolume.getValue()) != 0 && loops != 0 && AreaSoundPlayer.AREA_SOUND_CACHE_SIZE < 50 && soundId != -1) {
			AreaSoundPlayer.AREA_SOUND_CACHE[AreaSoundPlayer.AREA_SOUND_CACHE_SIZE++] = new Sound((byte) (voiceOver ? SoundType.VORBIS_VOICE_OVER : SoundType.VORBIS), soundId, loops, delay, volume, 0, rate, null);
		}
	}

	@OriginalMember(owner = "client!gea", name = "a", descriptor = "(IIIIBLclient!hf;)V")
	public static void updateAmbientSound(@OriginalArg(0) int step, @OriginalArg(1) int absX, @OriginalArg(2) int level, @OriginalArg(3) int absZ, @OriginalArg(5) AmbientSound positionedSound) {
		if (positionedSound.soundId == -1 && positionedSound.ambientSoundGroupIds == null) {
			return;
		}
		@Pc(15) int hearDistance = 0;
		@Pc(25) int preferredVolume = positionedSound.ambientSoundVolume * GamePreferences.uid.ambientSoundVolume.getValue() >> 8;
		if (positionedSound.absStartX < absX) {
			hearDistance = absX - positionedSound.absStartX;
		} else if (absX < positionedSound.minX) {
			hearDistance = positionedSound.minX - absX;
		}
		if (positionedSound.plane < absZ) {
			hearDistance += absZ - positionedSound.plane;
		} else if (positionedSound.absEndX > absZ) {
			hearDistance += positionedSound.absEndX - absZ;
		}
		if (positionedSound.ambientSoundMinHearDistance == 0 || positionedSound.ambientSoundMinHearDistance < hearDistance - 256 || GamePreferences.uid.ambientSoundVolume.getValue() == 0 || positionedSound.level != level) {
			if (positionedSound.soundEffect != null) {
				AmbientSound.gameSounds.remove(positionedSound.soundEffect);
				positionedSound.soundEffectBuffer = null;
				positionedSound.soundEffect = null;
				positionedSound.instrumentEffect = null;
			}
			if (positionedSound.ambientSoundGroupStream != null) {
				AmbientSound.gameSounds.remove(positionedSound.ambientSoundGroupStream);
				positionedSound.ambientSoundGroupStream = null;
				positionedSound.ambientSoundInstrument = null;
				positionedSound.soundGroupBuffer = null;
			}
			return;
		}
		hearDistance -= 256;
		if (hearDistance < 0) {
			hearDistance = 0;
		}
		@Pc(162) int volume = (positionedSound.ambientSoundMinHearDistance - hearDistance) * preferredVolume / positionedSound.ambientSoundMinHearDistance;
		PlayerUpdate.localPlayer.getSize();
		@Pc(172) int panValue = 8192;
		@Pc(183) int deltaX = (positionedSound.minX + positionedSound.absStartX) / 2 - absX;
		@Pc(195) int deltaZ = (positionedSound.plane + positionedSound.absEndX) / 2 - absZ;
		@Pc(219) int local219;
		@Pc(230) int local230;
		if (deltaX != 0 || deltaZ != 0) {
			int relativeAngle = -Camera.cameraYaw - (int) (Math.atan2((double) deltaX, (double) deltaZ) * 2607.5945876176133D) - 4096 & 0x3FFF;
			if (relativeAngle > 8192) {
				relativeAngle = 16384 - relativeAngle;
			}
			int panSpread;
			if (hearDistance <= 0) {
				panSpread = 8192;
			} else if (hearDistance < 4096) {
				panSpread = hearDistance * 8192 / 4096 + 8192;
			} else {
				panSpread = 16384;
			}
			panValue = (16384 - panSpread >> 1) + panSpread * relativeAngle / 8192;
		}
		@Pc(372) AreaSoundStream soundStream;
		if (positionedSound.soundEffect != null) {
			positionedSound.soundEffect.setVolume(volume);
			positionedSound.soundEffect.setPanPosition(panValue);
		} else if (positionedSound.soundId >= 0) {
			int rate = positionedSound.minDelay == 256 && positionedSound.ambientSoundMaxDelay == 256 ? 256 : Utils.randomLogUniform(positionedSound.minDelay, positionedSound.ambientSoundMaxDelay);
			if (positionedSound.vorbis) {
				if (positionedSound.instrumentEffect == null) {
					positionedSound.instrumentEffect = MusicSample.create(Resource.MIDI_INSTRUMENT, positionedSound.soundId);
				}
				if (positionedSound.instrumentEffect != null) {
					if (positionedSound.soundEffectBuffer == null) {
						positionedSound.soundEffectBuffer = positionedSound.instrumentEffect.toPcmSound(new int[] { 22050 });
					}
					if (positionedSound.soundEffectBuffer != null) {
						soundStream = AreaSoundSampleMixer.create(positionedSound.soundEffectBuffer, rate, volume << 6, panValue);
						soundStream.setNumLoops(-1);
						AmbientSound.gameSounds.addSubStream(soundStream);
						positionedSound.soundEffect = soundStream;
					}
				}
			} else {
				@Pc(308) SoundEffect synthSound = SoundEffectPlayer.getSoundEffect(Resource.SOUND_EFFECT, positionedSound.soundId, 0);
				if (synthSound != null) {
					@Pc(315) VariableRateSoundPacket variableRateSoundPacket = synthSound.getMixedAudio().resample(SoundEffectPlayer.resampler);
					@Pc(323) AreaSoundStream soundStream2 = AreaSoundSampleMixer.create(variableRateSoundPacket, rate, volume << 6, panValue);
					soundStream2.setNumLoops(-1);
					AmbientSound.gameSounds.addSubStream(soundStream2);
					positionedSound.soundEffect = soundStream2;
				}
			}
		}
		if (positionedSound.ambientSoundGroupStream != null) {
			positionedSound.ambientSoundGroupStream.setVolume(volume);
			positionedSound.ambientSoundGroupStream.setPanPosition(panValue);
			if (positionedSound.ambientSoundGroupStream.isLinked()) {
				return;
			}
			positionedSound.ambientSoundInstrument = null;
			positionedSound.soundGroupBuffer = null;
			positionedSound.ambientSoundGroupStream = null;
		} else if (positionedSound.ambientSoundGroupIds != null && (positionedSound.stepsUntilNextSound -= step) <= 0) {
			int rate = positionedSound.minDelay == 256 && positionedSound.ambientSoundMaxDelay == 256 ? 256 : positionedSound.ambientSoundMaxDelay + (int) (Math.random() * (double) (positionedSound.minDelay - positionedSound.ambientSoundMaxDelay));
			if (positionedSound.random) {
				if (positionedSound.ambientSoundInstrument == null) {
					int soundIndex = (int) ((double) positionedSound.ambientSoundGroupIds.length * Math.random());
					positionedSound.ambientSoundInstrument = MusicSample.create(Resource.MIDI_INSTRUMENT, positionedSound.ambientSoundGroupIds[soundIndex]);
				}
				if (positionedSound.ambientSoundInstrument != null) {
					if (positionedSound.soundGroupBuffer == null) {
						positionedSound.soundGroupBuffer = positionedSound.ambientSoundInstrument.toPcmSound(new int[] { 22050 });
					}
					if (positionedSound.soundGroupBuffer != null) {
						soundStream = AreaSoundSampleMixer.create(positionedSound.soundGroupBuffer, rate, volume << 6, panValue);
						soundStream.setNumLoops(0);
						AmbientSound.gameSounds.addSubStream(soundStream);
						positionedSound.ambientSoundGroupStream = soundStream;
						positionedSound.stepsUntilNextSound = (int) ((double) (positionedSound.soundMaxStep - positionedSound.soundMinStep) * Math.random()) + positionedSound.soundMinStep;
						return;
					}
				}
				return;
			}
			int soundIndex = (int) (Math.random() * (double) positionedSound.ambientSoundGroupIds.length);
			@Pc(510) SoundEffect synthSound2 = SoundEffectPlayer.getSoundEffect(Resource.SOUND_EFFECT, positionedSound.ambientSoundGroupIds[soundIndex], 0);
			if (synthSound2 != null) {
				@Pc(517) VariableRateSoundPacket variableRateSoundPacket2 = synthSound2.getMixedAudio().resample(SoundEffectPlayer.resampler);
				@Pc(525) AreaSoundStream soundStream3 = AreaSoundSampleMixer.create(variableRateSoundPacket2, rate, volume << 6, panValue);
				soundStream3.setNumLoops(0);
				AmbientSound.gameSounds.addSubStream(soundStream3);
				positionedSound.stepsUntilNextSound = (int) ((double) (positionedSound.soundMaxStep - positionedSound.soundMinStep) * Math.random()) + positionedSound.soundMinStep;
				positionedSound.ambientSoundGroupStream = soundStream3;
				return;
			}
		}
	}

	@OriginalMember(owner = "client!cb", name = "b", descriptor = "(I)V")
	public static void resetSounds() {
		for (@Pc(7) int i = 0; i < AreaSoundPlayer.AREA_SOUND_CACHE_SIZE; i++) {
			@Pc(13) Sound sound = AreaSoundPlayer.AREA_SOUND_CACHE[i];
			if (sound.type == SoundType.VORBIS_VOICE_OVER) {
				if (sound.aClass2_Sub8_Sub5_1 == null) {
					sound.delay = Integer.MIN_VALUE;
				} else {
					AmbientSound.gameSounds.remove(sound.aClass2_Sub8_Sub5_1);
				}
			}
		}
	}
	@OriginalMember(owner = "client!as", name = "a", descriptor = "(I)V")
	public static void reloadSoundSystem() {
		SoundEffectPlayer.shutdownPlayers();
		PcmPlayerManager.init(GamePreferences.uid.monoStereo.getValue() == 1);
		SoundEffectPlayer.jinglePlayer = PcmPlayerManager.createPcmPlayer(22050, GameShell.signLink, GameShell.gameCanvas, 0);
		SoundEffectPlayer.setJingleStream(MidiPcmStream.getStream(null));
		GameSoundsPcmPlayer.aClass33_31 = PcmPlayerManager.createPcmPlayer(2048, GameShell.signLink, GameShell.gameCanvas, 1);
		GameSoundsPcmPlayer.aClass33_31.setCurrentPcmStream(AmbientSound.gameSounds);
	}
	@OriginalMember(owner = "client!vp", name = "a", descriptor = "(Lclient!ga;ILclient!hg;I)V")
	public static void playSequenceSound(@OriginalArg(0) Entity entity, @OriginalArg(1) int frameId, @OriginalArg(2) SeqType seqType) {
		if (AreaSoundPlayer.AREA_SOUND_CACHE_SIZE >= 50 || (seqType == null || seqType.frameSounds == null || frameId >= seqType.frameSounds.length || seqType.frameSounds[frameId] == null)) {
			return;
		}
		@Pc(34) int bitpacked = seqType.frameSounds[frameId][0];
		@Pc(38) int soundId = bitpacked >> 8;
		@Pc(44) int timesRepeat = bitpacked >> 5 & 0x7;
		@Pc(68) int local68;
		if (seqType.frameSounds[frameId].length > 1) {
			int soundIndex = (int) ((double) seqType.frameSounds[frameId].length * Math.random());
			if (soundIndex > 0) {
				soundId = seqType.frameSounds[frameId][soundIndex];
			}
		}
		@Pc(81) int positionOffset = bitpacked & 0x1F;
		int rate = 256;
		if (seqType.soundMaxDelay != null && seqType.soundMinDelay != null) {
			rate = (int) (Math.random() * (double) (seqType.soundMinDelay[frameId] - seqType.soundMaxDelay[frameId])) + seqType.soundMaxDelay[frameId];
		}
		@Pc(119) int volume = seqType.frameSoundVolume == null ? 255 : seqType.frameSoundVolume[frameId];
		if (positionOffset == 0) {
			if (PlayerUpdate.localPlayer == entity) {
				if (!seqType.vorbis) {
					AreaSoundPlayer.playSoundSynth(volume, soundId, timesRepeat, 0, rate);
					return;
				}
				SoundManager.playSoundVorbis(timesRepeat, rate, false, volume, 0, soundId);
			}
		} else if (GamePreferences.uid.ambientSoundVolume.getValue() != 0) {
			@Pc(160) int sceneX = entity.x - 256 >> 9;
			@Pc(167) int sceneZ = entity.z - 256 >> 9;
			@Pc(187) int bitpackedPosition = PlayerUpdate.localPlayer == entity ? 0 : (entity.level << 24) + (sceneX << 16) + (sceneZ << 8) + positionOffset;
			AreaSoundPlayer.AREA_SOUND_CACHE[AreaSoundPlayer.AREA_SOUND_CACHE_SIZE++] = new Sound((byte) (seqType.vorbis ? 2 : 1), soundId, timesRepeat, 0, volume, bitpackedPosition, rate, entity);
		}
	}

	@OriginalMember(owner = "client!ui", name = "a", descriptor = "(ILclient!hg;I)V")
	public static void playInterfaceSequenceSound(@OriginalArg(0) int animation, @OriginalArg(1) SeqType seqType) {
		if (AreaSoundPlayer.AREA_SOUND_CACHE_SIZE >= 50 || (seqType == null || seqType.frameSounds == null || animation >= seqType.frameSounds.length || seqType.frameSounds[animation] == null)) {
			return;
		}
		@Pc(34) int bitpacked = seqType.frameSounds[animation][0];
		@Pc(38) int soundId = bitpacked >> 8;
		@Pc(57) int local57;
		if (seqType.frameSounds[animation].length > 1) {
			int soundIndex = (int) (Math.random() * (double) seqType.frameSounds[animation].length);
			if (soundIndex > 0) {
				soundId = seqType.frameSounds[animation][soundIndex];
			}
		}
		@Pc(75) int timesRepeated = bitpacked >> 5 & 0x7;
		int rate = 256;
		if (seqType.soundMaxDelay != null && seqType.soundMinDelay != null) {
			rate = Utils.randomLogUniform(seqType.soundMinDelay[animation], seqType.soundMaxDelay[animation]);
		}
		if (seqType.vorbis) {
			SoundManager.playSoundVorbis(timesRepeated, rate, false, 255, 0, soundId);
		} else {
			AreaSoundPlayer.playSoundSynth(255, soundId, timesRepeated, 0, rate);
		}
	}

	@OriginalMember(owner = "client!wda", name = "b", descriptor = "(I)V")
	public static void updateSound() {
		SoundResourceManager.updateSongFade();
		SoundEffectPlayer.updatePlayers();
	}
	@OriginalMember(owner = "client!fc", name = "e", descriptor = "(I)V")
	public static void initializeAudio() {
		PcmPlayerManager.init(GamePreferences.uid.monoStereo.getValue() == 1);
		SoundEffectPlayer.jinglePlayer = PcmPlayerManager.createPcmPlayer(22050, GameShell.signLink, GameShell.gameCanvas, 0);
		SoundEffectPlayer.setJingleStream(MidiPcmStream.getStream(null));
		GameSoundsPcmPlayer.aClass33_31 = PcmPlayerManager.createPcmPlayer(2048, GameShell.signLink, GameShell.gameCanvas, 1);
		AmbientSound.gameSounds = new PcmStreamMixer();
		GameSoundsPcmPlayer.aClass33_31.setCurrentPcmStream(AmbientSound.gameSounds);
		SoundEffectPlayer.resampler = new Resampler(22050, PcmPlayerManager.sampleRate);
		MidiChannelVolumeReset.resetChannelVolumes();
	}
}
