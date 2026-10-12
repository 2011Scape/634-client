package com.jagex.game.runetek5.js5

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.core.util.Arrays
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.js5.request.FileWorker
import com.jagex.game.runetek5.ExplicitRuntimeException
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.ByteArrayWrapper
import util.Locale
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import util.lowercase

@OriginalClass("client!in")
class Js5ResourceProvider {

	@OriginalMember(owner = "client!in", name = "E", descriptor = "[Ljava/lang/Object;")
	private var packed: Array<Any?>? = null

	@OriginalMember(owner = "client!in", name = "M", descriptor = "[[Ljava/lang/Object;")
	private var archiveFiles: Array<Array<Any?>?>? = null

	@OriginalMember(owner = "client!in", name = "d", descriptor = "Lclient!vw;")
	private var masterIndex: Js5MasterIndex? = null

	@OriginalMember(owner = "client!in", name = "H", descriptor = "Lclient!ci;")
	private val fileWorker: FileWorker?

	@OriginalMember(owner = "client!in", name = "D", descriptor = "Z")
	private val directMemoryMode: Boolean

	@OriginalMember(owner = "client!in", name = "i", descriptor = "I")
	@JvmField
	var loadType: Int

	@OriginalMember(owner = "client!in", name = "<init>", descriptor = "(Lclient!ci;ZI)V")
	constructor(@OriginalArg(0) resourceProvider: FileWorker?, @OriginalArg(1) directMemoryMode: Boolean, @OriginalArg(2) loadType: Int) {
		if (loadType < 0 || loadType > 2) {
			throw IllegalArgumentException("js5: Invalid value " + loadType + " supplied for discardunpacked")
		}
		this.fileWorker = resourceProvider
		this.directMemoryMode = directMemoryMode
		this.loadType = loadType
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(I)I")
	fun getIndexCrc(): Int {
		if (!this.ensureMasterIndexLoaded()) {
			throw IllegalStateException("")
		}
		return this.masterIndex!!.headerCrc
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(Ljava/lang/String;Ljava/lang/String;I)[B")
	fun getFile(@OriginalArg(0) groupName: String?, @OriginalArg(1) fileName: String?): ByteArray? {
		if (!this.ensureMasterIndexLoaded()) {
			return null
		}
		@Pc(12) val string3: String = groupName!!.lowercase(util.defaultLocale())
		@Pc(15) val string4: String = fileName!!.lowercase(util.defaultLocale())
		@Pc(24) val groupId: Int = this.masterIndex!!.archiveNames!!.find(Js5ResourceProvider.hashName(string3))
		if (this.isGroupValid(groupId)) {
			@Pc(52) val fileId: Int = this.masterIndex!!.namedFiles!![groupId]!!.find(Js5ResourceProvider.hashName(string4))
			return this.getFile(groupId, fileId)
		} else {
			return null
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(IB)Z")
	private fun isGroupValid(@OriginalArg(0) groupId: Int): Boolean {
		if (!this.ensureMasterIndexLoaded()) {
			return false
		} else if (groupId >= 0 && this.masterIndex!!.groupCapacities!!.size > groupId && this.masterIndex!!.groupCapacities!![groupId] != 0) {
			return true
		} else if (Js5ResourceProvider.THROW_ON_INVALID_ID) {
			throw IllegalArgumentException(groupId.toString())
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(III[I)[B")
	fun getFile(@OriginalArg(0) groupId: Int, @OriginalArg(1) fileId: Int, @OriginalArg(3) xteaKeys: IntArray?): ByteArray? {
		if (!this.isFileValid(fileId, groupId)) {
			return null
		}
		if (this.archiveFiles!![groupId] == null || this.archiveFiles!![groupId]!![fileId] == null) {
			@Pc(37) var unpacked: Boolean = this.unpackGroup(fileId, xteaKeys, groupId)
			if (!unpacked) {
				this.fetchGroup(groupId)
				unpacked = this.unpackGroup(fileId, xteaKeys, groupId)
				if (!unpacked) {
					return null
				}
			}
		}
		@Pc(64) val data: ByteArray? = ByteArrayWrapper.unwrap(false, this.archiveFiles!![groupId]!![fileId])
		if (this.loadType == 1) {
			this.archiveFiles!![groupId]!![fileId] = null
			if (this.masterIndex!!.groupCapacities!![groupId] == 1) {
				this.archiveFiles!![groupId] = null
			}
		} else if (this.loadType == 2) {
			this.archiveFiles!![groupId] = null
		}
		return data
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(IB[II)Z")
	private fun unpackGroup(@OriginalArg(0) fileId: Int, @OriginalArg(2) xteaKeys: IntArray?, @OriginalArg(3) groupId: Int): Boolean {
		if (!this.isGroupValid(groupId)) {
			return false
		} else if (this.packed!![groupId] == null) {
			return false
		} else {
			@Pc(25) val fileCount: Int = this.masterIndex!!.groupSizes!![groupId]
			@Pc(31) val fileIds: IntArray? = this.masterIndex!!.validFileIds!![groupId]
			if (this.archiveFiles!![groupId] == null) {
				this.archiveFiles!![groupId] = arrayOfNulls<Any>(this.masterIndex!!.groupCapacities!![groupId])
			}
			@Pc(51) val objects: Array<Any?> = this.archiveFiles!![groupId]!!
			@Pc(53) var fullyCached: Boolean = true
			@Pc(55) var i: Int = 0
			while (i < fileCount) {
				@Pc(61) var id: Int
				if (fileIds == null) {
					id = i
				} else {
					id = fileIds[i]
				}
				if (objects[id] == null) {
					fullyCached = false
					break
				}
				i++
			}
			if (fullyCached) {
				return true
			}
			@Pc(125) val containerData: ByteArray?
			if (xteaKeys == null || xteaKeys[0] == 0 && xteaKeys[1] == 0 && xteaKeys[2] == 0 && xteaKeys[3] == 0) {
				containerData = ByteArrayWrapper.unwrap(false, this.packed!![groupId])
			} else {
				containerData = ByteArrayWrapper.unwrap(true, this.packed!![groupId])
				@Pc(140) val packet: Packet = Packet(containerData)
				packet.decryptXtea(xteaKeys, packet.buffer!!.size)
			}
			@Pc(152) val groupData: ByteArray
			try {
				groupData = Js5Compression.decompress(containerData)
			} catch (@Pc(154) ex: RuntimeException) {
				throw ExplicitRuntimeException.wrap(ex, "T3 - " + (xteaKeys != null) + "," + groupId + "," + containerData!!.size + "," + Packet.crc32(containerData.size, containerData) + "," + Packet.crc32(containerData.size - 2, containerData) + "," + this.masterIndex!!.groupChecksums!![groupId] + "," + this.masterIndex!!.headerCrc)
			}
			if (this.directMemoryMode) {
				this.packed!![groupId] = null
			}
			@Pc(237) var local237: Int
			if (fileCount > 1) {
				@Pc(244) var blockCount: Int
				@Pc(257) var packet2: Packet
				@Pc(265) var local265: Int
				@Pc(269) var local269: Int
				@Pc(271) var local271: Int
				@Pc(325) var local325: Int
				@Pc(327) var local327: Int
				@Pc(331) var local331: Int
				@Pc(333) var local333: Int
				var tablePosition: Int
				if (this.loadType == 2) {
					tablePosition = groupData.size
					tablePosition--
					blockCount = groupData[tablePosition].toInt() and 0xFF
					tablePosition -= blockCount * 4 * fileCount
					packet2 = Packet(groupData)
					@Pc(442) var fileSize: Int = 0
					var matchedFileId: Int = 0
					packet2.currentPosition = tablePosition
					var block: Int = 0
					while (block < blockCount) {
						var chunkSize: Int = 0
						var fileIndex: Int = 0
						while (fileIndex < fileCount) {
							chunkSize += packet2.readInt()
							var candidateFileId: Int
							if (fileIds == null) {
								candidateFileId = fileIndex
							} else {
								candidateFileId = fileIds[fileIndex]
							}
							if (candidateFileId == fileId) {
								matchedFileId = candidateFileId
								fileSize += chunkSize
							}
							fileIndex++
						}
						block++
					}
					if (fileSize == 0) {
						return true
					}
					@Pc(508) val fileData: ByteArray = ByteArray(fileSize)
					packet2.currentPosition = tablePosition
					var writeOffset: Int = 0
					var dataOffset: Int = 0
					block = 0
					while (block < blockCount) {
						var chunkSize: Int = 0
						var fileIndex: Int = 0
						while (fileIndex < fileCount) {
							chunkSize += packet2.readInt()
							@Pc(537) var id: Int
							if (fileIds == null) {
								id = fileIndex
							} else {
								id = fileIds[fileIndex]
							}
							if (id == fileId) {
								Arrays.copy(groupData, dataOffset, fileData, writeOffset, chunkSize)
								writeOffset += chunkSize
							}
							dataOffset += chunkSize
							fileIndex++
						}
						block++
					}
					objects[matchedFileId] = fileData
				} else {
					tablePosition = groupData.size
					@Pc(240) val lastByteIndex: Int = tablePosition - 1
					blockCount = groupData[lastByteIndex].toInt() and 0xFF
					@Pc(252) val sizeTableOffset: Int = lastByteIndex - fileCount * 4 * blockCount
					packet2 = Packet(groupData)
					@Pc(260) val fileSizes: IntArray = IntArray(fileCount)
					packet2.currentPosition = sizeTableOffset
					var block: Int = 0
					while (block < blockCount) {
						var chunkSize: Int = 0
						var fileIndex: Int = 0
						while (fileIndex < fileCount) {
							chunkSize += packet2.readInt()
							fileSizes[fileIndex] += chunkSize
							fileIndex++
						}
						block++
					}
					@Pc(297) val fileBuffers: Array<ByteArray?> = arrayOfNulls<ByteArray>(fileCount)
					var fileIndex: Int = 0
					while (fileIndex < fileCount) {
						fileBuffers[fileIndex] = ByteArray(fileSizes[fileIndex])
						fileSizes[fileIndex] = 0
						fileIndex++
					}
					packet2.currentPosition = sizeTableOffset
					var dataOffset: Int = 0
					block = 0
					while (block < blockCount) {
						var chunkSize: Int = 0
						fileIndex = 0
						while (fileIndex < fileCount) {
							chunkSize += packet2.readInt()
							Arrays.copy(groupData, dataOffset, fileBuffers[fileIndex], fileSizes[fileIndex], chunkSize)
							dataOffset += chunkSize
							fileSizes[fileIndex] += chunkSize
							fileIndex++
						}
						block++
					}
					var file: Int = 0
					while (file < fileCount) {
						var resolvedFileId: Int
						if (fileIds == null) {
							resolvedFileId = file
						} else {
							resolvedFileId = fileIds[file]
						}
						if (this.loadType == 0) {
							objects[resolvedFileId] = ByteArrayWrapper.wrap(fileBuffers[file])
						} else {
							objects[resolvedFileId] = fileBuffers[file]
						}
						file++
					}
				}
			} else {
				var singleFileId: Int
				if (fileIds == null) {
					singleFileId = 0
				} else {
					singleFileId = fileIds[0]
				}
				if (this.loadType == 0) {
					objects[singleFileId] = ByteArrayWrapper.wrap(groupData)
				} else {
					objects[singleFileId] = groupData
				}
			}
			return true
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(II)[I")
	fun getFileIds(@OriginalArg(0) groupId: Int): IntArray? {
		if (!this.isGroupValid(groupId)) {
			return null
		}
		@Pc(24) var fileIds: IntArray? = this.masterIndex!!.validFileIds!![groupId]
		if (fileIds == null) {
			fileIds = IntArray(this.masterIndex!!.groupSizes!![groupId])
			@Pc(35) var i: Int = 0
			while (i < fileIds.size) {
				fileIds[i] = i++
			}
		}
		return fileIds
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(Ljava/lang/String;I)I")
	fun getGroupCompletePercentage(@OriginalArg(0) groupName: String?): Int {
		if (this.ensureMasterIndexLoaded()) {
			@Pc(12) val string2: String = groupName!!.lowercase(util.defaultLocale())
			@Pc(28) val groupId: Int = this.masterIndex!!.archiveNames!!.find(Js5ResourceProvider.hashName(string2))
			return this.getGroupCompletePercentage(groupId)
		} else {
			return 0
		}
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(B)I")
	fun getCompletePercentage(): Int {
		if (!this.ensureMasterIndexLoaded()) {
			return 0
		}
		@Pc(11) var total: Int = 0
		@Pc(13) var progress: Int = 0
		@Pc(15) var i: Int = 0
		while (i < this.packed!!.size) {
			if (this.masterIndex!!.groupSizes!![i] > 0) {
				progress += this.getGroupCompletePercentage(i)
				total += 100
			}
			i++
		}
		if (total == 0) {
			return 100
		} else {
			return progress * 100 / total
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(Z)Z")
	private fun ensureMasterIndexLoaded(): Boolean {
		if (this.masterIndex == null) {
			this.masterIndex = this.fileWorker!!.index()
			if (this.masterIndex == null) {
				return false
			}
			this.packed = arrayOfNulls<Any>(this.masterIndex!!.capacity)
			this.archiveFiles = arrayOfNulls<Array<Any?>>(this.masterIndex!!.capacity)
		}
		return true
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(ILjava/lang/String;)Z")
	fun groupExists(@OriginalArg(1) groupName: String?): Boolean {
		if (this.ensureMasterIndexLoaded()) {
			@Pc(17) val string2: String = groupName!!.lowercase(util.defaultLocale())
			@Pc(26) val groupId: Int = this.masterIndex!!.archiveNames!!.find(Js5ResourceProvider.hashName(string2))
			return groupId >= 0
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!in", name = "c", descriptor = "(I)Z")
	fun fullyLoaded(): Boolean {
		if (!this.ensureMasterIndexLoaded()) {
			return false
		}
		@Pc(13) var loaded: Boolean = true
		@Pc(15) var i: Int = 0
		while (i < this.masterIndex!!.groupIds!!.size) {
			@Pc(23) val groupId: Int = this.masterIndex!!.groupIds!![i]
			if (this.packed!![groupId] == null) {
				this.fetchGroup(groupId)
				if (this.packed!![groupId] == null) {
					loaded = false
				}
			}
			i++
		}
		return loaded
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(BI)V")
	private fun requestGroup(@OriginalArg(1) groupId: Int) {
		this.fileWorker!!.requestGroup(groupId)
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(Ljava/lang/String;ILjava/lang/String;)Z")
	private fun isFileReady(@OriginalArg(0) groupName: String?, @OriginalArg(2) fileName: String?): Boolean {
		if (!this.ensureMasterIndexLoaded()) {
			return false
		}
		@Pc(12) val string3: String = groupName!!.lowercase(util.defaultLocale())
		@Pc(15) val string4: String = fileName!!.lowercase(util.defaultLocale())
		@Pc(26) val groupId: Int = this.masterIndex!!.archiveNames!!.find(Js5ResourceProvider.hashName(string3))
		if (this.isGroupValid(groupId)) {
			@Pc(53) val fileId: Int = this.masterIndex!!.namedFiles!![groupId]!!.find(Js5ResourceProvider.hashName(string4))
			return this.isFileReady(groupId, fileId)
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(IZZ)V")
	fun discardNames(@OriginalArg(2) discardGroupNames: Boolean) {
		if (!this.ensureMasterIndexLoaded()) {
			return
		}
		if (discardGroupNames) {
			this.masterIndex!!.nameHashes = null
			this.masterIndex!!.archiveNames = null
		}
		this.masterIndex!!.fileNameHashes = null
		this.masterIndex!!.namedFiles = null
	}

	@OriginalMember(owner = "client!in", name = "d", descriptor = "(I)V")
	fun discardPacked() {
		if (this.packed != null) {
			@Pc(17) var i: Int = 0
			while (i < this.packed!!.size) {
				this.packed!![i] = null
				i++
			}
		}
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(II)V")
	private fun fetchGroup(@OriginalArg(0) groupId: Int) {
		if (this.directMemoryMode) {
			this.packed!![groupId] = this.fileWorker!!.fetchgroup(groupId)
		} else {
			this.packed!![groupId] = ByteArrayWrapper.wrap(this.fileWorker!!.fetchgroup(groupId))
		}
	}

	@OriginalMember(owner = "client!in", name = "c", descriptor = "(II)I")
	fun getGroupCapacity(@OriginalArg(1) groupId: Int): Int {
		return if (this.isGroupValid(groupId)) this.masterIndex!!.groupCapacities!![groupId] else 0
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(BI)Z")
	fun isGroupReady(@OriginalArg(1) groupId: Int): Boolean {
		if (!this.isGroupValid(groupId)) {
			return false
		} else if (this.packed!![groupId] == null) {
			this.fetchGroup(groupId)
			return this.packed!![groupId] != null
		} else {
			return true
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(Ljava/lang/String;Z)V")
	fun requestGroup(@OriginalArg(0) groupName: String?) {
		if (this.ensureMasterIndexLoaded()) {
			@Pc(15) val string2: String = groupName!!.lowercase(util.defaultLocale())
			@Pc(24) val groupId: Int = this.masterIndex!!.archiveNames!!.find(Js5ResourceProvider.hashName(string2))
			this.requestGroup(groupId)
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(III)[B")
	fun getFile(@OriginalArg(1) groupId: Int, @OriginalArg(2) fileId: Int): ByteArray? {
		return this.getFile(groupId, fileId, null)
	}

	@OriginalMember(owner = "client!in", name = "d", descriptor = "(II)V")
	fun discardUnpacked(@OriginalArg(0) groupId: Int) {
		if (this.isGroupValid(groupId) && this.archiveFiles != null) {
			this.archiveFiles!![groupId] = null
		}
	}

	@OriginalMember(owner = "client!in", name = "c", descriptor = "(B)V")
	fun discardUnpacked() {
		if (this.archiveFiles != null) {
			@Pc(6) var i: Int = 0
			while (i < this.archiveFiles!!.size) {
				this.archiveFiles!![i] = null
				i++
			}
		}
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(ILjava/lang/String;)Z")
	fun isGroupReady(@OriginalArg(1) groupName: String?): Boolean {
		if (this.ensureMasterIndexLoaded()) {
			@Pc(12) val string2: String = groupName!!.lowercase(util.defaultLocale())
			@Pc(23) val groupId: Int = this.masterIndex!!.archiveNames!!.find(Js5ResourceProvider.hashName(string2))
			return this.isGroupReady(groupId)
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!in", name = "e", descriptor = "(I)I")
	fun getCapacity(): Int {
		return if (this.ensureMasterIndexLoaded()) this.masterIndex!!.groupCapacities!!.size else -1
	}

	@OriginalMember(owner = "client!in", name = "c", descriptor = "(BI)[B")
	fun getFile(@OriginalArg(1) id: Int): ByteArray? {
		if (!this.ensureMasterIndexLoaded()) {
			return null
		} else if (this.masterIndex!!.groupCapacities!!.size == 1) {
			return this.getFile(0, id)
		} else if (!this.isGroupValid(id)) {
			return null
		} else if (this.masterIndex!!.groupCapacities!![id] == 1) {
			return this.getFile(id, 0)
		} else {
			throw RuntimeException()
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(BLjava/lang/String;)Z")
	fun isFileReady(@OriginalArg(1) string: String?): Boolean {
		@Pc(10) val groupId: Int = this.getGroupId("")
		return if (groupId == -1) this.isFileReady(string, "") else this.isFileReady("", string)
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(Ljava/lang/String;I)I")
	fun getGroupId(@OriginalArg(0) groupName: String?): Int {
		if (this.ensureMasterIndexLoaded()) {
			@Pc(16) val string2: String = groupName!!.lowercase(util.defaultLocale())
			@Pc(25) val groupId: Int = this.masterIndex!!.archiveNames!!.find(Js5ResourceProvider.hashName(string2))
			return if (this.isGroupValid(groupId)) groupId else -1
		} else {
			return -1
		}
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(III)Z")
	private fun isFileValid(@OriginalArg(0) fileId: Int, @OriginalArg(2) groupId: Int): Boolean {
		if (!this.ensureMasterIndexLoaded()) {
			return false
		} else if (groupId >= 0 && fileId >= 0 && groupId < this.masterIndex!!.groupCapacities!!.size && fileId < this.masterIndex!!.groupCapacities!![groupId]) {
			return true
		} else if (Js5ResourceProvider.THROW_ON_INVALID_ID) {
			throw IllegalArgumentException(groupId.toString() + "," + fileId)
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!in", name = "e", descriptor = "(II)I")
	private fun getGroupCompletePercentage(@OriginalArg(1) groupId: Int): Int {
		if (this.isGroupValid(groupId)) {
			return if (this.packed!![groupId] == null) this.fileWorker!!.completePercentage(groupId) else 100
		} else {
			return 0
		}
	}

	@OriginalMember(owner = "client!in", name = "c", descriptor = "(III)Z")
	fun isFileReady(@OriginalArg(1) groupId: Int, @OriginalArg(2) fileId: Int): Boolean {
		if (!this.isFileValid(fileId, groupId)) {
			return false
		} else if (this.archiveFiles!![groupId] != null && this.archiveFiles!![groupId]!![fileId] != null) {
			return true
		} else if (this.packed!![groupId] == null) {
			this.fetchGroup(groupId)
			return this.packed!![groupId] != null
		} else {
			return true
		}
	}

	@OriginalMember(owner = "client!in", name = "a", descriptor = "(ZI)Z")
	fun isFileReady(@OriginalArg(1) id: Int): Boolean {
		if (!this.ensureMasterIndexLoaded()) {
			return false
		} else if (this.masterIndex!!.groupCapacities!!.size == 1) {
			return this.isFileReady(0, id)
		} else if (!this.isGroupValid(id)) {
			return false
		} else if (this.masterIndex!!.groupCapacities!![id] == 1) {
			return this.isFileReady(id, 0)
		} else {
			throw RuntimeException()
		}
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(Ljava/lang/String;Ljava/lang/String;I)Z")
	fun fileExists(@OriginalArg(0) fileName: String?, @OriginalArg(1) groupName: String?): Boolean {
		if (!this.ensureMasterIndexLoaded()) {
			return false
		}
		@Pc(12) val string3: String = groupName!!.lowercase(util.defaultLocale())
		@Pc(20) val string4: String = fileName!!.lowercase(util.defaultLocale())
		@Pc(29) val groupId: Int = this.masterIndex!!.archiveNames!!.find(Js5ResourceProvider.hashName(string3))
		if (groupId < 0) {
			return false
		} else {
			@Pc(47) val fileId: Int = this.masterIndex!!.namedFiles!![groupId]!!.find(Js5ResourceProvider.hashName(string4))
			return fileId >= 0
		}
	}

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(IB)I")
	fun getGroupId(@OriginalArg(0) nameHash: Int): Int {
		if (this.ensureMasterIndexLoaded()) {
			@Pc(16) val groupId: Int = this.masterIndex!!.archiveNames!!.find(nameHash)
			return if (this.isGroupValid(groupId)) groupId else -1
		} else {
			return -1
		}
	}

	companion object {
		@OriginalMember(owner = "client!vea", name = "b", descriptor = "(Ljava/lang/String;I)I")
		@JvmStatic
		fun hashName(@OriginalArg(0) string: String?): Int {
			@Pc(8) val length: Int = string!!.length
			@Pc(10) var hash: Int = 0
			@Pc(12) var i: Int = 0
			while (i < length) {
				hash = Utils.charToCp1252(string[i]).toInt() + (hash shl 5) - hash
				i++
			}
			return hash
		}

		@OriginalMember(owner = "client!vi", name = "g", descriptor = "Z")
		const val THROW_ON_INVALID_ID: Boolean = false
	}
}
