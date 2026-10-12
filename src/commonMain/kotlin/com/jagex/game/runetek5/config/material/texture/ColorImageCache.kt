package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.core.datastruct.key.Deque
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!afa")
class ColorImageCache {

	@OriginalMember(owner = "client!afa", name = "h", descriptor = "I")
	private var cachedId: Int = 0

	@OriginalMember(owner = "client!afa", name = "e", descriptor = "I")
	private var count: Int = -1

	@OriginalMember(owner = "client!afa", name = "d", descriptor = "Lclient!uh;")
	private var history: Deque? = Deque()

	@OriginalMember(owner = "client!afa", name = "r", descriptor = "Z")
	@JvmField
	var dirty: Boolean = false

	@OriginalMember(owner = "client!afa", name = "c", descriptor = "I")
	private val totalRows: Int

	@OriginalMember(owner = "client!afa", name = "g", descriptor = "I")
	private val maxCachedRows: Int

	@OriginalMember(owner = "client!afa", name = "p", descriptor = "[Lclient!ls;")
	private var cacheEntries: Array<ColorImageCacheEntry?>?

	@OriginalMember(owner = "client!afa", name = "l", descriptor = "[[[I")
	private var rgbData: Array<Array<IntArray?>?>?

	@OriginalMember(owner = "client!afa", name = "<init>", descriptor = "(III)V")
	constructor(@OriginalArg(0) capacity: Int, @OriginalArg(1) height: Int, @OriginalArg(2) width: Int) {
		this.totalRows = capacity
		this.maxCachedRows = height
		this.cacheEntries = arrayOfNulls<ColorImageCacheEntry>(this.maxCachedRows)
		this.rgbData = Array<Array<IntArray?>?>(this.totalRows) { Array<IntArray?>(3) { IntArray(width) } }
	}

	@OriginalMember(owner = "client!afa", name = "a", descriptor = "(I)[[[I")
	fun getAllPalettes(): Array<Array<IntArray?>?>? {
		if (this.maxCachedRows != this.totalRows) {
			throw RuntimeException("Can only retrieve a full image cache")
		}
		@Pc(17) var i: Int = 0
		while (i < this.totalRows) {
			this.cacheEntries!![i] = ColorImageCache.SENTINEL_ENTRY
			i++
		}
		return this.rgbData
	}

	@OriginalMember(owner = "client!afa", name = "a", descriptor = "(II)[[I")
	fun get(@OriginalArg(1) count: Int): Array<IntArray?>? {
		if (this.totalRows == this.maxCachedRows) {
			this.dirty = this.cacheEntries!![count] == null
			this.cacheEntries!![count] = ColorImageCache.SENTINEL_ENTRY
			return this.rgbData!![count]
		} else if (this.totalRows == 1) {
			this.dirty = this.count != count
			this.count = count
			return this.rgbData!![0]
		} else {
			@Pc(47) var colourImageCacheSlot: ColorImageCacheEntry? = this.cacheEntries!![count]
			if (colourImageCacheSlot == null) {
				this.dirty = true
				if (this.cachedId >= this.totalRows) {
					@Pc(67) val colourImageCacheSlot2: ColorImageCacheEntry? = this.history!!.last() as ColorImageCacheEntry?
					colourImageCacheSlot = ColorImageCacheEntry(count, colourImageCacheSlot2!!.index)
					this.cacheEntries!![colourImageCacheSlot2.row] = null
					colourImageCacheSlot2.unlink()
				} else {
					colourImageCacheSlot = ColorImageCacheEntry(count, this.cachedId)
					this.cachedId++
				}
				this.cacheEntries!![count] = colourImageCacheSlot
			} else {
				this.dirty = false
			}
			this.history!!.addFirst(colourImageCacheSlot)
			return this.rgbData!![colourImageCacheSlot.index]
		}
	}

	@OriginalMember(owner = "client!afa", name = "b", descriptor = "(I)V")
	fun reset() {
		@Pc(13) var i: Int = 0
		while (i < this.totalRows) {
			this.rgbData!![i]!![0] = null
			this.rgbData!![i]!![1] = null
			this.rgbData!![i]!![2] = null
			this.rgbData!![i] = null
			i++
		}
		this.cacheEntries = null
		this.rgbData = null
		this.history!!.clear()
		this.history = null
	}

	companion object {
		@OriginalMember(owner = "client!ec", name = "a", descriptor = "Lclient!ls;")
		@JvmField
		val SENTINEL_ENTRY: ColorImageCacheEntry = ColorImageCacheEntry(0, 0)
	}
}
