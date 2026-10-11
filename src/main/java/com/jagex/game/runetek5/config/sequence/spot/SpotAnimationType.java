package com.jagex.game.runetek5.config.sequence.spot;

import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.config.sequence.SeqTypeList;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!fk")
public final class SpotAnimationType {

	@OriginalMember(owner = "client!fk", name = "e", descriptor = "[S")
	private short[] recolorSrc;

	@OriginalMember(owner = "client!fk", name = "n", descriptor = "[S")
	private short[] retextureSrc;

	@OriginalMember(owner = "client!fk", name = "p", descriptor = "I")
	public int currentId;

	@OriginalMember(owner = "client!fk", name = "q", descriptor = "[S")
	private short[] recolorDst;

	@OriginalMember(owner = "client!fk", name = "s", descriptor = "[S")
	private short[] retextureDst;

	@OriginalMember(owner = "client!fk", name = "u", descriptor = "I")
	private int modelId;

	@OriginalMember(owner = "client!fk", name = "B", descriptor = "Lclient!cn;")
	public SpotAnimationTypeList gfxTypeList;

	@OriginalMember(owner = "client!fk", name = "c", descriptor = "B")
	public byte contourType = 0;

	@OriginalMember(owner = "client!fk", name = "m", descriptor = "I")
	private int scaleXZ = 128;

	@OriginalMember(owner = "client!fk", name = "b", descriptor = "Z")
	public boolean replay = false;

	@OriginalMember(owner = "client!fk", name = "w", descriptor = "I")
	private int rotation = 0;

	@OriginalMember(owner = "client!fk", name = "r", descriptor = "I")
	public int sequenceId = -1;

	@OriginalMember(owner = "client!fk", name = "v", descriptor = "I")
	private int contourModifier = -1;

	@OriginalMember(owner = "client!fk", name = "g", descriptor = "I")
	private int scaleY = 128;

	@OriginalMember(owner = "client!fk", name = "d", descriptor = "I")
	private int brightness = 0;

	@OriginalMember(owner = "client!fk", name = "A", descriptor = "I")
	private int contrast = 0;

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(IILclient!ha;IILclient!lfa;I)Lclient!ka;")
	public Model getSecondaryModel(@OriginalArg(0) int frameCycle, @OriginalArg(1) int nextFrameId, @OriginalArg(2) Toolkit toolkit, @OriginalArg(3) int functionMask, @OriginalArg(5) SeqTypeList seqTypeList, @OriginalArg(6) int currentFrameId) {
		return this.getAnimatedModel(toolkit, currentFrameId, functionMask, null, 0, seqTypeList, frameCycle, (byte) 5, null, 0, false, 0, nextFrameId);
	}

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(ILclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(13) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(ILclient!ha;IIILclient!lfa;B)Lclient!ka;")
	public Model getModel(@OriginalArg(0) int currentFrameId, @OriginalArg(1) Toolkit toolkit, @OriginalArg(2) int frameCycle, @OriginalArg(3) int functionMask, @OriginalArg(4) int nextFrameId, @OriginalArg(5) SeqTypeList seqTypeList) {
		return this.getAnimatedModel(toolkit, currentFrameId, functionMask, null, 0, seqTypeList, frameCycle, (byte) 2, null, 0, false, 0, nextFrameId);
	}

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(ILclient!cea;I)V")
	private void decode(@OriginalArg(1) Packet packet, @OriginalArg(2) int opcode) {
		if (opcode == 1) {
			this.modelId = packet.readUShort();
		} else if (opcode == 2) {
			this.sequenceId = packet.readUShort();
		} else if (opcode == 4) {
			this.scaleXZ = packet.readUShort();
		} else if (opcode == 5) {
			this.scaleY = packet.readUShort();
		} else if (opcode == 6) {
			this.rotation = packet.readUShort();
		} else if (opcode == 7) {
			this.brightness = packet.readUByte();
		} else if (opcode == 8) {
			this.contrast = packet.readUByte();
		} else if (opcode == 9) {
			this.contourModifier = 8224;
			this.contourType = 3;
		} else if (opcode == 10) {
			this.replay = true;
		} else if (opcode == 11) {
			this.contourType = 1;
		} else if (opcode == 12) {
			this.contourType = 4;
		} else if (opcode == 13) {
			this.contourType = 5;
		} else if (opcode == 14) {
			this.contourType = 2;
			this.contourModifier = packet.readUByte() * 256;
		} else if (opcode == 15) {
			this.contourType = 3;
			this.contourModifier = packet.readUShort();
		} else if (opcode == 16) {
			this.contourType = 3;
			this.contourModifier = packet.readInt();
		} else {
			@Pc(124) int count;
			@Pc(134) int i;
			if (opcode == 40) {
				count = packet.readUByte();
				this.recolorSrc = new short[count];
				this.recolorDst = new short[count];
				for (i = 0; i < count; i++) {
					this.recolorSrc[i] = (short) packet.readUShort();
					this.recolorDst[i] = (short) packet.readUShort();
				}
			} else if (opcode == 41) {
				count = packet.readUByte();
				this.retextureDst = new short[count];
				this.retextureSrc = new short[count];
				for (i = 0; i < count; i++) {
					this.retextureSrc[i] = (short) packet.readUShort();
					this.retextureDst[i] = (short) packet.readUShort();
				}
				return;
			}
		}
	}

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(IIIIZLclient!ha;IIILclient!lfa;Lclient!s;ILclient!s;)Lclient!ka;")
	public Model getContouredModel(@OriginalArg(0) int frameCycle, @OriginalArg(1) int currentFrameId, @OriginalArg(2) int y, @OriginalArg(3) int functionMask, @OriginalArg(5) Toolkit toolkit, @OriginalArg(6) int nextFrameId, @OriginalArg(7) int x, @OriginalArg(9) SeqTypeList seqTypeList, @OriginalArg(10) Ground ground, @OriginalArg(11) int z, @OriginalArg(12) Ground ground2) {
		return this.getAnimatedModel(toolkit, currentFrameId, functionMask, ground, z, seqTypeList, frameCycle, (byte) 2, ground2, y, true, x, nextFrameId);
	}

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(Lclient!ha;IILclient!s;ILclient!lfa;IBLclient!s;IZIBI)Lclient!ka;")
	private Model getAnimatedModel(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int currentFrameId, @OriginalArg(2) int initialFunctionMask, @OriginalArg(3) Ground ground, @OriginalArg(4) int z, @OriginalArg(5) SeqTypeList seqTypeList, @OriginalArg(6) int frameCycle, @OriginalArg(7) byte copyType, @OriginalArg(8) Ground ground2, @OriginalArg(9) int y, @OriginalArg(10) boolean allowContouring, @OriginalArg(11) int x, @OriginalArg(13) int nextFrameId) {
		@Pc(7) int functionMask = initialFunctionMask;
		@Pc(22) SeqType seqType = this.sequenceId == -1 || currentFrameId == -1 ? null : seqTypeList.list(this.sequenceId);
		@Pc(31) boolean adjustToGround = allowContouring & this.contourType != 0;
		if (seqType != null) {
			functionMask = initialFunctionMask | seqType.getFunctionMask(nextFrameId, currentFrameId, false);
		}
		if (adjustToGround) {
			functionMask |= this.contourType == 3 ? 7 : 2;
		}
		if (this.scaleY != 128) {
			functionMask |= 0x2;
		}
		if (this.scaleXZ != 128 || this.rotation != 0) {
			functionMask |= 0x5;
		}
		@Pc(86) LRUCache referenceCache = this.gfxTypeList.modelCache;
		@Pc(106) Model model;
		synchronized (this.gfxTypeList.modelCache) {
			model = (Model) this.gfxTypeList.modelCache.get((long) (this.currentId |= toolkit.index << 29));
		}
		if (model == null || toolkit.compareFunctionMasks(model.ua(), functionMask) != 0) {
			if (model != null) {
				functionMask = toolkit.combineFunctionMasks(functionMask, model.ua());
			}
			@Pc(133) int modelFunctionMask = functionMask;
			if (this.recolorSrc != null) {
				modelFunctionMask = functionMask | 0x4000;
			}
			if (this.retextureSrc != null) {
				modelFunctionMask |= 0x8000;
			}
			@Pc(156) Mesh mesh = Mesh.fromResource(this.gfxTypeList.modelResource, this.modelId);
			if (mesh == null) {
				return null;
			}
			if (mesh.version < 13) {
				mesh.upscale();
			}
			model = toolkit.createModel(mesh, modelFunctionMask, this.gfxTypeList.featureMask, this.brightness + 64, this.contrast - -850);
			@Pc(191) int i;
			if (this.recolorSrc != null) {
				for (i = 0; i < this.recolorSrc.length; i++) {
					model.ia(this.recolorSrc[i], this.recolorDst[i]);
				}
			}
			if (this.retextureSrc != null) {
				for (i = 0; i < this.retextureSrc.length; i++) {
					model.aa(this.retextureSrc[i], this.retextureDst[i]);
				}
			}
			model.s(functionMask);
			@Pc(247) LRUCache referenceCache2 = this.gfxTypeList.modelCache;
			synchronized (this.gfxTypeList.modelCache) {
				this.gfxTypeList.modelCache.put(model, (long) (this.currentId |= toolkit.index << 29));
			}
		}
		@Pc(296) Model model2 = seqType == null ? model.copy(copyType, functionMask, true) : seqType.animateRotatedModel(model, 0, nextFrameId, copyType, currentFrameId, frameCycle, functionMask);
		if (this.scaleXZ != 128 || this.scaleY != 128) {
			model2.O(this.scaleXZ, this.scaleY, this.scaleXZ);
		}
		if (this.rotation != 0) {
			if (this.rotation == 90) {
				model2.a(4096);
			}
			if (this.rotation == 180) {
				model2.a(8192);
			}
			if (this.rotation == 270) {
				model2.a(12288);
			}
		}
		if (adjustToGround) {
			model2.p(this.contourType, this.contourModifier, ground2, ground, x, y, z);
		}
		model2.s(initialFunctionMask);
		return model2;
	}
}
