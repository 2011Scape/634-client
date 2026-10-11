package com.jagex.game.runetek5.js5;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.core.util.Arrays;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.js5.request.FileWorker;
import com.jagex.game.runetek5.ExplicitRuntimeException;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.ByteArrayWrapper;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!in")
public final class Js5ResourceProvider {

	@OriginalMember(owner = "client!in", name = "E", descriptor = "[Ljava/lang/Object;")
	private Object[] packed;

	@OriginalMember(owner = "client!in", name = "M", descriptor = "[[Ljava/lang/Object;")
	private Object[][] archiveFiles;

	@OriginalMember(owner = "client!in", name = "d", descriptor = "Lclient!vw;")
	private Js5MasterIndex masterIndex = null;

	@OriginalMember(owner = "client!in", name = "H", descriptor = "Lclient!ci;")
	private final FileWorker fileWorker;

	@OriginalMember(owner = "client!in", name = "D", descriptor = "Z")
	private final boolean directMemoryMode;

	@OriginalMember(owner = "client!in", name = "i", descriptor = "I")
	public int loadType;

	@OriginalMember(owner = "client!in", name = "<init>", descriptor = "(Lclient!ci;ZI)V")
	public Js5ResourceProvider(@OriginalArg(0) FileWorker resourceProvider, @OriginalArg(1) boolean directMemoryMode, @OriginalArg(2) int loadType) {
		if (loadType < 0 || loadType > 2) {
			throw new IllegalArgumentException("js5: Invalid value " + loadType + " supplied for discardunpacked");
		}
		this.fileWorker = resourceProvider;
		this.directMemoryMode = directMemoryMode;
		this.loadType = loadType;
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(I)I")
	public int getIndexCrc() {
		if (!this.ensureMasterIndexLoaded()) {
			throw new IllegalStateException("");
		}
		return this.masterIndex.headerCrc;
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(Ljava/lang/String;Ljava/lang/String;I)[B")
	public byte[] getFile(@OriginalArg(0) String groupName, @OriginalArg(1) String fileName) {
		if (!this.ensureMasterIndexLoaded()) {
			return null;
		}
		@Pc(12) String string3 = groupName.toLowerCase();
		@Pc(15) String string4 = fileName.toLowerCase();
		@Pc(24) int groupId = this.masterIndex.archiveNames.find(Js5ResourceProvider.hashName(string3));
		if (this.isGroupValid(groupId)) {
			@Pc(52) int fileId = this.masterIndex.namedFiles[groupId].find(Js5ResourceProvider.hashName(string4));
			return this.getFile(groupId, fileId);
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(IB)Z")
	private boolean isGroupValid(@OriginalArg(0) int groupId) {
		if (!this.ensureMasterIndexLoaded()) {
			return false;
		} else if (groupId >= 0 && this.masterIndex.groupCapacities.length > groupId && this.masterIndex.groupCapacities[groupId] != 0) {
			return true;
		} else if (Js5ResourceProvider.THROW_ON_INVALID_ID) {
			throw new IllegalArgumentException(Integer.toString(groupId));
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(III[I)[B")
	public byte[] getFile(@OriginalArg(0) int groupId, @OriginalArg(1) int fileId, @OriginalArg(3) int[] xteaKeys) {
		if (!this.isFileValid(fileId, groupId)) {
			return null;
		}
		if (this.archiveFiles[groupId] == null || this.archiveFiles[groupId][fileId] == null) {
			@Pc(37) boolean unpacked = this.unpackGroup(fileId, xteaKeys, groupId);
			if (!unpacked) {
				this.fetchGroup(groupId);
				unpacked = this.unpackGroup(fileId, xteaKeys, groupId);
				if (!unpacked) {
					return null;
				}
			}
		}
		@Pc(64) byte[] data = ByteArrayWrapper.unwrap(false, this.archiveFiles[groupId][fileId]);
		if (this.loadType == 1) {
			this.archiveFiles[groupId][fileId] = null;
			if (this.masterIndex.groupCapacities[groupId] == 1) {
				this.archiveFiles[groupId] = null;
			}
		} else if (this.loadType == 2) {
			this.archiveFiles[groupId] = null;
		}
		return data;
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(IB[II)Z")
	private boolean unpackGroup(@OriginalArg(0) int fileId, @OriginalArg(2) int[] xteaKeys, @OriginalArg(3) int groupId) {
		if (!this.isGroupValid(groupId)) {
			return false;
		} else if (this.packed[groupId] == null) {
			return false;
		} else {
			@Pc(25) int fileCount = this.masterIndex.groupSizes[groupId];
			@Pc(31) int[] fileIds = this.masterIndex.validFileIds[groupId];
			if (this.archiveFiles[groupId] == null) {
				this.archiveFiles[groupId] = new Object[this.masterIndex.groupCapacities[groupId]];
			}
			@Pc(51) Object[] objects = this.archiveFiles[groupId];
			@Pc(53) boolean fullyCached = true;
			for (@Pc(55) int i = 0; i < fileCount; i++) {
				@Pc(61) int id;
				if (fileIds == null) {
					id = i;
				} else {
					id = fileIds[i];
				}
				if (objects[id] == null) {
					fullyCached = false;
					break;
				}
			}
			if (fullyCached) {
				return true;
			}
			@Pc(125) byte[] containerData;
			if (xteaKeys == null || xteaKeys[0] == 0 && xteaKeys[1] == 0 && xteaKeys[2] == 0 && xteaKeys[3] == 0) {
				containerData = ByteArrayWrapper.unwrap(false, this.packed[groupId]);
			} else {
				containerData = ByteArrayWrapper.unwrap(true, this.packed[groupId]);
				@Pc(140) Packet packet = new Packet(containerData);
				packet.decryptXtea(xteaKeys, packet.buffer.length);
			}
			@Pc(152) byte[] groupData;
			try {
				groupData = Js5Compression.decompress(containerData);
			} catch (@Pc(154) RuntimeException ex) {
				throw ExplicitRuntimeException.wrap(ex, "T3 - " + (xteaKeys != null) + "," + groupId + "," + containerData.length + "," + Packet.crc32(containerData.length, containerData) + "," + Packet.crc32(containerData.length - 2, containerData) + "," + this.masterIndex.groupChecksums[groupId] + "," + this.masterIndex.headerCrc);
			}
			if (this.directMemoryMode) {
				this.packed[groupId] = null;
			}
			@Pc(237) int local237;
			if (fileCount > 1) {
				@Pc(244) int blockCount;
				@Pc(257) Packet packet2;
				@Pc(265) int local265;
				@Pc(269) int local269;
				@Pc(271) int local271;
				@Pc(325) int local325;
				@Pc(327) int local327;
				@Pc(331) int local331;
				@Pc(333) int local333;
				int tablePosition;
				if (this.loadType == 2) {
					tablePosition = groupData.length;
					tablePosition--;
					blockCount = groupData[tablePosition] & 0xFF;
					tablePosition -= blockCount * 4 * fileCount;
					packet2 = new Packet(groupData);
					@Pc(442) int fileSize = 0;
					int matchedFileId = 0;
					packet2.currentPosition = tablePosition;
					for (int block = 0; block < blockCount; block++) {
						int chunkSize = 0;
						for (int fileIndex = 0; fileIndex < fileCount; fileIndex++) {
							chunkSize += packet2.readInt();
							int candidateFileId;
							if (fileIds == null) {
								candidateFileId = fileIndex;
							} else {
								candidateFileId = fileIds[fileIndex];
							}
							if (candidateFileId == fileId) {
								matchedFileId = candidateFileId;
								fileSize += chunkSize;
							}
						}
					}
					if (fileSize == 0) {
						return true;
					}
					@Pc(508) byte[] fileData = new byte[fileSize];
					packet2.currentPosition = tablePosition;
					int writeOffset = 0;
					int dataOffset = 0;
					for (int block = 0; block < blockCount; block++) {
						int chunkSize = 0;
						for (int fileIndex = 0; fileIndex < fileCount; fileIndex++) {
							chunkSize += packet2.readInt();
							@Pc(537) int id;
							if (fileIds == null) {
								id = fileIndex;
							} else {
								id = fileIds[fileIndex];
							}
							if (id == fileId) {
								Arrays.copy(groupData, dataOffset, fileData, writeOffset, chunkSize);
								writeOffset += chunkSize;
							}
							dataOffset += chunkSize;
						}
					}
					objects[matchedFileId] = fileData;
				} else {
					tablePosition = groupData.length;
					@Pc(240) int lastByteIndex = tablePosition - 1;
					blockCount = groupData[lastByteIndex] & 0xFF;
					@Pc(252) int sizeTableOffset = lastByteIndex - fileCount * 4 * blockCount;
					packet2 = new Packet(groupData);
					@Pc(260) int[] fileSizes = new int[fileCount];
					packet2.currentPosition = sizeTableOffset;
					for (int block = 0; block < blockCount; block++) {
						int chunkSize = 0;
						for (int fileIndex = 0; fileIndex < fileCount; fileIndex++) {
							chunkSize += packet2.readInt();
							fileSizes[fileIndex] += chunkSize;
						}
					}
					@Pc(297) byte[][] fileBuffers = new byte[fileCount][];
					for (int fileIndex = 0; fileIndex < fileCount; fileIndex++) {
						fileBuffers[fileIndex] = new byte[fileSizes[fileIndex]];
						fileSizes[fileIndex] = 0;
					}
					packet2.currentPosition = sizeTableOffset;
					int dataOffset = 0;
					for (int block = 0; block < blockCount; block++) {
						int chunkSize = 0;
						for (int fileIndex = 0; fileIndex < fileCount; fileIndex++) {
							chunkSize += packet2.readInt();
							Arrays.copy(groupData, dataOffset, fileBuffers[fileIndex], fileSizes[fileIndex], chunkSize);
							dataOffset += chunkSize;
							fileSizes[fileIndex] += chunkSize;
						}
					}
					for (int file = 0; file < fileCount; file++) {
						int resolvedFileId;
						if (fileIds == null) {
							resolvedFileId = file;
						} else {
							resolvedFileId = fileIds[file];
						}
						if (this.loadType == 0) {
							objects[resolvedFileId] = ByteArrayWrapper.wrap(fileBuffers[file]);
						} else {
							objects[resolvedFileId] = fileBuffers[file];
						}
					}
				}
			} else {
				int singleFileId;
				if (fileIds == null) {
					singleFileId = 0;
				} else {
					singleFileId = fileIds[0];
				}
				if (this.loadType == 0) {
					objects[singleFileId] = ByteArrayWrapper.wrap(groupData);
				} else {
					objects[singleFileId] = groupData;
				}
			}
			return true;
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(II)[I")
	public int[] getFileIds(@OriginalArg(0) int groupId) {
		if (!this.isGroupValid(groupId)) {
			return null;
		}
		@Pc(24) int[] fileIds = this.masterIndex.validFileIds[groupId];
		if (fileIds == null) {
			fileIds = new int[this.masterIndex.groupSizes[groupId]];
			@Pc(35) int i = 0;
			while (i < fileIds.length) {
				fileIds[i] = i++;
			}
		}
		return fileIds;
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(Ljava/lang/String;I)I")
	public int getGroupCompletePercentage(@OriginalArg(0) String groupName) {
		if (this.ensureMasterIndexLoaded()) {
			@Pc(12) String string2 = groupName.toLowerCase();
			@Pc(28) int groupId = this.masterIndex.archiveNames.find(Js5ResourceProvider.hashName(string2));
			return this.getGroupCompletePercentage(groupId);
		} else {
			return 0;
		}
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(B)I")
	public int getCompletePercentage() {
		if (!this.ensureMasterIndexLoaded()) {
			return 0;
		}
		@Pc(11) int total = 0;
		@Pc(13) int progress = 0;
		for (@Pc(15) int i = 0; i < this.packed.length; i++) {
			if (this.masterIndex.groupSizes[i] > 0) {
				progress += this.getGroupCompletePercentage(i);
				total += 100;
			}
		}
		if (total == 0) {
			return 100;
		} else {
			return progress * 100 / total;
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(Z)Z")
	private boolean ensureMasterIndexLoaded() {
		if (this.masterIndex == null) {
			this.masterIndex = this.fileWorker.index();
			if (this.masterIndex == null) {
				return false;
			}
			this.packed = new Object[this.masterIndex.capacity];
			this.archiveFiles = new Object[this.masterIndex.capacity][];
		}
		return true;
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(ILjava/lang/String;)Z")
	public boolean groupExists(@OriginalArg(1) String groupName) {
		if (this.ensureMasterIndexLoaded()) {
			@Pc(17) String string2 = groupName.toLowerCase();
			@Pc(26) int groupId = this.masterIndex.archiveNames.find(Js5ResourceProvider.hashName(string2));
			return groupId >= 0;
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!in", name = "c", descriptor = "(I)Z")
	public boolean fullyLoaded() {
		if (!this.ensureMasterIndexLoaded()) {
			return false;
		}
		@Pc(13) boolean loaded = true;
		for (@Pc(15) int i = 0; i < this.masterIndex.groupIds.length; i++) {
			@Pc(23) int groupId = this.masterIndex.groupIds[i];
			if (this.packed[groupId] == null) {
				this.fetchGroup(groupId);
				if (this.packed[groupId] == null) {
					loaded = false;
				}
			}
		}
		return loaded;
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(BI)V")
	private void requestGroup(@OriginalArg(1) int groupId) {
		this.fileWorker.requestGroup(groupId);
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(Ljava/lang/String;ILjava/lang/String;)Z")
	private boolean isFileReady(@OriginalArg(0) String groupName, @OriginalArg(2) String fileName) {
		if (!this.ensureMasterIndexLoaded()) {
			return false;
		}
		@Pc(12) String string3 = groupName.toLowerCase();
		@Pc(15) String string4 = fileName.toLowerCase();
		@Pc(26) int groupId = this.masterIndex.archiveNames.find(Js5ResourceProvider.hashName(string3));
		if (this.isGroupValid(groupId)) {
			@Pc(53) int fileId = this.masterIndex.namedFiles[groupId].find(Js5ResourceProvider.hashName(string4));
			return this.isFileReady(groupId, fileId);
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(IZZ)V")
	public void discardNames(@OriginalArg(2) boolean discardGroupNames) {
		if (!this.ensureMasterIndexLoaded()) {
			return;
		}
		if (discardGroupNames) {
			this.masterIndex.nameHashes = null;
			this.masterIndex.archiveNames = null;
		}
		this.masterIndex.fileNameHashes = null;
		this.masterIndex.namedFiles = null;
	}

	@OriginalMember(owner = "client!in", name = "d", descriptor = "(I)V")
	public void discardPacked() {
		if (this.packed != null) {
			for (@Pc(17) int i = 0; i < this.packed.length; i++) {
				this.packed[i] = null;
			}
		}
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(II)V")
	private void fetchGroup(@OriginalArg(0) int groupId) {
		if (this.directMemoryMode) {
			this.packed[groupId] = this.fileWorker.fetchgroup(groupId);
		} else {
			this.packed[groupId] = ByteArrayWrapper.wrap(this.fileWorker.fetchgroup(groupId));
		}
	}

	@OriginalMember(owner = "client!in", name = "c", descriptor = "(II)I")
	public int getGroupCapacity(@OriginalArg(1) int groupId) {
		return this.isGroupValid(groupId) ? this.masterIndex.groupCapacities[groupId] : 0;
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(BI)Z")
	public boolean isGroupReady(@OriginalArg(1) int groupId) {
		if (!this.isGroupValid(groupId)) {
			return false;
		} else if (this.packed[groupId] == null) {
			this.fetchGroup(groupId);
			return this.packed[groupId] != null;
		} else {
			return true;
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(Ljava/lang/String;Z)V")
	public void requestGroup(@OriginalArg(0) String groupName) {
		if (this.ensureMasterIndexLoaded()) {
			@Pc(15) String string2 = groupName.toLowerCase();
			@Pc(24) int groupId = this.masterIndex.archiveNames.find(Js5ResourceProvider.hashName(string2));
			this.requestGroup(groupId);
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(III)[B")
	public byte[] getFile(@OriginalArg(1) int groupId, @OriginalArg(2) int fileId) {
		return this.getFile(groupId, fileId, null);
	}

	@OriginalMember(owner = "client!in", name = "d", descriptor = "(II)V")
	public void discardUnpacked(@OriginalArg(0) int groupId) {
		if (this.isGroupValid(groupId) && this.archiveFiles != null) {
			this.archiveFiles[groupId] = null;
		}
	}

	@OriginalMember(owner = "client!in", name = "c", descriptor = "(B)V")
	public void discardUnpacked() {
		if (this.archiveFiles != null) {
			for (@Pc(6) int i = 0; i < this.archiveFiles.length; i++) {
				this.archiveFiles[i] = null;
			}
		}
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(ILjava/lang/String;)Z")
	public boolean isGroupReady(@OriginalArg(1) String groupName) {
		if (this.ensureMasterIndexLoaded()) {
			@Pc(12) String string2 = groupName.toLowerCase();
			@Pc(23) int groupId = this.masterIndex.archiveNames.find(Js5ResourceProvider.hashName(string2));
			return this.isGroupReady(groupId);
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!in", name = "e", descriptor = "(I)I")
	public int getCapacity() {
		return this.ensureMasterIndexLoaded() ? this.masterIndex.groupCapacities.length : -1;
	}

	@OriginalMember(owner = "client!in", name = "c", descriptor = "(BI)[B")
	public byte[] getFile(@OriginalArg(1) int id) {
		if (!this.ensureMasterIndexLoaded()) {
			return null;
		} else if (this.masterIndex.groupCapacities.length == 1) {
			return this.getFile(0, id);
		} else if (!this.isGroupValid(id)) {
			return null;
		} else if (this.masterIndex.groupCapacities[id] == 1) {
			return this.getFile(id, 0);
		} else {
			throw new RuntimeException();
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(BLjava/lang/String;)Z")
	public boolean isFileReady(@OriginalArg(1) String string) {
		@Pc(10) int groupId = this.getGroupId("");
		return groupId == -1 ? this.isFileReady(string, "") : this.isFileReady("", string);
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(Ljava/lang/String;I)I")
	public int getGroupId(@OriginalArg(0) String groupName) {
		if (this.ensureMasterIndexLoaded()) {
			@Pc(16) String string2 = groupName.toLowerCase();
			@Pc(25) int groupId = this.masterIndex.archiveNames.find(Js5ResourceProvider.hashName(string2));
			return this.isGroupValid(groupId) ? groupId : -1;
		} else {
			return -1;
		}
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(III)Z")
	private boolean isFileValid(@OriginalArg(0) int fileId, @OriginalArg(2) int groupId) {
		if (!this.ensureMasterIndexLoaded()) {
			return false;
		} else if (groupId >= 0 && fileId >= 0 && groupId < this.masterIndex.groupCapacities.length && fileId < this.masterIndex.groupCapacities[groupId]) {
			return true;
		} else if (Js5ResourceProvider.THROW_ON_INVALID_ID) {
			throw new IllegalArgumentException(groupId + "," + fileId);
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!in", name = "e", descriptor = "(II)I")
	private int getGroupCompletePercentage(@OriginalArg(1) int groupId) {
		if (this.isGroupValid(groupId)) {
			return this.packed[groupId] == null ? this.fileWorker.completePercentage(groupId) : 100;
		} else {
			return 0;
		}
	}

	@OriginalMember(owner = "client!in", name = "c", descriptor = "(III)Z")
	public boolean isFileReady(@OriginalArg(1) int groupId, @OriginalArg(2) int fileId) {
		if (!this.isFileValid(fileId, groupId)) {
			return false;
		} else if (this.archiveFiles[groupId] != null && this.archiveFiles[groupId][fileId] != null) {
			return true;
		} else if (this.packed[groupId] == null) {
			this.fetchGroup(groupId);
			return this.packed[groupId] != null;
		} else {
			return true;
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(ZI)Z")
	public boolean isFileReady(@OriginalArg(1) int id) {
		if (!this.ensureMasterIndexLoaded()) {
			return false;
		} else if (this.masterIndex.groupCapacities.length == 1) {
			return this.isFileReady(0, id);
		} else if (!this.isGroupValid(id)) {
			return false;
		} else if (this.masterIndex.groupCapacities[id] == 1) {
			return this.isFileReady(id, 0);
		} else {
			throw new RuntimeException();
		}
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(Ljava/lang/String;Ljava/lang/String;I)Z")
	public boolean fileExists(@OriginalArg(0) String fileName, @OriginalArg(1) String groupName) {
		if (!this.ensureMasterIndexLoaded()) {
			return false;
		}
		@Pc(12) String string3 = groupName.toLowerCase();
		@Pc(20) String string4 = fileName.toLowerCase();
		@Pc(29) int groupId = this.masterIndex.archiveNames.find(Js5ResourceProvider.hashName(string3));
		if (groupId < 0) {
			return false;
		} else {
			@Pc(47) int fileId = this.masterIndex.namedFiles[groupId].find(Js5ResourceProvider.hashName(string4));
			return fileId >= 0;
		}
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(IB)I")
	public int getGroupId(@OriginalArg(0) int nameHash) {
		if (this.ensureMasterIndexLoaded()) {
			@Pc(16) int groupId = this.masterIndex.archiveNames.find(nameHash);
			return this.isGroupValid(groupId) ? groupId : -1;
		} else {
			return -1;
		}
	}
	@OriginalMember(owner = "client!vea", name = "b", descriptor = "(Ljava/lang/String;I)I")
	public static int hashName(@OriginalArg(0) String string) {
		@Pc(8) int length = string.length();
		@Pc(10) int hash = 0;
		for (@Pc(12) int i = 0; i < length; i++) {
			hash = Utils.charToCp1252(string.charAt(i)) + (hash << 5) - hash;
		}
		return hash;
	}
	@OriginalMember(owner = "client!vi", name = "g", descriptor = "Z")
	public static final boolean THROW_ON_INVALID_ID = false;
}
