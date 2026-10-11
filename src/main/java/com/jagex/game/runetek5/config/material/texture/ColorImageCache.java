package com.jagex.game.runetek5.config.material.texture;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.config.material.texture.ColorImageCacheEntry;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!afa")
public final class ColorImageCache {

	@OriginalMember(owner = "client!afa", name = "h", descriptor = "I")
	private int cachedId = 0;

	@OriginalMember(owner = "client!afa", name = "e", descriptor = "I")
	private int count = -1;

	@OriginalMember(owner = "client!afa", name = "d", descriptor = "Lclient!uh;")
	private Deque history = new Deque();

	@OriginalMember(owner = "client!afa", name = "r", descriptor = "Z")
	public boolean dirty = false;

	@OriginalMember(owner = "client!afa", name = "c", descriptor = "I")
	private final int totalRows;

	@OriginalMember(owner = "client!afa", name = "g", descriptor = "I")
	private final int maxCachedRows;

	@OriginalMember(owner = "client!afa", name = "p", descriptor = "[Lclient!ls;")
	private ColorImageCacheEntry[] cacheEntries;

	@OriginalMember(owner = "client!afa", name = "l", descriptor = "[[[I")
	private int[][][] rgbData;

	@OriginalMember(owner = "client!afa", name = "<init>", descriptor = "(III)V")
	public ColorImageCache(@OriginalArg(0) int capacity, @OriginalArg(1) int height, @OriginalArg(2) int width) {
		this.totalRows = capacity;
		this.maxCachedRows = height;
		this.cacheEntries = new ColorImageCacheEntry[this.maxCachedRows];
		this.rgbData = new int[this.totalRows][3][width];
	}

	@OriginalMember(owner = "client!afa", name = "a", descriptor = "(I)[[[I")
	public int[][][] getAllPalettes() {
		if (this.maxCachedRows != this.totalRows) {
			throw new RuntimeException("Can only retrieve a full image cache");
		}
		for (@Pc(17) int i = 0; i < this.totalRows; i++) {
			this.cacheEntries[i] = ColorImageCache.SENTINEL_ENTRY;
		}
		return this.rgbData;
	}

	@OriginalMember(owner = "client!afa", name = "a", descriptor = "(II)[[I")
	public int[][] get(@OriginalArg(1) int count) {
		if (this.totalRows == this.maxCachedRows) {
			this.dirty = this.cacheEntries[count] == null;
			this.cacheEntries[count] = ColorImageCache.SENTINEL_ENTRY;
			return this.rgbData[count];
		} else if (this.totalRows == 1) {
			this.dirty = this.count != count;
			this.count = count;
			return this.rgbData[0];
		} else {
			@Pc(47) ColorImageCacheEntry colourImageCacheSlot = this.cacheEntries[count];
			if (colourImageCacheSlot == null) {
				this.dirty = true;
				if (this.cachedId >= this.totalRows) {
					@Pc(67) ColorImageCacheEntry colourImageCacheSlot2 = (ColorImageCacheEntry) this.history.last();
					colourImageCacheSlot = new ColorImageCacheEntry(count, colourImageCacheSlot2.index);
					this.cacheEntries[colourImageCacheSlot2.row] = null;
					colourImageCacheSlot2.unlink();
				} else {
					colourImageCacheSlot = new ColorImageCacheEntry(count, this.cachedId);
					this.cachedId++;
				}
				this.cacheEntries[count] = colourImageCacheSlot;
			} else {
				this.dirty = false;
			}
			this.history.addFirst(colourImageCacheSlot);
			return this.rgbData[colourImageCacheSlot.index];
		}
	}

	@OriginalMember(owner = "client!afa", name = "b", descriptor = "(I)V")
	public void reset() {
		for (@Pc(13) int i = 0; i < this.totalRows; i++) {
			this.rgbData[i][0] = null;
			this.rgbData[i][1] = null;
			this.rgbData[i][2] = null;
			this.rgbData[i] = null;
		}
		this.cacheEntries = null;
		this.rgbData = null;
		this.history.clear();
		this.history = null;
	}
	@OriginalMember(owner = "client!ec", name = "a", descriptor = "Lclient!ls;")
	public static final ColorImageCacheEntry SENTINEL_ENTRY = new ColorImageCacheEntry(0, 0);
}
