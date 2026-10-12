package com.jagex.game.runetek5.config.sequence.frameset

import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import withLock

@OriginalClass("client!gw")
class FrameSet : CacheableNode {

	@OriginalMember(owner = "client!gw", name = "y", descriptor = "[[B")
	private var frameData: Array<ByteArray?>? = null

	@OriginalMember(owner = "client!gw", name = "A", descriptor = "[Lclient!gk;")
	@JvmField
	var frames: Array<AnimFrame?>? = null

	@OriginalMember(owner = "client!gw", name = "w", descriptor = "I")
	private val groupId: Int

	@OriginalMember(owner = "client!gw", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) groupId: Int) : super() {
		this.groupId = groupId
	}

	@OriginalMember(owner = "client!gw", name = "a", descriptor = "(BI)Z")
	fun modifiesBillboards(@OriginalArg(1) frameIndex: Int): Boolean {
		return this.frames!![frameIndex]!!.modifiesBillboard
	}

	@OriginalMember(owner = "client!gw", name = "a", descriptor = "(I)Z")
	fun isReady(): Boolean {
		if (this.frames != null) {
			return true
		}
		@Pc(35) var fileIds: IntArray?
		@Pc(42) val local42: Int
		if (this.frameData == null) {
			@Pc(15) val frameskeletonResource: Js5ResourceProvider? = FrameSet.frameskeletonResource
			withLock(FrameSet.frameskeletonResource!!) {
				if (!FrameSet.frameskeletonResource!!.isGroupReady(this.groupId)) {
					return false
				}
				fileIds = FrameSet.frameskeletonResource!!.getFileIds(this.groupId)
				this.frameData = arrayOfNulls<ByteArray>(fileIds!!.size)
				for (fileIndex in 0 until fileIds!!.size) {
					this.frameData!![fileIndex] = FrameSet.frameskeletonResource!!.getFile(this.groupId, fileIds!![fileIndex])
				}
			}
		}
		@Pc(73) var loaded: Boolean = true
		for (@Pc(75) i in 0 until this.frameData!!.size) {
			@Pc(82) val data: ByteArray? = this.frameData!![i]
			@Pc(87) val packet: Packet = Packet(data)
			packet.currentPosition = 1
			val frameBaseId: Int = packet.readUShort()
			@Pc(96) val framebaseLock: Js5ResourceProvider? = FrameSet.framebaseResource
			withLock(FrameSet.framebaseResource!!) {
				loaded = loaded and FrameSet.framebaseResource!!.isFileReady(frameBaseId)
			}
		}
		if (!loaded) {
			return false
		}
		@Pc(125) val deque: Deque = Deque()
		@Pc(127) val frameskeletonLock: Js5ResourceProvider? = FrameSet.frameskeletonResource
		withLock(FrameSet.frameskeletonResource!!) {
			@Pc(135) val fileCount: Int = FrameSet.frameskeletonResource!!.getGroupCapacity(this.groupId)
			this.frames = arrayOfNulls<AnimFrame>(fileCount)
			fileIds = FrameSet.frameskeletonResource!!.getFileIds(this.groupId)
		}
		for (fileIndex in 0 until fileIds!!.size) {
			@Pc(168) val data: ByteArray? = this.frameData!![fileIndex]
			@Pc(173) val packet2: Packet = Packet(data)
			packet2.currentPosition = 1
			@Pc(180) val baseId: Int = packet2.readUShort()
			@Pc(182) var animBase: AnimBase? = null
			@Pc(187) var animBase2: AnimBase? = deque.first() as AnimBase?
			while (animBase2 != null) {
				if (animBase2.id == baseId) {
					animBase = animBase2
					break
				}
				animBase2 = deque.next() as AnimBase?
			}
			if (animBase == null) {
				@Pc(207) val framebaseLock: Js5ResourceProvider? = FrameSet.framebaseResource
				withLock(FrameSet.framebaseResource!!) {
					animBase = AnimBase(baseId, FrameSet.framebaseResource!!.getFile(baseId))
				}
				deque.addLast(animBase)
			}
			this.frames!![fileIds!![fileIndex]] = AnimFrame(data, animBase)
		}
		this.frameData = null
		return true
	}

	@OriginalMember(owner = "client!gw", name = "c", descriptor = "(II)Z")
	fun modifiesAlpha(@OriginalArg(0) frameIndex: Int): Boolean {
		return this.frames!![frameIndex]!!.modifiesAlpha
	}

	@OriginalMember(owner = "client!gw", name = "d", descriptor = "(II)Z")
	fun modifiesColors(@OriginalArg(0) frameIndex: Int): Boolean {
		return this.frames!![frameIndex]!!.modifiesColor
	}

	companion object {
		@OriginalMember(owner = "client!oaa", name = "a", descriptor = "Lclient!in;")
		@JvmField
		var frameskeletonResource: Js5ResourceProvider? = null

		@OriginalMember(owner = "client!kaa", name = "u", descriptor = "Lclient!in;")
		@JvmField
		var framebaseResource: Js5ResourceProvider? = null

		@OriginalMember(owner = "client!su", name = "a", descriptor = "(ILclient!in;ILclient!in;)V")
		@JvmStatic
		fun initialize(@OriginalArg(1) frameskeletonResource: Js5ResourceProvider?, @OriginalArg(3) framebaseResource: Js5ResourceProvider?) {
			FrameSet.framebaseResource = framebaseResource
			FrameSet.frameskeletonResource = frameskeletonResource
		}
	}
}
