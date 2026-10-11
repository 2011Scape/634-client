package com.jagex.game.runetek5.config.material.texture;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.cache.node.MonochromeImageCacheSlot;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!qk")
public final class MonochromeImageCache {

	@OriginalMember(owner = "client!qk", name = "b", descriptor = "I")
	private int allocatedEntryCount = 0;

	@OriginalMember(owner = "client!qk", name = "g", descriptor = "I")
	private int lastUsedNodeIndex = -1;

	@OriginalMember(owner = "client!qk", name = "f", descriptor = "Lclient!uh;")
	private Deque history = new Deque();

	@OriginalMember(owner = "client!qk", name = "o", descriptor = "Z")
	public boolean dirty = false;

	@OriginalMember(owner = "client!qk", name = "d", descriptor = "I")
	private final int maxCachedEntries;

	@OriginalMember(owner = "client!qk", name = "j", descriptor = "I")
	private final int paletteSize;

	@OriginalMember(owner = "client!qk", name = "h", descriptor = "[Lclient!gba;")
	private MonochromeImageCacheSlot[] nodes;

	@OriginalMember(owner = "client!qk", name = "i", descriptor = "[[I")
	private int[][] paletteData;

	@OriginalMember(owner = "client!qk", name = "<init>", descriptor = "(III)V")
	public MonochromeImageCache(@OriginalArg(0) int maxCachedEntries, @OriginalArg(1) int paletteSize, @OriginalArg(2) int width) {
		this.maxCachedEntries = maxCachedEntries;
		this.paletteSize = paletteSize;
		this.nodes = new MonochromeImageCacheSlot[this.paletteSize];
		this.paletteData = new int[this.maxCachedEntries][width];
	}

	@OriginalMember(owner = "client!qk", name = "a", descriptor = "(B)[[I")
	public int[][] getAllPaletteData() {
		if (this.maxCachedEntries != this.paletteSize) {
			throw new RuntimeException("Can only retrieve a full image cache");
		}
		for (@Pc(17) int i = 0; i < this.maxCachedEntries; i++) {
			this.nodes[i] = MonochromeImageCache.EMPTY;
		}
		return this.paletteData;
	}

	@OriginalMember(owner = "client!qk", name = "b", descriptor = "(B)V")
	public void reset() {
		for (@Pc(3) int i = 0; i < this.maxCachedEntries; i++) {
			this.paletteData[i] = null;
		}
		this.nodes = null;
		this.paletteData = null;
		this.history.clear();
		this.history = null;
	}

	@OriginalMember(owner = "client!qk", name = "a", descriptor = "(II)[I")
	public int[] get(@OriginalArg(1) int row) {
		if (this.maxCachedEntries == this.paletteSize) {
			this.dirty = this.nodes[row] == null;
			this.nodes[row] = MonochromeImageCache.EMPTY;
			return this.paletteData[row];
		} else if (this.maxCachedEntries == 1) {
			this.dirty = row != this.lastUsedNodeIndex;
			this.lastUsedNodeIndex = row;
			return this.paletteData[0];
		} else {
			@Pc(45) MonochromeImageCacheSlot monochromeImageCacheSlot = this.nodes[row];
			if (monochromeImageCacheSlot == null) {
				this.dirty = true;
				if (this.maxCachedEntries > this.allocatedEntryCount) {
					monochromeImageCacheSlot = new MonochromeImageCacheSlot(row, this.allocatedEntryCount);
					this.allocatedEntryCount++;
				} else {
					@Pc(82) MonochromeImageCacheSlot monochromeImageCacheSlot2 = (MonochromeImageCacheSlot) this.history.last();
					monochromeImageCacheSlot = new MonochromeImageCacheSlot(row, monochromeImageCacheSlot2.slot);
					this.nodes[monochromeImageCacheSlot2.id] = null;
					monochromeImageCacheSlot2.unlink();
				}
				this.nodes[row] = monochromeImageCacheSlot;
			} else {
				this.dirty = false;
			}
			this.history.addFirst(monochromeImageCacheSlot);
			return this.paletteData[monochromeImageCacheSlot.slot];
		}
	}
	@OriginalMember(owner = "client!nba", name = "j", descriptor = "Lclient!gba;")
	public static final MonochromeImageCacheSlot EMPTY = new MonochromeImageCacheSlot(0, 0);
}
