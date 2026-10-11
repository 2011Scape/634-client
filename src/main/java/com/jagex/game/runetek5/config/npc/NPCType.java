package com.jagex.game.runetek5.config.npc;

import com.jagex.game.runetek5.config.sequence.frameset.FrameSet;
import com.jagex.game.runetek5.config.sequence.PathingEntitySequence;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.config.sequence.SeqTypeList;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.node.StringNode;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.bas.BasType;
import com.jagex.game.runetek5.config.bas.BasTypeList;
import com.jagex.game.runetek5.vardomain.VarDomain;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.npctype.NPCTypeList;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!bb")
public final class NPCType {

	@OriginalMember(owner = "client!bb", name = "d", descriptor = "[S")
	private short[] recolorSrc;

	@OriginalMember(owner = "client!bb", name = "f", descriptor = "B")
	private byte tintLightness;

	@OriginalMember(owner = "client!bb", name = "r", descriptor = "[I")
	public int[] quests;

	@OriginalMember(owner = "client!bb", name = "t", descriptor = "I")
	public int id;

	@OriginalMember(owner = "client!bb", name = "x", descriptor = "Lclient!vda;")
	public NPCTypeList npcTypeList;

	@OriginalMember(owner = "client!bb", name = "B", descriptor = "[S")
	private short[] recolorDst;

	@OriginalMember(owner = "client!bb", name = "J", descriptor = "B")
	private byte tintSaturation;

	@OriginalMember(owner = "client!bb", name = "T", descriptor = "Z")
	public boolean instrumentSoundEffect;

	@OriginalMember(owner = "client!bb", name = "X", descriptor = "[B")
	private byte[] recolorDPalette;

	@OriginalMember(owner = "client!bb", name = "Z", descriptor = "B")
	private byte tintHue;

	@OriginalMember(owner = "client!bb", name = "ab", descriptor = "[I")
	public int[] alternativeIds;

	@OriginalMember(owner = "client!bb", name = "db", descriptor = "[I")
	private int[] headModels;

	@OriginalMember(owner = "client!bb", name = "jb", descriptor = "Lclient!eq;")
	private IterableHashTable parameters;

	@OriginalMember(owner = "client!bb", name = "lb", descriptor = "[[I")
	private int[][] modelOffsets;

	@OriginalMember(owner = "client!bb", name = "ob", descriptor = "[S")
	private short[] retextureDst;

	@OriginalMember(owner = "client!bb", name = "qb", descriptor = "[S")
	private short[] retextureSrc;

	@OriginalMember(owner = "client!bb", name = "zb", descriptor = "[I")
	private int[] modelIds;

	@OriginalMember(owner = "client!bb", name = "i", descriptor = "I")
	public int shadowSize = -1;

	@OriginalMember(owner = "client!bb", name = "z", descriptor = "S")
	public short shadowColorDst = 0;

	@OriginalMember(owner = "client!bb", name = "v", descriptor = "I")
	private int scaleXZ = 128;

	@OriginalMember(owner = "client!bb", name = "k", descriptor = "I")
	public int primaryCursorActionIndex = -1;

	@OriginalMember(owner = "client!bb", name = "m", descriptor = "I")
	public int sizeShift = 0;

	@OriginalMember(owner = "client!bb", name = "D", descriptor = "I")
	private int varp = -1;

	@OriginalMember(owner = "client!bb", name = "u", descriptor = "Z")
	public boolean highPriority = false;

	@OriginalMember(owner = "client!bb", name = "l", descriptor = "I")
	public int overheadSprite = -1;

	@OriginalMember(owner = "client!bb", name = "E", descriptor = "B")
	public byte respawnDirection = 4;

	@OriginalMember(owner = "client!bb", name = "F", descriptor = "I")
	public int ambientSoundVolume = 255;

	@OriginalMember(owner = "client!bb", name = "p", descriptor = "I")
	public int ambientSoundMaxDelay = 256;

	@OriginalMember(owner = "client!bb", name = "W", descriptor = "I")
	public int healthBarSpriteGroup = -1;

	@OriginalMember(owner = "client!bb", name = "Y", descriptor = "I")
	public int headIcons = -1;

	@OriginalMember(owner = "client!bb", name = "n", descriptor = "I")
	public int secondaryCursor = -1;

	@OriginalMember(owner = "client!bb", name = "M", descriptor = "I")
	public int ambientSoundMinDelay = 256;

	@OriginalMember(owner = "client!bb", name = "o", descriptor = "S")
	public short shadowColorSrc = 0;

	@OriginalMember(owner = "client!bb", name = "y", descriptor = "[Ljava/lang/String;")
	public final String[] actions = new String[5];

	@OriginalMember(owner = "client!bb", name = "H", descriptor = "I")
	private int scaleY = 128;

	@OriginalMember(owner = "client!bb", name = "C", descriptor = "B")
	public byte shadowAlphaSrc = -96;

	@OriginalMember(owner = "client!bb", name = "L", descriptor = "Z")
	public boolean lowPriority = false;

	@OriginalMember(owner = "client!bb", name = "s", descriptor = "I")
	public int walkingSoundEffect = -1;

	@OriginalMember(owner = "client!bb", name = "hb", descriptor = "B")
	public byte gameType = -1;

	@OriginalMember(owner = "client!bb", name = "eb", descriptor = "Z")
	public boolean mediumPriority = false;

	@OriginalMember(owner = "client!bb", name = "gb", descriptor = "I")
	public int mecId = -1;

	@OriginalMember(owner = "client!bb", name = "V", descriptor = "Ljava/lang/String;")
	public String name = "null";

	@OriginalMember(owner = "client!bb", name = "fb", descriptor = "I")
	public int secondaryBarSpriteGroup = -1;

	@OriginalMember(owner = "client!bb", name = "U", descriptor = "I")
	public int primaryCursor = -1;

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "B")
	public byte walkingFlags = 0;

	@OriginalMember(owner = "client!bb", name = "R", descriptor = "I")
	private int varpbit = -1;

	@OriginalMember(owner = "client!bb", name = "nb", descriptor = "I")
	public int iconHeight = -1;

	@OriginalMember(owner = "client!bb", name = "g", descriptor = "Z")
	public boolean delayMovement = true;

	@OriginalMember(owner = "client!bb", name = "w", descriptor = "B")
	public byte shadowAlphaDst = -16;

	@OriginalMember(owner = "client!bb", name = "P", descriptor = "I")
	public int npcBasId = -1;

	@OriginalMember(owner = "client!bb", name = "vb", descriptor = "I")
	private int ambient = 0;

	@OriginalMember(owner = "client!bb", name = "pb", descriptor = "I")
	public int ambientSoundMinHearDistance = 0;

	@OriginalMember(owner = "client!bb", name = "N", descriptor = "I")
	public int idleSoundEffect = -1;

	@OriginalMember(owner = "client!bb", name = "c", descriptor = "I")
	public int runningSoundEffect = -1;

	@OriginalMember(owner = "client!bb", name = "K", descriptor = "I")
	public int combatLevel = -1;

	@OriginalMember(owner = "client!bb", name = "e", descriptor = "I")
	public int turnDirection = 32;

	@OriginalMember(owner = "client!bb", name = "Cb", descriptor = "B")
	private byte tintOpacity = 0;

	@OriginalMember(owner = "client!bb", name = "ib", descriptor = "I")
	public int secondaryCursorActionIndex = -1;

	@OriginalMember(owner = "client!bb", name = "sb", descriptor = "I")
	public int teleportSoundEffect = -1;

	@OriginalMember(owner = "client!bb", name = "S", descriptor = "Z")
	public boolean shadowed = true;

	@OriginalMember(owner = "client!bb", name = "wb", descriptor = "I")
	public int size = 1;

	@OriginalMember(owner = "client!bb", name = "yb", descriptor = "I")
	public int attackCursor = -1;

	@OriginalMember(owner = "client!bb", name = "tb", descriptor = "Z")
	public boolean visible = true;

	@OriginalMember(owner = "client!bb", name = "ub", descriptor = "Z")
	public boolean displayOnMinimap = true;

	@OriginalMember(owner = "client!bb", name = "Db", descriptor = "I")
	private int contrast = 0;

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(I)Z")
	public boolean hasSounds() {
		if (this.alternativeIds == null) {
			return this.walkingSoundEffect != -1 || this.idleSoundEffect != -1 || this.runningSoundEffect != -1;
		}
		for (@Pc(28) int i = 0; i < this.alternativeIds.length; i++) {
			if (this.alternativeIds[i] != -1) {
				@Pc(46) NPCType type = this.npcTypeList.list(this.alternativeIds[i]);
				if (type.walkingSoundEffect != -1 || type.idleSoundEffect != -1 || type.runningSoundEffect != -1) {
					return true;
				}
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(Lclient!dd;I)Lclient!bb;")
	public NPCType getMultiNpc(@OriginalArg(0) VarDomain varDomain) {
		@Pc(5) int varValue = -1;
		if (this.varpbit != -1) {
			varValue = varDomain.getVarBitValue(this.varpbit);
		} else if (this.varp != -1) {
			varValue = varDomain.getVarValueInt(this.varp);
		}
		if (varValue < 0 || varValue >= this.alternativeIds.length - 1 || this.alternativeIds[varValue] == -1) {
			@Pc(55) int defaultId = this.alternativeIds[this.alternativeIds.length - 1];
			return defaultId == -1 ? null : this.npcTypeList.list(defaultId);
		} else {
			return this.npcTypeList.list(this.alternativeIds[varValue]);
		}
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(Lclient!cea;II)V")
	private void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		@Pc(16) int local16;
		@Pc(22) int local22;
		if (opcode == 1) {
			int modelCount = packet.readUByte();
			this.modelIds = new int[modelCount];
			for (int modelIndex = 0; modelIndex < modelCount; modelIndex++) {
				this.modelIds[modelIndex] = packet.readUShort();
				if (this.modelIds[modelIndex] == 65535) {
					this.modelIds[modelIndex] = -1;
				}
			}
		} else if (opcode == 2) {
			this.name = packet.readString();
		} else if (opcode == 12) {
			this.size = packet.readUByte();
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
			this.retextureSrc = new short[retextureCount];
			this.retextureDst = new short[retextureCount];
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
		} else if (opcode == 60) {
			int headModelCount = packet.readUByte();
			this.headModels = new int[headModelCount];
			for (int headModelIndex = 0; headModelIndex < headModelCount; headModelIndex++) {
				this.headModels[headModelIndex] = packet.readUShort();
			}
		} else if (opcode == 93) {
			this.displayOnMinimap = false;
		} else if (opcode == 95) {
			this.combatLevel = packet.readUShort();
		} else if (opcode == 97) {
			this.scaleXZ = packet.readUShort();
		} else if (opcode == 98) {
			this.scaleY = packet.readUShort();
		} else if (opcode == 99) {
			this.highPriority = true;
		} else if (opcode == 100) {
			this.ambient = packet.readByte();
		} else if (opcode == 101) {
			this.contrast = packet.readByte() * 5;
		} else if (opcode == 102) {
			this.headIcons = packet.readUShort();
		} else if (opcode == 103) {
			this.turnDirection = packet.readUShort();
		} else {
			@Pc(267) int local267;
			if (opcode == 106 || opcode == 118) {
				this.varpbit = packet.readUShort();
				if (this.varpbit == 65535) {
					this.varpbit = -1;
				}
				this.varp = packet.readUShort();
				if (this.varp == 65535) {
					this.varp = -1;
				}
				int defaultAlternativeId = -1;
				if (opcode == 118) {
					defaultAlternativeId = packet.readUShort();
					if (defaultAlternativeId == 65535) {
						defaultAlternativeId = -1;
					}
				}
				int lastAlternativeIndex = packet.readUByte();
				this.alternativeIds = new int[lastAlternativeIndex + 2];
				for (int alternativeIndex = 0; alternativeIndex <= lastAlternativeIndex; alternativeIndex++) {
					this.alternativeIds[alternativeIndex] = packet.readUShort();
					if (this.alternativeIds[alternativeIndex] == 65535) {
						this.alternativeIds[alternativeIndex] = -1;
					}
				}
				this.alternativeIds[lastAlternativeIndex + 1] = defaultAlternativeId;
			} else if (opcode == 107) {
				this.visible = false;
			} else if (opcode == 109) {
				this.delayMovement = false;
			} else if (opcode == 111) {
				this.shadowed = false;
			} else if (opcode == 113) {
				this.shadowColorSrc = (short) packet.readUShort();
				this.shadowColorDst = (short) packet.readUShort();
			} else if (opcode == 114) {
				this.shadowAlphaSrc = packet.readByte();
				this.shadowAlphaDst = packet.readByte();
			} else if (opcode == 119) {
				this.walkingFlags = packet.readByte();
			} else if (opcode == 121) {
				this.modelOffsets = new int[this.modelIds.length][];
				int offsetCount = packet.readUByte();
				for (int offsetIndex = 0; offsetIndex < offsetCount; offsetIndex++) {
					int modelIndex = packet.readUByte();
					@Pc(377) int[] offsets = this.modelOffsets[modelIndex] = new int[3];
					offsets[0] = packet.readByte();
					offsets[1] = packet.readByte();
					offsets[2] = packet.readByte();
				}
			} else if (opcode == 122) {
				this.healthBarSpriteGroup = packet.readUShort();
			} else if (opcode == 123) {
				this.iconHeight = packet.readUShort();
			} else if (opcode == 125) {
				this.respawnDirection = packet.readByte();
			} else if (opcode == 127) {
				this.npcBasId = packet.readUShort();
			} else if (opcode == 128) {
				packet.readUByte();
			} else if (opcode == 134) {
				this.walkingSoundEffect = packet.readUShort();
				if (this.walkingSoundEffect == 65535) {
					this.walkingSoundEffect = -1;
				}
				this.teleportSoundEffect = packet.readUShort();
				if (this.teleportSoundEffect == 65535) {
					this.teleportSoundEffect = -1;
				}
				this.idleSoundEffect = packet.readUShort();
				if (this.idleSoundEffect == 65535) {
					this.idleSoundEffect = -1;
				}
				this.runningSoundEffect = packet.readUShort();
				if (this.runningSoundEffect == 65535) {
					this.runningSoundEffect = -1;
				}
				this.ambientSoundMinHearDistance = packet.readUByte();
			} else if (opcode == 135) {
				this.primaryCursorActionIndex = packet.readUByte();
				this.primaryCursor = packet.readUShort();
			} else if (opcode == 136) {
				this.secondaryCursorActionIndex = packet.readUByte();
				this.secondaryCursor = packet.readUShort();
			} else if (opcode == 137) {
				this.attackCursor = packet.readUShort();
			} else if (opcode == 138) {
				this.overheadSprite = packet.readUShort();
			} else if (opcode == 139) {
				this.secondaryBarSpriteGroup = packet.readUShort();
			} else if (opcode == 140) {
				this.ambientSoundVolume = packet.readUByte();
			} else if (opcode == 141) {
				this.lowPriority = true;
			} else if (opcode == 142) {
				this.mecId = packet.readUShort();
			} else if (opcode == 143) {
				this.mediumPriority = true;
			} else if (opcode >= 150 && opcode < 155) {
				this.actions[opcode - 150] = packet.readString();
				if (!this.npcTypeList.allowMembers) {
					this.actions[opcode - 150] = null;
				}
			} else if (opcode == 155) {
				this.tintHue = packet.readByte();
				this.tintSaturation = packet.readByte();
				this.tintLightness = packet.readByte();
				this.tintOpacity = packet.readByte();
			} else if (opcode == 158) {
				this.gameType = 1;
			} else if (opcode == 159) {
				this.gameType = 0;
			} else if (opcode == 160) {
				int questCount = packet.readUByte();
				this.quests = new int[questCount];
				for (int questIndex = 0; questIndex < questCount; questIndex++) {
					this.quests[questIndex] = packet.readUShort();
				}
			} else if (opcode == 162) {
				this.instrumentSoundEffect = true;
			} else if (opcode == 163) {
				this.shadowSize = packet.readUByte();
			} else if (opcode == 164) {
				this.ambientSoundMaxDelay = packet.readUShort();
				this.ambientSoundMinDelay = packet.readUShort();
			} else if (opcode == 165) {
				this.sizeShift = packet.readUByte();
			} else if (opcode == 249) {
				int paramCount = packet.readUByte();
				if (this.parameters == null) {
					int bucketCount = Utils.nextPowerOfTwo(paramCount);
					this.parameters = new IterableHashTable(bucketCount);
				}
				for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
					@Pc(687) boolean isString = packet.readUByte() == 1;
					@Pc(691) int key = packet.readUMedium();
					@Pc(700) Node node;
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

	@OriginalMember(owner = "client!bb", name = "b", descriptor = "(Lclient!dd;I)Z")
	public boolean resolvesToNpc(@OriginalArg(0) VarDomain varDomain) {
		if (this.alternativeIds == null) {
			return true;
		}
		@Pc(10) int varValue = -1;
		if (this.varpbit != -1) {
			varValue = varDomain.getVarBitValue(this.varpbit);
		} else if (this.varp != -1) {
			varValue = varDomain.getVarValueInt(this.varp);
		}
		if (varValue < 0 || varValue >= this.alternativeIds.length - 1 || this.alternativeIds[varValue] == -1) {
			@Pc(61) int defaultId = this.alternativeIds[this.alternativeIds.length - 1];
			return defaultId != -1;
		} else {
			return true;
		}
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(ILclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(5) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}

	@OriginalMember(owner = "client!bb", name = "b", descriptor = "(I)V")
	public void postDecode() {
		if (this.modelIds == null) {
			this.modelIds = new int[0];
		}
		if (this.gameType != -1) {
			return;
		}
		if (Game.RUNESCAPE == this.npcTypeList.game) {
			this.gameType = 1;
		} else {
			this.gameType = 0;
		}
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(I[Lclient!qca;Lclient!lfa;ZLclient!hg;ILclient!uga;ILclient!hg;Lclient!dd;Lclient!ha;I[IIIII)Lclient!ka;")
	public Model getBodyModel(@OriginalArg(0) int rotation, @OriginalArg(1) PathingEntitySequence[] sequences, @OriginalArg(2) SeqTypeList seqTypeList, @OriginalArg(4) SeqType seqType, @OriginalArg(5) int moveFrameDuration, @OriginalArg(6) BasTypeList basTypeList, @OriginalArg(7) int animFrameIndex, @OriginalArg(8) SeqType seqType2, @OriginalArg(9) VarDomain varDomain, @OriginalArg(10) Toolkit toolkit, @OriginalArg(11) int moveNextFrameIndex, @OriginalArg(12) int[] wornRotations, @OriginalArg(13) int animNextFrameIndex, @OriginalArg(14) int moveFrameIndex, @OriginalArg(15) int initialFunctionMask, @OriginalArg(16) int animFrameDuration) {
		if (this.alternativeIds != null) {
			@Pc(13) NPCType npcType = this.getMultiNpc(varDomain);
			return npcType == null ? null : npcType.getBodyModel(rotation, sequences, seqTypeList, seqType, moveFrameDuration, basTypeList, animFrameIndex, seqType2, varDomain, toolkit, moveNextFrameIndex, wornRotations, animNextFrameIndex, moveFrameIndex, initialFunctionMask, animFrameDuration);
		}
		@Pc(39) int functionMask = initialFunctionMask;
		if (this.scaleY != 128) {
			functionMask = initialFunctionMask | 0x2;
		}
		if (this.scaleXZ != 128) {
			functionMask |= 0x5;
		}
		@Pc(63) boolean animated = seqType2 != null || seqType != null;
		@Pc(65) boolean modifiesAlpha = false;
		@Pc(67) boolean modifiesColor = false;
		@Pc(69) boolean lights = false;
		@Pc(71) boolean modifiesBillboard = false;
		@Pc(78) int sequenceCount = sequences == null ? 0 : sequences.length;
		@Pc(112) int local112;
		@Pc(117) int local117;
		for (@Pc(80) int i = 0; i < sequenceCount; i++) {
			NPCType.aClass2_Sub6_Sub9Array1[i] = null;
			if (sequences[i] != null) {
				@Pc(98) SeqType seqType3 = seqTypeList.list(sequences[i].sequenceId);
				if (seqType3.frames != null) {
					animated = true;
					NPCType.aClass130Array2[i] = seqType3;
					int currentFrameIndex = sequences[i].currentFrameId;
					int nextFrameIndex = sequences[i].nextFrameId;
					@Pc(122) int frame = seqType3.frames[currentFrameIndex];
					NPCType.aClass2_Sub6_Sub9Array1[i] = seqTypeList.listFrameSet(frame >>> 16);
					@Pc(135) int frameId = frame & 0xFFFF;
					NPCType.ANIM_FRAME_IDS[i] = frameId;
					if (NPCType.aClass2_Sub6_Sub9Array1[i] != null) {
						modifiesColor |= NPCType.aClass2_Sub6_Sub9Array1[i].modifiesColors(frameId);
						modifiesAlpha |= NPCType.aClass2_Sub6_Sub9Array1[i].modifiesAlpha(frameId);
						modifiesBillboard |= NPCType.aClass2_Sub6_Sub9Array1[i].modifiesBillboards(frameId);
						lights |= seqType3.lights;
					}
					if ((seqType3.tweened || SeqType.forceTweening) && nextFrameIndex != -1 && nextFrameIndex < seqType3.frames.length) {
						NPCType.ANIM_FRAME_LENGTHS[i] = seqType3.durations[currentFrameIndex];
						NPCType.ANIM_FRAME_ELAPSED[i] = sequences[i].currentFrameDuration;
						@Pc(207) int nextFrame = seqType3.frames[nextFrameIndex];
						NPCType.aClass2_Sub6_Sub9Array2[i] = seqTypeList.listFrameSet(nextFrame >>> 16);
						@Pc(220) int nextFrameId = nextFrame & 0xFFFF;
						NPCType.ANIM_NEXT_FRAME_IDS[i] = nextFrameId;
						if (NPCType.aClass2_Sub6_Sub9Array2[i] != null) {
							modifiesColor |= NPCType.aClass2_Sub6_Sub9Array2[i].modifiesColors(nextFrameId);
							modifiesAlpha |= NPCType.aClass2_Sub6_Sub9Array2[i].modifiesAlpha(nextFrameId);
							modifiesBillboard |= NPCType.aClass2_Sub6_Sub9Array2[i].modifiesBillboards(nextFrameId);
						}
					} else {
						NPCType.ANIM_FRAME_LENGTHS[i] = 0;
						NPCType.ANIM_FRAME_ELAPSED[i] = 0;
						NPCType.aClass2_Sub6_Sub9Array2[i] = null;
						NPCType.ANIM_NEXT_FRAME_IDS[i] = -1;
					}
				}
			}
		}
		@Pc(283) int animFrame = -1;
		int animNextFrame = -1;
		int animFrameLength = 0;
		@Pc(289) FrameSet animFrameset = null;
		@Pc(291) FrameSet animFrameset2 = null;
		@Pc(293) int moveFrame = -1;
		@Pc(295) int moveNextFrame = -1;
		@Pc(297) int moveFrameLength = 0;
		@Pc(299) FrameSet animFrameset3 = null;
		@Pc(301) FrameSet animFrameset4 = null;
		if (animated) {
			functionMask |= 0x20;
			@Pc(318) int frameSetId;
			@Pc(382) int nextFrameSetId;
			if (seqType2 != null) {
				animFrame = seqType2.frames[animFrameIndex];
				frameSetId = animFrame >>> 16;
				animFrame &= 0xFFFF;
				animFrameset = seqTypeList.listFrameSet(frameSetId);
				if (animFrameset != null) {
					modifiesColor |= animFrameset.modifiesColors(animFrame);
					modifiesAlpha |= animFrameset.modifiesAlpha(animFrame);
					modifiesBillboard |= animFrameset.modifiesBillboards(animFrame);
					lights |= seqType2.lights;
				}
				if ((seqType2.tweened || SeqType.forceTweening) && animNextFrameIndex != -1 && seqType2.frames.length > animNextFrameIndex) {
					animNextFrame = seqType2.frames[animNextFrameIndex];
					animFrameLength = seqType2.durations[animFrameIndex];
					nextFrameSetId = animNextFrame >>> 16;
					if (frameSetId == nextFrameSetId) {
						animFrameset2 = animFrameset;
					} else {
						animFrameset2 = seqTypeList.listFrameSet(nextFrameSetId);
					}
					animNextFrame &= 0xFFFF;
					if (animFrameset2 != null) {
						modifiesColor |= animFrameset2.modifiesColors(animNextFrame);
						modifiesAlpha |= animFrameset2.modifiesAlpha(animNextFrame);
						modifiesBillboard |= animFrameset2.modifiesBillboards(animNextFrame);
					}
				}
			}
			if (seqType != null) {
				moveFrame = seqType.frames[moveFrameIndex];
				frameSetId = moveFrame >>> 16;
				animFrameset3 = seqTypeList.listFrameSet(frameSetId);
				moveFrame &= 0xFFFF;
				if (animFrameset3 != null) {
					modifiesColor |= animFrameset3.modifiesColors(moveFrame);
					modifiesAlpha |= animFrameset3.modifiesAlpha(moveFrame);
					modifiesBillboard |= animFrameset3.modifiesBillboards(moveFrame);
					lights |= seqType.lights;
				}
				if ((seqType.tweened || SeqType.forceTweening) && moveNextFrameIndex != -1 && seqType.frames.length > moveNextFrameIndex) {
					moveFrameLength = seqType.durations[moveFrameIndex];
					moveNextFrame = seqType.frames[moveNextFrameIndex];
					nextFrameSetId = moveNextFrame >>> 16;
					if (nextFrameSetId == frameSetId) {
						animFrameset4 = animFrameset3;
					} else {
						animFrameset4 = seqTypeList.listFrameSet(nextFrameSetId);
					}
					moveNextFrame &= 0xFFFF;
					if (animFrameset4 != null) {
						modifiesColor |= animFrameset4.modifiesColors(moveNextFrame);
						modifiesAlpha |= animFrameset4.modifiesAlpha(moveNextFrame);
						modifiesBillboard |= animFrameset4.modifiesBillboards(moveNextFrame);
					}
				}
			}
			if (modifiesColor) {
				functionMask |= 0x80;
			}
			if (modifiesAlpha) {
				functionMask |= 0x100;
			}
			if (lights) {
				functionMask |= 0x200;
			}
			if (modifiesBillboard) {
				functionMask |= 0x400;
			}
		}
		@Pc(566) long cacheKey = (long) (toolkit.index << 16 | this.id);
		@Pc(570) LRUCache referenceCache = this.npcTypeList.modelCache;
		@Pc(580) Model model;
		synchronized (this.npcTypeList.modelCache) {
			model = (Model) this.npcTypeList.modelCache.get(cacheKey);
		}
		@Pc(588) BasType basType = null;
		if (this.npcBasId != -1) {
			basType = basTypeList.list(this.npcBasId);
		}
		@Pc(621) boolean local621;
		@Pc(629) int local629;
		@Pc(789) int local789;
		@Pc(771) int index;
		if (model == null || (functionMask & model.ua()) != functionMask) {
			if (model != null) {
				functionMask |= model.ua();
			}
			@Pc(619) int modelFunctionMask = functionMask;
			boolean meshesNotReady = false;
			@Pc(625) Js5ResourceProvider meshResource = this.npcTypeList.meshes;
			synchronized (this.npcTypeList.meshes) {
				for (int modelIndex = 0; modelIndex < this.modelIds.length; modelIndex++) {
					if (this.modelIds[modelIndex] != -1 && !this.npcTypeList.meshes.isFileReady(this.modelIds[modelIndex], 0)) {
						meshesNotReady = true;
					}
				}
			}
			if (meshesNotReady) {
				return null;
			}
			@Pc(675) Mesh[] meshes = new Mesh[this.modelIds.length];
			for (int modelIndex = 0; modelIndex < this.modelIds.length; modelIndex++) {
				if (this.modelIds[modelIndex] != -1) {
					@Pc(690) Js5ResourceProvider meshResource2 = this.npcTypeList.meshes;
					synchronized (this.npcTypeList.meshes) {
						meshes[modelIndex] = Mesh.fromResource(this.npcTypeList.meshes, this.modelIds[modelIndex]);
					}
					if (meshes[modelIndex] != null) {
						if (meshes[modelIndex].version < 13) {
							meshes[modelIndex].upscale();
						}
						if (this.modelOffsets != null && this.modelOffsets[modelIndex] != null) {
							meshes[modelIndex].translate(this.modelOffsets[modelIndex][2], this.modelOffsets[modelIndex][0], this.modelOffsets[modelIndex][1]);
						}
					}
				}
			}
			if (basType != null && basType.equipmentTransforms != null) {
				for (index = 0; index < basType.equipmentTransforms.length; index++) {
					if (index < meshes.length && meshes[index] != null) {
						int translateX = 0;
						@Pc(791) int translateY = 0;
						@Pc(793) int translateZ = 0;
						@Pc(795) int rotateX = 0;
						@Pc(797) int rotateY = 0;
						@Pc(799) int rotateZ = 0;
						if (basType.equipmentTransforms[index] != null) {
							rotateY = basType.equipmentTransforms[index][4] << 3;
							rotateZ = basType.equipmentTransforms[index][5] << 3;
							translateY = basType.equipmentTransforms[index][1];
							translateZ = basType.equipmentTransforms[index][2];
							rotateX = basType.equipmentTransforms[index][3] << 3;
							translateX = basType.equipmentTransforms[index][0];
						}
						if (rotateX != 0 || rotateY != 0 || rotateZ != 0) {
							meshes[index].rotateWithoutNormals(rotateY, rotateZ, rotateX);
						}
						if (translateX != 0 || translateY != 0 || translateZ != 0) {
							meshes[index].translate(translateZ, translateX, translateY);
						}
					}
				}
			}
			@Pc(908) Mesh mesh;
			if (meshes.length == 1) {
				mesh = meshes[0];
			} else {
				mesh = new Mesh(meshes, meshes.length);
			}
			if (this.recolorSrc != null) {
				modelFunctionMask = functionMask | 0x4000;
			}
			if (this.retextureSrc != null) {
				modelFunctionMask |= 0x8000;
			}
			if (this.tintOpacity != 0) {
				modelFunctionMask |= 0x80000;
			}
			model = toolkit.createModel(mesh, modelFunctionMask, this.npcTypeList.featureMask, this.ambient + 64, this.contrast + 850);
			if (this.recolorSrc != null) {
				for (index = 0; index < this.recolorSrc.length; index++) {
					if (this.recolorDPalette == null || index >= this.recolorDPalette.length) {
						model.ia(this.recolorSrc[index], this.recolorDst[index]);
					} else {
						model.ia(this.recolorSrc[index], NPCType.npcRecolorPalette[this.recolorDPalette[index] & 0xFF]);
					}
				}
			}
			if (this.retextureSrc != null) {
				for (index = 0; index < this.retextureSrc.length; index++) {
					model.aa(this.retextureSrc[index], this.retextureDst[index]);
				}
			}
			if (this.tintOpacity != 0) {
				model.adjustColours(this.tintHue, this.tintSaturation, this.tintLightness, this.tintOpacity & 0xFF);
			}
			model.s(functionMask);
			@Pc(1059) LRUCache referenceCache2 = this.npcTypeList.modelCache;
			synchronized (this.npcTypeList.modelCache) {
				this.npcTypeList.modelCache.put(model, (long) (this.id | toolkit.index << 16));
			}
		}
		@Pc(1087) Model model2 = model.copy((byte) 4, functionMask, true);
		boolean hasWornRotation = false;
		if (wornRotations != null) {
			for (@Pc(1093) int j = 0; j < 12; j++) {
				if (wornRotations[j] != -1) {
					hasWornRotation = true;
				}
			}
		}
		if (!animated && !hasWornRotation) {
			return model2;
		}
		@Pc(1115) Matrix[] matrixes = null;
		if (basType != null) {
			matrixes = basType.getSubmeshMatrices(toolkit);
		}
		@Pc(1128) int slot;
		if (hasWornRotation && matrixes != null) {
			for (slot = 0; slot < 12; slot++) {
				if (matrixes[slot] != null) {
					model2.transform(matrixes[slot], 0x1 << slot, true);
				}
			}
		}
		slot = 0;
		int submeshMask = 1;
		while (slot < sequenceCount) {
			if (NPCType.aClass2_Sub6_Sub9Array1[slot] != null) {
				model2.animateUnrotatedSubmesh(NPCType.ANIM_FRAME_ELAPSED[slot] - 1, null, submeshMask, NPCType.ANIM_FRAME_IDS[slot], NPCType.ANIM_NEXT_FRAME_IDS[slot], NPCType.aClass2_Sub6_Sub9Array2[slot], NPCType.aClass2_Sub6_Sub9Array1[slot], NPCType.aClass130Array2[slot].lights, NPCType.ANIM_FRAME_LENGTHS[slot]);
			}
			slot++;
			submeshMask <<= 0x1;
		}
		if (hasWornRotation) {
			for (index = 0; index < 12; index++) {
				if (wornRotations[index] != -1) {
					int angle = wornRotations[index] - rotation;
					angle &= 0x3FFF;
					@Pc(1223) Matrix matrix = toolkit.createMatrix();
					matrix.rotate(angle);
					model2.transform(matrix, 0x1 << index, false);
				}
			}
		}
		if (hasWornRotation && matrixes != null) {
			for (index = 0; index < 12; index++) {
				if (matrixes[index] != null) {
					model2.transform(matrixes[index], 0x1 << index, false);
				}
			}
		}
		if (animFrameset != null && animFrameset3 != null) {
			model2.doubleAnimation(animFrameset2, animFrameLength, moveFrameLength, animFrameset3, animFrame, moveFrame, animFrameDuration - 1, animFrameset, seqType2.lights | seqType.lights, animNextFrame, animFrameset4, moveFrameDuration - 1, seqType2.interLeaveOrder, moveNextFrame);
		} else if (animFrameset != null) {
			model2.animateModelFrame(animFrame, animFrameLength, animFrameset2, 0, animFrameset, seqType2.lights, animNextFrame, animFrameDuration - 1);
		} else if (animFrameset3 != null) {
			model2.animateModelFrame(moveFrame, moveFrameLength, animFrameset4, 0, animFrameset3, seqType.lights, moveNextFrame, moveFrameDuration - 1);
		}
		for (index = 0; index < sequenceCount; index++) {
			NPCType.aClass2_Sub6_Sub9Array1[index] = null;
			NPCType.aClass2_Sub6_Sub9Array2[index] = null;
			NPCType.aClass130Array2[index] = null;
		}
		if (this.scaleXZ != 128 || this.scaleY != 128) {
			model2.O(this.scaleXZ, this.scaleY, this.scaleXZ);
		}
		model2.s(initialFunctionMask);
		return model2;
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(BILjava/lang/String;)Ljava/lang/String;")
	public String getStringParam(@OriginalArg(1) int paramKey, @OriginalArg(2) String string) {
		if (this.parameters == null) {
			return string;
		} else {
			@Pc(27) StringNode stringNode = (StringNode) this.parameters.get((long) paramKey);
			return stringNode == null ? string : stringNode.value;
		}
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(Lclient!dd;Lclient!ha;IILclient!hg;Lclient!lfa;III)Lclient!ka;")
	public Model getAnimatedHeadModel(@OriginalArg(0) VarDomain varDomain, @OriginalArg(1) Toolkit toolkit, @OriginalArg(2) int currentFrameDuration, @OriginalArg(3) int nextFrameId, @OriginalArg(4) SeqType seqType, @OriginalArg(5) SeqTypeList seqTypeList, @OriginalArg(6) int currentFrameId) {
		if (this.alternativeIds != null) {
			@Pc(13) NPCType npcType = this.getMultiNpc(varDomain);
			return npcType == null ? null : npcType.getAnimatedHeadModel(varDomain, toolkit, currentFrameDuration, nextFrameId, seqType, seqTypeList, currentFrameId);
		} else if (this.headModels == null) {
			return null;
		} else {
			@Pc(44) int functionMask = 2048;
			if (seqType != null && currentFrameId != -1) {
				functionMask = seqType.getFunctionMask(nextFrameId, currentFrameId, true) | 0x800;
			}
			@Pc(62) LRUCache referenceCache = this.npcTypeList.headModels;
			@Pc(79) Model model;
			synchronized (this.npcTypeList.headModels) {
				model = (Model) this.npcTypeList.headModels.get((long) (toolkit.index << 16 | this.id));
			}
			if (model == null || (model.ua() & functionMask) != functionMask) {
				if (model != null) {
					functionMask |= model.ua();
				}
				@Pc(106) int modelFunctionMask = functionMask;
				@Pc(108) boolean missingModels = false;
				@Pc(112) Js5ResourceProvider meshResource = this.npcTypeList.meshes;
				synchronized (this.npcTypeList.meshes) {
					@Pc(116) int index = 0;
					while (true) {
						if (index >= this.headModels.length) {
							break;
						}
						if (!this.npcTypeList.meshes.isFileReady(this.headModels[index], 0)) {
							missingModels = true;
						}
						index++;
					}
				}
				if (missingModels) {
					return null;
				}
				@Pc(156) Mesh[] meshes = new Mesh[this.headModels.length];
				@Pc(160) Js5ResourceProvider meshResource2 = this.npcTypeList.meshes;
				@Pc(164) int index;
				synchronized (this.npcTypeList.meshes) {
					for (index = 0; index < this.headModels.length; index++) {
						meshes[index] = Mesh.fromResource(this.npcTypeList.meshes, this.headModels[index]);
					}
				}
				for (@Pc(197) int i = 0; i < this.headModels.length; i++) {
					if (meshes[i] != null && meshes[i].version < 13) {
						meshes[i].upscale();
					}
				}
				@Pc(235) Mesh mesh;
				if (meshes.length == 1) {
					mesh = meshes[0];
				} else {
					mesh = new Mesh(meshes, meshes.length);
				}
				if (this.recolorSrc != null) {
					modelFunctionMask = functionMask | 0x4000;
				}
				if (this.retextureSrc != null) {
					modelFunctionMask |= 0x8000;
				}
				if (this.tintOpacity != 0) {
					modelFunctionMask |= 0x80000;
				}
				model = toolkit.createModel(mesh, modelFunctionMask, this.npcTypeList.featureMask, 64, 768);
				if (this.recolorSrc != null) {
					for (index = 0; index < this.recolorSrc.length; index++) {
						if (this.recolorDPalette == null || index >= this.recolorDPalette.length) {
							model.ia(this.recolorSrc[index], this.recolorDst[index]);
						} else {
							model.ia(this.recolorSrc[index], NPCType.npcRecolorPalette[this.recolorDPalette[index] & 0xFF]);
						}
					}
				}
				if (this.retextureSrc != null) {
					for (index = 0; index < this.retextureSrc.length; index++) {
						model.aa(this.retextureSrc[index], this.retextureDst[index]);
					}
				}
				if (this.tintOpacity != 0) {
					model.adjustColours(this.tintHue, this.tintSaturation, this.tintLightness, this.tintOpacity & 0xFF);
				}
				model.s(functionMask);
				@Pc(377) LRUCache referenceCache2 = this.npcTypeList.headModels;
				synchronized (this.npcTypeList.headModels) {
					this.npcTypeList.headModels.put(model, (long) (toolkit.index << 16 | this.id));
				}
			}
			if (seqType != null && currentFrameId != -1) {
				model = seqType.animateModel(model, nextFrameId, currentFrameDuration, functionMask, currentFrameId);
			}
			model.s(2048);
			return model;
		}
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(III)I")
	public int getParam(@OriginalArg(0) int defaultInt, @OriginalArg(1) int paramKey) {
		if (this.parameters == null) {
			return defaultInt;
		} else {
			@Pc(25) IntNode intNode = (IntNode) this.parameters.get((long) paramKey);
			return intNode == null ? defaultInt : intNode.value;
		}
	}
	@OriginalMember(owner = "client!lk", name = "A", descriptor = "[S")
	public static short[] npcRecolorPalette = new short[256];
	@OriginalMember(owner = "client!dm", name = "Y", descriptor = "[Lclient!gw;")
	public static final FrameSet[] aClass2_Sub6_Sub9Array1 = new FrameSet[14];

	@OriginalMember(owner = "client!gw", name = "z", descriptor = "[Lclient!gw;")
	public static final FrameSet[] aClass2_Sub6_Sub9Array2 = new FrameSet[14];
	@OriginalMember(owner = "client!qe", name = "j", descriptor = "[I")
	public static final int[] ANIM_NEXT_FRAME_IDS = new int[14];

	@OriginalMember(owner = "client!kd", name = "g", descriptor = "[I")
	public static final int[] ANIM_FRAME_ELAPSED = new int[14];
	@OriginalMember(owner = "client!ts", name = "z", descriptor = "[I")
	public static final int[] ANIM_FRAME_LENGTHS = new int[14];

	@OriginalMember(owner = "client!pe", name = "k", descriptor = "[Lclient!hg;")
	public static final SeqType[] aClass130Array2 = new SeqType[14];
	@OriginalMember(owner = "client!bc", name = "g", descriptor = "[I")
	public static final int[] ANIM_FRAME_IDS = new int[14];
}
