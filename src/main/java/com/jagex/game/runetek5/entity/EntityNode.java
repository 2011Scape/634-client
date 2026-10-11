package com.jagex.game.runetek5.entity;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import org.openrs2.deob.annotation.OriginalArg;

@OriginalClass("client!cm")
public class EntityNode {

	@OriginalMember(owner = "client!cm", name = "e", descriptor = "[Lclient!tca;")
	public static final EntityNodeList[] PICKABLE_ENTITY_POOLS = new EntityNodeList[5];

	@OriginalMember(owner = "client!cm", name = "a", descriptor = "Lclient!cm;")
	public EntityNode next;

	@OriginalMember(owner = "client!cm", name = "g", descriptor = "Lclient!cm;")
	public EntityNode previous;

	static {
		for (@Pc(4) int i = 0; i < PICKABLE_ENTITY_POOLS.length; i++) {
			PICKABLE_ENTITY_POOLS[i] = new EntityNodeList();
		}
	}

	@OriginalMember(owner = "client!cm", name = "a", descriptor = "(Z)V")
	public final void unlinkNode() {
		if (this.previous != null) {
			this.previous.next = this.next;
			this.next.previous = this.previous;
			this.next = null;
			this.previous = null;
		}
	}
	@OriginalMember(owner = "client!fga", name = "a", descriptor = "(I)V")
	public static void resetPickableEntityPools() {
		@Pc(3) EntityNodeList[] linkedLists = EntityNode.PICKABLE_ENTITY_POOLS;
		synchronized (EntityNode.PICKABLE_ENTITY_POOLS) {
			for (@Pc(7) int i = 0; i < EntityNode.PICKABLE_ENTITY_POOLS.length; i++) {
				EntityNode.PICKABLE_ENTITY_POOLS[i] = new EntityNodeList();
				EntityNode.PICKABLE_ENTITY_POOL_SIZES[i] = 0;
			}
		}
	}

	@OriginalMember(owner = "client!dia", name = "a", descriptor = "[I")
	public static final int[] PICKABLE_ENTITY_POOL_SIZES = new int[5];

	@OriginalMember(owner = "client!te", name = "a", descriptor = "(Lclient!cm;Lclient!cm;Z)V")
	public static void insertBefore(@OriginalArg(0) EntityNode node, @OriginalArg(1) EntityNode node2) {
		if (node2.previous != null) {
			node2.unlinkNode();
		}
		node2.next = node;
		node2.previous = node.previous;
		node2.previous.next = node2;
		node2.next.previous = node2;
	}
}
