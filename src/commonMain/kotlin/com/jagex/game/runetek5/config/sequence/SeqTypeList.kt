package com.jagex.game.runetek5.config.sequence

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.sequence.frameset.FrameSet
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import withLock

@OriginalClass("client!lfa")
class SeqTypeList {

	@OriginalMember(owner = "client!lfa", name = "e", descriptor = "Lclient!jr;")
	private val sequenceCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!lfa", name = "i", descriptor = "Lclient!jr;")
	private val framesetCache: LRUCache = LRUCache(100)

	@OriginalMember(owner = "client!lfa", name = "f", descriptor = "Lclient!in;")
	private val sequenceResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!lfa", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;Lclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) sequenceResource: Js5ResourceProvider?, @OriginalArg(3) frameskeletonResource: Js5ResourceProvider?, @OriginalArg(4) framebaseResource: Js5ResourceProvider?) {
		this.sequenceResource = sequenceResource
		if (this.sequenceResource != null) {
			@Pc(26) val lastGroupId: Int = this.sequenceResource.getCapacity() - 1
			this.sequenceResource.getGroupCapacity(lastGroupId)
		}
		FrameSet.initialize(frameskeletonResource, framebaseResource)
	}

	@OriginalMember(owner = "client!lfa", name = "a", descriptor = "(B)V")
	fun cacheReset() {
		@Pc(2) var referenceCache: LRUCache = this.sequenceCache
		withLock(this.sequenceCache) {
			this.sequenceCache.reset()
		}
		referenceCache = this.framesetCache
		withLock(this.framesetCache) {
			this.framesetCache.reset()
		}
	}

	@OriginalMember(owner = "client!lfa", name = "a", descriptor = "(II)Lclient!hg;")
	fun list(@OriginalArg(0) id: Int): SeqType {
		@Pc(6) val referenceCache: LRUCache = this.sequenceCache
		@Pc(18) var seqType: SeqType?
		withLock(this.sequenceCache) {
			seqType = this.sequenceCache.get(id.toLong()) as SeqType?
		}
		if (seqType != null) {
			return seqType!!
		}
		@Pc(37) val sequenceResource: Js5ResourceProvider? = this.sequenceResource
		@Pc(52) var data: ByteArray?
		withLock(this.sequenceResource!!) {
			data = this.sequenceResource.getFile(SeqTypeList.getGroupId(id), SeqTypeList.getFileId(id))
		}
		val newSeqType: SeqType = SeqType()
		seqType = newSeqType
		newSeqType.id = id
		newSeqType.seqtypelist = this
		if (data != null) {
			newSeqType.decode(Packet(data))
		}
		newSeqType.postDecode()
		@Pc(83) val referenceCache2: LRUCache = this.sequenceCache
		withLock(this.sequenceCache) {
			this.sequenceCache.put(newSeqType, id.toLong())
			return newSeqType
		}
	}

	@OriginalMember(owner = "client!lfa", name = "b", descriptor = "(II)V")
	fun cacheClean() {
		@Pc(6) var referenceCache: LRUCache = this.sequenceCache
		withLock(this.sequenceCache) {
			this.sequenceCache.clean(5)
		}
		referenceCache = this.framesetCache
		withLock(this.framesetCache) {
			this.framesetCache.clean(5)
		}
	}

	@OriginalMember(owner = "client!lfa", name = "a", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(2) var referenceCache: LRUCache = this.sequenceCache
		withLock(this.sequenceCache) {
			this.sequenceCache.removeSoftReferences()
		}
		referenceCache = this.framesetCache
		withLock(this.framesetCache) {
			this.framesetCache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!lfa", name = "c", descriptor = "(II)Lclient!gw;")
	fun listFrameSet(@OriginalArg(0) groupId: Int): FrameSet? {
		@Pc(6) val referenceCache: LRUCache = this.framesetCache
		withLock(this.framesetCache) {
			@Pc(22) var animFrameset: FrameSet? = this.framesetCache.get(groupId.toLong()) as FrameSet?
			if (animFrameset == null) {
				animFrameset = FrameSet(groupId)
				this.framesetCache.put(animFrameset, groupId.toLong())
			}
			return if (animFrameset.isReady()) animFrameset else null
		}
	}

	companion object {
		@OriginalMember(owner = "client!hg", name = "m", descriptor = "Z")
		@JvmField
		var forceTween: Boolean = false

		@OriginalMember(owner = "client!hea", name = "a", descriptor = "(IB)I")
		@JvmStatic
		fun getGroupId(@OriginalArg(0) id: Int): Int {
			return id ushr 7
		}

		@OriginalMember(owner = "client!jca", name = "a", descriptor = "(II)I")
		@JvmStatic
		fun getFileId(@OriginalArg(1) id: Int): Int {
			return id and 0x7F
		}
	}
}
