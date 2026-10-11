package com.jagex.game.runetek5.js5.request.remote;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.Decompressor;
import com.jagex.game.runetek5.js5.Js5MasterIndex;
import com.jagex.game.runetek5.js5.request.FileWorker;
import com.jagex.game.runetek5.js5.request.Js5ResourceRequest;
import com.jagex.game.runetek5.js5.request.Js5WorkerRequestMessage;
import com.jagex.game.runetek5.js5.request.local.LocalRequester;
import com.jagex.game.runetek5.js5.Js5WorkerThread;
import java.util.zip.CRC32;
import com.jagex.game.runetek5.js5.Whirlpool;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!bja")
public final class RemoteFileWorker extends FileWorker {

	@OriginalMember(owner = "client!bja", name = "i", descriptor = "Lclient!vw;")
	private Js5MasterIndex masterIndex;

	@OriginalMember(owner = "client!bja", name = "r", descriptor = "[B")
	private byte[] groupStatus;

	@OriginalMember(owner = "client!bja", name = "L", descriptor = "Z")
	private boolean prefetchAll;

	@OriginalMember(owner = "client!bja", name = "B", descriptor = "I")
	private int verifiedGroups = 0;

	@OriginalMember(owner = "client!bja", name = "A", descriptor = "Lclient!eq;")
	private final IterableHashTable pendingRequests = new IterableHashTable(16);

	@OriginalMember(owner = "client!bja", name = "N", descriptor = "I")
	private int group = 0;

	@OriginalMember(owner = "client!bja", name = "J", descriptor = "Lclient!uh;")
	private final Deque prefetchEntries = new Deque();

	@OriginalMember(owner = "client!bja", name = "O", descriptor = "J")
	private long cleanupCycle = 0L;

	@OriginalMember(owner = "client!bja", name = "q", descriptor = "I")
	private final int indexId;

	@OriginalMember(owner = "client!bja", name = "F", descriptor = "Lclient!nw;")
	private final Decompressor cacheDataFile;

	@OriginalMember(owner = "client!bja", name = "I", descriptor = "Z")
	private boolean verifyAll;

	@OriginalMember(owner = "client!bja", name = "M", descriptor = "Lclient!uh;")
	private Deque generalRequests;

	@OriginalMember(owner = "client!bja", name = "k", descriptor = "I")
	private final int tableCrc;

	@OriginalMember(owner = "client!bja", name = "m", descriptor = "[B")
	private final byte[] tableDigest;

	@OriginalMember(owner = "client!bja", name = "s", descriptor = "I")
	private final int tableRevision;

	@OriginalMember(owner = "client!bja", name = "E", descriptor = "Lclient!nw;")
	private final Decompressor masterIndexFile;

	@OriginalMember(owner = "client!bja", name = "P", descriptor = "Z")
	private final boolean discardOrphans;

	@OriginalMember(owner = "client!bja", name = "n", descriptor = "Lclient!tha;")
	private final Js5WorkerThread standardRequester;

	@OriginalMember(owner = "client!bja", name = "u", descriptor = "Lclient!aca;")
	private final LocalRequester localRequester;

	@OriginalMember(owner = "client!bja", name = "v", descriptor = "Lclient!ffa;")
	private Js5WorkerRequestMessage masterIndexRequest;

	@OriginalMember(owner = "client!bja", name = "<init>", descriptor = "(ILclient!nw;Lclient!nw;Lclient!tha;Lclient!aca;I[BIZ)V")
	public RemoteFileWorker(@OriginalArg(0) int archive, @OriginalArg(1) Decompressor fileSystem_Client, @OriginalArg(2) Decompressor fileSystem_Client2, @OriginalArg(3) Js5WorkerThread js5WorkerThread, @OriginalArg(4) LocalRequester cachedResourceWorker, @OriginalArg(5) int tableCrc, @OriginalArg(6) byte[] tableDigest, @OriginalArg(7) int tableRevision, @OriginalArg(8) boolean discardOrphans) {
		this.indexId = archive;
		this.cacheDataFile = fileSystem_Client;
		if (this.cacheDataFile == null) {
			this.verifyAll = false;
		} else {
			this.verifyAll = true;
			this.generalRequests = new Deque();
		}
		this.tableCrc = tableCrc;
		this.tableDigest = tableDigest;
		this.tableRevision = tableRevision;
		this.masterIndexFile = fileSystem_Client2;
		this.discardOrphans = discardOrphans;
		this.standardRequester = js5WorkerThread;
		this.localRequester = cachedResourceWorker;
		if (this.masterIndexFile != null) {
			this.masterIndexRequest = this.localRequester.createPriorityRequest(this.masterIndexFile, this.indexId);
		}
	}

	@OriginalMember(owner = "client!bja", name = "a", descriptor = "(I)I")
	public int getTotalVerifiedGroups() {
		if (this.masterIndex == null) {
			return 0;
		} else if (this.verifyAll) {
			@Pc(29) Node node = this.generalRequests.first();
			return node == null ? 0 : (int) node.key;
		} else {
			return this.masterIndex.size;
		}
	}

	@OriginalMember(owner = "client!bja", name = "b", descriptor = "(I)V")
	public void processPrefetchQueue() {
		if (this.generalRequests == null || this.index() == null) {
			return;
		}
		for (@Pc(24) Node node = this.prefetchEntries.first(); node != null; node = this.prefetchEntries.next()) {
			@Pc(30) int pointer = (int) node.key;
			if (pointer < 0 || pointer >= this.masterIndex.capacity || this.masterIndex.groupSizes[pointer] == 0) {
				node.unlink();
			} else {
				if (this.groupStatus[pointer] == 0) {
					this.request(pointer, 1);
				}
				if (this.groupStatus[pointer] == -1) {
					this.request(pointer, 2);
				}
				if (this.groupStatus[pointer] == 1) {
					node.unlink();
				}
			}
		}
	}

	@OriginalMember(owner = "client!bja", name = "c", descriptor = "(I)V")
	public void loop() {
		if (this.generalRequests != null) {
			if (this.index() == null) {
				return;
			}
			@Pc(18) boolean completed;
			@Pc(23) Node node;
			@Pc(29) int pointer;
			@Pc(114) Node node2;
			if (this.verifyAll) {
				completed = true;
				for (node = this.generalRequests.first(); node != null; node = this.generalRequests.next()) {
					pointer = (int) node.key;
					if (this.groupStatus[pointer] == 0) {
						this.request(pointer, 1);
					}
					if (this.groupStatus[pointer] == 0) {
						completed = false;
					} else {
						node.unlink();
					}
				}
				while (this.group < this.masterIndex.groupSizes.length) {
					if (this.masterIndex.groupSizes[this.group] == 0) {
						this.group++;
					} else {
						if (this.localRequester.requestCount >= 250) {
							completed = false;
							break;
						}
						if (this.groupStatus[this.group] == 0) {
							this.request(this.group, 1);
						}
						if (this.groupStatus[this.group] == 0) {
							node2 = new Node();
							node2.key = this.group;
							this.generalRequests.addLast(node2);
							completed = false;
						}
						this.group++;
					}
				}
				if (completed) {
					this.group = 0;
					this.verifyAll = false;
				}
			} else if (this.prefetchAll) {
				completed = true;
				for (node = this.generalRequests.first(); node != null; node = this.generalRequests.next()) {
					pointer = (int) node.key;
					if (this.groupStatus[pointer] != 1) {
						this.request(pointer, 2);
					}
					if (this.groupStatus[pointer] == 1) {
						node.unlink();
					} else {
						completed = false;
					}
				}
				while (this.group < this.masterIndex.groupSizes.length) {
					if (this.masterIndex.groupSizes[this.group] == 0) {
						this.group++;
					} else {
						if (this.standardRequester.isPrefetchRequestQueueFull()) {
							completed = false;
							break;
						}
						if (this.groupStatus[this.group] != 1) {
							this.request(this.group, 2);
						}
						if (this.groupStatus[this.group] != 1) {
							node2 = new Node();
							node2.key = this.group;
							completed = false;
							this.generalRequests.addLast(node2);
						}
						this.group++;
					}
				}
				if (completed) {
					this.group = 0;
					this.prefetchAll = false;
				}
			} else {
				this.generalRequests = null;
			}
		}
		if (!this.discardOrphans || Utils.currentTimeMillis() < this.cleanupCycle) {
			return;
		}
		for (@Pc(322) Js5WorkerRequestMessage resourceRequest = (Js5WorkerRequestMessage) this.pendingRequests.first(); resourceRequest != null; resourceRequest = (Js5WorkerRequestMessage) this.pendingRequests.next()) {
			if (!resourceRequest.waiting) {
				if (resourceRequest.isCompleted) {
					if (!resourceRequest.highPriority) {
						throw new RuntimeException();
					}
					resourceRequest.unlink();
				} else {
					resourceRequest.isCompleted = true;
				}
			}
		}
		this.cleanupCycle = Utils.currentTimeMillis() + 1000L;
	}

	@OriginalMember(owner = "client!bja", name = "e", descriptor = "(I)I")
	public int getTotalGroups() {
		return this.masterIndex == null ? 0 : this.masterIndex.size;
	}

	@OriginalMember(owner = "client!bja", name = "a", descriptor = "(IB)[B")
	@Override
	public byte[] fetchgroup(@OriginalArg(0) int groupId) {
		@Pc(17) Js5WorkerRequestMessage request = this.request(groupId, 0);
		if (request == null) {
			return null;
		} else {
			@Pc(25) byte[] data = request.getData();
			request.unlink();
			return data;
		}
	}

	@OriginalMember(owner = "client!bja", name = "f", descriptor = "(I)V")
	public void prefetchAll() {
		if (this.cacheDataFile != null) {
			this.prefetchAll = true;
			if (this.generalRequests == null) {
				this.generalRequests = new Deque();
			}
		}
	}

	@OriginalMember(owner = "client!bja", name = "a", descriptor = "(BI)V")
	@Override
	public void requestGroup(@OriginalArg(1) int groupId) {
		if (this.cacheDataFile == null) {
			return;
		}
		for (@Pc(22) Node existing = this.prefetchEntries.first(); existing != null; existing = this.prefetchEntries.next()) {
			if ((long) groupId == existing.key) {
				return;
			}
		}
		@Pc(42) Node request = new Node();
		request.key = groupId;
		this.prefetchEntries.addLast(request);
	}

	@OriginalMember(owner = "client!bja", name = "h", descriptor = "(I)I")
	public int getIndexPercentageComplete() {
		if (this.index() == null) {
			return this.masterIndexRequest == null ? 0 : this.masterIndexRequest.completePercentage();
		} else {
			return 100;
		}
	}

	@OriginalMember(owner = "client!bja", name = "a", descriptor = "(II)I")
	@Override
	public int completePercentage(@OriginalArg(0) int groupId) {
		@Pc(11) Js5WorkerRequestMessage request = (Js5WorkerRequestMessage) this.pendingRequests.get((long) groupId);
		return request == null ? 0 : request.completePercentage();
	}

	@OriginalMember(owner = "client!bja", name = "a", descriptor = "(IBI)Lclient!ffa;")
	private Js5WorkerRequestMessage request(@OriginalArg(0) int groupId, @OriginalArg(2) int actionType) {
		@Pc(19) Js5WorkerRequestMessage resourceRequest = (Js5WorkerRequestMessage) this.pendingRequests.get((long) groupId);
		if (resourceRequest != null && actionType == 0 && !resourceRequest.highPriority && resourceRequest.waiting) {
			resourceRequest.unlink();
			resourceRequest = null;
		}
		if (resourceRequest == null) {
			if (actionType == 0) {
				if (this.cacheDataFile == null || this.groupStatus[groupId] == -1) {
					if (this.standardRequester.isUrgentRequestQueueFull()) {
						return null;
					}
					resourceRequest = this.standardRequester.request((byte) 2, true, groupId, this.indexId);
				} else {
					resourceRequest = this.localRequester.createPriorityRequest(this.cacheDataFile, groupId);
				}
			} else if (actionType == 1) {
				if (this.cacheDataFile == null) {
					throw new RuntimeException();
				}
				resourceRequest = this.localRequester.createNormalRequest(this.cacheDataFile, groupId);
			} else if (actionType == 2) {
				if (this.cacheDataFile == null) {
					throw new RuntimeException();
				}
				if (this.groupStatus[groupId] != -1) {
					throw new RuntimeException();
				}
				if (this.standardRequester.isPrefetchRequestQueueFull()) {
					return null;
				}
				resourceRequest = this.standardRequester.request((byte) 2, false, groupId, this.indexId);
			} else {
				throw new RuntimeException();
			}
			this.pendingRequests.put((long) groupId, resourceRequest);
		}
		if (resourceRequest.waiting) {
			return null;
		}
		@Pc(157) byte[] data = resourceRequest.getData();
		@Pc(184) int checksum;
		@Pc(215) byte[] expectedHash;
		@Pc(226) byte[] actualHash;
		@Pc(228) int i;
		@Pc(288) Js5NetQueueRequest js5WorkerRequestMessage;
		if (!(resourceRequest instanceof Js5ResourceRequest)) {
			try {
				if (data == null || data.length <= 2) {
					throw new RuntimeException();
				}
				RemoteFileWorker.CRC32.reset();
				RemoteFileWorker.CRC32.update(data, 0, data.length - 2);
				checksum = (int) RemoteFileWorker.CRC32.getValue();
				if (checksum != this.masterIndex.groupChecksums[groupId]) {
					throw new RuntimeException();
				}
				if (this.masterIndex.groupHashes != null && this.masterIndex.groupHashes[groupId] != null) {
					expectedHash = this.masterIndex.groupHashes[groupId];
					actualHash = Whirlpool.digest(0, data, data.length - 2);
					for (i = 0; i < 64; i++) {
						if (actualHash[i] != expectedHash[i]) {
							throw new RuntimeException();
						}
					}
				}
				this.standardRequester.errors = 0;
				this.standardRequester.response = 0;
			} catch (@Pc(261) RuntimeException ex) {
				this.standardRequester.rekey();
				resourceRequest.unlink();
				if (resourceRequest.highPriority && !this.standardRequester.isUrgentRequestQueueFull()) {
					js5WorkerRequestMessage = this.standardRequester.request((byte) 2, true, groupId, this.indexId);
					this.pendingRequests.put((long) groupId, js5WorkerRequestMessage);
				}
				return null;
			}
			data[data.length - 2] = (byte) (this.masterIndex.groupVersions[groupId] >>> 8);
			data[data.length - 1] = (byte) this.masterIndex.groupVersions[groupId];
			if (this.cacheDataFile != null) {
				this.localRequester.createStoreRequest(data, this.cacheDataFile, groupId);
				if (this.groupStatus[groupId] != 1) {
					this.verifiedGroups++;
					this.groupStatus[groupId] = 1;
				}
			}
			if (!resourceRequest.highPriority) {
				resourceRequest.unlink();
			}
			return resourceRequest;
		}
		try {
			if (data == null || data.length <= 2) {
				throw new RuntimeException();
			}
			RemoteFileWorker.CRC32.reset();
			RemoteFileWorker.CRC32.update(data, 0, data.length - 2);
			checksum = (int) RemoteFileWorker.CRC32.getValue();
			if (this.masterIndex.groupChecksums[groupId] != checksum) {
				throw new RuntimeException();
			}
			if (this.masterIndex.groupHashes != null && this.masterIndex.groupHashes[groupId] != null) {
				expectedHash = this.masterIndex.groupHashes[groupId];
				actualHash = Whirlpool.digest(0, data, data.length - 2);
				for (i = 0; i < 64; i++) {
					if (expectedHash[i] != actualHash[i]) {
						throw new RuntimeException();
					}
				}
			}
			@Pc(475) int groupVersion = ((data[data.length - 2] & 0xFF) << 8) + (data[data.length - 1] & 0xFF);
			if ((this.masterIndex.groupVersions[groupId] & 0xFFFF) != groupVersion) {
				throw new RuntimeException();
			}
			if (this.groupStatus[groupId] != 1) {
				this.verifiedGroups++;
				this.groupStatus[groupId] = 1;
			}
			if (!resourceRequest.highPriority) {
				resourceRequest.unlink();
			}
			return resourceRequest;
		} catch (@Pc(514) Exception ex2) {
			this.groupStatus[groupId] = -1;
			resourceRequest.unlink();
			if (resourceRequest.highPriority && !this.standardRequester.isUrgentRequestQueueFull()) {
				js5WorkerRequestMessage = this.standardRequester.request((byte) 2, true, groupId, this.indexId);
				this.pendingRequests.put((long) groupId, js5WorkerRequestMessage);
			}
			return null;
		}
	}

	@OriginalMember(owner = "client!bja", name = "i", descriptor = "(I)I")
	public int getVerifiedGroups() {
		return this.verifiedGroups;
	}

	@OriginalMember(owner = "client!bja", name = "a", descriptor = "(B)Lclient!vw;")
	@Override
	public Js5MasterIndex index() {
		if (this.masterIndex != null) {
			return this.masterIndex;
		}
		if (this.masterIndexRequest == null) {
			if (this.standardRequester.isUrgentRequestQueueFull()) {
				return null;
			}
			this.masterIndexRequest = this.standardRequester.request((byte) 0, true, this.indexId, 255);
		}
		if (this.masterIndexRequest.waiting) {
			return null;
		}
		@Pc(48) byte[] data = this.masterIndexRequest.getData();
		if (this.masterIndexRequest instanceof Js5ResourceRequest) {
			try {
				if (data == null) {
					throw new RuntimeException();
				}
				this.masterIndex = new Js5MasterIndex(data, this.tableCrc, this.tableDigest);
				if (this.masterIndex.revision != this.tableRevision) {
					throw new RuntimeException();
				}
			} catch (@Pc(83) RuntimeException ignored) {
				this.masterIndex = null;
				if (this.standardRequester.isUrgentRequestQueueFull()) {
					this.masterIndexRequest = null;
				} else {
					this.masterIndexRequest = this.standardRequester.request((byte) 0, true, this.indexId, 255);
				}
				return null;
			}
		} else {
			try {
				if (data == null) {
					throw new RuntimeException();
				}
				this.masterIndex = new Js5MasterIndex(data, this.tableCrc, this.tableDigest);
			} catch (@Pc(129) RuntimeException ex2) {
				this.standardRequester.rekey();
				this.masterIndex = null;
				if (this.standardRequester.isUrgentRequestQueueFull()) {
					this.masterIndexRequest = null;
				} else {
					this.masterIndexRequest = this.standardRequester.request((byte) 0, true, this.indexId, 255);
				}
				return null;
			}
			if (this.masterIndexFile != null) {
				this.localRequester.createStoreRequest(data, this.masterIndexFile, this.indexId);
			}
		}
		this.masterIndexRequest = null;
		if (this.cacheDataFile != null) {
			this.verifiedGroups = 0;
			this.groupStatus = new byte[this.masterIndex.capacity];
		}
		return this.masterIndex;
	}
	@OriginalMember(owner = "client!vp", name = "g", descriptor = "Ljava/util/zip/CRC32;")
	public static final CRC32 CRC32 = new CRC32();
}
