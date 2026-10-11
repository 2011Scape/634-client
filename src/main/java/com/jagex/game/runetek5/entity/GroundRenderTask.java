package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.entity.EntityNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!vo")
public final class GroundRenderTask extends EntityNode {

	@OriginalMember(owner = "client!vo", name = "j", descriptor = "I")
	public final int level;

	@OriginalMember(owner = "client!vo", name = "<init>", descriptor = "(I)V")
	public GroundRenderTask(@OriginalArg(0) int level) {
		this.level = level;
	}
}
