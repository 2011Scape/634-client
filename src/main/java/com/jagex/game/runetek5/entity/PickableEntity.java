package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.entity.EntityNode;
import com.jagex.game.runetek5.entity.BoundingBoxPolygon;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!jda")
public final class PickableEntity extends EntityNode {

	@OriginalMember(owner = "client!jda", name = "k", descriptor = "Z")
	public boolean interactive;

	@OriginalMember(owner = "client!jda", name = "l", descriptor = "Lclient!ga;")
	public Entity entity;

	@OriginalMember(owner = "client!jda", name = "p", descriptor = "[Lclient!iw;")
	public BoundingBoxPolygon[] boundingBoxPolygons;

	@OriginalMember(owner = "client!jda", name = "a", descriptor = "(IILclient!ha;I)Z")
	public boolean hovered(@OriginalArg(0) int mouseY, @OriginalArg(1) int mouseX, @OriginalArg(2) Toolkit toolkit) {
		@Pc(17) int pickSizeShift = this.entity.getPickSizeShift();
		if (this.boundingBoxPolygons != null) {
			for (@Pc(22) int i = 0; i < this.boundingBoxPolygons.length; i++) {
				this.boundingBoxPolygons[i].size <<= pickSizeShift;
				if (this.boundingBoxPolygons[i].hovered(mouseX, mouseY) && this.entity.picked(toolkit, mouseY, mouseX)) {
					this.boundingBoxPolygons[i].size >>= pickSizeShift;
					return true;
				}
				this.boundingBoxPolygons[i].size >>= pickSizeShift;
			}
		}
		return false;
	}
	@OriginalMember(owner = "client!tk", name = "a", descriptor = "(IZZ)Lclient!jda;")
	public static PickableEntity obtain(@OriginalArg(0) int length, @OriginalArg(1) boolean interactive) {
		@Pc(7) EntityNodeList[] linkedLists = EntityNode.PICKABLE_ENTITY_POOLS;
		synchronized (EntityNode.PICKABLE_ENTITY_POOLS) {
			@Pc(26) PickableEntity pickableEntity;
			if (EntityNode.PICKABLE_ENTITY_POOLS.length <= length || EntityNode.PICKABLE_ENTITY_POOLS[length].isEmpty()) {
				pickableEntity = new PickableEntity();
				pickableEntity.boundingBoxPolygons = new BoundingBoxPolygon[length];
				for (@Pc(48) int i = 0; i < length; i++) {
					pickableEntity.boundingBoxPolygons[i] = new BoundingBoxPolygon();
				}
			} else {
				pickableEntity = (PickableEntity) EntityNode.PICKABLE_ENTITY_POOLS[length].getLast();
				pickableEntity.unlinkNode();
				@Pc(33) int poolCount = EntityNode.PICKABLE_ENTITY_POOL_SIZES[length]--;
			}
			pickableEntity.interactive = interactive;
			return pickableEntity;
		}
	}
	@OriginalMember(owner = "client!hha", name = "a", descriptor = "(Lclient!jda;I)V")
	public static void recycle(@OriginalArg(0) PickableEntity pickableEntity) {
		pickableEntity.entity = null;
		@Pc(12) int length = pickableEntity.boundingBoxPolygons.length;
		for (@Pc(22) int i = 0; i < length; i++) {
			pickableEntity.boundingBoxPolygons[i].positionCalculated = false;
		}
		@Pc(40) EntityNodeList[] linkedLists = EntityNode.PICKABLE_ENTITY_POOLS;
		synchronized (EntityNode.PICKABLE_ENTITY_POOLS) {
			if (length < EntityNode.PICKABLE_ENTITY_POOLS.length && EntityNode.PICKABLE_ENTITY_POOL_SIZES[length] < 200) {
				EntityNode.PICKABLE_ENTITY_POOLS[length].offer(pickableEntity);
				@Pc(67) int previousCount = EntityNode.PICKABLE_ENTITY_POOL_SIZES[length]++;
			}
		}
	}
}
