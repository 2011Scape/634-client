package com.jagex.game.runetek5;

import com.jagex.game.runetek5.entity.EntityNodeList;
import com.jagex.game.runetek5.entity.BoundingBoxPolygon;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.Entity;
import com.jagex.game.runetek5.entity.PickableEntity;
import com.jagex.game.runetek5.entity.EntityNode;

@OriginalClass("client!ks")
public final class PickList {

	@OriginalMember(owner = "client!ks", name = "e", descriptor = "Lclient!tca;")
	public final EntityNodeList entities = new EntityNodeList();

	@OriginalMember(owner = "client!ks", name = "f", descriptor = "Z")
	public boolean retained = false;

	@OriginalMember(owner = "client!ks", name = "<init>", descriptor = "(Z)V")
	public PickList(@OriginalArg(0) boolean retained) {
		this.retained = retained;
	}

	@OriginalMember(owner = "client!ks", name = "a", descriptor = "(Lclient!jda;I)V")
	public void add(@OriginalArg(0) PickableEntity pickableEntity) {
		@Pc(8) Entity entity = pickableEntity.entity;
		@Pc(10) boolean noneCalculated = true;
		@Pc(13) BoundingBoxPolygon[] pickingCylinders = pickableEntity.boundingBoxPolygons;
		for (@Pc(15) int i = 0; i < pickingCylinders.length; i++) {
			if (pickingCylinders[i].positionCalculated) {
				noneCalculated = false;
				break;
			}
		}
		if (noneCalculated) {
			return;
		}
		@Pc(43) PickableEntity pickableEntity2;
		if (this.retained) {
			for (pickableEntity2 = (PickableEntity) this.entities.getFirst(); pickableEntity2 != null; pickableEntity2 = (PickableEntity) this.entities.getNext()) {
				if (entity == pickableEntity2.entity) {
					pickableEntity2.unlinkNode();
					PickableEntity.recycle(pickableEntity2);
				}
			}
		}
		for (pickableEntity2 = (PickableEntity) this.entities.getFirst(); pickableEntity2 != null; pickableEntity2 = (PickableEntity) this.entities.getNext()) {
			if (entity.screenDepth >= pickableEntity2.entity.screenDepth) {
				EntityNode.insertBefore(pickableEntity2, pickableEntity);
				return;
			}
		}
		this.entities.offer(pickableEntity);
	}

	@OriginalMember(owner = "client!ks", name = "b", descriptor = "(B)V")
	public void clear() {
		while (true) {
			@Pc(7) PickableEntity entity = (PickableEntity) this.entities.removeFirst();
			if (entity == null) {
				return;
			}
			entity.unlinkNode();
			PickableEntity.recycle(entity);
		}
	}
	@OriginalMember(owner = "client!kga", name = "c", descriptor = "Lclient!ks;")
	public static final PickList aClass182_2 = new PickList(false);

	@OriginalMember(owner = "client!pe", name = "a", descriptor = "(Lclient!ks;)V")
	public static void setActive(@OriginalArg(0) PickList pickList) {
		PickList.aClass182_1 = pickList;
	}

	@OriginalMember(owner = "client!kga", name = "a", descriptor = "Lclient!ks;")
	public static PickList aClass182_1 = aClass182_2;
	@OriginalMember(owner = "client!pf", name = "P", descriptor = "Lclient!ks;")
	public static final PickList aClass182_3 = new PickList(true);

	@OriginalMember(owner = "client!ut", name = "a", descriptor = "()V")
	public static void resetActive() {
		PickList.aClass182_1 = PickList.aClass182_2;
	}
}
