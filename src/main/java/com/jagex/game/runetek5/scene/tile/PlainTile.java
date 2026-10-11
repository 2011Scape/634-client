package com.jagex.game.runetek5.scene.tile;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!mga")
public final class PlainTile {

	@OriginalMember(owner = "client!mga", name = "b", descriptor = "S")
	public short hslSw;

	@OriginalMember(owner = "client!mga", name = "c", descriptor = "S")
	public short hslNe;

	@OriginalMember(owner = "client!mga", name = "d", descriptor = "S")
	public short hslSe;

	@OriginalMember(owner = "client!mga", name = "e", descriptor = "S")
	public short hslNw;

	@OriginalMember(owner = "client!mga", name = "f", descriptor = "S")
	public short texture;

	@OriginalMember(owner = "client!mga", name = "g", descriptor = "B")
	public byte functionMask;

	@OriginalMember(owner = "client!mga", name = "a", descriptor = "I")
	public int color = -1;
}
