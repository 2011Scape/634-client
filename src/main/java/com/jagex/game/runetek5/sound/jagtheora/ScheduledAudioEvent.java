package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!om")
public abstract class ScheduledAudioEvent extends Node {

	@OriginalMember(owner = "client!om", name = "o", descriptor = "I")
	public int scheduledTime;

	@OriginalMember(owner = "client!om", name = "<init>", descriptor = "()V")
	private ScheduledAudioEvent() {
	}

	@OriginalMember(owner = "client!om", name = "a", descriptor = "(Lclient!sq;)I")
	public abstract int run(@OriginalArg(0) PcmStreamMixer mixer);

	@OriginalMember(owner = "client!om", name = "a", descriptor = "()V")
	public abstract void onRemove();
}
