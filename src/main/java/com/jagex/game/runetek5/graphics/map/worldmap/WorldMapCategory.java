package com.jagex.game.runetek5.graphics.map.worldmap;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!lga")
public final class WorldMapCategory extends Node {

	@OriginalMember(owner = "client!lga", name = "r", descriptor = "I")
	public int level;

	@OriginalMember(owner = "client!lga", name = "s", descriptor = "I")
	public int z;

	@OriginalMember(owner = "client!lga", name = "t", descriptor = "I")
	public int x;

	@OriginalMember(owner = "client!lga", name = "u", descriptor = "I")
	public int screenX;

	@OriginalMember(owner = "client!lga", name = "w", descriptor = "I")
	public int screenY;

	@OriginalMember(owner = "client!lga", name = "p", descriptor = "Z")
	public boolean isMouseOver = false;

	@OriginalMember(owner = "client!lga", name = "o", descriptor = "I")
	public int id = -1;

	@OriginalMember(owner = "client!lga", name = "<init>", descriptor = "(I)V")
	public WorldMapCategory(@OriginalArg(0) int mecId) {
		this.id = mecId;
	}
}
