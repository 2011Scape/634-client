package com.jagex.game.runetek5.client.input.keyboard;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ll")
public interface KeyRecord {

	@OriginalMember(owner = "client!ll", name = "a", descriptor = "(I)I")
	public int getModifierFlags();

	@OriginalMember(owner = "client!ll", name = "b", descriptor = "(I)I")
	public int getType();

	@OriginalMember(owner = "client!ll", name = "a", descriptor = "(B)C")
	public char getKeyChar();

	@OriginalMember(owner = "client!ll", name = "b", descriptor = "(B)J")
	public long getTime();

	@OriginalMember(owner = "client!ll", name = "a", descriptor = "(Z)I")
	public int getKeyCode();
}
