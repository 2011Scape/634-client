package com.jagex.game.runetek5.config.midi

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.cache.node.VorbisSound
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket
import com.jagex.game.runetek5.config.midi.instrument.MusicSample
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer

@OriginalClass("client!hs")
class SoundBankCache {

	@OriginalMember(owner = "client!hs", name = "b", descriptor = "Lclient!eq;")
	private val musicSampleCache: IterableHashTable = IterableHashTable(256)

	@OriginalMember(owner = "client!hs", name = "i", descriptor = "Lclient!eq;")
	private val sounds: IterableHashTable = IterableHashTable(256)

	@OriginalMember(owner = "client!hs", name = "e", descriptor = "Lclient!in;")
	private val musicSampleProvider: Js5ResourceProvider?

	@OriginalMember(owner = "client!hs", name = "g", descriptor = "Lclient!in;")
	private val soundEffectProvider: Js5ResourceProvider?

	@OriginalMember(owner = "client!hs", name = "<init>", descriptor = "(Lclient!in;Lclient!in;)V")
	constructor(@OriginalArg(0) synthArchive: Js5ResourceProvider?, @OriginalArg(1) vorbisArchive: Js5ResourceProvider?) {
		this.musicSampleProvider = vorbisArchive
		this.soundEffectProvider = synthArchive
	}

	@OriginalMember(owner = "client!hs", name = "a", descriptor = "(I[III)Lclient!ob;")
	private fun getSynthSound(@OriginalArg(0) fileId: Int, @OriginalArg(1) sampleBudget: IntArray?, @OriginalArg(3) archiveId: Int): VariableRateSoundPacket? {
		@Pc(15) val hash: Int = fileId xor ((archiveId ushr 12) or ((archiveId shl 4) and 0xFFF1))
		@Pc(21) val fullHash: Int = hash or (archiveId shl 16)
		@Pc(24) val key: Long = fullHash.toLong()
		@Pc(31) var variableRateSoundPacket: VariableRateSoundPacket? = this.sounds.get(key) as VariableRateSoundPacket?
		if (variableRateSoundPacket != null) {
			return variableRateSoundPacket
		} else if (sampleBudget == null || sampleBudget[0] > 0) {
			@Pc(49) val synthSound: SoundEffect? = SoundEffectPlayer.getSoundEffect(this.soundEffectProvider, archiveId, fileId)
			if (synthSound == null) {
				return null
			}
			variableRateSoundPacket = synthSound.getMixedAudio()
			this.sounds.put(key, variableRateSoundPacket)
			if (sampleBudget != null) {
				sampleBudget[0] -= variableRateSoundPacket.data!!.size
			}
			return variableRateSoundPacket
		} else {
			return null
		}
	}

	@OriginalMember(owner = "client!hs", name = "a", descriptor = "(I[II)Lclient!ob;")
	fun listEffect(@OriginalArg(0) archiveOrFileId: Int, @OriginalArg(1) sampleBudget: IntArray?): VariableRateSoundPacket? {
		if (this.soundEffectProvider!!.getCapacity() == 1) {
			return this.getSynthSound(archiveOrFileId, sampleBudget, 0)
		} else if (this.soundEffectProvider.getGroupCapacity(archiveOrFileId) == 1) {
			return this.getSynthSound(0, sampleBudget, archiveOrFileId)
		} else {
			throw RuntimeException()
		}
	}

	@OriginalMember(owner = "client!hs", name = "a", descriptor = "([III)Lclient!ob;")
	fun listMusic(@OriginalArg(0) sampleBudget: IntArray?, @OriginalArg(1) archiveOrFileId: Int): VariableRateSoundPacket? {
		if (this.musicSampleProvider!!.getCapacity() == 1) {
			return this.getMusicSample(archiveOrFileId, 0, sampleBudget)
		} else if (this.musicSampleProvider.getGroupCapacity(archiveOrFileId) == 1) {
			return this.getMusicSample(0, archiveOrFileId, sampleBudget)
		} else {
			throw RuntimeException()
		}
	}

	@OriginalMember(owner = "client!hs", name = "a", descriptor = "(IIB[I)Lclient!ob;")
	private fun getMusicSample(@OriginalArg(0) fileId: Int, @OriginalArg(1) archiveId: Int, @OriginalArg(3) sampleBudget: IntArray?): VariableRateSoundPacket? {
		@Pc(15) val hash: Int = fileId xor (((archiveId and 0xFFF) shl 4) or (archiveId ushr 12))
		@Pc(21) val fullHash: Int = hash or (archiveId shl 16)
		@Pc(34) val key: Long = fullHash.toLong() xor 0x100000000L
		@Pc(41) var variableRateSoundPacket: VariableRateSoundPacket? = this.sounds.get(key) as VariableRateSoundPacket?
		if (variableRateSoundPacket != null) {
			return variableRateSoundPacket
		} else if (sampleBudget == null || sampleBudget[0] > 0) {
			@Pc(60) var vorbisSound: VorbisSound? = this.musicSampleCache.get(key) as VorbisSound?
			if (vorbisSound == null) {
				vorbisSound = MusicSample.create(this.musicSampleProvider, archiveId, fileId)
				if (vorbisSound == null) {
					return null
				}
				this.musicSampleCache.put(key, vorbisSound)
			}
			variableRateSoundPacket = vorbisSound.toPcmSound(sampleBudget)
			if (variableRateSoundPacket == null) {
				return null
			} else {
				vorbisSound.unlink()
				this.sounds.put(key, variableRateSoundPacket)
				return variableRateSoundPacket
			}
		} else {
			return null
		}
	}
}
