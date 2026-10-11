package com.jagex.game.runetek5.sound.player.area;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.midi.SoundEffect;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.sound.Resampler;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.sound.player.PcmPlayer;
import com.jagex.game.runetek5.sound.SoundResourceManager;
import com.jagex.game.runetek5.GameSoundsPcmPlayer;
import com.jagex.game.runetek5.config.bas.BasType;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.sound.jagtheora.MidiPcmStream;
import com.jagex.game.runetek5.client.ClientState;

public final class SoundEffectPlayer {
	@OriginalMember(owner = "client!kn", name = "d", descriptor = "Z")
	public static boolean soundJinglePlaying = false;

	@OriginalMember(owner = "client!b", name = "e", descriptor = "Lclient!pe;")
	public static Resampler resampler;

	@OriginalMember(owner = "client!ck", name = "a", descriptor = "(Lclient!in;II)Lclient!ck;")
	public static SoundEffect getSoundEffect(@OriginalArg(0) Js5ResourceProvider resource, @OriginalArg(1) int archiveId, @OriginalArg(2) int fileId) {
		@Pc(5) byte[] data = resource.getFile(archiveId, fileId);
		return data == null ? null : new SoundEffect(new Packet(data));
	}

	private SoundEffectPlayer() {
		/* empty */
	}
	@OriginalMember(owner = "client!tf", name = "E", descriptor = "Lclient!cba;")
	public static PcmPlayer jinglePlayer;
	@OriginalMember(owner = "client!ic", name = "a", descriptor = "(IB)V")
	public static void setMusicVolume(@OriginalArg(0) int volume) {
		if (SoundResourceManager.songLoadState == 0) {
			SoundResourceManager.midiPcmStream.setVolume(volume);
		} else {
			SoundResourceManager.SOUND_LEVEL = volume;
		}
	}
	@OriginalMember(owner = "client!kh", name = "a", descriptor = "(Z)V")
	public static void updatePlayers() {
		if (SoundEffectPlayer.jinglePlayer != null) {
			SoundEffectPlayer.jinglePlayer.cycle();
		}
		if (GameSoundsPcmPlayer.aClass33_31 != null) {
			GameSoundsPcmPlayer.aClass33_31.cycle();
		}
	}

	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(ILclient!ke;)I")
	public static int getSoundEffectId(@OriginalArg(1) PlayerEntity playerEntity) {
		@Pc(8) int soundId = playerEntity.defaultSoundEffectId;
		@Pc(12) BasType basType = playerEntity.getBasType();
		if (playerEntity.movingSequence.sequenceId == -1 || playerEntity.movingSequence.hasType) {
			soundId = playerEntity.walkingSoundEffectId;
		} else if (playerEntity.movingSequence.sequenceId == basType.runningSequence || basType.runDir3 == playerEntity.movingSequence.sequenceId || playerEntity.movingSequence.sequenceId == basType.runDir1 || basType.runDir2 == playerEntity.movingSequence.sequenceId) {
			soundId = playerEntity.runningSoundEffectId;
		} else if (playerEntity.movingSequence.sequenceId == basType.teleportSequence || playerEntity.movingSequence.sequenceId == basType.teleDir3 || basType.teleDir1 == playerEntity.movingSequence.sequenceId || basType.teleDir2 == playerEntity.movingSequence.sequenceId) {
			soundId = playerEntity.teleportingSoundEffectId;
		}
		return soundId;
	}
	@OriginalMember(owner = "client!pd", name = "a", descriptor = "(I)V")
	public static void shutdownPlayers() {
		if (SoundEffectPlayer.jinglePlayer != null) {
			SoundEffectPlayer.jinglePlayer.shutdown();
		}
		if (GameSoundsPcmPlayer.aClass33_31 != null) {
			GameSoundsPcmPlayer.aClass33_31.shutdown();
		}
	}
	@OriginalMember(owner = "client!jh", name = "a", descriptor = "(ZILclient!ma;)V")
	public static void setJingleStream(@OriginalArg(2) MidiPcmStream mixBuss) {
		SoundEffectPlayer.jinglePlayer.setCurrentPcmStream(mixBuss);
		SoundResourceManager.initializePcmResources(Resource.SOUND_EFFECT, mixBuss, Resource.MIDI_INSTRUMENT, SoundEffectPlayer.jinglePlayer, Resource.SOUND_MIDI);
	}
}
