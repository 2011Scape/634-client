package com.jagex.game.runetek5.config.sequence;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!qca")
public final class PathingEntitySequence {

	@OriginalMember(owner = "client!qca", name = "a", descriptor = "I")
	public int currentLoopCount;

	@OriginalMember(owner = "client!qca", name = "d", descriptor = "I")
	public int speed;

	@OriginalMember(owner = "client!qca", name = "g", descriptor = "I")
	public int currentFrameId;

	@OriginalMember(owner = "client!qca", name = "j", descriptor = "I")
	public int sequenceId;

	@OriginalMember(owner = "client!qca", name = "k", descriptor = "I")
	public int nextFrameId;

	@OriginalMember(owner = "client!qca", name = "l", descriptor = "I")
	public int currentFrameDuration;
}
