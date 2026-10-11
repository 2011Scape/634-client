package com.jagex.game.runetek5.js5;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.NamedFileReference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!vw")
public final class Js5MasterIndex {

	@OriginalMember(owner = "client!vw", name = "c", descriptor = "[[I")
	public int[][] validFileIds;

	@OriginalMember(owner = "client!vw", name = "d", descriptor = "[I")
	public int[] groupVersions;

	@OriginalMember(owner = "client!vw", name = "e", descriptor = "Lclient!cj;")
	public NamedFileReference archiveNames;

	@OriginalMember(owner = "client!vw", name = "f", descriptor = "[I")
	public int[] groupCapacities;

	@OriginalMember(owner = "client!vw", name = "g", descriptor = "[I")
	public int[] groupSizes;

	@OriginalMember(owner = "client!vw", name = "i", descriptor = "I")
	public int size;

	@OriginalMember(owner = "client!vw", name = "j", descriptor = "[Lclient!cj;")
	public NamedFileReference[] namedFiles;

	@OriginalMember(owner = "client!vw", name = "k", descriptor = "[I")
	public int[] groupChecksums;

	@OriginalMember(owner = "client!vw", name = "l", descriptor = "[[B")
	public byte[][] groupHashes;

	@OriginalMember(owner = "client!vw", name = "n", descriptor = "I")
	public int revision;

	@OriginalMember(owner = "client!vw", name = "o", descriptor = "[I")
	public int[] nameHashes;

	@OriginalMember(owner = "client!vw", name = "p", descriptor = "I")
	public int capacity;

	@OriginalMember(owner = "client!vw", name = "q", descriptor = "[[I")
	public int[][] fileNameHashes;

	@OriginalMember(owner = "client!vw", name = "t", descriptor = "[I")
	public int[] groupIds;

	@OriginalMember(owner = "client!vw", name = "a", descriptor = "I")
	public final int headerCrc;

	@OriginalMember(owner = "client!vw", name = "v", descriptor = "[B")
	private byte[] headerWhirlpool;

	@OriginalMember(owner = "client!vw", name = "<init>", descriptor = "([BI[B)V")
	public Js5MasterIndex(@OriginalArg(0) byte[] tablePayload, @OriginalArg(1) int expectedCrc, @OriginalArg(2) byte[] expectedWhirlpool) {
		this.headerCrc = Packet.crc32(tablePayload.length, tablePayload);
		if (expectedCrc != this.headerCrc) {
			throw new RuntimeException();
		}
		if (expectedWhirlpool != null) {
			if (expectedWhirlpool.length != 64) {
				throw new RuntimeException();
			}
			this.headerWhirlpool = Whirlpool.digest(0, tablePayload, tablePayload.length);
			for (@Pc(40) int i = 0; i < 64; i++) {
				if (expectedWhirlpool[i] != this.headerWhirlpool[i]) {
					throw new RuntimeException();
				}
			}
		}
		this.decode(tablePayload);
	}

	@OriginalMember(owner = "client!vw", name = "a", descriptor = "(B[B)V")
	private void decode(@OriginalArg(1) byte[] tablePayload) {
		@Pc(12) Packet packet = new Packet(Js5Compression.decompress(tablePayload));
		@Pc(16) int protocol = packet.readUByte();
		if (protocol < 5 || protocol > 6) {
			throw new RuntimeException();
		}
		if (protocol < 6) {
			this.revision = 0;
		} else {
			this.revision = packet.readInt();
		}
		@Pc(45) int flags = packet.readUByte();
		@Pc(56) boolean named = (flags & 0x1) != 0;
		@Pc(64) boolean hasWhirlpool = (flags & 0x2) != 0;
		this.size = packet.readUShort();
		@Pc(71) int offset = 0;
		this.groupIds = new int[this.size];
		@Pc(78) int maxGroupId = -1;
		for (@Pc(80) int i = 0; i < this.size; i++) {
			this.groupIds[i] = offset += packet.readUShort();
			if (maxGroupId < this.groupIds[i]) {
				maxGroupId = this.groupIds[i];
			}
		}
		this.capacity = maxGroupId + 1;
		if (hasWhirlpool) {
			this.groupHashes = new byte[this.capacity][];
		}
		this.groupChecksums = new int[this.capacity];
		this.groupCapacities = new int[this.capacity];
		this.groupSizes = new int[this.capacity];
		this.groupVersions = new int[this.capacity];
		this.validFileIds = new int[this.capacity][];
		@Pc(159) int i;
		@Pc(173) int index;
		if (named) {
			this.nameHashes = new int[this.capacity];
			for (i = 0; i < this.capacity; i++) {
				this.nameHashes[i] = -1;
			}
			for (index = 0; index < this.size; index++) {
				this.nameHashes[this.groupIds[index]] = packet.readInt();
			}
			this.archiveNames = new NamedFileReference(this.nameHashes);
		}
		for (i = 0; i < this.size; i++) {
			this.groupChecksums[this.groupIds[i]] = packet.readInt();
		}
		if (hasWhirlpool) {
			for (index = 0; index < this.size; index++) {
				@Pc(240) byte[] groupHash = new byte[64];
				packet.readBytes(0, 64, groupHash);
				this.groupHashes[this.groupIds[index]] = groupHash;
			}
		}
		for (index = 0; index < this.size; index++) {
			this.groupVersions[this.groupIds[index]] = packet.readInt();
		}
		for (@Pc(280) int j = 0; j < this.size; j++) {
			this.groupSizes[this.groupIds[j]] = packet.readUShort();
		}
		@Pc(306) int local306;
		@Pc(313) int local313;
		@Pc(315) int local315;
		@Pc(323) int local323;
		@Pc(340) int local340;
		for (@Pc(299) int k = 0; k < this.size; k++) {
			int groupId = this.groupIds[k];
			offset = 0;
			int fileCount = this.groupSizes[groupId];
			int maxFileId = -1;
			this.validFileIds[groupId] = new int[fileCount];
			for (int fileIndex = 0; fileIndex < fileCount; fileIndex++) {
				int fileId = this.validFileIds[groupId][fileIndex] = offset += packet.readUShort();
				if (fileId > maxFileId) {
					maxFileId = fileId;
				}
			}
			this.groupCapacities[groupId] = maxFileId + 1;
			if (fileCount == maxFileId + 1) {
				this.validFileIds[groupId] = null;
			}
		}
		if (!named) {
			return;
		}
		this.fileNameHashes = new int[maxGroupId + 1][];
		this.namedFiles = new NamedFileReference[maxGroupId + 1];
		for (int groupIndex = 0; groupIndex < this.size; groupIndex++) {
			int groupId = this.groupIds[groupIndex];
			int fileCount = this.groupSizes[groupId];
			this.fileNameHashes[groupId] = new int[this.groupCapacities[groupId]];
			for (int slot = 0; slot < this.groupCapacities[groupId]; slot++) {
				this.fileNameHashes[groupId][slot] = -1;
			}
			for (int fileIndex = 0; fileIndex < fileCount; fileIndex++) {
				@Pc(458) int fileId;
				if (this.validFileIds[groupId] == null) {
					fileId = fileIndex;
				} else {
					fileId = this.validFileIds[groupId][fileIndex];
				}
				this.fileNameHashes[groupId][fileId] = packet.readInt();
			}
			this.namedFiles[groupId] = new NamedFileReference(this.fileNameHashes[groupId]);
		}
	}
}
