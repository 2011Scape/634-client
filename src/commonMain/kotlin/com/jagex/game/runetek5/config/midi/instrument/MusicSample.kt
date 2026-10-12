package com.jagex.game.runetek5.config.midi.instrument

import com.jagex.game.runetek5.cache.node.VorbisSound
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object MusicSample {
	@OriginalMember(owner = "client!hp", name = "a", descriptor = "(Lclient!in;)Z")
	@JvmStatic
	fun isLoaded(@OriginalArg(0) resource: Js5ResourceProvider?): Boolean {
		if (!VorbisSound.fullyCached) {
			@Pc(7) val data: ByteArray? = resource!!.getFile(0, 0)
			if (data == null) {
				return false
			}
			VorbisSound.decodeHeader(data)
		}
		return true
	}

	@OriginalMember(owner = "client!hp", name = "s", descriptor = "[Lclient!vba;")
	@JvmField
	var mappings: Array<MusicSamepleMapping?>? = null

	@OriginalMember(owner = "client!hp", name = "G", descriptor = "[Lclient!fq;")
	@JvmField
	var codebooks: Array<MusicSampleCodebook?>? = null

	@OriginalMember(owner = "client!hp", name = "D", descriptor = "[Lclient!rm;")
	@JvmField
	var floors: Array<MusicSampleFloor?>? = null

	@OriginalMember(owner = "client!hp", name = "y", descriptor = "[Lclient!eda;")
	@JvmField
	var residues: Array<MusicSampleResidue?>? = null

	@OriginalMember(owner = "client!hp", name = "a", descriptor = "(Lclient!in;II)Lclient!hp;")
	@JvmStatic
	fun create(@OriginalArg(0) vorbis: Js5ResourceProvider?, @OriginalArg(1) archiveId: Int, @OriginalArg(2) fileId: Int): VorbisSound? {
		if (isLoaded(vorbis)) {
			@Pc(16) val data: ByteArray? = vorbis!!.getFile(archiveId, fileId)
			return if (data == null) null else VorbisSound(data)
		} else {
			vorbis!!.isFileReady(archiveId, fileId)
			return null
		}
	}

	@OriginalMember(owner = "client!hp", name = "b", descriptor = "(I)I")
	@JvmStatic
	fun getBits(@OriginalArg(0) bits: Int): Int {
		var bits: Int = bits
		@Pc(1) var value: Int = 0
		@Pc(3) var read: Int = 0
		@Pc(8) var bitOff: Int
		while (bits >= 8 - VorbisSound.bitPos) {
			bitOff = 8 - VorbisSound.bitPos
			@Pc(14) val mask: Int = (0x1 shl bitOff) - 1
			value += ((VorbisSound.instrumentData!![VorbisSound.pos].toInt() shr VorbisSound.bitPos) and mask) shl read
			VorbisSound.bitPos = 0
			VorbisSound.pos++
			read += bitOff
			bits -= bitOff
		}
		if (bits > 0) {
			bitOff = (0x1 shl bits) - 1
			value += ((VorbisSound.instrumentData!![VorbisSound.pos].toInt() shr VorbisSound.bitPos) and bitOff) shl read
			VorbisSound.bitPos += bits
		}
		return value
	}

	@OriginalMember(owner = "client!hp", name = "a", descriptor = "(Lclient!in;I)Lclient!hp;")
	@JvmStatic
	fun create(@OriginalArg(0) resource: Js5ResourceProvider?, @OriginalArg(1) fileId: Int): VorbisSound? {
		if (isLoaded(resource)) {
			@Pc(14) val data: ByteArray? = resource!!.getFile(fileId)
			return if (data == null) null else VorbisSound(data)
		} else {
			resource!!.isFileReady(fileId)
			return null
		}
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(III)I")
	@JvmStatic
	fun reverseBits(@OriginalArg(0) bitCount: Int, @OriginalArg(2) value: Int): Int {
		var bitCount: Int = bitCount
		var value: Int = value
		@Pc(7) var reversed: Int = 0
		while (bitCount > 0) {
			reversed = (value and 0x1) or (reversed shl 1)
			bitCount--
			value = value ushr 0x1
		}
		return reversed
	}
}
