package com.jagex.game.runetek5.config.identitykit;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.idktype.IDKTypeList;

@OriginalClass("client!ic")
public final class IdkType {

	@OriginalMember(owner = "client!ic", name = "d", descriptor = "[S")
	private short[] retexSrc;

	@OriginalMember(owner = "client!ic", name = "k", descriptor = "[I")
	private int[] bodyModels;

	@OriginalMember(owner = "client!ic", name = "m", descriptor = "[S")
	private short[] recolorSrc;

	@OriginalMember(owner = "client!ic", name = "n", descriptor = "Lclient!oha;")
	public IDKTypeList identityTypeList;

	@OriginalMember(owner = "client!ic", name = "p", descriptor = "[S")
	private short[] retexDst;

	@OriginalMember(owner = "client!ic", name = "s", descriptor = "[S")
	private short[] recolorDst;

	@OriginalMember(owner = "client!ic", name = "c", descriptor = "[I")
	private final int[] headModels = new int[] { -1, -1, -1, -1, -1 };

	@OriginalMember(owner = "client!ic", name = "a", descriptor = "(I)Lclient!bh;")
	public Mesh getBodyMesh() {
		if (this.bodyModels == null) {
			return null;
		}
		@Pc(15) Mesh[] meshes2 = new Mesh[this.bodyModels.length];
		@Pc(19) Js5ResourceProvider meshResource = this.identityTypeList.meshResource;
		@Pc(23) int index;
		synchronized (this.identityTypeList.meshResource) {
			index = 0;
			while (true) {
				if (index >= this.bodyModels.length) {
					break;
				}
				meshes2[index] = Mesh.fromResource(this.identityTypeList.meshResource, this.bodyModels[index]);
				index++;
			}
		}
		for (@Pc(52) int i = 0; i < this.bodyModels.length; i++) {
			if (meshes2[i].version < 13) {
				meshes2[i].upscale();
			}
		}
		@Pc(91) Mesh mesh;
		if (meshes2.length == 1) {
			mesh = meshes2[0];
		} else {
			mesh = new Mesh(meshes2, meshes2.length);
		}
		if (mesh == null) {
			return null;
		}
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

	@OriginalMember(owner = "client!ic", name = "a", descriptor = "(B)Z")
	public boolean isHeadCached() {
		@Pc(7) boolean cached = true;
		@Pc(11) Js5ResourceProvider meshResource = this.identityTypeList.meshResource;
		synchronized (this.identityTypeList.meshResource) {
			for (@Pc(15) int i = 0; i < 5; i++) {
				if (this.headModels[i] != -1 && !this.identityTypeList.meshResource.isFileReady(this.headModels[i], 0)) {
					cached = false;
				}
			}
			return cached;
		}
	}

	@OriginalMember(owner = "client!ic", name = "b", descriptor = "(B)Lclient!bh;")
	public Mesh getHeadMesh() {
		@Pc(8) Mesh[] meshes = new Mesh[5];
		@Pc(10) int meshCount = 0;
		@Pc(14) Js5ResourceProvider meshResource = this.identityTypeList.meshResource;
		@Pc(18) int index;
		synchronized (this.identityTypeList.meshResource) {
			index = 0;
			while (true) {
				if (index >= 5) {
					break;
				}
				if (this.headModels[index] != -1) {
					meshes[meshCount++] = Mesh.fromResource(this.identityTypeList.meshResource, this.headModels[index]);
				}
				index++;
			}
		}
		for (@Pc(55) int i = 0; i < 5; i++) {
			if (meshes[i] != null && meshes[i].version < 13) {
				meshes[i].upscale();
			}
		}
		@Pc(90) Mesh mesh = new Mesh(meshes, meshCount);
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

	@OriginalMember(owner = "client!ic", name = "a", descriptor = "(ILclient!cea;B)V")
	private void decode(@OriginalArg(0) int opcode, @OriginalArg(1) Packet packet) {
		if (opcode == 1) {
			packet.readUByte();
			return;
		}
		@Pc(15) int count;
		@Pc(21) int index;
		if (opcode == 2) {
			count = packet.readUByte();
			this.bodyModels = new int[count];
			for (index = 0; index < count; index++) {
				this.bodyModels[index] = packet.readUShort();
			}
		} else if (opcode != 3) {
			if (opcode == 40) {
				count = packet.readUByte();
				this.recolorDst = new short[count];
				this.recolorSrc = new short[count];
				for (index = 0; index < count; index++) {
					this.recolorSrc[index] = (short) packet.readUShort();
					this.recolorDst[index] = (short) packet.readUShort();
				}
			} else if (opcode == 41) {
				count = packet.readUByte();
				this.retexSrc = new short[count];
				this.retexDst = new short[count];
				for (index = 0; index < count; index++) {
					this.retexSrc[index] = (short) packet.readUShort();
					this.retexDst[index] = (short) packet.readUShort();
				}
			} else if (opcode >= 60 && opcode < 70) {
				this.headModels[opcode - 60] = packet.readUShort();
			}
		}
	}

	@OriginalMember(owner = "client!ic", name = "a", descriptor = "(ILclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(9) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(opcode, packet);
		}
	}

	@OriginalMember(owner = "client!ic", name = "c", descriptor = "(B)Z")
	public boolean isBodyCached() {
		if (this.bodyModels == null) {
			return true;
		}
		@Pc(19) boolean cached = true;
		@Pc(23) Js5ResourceProvider meshResource = this.identityTypeList.meshResource;
		synchronized (this.identityTypeList.meshResource) {
			for (@Pc(27) int i = 0; i < this.bodyModels.length; i++) {
				if (!this.identityTypeList.meshResource.isFileReady(this.bodyModels[i], 0)) {
					cached = false;
				}
			}
			return cached;
		}
	}
}
