package com.jagex.game.runetek5.client.input.keyboard;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ica")
public final class KeyRecordEntry extends Node implements KeyRecord {

	@OriginalMember(owner = "client!ica", name = "p", descriptor = "C")
	public char keyCharacter;

	@OriginalMember(owner = "client!ica", name = "s", descriptor = "J")
	public long timeSubmitted;

	@OriginalMember(owner = "client!ica", name = "u", descriptor = "I")
	public int modifierFlags;

	@OriginalMember(owner = "client!ica", name = "v", descriptor = "I")
	public int keyCode;

	@OriginalMember(owner = "client!ica", name = "z", descriptor = "I")
	public int type;

	@OriginalMember(owner = "client!ica", name = "b", descriptor = "(I)I")
	@Override
	public int getType() {
		return this.type;
	}

	@OriginalMember(owner = "client!ica", name = "a", descriptor = "(B)C")
	@Override
	public char getKeyChar() {
		return this.keyCharacter;
	}

	@OriginalMember(owner = "client!ica", name = "a", descriptor = "(I)I")
	@Override
	public int getModifierFlags() {
		return this.modifierFlags;
	}

	@OriginalMember(owner = "client!ica", name = "b", descriptor = "(B)J")
	@Override
	public long getTime() {
		return this.timeSubmitted;
	}

	@OriginalMember(owner = "client!ica", name = "a", descriptor = "(Z)I")
	@Override
	public int getKeyCode() {
		return this.keyCode;
	}
}
