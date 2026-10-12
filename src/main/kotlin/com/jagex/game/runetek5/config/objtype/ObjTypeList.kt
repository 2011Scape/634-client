package com.jagex.game.runetek5.config.objtype

import com.jagex.game.runetek5.ObjSpriteCacheKey
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.graphics.sprite.SpriteDrawQueue
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance
import com.jagex.game.runetek5.config.param.ParamType
import com.jagex.game.runetek5.config.param.ParamTypeList
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.item.ItemType
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.Find
import java.util.Locale

@OriginalClass("client!ub")
class ObjTypeList {

	@OriginalMember(owner = "client!ub", name = "A", descriptor = "I")
	@JvmField
	var featureMask: Int = 0

	@OriginalMember(owner = "client!ub", name = "n", descriptor = "Lclient!jr;")
	private val typeCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!ub", name = "w", descriptor = "Lclient!jr;")
	@JvmField
	val modelCache: LRUCache = LRUCache(50)

	@OriginalMember(owner = "client!ub", name = "x", descriptor = "Lclient!pq;")
	@JvmField
	val spriteCache: SpriteDrawQueue = SpriteDrawQueue(250)

	@OriginalMember(owner = "client!ub", name = "y", descriptor = "Lclient!nia;")
	private val spriteCacheKey: ObjSpriteCacheKey = ObjSpriteCacheKey()

	@OriginalMember(owner = "client!ub", name = "e", descriptor = "Z")
	private var allowMembers: Boolean = false

	@OriginalMember(owner = "client!ub", name = "B", descriptor = "Lclient!dc;")
	private val paramTypeList: ParamTypeList?

	@OriginalMember(owner = "client!ub", name = "d", descriptor = "Lclient!in;")
	@JvmField
	val meshResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!ub", name = "v", descriptor = "I")
	@JvmField
	val languageId: Int

	@OriginalMember(owner = "client!ub", name = "c", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider?

	@OriginalMember(owner = "client!ub", name = "g", descriptor = "I")
	@JvmField
	val num: Int

	@OriginalMember(owner = "client!ub", name = "z", descriptor = "[Ljava/lang/String;")
	private val defaultOps: Array<String?>

	@OriginalMember(owner = "client!ub", name = "C", descriptor = "[Ljava/lang/String;")
	private val defaultIops: Array<String?>

	@OriginalMember(owner = "client!ub", name = "<init>", descriptor = "(Lclient!sj;IZLclient!dc;Lclient!in;Lclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) allowMembers: Boolean, @OriginalArg(3) paramTypeList: ParamTypeList?, @OriginalArg(4) configClient: Js5ResourceProvider?, @OriginalArg(5) meshResource: Js5ResourceProvider?) {
		this.allowMembers = allowMembers
		this.paramTypeList = paramTypeList
		this.meshResource = meshResource
		this.languageId = languageId
		this.configClient = configClient
		if (this.configClient == null) {
			this.num = 0
		} else {
			@Pc(49) val lastGroupId: Int = this.configClient.getCapacity() - 1
			this.num = this.configClient.getGroupCapacity(lastGroupId) + lastGroupId * 256
		}
		this.defaultOps = arrayOf(null, null, LocalizedText.aClass351_9.get(this.languageId), null, null)
		this.defaultIops = arrayOf(null, null, null, null, LocalizedText.aClass351_10.get(this.languageId))
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(I)V")
	fun modelCacheReset() {
		@Pc(2) val lock: LRUCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.reset()
		}
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(Lclient!ha;IILclient!da;Lclient!oo;IZBLclient!ha;IZI)Lclient!mi;")
	fun getSprite(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) shadowColor: Int, @OriginalArg(2) amount: Int, @OriginalArg(3) font: AbstractFontRenderer?, @OriginalArg(4) playerModel: PlayerAppearance?, @OriginalArg(5) objNumMode: Int, @OriginalArg(6) useMainToolkit: Boolean, @OriginalArg(8) toolkit2: Toolkit?, @OriginalArg(9) objId: Int, @OriginalArg(10) zoomedIn: Boolean, @OriginalArg(11) outlineSize: Int): NativeSprite? {
		@Pc(16) val sprite: NativeSprite? = this.getCachedSprite(outlineSize, objId, amount, shadowColor, objNumMode, toolkit2, playerModel)
		if (sprite != null) {
			return sprite
		}
		@Pc(25) var objType: ItemType = this.list(objId)
		if (amount > 1 && objType.stackIds != null) {
			@Pc(33) var stackId: Int = -1
			@Pc(35) var i: Int = 0
			while (i < 10) {
				if (amount >= objType.stackTriggerAmount!![i] && objType.stackTriggerAmount!![i] != 0) {
					stackId = objType.stackIds!![i]
				}
				i++
			}
			if (stackId != -1) {
				objType = this.list(stackId)
			}
		}
		@Pc(93) val pixels: IntArray? = objType.getSpritePixels(amount, zoomedIn, shadowColor, toolkit2, toolkit, font, playerModel, objNumMode, outlineSize)
		if (pixels == null) {
			return null
		}
		@Pc(108) val sprite2: NativeSprite?
		if (useMainToolkit) {
			sprite2 = toolkit!!.createNativeSprite(36, pixels, 36, 32)
		} else {
			sprite2 = toolkit2!!.createNativeSprite(36, pixels, 36, 32)
		}
		if (!useMainToolkit) {
			@Pc(125) val objSpriteCacheKey: ObjSpriteCacheKey = ObjSpriteCacheKey()
			objSpriteCacheKey.shadowColor = shadowColor
			objSpriteCacheKey.objWearCol = playerModel != null
			objSpriteCacheKey.amount = amount
			objSpriteCacheKey.itemId = objId
			objSpriteCacheKey.numMode = objNumMode
			objSpriteCacheKey.index = toolkit2!!.index
			objSpriteCacheKey.outlineSize = outlineSize
			this.spriteCache.put(sprite2, objSpriteCacheKey)
		}
		return sprite2
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(BZ)V")
	fun setAllowMembers(@OriginalArg(1) allowMembers: Boolean) {
		if (this.allowMembers != allowMembers) {
			this.allowMembers = allowMembers
			this.cacheReset()
		}
	}

	@OriginalMember(owner = "client!ub", name = "b", descriptor = "(I)V")
	fun spriteCacheReset() {
		@Pc(6) val lock: SpriteDrawQueue = this.spriteCache
		synchronized(this.spriteCache) {
			this.spriteCache.reset()
		}
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(IZ)V")
	fun cacheClean() {
		@Pc(6) var referenceCache: LRUCache = this.typeCache
		synchronized(this.typeCache) {
			this.typeCache.clean(5)
		}
		referenceCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.clean(5)
		}
		@Pc(44) val keyedReferenceCache: SpriteDrawQueue = this.spriteCache
		synchronized(this.spriteCache) {
			this.spriteCache.processCycle()
		}
	}

	@OriginalMember(owner = "client!ub", name = "c", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(6) var lock: LRUCache = this.typeCache
		synchronized(this.typeCache) {
			this.typeCache.removeSoftReferences()
		}
		lock = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.removeSoftReferences()
		}
		@Pc(36) val spriteLock: SpriteDrawQueue = this.spriteCache
		synchronized(this.spriteCache) {
			this.spriteCache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!ub", name = "d", descriptor = "(I)V")
	fun cacheReset() {
		@Pc(2) var lock: LRUCache = this.typeCache
		synchronized(this.typeCache) {
			this.typeCache.reset()
		}
		lock = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.reset()
		}
		@Pc(42) val spriteLock: SpriteDrawQueue = this.spriteCache
		synchronized(this.spriteCache) {
			this.spriteCache.reset()
		}
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(II)Lclient!rq;")
	fun list(@OriginalArg(1) id: Int): ItemType {
		@Pc(8) val referenceCache: LRUCache = this.typeCache
		@Pc(18) var objType: ItemType?
		synchronized(this.typeCache) {
			objType = this.typeCache.get(id.toLong()) as ItemType?
		}
		if (objType != null) {
			return objType!!
		}
		@Pc(31) val configClient: Js5ResourceProvider? = this.configClient
		@Pc(44) var data: ByteArray?
		synchronized(this.configClient!!) {
			data = this.configClient.getFile(ObjTypeList.getGroupId(id), ObjTypeList.getFileId(id))
		}
		val newType: ItemType = ItemType()
		objType = newType
		newType.typeList = this
		newType.id = id
		newType.groundActions = arrayOf(null, null, LocalizedText.aClass351_9.get(this.languageId), null, null)
		newType.inventoryActions = arrayOf(null, null, null, null, LocalizedText.aClass351_10.get(this.languageId))
		if (data != null) {
			newType.decode(Packet(data))
		}
		newType.postDecode()
		if (newType.certTemplateId != -1) {
			newType.generateNote(this.list(newType.certId), this.list(newType.certTemplateId))
		}
		if (newType.lendTemplateId != -1) {
			newType.generateLend(this.list(newType.lendId), this.list(newType.lendTemplateId))
		}
		if (!this.allowMembers && newType.membersOnly) {
			newType.name = LocalizedText.MEMBERS_OBJECT.get(this.languageId)
			newType.teamId = 0
			newType.groundActions = this.defaultOps
			newType.inventoryActions = this.defaultIops
			newType.stockmarket = false
			newType.quests = null
			if (newType.parameters != null) {
				@Pc(205) var keptParam: Boolean = false
				@Pc(210) var node: Node? = newType.parameters!!.first()
				while (node != null) {
					@Pc(220) val paramType: ParamType = this.paramTypeList!!.list(node.key.toInt())!!
					if (paramType.autoDisable) {
						node.unlink()
					} else {
						keptParam = true
					}
					node = newType.parameters!!.next()
				}
				if (!keptParam) {
					newType.parameters = null
				}
			}
		}
		@Pc(245) val referenceCache2: LRUCache = this.typeCache
		synchronized(this.typeCache) {
			this.typeCache.put(newType, id.toLong())
			return newType
		}
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(IBIIIILclient!ha;Lclient!oo;)Lclient!mi;")
	fun getCachedSprite(@OriginalArg(0) outlineSize: Int, @OriginalArg(2) objId: Int, @OriginalArg(3) amount: Int, @OriginalArg(4) shadowColor: Int, @OriginalArg(5) objNumMode: Int, @OriginalArg(6) toolkit: Toolkit?, @OriginalArg(7) playerModel: PlayerAppearance?): NativeSprite? {
		this.spriteCacheKey.itemId = objId
		this.spriteCacheKey.amount = amount
		this.spriteCacheKey.index = toolkit!!.index
		this.spriteCacheKey.numMode = objNumMode
		this.spriteCacheKey.objWearCol = playerModel != null
		this.spriteCacheKey.shadowColor = shadowColor
		this.spriteCacheKey.outlineSize = outlineSize
		return this.spriteCache.get(this.spriteCacheKey) as NativeSprite?
	}

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(IB)V")
	fun setFeatureMask(@OriginalArg(0) featureMask: Int) {
		this.featureMask = featureMask
		@Pc(9) val lock: LRUCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.reset()
		}
	}

	companion object {
		@OriginalMember(owner = "client!tr", name = "a", descriptor = "(II)I")
		@JvmStatic
		fun getFileId(@OriginalArg(1) id: Int): Int {
			return id and 0xFF
		}

		@OriginalMember(owner = "client!qa", name = "a", descriptor = "(ZLjava/lang/String;IIZLjava/lang/String;Z)V")
		@JvmStatic
		fun queryItem(@OriginalArg(0) isStringParam: Boolean, @OriginalArg(1) string: String?, @OriginalArg(2) intValue: Int, @OriginalArg(3) paramId: Int, @OriginalArg(4) tradeableOnly: Boolean, @OriginalArg(5) string2: String?) {
			Resource.OBJ!!.loadType = 1
			@Pc(11) val string3: String = string2!!.lowercase(Locale.getDefault())
			@Pc(14) var results: ShortArray = ShortArray(16)
			@Pc(16) var defaultInt: Int = -1
			@Pc(18) var string4: String? = null
			if (paramId != -1) {
				@Pc(26) val paramType: ParamType? = TypeLists.PARAMS!!.list(paramId)
				if (paramType == null || isStringParam != paramType.isString()) {
					return
				}
				if (paramType.isString()) {
					string4 = paramType.typeName
				} else {
					defaultInt = paramType.defaultInt
				}
			}
			@Pc(55) var count: Int = 0
			@Pc(57) var i: Int = 0
			while (i < TypeLists.OBJ!!.num) {
				@Pc(69) val objType: ItemType = TypeLists.OBJ!!.list(i)
				if ((!tradeableOnly || objType.stockmarket) && objType.certTemplateId == -1 && objType.lendTemplateId == -1 && objType.dummyitem == 0 && objType.name!!.lowercase(Locale.getDefault()).indexOf(string3) != -1) {
					if (paramId != -1) {
						if (isStringParam) {
							if (!string!!.equals(objType.getStringParam(string4, paramId))) {
								i++
								continue
							}
						} else if (objType.getIntParam(defaultInt, paramId) != intValue) {
							i++
							continue
						}
					}
					if (count >= 250) {
						Find.queryResults = null
						Find.findResultCount = -1
						return
					}
					if (results.size <= count) {
						@Pc(138) val grown: ShortArray = ShortArray(results.size * 2)
						@Pc(140) var j: Int = 0
						while (j < count) {
							grown[j] = results[j]
							j++
						}
						results = grown
					}
					results[count++] = i.toShort()
				}
				i++
			}
			Find.queryResults = results
			Find.findResultCount = count
			MessageBox.queryResultsCursor = 0
			@Pc(174) val strings: Array<String?> = arrayOfNulls<String>(Find.findResultCount)
			@Pc(176) var k: Int = 0
			while (k < Find.findResultCount) {
				strings[k] = TypeLists.OBJ!!.list(results[k].toInt()).name
				k++
			}
			Utils.sortByStrings(Find.queryResults, strings)
			Resource.OBJ!!.discardUnpacked()
			Resource.OBJ!!.loadType = 2
		}

		@OriginalMember(owner = "client!jea", name = "a", descriptor = "(II)I")
		@JvmStatic
		fun getGroupId(@OriginalArg(1) value: Int): Int {
			return value ushr 8
		}
	}
}
