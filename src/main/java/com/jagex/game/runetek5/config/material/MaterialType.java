package com.jagex.game.runetek5.config.material;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;

@OriginalClass("client!hd")
public final class MaterialType {

	@OriginalMember(owner = "client!hd", name = "a", descriptor = "B")
	public byte speedU;

	@OriginalMember(owner = "client!hd", name = "b", descriptor = "Z")
	public boolean isSmall;

	@OriginalMember(owner = "client!hd", name = "c", descriptor = "I")
	public int effectCombiner;

	@OriginalMember(owner = "client!hd", name = "d", descriptor = "B")
	public byte alpha;

	@OriginalMember(owner = "client!hd", name = "e", descriptor = "B")
	public byte effectParam1;

	@OriginalMember(owner = "client!hd", name = "f", descriptor = "I")
	public int combineMode;

	@OriginalMember(owner = "client!hd", name = "g", descriptor = "Z")
	public boolean skipTriangles;

	@OriginalMember(owner = "client!hd", name = "h", descriptor = "B")
	public byte mipmapping;

	@OriginalMember(owner = "client!hd", name = "i", descriptor = "I")
	public int effectParam2;

	@OriginalMember(owner = "client!hd", name = "j", descriptor = "Z")
	public boolean transposed;

	@OriginalMember(owner = "client!hd", name = "k", descriptor = "S")
	public short colorHsl;

	@OriginalMember(owner = "client!hd", name = "l", descriptor = "Z")
	public boolean detailsOnly;

	@OriginalMember(owner = "client!hd", name = "n", descriptor = "B")
	public byte speedV;

	@OriginalMember(owner = "client!hd", name = "o", descriptor = "Z")
	public boolean unusedFlag;

	@OriginalMember(owner = "client!hd", name = "p", descriptor = "B")
	public byte effectType;

	@OriginalMember(owner = "client!hd", name = "r", descriptor = "Z")
	public boolean repeatT;

	@OriginalMember(owner = "client!hd", name = "s", descriptor = "B")
	public byte brightness;

	@OriginalMember(owner = "client!hd", name = "t", descriptor = "Z")
	public boolean repeatS;

	@OriginalMember(owner = "client!hd", name = "u", descriptor = "Z")
	public boolean hdr;
	@OriginalMember(owner = "client!tr", name = "a", descriptor = "(BI)Z")
	public static boolean isOpaqueEffect(@OriginalArg(1) int effectType) {
		return effectType != 1 && effectType != 7;
	}
}
