package com.jagex.game.runetek5.config.rsinterface.itemcontainer

import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance
import com.jagex.game.runetek5.config.bas.BasType
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpDiagonalWaves
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.config.sequence.frameset.FrameSet
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.config.item.ItemType
import com.jagex.game.runetek5.cache.node.lru.LRUCache

@OriginalClass("client!ie")
class ItemContainer : Node() {

	@OriginalMember(owner = "client!ie", name = "r", descriptor = "[I")
	@JvmField
	var itemIds: IntArray? = intArrayOf(-1)

	@OriginalMember(owner = "client!ie", name = "s", descriptor = "[I")
	@JvmField
	var amounts: IntArray? = IntArray(1)

	@OriginalMember(owner = "client!ie", name = "a", descriptor = "([IIZ[IB)J")
	private fun calculateHash(@OriginalArg(0) recolorDstIndices: IntArray?, @OriginalArg(1) basTypeId: Int, @OriginalArg(2) female: Boolean, @OriginalArg(3) items: IntArray?): Long {
		@Pc(7) val crc64Table: LongArray = TextureOpDiagonalWaves.CRC64_TABLE
		@Pc(9) var hash: Long = -1L
		@Pc(25) val partialHash: Long = crc64Table[((hash xor (basTypeId shr 8).toLong()) and 0xFFL).toInt()] xor (hash ushr 8)
		hash = crc64Table[((partialHash xor basTypeId.toLong()) and 0xFFL).toInt()] xor (partialHash ushr 8)
		@Pc(41) var i: Int = 0
		while (i < items!!.size) {
			hash = crc64Table[((hash xor (items[i] shr 24).toLong()) and 0xFFL).toInt()] xor (hash ushr 8)
			hash = (hash ushr 8) xor crc64Table[((hash xor (items[i] shr 16).toLong()) and 0xFFL).toInt()]
			hash = (hash ushr 8) xor crc64Table[((hash xor (items[i] shr 8).toLong()) and 0xFFL).toInt()]
			hash = crc64Table[((items[i].toLong() xor hash) and 0xFFL).toInt()] xor (hash ushr 8)
			i++
		}
		if (recolorDstIndices != null) {
			@Pc(132) var j: Int = 0
			while (j < 5) {
				hash = (hash ushr 8) xor crc64Table[((hash xor recolorDstIndices[j].toLong()) and 0xFFL).toInt()]
				j++
			}
		}
		return crc64Table[(((if (female) 1 else 0).toLong() xor hash) and 0xFFL).toInt()] xor (hash ushr 8)
	}

	@OriginalMember(owner = "client!ie", name = "a", descriptor = "(Lclient!hg;Lclient!oo;IIIIILclient!ha;ZI)Lclient!ka;")
	fun getAnimatedBodyModel(@OriginalArg(0) seqType: SeqType?, @OriginalArg(1) playerModel: PlayerAppearance?, @OriginalArg(2) basTypeId: Int, @OriginalArg(3) currentFrameId: Int, @OriginalArg(4) currentFrameDuration: Int, @OriginalArg(5) nextFrameId: Int, @OriginalArg(7) toolkit: Toolkit?, @OriginalArg(8) female: Boolean): Model? {
		@Pc(7) var model: Model? = null
		@Pc(9) var functionMask: Int = 2048
		@Pc(11) var basType: BasType? = null
		if (basTypeId != -1) {
			basType = TypeLists.BAS!!.list(basTypeId)
		}
		@Pc(22) var items: IntArray? = this.itemIds
		if (basType != null && basType.objVisibility != null) {
			items = IntArray(basType.objVisibility!!.size)
			@Pc(34) var i: Int = 0
			while (i < basType.objVisibility!!.size) {
				@Pc(41) val visibleSlot: Int = basType.objVisibility!![i]
				if (visibleSlot >= 0 && this.itemIds!!.size > visibleSlot) {
					items[i] = this.itemIds!![visibleSlot]
				} else {
					items[i] = -1
				}
				i++
			}
		}
		@Pc(80) var modifiesAlpha: Boolean = false
		@Pc(82) var modifiesColor: Boolean = false
		@Pc(84) var usesLighting: Boolean = false
		@Pc(86) var modifiesBillboard: Boolean = false
		@Pc(88) var frame: Int = -1
		@Pc(90) var nextFrame: Int = -1
		@Pc(92) var frameDuration: Int = 0
		@Pc(94) var animFrameset: FrameSet? = null
		@Pc(96) var animFrameset2: FrameSet? = null
		if (seqType != null) {
			frame = seqType.frames!![currentFrameId]
			functionMask = 2080
			@Pc(111) val frameSetId: Int = frame ushr 16
			animFrameset = TypeLists.SEQ!!.listFrameSet(frameSetId)
			frame = frame and 0xFFFF
			if (animFrameset != null) {
				modifiesColor = animFrameset.modifiesColors(frame) or false
				modifiesAlpha = animFrameset.modifiesAlpha(frame) or false
				modifiesBillboard = animFrameset.modifiesBillboards(frame) or false
				usesLighting = seqType.lights or false
			}
			if ((seqType.tweened || SeqType.forceTweening) && nextFrameId != -1 && seqType.frames!!.size > nextFrameId) {
				nextFrame = seqType.frames!![nextFrameId]
				frameDuration = seqType.durations!![currentFrameId]
				@Pc(177) val nextFrameSetId: Int = nextFrame ushr 16
				nextFrame = nextFrame and 0xFFFF
				if (frameSetId == nextFrameSetId) {
					animFrameset2 = animFrameset
				} else {
					animFrameset2 = TypeLists.SEQ!!.listFrameSet(nextFrame ushr 16)
				}
				if (animFrameset2 != null) {
					modifiesColor = modifiesColor or animFrameset2.modifiesColors(nextFrame)
					modifiesAlpha = modifiesAlpha or animFrameset2.modifiesAlpha(nextFrame)
					modifiesBillboard = modifiesBillboard or animFrameset2.modifiesBillboards(nextFrame)
				}
			}
			if (modifiesColor) {
				functionMask = 2208
			}
			if (modifiesAlpha) {
				functionMask = functionMask or 0x100
			}
			if (usesLighting) {
				functionMask = functionMask or 0x200
			}
			if (modifiesBillboard) {
				functionMask = functionMask or 0x400
			}
		}
		@Pc(258) val hash: Long = this.calculateHash(if (playerModel == null) null else playerModel.recolorDstIndices, basTypeId, female, items)
		if (ItemContainer.aClass169_51 != null) {
			model = ItemContainer.aClass169_51.get(hash) as Model?
		}
		if (model == null || toolkit!!.compareFunctionMasks(model.ua(), functionMask) != 0) {
			if (model != null) {
				functionMask = toolkit!!.combineFunctionMasks(functionMask, model.ua())
			}
			@Pc(286) var combinedFunctionMask: Int = functionMask
			@Pc(288) var modelsMissing: Boolean = false
			@Pc(290) var j: Int = 0
			while (j < items!!.size) {
				if (items[j] != -1 && !TypeLists.OBJ!!.list(items[j]).isBodyCached(female)) {
					modelsMissing = true
				}
				j++
			}
			if (modelsMissing) {
				return null
			}
			@Pc(322) val meshes: Array<Mesh?> = arrayOfNulls<Mesh>(items.size)
			@Pc(324) var k: Int = 0
			while (k < items.size) {
				if (items[k] != -1) {
					meshes[k] = TypeLists.OBJ!!.list(items[k]).getBodyMesh(female)
				}
				k++
			}
			@Pc(382) var local382: Int
			@Pc(389) var local389: Int
			if (basType != null && basType.equipmentTransforms != null) {
				@Pc(362) var m: Int = 0
				while (m < basType.equipmentTransforms!!.size) {
					if (basType.equipmentTransforms!![m] != null && meshes[m] != null) {
						val translateX: Int = basType.equipmentTransforms!![m]!![0]
						val translateY: Int = basType.equipmentTransforms!![m]!![1]
						@Pc(396) val translateZ: Int = basType.equipmentTransforms!![m]!![2]
						@Pc(403) val rotateX: Int = basType.equipmentTransforms!![m]!![3]
						@Pc(410) val rotateY: Int = basType.equipmentTransforms!![m]!![4]
						@Pc(417) val rotateZ: Int = basType.equipmentTransforms!![m]!![5]
						if (rotateX != 0 || rotateY != 0 || rotateZ != 0) {
							meshes[m]!!.rotateWithoutNormals(rotateY, rotateZ, rotateX)
						}
						if (translateX != 0 || translateY != 0 || translateZ != 0) {
							meshes[m]!!.translate(translateZ, translateX, translateY)
						}
					}
					m++
				}
			}
			if (playerModel != null) {
				combinedFunctionMask = functionMask or 0x4000
			}
			@Pc(474) val mesh: Mesh = Mesh(meshes, meshes.size)
			model = toolkit!!.createModel(mesh, combinedFunctionMask, ItemContainer.modelFeatureMask, 64, 850)
			if (playerModel != null) {
				var partIndex: Int = 0
				while (partIndex < 5) {
					var paletteIndex: Int = 0
					while (paletteIndex < PlayerAppearance.recolorPaletteDst!!.size) {
						if (PlayerAppearance.recolorPaletteDst!![paletteIndex]!![partIndex]!!.size > playerModel.recolorDstIndices!![partIndex]) {
							model!!.ia(PlayerAppearance.recolorPaletteSrc!![paletteIndex]!![partIndex], PlayerAppearance.recolorPaletteDst!![paletteIndex]!![partIndex]!![playerModel.recolorDstIndices!![partIndex]])
						}
						paletteIndex++
					}
					partIndex++
				}
			}
			if (ItemContainer.aClass169_51 != null) {
				model!!.s(functionMask)
				ItemContainer.aClass169_51.put(model, hash)
			}
		}
		if (seqType == null || animFrameset == null) {
			return model
		} else {
			@Pc(555) val model2: Model? = model!!.copy(1.toByte(), functionMask, true)
			model2!!.animateModelFrame(frame, frameDuration, animFrameset2, 0, animFrameset, seqType.lights, nextFrame, currentFrameDuration - 1)
			return model2
		}
	}

	companion object {
		@OriginalMember(owner = "client!tf", name = "D", descriptor = "Lclient!eq;")
		@JvmField
		val CONTAINER_MAP: IterableHashTable = IterableHashTable(32)

		@OriginalMember(owner = "client!hga", name = "a", descriptor = "(ZII)V")
		@JvmStatic
		fun deleteItemContainer(@OriginalArg(0) negativeKey: Boolean, @OriginalArg(1) containerId: Int) {
			@Pc(12) val itemContainer: ItemContainer? = ItemContainer.getContainer(containerId, negativeKey)
			if (itemContainer != null) {
				itemContainer.unlink()
			}
		}

		@OriginalMember(owner = "client!afa", name = "a", descriptor = "(ZIII)I")
		@JvmStatic
		fun getItemIdAtSlot(@OriginalArg(0) otherPlayer: Boolean, @OriginalArg(1) inventoryId: Int, @OriginalArg(2) slot: Int): Int {
			@Pc(13) val container: ItemContainer? = ItemContainer.getContainer(inventoryId, otherPlayer)
			if (container == null) {
				return -1
			} else if (slot >= 0 && container.itemIds!!.size > slot) {
				return container.itemIds!![slot]
			} else {
				return -1
			}
		}

		@OriginalMember(owner = "client!qga", name = "a", descriptor = "(IZZ)V")
		@JvmStatic
		fun resetContainer(@OriginalArg(0) containerId: Int, @OriginalArg(1) negativeKey: Boolean) {
			@Pc(10) val container: ItemContainer? = ItemContainer.getContainer(containerId, negativeKey)
			if (container != null) {
				@Pc(19) var i: Int = 0
				while (i < container.itemIds!!.size) {
					container.itemIds!![i] = -1
					container.amounts!![i] = 0
					i++
				}
			}
		}

		@OriginalMember(owner = "client!vg", name = "a", descriptor = "(IIZIII)V")
		@JvmStatic
		fun setItemInContainer(@OriginalArg(0) containerId: Int, @OriginalArg(2) isNegativeKey: Boolean, @OriginalArg(3) slot: Int, @OriginalArg(4) amount: Int, @OriginalArg(5) itemId: Int) {
			@Pc(19) val key: Long = (containerId or (if (isNegativeKey) Int.MIN_VALUE else 0)).toLong()
			@Pc(25) var container: ItemContainer? = ItemContainer.CONTAINER_MAP.get(key) as ItemContainer?
			if (container == null) {
				container = ItemContainer()
				ItemContainer.CONTAINER_MAP.put(key, container)
			}
			if (container.itemIds!!.size <= slot) {
				@Pc(46) val itemIds: IntArray = IntArray(slot + 1)
				@Pc(51) val itemAmounts: IntArray = IntArray(slot + 1)
				@Pc(53) var i: Int = 0
				while (i < container.itemIds!!.size) {
					itemIds[i] = container.itemIds!![i]
					itemAmounts[i] = container.amounts!![i]
					i++
				}
				@Pc(83) var length: Int = container.itemIds!!.size
				while (length < slot) {
					itemIds[length] = -1
					itemAmounts[length] = 0
					length++
				}
				container.itemIds = itemIds
				container.amounts = itemAmounts
			}
			container.itemIds!![slot] = itemId
			container.amounts!![slot] = amount
		}

		@OriginalMember(owner = "client!wha", name = "a", descriptor = "(BIZ)Lclient!ie;")
		@JvmStatic
		fun getContainer(@OriginalArg(1) containerId: Int, @OriginalArg(2) negativeKey: Boolean): ItemContainer? {
			@Pc(20) val key: Long = (containerId or (if (negativeKey) Int.MIN_VALUE else 0)).toLong()
			return ItemContainer.CONTAINER_MAP.get(key) as ItemContainer?
		}

		@OriginalMember(owner = "client!jl", name = "a", descriptor = "(IIZI)I")
		@JvmStatic
		fun getInventoryTotal(@OriginalArg(0) itemId: Int, @OriginalArg(1) inventoryId: Int, @OriginalArg(2) otherPlayer: Boolean): Int {
			@Pc(10) val container: ItemContainer? = ItemContainer.getContainer(inventoryId, otherPlayer)
			if (container == null) {
				return 0
			} else if (itemId == -1) {
				return 0
			} else {
				@Pc(21) var total: Int = 0
				@Pc(23) var i: Int = 0
				while (i < container.amounts!!.size) {
					if (itemId == container.itemIds!![i]) {
						total += container.amounts!![i]
					}
					i++
				}
				return total
			}
		}

		@OriginalMember(owner = "client!jv", name = "a", descriptor = "(ZBZII)I")
		@JvmStatic
		fun getInventoryParamTotal(@OriginalArg(0) multiplyByAmount: Boolean, @OriginalArg(3) inventoryId: Int, @OriginalArg(4) paramId: Int): Int {
			@Pc(15) val container: ItemContainer? = ItemContainer.getContainer(inventoryId, false)
			if (container == null) {
				return 0
			}
			@Pc(21) var total: Int = 0
			@Pc(23) var i: Int = 0
			while (i < container.itemIds!!.size) {
				if (container.itemIds!![i] >= 0 && container.itemIds!![i] < TypeLists.OBJ!!.num) {
					@Pc(52) val objType: ItemType? = TypeLists.OBJ!!.list(container.itemIds!![i])
					@Pc(62) val paramValue: Int = objType!!.getIntParam(TypeLists.PARAMS!!.list(paramId)!!.defaultInt, paramId)
					if (multiplyByAmount) {
						total += paramValue * container.amounts!![i]
					} else {
						total += paramValue
					}
				}
				i++
			}
			return total
		}

		@OriginalMember(owner = "client!mfa", name = "d", descriptor = "(II)V")
		@JvmStatic
		fun setFeatureMask(@OriginalArg(1) featureMask: Int) {
			ItemContainer.modelFeatureMask = featureMask
			ItemContainer.aClass169_51.reset()
		}

		@OriginalMember(owner = "client!ta", name = "a", descriptor = "(IIZI)I")
		@JvmStatic
		fun getItemAmountAtSlot(@OriginalArg(1) inventoryId: Int, @OriginalArg(2) otherPlayer: Boolean, @OriginalArg(3) slot: Int): Int {
			@Pc(16) val container: ItemContainer? = ItemContainer.getContainer(inventoryId, otherPlayer)
			if (container == null) {
				return 0
			} else if (slot >= 0 && slot < container.amounts!!.size) {
				return container.amounts!![slot]
			} else {
				return 0
			}
		}

		@OriginalMember(owner = "client!fga", name = "b", descriptor = "(I)V")
		@JvmStatic
		fun clearContainers() {
			ItemContainer.CONTAINER_MAP.clear()
		}

		@OriginalMember(owner = "client!wt", name = "a", descriptor = "(BIZ)I")
		@JvmStatic
		fun getInventoryFreeSpace(@OriginalArg(1) inventoryId: Int): Int {
			@Pc(14) val container: ItemContainer? = ItemContainer.getContainer(inventoryId, false)
			if (container == null) {
				return TypeLists.OP_MAPELEMENT2!!.list(inventoryId)!!.size
			}
			@Pc(24) var freeSlots: Int = 0
			@Pc(26) var i: Int = 0
			while (i < container.itemIds!!.size) {
				if (container.itemIds!![i] == -1) {
					freeSlots++
				}
				i++
			}
			return freeSlots + TypeLists.OP_MAPELEMENT2!!.list(inventoryId)!!.size - container.itemIds!!.size
		}

		@OriginalMember(owner = "client!bda", name = "w", descriptor = "I")
		@JvmField
		var modelFeatureMask: Int = 0

		@OriginalMember(owner = "client!re", name = "z", descriptor = "Lclient!jr;")
		@JvmField
		val aClass169_51: LRUCache = LRUCache(10)

		@OriginalMember(owner = "client!rc", name = "j", descriptor = "[I")
		@JvmField
		val RECENTLY_USED_ITEM_CONTAINERS: IntArray = IntArray(32)

		@OriginalMember(owner = "client!cm", name = "f", descriptor = "(I)V")
		@JvmStatic
		fun resetItemContainerModelCache() {
			ItemContainer.aClass169_51.reset()
		}
	}
}
