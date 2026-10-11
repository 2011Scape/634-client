package com.jagex.game.runetek5;

import com.jagex.game.runetek5.entity.ComparableGraphNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpDiagonalWaves;

@OriginalClass("client!nia")
public final class ObjSpriteCacheKey implements ComparableGraphNode {

	@OriginalMember(owner = "client!nia", name = "a", descriptor = "I")
	public int numMode;

	@OriginalMember(owner = "client!nia", name = "b", descriptor = "I")
	public int shadowColor;

	@OriginalMember(owner = "client!nia", name = "i", descriptor = "I")
	public int amount;

	@OriginalMember(owner = "client!nia", name = "j", descriptor = "Z")
	public boolean objWearCol;

	@OriginalMember(owner = "client!nia", name = "k", descriptor = "I")
	public int index;

	@OriginalMember(owner = "client!nia", name = "l", descriptor = "I")
	public int itemId;

	@OriginalMember(owner = "client!nia", name = "m", descriptor = "I")
	public int outlineSize;

	@OriginalMember(owner = "client!nia", name = "a", descriptor = "(B)J")
	@Override
	public long toLong() {
		@Pc(5) long[] crc64 = TextureOpDiagonalWaves.CRC64_TABLE;
		@Pc(7) long hash01 = -1L;
		@Pc(22) long hash02 = crc64[(int) ((hash01 ^ (long) this.index) & 0xFFL)] ^ hash01 >>> 8;
		@Pc(39) long hash03 = hash02 >>> 8 ^ crc64[(int) (((long) (this.itemId >> 8) ^ hash02) & 0xFFL)];
		@Pc(54) long hash04 = hash03 >>> 8 ^ crc64[(int) ((hash03 ^ (long) this.itemId) & 0xFFL)];
		@Pc(71) long hash05 = hash04 >>> 8 ^ crc64[(int) ((hash04 ^ (long) (this.amount >> 24)) & 0xFFL)];
		@Pc(88) long hash06 = crc64[(int) (((long) (this.amount >> 16) ^ hash05) & 0xFFL)] ^ hash05 >>> 8;
		@Pc(105) long hash07 = hash06 >>> 8 ^ crc64[(int) ((hash06 ^ (long) (this.amount >> 8)) & 0xFFL)];
		@Pc(120) long hash08 = hash07 >>> 8 ^ crc64[(int) ((hash07 ^ (long) this.amount) & 0xFFL)];
		@Pc(135) long hash09 = hash08 >>> 8 ^ crc64[(int) (((long) this.outlineSize ^ hash08) & 0xFFL)];
		@Pc(152) long hash10 = crc64[(int) ((hash09 ^ (long) (this.shadowColor >> 24)) & 0xFFL)] ^ hash09 >>> 8;
		@Pc(169) long hash11 = hash10 >>> 8 ^ crc64[(int) ((hash10 ^ (long) (this.shadowColor >> 16)) & 0xFFL)];
		@Pc(191) long hash12 = crc64[(int) ((hash11 ^ (long) (this.shadowColor >> 8)) & 0xFFL)] ^ hash11 >>> 8;
		@Pc(206) long hash13 = hash12 >>> 8 ^ crc64[(int) (((long) this.shadowColor ^ hash12) & 0xFFL)];
		@Pc(221) long hash14 = crc64[(int) ((hash13 ^ (long) this.numMode) & 0xFFL)] ^ hash13 >>> 8;
		return hash14 >>> 8 ^ crc64[(int) (((long) (this.objWearCol ? 1 : 0) ^ hash14) & 0xFFL)];
	}

	@OriginalMember(owner = "client!nia", name = "a", descriptor = "(ILclient!we;)Z")
	@Override
	public boolean matches(@OriginalArg(1) ComparableGraphNode other) {
		if (!(other instanceof ObjSpriteCacheKey)) {
			return false;
		}
		@Pc(11) ObjSpriteCacheKey data = (ObjSpriteCacheKey) other;
		if (this.index != data.index) {
			return false;
		} else if (this.itemId != data.itemId) {
			return false;
		} else if (this.amount != data.amount) {
			return false;
		} else if (data.outlineSize != this.outlineSize) {
			return false;
		} else if (this.shadowColor != data.shadowColor) {
			return false;
		} else if (this.numMode == data.numMode) {
			return data.objWearCol == this.objWearCol;
		} else {
			return false;
		}
	}
}
