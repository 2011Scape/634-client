package com.jagex.game.runetek5.config.particle;

import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;

@OriginalClass("client!po")
public final class ParticleType {

	@OriginalMember(owner = "client!po", name = "c", descriptor = "I")
	public int effectType;

	@OriginalMember(owner = "client!po", name = "d", descriptor = "I")
	public int offsetY;

	@OriginalMember(owner = "client!po", name = "e", descriptor = "I")
	public int offsetX;

	@OriginalMember(owner = "client!po", name = "h", descriptor = "I")
	public int offsetZ;

	@OriginalMember(owner = "client!po", name = "j", descriptor = "I")
	public int particleHandlingType;

	@OriginalMember(owner = "client!po", name = "l", descriptor = "I")
	public int zan;

	@OriginalMember(owner = "client!po", name = "m", descriptor = "I")
	public int size3d;

	@OriginalMember(owner = "client!po", name = "o", descriptor = "J")
	public long uid;

	@OriginalMember(owner = "client!po", name = "p", descriptor = "I")
	private int rotation;

	@OriginalMember(owner = "client!po", name = "q", descriptor = "I")
	public int partileFileId;

	@OriginalMember(owner = "client!po", name = "r", descriptor = "I")
	public int sizeMultiplier;

	@OriginalMember(owner = "client!po", name = "k", descriptor = "I")
	public int currentOffset = 0;

	@OriginalMember(owner = "client!po", name = "f", descriptor = "I")
	public int verticeCalculationType = 0;

	@OriginalMember(owner = "client!po", name = "v", descriptor = "Z")
	private boolean invert = false;

	@OriginalMember(owner = "client!po", name = "a", descriptor = "(Lclient!cea;IB)V")
	private void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 1) {
			this.rotation = packet.readUShort();
		} else if (opcode == 2) {
			packet.readUByte();
		} else if (opcode == 3) {
			this.offsetX = packet.readInt();
			this.offsetY = packet.readInt();
			this.offsetZ = packet.readInt();
		} else if (opcode == 4) {
			this.effectType = packet.readUByte();
			this.sizeMultiplier = packet.readInt();
		} else if (opcode == 6) {
			this.particleHandlingType = packet.readUByte();
		} else if (opcode == 8) {
			this.currentOffset = 1;
		} else if (opcode == 9) {
			this.verticeCalculationType = 1;
		} else if (opcode == 10) {
			this.invert = true;
		}
	}

	@OriginalMember(owner = "client!po", name = "a", descriptor = "(ILclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(5) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}

	@OriginalMember(owner = "client!po", name = "a", descriptor = "(I)V")
	public void postDecode() {
		this.zan = TextureCombineSource.COSINE[this.rotation << 3];
		@Pc(17) long x = (long) this.offsetX;
		@Pc(21) long y = (long) this.offsetY;
		@Pc(25) long z = (long) this.offsetZ;
		this.size3d = (int) Math.sqrt((double) (y * y + x * x + z * z));
		if (this.sizeMultiplier == 0) {
			this.sizeMultiplier = 1;
		}
		if (this.effectType == 0) {
			this.uid = 2147483647L;
		} else if (this.effectType == 1) {
			this.uid = this.size3d * 8 / this.sizeMultiplier;
			this.uid *= this.uid;
		} else if (this.effectType == 2) {
			this.uid = this.size3d * 8 / this.sizeMultiplier;
		}
		if (this.invert) {
			this.size3d *= -1;
		}
	}
	@OriginalMember(owner = "client!wd", name = "F", descriptor = "Lclient!eq;")
	public static final IterableHashTable particleTypeCache = new IterableHashTable(16);

	@OriginalMember(owner = "client!hi", name = "h", descriptor = "[Lclient!po;")
	public static final ParticleType[] particleTypes = new ParticleType[16];

	@OriginalMember(owner = "client!fha", name = "C", descriptor = "Lclient!jr;")
	public static final LRUCache particleCache = new LRUCache(64);
}
