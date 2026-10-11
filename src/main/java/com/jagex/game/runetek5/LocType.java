package com.jagex.game.runetek5;

import com.jagex.game.runetek5.core.constants.LocShapes;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.node.StringNode;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.loctype.LocInteractivity;
import com.jagex.game.runetek5.config.loctype.LocOcclusionMode;
import com.jagex.game.runetek5.config.loctype.LocTypeList;
import com.jagex.game.runetek5.vardomain.VarDomain;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.ModelAndShadow;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.config.object.ObjectType;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!iv")
public final class LocType {

	@OriginalMember(owner = "client!iv", name = "g", descriptor = "[B")
	private byte[] recolorDPalette;

	@OriginalMember(owner = "client!iv", name = "n", descriptor = "B")
	private byte tintLightness;

	@OriginalMember(owner = "client!iv", name = "p", descriptor = "Lclient!eq;")
	private IterableHashTable parameters;

	@OriginalMember(owner = "client!iv", name = "t", descriptor = "[B")
	private byte[] shapes;

	@OriginalMember(owner = "client!iv", name = "A", descriptor = "[S")
	private short[] retextureDst;

	@OriginalMember(owner = "client!iv", name = "M", descriptor = "[I")
	public int[] soundGroupIds;

	@OriginalMember(owner = "client!iv", name = "Q", descriptor = "[[I")
	private int[][] objectModelIds;

	@OriginalMember(owner = "client!iv", name = "R", descriptor = "[S")
	private short[] recolorDst;

	@OriginalMember(owner = "client!iv", name = "ab", descriptor = "[I")
	public int[] quests;

	@OriginalMember(owner = "client!iv", name = "cb", descriptor = "[S")
	private short[] retextureSrc;

	@OriginalMember(owner = "client!iv", name = "qb", descriptor = "Lclient!uha;")
	public LocTypeList locTypeList;

	@OriginalMember(owner = "client!iv", name = "xb", descriptor = "[S")
	private short[] recolorSrc;

	@OriginalMember(owner = "client!iv", name = "yb", descriptor = "I")
	public int id;

	@OriginalMember(owner = "client!iv", name = "Bb", descriptor = "B")
	private byte tintHue;

	@OriginalMember(owner = "client!iv", name = "Cb", descriptor = "[I")
	public int[] alternativeIds;

	@OriginalMember(owner = "client!iv", name = "Jb", descriptor = "B")
	private byte tintSaturation;

	@OriginalMember(owner = "client!iv", name = "c", descriptor = "I")
	public int animationId = -1;

	@OriginalMember(owner = "client!iv", name = "k", descriptor = "Z")
	public boolean ignoreClipOnAltRoute = false;

	@OriginalMember(owner = "client!iv", name = "d", descriptor = "I")
	public int primaryCursor = -1;

	@OriginalMember(owner = "client!iv", name = "y", descriptor = "I")
	private int shadowOffsetZ = 0;

	@OriginalMember(owner = "client!iv", name = "E", descriptor = "Z")
	public boolean blocksProjectiles = true;

	@OriginalMember(owner = "client!iv", name = "i", descriptor = "I")
	public int hasActions = -1;

	@OriginalMember(owner = "client!iv", name = "q", descriptor = "I")
	private int resizeZ = 128;

	@OriginalMember(owner = "client!iv", name = "s", descriptor = "Ljava/lang/String;")
	public String name = "null";

	@OriginalMember(owner = "client!iv", name = "o", descriptor = "I")
	private int contrast = 0;

	@OriginalMember(owner = "client!iv", name = "u", descriptor = "B")
	private byte groundContourType = 0;

	@OriginalMember(owner = "client!iv", name = "w", descriptor = "Z")
	public boolean instrumentSoundEffect = false;

	@OriginalMember(owner = "client!iv", name = "O", descriptor = "Z")
	public boolean delayShading = false;

	@OriginalMember(owner = "client!iv", name = "L", descriptor = "Z")
	public boolean instrumentAmbientSound = false;

	@OriginalMember(owner = "client!iv", name = "l", descriptor = "I")
	public int secondaryCursor = -1;

	@OriginalMember(owner = "client!iv", name = "X", descriptor = "I")
	public int accessBlockFlags = 0;

	@OriginalMember(owner = "client!iv", name = "j", descriptor = "I")
	public int mapIconId = -1;

	@OriginalMember(owner = "client!iv", name = "W", descriptor = "[Ljava/lang/String;")
	public String[] actions = new String[5];

	@OriginalMember(owner = "client!iv", name = "V", descriptor = "Z")
	public boolean mapIconRotates = false;

	@OriginalMember(owner = "client!iv", name = "C", descriptor = "Z")
	public boolean dynamicShadow = true;

	@OriginalMember(owner = "client!iv", name = "hb", descriptor = "Z")
	public boolean replaySequence = true;

	@OriginalMember(owner = "client!iv", name = "gb", descriptor = "I")
	public int mapIconRotation = 0;

	@OriginalMember(owner = "client!iv", name = "h", descriptor = "I")
	private int offsetY = 0;

	@OriginalMember(owner = "client!iv", name = "ob", descriptor = "Z")
	public boolean requiresTextures = false;

	@OriginalMember(owner = "client!iv", name = "U", descriptor = "I")
	private int animationOddsTotal = 0;

	@OriginalMember(owner = "client!iv", name = "Z", descriptor = "I")
	public int ambientSoundMinDelay = 256;

	@OriginalMember(owner = "client!iv", name = "eb", descriptor = "I")
	public int mapCategoryid = -1;

	@OriginalMember(owner = "client!iv", name = "wb", descriptor = "I")
	private int varpId = -1;

	@OriginalMember(owner = "client!iv", name = "r", descriptor = "I")
	public int decorDisplacement = 64;

	@OriginalMember(owner = "client!iv", name = "jb", descriptor = "I")
	public int sizeY = 1;

	@OriginalMember(owner = "client!iv", name = "lb", descriptor = "I")
	private int resizeX = 128;

	@OriginalMember(owner = "client!iv", name = "S", descriptor = "I")
	public int cullY = 960;

	@OriginalMember(owner = "client!iv", name = "rb", descriptor = "I")
	private int varpbitId = -1;

	@OriginalMember(owner = "client!iv", name = "x", descriptor = "I")
	public int soundRadius = 0;

	@OriginalMember(owner = "client!iv", name = "D", descriptor = "I")
	public int supportsItems = -1;

	@OriginalMember(owner = "client!iv", name = "fb", descriptor = "B")
	private byte tintOpacity = 0;

	@OriginalMember(owner = "client!iv", name = "Eb", descriptor = "Z")
	public boolean forceDisplayDecoration = false;

	@OriginalMember(owner = "client!iv", name = "m", descriptor = "I")
	public int ambientSoundMaxDelay = 256;

	@OriginalMember(owner = "client!iv", name = "B", descriptor = "I")
	private int shadowOffsetX = 0;

	@OriginalMember(owner = "client!iv", name = "tb", descriptor = "I")
	public int soundVolume = 255;

	@OriginalMember(owner = "client!iv", name = "Ab", descriptor = "[I")
	public int[] animations = null;

	@OriginalMember(owner = "client!iv", name = "K", descriptor = "Z")
	public boolean inverted = false;

	@OriginalMember(owner = "client!iv", name = "bb", descriptor = "Z")
	public boolean staticShadow = true;

	@OriginalMember(owner = "client!iv", name = "Gb", descriptor = "I")
	private int ambient = 0;

	@OriginalMember(owner = "client!iv", name = "zb", descriptor = "Z")
	public boolean members = false;

	@OriginalMember(owner = "client!iv", name = "Kb", descriptor = "I")
	public int cullXZ = 0;

	@OriginalMember(owner = "client!iv", name = "Lb", descriptor = "I")
	private int shadowOffsetY = 0;

	@OriginalMember(owner = "client!iv", name = "ub", descriptor = "I")
	public int soundMaxInterval = 0;

	@OriginalMember(owner = "client!iv", name = "I", descriptor = "I")
	private int offsetX = 0;

	@OriginalMember(owner = "client!iv", name = "nb", descriptor = "I")
	public int groundDecorationHeight = 0;

	@OriginalMember(owner = "client!iv", name = "Y", descriptor = "I")
	private int goundContourModifier = -1;

	@OriginalMember(owner = "client!iv", name = "Ib", descriptor = "I")
	public int secondaryCursorActionIndex = -1;

	@OriginalMember(owner = "client!iv", name = "Mb", descriptor = "I")
	public int occlusionMode = -1;

	@OriginalMember(owner = "client!iv", name = "ib", descriptor = "Z")
	public boolean mapIconFlipped = false;

	@OriginalMember(owner = "client!iv", name = "Db", descriptor = "I")
	private int offsetZ = 0;

	@OriginalMember(owner = "client!iv", name = "Hb", descriptor = "I")
	public int primaryCursorActionIndex = -1;

	@OriginalMember(owner = "client!iv", name = "Qb", descriptor = "[I")
	private int[] animationOdds = null;

	@OriginalMember(owner = "client!iv", name = "db", descriptor = "I")
	public int blocksMovement = 2;

	@OriginalMember(owner = "client!iv", name = "Sb", descriptor = "I")
	public int sizeX = 1;

	@OriginalMember(owner = "client!iv", name = "mb", descriptor = "Z")
	public boolean hasAnimation = false;

	@OriginalMember(owner = "client!iv", name = "sb", descriptor = "I")
	private int resizeY = 128;

	@OriginalMember(owner = "client!iv", name = "Tb", descriptor = "I")
	public int soundMinInterval = 0;

	@OriginalMember(owner = "client!iv", name = "v", descriptor = "I")
	public int soundId = -1;

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(B)Z")
	public boolean hasSounds() {
		if (this.alternativeIds == null) {
			return this.soundId != -1 || this.soundGroupIds != null;
		}
		for (@Pc(21) int i = 0; i < this.alternativeIds.length; i++) {
			if (this.alternativeIds[i] != -1) {
				@Pc(39) LocType locType = this.locTypeList.list(this.alternativeIds[i]);
				if (locType.soundId != -1 || locType.soundGroupIds != null) {
					return true;
				}
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(Ljava/lang/String;II)Ljava/lang/String;")
	public String getStringParam(@OriginalArg(0) String string, @OriginalArg(1) int key) {
		if (this.parameters == null) {
			return string;
		} else {
			@Pc(21) StringNode stringNode = (StringNode) this.parameters.get((long) key);
			return stringNode == null ? string : stringNode.value;
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(Lclient!ha;Lclient!s;IIIZLclient!s;IIII)Lclient!ak;")
	public ModelAndShadow getModelAndShadow(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) Ground ground, @OriginalArg(2) int shape, @OriginalArg(3) int functionMask, @OriginalArg(4) int sceneX, @OriginalArg(5) boolean castShadow, @OriginalArg(6) Ground ground2, @OriginalArg(7) int averageHeight, @OriginalArg(8) int sceneZ, @OriginalArg(9) int rotation) {
		if (LocShapes.isWallDecoration(shape)) {
			shape = LocShapes.WALLDECOR_STRAIGHT_NOOFFSET;
		}
		@Pc(23) long uid = (long) (rotation + (shape << 3) + (this.id << 10));
		@Pc(31) long cacheKey = uid | (long) (toolkit.index << 29);
		@Pc(33) int adjustedFunctionMask = functionMask;
		if (this.groundContourType == 3) {
			adjustedFunctionMask = functionMask | 0x7;
		} else {
			if (this.groundContourType != 0 || this.shadowOffsetY != 0) {
				adjustedFunctionMask = functionMask | 0x2;
			}
			if (this.shadowOffsetX != 0) {
				adjustedFunctionMask |= 0x1;
			}
			if (this.shadowOffsetZ != 0) {
				adjustedFunctionMask |= 0x4;
			}
		}
		if (castShadow) {
			adjustedFunctionMask |= 0x40000;
		}
		@Pc(83) LRUCache referenceCache = this.locTypeList.modelAndShadowCache;
		@Pc(95) ModelAndShadow modelAndShadow;
		synchronized (this.locTypeList.modelAndShadowCache) {
			modelAndShadow = (ModelAndShadow) this.locTypeList.modelAndShadowCache.get(cacheKey);
		}
		@Pc(108) Model model = modelAndShadow == null ? null : modelAndShadow.model;
		@Pc(110) Shadow shadow = null;
		if (model != null && toolkit.compareFunctionMasks(model.ua(), adjustedFunctionMask) == 0) {
			model = modelAndShadow.model;
			shadow = modelAndShadow.shadow;
			if (castShadow && shadow == null) {
				shadow = modelAndShadow.shadow = model.ba(null);
			}
		} else {
			if (model != null) {
				adjustedFunctionMask = toolkit.combineFunctionMasks(adjustedFunctionMask, model.ua());
			}
			@Pc(128) int stationaryFunctionMask = adjustedFunctionMask;
			if (shape == LocShapes.CENTREPIECE_STRAIGHT && rotation > 3) {
				stationaryFunctionMask = adjustedFunctionMask | 0x5;
			}
			model = this.getStationaryModel(rotation, toolkit, shape, stationaryFunctionMask);
			if (model == null) {
				return null;
			}
			if (shape == LocShapes.CENTREPIECE_STRAIGHT && rotation > 3) {
				model.a(2048);
			}
			if (castShadow) {
				shadow = model.ba(null);
			}
			model.s(adjustedFunctionMask);
			modelAndShadow = new ModelAndShadow();
			modelAndShadow.model = model;
			modelAndShadow.shadow = shadow;
			@Pc(186) LRUCache referenceCache2 = this.locTypeList.modelAndShadowCache;
			synchronized (this.locTypeList.modelAndShadowCache) {
				this.locTypeList.modelAndShadowCache.put(modelAndShadow, cacheKey);
			}
		}
		@Pc(243) boolean hasGroundContour = this.groundContourType != 0 && (ground2 != null || ground != null);
		@Pc(259) boolean hasTranslation = this.shadowOffsetX != 0 || this.shadowOffsetY != 0 || this.shadowOffsetZ != 0;
		if (hasGroundContour || hasTranslation) {
			model = model.copy((byte) 0, adjustedFunctionMask, true);
			if (hasGroundContour) {
				model.p(this.groundContourType, this.goundContourModifier, ground2, ground, sceneX, averageHeight, sceneZ);
			}
			if (hasTranslation) {
				model.H(this.shadowOffsetX, this.shadowOffsetY, this.shadowOffsetZ);
			}
			model.s(functionMask);
		} else {
			model = model.copy((byte) 0, functionMask, true);
		}
		LocType.aClass16_31.model = model;
		LocType.aClass16_31.shadow = shadow;
		return LocType.aClass16_31;
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(ILclient!ha;III)Lclient!ka;")
	private Model getStationaryModel(@OriginalArg(0) int rotation, @OriginalArg(1) Toolkit toolkit, @OriginalArg(2) int shape, @OriginalArg(4) int functionMask) {
		@Pc(10) int adjustedAmbient = this.ambient + 64;
		@Pc(15) int adjustedContrast = this.contrast + 850;
		@Pc(17) int initialFunctionMask = functionMask;
		@Pc(32) boolean invert = this.inverted || shape == LocShapes.WALL_L && rotation > 3;
		if (invert) {
			functionMask |= 0x10;
		}
		if (rotation == 0) {
			if (this.resizeX != 128 || this.offsetX != 0) {
				functionMask |= 0x1;
			}
			if (this.resizeZ != 128 || this.offsetZ != 0) {
				functionMask |= 0x4;
			}
		} else {
			functionMask |= 0xD;
		}
		if (this.resizeY != 128 || this.offsetY != 0) {
			functionMask |= 0x2;
		}
		if (this.recolorSrc != null) {
			functionMask |= 0x4000;
		}
		if (this.retextureSrc != null) {
			functionMask |= 0x8000;
		}
		if (this.tintOpacity != 0) {
			functionMask |= 0x80000;
		}
		@Pc(110) Model model = null;
		if (this.shapes == null) {
			return null;
		}
		@Pc(117) int shapeIndex = -1;
		for (@Pc(119) int i = 0; i < this.shapes.length; i++) {
			if (shape == this.shapes[i]) {
				shapeIndex = i;
				break;
			}
		}
		if (shapeIndex == -1) {
			return null;
		}
		@Pc(157) int modelCount = this.objectModelIds[shapeIndex].length;
		if (modelCount > 0) {
			@Pc(166) long uid = (long) toolkit.index;
			for (@Pc(168) int j = 0; j < modelCount; j++) {
				uid = uid * 67783L + (long) this.objectModelIds[shapeIndex][j];
			}
			@Pc(194) LRUCache referenceCache = this.locTypeList.modelsByShapeCache;
			synchronized (this.locTypeList.modelsByShapeCache) {
				model = (Model) this.locTypeList.modelsByShapeCache.get(uid);
			}
			if (model != null) {
				if (model.WA() != adjustedAmbient) {
					functionMask |= 0x1000;
				}
				if (adjustedContrast != model.da()) {
					functionMask |= 0x2000;
				}
			}
			if (model == null || toolkit.compareFunctionMasks(model.ua(), functionMask) != 0) {
				@Pc(247) int meshFunctionMask = functionMask | 0x1F01F;
				if (model != null) {
					meshFunctionMask = toolkit.combineFunctionMasks(meshFunctionMask, model.ua());
				}
				@Pc(257) Mesh mesh = null;
				@Pc(259) Mesh[] meshes = LocType.aClass32Array1;
				synchronized (LocType.aClass32Array1) {
					for (@Pc(263) int k = 0; k < modelCount; k++) {
						@Pc(268) Js5ResourceProvider meshesLock = this.locTypeList.meshResource;
						synchronized (this.locTypeList.meshResource) {
							mesh = Mesh.fromResource(this.locTypeList.meshResource, this.objectModelIds[shapeIndex][k] & 0xFFFF);
						}
						if (mesh == null) {
							return null;
						}
						if (mesh.version < 13) {
							mesh.upscale();
						}
						if (modelCount > 1) {
							LocType.aClass32Array1[k] = mesh;
						}
					}
					if (modelCount > 1) {
						mesh = new Mesh(LocType.aClass32Array1, modelCount);
					}
				}
				model = toolkit.createModel(mesh, meshFunctionMask, this.locTypeList.featureMask, adjustedAmbient, adjustedContrast);
				@Pc(349) LRUCache referenceCache2 = this.locTypeList.modelsByShapeCache;
				synchronized (this.locTypeList.modelsByShapeCache) {
					this.locTypeList.modelsByShapeCache.put(model, uid);
				}
			}
		}
		@Pc(370) Model model2 = model.copy((byte) 0, functionMask, true);
		if (model.WA() != adjustedAmbient) {
			model2.C(adjustedAmbient);
		}
		if (model.da() != adjustedContrast) {
			model2.LA(adjustedContrast);
		}
		if (invert) {
			model2.v();
		}
		if (shape == 4 && rotation > 3) {
			model2.k(2048);
			model2.H(180, 0, -180);
		}
		@Pc(414) int rotationQuarter = rotation & 0x3;
		if (rotationQuarter == 1) {
			model2.k(4096);
		} else if (rotationQuarter == 2) {
			model2.k(8192);
		} else if (rotationQuarter == 3) {
			model2.k(12288);
		}
		@Pc(443) int index;
		if (this.recolorSrc != null) {
			for (index = 0; index < this.recolorSrc.length; index++) {
				if (this.recolorDPalette == null || index >= this.recolorDPalette.length) {
					model2.ia(this.recolorSrc[index], this.recolorDst[index]);
				} else {
					model2.ia(this.recolorSrc[index], ObjectType.locRecolorPalette[this.recolorDPalette[index] & 0xFF]);
				}
			}
		}
		if (this.retextureSrc != null) {
			for (index = 0; index < this.retextureSrc.length; index++) {
				model2.aa(this.retextureSrc[index], this.retextureDst[index]);
			}
		}
		if (this.tintOpacity != 0) {
			model2.adjustColours(this.tintHue, this.tintSaturation, this.tintLightness, this.tintOpacity & 0xFF);
		}
		if (this.resizeX != 128 || this.resizeY != 128 || this.resizeZ != 128) {
			model2.O(this.resizeX, this.resizeY, this.resizeZ);
		}
		if (this.offsetX != 0 || this.offsetY != 0 || this.offsetZ != 0) {
			model2.H(this.offsetX, this.offsetY, this.offsetZ);
		}
		model2.s(initialFunctionMask);
		return model2;
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(II)Z")
	public boolean loadedModels(@OriginalArg(0) int shape) {
		if (this.objectModelIds == null) {
			return true;
		}
		@Pc(19) Js5ResourceProvider meshesLock = this.locTypeList.meshResource;
		synchronized (this.locTypeList.meshResource) {
			for (@Pc(23) int i = 0; i < this.shapes.length; i++) {
				if (this.shapes[i] == shape) {
					for (@Pc(33) int j = 0; j < this.objectModelIds[i].length; j++) {
						if (!this.locTypeList.meshResource.isFileReady(this.objectModelIds[i][j], 0)) {
							return false;
						}
					}
					return true;
				}
			}
			return true;
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(BLclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(9) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decodeOpcode(packet, opcode);
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(Lclient!dd;B)Lclient!iv;")
	public LocType getMultiLoc(@OriginalArg(0) VarDomain varDomain) {
		@Pc(5) int index = -1;
		if (this.varpbitId != -1) {
			index = varDomain.getVarBitValue(this.varpbitId);
		} else if (this.varpId != -1) {
			index = varDomain.getVarValueInt(this.varpId);
		}
		if (index < 0 || this.alternativeIds.length - 1 <= index || this.alternativeIds[index] == -1) {
			@Pc(61) int fallbackId = this.alternativeIds[this.alternativeIds.length - 1];
			return fallbackId == -1 ? null : this.locTypeList.list(fallbackId);
		} else {
			return this.locTypeList.list(this.alternativeIds[index]);
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(BI)Z")
	public boolean hasAnimation(@OriginalArg(1) int animationId) {
		if (animationId == -1) {
			return false;
		} else if (animationId == this.animationId) {
			return true;
		} else {
			if (this.animations != null) {
				for (@Pc(21) int i = 0; i < this.animations.length; i++) {
					if (animationId == this.animations[i]) {
						return true;
					}
				}
			}
			return false;
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(Lclient!cea;IB)V")
	private void decodeOpcode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		@Pc(391) int local391;
		@Pc(401) int local401;
		@Pc(414) int local414;
		@Pc(583) int local583;
		if (opcode == 1 || opcode == 5) {
			if (opcode == 5 && this.locTypeList.lowDetail) {
				this.skipUnusedModelData(packet);
			}
			int shapeCount = packet.readUByte();
			this.objectModelIds = new int[shapeCount][];
			this.shapes = new byte[shapeCount];
			for (int shapeIndex = 0; shapeIndex < shapeCount; shapeIndex++) {
				this.shapes[shapeIndex] = packet.readByte();
				int modelCount = packet.readUByte();
				this.objectModelIds[shapeIndex] = new int[modelCount];
				for (int modelIndex = 0; modelIndex < modelCount; modelIndex++) {
					this.objectModelIds[shapeIndex][modelIndex] = packet.readUShort();
				}
			}
			if (opcode == 5 && !this.locTypeList.lowDetail) {
				this.skipUnusedModelData(packet);
			}
		} else if (opcode == 2) {
			this.name = packet.readString();
		} else if (opcode == 14) {
			this.sizeX = packet.readUByte();
		} else if (opcode == 15) {
			this.sizeY = packet.readUByte();
		} else if (opcode == 17) {
			this.blocksProjectiles = false;
			this.blocksMovement = 0;
		} else if (opcode == 18) {
			this.blocksProjectiles = false;
		} else if (opcode == 19) {
			this.hasActions = packet.readUByte();
		} else if (opcode == 21) {
			this.groundContourType = 1;
		} else if (opcode == 22) {
			this.delayShading = true;
		} else if (opcode == 23) {
			this.occlusionMode = LocOcclusionMode.ALL;
		} else if (opcode == 24) {
			this.animationId = packet.readUShort();
			if (this.animationId == 65535) {
				this.animationId = -1;
			}
		} else if (opcode == 27) {
			this.blocksMovement = 1;
		} else if (opcode == 28) {
			this.decorDisplacement = packet.readUByte() << 2;
		} else if (opcode == 29) {
			this.ambient = packet.readByte();
		} else if (opcode == 39) {
			this.contrast = packet.readByte() * 5;
		} else if (opcode >= 30 && opcode < 35) {
			this.actions[opcode - 30] = packet.readString();
		} else if (opcode == 40) {
			int recolorCount = packet.readUByte();
			this.recolorSrc = new short[recolorCount];
			this.recolorDst = new short[recolorCount];
			for (int recolorIndex = 0; recolorIndex < recolorCount; recolorIndex++) {
				this.recolorSrc[recolorIndex] = (short) packet.readUShort();
				this.recolorDst[recolorIndex] = (short) packet.readUShort();
			}
		} else if (opcode == 41) {
			int retextureCount = packet.readUByte();
			this.retextureDst = new short[retextureCount];
			this.retextureSrc = new short[retextureCount];
			for (int retextureIndex = 0; retextureIndex < retextureCount; retextureIndex++) {
				this.retextureSrc[retextureIndex] = (short) packet.readUShort();
				this.retextureDst[retextureIndex] = (short) packet.readUShort();
			}
		} else if (opcode == 42) {
			int paletteCount = packet.readUByte();
			this.recolorDPalette = new byte[paletteCount];
			for (int paletteIndex = 0; paletteIndex < paletteCount; paletteIndex++) {
				this.recolorDPalette[paletteIndex] = packet.readByte();
			}
		} else if (opcode == 62) {
			this.inverted = true;
		} else if (opcode == 64) {
			this.staticShadow = false;
		} else if (opcode == 65) {
			this.resizeX = packet.readUShort();
		} else if (opcode == 66) {
			this.resizeY = packet.readUShort();
		} else if (opcode == 67) {
			this.resizeZ = packet.readUShort();
		} else if (opcode == 69) {
			this.accessBlockFlags = packet.readUByte();
		} else if (opcode == 70) {
			this.offsetX = packet.readShort() << 2;
		} else if (opcode == 71) {
			this.offsetY = packet.readShort() << 2;
		} else if (opcode == 72) {
			this.offsetZ = packet.readShort() << 2;
		} else if (opcode == 73) {
			this.forceDisplayDecoration = true;
		} else if (opcode == 74) {
			this.ignoreClipOnAltRoute = true;
		} else if (opcode == 75) {
			this.supportsItems = packet.readUByte();
		} else if (opcode == 77 || opcode == 92) {
			this.varpbitId = packet.readUShort();
			if (this.varpbitId == 65535) {
				this.varpbitId = -1;
			}
			this.varpId = packet.readUShort();
			if (this.varpId == 65535) {
				this.varpId = -1;
			}
			int defaultAlternativeId = -1;
			if (opcode == 92) {
				defaultAlternativeId = packet.readUShort();
				if (defaultAlternativeId == 65535) {
					defaultAlternativeId = -1;
				}
			}
			int alternativeCount = packet.readUByte();
			this.alternativeIds = new int[alternativeCount + 2];
			for (int alternativeIndex = 0; alternativeIndex <= alternativeCount; alternativeIndex++) {
				this.alternativeIds[alternativeIndex] = packet.readUShort();
				if (this.alternativeIds[alternativeIndex] == 65535) {
					this.alternativeIds[alternativeIndex] = -1;
				}
			}
			this.alternativeIds[alternativeCount + 1] = defaultAlternativeId;
		} else if (opcode == 78) {
			this.soundId = packet.readUShort();
			this.soundRadius = packet.readUByte();
		} else if (opcode == 79) {
			this.soundMinInterval = packet.readUShort();
			this.soundMaxInterval = packet.readUShort();
			this.soundRadius = packet.readUByte();
			int soundCount = packet.readUByte();
			this.soundGroupIds = new int[soundCount];
			for (int soundIndex = 0; soundIndex < soundCount; soundIndex++) {
				this.soundGroupIds[soundIndex] = packet.readUShort();
			}
		} else if (opcode == 81) {
			this.groundContourType = 2;
			this.goundContourModifier = packet.readUByte() * 256;
		} else if (opcode == 82) {
			this.requiresTextures = true;
		} else if (opcode == 88) {
			this.dynamicShadow = false;
		} else if (opcode == 89) {
			this.replaySequence = false;
		} else if (opcode == 91) {
			this.members = true;
		} else if (opcode == 93) {
			this.groundContourType = 3;
			this.goundContourModifier = packet.readUShort();
		} else if (opcode == 94) {
			this.groundContourType = 4;
		} else if (opcode == 95) {
			this.groundContourType = 5;
			this.goundContourModifier = packet.readShort();
		} else if (opcode == 97) {
			this.mapIconRotates = true;
		} else if (opcode == 98) {
			this.hasAnimation = true;
		} else if (opcode == 99) {
			this.primaryCursorActionIndex = packet.readUByte();
			this.primaryCursor = packet.readUShort();
		} else if (opcode == 100) {
			this.secondaryCursorActionIndex = packet.readUByte();
			this.secondaryCursor = packet.readUShort();
		} else if (opcode == 101) {
			this.mapIconRotation = packet.readUByte();
		} else if (opcode == 102) {
			this.mapIconId = packet.readUShort();
		} else if (opcode == 103) {
			this.occlusionMode = LocOcclusionMode.ROOFS;
		} else if (opcode == 104) {
			this.soundVolume = packet.readUByte();
		} else if (opcode == 105) {
			this.mapIconFlipped = true;
		} else if (opcode == 106) {
			int animationCount = packet.readUByte();
			this.animations = new int[animationCount];
			this.animationOdds = new int[animationCount];
			for (int animationIndex = 0; animationIndex < animationCount; animationIndex++) {
				this.animations[animationIndex] = packet.readUShort();
				int odds = packet.readUByte();
				this.animationOdds[animationIndex] = odds;
				this.animationOddsTotal += odds;
			}
		} else if (opcode == 107) {
			this.mapCategoryid = packet.readUShort();
		} else if (opcode >= 150 && opcode < 155) {
			this.actions[opcode - 150] = packet.readString();
			if (!this.locTypeList.allowMembers) {
				this.actions[opcode - 150] = null;
			}
		} else if (opcode == 160) {
			int questCount = packet.readUByte();
			this.quests = new int[questCount];
			for (int questIndex = 0; questIndex < questCount; questIndex++) {
				this.quests[questIndex] = packet.readUShort();
			}
		} else if (opcode == 162) {
			this.groundContourType = 3;
			this.goundContourModifier = packet.readInt();
		} else if (opcode == 163) {
			this.tintHue = packet.readByte();
			this.tintSaturation = packet.readByte();
			this.tintLightness = packet.readByte();
			this.tintOpacity = packet.readByte();
		} else if (opcode == 164) {
			this.shadowOffsetX = packet.readShort();
		} else if (opcode == 165) {
			this.shadowOffsetY = packet.readShort();
		} else if (opcode == 166) {
			this.shadowOffsetZ = packet.readShort();
		} else if (opcode == 167) {
			this.groundDecorationHeight = packet.readUShort();
		} else if (opcode == 168) {
			this.instrumentSoundEffect = true;
		} else if (opcode == 169) {
			this.instrumentAmbientSound = true;
		} else if (opcode == 170) {
			this.cullY = packet.readUSmart();
		} else if (opcode == 171) {
			this.cullXZ = packet.readUSmart();
		} else if (opcode == 173) {
			this.ambientSoundMaxDelay = packet.readUShort();
			this.ambientSoundMinDelay = packet.readUShort();
		} else if (opcode == 249) {
			int paramCount = packet.readUByte();
			if (this.parameters == null) {
				int bucketCount = Utils.nextPowerOfTwo(paramCount);
				this.parameters = new IterableHashTable(bucketCount);
			}
			for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
				@Pc(579) boolean isString = packet.readUByte() == 1;
				int paramKey = packet.readUMedium();
				@Pc(592) Node node;
				if (isString) {
					node = new StringNode(packet.readString());
				} else {
					node = new IntNode(packet.readInt());
				}
				this.parameters.put((long) paramKey, node);
			}
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(IIIILclient!ha;IIBLclient!s;IIILclient!hg;Lclient!s;)Lclient!ka;")
	public Model getAnimatedModel(@OriginalArg(0) int nextFrame, @OriginalArg(1) int frameCycle, @OriginalArg(2) int shape, @OriginalArg(3) int averageHeight, @OriginalArg(4) Toolkit toolkit, @OriginalArg(5) int rotation, @OriginalArg(6) int functionMask, @OriginalArg(8) Ground ground, @OriginalArg(9) int sceneZ, @OriginalArg(10) int currentFrame, @OriginalArg(11) int sceneX, @OriginalArg(12) SeqType seqType, @OriginalArg(13) Ground ground2) {
		if (LocShapes.isWallDecoration(shape)) {
			shape = LocShapes.WALLDECOR_STRAIGHT_NOOFFSET;
		}
		@Pc(22) long uid = (long) ((this.id << 10) + ((shape << 3) + rotation));
		@Pc(24) int initialFunctionMask = functionMask;
		@Pc(32) long cacheKey = uid | (long) (toolkit.index << 29);
		if (seqType != null) {
			functionMask |= seqType.getFunctionMask(nextFrame, currentFrame, false);
		}
		if (this.groundContourType == 3) {
			functionMask |= 0x7;
		} else {
			if (this.groundContourType != 0 || this.shadowOffsetY != 0) {
				functionMask |= 0x2;
			}
			if (this.shadowOffsetX != 0) {
				functionMask |= 0x1;
			}
			if (this.shadowOffsetZ != 0) {
				functionMask |= 0x4;
			}
		}
		if (shape == LocShapes.CENTREPIECE_STRAIGHT && rotation > 3) {
			functionMask |= 0x5;
		}
		@Pc(104) LRUCache referenceCache = this.locTypeList.modelCache;
		@Pc(114) Model model;
		synchronized (this.locTypeList.modelCache) {
			model = (Model) this.locTypeList.modelCache.get(cacheKey);
		}
		if (model == null || toolkit.compareFunctionMasks(model.ua(), functionMask) != 0) {
			if (model != null) {
				functionMask = toolkit.combineFunctionMasks(functionMask, model.ua());
			}
			model = this.getStationaryModel(rotation, toolkit, shape, functionMask);
			if (model == null) {
				return null;
			}
			referenceCache = this.locTypeList.modelCache;
			synchronized (this.locTypeList.modelCache) {
				this.locTypeList.modelCache.put(model, cacheKey);
			}
		}
		@Pc(169) boolean copied = false;
		if (seqType != null) {
			model = seqType.animateRotatedModel(model, rotation & 0x3, nextFrame, (byte) 1, currentFrame, frameCycle, functionMask);
			copied = true;
		}
		if (shape == LocShapes.CENTREPIECE_STRAIGHT && rotation > 3) {
			if (!copied) {
				model = model.copy((byte) 3, functionMask, true);
				copied = true;
			}
			model.a(2048);
		}
		if (this.groundContourType != 0) {
			if (!copied) {
				copied = true;
				model = model.copy((byte) 3, functionMask, true);
			}
			model.p(this.groundContourType, this.goundContourModifier, ground2, ground, sceneX, averageHeight, sceneZ);
		}
		if (this.shadowOffsetX != 0 || this.shadowOffsetY != 0 || this.shadowOffsetZ != 0) {
			if (!copied) {
				copied = true;
				model = model.copy((byte) 3, functionMask, true);
			}
			model.H(this.shadowOffsetX, this.shadowOffsetY, this.shadowOffsetZ);
		}
		if (copied) {
			model.s(initialFunctionMask);
		}
		return model;
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(Lclient!cea;I)V")
	private void skipUnusedModelData(@OriginalArg(0) Packet packet) {
		@Pc(14) int count = packet.readUByte();
		for (@Pc(16) int i = 0; i < count; i++) {
			packet.currentPosition++;
			@Pc(30) int length = packet.readUByte();
			packet.currentPosition += length * 2;
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(I)Z")
	public boolean isLoaded() {
		if (this.objectModelIds == null) {
			return true;
		}
		@Pc(19) boolean loaded = true;
		@Pc(23) Js5ResourceProvider meshesLock = this.locTypeList.meshResource;
		synchronized (this.locTypeList.meshResource) {
			for (@Pc(27) int i = 0; i < this.objectModelIds.length; i++) {
				for (@Pc(31) int j = 0; j < this.objectModelIds[i].length; j++) {
					loaded &= this.locTypeList.meshResource.isFileReady(this.objectModelIds[i][j], 0);
				}
			}
			return loaded;
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(III)I")
	public int getIntParam(@OriginalArg(0) int key, @OriginalArg(1) int defaultInt) {
		if (this.parameters == null) {
			return defaultInt;
		} else {
			@Pc(24) IntNode intNode = (IntNode) this.parameters.get((long) key);
			return intNode == null ? defaultInt : intNode.value;
		}
	}

	@OriginalMember(owner = "client!iv", name = "c", descriptor = "(I)V")
	public void postDecode() {
		if (this.hasActions == -1) {
			this.hasActions = LocInteractivity.NONINTERACTIVE;
			if (this.shapes != null && this.shapes.length == 1 && this.shapes[0] == 10) {
				this.hasActions = LocInteractivity.INTERACTIVE;
			}
			for (@Pc(30) int i = 0; i < 5; i++) {
				if (this.actions[i] != null) {
					this.hasActions = LocInteractivity.INTERACTIVE;
					break;
				}
			}
		}
		if (this.supportsItems == -1) {
			this.supportsItems = this.blocksMovement == 0 ? 0 : 1;
		}
	}

	@OriginalMember(owner = "client!iv", name = "d", descriptor = "(I)I")
	public int randomAnimation() {
		if (this.animations == null) {
			return -1;
		}
		@Pc(15) int random = (int) (Math.random() * (double) this.animationOddsTotal);
		@Pc(17) int i;
		for (i = 0; this.animationOdds[i] <= random; i++) {
			random -= this.animationOdds[i];
		}
		return this.animations[i];
	}
	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(IZI)Z")
	public static boolean isModelReady(@OriginalArg(0) int shape, @OriginalArg(2) int locId) {
		@Pc(8) LocType locType = TypeLists.LOC.list(locId);
		if (shape == 11) {
			shape = 10;
		}
		if (shape >= LocShapes.WALLDECOR_STRAIGHT_OFFSET && shape <= LocShapes.WALLDECOR_DIAGONAL_BOTH) {
			shape = LocShapes.WALLDECOR_STRAIGHT_NOOFFSET;
		}
		return locType.loadedModels(shape);
	}
	@OriginalMember(owner = "client!ec", name = "k", descriptor = "[Lclient!bh;")
	public static final Mesh[] aClass32Array1 = new Mesh[4];

	@OriginalMember(owner = "client!vl", name = "p", descriptor = "Lclient!ak;")
	public static final ModelAndShadow aClass16_31 = new ModelAndShadow();
}
