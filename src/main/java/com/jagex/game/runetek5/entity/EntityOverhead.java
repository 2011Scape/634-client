package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.entity.EntityNode;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.EntityOverheadAllocator;
import org.openrs2.deob.annotation.OriginalArg;

@OriginalClass("client!ql")
public final class EntityOverhead extends EntityNode {

	@OriginalMember(owner = "client!ql", name = "j", descriptor = "I")
	public int maxY;

	@OriginalMember(owner = "client!ql", name = "k", descriptor = "I")
	public int maxX;

	@OriginalMember(owner = "client!ql", name = "l", descriptor = "I")
	public int minY;

	@OriginalMember(owner = "client!ql", name = "n", descriptor = "I")
	public int minX;

	@OriginalMember(owner = "client!ql", name = "p", descriptor = "Lclient!kda;")
	public PathingEntity pathingEntity;
	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(Lclient!ql;Z)V")
	public static void release(@OriginalArg(0) EntityOverhead node) {
		node.pathingEntity = null;
		if (EntityOverheadAllocator.poolSize < 20) {
			EntityOverheadAllocator.aClass325_3.offer(node);
			EntityOverheadAllocator.poolSize++;
		}
	}
}
