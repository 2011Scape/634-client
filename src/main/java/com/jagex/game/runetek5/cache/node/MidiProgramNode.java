package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!gia")
public final class MidiProgramNode extends Node {

	@OriginalMember(owner = "client!gia", name = "r", descriptor = "[B")
	public final byte[] notes;

	@OriginalMember(owner = "client!gia", name = "<init>", descriptor = "([B)V")
	public MidiProgramNode(@OriginalArg(0) byte[] value) {
		this.notes = value;
	}
}
