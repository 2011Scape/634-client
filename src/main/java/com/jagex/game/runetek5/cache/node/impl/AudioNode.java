package com.jagex.game.runetek5.cache.node.impl;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ji")
public abstract class AudioNode extends Node {

	@OriginalMember(owner = "client!ji", name = "o", descriptor = "I")
	public int activeVoiceCount;

	@OriginalMember(owner = "client!ji", name = "<init>", descriptor = "()V")
	public AudioNode() {
	}
}
