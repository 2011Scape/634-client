package com.jagex.game.runetek5.config.rsinterface.itemcontainer;

import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance;
import com.jagex.game.runetek5.config.bas.BasType;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpDiagonalWaves;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.config.sequence.frameset.FrameSet;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.config.item.ItemType;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;

@OriginalClass("client!ie")
public final class ItemContainer extends Node {

	@OriginalMember(owner = "client!ie", name = "r", descriptor = "[I")
	public int[] itemIds = new int[] { -1 };

	@OriginalMember(owner = "client!ie", name = "s", descriptor = "[I")
	public int[] amounts = new int[1];

	@OriginalMember(owner = "client!ie", name = "a", descriptor = "([IIZ[IB)J")
	private long calculateHash(@OriginalArg(0) int[] recolorDstIndices, @OriginalArg(1) int basTypeId, @OriginalArg(2) boolean female, @OriginalArg(3) int[] items) {
		@Pc(7) long[] crc64Table = TextureOpDiagonalWaves.CRC64_TABLE;
		@Pc(9) long hash = -1L;
		@Pc(25) long partialHash = crc64Table[(int) ((hash ^ (long) (basTypeId >> 8)) & 0xFFL)] ^ hash >>> 8;
		hash = crc64Table[(int) ((partialHash ^ (long) basTypeId) & 0xFFL)] ^ partialHash >>> 8;
		for (@Pc(41) int i = 0; i < items.length; i++) {
			hash = crc64Table[(int) ((hash ^ (long) (items[i] >> 24)) & 0xFFL)] ^ hash >>> 8;
			hash = hash >>> 8 ^ crc64Table[(int) ((hash ^ (long) (items[i] >> 16)) & 0xFFL)];
			hash = hash >>> 8 ^ crc64Table[(int) ((hash ^ (long) (items[i] >> 8)) & 0xFFL)];
			hash = crc64Table[(int) (((long) items[i] ^ hash) & 0xFFL)] ^ hash >>> 8;
		}
		if (recolorDstIndices != null) {
			for (@Pc(132) int j = 0; j < 5; j++) {
				hash = hash >>> 8 ^ crc64Table[(int) ((hash ^ (long) recolorDstIndices[j]) & 0xFFL)];
			}
		}
		return crc64Table[(int) (((long) (female ? 1 : 0) ^ hash) & 0xFFL)] ^ hash >>> 8;
	}

	@OriginalMember(owner = "client!ie", name = "a", descriptor = "(Lclient!hg;Lclient!oo;IIIIILclient!ha;ZI)Lclient!ka;")
	public Model getAnimatedBodyModel(@OriginalArg(0) SeqType seqType, @OriginalArg(1) PlayerAppearance playerModel, @OriginalArg(2) int basTypeId, @OriginalArg(3) int currentFrameId, @OriginalArg(4) int currentFrameDuration, @OriginalArg(5) int nextFrameId, @OriginalArg(7) Toolkit toolkit, @OriginalArg(8) boolean female) {
		@Pc(7) Model model = null;
		@Pc(9) int functionMask = 2048;
		@Pc(11) BasType basType = null;
		if (basTypeId != -1) {
			basType = TypeLists.BAS.list(basTypeId);
		}
		@Pc(22) int[] items = this.itemIds;
		if (basType != null && basType.objVisibility != null) {
			items = new int[basType.objVisibility.length];
			for (@Pc(34) int i = 0; i < basType.objVisibility.length; i++) {
				@Pc(41) int visibleSlot = basType.objVisibility[i];
				if (visibleSlot >= 0 && this.itemIds.length > visibleSlot) {
					items[i] = this.itemIds[visibleSlot];
				} else {
					items[i] = -1;
				}
			}
		}
		@Pc(80) boolean modifiesAlpha = false;
		@Pc(82) boolean modifiesColor = false;
		@Pc(84) boolean usesLighting = false;
		@Pc(86) boolean modifiesBillboard = false;
		@Pc(88) int frame = -1;
		@Pc(90) int nextFrame = -1;
		@Pc(92) int frameDuration = 0;
		@Pc(94) FrameSet animFrameset = null;
		@Pc(96) FrameSet animFrameset2 = null;
		if (seqType != null) {
			frame = seqType.frames[currentFrameId];
			functionMask = 2080;
			@Pc(111) int frameSetId = frame >>> 16;
			animFrameset = TypeLists.SEQ.listFrameSet(frameSetId);
			frame &= 0xFFFF;
			if (animFrameset != null) {
				modifiesColor = animFrameset.modifiesColors(frame) | false;
				modifiesAlpha = animFrameset.modifiesAlpha(frame) | false;
				modifiesBillboard = animFrameset.modifiesBillboards(frame) | false;
				usesLighting = seqType.lights | false;
			}
			if ((seqType.tweened || SeqType.forceTweening) && nextFrameId != -1 && seqType.frames.length > nextFrameId) {
				nextFrame = seqType.frames[nextFrameId];
				frameDuration = seqType.durations[currentFrameId];
				@Pc(177) int nextFrameSetId = nextFrame >>> 16;
				nextFrame &= 0xFFFF;
				if (frameSetId == nextFrameSetId) {
					animFrameset2 = animFrameset;
				} else {
					animFrameset2 = TypeLists.SEQ.listFrameSet(nextFrame >>> 16);
				}
				if (animFrameset2 != null) {
					modifiesColor |= animFrameset2.modifiesColors(nextFrame);
					modifiesAlpha |= animFrameset2.modifiesAlpha(nextFrame);
					modifiesBillboard |= animFrameset2.modifiesBillboards(nextFrame);
				}
			}
			if (modifiesColor) {
				functionMask = 2208;
			}
			if (modifiesAlpha) {
				functionMask |= 0x100;
			}
			if (usesLighting) {
				functionMask |= 0x200;
			}
			if (modifiesBillboard) {
				functionMask |= 0x400;
			}
		}
		@Pc(258) long hash = this.calculateHash(playerModel == null ? null : playerModel.recolorDstIndices, basTypeId, female, items);
		if (ItemContainer.aClass169_51 != null) {
			model = (Model) ItemContainer.aClass169_51.get(hash);
		}
		if (model == null || toolkit.compareFunctionMasks(model.ua(), functionMask) != 0) {
			if (model != null) {
				functionMask = toolkit.combineFunctionMasks(functionMask, model.ua());
			}
			@Pc(286) int combinedFunctionMask = functionMask;
			@Pc(288) boolean modelsMissing = false;
			for (@Pc(290) int j = 0; j < items.length; j++) {
				if (items[j] != -1 && !TypeLists.OBJ.list(items[j]).isBodyCached(female)) {
					modelsMissing = true;
				}
			}
			if (modelsMissing) {
				return null;
			}
			@Pc(322) Mesh[] meshes = new Mesh[items.length];
			for (@Pc(324) int k = 0; k < items.length; k++) {
				if (items[k] != -1) {
					meshes[k] = TypeLists.OBJ.list(items[k]).getBodyMesh(female);
				}
			}
			@Pc(382) int local382;
			@Pc(389) int local389;
			if (basType != null && basType.equipmentTransforms != null) {
				for (@Pc(362) int m = 0; m < basType.equipmentTransforms.length; m++) {
					if (basType.equipmentTransforms[m] != null && meshes[m] != null) {
						int translateX = basType.equipmentTransforms[m][0];
						int translateY = basType.equipmentTransforms[m][1];
						@Pc(396) int translateZ = basType.equipmentTransforms[m][2];
						@Pc(403) int rotateX = basType.equipmentTransforms[m][3];
						@Pc(410) int rotateY = basType.equipmentTransforms[m][4];
						@Pc(417) int rotateZ = basType.equipmentTransforms[m][5];
						if (rotateX != 0 || rotateY != 0 || rotateZ != 0) {
							meshes[m].rotateWithoutNormals(rotateY, rotateZ, rotateX);
						}
						if (translateX != 0 || translateY != 0 || translateZ != 0) {
							meshes[m].translate(translateZ, translateX, translateY);
						}
					}
				}
			}
			if (playerModel != null) {
				combinedFunctionMask = functionMask | 0x4000;
			}
			@Pc(474) Mesh mesh = new Mesh(meshes, meshes.length);
			model = toolkit.createModel(mesh, combinedFunctionMask, ItemContainer.modelFeatureMask, 64, 850);
			if (playerModel != null) {
				for (int partIndex = 0; partIndex < 5; partIndex++) {
					for (int paletteIndex = 0; paletteIndex < PlayerAppearance.recolorPaletteDst.length; paletteIndex++) {
						if (PlayerAppearance.recolorPaletteDst[paletteIndex][partIndex].length > playerModel.recolorDstIndices[partIndex]) {
							model.ia(PlayerAppearance.recolorPaletteSrc[paletteIndex][partIndex], PlayerAppearance.recolorPaletteDst[paletteIndex][partIndex][playerModel.recolorDstIndices[partIndex]]);
						}
					}
				}
			}
			if (ItemContainer.aClass169_51 != null) {
				model.s(functionMask);
				ItemContainer.aClass169_51.put(model, hash);
			}
		}
		if (seqType == null || animFrameset == null) {
			return model;
		} else {
			@Pc(555) Model model2 = model.copy((byte) 1, functionMask, true);
			model2.animateModelFrame(frame, frameDuration, animFrameset2, 0, animFrameset, seqType.lights, nextFrame, currentFrameDuration - 1);
			return model2;
		}
	}
	@OriginalMember(owner = "client!tf", name = "D", descriptor = "Lclient!eq;")
	public static final IterableHashTable CONTAINER_MAP = new IterableHashTable(32);
	@OriginalMember(owner = "client!hga", name = "a", descriptor = "(ZII)V")
	public static void deleteItemContainer(@OriginalArg(0) boolean negativeKey, @OriginalArg(1) int containerId) {
		@Pc(12) ItemContainer itemContainer = ItemContainer.getContainer(containerId, negativeKey);
		if (itemContainer != null) {
			itemContainer.unlink();
		}
	}
	@OriginalMember(owner = "client!afa", name = "a", descriptor = "(ZIII)I")
	public static int getItemIdAtSlot(@OriginalArg(0) boolean otherPlayer, @OriginalArg(1) int inventoryId, @OriginalArg(2) int slot) {
		@Pc(13) ItemContainer container = ItemContainer.getContainer(inventoryId, otherPlayer);
		if (container == null) {
			return -1;
		} else if (slot >= 0 && container.itemIds.length > slot) {
			return container.itemIds[slot];
		} else {
			return -1;
		}
	}

	@OriginalMember(owner = "client!qga", name = "a", descriptor = "(IZZ)V")
	public static void resetContainer(@OriginalArg(0) int containerId, @OriginalArg(1) boolean negativeKey) {
		@Pc(10) ItemContainer container = ItemContainer.getContainer(containerId, negativeKey);
		if (container != null) {
			for (@Pc(19) int i = 0; i < container.itemIds.length; i++) {
				container.itemIds[i] = -1;
				container.amounts[i] = 0;
			}
		}
	}

	@OriginalMember(owner = "client!vg", name = "a", descriptor = "(IIZIII)V")
	public static void setItemInContainer(@OriginalArg(0) int containerId, @OriginalArg(2) boolean isNegativeKey, @OriginalArg(3) int slot, @OriginalArg(4) int amount, @OriginalArg(5) int itemId) {
		@Pc(19) long key = (long) (containerId | (isNegativeKey ? Integer.MIN_VALUE : 0));
		@Pc(25) ItemContainer container = (ItemContainer) ItemContainer.CONTAINER_MAP.get(key);
		if (container == null) {
			container = new ItemContainer();
			ItemContainer.CONTAINER_MAP.put(key, container);
		}
		if (container.itemIds.length <= slot) {
			@Pc(46) int[] itemIds = new int[slot + 1];
			@Pc(51) int[] itemAmounts = new int[slot + 1];
			for (@Pc(53) int i = 0; i < container.itemIds.length; i++) {
				itemIds[i] = container.itemIds[i];
				itemAmounts[i] = container.amounts[i];
			}
			for (@Pc(83) int length = container.itemIds.length; length < slot; length++) {
				itemIds[length] = -1;
				itemAmounts[length] = 0;
			}
			container.itemIds = itemIds;
			container.amounts = itemAmounts;
		}
		container.itemIds[slot] = itemId;
		container.amounts[slot] = amount;
	}

	@OriginalMember(owner = "client!wha", name = "a", descriptor = "(BIZ)Lclient!ie;")
	public static ItemContainer getContainer(@OriginalArg(1) int containerId, @OriginalArg(2) boolean negativeKey) {
		@Pc(20) long key = (long) (containerId | (negativeKey ? Integer.MIN_VALUE : 0));
		return (ItemContainer) ItemContainer.CONTAINER_MAP.get(key);
	}

	@OriginalMember(owner = "client!jl", name = "a", descriptor = "(IIZI)I")
	public static int getInventoryTotal(@OriginalArg(0) int itemId, @OriginalArg(1) int inventoryId, @OriginalArg(2) boolean otherPlayer) {
		@Pc(10) ItemContainer container = ItemContainer.getContainer(inventoryId, otherPlayer);
		if (container == null) {
			return 0;
		} else if (itemId == -1) {
			return 0;
		} else {
			@Pc(21) int total = 0;
			for (@Pc(23) int i = 0; i < container.amounts.length; i++) {
				if (itemId == container.itemIds[i]) {
					total += container.amounts[i];
				}
			}
			return total;
		}
	}

	@OriginalMember(owner = "client!jv", name = "a", descriptor = "(ZBZII)I")
	public static int getInventoryParamTotal(@OriginalArg(0) boolean multiplyByAmount, @OriginalArg(3) int inventoryId, @OriginalArg(4) int paramId) {
		@Pc(15) ItemContainer container = ItemContainer.getContainer(inventoryId, false);
		if (container == null) {
			return 0;
		}
		@Pc(21) int total = 0;
		for (@Pc(23) int i = 0; i < container.itemIds.length; i++) {
			if (container.itemIds[i] >= 0 && container.itemIds[i] < TypeLists.OBJ.num) {
				@Pc(52) ItemType objType = TypeLists.OBJ.list(container.itemIds[i]);
				@Pc(62) int paramValue = objType.getIntParam(TypeLists.PARAMS.list(paramId).defaultInt, paramId);
				if (multiplyByAmount) {
					total += paramValue * container.amounts[i];
				} else {
					total += paramValue;
				}
			}
		}
		return total;
	}
	@OriginalMember(owner = "client!mfa", name = "d", descriptor = "(II)V")
	public static void setFeatureMask(@OriginalArg(1) int featureMask) {
		ItemContainer.modelFeatureMask = featureMask;
		ItemContainer.aClass169_51.reset();
	}
	@OriginalMember(owner = "client!ta", name = "a", descriptor = "(IIZI)I")
	public static int getItemAmountAtSlot(@OriginalArg(1) int inventoryId, @OriginalArg(2) boolean otherPlayer, @OriginalArg(3) int slot) {
		@Pc(16) ItemContainer container = ItemContainer.getContainer(inventoryId, otherPlayer);
		if (container == null) {
			return 0;
		} else if (slot >= 0 && slot < container.amounts.length) {
			return container.amounts[slot];
		} else {
			return 0;
		}
	}
	@OriginalMember(owner = "client!fga", name = "b", descriptor = "(I)V")
	public static void clearContainers() {
		ItemContainer.CONTAINER_MAP.clear();
	}

	@OriginalMember(owner = "client!wt", name = "a", descriptor = "(BIZ)I")
	public static int getInventoryFreeSpace(@OriginalArg(1) int inventoryId) {
		@Pc(14) ItemContainer container = ItemContainer.getContainer(inventoryId, false);
		if (container == null) {
			return TypeLists.OP_MAPELEMENT2.list(inventoryId).size;
		}
		@Pc(24) int freeSlots = 0;
		for (@Pc(26) int i = 0; i < container.itemIds.length; i++) {
			if (container.itemIds[i] == -1) {
				freeSlots++;
			}
		}
		return freeSlots + TypeLists.OP_MAPELEMENT2.list(inventoryId).size - container.itemIds.length;
	}
	@OriginalMember(owner = "client!bda", name = "w", descriptor = "I")
	public static int modelFeatureMask;

	@OriginalMember(owner = "client!re", name = "z", descriptor = "Lclient!jr;")
	public static final LRUCache aClass169_51 = new LRUCache(10);
	@OriginalMember(owner = "client!rc", name = "j", descriptor = "[I")
	public static final int[] RECENTLY_USED_ITEM_CONTAINERS = new int[32];

	@OriginalMember(owner = "client!cm", name = "f", descriptor = "(I)V")
	public static void resetItemContainerModelCache() {
		ItemContainer.aClass169_51.reset();
	}
}
