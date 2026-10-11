package com.jagex.game.runetek5.config.rsinterface;

import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.config.rsinterface.Component;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!rk")
public final class HookRequest extends Node {

	@OriginalMember(owner = "client!rk", name = "o", descriptor = "I")
	public int typedKeyChar;

	@OriginalMember(owner = "client!rk", name = "p", descriptor = "Lclient!at;")
	public Component targetComponent;

	@OriginalMember(owner = "client!rk", name = "q", descriptor = "I")
	public int mouseX;

	@OriginalMember(owner = "client!rk", name = "s", descriptor = "I")
	public int clickedOption;

	@OriginalMember(owner = "client!rk", name = "t", descriptor = "[Ljava/lang/Object;")
	public Object[] params;

	@OriginalMember(owner = "client!rk", name = "u", descriptor = "I")
	public int id;

	@OriginalMember(owner = "client!rk", name = "v", descriptor = "Lclient!at;")
	public Component source;

	@OriginalMember(owner = "client!rk", name = "x", descriptor = "I")
	public int typedKeyCode;

	@OriginalMember(owner = "client!rk", name = "z", descriptor = "Z")
	public boolean hasMousePosition;

	@OriginalMember(owner = "client!rk", name = "A", descriptor = "Ljava/lang/String;")
	public String optionName;

	@OriginalMember(owner = "client!rk", name = "B", descriptor = "I")
	public int mouseY;
}
