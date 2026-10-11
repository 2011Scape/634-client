package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.entity.NPCEntity;

@OriginalClass("client!lo")
public final class NPCEntityNode extends Node {

	@OriginalMember(owner = "client!lo", name = "q", descriptor = "Lclient!ff;")
	public final NPCEntity npc;

	@OriginalMember(owner = "client!lo", name = "<init>", descriptor = "(Lclient!ff;)V")
	public NPCEntityNode(@OriginalArg(0) NPCEntity npcEntity) {
		this.npc = npcEntity;
	}
}
