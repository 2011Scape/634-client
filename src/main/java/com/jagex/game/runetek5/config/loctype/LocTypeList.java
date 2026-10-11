package com.jagex.game.runetek5.config.loctype;

import com.jagex.game.runetek5.LocType;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!uha")
public final class LocTypeList {

	@OriginalMember(owner = "client!uha", name = "j", descriptor = "[C")
	private static final char[] BASE64_CHARS = new char[64];

	@OriginalMember(owner = "client!uha", name = "u", descriptor = "I")
	public int featureMask;

	@OriginalMember(owner = "client!uha", name = "m", descriptor = "Z")
	public boolean lowDetail = false;

	@OriginalMember(owner = "client!uha", name = "h", descriptor = "Lclient!jr;")
	private LRUCache typeCache = new LRUCache(64);

	@OriginalMember(owner = "client!uha", name = "r", descriptor = "Lclient!jr;")
	public final LRUCache modelsByShapeCache = new LRUCache(500);

	@OriginalMember(owner = "client!uha", name = "s", descriptor = "Lclient!jr;")
	public final LRUCache modelAndShadowCache = new LRUCache(30);

	@OriginalMember(owner = "client!uha", name = "t", descriptor = "Lclient!jr;")
	public final LRUCache modelCache = new LRUCache(50);

	@OriginalMember(owner = "client!uha", name = "q", descriptor = "Z")
	public boolean allowMembers;

	@OriginalMember(owner = "client!uha", name = "c", descriptor = "Lclient!in;")
	public final Js5ResourceProvider meshResource;

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	static {
		for (@Pc(7) int i = 0; i < 26; i++) {
			BASE64_CHARS[i] = (char) (i + 65);
		}
		for (@Pc(23) int i = 26; i < 52; i++) {
			BASE64_CHARS[i] = (char) (i + 97 - 26);
		}
		for (@Pc(39) int i = 52; i < 62; i++) {
			BASE64_CHARS[i] = (char) (i + 48 - 52);
		}
		BASE64_CHARS[62] = '+';
		BASE64_CHARS[63] = '/';
	}

	@OriginalMember(owner = "client!uha", name = "<init>", descriptor = "(Lclient!sj;IZLclient!in;Lclient!in;)V")
	public LocTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) boolean allowMembers, @OriginalArg(3) Js5ResourceProvider configClient, @OriginalArg(4) Js5ResourceProvider meshResource) {
		this.allowMembers = allowMembers;
		this.meshResource = meshResource;
		this.configClient = configClient;
		if (this.configClient != null) {
			@Pc(47) int lastGroupId = this.configClient.getCapacity() - 1;
			this.configClient.getGroupCapacity(lastGroupId);
		}
	}

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "(ZB)V")
	public void setAllowMembers(@OriginalArg(0) boolean allowMembers) {
		if (allowMembers != this.allowMembers) {
			this.allowMembers = allowMembers;
			this.cacheReset();
		}
	}

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "(II)Lclient!iv;")
	public LocType list(@OriginalArg(1) int id) {
		@Pc(6) LRUCache referenceCache = this.typeCache;
		@Pc(18) LocType locType;
		synchronized (this.typeCache) {
			locType = (LocType) this.typeCache.get((long) id);
		}
		if (locType != null) {
			return locType;
		}
		@Pc(31) Js5ResourceProvider configClient = this.configClient;
		@Pc(46) byte[] data;
		synchronized (this.configClient) {
			data = this.configClient.getFile(LocTypeList.getGroupId(id), LocTypeList.getFileId(id));
		}
		locType = new LocType();
		locType.id = id;
		locType.locTypeList = this;
		if (data != null) {
			locType.decode(new Packet(data));
		}
		locType.postDecode();
		if (!this.allowMembers && locType.members) {
			locType.quests = null;
			locType.actions = null;
		}
		if (locType.ignoreClipOnAltRoute) {
			locType.blocksMovement = 0;
			locType.blocksProjectiles = false;
		}
		@Pc(103) LRUCache referenceCache2 = this.typeCache;
		synchronized (this.typeCache) {
			this.typeCache.put(locType, (long) id);
			return locType;
		}
	}

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(10) LRUCache referenceCache = this.typeCache;
		synchronized (this.typeCache) {
			this.typeCache.removeSoftReferences();
		}
		referenceCache = this.modelsByShapeCache;
		synchronized (this.modelsByShapeCache) {
			this.modelsByShapeCache.removeSoftReferences();
		}
		referenceCache = this.modelAndShadowCache;
		synchronized (this.modelAndShadowCache) {
			this.modelAndShadowCache.removeSoftReferences();
		}
		referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "(IZ)V")
	public void setLowDetail(@OriginalArg(1) boolean lowDetail) {
		if (this.lowDetail != lowDetail) {
			this.lowDetail = lowDetail;
			this.cacheReset();
		}
	}

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "(IB)V")
	public void setFeatureMask(@OriginalArg(0) int featureMask) {
		this.featureMask = featureMask;
		@Pc(9) LRUCache referenceCache = this.modelsByShapeCache;
		synchronized (this.modelsByShapeCache) {
			this.modelsByShapeCache.reset();
		}
		referenceCache = this.modelAndShadowCache;
		synchronized (this.modelAndShadowCache) {
			this.modelAndShadowCache.reset();
		}
		referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.reset();
		}
	}

	@OriginalMember(owner = "client!uha", name = "b", descriptor = "(II)V")
	public void cacheClean() {
		@Pc(6) LRUCache referenceCache = this.typeCache;
		synchronized (this.typeCache) {
			this.typeCache.clean(5);
		}
		referenceCache = this.modelsByShapeCache;
		synchronized (this.modelsByShapeCache) {
			this.modelsByShapeCache.clean(5);
		}
		referenceCache = this.modelAndShadowCache;
		synchronized (this.modelAndShadowCache) {
			this.modelAndShadowCache.clean(5);
		}
		referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.clean(5);
		}
	}

	@OriginalMember(owner = "client!uha", name = "b", descriptor = "(I)V")
	public void cacheReset() {
		@Pc(2) LRUCache referenceCache = this.typeCache;
		synchronized (this.typeCache) {
			this.typeCache.reset();
		}
		referenceCache = this.modelsByShapeCache;
		synchronized (this.modelsByShapeCache) {
			this.modelsByShapeCache.reset();
		}
		referenceCache = this.modelAndShadowCache;
		synchronized (this.modelAndShadowCache) {
			this.modelAndShadowCache.reset();
		}
		referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.reset();
		}
	}

	@OriginalMember(owner = "client!uha", name = "b", descriptor = "(IZ)V")
	public void setCacheSize(@OriginalArg(0) int capacity) {
		this.typeCache = new LRUCache(capacity);
	}
	@OriginalMember(owner = "client!ce", name = "a", descriptor = "(IB)I")
	public static int getFileId(@OriginalArg(0) int id) {
		return id & 0xFF;
	}
	@OriginalMember(owner = "client!fv", name = "d", descriptor = "(II)I")
	public static int getGroupId(@OriginalArg(0) int value) {
		return value >>> 8;
	}
}
