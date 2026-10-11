package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!f")
public final class PlayerUpdateEntry {

	@OriginalMember(owner = "client!f", name = "b", descriptor = "I")
	public int packedCoordinates;

	@OriginalMember(owner = "client!f", name = "e", descriptor = "I")
	public int rotation;

	@OriginalMember(owner = "client!f", name = "g", descriptor = "I")
	public int faceEntity;

	@OriginalMember(owner = "client!f", name = "h", descriptor = "Z")
	public boolean pMapdot;
}
