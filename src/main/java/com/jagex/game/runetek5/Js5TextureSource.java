package com.jagex.game.runetek5;

import com.jagex.game.runetek5.cache.node.CacheableNodeQueue;
import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.material.MaterialType;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureType;

@OriginalClass("client!tda")
public final class Js5TextureSource implements MaterialProvider {

	@OriginalMember(owner = "client!tda", name = "n", descriptor = "Lclient!wu;")
	private final CacheableNodeQueue recentUse = new CacheableNodeQueue(256);

	@OriginalMember(owner = "client!tda", name = "k", descriptor = "Lclient!in;")
	private final Js5ResourceProvider textureArchive;

	@OriginalMember(owner = "client!tda", name = "p", descriptor = "Lclient!in;")
	private final Js5ResourceProvider spriteArchive;

	@OriginalMember(owner = "client!tda", name = "q", descriptor = "I")
	private final int textureCount;

	@OriginalMember(owner = "client!tda", name = "c", descriptor = "[Lclient!hd;")
	private final MaterialType[] metrics;

	@OriginalMember(owner = "client!tda", name = "<init>", descriptor = "(Lclient!in;Lclient!in;Lclient!in;)V")
	public Js5TextureSource(@OriginalArg(0) Js5ResourceProvider metricsArchive, @OriginalArg(1) Js5ResourceProvider textureArchive, @OriginalArg(2) Js5ResourceProvider spriteArchive) {
		this.textureArchive = textureArchive;
		this.spriteArchive = spriteArchive;
		@Pc(24) Packet packet = new Packet(metricsArchive.getFile(0, 0));
		this.textureCount = packet.readUShort();
		this.metrics = new MaterialType[this.textureCount];
		for (@Pc(36) int i = 0; i < this.textureCount; i++) {
			if (packet.readUByte() == 1) {
				this.metrics[i] = new MaterialType();
			}
		}
		for (@Pc(61) int j = 0; j < this.textureCount; j++) {
			if (this.metrics[j] != null) {
				this.metrics[j].detailsOnly = packet.readUByte() == 0;
			}
		}
		for (@Pc(87) int k = 0; k < this.textureCount; k++) {
			if (this.metrics[k] != null) {
				this.metrics[k].isSmall = packet.readUByte() == 1;
			}
		}
		for (@Pc(118) int m = 0; m < this.textureCount; m++) {
			if (this.metrics[m] != null) {
				this.metrics[m].skipTriangles = packet.readUByte() == 1;
			}
		}
		for (@Pc(145) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].brightness = packet.readByte();
			}
		}
		for (@Pc(167) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].alpha = packet.readByte();
			}
		}
		for (@Pc(193) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].effectType = packet.readByte();
			}
		}
		for (@Pc(215) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].effectParam1 = packet.readByte();
			}
		}
		for (@Pc(237) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].colorHsl = (short) packet.readUShort();
			}
		}
		for (@Pc(260) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].speedU = packet.readByte();
			}
		}
		for (@Pc(286) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].speedV = packet.readByte();
			}
		}
		for (@Pc(312) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].unusedFlag = packet.readUByte() == 1;
			}
		}
		for (@Pc(345) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].transposed = packet.readUByte() == 1;
			}
		}
		for (@Pc(376) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].mipmapping = packet.readByte();
			}
		}
		for (@Pc(398) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].repeatS = packet.readUByte() == 1;
			}
		}
		for (@Pc(427) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].repeatT = packet.readUByte() == 1;
			}
		}
		for (@Pc(456) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].hdr = packet.readUByte() == 1;
			}
		}
		for (@Pc(483) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].combineMode = packet.readUByte();
			}
		}
		for (@Pc(505) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].effectParam2 = packet.readInt();
			}
		}
		for (@Pc(527) int i = 0; i < this.textureCount; i++) {
			if (this.metrics[i] != null) {
				this.metrics[i].effectCombiner = packet.readUByte();
			}
		}
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "(IIFIZI)[I")
	@Override
	public int[] getPixelsArgb(@OriginalArg(1) int height, @OriginalArg(2) float brightness, @OriginalArg(3) int materialId, @OriginalArg(5) int width) {
		return this.getTextureType(materialId).getPixelsArgb(this, width, height, this.metrics[materialId].transposed, (double) brightness, this.spriteArchive);
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "(IZ)Lclient!lr;")
	private TextureType getTextureType(@OriginalArg(0) int materialId) {
		@Pc(10) CacheableNode node2 = this.recentUse.get((long) materialId);
		if (node2 != null) {
			return (TextureType) node2;
		}
		@Pc(21) byte[] data = this.textureArchive.getFile(materialId);
		if (data == null) {
			return null;
		} else {
			@Pc(45) TextureType texture = new TextureType(new Packet(data));
			this.recentUse.put((long) materialId, texture);
			return texture;
		}
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "(II)Lclient!hd;")
	@Override
	public MaterialType getMetrics(@OriginalArg(0) int id) {
		return this.metrics[id];
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "(ZIFIII)[I")
	@Override
	public int[] getPixels(@OriginalArg(0) boolean flipX, @OriginalArg(1) int materialId, @OriginalArg(2) float brightness, @OriginalArg(3) int width, @OriginalArg(4) int height) {
		return this.getTextureType(materialId).getPixelsRgb(width, this, this.metrics[materialId].transposed, (double) brightness, flipX, this.spriteArchive, height);
	}

	@OriginalMember(owner = "client!tda", name = "b", descriptor = "(II)Z")
	@Override
	public boolean textureAvailable(@OriginalArg(1) int id) {
		@Pc(8) TextureType texture = this.getTextureType(id);
		return texture != null && texture.isReady(this, this.spriteArchive);
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "(IFZIII)[F")
	@Override
	public float[] floatArgbOutput(@OriginalArg(0) int height, @OriginalArg(1) float brightness, @OriginalArg(4) int width, @OriginalArg(5) int materialId) {
		return this.getTextureType(materialId).getPixelsHdr(height, this, this.metrics[materialId].transposed, width, this.spriteArchive);
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "(Z)I")
	@Override
	public int textureCount() {
		return this.textureCount;
	}
	@OriginalMember(owner = "client!hl", name = "K", descriptor = "Lclient!d;")
	public static MaterialProvider anInterface4_10;
}
