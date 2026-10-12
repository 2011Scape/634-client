package com.jagex.game.runetek5.js5

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!vw")
class Js5MasterIndex {

	@OriginalMember(owner = "client!vw", name = "c", descriptor = "[[I")
	@JvmField
	var validFileIds: Array<IntArray?>? = null

	@OriginalMember(owner = "client!vw", name = "d", descriptor = "[I")
	@JvmField
	var groupVersions: IntArray? = null

	@OriginalMember(owner = "client!vw", name = "e", descriptor = "Lclient!cj;")
	@JvmField
	var archiveNames: NamedFileReference? = null

	@OriginalMember(owner = "client!vw", name = "f", descriptor = "[I")
	@JvmField
	var groupCapacities: IntArray? = null

	@OriginalMember(owner = "client!vw", name = "g", descriptor = "[I")
	@JvmField
	var groupSizes: IntArray? = null

	@OriginalMember(owner = "client!vw", name = "i", descriptor = "I")
	@JvmField
	var size: Int = 0

	@OriginalMember(owner = "client!vw", name = "j", descriptor = "[Lclient!cj;")
	@JvmField
	var namedFiles: Array<NamedFileReference?>? = null

	@OriginalMember(owner = "client!vw", name = "k", descriptor = "[I")
	@JvmField
	var groupChecksums: IntArray? = null

	@OriginalMember(owner = "client!vw", name = "l", descriptor = "[[B")
	@JvmField
	var groupHashes: Array<ByteArray?>? = null

	@OriginalMember(owner = "client!vw", name = "n", descriptor = "I")
	@JvmField
	var revision: Int = 0

	@OriginalMember(owner = "client!vw", name = "o", descriptor = "[I")
	@JvmField
	var nameHashes: IntArray? = null

	@OriginalMember(owner = "client!vw", name = "p", descriptor = "I")
	@JvmField
	var capacity: Int = 0

	@OriginalMember(owner = "client!vw", name = "q", descriptor = "[[I")
	@JvmField
	var fileNameHashes: Array<IntArray?>? = null

	@OriginalMember(owner = "client!vw", name = "t", descriptor = "[I")
	@JvmField
	var groupIds: IntArray? = null

	@OriginalMember(owner = "client!vw", name = "a", descriptor = "I")
	@JvmField
	val headerCrc: Int

	@OriginalMember(owner = "client!vw", name = "v", descriptor = "[B")
	private var headerWhirlpool: ByteArray? = null

	@OriginalMember(owner = "client!vw", name = "<init>", descriptor = "([BI[B)V")
	constructor(@OriginalArg(0) tablePayload: ByteArray?, @OriginalArg(1) expectedCrc: Int, @OriginalArg(2) expectedWhirlpool: ByteArray?) {
		this.headerCrc = Packet.crc32(tablePayload!!.size, tablePayload)
		if (expectedCrc != this.headerCrc) {
			throw RuntimeException()
		}
		if (expectedWhirlpool != null) {
			if (expectedWhirlpool.size != 64) {
				throw RuntimeException()
			}
			this.headerWhirlpool = Whirlpool.digest(0, tablePayload, tablePayload.size)
			@Pc(40) var i: Int = 0
			while (i < 64) {
				if (expectedWhirlpool[i] != this.headerWhirlpool!![i]) {
					throw RuntimeException()
				}
				i++
			}
		}
		this.decode(tablePayload)
	}

	@OriginalMember(owner = "client!vw", name = "a", descriptor = "(B[B)V")
	private fun decode(@OriginalArg(1) tablePayload: ByteArray?) {
		@Pc(12) val packet: Packet = Packet(Js5Compression.decompress(tablePayload))
		@Pc(16) val protocol: Int = packet.readUByte()
		if (protocol < 5 || protocol > 6) {
			throw RuntimeException()
		}
		if (protocol < 6) {
			this.revision = 0
		} else {
			this.revision = packet.readInt()
		}
		@Pc(45) val flags: Int = packet.readUByte()
		@Pc(56) val named: Boolean = (flags and 0x1) != 0
		@Pc(64) val hasWhirlpool: Boolean = (flags and 0x2) != 0
		this.size = packet.readUShort()
		@Pc(71) var offset: Int = 0
		this.groupIds = IntArray(this.size)
		@Pc(78) var maxGroupId: Int = -1
		@Pc(80) var i0: Int = 0
		while (i0 < this.size) {
			offset += packet.readUShort()
			this.groupIds!![i0] = offset
			if (maxGroupId < this.groupIds!![i0]) {
				maxGroupId = this.groupIds!![i0]
			}
			i0++
		}
		this.capacity = maxGroupId + 1
		if (hasWhirlpool) {
			this.groupHashes = arrayOfNulls<ByteArray>(this.capacity)
		}
		this.groupChecksums = IntArray(this.capacity)
		this.groupCapacities = IntArray(this.capacity)
		this.groupSizes = IntArray(this.capacity)
		this.groupVersions = IntArray(this.capacity)
		this.validFileIds = arrayOfNulls<IntArray>(this.capacity)
		@Pc(159) var i: Int
		@Pc(173) var index: Int
		if (named) {
			this.nameHashes = IntArray(this.capacity)
			i = 0
			while (i < this.capacity) {
				this.nameHashes!![i] = -1
				i++
			}
			index = 0
			while (index < this.size) {
				this.nameHashes!![this.groupIds!![index]] = packet.readInt()
				index++
			}
			this.archiveNames = NamedFileReference(this.nameHashes)
		}
		i = 0
		while (i < this.size) {
			this.groupChecksums!![this.groupIds!![i]] = packet.readInt()
			i++
		}
		if (hasWhirlpool) {
			index = 0
			while (index < this.size) {
				@Pc(240) val groupHash: ByteArray = ByteArray(64)
				packet.readBytes(0, 64, groupHash)
				this.groupHashes!![this.groupIds!![index]] = groupHash
				index++
			}
		}
		index = 0
		while (index < this.size) {
			this.groupVersions!![this.groupIds!![index]] = packet.readInt()
			index++
		}
		@Pc(280) var j: Int = 0
		while (j < this.size) {
			this.groupSizes!![this.groupIds!![j]] = packet.readUShort()
			j++
		}
		@Pc(306) var local306: Int
		@Pc(313) var local313: Int
		@Pc(315) var local315: Int
		@Pc(323) var local323: Int
		@Pc(340) var local340: Int
		@Pc(299) var k: Int = 0
		while (k < this.size) {
			val groupId: Int = this.groupIds!![k]
			offset = 0
			val fileCount: Int = this.groupSizes!![groupId]
			var maxFileId: Int = -1
			this.validFileIds!![groupId] = IntArray(fileCount)
			var fileIndex: Int = 0
			while (fileIndex < fileCount) {
				offset += packet.readUShort()
				this.validFileIds!![groupId]!![fileIndex] = offset
				val fileId: Int = offset
				if (fileId > maxFileId) {
					maxFileId = fileId
				}
				fileIndex++
			}
			this.groupCapacities!![groupId] = maxFileId + 1
			if (fileCount == maxFileId + 1) {
				this.validFileIds!![groupId] = null
			}
			k++
		}
		if (!named) {
			return
		}
		this.fileNameHashes = arrayOfNulls<IntArray>(maxGroupId + 1)
		this.namedFiles = arrayOfNulls<NamedFileReference>(maxGroupId + 1)
		var groupIndex: Int = 0
		while (groupIndex < this.size) {
			val groupId: Int = this.groupIds!![groupIndex]
			val fileCount: Int = this.groupSizes!![groupId]
			this.fileNameHashes!![groupId] = IntArray(this.groupCapacities!![groupId])
			var slot: Int = 0
			while (slot < this.groupCapacities!![groupId]) {
				this.fileNameHashes!![groupId]!![slot] = -1
				slot++
			}
			var fileIndex: Int = 0
			while (fileIndex < fileCount) {
				@Pc(458) var fileId: Int
				if (this.validFileIds!![groupId] == null) {
					fileId = fileIndex
				} else {
					fileId = this.validFileIds!![groupId]!![fileIndex]
				}
				this.fileNameHashes!![groupId]!![fileId] = packet.readInt()
				fileIndex++
			}
			this.namedFiles!![groupId] = NamedFileReference(this.fileNameHashes!![groupId])
			groupIndex++
		}
	}
}
