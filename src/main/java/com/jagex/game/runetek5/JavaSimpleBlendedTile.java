package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ul")
public final class JavaSimpleBlendedTile {

	@OriginalMember(owner = "client!ul", name = "a", descriptor = "S")
	public short texture;

	@OriginalMember(owner = "client!ul", name = "b", descriptor = "I")
	public int colourNe;

	@OriginalMember(owner = "client!ul", name = "c", descriptor = "I")
	public int colourNw;

	@OriginalMember(owner = "client!ul", name = "d", descriptor = "S")
	public short waterDepthSw;

	@OriginalMember(owner = "client!ul", name = "e", descriptor = "S")
	public short waterDepthSe;

	@OriginalMember(owner = "client!ul", name = "f", descriptor = "S")
	public short waterDepthNe;

	@OriginalMember(owner = "client!ul", name = "g", descriptor = "S")
	public short waterDepthNw;

	@OriginalMember(owner = "client!ul", name = "h", descriptor = "B")
	public byte flags;

	@OriginalMember(owner = "client!ul", name = "i", descriptor = "I")
	public int colourSe;

	@OriginalMember(owner = "client!ul", name = "k", descriptor = "I")
	public int colourSw;

	@OriginalMember(owner = "client!ul", name = "j", descriptor = "I")
	public int blendedColour = 0;
}
