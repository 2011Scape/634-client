package com.jagex.game.runetek5.client.input.mouse;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.AbstractMouseManager;

@OriginalClass("client!bu")
public abstract class MouseRecord extends Node {

	@OriginalMember(owner = "client!bu", name = "a", descriptor = "(B)I")
	public abstract int getMouseX();

	@OriginalMember(owner = "client!bu", name = "b", descriptor = "(I)I")
	public abstract int getClickType();

	@OriginalMember(owner = "client!bu", name = "d", descriptor = "(I)I")
	public abstract int getMouseY();

	@OriginalMember(owner = "client!bu", name = "b", descriptor = "(B)J")
	public abstract long getTime();

	@OriginalMember(owner = "client!bu", name = "f", descriptor = "(I)I")
	public abstract int getMeta();
	@OriginalMember(owner = "client!wha", name = "r", descriptor = "Lclient!g;")
	public static AbstractMouseManager mouseRecorder;
}
