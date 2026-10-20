// The warnings of the original (deobfuscated) code, left as it was: the build treats warnings as errors.
@file:Suppress("SENSELESS_COMPARISON")

package com.jagex.game.runetek5.config.item

import com.jagex.game.runetek5.plugin.api.ItemDefinition

import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.cache.node.IntNode
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.StringNode
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.objtype.ObjNumMode
import com.jagex.game.runetek5.config.objtype.ObjStackability
import com.jagex.game.runetek5.config.objtype.ObjTypeList
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField
import withLock

@OriginalClass("client!rq")
class ItemType : ItemDefinition {

	/* Not part of the original client: the plugin API's view of an item type (plugin.api.ItemDefinition). */

	override fun id(): Int {
		return this.id
	}

	override fun name(): String? {
		return this.name
	}

	override fun isStackable(): Boolean {
		return this.stackable == 1
	}

	override fun isNoted(): Boolean {
		return this.certTemplateId != -1
	}

	override fun linkedId(): Int {
		return this.certId
	}

	override fun value(): Int {
		return this.value
	}

	override fun isMembers(): Boolean {
		return this.membersOnly
	}

	override fun isEquipable(): Boolean {
		return this.maleEquip1 != -1 || this.femaleEquip1 != -1
	}

	override fun inventoryAction(i: Int): String? {
		val actions: Array<String?> = this.inventoryActions ?: return null
		return if (i >= 0 && i < actions.size) actions[i] else null
	}

	override fun groundAction(i: Int): String? {
		val actions: Array<String?> = this.groundActions ?: return null
		return if (i >= 0 && i < actions.size) actions[i] else null
	}

	override fun teamId(): Int {
		return this.teamId
	}

	override fun intParam(key: Int, default: Int): Int {
		return this.getIntParam(default, key)
	}

	override fun stringParam(key: Int, default: String?): String? {
		return this.getStringParam(default, key)
	}

	@OriginalMember(owner = "client!rq", name = "f", descriptor = "I")
	private var modelId: Int = 0

	@OriginalMember(owner = "client!rq", name = "g", descriptor = "Lclient!eq;")
	@JvmField
	var parameters: IterableHashTable? = null

	@OriginalMember(owner = "client!rq", name = "k", descriptor = "Lclient!ub;")
	@JvmField
	var typeList: ObjTypeList? = null

	@OriginalMember(owner = "client!rq", name = "l", descriptor = "[I")
	@JvmField
	var stackIds: IntArray? = null

	@OriginalMember(owner = "client!rq", name = "m", descriptor = "[Ljava/lang/String;")
	@JvmField
	var inventoryActions: Array<String?>? = null

	@OriginalMember(owner = "client!rq", name = "s", descriptor = "I")
	@JvmField
	var id: Int = 0

	@OriginalMember(owner = "client!rq", name = "u", descriptor = "[S")
	private var recolorDst: ShortArray? = null

	@OriginalMember(owner = "client!rq", name = "v", descriptor = "[I")
	@JvmField
	var quests: IntArray? = null

	@OriginalMember(owner = "client!rq", name = "A", descriptor = "[S")
	private var recolorSrc: ShortArray? = null

	@OriginalMember(owner = "client!rq", name = "I", descriptor = "[S")
	private var retexSrc: ShortArray? = null

	@OriginalMember(owner = "client!rq", name = "Y", descriptor = "[S")
	private var retexDst: ShortArray? = null

	@OriginalMember(owner = "client!rq", name = "ib", descriptor = "[Ljava/lang/String;")
	@JvmField
	var groundActions: Array<String?>? = null

	@OriginalMember(owner = "client!rq", name = "sb", descriptor = "[B")
	private var recolorDPalette: ByteArray? = null

	@OriginalMember(owner = "client!rq", name = "Cb", descriptor = "[I")
	@JvmField
	var stackTriggerAmount: IntArray? = null

	@OriginalMember(owner = "client!rq", name = "b", descriptor = "I")
	@JvmField
	var primaryCursorActionIndex: Int = -1

	@OriginalMember(owner = "client!rq", name = "t", descriptor = "I")
	private var femaleEquip3: Int = -1

	@OriginalMember(owner = "client!rq", name = "p", descriptor = "I")
	@JvmField
	var customCursorOp1: Int = -1

	@OriginalMember(owner = "client!rq", name = "y", descriptor = "I")
	private var femaleHead2: Int = -1

	@OriginalMember(owner = "client!rq", name = "o", descriptor = "I")
	private var resizeZ: Int = 128

	@OriginalMember(owner = "client!rq", name = "B", descriptor = "I")
	@JvmField
	var lendId: Int = -1

	@OriginalMember(owner = "client!rq", name = "h", descriptor = "I")
	@JvmField
	var certId: Int = -1

	@OriginalMember(owner = "client!rq", name = "S", descriptor = "Ljava/lang/String;")
	@JvmField
	var name: String? = "null"

	@OriginalMember(owner = "client!rq", name = "R", descriptor = "I")
	private var resizeY: Int = 128

	@OriginalMember(owner = "client!rq", name = "W", descriptor = "I")
	@JvmField
	var dummyitem: Int = 0

	@OriginalMember(owner = "client!rq", name = "L", descriptor = "I")
	@JvmField
	var femaleEquip1: Int = -1

	@OriginalMember(owner = "client!rq", name = "H", descriptor = "I")
	@JvmField
	var pickSizeShift: Int = 0

	@OriginalMember(owner = "client!rq", name = "bb", descriptor = "I")
	private var maleEquip2: Int = -1

	@OriginalMember(owner = "client!rq", name = "P", descriptor = "I")
	private var maleHead1: Int = -1

	@OriginalMember(owner = "client!rq", name = "e", descriptor = "Z")
	@JvmField
	var stockmarket: Boolean = false

	@OriginalMember(owner = "client!rq", name = "K", descriptor = "I")
	@JvmField
	var modelRotationX: Int = 0

	@OriginalMember(owner = "client!rq", name = "jb", descriptor = "I")
	@JvmField
	var lendTemplateId: Int = -1

	@OriginalMember(owner = "client!rq", name = "hb", descriptor = "I")
	@JvmField
	var modelRotationZ: Int = 0

	@OriginalMember(owner = "client!rq", name = "q", descriptor = "I")
	private var maleHead2: Int = -1

	@OriginalMember(owner = "client!rq", name = "j", descriptor = "I")
	private var femaleEquip2: Int = -1

	@OriginalMember(owner = "client!rq", name = "ob", descriptor = "I")
	@JvmField
	var customCursorId2: Int = -1

	@OriginalMember(owner = "client!rq", name = "C", descriptor = "I")
	@JvmField
	var modelOffsetX: Int = 0

	@OriginalMember(owner = "client!rq", name = "J", descriptor = "I")
	private var resizeX: Int = 128

	@OriginalMember(owner = "client!rq", name = "fb", descriptor = "I")
	private var maleEquipmentOffsetZ: Int = 0

	@OriginalMember(owner = "client!rq", name = "ab", descriptor = "I")
	private var femaleEquipmentOffsetX: Int = 0

	@OriginalMember(owner = "client!rq", name = "tb", descriptor = "I")
	private var femaleHead1: Int = -1

	@OriginalMember(owner = "client!rq", name = "mb", descriptor = "I")
	@JvmField
	var maleEquip1: Int = -1

	@OriginalMember(owner = "client!rq", name = "ub", descriptor = "I")
	private var femaleEquipmentOffsetZ: Int = 0

	@OriginalMember(owner = "client!rq", name = "pb", descriptor = "I")
	@JvmField
	var customCursorId1: Int = -1

	@OriginalMember(owner = "client!rq", name = "E", descriptor = "I")
	@JvmField
	var modelRotationY: Int = 0

	@OriginalMember(owner = "client!rq", name = "x", descriptor = "I")
	@JvmField
	var customCursorOp2: Int = -1

	@OriginalMember(owner = "client!rq", name = "xb", descriptor = "I")
	@JvmField
	var modelOffsetY: Int = 0

	@OriginalMember(owner = "client!rq", name = "eb", descriptor = "I")
	private var maleEquipmentOffsetX: Int = 0

	@OriginalMember(owner = "client!rq", name = "O", descriptor = "I")
	private var ambient: Int = 0

	@OriginalMember(owner = "client!rq", name = "cb", descriptor = "I")
	private var maleEquip3: Int = -1

	@OriginalMember(owner = "client!rq", name = "n", descriptor = "I")
	@JvmField
	var secondaryCursorActionIndex: Int = -1

	@OriginalMember(owner = "client!rq", name = "rb", descriptor = "I")
	@JvmField
	var stackable: Int = 0

	@OriginalMember(owner = "client!rq", name = "i", descriptor = "I")
	@JvmField
	var primaryCursor: Int = -1

	@OriginalMember(owner = "client!rq", name = "Z", descriptor = "I")
	@JvmField
	var multiStackSize: Int = -1

	@OriginalMember(owner = "client!rq", name = "U", descriptor = "I")
	private var maleEquipmentOffsetY: Int = 0

	@OriginalMember(owner = "client!rq", name = "Bb", descriptor = "I")
	@JvmField
	var secondaryCursor: Int = -1

	@OriginalMember(owner = "client!rq", name = "yb", descriptor = "I")
	@JvmField
	var teamId: Int = 0

	@OriginalMember(owner = "client!rq", name = "vb", descriptor = "I")
	private var contrast: Int = 0

	@OriginalMember(owner = "client!rq", name = "qb", descriptor = "I")
	@JvmField
	var value: Int = 1

	@OriginalMember(owner = "client!rq", name = "c", descriptor = "I")
	private var femaleEquipmentOffsetY: Int = 0

	@OriginalMember(owner = "client!rq", name = "wb", descriptor = "I")
	@JvmField
	var modelZoom: Int = 2000

	@OriginalMember(owner = "client!rq", name = "Eb", descriptor = "I")
	@JvmField
	var certTemplateId: Int = -1

	@OriginalMember(owner = "client!rq", name = "G", descriptor = "Z")
	@JvmField
	var membersOnly: Boolean = false

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(ZI)Lclient!bh;")
	fun getHeadMesh(@OriginalArg(0) female: Boolean): Mesh? {
		@Pc(8) var maleEquip1: Int = this.maleHead1
		@Pc(11) var maleEquipmentOffsetX: Int = this.maleHead2
		if (female) {
			maleEquipmentOffsetX = this.femaleHead2
			maleEquip1 = this.femaleHead1
		}
		if (maleEquip1 == -1) {
			return null
		}
		@Pc(33) var mesh: Mesh? = Mesh.fromResource(this.typeList!!.meshResource, maleEquip1)
		if (mesh!!.version < 13) {
			mesh.upscale()
		}
		if (maleEquipmentOffsetX != -1) {
			@Pc(55) val mesh2: Mesh? = Mesh.fromResource(this.typeList!!.meshResource, maleEquipmentOffsetX)
			if (mesh2!!.version < 13) {
				mesh2.upscale()
			}
			@Pc(78) val meshes: Array<Mesh?> = arrayOf(mesh, mesh2)
			mesh = Mesh(meshes, 2)
		}
		@Pc(89) var index: Int
		if (this.recolorSrc != null) {
			index = 0
			while (index < this.recolorSrc!!.size) {
				mesh.recolor(this.recolorSrc!![index], this.recolorDst!![index])
				index++
			}
		}
		if (this.retexSrc != null) {
			index = 0
			while (index < this.retexSrc!!.size) {
				mesh.retexture(this.retexSrc!![index], this.retexDst!![index])
				index++
			}
		}
		return mesh
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(BZ)Z")
	fun isHeadCached(@OriginalArg(1) female: Boolean): Boolean {
		@Pc(6) var maleEquip1: Int = this.maleHead1
		@Pc(17) var maleEquipmentOffsetX: Int = this.maleHead2
		if (female) {
			maleEquip1 = this.femaleHead1
			maleEquipmentOffsetX = this.femaleHead2
		}
		if (maleEquip1 == -1) {
			return true
		}
		@Pc(33) var cached: Boolean = true
		if (!this.typeList!!.meshResource!!.isFileReady(maleEquip1, 0)) {
			cached = false
		}
		if (maleEquipmentOffsetX != -1 && !this.typeList!!.meshResource!!.isFileReady(maleEquipmentOffsetX, 0)) {
			cached = false
		}
		return cached
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(Lclient!rq;BLclient!rq;)V")
	fun generateLend(@OriginalArg(0) objType: ItemType?, @OriginalArg(2) objType2: ItemType?) {
		this.maleEquipmentOffsetZ = objType!!.maleEquipmentOffsetZ
		this.modelOffsetX = objType2!!.modelOffsetX
		this.value = 0
		this.retexSrc = objType.retexSrc
		this.femaleEquip1 = objType.femaleEquip1
		this.modelRotationY = objType2.modelRotationY
		this.groundActions = objType.groundActions
		this.maleEquipmentOffsetY = objType.maleEquipmentOffsetY
		this.recolorDPalette = objType.recolorDPalette
		this.recolorDst = objType.recolorDst
		this.name = objType.name
		this.modelId = objType2.modelId
		this.femaleEquip3 = objType.femaleEquip3
		this.recolorSrc = objType.recolorSrc
		this.maleEquip1 = objType.maleEquip1
		this.femaleHead1 = objType.femaleHead1
		this.maleEquip2 = objType.maleEquip2
		this.modelZoom = objType2.modelZoom
		this.maleHead2 = objType.maleHead2
		this.retexDst = objType.retexDst
		this.membersOnly = objType.membersOnly
		this.modelRotationZ = objType2.modelRotationZ
		this.inventoryActions = arrayOfNulls<String>(5)
		this.femaleEquipmentOffsetZ = objType.femaleEquipmentOffsetZ
		this.femaleHead2 = objType.femaleHead2
		this.maleEquipmentOffsetX = objType.maleEquipmentOffsetX
		this.maleHead1 = objType.maleHead1
		this.parameters = objType.parameters
		this.maleEquip3 = objType.maleEquip3
		this.teamId = objType.teamId
		this.modelRotationX = objType2.modelRotationX
		this.femaleEquip2 = objType.femaleEquip2
		this.femaleEquipmentOffsetY = objType.femaleEquipmentOffsetY
		this.femaleEquipmentOffsetX = objType.femaleEquipmentOffsetX
		this.modelOffsetY = objType2.modelOffsetY
		if (objType.inventoryActions != null) {
			@Pc(157) var i: Int = 0
			while (i < 4) {
				this.inventoryActions!![i] = objType.inventoryActions!![i]
				i++
			}
		}
		this.inventoryActions!![4] = LocalizedText.DISCARD.get(this.typeList!!.languageId)
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(II)Ljava/lang/String;")
	private fun formatAmount(@OriginalArg(0) amount: Int): String {
		if (amount < 100000) {
			return "<col=ffff00>" + amount + "</col>"
		} else if (amount < 10000000) {
			return "<col=ffffff>" + amount / 1000 + LocalizedText.aClass351_38.get(this.typeList!!.languageId) + "</col>"
		} else {
			return "<col=00ff80>" + amount / 1000000 + LocalizedText.aClass351_36.get(this.typeList!!.languageId) + "</col>"
		}
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(ZZ)Lclient!bh;")
	fun getBodyMesh(@OriginalArg(0) female: Boolean): Mesh? {
		@Pc(8) var primaryModel: Int = this.maleEquip1
		@Pc(11) var secondaryModel: Int = this.maleEquip2
		@Pc(20) var tertiaryModel: Int = this.maleEquip3
		if (female) {
			primaryModel = this.femaleEquip1
			tertiaryModel = this.femaleEquip3
			secondaryModel = this.femaleEquip2
		}
		if (primaryModel == -1) {
			return null
		}
		@Pc(44) var primaryMesh: Mesh? = Mesh.fromResource(this.typeList!!.meshResource, primaryModel)
		if (primaryMesh!!.version < 13) {
			primaryMesh.upscale()
		}
		if (secondaryModel != -1) {
			@Pc(66) val secondaryMesh: Mesh? = Mesh.fromResource(this.typeList!!.meshResource, secondaryModel)
			if (secondaryMesh!!.version < 13) {
				secondaryMesh.upscale()
			}
			if (tertiaryModel == -1) {
				@Pc(129) val meshes: Array<Mesh?> = arrayOf(primaryMesh, secondaryMesh)
				primaryMesh = Mesh(meshes, 2)
			} else {
				@Pc(87) val tertiaryMesh: Mesh? = Mesh.fromResource(this.typeList!!.meshResource, tertiaryModel)
				if (tertiaryMesh!!.version < 13) {
					tertiaryMesh.upscale()
				}
				@Pc(110) val meshesToCombine: Array<Mesh?> = arrayOf(primaryMesh, secondaryMesh, tertiaryMesh)
				primaryMesh = Mesh(meshesToCombine, 3)
			}
		}
		if (primaryMesh == null) {
			return null
		}
		if (!female && (this.maleEquipmentOffsetX != 0 || this.maleEquipmentOffsetY != 0 || this.maleEquipmentOffsetZ != 0)) {
			primaryMesh.translate(this.maleEquipmentOffsetZ, this.maleEquipmentOffsetX, this.maleEquipmentOffsetY)
		}
		if (female && (this.femaleEquipmentOffsetX != 0 || this.femaleEquipmentOffsetY != 0 || this.femaleEquipmentOffsetZ != 0)) {
			primaryMesh.translate(this.femaleEquipmentOffsetZ, this.femaleEquipmentOffsetX, this.femaleEquipmentOffsetY)
		}
		@Pc(196) var index: Int
		if (this.recolorSrc != null) {
			index = 0
			while (index < this.recolorSrc!!.size) {
				primaryMesh.recolor(this.recolorSrc!![index], this.recolorDst!![index])
				index++
			}
		}
		if (this.retexSrc != null) {
			index = 0
			while (index < this.retexSrc!!.size) {
				primaryMesh.retexture(this.retexSrc!![index], this.retexDst!![index])
				index++
			}
		}
		return primaryMesh
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(Lclient!oo;Lclient!hg;Lclient!ha;IIIIBI)Lclient!ka;")
	fun getAnimatedModel(@OriginalArg(0) playerModel: PlayerAppearance?, @OriginalArg(1) seqType: SeqType?, @OriginalArg(2) toolkit: Toolkit?, @OriginalArg(3) functionMask: Int, @OriginalArg(4) currentFrameDuration: Int, @OriginalArg(5) count: Int, @OriginalArg(6) currentFrameId: Int, @OriginalArg(8) nextFrameId: Int): Model? {
		@Pc(15) var local15: Int
		if (this.stackIds != null && count > 1) {
			var stackId: Int = -1
			@Pc(17) var i: Int = 0
			while (i < 10) {
				if (count >= this.stackTriggerAmount!![i] && this.stackTriggerAmount!![i] != 0) {
					stackId = this.stackIds!![i]
				}
				i++
			}
			if (stackId != -1) {
				return this.typeList!!.list(stackId).getAnimatedModel(playerModel, seqType, toolkit, functionMask, currentFrameDuration, 1, currentFrameId, nextFrameId)
			}
		}
		var combinedFunctionMask: Int = functionMask
		if (seqType != null) {
			combinedFunctionMask = functionMask or seqType.getFunctionMask(nextFrameId, currentFrameId, true)
		}
		@Pc(84) val referenceCache: LRUCache = this.typeList!!.modelCache
		@Pc(101) var model: Model?
		withLock(this.typeList!!.modelCache) {
			model = this.typeList!!.modelCache.get(((toolkit!!.index shl 29) or this.id).toLong()) as Model?
		}
		if (model == null || toolkit!!.compareFunctionMasks(model!!.ua(), combinedFunctionMask) != 0) {
			if (model != null) {
				combinedFunctionMask = toolkit!!.combineFunctionMasks(combinedFunctionMask, model!!.ua())
			}
			@Pc(128) var modelFunctionMask: Int = combinedFunctionMask
			if (this.retexSrc != null) {
				modelFunctionMask = combinedFunctionMask or 0x8000
			}
			if (this.recolorSrc != null || playerModel != null) {
				modelFunctionMask = modelFunctionMask or 0x4000
			}
			if (this.resizeX != 128) {
				modelFunctionMask = modelFunctionMask or 0x1
			}
			if (this.resizeX != 128) {
				modelFunctionMask = modelFunctionMask or 0x2
			}
			if (this.resizeX != 128) {
				modelFunctionMask = modelFunctionMask or 0x4
			}
			@Pc(181) val mesh: Mesh? = Mesh.fromResource(this.typeList!!.meshResource, this.modelId)
			if (mesh == null) {
				return null
			}
			if (mesh.version < 13) {
				mesh.upscale()
			}
			val newModel: Model = toolkit!!.createModel(mesh, modelFunctionMask, this.typeList!!.featureMask, this.ambient + 64, 850 - -this.contrast)!!
			model = newModel
			if (this.resizeX != 128 || this.resizeY != 128 || this.resizeZ != 128) {
				newModel.O(this.resizeX, this.resizeY, this.resizeZ)
			}
			@Pc(239) var index: Int
			if (this.recolorSrc != null) {
				index = 0
				while (index < this.recolorSrc!!.size) {
					if (this.recolorDPalette == null || index >= this.recolorDPalette!!.size) {
						newModel.ia(this.recolorSrc!![index], this.recolorDst!![index])
					} else {
						newModel.ia(this.recolorSrc!![index], ItemType.recolorPalette[this.recolorDPalette!![index].toInt() and 0xFF])
					}
					index++
				}
			}
			if (this.retexSrc != null) {
				index = 0
				while (index < this.retexSrc!!.size) {
					newModel.aa(this.retexSrc!![index], this.retexDst!![index])
					index++
				}
			}
			if (playerModel != null) {
				index = 0
				while (index < 5) {
					@Pc(320) var j: Int = 0
					while (j < PlayerAppearance.recolorPaletteDst!!.size) {
						if (playerModel.recolorDstIndices!![index] < PlayerAppearance.recolorPaletteDst!![j]!![index]!!.size) {
							newModel.ia(PlayerAppearance.recolorPaletteSrc!![j]!![index], PlayerAppearance.recolorPaletteDst!![j]!![index]!![playerModel.recolorDstIndices!![index]])
						}
						j++
					}
					index++
				}
			}
			newModel.s(combinedFunctionMask)
			@Pc(368) val referenceCache2: LRUCache = this.typeList!!.modelCache
			withLock(this.typeList!!.modelCache) {
				this.typeList!!.modelCache.put(newModel, ((toolkit.index shl 29) or this.id).toLong())
			}
		}
		if (seqType != null) {
			model = seqType.animateModel(model, nextFrameId, currentFrameDuration, combinedFunctionMask, currentFrameId)
		}
		model!!.s(functionMask)
		return model
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(IB)Lclient!rq;")
	fun getStackVariant(@OriginalArg(0) stackSize: Int): ItemType {
		if (this.stackIds != null && stackSize > 1) {
			@Pc(21) var stackId: Int = -1
			@Pc(23) var i: Int = 0
			while (i < 10) {
				if (stackSize >= this.stackTriggerAmount!![i] && this.stackTriggerAmount!![i] != 0) {
					stackId = this.stackIds!![i]
				}
				i++
			}
			if (stackId != -1) {
				return this.typeList!!.list(stackId)
			}
		}
		return this
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(Ljava/lang/String;II)Ljava/lang/String;")
	fun getStringParam(@OriginalArg(0) string: String?, @OriginalArg(1) paramKey: Int): String? {
		if (this.parameters == null) {
			return string
		} else {
			@Pc(25) val stringNode: StringNode? = this.parameters!!.get(paramKey.toLong()) as StringNode?
			return if (stringNode == null) string else stringNode.value
		}
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(IZILclient!ha;Lclient!ha;Lclient!da;Lclient!oo;IBI)[I")
	fun getSpritePixels(@OriginalArg(0) amount: Int, @OriginalArg(1) zoomedIn: Boolean, @OriginalArg(2) shadowColor: Int, @OriginalArg(3) toolkit: Toolkit?, @OriginalArg(4) toolkit2: Toolkit?, @OriginalArg(5) font: AbstractFontRenderer?, @OriginalArg(6) playerModel: PlayerAppearance?, @OriginalArg(7) numMode: Int, @OriginalArg(9) outlineSize: Int): IntArray? {
		@Pc(16) val mesh: Mesh? = Mesh.fromResource(this.typeList!!.meshResource, this.modelId)
		if (mesh == null) {
			return null
		}
		if (mesh.version < 13) {
			mesh.upscale()
		}
		@Pc(35) var index: Int
		if (this.recolorSrc != null) {
			index = 0
			while (index < this.recolorSrc!!.size) {
				if (this.recolorDPalette == null || index >= this.recolorDPalette!!.size) {
					mesh.recolor(this.recolorSrc!![index], this.recolorDst!![index])
				} else {
					mesh.recolor(this.recolorSrc!![index], ItemType.recolorPalette[this.recolorDPalette!![index].toInt() and 0xFF])
				}
				index++
			}
		}
		if (this.retexSrc != null) {
			index = 0
			while (index < this.retexSrc!!.size) {
				mesh.retexture(this.retexSrc!![index], this.retexDst!![index])
				index++
			}
		}
		if (playerModel != null) {
			index = 0
			while (index < 5) {
				@Pc(119) var i: Int = 0
				while (i < PlayerAppearance.recolorPaletteDst!!.size) {
					if (PlayerAppearance.recolorPaletteDst!![i]!![index]!!.size > playerModel.recolorDstIndices!![index]) {
						mesh.recolor(PlayerAppearance.recolorPaletteSrc!![i]!![index], PlayerAppearance.recolorPaletteDst!![i]!![index]!![playerModel.recolorDstIndices!![index]])
					}
					i++
				}
				index++
			}
		}
		@Pc(161) var functionMask: Short = 2048
		@Pc(163) var resized: Boolean = false
		if (this.resizeX != 128 || this.resizeY != 128 || this.resizeZ != 128) {
			functionMask = 2055
			resized = true
		}
		@Pc(197) val model: Model = toolkit2!!.createModel(mesh, functionMask.toInt(), 64, this.ambient + 64, this.contrast + 768)!!
		if (!model.loadedTextures()) {
			return null
		}
		if (resized) {
			model.O(this.resizeX, this.resizeY, this.resizeZ)
		}
		@Pc(214) var sprite: NativeSprite? = null
		if (this.certTemplateId != -1) {
			sprite = this.typeList!!.getSprite(toolkit2, 0, 10, font, playerModel, ObjNumMode.SHOWCOUNT_NEVER, true, toolkit, this.certId, true, 1)
			if (sprite == null) {
				return null
			}
		} else if (this.lendTemplateId != -1) {
			sprite = this.typeList!!.getSprite(toolkit2, shadowColor, amount, font, playerModel, ObjNumMode.SHOWCOUNT_NEVER, true, toolkit, this.lendId, false, outlineSize)
			if (sprite == null) {
				return null
			}
		}
		@Pc(281) val zoom: Int
		if (zoomedIn) {
			zoom = (this.modelZoom.toDouble() * 1.5).toInt() shl 2
		} else if (outlineSize == 2) {
			zoom = (this.modelZoom.toDouble() * 1.04).toInt() shl 2
		} else {
			zoom = this.modelZoom shl 2
		}
		toolkit2.DA(16, 16, 512, 512)
		@Pc(308) val matrix: Matrix = toolkit2.createMatrix()!!
		matrix.makeIdentity()
		toolkit2.setCamera(matrix)
		toolkit2.xa(1.0f)
		toolkit2.ZA(16777215, 1.0f, 1.0f, -50.0f, -10.0f, -50.0f)
		@Pc(327) val matrix2: Matrix = toolkit2.scratchMatrix()!!
		matrix2.makeRotationZ(-this.modelRotationZ shl 3)
		matrix2.rotateAxisY(this.modelRotationY shl 3)
		matrix2.translate(this.modelOffsetX shl 2, ((zoom * TextureCombineSource.SINE[this.modelRotationX shl 3]) shr 14) + (this.modelOffsetY shl 2) - model.fa() / 2, ((zoom * TextureCombineSource.COSINE[this.modelRotationX shl 3]) shr 14) - -(this.modelOffsetY shl 2))
		matrix2.rotateAxisX(this.modelRotationX shl 3)
		@Pc(391) val zNear: Int = toolkit2.i()
		@Pc(394) val zFar: Int = toolkit2.XA()
		toolkit2.f(50, Int.MAX_VALUE)
		toolkit2.ya()
		toolkit2.la()
		toolkit2.aa(0, 0, 36, 32, 0, 0)
		model.render(matrix2, null, 1)
		toolkit2.f(zNear, zFar)
		@Pc(426) var pixels: IntArray? = toolkit2.na(0, 0, 36, 32)
		if (outlineSize >= 1) {
			pixels = this.outline(-16777214, pixels)
			if (outlineSize >= 2) {
				pixels = this.outline(-1, pixels)
			}
		}
		if (shadowColor != 0) {
			this.shadow(shadowColor, pixels)
		}
		toolkit2.createNativeSprite(36, pixels, 36, 32)!!.render(0, 0)
		if (this.certTemplateId != -1) {
			sprite!!.render(0, 0)
		} else if (this.lendTemplateId != -1) {
			sprite!!.render(0, 0)
		}
		if (numMode == ObjNumMode.SHOWCOUNT_ALWAYS || numMode == ObjNumMode.SHOWCOUNT_IFNOT1 && (this.stackable == ObjStackability.ALWAYS || amount != 1) && amount != -1) {
			font!!.drawString(this.formatAmount(amount), -256, 9, 0, -16777215)
		}
		pixels = toolkit2.na(0, 0, 36, 32)
		@Pc(533) var j: Int = 0
		while (j < pixels!!.size) {
			if ((pixels[j] and 0xFFFFFF) == 0) {
				pixels[j] = 0
			} else {
				pixels[j] = pixels[j] or 0xFF000000.toInt()
			}
			j++
		}
		return pixels
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(B)V")
	fun postDecode() {
	}

	@OriginalMember(owner = "client!rq", name = "b", descriptor = "(ZI)Z")
	fun isBodyCached(@OriginalArg(0) female: Boolean): Boolean {
		@Pc(6) var primaryModel: Int = this.maleEquip1
		@Pc(9) var certId: Int = this.maleEquip2
		@Pc(12) var tertiaryModel: Int = this.maleEquip3
		if (female) {
			tertiaryModel = this.femaleEquip3
			primaryModel = this.femaleEquip1
			certId = this.femaleEquip2
		}
		if (primaryModel == -1) {
			return true
		}
		@Pc(30) var cached: Boolean = true
		if (!this.typeList!!.meshResource!!.isFileReady(primaryModel, 0)) {
			cached = false
		}
		if (certId != -1 && !this.typeList!!.meshResource!!.isFileReady(certId, 0)) {
			cached = false
		}
		if (tertiaryModel != -1 && !this.typeList!!.meshResource!!.isFileReady(tertiaryModel, 0)) {
			cached = false
		}
		return cached
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(IILclient!cea;)V")
	private fun decode(@OriginalArg(1) opcode: Int, @OriginalArg(2) packet: Packet?) {
		if (opcode == 1) {
			this.modelId = packet!!.readUShort()
		} else if (opcode == 2) {
			this.name = packet!!.readString()
		} else if (opcode == 4) {
			this.modelZoom = packet!!.readUShort()
		} else if (opcode == 5) {
			this.modelRotationX = packet!!.readUShort()
		} else if (opcode == 6) {
			this.modelRotationY = packet!!.readUShort()
		} else if (opcode == 7) {
			this.modelOffsetX = packet!!.readUShort()
			if (this.modelOffsetX > 32767) {
				this.modelOffsetX -= 65536
			}
		} else if (opcode == 8) {
			this.modelOffsetY = packet!!.readUShort()
			if (this.modelOffsetY > 32767) {
				this.modelOffsetY -= 65536
			}
		} else if (opcode == 11) {
			this.stackable = 1
		} else if (opcode == 12) {
			this.value = packet!!.readInt()
		} else if (opcode == 16) {
			this.membersOnly = true
		} else if (opcode == 18) {
			this.multiStackSize = packet!!.readUShort()
		} else if (opcode == 23) {
			this.maleEquip1 = packet!!.readUShort()
		} else if (opcode == 24) {
			this.maleEquip2 = packet!!.readUShort()
		} else if (opcode == 25) {
			this.femaleEquip1 = packet!!.readUShort()
		} else if (opcode == 26) {
			this.femaleEquip2 = packet!!.readUShort()
		} else if (opcode >= 30 && opcode < 35) {
			this.groundActions!![opcode - 30] = packet!!.readString()
		} else if (opcode >= 35 && opcode < 40) {
			this.inventoryActions!![opcode - 35] = packet!!.readString()
		} else {
			@Pc(191) var count: Int
			@Pc(201) var local201: Int
			if (opcode == 40) {
				count = packet!!.readUByte()
				this.recolorSrc = ShortArray(count)
				this.recolorDst = ShortArray(count)
				var recolorIndex: Int = 0
				while (recolorIndex < count) {
					this.recolorSrc!![recolorIndex] = packet.readUShort().toShort()
					this.recolorDst!![recolorIndex] = packet.readUShort().toShort()
					recolorIndex++
				}
			} else if (opcode == 41) {
				count = packet!!.readUByte()
				this.retexDst = ShortArray(count)
				this.retexSrc = ShortArray(count)
				var retexIndex: Int = 0
				while (retexIndex < count) {
					this.retexSrc!![retexIndex] = packet.readUShort().toShort()
					this.retexDst!![retexIndex] = packet.readUShort().toShort()
					retexIndex++
				}
			} else if (opcode == 42) {
				count = packet!!.readUByte()
				this.recolorDPalette = ByteArray(count)
				var paletteIndex: Int = 0
				while (paletteIndex < count) {
					this.recolorDPalette!![paletteIndex] = packet.readByte()
					paletteIndex++
				}
			} else if (opcode == 65) {
				this.stockmarket = true
			} else if (opcode == 78) {
				this.maleEquip3 = packet!!.readUShort()
			} else if (opcode == 79) {
				this.femaleEquip3 = packet!!.readUShort()
			} else if (opcode == 90) {
				this.maleHead1 = packet!!.readUShort()
			} else if (opcode == 91) {
				this.femaleHead1 = packet!!.readUShort()
			} else if (opcode == 92) {
				this.maleHead2 = packet!!.readUShort()
			} else if (opcode == 93) {
				this.femaleHead2 = packet!!.readUShort()
			} else if (opcode == 95) {
				this.modelRotationZ = packet!!.readUShort()
			} else if (opcode == 96) {
				this.dummyitem = packet!!.readUByte()
			} else if (opcode == 97) {
				this.certId = packet!!.readUShort()
			} else if (opcode == 98) {
				this.certTemplateId = packet!!.readUShort()
			} else if (opcode >= 100 && opcode < 110) {
				if (this.stackIds == null) {
					this.stackTriggerAmount = IntArray(10)
					this.stackIds = IntArray(10)
				}
				this.stackIds!![opcode - 100] = packet!!.readUShort()
				this.stackTriggerAmount!![opcode - 100] = packet.readUShort()
			} else if (opcode == 110) {
				this.resizeX = packet!!.readUShort()
			} else if (opcode == 111) {
				this.resizeY = packet!!.readUShort()
			} else if (opcode == 112) {
				this.resizeZ = packet!!.readUShort()
			} else if (opcode == 113) {
				this.ambient = packet!!.readByte().toInt()
			} else if (opcode == 114) {
				this.contrast = packet!!.readByte().toInt() * 5
			} else if (opcode == 115) {
				this.teamId = packet!!.readUByte()
			} else if (opcode == 121) {
				this.lendId = packet!!.readUShort()
			} else if (opcode == 122) {
				this.lendTemplateId = packet!!.readUShort()
			} else if (opcode == 125) {
				this.maleEquipmentOffsetX = packet!!.readByte().toInt() shl 2
				this.maleEquipmentOffsetY = packet.readByte().toInt() shl 2
				this.maleEquipmentOffsetZ = packet.readByte().toInt() shl 2
			} else if (opcode == 126) {
				this.femaleEquipmentOffsetX = packet!!.readByte().toInt() shl 2
				this.femaleEquipmentOffsetY = packet.readByte().toInt() shl 2
				this.femaleEquipmentOffsetZ = packet.readByte().toInt() shl 2
			} else if (opcode == 127) {
				this.primaryCursorActionIndex = packet!!.readUByte()
				this.primaryCursor = packet.readUShort()
			} else if (opcode == 128) {
				this.secondaryCursorActionIndex = packet!!.readUByte()
				this.secondaryCursor = packet.readUShort()
			} else if (opcode == 129) {
				this.customCursorOp1 = packet!!.readUByte()
				this.customCursorId1 = packet.readUShort()
			} else if (opcode == 130) {
				this.customCursorOp2 = packet!!.readUByte()
				this.customCursorId2 = packet.readUShort()
			} else if (opcode == 132) {
				count = packet!!.readUByte()
				this.quests = IntArray(count)
				var questIndex: Int = 0
				while (questIndex < count) {
					this.quests!![questIndex] = packet.readUShort()
					questIndex++
				}
			} else if (opcode == 134) {
				this.pickSizeShift = packet!!.readUByte()
			} else if (opcode == 249) {
				count = packet!!.readUByte()
				if (this.parameters == null) {
					val tableSize: Int = Utils.nextPowerOfTwo(count)
					this.parameters = IterableHashTable(tableSize)
				}
				var paramIndex: Int = 0
				while (paramIndex < count) {
					@Pc(580) val isString: Boolean = packet.readUByte() == 1
					@Pc(584) val key: Int = packet.readUMedium()
					@Pc(593) val node: Node
					if (isString) {
						node = StringNode(packet.readString())
					} else {
						node = IntNode(packet.readInt())
					}
					this.parameters!!.put(key.toLong(), node)
					paramIndex++
				}
			}
		}
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(III)I")
	fun getIntParam(@OriginalArg(0) defaultInt: Int, @OriginalArg(2) paramKey: Int): Int {
		if (this.parameters == null) {
			return defaultInt
		} else {
			@Pc(16) val intNode: IntNode? = this.parameters!!.get(paramKey.toLong()) as IntNode?
			return if (intNode == null) defaultInt else intNode.value
		}
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(ILclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet?) {
		while (true) {
			@Pc(15) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(opcode, packet)
		}
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(ILclient!rq;Lclient!rq;)V")
	fun generateNote(@OriginalArg(1) objType: ItemType?, @OriginalArg(2) objType2: ItemType?) {
		this.recolorDst = objType2!!.recolorDst
		this.retexSrc = objType2.retexSrc
		this.recolorDPalette = objType2.recolorDPalette
		this.membersOnly = objType!!.membersOnly
		this.modelRotationX = objType2.modelRotationX
		this.modelRotationZ = objType2.modelRotationZ
		this.modelOffsetX = objType2.modelOffsetX
		this.retexDst = objType2.retexDst
		this.modelRotationY = objType2.modelRotationY
		this.value = objType.value
		this.modelOffsetY = objType2.modelOffsetY
		this.stackable = 1
		this.recolorSrc = objType2.recolorSrc
		this.modelZoom = objType2.modelZoom
		this.name = objType.name
		this.modelId = objType2.modelId
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(I[IB)V")
	private fun shadow(@OriginalArg(0) color: Int, @OriginalArg(1) pixels: IntArray?) {
		@Pc(9) var i: Int = 31
		while (i > 0) {
			@Pc(15) val rowOffset: Int = i * 36
			@Pc(17) var j: Int = 35
			while (j > 0) {
				if (pixels!![j + rowOffset] == 0 && pixels[j + rowOffset - 1 - 36] != 0) {
					pixels[rowOffset + j] = color
				}
				j--
			}
			i--
		}
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(II[I)[I")
	private fun outline(@OriginalArg(0) outlineColor: Int, @OriginalArg(2) pixels: IntArray?): IntArray {
		@Pc(13) val outlinedPixels: IntArray = IntArray(1152)
		@Pc(15) var pixelIndex: Int = 0
		@Pc(17) var i: Int = 0
		while (i < 32) {
			@Pc(21) var j: Int = 0
			while (j < 36) {
				@Pc(27) var pixel: Int = pixels!![pixelIndex]
				if (pixel == 0) {
					if (j > 0 && pixels[pixelIndex - 1] != 0) {
						pixel = outlineColor
					} else if (i > 0 && pixels[pixelIndex - 36] != 0) {
						pixel = outlineColor
					} else if (j < 35 && pixels[pixelIndex + 1] != 0) {
						pixel = outlineColor
					} else if (i < 31 && pixels[pixelIndex + 36] != 0) {
						pixel = outlineColor
					}
				}
				outlinedPixels[pixelIndex++] = pixel
				j++
			}
			i++
		}
		return outlinedPixels
	}

	companion object {
		@OriginalMember(owner = "client!dt", name = "c", descriptor = "[S")
		@JvmField
		var recolorPalette: ShortArray = ShortArray(256)
	}
}
