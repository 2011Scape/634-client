package com.jagex.game.runetek5.client.input.mouse;

import com.jagex.game.runetek5.client.input.mouse.MouseRecord;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!qd")
public final class SimpleMouseLog extends MouseRecord {

	@OriginalMember(owner = "client!qd", name = "v", descriptor = "I")
	public int meta;

	@OriginalMember(owner = "client!qd", name = "y", descriptor = "I")
	public int mouseY;

	@OriginalMember(owner = "client!qd", name = "z", descriptor = "J")
	public long time;

	@OriginalMember(owner = "client!qd", name = "B", descriptor = "I")
	public int clickType;

	@OriginalMember(owner = "client!qd", name = "E", descriptor = "I")
	public int mouseX;

	@OriginalMember(owner = "client!qd", name = "b", descriptor = "(I)I")
	@Override
	public int getClickType() {
		return this.clickType;
	}

	@OriginalMember(owner = "client!qd", name = "d", descriptor = "(I)I")
	@Override
	public int getMouseY() {
		return this.mouseY;
	}

	@OriginalMember(owner = "client!qd", name = "a", descriptor = "(B)I")
	@Override
	public int getMouseX() {
		return this.mouseX;
	}

	@OriginalMember(owner = "client!qd", name = "b", descriptor = "(B)J")
	@Override
	public long getTime() {
		return this.time;
	}

	@OriginalMember(owner = "client!qd", name = "f", descriptor = "(I)I")
	@Override
	public int getMeta() {
		return this.meta;
	}
}
