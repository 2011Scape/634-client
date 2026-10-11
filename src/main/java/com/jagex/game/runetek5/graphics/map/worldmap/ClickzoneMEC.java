package com.jagex.game.runetek5.graphics.map.worldmap;

import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ar")
public final class ClickzoneMEC extends Node {

	@OriginalMember(owner = "client!ar", name = "s", descriptor = "I")
	public int rectY2 = Integer.MIN_VALUE;

	@OriginalMember(owner = "client!ar", name = "p", descriptor = "I")
	public int spriteTop = Integer.MIN_VALUE;

	@OriginalMember(owner = "client!ar", name = "E", descriptor = "I")
	public int spriteRight = Integer.MIN_VALUE;

	@OriginalMember(owner = "client!ar", name = "z", descriptor = "I")
	public int textLeft = Integer.MAX_VALUE;

	@OriginalMember(owner = "client!ar", name = "A", descriptor = "I")
	public int spriteBottom = Integer.MAX_VALUE;

	@OriginalMember(owner = "client!ar", name = "r", descriptor = "I")
	public int textRight = Integer.MIN_VALUE;

	@OriginalMember(owner = "client!ar", name = "B", descriptor = "I")
	public int spriteLeft = Integer.MAX_VALUE;

	@OriginalMember(owner = "client!ar", name = "H", descriptor = "I")
	public int textBottom = Integer.MAX_VALUE;

	@OriginalMember(owner = "client!ar", name = "F", descriptor = "Lclient!lga;")
	public final WorldMapCategory category;

	@OriginalMember(owner = "client!ar", name = "<init>", descriptor = "(Lclient!lga;)V")
	public ClickzoneMEC(@OriginalArg(0) WorldMapCategory mapElementListEntry) {
		this.category = mapElementListEntry;
	}

	@OriginalMember(owner = "client!ar", name = "b", descriptor = "(III)Z")
	public boolean contains(@OriginalArg(0) int x, @OriginalArg(1) int mouseX) {
		if (this.spriteLeft <= mouseX && this.spriteRight >= mouseX && x >= this.spriteBottom && this.spriteTop >= x) {
			return true;
		} else {
			return this.textLeft <= mouseX && this.textRight >= mouseX && this.textBottom <= x && this.rectY2 >= x;
		}
	}
}
