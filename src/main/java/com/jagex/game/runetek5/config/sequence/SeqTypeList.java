package com.jagex.game.runetek5.config.sequence;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.sequence.frameset.FrameSet;

@OriginalClass("client!lfa")
public final class SeqTypeList {

	@OriginalMember(owner = "client!lfa", name = "e", descriptor = "Lclient!jr;")
	private final LRUCache sequenceCache = new LRUCache(64);

	@OriginalMember(owner = "client!lfa", name = "i", descriptor = "Lclient!jr;")
	private final LRUCache framesetCache = new LRUCache(100);

	@OriginalMember(owner = "client!lfa", name = "f", descriptor = "Lclient!in;")
	private final Js5ResourceProvider sequenceResource;

	@OriginalMember(owner = "client!lfa", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;Lclient!in;)V")
	public SeqTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider sequenceResource, @OriginalArg(3) Js5ResourceProvider frameskeletonResource, @OriginalArg(4) Js5ResourceProvider framebaseResource) {
		this.sequenceResource = sequenceResource;
		if (this.sequenceResource != null) {
			@Pc(26) int lastGroupId = this.sequenceResource.getCapacity() - 1;
			this.sequenceResource.getGroupCapacity(lastGroupId);
		}
		FrameSet.initialize(frameskeletonResource, framebaseResource);
	}

	@OriginalMember(owner = "client!lfa", name = "a", descriptor = "(B)V")
	public void cacheReset() {
		@Pc(2) LRUCache referenceCache = this.sequenceCache;
		synchronized (this.sequenceCache) {
			this.sequenceCache.reset();
		}
		referenceCache = this.framesetCache;
		synchronized (this.framesetCache) {
			this.framesetCache.reset();
		}
	}

	@OriginalMember(owner = "client!lfa", name = "a", descriptor = "(II)Lclient!hg;")
	public SeqType list(@OriginalArg(0) int id) {
		@Pc(6) LRUCache referenceCache = this.sequenceCache;
		@Pc(18) SeqType seqType;
		synchronized (this.sequenceCache) {
			seqType = (SeqType) this.sequenceCache.get((long) id);
		}
		if (seqType != null) {
			return seqType;
		}
		@Pc(37) Js5ResourceProvider sequenceResource = this.sequenceResource;
		@Pc(52) byte[] data;
		synchronized (this.sequenceResource) {
			data = this.sequenceResource.getFile(SeqTypeList.getGroupId(id), SeqTypeList.getFileId(id));
		}
		seqType = new SeqType();
		seqType.id = id;
		seqType.seqtypelist = this;
		if (data != null) {
			seqType.decode(new Packet(data));
		}
		seqType.postDecode();
		@Pc(83) LRUCache referenceCache2 = this.sequenceCache;
		synchronized (this.sequenceCache) {
			this.sequenceCache.put(seqType, (long) id);
			return seqType;
		}
	}

	@OriginalMember(owner = "client!lfa", name = "b", descriptor = "(II)V")
	public void cacheClean() {
		@Pc(6) LRUCache referenceCache = this.sequenceCache;
		synchronized (this.sequenceCache) {
			this.sequenceCache.clean(5);
		}
		referenceCache = this.framesetCache;
		synchronized (this.framesetCache) {
			this.framesetCache.clean(5);
		}
	}

	@OriginalMember(owner = "client!lfa", name = "a", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(2) LRUCache referenceCache = this.sequenceCache;
		synchronized (this.sequenceCache) {
			this.sequenceCache.removeSoftReferences();
		}
		referenceCache = this.framesetCache;
		synchronized (this.framesetCache) {
			this.framesetCache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!lfa", name = "c", descriptor = "(II)Lclient!gw;")
	public FrameSet listFrameSet(@OriginalArg(0) int groupId) {
		@Pc(6) LRUCache referenceCache = this.framesetCache;
		synchronized (this.framesetCache) {
			@Pc(22) FrameSet animFrameset = (FrameSet) this.framesetCache.get((long) groupId);
			if (animFrameset == null) {
				animFrameset = new FrameSet(groupId);
				this.framesetCache.put(animFrameset, (long) groupId);
			}
			return animFrameset.isReady() ? animFrameset : null;
		}
	}
	@OriginalMember(owner = "client!hg", name = "m", descriptor = "Z")
	public static boolean forceTween = false;
	@OriginalMember(owner = "client!hea", name = "a", descriptor = "(IB)I")
	public static int getGroupId(@OriginalArg(0) int id) {
		return id >>> 7;
	}

	@OriginalMember(owner = "client!jca", name = "a", descriptor = "(II)I")
	public static int getFileId(@OriginalArg(1) int id) {
		return id & 0x7F;
	}
}
