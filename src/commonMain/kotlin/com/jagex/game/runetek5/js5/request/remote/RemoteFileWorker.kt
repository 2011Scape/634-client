package com.jagex.game.runetek5.js5.request.remote

import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.Decompressor
import com.jagex.game.runetek5.js5.Js5MasterIndex
import com.jagex.game.runetek5.js5.request.FileWorker
import com.jagex.game.runetek5.js5.request.Js5ResourceRequest
import com.jagex.game.runetek5.js5.request.Js5WorkerRequestMessage
import com.jagex.game.runetek5.js5.request.local.LocalRequester
import com.jagex.game.runetek5.js5.Js5WorkerThread
import com.jagex.game.runetek5.js5.Whirlpool
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField
import util.zip.CRC32

@OriginalClass("client!bja")
class RemoteFileWorker : FileWorker {

	@OriginalMember(owner = "client!bja", name = "i", descriptor = "Lclient!vw;")
	private var masterIndex: Js5MasterIndex? = null

	@OriginalMember(owner = "client!bja", name = "r", descriptor = "[B")
	private var groupStatus: ByteArray? = null

	@OriginalMember(owner = "client!bja", name = "L", descriptor = "Z")
	private var prefetchAll: Boolean = false

	@OriginalMember(owner = "client!bja", name = "B", descriptor = "I")
	private var verifiedGroups: Int = 0

	@OriginalMember(owner = "client!bja", name = "A", descriptor = "Lclient!eq;")
	private val pendingRequests: IterableHashTable = IterableHashTable(16)

	@OriginalMember(owner = "client!bja", name = "N", descriptor = "I")
	private var group: Int = 0

	@OriginalMember(owner = "client!bja", name = "J", descriptor = "Lclient!uh;")
	private val prefetchEntries: Deque = Deque()

	@OriginalMember(owner = "client!bja", name = "O", descriptor = "J")
	private var cleanupCycle: Long = 0L

	@OriginalMember(owner = "client!bja", name = "q", descriptor = "I")
	private val indexId: Int

	@OriginalMember(owner = "client!bja", name = "F", descriptor = "Lclient!nw;")
	private val cacheDataFile: Decompressor?

	@OriginalMember(owner = "client!bja", name = "I", descriptor = "Z")
	private var verifyAll: Boolean = false

	@OriginalMember(owner = "client!bja", name = "M", descriptor = "Lclient!uh;")
	private var generalRequests: Deque? = null

	@OriginalMember(owner = "client!bja", name = "k", descriptor = "I")
	private val tableCrc: Int

	@OriginalMember(owner = "client!bja", name = "m", descriptor = "[B")
	private val tableDigest: ByteArray?

	@OriginalMember(owner = "client!bja", name = "s", descriptor = "I")
	private val tableRevision: Int

	@OriginalMember(owner = "client!bja", name = "E", descriptor = "Lclient!nw;")
	private val masterIndexFile: Decompressor?

	@OriginalMember(owner = "client!bja", name = "P", descriptor = "Z")
	private val discardOrphans: Boolean

	@OriginalMember(owner = "client!bja", name = "n", descriptor = "Lclient!tha;")
	private val standardRequester: Js5WorkerThread?

	@OriginalMember(owner = "client!bja", name = "u", descriptor = "Lclient!aca;")
	private val localRequester: LocalRequester?

	@OriginalMember(owner = "client!bja", name = "v", descriptor = "Lclient!ffa;")
	private var masterIndexRequest: Js5WorkerRequestMessage? = null

	@OriginalMember(owner = "client!bja", name = "<init>", descriptor = "(ILclient!nw;Lclient!nw;Lclient!tha;Lclient!aca;I[BIZ)V")
	constructor(@OriginalArg(0) archive: Int, @OriginalArg(1) fileSystem_Client: Decompressor?, @OriginalArg(2) fileSystem_Client2: Decompressor?, @OriginalArg(3) js5WorkerThread: Js5WorkerThread?, @OriginalArg(4) cachedResourceWorker: LocalRequester?, @OriginalArg(5) tableCrc: Int, @OriginalArg(6) tableDigest: ByteArray?, @OriginalArg(7) tableRevision: Int, @OriginalArg(8) discardOrphans: Boolean) : super() {
		this.indexId = archive
		this.cacheDataFile = fileSystem_Client
		if (this.cacheDataFile == null) {
			this.verifyAll = false
		} else {
			this.verifyAll = true
			this.generalRequests = Deque()
		}
		this.tableCrc = tableCrc
		this.tableDigest = tableDigest
		this.tableRevision = tableRevision
		this.masterIndexFile = fileSystem_Client2
		this.discardOrphans = discardOrphans
		this.standardRequester = js5WorkerThread
		this.localRequester = cachedResourceWorker
		if (this.masterIndexFile != null) {
			this.masterIndexRequest = this.localRequester!!.createPriorityRequest(this.masterIndexFile, this.indexId)
		}
	}

	@OriginalMember(owner = "client!bja", name = "a", descriptor = "(I)I")
	fun getTotalVerifiedGroups(): Int {
		if (this.masterIndex == null) {
			return 0
		} else if (this.verifyAll) {
			@Pc(29) val node: Node? = this.generalRequests!!.first()
			return if (node == null) 0 else node.key.toInt()
		} else {
			return this.masterIndex!!.size
		}
	}

	@OriginalMember(owner = "client!bja", name = "b", descriptor = "(I)V")
	fun processPrefetchQueue() {
		if (this.generalRequests == null || this.index() == null) {
			return
		}
		@Pc(24) var node: Node? = this.prefetchEntries.first()
		while (node != null) {
			@Pc(30) val pointer: Int = node.key.toInt()
			if (pointer < 0 || pointer >= this.masterIndex!!.capacity || this.masterIndex!!.groupSizes!![pointer] == 0) {
				node.unlink()
			} else {
				if (this.groupStatus!![pointer].toInt() == 0) {
					this.request(pointer, 1)
				}
				if (this.groupStatus!![pointer].toInt() == -1) {
					this.request(pointer, 2)
				}
				if (this.groupStatus!![pointer].toInt() == 1) {
					node.unlink()
				}
			}
			node = this.prefetchEntries.next()
		}
	}

	@OriginalMember(owner = "client!bja", name = "c", descriptor = "(I)V")
	fun loop() {
		if (this.generalRequests != null) {
			if (this.index() == null) {
				return
			}
			@Pc(18) var completed: Boolean
			@Pc(23) var node: Node?
			@Pc(29) var pointer: Int
			@Pc(114) var node2: Node
			if (this.verifyAll) {
				completed = true
				node = this.generalRequests!!.first()
				while (node != null) {
					pointer = node.key.toInt()
					if (this.groupStatus!![pointer].toInt() == 0) {
						this.request(pointer, 1)
					}
					if (this.groupStatus!![pointer].toInt() == 0) {
						completed = false
					} else {
						node.unlink()
					}
					node = this.generalRequests!!.next()
				}
				while (this.group < this.masterIndex!!.groupSizes!!.size) {
					if (this.masterIndex!!.groupSizes!![this.group] == 0) {
						this.group++
					} else {
						if (this.localRequester!!.requestCount >= 250) {
							completed = false
							break
						}
						if (this.groupStatus!![this.group].toInt() == 0) {
							this.request(this.group, 1)
						}
						if (this.groupStatus!![this.group].toInt() == 0) {
							node2 = Node()
							node2.key = this.group.toLong()
							this.generalRequests!!.addLast(node2)
							completed = false
						}
						this.group++
					}
				}
				if (completed) {
					this.group = 0
					this.verifyAll = false
				}
			} else if (this.prefetchAll) {
				completed = true
				node = this.generalRequests!!.first()
				while (node != null) {
					pointer = node.key.toInt()
					if (this.groupStatus!![pointer].toInt() != 1) {
						this.request(pointer, 2)
					}
					if (this.groupStatus!![pointer].toInt() == 1) {
						node.unlink()
					} else {
						completed = false
					}
					node = this.generalRequests!!.next()
				}
				while (this.group < this.masterIndex!!.groupSizes!!.size) {
					if (this.masterIndex!!.groupSizes!![this.group] == 0) {
						this.group++
					} else {
						if (this.standardRequester!!.isPrefetchRequestQueueFull()) {
							completed = false
							break
						}
						if (this.groupStatus!![this.group].toInt() != 1) {
							this.request(this.group, 2)
						}
						if (this.groupStatus!![this.group].toInt() != 1) {
							node2 = Node()
							node2.key = this.group.toLong()
							completed = false
							this.generalRequests!!.addLast(node2)
						}
						this.group++
					}
				}
				if (completed) {
					this.group = 0
					this.prefetchAll = false
				}
			} else {
				this.generalRequests = null
			}
		}
		if (!this.discardOrphans || Utils.currentTimeMillis() < this.cleanupCycle) {
			return
		}
		@Pc(322) var resourceRequest: Js5WorkerRequestMessage? = this.pendingRequests.first() as Js5WorkerRequestMessage?
		while (resourceRequest != null) {
			if (!resourceRequest.waiting) {
				if (resourceRequest.isCompleted) {
					if (!resourceRequest.highPriority) {
						throw RuntimeException()
					}
					resourceRequest.unlink()
				} else {
					resourceRequest.isCompleted = true
				}
			}
			resourceRequest = this.pendingRequests.next() as Js5WorkerRequestMessage?
		}
		this.cleanupCycle = Utils.currentTimeMillis() + 1000L
	}

	@OriginalMember(owner = "client!bja", name = "e", descriptor = "(I)I")
	fun getTotalGroups(): Int {
		return if (this.masterIndex == null) 0 else this.masterIndex!!.size
	}

	@OriginalMember(owner = "client!bja", name = "a", descriptor = "(IB)[B")
	override fun fetchgroup(@OriginalArg(0) groupId: Int): ByteArray? {
		@Pc(17) val request: Js5WorkerRequestMessage? = this.request(groupId, 0)
		if (request == null) {
			return null
		} else {
			@Pc(25) val data: ByteArray? = request.getData()
			request.unlink()
			return data
		}
	}

	@OriginalMember(owner = "client!bja", name = "f", descriptor = "(I)V")
	fun prefetchAll() {
		if (this.cacheDataFile != null) {
			this.prefetchAll = true
			if (this.generalRequests == null) {
				this.generalRequests = Deque()
			}
		}
	}

	@OriginalMember(owner = "client!bja", name = "a", descriptor = "(BI)V")
	override fun requestGroup(@OriginalArg(1) groupId: Int) {
		if (this.cacheDataFile == null) {
			return
		}
		@Pc(22) var existing: Node? = this.prefetchEntries.first()
		while (existing != null) {
			if (groupId.toLong() == existing.key) {
				return
			}
			existing = this.prefetchEntries.next()
		}
		@Pc(42) val request: Node = Node()
		request.key = groupId.toLong()
		this.prefetchEntries.addLast(request)
	}

	@OriginalMember(owner = "client!bja", name = "h", descriptor = "(I)I")
	fun getIndexPercentageComplete(): Int {
		if (this.index() == null) {
			return if (this.masterIndexRequest == null) 0 else this.masterIndexRequest!!.completePercentage()
		} else {
			return 100
		}
	}

	@OriginalMember(owner = "client!bja", name = "a", descriptor = "(II)I")
	override fun completePercentage(@OriginalArg(0) groupId: Int): Int {
		@Pc(11) val request: Js5WorkerRequestMessage? = this.pendingRequests.get(groupId.toLong()) as Js5WorkerRequestMessage?
		return if (request == null) 0 else request.completePercentage()
	}

	@OriginalMember(owner = "client!bja", name = "a", descriptor = "(IBI)Lclient!ffa;")
	private fun request(@OriginalArg(0) groupId: Int, @OriginalArg(2) actionType: Int): Js5WorkerRequestMessage? {
		@Pc(19) var resourceRequest: Js5WorkerRequestMessage? = this.pendingRequests.get(groupId.toLong()) as Js5WorkerRequestMessage?
		if (resourceRequest != null && actionType == 0 && !resourceRequest.highPriority && resourceRequest.waiting) {
			resourceRequest.unlink()
			resourceRequest = null
		}
		if (resourceRequest == null) {
			if (actionType == 0) {
				if (this.cacheDataFile == null || this.groupStatus!![groupId].toInt() == -1) {
					if (this.standardRequester!!.isUrgentRequestQueueFull()) {
						return null
					}
					resourceRequest = this.standardRequester.request(2.toByte(), true, groupId, this.indexId)
				} else {
					resourceRequest = this.localRequester!!.createPriorityRequest(this.cacheDataFile, groupId)
				}
			} else if (actionType == 1) {
				if (this.cacheDataFile == null) {
					throw RuntimeException()
				}
				resourceRequest = this.localRequester!!.createNormalRequest(this.cacheDataFile, groupId)
			} else if (actionType == 2) {
				if (this.cacheDataFile == null) {
					throw RuntimeException()
				}
				if (this.groupStatus!![groupId].toInt() != -1) {
					throw RuntimeException()
				}
				if (this.standardRequester!!.isPrefetchRequestQueueFull()) {
					return null
				}
				resourceRequest = this.standardRequester.request(2.toByte(), false, groupId, this.indexId)
			} else {
				throw RuntimeException()
			}
			this.pendingRequests.put(groupId.toLong(), resourceRequest)
		}
		if (resourceRequest!!.waiting) {
			return null
		}
		@Pc(157) val data: ByteArray? = resourceRequest.getData()
		@Pc(184) var checksum: Int
		@Pc(215) var expectedHash: ByteArray?
		@Pc(226) var actualHash: ByteArray?
		@Pc(228) var i: Int
		@Pc(288) var js5WorkerRequestMessage: Js5NetQueueRequest?
		if (resourceRequest !is Js5ResourceRequest) {
			try {
				if (data == null || data.size <= 2) {
					throw RuntimeException()
				}
				RemoteFileWorker.CRC32.reset()
				RemoteFileWorker.CRC32.update(data, 0, data.size - 2)
				checksum = RemoteFileWorker.CRC32.getValue().toInt()
				if (checksum != this.masterIndex!!.groupChecksums!![groupId]) {
					throw RuntimeException()
				}
				if (this.masterIndex!!.groupHashes != null && this.masterIndex!!.groupHashes!![groupId] != null) {
					expectedHash = this.masterIndex!!.groupHashes!![groupId]
					actualHash = Whirlpool.digest(0, data, data.size - 2)
					i = 0
					while (i < 64) {
						if (actualHash!![i] != expectedHash!![i]) {
							throw RuntimeException()
						}
						i++
					}
				}
				this.standardRequester!!.errors = 0
				this.standardRequester.response = 0
			} catch (@Pc(261) ex: RuntimeException) {
				this.standardRequester!!.rekey()
				resourceRequest.unlink()
				if (resourceRequest.highPriority && !this.standardRequester.isUrgentRequestQueueFull()) {
					js5WorkerRequestMessage = this.standardRequester.request(2.toByte(), true, groupId, this.indexId)
					this.pendingRequests.put(groupId.toLong(), js5WorkerRequestMessage)
				}
				return null
			}
			data!![data.size - 2] = (this.masterIndex!!.groupVersions!![groupId] ushr 8).toByte()
			data[data.size - 1] = this.masterIndex!!.groupVersions!![groupId].toByte()
			if (this.cacheDataFile != null) {
				this.localRequester!!.createStoreRequest(data, this.cacheDataFile, groupId)
				if (this.groupStatus!![groupId].toInt() != 1) {
					this.verifiedGroups++
					this.groupStatus!![groupId] = 1
				}
			}
			if (!resourceRequest.highPriority) {
				resourceRequest.unlink()
			}
			return resourceRequest
		}
		try {
			if (data == null || data.size <= 2) {
				throw RuntimeException()
			}
			RemoteFileWorker.CRC32.reset()
			RemoteFileWorker.CRC32.update(data, 0, data.size - 2)
			checksum = RemoteFileWorker.CRC32.getValue().toInt()
			if (this.masterIndex!!.groupChecksums!![groupId] != checksum) {
				throw RuntimeException()
			}
			if (this.masterIndex!!.groupHashes != null && this.masterIndex!!.groupHashes!![groupId] != null) {
				expectedHash = this.masterIndex!!.groupHashes!![groupId]
				actualHash = Whirlpool.digest(0, data, data.size - 2)
				i = 0
				while (i < 64) {
					if (expectedHash!![i] != actualHash!![i]) {
						throw RuntimeException()
					}
					i++
				}
			}
			@Pc(475) val groupVersion: Int = ((data[data.size - 2].toInt() and 0xFF) shl 8) + (data[data.size - 1].toInt() and 0xFF)
			if ((this.masterIndex!!.groupVersions!![groupId] and 0xFFFF) != groupVersion) {
				throw RuntimeException()
			}
			if (this.groupStatus!![groupId].toInt() != 1) {
				this.verifiedGroups++
				this.groupStatus!![groupId] = 1
			}
			if (!resourceRequest.highPriority) {
				resourceRequest.unlink()
			}
			return resourceRequest
		} catch (@Pc(514) ex2: Exception) {
			this.groupStatus!![groupId] = -1
			resourceRequest.unlink()
			if (resourceRequest.highPriority && !this.standardRequester!!.isUrgentRequestQueueFull()) {
				js5WorkerRequestMessage = this.standardRequester.request(2.toByte(), true, groupId, this.indexId)
				this.pendingRequests.put(groupId.toLong(), js5WorkerRequestMessage)
			}
			return null
		}
	}

	@OriginalMember(owner = "client!bja", name = "i", descriptor = "(I)I")
	fun getVerifiedGroups(): Int {
		return this.verifiedGroups
	}

	@OriginalMember(owner = "client!bja", name = "a", descriptor = "(B)Lclient!vw;")
	override fun index(): Js5MasterIndex? {
		if (this.masterIndex != null) {
			return this.masterIndex
		}
		if (this.masterIndexRequest == null) {
			if (this.standardRequester!!.isUrgentRequestQueueFull()) {
				return null
			}
			this.masterIndexRequest = this.standardRequester.request(0.toByte(), true, this.indexId, 255)
		}
		if (this.masterIndexRequest!!.waiting) {
			return null
		}
		@Pc(48) val data: ByteArray? = this.masterIndexRequest!!.getData()
		if (this.masterIndexRequest is Js5ResourceRequest) {
			try {
				if (data == null) {
					throw RuntimeException()
				}
				this.masterIndex = Js5MasterIndex(data, this.tableCrc, this.tableDigest)
				if (this.masterIndex!!.revision != this.tableRevision) {
					throw RuntimeException()
				}
			} catch (@Pc(83) ignored: RuntimeException) {
				this.masterIndex = null
				if (this.standardRequester!!.isUrgentRequestQueueFull()) {
					this.masterIndexRequest = null
				} else {
					this.masterIndexRequest = this.standardRequester.request(0.toByte(), true, this.indexId, 255)
				}
				return null
			}
		} else {
			try {
				if (data == null) {
					throw RuntimeException()
				}
				this.masterIndex = Js5MasterIndex(data, this.tableCrc, this.tableDigest)
			} catch (@Pc(129) ex2: RuntimeException) {
				this.standardRequester!!.rekey()
				this.masterIndex = null
				if (this.standardRequester.isUrgentRequestQueueFull()) {
					this.masterIndexRequest = null
				} else {
					this.masterIndexRequest = this.standardRequester.request(0.toByte(), true, this.indexId, 255)
				}
				return null
			}
			if (this.masterIndexFile != null) {
				this.localRequester!!.createStoreRequest(data, this.masterIndexFile, this.indexId)
			}
		}
		this.masterIndexRequest = null
		if (this.cacheDataFile != null) {
			this.verifiedGroups = 0
			this.groupStatus = ByteArray(this.masterIndex!!.capacity)
		}
		return this.masterIndex
	}

	companion object {
		@OriginalMember(owner = "client!vp", name = "g", descriptor = "Ljava/util/zip/CRC32;")
		@JvmField
		val CRC32: CRC32 = CRC32()
	}
}
