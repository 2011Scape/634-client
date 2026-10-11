package com.jagex.game.runetek5.config.midi.instrument;

import com.jagex.game.runetek5.cache.node.VorbisSound;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class MusicSample {
	@OriginalMember(owner = "client!hp", name = "a", descriptor = "(Lclient!in;)Z")
	public static boolean isLoaded(@OriginalArg(0) Js5ResourceProvider resource) {
		if (!VorbisSound.fullyCached) {
			@Pc(7) byte[] data = resource.getFile(0, 0);
			if (data == null) {
				return false;
			}
			VorbisSound.decodeHeader(data);
		}
		return true;
	}

	@OriginalMember(owner = "client!hp", name = "s", descriptor = "[Lclient!vba;")
	public static MusicSamepleMapping[] mappings;

	@OriginalMember(owner = "client!hp", name = "G", descriptor = "[Lclient!fq;")
	public static MusicSampleCodebook[] codebooks;

	@OriginalMember(owner = "client!hp", name = "D", descriptor = "[Lclient!rm;")
	public static MusicSampleFloor[] floors;

	@OriginalMember(owner = "client!hp", name = "y", descriptor = "[Lclient!eda;")
	public static MusicSampleResidue[] residues;

	@OriginalMember(owner = "client!hp", name = "a", descriptor = "(Lclient!in;II)Lclient!hp;")
	public static VorbisSound create(@OriginalArg(0) Js5ResourceProvider vorbis, @OriginalArg(1) int archiveId, @OriginalArg(2) int fileId) {
		if (isLoaded(vorbis)) {
			@Pc(16) byte[] data = vorbis.getFile(archiveId, fileId);
			return data == null ? null : new VorbisSound(data);
		} else {
			vorbis.isFileReady(archiveId, fileId);
			return null;
		}
	}

	@OriginalMember(owner = "client!hp", name = "b", descriptor = "(I)I")
	public static int getBits(@OriginalArg(0) int bits) {
		@Pc(1) int value = 0;
		@Pc(3) int read = 0;
		@Pc(8) int bitOff;
		while (bits >= 8 - VorbisSound.bitPos) {
			bitOff = 8 - VorbisSound.bitPos;
			@Pc(14) int mask = (0x1 << bitOff) - 1;
			value += (VorbisSound.instrumentData[VorbisSound.pos] >> VorbisSound.bitPos & mask) << read;
			VorbisSound.bitPos = 0;
			VorbisSound.pos++;
			read += bitOff;
			bits -= bitOff;
		}
		if (bits > 0) {
			bitOff = (0x1 << bits) - 1;
			value += (VorbisSound.instrumentData[VorbisSound.pos] >> VorbisSound.bitPos & bitOff) << read;
			VorbisSound.bitPos += bits;
		}
		return value;
	}

	@OriginalMember(owner = "client!hp", name = "a", descriptor = "(Lclient!in;I)Lclient!hp;")
	public static VorbisSound create(@OriginalArg(0) Js5ResourceProvider resource, @OriginalArg(1) int fileId) {
		if (isLoaded(resource)) {
			@Pc(14) byte[] data = resource.getFile(fileId);
			return data == null ? null : new VorbisSound(data);
		} else {
			resource.isFileReady(fileId);
			return null;
		}
	}

	private MusicSample() {
		/* empty */
	}
	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(III)I")
	public static int reverseBits(@OriginalArg(0) int bitCount, @OriginalArg(2) int value) {
		@Pc(7) int reversed = 0;
		while (bitCount > 0) {
			reversed = value & 0x1 | reversed << 1;
			bitCount--;
			value >>>= 0x1;
		}
		return reversed;
	}
}
