package com.jagex.game.runetek5.config.objtype;

import com.jagex.game.runetek5.ObjSpriteCacheKey;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.graphics.sprite.SpriteDrawQueue;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance;
import com.jagex.game.runetek5.config.param.ParamType;
import com.jagex.game.runetek5.config.param.ParamTypeList;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.item.ItemType;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.rendering.MessageBox;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.Find;

@OriginalClass("client!ub")
public final class ObjTypeList {

	@OriginalMember(owner = "client!ub", name = "A", descriptor = "I")
	public int featureMask;

	@OriginalMember(owner = "client!ub", name = "n", descriptor = "Lclient!jr;")
	private final LRUCache typeCache = new LRUCache(64);

	@OriginalMember(owner = "client!ub", name = "w", descriptor = "Lclient!jr;")
	public final LRUCache modelCache = new LRUCache(50);

	@OriginalMember(owner = "client!ub", name = "x", descriptor = "Lclient!pq;")
	public final SpriteDrawQueue spriteCache = new SpriteDrawQueue(250);

	@OriginalMember(owner = "client!ub", name = "y", descriptor = "Lclient!nia;")
	private final ObjSpriteCacheKey spriteCacheKey = new ObjSpriteCacheKey();

	@OriginalMember(owner = "client!ub", name = "e", descriptor = "Z")
	private boolean allowMembers;

	@OriginalMember(owner = "client!ub", name = "B", descriptor = "Lclient!dc;")
	private final ParamTypeList paramTypeList;

	@OriginalMember(owner = "client!ub", name = "d", descriptor = "Lclient!in;")
	public final Js5ResourceProvider meshResource;

	@OriginalMember(owner = "client!ub", name = "v", descriptor = "I")
	public final int languageId;

	@OriginalMember(owner = "client!ub", name = "c", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	@OriginalMember(owner = "client!ub", name = "g", descriptor = "I")
	public final int num;

	@OriginalMember(owner = "client!ub", name = "z", descriptor = "[Ljava/lang/String;")
	private final String[] defaultOps;

	@OriginalMember(owner = "client!ub", name = "C", descriptor = "[Ljava/lang/String;")
	private final String[] defaultIops;

	@OriginalMember(owner = "client!ub", name = "<init>", descriptor = "(Lclient!sj;IZLclient!dc;Lclient!in;Lclient!in;)V")
	public ObjTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) boolean allowMembers, @OriginalArg(3) ParamTypeList paramTypeList, @OriginalArg(4) Js5ResourceProvider configClient, @OriginalArg(5) Js5ResourceProvider meshResource) {
		this.allowMembers = allowMembers;
		this.paramTypeList = paramTypeList;
		this.meshResource = meshResource;
		this.languageId = languageId;
		this.configClient = configClient;
		if (this.configClient == null) {
			this.num = 0;
		} else {
			@Pc(49) int lastGroupId = this.configClient.getCapacity() - 1;
			this.num = this.configClient.getGroupCapacity(lastGroupId) + lastGroupId * 256;
		}
		this.defaultOps = new String[] { null, null, LocalizedText.aClass351_9.get(this.languageId), null, null };
		this.defaultIops = new String[] { null, null, null, null, LocalizedText.aClass351_10.get(this.languageId) };
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(I)V")
	public void modelCacheReset() {
		@Pc(2) LRUCache lock = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.reset();
		}
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(Lclient!ha;IILclient!da;Lclient!oo;IZBLclient!ha;IZI)Lclient!mi;")
	public NativeSprite getSprite(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int shadowColor, @OriginalArg(2) int amount, @OriginalArg(3) AbstractFontRenderer font, @OriginalArg(4) PlayerAppearance playerModel, @OriginalArg(5) int objNumMode, @OriginalArg(6) boolean useMainToolkit, @OriginalArg(8) Toolkit toolkit2, @OriginalArg(9) int objId, @OriginalArg(10) boolean zoomedIn, @OriginalArg(11) int outlineSize) {
		@Pc(16) NativeSprite sprite = this.getCachedSprite(outlineSize, objId, amount, shadowColor, objNumMode, toolkit2, playerModel);
		if (sprite != null) {
			return sprite;
		}
		@Pc(25) ItemType objType = this.list(objId);
		if (amount > 1 && objType.stackIds != null) {
			@Pc(33) int stackId = -1;
			for (@Pc(35) int i = 0; i < 10; i++) {
				if (amount >= objType.stackTriggerAmount[i] && objType.stackTriggerAmount[i] != 0) {
					stackId = objType.stackIds[i];
				}
			}
			if (stackId != -1) {
				objType = this.list(stackId);
			}
		}
		@Pc(93) int[] pixels = objType.getSpritePixels(amount, zoomedIn, shadowColor, toolkit2, toolkit, font, playerModel, objNumMode, outlineSize);
		if (pixels == null) {
			return null;
		}
		@Pc(108) NativeSprite sprite2;
		if (useMainToolkit) {
			sprite2 = toolkit.createNativeSprite(36, pixels, 36, 32);
		} else {
			sprite2 = toolkit2.createNativeSprite(36, pixels, 36, 32);
		}
		if (!useMainToolkit) {
			@Pc(125) ObjSpriteCacheKey objSpriteCacheKey = new ObjSpriteCacheKey();
			objSpriteCacheKey.shadowColor = shadowColor;
			objSpriteCacheKey.objWearCol = playerModel != null;
			objSpriteCacheKey.amount = amount;
			objSpriteCacheKey.itemId = objId;
			objSpriteCacheKey.numMode = objNumMode;
			objSpriteCacheKey.index = toolkit2.index;
			objSpriteCacheKey.outlineSize = outlineSize;
			this.spriteCache.put(sprite2, objSpriteCacheKey);
		}
		return sprite2;
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(BZ)V")
	public void setAllowMembers(@OriginalArg(1) boolean allowMembers) {
		if (this.allowMembers != allowMembers) {
			this.allowMembers = allowMembers;
			this.cacheReset();
		}
	}

	@OriginalMember(owner = "client!ub", name = "b", descriptor = "(I)V")
	public void spriteCacheReset() {
		@Pc(6) SpriteDrawQueue lock = this.spriteCache;
		synchronized (this.spriteCache) {
			this.spriteCache.reset();
		}
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(IZ)V")
	public void cacheClean() {
		@Pc(6) LRUCache referenceCache = this.typeCache;
		synchronized (this.typeCache) {
			this.typeCache.clean(5);
		}
		referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.clean(5);
		}
		@Pc(44) SpriteDrawQueue keyedReferenceCache = this.spriteCache;
		synchronized (this.spriteCache) {
			this.spriteCache.processCycle();
		}
	}

	@OriginalMember(owner = "client!ub", name = "c", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(6) LRUCache lock = this.typeCache;
		synchronized (this.typeCache) {
			this.typeCache.removeSoftReferences();
		}
		lock = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.removeSoftReferences();
		}
		@Pc(36) SpriteDrawQueue spriteLock = this.spriteCache;
		synchronized (this.spriteCache) {
			this.spriteCache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!ub", name = "d", descriptor = "(I)V")
	public void cacheReset() {
		@Pc(2) LRUCache lock = this.typeCache;
		synchronized (this.typeCache) {
			this.typeCache.reset();
		}
		lock = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.reset();
		}
		@Pc(42) SpriteDrawQueue spriteLock = this.spriteCache;
		synchronized (this.spriteCache) {
			this.spriteCache.reset();
		}
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(II)Lclient!rq;")
	public ItemType list(@OriginalArg(1) int id) {
		@Pc(8) LRUCache referenceCache = this.typeCache;
		@Pc(18) ItemType objType;
		synchronized (this.typeCache) {
			objType = (ItemType) this.typeCache.get((long) id);
		}
		if (objType != null) {
			return objType;
		}
		@Pc(31) Js5ResourceProvider configClient = this.configClient;
		@Pc(44) byte[] data;
		synchronized (this.configClient) {
			data = this.configClient.getFile(ObjTypeList.getGroupId(id), ObjTypeList.getFileId(id));
		}
		objType = new ItemType();
		objType.typeList = this;
		objType.id = id;
		objType.groundActions = new String[] { null, null, LocalizedText.aClass351_9.get(this.languageId), null, null };
		objType.inventoryActions = new String[] { null, null, null, null, LocalizedText.aClass351_10.get(this.languageId) };
		if (data != null) {
			objType.decode(new Packet(data));
		}
		objType.postDecode();
		if (objType.certTemplateId != -1) {
			objType.generateNote(this.list(objType.certId), this.list(objType.certTemplateId));
		}
		if (objType.lendTemplateId != -1) {
			objType.generateLend(this.list(objType.lendId), this.list(objType.lendTemplateId));
		}
		if (!this.allowMembers && objType.membersOnly) {
			objType.name = LocalizedText.MEMBERS_OBJECT.get(this.languageId);
			objType.teamId = 0;
			objType.groundActions = this.defaultOps;
			objType.inventoryActions = this.defaultIops;
			objType.stockmarket = false;
			objType.quests = null;
			if (objType.parameters != null) {
				@Pc(205) boolean keptParam = false;
				for (@Pc(210) Node node = objType.parameters.first(); node != null; node = objType.parameters.next()) {
					@Pc(220) ParamType paramType = this.paramTypeList.list((int) node.key);
					if (paramType.autoDisable) {
						node.unlink();
					} else {
						keptParam = true;
					}
				}
				if (!keptParam) {
					objType.parameters = null;
				}
			}
		}
		@Pc(245) LRUCache referenceCache2 = this.typeCache;
		synchronized (this.typeCache) {
			this.typeCache.put(objType, (long) id);
			return objType;
		}
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(IBIIIILclient!ha;Lclient!oo;)Lclient!mi;")
	public NativeSprite getCachedSprite(@OriginalArg(0) int outlineSize, @OriginalArg(2) int objId, @OriginalArg(3) int amount, @OriginalArg(4) int shadowColor, @OriginalArg(5) int objNumMode, @OriginalArg(6) Toolkit toolkit, @OriginalArg(7) PlayerAppearance playerModel) {
		this.spriteCacheKey.itemId = objId;
		this.spriteCacheKey.amount = amount;
		this.spriteCacheKey.index = toolkit.index;
		this.spriteCacheKey.numMode = objNumMode;
		this.spriteCacheKey.objWearCol = playerModel != null;
		this.spriteCacheKey.shadowColor = shadowColor;
		this.spriteCacheKey.outlineSize = outlineSize;
		return (NativeSprite) this.spriteCache.get(this.spriteCacheKey);
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(IB)V")
	public void setFeatureMask(@OriginalArg(0) int featureMask) {
		this.featureMask = featureMask;
		@Pc(9) LRUCache lock = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.reset();
		}
	}
	@OriginalMember(owner = "client!tr", name = "a", descriptor = "(II)I")
	public static int getFileId(@OriginalArg(1) int id) {
		return id & 0xFF;
	}
	@OriginalMember(owner = "client!qa", name = "a", descriptor = "(ZLjava/lang/String;IIZLjava/lang/String;Z)V")
	public static void queryItem(@OriginalArg(0) boolean isStringParam, @OriginalArg(1) String string, @OriginalArg(2) int intValue, @OriginalArg(3) int paramId, @OriginalArg(4) boolean tradeableOnly, @OriginalArg(5) String string2) {
		Resource.OBJ.loadType = 1;
		@Pc(11) String string3 = string2.toLowerCase();
		@Pc(14) short[] results = new short[16];
		@Pc(16) int defaultInt = -1;
		@Pc(18) String string4 = null;
		if (paramId != -1) {
			@Pc(26) ParamType paramType = TypeLists.PARAMS.list(paramId);
			if (paramType == null || isStringParam != paramType.isString()) {
				return;
			}
			if (paramType.isString()) {
				string4 = paramType.typeName;
			} else {
				defaultInt = paramType.defaultInt;
			}
		}
		@Pc(55) int count = 0;
		for (@Pc(57) int i = 0; i < TypeLists.OBJ.num; i++) {
			@Pc(69) ItemType objType = TypeLists.OBJ.list(i);
			if ((!tradeableOnly || objType.stockmarket) && objType.certTemplateId == -1 && objType.lendTemplateId == -1 && objType.dummyitem == 0 && objType.name.toLowerCase().indexOf(string3) != -1) {
				if (paramId != -1) {
					if (isStringParam) {
						if (!string.equals(objType.getStringParam(string4, paramId))) {
							continue;
						}
					} else if (objType.getIntParam(defaultInt, paramId) != intValue) {
						continue;
					}
				}
				if (count >= 250) {
					Find.queryResults = null;
					Find.findResultCount = -1;
					return;
				}
				if (results.length <= count) {
					@Pc(138) short[] grown = new short[results.length * 2];
					for (@Pc(140) int j = 0; j < count; j++) {
						grown[j] = results[j];
					}
					results = grown;
				}
				results[count++] = (short) i;
			}
		}
		Find.queryResults = results;
		Find.findResultCount = count;
		MessageBox.queryResultsCursor = 0;
		@Pc(174) String[] strings = new String[Find.findResultCount];
		for (@Pc(176) int k = 0; k < Find.findResultCount; k++) {
			strings[k] = TypeLists.OBJ.list(results[k]).name;
		}
		Utils.sortByStrings(Find.queryResults, strings);
		Resource.OBJ.discardUnpacked();
		Resource.OBJ.loadType = 2;
	}

	@OriginalMember(owner = "client!jea", name = "a", descriptor = "(II)I")
	public static int getGroupId(@OriginalArg(1) int value) {
		return value >>> 8;
	}
}
