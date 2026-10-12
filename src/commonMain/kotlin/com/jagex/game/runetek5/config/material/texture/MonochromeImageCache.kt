package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.cache.node.MonochromeImageCacheSlot
import com.jagex.game.runetek5.core.datastruct.key.Deque
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!qk")
class MonochromeImageCache {

	@OriginalMember(owner = "client!qk", name = "b", descriptor = "I")
	private var allocatedEntryCount: Int = 0

	@OriginalMember(owner = "client!qk", name = "g", descriptor = "I")
	private var lastUsedNodeIndex: Int = -1

	@OriginalMember(owner = "client!qk", name = "f", descriptor = "Lclient!uh;")
	private var history: Deque? = Deque()

	@OriginalMember(owner = "client!qk", name = "o", descriptor = "Z")
	@JvmField
	var dirty: Boolean = false

	@OriginalMember(owner = "client!qk", name = "d", descriptor = "I")
	private val maxCachedEntries: Int

	@OriginalMember(owner = "client!qk", name = "j", descriptor = "I")
	private val paletteSize: Int

	@OriginalMember(owner = "client!qk", name = "h", descriptor = "[Lclient!gba;")
	private var nodes: Array<MonochromeImageCacheSlot?>?

	@OriginalMember(owner = "client!qk", name = "i", descriptor = "[[I")
	private var paletteData: Array<IntArray?>?

	@OriginalMember(owner = "client!qk", name = "<init>", descriptor = "(III)V")
	constructor(@OriginalArg(0) maxCachedEntries: Int, @OriginalArg(1) paletteSize: Int, @OriginalArg(2) width: Int) {
		this.maxCachedEntries = maxCachedEntries
		this.paletteSize = paletteSize
		this.nodes = arrayOfNulls<MonochromeImageCacheSlot>(this.paletteSize)
		this.paletteData = Array<IntArray?>(this.maxCachedEntries) { IntArray(width) }
	}

	@OriginalMember(owner = "client!qk", name = "a", descriptor = "(B)[[I")
	fun getAllPaletteData(): Array<IntArray?>? {
		if (this.maxCachedEntries != this.paletteSize) {
			throw RuntimeException("Can only retrieve a full image cache")
		}
		@Pc(17) var i: Int = 0
		while (i < this.maxCachedEntries) {
			this.nodes!![i] = MonochromeImageCache.EMPTY
			i++
		}
		return this.paletteData
	}

	@OriginalMember(owner = "client!qk", name = "b", descriptor = "(B)V")
	fun reset() {
		@Pc(3) var i: Int = 0
		while (i < this.maxCachedEntries) {
			this.paletteData!![i] = null
			i++
		}
		this.nodes = null
		this.paletteData = null
		this.history!!.clear()
		this.history = null
	}

	@OriginalMember(owner = "client!qk", name = "a", descriptor = "(II)[I")
	fun get(@OriginalArg(1) row: Int): IntArray? {
		if (this.maxCachedEntries == this.paletteSize) {
			this.dirty = this.nodes!![row] == null
			this.nodes!![row] = MonochromeImageCache.EMPTY
			return this.paletteData!![row]
		} else if (this.maxCachedEntries == 1) {
			this.dirty = row != this.lastUsedNodeIndex
			this.lastUsedNodeIndex = row
			return this.paletteData!![0]
		} else {
			@Pc(45) var monochromeImageCacheSlot: MonochromeImageCacheSlot? = this.nodes!![row]
			if (monochromeImageCacheSlot == null) {
				this.dirty = true
				if (this.maxCachedEntries > this.allocatedEntryCount) {
					monochromeImageCacheSlot = MonochromeImageCacheSlot(row, this.allocatedEntryCount)
					this.allocatedEntryCount++
				} else {
					@Pc(82) val monochromeImageCacheSlot2: MonochromeImageCacheSlot? = this.history!!.last() as MonochromeImageCacheSlot?
					monochromeImageCacheSlot = MonochromeImageCacheSlot(row, monochromeImageCacheSlot2!!.slot)
					this.nodes!![monochromeImageCacheSlot2.id] = null
					monochromeImageCacheSlot2.unlink()
				}
				this.nodes!![row] = monochromeImageCacheSlot
			} else {
				this.dirty = false
			}
			this.history!!.addFirst(monochromeImageCacheSlot)
			return this.paletteData!![monochromeImageCacheSlot.slot]
		}
	}

	companion object {
		@OriginalMember(owner = "client!nba", name = "j", descriptor = "Lclient!gba;")
		@JvmField
		val EMPTY: MonochromeImageCacheSlot = MonochromeImageCacheSlot(0, 0)
	}
}
