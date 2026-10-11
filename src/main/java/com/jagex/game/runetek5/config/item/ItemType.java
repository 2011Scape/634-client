package com.jagex.game.runetek5.config.item;

import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.node.StringNode;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.objtype.ObjNumMode;
import com.jagex.game.runetek5.config.objtype.ObjStackability;
import com.jagex.game.runetek5.config.objtype.ObjTypeList;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!rq")
public final class ItemType {

	@OriginalMember(owner = "client!rq", name = "f", descriptor = "I")
	private int modelId;

	@OriginalMember(owner = "client!rq", name = "g", descriptor = "Lclient!eq;")
	public IterableHashTable parameters;

	@OriginalMember(owner = "client!rq", name = "k", descriptor = "Lclient!ub;")
	public ObjTypeList typeList;

	@OriginalMember(owner = "client!rq", name = "l", descriptor = "[I")
	public int[] stackIds;

	@OriginalMember(owner = "client!rq", name = "m", descriptor = "[Ljava/lang/String;")
	public String[] inventoryActions;

	@OriginalMember(owner = "client!rq", name = "s", descriptor = "I")
	public int id;

	@OriginalMember(owner = "client!rq", name = "u", descriptor = "[S")
	private short[] recolorDst;

	@OriginalMember(owner = "client!rq", name = "v", descriptor = "[I")
	public int[] quests;

	@OriginalMember(owner = "client!rq", name = "A", descriptor = "[S")
	private short[] recolorSrc;

	@OriginalMember(owner = "client!rq", name = "I", descriptor = "[S")
	private short[] retexSrc;

	@OriginalMember(owner = "client!rq", name = "Y", descriptor = "[S")
	private short[] retexDst;

	@OriginalMember(owner = "client!rq", name = "ib", descriptor = "[Ljava/lang/String;")
	public String[] groundActions;

	@OriginalMember(owner = "client!rq", name = "sb", descriptor = "[B")
	private byte[] recolorDPalette;

	@OriginalMember(owner = "client!rq", name = "Cb", descriptor = "[I")
	public int[] stackTriggerAmount;

	@OriginalMember(owner = "client!rq", name = "b", descriptor = "I")
	public int primaryCursorActionIndex = -1;

	@OriginalMember(owner = "client!rq", name = "t", descriptor = "I")
	private int femaleEquip3 = -1;

	@OriginalMember(owner = "client!rq", name = "p", descriptor = "I")
	public int customCursorOp1 = -1;

	@OriginalMember(owner = "client!rq", name = "y", descriptor = "I")
	private int femaleHead2 = -1;

	@OriginalMember(owner = "client!rq", name = "o", descriptor = "I")
	private int resizeZ = 128;

	@OriginalMember(owner = "client!rq", name = "B", descriptor = "I")
	public int lendId = -1;

	@OriginalMember(owner = "client!rq", name = "h", descriptor = "I")
	public int certId = -1;

	@OriginalMember(owner = "client!rq", name = "S", descriptor = "Ljava/lang/String;")
	public String name = "null";

	@OriginalMember(owner = "client!rq", name = "R", descriptor = "I")
	private int resizeY = 128;

	@OriginalMember(owner = "client!rq", name = "W", descriptor = "I")
	public int dummyitem = 0;

	@OriginalMember(owner = "client!rq", name = "L", descriptor = "I")
	public int femaleEquip1 = -1;

	@OriginalMember(owner = "client!rq", name = "H", descriptor = "I")
	public int pickSizeShift = 0;

	@OriginalMember(owner = "client!rq", name = "bb", descriptor = "I")
	private int maleEquip2 = -1;

	@OriginalMember(owner = "client!rq", name = "P", descriptor = "I")
	private int maleHead1 = -1;

	@OriginalMember(owner = "client!rq", name = "e", descriptor = "Z")
	public boolean stockmarket = false;

	@OriginalMember(owner = "client!rq", name = "K", descriptor = "I")
	public int modelRotationX = 0;

	@OriginalMember(owner = "client!rq", name = "jb", descriptor = "I")
	public int lendTemplateId = -1;

	@OriginalMember(owner = "client!rq", name = "hb", descriptor = "I")
	public int modelRotationZ = 0;

	@OriginalMember(owner = "client!rq", name = "q", descriptor = "I")
	private int maleHead2 = -1;

	@OriginalMember(owner = "client!rq", name = "j", descriptor = "I")
	private int femaleEquip2 = -1;

	@OriginalMember(owner = "client!rq", name = "ob", descriptor = "I")
	public int customCursorId2 = -1;

	@OriginalMember(owner = "client!rq", name = "C", descriptor = "I")
	public int modelOffsetX = 0;

	@OriginalMember(owner = "client!rq", name = "J", descriptor = "I")
	private int resizeX = 128;

	@OriginalMember(owner = "client!rq", name = "fb", descriptor = "I")
	private int maleEquipmentOffsetZ = 0;

	@OriginalMember(owner = "client!rq", name = "ab", descriptor = "I")
	private int femaleEquipmentOffsetX = 0;

	@OriginalMember(owner = "client!rq", name = "tb", descriptor = "I")
	private int femaleHead1 = -1;

	@OriginalMember(owner = "client!rq", name = "mb", descriptor = "I")
	public int maleEquip1 = -1;

	@OriginalMember(owner = "client!rq", name = "ub", descriptor = "I")
	private int femaleEquipmentOffsetZ = 0;

	@OriginalMember(owner = "client!rq", name = "pb", descriptor = "I")
	public int customCursorId1 = -1;

	@OriginalMember(owner = "client!rq", name = "E", descriptor = "I")
	public int modelRotationY = 0;

	@OriginalMember(owner = "client!rq", name = "x", descriptor = "I")
	public int customCursorOp2 = -1;

	@OriginalMember(owner = "client!rq", name = "xb", descriptor = "I")
	public int modelOffsetY = 0;

	@OriginalMember(owner = "client!rq", name = "eb", descriptor = "I")
	private int maleEquipmentOffsetX = 0;

	@OriginalMember(owner = "client!rq", name = "O", descriptor = "I")
	private int ambient = 0;

	@OriginalMember(owner = "client!rq", name = "cb", descriptor = "I")
	private int maleEquip3 = -1;

	@OriginalMember(owner = "client!rq", name = "n", descriptor = "I")
	public int secondaryCursorActionIndex = -1;

	@OriginalMember(owner = "client!rq", name = "rb", descriptor = "I")
	public int stackable = 0;

	@OriginalMember(owner = "client!rq", name = "i", descriptor = "I")
	public int primaryCursor = -1;

	@OriginalMember(owner = "client!rq", name = "Z", descriptor = "I")
	public int multiStackSize = -1;

	@OriginalMember(owner = "client!rq", name = "U", descriptor = "I")
	private int maleEquipmentOffsetY = 0;

	@OriginalMember(owner = "client!rq", name = "Bb", descriptor = "I")
	public int secondaryCursor = -1;

	@OriginalMember(owner = "client!rq", name = "yb", descriptor = "I")
	public int teamId = 0;

	@OriginalMember(owner = "client!rq", name = "vb", descriptor = "I")
	private int contrast = 0;

	@OriginalMember(owner = "client!rq", name = "qb", descriptor = "I")
	public int value = 1;

	@OriginalMember(owner = "client!rq", name = "c", descriptor = "I")
	private int femaleEquipmentOffsetY = 0;

	@OriginalMember(owner = "client!rq", name = "wb", descriptor = "I")
	public int modelZoom = 2000;

	@OriginalMember(owner = "client!rq", name = "Eb", descriptor = "I")
	public int certTemplateId = -1;

	@OriginalMember(owner = "client!rq", name = "G", descriptor = "Z")
	public boolean membersOnly = false;

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(ZI)Lclient!bh;")
	public Mesh getHeadMesh(@OriginalArg(0) boolean female) {
		@Pc(8) int maleEquip1 = this.maleHead1;
		@Pc(11) int maleEquipmentOffsetX = this.maleHead2;
		if (female) {
			maleEquipmentOffsetX = this.femaleHead2;
			maleEquip1 = this.femaleHead1;
		}
		if (maleEquip1 == -1) {
			return null;
		}
		@Pc(33) Mesh mesh = Mesh.fromResource(this.typeList.meshResource, maleEquip1);
		if (mesh.version < 13) {
			mesh.upscale();
		}
		if (maleEquipmentOffsetX != -1) {
			@Pc(55) Mesh mesh2 = Mesh.fromResource(this.typeList.meshResource, maleEquipmentOffsetX);
			if (mesh2.version < 13) {
				mesh2.upscale();
			}
			@Pc(78) Mesh[] meshes = new Mesh[] { mesh, mesh2 };
			mesh = new Mesh(meshes, 2);
		}
		@Pc(89) int index;
		if (this.recolorSrc != null) {
			for (index = 0; index < this.recolorSrc.length; index++) {
				mesh.recolor(this.recolorSrc[index], this.recolorDst[index]);
			}
		}
		if (this.retexSrc != null) {
			for (index = 0; index < this.retexSrc.length; index++) {
				mesh.retexture(this.retexSrc[index], this.retexDst[index]);
			}
		}
		return mesh;
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(BZ)Z")
	public boolean isHeadCached(@OriginalArg(1) boolean female) {
		@Pc(6) int maleEquip1 = this.maleHead1;
		@Pc(17) int maleEquipmentOffsetX = this.maleHead2;
		if (female) {
			maleEquip1 = this.femaleHead1;
			maleEquipmentOffsetX = this.femaleHead2;
		}
		if (maleEquip1 == -1) {
			return true;
		}
		@Pc(33) boolean cached = true;
		if (!this.typeList.meshResource.isFileReady(maleEquip1, 0)) {
			cached = false;
		}
		if (maleEquipmentOffsetX != -1 && !this.typeList.meshResource.isFileReady(maleEquipmentOffsetX, 0)) {
			cached = false;
		}
		return cached;
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(Lclient!rq;BLclient!rq;)V")
	public void generateLend(@OriginalArg(0) ItemType objType, @OriginalArg(2) ItemType objType2) {
		this.maleEquipmentOffsetZ = objType.maleEquipmentOffsetZ;
		this.modelOffsetX = objType2.modelOffsetX;
		this.value = 0;
		this.retexSrc = objType.retexSrc;
		this.femaleEquip1 = objType.femaleEquip1;
		this.modelRotationY = objType2.modelRotationY;
		this.groundActions = objType.groundActions;
		this.maleEquipmentOffsetY = objType.maleEquipmentOffsetY;
		this.recolorDPalette = objType.recolorDPalette;
		this.recolorDst = objType.recolorDst;
		this.name = objType.name;
		this.modelId = objType2.modelId;
		this.femaleEquip3 = objType.femaleEquip3;
		this.recolorSrc = objType.recolorSrc;
		this.maleEquip1 = objType.maleEquip1;
		this.femaleHead1 = objType.femaleHead1;
		this.maleEquip2 = objType.maleEquip2;
		this.modelZoom = objType2.modelZoom;
		this.maleHead2 = objType.maleHead2;
		this.retexDst = objType.retexDst;
		this.membersOnly = objType.membersOnly;
		this.modelRotationZ = objType2.modelRotationZ;
		this.inventoryActions = new String[5];
		this.femaleEquipmentOffsetZ = objType.femaleEquipmentOffsetZ;
		this.femaleHead2 = objType.femaleHead2;
		this.maleEquipmentOffsetX = objType.maleEquipmentOffsetX;
		this.maleHead1 = objType.maleHead1;
		this.parameters = objType.parameters;
		this.maleEquip3 = objType.maleEquip3;
		this.teamId = objType.teamId;
		this.modelRotationX = objType2.modelRotationX;
		this.femaleEquip2 = objType.femaleEquip2;
		this.femaleEquipmentOffsetY = objType.femaleEquipmentOffsetY;
		this.femaleEquipmentOffsetX = objType.femaleEquipmentOffsetX;
		this.modelOffsetY = objType2.modelOffsetY;
		if (objType.inventoryActions != null) {
			for (@Pc(157) int i = 0; i < 4; i++) {
				this.inventoryActions[i] = objType.inventoryActions[i];
			}
		}
		this.inventoryActions[4] = LocalizedText.DISCARD.get(this.typeList.languageId);
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(II)Ljava/lang/String;")
	private String formatAmount(@OriginalArg(0) int amount) {
		if (amount < 100000) {
			return "<col=ffff00>" + amount + "</col>";
		} else if (amount < 10000000) {
			return "<col=ffffff>" + amount / 1000 + LocalizedText.aClass351_38.get(this.typeList.languageId) + "</col>";
		} else {
			return "<col=00ff80>" + amount / 1000000 + LocalizedText.aClass351_36.get(this.typeList.languageId) + "</col>";
		}
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(ZZ)Lclient!bh;")
	public Mesh getBodyMesh(@OriginalArg(0) boolean female) {
		@Pc(8) int primaryModel = this.maleEquip1;
		@Pc(11) int secondaryModel = this.maleEquip2;
		@Pc(20) int tertiaryModel = this.maleEquip3;
		if (female) {
			primaryModel = this.femaleEquip1;
			tertiaryModel = this.femaleEquip3;
			secondaryModel = this.femaleEquip2;
		}
		if (primaryModel == -1) {
			return null;
		}
		@Pc(44) Mesh primaryMesh = Mesh.fromResource(this.typeList.meshResource, primaryModel);
		if (primaryMesh.version < 13) {
			primaryMesh.upscale();
		}
		if (secondaryModel != -1) {
			@Pc(66) Mesh secondaryMesh = Mesh.fromResource(this.typeList.meshResource, secondaryModel);
			if (secondaryMesh.version < 13) {
				secondaryMesh.upscale();
			}
			if (tertiaryModel == -1) {
				@Pc(129) Mesh[] meshes = new Mesh[] { primaryMesh, secondaryMesh };
				primaryMesh = new Mesh(meshes, 2);
			} else {
				@Pc(87) Mesh tertiaryMesh = Mesh.fromResource(this.typeList.meshResource, tertiaryModel);
				if (tertiaryMesh.version < 13) {
					tertiaryMesh.upscale();
				}
				@Pc(110) Mesh[] meshesToCombine = new Mesh[] { primaryMesh, secondaryMesh, tertiaryMesh };
				primaryMesh = new Mesh(meshesToCombine, 3);
			}
		}
		if (primaryMesh == null) {
			return null;
		}
		if (!female && (this.maleEquipmentOffsetX != 0 || this.maleEquipmentOffsetY != 0 || this.maleEquipmentOffsetZ != 0)) {
			primaryMesh.translate(this.maleEquipmentOffsetZ, this.maleEquipmentOffsetX, this.maleEquipmentOffsetY);
		}
		if (female && (this.femaleEquipmentOffsetX != 0 || this.femaleEquipmentOffsetY != 0 || this.femaleEquipmentOffsetZ != 0)) {
			primaryMesh.translate(this.femaleEquipmentOffsetZ, this.femaleEquipmentOffsetX, this.femaleEquipmentOffsetY);
		}
		@Pc(196) int index;
		if (this.recolorSrc != null) {
			for (index = 0; index < this.recolorSrc.length; index++) {
				primaryMesh.recolor(this.recolorSrc[index], this.recolorDst[index]);
			}
		}
		if (this.retexSrc != null) {
			for (index = 0; index < this.retexSrc.length; index++) {
				primaryMesh.retexture(this.retexSrc[index], this.retexDst[index]);
			}
		}
		return primaryMesh;
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(Lclient!oo;Lclient!hg;Lclient!ha;IIIIBI)Lclient!ka;")
	public Model getAnimatedModel(@OriginalArg(0) PlayerAppearance playerModel, @OriginalArg(1) SeqType seqType, @OriginalArg(2) Toolkit toolkit, @OriginalArg(3) int functionMask, @OriginalArg(4) int currentFrameDuration, @OriginalArg(5) int count, @OriginalArg(6) int currentFrameId, @OriginalArg(8) int nextFrameId) {
		@Pc(15) int local15;
		if (this.stackIds != null && count > 1) {
			int stackId = -1;
			for (@Pc(17) int i = 0; i < 10; i++) {
				if (count >= this.stackTriggerAmount[i] && this.stackTriggerAmount[i] != 0) {
					stackId = this.stackIds[i];
				}
			}
			if (stackId != -1) {
				return this.typeList.list(stackId).getAnimatedModel(playerModel, seqType, toolkit, functionMask, currentFrameDuration, 1, currentFrameId, nextFrameId);
			}
		}
		int combinedFunctionMask = functionMask;
		if (seqType != null) {
			combinedFunctionMask = functionMask | seqType.getFunctionMask(nextFrameId, currentFrameId, true);
		}
		@Pc(84) LRUCache referenceCache = this.typeList.modelCache;
		@Pc(101) Model model;
		synchronized (this.typeList.modelCache) {
			model = (Model) this.typeList.modelCache.get((long) (toolkit.index << 29 | this.id));
		}
		if (model == null || toolkit.compareFunctionMasks(model.ua(), combinedFunctionMask) != 0) {
			if (model != null) {
				combinedFunctionMask = toolkit.combineFunctionMasks(combinedFunctionMask, model.ua());
			}
			@Pc(128) int modelFunctionMask = combinedFunctionMask;
			if (this.retexSrc != null) {
				modelFunctionMask = combinedFunctionMask | 0x8000;
			}
			if (this.recolorSrc != null || playerModel != null) {
				modelFunctionMask |= 0x4000;
			}
			if (this.resizeX != 128) {
				modelFunctionMask |= 0x1;
			}
			if (this.resizeX != 128) {
				modelFunctionMask |= 0x2;
			}
			if (this.resizeX != 128) {
				modelFunctionMask |= 0x4;
			}
			@Pc(181) Mesh mesh = Mesh.fromResource(this.typeList.meshResource, this.modelId);
			if (mesh == null) {
				return null;
			}
			if (mesh.version < 13) {
				mesh.upscale();
			}
			model = toolkit.createModel(mesh, modelFunctionMask, this.typeList.featureMask, this.ambient + 64, 850 - -this.contrast);
			if (this.resizeX != 128 || this.resizeY != 128 || this.resizeZ != 128) {
				model.O(this.resizeX, this.resizeY, this.resizeZ);
			}
			@Pc(239) int index;
			if (this.recolorSrc != null) {
				for (index = 0; index < this.recolorSrc.length; index++) {
					if (this.recolorDPalette == null || index >= this.recolorDPalette.length) {
						model.ia(this.recolorSrc[index], this.recolorDst[index]);
					} else {
						model.ia(this.recolorSrc[index], ItemType.recolorPalette[this.recolorDPalette[index] & 0xFF]);
					}
				}
			}
			if (this.retexSrc != null) {
				for (index = 0; index < this.retexSrc.length; index++) {
					model.aa(this.retexSrc[index], this.retexDst[index]);
				}
			}
			if (playerModel != null) {
				for (index = 0; index < 5; index++) {
					for (@Pc(320) int j = 0; j < PlayerAppearance.recolorPaletteDst.length; j++) {
						if (playerModel.recolorDstIndices[index] < PlayerAppearance.recolorPaletteDst[j][index].length) {
							model.ia(PlayerAppearance.recolorPaletteSrc[j][index], PlayerAppearance.recolorPaletteDst[j][index][playerModel.recolorDstIndices[index]]);
						}
					}
				}
			}
			model.s(combinedFunctionMask);
			@Pc(368) LRUCache referenceCache2 = this.typeList.modelCache;
			synchronized (this.typeList.modelCache) {
				this.typeList.modelCache.put(model, (long) (toolkit.index << 29 | this.id));
			}
		}
		if (seqType != null) {
			model = seqType.animateModel(model, nextFrameId, currentFrameDuration, combinedFunctionMask, currentFrameId);
		}
		model.s(functionMask);
		return model;
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(IB)Lclient!rq;")
	public ItemType getStackVariant(@OriginalArg(0) int stackSize) {
		if (this.stackIds != null && stackSize > 1) {
			@Pc(21) int stackId = -1;
			for (@Pc(23) int i = 0; i < 10; i++) {
				if (stackSize >= this.stackTriggerAmount[i] && this.stackTriggerAmount[i] != 0) {
					stackId = this.stackIds[i];
				}
			}
			if (stackId != -1) {
				return this.typeList.list(stackId);
			}
		}
		return this;
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(Ljava/lang/String;II)Ljava/lang/String;")
	public String getStringParam(@OriginalArg(0) String string, @OriginalArg(1) int paramKey) {
		if (this.parameters == null) {
			return string;
		} else {
			@Pc(25) StringNode stringNode = (StringNode) this.parameters.get((long) paramKey);
			return stringNode == null ? string : stringNode.value;
		}
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(IZILclient!ha;Lclient!ha;Lclient!da;Lclient!oo;IBI)[I")
	public int[] getSpritePixels(@OriginalArg(0) int amount, @OriginalArg(1) boolean zoomedIn, @OriginalArg(2) int shadowColor, @OriginalArg(3) Toolkit toolkit, @OriginalArg(4) Toolkit toolkit2, @OriginalArg(5) AbstractFontRenderer font, @OriginalArg(6) PlayerAppearance playerModel, @OriginalArg(7) int numMode, @OriginalArg(9) int outlineSize) {
		@Pc(16) Mesh mesh = Mesh.fromResource(this.typeList.meshResource, this.modelId);
		if (mesh == null) {
			return null;
		}
		if (mesh.version < 13) {
			mesh.upscale();
		}
		@Pc(35) int index;
		if (this.recolorSrc != null) {
			for (index = 0; index < this.recolorSrc.length; index++) {
				if (this.recolorDPalette == null || index >= this.recolorDPalette.length) {
					mesh.recolor(this.recolorSrc[index], this.recolorDst[index]);
				} else {
					mesh.recolor(this.recolorSrc[index], ItemType.recolorPalette[this.recolorDPalette[index] & 0xFF]);
				}
			}
		}
		if (this.retexSrc != null) {
			for (index = 0; index < this.retexSrc.length; index++) {
				mesh.retexture(this.retexSrc[index], this.retexDst[index]);
			}
		}
		if (playerModel != null) {
			for (index = 0; index < 5; index++) {
				for (@Pc(119) int i = 0; i < PlayerAppearance.recolorPaletteDst.length; i++) {
					if (PlayerAppearance.recolorPaletteDst[i][index].length > playerModel.recolorDstIndices[index]) {
						mesh.recolor(PlayerAppearance.recolorPaletteSrc[i][index], PlayerAppearance.recolorPaletteDst[i][index][playerModel.recolorDstIndices[index]]);
					}
				}
			}
		}
		@Pc(161) short functionMask = 2048;
		@Pc(163) boolean resized = false;
		if (this.resizeX != 128 || this.resizeY != 128 || this.resizeZ != 128) {
			functionMask = 2055;
			resized = true;
		}
		@Pc(197) Model model = toolkit2.createModel(mesh, functionMask, 64, this.ambient + 64, this.contrast + 768);
		if (!model.loadedTextures()) {
			return null;
		}
		if (resized) {
			model.O(this.resizeX, this.resizeY, this.resizeZ);
		}
		@Pc(214) NativeSprite sprite = null;
		if (this.certTemplateId != -1) {
			sprite = this.typeList.getSprite(toolkit2, 0, 10, font, playerModel, ObjNumMode.SHOWCOUNT_NEVER, true, toolkit, this.certId, true, 1);
			if (sprite == null) {
				return null;
			}
		} else if (this.lendTemplateId != -1) {
			sprite = this.typeList.getSprite(toolkit2, shadowColor, amount, font, playerModel, ObjNumMode.SHOWCOUNT_NEVER, true, toolkit, this.lendId, false, outlineSize);
			if (sprite == null) {
				return null;
			}
		}
		@Pc(281) int zoom;
		if (zoomedIn) {
			zoom = (int) ((double) this.modelZoom * 1.5D) << 2;
		} else if (outlineSize == 2) {
			zoom = (int) ((double) this.modelZoom * 1.04D) << 2;
		} else {
			zoom = this.modelZoom << 2;
		}
		toolkit2.DA(16, 16, 512, 512);
		@Pc(308) Matrix matrix = toolkit2.createMatrix();
		matrix.makeIdentity();
		toolkit2.setCamera(matrix);
		toolkit2.xa(1.0F);
		toolkit2.ZA(16777215, 1.0F, 1.0F, -50.0F, -10.0F, -50.0F);
		@Pc(327) Matrix matrix2 = toolkit2.scratchMatrix();
		matrix2.makeRotationZ(-this.modelRotationZ << 3);
		matrix2.rotateAxisY(this.modelRotationY << 3);
		matrix2.translate(this.modelOffsetX << 2, (zoom * TextureCombineSource.SINE[this.modelRotationX << 3] >> 14) + (this.modelOffsetY << 2) - model.fa() / 2, (zoom * TextureCombineSource.COSINE[this.modelRotationX << 3] >> 14) - -(this.modelOffsetY << 2));
		matrix2.rotateAxisX(this.modelRotationX << 3);
		@Pc(391) int zNear = toolkit2.i();
		@Pc(394) int zFar = toolkit2.XA();
		toolkit2.f(50, Integer.MAX_VALUE);
		toolkit2.ya();
		toolkit2.la();
		toolkit2.aa(0, 0, 36, 32, 0, 0);
		model.render(matrix2, null, 1);
		toolkit2.f(zNear, zFar);
		@Pc(426) int[] pixels = toolkit2.na(0, 0, 36, 32);
		if (outlineSize >= 1) {
			pixels = this.outline(-16777214, pixels);
			if (outlineSize >= 2) {
				pixels = this.outline(-1, pixels);
			}
		}
		if (shadowColor != 0) {
			this.shadow(shadowColor, pixels);
		}
		toolkit2.createNativeSprite(36, pixels, 36, 32).render(0, 0);
		if (this.certTemplateId != -1) {
			sprite.render(0, 0);
		} else if (this.lendTemplateId != -1) {
			sprite.render(0, 0);
		}
		if (numMode == ObjNumMode.SHOWCOUNT_ALWAYS || numMode == ObjNumMode.SHOWCOUNT_IFNOT1 && (this.stackable == ObjStackability.ALWAYS || amount != 1) && amount != -1) {
			font.drawString(this.formatAmount(amount), -256, 9, 0, -16777215);
		}
		pixels = toolkit2.na(0, 0, 36, 32);
		for (@Pc(533) int j = 0; j < pixels.length; j++) {
			if ((pixels[j] & 0xFFFFFF) == 0) {
				pixels[j] = 0;
			} else {
				pixels[j] |= 0xFF000000;
			}
		}
		return pixels;
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(B)V")
	public void postDecode() {
	}

	@OriginalMember(owner = "client!rq", name = "b", descriptor = "(ZI)Z")
	public boolean isBodyCached(@OriginalArg(0) boolean female) {
		@Pc(6) int primaryModel = this.maleEquip1;
		@Pc(9) int certId = this.maleEquip2;
		@Pc(12) int tertiaryModel = this.maleEquip3;
		if (female) {
			tertiaryModel = this.femaleEquip3;
			primaryModel = this.femaleEquip1;
			certId = this.femaleEquip2;
		}
		if (primaryModel == -1) {
			return true;
		}
		@Pc(30) boolean cached = true;
		if (!this.typeList.meshResource.isFileReady(primaryModel, 0)) {
			cached = false;
		}
		if (certId != -1 && !this.typeList.meshResource.isFileReady(certId, 0)) {
			cached = false;
		}
		if (tertiaryModel != -1 && !this.typeList.meshResource.isFileReady(tertiaryModel, 0)) {
			cached = false;
		}
		return cached;
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(IILclient!cea;)V")
	private void decode(@OriginalArg(1) int opcode, @OriginalArg(2) Packet packet) {
		if (opcode == 1) {
			this.modelId = packet.readUShort();
		} else if (opcode == 2) {
			this.name = packet.readString();
		} else if (opcode == 4) {
			this.modelZoom = packet.readUShort();
		} else if (opcode == 5) {
			this.modelRotationX = packet.readUShort();
		} else if (opcode == 6) {
			this.modelRotationY = packet.readUShort();
		} else if (opcode == 7) {
			this.modelOffsetX = packet.readUShort();
			if (this.modelOffsetX > 32767) {
				this.modelOffsetX -= 65536;
			}
		} else if (opcode == 8) {
			this.modelOffsetY = packet.readUShort();
			if (this.modelOffsetY > 32767) {
				this.modelOffsetY -= 65536;
			}
		} else if (opcode == 11) {
			this.stackable = 1;
		} else if (opcode == 12) {
			this.value = packet.readInt();
		} else if (opcode == 16) {
			this.membersOnly = true;
		} else if (opcode == 18) {
			this.multiStackSize = packet.readUShort();
		} else if (opcode == 23) {
			this.maleEquip1 = packet.readUShort();
		} else if (opcode == 24) {
			this.maleEquip2 = packet.readUShort();
		} else if (opcode == 25) {
			this.femaleEquip1 = packet.readUShort();
		} else if (opcode == 26) {
			this.femaleEquip2 = packet.readUShort();
		} else if (opcode >= 30 && opcode < 35) {
			this.groundActions[opcode - 30] = packet.readString();
		} else if (opcode >= 35 && opcode < 40) {
			this.inventoryActions[opcode - 35] = packet.readString();
		} else {
			@Pc(191) int count;
			@Pc(201) int local201;
			if (opcode == 40) {
				count = packet.readUByte();
				this.recolorSrc = new short[count];
				this.recolorDst = new short[count];
				for (int recolorIndex = 0; recolorIndex < count; recolorIndex++) {
					this.recolorSrc[recolorIndex] = (short) packet.readUShort();
					this.recolorDst[recolorIndex] = (short) packet.readUShort();
				}
			} else if (opcode == 41) {
				count = packet.readUByte();
				this.retexDst = new short[count];
				this.retexSrc = new short[count];
				for (int retexIndex = 0; retexIndex < count; retexIndex++) {
					this.retexSrc[retexIndex] = (short) packet.readUShort();
					this.retexDst[retexIndex] = (short) packet.readUShort();
				}
			} else if (opcode == 42) {
				count = packet.readUByte();
				this.recolorDPalette = new byte[count];
				for (int paletteIndex = 0; paletteIndex < count; paletteIndex++) {
					this.recolorDPalette[paletteIndex] = packet.readByte();
				}
			} else if (opcode == 65) {
				this.stockmarket = true;
			} else if (opcode == 78) {
				this.maleEquip3 = packet.readUShort();
			} else if (opcode == 79) {
				this.femaleEquip3 = packet.readUShort();
			} else if (opcode == 90) {
				this.maleHead1 = packet.readUShort();
			} else if (opcode == 91) {
				this.femaleHead1 = packet.readUShort();
			} else if (opcode == 92) {
				this.maleHead2 = packet.readUShort();
			} else if (opcode == 93) {
				this.femaleHead2 = packet.readUShort();
			} else if (opcode == 95) {
				this.modelRotationZ = packet.readUShort();
			} else if (opcode == 96) {
				this.dummyitem = packet.readUByte();
			} else if (opcode == 97) {
				this.certId = packet.readUShort();
			} else if (opcode == 98) {
				this.certTemplateId = packet.readUShort();
			} else if (opcode >= 100 && opcode < 110) {
				if (this.stackIds == null) {
					this.stackTriggerAmount = new int[10];
					this.stackIds = new int[10];
				}
				this.stackIds[opcode - 100] = packet.readUShort();
				this.stackTriggerAmount[opcode - 100] = packet.readUShort();
			} else if (opcode == 110) {
				this.resizeX = packet.readUShort();
			} else if (opcode == 111) {
				this.resizeY = packet.readUShort();
			} else if (opcode == 112) {
				this.resizeZ = packet.readUShort();
			} else if (opcode == 113) {
				this.ambient = packet.readByte();
			} else if (opcode == 114) {
				this.contrast = packet.readByte() * 5;
			} else if (opcode == 115) {
				this.teamId = packet.readUByte();
			} else if (opcode == 121) {
				this.lendId = packet.readUShort();
			} else if (opcode == 122) {
				this.lendTemplateId = packet.readUShort();
			} else if (opcode == 125) {
				this.maleEquipmentOffsetX = packet.readByte() << 2;
				this.maleEquipmentOffsetY = packet.readByte() << 2;
				this.maleEquipmentOffsetZ = packet.readByte() << 2;
			} else if (opcode == 126) {
				this.femaleEquipmentOffsetX = packet.readByte() << 2;
				this.femaleEquipmentOffsetY = packet.readByte() << 2;
				this.femaleEquipmentOffsetZ = packet.readByte() << 2;
			} else if (opcode == 127) {
				this.primaryCursorActionIndex = packet.readUByte();
				this.primaryCursor = packet.readUShort();
			} else if (opcode == 128) {
				this.secondaryCursorActionIndex = packet.readUByte();
				this.secondaryCursor = packet.readUShort();
			} else if (opcode == 129) {
				this.customCursorOp1 = packet.readUByte();
				this.customCursorId1 = packet.readUShort();
			} else if (opcode == 130) {
				this.customCursorOp2 = packet.readUByte();
				this.customCursorId2 = packet.readUShort();
			} else if (opcode == 132) {
				count = packet.readUByte();
				this.quests = new int[count];
				for (int questIndex = 0; questIndex < count; questIndex++) {
					this.quests[questIndex] = packet.readUShort();
				}
			} else if (opcode == 134) {
				this.pickSizeShift = packet.readUByte();
			} else if (opcode == 249) {
				count = packet.readUByte();
				if (this.parameters == null) {
					int tableSize = Utils.nextPowerOfTwo(count);
					this.parameters = new IterableHashTable(tableSize);
				}
				for (int paramIndex = 0; paramIndex < count; paramIndex++) {
					@Pc(580) boolean isString = packet.readUByte() == 1;
					@Pc(584) int key = packet.readUMedium();
					@Pc(593) Node node;
					if (isString) {
						node = new StringNode(packet.readString());
					} else {
						node = new IntNode(packet.readInt());
					}
					this.parameters.put((long) key, node);
				}
			}
		}
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(III)I")
	public int getIntParam(@OriginalArg(0) int defaultInt, @OriginalArg(2) int paramKey) {
		if (this.parameters == null) {
			return defaultInt;
		} else {
			@Pc(16) IntNode intNode = (IntNode) this.parameters.get((long) paramKey);
			return intNode == null ? defaultInt : intNode.value;
		}
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(ILclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(15) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(opcode, packet);
		}
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(ILclient!rq;Lclient!rq;)V")
	public void generateNote(@OriginalArg(1) ItemType objType, @OriginalArg(2) ItemType objType2) {
		this.recolorDst = objType2.recolorDst;
		this.retexSrc = objType2.retexSrc;
		this.recolorDPalette = objType2.recolorDPalette;
		this.membersOnly = objType.membersOnly;
		this.modelRotationX = objType2.modelRotationX;
		this.modelRotationZ = objType2.modelRotationZ;
		this.modelOffsetX = objType2.modelOffsetX;
		this.retexDst = objType2.retexDst;
		this.modelRotationY = objType2.modelRotationY;
		this.value = objType.value;
		this.modelOffsetY = objType2.modelOffsetY;
		this.stackable = 1;
		this.recolorSrc = objType2.recolorSrc;
		this.modelZoom = objType2.modelZoom;
		this.name = objType.name;
		this.modelId = objType2.modelId;
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(I[IB)V")
	private void shadow(@OriginalArg(0) int color, @OriginalArg(1) int[] pixels) {
		for (@Pc(9) int i = 31; i > 0; i--) {
			@Pc(15) int rowOffset = i * 36;
			for (@Pc(17) int j = 35; j > 0; j--) {
				if (pixels[j + rowOffset] == 0 && pixels[j + rowOffset - 1 - 36] != 0) {
					pixels[rowOffset + j] = color;
				}
			}
		}
	}

	@OriginalMember(owner = "client!rq", name = "a", descriptor = "(II[I)[I")
	private int[] outline(@OriginalArg(0) int outlineColor, @OriginalArg(2) int[] pixels) {
		@Pc(13) int[] outlinedPixels = new int[1152];
		@Pc(15) int pixelIndex = 0;
		for (@Pc(17) int i = 0; i < 32; i++) {
			for (@Pc(21) int j = 0; j < 36; j++) {
				@Pc(27) int pixel = pixels[pixelIndex];
				if (pixel == 0) {
					if (j > 0 && pixels[pixelIndex - 1] != 0) {
						pixel = outlineColor;
					} else if (i > 0 && pixels[pixelIndex - 36] != 0) {
						pixel = outlineColor;
					} else if (j < 35 && pixels[pixelIndex + 1] != 0) {
						pixel = outlineColor;
					} else if (i < 31 && pixels[pixelIndex + 36] != 0) {
						pixel = outlineColor;
					}
				}
				outlinedPixels[pixelIndex++] = pixel;
			}
		}
		return outlinedPixels;
	}
	@OriginalMember(owner = "client!dt", name = "c", descriptor = "[S")
	public static short[] recolorPalette = new short[256];
}
