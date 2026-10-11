package com.jagex.game.runetek5.config.midi;

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.cache.node.VorbisSound;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket;
import com.jagex.game.runetek5.config.midi.instrument.MusicSample;
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer;

@OriginalClass("client!hs")
public final class SoundBankCache {

	@OriginalMember(owner = "client!hs", name = "b", descriptor = "Lclient!eq;")
	private final IterableHashTable musicSampleCache = new IterableHashTable(256);

	@OriginalMember(owner = "client!hs", name = "i", descriptor = "Lclient!eq;")
	private final IterableHashTable sounds = new IterableHashTable(256);

	@OriginalMember(owner = "client!hs", name = "e", descriptor = "Lclient!in;")
	private final Js5ResourceProvider musicSampleProvider;

	@OriginalMember(owner = "client!hs", name = "g", descriptor = "Lclient!in;")
	private final Js5ResourceProvider soundEffectProvider;

	@OriginalMember(owner = "client!hs", name = "<init>", descriptor = "(Lclient!in;Lclient!in;)V")
	public SoundBankCache(@OriginalArg(0) Js5ResourceProvider synthArchive, @OriginalArg(1) Js5ResourceProvider vorbisArchive) {
		this.musicSampleProvider = vorbisArchive;
		this.soundEffectProvider = synthArchive;
	}

	@OriginalMember(owner = "client!hs", name = "a", descriptor = "(I[III)Lclient!ob;")
	private VariableRateSoundPacket getSynthSound(@OriginalArg(0) int fileId, @OriginalArg(1) int[] sampleBudget, @OriginalArg(3) int archiveId) {
		@Pc(15) int hash = fileId ^ (archiveId >>> 12 | archiveId << 4 & 0xFFF1);
		@Pc(21) int fullHash = hash | archiveId << 16;
		@Pc(24) long key = (long) fullHash;
		@Pc(31) VariableRateSoundPacket variableRateSoundPacket = (VariableRateSoundPacket) this.sounds.get(key);
		if (variableRateSoundPacket != null) {
			return variableRateSoundPacket;
		} else if (sampleBudget == null || sampleBudget[0] > 0) {
			@Pc(49) SoundEffect synthSound = SoundEffectPlayer.getSoundEffect(this.soundEffectProvider, archiveId, fileId);
			if (synthSound == null) {
				return null;
			}
			variableRateSoundPacket = synthSound.getMixedAudio();
			this.sounds.put(key, variableRateSoundPacket);
			if (sampleBudget != null) {
				sampleBudget[0] -= variableRateSoundPacket.data.length;
			}
			return variableRateSoundPacket;
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!hs", name = "a", descriptor = "(I[II)Lclient!ob;")
	public VariableRateSoundPacket listEffect(@OriginalArg(0) int archiveOrFileId, @OriginalArg(1) int[] sampleBudget) {
		if (this.soundEffectProvider.getCapacity() == 1) {
			return this.getSynthSound(archiveOrFileId, sampleBudget, 0);
		} else if (this.soundEffectProvider.getGroupCapacity(archiveOrFileId) == 1) {
			return this.getSynthSound(0, sampleBudget, archiveOrFileId);
		} else {
			throw new RuntimeException();
		}
	}

	@OriginalMember(owner = "client!hs", name = "a", descriptor = "([III)Lclient!ob;")
	public VariableRateSoundPacket listMusic(@OriginalArg(0) int[] sampleBudget, @OriginalArg(1) int archiveOrFileId) {
		if (this.musicSampleProvider.getCapacity() == 1) {
			return this.getMusicSample(archiveOrFileId, 0, sampleBudget);
		} else if (this.musicSampleProvider.getGroupCapacity(archiveOrFileId) == 1) {
			return this.getMusicSample(0, archiveOrFileId, sampleBudget);
		} else {
			throw new RuntimeException();
		}
	}

	@OriginalMember(owner = "client!hs", name = "a", descriptor = "(IIB[I)Lclient!ob;")
	private VariableRateSoundPacket getMusicSample(@OriginalArg(0) int fileId, @OriginalArg(1) int archiveId, @OriginalArg(3) int[] sampleBudget) {
		@Pc(15) int hash = fileId ^ ((archiveId & 0xFFF) << 4 | archiveId >>> 12);
		@Pc(21) int fullHash = hash | archiveId << 16;
		@Pc(34) long key = (long) fullHash ^ 0x100000000L;
		@Pc(41) VariableRateSoundPacket variableRateSoundPacket = (VariableRateSoundPacket) this.sounds.get(key);
		if (variableRateSoundPacket != null) {
			return variableRateSoundPacket;
		} else if (sampleBudget == null || sampleBudget[0] > 0) {
			@Pc(60) VorbisSound vorbisSound = (VorbisSound) this.musicSampleCache.get(key);
			if (vorbisSound == null) {
				vorbisSound = MusicSample.create(this.musicSampleProvider, archiveId, fileId);
				if (vorbisSound == null) {
					return null;
				}
				this.musicSampleCache.put(key, vorbisSound);
			}
			variableRateSoundPacket = vorbisSound.toPcmSound(sampleBudget);
			if (variableRateSoundPacket == null) {
				return null;
			} else {
				vorbisSound.unlink();
				this.sounds.put(key, variableRateSoundPacket);
				return variableRateSoundPacket;
			}
		} else {
			return null;
		}
	}
}
