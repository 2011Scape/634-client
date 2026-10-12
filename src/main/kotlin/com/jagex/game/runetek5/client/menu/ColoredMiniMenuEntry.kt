package com.jagex.game.runetek5.client.menu

import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import com.jagex.game.runetek5.cache.node.Queue
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!vq")
class ColoredMiniMenuEntry : CacheableNode {

	@OriginalMember(owner = "client!vq", name = "y", descriptor = "I")
	@JvmField
	var queueSize: Int = 0

	@OriginalMember(owner = "client!vq", name = "A", descriptor = "Ljava/lang/String;")
	@JvmField
	val tagStart: String?

	@OriginalMember(owner = "client!vq", name = "E", descriptor = "Lclient!mn;")
	@JvmField
	val nodeQueue: Queue

	@OriginalMember(owner = "client!vq", name = "<init>", descriptor = "(Ljava/lang/String;)V")
	constructor(@OriginalArg(0) string: String?) : super() {
		this.tagStart = string
		this.nodeQueue = Queue()
	}

	@OriginalMember(owner = "client!vq", name = "a", descriptor = "(Lclient!db;I)Z")
	fun addEntry(@OriginalArg(0) miniMenuEntryInner: MiniMenuEntry?): Boolean {
		@Pc(7) var isFirst: Boolean = true
		miniMenuEntryInner!!.unlinkSecondary()
		@Pc(16) var miniMenuEntryInner2: MiniMenuEntry? = this.nodeQueue.first() as MiniMenuEntry?
		while (miniMenuEntryInner2 != null) {
			if (MiniMenu.hasHigherPriority(miniMenuEntryInner2.clickType, miniMenuEntryInner.clickType)) {
				CacheableNode.insertSecondaryBefore(miniMenuEntryInner2, miniMenuEntryInner)
				this.queueSize++
				if (isFirst) {
					return false
				}
				return true
			}
			miniMenuEntryInner2 = this.nodeQueue.next() as MiniMenuEntry?
			isFirst = false
		}
		this.nodeQueue.insert(miniMenuEntryInner)
		this.queueSize++
		return isFirst
	}

	@OriginalMember(owner = "client!vq", name = "a", descriptor = "(BLclient!db;)Z")
	fun removeEntry(@OriginalArg(1) miniMenuEntryInner: MiniMenuEntry?): Boolean {
		@Pc(15) val action: Int = this.getAction()
		miniMenuEntryInner!!.unlinkSecondary()
		this.queueSize--
		if (this.queueSize != 0) {
			return this.getAction() != action
		}
		this.unlink()
		this.unlinkSecondary()
		MiniMenu.coloredEntryCount--
		MiniMenu.coloredEntryCache.put(this, miniMenuEntryInner.bitpacked)
		return false
	}

	@OriginalMember(owner = "client!vq", name = "a", descriptor = "(I)I")
	fun getAction(): Int {
		return if (this.nodeQueue.sentinel === this.nodeQueue.sentinel.secondaryNext) -1 else (this.nodeQueue.sentinel.secondaryNext as MiniMenuEntry).clickType
	}
}
