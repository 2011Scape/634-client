package com.jagex.game.runetek5.client.input.mouse;

import com.jagex.game.runetek5.client.input.mouse.MouseRecord;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!no")
public final class CompleteMouseLog extends MouseRecord {

	@OriginalMember(owner = "client!no", name = "v", descriptor = "I")
	public int mouseY;

	@OriginalMember(owner = "client!no", name = "w", descriptor = "J")
	public long time;

	@OriginalMember(owner = "client!no", name = "x", descriptor = "I")
	public int meta;

	@OriginalMember(owner = "client!no", name = "y", descriptor = "I")
	public int clickType;

	@OriginalMember(owner = "client!no", name = "z", descriptor = "I")
	public int extra;

	@OriginalMember(owner = "client!no", name = "b", descriptor = "(I)I")
	@Override
	public int getClickType() {
		return this.clickType;
	}

	@OriginalMember(owner = "client!no", name = "b", descriptor = "(B)J")
	@Override
	public long getTime() {
		return this.time;
	}

	@OriginalMember(owner = "client!no", name = "d", descriptor = "(I)I")
	@Override
	public int getMouseY() {
		return this.mouseY;
	}

	@OriginalMember(owner = "client!no", name = "f", descriptor = "(I)I")
	@Override
	public int getMeta() {
		return this.meta;
	}

	@OriginalMember(owner = "client!no", name = "a", descriptor = "(B)I")
	@Override
	public int getMouseX() {
		return this.extra;
	}
}
